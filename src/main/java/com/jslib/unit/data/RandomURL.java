package com.jslib.unit.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.jslib.unit.JsUnitException;
import com.jslib.unit.util.Strings;

public final class RandomURL implements RandomValue {
	private static final int MAX_LENGTH = 128;

	private static final RandomString randomString = new RandomString();
	private static final Random random = new Random();

	private static final String[] PROTOCOLS = { "http", "ftp" };

	private static final List<String> TLDS = new ArrayList<String>();
	static {
		try {
			InputStream stream = RandomURL.class.getResourceAsStream("tlds-alpha-by-domain.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				if (line.startsWith("#"))
					continue;
				line = line.toLowerCase();
				if (line.startsWith("x"))
					continue;
				TLDS.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object value(int maxLength) {
		try {
			if (maxLength > 0 && maxLength < 20)
				throw new IllegalArgumentException("URL can't have less that 20 chars.");
			int length = maxLength != 0 ? maxLength : MAX_LENGTH;
			String prefix = PROTOCOLS[random.nextInt(PROTOCOLS.length)] + "://";
			return new URL(prefix + host(length - prefix.length()));
		} catch (MalformedURLException e) {
			throw new JsUnitException(e);
		}
	}

	public String host(int maxLength) {
		int length = maxLength != 0 ? maxLength : MAX_LENGTH;
		int domainElementsCount = random.nextInt(4);
		if (domainElementsCount == 0)
			domainElementsCount = 1;
		int dotsCount = domainElementsCount - 1;

		String tld = TLDS.get(random.nextInt(TLDS.size()));
		int domainElementLength = (length - tld.length() - dotsCount) / domainElementsCount;

		Collection<String> names = randomString.names(domainElementsCount, domainElementLength);
		names.add(tld);
		return Strings.join(names, ".");
	}
}
