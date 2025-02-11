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
import ratpack.core.jackson.Jackson;
import ratpack.exec.Promise;

public class PostFileHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    String contentType = ctx.getRequest().getContentType().getType();
    ResponseBuilder rb = new ResponseBuilder();

    if (contentType == null || !contentType.equalsIgnoreCase("multipart/form-data")) {
      ctx.getResponse().status(Status.BAD_REQUEST);
      rb.setMessage("request must be multipart/form-data");
      ctx.render(Jackson.json(rb));
      return;
    }

    Database db = ctx.get(Database.class);
    FileManager fm = ctx.get(FileManager.class);

    Promise<Form> formPromise = ctx.parse(Form.class);
    formPromise.then(form -> {
      UploadedFile file = form.file("file");
      if (file == null) {
        ctx.getResponse().status(Status.BAD_REQUEST);
        rb.setMessage("missing file from the request");
        ctx.render(Jackson.json(rb));
        return;
      }

      FileInfo fileInfo = fm.createFile(file.getFileName(), file.getBytes());
      if (fileInfo == null) {
        ctx.getResponse().status(Status.CONFLICT);
        rb.setMessage("file with that name already exists");
        ctx.render(Jackson.json(rb));
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
        ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR);
        rb.setMessage("uh oh. something went wrong");
        ctx.render(Jackson.json(rb));

        fm.removeFile(file.getFileName());
      } else {
        ctx.getResponse().status(Status.OK);
        rb.setMessage("file uploaded");
        rb.setFileInfo(fileInfo);
        ctx.render(Jackson.json(rb));
      }
    });
  }

}
