package contest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import agents.Agent;

public class AgentPool {

	private List<Class> agents;
	private int[] basis;
	private int[] score;

	private int[] used1;
	private int[] used2;
	private int matches = 0;

	public AgentPool(List<Class> agents, int[] basis) {
		this.agents = agents;
		this.basis = basis;
		if (agents.size() != basis.length) throw new IllegalArgumentException("Agent list size doesn't match basis allocation");

		int total = 0;
		for (int i=0; i < basis.length; i++) total += basis[i];
		if (total != 10000) throw new IllegalArgumentException("Basis points do not total 10000 [" + total + "]");			

		score = new int[basis.length];
		used1 = new int[basis.length];
		used2 = new int[basis.length];
	}
	
	public void score(Class a1, Class a2, int s1, int s2) {
		for (int i=0; i < score.length; i++) {
			Class c = agents.get(i);
			if (c.equals(a1)) score[i] += s1;
			if (c.equals(a2)) score[i] += s2;
		}
	}

	public int getMatchNumber() {
		return matches;
	}

	public String distributionTable() {
		String s = "";
		for (int i=0; i < basis.length; i++) {
			s += agents.get(i).getSimpleName() + " (" + (basis[i] / 100) + "%): " +
					((used1[i] * 100) / matches) + "% as a1, " + 
					((used2[i] * 100) / matches) + "% as a2\n";
		}
		return s;
	}
	
	public boolean domination() {
		for (int i=0; i < basis.length; i++)
			if (basis[i] > 9900) return true;
		return false;
	}

	public boolean fairDistribution() {
		for (int i=0; i < basis.length; i++) {
			if ((used1[i] * 100) / matches != basis[i] / 100) return false;
			if ((used2[i] * 100) / matches != basis[i] / 100) return false;
		}
		return true;
	}

	public Agent underrepresentedAgent(Long seed, boolean first) {
		int furthestbehind = 0;
		int delta = Integer.MAX_VALUE;
		for (int i=0; i < basis.length; i++) {
			int d = first ? used1[i] : used2[i];
			d = ((d * 10000) / matches) - basis[i];
			if (d < delta) {
				furthestbehind = i;
				delta = d;
			}
		}
		if (first) used1[furthestbehind]++;
		else {
			used2[furthestbehind]++;
			matches++;
		}
		return fromAgentClass(furthestbehind, seed);
	}

	private Agent fromAgentClass(int i, Long seed) {
		try {
			return (Agent)agents.get(i).getDeclaredConstructor(Long.class).newInstance(seed);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Invalid class instantiation " + e);
		}
	}

	public Agent randomAgent(double pool, Long seed, boolean first) {
		int total = 0;
		double min = pool * 10000;		// convert to basis points
		for (int i=0; i < basis.length; i++) {
			if (min < total + basis[i]) {
				if (first) {
					used1[i]++;
					matches++;
				} else used2[i]++;
				return fromAgentClass(i, seed);
			}
			total += basis[i];
		}
		throw new IllegalArgumentException("Random pool selection failed to get a hit: " + pool);
	}
	
	public AgentPool survive() {
		int totalscore = 0;
		for (int s : score) totalscore += s;
		
		int worst = 0;
		int worstbasis = Integer.MAX_VALUE;
		int totalbasis = 0;
		int best = 0;
		int bestbasis = Integer.MIN_VALUE;
		
		int[] newbasis = new int[basis.length];
		for (int i=0; i < score.length; i++) {
			int newbase = (int)(((long)score[i] * 10000) / totalscore);
			totalbasis += newbase;
			if (newbase < worstbasis) {
				worstbasis = newbase;
				worst = i;
			}
			if (newbase > bestbasis) {
				bestbasis = newbase;
				best = i;
			}
			newbasis[i] = newbase;
			// System.out.println(agents.get(i).getSimpleName() + " " + score[i] + "/" + totalscore + " = " + newbasis[i]);
		}
		if (totalbasis < 10000) newbasis[worst] += 10000 - totalbasis;
		else if (totalbasis > 10000) newbasis[best] += 10000 - totalbasis;
		
		return new AgentPool(agents, newbasis);
	}

	public String toString() {
		String s = "";
		for (int i=0; i < basis.length; i++) {
			s += agents.get(i).getSimpleName() + " (" + (basis[i] / 100) + "%)\n";
		}
		return s;
	}
}
