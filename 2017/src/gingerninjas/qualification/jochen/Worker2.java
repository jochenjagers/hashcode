package gingerninjas.qualification.jochen;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gingerninjas.qualification.CacheServer;
import gingerninjas.qualification.Endpoint;
import gingerninjas.qualification.Video;

public class Worker2 extends Thread
{
	protected transient final Logger	logger		= LogManager.getLogger(getClass());

	int									start;
	int									end;

	ArrayList<Endpoint>					endpoints;

	BlockingQueue<Integer>				sleepQueue	= new LinkedBlockingQueue<>();

	boolean								running		= true;

	BlockingQueue<Integer>				resultQueue;

	Video								bestVideo	= null;
	CacheServer							bestServer	= null;
	int									bestScore	= 0;

	Worker2(int start, int end, ArrayList<Endpoint> endpoints, BlockingQueue<Integer> q)
	{
		this.start = start;
		this.end = end;
		this.endpoints = endpoints;
		this.resultQueue = q;
		logger.info("New Worker for Endpoints from " + this.start + " to " + this.end);
		this.start();
	}

	private void searchVideo()
	{
		for(int i = this.start; i < this.end; ++i)
		{
			Endpoint e = this.endpoints.get(i);

			for(Map.Entry<Video, Integer> v : e.getRequests().entrySet())
			{
				CacheServer s = e.getFastestServerWithFreeSpaceAndVideoNotInCache(v.getKey());
				if(s != null)
				{
					int score = v.getValue() / e.getLatencies().get(s);
					if(score > bestScore)
					{
						bestServer = s;
						bestVideo = v.getKey();
						bestScore = score;
					}
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
				this.bestServer = null;
				this.bestVideo = null;
				this.bestScore = 0;
				this.searchVideo();
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				resultQueue.put(0);
				// logger.info("remover fertig");
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
