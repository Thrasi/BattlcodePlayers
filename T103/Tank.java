package T103;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Tank extends BaseBot {
	
	// Swarm index
	private static int swarmIdx = -1;
	

	public Tank(RobotController rc) throws GameActionException {
		super(rc);
		swarmIdx = rc.readBroadcast(Channels.SWARMIDXTANK);
	}

	@Override
	public void execute() throws GameActionException {

		tryShootMissilesOrWeakest();
		if ( getEnemiesInAttackingRange().length > 0 ) {
			return;
		}
		tryMoveToEnemy();

		if (Channels.isSet(Channels.SWARMSET + swarmIdx)) {
			tryMoveTo(new MapLocation(
					rc.readBroadcast(Channels.SWARMFIRSTX + swarmIdx),
					rc.readBroadcast(Channels.SWARMFIRSTY + swarmIdx)
			));
		}
		
		isSupplyLow = addToQueue(isSupplyLow);
		
		rc.yield();
	}

}
