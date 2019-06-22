package gingerninjas.qualification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gingerninjas.util.uploader.Uploader;

public class CacheServer
{
	private static final Logger	logger		= LogManager.getLogger(CacheServer.class);

	protected int				id;
	protected int				size;
	protected int				freeSpace;

	protected List<Video>		videos;

	protected List<Endpoint>	endpoints;

	public boolean				calcValue	= true;

	public CacheServer(int id, int size)
	{
		super();
		this.id = id;
		this.size = size;
		this.freeSpace = size;
		this.videos = new ArrayList<>();
		this.endpoints = new ArrayList<>();
	}

	public int getFreeSpace()
	{
		return freeSpace;
	}

	public int getId()
	{
		return id;
	}

	public int getSize()
	{
		return size;
	}

	public List<Video> getVideos()
	{
		return videos;
	}

	public List<Endpoint> getEndpoints()
	{
		return endpoints;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	public String toString()
	{
		return "Server: " + this.id + " Size: " + this.size;
	}

	public void reset()
	{
		this.videos.clear();
		this.freeSpace = this.size;
	}

	public synchronized boolean addVideo(Video v)
	{
		if(this.videos.contains(v))
		{
			return true;
		}
		if(this.freeSpace >= v.getSize())
		{
			this.videos.add(v);
			this.freeSpace -= v.getSize();
			int count = 0;
			for(Endpoint e : this.endpoints)
			{
				for(Map.Entry<CacheServer, Integer> s : e.getLatencies().entrySet())
				{
					if(!s.getKey().calcValue)
					{
						++count;
					}
					s.getKey().calcValue = true;
				}
			}
			logger.info(count + " Server müssen neu berechnet werden.");
			// logger.info("Left Space in Server: " + this.id + ": " + this.freeSpace);
			return true;
		}
		return false;
	}
}
