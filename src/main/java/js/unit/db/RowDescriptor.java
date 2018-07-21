package js.unit.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RowDescriptor {
	private final String tableName;
	private List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>();

	public RowDescriptor(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void addColumnDescritor(ColumnDescriptor column) {
		columns.add(column);
	}

	public Iterator<ColumnDescriptor> getColumns() {
		return columns.iterator();
	}
}
