package gingerninjas.qualification.julian;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import gingerninjas.qualification.QualiSolver;
import gingerninjas.qualification.Ride;
import gingerninjas.qualification.Vehicle;

public class Dummy extends QualiSolver
{
	@Override
	public void solve()
	{
		Arrays.sort(input.getRides(), new Comparator<Ride>() {
			@Override
			public int compare(Ride arg0, Ride arg1)
			{
				return arg0.getStartTime() - arg1.getStartTime();
			}
		});
		int time;
		int x;
		int y;
		for(Vehicle v : output.getVehicles())
		{
			x = 0;
			y = 0;
			time = 0;
			List<Ride> rides = new LinkedList<>();
			for(Ride r : input.getRides())
			{
				if(r.isDone())
					continue;

				if(r.getEndTime() - r.getDistance() > time + r.calcTimeToGetThere(x, y))
				{
					rides.add(r);
					r.setDone(true);
					time += r.calcTimeToGetThere(x, y) + r.getDistance();
					x = r.getEndX();
					y = r.getEndY();
				}
			}
			v.setRides(rides);
		}
	}
}
