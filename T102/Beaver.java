package T102;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Beaver extends BaseBot {


	public Beaver(RobotController rc) {
		super(rc);
	}
	
	@Override
	public void execute() throws GameActionException {
		if (rc.getID() == rc.readBroadcast(RobotPlayer.CORNERBEAVER)) {
			cornerBeaver();
		}
		
		if (rc.readBroadcast(RobotPlayer.numMINERFACTORY) < 1) {
			tryBuild(RobotType.MINERFACTORY);
		} else if (rc.readBroadcast(RobotPlayer.numHELIPAD) < 1) {
			tryBuild(RobotType.HELIPAD);
		}
		
		rc.yield();
	}

	private void cornerBeaver() throws GameActionException {
		int xc = (myHQ.x + theirHQ.x) / 2;
		int yc = (myHQ.y + theirHQ.y) / 2;
		MapLocation endPoint = myHQ.add(myHQ.directionTo(theirHQ).opposite(), 3);
		for (int i = 0; i < 5; i++) {
			tryMoveTo(endPoint);
			rc.yield();
		}
		while (true) {
			rc.yield();
		}
	}

}
