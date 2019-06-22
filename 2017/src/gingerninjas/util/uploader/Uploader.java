package gingerninjas.util.uploader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.*;

public class Uploader
{
	private static final Logger	logger		= LogManager.getLogger(Uploader.class);

	private String				accessToken	= this.getAccessToken();

	static
	{
		Cookies.init();
	}

	public String getAccessToken()
	{
		try
		{

			URL ur = new URL(
					"https://accounts.google.com/o/oauth2/auth?client_id=702507943014-es33c7n1efdijf0ppkmss62kg22hsd4e.apps.googleusercontent.com&immediate=true&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&include_granted_scopes=true&proxy=oauth2relay480143864&redirect_uri=postmessage&origin=https%3A%2F%2Fhashcodejudge.withgoogle.com&response_type=token&gsiwebsdk=1&state=629846760%7C0.2613824791&authuser=0&jsh=m%3B%2F_%2Fscs%2Fapps-static%2F_%2Fjs%2Fk%3Doz.gapi.de.K62UtohXC08.O%2Fm%3D__features__%2Fam%3DAQ%2Frt%3Dj%2Fd%3D1%2Frs%3DAGLTcCPI_V12BWLgcKKRuWMnzztkz7BrHg");
			HttpURLConnection yc = (HttpURLConnection) ur.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			String data = "";
			while((inputLine = in.readLine()) != null)
				data += inputLine;
			in.close();
			int start = data.indexOf("access_token=") + 13;
			int end = data.indexOf("&", start);
			return "Bearer " + data.substring(start, end);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
		return null;
	}

	private String request(String url, boolean post)
	{
		String result = "";
		try
		{
			URL ur = new URL(url);

			HttpURLConnection yc = (HttpURLConnection) ur.openConnection();

			if(post)
			{
				yc.setRequestMethod("POST");
				yc.setFixedLengthStreamingMode(0);
				yc.setDoOutput(true);
			}
			yc.setRequestProperty("Authorization", this.accessToken);

			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			while((inputLine = in.readLine()) != null)
				result += inputLine;
			in.close();
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return result;
	}

	private String createUrl()
	{
		JSONObject value = this.getData("https://hashcode-judge.appspot.com/_ah/api/judge/v1/upload/createUrl");

		if(value != null)
		{
			return value.getString("value");
		}
		logger.error("Upload URL could not be created.");
		return null;
	}

	private String uploadFile(File file)
	{
		logger.info("Uploading " + file.getName() + "...");
		
		// Request URL
		String url = this.createUrl();

		if(url != null)
		{
			try
			{
				MultipartUtility multipart = new MultipartUtility(url, "UTF-8");
				multipart.addFilePart("file", file);
				String response = multipart.finish();
				JSONObject obj = new JSONObject(response);
				JSONArray arr = obj.getJSONArray("file");
				if(arr.length() > 0)
				{
					logger.info("Upload ok.");
					return arr.getString(0);
				}
				throw new Exception("Wrong response");
			}
			catch(Exception e)
			{
				logger.error("Error during upload. " + e.getMessage());
			}
		}
		return null;
	}

	private JSONObject getData(String command)
	{
		return this.getData(command, false);
	}

	private JSONObject getData(String command, boolean post)
	{
		try
		{
			String response = this.request(command, post);
			return new JSONObject(response);
		}
		catch(JSONException e)
		{
			logger.error("Could not load data. Login and update authorization token");
		}
		return null;
	}

	public ArrayList<Round> getRounds()
	{
		ArrayList<Round> result = new ArrayList<>();

		JSONObject obj = this.getData("https://hashcode-judge.appspot.com/_ah/api/judge/v1/rounds");
		if(obj != null)
		{
			JSONArray items = obj.getJSONArray("items");
			for(int i = 0; i < items.length(); i++)
			{
				result.add(new Round(items.getJSONObject(i)));
			}
		}
		return result;
	}

	public void uploadResults(long timestamp, String name)
	{
		ArrayList<Round> rounds = this.getRounds();

		for(Round r : rounds)
		{
			if(r.isRunning())
			{
				File folder = new File("./" + r.getName());
				// Ergebnisse nur für laufende Runden hochladen.
				String sourceId = null;
				if(!folder.isDirectory())
				{
					logger.error("Can't open result directory (" + r.getName() + ")");
					return;
				}
				for(DataSet d : r.getDatasets())
				{
					logger.info("Searching ./" + r.getName() + "/" + d.getName() + "." + timestamp + ".out");
					File result = new File("./" + r.getName() + "/" + d.getName() + "." + timestamp + ".out");
					if(name != null && !name.equals(d.getName())) {
						logger.info("Skipping " + d.getName() + "(Only Upload " + name + ")");
						continue;
					}
					if(!result.isFile())
					{
						logger.error("Can't find result for dataset " + d.getName() + ". Skipping upload.");
						continue;
					}
					try
					{
						// Do Upload
						String resultId = this.uploadFile(result);
						if(sourceId == null)
						{
							// upload source file if not available
							File source = new File(timestamp + ".zip");
							sourceId = this.uploadFile(source);
						}
						if(sourceId != null && resultId != null)
						{
							JSONObject submission = this.getData(
									"https://hashcode-judge.appspot.com/_ah/api/judge/v1/submissions?dataSet=" + d.getId() + "&sourcesBlobKey="
											+ URLEncoder.encode(sourceId, "UTF-8") + "&submissionBlobKey=" + URLEncoder.encode(resultId, "UTF-8"),
									true);
							if(submission != null && submission.getString("id") != null && submission.getString("id") != "")
							{
								logger.info("Submission ok.");
							}
							else
							{
								throw new Exception("Submission failed.");
							}
						}
					}
					catch(Exception e)
					{
						logger.error("Upload failed. " + e.getMessage());
					}
				}
			}
		}
	}
}
