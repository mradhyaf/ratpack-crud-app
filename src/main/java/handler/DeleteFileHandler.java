package handler;

import db.Database;
import file.FileInfo;
import file.FileManager;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.http.Status;
import ratpack.core.jackson.Jackson;

public class DeleteFileHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    String name = ctx.getPathTokens().get("name");

    Database db = ctx.get(Database.class);
    FileInfo fileInfo = db.selectFileByName(name);
    ResponseBuilder resBody = new ResponseBuilder();

    if (fileInfo == null) {
      ctx.getResponse().status(Status.NOT_FOUND);
      ctx.render(Jackson.json(resBody.setMessage("file not found")));
      return;
    }

    if (!ctx.get(Auth.class).uid.equals(fileInfo.getOwner())) {
      ctx.getResponse().status(Status.UNAUTHORIZED).send();
      ctx.render(Jackson.json(resBody.setMessage("unauthorized to modify file")));
      return;
    }

    FileManager fm = ctx.get(FileManager.class);
    boolean fileRemoved = fm.removeFile(fileInfo.name);
    int rowsDeleted = db.deleteFileByName(fileInfo.name);

    if (!fileRemoved || rowsDeleted == 0) {
      ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR);
      ctx.render(Jackson.json(resBody.setMessage("uh oh. something went wrong")));
      return;
    }

    resBody.setMessage("file deleted");
    resBody.setFileInfo(fileInfo);
    ctx.getResponse().status(Status.OK);
    ctx.render(Jackson.json(resBody));
  }

}
