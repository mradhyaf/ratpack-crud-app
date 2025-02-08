package db;

import file.FileInfo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ratpack.core.service.Service;
import ratpack.core.service.StartEvent;
import ratpack.core.service.StopEvent;

public class Database implements Service {

  private final Path root;
  private Connection dbConn;

  public Database(String path) throws DatabaseException {
    root = Paths.get(path);
    try {
      System.out.println("creating a database in " + root.toAbsolutePath());
      Files.createDirectories(root);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new DatabaseException("unable to create files for database");
    }
  }

  @Override
  public void onStart(StartEvent event) throws DatabaseException {
    try {
      dbConn = DriverManager.getConnection("jdbc:sqlite:" + root.resolve("db.db"));
      dbConn.createStatement().execute(SqlQueries.CREATE_TABLE);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      throw new DatabaseException("unable to start database");
    }
  }

  @Override
  public void onStop(StopEvent event) throws DatabaseException {
    try {
      // TODO: Remove this on stable build
      dbConn.createStatement().execute(SqlQueries.DROP_TABLE);
      dbConn.close();
    } catch (SQLException e) {
      throw new DatabaseException("something went wrong when closing the database");
    }
  }

  public List<FileInfo> selectAllFiles() {
    try {
      Statement stmt = dbConn.createStatement();
      ResultSet result = stmt.executeQuery(SqlQueries.SELECT_ALL_FILES);

      ArrayList<FileInfo> response = new ArrayList<>();
      while (result.next()) {
        FileInfo row = new FileInfo();
        row.name = result.getString("name");
        row.type = result.getString("type");
        row.type = result.getString("category");
        response.add(row);
        System.out.println(row.name);
      }

      return response;
    } catch (SQLException e) {
      return Collections.emptyList();
    }
  }

  /**
   * @param file the file to be stored.
   * @return file identifier string or an empty string if failed.
   */
  public int insertFile(FileInfo file) {
    try (PreparedStatement stmt = dbConn.prepareStatement(SqlQueries.INSERT_FILE)) {
      // TODO: add a unique file constraint
      stmt.setString(1, file.name);
      stmt.setString(2, file.type);
      stmt.setString(3, file.category);
      stmt.setString(4, file.owner);

      return stmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return 0;
    }
  }

}
