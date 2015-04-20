package T103;

import static T103.Channels.isSet;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Tank extends BaseBot {
	
	// Swarm index
	private static int swarmIdx = -1;
	

	public Tank(RobotController rc) throws GameActionException {
		super(rc);
		swarmIdx = rc.readBroadcast(Channels.SWARMIDXTANK);
		rc.broadcast(Channels.SWARMCOUNTTANK+swarmIdx, rc.readBroadcast(Channels.SWARMCOUNTTANK+swarmIdx)+1);
	}

	@Override
	public void execute() throws GameActionException {
		

		tryShootMissilesOrWeakest();
		if ( getEnemiesInAttackingRange().length > 0 ) {
			rc.yield();
			return;
		}
		
//		int primaryID = rc.readBroadcast(Channels.SWARMPRIMARY + swarmIdx);
//		if (primaryID != -1) {
//			try {
//				tryMoveTo(rc.senseRobot(primaryID).location);
//			} catch (GameActionException e) {
//				
//			}
//		}

		if (isSet(Channels.SWARMSETFLOOD + swarmIdx)) {
			tryMoveFlood(rc.readBroadcast(Channels.SWARMFLOODIDX + swarmIdx));
		} else
			if (isSet(Channels.SWARMSET + swarmIdx)) {
			tryMoveTo(new MapLocation(
					rc.readBroadcast(Channels.SWARMFIRSTX + swarmIdx),
					rc.readBroadcast(Channels.SWARMFIRSTY + swarmIdx)
			));
		}
		
		isSupplyLow = addToQueue(isSupplyLow);
	}
	
	@Override
	public void endOfTurn() {
		rc.yield();
	}

}
