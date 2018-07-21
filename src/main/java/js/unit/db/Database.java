package js.unit.db;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Database
{
  private final Session session;

  public Database(String database)
  {
    this(database, database, database);
  }

  public Database(String database, String user)
  {
    this(database, user, user);
  }

  public Database(String database, String user, String password)
  {
    session = new Session(new MySQLDriver("localhost", database, user, password));
  }

  public Database(Driver driver)
  {
    session = new Session(driver);
  }

  public void setVerbose(boolean verbose)
  {
    session.setVerbose(verbose);
  }

  public void clear() throws SQLException
  {
    session.doWork(new CleanDB());
  }

  public void load(InputStream stream) throws SQLException
  {
    DatabaseDescriptor dataSet = new DatabaseDescriptor(stream);
    Iterator<RowDescriptor> it = dataSet.getRows();
    while(it.hasNext()) {
      session.doWork(new InsertTableValues(it.next()));
    }
  }

  static class Table
  {
    String name;
    List<Table> foreignKeys = new ArrayList<Table>();
    List<Table> dependencies = new ArrayList<Table>();

    Table(String name)
    {
      this.name = name;
    }

    void addForeignKey(Table foreignKey)
    {
      this.foreignKeys.add(foreignKey);
      foreignKey.dependencies.add(this);
    }
  }
}
