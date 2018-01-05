package contest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import agents.Agent;
import agents.PublicCliqueAgent;
import agents.GreedyAgent;
import agents.JamesAgent;
import agents.NaughtyAgent;
import agents.NiceAgent;
import agents.ParityAgent;
import agents.PunisherAgent;
import agents.RandomAgent;
import agents.SecretCliqueAgent;

public class SecretHandshakeTester {
	
	private static Random rand = new Random();
	private static ArrayList<Round> rounds = new ArrayList<Round>();
	
	private static final Class<Agent>[] DEFAULTAGENTS = new Class[] {
			GreedyAgent.class,
			NiceAgent.class,
			NaughtyAgent.class,
			PunisherAgent.class,
			RandomAgent.class,
			PublicCliqueAgent.class,
			// add your agent populations here
			JamesAgent.class,
			ParityAgent.class,
			SecretCliqueAgent.class,
	};
	
	private static int[] BASIS = new int[] {
			// default agents - these will take up 30%-70% of the starting pool
			1000,	// greedy
			1000,	// nice
			1000,	// naughty
			1000,	// punisher
			1000,	// random
			1000,	// clique
			// add your agent populations here - these will need to add up to 10000 basis points (100%)
			// during the actual competition, other contestants will make up the remaining 30%-70% of the pool
			2000,	// myagent
			1000,	// fairagent
			1000,	// SecretCliqueAgent
	};
	
	private static long runWithSecret(int secret, long seed) {
		int round = 1;
		AgentPool pool = new AgentPool(Arrays.asList(DEFAULTAGENTS), BASIS);
		SecretCliqueAgent.setSecret(secret);
		rand.setSeed(seed);
		while (!pool.domination() && (round < 101)) {
			Round r = new Round(round, 90 + (int)Math.round(rand.nextDouble() * 20), new PayoutTable(rand.nextLong()), pool, rand.nextLong());
			rounds.add(r);
			System.out.println(r);
			do {
				for (int i=0; i < 100; i++) r.playNextMatch(false);
			} while (!r.done()) ;
			
			pool = r.survive();
			//System.out.println(pool);
			round++;
		}
		return SecretCliqueAgent.codescore() / (round - 1);
	}

	public static void main(String[] args) {
		Long seed = rand.nextLong();
		long[] score = new long[32];
		for (int i=0; i < 32; i++) {
			score[i] = runWithSecret(i, seed);
		}
		for (int i=0; i < 32; i++) {
			System.out.println(i + ":" + score[i]);
		}
	}
}
