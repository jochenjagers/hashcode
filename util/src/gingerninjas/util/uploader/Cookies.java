package gingerninjas.util.uploader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cookies {
	private static final Logger logger = LogManager.getLogger(Cookies.class);

	private static Properties cookieProperties;
	private static CookieManager cookieManager;

	public static String TOKEN_URL;

	static {
		try {
			cookieProperties = new Properties();
			cookieProperties.load(new FileInputStream("../cookies.properties"));
			TOKEN_URL = cookieProperties.getProperty("token.url");

			cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);
			loadCookies();
			/*
			 * String key, value, domain; for(int i = 1; i < 100; i++) {
			 * if(cookieProperties.containsKey(i + ".key")) { key =
			 * cookieProperties.getProperty(i + ".key"); value =
			 * cookieProperties.getProperty(i + ".value"); domain =
			 * cookieProperties.getProperty(i + ".domain"); logger.debug("adding cookie: '"
			 * + key + "'"); addCookie(key, value, domain); } else { break; } }
			 */
		} catch (Exception e) {
			logger.error("Cookies konnten nicht erzeugt werden.");
		}
	}

	public static void addCookie(String key, String value, String domain) throws URISyntaxException {
		HttpCookie cookie = new HttpCookie(key, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieManager.getCookieStore().add(new URI("https://" + domain + "/"), cookie);
	}

	public static String getCookie(String key, String domain) throws URISyntaxException {
		List<HttpCookie> cookies = cookieManager.getCookieStore().get(new URI("https://" + domain + "/"));
		for (HttpCookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(key))
				return cookie.getValue();
		}
		return null;
	}
	
	public static void printCookies()
	{
		for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
			logger.info(cookie.getDomain() + "\t" + cookie.getName() + "\t" + cookie.getValue());
		}
	}

	private static void loadCookies() {
		FileReader fr = null;
		BufferedReader br = null;
		try
		{
			fr = new FileReader("../cookies.txt");
			br = new BufferedReader(fr);
			
			String line = null;
			while((line = br.readLine()) != null) {
				logger.debug("endpoint: " + line);
				ArrayList<String> splittedLine = new ArrayList<>(Arrays.asList(line.split("\t")));
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if(br != null)
			{
				try
				{
					br.close();
				}
				catch(IOException e)
				{
					// ignore
				}
			}
			if(fr != null)
			{
				try
				{
					fr.close();
				}
				catch(IOException e)
				{
					// ignore
				}
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(TOKEN_URL);
		System.out.println(cookieManager.getCookieStore().getCookies());
	}
}
