package T102;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Drone extends BaseBot {
	
	// Explore until target location is at this distance
	private static int EXPLORE_DIST = 15;

	private int explore = -1;
	
	/**
	 * Decide if the drone is supposed to explore or do something else
	 * @param rc robot controller
	 * @throws GameActionException
	 */
	public Drone(RobotController rc) throws GameActionException {
		super(rc);
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
		if (explore != -1) {
			if (isVerticalSym()) {
				exploreVertical();
			} else if (isHorizontalSym()) {
				exploreHorizontal();
			} else {
				exploreRotational();
			}
		}
		
		rc.yield();
	}

	/**
	 * Explores map with rotational symmetry.
	 * @throws GameActionException
	 */
	private void exploreRotational() throws GameActionException {
		if (explore == 1) {
			Direction dir = myHQ.directionTo(theirHQ).opposite();
			
			MapLocation corner = exploreCorner(dir);
			double xc = (myHQ.x + theirHQ.x) / 2.0;
			double yc = (myHQ.y + theirHQ.y) / 2.0;
			int w = (int) (Math.abs(xc - corner.x) * 2) + 1;
			int h = (int) (Math.abs(yc - corner.y) * 2) + 1;
			
			rc.broadcast(RobotPlayer.MAPWIDTH, w);
			rc.broadcast(RobotPlayer.MAPHEIGHT, h);
			rc.broadcast(RobotPlayer.TOPLEFTX, (int) (xc - w / 2.0 + 1));
			rc.broadcast(RobotPlayer.TOPLEFTY, (int) (yc - h / 2.0 + 1));
			rc.broadcast(RobotPlayer.MAPSET, 1);
		} else if (explore == 2) {
			int xc = (myHQ.x + theirHQ.x) / 2;
			int yc = (myHQ.y + theirHQ.y) / 2;
			MapLocation loc = new MapLocation(xc, yc);
			explore(loc);
		} else if (explore == 3) {
			exploreLeft();
		} else if (explore == 4) {
			exploreRight();
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
			tryMoveTo(loc);
			rc.yield();
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
		MapLocation loc = myHQ.add(dir, 200);
		while (true) {
			for (MapLocation l : getSurroundingLocations()) {
				if (isCorner(l)) {
					rc.yield();
					return l;
				}
			}
			tryMoveTo(loc);
			rc.yield();
		}
	}

	private void exploreHorizontal() {
		// TODO Auto-generated method stub
		
	}

	private void exploreVertical() {
		// TODO Auto-generated method stub
		
	}

	private void exploreRight() {
		// TODO Auto-generated method stub
		
	}

	private void exploreLeft() {
		// TODO Auto-generated method stub
		
	}

	
}
