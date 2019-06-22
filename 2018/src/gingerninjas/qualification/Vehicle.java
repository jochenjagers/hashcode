package gingerninjas.qualification;

import java.util.LinkedList;
import java.util.List;

public class Vehicle
{
	private List<Ride>	rides;

	private int			x;
	private int			y;

	private int			timeAvailable;
	
	public Vehicle()
	{
		super();
		reset();
	}

	public void reset()
	{
		rides = new LinkedList<>();
		x = 0;
		y = 0;
		timeAvailable = 0;
	}

	public List<Ride> getRides()
	{
		return rides;
	}

	public void addRide(Ride r)
	{
		timeAvailable += r.calcTimeToGetThere(x, y);
		if(timeAvailable < r.getStartTime())
		{
			// ggf. auf Ride warten
			timeAvailable = r.getStartTime();
		}
		r.setStartedAt(timeAvailable);
		timeAvailable += r.getDistance();
		r.setFinishedAt(timeAvailable);
		r.setDone(true);
		this.x = r.getEndX();
		this.y = r.getEndY();
		this.rides.add(r);
	}

	public void setRides(List<Ride> rides)
	{
		this.rides = rides;
	}

	public int getTimeAvailable()
	{
		return timeAvailable;
	}

	public void setTimeAvailable(int timeAvailable)
	{
		this.timeAvailable = timeAvailable;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Ride getNearestRide(Ride[] rides)
	{
		Ride result = null;
		int distance = Integer.MAX_VALUE;

		int posX = this.x;
		int posY = this.y;

		for(Ride r : rides)
		{
			int d = Math.abs(posY - r.getStartY()) + Math.abs(posX - r.getStartX());
			if(d < distance)
			{
				distance = d;
				result = r;
			}
		}

		return result;
	}

	public int getScore(Input input)
	{
		int totalScore = 0;
		int time = 0;
		int posX = 0;
		int posY = 0;
		for(Ride r : rides)
		{
			// Drive to Startpos
			time += r.calcTimeToGetThere(posX, posY);
			posX = r.getEndX();
			posY = r.getEndY();

			totalScore += r.getScore(time, input);
			if(time < r.getStartTime())
				time = r.getStartTime();
			time += r.getDistance();
		}
		return totalScore;
	}
}
