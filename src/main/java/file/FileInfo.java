package file;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileInfo {

  public String name;
  public String type;
  public String category;
  public String owner;
  public String description;

  @JsonCreator
  public FileInfo(
      @JsonProperty("name") String name,
      @JsonProperty("type") String type,
      @JsonProperty("category") String category,
      @JsonProperty("owner") String owner,
      @JsonProperty("description") String description
  ) {
    this.name = name;
    this.type = type;
    this.category = category;
    this.owner = owner;
    this.description = description;
  }

  public FileInfo() {
  }

  @Override
  public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return "FileInfo{" +
          "name='" + name + '\'' +
          ", type='" + type + '\'' +
          ", category='" + category + '\'' +
          ", owner='" + owner + '\'' +
          ", description='" + description + '\'' +
          '}';
    }
  }

  public void extend(FileInfo other) {
    if (other == null) {
      return;
    }

    if (other.name != null && !other.name.isEmpty()) {
      this.name = other.name;
    }

    if (other.type != null && !other.type.isEmpty()) {
      this.type = other.type;
    }

    if (other.category != null && !other.category.isEmpty()) {
      this.category = other.category;
    }

    if (other.owner != null && !other.owner.isEmpty()) {
      this.owner = other.owner;
    }

    if (other.description != null && !other.description.isEmpty()) {
      this.description = other.description;
    }
  }

}
