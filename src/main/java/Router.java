import db.Database;
import file.FileInfo;
import file.FileManager;
import java.util.List;
import ratpack.core.form.Form;
import ratpack.core.form.UploadedFile;
import ratpack.core.handling.Chain;
import ratpack.core.handling.Handler;
import ratpack.core.handling.Handlers;
import ratpack.core.http.Headers;
import ratpack.core.http.Request;
import ratpack.core.http.Status;
import ratpack.core.jackson.Jackson;
import ratpack.exec.Promise;
import ratpack.func.Action;
import ratpack.func.MultiValueMap;

public class Router implements Action<Chain> {

  private static final Handler getFileHandler = ctx -> {};
  private static final Handler postFileHandler = ctx -> {
    System.out.println("POST /api/file");

    Request req = ctx.getRequest();
    Headers headers = req.getHeaders();

    if (headers.get("content-type").equalsIgnoreCase("multipart/form-data")) {
      ctx.getResponse().status(Status.BAD_REQUEST).send();
      return;
    }

    Promise<Form> formPromise = ctx.parse(Form.class);
    formPromise.then(form -> {
      UploadedFile file = form.file("file");
      FileManager fm = ctx.get(FileManager.class);

      boolean created = fm.createFile(file.getFileName(), file.getBytes());
      if (!created) {
        // TODO: add error message
        ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR).send();
        return;
      }

      Database db = ctx.get(Database.class);
      Integer numInserts = db.insertFile(fm.extractFileInfo(file.getFileName()));

      if (numInserts == 0) {
        ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR).send();
        fm.removeFile(file.getFileName());
      } else {
        ctx.getResponse().status(Status.OK).send(numInserts.toString());
      }
    });
  };
  private static final Handler patchFileHandler = ctx -> {};
  private static final Handler deleteFileHandler = ctx -> {};
  private static final Handler searchHandler = ctx -> {
    System.out.println("GET /api/search");
    MultiValueMap<String, String> query = ctx.getRequest().getQueryParams();

    if (query.isEmpty()) {
      System.out.println("query is empty");
      Database db = ctx.get(Database.class);

      List<FileInfo> fileInfos = db.selectAllFiles();
      ctx.render(Jackson.json(fileInfos));
      return;
    }
    ctx.notFound();
  };

  @Override
  public void execute(Chain chain) throws Exception {
    chain
        .prefix("api", apiChain -> apiChain
            .prefix("file", fileChain -> fileChain
                .get(":id", getFileHandler)
                .post(postFileHandler)
                .patch(patchFileHandler)
                .delete(deleteFileHandler)
                .notFound()
            )
            .get("search", searchHandler)
            .notFound()
        )
        .all(Handlers.clientError(400));
  }

}
