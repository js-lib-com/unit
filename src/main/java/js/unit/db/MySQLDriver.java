package js.unit.db;

public class MySQLDriver extends Driver {
	public MySQLDriver(String address, String catalog, String user, String password) {
		this.name = "com.mysql.jdbc.Driver";
		this.catalog = catalog;
		this.schema = null;
		this.user = user;
		this.password = password;
	    this.url = String.format("jdbc:mysql://%s:3306/%s?%s", address, catalog, "useUnicode=true&characterEncoding=UTF-8");
		this.tableQuotationMark = "`";
	}
}
