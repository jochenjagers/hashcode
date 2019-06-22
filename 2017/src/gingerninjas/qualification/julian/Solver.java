package gingerninjas.qualification.julian;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.ThreadContext;

import gingerninjas.qualification.CacheServer;
import gingerninjas.qualification.Endpoint;
import gingerninjas.qualification.Input;
import gingerninjas.qualification.Output;
import gingerninjas.qualification.Video;

public class Solver extends gingerninjas.qualification.Solver
{
	public static final int NUM_THREADS = 4;

	@Override
	protected Output solve(Input input, Random random)
	{
		Map<String, Object> args = new HashMap<>();
		Output output = createOutput(in.getName(), args);
		output.setServers(input.getServers().values());

		final Queue<Endpoint> endpoints = new ConcurrentLinkedQueue<>(input.getEndpoints());
		
		logger.info("sorting videos per endpoint");
		HashMap<Endpoint, List<Entry<Video, Integer>>> videosPerEndpoint = new HashMap<>();
		List<Entry<Video, Integer>> videos;
		for(Endpoint e: input.getEndpoints())
		{
			videos = new ArrayList<>(e.getRequests().entrySet());
			videos.sort(new Comparator<Entry<Video, Integer>>() {
				@Override
				public int compare(Entry<Video, Integer> o1, Entry<Video, Integer> o2)
				{
//					return o1.getKey().getSize() - o2.getKey().getSize();
					return o2.getValue() - o1.getValue();
				}
			});
			videosPerEndpoint.put(e, videos);
		}

		CountDownLatch latch = new CountDownLatch(NUM_THREADS);
		for(int i = 0; i < NUM_THREADS; i++)
		{
			new Thread(this.name + "#" + i) {
				public void run()
				{
					ThreadContext.put("filename", log.getPath());

					Endpoint e;
					while((e = endpoints.poll()) != null && !abortRequested)
					{
						if(videosPerEndpoint.get(e).size() == 0)
							continue;
						
						// find best video
						Entry<Video, Integer> mostReq = videosPerEndpoint.get(e).remove(0); 
								
//						logger.info("handling endpoint " + e.getId() + " -> video " + mostReq.getKey().getId() + " (remaining " + videosPerEndpoint.get(e).size() + ")");
						
						int score;
						Map<CacheServer, Integer> scores = new HashMap<>();
						for(Entry<CacheServer, Integer> s : e.getLatencies().entrySet())
						{
//							logger.info(e + " " + e.getLatencies()  + " " + mostReq);
							score = (e.getDatacenterLatency() - s.getValue()) * mostReq.getValue();
							
							for(Endpoint other : s.getKey().getEndpoints())
							{
//								logger.info(mostReq.getKey().getId() + " vs. " + other.getRequests().keySet());
								if(other.getRequests().containsKey(mostReq))
									score += (other.getDatacenterLatency() - other.getLatencies().get(s)) * other.getRequests().get(mostReq.getKey());
							}
							scores.put(s.getKey(), score);
//							logger.debug("  score for server " + s.getKey().getId() + " = " + score);
						}
						
						List<Entry<CacheServer, Integer>> sorted = new ArrayList<>(scores.entrySet());
						sorted.sort(new Comparator<Entry<CacheServer, Integer>>() {
							@Override
							public int compare(Entry<CacheServer, Integer> o1, Entry<CacheServer, Integer> o2)
							{
								return o1.getValue() - o2.getValue();
							}});
						
						boolean added = false;
						CacheServer s = null;
						do
						{
							if(sorted.size() == 0)
								break;
							s = sorted.remove(0).getKey();
							added = s.addVideo(mostReq.getKey());
						} while(!added);
						
//						if(added)
//							logger.info("added video to server " + s.getId());
						
						endpoints.add(e);
					}
					latch.countDown();
				}
			}.start();			
		}
		
		new Thread() {
			public void run()
			{
				ThreadContext.put("filename", log.getPath());
				while(latch.getCount() > 0)
				{
					try
					{
						Thread.sleep(1000);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					
					int totalRemaining = 0;
					for( Endpoint e: videosPerEndpoint.keySet())
					{
						totalRemaining += videosPerEndpoint.get(e).size();
//						logger.info("handling endpoint " + e.getId() + " -> remaining videos " + videosPerEndpoint.get(e).size() + ")");
					}
					logger.info("score = " + getScore(input.getTotalRequests()) + "\ttotalRemaining = " + totalRemaining);
				}
			}
		}.start();

		try
		{
			latch.await();
		}
		catch(InterruptedException e)
		{
			logger.error(e);
		}

		output.setScore(getScore(input.getTotalRequests()));

		return output;
	}
}
