package T103;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Launcher extends BaseBot {

	public Launcher(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws GameActionException {
		RobotInfo enemy = getNearestNearByEnemy();
		if (enemy != null) {
			Direction dir = rc.getLocation().directionTo(enemy.location);
			if (rc.canLaunch(dir)) {
				rc.launchMissile(dir);
			}
		}
		rc.yield();
	}
}
