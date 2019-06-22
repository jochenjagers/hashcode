package gingerninjas.util.uploader;

import java.io.FileInputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cookies
{
	private static final Logger		logger	= LogManager.getLogger(Cookies.class);

	private static Properties		cookieProperties;
	private static CookieManager	cookieManager;

	public static String			TOKEN_URL;

	static
	{
		try
		{
			cookieProperties = new Properties();
			cookieProperties.load(new FileInputStream("../cookies.properties"));
			TOKEN_URL = cookieProperties.getProperty("token.url");

			cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);

			String key, value, domain;
			for(int i = 1; i < 100; i++)
			{
				if(cookieProperties.containsKey(i + ".key"))
				{
					key = cookieProperties.getProperty(i + ".key");
					value = cookieProperties.getProperty(i + ".value");
					domain = cookieProperties.getProperty(i + ".domain");
					addCookie(key, value, domain);
				}
				else
				{
					break;
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Cookies konnten nicht erzeugt werden.");
		}
	}

	private static void addCookie(String key, String value, String domain) throws URISyntaxException
	{
		HttpCookie cookie = new HttpCookie(key, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieManager.getCookieStore().add(new URI("https://" + domain + "/"), cookie);
	}
}
