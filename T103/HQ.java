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
import static T103.Channels.isSet;


public class HQ extends BaseBot {
	
	public static final int[] maxMINERSC = {20, 35, 35};
	
	// Drone count
	public static final int[] maxEXPLC = {0, 4, 6};		// Explore turn into supplier
	public static final int[] maxDRONES = {1, 0, 0};	// Supplier
	
	private static final int maxBEAVERS = 3;
	
	
	private static final Map<RobotType, Tuple> hqSupplies = new HashMap<>();
	static {
		hqSupplies.put(RobotType.BEAVER, new Tuple(100, 500));
		hqSupplies.put(RobotType.MINER, new Tuple(200, 3000));
		hqSupplies.put(RobotType.DRONE, new Tuple(10000, 15000));
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
		RobotType.HELIPAD,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, 
		RobotType.BARRACKS,RobotType.TANKFACTORY,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, 
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, RobotType.TANKFACTORY,
		RobotType.TANKFACTORY, RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, RobotType.TANKFACTORY,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, RobotType.TANKFACTORY,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, RobotType.TANKFACTORY,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, RobotType.TANKFACTORY,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,RobotType.TANKFACTORY };

	/*
	private static final RobotType[] buildQueue = {RobotType.MINERFACTORY,
		RobotType.HELIPAD, RobotType.BARRACKS,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.BARRACKS,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.BARRACKS,RobotType.BARRACKS,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.BARRACKS,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, RobotType.BARRACKS,
		RobotType.BARRACKS, RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT, RobotType.BARRACKS,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT,
		RobotType.SUPPLYDEPOT, RobotType.SUPPLYDEPOT};
	*/
	
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
		
		MapLocation[] q = {theirHQ};
		MapInfo.setQueue(q);
		MapInfo.requestFlood(0);
		MapLocation[] myTowers = rc.senseTowerLocations();
		for (int i = 0; i < myTowers.length; i++) {
			RobotInfo tower = rc.senseRobotAtLocation(myTowers[i]);
			rc.broadcast(Channels.TOWERID + i, tower.ID);
		}
		
		rc.broadcast(Channels.expDRONECOUNT, maxEXPLC[mapClass]);
		
		System.out.println(theirHQ.distanceSquaredTo(myHQ));
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
	

	boolean tankDone[] = new boolean[20];
	boolean reconstructed = false;
	
	int idx = 0;
	
	public void execute() throws GameActionException {
		RobotCounter.countRobots();
		
		int ss = 30;		// Swarm size
		for (int idx = 0; idx < 15; idx++) {
//			if (idx == 0) {
//				ss = 40;
//			}
			if (rc.readBroadcast(Channels.SWARMCOUNTTANK+idx) < ss) {
				Direction dir = myHQ.directionTo(theirHQ);
				int dist = (int) (Math.sqrt(myHQ.distanceSquaredTo(theirHQ)) / 5);
				MapLocation rally = myHQ.add(dir, dist);
				rc.broadcast(Channels.SWARMFIRSTX + idx, rally.x);
				rc.broadcast(Channels.SWARMFIRSTY + idx, rally.y);
				Channels.set(Channels.SWARMSET + idx);
			} else if (!tankDone[idx]) {
				tankDone[idx] = true;
				if (MapInfo.isActive(0)) {
					rc.broadcast(Channels.SWARMFLOODIDX, 0);
					Channels.set(Channels.SWARMSETFLOOD+idx);
				} else {
					rc.broadcast(Channels.SWARMFIRSTX + idx, theirHQ.x);
					rc.broadcast(Channels.SWARMFIRSTY + idx, theirHQ.y);
					Channels.set(Channels.SWARMSET + idx);
				}
				rc.broadcast(Channels.SWARMIDXTANK, idx+1);
			}

		}
		
		
		if (Clock.getRoundNum() > 1700) {
			for (int i = 0; i < 20; i++) {
				if (MapInfo.isActive(0)) {
					rc.broadcast(Channels.SWARMFLOODIDX, 0);
					Channels.set(Channels.SWARMSETFLOOD + i);
				} else {
					rc.broadcast(Channels.SWARMFIRSTX + i, theirHQ.x);
					rc.broadcast(Channels.SWARMFIRSTY + i, theirHQ.y);
					Channels.set(Channels.SWARMSET + i);
				}
				rc.broadcast(Channels.SWARMIDXTANK, i+1);
			}
		}
		
		
		if (rc.readBroadcast(Channels.MAPSET) == 1 && rc.readBroadcast(Channels.expSTARTED) == 0) {
			MapInfo.decideExploringPoints();
		}
		
		if (!reconstructed && (allDone() || Clock.getRoundNum() > 600)) {
			MapInfo.reconstructMap();
			MapInfo.floodAndServe();
			MapInfo.serve();
			reconstructed = true;
		}
		
		
		if (rc.readBroadcast(Channels.numBEAVERS) < maxBEAVERS) {
			trySpawn(RobotType.BEAVER);
		}
//		int beaverCount = rc.readBroadcast(Channels.numBEAVERS); 
//		if (beaverCount < maxBEAVERS) {
//			BuildingStrategies.trySpawnEmpty(RobotType.BEAVER);
//		}
//		if (rc.readBroadcast(Channels.CORNERBEAVER) == 0 && beaverCount > 0) {
//			RobotInfo[] robots = rc.senseNearbyRobots(2, myTeam);
//			RobotInfo firstBeaver = null;
//			for (RobotInfo ri : robots) {
//				if (ri.type == RobotType.BEAVER) {
//					firstBeaver = ri;
//					break;
//				}
//			}
//			if (firstBeaver != null) {
//				rc.broadcast(Channels.CORNERBEAVER, firstBeaver.ID);
//			}
//		}
//		
		
		
		//Channels.reset(Channels.SWARMCOUNTTANK);
		rc.yield();
	}
	
	/** 
	 * Checks if all exploration drones have finished.
	 * @return true if they have, false otherwise
	 * @throws GameActionException
	 */
	private static boolean allDone() throws GameActionException {
		int count = rc.readBroadcast(Channels.expDRONECOUNT);
		for (int i = count-1; i >= 0; i--) {
			if (!isSet(Channels.expDRONEDONE + i)) {
				return false;
			}
		}
		return true;
	}
	

}
