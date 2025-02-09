package handler;

import db.Database;
import file.FileInfo;
import file.FileManager;
import java.nio.file.Path;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

public class GetFileHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    String name = ctx.getPathTokens().get("name");

    Database db = ctx.get(Database.class);
    FileManager fm = ctx.get(FileManager.class);

    FileInfo fileInfo = db.selectFileByName(name);
    if (fileInfo == null) {
      ctx.notFound();
      return;
    }

    Path filepath = fm.getFile(fileInfo.name);
    if (filepath == null) {
      ctx.notFound();
      return;
    }

    ctx.render(filepath);
  }

}
