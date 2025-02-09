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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.core.service.Service;
import ratpack.core.service.StartEvent;
import ratpack.core.service.StopEvent;

public class Database implements Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
  private static final String DB_FILENAME = "db.db";

  private final Path root;
  private Connection dbConn;

  public Database(String path) throws DatabaseException {
    root = Paths.get(path);
    try {
      LOGGER.debug("creating a database in {}", root.toAbsolutePath());
      Files.createDirectories(root);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      throw new DatabaseException("unable to create files for database");
    }
  }

  @Override
  public void onStart(StartEvent event) throws DatabaseException {
    try {
      dbConn = DriverManager.getConnection("jdbc:sqlite:" + root.resolve(DB_FILENAME));

      Statement stmt = dbConn.createStatement();
      stmt.execute(SqlQueries.CREATE_TABLE);
      stmt.close();

      LOGGER.info("database running with backing file at {}",
          root.resolve(DB_FILENAME).toAbsolutePath());
    } catch (SQLException e) {
      LOGGER.error(e.getMessage());
      throw new DatabaseException("unable to start database");
    }
  }

  @Override
  public void onStop(StopEvent event) throws DatabaseException {
    try {
      // TODO: Remove this on stable build
      dbConn.createStatement().execute(SqlQueries.DROP_TABLE);
      dbConn.close();

      LOGGER.info("database connection closed");
    } catch (SQLException e) {
      LOGGER.error(e.getMessage());
      throw new DatabaseException("unable to close database");
    }
  }

  public List<FileInfo> selectAllFiles() {
    try (Statement stmt = dbConn.createStatement()) {
      ResultSet result = stmt.executeQuery(SqlQueries.SELECT_ALL_FILES);
      LOGGER.debug("select all files, found {} row(s)", result.getFetchSize());

      ArrayList<FileInfo> response = new ArrayList<>();
      while (result.next()) {
        FileInfo row = new FileInfo();
        row.name = result.getString("name");
        row.type = result.getString("type");
        row.category = result.getString("category");
        row.owner = result.getString("owner");
        row.description = result.getString("description");
        response.add(row);
      }

      return response;
    } catch (SQLException e) {
      return Collections.emptyList();
    }
  }

  /**
   * @param name name of the file.
   * @return the {@link FileInfo} describing the file, or {@literal null} if not found.
   */
  public FileInfo selectFileByName(String name) {
    try (PreparedStatement stmt = dbConn.prepareStatement(SqlQueries.SELECT_FILE_BY_NAME)) {
      stmt.setString(1, name);

      ResultSet result = stmt.executeQuery();
      LOGGER.debug("select file with name {}, found {} row(s)", name, result.getFetchSize());

      if (!result.next()) {
        return null;
      }

      FileInfo row = new FileInfo();
      row.name = result.getString("name");
      row.type = result.getString("type");
      row.category = result.getString("category");
      row.owner = result.getString("owner");
      row.description = result.getString("description");

      return row;
    } catch (SQLException e) {
      LOGGER.warn(e.getMessage());
      return null;
    }
  }

  /**
   * @param file the file to be stored.
   * @return file identifier string or an empty string if failed.
   */
  public int insertFile(FileInfo file) {
    try (PreparedStatement stmt = dbConn.prepareStatement(SqlQueries.INSERT_FILE)) {
      stmt.setString(1, file.name);
      stmt.setString(2, file.type);
      stmt.setString(3, file.category);
      stmt.setString(4, file.owner);
      stmt.setString(5, file.description);

      int rows = stmt.executeUpdate();
      LOGGER.info("inserted {} row(s) to files", rows);

      return rows;
    } catch (SQLException e) {
      LOGGER.error(e.getMessage());
      return 0;
    }
  }

  public int updateFile(FileInfo file) {
    try (PreparedStatement stmt = dbConn.prepareStatement(SqlQueries.UPDATE_FILE)) {
      stmt.setString(1, file.name);
      stmt.setString(2, file.type);
      stmt.setString(3, file.category);
      stmt.setString(4, file.owner);
      stmt.setString(5, file.description);

      int rows = stmt.executeUpdate();
      LOGGER.info("updated {} row(s) from files", rows);

      return rows;
    } catch (SQLException e) {
      LOGGER.error(e.getMessage());
      return 0;
    }
  }

  public int deleteFileByName(String name) {
    try (PreparedStatement stmt = dbConn.prepareStatement(SqlQueries.DELETE_FILE_BY_NAME)) {
      stmt.setString(1, name);

      int rows = stmt.executeUpdate();
      LOGGER.info("deleted {} row(s) from files", rows);
      LOGGER.debug("deleted row {}", name);

      return rows;
    } catch (SQLException e) {
      LOGGER.error(e.getMessage());
      return 0;
    }
  }

}
