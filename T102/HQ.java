package T102;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.world.Robot;
import T102.RobotPlayer;

public class HQ extends BaseBot {
	
	private static class Tuple {
		int x, y;
		public Tuple(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private static final Map<RobotType, Tuple> hqSupplies = new HashMap<>();
	static {
		hqSupplies.put(RobotType.BEAVER, new Tuple(100, 1000));
		hqSupplies.put(RobotType.MINER, new Tuple(200, 2000));
		hqSupplies.put(RobotType.DRONE, new Tuple(400, 3000));
	}

	public HQ(RobotController rc) {
		super(rc);
		
		/*
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
			System.out.println("rotation");
		} else if (onLine(xs, ys)) {				// Reflection symmetry
			System.out.println("reflection");
		} else {									// Unknown symmetry
			System.out.println("something else");
		}
		*/
		
		if (myHQ.x == theirHQ.x || myHQ.y == theirHQ.y) {
			System.out.println("reflection");
		} else {
			System.out.println("rotation");
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
		
		if (rc.readBroadcast(RobotPlayer.MAPSET) == 1 && rc.readBroadcast(RobotPlayer.expSTARTED) == 0) {
			decideExploringPoints();
		}
		
		int beaverCount = rc.readBroadcast(RobotPlayer.numBEAVERS); 
		//if (rc.readBroadcast(RobotType.BEAVER.ordinal()) < 3) {
		if (rc.readBroadcast(RobotPlayer.numBEAVERS) < 3) {
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
		//transferToSupplier();
		rc.yield();
	}
	
	
	private void decideExploringPoints() throws GameActionException {
		int height = rc.readBroadcast(RobotPlayer.MAPHEIGHT);
		int width = rc.readBroadcast(RobotPlayer.MAPWIDTH);
		int xs = rc.readBroadcast(RobotPlayer.TOPLEFTX);
		int ys = rc.readBroadcast(RobotPlayer.TOPLEFTY);
		/*
		MapLocation pointA = null;
		MapLocation pointB = null;
		
		if (isRotationSym()) {
			MapLocation[] corners = corners(height, width, xs, ys);
			Arrays.sort(corners, new Comparator<MapLocation>() {

				@Override
				public int compare(MapLocation o1, MapLocation o2) {
					return o1.distanceSquaredTo(myHQ) - o2.distanceSquaredTo(myHQ);
				}
			});
			
			pointA = corners[1];
			pointB = corners[2];
		} else {
			// TODO other shit
		}
*/
		//System.out.println(height + " " + width + " " + xs + " " + ys);
		List<MapLocation> exploreLocations = new LinkedList<>();
		//rc.setIndicatorDot(new MapLocation(xs, ys), 0, 200, 0);
		for (int y = ys; y < ys + height; y += 6) {
			for (int x = xs; x < xs + width; x+= 3) {
				MapLocation loc = new MapLocation(x, y);
				if (loc.distanceSquaredTo(myHQ) <= loc.distanceSquaredTo(theirHQ)) {
					exploreLocations.add(loc);
				}
				
			}
			for (int x = xs + width; x >= xs; x -= 3) {
				MapLocation loc = new MapLocation(x, y+3);
				if (loc.distanceSquaredTo(myHQ) <= loc.distanceSquaredTo(theirHQ)) {
					exploreLocations.add(loc);
				}
			}
		}
		int offset = 0;
		for (MapLocation loc : exploreLocations) {
			if (RobotPlayer.expLOCFIRST + offset < RobotPlayer.expLOCLAST) {
				rc.broadcast(RobotPlayer.expLOCFIRST + offset, loc.x);
				rc.broadcast(RobotPlayer.expLOCFIRST + offset + 1, loc.y);
				offset += 2;
			}
		}
		int diff = (exploreLocations.size() / 4) << 1;
		rc.broadcast(RobotPlayer.expOFFSET1, RobotPlayer.expLOCFIRST);
		rc.broadcast(RobotPlayer.expOFFSET2, RobotPlayer.expLOCFIRST + diff);
		rc.broadcast(RobotPlayer.expOFFSET3, RobotPlayer.expLOCFIRST + 2*diff);
		rc.broadcast(RobotPlayer.expOFFSET4, RobotPlayer.expLOCFIRST + 3*diff);
		rc.broadcast(RobotPlayer.expLOCCOUNT, exploreLocations.size());
		rc.broadcast(RobotPlayer.expSTARTED, 1);
		
		/*
		for (int i = rc.readBroadcast(RobotPlayer.expOFFSET1); i < rc.readBroadcast(RobotPlayer.expOFFSET2); i+= 2) {
			MapLocation loc = new MapLocation(rc.readBroadcast(i), rc.readBroadcast(i+1));
			rc.setIndicatorDot(loc, 20, 100, 20);
		}
		for (int i = rc.readBroadcast(RobotPlayer.expOFFSET2); i < rc.readBroadcast(RobotPlayer.expOFFSET3); i+= 2) {
			MapLocation loc = new MapLocation(rc.readBroadcast(i), rc.readBroadcast(i+1));
			rc.setIndicatorDot(loc, 100, 20, 20);
		}
		for (int i = rc.readBroadcast(RobotPlayer.expOFFSET3); i < rc.readBroadcast(RobotPlayer.expOFFSET4); i+= 2) {
			MapLocation loc = new MapLocation(rc.readBroadcast(i), rc.readBroadcast(i+1));
			rc.setIndicatorDot(loc, 20, 200, 100);
		}
		for (int i = rc.readBroadcast(RobotPlayer.expOFFSET4);
				i < RobotPlayer.expLOCFIRST + rc.readBroadcast(RobotPlayer.expLOCCOUNT) * 2; i+= 2) {
			MapLocation loc = new MapLocation(rc.readBroadcast(i), rc.readBroadcast(i+1));
			rc.setIndicatorDot(loc, 20, 20, 100);
		}*/
		
		rc.yield();
	}


	/**
	 * If the supplier is within transfer range we transfer supplies to it.
	 * @throws GameActionException
	 */
	public void transferToSupplier() throws GameActionException {
		/*RobotInfo[] allies = rc.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, 
				myTeam);
		
		int idToLook = rc.readBroadcast(RobotPlayer.SUPPLIERID);

		for (int i=0; i<allies.length; ++i) {
			RobotInfo k = allies[i];

			if (k.ID == idToLook) {
				rc.transferSupplies(100000, allies[i].location);
			}
		}*/
		int idToLook = rc.readBroadcast(RobotPlayer.SUPPLIERID);
		RobotInfo supplier = getRobot(idToLook); 
		if (supplier != null && rc.getLocation().distanceSquaredTo(supplier.location)
				<= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
			rc.transferSupplies((int) (rc.getSupplyLevel() / 2), supplier.location);
		}
	}

	/**
	 * Counts the number of robots and broadcasts them for others to use.
	 * @throws GameActionException
	 */
	private void countRobots() throws GameActionException {
		
		//System.out.println("before " + Clock.getBytecodeNum());
		RobotInfo[] myRobots = rc.senseNearbyRobots(999999, myTeam);
		/*
		int n = RobotType.values().length;
		int[] count = new int[n];

		//System.out.println(Clock.getBytecodeNum());
		
		for (RobotInfo ri : myRobots) {
			count[ri.type.ordinal()]++;
		}
		//System.out.println(Clock.getBytecodeNum());
		for (int i = 0; i < n; i++) {
			rc.broadcast(i, count[i]);
		}
		//System.out.println("after " + Clock.getBytecodeNum());
		*/
		
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
