package contest;
import java.util.ArrayList;
import java.util.Random;

import agents.Agent;

public class Round {
	
	private int number;
	private int iterations;
	private PayoutTable pt;
	private AgentPool pool;
	private Random rand;
	
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
	
	public void playNextMatch() {
		Agent a1 = null;
		Agent a2 = null;
		if (pool.getMatchNumber() < 10000) {
			a1 = pool.randomAgent(rand.nextDouble(), rand.nextLong(), true);
			a2 = pool.randomAgent(rand.nextDouble(), rand.nextLong(), false);
		} else {
			a1 = pool.underrepresentedAgent(rand.nextLong(), true);
			a2 = pool.underrepresentedAgent(rand.nextLong(), false);
		}
		//System.out.println("Next match [" + pool.getMatchNumber() + "]: " +
		//		a1.getClass().getSimpleName() + " vs. " + a2.getClass().getSimpleName());
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
		//System.out.println(m1 + "\n" + m2 + "\nResult: " + score1 + " - " + score2 + "\n");
	}
	
	public boolean done() {
		//System.out.println(pool.distributionTable());
		return pool.fairDistribution();
	}
	
	public AgentPool survive() {
		return pool.survive();
	}
	
	public String toString() {
		return "Round " + number + ", " + iterations + " iterations\n" + pt + "\n" + pool;
	}
}
