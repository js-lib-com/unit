package js.unit.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import js.unit.db.Database.Table;

class CleanDB extends Work
{
  @Override
  public Object execute(Connection connection) throws SQLException
  {
    DatabaseMetaData meta = connection.getMetaData();
    ResultSet rs = meta.getTables(null, driver.getSchema(), null, new String[]
    {
      "TABLE"
    });

    Map<String, Table> tablesMap = new HashMap<String, Table>();
    while(rs.next()) {
      final String tableName = rs.getString(3);
      tablesMap.put(tableName, new Table(tableName));
    }

    for(Table table : tablesMap.values()) {
      rs = meta.getImportedKeys(null, null, table.name);
      while(rs.next()) {
        String foreignKeyTableName = rs.getString(3);
        // avoid self-referencing foreign keys
        if(foreignKeyTableName.equals(table.name)) continue;
        table.addForeignKey(tablesMap.get(foreignKeyTableName));
      }
    }

    List<Table> tablesToDrop = new ArrayList<Table>();
    collect(tablesToDrop, tablesMap.values());
    for(Table table : tablesToDrop) {
      Statement stm = connection.createStatement();
      final String sql = String.format("DELETE FROM %1$s" + table.name + "%1$s", driver.getTableQuotationMark());
      if(verbose) {
        System.out.println(sql);
      }
      stm.execute(sql);
    }

    return null;
  }

  private void collect(Collection<Table> tablesToDrop, Collection<Table> tablesHierarchy, int... guardArgument)
  {
    int guard = guardArgument.length == 1 ? guardArgument[0] : 0;
    if(++guard == 8) {
      return;
    }

    Iterator<Table> it = tablesHierarchy.iterator();
    while(it.hasNext()) {
      Table table = it.next();
      collect(tablesToDrop, table.dependencies, guard);
      if(!tablesToDrop.contains(table)) {
        tablesToDrop.add(table);
      }
      it.remove();
    }
  }
}