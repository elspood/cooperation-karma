package contest;
import java.util.Random;

public class PayoutTable {

	private final int t1;
	private final int r1;
	private final int p1;
	private final int s1;
	
	private final int t2;
	private final int r2;
	private final int p2;
	private final int s2;
	
	public PayoutTable(Long seed) {
		Random r = new Random(seed);
		t1 = 3 + (int)Math.round(r.nextDouble() * 17);
		r1 = 2 + (int)Math.round(r.nextDouble() * (t1 - 3));
		p1 = 1 + (int)Math.round(r.nextDouble() * (r1 - 2));
		s1 = (int)Math.round(r.nextDouble() * (p1 - 1));
		
		t2 = 3 + (int)Math.round(r.nextDouble() * 17);
		r2 = 2 + (int)Math.round(r.nextDouble() * (t2 - 3));
		p2 = 1 + (int)Math.round(r.nextDouble() * (r2 - 2));
		s2 = (int)Math.round(r.nextDouble() * (p2 - 1));
	}
	
	public int payoutSelf(boolean first, Action myAction, Action oppAction) {
		if ((myAction == Action.COOP) && (oppAction == Action.COOP)) return (first ? r1 : r2);
		if ((myAction == Action.COOP) && (oppAction == Action.DEFECT)) return (first ? s1 : s2);
		if ((myAction == Action.DEFECT) && (oppAction == Action.COOP)) return (first ? t1 : t2);
		if ((myAction == Action.DEFECT) && (oppAction == Action.DEFECT)) return (first ? p1 : p2);
		throw new IllegalArgumentException("Invalid Action specified");
	}
	
	public int payoutOpp(boolean first, Action myAction, Action oppAction) {
		if ((myAction == Action.COOP) && (oppAction == Action.COOP)) return (first ? r2 : r1);
		if ((myAction == Action.COOP) && (oppAction == Action.DEFECT)) return (first ? t2 : t1);
		if ((myAction == Action.DEFECT) && (oppAction == Action.COOP)) return (first ? s2 : s1);
		if ((myAction == Action.DEFECT) && (oppAction == Action.DEFECT)) return (first ? p2 : p1);
		throw new IllegalArgumentException("Invalid Action specified");
	}
	
	public String toString() {
		return r1 + "\\" + r2 + " | " + s1 + "\\" + t2 + "\n" + t1 + "\\" + s2 + " | " + p1 + "\\" + p2;
	}
}
