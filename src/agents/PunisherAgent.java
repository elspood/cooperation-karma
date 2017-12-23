package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class PunisherAgent extends Agent {
	
	public PunisherAgent(Long seed) {
		super(seed);
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		int moves = plays.size();
		if (moves == 0) return Action.COOP;
		IterationPlay last = plays.get(moves-1);
		return last.opp(first);
		// TODO: probabilistically punish based on average of previous moves
	}

}
