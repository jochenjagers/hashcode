package gingerninjas.qualification.jochen;

import java.util.Date;
import java.util.LinkedList;

import gingerninjas.qualification.Photo;
import gingerninjas.qualification.QualiSolver;
import gingerninjas.qualification.Slide;

public class Solver extends QualiSolver
{


	@Override
	protected void solve()
	{
		//while(true) {
			input.reset();
			output.reset();
		
		// create startconditions
		LinkedList<LinkedList<Slide>> chains = new LinkedList<>();
		Photo v = null;
		for(Photo p : input.getPhotos()) {
			if(p.isHorizontal()) {
				if(p.getTags().size() <= 1) {
					continue;
				}
				LinkedList<Slide> l = new LinkedList<>();
				l.add(new Slide(p));
				chains.add(l);
			} else {
				if(v == null) {
					v = p;
				} else {
					LinkedList<Slide> l = new LinkedList<>();
					l.add(new Slide(p,v));
					chains.add(l);
					v = null;
				}
			}
		}
		long d = System.currentTimeMillis();
		while(chains.size() > 1) {
			
			if((System.currentTimeMillis() - d) > 5000) {
				logger.info(input.getName() + " chaincount: " + chains.size());
				d = System.currentTimeMillis();
			}

			LinkedList<Slide> s = chains.getFirst();
			//LinkedList<Slide> s = chains.get((int)(Math.random()*(chains.size()-1)));
			
			LinkedList<Slide> bestChain = null;
			boolean front = true;
			int points = 0;
			int count = 0;
			for(LinkedList<Slide> chain : chains) {
			//for(int i = 0; i < 100; ++i) {
				//LinkedList<Slide> chain = chains.get((int)(Math.random()*(chains.size()-1)));
				//if(bestChain != null && count > 100) {
				//	break;
				//}
				if(s != chain /*&& (chain.getFirst().getTags().size()/2 > ponits || chain.getLast().getTags().size()/2 > ponits)*/) {
					int p1 = calcInterestFactor(s, chain);
					int p2 = calcInterestFactor(chain, s);
					if(p1 >= p2 && p1 > points) {
						// more points
						bestChain = chain;
						front = true;
						points = p1;
						/*
						if(s.getLast().getMaxScore() <= ponits && chain.getFirst().getMaxScore() <= ponits ) {
							break;
						}
						*/
					} else if(p2 > points) {
						bestChain = chain;
						front = false;
						points = p2;
						/*
						if(chain.getLast().getMaxScore() <= ponits && s.getFirst().getMaxScore() <= ponits ) {
							break;
						}
						*/
					}
				}
			}
			if(bestChain != null ) {
				if(front == true) {
					// more points
					s.addAll(bestChain);
					chains.remove(bestChain);
				} else {
					bestChain.addAll(s);
					chains.remove(s);
				}
			} else {
				s.addAll(chains.getLast());
				chains.remove(chains.getLast());
			}
		}
		output.addSlides(chains.getFirst());
		output.calcScore();
		logger.info("Points: " + output.getScore());
		//}
	}

	
}
