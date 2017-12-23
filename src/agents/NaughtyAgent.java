package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class NaughtyAgent extends Agent {
	
	public NaughtyAgent(Long seed) {
		super(seed);
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		return Action.DEFECT;
	}

}
