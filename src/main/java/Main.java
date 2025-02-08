import db.Database;
import db.DatabaseException;
import file.FileException;
import file.FileManager;
import ratpack.core.server.RatpackServer;

public class Main {

  public static void main(String[] args) {
    try {
      // TODO: Add configuration for app data path
      Database db = new Database("build/database");
      FileManager fm = new FileManager("build/filestore");

      RatpackServer.start(server -> server
          .serverConfig(config -> config
              .port(5050))
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
