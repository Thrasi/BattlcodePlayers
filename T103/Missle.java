package T103;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Missle extends BaseBot {

	public Missle(RobotController rc) {
		super(rc);
		
	}

	@Override
	public void execute() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(24, theirTeam);
		if (enemies.length == 0) {
			rc.yield();
			return;
		}
		
		int count = 0, minDist = Integer.MAX_VALUE;
		MapLocation target = null;
		for (RobotInfo ri : enemies) {
			int dist = rc.getLocation().distanceSquaredTo(ri.location);
			if (dist < minDist) {
				minDist = dist;
				target = ri.location;
			}
			if (dist <= 2) {
				count++;
			}
		}
		if (count >= 2) {
			rc.explode();
		} else {
			tryMove(rc.getLocation().directionTo(target));
		}
		rc.yield();
	}
}
