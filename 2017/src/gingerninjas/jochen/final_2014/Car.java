package gingerninjas.jochen.final_2014;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;

public class Car implements Comparable<Car>
{
	Junction			position;
	ArrayList<Junction>	route	= new ArrayList<>();
	int					remainingTime;
	Random				random;

	Car(Junction start, int remainingTime)
	{
		this.position = start;
		route.add(start);
		this.remainingTime = remainingTime;
	}

	Street selectNextStreet(int penalty, int depth)
	{
		Street next = null;
		for(int i = 0; i < this.position.outgoing.size(); ++i)
		{
			// Suche nach der abgehenden Straße mit der besten Qualität die noch befahrbar
			// ist
			float quality = this.position.outgoing.get(i).getQuality(penalty, depth);
			boolean isDeadEnd = this.position.outgoing.get(i).isDeadEnd();
			if(this.position.outgoing.get(i).cost < this.remainingTime && (next == null || next.getQuality(penalty, depth) < quality) && !isDeadEnd)
			{
				next = this.position.outgoing.get(i);
			}
		}
		return next;
	}

	Street searchNextUnvisitedRoad(Junction start)
	{
		Street result = null;

		LinkedHashSet<Street> streets = new LinkedHashSet<Street>();
		streets.addAll(start.outgoing);

		for(Street s : streets)
		{
			if(s.visits == 0)
			{
				return s;
			}
		}

		return result;
	}

	Street selectNextStreetRandom()
	{
		if(this.position.outgoing.size() == 1)
		{
			return this.position.outgoing.get(0);
		}
		Street next = null;
		boolean allTheSame = true;
		for(int i = 0; i < this.position.outgoing.size(); ++i)
		{
			if(this.position.name != this.position.outgoing.get(i).to.name)
			{
				allTheSame = false;
				break;
			}
		}
		do
		{
			int r = Math.abs(random.nextInt());
			r = (r % this.position.outgoing.size());
			next = this.position.outgoing.get(r);
		} while(!allTheSame && next.to.name == this.position.name);
		return next;
	}

	boolean drive(int penalty, int depth)
	{
		if(this.remainingTime > 0)
		{
			if(this.position.outgoing.size() > 0)
			{
				Street next = this.selectNextStreet(penalty, depth);
				if(next == null)
				{
//					System.out.println("Fahrzeug kann über keine weitere Straße mehr fahren. Zeit abgelaufen. Verbleibend: " + this.remainingTime);
//					System.out.println("Posiion: " + this.position.name);
				}
				else
				{
					// System.out.println("Fahre Straße: " + next.name);
					next.visits++;
					if(this.position == next.from)
					{
						this.position = next.to;
					}
					else if(this.position == next.to)
					{
						this.position = next.from;
					}
					else
					{
						System.out.println("Hier stimmt was nicht.");
						System.exit(1);
					}
					route.add(this.position);
					this.remainingTime -= next.cost;
					return true;
				}
			}
			else
			{
				System.out.println("FEHLER: keine Abgehende Straße vorhanden. Fahrzeug kann Junction nicht verlassen.");
			}
		}
		return false;
	}

	@Override
	public int compareTo(Car o)
	{
		return o.remainingTime - this.remainingTime;
	}
}
