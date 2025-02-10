import db.Database;
import db.DatabaseException;
import file.FileException;
import file.FileManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.core.server.RatpackServer;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    int port;
    Path datapath;

    try {
      Map<String, String> env = System.getenv();
      port = Integer.parseInt(env.getOrDefault("SERVER_PORT", "5050"));
      String dp = env.getOrDefault("DATA_PATH", "data");

      datapath = Paths.get(dp);
      Files.createDirectories(datapath);
      if (!Files.isReadable(datapath) || !Files.isWritable(datapath)) {
        throw new IOException("Bad data path");
      }
    } catch (Exception e) {
      throw new RuntimeException("Invalid environment variables");
    }

    try {
      Database db = new Database(datapath);
      FileManager fm = new FileManager(datapath);

      RatpackServer.start(server -> server
          .serverConfig(config -> config
              .maxContentLength(1024 * 1024 * 1024) // 1 gigabyte
              .port(port))
          .registryOf(registry -> registry
              .add(Database.class, db)
              .add(FileManager.class, fm))
          .handlers(new Router())
      );
    } catch (DatabaseException e) {
      throw new RuntimeException("Database error: " + e.getMessage());
    } catch (FileException e) {
      throw new RuntimeException("FileManager error: " + e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
