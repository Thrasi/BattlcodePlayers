package T103;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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

	//TODO isTaken change to affect only allies, check with HQ

	// All 8 directions in one place
	public static final Direction[] directions = { Direction.NORTH,
			Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH,
			Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST };

	// Current robot
	public static RobotController rc;

	// Locations of both HQs
	public static MapLocation myHQ, theirHQ;

	// References to both teams
	public static Team myTeam, theirTeam;

	// Random number generator
	public static Random rand;
	static boolean isSupplyLow = false;
	
	public static int mapClass;

	/**
	 * Sets HQ locations, teams and initializes random generator.
	 * @param rc this robot's controller
	 */
	public BaseBot(RobotController rc) {
		BaseBot.rc = rc;
		BaseBot.myHQ = rc.senseHQLocation();
		BaseBot.theirHQ = rc.senseEnemyHQLocation();
		BaseBot.myTeam = rc.getTeam();
		BaseBot.theirTeam = BaseBot.myTeam.opponent();
		BaseBot.rand = new Random(rc.getID());
		int estSize = myHQ.distanceSquaredTo(theirHQ);
		if (estSize < 1000) {
			mapClass = 0;
		} else if (estSize < 3000) {
			mapClass = 1;
		} else {
			mapClass = 2;
		}
	}

	// RUN

	/**
	 * Called at the beginning of the turn. Just transfers supplies to the robot with lowest amount
	 * of supplies.
	 * @throws GameActionException
	 */
	public void beginingOfTurn() throws GameActionException {
		//if (rc.getType() == RobotType.SOLDIER) {
		//rc.broadcast(RobotPlayer.numSOLDIERS, rc.readBroadcast(RobotPlayer.numSOLDIERS)+1);
		//}
//		if (rc.senseEnemyHQLocation() != null) {
//			BaseBot.theirHQ = rc.senseEnemyHQLocation();
//		}
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
	 * Tries to spawn the bot with given type and direction. Function just calls isCoreReady and
	 * canSpawn and helps to avoid all unnecessary code.
	 * @param dir direction for spawning the bot
	 * @param type type for the bot to spawn
	 * @return true if spawned, false otherwise
	 * @throws GameActionException should not be thrown
	 */
	public static boolean trySpawn(Direction dir, RobotType type) throws GameActionException {
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
	public static boolean tryMove(Direction dir) throws GameActionException {
		if (rc.isCoreReady() && rc.canMove(dir)) {
			rc.move(dir);
			return true;
		}
		return false;
	}

	/**
	 * Tries to build the certain building in given direction. Helps to avoid performing check every
	 * time.
	 * @param dir direction in which to build
	 * @param type type of building
	 * @return true if built, false otherwise
	 * @throws GameActionException should not be thrown
	 */
	protected static boolean tryBuild(Direction dir, RobotType type) throws GameActionException {
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
	 * Returns all 8 direction but the ones towards the target come first in the array. Direction
	 * directly towards the target is then at index 0, and opposite direction is at index 7.
	 * @param target target location
	 * @return array of 8 directions
	 */
	public static Direction[] getAllDirectionsTowards(MapLocation target) {
		Direction toTarget = rc.getLocation().directionTo(target);
		Direction dirs[] = { toTarget, toTarget.rotateLeft(), toTarget.rotateRight(),
				toTarget.rotateLeft().rotateLeft(), toTarget.rotateRight().rotateRight(),
				toTarget.rotateLeft().rotateLeft().rotateLeft(),
				toTarget.rotateRight().rotateRight().rotateRight(), toTarget.opposite() };
		return dirs;
	}

	/**
	 * Returns 5 directions that will still lead towards the target location.
	 * @param target target location
	 * @return array of 5 directions
	 */
	protected static Direction[] getDirectionsTowards(MapLocation target) {
		Direction toTarget = rc.getLocation().directionTo(target);
		Direction dirs[] = { toTarget, toTarget.rotateLeft(), toTarget.rotateRight(),
				toTarget.rotateLeft().rotateLeft(), toTarget.rotateRight().rotateRight() };
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
	 * Tries to move in any direction but checking the ones that are towards the target first.
	 * @param loc target location
	 * @return true if moved, false if everything is occupied
	 */
	public static boolean tryMoveTo(MapLocation loc) throws GameActionException {
		for (Direction dir : getDirectionsTowards(loc)) {
			if (tryMove(dir)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean tryPrimitiveMoveTo(MapLocation target) throws GameActionException {
		Direction dir = rc.getLocation().directionTo(target);
		if (tryMove(dir)) {
			return true;
		}
		if (tryMove(dir.rotateLeft())) {
			return true;
		}
		if (tryMove(dir.rotateRight())) {
			return true;
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
	public static boolean tryBuild(RobotType type) throws GameActionException {
		for (Direction dir : directions) {
			if (tryBuild(dir, type)) {
				return true;
			}
		}
		return false;
	}
	
	public static Direction tryBuildDir(RobotType type) throws GameActionException {
		for (Direction dir : directions) {
			if (tryBuild(dir, type)) {
				return dir;
			}
		}
		return null;
	}

	// SENSING ROBOTS

	/**
	 * Senses all allies.
	 * @return array of ally robots
	 */
	public static RobotInfo[] getAllAllies() {
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
	public static RobotInfo[] getNearbyEnemies() {
		return rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, theirTeam);
	}

	/**
	 * Senses the closest enemy robot in sensing radius of the robot.
	 * @return closest enemy within sensing range
	 */
	public RobotInfo getNearestNearByEnemy() {
		double closestDistance = Double.MAX_VALUE - 1;
		double distanceToEnemy;
		RobotInfo closestEnemy = null;
		for (RobotInfo ri : getNearbyEnemies()) {
			distanceToEnemy = rc.getLocation().distanceSquaredTo(ri.location);
			if (closestDistance > distanceToEnemy) {
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
	public static MapLocation[] getSurroundingLocations() {
		return MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(),
				rc.getType().sensorRadiusSquared);
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
	
	public static boolean isAlive(int id) {
		try {
			rc.senseRobot(id);
			return true;
		} catch (GameActionException e) {
			return false;
		}
	}
	
	public static int findTarget(MapLocation location) {
		double minHealth = Double.MAX_VALUE;
		int bestID = -1;
		for (RobotInfo ri : rc.senseNearbyRobots(location, 50, theirTeam)) {
			double health = ((double) ri.health) / ri.type.maxHealth;
			if (health < minHealth) {
				minHealth = health;
				bestID = ri.ID;
			}
		}
		return bestID;
	}
	
	/**
	 * Check whether a location is taken by a robot.
	 * @param loc location to check
	 * @return true if it is occupied or out of sensing range, false otherwise
	 */
	public static boolean isOccupied(MapLocation loc) {
		try {
			return rc.isLocationOccupied(loc);
		} catch (GameActionException e) {
			return false;
		}
	}

	/**
	 * Checks whether a location is a normal tile.
	 * @param loc location to check
	 * @return true if it is a normal tile, false otherwise
	 */
	public static boolean isNormal(MapLocation loc) {
		return rc.senseTerrainTile(loc) == TerrainTile.NORMAL;
	}

	


	// MORE COMPLEX ACTIONS
	
	public static boolean tryMoveToEnemy() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(24, theirTeam);
		int minDist = Integer.MAX_VALUE;
		MapLocation loc = null;
		for (RobotInfo ri : enemies) {
			if ( ri.type != RobotType.MISSILE) {
				int dist = rc.getLocation().distanceSquaredTo(ri.location);
				if (dist < minDist) {
					minDist = dist;
					loc = ri.location;
				}
			}
		}
		if (loc == null) {
			return false;
		}
		return tryMoveTo(loc);
	}
	
	public static boolean tryMoveFlood(int idx) throws GameActionException {
		MapLocation curr = rc.getLocation();
		Direction direction = MapInfo.get(idx, curr.x, curr.y);
		return tryMove(direction);
	}
	
	public static void tryMoveAway() throws GameActionException {
		RobotInfo[] enemies = getNearbyEnemies();
		if (enemies.length == 0) {
			return;
		}
		
		int minDist = Integer.MAX_VALUE;
		MapLocation loc = null;
		for (RobotInfo ri : enemies) {
			int dist = rc.getLocation().distanceSquaredTo(ri.location);
			if (dist < minDist) {
				minDist = dist;
				loc = ri.location;
			}
		}
		tryMove(loc.directionTo(rc.getLocation()));
		//rc.yield();
	}
	
	

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
	 * Shoots the weakest enemy in range. Finds all enemies in sensing range and tries to attack the
	 * one with lowest health.  If there are missiles in range the closest of them becomes a priotity
	 * target.
	 */
	public boolean tryShootMissilesOrWeakest() throws GameActionException {
		RobotInfo[] nearbyEnemies = getNearbyEnemies();
		boolean weaponFired = false;
		if (nearbyEnemies.length > 0) {
			
			// Weakest
			double minHealth = Integer.MAX_VALUE;
			RobotInfo weakestRobot = null;
			
			// Missile
			double minDist = Double.MAX_VALUE;
			RobotInfo closestMissile = null;
			
			// Find weakest robot and closest missile
			for (RobotInfo info : nearbyEnemies) {
				if (info.type == RobotType.MISSILE) {
					if (rc.getLocation().distanceSquaredTo(info.location) < minDist) {
						closestMissile = info;
						minDist = info.health;
					}
				}
				else if (info.health/info.type.maxHealth < minHealth) {
					weakestRobot = info;
					minHealth = info.health;
				}
			}
			if (closestMissile != null) {
				weaponFired = tryAttack(closestMissile.location);
			}
			weaponFired = tryAttack(weakestRobot.location);
		}
		return weaponFired;
	}
	
	/**
	 * Check if the supply is low add this robot to the supply queue
	 * @param isSupplyLow
	 * @return isSupplyLow
	 * @throws GameActionException
	 */
	public boolean addToQueue(boolean isSupplyLow) throws GameActionException {
		double supply = rc.getSupplyLevel();
		if (!isSupplyLow && supply <= 500) {
			addToQueue(rc);
            isSupplyLow = true;
		}
        else if (supply > 500) {
            isSupplyLow = false;
        }
		
		return isSupplyLow;
	}
	
	/**
	 * Add the robot to the supply queue
	 * 
	 * @param rc the robot to add to queue
	 * @throws GameActionException
	 */
	public void addToQueue(RobotController rc) throws GameActionException {
        int queueEnd = rc.readBroadcast(Channels.SUPPLYQEND);
        rc.broadcast(queueEnd, rc.getID());

        queueEnd++;
        if (queueEnd >= Channels.UPPERSUPPLYBOUND) {
            queueEnd = Channels.LOWERSUPPLYBOUND;
        }
        rc.broadcast(Channels.SUPPLYQEND, queueEnd);
	}

	public void trySupplyTower() throws GameActionException {
		MapLocation[] towers = rc.senseTowerLocations();
		for (MapLocation t : towers) {
			if (rc.getLocation().distanceSquaredTo(t) > GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
				continue;
			}
			RobotInfo tow = rc.senseRobotAtLocation(t);
			if (tow.supplyLevel < 2000) {
				rc.transferSupplies((int) (2000 - tow.supplyLevel), t);
				return;
			}
		}
	}
	
	/**
	 * I have to admit, I have no idea what this is.
	 */
	public void moveAround() {

	}

	/**
	 * Sends supply to the ally robot in range which has lowest supply. After transfer is done, both
	 * robots have the same amount of supplies.
	 */
	public void transferSuppliesTolowest() throws GameActionException {
		if (Clock.getRoundNum() % 10 != 0 || !isInSupplyChain(rc.getType())) {
			return;
		}

		RobotInfo[] nearbyAllies = getAlliesInRange(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED);

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
		return (!type.isBuilding && type != RobotType.DRONE && type != RobotType.MINER) || type == RobotType.TOWER;
	}

}
