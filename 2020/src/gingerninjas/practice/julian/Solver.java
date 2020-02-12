package gingerninjas.practice.julian;

import gingerninjas.practice.PractiSolver;

public class Solver extends PractiSolver
{
	@Override
	protected void solve()
	{
		System.out.println(input.getSlicesPerType());
		int sum = 0;
		int max = input.getSlices();
		for(int i = 0; i < input.getSlicesPerType().length; i++)
		{
			logger.info("sum= "+ sum + "\tsize=" + input.getSlicesPerType()[i]);
			if(sum + input.getSlicesPerType()[i] <= max)
			{
				sum += input.getSlicesPerType()[i];
				output.getTypes().add(i);
				output.setScore(sum);
			}
		}
	}
}
