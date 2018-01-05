package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class DansCrappyAgent extends Agent {
	
	public DansCrappyAgent(Long seed) {
		super(seed);
	}

	private static Action[] secret = {
			Action.COOP,
			Action.DEFECT,
			Action.COOP,
			Action.COOP,
			Action.DEFECT,
			Action.COOP,
			Action.DEFECT,
			Action.DEFECT,
	};

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		int moves = plays.size();
		if (moves > 100 || (moves > 60 && moves % 2 == 1))
				return Action.DEFECT;
		boolean correct = true;
		int i = 0;
		while (correct && (i < secret.length) && (i < moves)) {
			if (plays.get(i).opp(first) != secret[i]) {
				correct = false;
				break;
			}
			i++;
		}
		if (correct) {
			if (moves < secret.length) return secret[moves];
			return maxPayout(pt, first);
		}
		// plays modified tit for tat against non-clique agents
		return plays.get(moves-1).opp(first);
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
}
