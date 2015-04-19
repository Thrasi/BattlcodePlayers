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
import static T103.BaseBot.trySpawn;
import static T103.BaseBot.theirHQ;
import static T103.BaseBot.getAllDirectionsTowards;


public class BuildingStrategies {
	
	// How much to sense for finding the spot
	private static final int SENSERANGE = 24;
	
	/**
	 * Finds a safe spot for building things. NOTE: Score system can be changed if it
	 * sucks.
	 * @return safest location according to score system
	 */
	public static MapLocation safeSpotForBuilding() throws GameActionException {
		MapLocation current = rc.getLocation();
		MapLocation[] potLoc =
				MapLocation.getAllMapLocationsWithinRadiusSq(current, SENSERANGE);

		int bestScore = Integer.MIN_VALUE;
		MapLocation best = null;
		for (MapLocation l : potLoc) {
			if (!isNormal(l) || isOccupied(l)) {
				continue;
			}
			int s = emptyScore(l);
			if (s < 2) {				// Otherwise I will close myself in
				continue;
			}
			
			// Square of empty spots around location
			s *= s*s;
			
			// Farther away from enemy HQ, higher score
			s += l.distanceSquaredTo(theirHQ) - current.distanceSquaredTo(theirHQ);
			
			// Closer to my HQ, higher score
			s -= l.distanceSquaredTo(current);
			
			if (s > bestScore) {
				bestScore = s;
				best = l;
			}
		}
		return best;
	}
	
	/**
	 * Finds good open spot for building.
	 * @return location
	 */
	public static MapLocation openSpotForBuilding() {
		MapLocation current = rc.getLocation();
		MapLocation[] potLoc =
				MapLocation.getAllMapLocationsWithinRadiusSq(current, SENSERANGE);

		int bestScore = emptyScore(current);
		MapLocation best = current;
		for (MapLocation l : potLoc) {
			if (!isNormal(l) || isOccupied(l)) {
				continue;
			}
			int s = emptyScore(l);
			if (s < 2) {				// Otherwise I will close myself in
				continue;
			}
			if (s > bestScore) {
				bestScore = s;
				best = l;
			}
		}
		return best;
	}

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
	 * Tries to build the building towards the given direction.
	 * @param target build towards this direction
	 * @param type type of the building to build
	 * @return true if built, false otherwise
	 */
	public static boolean tryBuildTowards(MapLocation target, RobotType type)
			throws GameActionException {
		
		for (Direction dir : getAllDirectionsTowards(target)) {
			if (tryBuild(dir, type)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean trySpawnEmpty(RobotType type) throws GameActionException {
		MapLocation current = rc.getLocation();
		int bestScore = Integer.MIN_VALUE;
		Direction best = null;
		for (Direction dir : directions) {
			MapLocation loc = current.add(dir);
			if (!isNormal(loc) || isOccupied(loc)) {		// Cannot spawn here
				continue;
			}
			int score = emptyScore(loc);
			if (score > bestScore) {
				bestScore = score;
				best = dir;
			}
		}
		if (best == null) {
			return false;
		}
		return trySpawn(best, type);
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
	 * Number of empty spots around given location. Empty spot means not occupied and
	 * normal.
	 * @param loc location to score
	 * @return empty score
	 */
	private static int emptyScore(MapLocation loc) {
		int score = 0;
		for (Direction d : directions) {
			MapLocation a = loc.add(d);
			if (!isOccupied(a) && isNormal(a)) {
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
