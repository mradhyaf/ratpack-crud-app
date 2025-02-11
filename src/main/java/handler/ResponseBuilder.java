package handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import file.FileInfo;

public class ResponseBuilder {
  @JsonProperty("message")
  String msg = "";

  @JsonProperty("file")
  @JsonInclude(Include.NON_NULL)
  FileInfo fi;

  public ResponseBuilder setMessage(String message) {
    this.msg = message;
    return this;
  }

  public ResponseBuilder setFileInfo(FileInfo fileInfo) {
    this.fi = fileInfo;
    return this;
  }
}
