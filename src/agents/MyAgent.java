package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class MyAgent extends Agent {
	
	public MyAgent(Long seed) {
		super(seed);
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		int coop = pt.payoutSelf(first, Action.COOP, Action.COOP) + pt.payoutSelf(first, Action.COOP, Action.DEFECT);
		int defect = pt.payoutSelf(first, Action.DEFECT, Action.COOP) + pt.payoutSelf(first, Action.DEFECT, Action.DEFECT);
		// play randomly, weighted by payout result
		if (rand() < (float)coop / (coop + defect)) return Action.COOP;
		return Action.DEFECT;
	}

}
