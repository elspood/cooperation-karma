package contest;
public class IterationPlay {

		private Action a1;
		private Action a2;
		
		public IterationPlay(Action act1, Action act2) {
			a1 = act1;
			a2 = act2;
		}

		public Action self(boolean first) {
			return (first ? a1 : a2);
		}
		
		public Action opp(boolean first) {
			return (first ? a2 : a1);
		}
}
