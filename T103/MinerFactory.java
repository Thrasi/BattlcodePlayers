package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class MinerFactory extends BaseBot {
	
	private static int maxMINERS;

	public MinerFactory(RobotController rc) throws GameActionException {
		super(rc);
		
		maxMINERS = HQ.maxMINERSC[mapClass];
	}

	@Override
	public void execute() throws GameActionException {
		if (rc.readBroadcast(Channels.numMINERS) < maxMINERS) {
			trySpawn(RobotType.MINER);
		}
		rc.yield();
	}
}
