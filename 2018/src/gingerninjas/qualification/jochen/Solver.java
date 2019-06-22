package gingerninjas.qualification.jochen;

import java.io.Console;
import java.util.ArrayList;
import java.util.LinkedList;

import gingerninjas.qualification.QualiSolver;
import gingerninjas.qualification.Ride;
import gingerninjas.qualification.Vehicle;
import gingerninjas.util.SortedLinkedList;

public class Solver extends QualiSolver
{
	@Override
	public void solve()
	{
		// solve2();
		// if(1 == 2)
		for(int maxWait = 0; maxWait < input.getCols(); maxWait += input.getCols() / 15)
		{
			output.reset();
			int nextTime = Integer.MAX_VALUE;
			for(int time = 0; time < input.getSimulationSteps(); ++time)
			{
				//System.out.println(time + " / " + input.getSimulationSteps());
				nextTime = Integer.MAX_VALUE;
				for(Vehicle v : output.getVehicles())
				{
					if(v.getTimeAvailable() <= time)
					{
						// Fahrzeug ist frei
						Ride bestRide = null;
						Ride bestRide2 = null;
						float val = 0;
						int startX = v.getX();
						int startY = v.getY();

						for(Ride r : input.getRides())
						{
							if(!r.isDone())
							{
								int ttgt = r.calcTimeToGetThere(startX, startY);

								if(ttgt + time + maxWait >= r.getStartTime() && Math.max(ttgt + time, r.getStartTime()) + r.getDistance() < r.getEndTime()
										&& Math.max(ttgt + time, r.getStartTime()) + r.getDistance() < input.getSimulationSteps())
								{
									/*
									 * float best2 = 0;
									 * Ride best2Ride = null;
									 * for(Ride r2 : input.getRides())
									 * {
									 * if(!r2.isDone())
									 * {
									 * int ttgt2 = r2.calcTimeToGetThere(r.getEndX(), r.getEndY());
									 * int tmpTime = time + ttgt + r.getDistance();
									 * {
									 * if(r != r2 && ttgt2 + tmpTime >= r2.getStartTime()
									 * && Math.max(ttgt2 + tmpTime, r2.getStartTime()) +
									 * r2.getDistance() < r2.getEndTime() &&
									 * Math.max(ttgt2 + tmpTime, r2.getStartTime()) +
									 * r2.getDistance() < input.getSimulationSteps())
									 * {
									 * float val2 = (float) ((ttgt2 + tmpTime == r.getStartedAt()) ?
									 * input.getBonus() : 0 + r2.getDistance())
									 * / (float) (ttgt2 + r2.getDistance());
									 * if(val2 > best2)
									 * {
									 * best2 = val2;
									 * best2Ride = r2;
									 * }
									 * }
									 * }
									 * }
									 * }
									 * int rest = Math.max(0, input.getSimulationSteps() - time -
									 * ttgt- r.getDistance());
									 * if(best2Ride != null)
									 * {
									 * rest = best2Ride.calcTimeToGetThere(r.getEndX(),
									 * r.getEndY());
									 * }
									 */
									int rest = 0;
									r.setRating((float) ((ttgt + time == r.getStartTime()) ? input.getBonus() : 0 + r.getDistance())
											/ (float) (ttgt + r.getDistance() + rest +Math.max(0, (r.getStartTime() - time - ttgt))));
									if(r.getRating() > val)
									{
										// Ride ist ausführbar und besser
										val = (float) r.getRating();
										bestRide = r;
										// bestRide2 = best2Ride;
									}
								}
							}
						}
						if(bestRide != null)
						{
							// System.out.println("Add");
							v.addRide(bestRide);
							// if(bestRide2 != null)
							// v.addRide(bestRide2);
							nextTime = Math.min(nextTime, v.getTimeAvailable());
						}
					}
				}
				if(nextTime > time && nextTime < Integer.MAX_VALUE)
				{
					time = nextTime;
				}
			}
			System.out.println("Score: " + output.getScore());
			output.publish();
		}
	}

	void optimize()
	{
		for(Vehicle v : output.getVehicles())
		{
			for(Ride r : input.getRides())
			{
				if(!r.isDone())
				{
					LinkedList<Ride> ridesBetween = new LinkedList<>();
					for(Ride vr : v.getRides())
					{

					}

				}
			}
		}
	}

	public void solve2()
	{
		int count = 0;
		for(Vehicle v : output.getVehicles())
		{

			System.out.println(++count + " / " + output.getVehicles().size());
			while(v.getTimeAvailable() <= input.getSimulationSteps())
			{
				Ride bestRide = null;
				float val = 0;
				for(Ride r : input.getRides())
				{
					if(!r.isDone())
					{
						int ttgt = r.calcTimeToGetThere(v);
						if(ttgt + v.getTimeAvailable() >= r.getStartTime() && ttgt + v.getTimeAvailable() + r.getDistance() < r.getEndTime())
						{
							float qualaty = (float) (input.getBonus() + r.getDistance()) / (float) (ttgt + r.getDistance());
							if(qualaty > val)
							{
								// Ride ist ausführbar und besser
								val = qualaty;
								bestRide = r;
							}
						}
					}
				}
				if(bestRide != null)
				{
					System.out.println("Add");
					v.addRide(bestRide);
				}
				else
				{
					break;
				}
			}
		}
	}
}
