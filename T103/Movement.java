package T103;

import java.util.Arrays;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;
import static T103.BaseBot.rc;
import static T103.BaseBot.tryMove;
import static T103.BaseBot.isOccupied;

public class Movement {

	// Depth for bug nav search
	//private static final int DEPTH = 8;
	
	private static MapLocation target = null;
	private static int minDist = Integer.MAX_VALUE;
	private static MapLocation start = null;
	
	public static void setTarget(MapLocation loc) {
		if (!loc.equals(target)) {
			target = loc;
			minDist = Integer.MAX_VALUE;
			start = rc.getLocation();
		}
	}
	
	public static boolean tryBugMove() throws GameActionException {
		MapLocation current = rc.getLocation();
		Direction desired = current.directionTo(target);
		MapLocation next = current.add(desired);
		TerrainTile tile = rc.senseTerrainTile(next);
		int dist = next.distanceSquaredTo(target);
		if (!isOccupied(next) && isNormalOrUnknown(tile) && dist < minDist) {
			// On the desired move, exit
			
			boolean moved = tryMove(desired);
			if (moved) {
				minDist = dist;
			}
			return moved;
		}
		
		// Rotate left until you find something useful
		for (int i = 0; i < 7; i++) {
			desired = desired.rotateLeft();
			next = current.add(desired);
			tile = rc.senseTerrainTile(next);
			if (isOccupied(next) || !isNormalOrUnknown(tile)) {
				continue;
			}
			return tryMove(desired);
		}
		return false;
	}
	
	/**
	 * Bug search algorithm.
	 * @param target where I want to go
	 * @param avoidObst true if I should avoid obstacles, false otherwise
	 * @return pair with an array of moves and number of moves
	 * @throws GameActionException incorrect locations
	 */
	public static Direction[] bugPlanning(
			MapLocation target, boolean avoidObst, int depth) throws GameActionException {
		
		int d = 0;										// Current depth
		
		MapLocation current = rc.getLocation();			// Current search location
		int dist = current.distanceSquaredTo(target);	// Distance to continue
		
		Direction[] moves = new Direction[depth];		// Acts as queue of moves
		int idx = 0;
		
		while (d < depth) {
			d++;
			
			if (current.equals(target)) {
				return Arrays.copyOf(moves, idx);
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
				return Arrays.copyOf(moves, idx);
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
		return Arrays.copyOf(moves, idx);
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
