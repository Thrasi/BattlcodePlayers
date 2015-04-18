package T103;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;
import static T103.BaseBot.rc;
import static T103.BaseBot.isOccupied;
import static T103.BaseBot.isNormal;
import static T103.BaseBot.directions;
import static T103.BaseBot.tryBuild;


public class BuildingStrategies {

	/**
	 * Tries to build the building in the safest possible spot around builder's location.
	 * Safe spot is the one surrounded by as many VOID, OFF_MAP and other buildings.
	 * @param type type to build
	 * @return direction where it was built, null if it wasn't
	 * @throws GameActionException
	 */
	public static Direction tryBuildSafe(RobotType type) throws GameActionException {
		MapLocation current = rc.getLocation();
		int bestScore = Integer.MIN_VALUE;
		Direction bestDir = null;
		for (Direction dir : directions) {				// Check all possible moves
			MapLocation loc = current.add(dir);
			if (!isNormal(loc) || isOccupied(loc)) {		// Cannot build here
				continue;
			}

			int score = safeScore(loc);
			if (score > bestScore) {
				bestScore = score;
				bestDir = dir;
			}
		}
		
		if (bestDir == null) {
			return null;
		}
		if (tryBuild(bestDir, type)) {
			return bestDir;
		}
		return null;
	}
	
	/**
	 * Calculates score for safe building. Building is safe if everything around it
	 * is VOID, OFF_MAP or another building.
	 * @param loc location to score
	 * @return number of VOID, OFF_MAP and building in neighboring locations
	 */
	private static int safeScore(MapLocation loc) {
		int score = 0;
		for (Direction dir : directions) {
			MapLocation neighborLoc = loc.add(dir);
			if (isOffMapOrVoid(neighborLoc) || isOccupiedByBuilding(neighborLoc)) {
				score++;
			}
		}
		return score;
	}
	
	/**
	 * Does exactly what it says.
	 * @param loc location to test
	 * @return false if it is out of sensing range or if it not building, true otherwise
	 */
	private static boolean isOccupiedByBuilding(MapLocation loc) {
		try {
			RobotInfo robot = rc.senseRobotAtLocation(loc);
			if (robot == null) {
				return false;
			}
			return robot.type.isBuilding;
		} catch (GameActionException e) {
			return false;
		}
	}
	
	/**
	 * Does exactly what it says.
	 * @param loc location to test
	 * @return true if it is, false otherwise
	 */
	private static boolean isOffMapOrVoid(MapLocation loc) {
		TerrainTile tile = rc.senseTerrainTile(loc);
		return tile == TerrainTile.OFF_MAP || tile == TerrainTile.VOID;
	}
}
