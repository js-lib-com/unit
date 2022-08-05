package com.jslib.unit.db;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.jslib.unit.JsUnitException;

public class DatabaseDescriptor {
	public DatabaseDescriptor(InputStream stream) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(new Loader());
			reader.parse(new InputSource(stream));
		} catch (Exception e) {
			throw new JsUnitException("Fail to load database descriptor.");
		}
	}

	private List<RowDescriptor> rows = new ArrayList<RowDescriptor>();

	public Iterator<RowDescriptor> getRows() {
		return rows.iterator();
	}

	public class Loader extends DefaultHandler {
		/** Current element is document root. */
		private static final int LEVEL_ROOT = 1;
		/** Current element is a table row. */
		private static final int LEVEL_TABLE_ROW = 2;
		/** Current element is a table column. While processing table column value builder is updated. */
		private static final int LEVEL_TABLE_COLUMN = 3;

		private int level;
		private RowDescriptor row;
		private ColumnDescriptor column;
		private StringBuilder value = new StringBuilder();

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			level++;
			switch (level) {
			case LEVEL_ROOT:
				break;

			case LEVEL_TABLE_ROW:
				row = new RowDescriptor(qName);
				rows.add(row);
				break;

			case LEVEL_TABLE_COLUMN:
				column = new ColumnDescriptor(qName);
				row.addColumnDescritor(column);
				break;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (level == LEVEL_TABLE_COLUMN) {
				column.setValue(value.toString());
				value.setLength(0);
			}
			level--;
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (level == LEVEL_TABLE_COLUMN) {
				value.append(ch, start, length);
			}
		}
	}
}
