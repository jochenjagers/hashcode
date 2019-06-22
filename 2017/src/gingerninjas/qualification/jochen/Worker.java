package gingerninjas.qualification.jochen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gingerninjas.qualification.CacheServer;
import gingerninjas.qualification.Endpoint;
import gingerninjas.qualification.Video;

public class Worker extends Thread
{
	protected transient final Logger	logger		= LogManager.getLogger(getClass());

	int									start;
	int									end;

	ArrayList<Video>					videos;

	BlockingQueue<Integer>				sleepQueue	= new LinkedBlockingQueue<>();

	boolean								running		= true;

	BlockingQueue<Integer>				resultQueue;

	Map<Integer, CacheServer>			servers;

	ServerVideoPair						bestVideo	= new ServerVideoPair();

	HashMap<ServerVideoPair, Integer>	cache		= new HashMap<>();

	Worker(int start, int end, ArrayList<Video> videos, BlockingQueue<Integer> q, Map<Integer, CacheServer> servers)
	{
		this.start = start;
		this.end = end;
		this.videos = videos;
		this.resultQueue = q;
		this.servers = servers;
		logger.info("New Worker for Endpoints from " + this.start + " to " + this.end);
		this.start();
	}

	private void searchVideo()
	{
		this.bestVideo.p = 0;
		this.bestVideo.s = null;
		this.bestVideo.v = null;

		for(int i = this.start; i < this.end; ++i)
		{
			Video v = videos.get(i);
			int maxVideoScore = 0;
			CacheServer maxVideoScoreServer = null;
			for(Map.Entry<Integer, CacheServer> s : servers.entrySet())
			{
				if(s.getValue().getFreeSpace() < v.getSize() || s.getValue().getVideos().contains(v))
				{
					// Überspringen wenn der Server das Video schon enthält oder der Platz auf dem
					// Server nicht reicht
					continue;
				}
				int maxVideoScorePerServer = 0;
				ServerVideoPair svp = new ServerVideoPair(v, s.getValue(), 0);

				if(s.getValue().calcValue)
				{
					for(Endpoint e : s.getValue().getEndpoints())
					{
						if(!e.isVideoInCache(v) && e.getRequests().containsKey(v))
						{
							// Endpoint hat interesse an diesem Video und es ist im cache noch nicht
							// vorhanden
							maxVideoScorePerServer += (e.getDatacenterLatency() - (e.getLatencies().get(s.getValue()))) * e.getRequests().get(v);
						}
					}
					cache.put(svp,  maxVideoScorePerServer);
				}
				else
				{
					//logger.info("Value from Cache");
					maxVideoScorePerServer = cache.get(svp);
				}
				if(maxVideoScorePerServer > maxVideoScore)
				{
					maxVideoScore = maxVideoScorePerServer;
					maxVideoScoreServer = s.getValue();

				}
			}
			if(maxVideoScoreServer != null)
			{
				float score = (float) maxVideoScore / (float) v.getSize();
				if(score > this.bestVideo.p)
				{
					this.bestVideo.v = v;
					this.bestVideo.s = maxVideoScoreServer;
					this.bestVideo.p = score;
				}
			}
		}

	}

	public void wakeUp()
	{
		try
		{
			sleepQueue.put(0);
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run()
	{
		while(this.running)
		{
			try
			{
				sleepQueue.take();
				this.searchVideo();
				resultQueue.put(0);
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
