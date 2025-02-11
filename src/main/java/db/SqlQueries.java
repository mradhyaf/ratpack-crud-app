package db;

import file.FileInfo;

public class SqlQueries {

  public static final String CREATE_TABLE =
      "CREATE TABLE IF NOT EXISTS files (" +
          "name TEXT PRIMARY KEY," +
          "type TEXT," +
          "category TEXT," +
          "owner TEXT," +
          "description TEXT" +
          ");";

  public static final String DROP_TABLE =
      "DROP TABLE IF EXISTS files;";

  public static final String SELECT_ALL_FILES =
      "SELECT * FROM files;";

  /**
   * parameters:
   * 1:name (String)
   */
  public static final String SELECT_FILE_BY_NAME =
      "SELECT * FROM files WHERE name = ?;";

  /**
   * parameters:
   * 1:name (String)
   * 2:type (String)
   * 3:category (String)
   * 4:owner (String)
   * 5:description (String)
   */
  public static final String INSERT_FILE =
      "INSERT INTO files(name,type,category,owner,description) " +
          "VALUES(?,?,?,?,?);";

  public static final String UPDATE_FILE =
      "UPDATE files "
          + "SET(name,type,category,owner,description)"
          + "=(?,?,?,?,?);";

  /**
   * parameters:
   * 1:name (String)
   */
  public static final String DELETE_FILE_BY_NAME =
      "DELETE FROM files WHERE name = ?;";

  public static String selectFileWhere(FileInfo filters) {
    StringBuilder sb = new StringBuilder("SELECT * FROM files WHERE ");
    boolean isFirst = true;
    if (filters.name != null) {
      sb.append("name LIKE '%").append(filters.name).append("%' ");
      isFirst = false;
    }
    if (filters.type != null) {
      if (!isFirst) {
        sb.append("AND ");
      }
      sb.append("type = '").append(filters.type).append("' ");
      isFirst = false;
    }
    if (filters.category != null) {
      if (!isFirst) {
        sb.append("AND ");
      }
      sb.append("category = '").append(filters.category).append("' ");
      isFirst = false;
    }
    if (filters.description != null) {
      if (!isFirst) {
        sb.append("AND ");
      }
      sb.append("description like '%").append(filters.description).append("%' ");
    }
    sb.append(";");

    return sb.toString();
  }
}
