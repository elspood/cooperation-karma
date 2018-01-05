package contest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import agents.Agent;

public class Round {
	
	private int number;
	private int iterations;
	private PayoutTable pt;
	private AgentPool pool;
	private Random rand;
	private HashMap<String,Boolean> shown = new HashMap<String,Boolean>();
	
	public Round(int number, int iterations, PayoutTable pt, AgentPool pool, Long seed) {
		this.number = number;
		this.iterations = iterations;
		this.pt = pt;
		this.pool = pool;
		this.rand = new Random(seed);
	}

	public PayoutTable payoutTable() {
		return pt;
	}
	
	public int iterations() {
		return iterations;
	}
	
	public boolean playNextMatch() {
		Agent a1 = null;
		Agent a2 = null;
		if (pool.getMatchNumber() < 10000) {
			a1 = pool.randomAgent(rand.nextDouble(), rand.nextLong(), true);
			a2 = pool.randomAgent(rand.nextDouble(), rand.nextLong(), false);
		} else {
			a1 = pool.underrepresentedAgent(rand.nextLong(), true);
			a2 = pool.underrepresentedAgent(rand.nextLong(), false);
		}
		ArrayList<IterationPlay> plays = new ArrayList<IterationPlay>();
		int score1 = 0, score2 = 0;
		for (int i=0; i < iterations; i++) {
			Action act1 = a1.decide(plays, pt, true);
			Action act2 = a2.decide(plays, pt, false);
			plays.add(new IterationPlay(act1, act2));
			score1 += pt.payoutSelf(true, act1, act2);
			score2 += pt.payoutOpp(true, act1, act2);
		}
		pool.score(a1.getClass(), a2.getClass(), score1, score2);
		String m1 = "M1: ", m2 = "M2: ";
		for (IterationPlay p : plays) {
			m1 += p.self(true) == Action.COOP ? "C" : "D";
			m2 += p.opp(true) == Action.COOP ? "C" : "D";
		}
		String key = a1.getClass().getSimpleName() + a2.getClass().getSimpleName();
		if (!shown.containsKey(key) && (a1.showMatches() || a2.showMatches())) {
			System.out.println("Next match [" + pool.getMatchNumber() + "]: " +
					a1.getClass().getSimpleName() + " vs. " + a2.getClass().getSimpleName());
			System.out.println(m1 + "\n" + m2 + "\nResult: " + score1 + " - " + score2 + "\n");
			shown.put(key, true);
			return true;
		}
		return false;
	}
	
	public boolean done() {
		//System.out.println(pool.distributionTable());
		if (pool.getMatchNumber() < 10000) return false;
		return pool.fairDistribution();
	}
	
	public AgentPool survive() {
		return pool.survive();
	}
	
	public String toString() {
		return "Round " + number + ", " + iterations + " iterations\n" + pt + "\n" + pool;
	}
}
