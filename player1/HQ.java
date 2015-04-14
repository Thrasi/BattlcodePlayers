package player1;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class HQ extends BaseBot{
	
	// Number of beavers to use
	private static final int BEAVERS = 3;
	private static int beaverCount = 0;
	
	public HQ(RobotController rc) {
		super(rc);
	}

	
	@Override
	public void execute() throws GameActionException {
		// Shoot weakest
		tryShootWeakest();
		
		// Spawn beaver if less than 3
		if (beaverCount < BEAVERS && trySpawn(randomDirection(), RobotType.BEAVER)) {
			beaverCount++;
		}
		
		// Transfering supplies
		for (RobotInfo ri : senseNearbyAllies(15)) {
			if (ri.type == RobotType.MINER && ri.supplyLevel < 300) {
				rc.transferSupplies(500, ri.location);
			} else if (ri.type == RobotType.SOLDIER && ri.supplyLevel < 500) {
				rc.transferSupplies(2000, ri.location);
			} else if (ri.supplyLevel < 100) {
				rc.transferSupplies(100, ri.location);
			}
		}
		
		rc.yield();
	}
	
}
