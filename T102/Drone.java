package T102;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Drone extends BaseBot {
	
	// Explore until target location is at this distance
	private static int EXPLORE_DIST = 15;

	// Exploring tag, -1 means it is not used for exploring
	// since only 4 drones are used for exploring, numbers 1, 2, 3, 4
	// are tags for drones and each tag means that the drone will explore
	// some part of the map
	private int explore = -1;
	
	private int scout = 0;
	
	private boolean visitedHQ = false;
	
	
	/**
	 * Decide if the drone is supposed to explore or do something else
	 * @param rc robot controller
	 * @throws GameActionException
	 */
	public Drone(RobotController rc) throws GameActionException {
		super(rc);
		
		// Decide which of the exploring drones this is
		if (rc.readBroadcast(RobotPlayer.expDRONE1) == 0) {
			explore = 1;
			rc.broadcast(RobotPlayer.expDRONE1, rc.getID());
		} else if (rc.readBroadcast(RobotPlayer.expDRONE2) == 0) {
			explore = 2;
			rc.broadcast(RobotPlayer.expDRONE2, rc.getID());
		} else if (rc.readBroadcast(RobotPlayer.expDRONE3) == 0) {
			explore = 3;
			rc.broadcast(RobotPlayer.expDRONE3, rc.getID());
		} else if (rc.readBroadcast(RobotPlayer.expDRONE4) == 0) {
			explore = 4;
			rc.broadcast(RobotPlayer.expDRONE4, rc.getID());
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
				rc.yield();
				visitedHQ = true;
			}
			
			// Before exploring the whole map, these functions are called
			// and they are used to decide some parameters such as map size
			// and upper left corner
			if (isVerticalSym()) {
				exploreVertical();
			} else if (isHorizontalSym()) {
				exploreHorizontal();
			} else {
				exploreRotational();
			}
			
			// Wait until the points are ready, it wont take more than 2-3 turns
			while (rc.readBroadcast(RobotPlayer.expSTARTED) == 0) {
				// TODO this time can be spend to do something else
				rc.yield();
			}
			
			// After some parameters of the map are known, drones are set to
			// explore their parts of the map
			if (explore == 1) {
				exploreAll(
						rc.readBroadcast(RobotPlayer.expOFFSET1),
						rc.readBroadcast(RobotPlayer.expOFFSET2)
				);
				rc.broadcast(RobotPlayer.expDRONE1DONE, 1);
			} else if (explore == 2) {
				exploreAll(
						rc.readBroadcast(RobotPlayer.expOFFSET2),
						rc.readBroadcast(RobotPlayer.expOFFSET3)
				);
				rc.broadcast(RobotPlayer.expDRONE2DONE, 1);
			} else if (explore == 3) {
				exploreAll(
						rc.readBroadcast(RobotPlayer.expOFFSET3),
						rc.readBroadcast(RobotPlayer.expOFFSET4)
				);
				rc.broadcast(RobotPlayer.expDRONE3DONE, 1);
			} else if (explore == 4) {
				exploreAll(
						rc.readBroadcast(RobotPlayer.expOFFSET4),
						RobotPlayer.expLOCFIRST + rc.readBroadcast(RobotPlayer.expLOCCOUNT) * 2
				);
				rc.broadcast(RobotPlayer.expDRONE4DONE, 1);
			}
			
			// Disable exploring role, this drone will continue to do whatever its
			// role is to do afterwards
			explore = -1;
		}
		
		rc.yield();
	}

	/**
	 * Explores map with rotational symmetry.
	 * @throws GameActionException
	 */
	private void exploreRotational() throws GameActionException {
		if (explore == 1 && rc.readBroadcast(RobotPlayer.MAPSET) == 0) {
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
			rc.broadcast(RobotPlayer.MAPWIDTH, w);
			rc.broadcast(RobotPlayer.MAPHEIGHT, h);
			rc.broadcast(RobotPlayer.TOPLEFTX, tlx);
			rc.broadcast(RobotPlayer.TOPLEFTY, tly);
			rc.broadcast(RobotPlayer.MAPSET, 1);
		}
	}
	
	private void exploreVertical() throws GameActionException {
		boolean mapSet = rc.readBroadcast(RobotPlayer.MAPSET) == 0;
		Direction dir = myHQ.directionTo(theirHQ).opposite();
		if (explore == 1 && mapSet) {
			System.out.println("drone 1");
			MapLocation corner = exploreCorner(dir.rotateLeft());
			System.out.println(corner);
		} else if (explore == 2 && mapSet) {
			System.out.println("drone 2");
			MapLocation corner = exploreCorner(dir.rotateRight());
			System.out.println(corner);
		}
		
	}

	
	/**
	 * Drone explores way to given location.
	 * @param loc location to explore
	 * @throws GameActionException
	 */
	private void explore(MapLocation loc) throws GameActionException {
		while (true) {
			if (rc.getLocation().distanceSquaredTo(loc) < EXPLORE_DIST) {
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
	 * Explores one corner of the map and calculates the size of the map. Drone tries to
	 * move in given direction if possible. In each step it tries to detect corner of
	 * the map. Once the corner is found it returns it. If it cannot find corner,
	 * it will move infinitely towards it.
	 * @param dir direction in which to move to find corner
	 * @return corner it finds when going in given direction
	 * @throws GameActionException
	 */
	private MapLocation exploreCorner(Direction dir) throws GameActionException {
		MapLocation loc = myHQ.add(dir, 400);
		while (true) {
			for (MapLocation l : getSurroundingLocations()) {
				if (isCorner(l)) {
					rc.yield();
					return l;
				}
			}
			if (!rc.canMove(rc.getLocation().directionTo(loc))) {
				//rc.setIndicatorString(0, bugPlanning(loc).toString());
				
			}
			if (!tryMoveTo(loc)) {
				rc.setIndicatorString(2, "nisam uspio");
				Direction[] ds = bugPlanning(loc);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < ds.length; i++) {
					sb.append(" " + ds[i]);
				}
				rc.setIndicatorString(0, ds.length + "");
				rc.setIndicatorString(1, sb.toString());
			}
			rc.yield();
		}
	}

	private void exploreHorizontal() {
		// TODO Auto-generated method stub
		
	}

}
