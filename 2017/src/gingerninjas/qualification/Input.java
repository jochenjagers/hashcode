package gingerninjas.qualification;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gingerninjas.BaseInput;

public class Input extends BaseInput
{
	protected ArrayList<Endpoint> endpoints;
	
	protected ArrayList<Video> videos;
	
	protected Map<Integer, CacheServer> servers;
	
	protected int totalRequests = 0;
	
	public Input(String name)
	{
		super(name);
	}

	public ArrayList<Video> getVideos()
	{
		return videos;
	}

	public ArrayList<Endpoint> getEndpoints()
	{
		return endpoints;
	}

	public Map<Integer, CacheServer> getServers()
	{
		return servers;
	}

	@Override
	protected void parse(BufferedReader reader) throws IOException
	{
		String line = null;		

		line = reader.readLine();
		String[] firstLine = line.split(" ");
		String[] lineN;

		int nVideos = Integer.parseInt(firstLine[0]);
		int nEndpoints = Integer.parseInt(firstLine[1]);
		int nRequests = Integer.parseInt(firstLine[2]);
		int nCaches = Integer.parseInt(firstLine[3]);
		int cacheSize = Integer.parseInt(firstLine[4]);

		
		
		this.videos = new ArrayList<>(nVideos);
		line = reader.readLine();
		String[] vids = line.split(" ");
		int vidId = 0;
		for(String v: vids)
		{
			this.videos.add(new Video(vidId, Integer.parseInt(v)));
			vidId++;
		}
		
		this.servers = new TreeMap<>();
		for(int c = 0; c < nCaches; c++)
		{
			this.servers.put(c, new CacheServer(c, cacheSize));
		}
				
		int cacheId;
		int cacheLatency;
		this.endpoints = new ArrayList<>(nEndpoints);
		Endpoint endpoint;
		for(int e = 0; e < nEndpoints; e++)
		{
			line = reader.readLine();
			logger.debug("endpoint: " + line);
			firstLine = line.split(" ");
			
			int dcLatency = Integer.parseInt(firstLine[0]);
			int connectedCaches = Integer.parseInt(firstLine[1]);
			
			Map<CacheServer, Integer> latencies = new HashMap<>();
			endpoint = new Endpoint(e, dcLatency, latencies);

			for(int c = 0; c < connectedCaches; c++)
			{
				line = reader.readLine();
				logger.debug("  connected cache: " + line);
				lineN = line.split(" ");
				
				cacheId = Integer.parseInt(lineN[0]);
				cacheLatency = Integer.parseInt(lineN[1]);
				
				endpoint.getLatencies().put(this.servers.get(cacheId), cacheLatency);
				
				this.servers.get(cacheId).getEndpoints().add(endpoint);
			}
			
			this.endpoints.add(endpoint);
			
		}
		
		int videoId;
		int endpointId;
		int numRequests;
		for(int r = 0; r < nRequests; r++)
		{
			line = reader.readLine();
			logger.debug("requests: " + line);
			lineN = line.split(" ");
			
			videoId = Integer.parseInt(lineN[0]);
			endpointId = Integer.parseInt(lineN[1]);
			numRequests = Integer.parseInt(lineN[2]);
			this.totalRequests += numRequests;
			if(this.endpoints.get(endpointId).getRequests().containsKey(this.videos.get(videoId))) {
				numRequests += this.endpoints.get(endpointId).getRequests().get(this.videos.get(videoId));
			}
			this.endpoints.get(endpointId).getRequests().put(this.videos.get(videoId), numRequests);
		}
	}
	
	public int getTotalRequests()
	{
		return totalRequests;
	}

	public void reset()
	{
		for(CacheServer s: this.servers.values())
		{
			s.reset();
		}
		for(Video v: this.videos)
		{
			v.reset();
		}
	}
}
