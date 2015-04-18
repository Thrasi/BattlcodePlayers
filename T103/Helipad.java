package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Helipad extends BaseBot {

	private static int EXPLCOUNT;
	
	public Helipad(RobotController rc) {
		super(rc);
		EXPLCOUNT = HQ.maxEXPLC[mapClass];
	}

	@Override
	public void execute() throws GameActionException {
		if (rc.readBroadcast(Channels.numDRONE) < EXPLCOUNT + 1) { 	// +1 for supplier
			trySpawn(RobotType.DRONE);
		}
		rc.yield();
	}
}
