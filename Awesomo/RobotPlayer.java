package Awesomo;

import java.util.Random;

import battlecode.common.*;

public class RobotPlayer {

	static RobotController rc;
	static Team myTeam;
	static Team enemyTeam;
	static int myRange;
	static Random rand = new Random();
	static RobotInfo[] myRobots;
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	public static void run(RobotController myRC) {
		rc = myRC;
		while (true) {
			try {
				RobotType type = rc.getType();
				if (type == RobotType.SOLDIER) {
					soldierCode();

				} else if (type == RobotType.HQ) {
					hqCode();

				} else if (type == RobotType.BEAVER) {
					beaverCode();

				} else if (type == RobotType.BARRACKS) {
					barracksCode();

				} else if (type == RobotType.TOWER) {
					
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			rc.yield();
		}
			
	}
	
	private static void barracksCode() throws GameActionException {
		if (rc.isCoreReady() && rc.getTeamOre() >= 100 ) {
			trySpawn(directions[rand.nextInt(8)], RobotType.SOLDIER);	
		}
//		
	}

	private static void beaverCode() throws GameActionException {
		if (rc.isCoreReady() && rc.isWeaponReady()) {
			attackSomething();
		}
		if (rc.isCoreReady()) {
			int fate = rand.nextInt(1000);
			if (fate < 8 && rc.getTeamOre() >= 300) {
				tryBuild(directions[rand.nextInt(8)],RobotType.BARRACKS);
			} else if (fate < 900) {
				if (rc.senseOre(rc.getLocation())>1 && rc.canMine()) {
					rc.mine();
					System.out.println("mine");
				} else {
					tryMove(directions[rand.nextInt(8)]);
				}
			} else {
				tryMove(rc.senseHQLocation().directionTo(rc.getLocation()));
			}
		}
		
	}

	private static void hqCode() throws GameActionException {
		int fate = rand.nextInt(10000);
		myRobots = rc.senseNearbyRobots(999999, myTeam);
		int numSoldiers = 0;
		int numBashers = 0;
		int numBeavers = 0;
		int numBarracks = 0;
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
			}
		}
		rc.broadcast(0, numBeavers);
		rc.broadcast(1, numSoldiers);
		rc.broadcast(2, numBashers);
		rc.broadcast(100, numBarracks);

		if (rc.isWeaponReady()) {
			attackSomething();
		}
		
		if (rc.isCoreReady() && rc.getTeamOre() >= 100 && fate < Math.pow(1.2,12-numBeavers)*10000) {
			trySpawn(directions[rand.nextInt(8)], RobotType.BEAVER);	
		}
	}

	static void soldierCode() throws GameActionException {
		if ( areEnemiesInRange() ) {
			if (rc.isCoreReady() && rc.isWeaponReady()) {
				attackSomething();
			} else {
				return;
			}
		} 
		
		if (rc.isCoreReady() ) {
			int fate = rand.nextInt(1000);
			if (fate < 800 && Clock.getRoundNum() < 200 ) {
				MapLocation rallyPoint = findRallyPoint();
				goToLocation(rallyPoint);
			} else {
				goToLocation(rc.senseEnemyHQLocation());
				tryMove(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
			}
		}
	}
	
	private static MapLocation findRallyPoint() {
		MapLocation enemyLoc = rc.senseEnemyHQLocation();
		MapLocation ourLoc = rc.senseHQLocation();
		int x = ( enemyLoc.x + ourLoc.x )/2;
		int y = ( enemyLoc.y + ourLoc.y )/2;
		
		return new MapLocation(x,y);
	}

	static void goToLocation(MapLocation whereToGo) throws GameActionException {
		int dist = rc.getLocation().distanceSquaredTo(whereToGo);
		if (dist > 0) {
			Direction dir;
			if (dist > 10) {
				MapLocation ml = rc.getLocation();
				int dx = whereToGo.x - ml.x;
				int dy = whereToGo.y - ml.y;
				if (Math.abs(dx) > Math.abs(dy)) {
					dir = rc.getLocation().directionTo(new MapLocation(whereToGo.x, ml.y));
				} else {
					dir = rc.getLocation().directionTo(new MapLocation(ml.x, whereToGo.y));
				}
			} else {
				dir = rc.getLocation().directionTo(whereToGo);
			}
			
			MapLocation tile = rc.getLocation().add(dir);
			TerrainTile terrain = rc.senseTerrainTile(tile);
			if ( !terrain.isTraversable() ) {
				moveAroundObstacle(dir);
			} else {
				tryMove(dir);
			}
		}
	}
	
	/**
	 * Moves counter-clockwise around an obstacle blocking the desired direction.
	 * @param d desired direction to go.
	 * @throws GameActionException
	 */
	private static void moveAroundObstacle(Direction d) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,2,3};
		int dirint = directionToInt(d);

		while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 4) {
			rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
		}
		
	}

	// This method will attack an enemy in sight, if there is one
	static void attackSomething() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		if (enemies.length > 0) {
			rc.attackLocation(enemies[0].location);
		}
	}
	
	static boolean areEnemiesInRange() {
		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		return enemies.length > 0;
	}
	
	
	// This method will attempt to move in Direction d (or as close to it as possible)
	static void tryMove(Direction d) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 5) {
			rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
		}
	}
	
	// This method will attempt to spawn in the given direction (or as close to it as possible)
		static void trySpawn(Direction d, RobotType type) throws GameActionException {
			int offsetIndex = 0;
			int[] offsets = {0,1,-1,2,-2,3,-3,4};
			int dirint = directionToInt(d);
			boolean blocked = false;
//			System.out.println("before loop");
			while (offsetIndex < 8 && !rc.canSpawn(directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
				offsetIndex++;
			}
//			System.out.println("between: "+ offsetIndex);
			
			if (offsetIndex < 8) {
				rc.spawn(directions[(dirint+offsets[offsetIndex]+8)%8], type);
//				System.out.println("spawned");
			}
		}
	
	// This method will attempt to build in the given direction (or as close to it as possible)
	static void tryBuild(Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			rc.build(directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}
	
	static int directionToInt(Direction d) {
		switch(d) {
			case NORTH:
				return 0;
			case NORTH_EAST:
				return 1;
			case EAST:
				return 2;
			case SOUTH_EAST:
				return 3;
			case SOUTH:
				return 4;
			case SOUTH_WEST:
				return 5;
			case WEST:
				return 6;
			case NORTH_WEST:
				return 7;
			default:
				return -1;
		}
	}
}


