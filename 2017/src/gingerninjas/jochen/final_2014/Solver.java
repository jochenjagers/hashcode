package gingerninjas.jochen.final_2014;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import gingerninjas.BaseSolver;

public class Solver extends BaseSolver<Input, Output>
{
	@Override
	public Input parse(File in) throws IOException
	{
		Input input = new Input(in.getName());
		input.fromFile(in);
		return input;
	}

	@Override
	public void write(File out, Output output) throws IOException
	{
		// output.toFile(out);
	}

	@Override
	public Output solve(Input in)
	{
		Map<String, Object> args = new HashMap<>();
		Output output = new Output(in.getName(), args);
		output.sim = in.sim;
		int maxPoints = 0;

		for(int i = 0; i < in.sim.cars.length; ++i)
		{
			in.sim.cars[i].random = this.random;
		}
		
		int totalPoints = 0;
		for(int i = 0; i < in.sim.streets.size(); ++i)
		{
			totalPoints += in.sim.streets.get(i).points;
		}

		// Gibt es Einbahnstraßen?
		/*
		int count = 0;
		boolean added = false;
		do
		{
			added = false;
			for(int i = 0; i < in.sim.streets.size(); ++i)
			{
				if(in.sim.streets.get(i).oneDir && in.sim.streets.get(i).to.outgoing.size() == 1 && !in.sim.streets.get(i).isDeadEnd())
				{
					Street next = in.sim.streets.get(i).to.outgoing.get(0);
					Street act = in.sim.streets.get(i);
					if(act.to.name == next.from.name)
					{
						act.inbetween.add(act.to);
						act.to = next.to;
					}
					else if(act.to.name == next.to.name)
					{
						act.inbetween.add(act.to);
						act.to = next.from;
					}
					else
					{
						System.out.println("Hier stimmt was nicht");
						System.exit(1);
					}
					act.cost += next.cost;
					act.points += next.points;
					in.sim.streets.remove(next);
					added = true;
					// System.out.println(i + " ist eine Einbahnstraße und kann mit dem Nachfolger
					// zusammen gefasst werden.");
					count++;
					break;
				}
			}
		} while(added == true);
		*/
		for(int l = 0; l < 10; ++l) {
		for(int k = 0; k < 50; ++k)
		{
			in.sim.reset();
			logger.info("K: " + k + " D: " + l);
			//System.out.println("Es gibt " + count + " Einbahnstraßen die zusammen gefasst werden können.");
			boolean driven = false;
			do
			{
				Arrays.sort(in.sim.cars);
				driven = in.sim.cars[0].drive(k, l);
			} while(driven);

			int sum = 0;
			for(int i = 0; i < in.sim.streets.size(); ++i)
			{
				if(in.sim.streets.get(i).visits > 0)
				{
					sum += in.sim.streets.get(i).points;
				}
			}
			if(sum > maxPoints)
			{
				System.out.println("Simulation abgeschlossen. Punkte erreicht: " + sum + " von " + totalPoints + " k:" + k);
				maxPoints = sum;
			}
			/*
			if((k % 100) == 0)
			{
				System.out.println("Iteration " + k + " Max: " + maxPoints);
			}
			Collections.sort(in.sim.streets);
			for(int i = 0; i < 5; ++i)
			{
				System.out.println("Straße: " + in.sim.streets.get(i).name + " - Wiederholungen: " + in.sim.streets.get(i).visits);
			}
			*/
			
			if(abortRequested) {
				return output;
			}
		}
		}
		return output;
	}

	@Override
	protected Input createInput(String name)
	{
		return new Input(in.getName());
	}

	@Override
	protected Output createOutput(String name, Map<String, Object> args)
	{
		return new Output(in.getName(), args);
	}
}
