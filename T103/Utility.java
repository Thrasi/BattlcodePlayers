package T103;

public class Utility {

	/**
	 * Checks if the array is filled with same int values.
	 * @param xs array of ints
	 * @return true if all the same values, false otherwise
	 */
	public static boolean allEqual(int[] xs) {
		if (xs.length < 2) {
			return true;
		}
		for (int i = 1; i < xs.length; i++) {
			if (xs[i] != xs[i-1]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if all coordinates are on the same line.
	 * @param xs array of x coordinates
	 * @param ys array of y coordinates
	 * @return true if on the same line, false otherwise
	 */
	public static boolean onLine(int[] xs, int[] ys) {
		if (xs.length < 3) {
			return true;
		}
		
		int dx = xs[1] - xs[0];
		int dy = ys[1] - ys[0];
		if (dx == 0) {			// Lines are vertical
			for (int i = 2; i < xs.length; i++) {
				if (xs[i] - xs[i-1] != 0) {
					return false;
				}
			}
		} else {				// Not vertical
			double k = ((double) dy) / dx;
			for (int i = 2; i < xs.length; i++) {
				int dxi = xs[i] - xs[i-1];
				int dyi = ys[i] - ys[i-1];
				if (Math.abs(k - ((double) dyi) / dxi) > 0.01) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	/**
	 * Tuple class for some things.
	 */
	public static class Tuple {
		
		public int x, y;
		
		public Tuple(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	/**
	 * Generic tuple.
	 */
	public static class Pair<T1, T2> {
		
		public T1 x;
		public T2 y;
		
		public Pair(T1 x, T2 y) {
			this.x = x;
			this.y = y;
		}
	}
}
