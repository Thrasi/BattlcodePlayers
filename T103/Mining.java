package T103;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;

import static T103.BaseBot.rc;
import static T103.BaseBot.isOccupied;

public class Mining {

	public static final double MINORE = 8.0;
	private static final int SENSERANGE = 50;

	protected static MapLocation closestOre() throws GameActionException {
		//System.out.println("before " + Clock.getBytecodeNum());
		MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(
				rc.getLocation(), SENSERANGE);

		MapLocation oreLoc = null;
		int dist = Integer.MAX_VALUE;
		for (MapLocation loc : locations) {
			if (rc.senseTerrainTile(loc) == TerrainTile.NORMAL && rc.senseOre(loc) > 0
					&& rc.getLocation().distanceSquaredTo(loc) < dist && !isOccupied(loc)) {
				oreLoc = loc;
				dist = rc.getLocation().distanceSquaredTo(loc);
			}
		}
		//System.out.println("after " + Clock.getBytecodeNum());
		return oreLoc;
	}
}
