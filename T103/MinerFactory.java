package T102;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class MinerFactory extends BaseBot {

	public MinerFactory(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws GameActionException {
		//if (rc.readBroadcast(RobotType.MINER.ordinal()) < 15) {
		if (rc.readBroadcast(RobotPlayer.numMINERS) < 15) {
			trySpawn(RobotType.MINER);
		}
		rc.yield();
	}
}
