package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Helipad extends BaseBot {

	private static int EXPLCOUNT;
	
	private static int maxDRONE;
	
	public Helipad(RobotController rc) {
		super(rc);
		EXPLCOUNT = HQ.maxEXPLC[mapClass];
		maxDRONE = HQ.maxDRONES[mapClass];
	}

	@Override
	public void execute() throws GameActionException {
		if (rc.readBroadcast(Channels.numDRONE) < EXPLCOUNT + maxDRONE) {
			trySpawn(RobotType.DRONE);
		}
		rc.yield();
	}
}
