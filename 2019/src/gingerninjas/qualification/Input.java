package gingerninjas.qualification;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import gingerninjas.BaseInput;

public class Input extends BaseInput
{
	int collectionSize;
	
	
	public Input(File path, String name) throws IOException
	{
		super(path, name);
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
	}

	@Override
	public String toString()
	{
		return "Input [rows=" + rows + ", cols=" + cols + ", fleetSize=" + fleetSize + ", numberOfRides=" + numberOfRides + ", bonus=" + bonus
				+ ", simulationSteps=" + simulationSteps + "]";
	}

	public static void main(String[] args) throws IOException
	{
	}
}
