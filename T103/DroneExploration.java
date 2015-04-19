package T103;

import T103.Utility.Pair;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import static T103.BaseBot.myHQ;
import static T103.BaseBot.rc;
import static T103.BaseBot.getSurroundingLocations;
import static T103.BaseBot.tryMoveTo;
import static T103.BaseBot.tryMove;


public class DroneExploration {
	
	// How far to move point to ensure that corner will be reached
	private static final int CORNERMULTIPLIER = 400;
	
	// Max unsuccessful moves until I try to use bug navigation
	private static final int MAXFAILS = 4;

	
	/**
	 * Explores one corner of the map and calculates the size of the map. Drone tries to
	 * move in given direction if possible. In each step it tries to detect corner of
	 * the map. Once the corner is found it returns it. If it cannot find corner,
	 * it will move infinitely towards it.
	 * @param dir direction in which to move to find corner
	 * @return corner it finds when going in given direction
	 * @throws GameActionException
	 */
	public static MapLocation exploreCorner(Direction dir) throws GameActionException {
		MapLocation loc = myHQ.add(dir, CORNERMULTIPLIER);		// Assume corner
		while (true) {
			for (MapLocation l : getSurroundingLocations()) {
				if (MapInfo.isCorner(l)) {
					return l;
				}
			}

			// Try to move using no smart moves
			int failsInARow = 0;
			while (!tryMoveTo(loc) && failsInARow < MAXFAILS) {
				failsInARow++;
				rc.yield();
			}
			
			// I have succeeded to move using no smart moves
			if (failsInARow < MAXFAILS) {
				continue;
			}
			
			// Otherwise use bug navigation to move
			Direction[] dirs = Movement.bugPlanning(loc, false, 4);
			int size = dirs.length;
			for (int i = 0; i < size;) {
				if (tryMove(dirs[i])) {
					i++;
				}
				rc.yield();
			}
		}
	}
	
}
