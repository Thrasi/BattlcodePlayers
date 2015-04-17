package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Helipad extends BaseBot {

	public Helipad(RobotController rc) {
		super(rc);
	}

	@Override
	public void execute() throws GameActionException {
		if (rc.readBroadcast(Channels.numDRONE) < HQ.EXPLCOUNT) {
			trySpawn(RobotType.DRONE);
		}
		rc.yield();
	}
}
