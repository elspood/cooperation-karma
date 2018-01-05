package agents;
import java.util.Random;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

import java.util.ArrayList;

public abstract class Agent {
	
	private Random rand;
	private boolean show = false;
	
	public Agent(Long seed) {
		rand = new Random(seed);
	}
	
	protected double rand() {
		return rand.nextDouble();
	}
	
	public void show(boolean show) {
		this.show = show;
	}
	
	public boolean showMatches() {
		return show;
	}

	public abstract Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) ;

	protected static Action maxTotalPayout(PayoutTable pt, boolean first) {
		int cc = pt.payoutSelf(first, Action.COOP, Action.COOP) + pt.payoutOpp(first, Action.COOP, Action.COOP);
		int cd = pt.payoutSelf(first, Action.COOP, Action.DEFECT) + pt.payoutOpp(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutSelf(first, Action.DEFECT, Action.COOP) + pt.payoutOpp(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutSelf(first, Action.DEFECT, Action.DEFECT) + pt.payoutOpp(first, Action.DEFECT, Action.DEFECT);
		
		//System.out.println(cc + " " + cd + " " + dc + " " + dd);

		if ((cc > dc) && (cc > dd)) return Action.COOP;
		if ((cd > dc) && (cd > dd)) return Action.COOP;
		return Action.DEFECT;
	}
	
	protected static Action maxSelfPayout(PayoutTable pt, boolean first) {
		int cc = pt.payoutSelf(first, Action.COOP, Action.COOP);
		int cd = pt.payoutSelf(first, Action.COOP, Action.DEFECT);
		int dc = pt.payoutSelf(first, Action.DEFECT, Action.COOP);
		int dd = pt.payoutSelf(first, Action.DEFECT, Action.DEFECT);

		Action a = Action.DEFECT;
		if ((cc > dc) && (cc > dd)) a = Action.COOP;
		if ((cd > dc) && (cd > dd)) a = Action.COOP;
		if (a != Action.DEFECT)
			System.err.println("Max self payout is not DEFECT? Hmm.....agent " + ((first) ? 1 : 2) + "\n" + pt.toString());
		return Action.DEFECT;
	}
	
}
