package T102;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Computer extends BaseBot {

	public Computer(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void execute() throws GameActionException {
		rc.yield();
	}

}
