package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Basher extends BaseBot {

	public Basher(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	public void execute() throws GameActionException {
		RobotInfo nearestEnemy = getNearestNearByEnemy();
		if ( nearestEnemy != null ) {
			if (rc.getLocation().distanceSquaredTo(nearestEnemy.location) >= 2) {
				tryMoveTo(nearestEnemy.location);
			}
		}
		
		isSupplyLow = addToQueue(isSupplyLow);
		
		rc.yield();
	}

}
