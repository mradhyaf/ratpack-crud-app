package handler;

import db.Database;
import file.FileInfo;
import java.util.List;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.jackson.Jackson;
import ratpack.func.MultiValueMap;

public class SearchHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    MultiValueMap<String, String> query = ctx.getRequest().getQueryParams();
    Database db = ctx.get(Database.class);

    if (query.isEmpty()) {
      List<FileInfo> fileInfos = db.selectAllFiles();
      ctx.render(Jackson.json(fileInfos));
      return;
    }

    ctx.notFound();
  }

}
