package player1;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Tower extends BaseBot {


	public Tower(RobotController rc) {
		super(rc);
	}

	@Override
	public void execute() throws GameActionException {
		tryShootWeakest();
		rc.yield();
	}
}
