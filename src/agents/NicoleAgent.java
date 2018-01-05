package agents;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

import java.util.ArrayList;
import java.util.Arrays;

public class NicoleAgent extends Agent {

	private Action a1;
	private int moves = -1;
	private int myScore = 0;
	private int oppScore = 0;
	private Action myBest;
	private Action oppBest;
	private Action teamBest;
	private Integer react_CD = 0;
	private Integer react_CC = 0;
	private Integer react_DD = 0;
	private Integer react_DC = 0;

	private int defects = 0;
	private int coops = 0;
	private int strategies = 8;
	private ArrayList<String> strategyOptions = new ArrayList<String>(Arrays.asList(
			"COPYCAT",
			"NICE",
			"NAUGHTY",
			"GREEDY",
			"TITTAT",
			"FORGIVING_TITTAT",
			"MY_CLIQUE"
	));

	private boolean copyCat = true;
	private boolean nice = true;
	private boolean naughty = true;
	private boolean greedy = true;
	private boolean titTat = true;
	private boolean forgivingTitTat = true;
	private boolean myClique = true;

	public NicoleAgent(Long seed) {
		super(seed);
	}

	private static Action[] secret = {
			Action.COOP,
			Action.DEFECT,
			Action.COOP,
			Action.DEFECT,
			Action.DEFECT,
			Action.COOP,
			Action.COOP,
			Action.DEFECT,
			Action.DEFECT,
			Action.DEFECT
	};

	private Action getMyBest(PayoutTable pt, boolean first){
		int cc = pt.payoutSelf(first, Action.COOP, Action.COOP);
		int cd = pt.payoutSelf(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutSelf(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutSelf(first, Action.DEFECT, Action.DEFECT);

		if (cc >= cd && cc >= dc && cc >= dd) {
			return Action.COOP;
		}
		if (cd >= cc && cd >= dc && cc >= dd) {
			return Action.COOP;
		}
		if (dc >= cc && dc >= cd && dc >= dd) {
			return Action.DEFECT;
		}
		return Action.DEFECT;
	}

	private Action getOppBest(PayoutTable pt, boolean first){
		int cc = pt.payoutOpp(first, Action.COOP, Action.COOP);
		int cd = pt.payoutOpp(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutOpp(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutOpp(first, Action.DEFECT, Action.DEFECT);

		if (cc >= cd && cc >= dc && cc >= dd) {
			return Action.COOP;
		}
		if (cd >= cc && cd >= dc && cc >= dd) {
			return Action.COOP;
		}
		if (dc >= cc && dc >= cd && dc >= dd) {
			return Action.DEFECT;
		}
		return Action.DEFECT;
	}

	private Action beatKnownOpp(PayoutTable pt, boolean first, Action opp){
		int myCoop = pt.payoutSelf(first, Action.COOP, opp);
		int myDefect = pt.payoutSelf(first, Action.DEFECT, opp);
		int oppCoop = pt.payoutOpp(first, Action.COOP, opp);

		if ((myCoop >= myDefect)) {
			return Action.COOP;
		}
		return Action.DEFECT;
	}

	private void eliminateStrategies(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		int moves = plays.size();

		if (moves < 1) {
			return;
		}

		IterationPlay lastPlay = plays.get(moves - 1);
		Action opponentAction = lastPlay.opp(first);

		if (opponentAction == Action.DEFECT) {
			defects += 1;
		} else {
			coops += 1;
		}

		if(coops < moves){
			strategyOptions.remove("NICE");
		}
		if (defects < moves){
			strategyOptions.remove("NAUGHTY");
		}
		if (opponentAction != oppBest) {
			strategyOptions.remove("GREEDY");
		}
		if (myClique && moves < secret.length) {
			if ( opponentAction != secret[moves-1]) {
					myClique = false;
					strategyOptions.remove("MY_CLIQUE");
			}
		}

		Action myAction;

		if (moves > 1){
			myAction = plays.get(moves - 2).self(first);

			if (opponentAction != myAction) {
				strategyOptions.remove("COPYCAT");
			}
			if ((myAction == Action.DEFECT && opponentAction != Action.DEFECT)) {
				strategyOptions.remove("TITTAT");
			}
			if (myAction == Action.COOP && opponentAction != Action.COOP) {
				strategyOptions.remove("TITTAT");
			}
			if (myAction == Action.COOP && opponentAction != Action.COOP) {
				strategyOptions.remove("FORGIVING_TITTAT");
			}

		}

		if (moves > 2) {
			myAction = plays.get(moves - 1).self(first);
			Action myEarlierAction = plays.get(moves - 2).self(first);

			if (myAction == Action.DEFECT
					&& myEarlierAction == Action.DEFECT
					&& opponentAction != Action.DEFECT) {
				strategyOptions.remove("FORGIVING_TITTAT");
			} else if (myAction == Action.DEFECT
					&& myEarlierAction == Action.COOP
					&& opponentAction != Action.DEFECT) {
				strategyOptions.remove("FORGIVING_TITTAT");
			}
		}
	}

	private Action findWinningAction(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first){
		eliminateStrategies(plays, pt, first);
		//System.out.println(strategyOptions);
		int moves = plays.size();

		if(strategyOptions.size() == 0 || strategyOptions.size() > 1) {
			return null;
		}

		if(strategyOptions.contains("NAUGHTY")){
			// Beat Naughty
			return beatKnownOpp(pt, first, Action.DEFECT);
		}
		if(strategyOptions.contains("NICE")){
			// Beat Nice
			return beatKnownOpp(pt, first, Action.DEFECT);
		}
		if(strategyOptions.contains("COPYCAT")){
			//Beat CopyCat
			return beatKnownOpp(pt, first, plays.get(moves - 1).self(first));
		}
		if(strategyOptions.contains("GREEDY")){
			//Beat Greedy
			return beatKnownOpp(pt, first, oppBest);
		}
		if (strategyOptions.contains("TITTAT") && moves >= 1) {
			if(pt.payoutSelf(first, Action.COOP, Action.COOP) >
					((pt.payoutSelf(first, Action.DEFECT, Action.COOP) + pt.payoutSelf(first, Action.COOP, Action.DEFECT))/2)){
				return plays.get(moves - 1).opp(first);
			}
			else if (((pt.payoutSelf(first, Action.DEFECT, Action.COOP) + pt.payoutSelf(first, Action.COOP, Action.DEFECT))/2) >
					((pt.payoutOpp(first, Action.DEFECT, Action.COOP) + pt.payoutOpp(first, Action.COOP, Action.DEFECT))/2)) {
				if (plays.get(moves).self(first) == Action.COOP) {
					return plays.get(moves-1).opp(first);
				} else {
					return Action.COOP;
				}
			}
		}
		if (forgivingTitTat && moves > 2) {
			if(pt.payoutSelf(first, Action.COOP, Action.COOP) >
					((2*(pt.payoutSelf(first, Action.DEFECT, Action.COOP)) + pt.payoutSelf(first, Action.COOP, Action.DEFECT))/3)){
				return plays.get(moves-1).opp(first);
			}
			else if (((2*(pt.payoutSelf(first, Action.DEFECT, Action.COOP)) + pt.payoutSelf(first, Action.COOP, Action.DEFECT))/3) >
					((2*(pt.payoutOpp(first, Action.DEFECT, Action.COOP)) + pt.payoutOpp(first, Action.COOP, Action.DEFECT))/3)) {
				if ((plays.get(moves-2).self(first) == Action.DEFECT) && (plays.get(moves - 1).self(first) == Action.DEFECT)) {
					return Action.COOP;
				} else {
					return Action.DEFECT;
				}
			}
		}
		return null;
	}

	private Action maxPayout(PayoutTable pt, boolean first) {
		int cc = pt.payoutSelf(first, Action.COOP, Action.COOP) + pt.payoutOpp(first, Action.COOP, Action.COOP);
		int cd = pt.payoutSelf(first, Action.COOP, Action.DEFECT) + pt.payoutOpp(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutSelf(first, Action.DEFECT, Action.COOP) + pt.payoutOpp(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutSelf(first, Action.DEFECT, Action.DEFECT) + pt.payoutOpp(first, Action.DEFECT, Action.DEFECT);

		//System.out.println(cc + " " + cd + " " + dc + " " + dd);

		if ((cc > dc) && (cc > dd)) return Action.COOP;
		if ((cd > dc) && (cd > dd)) return Action.COOP;
		return Action.DEFECT;
	}

	private Action one_step_expectedValue(PayoutTable pt, ArrayList<IterationPlay> plays, boolean first) {
		int moves = plays.size();
		int cc = pt.payoutSelf(first, Action.COOP, Action.COOP) + pt.payoutOpp(first, Action.COOP, Action.COOP);
		int cd = pt.payoutSelf(first, Action.COOP, Action.DEFECT) + pt.payoutOpp(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutSelf(first, Action.DEFECT, Action.COOP) + pt.payoutOpp(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutSelf(first, Action.DEFECT, Action.DEFECT) + pt.payoutOpp(first, Action.DEFECT, Action.DEFECT);

		int total = react_CC + react_CD + react_DC + react_DD;
		double likely_opp_coop = 0.0;
		double likely_opp_defect = 0.0;

		if(plays.get(moves-1).self(first) == Action.COOP){
			likely_opp_coop = (react_CC *1.0) / (react_CC + react_CD);
		} else {
			likely_opp_coop = (react_DC * 1.0) / (react_DC + react_DD);
		}
		likely_opp_defect = 1 - likely_opp_coop;

		double coop_ev = (cc * likely_opp_coop) + (cd * (likely_opp_defect));
		double defect_ev = (dc * likely_opp_coop) + (dd * (likely_opp_defect));

		if (coop_ev >= defect_ev) return Action.COOP;
		return Action.DEFECT;
	}

	private Action two_step_expectedValue(PayoutTable pt, ArrayList<IterationPlay> plays, boolean first) {
		int moves = plays.size();
		int cc = pt.payoutSelf(first, Action.COOP, Action.COOP);
		int cd = pt.payoutSelf(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutSelf(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutSelf(first, Action.DEFECT, Action.DEFECT);

		int total = react_CC + react_CD + react_DC + react_DD;
		double likely_opp_coop = 0.0;
		double likely_opp_defect = 0.0;

		if(plays.get(moves-1).self(first) == Action.COOP){
			likely_opp_coop = (react_CC *1.0) / (react_CC + react_CD);
		} else {
			likely_opp_coop = (react_DC * 1.0) / (react_DC + react_DD);
		}
		likely_opp_defect = 1 - likely_opp_coop;

		double coop_ev = (((cc * likely_opp_coop) + (cd * (likely_opp_defect)))
							+ ((cc * ((react_CC *1.0) / (react_CC + react_CD))) + (cd * ((react_CC *1.0) / (react_CC + react_CD)))))/2.0;
		double defect_ev = (((dc * likely_opp_coop) + (dd * (likely_opp_defect)))
							+ ((dc * ((react_DC *1.0) / (react_CC + react_CD))) + (dd * ((react_DC *1.0) / (react_CC + react_CD)))))/2.0;

		if (coop_ev >= defect_ev) return Action.COOP;
		return Action.DEFECT;
	}

	

	private void update_reactions(ArrayList<IterationPlay> plays, boolean first) {
		int moves = plays.size();
		if(plays.get(moves-2).self(first) == Action.COOP && plays.get(moves-1).opp(first) == Action.DEFECT) {
			react_CD += 1;
		}
		if(plays.get(moves-2).self(first) == Action.COOP && plays.get(moves-1).opp(first) == Action.COOP) {
			react_CC += 1;
		}
		if(plays.get(moves-2).self(first) == Action.DEFECT && plays.get(moves-1).opp(first) == Action.COOP) {
			react_DC += 1;
		}
		if(plays.get(moves-2).self(first) == Action.DEFECT && plays.get(moves-1).opp(first) == Action.COOP) {
			react_DD += 1;
		}
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		moves = plays.size();
		Action knownOpp = null;
		if (moves <= 0) {
			myBest = getMyBest(pt, first);
			oppBest = getOppBest(pt, first);
		} else {
			knownOpp = findWinningAction(plays, pt, first);
			myScore += pt.payoutSelf(first, plays.get(moves -1).self(first), plays.get(moves -1).opp(first));
			oppScore += pt.payoutOpp(first, plays.get(moves -1).self(first), plays.get(moves -1).opp(first));

		}
		if(moves > 1) {
			update_reactions(plays, first);
		}

		if(moves < secret.length && myClique) {
			return secret[moves];
		}

		if(myClique) {
			return maxPayout(pt, first);
		}
		if(oppScore > (myScore * 2.0)){
			return Action.DEFECT;
		}
		if(knownOpp != null) {
			return knownOpp;
		}
		return two_step_expectedValue(pt, plays, first);

	}

}

