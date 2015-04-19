package T103;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import static T103.Channels.isSet;

public class Soldier extends BaseBot {

	
	private static int swarmIdx = -1;

	public Soldier(RobotController rc) throws GameActionException {
		super(rc);
		swarmIdx = rc.readBroadcast(Channels.SWARMIDXSOLDIER);
	}

	@Override
	public void execute() throws GameActionException {
		attackOnSight();
		//tryShootMissilesOrWeakest();
		
		
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
