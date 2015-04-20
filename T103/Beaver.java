package T103;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Beaver extends BaseBot {
	
	
	private static int maxSUPPLYDEPOTS;
	private static int maxTECH;
	private static int maxBARRACKS;
	private static int maxTANKFACTORY;
	private static int maxHELIPAD;
	private static int maxAERO;
	private static int maxMINFACT;
	
	private static boolean movedOnce = false;
	
	public Beaver(RobotController rc) throws GameActionException {
		super(rc);
		
		maxSUPPLYDEPOTS = HQ.maxSUPPLYDEPOTSC[mapClass];
		maxTECH = HQ.maxTECHC[mapClass];
		maxBARRACKS = HQ.maxBARRACKSC[mapClass];
		maxTANKFACTORY = HQ.maxTANKFACTORIESC[mapClass];
		maxHELIPAD = HQ.maxHELIPADC[mapClass];
		maxAERO = HQ.maxAEROC[mapClass];
		maxMINFACT = HQ.maxMINFACTORYC[mapClass];
	}
	
	
	
	@Override
	public void execute() throws GameActionException {
		if (rc.getID() == rc.readBroadcast(Channels.CORNERBEAVER)) {
			//cornerBeaver();
		}
		
		
		if (!movedOnce) {
			movedOnce = tryMove(rc.getLocation().directionTo(myHQ).opposite());
		}
		// TODO is this a good strategy?
					if (BuildingStrategies.emptyScore(rc.getLocation()) < 6) {
						tryMoveTo(BuildingStrategies.openSpotForBuilding());
						//rc.yield();
					}
		
		
		boolean hasBuilt = false;
		/*
		if (rc.readBroadcast(Channels.numMINERFACTORY) < maxMINFACT) {
			hasBuilt = BuildingStrategies.tryBuildEmpty(RobotType.MINERFACTORY);
		} else if (rc.readBroadcast(Channels.numHELIPAD) < maxHELIPAD) {
			hasBuilt = tryBuild(RobotType.HELIPAD);
		} else if (rc.readBroadcast(Channels.numTECHNOLOGYINSTITUTE) < maxTECH) {
			hasBuilt = tryBuild(RobotType.TECHNOLOGYINSTITUTE);
		} else if (rc.readBroadcast(Channels.numAEROSPACELAB) < maxAERO) {
			hasBuilt = tryBuild(RobotType.AEROSPACELAB);
		} else if (rc.readBroadcast(Channels.numBARRACKS) < maxBARRACKS) {
			hasBuilt = BuildingStrategies.tryBuildTowards(theirHQ, RobotType.BARRACKS);
		} else if (rc.readBroadcast(Channels.numTANKFACTORY) < maxTANKFACTORY) {
			hasBuilt = BuildingStrategies.tryBuildTowards(theirHQ, RobotType.TANKFACTORY);
		}
		*/
		int queueStart = rc.readBroadcast(Channels.BUILDQSTART);
		int queueEnd = rc.readBroadcast(Channels.BUILDQEND);
		if (queueStart < queueEnd) {
			int typeID = rc.readBroadcast(queueStart);
			hasBuilt = BuildingStrategies.tryBuildEmpty(HQ.TYPES[typeID]);//tryBuild(HQ.TYPES[typeID]);
			if (hasBuilt) {
				queueStart++;
			}
			rc.broadcast(Channels.BUILDQSTART, queueStart);
		}
		
		// If you don't build anything we want the beaver to move.
		if ( !hasBuilt ) {
//			boolean didMine = tryMine();
//			if ( !didMine ) {
//				rc.yield();
//				if (Clock.getRoundNum() % 5 == 0) {
//					tryMove( getRandomDirection() );
//				}
//			}
			
			
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
		while (rc.readBroadcast(Channels.numHELIPAD) < maxHELIPAD) {
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
