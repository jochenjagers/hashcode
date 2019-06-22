package gingerninjas.util.uploader;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cookies
{
	private static final Logger		logger	= LogManager.getLogger(Cookies.class);

	private static CookieManager	cookieManager;

	private static void addCookie(String key, String value, String domain) throws URISyntaxException
	{
		HttpCookie cookie = new HttpCookie(key, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieManager.getCookieStore().add(new URI("https://" + domain + "/"), cookie);
	}

	static void init()
	{
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		try
		{

		}
		catch(Exception e)
		{
			logger.error("Cookies konnten nicht erzeugt werden.");
		}
	}

}
