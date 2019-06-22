package gingerninjas.qualification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import gingerninjas.BaseOutput;

public class Output extends BaseOutput
{
	private ArrayList<Vehicle>	vehicles;

	private Input				input;

	public ArrayList<Vehicle> getVehicles()
	{
		return vehicles;
	}

	public void init(Input input)
	{
		this.input = input;
		vehicles = new ArrayList<>(input.getFleetSize());
		for(int i = 0; i < input.getFleetSize(); ++i)
		{
			vehicles.add(new Vehicle());
		}
	}

	public Output(File path, String name, boolean load) throws IOException
	{
		super(path, name, load);
	}

	@Override
	protected void write(BufferedWriter r) throws IOException
	{
		// TODO
		for(Vehicle v : vehicles)
		{
			String line = v.getRides().size() + "";
			for(Ride ri : v.getRides())
			{
				line += " " + ri.getId();
			}
			r.write(line + "\n");
		}
	}

	@Override
	protected void parse(BufferedReader reader) throws IOException
	{
		String line = null;

		line = reader.readLine();
		String[] splittedLine = line.split(" ");

		do
		{
			splittedLine = line.split(" ");

			int rides = Integer.parseInt(splittedLine[0]);

			Vehicle v = new Vehicle();

			for(int i = 1; i <= rides; i++)
			{
				v.getRides().add(input.getRides()[Integer.parseInt(splittedLine[i])]);
			}
			this.vehicles.add(v);
		} while(line != null);
	}
	
	@Override
	public double getScore()
	{
		this.calcScore();
		return super.getScore();
	}
	

	public void calcScore()
	{
		int totalScore = 0;
		for(Vehicle v : vehicles)
		{
			totalScore += v.getScore(input);
		}
		this.score = totalScore;
	}
	
	public void reset()
	{
		for(Vehicle v: vehicles)
		{
			v.reset();
		}
		for(Ride r: input.getRides())
		{
			r.setDone(false);
			r.setRating(0);
		}
	}
}
