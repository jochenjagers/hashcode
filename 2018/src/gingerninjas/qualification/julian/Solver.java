package gingerninjas.qualification.julian;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

import gingerninjas.qualification.QualiSolver;
import gingerninjas.qualification.Ride;
import gingerninjas.qualification.Vehicle;

public class Solver extends QualiSolver
{
	@Override
	public void solve()
	{
//		for(double factor = 0.1; factor < 2; factor += 0.1)
		{
			output.reset();
			input.reset();
			
			// init
			LinkedList<Ride> unplannedRides = new LinkedList<>();
			unplannedRides.addAll(Arrays.asList(input.getRides()));
			
			// stepping
			Ride r;
			Vehicle v;
			double rating;
			double a, b;
			int timeToGetThere, timeLeft;
			for(int time = 0; time < input.getSimulationSteps(); time++)
			{
				for(int vi = 0; vi < output.getVehicles().size(); vi++)
				{
					v = output.getVehicles().get(vi);
					if(time < v.getTimeAvailable())
						continue;
					
					if(unplannedRides.size() == 0)
						break;
						
					// vehicle is available
					
					// rate rides
					for(Ride ri: unplannedRides)
					{
						timeToGetThere = ri.calcTimeToGetThere(v);
						timeLeft = ri.getEndTime() - (time + timeToGetThere + ri.getDistance());
						
						a = ri.getScore(time+timeToGetThere, input);
						b = timeToGetThere + ri.getDistance();
						
//						a = ri.getScore(time+timeToGetThere, input) - factor*timeLeft;
//						b = timeToGetThere + ri.getDistance();
						
//						a = ri.getScore(time+timeToGetThere, input);
//						b = timeToGetThere + ri.getDistance() + timeLeft;
						
//						a = ri.getScore(time+timeToGetThere, input);
//						b = (timeToGetThere + ri.getDistance())*timeLeft;
						
	//					a = ri.getScore(time+timeToGetThere, input);
	//					b = timeLeft;
						
						rating = a/b;
						ri.setRating(rating);
					}
					
					// resort
					Collections.sort(unplannedRides, Ride.BY_RATING);
					
					// get best ride
					r = unplannedRides.getFirst();
	
					logger.info("time=" + time + " ride=" + r.getId() + " @ vehicle=" + vi + " ridesLeft=" + unplannedRides.size());
					
					v.addRide(r);
					r.setDone(true);
					unplannedRides.remove(r);
				}
				if(unplannedRides.size() == 0)
					break;
			}

			// optimization (fill gaps)
			
//			for(Vehicle vi: output.getVehicles())
//			{
//				int x = 0;
//				int y = 0;
//				int time = 0;
//				int gap;
//				int dist;
//				
//				logger.info("optimizing vehicle=" + vi);
//				
//				ListIterator<Ride> ri = vi.getRides().listIterator();
//				Ride rp;
//				while(ri.hasNext())
//				{
//					rp = ri.next();
//					
//					gap = rp.getStartedAt() - time;
//					dist = rp.calcTimeToGetThere(x, y);
//					
//					logger.info("dist=" + dist + " / gap=" + gap);
//					
//
//					for(Ride ru: unplannedRides)
//					{
//						if(gap - ru.getDistance() == 0)
//							continue;
//						ru.setRating(ru.getScore(time + ru.calcTimeToGetThere(x, y), input) + rp.getScore(time + ru.calcTimeToGetThere(x, y) + ru.getDistance() + rp.calcTimeToGetThere(ru.getEndX(), ru.getEndY()), input));
//					}
//					Collections.sort(unplannedRides, Ride.BY_RATING);
//					
//					Ride candidate;
//					LinkedList<Ride> tmp = new LinkedList<>(unplannedRides);
//					
//					while(tmp.size() > 0)
//					{
//						candidate = tmp.poll();
//
//						int startedAt = Math.max(time + candidate.calcTimeToGetThere(x, y), candidate.getStartTime());
//						if(gap >= startedAt + candidate.getDistance() + rp.calcTimeToGetThere(candidate.getEndX(), candidate.getEndY()))
//						{
//							ri.previous();
//							ri.add(candidate);
//							rp = candidate;
//							
//							candidate.setDone(true);
//							candidate.setStartedAt(startedAt);
//							candidate.setFinishedAt(startedAt + candidate.getDistance());
//							unplannedRides.remove(candidate);
//							
//							logger.info("added ride    rides=" + unplannedRides.size());
//							break;
//						}
//					}
//					
//					
//					x = rp.getEndX();
//					y = rp.getEndY();
//					time = rp.getFinishedAt();
//				}
//			}
		
			
			output.publish();
		}
	}
}
