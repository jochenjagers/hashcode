package gingerninjas.qualification.jochen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import gingerninjas.jochen.pizza.SliceRemover;
import gingerninjas.qualification.CacheServer;
import gingerninjas.qualification.Endpoint;
import gingerninjas.qualification.Input;
import gingerninjas.qualification.Output;
import gingerninjas.qualification.Video;

public class Solver2 extends gingerninjas.qualification.Solver
{

	@Override
	protected Output solve(Input input, Random random)
	{
		Map<String, Object> args = new HashMap<>();
		Output output = createOutput(in.getName(), args);
		output.setServers(input.getServers().values());
		BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

		ArrayList<Worker2> worker = new ArrayList<Worker2>();
		
		final int threads = 3;
		float partSize = input.getEndpoints().size() / (float) threads;
		for(int i = 0; i < threads; ++i)
		{
			worker.add(new Worker2(Math.round(i * partSize), Math.min(input.getEndpoints().size(), Math.round((i + 1) * partSize)), input.getEndpoints(), queue));
		}
		while(true)
		{

			
			for(Worker2 sr : worker)
			{
				sr.wakeUp();
			}
			
			int counter = 0;
			while(counter < threads)
			{
				counter++;
				try
				{
					queue.take();
				}
				catch(InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			CacheServer bestServer = null;
			Video bestVideo = null;
			int bestResult = 0;
			for(Worker2 sr : worker) {
				if(sr.bestScore > bestResult) {
					bestResult = sr.bestScore;
					bestServer = sr.bestServer;
					bestVideo = sr.bestVideo;
				}
			}

				
			
			if(bestServer != null)
			{
				bestServer.addVideo(bestVideo);
			}
			else
			{
				logger.info("Nichts mehr gefunden");
				break;
			}
		}

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

		output.setScore(this.getScore(input.getTotalRequests()));
		return output;
	}

}
