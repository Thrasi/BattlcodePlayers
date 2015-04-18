package T103;

import java.util.HashMap;
import java.util.Map;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import static T103.Utility.Tuple;
import static T103.Channels.set;


public class HQ extends BaseBot {
	
	public static final int EXPLCOUNT = 4;
	
	private static final Map<RobotType, Tuple> hqSupplies = new HashMap<>();
	static {
		hqSupplies.put(RobotType.BEAVER, new Tuple(100, 1000));
		hqSupplies.put(RobotType.MINER, new Tuple(200, 3000));
		hqSupplies.put(RobotType.DRONE, new Tuple(10000, 15000));
		hqSupplies.put(RobotType.SOLDIER, new Tuple(1000, 4000));
	}

	public HQ(RobotController rc) throws GameActionException {
		super(rc);
		rc.broadcast(Channels.expDRONECOUNT, EXPLCOUNT);
		rc.broadcast(Channels.SUPPLYQSTART, Channels.LOWERSUPPLYBOUND);
		rc.broadcast(Channels.SUPPLYQEND, Channels.LOWERSUPPLYBOUND);
		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		MapLocation[] q = new MapLocation[enemyTowers.length+1];
		for (int i = 0; i < enemyTowers.length; i++) {
			q[i+1] = enemyTowers[i];
		}
		q[0] = theirHQ;
		MapInfo.setQueue(q);
		MapInfo.requestFlood(0);
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
		
		if (Clock.getRoundNum() < 600) {
			// Set rally
			MapLocation[] myTowers = rc.senseTowerLocations();
			if (myTowers.length > 0) {
				rc.broadcast(Channels.SWARMFIRSTX, myTowers[0].x);
				rc.broadcast(Channels.SWARMFIRSTY, myTowers[0].y);
				set(Channels.SWARMSET);
			} else {
				Direction dir = myHQ.directionTo(theirHQ);
				int dist = (int) (Math.sqrt(myHQ.distanceSquaredTo(theirHQ)) / 3);
				
				MapLocation rally = myHQ.add(dir, dist);
				rc.broadcast(Channels.SWARMFIRSTX, rally.x);
				rc.broadcast(Channels.SWARMFIRSTY, rally.y);
				set(Channels.SWARMSET);
			}
		} else if (Clock.getRoundNum() > 600 && Clock.getRoundNum() < 1200) {
			// Set rally
			rc.broadcast(Channels.SWARMFIRSTX, theirHQ.x);
			rc.broadcast(Channels.SWARMFIRSTY, theirHQ.y);
			
			rc.broadcast(Channels.SWARMIDXSOLDIER, 1);
			MapLocation[] myTowers = rc.senseTowerLocations();
			if (myTowers.length > 0) {
				rc.broadcast(Channels.SWARMFIRSTX+1, myTowers[1].x);
				rc.broadcast(Channels.SWARMFIRSTY+1, myTowers[1].y);
				set(Channels.SWARMSET+1);
			} else {
				Direction dir = myHQ.directionTo(theirHQ);
				int dist = (int) (Math.sqrt(myHQ.distanceSquaredTo(theirHQ)) / 3);
				
				MapLocation rally = myHQ.add(dir, dist);
				rc.broadcast(Channels.SWARMFIRSTX+1, rally.x);
				rc.broadcast(Channels.SWARMFIRSTY+1, rally.y);
				set(Channels.SWARMSET+1);
			}
		} else {
			//rc.broadcast(Channels.SWARMFLOODIDX+1, 0);
			//set(Channels.SWARMSETFLOOD + 1);
		}
		
		if (rc.readBroadcast(Channels.MAPSET) == 1 && rc.readBroadcast(Channels.expSTARTED) == 0) {
			MapInfo.decideExploringPoints();
		}
//		if (isSet(Channels.expSTARTED)) {
//			MapInfo.markExploreLocations();
//		}
		
//		if (printMap && isSet(Channels.MAPBROADCASTED)) {
//			printMap = false;
//			System.out.println("entered here");
//			MapInfo.printMap();
//		}
		
/*		
		if (reqFlood == 1 && !isSet(Channels.FLOODREQUEST)) {
			MapInfo.requestFlood(0);
			reqFlood = 2;
			rCount = 0;
		} else if (reqFlood == 2 && !isSet(Channels.FLOODREQUEST)) {
			MapInfo.requestFlood(1);
			reqFlood = 3;
			rCount = 0;
		} else if (reqFlood == 3 && !isSet(Channels.FLOODREQUEST)) {
			MapInfo.requestFlood(3);
			reqFlood = 4;
		} else if (reqFlood == 4 && !isSet(Channels.FLOODREQUEST)) {
			MapInfo.requestFlood(3);
			reqFlood = 5;
		} else if (reqFlood == 5 && !isSet(Channels.FLOODREQUEST)) {
			MapInfo.requestFlood(1);
			reqFlood = 6;
		} else if (reqFlood == 6 && !isSet(Channels.FLOODREQUEST)) {
			MapInfo.requestFlood(2);
			reqFlood = 6;
		}
		*/
/*	
		if (MapInfo.isActive(0)) {
			MapInfo.markFloodFromChannels(0);
		}
	
		if (MapInfo.isActive(0)) {
			MapInfo.markFloodFromChannels(0);
		}
		if (MapInfo.isActive(1)) {
			MapInfo.markFloodFromChannels(1);
		}
		if (MapInfo.isActive(3)) {
			MapInfo.markFloodFromChannels(3);
		}
	*/	
		
		int beaverCount = rc.readBroadcast(Channels.numBEAVERS); 
		//if (rc.readBroadcast(RobotType.BEAVER.ordinal()) < 3) {
		if (rc.readBroadcast(Channels.numBEAVERS) < 2) {
			trySpawn(RobotType.BEAVER);
		}
		if (rc.readBroadcast(Channels.CORNERBEAVER) == 0 && beaverCount > 0) {
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
				rc.broadcast(Channels.CORNERBEAVER, firstBeaver.ID);
			}
		}
		
		//transferToSupplier();
		
		rc.yield();
	}
	
	
	


	/**
	 * If the supplier is within transfer range we transfer supplies to it.
	 * @throws GameActionException
	 */
	public void transferToSupplier() throws GameActionException {
		/*RobotInfo[] allies = rc.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, 
				myTeam);
		
		int idToLook = rc.readBroadcast(Channels.SUPPLIERID);

		for (int i=0; i<allies.length; ++i) {
			RobotInfo k = allies[i];

			if (k.ID == idToLook) {
				rc.transferSupplies(100000, allies[i].location);
			}
		}*/
		int idToLook = rc.readBroadcast(Channels.SUPPLIERID);
		RobotInfo supplier = getRobot(idToLook); 
		if (supplier != null && rc.getLocation().distanceSquaredTo(supplier.location)
				<= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
			rc.transferSupplies((int) (rc.getSupplyLevel() / 2), supplier.location);
		}
	}

	

}
