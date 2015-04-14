package player1;

import static player1.Channels.*;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class MinerFactory extends BaseBot {

	private static final int MINERS = 15;
	
	public MinerFactory(RobotController rc) {
		super(rc);
	}

	@Override
	public void execute() throws GameActionException {
		int minerCount = rc.readBroadcast(MINER_CHANNEL);
		if (minerCount < MINERS && trySpawn(randomDirection(), RobotType.MINER)) {
			rc.broadcast(MINER_CHANNEL, minerCount+1);
		}
		rc.yield();
	}

}
