import handler.CookieHandler;
import handler.CorsHandler;
import handler.DeleteFileHandler;
import handler.GetFileHandler;
import handler.PatchFileHandler;
import handler.PostFileHandler;
import handler.SearchHandler;
import ratpack.core.handling.Chain;
import ratpack.core.handling.Handlers;
import ratpack.core.handling.RequestLogger;
import ratpack.func.Action;

public class Router implements Action<Chain> {

  @Override
  public void execute(Chain chain) throws Exception {
    chain
        .all(RequestLogger.ncsa())
        .all(new CorsHandler())
        .prefix("file", fileChain -> fileChain
            .all(new CookieHandler())
            .path(":name", nameCtx -> nameCtx.byMethod(m -> m
                .get(new GetFileHandler())
                .patch(new PatchFileHandler())
                .delete(new DeleteFileHandler())))
            .post(new PostFileHandler())
        )
        .prefix("search", searchChain -> searchChain
            .get(new SearchHandler()))
        .notFound()
        .all(Handlers.clientError(400));
  }

}
