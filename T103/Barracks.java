package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Barracks extends BaseBot {

	
	public Barracks(RobotController rc) {
		super(rc);
	}
	
	@Override
	public void execute() throws GameActionException {
		if (rc.readBroadcast(Channels.numBASHERS) < 10) {
			trySpawn(RobotType.BASHER);
		} else {
			//trySpawn(RobotType.SOLDIER);
		}
		rc.yield();
	}

}
