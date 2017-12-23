package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class GreedyAgent extends Agent {
	
	public GreedyAgent(Long seed) {
		super(seed);
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		int cc = pt.payoutSelf(first, Action.COOP, Action.COOP);
		int cd = pt.payoutSelf(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutSelf(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutSelf(first, Action.DEFECT, Action.DEFECT);

		if ((cc > dc) && (cc > dd)) return Action.COOP;
		if ((cd > dc) && (cd > dd)) return Action.COOP;
		return Action.DEFECT;
	}

}
