package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class MinerFactory extends BaseBot {

	public MinerFactory(RobotController rc) {
		super(rc);
	}

	@Override
	public void execute() throws GameActionException {
		if (rc.readBroadcast(Channels.numMINERS) < 10) {
			trySpawn(RobotType.MINER);
		}
		rc.yield();
	}
}
