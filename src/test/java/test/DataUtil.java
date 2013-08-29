package test;

import junit.framework.Assert;

import java.io.InputStream;
import java.net.URL;

/**
 * Utilities to retrieve test data.
 */
public class DataUtil {

	public static InputStream getStream(String resource) {
		return checkNotNull(DataUtil.class.getResourceAsStream(resource), resource);
	}

	public static URL getUrl(String resource) {
		return checkNotNull(DataUtil.class.getResource(resource), resource);
	}

	public static String getFile(String resource) {
		return getUrl(resource).getFile();
	}

	private static <T> T checkNotNull(T t, String resource) {
		Assert.assertNotNull('\'' + resource + "' could not be retrieved", t);
		return t;
	}

}
