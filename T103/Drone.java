package T103;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;
import static T103.MapInfo.isHorizontalSym;
import static T103.MapInfo.isRotationSym;
import static T103.MapInfo.isVerticalSym;
import static T103.Channels.isSet;
import static T103.DroneExploration.exploreCorner;

public class Drone extends BaseBot {
	
	// Explore until target location is at this distance
	private static int EXPLORE_DIST = 15;

	// Exploring tag, -1 means it is not used for exploring
	// since only 4 drones are used for exploring, numbers 1, 2, 3, 4
	// are tags for drones and each tag means that the drone will explore
	// some part of the map
	private int explore = -1;
	
	private int scout = 0;
	private int supply = -1;
	
	private boolean visitedHQ = false;
	
	private static Supplier mesupply = null;
	
	
	/**
	 * Decide if the drone is supposed to explore or do something else
	 * @param rc robot controller
	 * @throws GameActionException
	 */
	public Drone(RobotController rc) throws GameActionException {
		super(rc);
		
		if (!allDone()) {
			int count = HQ.maxEXPLC[mapClass];
			for (int i = 0; i < count; i++) {
				int prevId = rc.readBroadcast(Channels.expDRONE + i);
				if (prevId == 0 || !isAlive(prevId)) {
					rc.broadcast(Channels.expDRONE + i, rc.getID());
					explore = i+1;
					break;
				}
			}
		} else {
			mesupply = new Supplier(rc);
		}
	}

	@Override
	public void execute() throws GameActionException {
		if (explore != -1) {		// Drone is exploring one
			if (!visitedHQ) {
				while (rc.getLocation().distanceSquaredTo(myHQ) > GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
					tryMoveTo(myHQ);
					rc.yield();
				}
				rc.yield();		// TODO receive supplies ?
				visitedHQ = true;
			}
			
			// Before exploring the whole map, these functions are called
			// and they are used to decide some parameters such as map size
			// and upper left corner
			if (isVerticalSym()) {
				exploreVertical();
			} else if (isHorizontalSym()) {
				exploreHorizontal();
			} else if (isRotationSym()) {
				exploreRotational();
			} else {
				throw new RobotException("Don't know which symmetry");
			}
			
			// Wait until the points are ready, it wont take more than 2-3 turns
			while (rc.readBroadcast(Channels.expSTARTED) == 0) {
				// TODO this time can be spend to do something else
				rc.yield();
			}
			
			// After some parameters of the map are known, drones are set to
			// explore their parts of the map
			exploreAll(
					rc.readBroadcast(Channels.expOFFSET + explore - 1),
					rc.readBroadcast(Channels.expOFFSET + explore)
			);
			rc.broadcast(Channels.expDRONEDONE + explore - 1, 1);
			
			// Disable exploring role, this drone will continue to do whatever its
			// role is to do afterwards
			supply = explore;	// Become supplier
			explore = -1;
			
			mesupply = new Supplier(rc);
		}
		
		if (mesupply != null) {
			mesupply.execute();
		}
		
		//rc.yield();
	}

	
	
	private void exploreVertical() throws GameActionException {
		boolean mapSet = rc.readBroadcast(Channels.MAPSET) == 0;
		Direction dir = myHQ.directionTo(theirHQ).opposite();
		if (explore == 1 && mapSet) {
			MapLocation corner = exploreCorner(dir.rotateLeft());
			rc.broadcast(Channels.MAPCORNER1X, corner.x);
			rc.broadcast(Channels.MAPCORNER1Y, corner.y);
			Channels.set(Channels.MAPCORNER1SET);
		} else if (explore == 2 && mapSet) {
			MapLocation corner = exploreCorner(dir.rotateRight());
			rc.broadcast(Channels.MAPCORNER2X, corner.x);
			rc.broadcast(Channels.MAPCORNER2Y, corner.y);
			Channels.set(Channels.MAPCORNER2SET);
			
			while (!Channels.isSet(Channels.MAPCORNER1SET)) {
				rc.yield();
			}
			
			int cx = rc.readBroadcast(Channels.MAPCORNER1X);
			int cy = rc.readBroadcast(Channels.MAPCORNER1Y);
			
			double centerX = (myHQ.x + theirHQ.x) / 2.0;
			int h = Math.abs(cy - corner.y) + 1;
			int w = (int) (Math.abs(centerX - cx) * 2 + 1);
			int tlx = (int) (centerX - w / 2.0 + 1);
			tlx = tlx < 0 ? tlx - 1 : tlx;
			int tly = Math.min(cy, corner.y);
			//System.out.println(w + " " + h + " " + tlx + " " + tly);
			rc.broadcast(Channels.MAPWIDTH, w);
			rc.broadcast(Channels.MAPHEIGHT, h);
			rc.broadcast(Channels.TOPLEFTX, tlx);
			rc.broadcast(Channels.TOPLEFTY, tly);
			rc.broadcast(Channels.MAPSET, 1);
		}
	}
	
	/**
	 * TODO not tested
	 * @throws GameActionException
	 */
	private void exploreHorizontal() throws GameActionException {
		boolean mapSet = rc.readBroadcast(Channels.MAPSET) == 0;
		Direction dir = myHQ.directionTo(theirHQ).opposite();
		if (explore == 1 && mapSet) {
			MapLocation corner = exploreCorner(dir.rotateLeft());
			rc.broadcast(Channels.MAPCORNER1X, corner.x);
			rc.broadcast(Channels.MAPCORNER1Y, corner.y);
			Channels.set(Channels.MAPCORNER1SET);
		} else if (explore == 2 && mapSet) {
			MapLocation corner = exploreCorner(dir.rotateRight());
			rc.broadcast(Channels.MAPCORNER2X, corner.x);
			rc.broadcast(Channels.MAPCORNER2Y, corner.y);
			Channels.set(Channels.MAPCORNER2SET);
			
			while (!Channels.isSet(Channels.MAPCORNER1SET)) {
				rc.yield();
			}
			
			int cx = rc.readBroadcast(Channels.MAPCORNER1X);
			int cy = rc.readBroadcast(Channels.MAPCORNER1Y);
			
			double centerY = (myHQ.y + theirHQ.y) / 2.0;
			int w = Math.abs(cx - corner.x) + 1;
			int h = (int) (Math.abs(centerY - cy) * 2 + 1);
			int tly = (int) (centerY - h / 2.0 + 1);
			tly = tly < 0 ? tly - 1 : tly;
			int tlx = Math.min(cx, corner.x);
			//System.out.println(w + " " + h + " " + tlx + " " + tly);
			rc.broadcast(Channels.MAPWIDTH, w);
			rc.broadcast(Channels.MAPHEIGHT, h);
			rc.broadcast(Channels.TOPLEFTX, tlx);
			rc.broadcast(Channels.TOPLEFTY, tly);
			rc.broadcast(Channels.MAPSET, 1);
		}
	}

	
	/**
	 * Drone explores way to given location.
	 * @param loc location to explore
	 * @throws GameActionException
	 */
	private void explore(MapLocation loc) throws GameActionException {
		while (true) {
			if (rc.getLocation().distanceSquaredTo(loc) < EXPLORE_DIST
					|| rc.senseTerrainTile(loc) != TerrainTile.UNKNOWN) {
				return;
			}
			trySupplyTower();
			tryMoveTo(loc);
			rc.yield();
		}
	}
	
	/**
	 * Explores all points that were previously broadcasted at given channels.
	 * @param offset offset of the channel to start exploring
	 * @param limit upper bound on the channel
	 */
	private void exploreAll(int offset, int limit) throws GameActionException {
		for (int i = offset; i < limit; i += 2) {
			explore(new MapLocation(rc.readBroadcast(i), rc.readBroadcast(i+1)));
		}
	}

	/**
	 * Explores map with rotational symmetry.
	 * @throws GameActionException
	 */
	public void exploreRotational() throws GameActionException {
		if (Channels.isSet(Channels.MAPSET)) {
			return;
		}
		Direction dir = myHQ.directionTo(theirHQ).opposite();
		
		// Calculating parameters (size and top left corner)
		MapLocation corner = exploreCorner(dir);
		double xc = (myHQ.x + theirHQ.x) / 2.0;
		double yc = (myHQ.y + theirHQ.y) / 2.0;
		int w = (int) (Math.abs(xc - corner.x) * 2) + 1;
		int h = (int) (Math.abs(yc - corner.y) * 2) + 1;
		int tlx = (int) (xc - w / 2.0 + 1);
		tlx = tlx < 0 ? tlx - 1 : tlx;
		int tly = (int) (yc - h / 2.0 + 1);
		tly = tly < 0 ? tly - 1 : tly;
		
		// Broadcast map parameters
		rc.broadcast(Channels.MAPWIDTH, w);
		rc.broadcast(Channels.MAPHEIGHT, h);
		rc.broadcast(Channels.TOPLEFTX, tlx);
		rc.broadcast(Channels.TOPLEFTY, tly);
		rc.broadcast(Channels.MAPSET, 1);
	}
	
	/** 
	 * Checks if all exploration drones have finished.
	 * @return true if they have, false otherwise
	 * @throws GameActionException
	 */
	private static boolean allDone() throws GameActionException {
		int count = rc.readBroadcast(Channels.expDRONECOUNT);
		for (int i = count-1; i >= 0; i--) {
			if (!isSet(Channels.expDRONEDONE + i)) {
				return false;
			}
		}
		return true;
	}

}
