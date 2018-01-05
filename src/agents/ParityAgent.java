package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class ParityAgent extends Agent {
	
	public ParityAgent(Long seed) {
		super(seed);
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		int self = 0, opp = 0;
		for (IterationPlay play : plays) {
			self += pt.payoutSelf(first, play.self(first), play.opp(first));
			opp += pt.payoutOpp(first, play.self(first), play.opp(first));
		}
		if (self < opp) return Action.DEFECT;
		return Action.COOP;
	}

}
