package handler;

import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

public class CorsHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    ctx.getResponse().getHeaders().add("access-control-allow-origin", "*");
    ctx.getResponse().getHeaders().add("access-control-allow-methods", "get,post,patch,delete");
    ctx.next();
  }

}
