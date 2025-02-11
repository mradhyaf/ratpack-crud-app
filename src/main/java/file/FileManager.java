package file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.core.service.Service;
import ratpack.core.service.StopEvent;

/**
 * A FileManager manages a directory and all the files contained within. This includes creating and
 * removing files as well as extracting the file metadata. A File managed by a FileManager is
 * identified by a unique filename.
 */
public class FileManager implements Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

  private final Path root;

  public FileManager(Path path) throws FileException {
    root = path.resolve("files");

    try {
      LOGGER.debug("creating a file store in {}", root.toAbsolutePath());
      Files.createDirectories(root);
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      throw new FileException("unable to create directories for file store");
    }

    if (!Files.isReadable(root) || !Files.isWritable(root)) {
      throw new FileException("file manager has insufficient permissions");
    }
  }

  @Override
  public void onStop(StopEvent event) throws Exception {
//    try (DirectoryStream<Path> dir = Files.newDirectoryStream(root)) {
//      dir.forEach(path -> {
//        try {
//          Files.deleteIfExists(path);
//        } catch (IOException e) {
//          LOGGER.error("fail to cleanup files");
//        }
//      });
//      Files.deleteIfExists(root);
//    } catch (Exception e) {
//      LOGGER.error("fail to cleanup dir");
//      throw new FileException("unable to cleanup files");
//    }
  }

  /**
   * @param name  name of the file to create.
   * @param bytes byte array of the file contents.
   * @return {@link FileInfo} describing the created file.
   */
  public FileInfo createFile(String name, byte[] bytes) {
    Path filepath = root.resolve(name);
    if (Files.exists(filepath)) {
      // Possible extension: assign another name in case of a name clash.
      LOGGER.debug("file {} already exists", name);
      return null;
    }

    try {
      Files.createFile(filepath);
      Files.write(filepath, bytes);
      LOGGER.info("created file {}", name);
    } catch (IOException e) {
      LOGGER.error("unable to create file {}", name);
      return null;
    }

    return extractFileInfo(filepath);
  }

  /**
   * @param name name of the file to retrieve.
   * @return {@link Path} of the file if it exists, {@literal null} otherwise.
   */
  public Path getFile(String name) {
    Path path = root.resolve(name);

    if (Files.notExists(path)) {
      LOGGER.debug("file {} does not exist", name);
      return null;
    }

    return path;
  }

  /**
   * @param name name of the file to delete.
   * @return {@literal true} if the deletion is successful, {@literal false} otherwise.
   */
  public boolean removeFile(String name) {
    Path filepath = root.resolve(name);

    try {
      Files.deleteIfExists(filepath);
      LOGGER.info("deleted (if existed) file {}", filepath);
    } catch (IOException e) {
      LOGGER.warn("unable to delete file {}", filepath);
      return false;
    }

    return true;
  }

  private FileInfo extractFileInfo(Path filepath) {
    FileInfo info = new FileInfo();
    info.name = filepath.getFileName().toString();
    info.type = getFileType(filepath);
    info.category = getFileCategory(info.type);

    return info;
  }

  private static String getFileType(Path filepath) {
    // Possible extension: use Files::probeContentType
    String filename = filepath.getFileName().toString();
    String filetype = filename.substring(filename.lastIndexOf('.') + 1);
    return filetype.isBlank() ? "unknown" : filetype;
  }

  private static String getFileCategory(String filetype) {
    if (filetype.equalsIgnoreCase("unknown")) {
      return "others";
    }

    return switch (filetype) {
      case "mp3", "wav", "flac" -> "music";
      case "mp4", "mkv", "avi", "mov" -> "video";
      case "png", "jpeg", "jpg", "gif", "bmp" -> "picture";
      case "doc", "docx", "odt", "pdf", "txt", "md", "rtf" -> "document";
      case "xls", "xlsx", "ods" -> "spreadsheet";
      case "ppt", "pptx" -> "presentation";
      case "zip", "rar", "tar", "gz" -> "archive";
      case "html", "htm" -> "webpage";
      default -> "others";
    };
  }

}
