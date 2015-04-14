package player1;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Tank extends BaseBot {

	public Tank(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws GameActionException {
		tryShootWeakest();
	}

}
