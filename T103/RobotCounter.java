package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import static T103.BaseBot.rc;
import static T103.BaseBot.getAllAllies;;

public class RobotCounter {

	/**
	 * Counts the number of robots and broadcasts them for others to use.
	 * @throws GameActionException
	 */
	public static void countRobots() throws GameActionException {
		RobotInfo[] myRobots = getAllAllies();
		
		int numSoldiers = 0;
		int numBashers = 0;
		int numBeavers = 0;
		int numBarracks = 0;
		int numMiners = 0;
		int numMinerFactory = 0;
		int numTankFactory = 0;
		int numTanks = 0;
		int numSupplyDepot = 0;
		int numHelipad = 0;
		int numDrone = 0;
		int numComputer = 0;
		int numTech = 0;
		
		for (RobotInfo r : myRobots) {
			RobotType type = r.type;
			if (type == RobotType.SOLDIER) {
				numSoldiers++;
			} else if (type == RobotType.DRONE) {
				numDrone++;
			} else if (type == RobotType.MINER) {
				numMiners++;
			} else if (type == RobotType.BASHER) {
				numBashers++;
			} else if (type == RobotType.BEAVER) {
				numBeavers++;
			} else if (type == RobotType.BARRACKS) {
				numBarracks++;
			} else if (type == RobotType.MINERFACTORY) {
				numMinerFactory++;
			} else if (type == RobotType.TANKFACTORY) {
				numTankFactory++;
			} else if (type == RobotType.TANK) {
				numTanks++;
			} else if (type == RobotType.SUPPLYDEPOT) {
				numSupplyDepot++;
			} else if (type == RobotType.HELIPAD) {
				numHelipad++;
			} else if (type == RobotType.COMPUTER) {
				numComputer++;
			} else if (type == RobotType.TECHNOLOGYINSTITUTE) {
				numTech++;
			}
		}
		
		rc.broadcast(Channels.numBEAVERS, numBeavers);
		rc.broadcast(Channels.numSOLDIERS, numSoldiers);
		rc.broadcast(Channels.numBASHERS, numBashers);
		rc.broadcast(Channels.numBARRACKS, numBarracks);
		rc.broadcast(Channels.numMINERS, numMiners);
		rc.broadcast(Channels.numMINERFACTORY, numMinerFactory);
		rc.broadcast(Channels.numTANKFACTORY, numTankFactory);
		rc.broadcast(Channels.numTANKS, numTanks);
		rc.broadcast(Channels.numSUPPLYDEPOT, numSupplyDepot);
		rc.broadcast(Channels.numHELIPAD, numHelipad);
		rc.broadcast(Channels.numDRONE, numDrone);
		rc.broadcast(Channels.numCOMPUTER, numComputer);
		rc.broadcast(Channels.numTECHNOLOGYINSTITUTE, numTech);
	}
	
	
//	public static Map<RobotType, Integer> countChannels = new HashMap<>();
//	static {
//		int i = 2;
//		for (RobotType type : RobotType.values()) {
//			countChannels.put(type, i);
//			i++;
//		}
//	}
	
	/**
	 * !!!! DONT USE THIS. ITS NOT ALIGNED PROPERLY WITH CHANNELS !!!!
	 * Another function to count robots.
	 * @throws GameActionException
	 */
	public static void countRobots2() throws GameActionException {
		RobotInfo[] myRobots = getAllAllies();
		int n = RobotType.values().length;
		int[] count = new int[n];
		
		for (RobotInfo ri : myRobots) {
			count[ri.type.ordinal()]++;
		}
		for (int i = 0; i < n; i++) {
			rc.broadcast(i, count[i]);
		}
	}
}
