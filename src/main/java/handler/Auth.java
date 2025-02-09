package handler;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Auth {

  public String uid;

  public Auth(@JsonProperty("uid") String uid) {
    this.uid = uid;
  }
}
