package js.unit.db;

public class MicrosoftSQLDriver extends Driver {
	public MicrosoftSQLDriver(String address, String catalog, String user, String password) {
		this.name = "net.sourceforge.jtds.jdbc.Driver";
		this.catalog = catalog;
		this.schema = "DBO";
		this.user = user;
		this.password = password;
		this.url = String.format("jdbc:jtds:sqlserver://%s:1433/%s;user=%s;password=%s;encrypt=false", address, catalog, user, password);
	}
}
