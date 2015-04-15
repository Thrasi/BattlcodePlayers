package T102;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Tower extends BaseBot {

	public Tower(RobotController rc) {
		super(rc);
	}
	
	@Override
	public void execute() throws GameActionException {
		tryShootWeakest();
	}

}
