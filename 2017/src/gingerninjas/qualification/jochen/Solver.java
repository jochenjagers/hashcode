package gingerninjas.qualification.jochen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import com.sun.corba.se.spi.activation.Server;

import gingerninjas.jochen.pizza.SliceRemover;
import gingerninjas.qualification.CacheServer;
import gingerninjas.qualification.Endpoint;
import gingerninjas.qualification.Input;
import gingerninjas.qualification.Output;
import gingerninjas.qualification.Video;

public class Solver extends gingerninjas.qualification.Solver
{

	@Override
	protected Output solve(Input input, Random random)
	{

		Map<String, Object> args = new HashMap<>();
		Output output = createOutput(in.getName(), args);
		output.setServers(input.getServers().values());
		boolean videoAdded = true;
		
		BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

		ArrayList<Worker> worker = new ArrayList<>();
		
		final int threads = 8;
		float partSize = input.getVideos().size() / (float) threads;
		for(int i = 0; i < threads; ++i)
		{
			worker.add(new Worker(Math.round(i * partSize), Math.min(input.getVideos().size(), Math.round((i + 1) * partSize)), input.getVideos(), queue, input.getServers()));
		}
		
		while(videoAdded)
		{
			videoAdded = false;
			// Clear Endpoint-Cache
			for(Endpoint e : input.getEndpoints()) {
				e.cache.clear();
			}
			for(Worker w : worker)
			{
				// Wakeup all workers
				w.wakeUp();
			}
			
			int counter = 0;
			while(counter < threads)
			{
				// Wait for all results
				counter++;
				try
				{
					queue.take();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			ServerVideoPair svp = new ServerVideoPair();
			
			// Suche bestes Ergebnis der Solver
			for(Worker w : worker) {
				if(w.bestVideo.p > svp.p) {
					svp = w.bestVideo;
				}
			}
			for(Map.Entry<Integer, CacheServer> e : input.getServers().entrySet()) {
				e.getValue().calcValue = false;
			}
			
			if(svp.v != null)
			{
				logger.info("Add Video " + svp.v + " to Server " + svp.s);
				svp.s.addVideo(svp.v);
				//bestVideoPerSize.lastEntry().getValue().s.addVideo(bestVideoPerSize.lastEntry().getValue().v);
				videoAdded = true;
			}
		}
		// for(Map.Entry<Integer, CacheServer> s : input.getServers().entrySet()) {
		// boolean videoAdded = false;
		// do {
		// videoAdded = false;
		// for(Video v : input.getVideos()) {
		// videoAdded |= s.getValue().addVideo(v);
		// }
		// } while(videoAdded == true);
		// break;
		// }

		// for(Map.Entry<Integer, CacheServer> s : input.getServers().entrySet())
		// {
		// HashMap<Video, Integer> videos = new HashMap<>();
		//
		// for(Endpoint e : s.getValue().getEndpoints())
		// {
		// for(Map.Entry<Video, Integer> v : e.getRequests().entrySet())
		// {
		// Integer value = videos.get(v.getKey());
		// value += v.getValue();
		// videos.put(v.getKey(), value);
		// }
		// }
		// }
		for(Worker w : worker)
		{
			// Wakeup all workers
			w.running = false;
			w.wakeUp();

		}
		output.setScore(this.getScore(input.getTotalRequests()));
		return output;
	}

}
