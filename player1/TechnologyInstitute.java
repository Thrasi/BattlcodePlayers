package player1;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class TechnologyInstitute extends BaseBot {
	

	public TechnologyInstitute(RobotController rc) {
		super(rc);
	}

	@Override
	public void execute() throws GameActionException {
		
		rc.yield();
	}

}
