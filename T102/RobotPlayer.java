package T102;

/* This is built on a template for a player we found from robotplayerNPHard. 
 * That template had errors, but is a fine framework.  T100 is currently a shitty
 * swarm bot as described in the swarmbot video lectures.
 * From here we ... profit.
 * 
 * 
 * */


import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	
	private static RobotController rc;
	
	// COMMUNICATION CHANNELS:
	public static int numBEAVERS = 2, 
			numMINERS = 3, 
			numSOLDIERS = 4, 
			numBASHERS = 5,
			numBARRACKS = 6, 
			numMINERFACTORY = 7, 
			numTANKFACTORY = 8, 
			numTANKS = 9,
			numSUPPLYDEPOT = 10, 
			numAEROSPACELAB = 11, 
			numCOMMANDER = 12, 
			numCOMPUTER = 13,
			numDRONE = 14, 
			numHELIPAD = 15, 
			numLAUNCHERS = 16, 
			numMISSILE = 17,
			numTECHNOLOGYINSTITUTE = 18, 
			numTOWER = 19, 
			numTRAININGFIED = 20,
	
		// SUPPLY QUEUE:
			SUPPLIERID = 296, 
			numSUPPLIERS = 297,
			SUPPLYQSTART = 298,
			SUPPLYQEND = 299,
			
		// EXPLORING ROBOTS
			CORNERBEAVER = 800,
			expDRONE1 = 801,
			expDRONE2 = 802,
			expDRONE3 = 803,
			expDRONE4 = 804,
			
		// MAP
			MAPWIDTH = 900,
			MAPHEIGHT = 901,
			TOPLEFTX = 902,
			TOPLEFTY = 903,
			MAPSET = 904
			;
	
	public static Map<RobotType, Integer> countChannels = new HashMap<>();
	static {
		int i = 2;
		for (RobotType type : RobotType.values()) {
			countChannels.put(type, i);
			i++;
		}
	}
	

	// MAXIMUM AMOUNT OF UNITS OF A SPECIFIC TYPE:
	static int MAXSOLDIERS = 20, MAXBASHERS = 20, MAXMINERS = 15, MAXBEAVERS = 3,
			MAXTANKS = 30;

	/**
	 * Main run function
	 * @param SkyNet killer robots
	 */
	public static void run(RobotController SkyNet) {
		BaseBot myself = null;
		rc = SkyNet;

		RobotType type = rc.getType();

		try {
			if (type == RobotType.HQ) {
				myself = new HQ(rc);
			} else if (type == RobotType.AEROSPACELAB) {
				myself = new AeroSpaceLab(rc);
			} else if (type == RobotType.BARRACKS) {
				myself = new Barracks(rc);
			} else if (type == RobotType.BASHER) {
				myself = new Basher(rc);
			} else if (type == RobotType.BEAVER) {
				myself = new Beaver(rc);
			} else if (type == RobotType.COMMANDER) {
				myself = new Commander(rc);
			} else if (type == RobotType.COMPUTER) {
				myself = new Computer(rc);
			} else if (type == RobotType.DRONE) {
				try {
					if (rc.getType() == RobotType.DRONE && needsSupplier(rc)) {
						myself = new Supplier(rc);
						rc.broadcast(numSUPPLIERS, rc.readBroadcast(numSUPPLIERS) + 1);
					    rc.broadcast(SUPPLIERID, rc.getID());
					}
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myself = new Drone(rc);
			} else if (type == RobotType.HELIPAD) {
				myself = new Helipad(rc);
			} else if (type == RobotType.LAUNCHER) {
				myself = new Launcher(rc);
			} else if (type == RobotType.MINER) {
				myself = new Miner(rc);
			} else if (type == RobotType.MINERFACTORY) {
				myself = new MinerFactory(rc);
			} else if (type == RobotType.MISSILE) {
				myself = new Missle(rc);
			} else if (type == RobotType.SOLDIER) {
				myself = new Soldier(rc);
			} else if (type == RobotType.TANK) {
				myself = new Tank(rc);
			} else if (type == RobotType.TANKFACTORY) {
				myself = new TankFactory(rc);
			} else if (type == RobotType.TECHNOLOGYINSTITUTE) {
				myself = new TechnologyInstitute(rc);
			} else if (type == RobotType.TOWER) {
				myself = new Tower(rc);
			} else if (type == RobotType.TRAININGFIELD) {
				myself = new TrainingField(rc);
			} else {
				myself = new BaseBot(rc);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				myself.go();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean needsSupplier(RobotController rc) throws GameActionException {
		if (rc.readBroadcast(numSUPPLIERS) == 0) {
			return true;
		}
		return false;
	}

	
}
