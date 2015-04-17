package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class TechnologyInstitute extends BaseBot {

	public TechnologyInstitute(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws GameActionException {
		if (rc.readBroadcast(RobotPlayer.numCOMPUTER) < 1) {
			trySpawn(RobotType.COMPUTER);
		}
		rc.yield();
	}
}
