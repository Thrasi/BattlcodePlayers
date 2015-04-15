package T102;

import java.util.HashMap;
import java.util.Map;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import T102.RobotPlayer;

public class HQ extends BaseBot {
	
	private static class Tuple {
		int x, y;
		public Tuple(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private static final Map<RobotType, Tuple> hqSupplies;
	static {
		hqSupplies = new HashMap<>();
		hqSupplies.put(RobotType.BEAVER, new Tuple(100, 1000));
		hqSupplies.put(RobotType.MINER, new Tuple(200, 2000));
	}

	public HQ(RobotController rc) {
		super(rc);
		
		// Initial tower locations (before destruction)
		MapLocation[] myTowers = rc.senseTowerLocations();
		MapLocation[] theirTowers = rc.senseEnemyTowerLocations();
		
		// Middle points of HQs and towers
		int towerCount = myTowers.length; 
		int[] xs = new int[towerCount+1];
		int[] ys = new int[towerCount+1];
		xs[0] = (myHQ.x + theirHQ.x) / 2;
		ys[0] = (myHQ.y + theirHQ.y) / 2;
		
		// Set middle points for towers
		for (int i = 0; i < myTowers.length; i++) {
			xs[i+1] = (myTowers[i].x + theirTowers[i].x) / 2;
			ys[i+1] = (myTowers[i].y + theirTowers[i].y) / 2;
		}
		
		if (towerCount == 0) {						// Unknown symmetry
		}
		if (allEqual(xs) && allEqual(ys)) {			// Rotation symmetry
			
		} else if (onLine(xs, ys)) {				// Reflection symmetry
			System.out.println("reflection");
		} else {									// Unknown symmetry
			System.out.println("something else");
		}
	}


	@Override
	public void beginingOfTurn() throws GameActionException {
		RobotInfo[] allies = 
				getAlliesInRange(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED);
		for (RobotInfo ri : allies) {
			if (hqSupplies.containsKey(ri.type)) {
				Tuple sup = hqSupplies.get(ri.type);
				if (ri.supplyLevel < sup.x) {
					rc.transferSupplies(sup.y, ri.location);
				}
			}
		}
	}
	
	public void execute() throws GameActionException {
		countRobots();
		
		int beaverCount = rc.readBroadcast(RobotPlayer.numBEAVERS); 
		if (beaverCount < 3) {
			trySpawn(RobotType.BEAVER);
		}
		if (rc.readBroadcast(RobotPlayer.CORNERBEAVER) == 0 && beaverCount > 0) {
			RobotInfo[] robots = rc.senseNearbyRobots(2, myTeam);
			RobotInfo firstBeaver = null;
			for (RobotInfo ri : robots) {
				System.out.println(ri);
				if (ri.type == RobotType.BEAVER) {
					firstBeaver = ri;
					break;
				}
			}
			if (firstBeaver != null) {
				rc.broadcast(RobotPlayer.CORNERBEAVER, firstBeaver.ID);
			}
		}
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
		int numHelipad = 0;
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
			} else if (type == RobotType.HELIPAD) {
				numHelipad++;
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
	}
	
	/**
	 * Checks if the array is filled with same int values.
	 * @param xs array of ints
	 * @return true if all the same values, false otherwise
	 */
	private static boolean allEqual(int[] xs) {
		if (xs.length < 2) {
			return true;
		}
		for (int i = 1; i < xs.length; i++) {
			if (xs[i] != xs[i-1]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if all coordinates are on the same line.
	 * @param xs array of x coordinates
	 * @param ys array of y coordinates
	 * @return true if on the same line, false otherwise
	 */
	private static boolean onLine(int[] xs, int[] ys) {
		if (xs.length < 3) {
			return true;
		}
		
		int dx = xs[1] - xs[0];
		int dy = ys[1] - ys[0];
		if (dx == 0) {			// Lines are vertical
			for (int i = 2; i < xs.length; i++) {
				if (xs[i] - xs[i-1] != 0) {
					return false;
				}
			}
		} else {				// Not vertical
			double k = ((double) dy) / dx;
			for (int i = 2; i < xs.length; i++) {
				int dxi = xs[i] - xs[i-1];
				int dyi = ys[i] - ys[i-1];
				if (Math.abs(k - ((double) dyi) / dxi) > 0.01) {
					return false;
				}
			}
		}
		return true;
	}

}
