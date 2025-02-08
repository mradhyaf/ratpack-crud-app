package db;

public class SqlQueries {

  public static final String CREATE_TABLE =
      // TODO: Fix unique filename constraint
      "CREATE TABLE IF NOT EXISTS files (" +
          "id INTEGER PRIMARY KEY," +
          "name VARCHAR(32)," +
          "type VARCHAR(32)," +
          "category VARCHAR(32)," +
          "owner VARCHAR(128)" +
          ");";

  public static final String DROP_TABLE =
      "DROP TABLE IF EXISTS files;";

  public static final String SELECT_ALL_FILES =
      "SELECT * FROM files;";

  public static final String INSERT_FILE =
      "INSERT INTO files(name,type,category,owner)" +
          "VALUES(?,?,?,?);";

}
