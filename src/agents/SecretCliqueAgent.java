package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class SecretCliqueAgent extends CliqueAgent {

	public SecretCliqueAgent(Long seed) {
		super(seed);
		secret = 30;
	}

	private static void scoreOpening(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		for (int i=0; i < CODELENGTH; i++) {
			IterationPlay play = plays.get(i);
			codescore += pt.payoutSelf(first, play.self(first), play.opp(first));
		}
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		int moves = plays.size();
		boolean correct = true;
		if (moves == CODELENGTH) scoreOpening(plays, pt, first);
		int i = 0;
		while (correct && (i < CODELENGTH) && (i < moves)) {
			if (plays.get(i).opp(first) != secretCodeAction(i)) {
				correct = false;
				break;
			}
			i++;
		}
		// check to make sure partner is playing max payout moves, too
		Action max = maxTotalPayout(pt, !first);
		while (correct && (i < moves)) {
			if (plays.get(i).opp(first) != max) correct = false;
			else i++;
		}
		if (correct) {
			if (moves < CODELENGTH) return secretCodeAction(moves);
			return maxTotalPayout(pt, first);
		}
		// plays modified tit for tat against non-clique agents
		return plays.get(moves-1).opp(first);
	}
}
