package js.unit.db;

public class OracleDriver extends Driver {
	public OracleDriver(String address, String catalog, String user, String password) {
		this.name = "oracle.jdbc.driver.OracleDriver";
		this.catalog = null;
		this.schema = user.toUpperCase();
		this.user = user;
		this.password = password;
		this.url = String.format("jdbc:oracle:thin:@%s:1521:%s", address, catalog);
		this.tableQuotationMark = "\"";
	}
}
