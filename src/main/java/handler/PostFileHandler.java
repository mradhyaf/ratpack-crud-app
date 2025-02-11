package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.Database;
import file.FileInfo;
import file.FileManager;
import ratpack.core.form.Form;
import ratpack.core.form.UploadedFile;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.http.Status;
import ratpack.exec.Promise;

public class PostFileHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    String contentType = ctx.getRequest().getContentType().getType();

    if (contentType == null || !contentType.equalsIgnoreCase("multipart/form-data")) {
      ctx.getResponse().status(Status.BAD_REQUEST)
         .send("{ \"message\" : \"request is not multipart/form-data\"}");
      return;
    }

    Database db = ctx.get(Database.class);
    FileManager fm = ctx.get(FileManager.class);

    Promise<Form> formPromise = ctx.parse(Form.class);
    formPromise.then(form -> {
      UploadedFile file = form.file("file");
      if (file == null) {
        // TODO: write a message
        ctx.getResponse().status(Status.BAD_REQUEST).send("{ \"message\" : \"file is missing\"}");
        return;
      }

      FileInfo fileInfo = fm.createFile(file.getFileName(), file.getBytes());
      if (fileInfo == null) {
        ctx.getResponse().status(Status.CONFLICT).send("{ \"message\" : \"fail to store file\"}");
        return;
      }

      String jsonInfo = form.get("info");
      if (jsonInfo != null) {
        FileInfo parsedInfo = new ObjectMapper().readValue(jsonInfo, FileInfo.class);
        fileInfo.extend(parsedInfo);
        fileInfo.setOwner(ctx.get(Auth.class).uid);
      }

      int numInserts = db.insertFile(fileInfo);
      if (numInserts == 0) {
        ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR).send();
        fm.removeFile(file.getFileName());
      } else {
        ctx.getResponse().status(Status.OK).send(Integer.toString(numInserts));
      }
    });
  }

}
