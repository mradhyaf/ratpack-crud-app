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

    FileInfo filters = new FileInfo();
    filters.name = query.get("name");
    filters.type = query.get("type");
    filters.category = query.get("category");
    filters.description = query.get("description");

    List<FileInfo> fileInfos = db.selectFilesWhere(filters);
    ctx.render(Jackson.json(fileInfos));
  }

}
