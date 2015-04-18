package T103;

import java.util.HashSet;

import T103.Utility.Pair;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import static T103.BaseBot.rc;
import static T103.BaseBot.isOccupied;
import static T103.BaseBot.isNormal;
import static T103.BaseBot.directions;

public class Mining {

	// Minimum ore required to be worth mining
	public static final double MINOREMINER = 8.0;
	public static final double MINOREBEAVER = 0.0;
	
	// Sense range to search for ore, low values make strategies go fast
	private static final int SENSERANGE = 50;

	
	/**
	 * Mining strategy that scans all map locations in a certain sense range and finds
	 * the closest one. Strategy is quite inefficient, takes in between 7000 and 12000
	 * bytecode.
	 * @return a pair with map location to go to mine and a value of minimum ore limit
	 * which is used to adjust miner limits 
	 */
	public static Pair<MapLocation, Double> closestOre() throws GameActionException {
		MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(
				rc.getLocation(), SENSERANGE);
		double minOre = rc.getType() == RobotType.MINER ? MINOREMINER : MINOREBEAVER;
		
		// For looking for best location
		MapLocation oreLoc = null;
		MapLocation lowOreLoc = null;
		int minDist = Integer.MAX_VALUE;
		int minDistLow = Integer.MAX_VALUE;
		boolean foundBigger = false;
		
		// Iterating over all map locations to find best one
		for (MapLocation loc : locations) {
			if (!isNormal(loc) || isOccupied(loc)) {	// Can't mine here
				continue;
			}
		
			// Decide is this the high or low ore location
			double oreLevel = rc.senseOre(loc);
			int dist = rc.getLocation().distanceSquaredTo(loc);
			if (dist < minDist && oreLevel > minOre) {
				minDist = dist;
				oreLoc = loc;
				foundBigger = true;
			} else if (!foundBigger && dist < minDistLow && oreLevel > 0.0) {
				minDistLow = dist;
				lowOreLoc = loc;
			}
		}

		if (foundBigger) {
			return new Pair<>(oreLoc, minOre);
		}
		return new Pair<>(lowOreLoc, 0.0);
	}
	
	/**
	 * Mining strategy that scans locations for ore looking at closest first. It is 
	 * a bfs implementation to look for ore. Strategy is very fast in the beginning
	 * when everything is very close (1500-3000 bytecode) but becomes extremely
	 * inefficient towards the end when it has to look far away (2-3 turns).
	 * It works well for small values of SENSERANGE. SENSERANGE is not really
	 * a range how far to sense ore, rather how many nodes will the algorithm explore.
	 * @return same as closestOre function
	 */
	public static Pair<MapLocation, Double> closestOrebfs() {
		MapLocation curr = rc.getLocation();
		
		// Open queue
		MapLocation[] open = new MapLocation[SENSERANGE];
		open[0] = curr;
		int i = 0, j = 1;
		
		// Visite set
		HashSet<MapLocation> visited = new HashSet<>();
		
		double minOre = rc.getType() == RobotType.MINER ? MINOREMINER : MINOREBEAVER;
		MapLocation lowOre = null;
		
		while (i < j) {			// !isEmpty :)
			curr = open[i];		// Dequeue
			i++;
			visited.add(curr);
			
			double oreHere = rc.senseOre(curr);
			if (lowOre == null && oreHere > 0.0) {		// Check for low ore
				lowOre = curr;
			}
			if (oreHere > minOre) {						// Return first high if exists
				return new Pair<>(curr, minOre);
			}
			
			// Iterate neighbors
			for (Direction d : directions) {		// Succ
				if (j >= SENSERANGE) {				// Queue full, get out
					return new Pair<>(lowOre, 0.0);
				}
				
				MapLocation loc = curr.add(d);
				if (isNormal(loc) && !isOccupied(loc) && !visited.contains(loc)) {
					open[j] = loc;		// Enqueue
					j++;
				}
			}
		}
		return new Pair<>(lowOre, 0.0);
	}
}
