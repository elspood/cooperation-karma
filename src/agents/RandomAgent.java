package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class RandomAgent extends Agent {
	
	public RandomAgent(Long seed) {
		super(seed);
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		if (rand() < 0.5) return Action.COOP;
		return Action.DEFECT;
	}

}
