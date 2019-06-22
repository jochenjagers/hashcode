package gingerninjas.qualification.julian;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import gingerninjas.qualification.Photo;
import gingerninjas.qualification.QualiSolver;
import gingerninjas.qualification.Slide;

public class Solver extends QualiSolver
{
	public static final double TARGET_SCORE = 0.8;
	public static final int MAX_TRIES = 100;
	
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
		int score_best_abs;
		int score_max_abs;
		double score_current_other;
		double score_other_current;
		double score_best;
		boolean current_first = false;
		int tries;
		
		while(chains.size() > 1)
		{
			logger.debug("chains: " + chains.size());
			Collections.shuffle(chains);
			current = chains.getFirst();
			iter = chains.iterator();
			iter.next(); // skip first = self
			best = null;
			score_best = -1.0;
			score_best_abs = 0;
			score_max_abs = Math.max(current.getFirst().getTags().size() / 2, current.getLast().getTags().size() / 2);
			tries = 0;
			
			while(iter.hasNext() && (tries < MAX_TRIES))// || best == null))
			{
				tries++;
				other = iter.next();
				
				if(other.getFirst().getTags().size() / 2 > score_best_abs)
				{
					score_current_other_abs = calcInterestFactor(current, other);
//					score_current_other = Math.min(score_current_other_abs / (double) (other.getFirst().getTags().size() / 2), score_current_other_abs / (double) (current.getLast().getTags().size() / 2));
				}
				else
				{
					score_current_other_abs = 0;
					score_current_other = 0;
				}
				
				if(other.getLast().getTags().size() / 2 > score_best_abs)
				{
					score_other_current_abs = calcInterestFactor(other, current);
//					score_other_current = Math.min(score_other_current_abs / (double) (other.getLast().getTags().size() / 2), score_other_current_abs / (double) (current.getFirst().getTags().size() / 2));
				}
				else
				{
					score_other_current_abs = 0;
					score_other_current = 0;
				}
				
//				if(score_current_other_abs > score_best_abs || (score_current_other_abs == score_best_abs && score_current_other > score_best))
				if(score_current_other_abs > score_best_abs)
				{
//					score_best = score_current_other;
					score_best_abs = score_current_other_abs;
					best = other;
					current_first = true;
					
//					logger.debug("score_best_abs=" + score_best_abs);
					if(score_best >= score_max_abs)//TARGET_SCORE)
						break;
				}
//				if(score_other_current_abs > score_best_abs || (score_other_current_abs == score_best_abs && score_other_current > score_best))
				if(score_other_current_abs > score_best_abs)
				{
//					score_best = score_other_current;
					score_best_abs = score_other_current_abs;
					best = other;
					current_first = false;

//					logger.debug("score_best_abs=" + score_best_abs);
					if(score_best >= score_max_abs)//TARGET_SCORE)
						break;
				}
			}
			
			if(best == null)
			{
				current.addAll(chains.getLast());
				chains.removeLast();
			}
			else if(current_first)
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
				if(p.getTags().size() <= 1)
					continue;
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
