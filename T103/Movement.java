package T103;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;
import static T103.BaseBot.rc;
import static T103.BaseBot.isOccupied;
import static T103.Utility.Pair;

public class Movement {

	// Depth for bug nav search
	//private static final int DEPTH = 8;
	
	/**
	 * Bug search algorithm.
	 * @param target where I want to go
	 * @param avoidObst true if I should avoid obstacles, false otherwise
	 * @return pair with an array of moves and number of moves
	 * @throws GameActionException incorrect locations
	 */
	public static Pair<Direction[], Integer> bugPlanning(
			MapLocation target, boolean avoidObst, int depth) throws GameActionException {
		
		int d = 0;										// Current depth
		
		MapLocation current = rc.getLocation();			// Current search location
		int dist = current.distanceSquaredTo(target);	// Distance to continue
		
		Direction[] moves = new Direction[depth];		// Acts as queue of moves
		int idx = 0;
		
		while (d < depth) {
			d++;
			
			if (current.equals(target)) {
				return new Pair<>(moves, idx);
			}
			
			Direction desired = current.directionTo(target);
			MapLocation next = current.add(desired);
			TerrainTile tile = rc.senseTerrainTile(next);
			if (!isOccupied(next)
					&& (!avoidObst || isNormalOrUnknown(tile))
					&& (avoidObst || tile != TerrainTile.OFF_MAP)
					&& next.distanceSquaredTo(target) < dist) {
				// On the desired move, exit
				
				moves[idx] = desired;
				idx++;
				return new Pair<>(moves, idx);
			}
			
			// Rotate left until you find something useful
			for (int i = 0; i < 7; i++) {
				desired = desired.rotateLeft();
				next = current.add(desired);
				tile = rc.senseTerrainTile(next);
				if (isOccupied(next)
						|| (avoidObst && !isNormalOrUnknown(tile))
						|| (!avoidObst && tile == TerrainTile.OFF_MAP)) {
					continue;
				}
				
				moves[idx] = desired;
				idx++;
				current = current.add(desired);
				break;
			}
		}
		return new Pair<>(moves, idx);
	}
	
	/**
	 * Checks if the tile is normal or unknown.
	 * @param tile tile to check
	 * @return true if normal or unknown, false otherwise
	 */
	private static boolean isNormalOrUnknown(TerrainTile tile) {
		return tile == TerrainTile.NORMAL || tile == TerrainTile.UNKNOWN;
	}
	
}
