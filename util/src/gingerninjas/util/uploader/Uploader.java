package gingerninjas.util.uploader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gingerninjas.BaseOutput;

public class Uploader
{
	private static final Logger	logger			= LogManager.getLogger(Uploader.class);

	private static String		accessToken		= null;
	private static long			accessTokenTime	= 0;
	private static String		sourceId		= null;

	private static final boolean online		= true;
	
	static
	{
		accessTokenTime = System.currentTimeMillis();
		accessToken = getAccessToken();
	}

	public static String getAccessToken()
	{
		if(online) {
			try
			{
				// public static String TOKEN_URL = "...";
				JSONObject value = getData(Cookies.TOKEN_URL, false, false);
				logger.info("New Token: " + "Bearer " + value.getString("access_token"));
				return "Bearer " + value.getString("access_token");
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
			}
		}
		return null;
	}

	private static String request(String url, boolean post)
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
			yc.setRequestProperty("Authorization", accessToken);

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

	private static String createUrl()
	{
		JSONObject value = getData("https://hashcode-judge.appspot.com/api/judge/v1/upload/createUrl");

		if(value != null)
		{
			return value.getString("value");
		}
		logger.error("Upload URL could not be created.");
		return null;
	}

	private static String uploadFile(File file)
	{
		logger.info("Uploading " + file.getName() + "...");

		// Request URL
		String url = createUrl();

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

	private static JSONObject getData(String command)
	{
		return getData(command, false, true);
	}

	private static JSONObject getData(String command, boolean post)
	{
		return getData(command, post, false);
	}
	
	private static JSONObject getData(String command, boolean post, boolean tokenCheck)
	{
		try
		{
			long timediff = System.currentTimeMillis() - accessTokenTime;
			if(tokenCheck && timediff > (1000 * 60 * 40))
			{
				// Letzte aktualisierung des accessTonkens ist mindestens 40 Minuten her
				synchronized(accessToken)
				{
					accessTokenTime = System.currentTimeMillis();
					accessToken = getAccessToken();
				}
			}
			String response = request(command, post);
			return new JSONObject(response);
		}
		catch(JSONException e)
		{
			logger.error("Could not load data. Login and update authorization token");
		}
		return null;
	}

	public static ArrayList<Round> getRounds()
	{
		ArrayList<Round> result = new ArrayList<>();
		if(online) {
			JSONObject obj = getData("https://hashcode-judge.appspot.com/api/judge/v1/rounds");
			if(obj != null)
			{
				JSONArray items = obj.getJSONArray("items");
				for(int i = 0; i < items.length(); i++)
				{
					result.add(new Round(items.getJSONObject(i)));
				}
			}
		} else {
				result.add(new Round("Online Qualification Round", "A - Example", "B - Lovely Landscapes", "C - Memorable Moments", "D - Pet Pictures", "E - Shiny Selfies"));
		}
		return result;
	}

	public static void loadHighScores(Round round)
	{
		if(online) {
			JSONObject obj = getData("https://hashcode-judge.appspot.com/api/judge/v1/submissions/" + round.getId());
			if(obj != null)
			{
				JSONArray items = obj.getJSONArray("items");
				for(int i = 0; i < items.length(); i++)
				{
					JSONObject submission = items.getJSONObject(i);
					if(submission.getBoolean("valid") == true && submission.getBoolean("scored") == true)
					{
						// Ergbnis ist valid und wurde bewertet
						String datasetId = submission.getJSONObject("dataSet").getString("id");
						for(int j = 0; j < round.getDatasets().size(); ++j)
						{
							if(round.getDatasets().get(j).getId().equals(datasetId))
							{
								round.getDatasets().get(j).setHighScore(submission.getInt("score"));
							}
						}
					}
				}
			}
		}
		else
		{
			try
			{
				File highscore = new File(round.getName() + "/highscore.properties");
				if(highscore.exists())
				{
					Properties p = new Properties();
					p.load(new FileInputStream(highscore));
					for(DataSet ds: round.getDatasets())
					{
						String key = ds.getName().substring(0, ds.getName().indexOf(" "));
						if(p.containsKey(key))
							ds.setHighScore((int) Double.parseDouble(p.getProperty(key)));
						else
							ds.setHighScore(0);
					}
				}
				else
				{
					for(DataSet ds: round.getDatasets())
					{
						ds.setHighScore(0);
					}
				}
			}
			catch(IOException e)
			{
				logger.error("could not load highscore from file", e); 
			}
		}
	}

	public static synchronized void publishResult(BaseOutput output)
	{
		ArrayList<Round> rounds = getRounds();

		for(Round r : rounds)
		{
			if(r.isRunning())
			{
				File folder = new File("./" + r.getName());
				// Ergebnisse nur für laufende Runden hochladen.
				if(!folder.isDirectory())
				{
					logger.error("Can't open result directory (" + r.getName() + ")");
					return;
				}
				// update highscores
				loadHighScores(r);
				if(online) {
	
					for(DataSet d : r.getDatasets())
					{
						if(d.getName().equalsIgnoreCase(output.getName()))
						{
							if(d.getHighScore() >= output.getScore())
							{
								logger.info("Eine gleiche oder höhere Punktzahl wurde bereits hochgeladen. Skipping upload. " + output.getScore());
								break;
							}
							try
							{
								output.toFile();
							}
							catch(IOException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							File result = new File("./" + r.getName() + "/" + d.getName() + ".out");
							if(!result.isFile())
							{
								logger.error("Can't find result for dataset " + d.getName() + ". Skipping upload.");
								break;
							}
							try
							{
								// Do Upload
								String resultId = uploadFile(result);
								if(sourceId == null)
								{
									// upload source file if not available
									File source = new File("source.zip");
									sourceId = uploadFile(source);
								}
								if(sourceId != null && resultId != null)
								{
									JSONObject submission = getData(
											"https://hashcode-judge.appspot.com/api/judge/v1/submissions?dataSet=" + d.getId() + "&sourcesBlobKey="
													+ URLEncoder.encode(sourceId, "UTF-8") + "&submissionBlobKey=" + URLEncoder.encode(resultId, "UTF-8"),
											true);
									if(submission != null && submission.getString("id") != null && !submission.getString("id").equalsIgnoreCase(""))
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
				else
				{
					try
					{
						Properties p = new Properties();
						File highscore = new File(r.getName() + "/highscore.properties");
						if(highscore.exists())
							p.load(new FileInputStream(highscore));
						for(DataSet d : r.getDatasets())
						{
							if(d.getName().equalsIgnoreCase(output.getName()))
							{
								if(d.getHighScore() >= output.getScore())
								{
									logger.info("Eine gleiche oder höhere Punktzahl wurde bereits gespeichert. Skipping save. " + output.getScore());
									p.setProperty(d.getName().substring(0, d.getName().indexOf(" ")), "" + d.getHighScore());
								}
								else
								{
									p.setProperty(d.getName().substring(0, d.getName().indexOf(" ")), "" + output.getScore());
									logger.info("Save ok.");
								}
							}
						}
						p.store(new FileWriter(r.getName() + "/highscore.properties"), new Date().toString());
					}
					catch(IOException e)
					{
						logger.error("could not save highscore to file", e); 
					}
				}
			}
		}
	}

	public static void main(String[] args)
	{
		ArrayList<Round> rounds = Uploader.getRounds();
		for(int i = 0; i < rounds.size(); ++i)
		{
			Uploader.loadHighScores(rounds.get(i));
			System.out.println(rounds.get(i).toString());
		}
	}

}