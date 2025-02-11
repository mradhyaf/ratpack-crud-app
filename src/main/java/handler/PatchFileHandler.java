package handler;

import db.Database;
import file.FileInfo;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.http.Status;
import ratpack.core.jackson.Jackson;
import ratpack.exec.Promise;

public class PatchFileHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    String name = ctx.getPathTokens().get("name");
    Database db = ctx.get(Database.class);
    ResponseBuilder rb = new ResponseBuilder();

    FileInfo fileInfo = db.selectFileByName(name);
    if (fileInfo == null) {
      ctx.getResponse().status(Status.NOT_FOUND);
      ctx.render(Jackson.json(rb.setMessage("file not found")));
      return;
    }

    if (!ctx.get(Auth.class).uid.equals(fileInfo.getOwner())) {
      ctx.getResponse().status(Status.UNAUTHORIZED).send();
      ctx.render(Jackson.json(rb.setMessage("unauthorized to modify file")));
      return;
    }

    Promise<FileInfo> reqBody = ctx.parse(FileInfo.class);
    reqBody.then(newFileInfo -> {
      fileInfo.extend(newFileInfo);
      int updates = db.updateFile(fileInfo);

      if (updates == 0) {
        ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR).send();
        ctx.render(Jackson.json(rb.setMessage("uh oh. something went wrong")));
        return;
      }

      rb.setMessage("file updated");
      rb.setFileInfo(fileInfo);
      ctx.getResponse().status(Status.OK);
      ctx.render(Jackson.json(rb));
    });
  }

}
