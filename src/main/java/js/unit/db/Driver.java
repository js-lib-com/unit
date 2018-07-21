package js.unit.db;

public class Driver
{
  protected String name;
  protected String catalog;
  protected String schema;
  protected String user;
  protected String password;
  protected String url;
  protected String tableQuotationMark = "";
  
  public String getName()
  {
    return name;
  }

  public String getCatalog()
  {
    return catalog;
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
