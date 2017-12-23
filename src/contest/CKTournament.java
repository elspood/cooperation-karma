package contest;
import java.util.Random;

import agents.Agent;
import agents.CliqueAgent;
import agents.GreedyAgent;
import agents.NaughtyAgent;
import agents.NiceAgent;
import agents.PunisherAgent;
import agents.RandomAgent;
import agents.MyAgent;

import java.util.ArrayList;
import java.util.Arrays;

public class CKTournament {
	
	private static Random rand = new Random();
	private static ArrayList<Round> rounds = new ArrayList<Round>();

	private static final Class<Agent>[] DEFAULTAGENTS = new Class[] {
			// default agents - these will take up 30-70% of the starting pool
			GreedyAgent.class,
			NiceAgent.class,
			NaughtyAgent.class,
			PunisherAgent.class,
			RandomAgent.class,
			CliqueAgent.class,
			// add your agent populations here
			MyAgent.class,
	};
	
	private static int[] DEFAULTBASIS = new int[] {
			// default agents - these will take up 30%-70% of the starting pool
			1000,	// greedy
			1000,	// nice
			1000,	// naughty
			1000,	// punisher
			1000,	// random
			1000,	// clique
			// add your agent populations here - these will need to add up to 10000 basis points (100%)
			// during the actual competition, other contestants will make up the remaining 30%-70% of the pool
			4000,	// myagent
	};

	public static void main(String[] args) {
		// TODO: take random seed as arg
		int round = 1;
		AgentPool pool = new AgentPool(Arrays.asList(DEFAULTAGENTS), DEFAULTBASIS);
		while (!pool.domination()) {
			Round r = new Round(round, 90 + (int)Math.round(rand.nextDouble() * 20), new PayoutTable(rand.nextLong()), pool, rand.nextLong());
			rounds.add(r);
			System.out.println(r);
			do {
				for (int i=0; i < 100; i++) r.playNextMatch();
			} while (!r.done()) ;
			
			pool = r.survive();
			//System.out.println(pool);
			round++;
		}
	}

}
