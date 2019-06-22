package gingerninjas.qualification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Endpoint
{
	protected int						id;
	protected Map<Video, Integer>		requests;

	protected int						datacenterLatency;

	protected Map<CacheServer, Integer>	latencies;

	public HashMap<Video, Boolean>		cache;

	public Endpoint(int id, int datacenterLatency, Map<CacheServer, Integer> latencies)
	{
		super();
		this.id = id;
		this.datacenterLatency = datacenterLatency;
		this.latencies = latencies;
		this.requests = new HashMap<>();

		this.cache = new HashMap<>();
	}

	public int getId()
	{
		return id;
	}

	public int getDatacenterLatency()
	{
		return datacenterLatency;
	}

	public Map<CacheServer, Integer> getLatencies()
	{
		return latencies;
	}

	public Map<Video, Integer> getRequests()
	{
		return requests;
	}

	public long getPoints()
	{
		long result = 0;
		for(Map.Entry<Video, Integer> request : requests.entrySet())
		{
			int minLatency = datacenterLatency;
			for(Map.Entry<CacheServer, Integer> latency : latencies.entrySet())
			{
				if(latency.getKey().videos.contains(request.getKey()) && latency.getValue() < minLatency)
				{
					minLatency = latency.getValue();
				}
			}
			result += (datacenterLatency - minLatency) * request.getValue();

		}
		return result;
	}

	public boolean isVideoInCache(Video v)
	{
		if(cache.containsKey(v))
		{
			synchronized(cache)
			{
				Boolean result = cache.get(v);
				return result;
			}
		}
		for(Map.Entry<CacheServer, Integer> e : latencies.entrySet())
		{
			if(e.getKey().getVideos().contains(v))
			{
				synchronized(cache)
				{
					cache.put(v, new Boolean(true));
					return true;
				}
			}
		}
		synchronized(cache)
		{
			cache.put(v, new Boolean(false));
			return false;
		}
	}

	public CacheServer getFastestServerWithFreeSpace(int space)
	{
		CacheServer result = null;
		int maxLat = Integer.MAX_VALUE;
		for(Map.Entry<CacheServer, Integer> e : latencies.entrySet())
		{
			if(e.getKey().getFreeSpace() >= space && e.getValue() < maxLat)
			{
				maxLat = e.getValue();
				result = e.getKey();
			}
		}
		return result;
	}

	public CacheServer getFastestServerWithFreeSpaceAndVideoNotInCache(Video v)
	{
		CacheServer result = null;
		int maxLat = Integer.MAX_VALUE;
		for(Map.Entry<CacheServer, Integer> e : latencies.entrySet())
		{
			if(e.getKey().getVideos().contains(v))
			{
				return null;
			}
			if(e.getKey().getFreeSpace() >= v.getSize() && e.getValue() < maxLat)
			{
				maxLat = e.getValue();
				result = e.getKey();
			}
		}
		return result;
	}
}
