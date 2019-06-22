package gingerninjas.jochen.final_2014;

import java.util.ArrayList;

public class Simulation
{
	Car[] cars;
	ArrayList<Street> streets;
	Junction[] junctions;
	int runtime = 0;
	Junction start;
	
	public void reset() {
		for(int i = 0; i<cars.length; ++i) {
			cars[i].route.clear();
			cars[i].route.add(start);
			cars[i].position = start;
			cars[i].remainingTime = runtime;
		}
		
		for(int i = 0; i < streets.size(); ++i) {
			streets.get(i).visits = 0;
		}
	}
}
