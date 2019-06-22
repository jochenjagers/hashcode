package gingerninjas.qualification.julian;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import gingerninjas.qualification.Photo;
import gingerninjas.qualification.QualiSolver;
import gingerninjas.qualification.Slide;

public class Solver extends QualiSolver
{
	@Override
	protected void solve()
	{
		List<Slide> slides = generateSlides(this.input.getPhotos());
		
		// chains mit 1-chains initialisieren
		LinkedList<LinkedList<Slide>> chains = new LinkedList<LinkedList<Slide>>();
		LinkedList<Slide> chain;
		for(Slide s: slides)
		{
			chain = new LinkedList<>();
			chain.add(s);
			chains.add(chain);
		}
		
		LinkedList<Slide> current;
		LinkedList<Slide> other; 
		LinkedList<Slide> best; 
		Iterator<LinkedList<Slide>> iter;
		int score_current_other_abs;
		int score_other_current_abs;
		double score_current_other;
		double score_other_current;
		double score_best;
		boolean current_first = false;
		
		while(chains.size() > 1)
		{
			logger.debug("chains: " + chains.size());
			current = chains.getFirst();
			iter = chains.iterator();
			iter.next(); // skip first = self
			best = null;
			score_best = -1.0;
			
			while(iter.hasNext())
			{
				other = iter.next();
				
				score_current_other_abs = calcInterestFactor(current, other);
				score_other_current_abs = calcInterestFactor(other, current);
				
				score_current_other = Math.min(score_current_other_abs / other.getFirst().getTags().size(), score_current_other_abs / current.getLast().getTags().size());
				score_other_current = Math.min(score_other_current_abs / other.getLast().getTags().size(), score_other_current_abs / current.getFirst().getTags().size());
				
				if(score_current_other > score_best)
				{
					score_best = score_current_other;
					best = other;
					current_first = true;
					
					if(score_best >= 1.0)
						break;
				}
				else if(score_other_current > score_best)
				{
					score_best = score_current_other;
					best = other;
					current_first = false;
					
					if(score_best >= 1.0)
						break;
				}
			}
			
			if(current_first)
			{
				current.addAll(best);
				chains.remove(best);
			}
			else
			{
				best.addAll(current);
				chains.remove(current);
			}
		}	
		
		this.output.addSlides(chains.getFirst());
		
	}
	
	public List<Slide> generateSlides(List<Photo> photos)
	{
		List<Slide> slides = new LinkedList<>();
		
		Photo vTmp = null;
		for(Photo p: photos)
		{
			if(p.isHorizontal())
			{
				slides.add(new Slide(p));
			}
			else if(vTmp != null)
			{
				slides.add(new Slide(p, vTmp));
				vTmp = null;
			}
			else
			{
				vTmp = p;
			}
		}
		
		return slides;
	}
}
