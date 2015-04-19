package T103;

import static T103.Channels.isSet;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Basher extends BaseBot {
	
	private static int swarmIdx = -1;

	public Basher(RobotController rc) throws GameActionException {
		super(rc);
		swarmIdx = rc.readBroadcast(Channels.SWARMIDXBASHER);
	}
	
	public void execute() throws GameActionException {
		RobotInfo nearestEnemy = getNearestNearByEnemy();
		if ( nearestEnemy != null ) {
			if (rc.getLocation().distanceSquaredTo(nearestEnemy.location) >= 2) {
				tryMoveTo(nearestEnemy.location);
			}
		}
		
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
		
		rc.yield();
	}

}
