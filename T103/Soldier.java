package T103;

import T103.Utility.Pair;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import static T103.Channels.isSet;

public class Soldier extends BaseBot {

	
	private static int swarmIdx = -1;

	public Soldier(RobotController rc) throws GameActionException {
		super(rc);
		swarmIdx = rc.readBroadcast(Channels.SWARMIDXSOLDIER);
		rc.broadcast(Channels.SWARMCOUNTSOLDIER+swarmIdx, rc.readBroadcast(Channels.SWARMCOUNTSOLDIER+swarmIdx)+1);
	}

	@Override
	public void execute() throws GameActionException {
		
		tryShootMissilesOrWeakest();
		if ( getEnemiesInAttackingRange().length > 0 ) {
			rc.yield();
			return;
		}
		tryMoveToEnemy();
		
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
				MapLocation loc = new MapLocation(
						rc.readBroadcast(Channels.SWARMFIRSTX + swarmIdx),
						rc.readBroadcast(Channels.SWARMFIRSTY + swarmIdx)
				); 
				tryMoveTo(loc);
//				Direction[] dir = Movement.bugPlanning(loc, true, 8);
//				if (dir.length < 0) {
//					tryMove(dir[0]);
//				} else {
//					tryMoveTo(loc);
//				}
		}
		
		isSupplyLow = addToQueue(isSupplyLow);
		
	}
	
	@Override
	public void endOfTurn() {
		rc.yield();
	}

}
