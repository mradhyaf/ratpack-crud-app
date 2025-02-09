package handler;

import db.Database;
import file.FileInfo;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.http.Status;
import ratpack.exec.Promise;

public class PatchFileHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    String name = ctx.getPathTokens().get("name");
    Database db = ctx.get(Database.class);

    FileInfo fileInfo = db.selectFileByName(name);
    if (fileInfo == null) {
      ctx.getResponse().status(Status.NOT_FOUND).send();
      return;
    }

    Promise<FileInfo> reqBody = ctx.parse(FileInfo.class);
    reqBody.then(newFileInfo -> {
      fileInfo.extend(newFileInfo);
      int updates = db.updateFile(fileInfo);

      if (updates == 0) {
        ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR).send();
        return;
      }

      ctx.getResponse().status(Status.OK).send();
    });
  }

}
