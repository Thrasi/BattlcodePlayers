package T103;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import T103.RobotPlayer;
import static T103.Utility.Tuple;

public class HQ extends BaseBot {
	
	
	
	private static final Map<RobotType, Tuple> hqSupplies = new HashMap<>();
	static {
		hqSupplies.put(RobotType.BEAVER, new Tuple(100, 1000));
		hqSupplies.put(RobotType.MINER, new Tuple(200, 2000));
		hqSupplies.put(RobotType.DRONE, new Tuple(10000, 15000));
		hqSupplies.put(RobotType.SOLDIER, new Tuple(500, 2500));
	}

	public HQ(RobotController rc) throws GameActionException {
		super(rc);
		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		MapLocation[] q = new MapLocation[enemyTowers.length+1];
		for (int i = 0; i < enemyTowers.length; i++) {
			q[i+1] = enemyTowers[i];
		}
		q[0] = theirHQ;
		MapInfo.setQueue(q);
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
	
	boolean printMap = false;
	int reqFlood = 1;
	int rCount = 0;
	
	public void execute() throws GameActionException {
		RobotCounter.countRobots();
		
		if (rc.readBroadcast(RobotPlayer.MAPSET) == 1 && rc.readBroadcast(RobotPlayer.expSTARTED) == 0) {
			decideExploringPoints();
		}
		
//		if (printMap && isSet(RobotPlayer.MAPBROADCASTED)) {
//			printMap = false;
//			System.out.println("entered here");
//			MapInfo.printMap();
//		}
		
		if (reqFlood == 1) {
			rc.broadcast(RobotPlayer.FLOODINDEX, 1);
			rc.broadcast(RobotPlayer.FLOODREQUEST, 1);
			reqFlood = 2;
			rCount = 0;
		} else if (reqFlood == 2 && MapInfo.isActive(1) && rCount > 3) {
			System.out.println(rCount);
			rc.broadcast(RobotPlayer.FLOODINDEX, 0);
			rc.broadcast(RobotPlayer.FLOODREQUEST, 1);
			reqFlood = 3;
			rCount = 0;
		} else if (reqFlood == 3  && MapInfo.isActive(0) && rCount > 3) {
			rc.broadcast(RobotPlayer.FLOODINDEX, 3);
			rc.broadcast(RobotPlayer.FLOODREQUEST, 1);
			reqFlood = 0;
		}
		
		if (isSet(RobotPlayer.FLOODACTIVE)) {
			rCount++;
			MapInfo.markFloodFromChannels();
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
		
		List<MapLocation> exploreLocations = new LinkedList<>();
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

	

}