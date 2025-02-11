package handler;

import java.util.UUID;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.exec.registry.Registry;

public class CookieHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    String uid = ctx.getRequest().oneCookie("uid");
    if (uid == null) {
      uid = UUID.randomUUID().toString();
      ctx.getResponse().cookie("uid", uid);
    }
    ctx.next(Registry.single(new Auth(uid)));
  }

}
