package agents;
import java.util.Random;

import contest.Action;
import contest.IterationPlay;
import contest.PayoutTable;

import java.util.ArrayList;

public abstract class Agent {
	
	private Random rand;
	
	public Agent(Long seed) {
		rand = new Random(seed);
	}
	
	protected double rand() {
		return rand.nextDouble();
	}

	public abstract Action decide(ArrayList<IterationPlay> plays, PayoutTable pt, boolean first) ;
	
}
