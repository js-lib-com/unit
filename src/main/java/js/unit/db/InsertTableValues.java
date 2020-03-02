package js.unit.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import js.unit.util.Strings;

class InsertTableValues extends Work
{
  private static enum Type
  {
    NONE, NULL, STRING, INTEGER, BOOLEAN, DATE
  };

  // date format is not thread safe but this utility class is not designed for multi-thread
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private RowDescriptor row;

  public InsertTableValues(RowDescriptor row)
  {
    this.row = row;
  }

  @Override
  public Object execute(Connection connection) throws SQLException
  {
    if(row.isEmpty()) {
      return null;
    }

    List<String> columns = new ArrayList<>();
    List<String> parameters = new ArrayList<>();
    Iterator<ColumnDescriptor> it = row.getColumns();
    while(it.hasNext()) {
      columns.add(it.next().getName());
      parameters.add("?");
    }

    StringBuilder sql = new StringBuilder("INSERT INTO ");

    if(driver.hasSchema()) {
      sql.append(driver.getTableQuotationMark());
      sql.append(driver.getSchema());
      sql.append(driver.getTableQuotationMark());
      sql.append('.');
    }

    sql.append(driver.getTableQuotationMark());
    sql.append(row.getTableName());
    sql.append(driver.getTableQuotationMark());

    sql.append(" (");
    for(int i = 0; i < columns.size(); ++i) {
      if(i > 0) {
        sql.append(',');
      }
      sql.append(driver.getTableQuotationMark());
      sql.append(columns.get(i));
      sql.append(driver.getTableQuotationMark());
    }
    sql.append(") ");

    sql.append("VALUES(");
    sql.append(Strings.join(parameters, ","));
    sql.append(")");

    if(verbose) {
      System.out.println(sql.toString());
    }

    PreparedStatement ps = connection.prepareStatement(sql.toString());
    int index = 1;
    it = row.getColumns();
    while(it.hasNext()) {
      ps.setObject(index++, getValue(it.next()));
    }
    ps.execute();

    return null;
  }

  private Object getValue(ColumnDescriptor column)
  {
    String typeValue = column.getType();
    if(typeValue == null) {
      typeValue = "string";
    }
    String value = column.getValue();
    if(value.isEmpty()) {
      typeValue = "null";
    }
    InsertTableValues.Type type = Type.valueOf(typeValue.toUpperCase());

    switch(type) {
    case NULL:
      return null;
    case BOOLEAN:
      return Boolean.parseBoolean(value);
    case INTEGER:
      return Integer.parseInt(value);
    case DATE:
      return dateFormat.parse(value, new ParsePosition(0));
    default:
      break;
    }
    return value;
  }
}