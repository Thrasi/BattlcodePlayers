package T102;

import battlecode.common.Direction;
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
		boolean hasBuilt = false;
		
		//if (rc.readBroadcast(RobotType.MINERFACTORY.ordinal()) < 1) {
		if (rc.readBroadcast(RobotPlayer.numMINERFACTORY) < 1) {
			hasBuilt = tryBuild(RobotType.MINERFACTORY);
		}
		else if (rc.readBroadcast(RobotPlayer.numHELIPAD) < 1) {
			hasBuilt = tryBuild(RobotType.HELIPAD);
		}
		else if (rc.readBroadcast(RobotPlayer.numBARRACKS) < 2) {
			hasBuilt = tryBuild(RobotType.BARRACKS);
		}
		else if (rc.readBroadcast(RobotPlayer.numTANKFACTORY) < 2) {
			hasBuilt = tryBuild(RobotType.TANKFACTORY);
		}
		
		// If you don't build anything we want the beaver to move.
		if ( !hasBuilt ) {
			boolean didMine = tryMine();
			if ( !didMine ) {
				tryMove( getRandomDirection() );
			}
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
		if (rc.readBroadcast(RobotPlayer.numCOMPUTER) < 1) {
			//tryBuild(RobotType.TECHNOLOGYINSTITUTE);
		}
		while (true) {
			rc.yield();
		}
	}

}
