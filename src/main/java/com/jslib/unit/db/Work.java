package com.jslib.unit.db;

import java.sql.Connection;
import java.sql.SQLException;

abstract class Work
{
  protected Driver driver;
  protected boolean verbose;

  public void setDriver(Driver driver)
  {
    this.driver = driver;
  }

  public void setVerbose(boolean verbose)
  {
    this.verbose = verbose;
  }

  public abstract Object execute(Connection connection) throws SQLException;
}
