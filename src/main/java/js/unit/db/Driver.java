package js.unit.db;

import java.util.HashMap;
import java.util.Map;

public class Driver
{
  private static final Map<String, String> QUOTATION_MARKS = new HashMap<>();
  static {
    QUOTATION_MARKS.put("com.mysql.jdbc.Driver", "`");
    QUOTATION_MARKS.put("oracle.jdbc.driver.OracleDriver", "\"");
  }

  protected String name;
  protected String catalog;
  protected String schema;
  protected String user;
  protected String password;
  protected String url;
  protected String tableQuotationMark = "";

  protected Driver()
  {
  }

  public Driver(Map<String, String> properties)
  {
    this.name = properties.get("connection.driver_class");
    this.catalog = properties.get("default_catalog");
    this.schema = properties.get("default_schema");
    this.user = properties.get("connection.username");
    this.password = properties.get("connection.password");
    this.url = properties.get("connection.url");

    if(this.catalog == null) {
      this.catalog = this.schema;
    }

    this.tableQuotationMark = QUOTATION_MARKS.get(this.name);
    if(this.tableQuotationMark == null) {
      this.tableQuotationMark = "";
    }
  }

  public String getName()
  {
    return name;
  }

  public String getCatalog()
  {
    return catalog;
  }

  public boolean hasSchema()
  {
    return schema != null;
  }

  public String getSchema()
  {
    return schema;
  }

  public String getUser()
  {
    return user;
  }

  public String getPassword()
  {
    return password;
  }

  public String getUrl()
  {
    return url;
  }

  public String getTableQuotationMark()
  {
    return tableQuotationMark;
  }
}
