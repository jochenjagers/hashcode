package gingerninjas.practice.julian;

import java.util.ArrayList;
import java.util.List;

import gingerninjas.practice.PractiSolver;

public class Solver extends PractiSolver
{
	@Override
	protected void solve()
	{
		int sum = 0;
		int max = input.getSlices();
		
		List<Integer> used = new ArrayList<Integer>();
		List<Integer> unused = new ArrayList<Integer>();
		
		for(int i = input.getSlicesPerType().length-1; i >= 0; i--)
		{
			logger.info("sum= "+ sum + "\tsize=" + input.getSlicesPerType()[i]);
			if(sum + input.getSlicesPerType()[i] <= max)
			{
				sum += input.getSlicesPerType()[i];
				output.getTypes().add(i);
				output.setScore(sum);
				used.add(i);
			}
			else
			{
				unused.add(i);
			}
		}
		int delta = max - sum;
		
		if(delta == 0)
			return;
		
		int tries = 0;
		int selected_u;
		int selected_n1, selected_n2;
		int selected_sum = 0;
		int new_sum = 0;
		do
		{
			selected_u = -1;
			selected_n1 = -1;
			selected_n2 = -1;
			
			logger.info("delta=" + delta);
			
			for(int u = used.size()-1; u >= 0; u--)
			{
				// find unused types that can replace a used type to get closer to the target
				for(int n1 = 0; n1 < unused.size(); n1++)
				{
					new_sum = sum + input.getSlicesPerType()[unused.get(n1)] - input.getSlicesPerType()[used.get(u)];
					
					logger.info("checking: " + u + " (" + input.getSlicesPerType()[used.get(u)] + ") <=> " + n1 + " (" + input.getSlicesPerType()[unused.get(n1)] + ") => (" + (max - new_sum) + ")");
					
					if(new_sum > max)
						continue;
					
					for(int n2 = unused.size()-1; n2 >= 0; n2--)
					{
						if(n1 == n2)
							continue;
						
						new_sum = sum + input.getSlicesPerType()[unused.get(n1)] + input.getSlicesPerType()[unused.get(n2)] - input.getSlicesPerType()[used.get(u)];
						

						if(new_sum > selected_sum && new_sum <= max)
						{
							logger.info("checking: " + u + " (" + input.getSlicesPerType()[used.get(u)] + ") <=> " + n1 + " (" + input.getSlicesPerType()[unused.get(n1)] + ") + " + n2 + " (" + input.getSlicesPerType()[unused.get(n2)] + ") => (" + (max - new_sum) + ") *");
							selected_u = used.get(u);
							selected_n1 = unused.get(n1);
							selected_n2 = unused.get(n2);
							selected_sum = new_sum;
							if(selected_sum == max)
							{
								logger.info("Volltreffer!");
								break;
							}
						}
						else
						{
							logger.info("checking: " + u + " (" + input.getSlicesPerType()[used.get(u)] + ") <=> " + n1 + " (" + input.getSlicesPerType()[unused.get(n1)] + ") + " + n2 + " (" + input.getSlicesPerType()[unused.get(n2)] + ") => (" + (max - new_sum) + ")");
						}
					}
					
					if(selected_sum == max)
						break;
				}
				
				if(selected_sum == max)
					break;
			}
			
			if(selected_u == -1 || selected_n1 == -1 || selected_n2 == -1)
			{
				delta--;
				continue;
			}
			
			logger.info("switching: " + selected_u + " (" + input.getSlicesPerType()[selected_u] + ") <=> " + selected_n1 + " (" + input.getSlicesPerType()[selected_n1] + ") + " + selected_n2 + " (" + input.getSlicesPerType()[selected_n2] + ") = (" + (max - new_sum) + ")");

			sum = selected_sum;
			output.getTypes().remove((Object) selected_u);
			output.getTypes().add(selected_n1);
			output.getTypes().add(selected_n2);
			
			output.setScore(sum);
			
			used.add(selected_n1);
			used.add(selected_n2);
			unused.remove((Object) selected_n1);
			unused.remove((Object) selected_n2);
			
			unused.add(selected_u);
			used.remove((Object) selected_u);

			delta = max - sum;
			tries++;
		} while (selected_sum != max && tries < 10);
	}
}
