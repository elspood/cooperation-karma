package agents;
import java.util.ArrayList;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

public class JamesAgent extends Agent {
	
	private static final int CHECKROUND = 12;
	
	public JamesAgent(Long seed) {
		super(seed);
	}

	@Override
	public Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) {
		boolean isSelf = true;
		boolean isClique = true;
		boolean isNaughty = true;
		boolean isNice = true;
		boolean isPunisher = true;
		int defectcount = 0;
		
		int rounds = plays.size();
		for (int i=0; i < rounds; i++) {
			Action cliqueaction = (i < CliqueAgent.CODELENGTH) ?
				CliqueAgent.secretCodeAction(i, CliqueAgent.PUBLICSECRET) : maxTotalPayout(pt, !first);
			Action opp = plays.get(i).opp(first);
			if (opp != cliqueaction) {
				isClique = false;
				if (i != CHECKROUND) isSelf = false;
			}
			if (opp != Action.DEFECT) isNaughty = false;
			if (opp != Action.COOP) isNice = false;
			if ((i == 0) && (opp != Action.COOP)) isPunisher = false;
			if ((i > 0) && (opp != plays.get(i-1).self(first))) isPunisher = false;
			if (opp == Action.DEFECT) defectcount++;
		}
		
		if (isClique) {
			if (rounds < CliqueAgent.CODELENGTH) return CliqueAgent.secretCodeAction(rounds, CliqueAgent.PUBLICSECRET);
			// exploit fully if public clique agent (or self)
			if (maxTotalPayout(pt, first) == Action.DEFECT) return Action.DEFECT;
			if (rounds == CHECKROUND) return Action.DEFECT;	// todo: change this to second-best action
		}
		if (isSelf) return maxTotalPayout(pt, first);
		if (isNaughty ^ isNice) return Action.DEFECT;
		
		if (isPunisher && (rounds < 90)) return Action.COOP;
		
		int ratio = (defectcount * 100) / rounds;
		if ((45 < ratio) && (ratio < 55)) {
			// probably random - defect to maximize payout
			return Action.DEFECT;
		}
		
		if (rand() < 0.05) return Action.COOP;
		return Action.DEFECT;
	}

}
