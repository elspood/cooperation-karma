package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;


//TODO - fight against T2F optimizers (never did...)
public class GregLullAgent extends Agent {
	
	public GregLullAgent(Long seed) {
		super(seed);
	}

	private static Action[] secret = {
			Action.DEFECT,
			Action.COOP,
			Action.COOP,
			Action.DEFECT,
			Action.COOP,
		};
	
	private static Action[] friendSecret = {
			Action.DEFECT,
			Action.COOP,
			Action.COOP,
			Action.DEFECT,
			Action.COOP,
		};

		@Override
		public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
			
			String opponentPersona = "IDK";
			
			// do our little secret thing at the very start
		
			int moves = plays.size();
			int i;
			
			if(moves == 0) { // experiment - build a secret code that doesn't throw away too many points
				Action bestmove = maxPayout(pt, first);
				Action friendmove = maxPayout(pt,!first);
						
				
				for(i=0;i<secret.length;i++) {
						secret[i] = bestmove;
						friendSecret[i] = friendmove;
						if((i==1 )|| (i==4)) {
							if(bestmove == Action.COOP) // make a sub-optimal move on #2 and #5
								secret[i] =Action.DEFECT;
							else
								secret[i]= Action.COOP;
							
							if(friendmove == Action.COOP) // make a sub-optimal move on #2 and #5
								friendSecret[i] =Action.DEFECT;
							else
								friendSecret[i]= Action.COOP;

						}
				}
				
		
				return secret[0]; // let's kick it off!
			}
						

			// not at the start, the game is afoot!
			opponentPersona = guessPersona(plays, pt, first);
			//	System.out.println("I think I'm up against a " + opponentPersona);
			
			// if one of us, maximize payout for both

			if (	opponentPersona == 	"oneOfUs") {
				if(moves<secret.length) {
					return secret[moves];
				}
				
				return maxPayout(pt, first);
			}

			// if we have a tit-for-tatter, optimize for us over 4 rounds
			if (	opponentPersona == 	"tft") {
				return maxForTatter(pt, plays.get(moves-1).self(first), first);
			}
			
			// if we have a dove, devil or rando, always defect 
			if (	opponentPersona == 	"dove"){
				return maxForDove(pt,first);
			}
			
			if (opponentPersona ==	"devil") {
				return maxForDevil(pt,first);
			}
			if (opponentPersona ==	"random") {
				return maxForRandom(pt,first);
			}

			// if we're not sure, TFT but random a little to throw other bots off
			if(rand() < 0.1) 
					return Action.COOP;
			else if (rand() < 0.1)  
				return Action.DEFECT;
			else
				return (plays.get(moves-1).opp(first));			
		}

	private Action maxPayout(PayoutTable pt, boolean first) {
		int cc = pt.payoutSelf(first, Action.COOP, Action.COOP) + pt.payoutOpp(first, Action.COOP, Action.COOP);
		int cd = pt.payoutSelf(first, Action.COOP, Action.DEFECT) + pt.payoutOpp(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutSelf(first, Action.DEFECT, Action.COOP) + pt.payoutOpp(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutSelf(first, Action.DEFECT, Action.DEFECT) + pt.payoutOpp(first, Action.DEFECT, Action.DEFECT);
		
		
		if ((cc > dc) && (cc > dd)) return Action.COOP;
		if ((cd > dc) && (cd > dd)) return Action.COOP;
		
				
		return Action.DEFECT;
	}
	
	private Action maxForDove(PayoutTable pt, boolean first) {
		
		if(pt.payoutSelf(first, Action.COOP, Action.COOP) > pt.payoutSelf(first, Action.DEFECT, Action.COOP))
		return Action.COOP;	
				
		return Action.DEFECT;
	}
	
	private Action maxForDevil(PayoutTable pt, boolean first) {
		if(pt.payoutSelf(first, Action.COOP, Action.DEFECT) > pt.payoutSelf(first, Action.DEFECT, Action.DEFECT))
		return Action.COOP;	
		return Action.DEFECT;
		
	}
	
	private Action maxForRandom(PayoutTable pt, boolean first) {
		
		if((pt.payoutSelf(first, Action.COOP, Action.DEFECT) + pt.payoutSelf(first, Action.COOP, Action.COOP)) >
			(pt.payoutSelf(first, Action.DEFECT, Action.DEFECT) + pt.payoutSelf(first, Action.DEFECT, Action.COOP)))
			return Action.COOP;
		
		return Action.DEFECT;
		
	}
	
	
	
	private Action maxForTatter(PayoutTable pt, Action opponentMove, boolean first) {
		int cc = pt.payoutSelf(first, Action.COOP, Action.COOP);
		int cd = pt.payoutSelf(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutSelf(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutSelf(first, Action.DEFECT, Action.DEFECT);
		
		int defectEV = 0;
		int coopEV = 0;
		
		if(opponentMove == Action.COOP) {
			defectEV =  dc+cd;
			coopEV = cc+cc;
			if(defectEV > coopEV) return Action.DEFECT; return Action.COOP;
		}

		if(opponentMove == Action.DEFECT) {
			defectEV =  dd+dd;
			coopEV = cd+cc;
			if(defectEV > coopEV) return Action.DEFECT; return Action.COOP;
		}
		
		return Action.DEFECT;
	}
	
	
	private String guessPersona(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {

		int i;
		
		Action bestmove = maxPayout(pt, first);
		Action friendmove = maxPayout(pt,!first);
		// TODO: figure out how to not have to recreate the best move table every time!
		for(i=0;i<secret.length;i++) {
			secret[i] = bestmove;
			friendSecret[i] = friendmove;
			if((i==1 )|| (i==4)) {
				if(bestmove == Action.COOP) // make a sub-optimal move on #2 and #5
					secret[i] =Action.DEFECT;
				else
					secret[i]= Action.COOP;
				
				if(friendmove == Action.COOP) // make a sub-optimal move on #2 and #5
					friendSecret[i] =Action.DEFECT;
				else
					friendSecret[i]= Action.COOP;

			}
	}
		
		int moves = plays.size();
		
		boolean oneOfUs = true;
		boolean tft=true;
		boolean dove = true;
		boolean devil = true;
		boolean random = true;
	
		
		int searchDepth = 5; // tune to perfection...
		
		
		// is it one of us?
		for(i=0;i<moves;i++) {	

				if(i<friendSecret.length){
					if(plays.get(i).opp(first) != friendSecret[i]) {
						oneOfUs = false;
						break; // not one of us --- keep looking!
					}
				} else {
					if(plays.get(i).opp(first) != maxPayout(pt,!first)) { // they should have done the optimal thing
						oneOfUs = false;
						break; // not one of us --- keep looking!	
				}
					
			}
		}
		

		if(oneOfUs) {
//			System.out.println("I found one of us!");
			return "oneOfUs"; // welcome sister, all scores are ours!
		}		

		// didn't work out, let's look for a static player	
	
		for(i=0;i<moves;i++) {
			if(dove && (plays.get(i).opp(first) != Action.COOP)) dove=false;
			if(devil && (plays.get(i).opp(first) != Action.DEFECT)) devil =false;
		}
		
		if(dove) {
			return "dove";
		}
		
		if(devil) {
			return "devil";
		}
		
		//still looking - let's try tit for tat
		
	if(moves>searchDepth) {
		for(i=0; i<searchDepth; i++) {		
			if(plays.get(moves-i-1).opp(first) != plays.get(moves-i-2).self(first)) {
				tft = false;
				break;
			}
		}
		if(tft) {
			return "tft";
		}
	}
		
	if (moves>searchDepth)
		for(i=0; i<searchDepth; i++) {		
			if(plays.get(moves-i-1).opp(first) != plays.get(moves-i-2).self(first)) {
				tft = false;
				break;
			}
		}
		if(random) {
			return "random";
		}

		return "IDK";
	
	}
}


