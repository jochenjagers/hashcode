package gingerninjas.qualification;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import gingerninjas.BaseInput;

public class Input extends BaseInput
{
	private int		rows;
	private int		cols;
	private int		fleetSize;
	private int		numberOfRides;
	private int		bonus;
	private int		simulationSteps;

	private Ride[]	rides;

	public Input(File path, String name) throws IOException
	{
		super(path, name);
	}

	public int getRows()
	{
		return rows;
	}

	public int getCols()
	{
		return cols;
	}

	public int getFleetSize()
	{
		return fleetSize;
	}

	public int getNumberOfRides()
	{
		return numberOfRides;
	}

	public int getBonus()
	{
		return bonus;
	}

	public int getSimulationSteps()
	{
		return simulationSteps;
	}

	public Ride[] getRides()
	{
		return rides;
	}
	
	@Override
	protected void parse(BufferedReader reader) throws IOException
	{
		String line = null;

		line = reader.readLine();
		String[] splittedLine = line.split(" ");

		rows = Integer.parseInt(splittedLine[0]);
		cols = Integer.parseInt(splittedLine[1]);
		fleetSize = Integer.parseInt(splittedLine[2]);
		numberOfRides = Integer.parseInt(splittedLine[3]);
		bonus = Integer.parseInt(splittedLine[4]);
		simulationSteps = Integer.parseInt(splittedLine[5]);

		rides = new Ride[numberOfRides];

		this.maxScore = 0;
		
		for(int r = 0; r < numberOfRides; r++)
		{
			line = reader.readLine();
			logger.debug("endpoint: " + line);
			splittedLine = line.split(" ");

			int startX = Integer.parseInt(splittedLine[0]);
			int startY = Integer.parseInt(splittedLine[1]);
			int endX = Integer.parseInt(splittedLine[2]);
			int endY = Integer.parseInt(splittedLine[3]);
			int startTime = Integer.parseInt(splittedLine[4]);
			int endTime = Integer.parseInt(splittedLine[5]);

			rides[r] = new Ride(r, startX, startY, endX, endY, startTime, endTime);
			this.maxScore += rides[r].getDistance();
			if(rides[r].calcTimeToGetThere(0, 0) < startTime) {
				this.maxScore += bonus;
			}
		}
	}

	public void reset()
	{
		Arrays.sort(rides, Ride.BY_ID);
		for(Ride r: rides)
		{
			r.setStartedAt(-1);
		}
	}

	@Override
	public String toString()
	{
		return "Input [rows=" + rows + ", cols=" + cols + ", fleetSize=" + fleetSize + ", numberOfRides=" + numberOfRides + ", bonus=" + bonus
				+ ", simulationSteps=" + simulationSteps + "]";
	}

	public static void main(String[] args) throws IOException
	{
		Input i = new Input(new File("Online Qualification Round"), "A - Example");
		System.out.println(i);
		for(Ride r: i.rides)
			System.out.println(r);
	}
}
