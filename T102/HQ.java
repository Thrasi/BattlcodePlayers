package T102;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import T102.RobotPlayer;

public class HQ extends BaseBot {

	public HQ(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	
	public void execute() throws GameActionException {
		countRobots();
		transferToSupplier();
		rc.yield();
	}
	
	
	/**
	 * If the supplier is within transfer range we transfer supplies to it.
	 * @throws GameActionException
	 */
	public void transferToSupplier() throws GameActionException {
		RobotInfo[] allies = rc.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, 
				myTeam);
		
		int idToLook = rc.readBroadcast(RobotPlayer.SUPPLIERID);

		for (int i=0; i<allies.length; ++i) {
			RobotInfo k = allies[i];

			if (k.ID == idToLook) {
				rc.transferSupplies(100000, allies[i].location);
			}
		}
	}

	/**
	 * Counts the number of robots and broadcasts them for others to use.
	 * @throws GameActionException
	 */
	private void countRobots() throws GameActionException {
		RobotInfo[] myRobots = rc.senseNearbyRobots(999999, myTeam);
		int numSoldiers = 0;
		int numBashers = 0;
		int numBeavers = 0;
		int numBarracks = 0;
		int numMiners = 0;
		int numMinerFactory = 0;
		int numTankFactory = 0;
		int numTanks = 0;
		int numSupplyDepot = 0;
		for (RobotInfo r : myRobots) {
			RobotType type = r.type;
			if (type == RobotType.SOLDIER) {
				numSoldiers++;
			} else if (type == RobotType.BASHER) {
				numBashers++;
			} else if (type == RobotType.BEAVER) {
				numBeavers++;
			} else if (type == RobotType.BARRACKS) {
				numBarracks++;
			} else if (type == RobotType.MINER) {
				numMiners++;
			} else if (type == RobotType.MINER) {
				numMiners++;
			} else if (type == RobotType.MINERFACTORY) {
				numMinerFactory++;
			} else if (type == RobotType.TANKFACTORY) {
				numTankFactory++;
			} else if (type == RobotType.TANK) {
				numTanks++;
			} else if (type == RobotType.SUPPLYDEPOT) {
				numSupplyDepot++;
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

	}

}
