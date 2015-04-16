package T102;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

public class BaseBot {

	// All 8 directions in one place
	protected static final Direction[] directions = {
		Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
		Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST
	};
	
	
	// Current robot
	protected RobotController rc;
	
	// Locations of both HQs
	protected MapLocation myHQ, theirHQ;
	
	// References to both teams
	protected Team myTeam, theirTeam;
	
	// Random number generator
	protected Random rand;

	
	/**
	 * Sets HQ locations, teams and initializes random generator.
	 * @param rc this robot's controller
	 */
	public BaseBot(RobotController rc) {
		this.rc = rc;
		this.myHQ = rc.senseHQLocation();
		this.theirHQ = rc.senseEnemyHQLocation();
		this.myTeam = rc.getTeam();
		this.theirTeam = this.myTeam.opponent();
		this.rand = new Random(rc.getID());
	}
	
	
	
	// RUN

		/**
		 * Called at the beginning of the turn. Just transfers supplies to the robot
		 * with lowest amount of supplies.
		 * @throws GameActionException
		 */
		public void beginingOfTurn() throws GameActionException {
			//if (rc.getType() == RobotType.SOLDIER) {
				//rc.broadcast(RobotPlayer.numSOLDIERS, rc.readBroadcast(RobotPlayer.numSOLDIERS)+1);
			//}
			if (rc.senseEnemyHQLocation() != null) {
				this.theirHQ = rc.senseEnemyHQLocation();
			}
			transferSuppliesTolowest();
		}


		/**
		 * Executes main actions
		 * @throws GameActionException
		 */
		public void execute() throws GameActionException {
			rc.yield();
		}

		/**
		 * Called at the end of the turn
		 */
		public void endOfTurn() {
		}

		/**
		 * Called to perform actions of this robot.
		 * @throws GameActionException if anything weird happens
		 */
		public void go() throws GameActionException {
			beginingOfTurn();
			execute();
			endOfTurn();
		}
	
	
	
	// BLOCK FOR SHORT FUNCTIONS TO MAKE ACTIONS EASIER TO PERFORM
	
	/**
	 * Tries to spawn the bot with given type and direction. Function just calls
	 * isCoreReady and canSpawn and helps to avoid all unnecessary code.
	 * @param dir direction for spawning the bot
	 * @param type type for the bot to spawn
	 * @return true if spawned, false otherwise
	 * @throws GameActionException should not be thrown
	 */
	protected boolean trySpawn(Direction dir, RobotType type) throws GameActionException {
		if (rc.isCoreReady() && rc.canSpawn(dir, type)) {
			rc.spawn(dir, type);
			return true;
		}
		return false;
	}
	
	/**
	 * Tries to attack the given location. Helps to avoid performing check every time.
	 * @param location location to shoot
	 * @return true if shot, false otherwise
	 * @throws GameActionException should not be thrown
	 */
	protected boolean tryAttack(MapLocation location) throws GameActionException {
		if (rc.isWeaponReady() && rc.canAttackLocation(location)) {
			rc.attackLocation(location);
			return true;
		}
		return false;
	}
	
	/**
	 * Tries to move in certain direction. Helps to avoid performing check every time.
	 * @param dir direction to move
	 * @return true if moved, false otherwise
	 * @throws GameActionException should not be thrown
	 */
	protected boolean tryMove(Direction dir) throws GameActionException {
		if (rc.isCoreReady() && rc.canMove(dir)) {
			rc.move(dir);
			return true;
		}
		return false;
	}
	
	/**
	 * Tries to build the certain building in given direction. Helps to avoid
	 * performing check every time.
	 * @param dir direction in which to build
	 * @param type type of building
	 * @return true if built, false otherwise
	 * @throws GameActionException should not be thrown
	 */
	protected boolean tryBuild(Direction dir, RobotType type) throws GameActionException {
		if (rc.isCoreReady() && rc.canBuild(dir, type)) {
			rc.build(dir, type);
			return true;
		}
		return false;
	}
	
	/**
	 * Tries to mine current location.
	 * @return true if mined, false otherwise
	 * @throws GameActionException should not be thrown
	 */
	protected boolean tryMine() throws GameActionException {
		if (rc.isCoreReady() && rc.canMine()) {
			rc.mine();
			return true;
		}
		return false;
	}

	
	
	// DIRECTIONS BLOCK
	
	/**
	 * Random direction generator.
	 * @return random direction
	 */
	protected Direction getRandomDirection() {
		return directions[rand.nextInt(8)];
	}

	/**
	 * Generates direction directly away from target location.
	 * @param target target location
	 * @return direction away
	 */
	protected Direction getDirectionAwayFrom(MapLocation target) {
		return rc.getLocation().directionTo(target).opposite();
	}
	
	/**
	 * Returns all 8 direction but the ones towards the target come first in the array.
	 * Direction directly towards the target is then at index 0, and opposite direction
	 * is at index 7.
	 * @param target target location
	 * @return array of 8 directions
	 */
	protected Direction[] getAllDirectionsTowards(MapLocation target) {
		Direction toTarget = rc.getLocation().directionTo(target);
		Direction dirs[] = {
				toTarget,
				toTarget.rotateLeft(),
				toTarget.rotateRight(),
				toTarget.rotateLeft().rotateLeft(),
				toTarget.rotateRight().rotateRight(),
				toTarget.rotateLeft().rotateLeft().rotateLeft(),
				toTarget.rotateRight().rotateRight().rotateRight(),
				toTarget.opposite()
		};
		return dirs;
	}
	
	/**
	 * Returns 5 directions that will still lead towards the target location.
	 * @param target target location
	 * @return array of 5 directions
	 */
	protected Direction[] getDirectionsTowards(MapLocation target) {
		Direction toTarget = rc.getLocation().directionTo(target);
		Direction dirs[] = {
				toTarget,
				toTarget.rotateLeft(),
				toTarget.rotateRight(),
				toTarget.rotateLeft().rotateLeft(),
				toTarget.rotateRight().rotateRight()
		};
		return dirs;
	}


	
	// ACTIONS BLOCK
	
	/**
	 * Returns direction towards the target that is possible to perform.
	 * @param target target location
	 * @return direction if possible to move, null otherwise
	 */
	public Direction getMoveDirection(MapLocation target) {
		Direction[] dirs = getDirectionsTowards(target);
		for (Direction d : dirs) {
			if (rc.canMove(d)) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Attempts to return a direction to enemy HQ, then tries the other directions.
	 * @param type robot type to spawn
	 * @return Direction to spawn to or null if all is occupied.
	 */
	public Direction getSpawnDirection(RobotType type) {
		for (Direction dir : getAllDirectionsTowards(theirHQ)) {
			if (rc.canSpawn(dir, type)) {
				return dir;
			}
		}
		return null;
	}

	/**
	 * Attempts to return a direction to my HQ, then tries the other directions.
	 * @param type robot type to build
	 * @return Direction to build or null if everything is occupied.
	 */
	public Direction getBuildDirection(RobotType type) {
		for (Direction dir : getAllDirectionsTowards(myHQ)) {
			if (rc.canBuild(dir, type)) {
				return dir;
			}
		}
		return null;
	}
	
	/**
	 * Attempt to spawn a robot of given type.
	 * @param type The type of robot to spawn
	 */
	public void tryToSpawn(RobotType type) throws GameActionException {
		if (type != null) {
			Direction newDir = getSpawnDirection(type);
			if (newDir != null) {
				rc.spawn(newDir, type);
			}
		}
	}
	
	/**
	 * Tries to move in any direction but checking the ones that are towards
	 * the target first.
	 * @param loc target location
	 * @return true if moved, false if everything is occupied
	 */
	public boolean tryMoveTo(MapLocation loc) throws GameActionException {
		for (Direction dir : getDirectionsTowards(loc)) {
			if (tryMove(dir)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Tries to spawn robot no matter what in any direction.
	 * @param type robot type to spawn
	 * @return true if spawned, false if everything is occupied
	 */
	public boolean trySpawn(RobotType type) throws GameActionException {
		for (Direction dir : directions) {
			if (trySpawn(dir, type)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Tries to build robot no matter what in any direction.
	 * @param type robot type to build
	 * @return true if built, false otherwise
	 */
	public boolean tryBuild(RobotType type) throws GameActionException {
		for (Direction dir : directions) {
			if (tryBuild(dir, type)) {
				return true;
			}
		}
		return false;
	}
	

	
	// SENSING ROBOTS

	/**
	 * Senses all allies.
	 * @return array of ally robots
	 */
	public RobotInfo[] getAllAllies() {
		return rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
	}

	/**
	 * Senses all enemies in attacking range of this robot.
	 * @return array of enemy robots
	 */
	public RobotInfo[] getEnemiesInAttackingRange() {
		return rc.senseNearbyRobots(rc.getType().attackRadiusSquared, theirTeam);
	}
	
	/**
	 * Senses all ally robots in radius of this robot.
	 * @return array of ally robots
	 */
	public RobotInfo[] getNearbyAllies() {
		return rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, myTeam);
	}
	
	/**
	 * Senses all enemy robots in sensing radius of this robot.
	 * @return array of enemy robots
	 */
	public RobotInfo[] getNearbyEnemies() {
		return rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, theirTeam);
	}
	
	/**
	 * Senses the closest enemy robot in sensing radius of the robot.
	 * @return closest enemy within sensing range
	 */
	public RobotInfo getNearestNearByEnemy() {
		double closestDistance = Double.MAX_VALUE-1;
		double distanceToEnemy;
		RobotInfo closestEnemy = null;
		for ( RobotInfo ri : getNearbyEnemies() ) {
			distanceToEnemy = rc.getLocation().distanceSquaredTo(ri.location);
			if (closestDistance >  distanceToEnemy ) {
				closestDistance = distanceToEnemy;
				closestEnemy = ri;
			}
		}
		return closestEnemy;
	}
	
	/**
	 * Senses all ally robots in given squared range.
	 * @param sqrRange squared range
	 * @return array of ally robots
	 */
	public RobotInfo[] getAlliesInRange(int sqrRange) {
		return rc.senseNearbyRobots(sqrRange, myTeam);
	}
	
	/**
	 * Senses all ally robots in given squared range with given type.
	 * @param sqrRange squared range
	 * @param type type of robot
	 * @return array of ally robots
	 */
	public RobotInfo[] getAlliesInRange(int sqrRange, RobotType type) {
		RobotInfo[] robots = getAlliesInRange(sqrRange);
		List<RobotInfo> typeRobots = new LinkedList<>();
		for (RobotInfo ri : robots) {
			if (ri.type == type) {
				typeRobots.add(ri);
			}
		}
		return typeRobots.toArray(new RobotInfo[typeRobots.size()]);
	}
	
	/**
	 * Returns all map locations in the sensing range of this robot.
	 * @return array of map locations in range
	 */
	public MapLocation[] getSurroundingLocations() {
		return MapLocation.getAllMapLocationsWithinRadiusSq(
				rc.getLocation(),
				rc.getType().sensorRadiusSquared
		);
	}
	
	/**
	 * Senses robot with given ID.
	 * @param id ID of the robot
	 * @return robot with given ID if it exists and is in sensing range, null otherwise
	 */
	public RobotInfo getRobot(int id) {
		try {
			return rc.senseRobot(id);
		} catch (GameActionException e) {
			return null;
		}
	}
	
	public MapLocation findSpotForBuilding() throws GameActionException {
		MapLocation loc = rc.getLocation();
		MapLocation[] potLoc = getSurroundingLocations();
		
		int score = Integer.MIN_VALUE;
		MapLocation best = null;
		for (MapLocation l : potLoc) {
			if (isNormal(l) && !isTaken(l)) {
				int s = 0;
				for (Direction d : directions) {
					MapLocation a = l.add(d);
					if (!isTaken(a) && isNormal(a)) {
						s++;
					}
				}
				s *= s;
				s += l.distanceSquaredTo(theirHQ) - loc.distanceSquaredTo(theirHQ);
				s -= l.distanceSquaredTo(loc);
				if (s > score) {
					score = s;
					best = l;
				}
			}
		}
		return best;
	}
	



	/**
	 * Check whether a location is taken by a robot.
	 * @param loc location to check
	 * @return true if it taken or out of sensing range, false otherwise
	 * @throws GameActionException
	 */
	public boolean isTaken(MapLocation loc) {
		try {
			return rc.senseRobotAtLocation(loc) != null;
		} catch (GameActionException e) {
			return true;
		}
	}
	
	/**
	 * Checks whether a location is a normal tile.
	 * @param loc location to check
	 * @return true if it is a normal tile, false otherwise
	 */
	public boolean isNormal(MapLocation loc) {
		return rc.senseTerrainTile(loc) == TerrainTile.NORMAL;
	}
	
	
	
	// MAP BLOCK
	
	/**
	 * Decides whether location is corner or not.
	 * @param loc location to check
	 * @return true if is corner, false otherwise
	 */
	public boolean isCorner(MapLocation loc) {
		boolean n = rc.senseTerrainTile(loc.add(Direction.NORTH)) == TerrainTile.OFF_MAP;
		boolean s = rc.senseTerrainTile(loc.add(Direction.SOUTH)) == TerrainTile.OFF_MAP;
		boolean e = rc.senseTerrainTile(loc.add(Direction.EAST)) == TerrainTile.OFF_MAP;
		boolean w = rc.senseTerrainTile(loc.add(Direction.WEST)) == TerrainTile.OFF_MAP;
		TerrainTile curr = rc.senseTerrainTile(loc);
		
		return (n && e || n && w || s && e || s && w)
				&& (curr == TerrainTile.NORMAL || curr == TerrainTile.VOID);
	}
	
	public boolean isRotationSym() {
		return myHQ.x != theirHQ.x && myHQ.y != theirHQ.y;
	}
	
	public boolean isHorizontalSym() {
		return myHQ.x == theirHQ.x;
	}
	
	public boolean isVerticalSym() {
		return myHQ.y == theirHQ.y;
	}
	
	public MapLocation[] corners(int height, int width, int tlx, int tly) {
		MapLocation tl = new MapLocation(tlx, tly);
		MapLocation tr = tl.add(Direction.EAST, width-1);
		MapLocation bl = tl.add(Direction.SOUTH, height-1);
		MapLocation br = bl.add(Direction.EAST, width-1);
		return new MapLocation[] {tl, tr, bl, br};
	}
	

	
	// MORE COMPLEX ACTIONS
	
	/**
	 * Attack enemy with least health if there is one
	 * @param enemies array of enemies
	 * @throws GameActionException should not be thrown
	 */
	public void attackLeastHealthEnemies(RobotInfo[] enemies) throws GameActionException {
		if (enemies.length == 0) {
			return;
		}
		
		// Find weakest
		double minHealth = Integer.MAX_VALUE;
		MapLocation toAttack = null;
		for (RobotInfo info : enemies) {
			if (info.health < minHealth) {
				toAttack = info.location;
				minHealth = info.health;
			}
		}
		rc.attackLocation(toAttack);
	}
	
	/**
	 * Shoots the weakest enemy in range. Finds all enemies in sensing range and
	 * tries to attack the one with lowest health.
	 */
	public void tryShootWeakest() throws GameActionException {
		RobotInfo[] nearbyEnemies = getNearbyEnemies();
		if (nearbyEnemies.length > 0) {
			
			double minHealth = Integer.MAX_VALUE;
			RobotInfo weakestRobot = null;
			// Find weakest robot
			for (RobotInfo info : nearbyEnemies) {
				if (info.health < minHealth) {
					weakestRobot = info;
					minHealth = info.health;
				}
			}
			
			tryAttack(weakestRobot.location);
		}
	}
	
	public Direction[] bugPlanning(MapLocation target) throws GameActionException {
		MapLocation loc = rc.getLocation();
		Direction dir = loc.directionTo(target);
		for (int i = 0; i < 8; i++) {
			if (!rc.canMove(dir)) {
				dir.rotateLeft();
			} else {
				break;
			}
		}
		List<Direction> moves = new LinkedList<>();
		
		int d = 0;
		
		
		while (true) {
			if (dir == loc.directionTo(target) || dir == null || d > 8) {
				break;
			}
			moves.add(dir);
			
			
			loc = loc.add(dir);
			
			Direction right = dir.rotateRight().rotateRight();
			while (true) {
				MapLocation newLoc = loc.add(right);
				TerrainTile tile = rc.senseTerrainTile(loc);
				if (isTaken(newLoc) || tile == TerrainTile.OFF_MAP) {
					right.rotateLeft();
				} else {
					break;
				}
			}
			dir = right;//directionLeftTowards(tloc);
			d++;
		}
		return moves.toArray(new Direction[moves.size()]);
	}

	/**
	 * I have to admit, I have no idea what this is.
	 */
	public void moveAround() {

	}

	/**
	 * Sends supply to the ally robot in range which has lowest supply. After transfer
	 * is done, both robots have the same amount of supplies.
	 */
	public void transferSuppliesTolowest() throws GameActionException {
		if (Clock.getRoundNum() % 10 != 0 || !isInSupplyChain(rc.getType())) {
			return;
		}
		
		RobotInfo[] nearbyAllies =
				getAlliesInRange(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED); 
		
		double lowestSupply = rc.getSupplyLevel();
		double transferAmount = 0;
		MapLocation supplyTarget = null;
		for (RobotInfo ri : nearbyAllies) {
			if (ri.supplyLevel < lowestSupply && isInSupplyChain(ri.type)) {
				lowestSupply = ri.supplyLevel;
				transferAmount = (rc.getSupplyLevel() - lowestSupply) / 2;
				supplyTarget = ri.location;
			}
		}

		if (supplyTarget != null) {
			//System.out.println(rc.getSupplyLevel() + " " + lowestSupply);
			rc.transferSupplies((int) transferAmount, supplyTarget);
		}
	}
	
	private boolean isInSupplyChain(RobotType type) {
		return !type.isBuilding || type == RobotType.TOWER;
	}
	
	protected MapLocation closestOre() throws GameActionException {
		//System.out.println("before " + Clock.getBytecodeNum());
		MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(
				rc.getLocation(),
				50
		);
		
		MapLocation oreLoc = null;
		int dist = Integer.MAX_VALUE;
		for (MapLocation loc : locations) {
			if (rc.senseTerrainTile(loc) == TerrainTile.NORMAL && rc.senseOre(loc) > 0
					&& rc.getLocation().distanceSquaredTo(loc) < dist
					&& rc.senseRobotAtLocation(loc) == null) {
				oreLoc = loc;
				dist = rc.getLocation().distanceSquaredTo(loc);
			}
		}
		//System.out.println("after " + Clock.getBytecodeNum());
		return oreLoc;
		
		/*System.out.println(Clock.getBytecodeNum());
		final MapLocation curr = rc.getLocation();
		Set<MapLocation> visited = new HashSet<>();
		List<MapLocation> open = new LinkedList<>();
		open.add(curr);
		
		int i = 0;
		while (!open.isEmpty()) {
			i++;
			MapLocation loc = open.remove(0);
			visited.add(loc);
			if (rc.senseOre(loc) > 0.5) {
				System.out.println(i + " " + Clock.getBytecodeNum());
				return loc;
			}
			for (MapLocation l : succ(loc)) {
				if (visited.contains(l)) {
					continue;
				}
				open.add(l);
			}
		}*/
	}
	
	protected List<MapLocation> succ(MapLocation loc) {
		List<MapLocation> adjacent = new LinkedList<>();
		for (Direction dir : directions) {
			MapLocation adj = loc.add(dir);
			if (rc.senseTerrainTile(adj) == TerrainTile.NORMAL) {
				adjacent.add(loc.add(dir));
			}
		}
		return adjacent;
	}
	
}
