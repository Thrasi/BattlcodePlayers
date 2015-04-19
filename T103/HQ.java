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
import static T103.BaseBot.rc;
import static T103.Channels.set;


public class HQ extends BaseBot {
	
	public static final int[] maxMINERSC = {15, 15, 20};
	public static final int[] maxSUPPLYDEPOTSC = {2, 8, 12};
	public static final int[] maxTECHC = {0, 1, 1};
	public static final int[] maxEXPLC = {0, 0, 0};
	public static final int[] maxTANKFACTORIESC = {3, 4, 6};
	public static final int[] maxBARRACKSC = {2, 4, 6};
	public static final int[] maxHELIPADC = {0, 1, 1};
	public static final int[] maxAEROC = {0, 0, 0};
	public static final int[] maxMINFACTORYC = {1, 1, 1};
	public static final int[] maxDRONES = {1, 4, 6};
	
	private static final int maxBEAVERS = 3;
	
	
	private static final Map<RobotType, Tuple> hqSupplies = new HashMap<>();
	static {
		hqSupplies.put(RobotType.BEAVER, new Tuple(100, 1000));
		hqSupplies.put(RobotType.MINER, new Tuple(200, 3000));
		hqSupplies.put(RobotType.DRONE, new Tuple(20000, 25000));
		hqSupplies.put(RobotType.SOLDIER, new Tuple(1000, 4000));
		hqSupplies.put(RobotType.TANK, new Tuple(500, 5000));
	}
	
	public static final RobotType[] TYPES = new RobotType[21];		// 21 unit types
	static {
		int i = 0;
		for (RobotType type : RobotType.values()) {
			TYPES[i] = type;
			i++;
		}
	}
	
	private static final RobotType[] buildQueue = {RobotType.MINERFACTORY,
		RobotType.HELIPAD, RobotType.BARRACKS, RobotType.TANKFACTORY,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, RobotType.TANKFACTORY,
		RobotType.TANKFACTORY, RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, RobotType.TANKFACTORY,
		RobotType.TANKFACTORY, RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT};

	
	/**
	 * Constructor!!!!
	 * @param rc
	 * @throws GameActionException
	 */
	public HQ(RobotController rc) throws GameActionException {
		super(rc);
		rc.broadcast(Channels.SUPPLYQSTART, Channels.LOWERSUPPLYBOUND);
		rc.broadcast(Channels.SUPPLYQEND, Channels.LOWERSUPPLYBOUND);
		rc.broadcast(Channels.BUILDQSTART, Channels.BUILDQLO);
		rc.broadcast(Channels.BUILDQEND, Channels.BUILDQLO);
		
		for (RobotType type : buildQueue) {
			addToBuildQueue(type);
		}
		
		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		MapLocation[] q = new MapLocation[enemyTowers.length+1];
		for (int i = 0; i < enemyTowers.length; i++) {
			q[i+1] = enemyTowers[i];
		}
		q[0] = theirHQ;
		MapInfo.setQueue(q);
		MapInfo.requestFlood(0);
		MapLocation[] myTowers = rc.senseTowerLocations();
		for (int i = 0; i < myTowers.length; i++) {
			RobotInfo tower = rc.senseRobotAtLocation(myTowers[i]);
			rc.broadcast(Channels.TOWERID + i, tower.ID);
		}
		
		rc.broadcast(Channels.expDRONECOUNT, maxEXPLC[mapClass]);
		
//		rc.broadcast(Channels.SWARMIDXTANK, 0);
//		rc.broadcast(Channels.SWARMFIRSTX + 0, myTowers[2].x);
//		rc.broadcast(Channels.SWARMFIRSTY + 0, myTowers[2].y);
//		Channels.set(Channels.SWARMSET + 0);
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
	static boolean[] tankDone = new boolean[10];
	
	public void execute() throws GameActionException {
		RobotCounter.countRobots();
		
//		for (int i = 0; i < 20; i++) {
//			if (Channels.isSet(Channels.SWARMSET+i)) {
//				MapLocation loc = new MapLocation(
//						rc.readBroadcast(Channels.SWARMFIRSTX+i),
//						rc.readBroadcast(Channels.SWARMFIRSTY+i)
//				);
//				int id = findTarget(loc);
//				rc.broadcast(Channels.SWARMPRIMARY+i, id);
//			}
//		}
		
		
		MapLocation towerInDanger = friendlyTowerInDanger();
		int idx = 0;
		MapLocation[] myTowers = rc.senseTowerLocations();
		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		rc.setIndicatorString(0, towerInDanger + " " + rc.readBroadcast(Channels.SWARMCOUNTTANK));
		if (rc.readBroadcast(Channels.SWARMCOUNTTANK) <= 20) {
			rc.broadcast(Channels.SWARMIDXTANK, idx);
			rc.broadcast(Channels.SWARMFIRSTX + idx, myTowers[0].x);
			rc.broadcast(Channels.SWARMFIRSTY + idx, myTowers[0].y);
			Channels.set(Channels.SWARMSET + idx);
		} else {
			Direction dir = myHQ.directionTo(theirHQ);
			int dist = (int) (Math.sqrt(myHQ.distanceSquaredTo(theirHQ)) / 5);
			MapLocation rally = myHQ.add(dir, dist);
			rc.broadcast(Channels.SWARMIDXTANK, idx);
			rc.broadcast(Channels.SWARMFIRSTX + idx, rally.x);
			rc.broadcast(Channels.SWARMFIRSTY + idx, rally.y);
			Channels.set(Channels.SWARMSET + idx);
		}
		/*if (!tankDone[idx]) {
			tankDone[0] = true;
			//rc.broadcast(Channels.SWARMIDXTANK, idx);
			rc.broadcast(Channels.SWARMFIRSTX + idx, enemyTowers[0].x);
			rc.broadcast(Channels.SWARMFIRSTY + idx, enemyTowers[0].y);
			Channels.set(Channels.SWARMSET + idx);
		}*/
		/*
		idx++;
		if (rc.readBroadcast(Channels.SWARMCOUNTTANK) <= 12) {
			rc.broadcast(Channels.SWARMIDXTANK, idx);
			rc.broadcast(Channels.SWARMFIRSTX + idx, myTowers[2].x);
			rc.broadcast(Channels.SWARMFIRSTY + idx, myTowers[2].y);
			Channels.set(Channels.SWARMSET + idx);
		} else if (!tankDone[idx]) {
			tankDone[0] = true;
			//rc.broadcast(Channels.SWARMIDXTANK, idx);
			rc.broadcast(Channels.SWARMFIRSTX + idx, enemyTowers[0].x);
			rc.broadcast(Channels.SWARMFIRSTY + idx, enemyTowers[0].y);
			Channels.set(Channels.SWARMSET + idx);
		}
		
		idx++;
		if (rc.readBroadcast(Channels.SWARMCOUNTTANK) <= 12) {
			rc.broadcast(Channels.SWARMIDXTANK, idx);
			rc.broadcast(Channels.SWARMFIRSTX + idx, myTowers[2].x);
			rc.broadcast(Channels.SWARMFIRSTY + idx, myTowers[2].y);
			Channels.set(Channels.SWARMSET + idx);
		} else if (!tankDone[idx]) {
			tankDone[0] = true;
			//rc.broadcast(Channels.SWARMIDXTANK, idx);
			rc.broadcast(Channels.SWARMFIRSTX + idx, enemyTowers[0].x);
			rc.broadcast(Channels.SWARMFIRSTY + idx, enemyTowers[0].y);
			Channels.set(Channels.SWARMSET + idx);
		}
		
		idx++;
		if (rc.readBroadcast(Channels.SWARMCOUNTTANK) <= 12) {
			rc.broadcast(Channels.SWARMIDXTANK, idx);
			rc.broadcast(Channels.SWARMFIRSTX + idx, myTowers[2].x);
			rc.broadcast(Channels.SWARMFIRSTY + idx, myTowers[2].y);
			Channels.set(Channels.SWARMSET + idx);
		} else if (!tankDone[idx]) {
			tankDone[0] = true;
			//rc.broadcast(Channels.SWARMIDXTANK, idx);
			rc.broadcast(Channels.SWARMFIRSTX + idx, enemyTowers[0].x);
			rc.broadcast(Channels.SWARMFIRSTY + idx, enemyTowers[0].y);
			Channels.set(Channels.SWARMSET + idx);
		}
		*/
		
		/*
		if (Clock.getRoundNum() < 1500) {
			// Set rally
			myTowers = rc.senseTowerLocations();
//			if (myTowers.length > 0) {
				rc.broadcast(Channels.SWARMFIRSTX, myTowers[0].x);
				rc.broadcast(Channels.SWARMFIRSTY, myTowers[0].y);
				set(Channels.SWARMSET);
//			} else {
				Direction dir = myHQ.directionTo(theirHQ);
				int dist = (int) (Math.sqrt(myHQ.distanceSquaredTo(theirHQ)) / 5);
				
				MapLocation rally = myHQ.add(dir, dist);
				rc.broadcast(Channels.SWARMFIRSTX, rally.x);
				rc.broadcast(Channels.SWARMFIRSTY, rally.y);
				set(Channels.SWARMSET);
//			}
		} else {
			rc.broadcast(Channels.SWARMFIRSTX, theirHQ.x);
			rc.broadcast(Channels.SWARMFIRSTY, theirHQ.y);
			rc.broadcast(Channels.SWARMFIRSTX+1, theirHQ.x);
			rc.broadcast(Channels.SWARMFIRSTY+1, theirHQ.y);
		}
		*/
		if (MapInfo.isActive(0)) {
			rc.broadcast(Channels.SWARMFLOODIDX, 0);
			set(Channels.SWARMSETFLOOD);
		}
		
		if (rc.readBroadcast(Channels.MAPSET) == 1 && rc.readBroadcast(Channels.expSTARTED) == 0) {
			MapInfo.decideExploringPoints();
		}
		
		
		int beaverCount = rc.readBroadcast(Channels.numBEAVERS); 
		if (rc.readBroadcast(Channels.numBEAVERS) < maxBEAVERS) {
			BuildingStrategies.trySpawnEmpty(RobotType.BEAVER);//trySpawn(RobotType.BEAVER);
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
		
		
		
		//Channels.reset(Channels.SWARMCOUNTTANK);
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
