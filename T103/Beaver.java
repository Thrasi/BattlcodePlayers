package T103;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Beaver extends BaseBot {
	
	
	private static int maxSUPPLYDEPOTS;
	private static int maxTECH;

	public Beaver(RobotController rc) throws GameActionException {
		super(rc);
		
		maxSUPPLYDEPOTS = HQ.maxSUPPLYDEPOTSC[mapClass];
		maxTECH = HQ.maxTECHC[mapClass];
	}
	
	
	
	@Override
	public void execute() throws GameActionException {
		if (rc.getID() == rc.readBroadcast(Channels.CORNERBEAVER)) {
			cornerBeaver();
		}
		/*while (true) {
			//tryPrimitiveMoveTo(theirHQ);
			System.out.println("start " + Clock.getBytecodeNum() + " " + Clock.getRoundNum());
			Pair<Direction[], Integer> d = Movement.bugPlanning(theirHQ, true);
			System.out.println("end " + Clock.getBytecodeNum() + " " + Clock.getRoundNum());
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < d.length; i++) {
//				sb.append(d[i]).append(" ; ");
//			}
//			rc.setIndicatorString(0, d.length + "");
//			rc.setIndicatorString(1, sb.toString());
			for (int i = 0; i < d.y;) {
				if (tryMove(d.x[i])) {
					i++;
				}
				rc.yield();
			}
			//System.out.println("end " + Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		}*/
		
		
		boolean hasBuilt = false;
		
		if (rc.readBroadcast(Channels.numMINERFACTORY) < 1) {
			hasBuilt = tryBuild(RobotType.MINERFACTORY);
		}
		else if (rc.readBroadcast(Channels.numHELIPAD) < 1) {
			//hasBuilt = tryBuild(RobotType.HELIPAD);
		} else if (rc.readBroadcast(Channels.numTECHNOLOGYINSTITUTE) < maxTECH) {
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
		MapLocation spot = BuildingStrategies.safeSpotForBuilding();
		while (spot == null) {
			tryMove(getRandomDirection());
			rc.yield();
			spot = BuildingStrategies.safeSpotForBuilding();
		}
		while (!rc.getLocation().equals(spot)) {
			tryMoveTo(spot);
			rc.yield();
		}
		while (rc.readBroadcast(Channels.numHELIPAD) < 1) {
			tryBuild(RobotType.HELIPAD);
			rc.yield();
		}
		
		if (rc.readBroadcast(Channels.numCOMPUTER) < 1) {
			//tryBuild(RobotType.TECHNOLOGYINSTITUTE);
		}
		while (rc.readBroadcast(Channels.numSUPPLYDEPOT) < maxSUPPLYDEPOTS) {
			if (rc.readBroadcast(Channels.numMINERFACTORY) < 1) {
				continue;
			}
			//Direction dirBuilt = tryBuildDir(RobotType.SUPPLYDEPOT);
			//TODO see if this works well
			Direction dirBuilt = BuildingStrategies.tryBuildSafe(RobotType.SUPPLYDEPOT);
			if (dirBuilt != null) {
				MapLocation newBuild = rc.getLocation().add(dirBuilt);
				while (rc.isBuildingSomething()) {
					rc.yield();
				}
				spot = BuildingStrategies.safeSpotForBuilding();
				
				while (spot == null || spot.equals(newBuild)) {
					tryMove(getRandomDirection());
					spot = BuildingStrategies.safeSpotForBuilding();
					rc.yield();
				}
				while (!rc.getLocation().equals(spot)) {
					rc.setIndicatorString(1, "Occupied " + spot + " " + isOccupied(spot));
					rc.setIndicatorString(0, tryPrimitiveMoveTo(spot) + " " + spot);
					rc.yield();
				}
			}
		}
		while (true) {
			rc.yield();
		}
	}

}
