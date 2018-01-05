package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class PublicCliqueAgent extends CliqueAgent {
	
	public PublicCliqueAgent(Long seed) {
		super(seed);
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		int moves = plays.size();
		boolean correct = true;
		int i = 0;
		while (correct && (i < CODELENGTH) && (i < moves)) {
			if (plays.get(i).opp(first) != secretCodeAction(i, PUBLICSECRET)) {
				correct = false;
				break;
			}
			i++;
		}
		if (correct) {
			if (moves < CODELENGTH) return secretCodeAction(i, PUBLICSECRET);
			return maxTotalPayout(pt, first);
		}
		// plays modified tit for tat against non-clique agents
		return plays.get(moves-1).opp(first);
	}
}
