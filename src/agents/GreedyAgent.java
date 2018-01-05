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
		return maxSelfPayout(pt, first);
	}

}
