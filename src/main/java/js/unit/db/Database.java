package js.unit.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import js.unit.util.Classes;

public final class Database
{
  private final Session session;

  public Database(String cfg) throws IOException
  {
    Map<String, String> properties = new HashMap<>();

    BufferedReader reader = new BufferedReader(new InputStreamReader(Classes.getResourceAsStream(cfg)));
    String line;
    while((line = reader.readLine()) != null) {
      int index = line.indexOf("<p");
      if(index == -1) {
        continue;
      }
      index = line.indexOf('"', index);

      int start = index + 1;
      int end = line.indexOf('"', start);
      String name = line.substring(start, end);

      start = line.indexOf('>', end) + 1;
      end = line.indexOf('<', start);
      properties.put(name, line.substring(start, end));
    }

    session = new Session(new Driver(properties));
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
    session.doWork(new CleanDB());

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
