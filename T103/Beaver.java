package T103;

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
		if (rc.getID() == rc.readBroadcast(Channels.CORNERBEAVER)) {
			cornerBeaver();
		}
		boolean hasBuilt = false;
		
		if (rc.readBroadcast(Channels.numMINERFACTORY) < 1) {
			hasBuilt = tryBuild(RobotType.MINERFACTORY);
		}
		else if (rc.readBroadcast(Channels.numHELIPAD) < 1) {
			hasBuilt = tryBuild(RobotType.HELIPAD);
		} else if (rc.readBroadcast(Channels.numTECHNOLOGYINSTITUTE) < 1) {
			hasBuilt = tryBuild(RobotType.TECHNOLOGYINSTITUTE);
		}
		else if (rc.readBroadcast(Channels.numBARRACKS) < 2) {
			hasBuilt = tryBuild(RobotType.BARRACKS);
		}
		else if (rc.readBroadcast(Channels.numTANKFACTORY) < 2) {
			hasBuilt = tryBuild(RobotType.TANKFACTORY);
		}
		
		// If you don't build anything we want the beaver to move.
		if ( !hasBuilt ) {
			boolean didMine = tryMine();
			if ( !didMine ) {
				rc.yield();
				tryMove( getRandomDirection() );
			}
		}
		
		rc.yield();
	}

	private void cornerBeaver() throws GameActionException {
		/*int xc = (myHQ.x + theirHQ.x) / 2;
		int yc = (myHQ.y + theirHQ.y) / 2;
		MapLocation endPoint = myHQ.add(myHQ.directionTo(theirHQ).opposite(), 3);
		for (int i = 0; i < 5; i++) {
			tryMoveTo(endPoint);
			rc.yield();
		}*/
		MapLocation spot = findSpotForBuilding();
		while (spot == null) {
			tryMove(getRandomDirection());
			rc.yield();
			spot = findSpotForBuilding();
		}
		while (!rc.getLocation().equals(spot)) {
			tryMoveTo(spot);
			rc.yield();
		}
		
		
		if (rc.readBroadcast(Channels.numCOMPUTER) < 1) {
			//tryBuild(RobotType.TECHNOLOGYINSTITUTE);
		}
		while (rc.readBroadcast(Channels.numSUPPLYDEPOT) < 10) {
			boolean hasBuilt = tryBuild(RobotType.SUPPLYDEPOT);
			rc.yield();
			if (hasBuilt) {
				spot = findSpotForBuilding();
				while (spot == null) {
					tryMove(getRandomDirection());
					rc.yield();
					spot = findSpotForBuilding();
				}
				while (!rc.getLocation().equals(spot)) {
					tryMoveTo(spot);
					rc.yield();
				}
			}
		}
		while (true) {
			rc.yield();
		}
	}

}
