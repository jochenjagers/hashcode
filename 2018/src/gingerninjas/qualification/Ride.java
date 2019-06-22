package gingerninjas.qualification;

import java.util.Comparator;

import gingerninjas.util.Copyable;

public class Ride implements Comparable<Ride>
{
	private int		id;
	private int		startX;
	private int		startY;
	private int		endX;
	private int		endY;
	private int		startTime;
	private int		endTime;

	private boolean	done;
	private int		distance	= -1;
	private double	rating;
	private int		startedAt	= -1;
	private int		finishedAt	= -1;

	public Ride(int id, int startX, int startY, int endX, int endY, int startTime, int endTime)
	{
		super();
		this.id = id;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.startTime = startTime;
		this.endTime = endTime;

		this.done = false;
	}

	public boolean isDone()
	{
		return done;
	}

	public void setDone(boolean done)
	{
		this.done = done;
	}

	public int getId()
	{
		return id;
	}

	public int getStartX()
	{
		return startX;
	}

	public int getStartY()
	{
		return startY;
	}

	public int getEndX()
	{
		return endX;
	}

	public int getEndY()
	{
		return endY;
	}

	public int getStartTime()
	{
		return startTime;
	}

	public int getEndTime()
	{
		return endTime;
	}

	public double getRating()
	{
		return rating;
	}

	public void setRating(double rating)
	{
		this.rating = rating;
	}

	public int getStartedAt()
	{
		return startedAt;
	}

	public void setStartedAt(int startedAt)
	{
		this.startedAt = startedAt;
	}

	public int getFinishedAt()
	{
		return finishedAt;
	}

	public void setFinishedAt(int finishedAt)
	{
		this.finishedAt = finishedAt;
	}

	public int getDistance()
	{
		if(distance < 0)
			this.distance = Math.abs(endX - startX) + Math.abs(endY - startY);
		return distance;
	}

	public int calcTimeToGetThere(Vehicle v)
	{
		return calcTimeToGetThere(v.getX(), v.getY());
	}

	public int calcTimeToGetThere(int currentX, int currentY)
	{
		return Math.abs(currentX - startX) + Math.abs(currentY - startY);
	}

	public int getScore(int earliestArrivalTime, Input input)
	{
		int rideScore = 0;
		int distance = getDistance();
		int time = earliestArrivalTime;
		if(time <= getStartTime())
		{
			// Ride Starts in its earlisest allowd start
			rideScore += input.getBonus();
			// Fahrzeug wartet auf Gast
			time = getStartTime();
		}
		if(time + distance <= getEndTime())
		{
			// Rechtzeitig am Ziel
			rideScore += distance;
			time += distance;
			if(time <= input.getSimulationSteps())
			{
				return rideScore;
			}
		}
		return 0;
	}

	@Override
	public String toString()
	{
		return "Ride [id=" + id + " start=" + startX + "|" + startY + "@" + startTime + " end=" + endX + "|" + endY + "@" + +endTime + "]";
	}

	public static final Comparator<Ride>	BY_ID		= new Comparator<Ride>() {
															@Override
															public int compare(Ride o1, Ride o2)
															{
																return o1.id - o2.id;
															}
														};

	public static final Comparator<Ride>	BY_RATING	= new Comparator<Ride>() {
															@Override
															public int compare(Ride o1, Ride o2)
															{
																return Double.compare(o2.rating, o1.rating);
															}
														};

	@Override
	public int compareTo(Ride o)
	{
		return Double.compare(o.rating, this.rating);
	}
}
