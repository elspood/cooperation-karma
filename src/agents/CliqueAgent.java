package agents;

import contest.Action;

public abstract class CliqueAgent extends Agent {

	public static final int CODELENGTH = 5;
	public static final int PUBLICSECRET = 13;
	protected static int secret = PUBLICSECRET;
	protected static long codescore = 0;

	public CliqueAgent(Long seed) {
		super(seed);
	}

	public static void setSecret(int code) {
		secret = code;
		codescore = 0;
	}

	public static long codescore() {
		return codescore;
	}
	
	public static Action secretCodeAction(int i, int secret) {
		if ((i < 0) || (i >= CODELENGTH))
			throw new IllegalArgumentException("Secret sequence length is only " + CODELENGTH);
		if ((secret & (1 << i)) > 0) return Action.COOP;
		return Action.DEFECT;
	}

	protected static Action secretCodeAction(int i) {
		return secretCodeAction(i, secret);
	}

	public static void main(String[] args) {
		for (int i=0; i < 32; i++) {
			secret = i;
			System.out.print(i + ": ");
			for (int j=0; j < CODELENGTH; j++)
				System.out.print((secretCodeAction(j) == Action.COOP) ? "C" : "D");
			System.out.println();
		}
	}
}
