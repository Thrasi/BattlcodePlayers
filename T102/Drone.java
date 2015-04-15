package T102;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class Drone extends BaseBot {

	private int explore = -1;
	
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
			if (explore == 1) {
				exploreCorner();
			} else if (explore == 2) {
				exploreCenter();
			} else if (explore == 3) {
				exploreLeft();
			} else if (explore == 4) {
				exploreRight();
			}
		}
		rc.yield();
	}

	private void exploreRight() {
		// TODO Auto-generated method stub
		
	}

	private void exploreLeft() {
		// TODO Auto-generated method stub
		
	}

	private void exploreCenter() throws GameActionException {
		int xc = (myHQ.x + theirHQ.x) / 2;
		int yc = (myHQ.y + theirHQ.y) / 2;
		MapLocation loc = new MapLocation(xc, yc);
		while (true) {
			tryMoveTo(loc);
			rc.yield();
		}
	}

	private void exploreCorner() throws GameActionException {
		Direction dir = myHQ.directionTo(theirHQ).opposite();
		MapLocation loc = myHQ.add(dir, 200);
		while (rc.getLocation().distanceSquaredTo(loc) > 10) {
			for (MapLocation l : getSurroundingLocations()) {
				
			}
			tryMoveTo(loc);
			rc.yield();
		}
		
	}
}
