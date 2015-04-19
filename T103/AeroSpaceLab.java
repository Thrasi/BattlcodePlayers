package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class AeroSpaceLab extends BaseBot {

	public AeroSpaceLab(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void execute() throws GameActionException {
		if (rc.readBroadcast(Channels.numLAUNCHERS) < 3) {
			trySpawn(RobotType.LAUNCHER);
		}
		rc.yield();
	}

}
