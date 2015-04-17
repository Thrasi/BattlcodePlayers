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
		
		rc.broadcast(RobotPlayer.numBEAVERS, numBeavers);
		rc.broadcast(RobotPlayer.numSOLDIERS, numSoldiers);
		rc.broadcast(RobotPlayer.numBASHERS, numBashers);
		rc.broadcast(RobotPlayer.numBARRACKS, numBarracks);
		rc.broadcast(RobotPlayer.numMINERS, numMiners);
		rc.broadcast(RobotPlayer.numMINERFACTORY, numMinerFactory);
		rc.broadcast(RobotPlayer.numTANKFACTORY, numTankFactory);
		rc.broadcast(RobotPlayer.numTANKS, numTanks);
		rc.broadcast(RobotPlayer.numSUPPLYDEPOT, numSupplyDepot);
		rc.broadcast(RobotPlayer.numHELIPAD, numHelipad);
		rc.broadcast(RobotPlayer.numDRONE, numDrone);
		rc.broadcast(RobotPlayer.numCOMPUTER, numComputer);
		rc.broadcast(RobotPlayer.numTECHNOLOGYINSTITUTE, numTech);
	}
	
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
