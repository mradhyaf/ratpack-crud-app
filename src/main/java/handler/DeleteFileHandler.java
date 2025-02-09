package handler;

import db.Database;
import file.FileInfo;
import file.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.http.Status;

public class DeleteFileHandler implements Handler {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteFileHandler.class);

  @Override
  public void handle(Context ctx) throws Exception {
    String name = ctx.getPathTokens().get("name");

    Database db = ctx.get(Database.class);
    FileInfo fileInfo = db.selectFileByName(name);
    LOGGER.debug("fileInfo={}", fileInfo);

    if (fileInfo == null) {
      LOGGER.info("file {} not found", name);
      ctx.getResponse().status(Status.NOT_FOUND).send();
      return;
    }

    if (!fileInfo.owner.equals(ctx.get(Auth.class).uid)) {
      ctx.getResponse().status(Status.UNAUTHORIZED).send();
      return;
    }

    FileManager fm = ctx.get(FileManager.class);
    boolean fileRemoved = fm.removeFile(fileInfo.name);
    int rowsDeleted = db.deleteFileByName(fileInfo.name);

    if (!fileRemoved || rowsDeleted == 0) {
      ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR).send();
      return;
    }

    ctx.getResponse().status(Status.OK).send();
  }

}
