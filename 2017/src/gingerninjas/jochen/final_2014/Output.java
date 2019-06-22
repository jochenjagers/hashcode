package gingerninjas.jochen.final_2014;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import gingerninjas.BaseOutput;

public class Output extends BaseOutput
{
	Simulation sim;
	
	public Output(String name, Map<String, Object> args)
	{
		super(name, args);
	}

	@Override
	protected void write(BufferedWriter r) throws IOException
	{
		r.write(sim.cars.length + "\n");
		for(int i = 0; i < sim.cars.length; ++i) {
			r.write(sim.cars[i].route.size() + "\n");
			for(int j = 0; j < sim.cars[i].route.size(); ++j) {
				r.write(sim.cars[i].route.get(j).name + "\n");	
			}
		}
	}
}
