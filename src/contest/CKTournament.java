package contest;
import java.util.Random;

import agents.*;

import java.util.ArrayList;
import java.util.Arrays;

public class CKTournament {
	
	private static Random rand = new Random();
	private static ArrayList<Round> rounds = new ArrayList<Round>();
	
	private static long timer;
	private static long start;

	private static final Class<Agent>[] DEFAULTAGENTS = new Class[] {
			// default agents - these will take up 30-70% of the starting pool
			SecretCliqueAgent.class,
			PunisherAgent.class,
			RandomAgent.class,
			GreedyAgent.class,
			NaughtyAgent.class,
			NiceAgent.class,
			PublicCliqueAgent.class,
			// add your agent populations here
			DansCrappyAgent.class,
			NicoleAgent.class,
			GregLullAgent.class,
	};
	
	private static int[] DEFAULTBASIS = new int[] {
			// default agents - these will take up 30%-70% of the starting pool
			3000,
			1000,
			1000,
			167,
			167,
			166,
			1500,
			// add your agent populations here - these will need to add up to 10000 basis points (100%)
			// during the actual competition, other contestants will make up the remaining 30%-70% of the pool
			1000,
			1000,
			1000,
	};

	public static void main(String[] args) {
		Long seed = 0L;
		try {
			seed = Long.decode(args[0]);
			rand.setSeed(seed);
		} catch (Exception e) {
			System.err.println("Invalid seed parameter\n" + e);
			return;
		}
		
		start = System.currentTimeMillis();
		try {
			timer = Integer.decode(args[1]) * 60 * 1000;
		} catch (Exception e) {
			System.err.println("Invalid timer parameter - time must be specified in minutes\n" + e);
			return;
		}
		
		AgentPool pool= new AgentPool(Arrays.asList(DEFAULTAGENTS), DEFAULTBASIS, 7, rand.nextLong());
		int round = 1;
 		while (!pool.domination() && (round < 101)) {
			Round r = new Round(round, 90 + (int)Math.round(rand.nextDouble() * 20), new PayoutTable(rand.nextLong()), pool, rand.nextLong());
			rounds.add(r);
			System.out.println(r);
			int matchesshown = 0;
			int matches = 0;
			do {
				for (int i=0; i < 100; i++) {
					if (r.playNextMatch(round < 6)) {
						matchesshown++;
						sleep();
						if (matchesshown % 10 == 9) System.out.println(r);
					}
				}
				matches += 100;
				if (matches % 2500 == 0) {
					System.out.println(r.scoreboard());
					sleep();
				}
			} while (!r.done()) ;
			
			pool = r.survive();
			//System.out.println(pool);
			round++;
		}
		
		System.out.println("FINAL RESULT after " + (round-1) + " rounds:\n" + pool);
	}
	
	private static void sleep() {
		try {
			long elapsed = System.currentTimeMillis() - start;
			int sleep = 6;
			if (timer - elapsed < 5 * 60 * 1000) sleep = 3;
			if (timer - elapsed < 60000) sleep = 1;
			if (timer - elapsed < 20000) sleep = 0;
			Thread.sleep(sleep * 1000);
		} catch (InterruptedException e) {
			
		}
	}
}
