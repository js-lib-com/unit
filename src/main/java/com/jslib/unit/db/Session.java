package com.jslib.unit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.jslib.unit.util.Classes;

public class Session
{
  private final Driver driver;
  private boolean verbose;

  public Session(Driver driver)
  {
    Classes.forName(driver.getName());
    this.driver = driver;
  }

  public void setVerbose(boolean verbose)
  {
    this.verbose = verbose;
  }

  public void doWork(Work work) throws SQLException
  {
    work.setDriver(driver);
    work.setVerbose(verbose);

    Connection connection = null;
    try {
      connection = DriverManager.getConnection(driver.getUrl(), driver.getUser(), driver.getPassword());
    }
    catch(SQLException e) {
      if(e.getMessage().startsWith("Access denied")) {
        String message = "Database not found or access denied for user '%s' using password '%s'. Implicit database user and password should be database name.";
        e = new SQLException(String.format(message, driver.getUser(), driver.getPassword()));
      }
      throw e;
    }
    connection.setAutoCommit(true);

    try {
      work.execute(connection);
    }
    finally {
      if(connection != null) {
        connection.close();
      }
    }
  }
}
