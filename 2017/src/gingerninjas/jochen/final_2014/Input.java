package gingerninjas.jochen.final_2014;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import gingerninjas.BaseInput;

public class Input extends BaseInput
{
	Simulation sim;
	
	public Input(String name)
	{
		super(name);
		
		sim = new Simulation();
	}

	@Override
	protected void parse(BufferedReader r) throws IOException
	{
		int lineNumber = 0;
		String line = null;
		
		Integer juntionCount = 0;
		Integer streetCount = 0;
		Integer carCount = 0;
		Integer startJunction = 0;
		
		while((line = r.readLine()) != null)
		{
			String[] values = line.split(" ");
			if(lineNumber == 0) {
				// Parse first line
				if(values.length != 5) {
					System.out.println("Wrong file format");
					return;
				}
				juntionCount = Integer.parseInt(values[0]);
				streetCount = Integer.parseInt(values[1]);
				carCount = Integer.parseInt(values[3]);
				startJunction = Integer.parseInt(values[4]);
				sim.junctions = new Junction[juntionCount];
				sim.streets = new ArrayList<Street>(streetCount);
				sim.runtime = Integer.parseInt(values[2]);
				sim.cars = new Car[carCount];
			} else if(lineNumber < 1+juntionCount) {
				sim.junctions[lineNumber-1] = new Junction(lineNumber-1);
			} else if(lineNumber < 1+juntionCount+streetCount) {
				int from = Integer.parseInt(values[0]);
				int to = Integer.parseInt(values[1]);
				if(from == to) {
					System.out.println("Zykel gefunden");
					System.exit(1);
				}
				Street newStreet = new Street(lineNumber-1-juntionCount, sim.junctions[from], sim.junctions[to], (Integer.parseInt(values[2]) == 1)?true:false, Integer.parseInt(values[3]),Integer.parseInt(values[4]));
				
				newStreet.from.outgoing.add(newStreet);
				if(!newStreet.oneDir) {
					newStreet.to.outgoing.add(newStreet);
				}
				
				sim.streets.add(newStreet); 
			}
			lineNumber++;
		}
		// Create Cars
		sim.start = sim.junctions[startJunction];
		for(int i = 0; i < carCount; ++i) {
			sim.cars[i] = new Car(sim.junctions[startJunction], sim.runtime);
		}
		System.out.println("Input eingelesen: ");
		System.out.println("Junctions: " + juntionCount.toString());
		System.out.println("Streets: " + streetCount.toString());
		System.out.println("Cars: " + carCount.toString());
		System.out.println("Runtime: " + sim.runtime);
	}
}
