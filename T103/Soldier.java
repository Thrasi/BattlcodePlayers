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
		tryShootWeakest();
		
		if (isSet(Channels.SWARMSET + swarmIdx)) {
			tryMoveTo(new MapLocation(
					rc.readBroadcast(Channels.SWARMFIRSTX + swarmIdx),
					rc.readBroadcast(Channels.SWARMFIRSTY + swarmIdx)
			));
		}
		rc.yield();
	}

}
