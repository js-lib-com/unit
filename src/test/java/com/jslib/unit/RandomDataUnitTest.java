package com.jslib.unit;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.jslib.unit.data.TestData;
import com.jslib.unit.util.Classes;
import com.jslib.unit.util.GType;
import com.jslib.unit.util.Types;

public class RandomDataUnitTest extends TestCase {
	public void testConstrainedObjectInitialization() throws Exception {
		Document configXML = parseXML(RandomDataUnitTest.class.getResourceAsStream("/test-data.xml"));
		TestData.config(configXML);

		int counter = 1000;
		while (counter-- > 0) {
			Person p = TestData.newInstance(Person.class);
			assertNotNull(p);
			assertEquals(0, (int)Classes.getFieldValue(p, "id"));
		}

		Car c = TestData.newInstance(Car.class);
		assertNotNull(c);
		assertNotNull(Classes.getFieldValue(c, "engine"));

		// take care to remove used configuration in order to avoid interfering with other tests
		TestData.config(null);
	}

	public void testNewInstanceOnInterface() {
		Customer customer = TestData.newInstance(Customer.class);
		assertNotNull(customer);
		assertValid(customer.getContact());
		assertValid(customer.getShipping());
		assertNotNull(customer.getState());
	}

	public void testRandomPrimitive() {
		Type[] types = new Type[] { char.class, Character.class, boolean.class, Boolean.class, byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class };
		for (Type t : types) {
			Object o = TestData.newInstance(t);
			assertNotNull(o);
		}
	}

	public void testRandomArray() {
		Type[] types = new Type[] { char[].class, Character[].class, boolean[].class, Boolean[].class, byte[].class, Byte[].class, short[].class, Short[].class, int[].class, Integer[].class, long[].class, Long[].class, float[].class, Float[].class, double[].class, Double[].class };
		for (Type t : types) {
			Object array = TestData.newInstance(t);
			assertNotNull(array);
		}

		for (;;) {
			TestData.Context context = new TestData.Context();
			Person[] persons = context.createObject(Person[].class);
			assertNotNull(persons);
			if (persons.length > 0) {
				assertValid(persons[0]);
				break;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void testRandomCollection() {
		Class[] classes = new Class[] { char.class, Character.class, boolean.class, Boolean.class, byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class };
		for (Class clazz : classes) {
			for (;;) {
				Collection collection = TestData.newInstance(Collection.class, clazz);
				assertTrue(collection instanceof Vector);
				if (collection.size() > 0) {
					Object o = collection.iterator().next();
					assertTrue(o.getClass() == Types.getBoxingClass(clazz));
					break;
				}
			}
		}

		Collection collection = TestData.newInstance(Collection.class, int.class);
		assertTrue(collection instanceof Vector);

		collection = TestData.newInstance(List.class, int.class);
		assertTrue(collection instanceof ArrayList);
	}

	public void testRandomDate() {
		assertEquals(Date.class, TestData.newInstance(Date.class).getClass());
		assertEquals(java.sql.Date.class, TestData.newInstance(java.sql.Date.class).getClass());
		assertEquals(Time.class, TestData.newInstance(Time.class).getClass());
		assertEquals(Timestamp.class, TestData.newInstance(Timestamp.class).getClass());

		TestData.Context context = new TestData.Context();
		try {
			context.createObject(Date.class, 10);
			fail("Trying to limit date length should rise exception.");
		} catch (IllegalArgumentException e) {
			assertEquals("Random date does not support maximum length.", e.getMessage());
		}

		try {
			context.createObject(new Date() {
				private static final long serialVersionUID = 1L;

			}.getClass());
			fail("User defined date should rise exception.");
		} catch (IllegalStateException e) {
			assertTrue(e.getMessage().startsWith("No random generator for "));
		}
	}

	public void testRandomEmailAddr() {

	}

	public void testRandomEnum() {

	}

	public void testRandomFile() {

	}

	public void testRandomMap() {
		Map<String, String> m1 = TestData.newInstance(Map.class, String.class, String.class);
		TestCase.assertNotNull(m1);
		ParameterizedType listType = new GType(List.class, URL.class);
		ParameterizedType mapType = new GType(Map.class, String.class, listType);
		Map<String, List<URL>> m2 = TestData.newInstance(mapType);
		TestCase.assertNotNull(m2);
	}

	public void testRandomMessageID() {

	}

	public void testRandomPassword() {

	}

	public void testRandomString() {

	}

	public void testRandomTimeZone() {
		TimeZone timezone = TestData.newInstance(TimeZone.class);
		assertNotNull(timezone);
		assertEquals(timezone, TimeZone.getTimeZone(timezone.getID()));
	}

	public void testRandomURL() {
		TestData.Context context = new TestData.Context();
		int counter = 1000;
		while (counter-- > 0) {
			URL url = context.createObject(URL.class, 20);
			assertTrue(url.toExternalForm().length() <= 20);
		}
		URL url = TestData.newInstance(URL.class);
		assertTrue(url.toString().startsWith("http") || url.toString().startsWith("ftp"));
	}

	public void testRandomObject() {
		Car car = TestData.newInstance(Car.class);
		assertValid(car);
	}

	public void testCircularDependecies() {
		Parent parent = TestData.newInstance(Parent.class);
		assertNotNull(parent);
		assertNotNull(Classes.getFieldValue(parent, "name"));
		List<Child> children = Classes.getFieldValue(parent, "children");
		assertNotNull(children);
		for (Child child : children) {
			assertNotNull(child);
			assertNotNull(Classes.getFieldValue(child, "name"));
			assertNotNull(Classes.getFieldValue(child, "parent"));
		}
	}

	private void assertValid(Person person) {
		assertNotNull(person);
		assertNotNull(Classes.getFieldValue(person, "name"));
		assertNotNull(Classes.getFieldValue(person, "surname"));
		assertNotNull(Classes.getFieldValue(person, "webPage"));
		assertNotNull(Classes.getFieldValue(person, "landline"));
		assertNotNull(Classes.getFieldValue(person, "mobile"));
		assertNotNull(Classes.getFieldValue(person, "birthday"));
		assertNotNull(Classes.getFieldValue(person, "state"));
	}

	private void assertValid(Car car) {
		assertNotNull(car);
		assertNotNull(Classes.getFieldValue(car, "managerBirth"));
		assertNotNull(Classes.getFieldValue(car, "manufactureDate"));
		assertNotNull(Classes.getFieldValue(car, "purchaseDate"));
		assertNotNull(Classes.getFieldValue(car, "model"));
		assertNotNull(Classes.getFieldValue(car, "owners"));
		assertNotNull(Classes.getFieldValue(car, "transactions"));

		Car.Engine engine = Classes.getFieldValue(car, "engine");
		assertNotNull(engine);
		assertNotNull(Classes.getFieldValue(engine, "model"));
		assertNotNull(Classes.getFieldValue(engine, "specifications"));

		Car.Wheel[] wheels = Classes.getFieldValue(car, "wheels");
		assertNotNull(wheels);
		for (Car.Wheel wheel : wheels) {
			assertNotNull(wheel);
			assertNotNull(Classes.getFieldValue(wheel, "position"));
		}
	}

	private Document parseXML(InputStream stream) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(stream);
	}

	// ----------------------------------------------------
	// FIXTURE

	public static final class Person {
		int id;
		String name;
		String surname;
		URL webPage;
		String landline;
		String mobile;
		Date birthday;
		State state = State.NONE;
	}

	public static enum State {
		NONE, ACTIVE, DETACHED
	}

	public static final class Parent {
		String name;
		List<Child> children;
	}

	public static final class Child {
		Parent parent;
		String name;
	}

	public static final class Car {
		public final static class Specifications {
			int capacity;
			int[] torque;
		}

		public final static class Engine {
			String model;
			Specifications specifications;
		}

		public final static class Wheel {
			public static enum Position {
				NONE, FRONT_LEFT, FRONT_RIGHT, BACK_LEFT, BACK_RIGHT
			};

			Position position;
			int radius;
			double pressure;
		}

		public static enum Model {
			OPEL_CORSA_1_2, LOGAN_1_6
		}

		Engine engine;
		Timestamp managerBirth;
		Date manufactureDate;
		Date purchaseDate;
		Model model;
		boolean registered;
		Wheel[] wheels;
		List<Person> owners;
		Map<String, Person> transactions;
	}

	public static interface Customer {
		Person getContact();

		Car getShipping();

		State getState();
	}
}
