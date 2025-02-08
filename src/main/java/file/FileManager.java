package file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ratpack.core.service.Service;

/**
 * A FileManager manages a directory and all the files contained within. This includes creating and
 * removing files as well as extracting the file metadata. A File managed by a FileManager is
 * identified by a unique filename.
 */
public class FileManager implements Service {

  private final Path root;

  public FileManager(String path) throws FileException {
    root = Paths.get(path);
    System.out.println("creating a file store in " + root.toAbsolutePath());
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw new FileException("unable to create directories for file store");
    }
    if (!Files.isReadable(root) || !Files.isWritable(root)) {
      throw new FileException("filestore has insufficient permissions");
    }
  }

  public boolean createFile(String name, byte[] bytes) {
    Path filepath = root.resolve(name);
    if (Files.exists(filepath)) {
      // Possible extension: assign another name in case of a name clash.
      return false;
    }

    try {
      Files.createFile(filepath);
      Files.write(filepath, bytes);
    } catch (IOException e) {
      System.out.println("filestore unable to create file");
      return false;
    }

    return true;
  }

  public boolean removeFile(String name) {
    Path filepath = root.resolve(name);

    try {
      Files.deleteIfExists(filepath);
    } catch (IOException e) {
      System.out.println("filestore unable to delete file");
      return false;
    }

    return true;
  }

  public FileInfo extractFileInfo(String filename) {
    Path filepath = root.resolve(filename);
    if (Files.notExists(filepath)) {
      return null;
    }

    FileInfo info = new FileInfo();
    info.name = filename;

    try {
      info.type = Files.probeContentType(filepath);
    } catch (Exception e) {
      System.out.println("filestore unable to complete file info extraction");
    }

    return info;
  }

}
