package T102;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class BaseBot {

	// All 8 directions in one place
	protected static final Direction[] directions = { Direction.NORTH,
			Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH,
			Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST };

	
	// Current robot
	protected RobotController rc;
	
	// Locations of both HQs
	protected MapLocation myHQ, theirHQ;
	
	// References to both teams
	protected Team myTeam, theirTeam;
	
	// Random number generator
	private Random rand;

	
	/**
	 * Sets HQ locations, teams and initializes random generator.
	 * @param rc
	 */
	public BaseBot(RobotController rc) {
		this.rc = rc;
		this.myHQ = rc.senseHQLocation();
		this.theirHQ = rc.senseEnemyHQLocation();
		this.myTeam = rc.getTeam();
		this.theirTeam = this.myTeam.opponent();
		this.rand = new Random(rc.getID());
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
	 * Tries to shoot the given location. Helps to avoid performing check every time.
	 * @param location location to shoot
	 * @return true if shot, false otherwise
	 * @throws GameActionException should not be thrown
	 */
	protected boolean tryShoot(MapLocation location) throws GameActionException {
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
	public Direction getRandomDirection() {
		return directions[rand.nextInt(8)];
	}

	/**
	 * Generates direction directly away from target location.
	 * @param target target location
	 * @return direction away
	 */
	public Direction getDirectionAwayFrom(MapLocation target) {
		return rc.getLocation().directionTo(target).opposite();
	}
	
	/**
	 * Returns all 8 direction but the ones towards the target come first in the array.
	 * Direction directly towards the target is then at index 0, and opposite direction
	 * is at index 7.
	 * @param target target location
	 * @return array of 8 directions
	 */
	protected Direction[] getAllDirectionTowards(MapLocation target) {
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
	protected Direction[] getDirectionTowards(MapLocation target) {
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

	
	
	// SPECIFIC DIRECTION BLOCK
	
	/**
	 * Returns direction towards the target that is possible to perform.
	 * @param target target location
	 * @return direction if possible to move, null otherwise
	 */
	public Direction getMoveDirection(MapLocation target) {
		Direction[] dirs = getDirectionTowards(target);
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
		for (Direction dir : getAllDirectionTowards(theirHQ)) {
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
		for (Direction dir : getAllDirectionTowards(myHQ)) {
			if (rc.canBuild(dir, type)) {
				return dir;
			}
		}
		return null;
	}
	
	/**
	 * Attempt to spawn a robot of given type.
	 * @param type The type of robot to spawn
	 * @throws GameActionException
	 */
	public void tryToSpawn(RobotType type) throws GameActionException {
		if (type != null) {
			Direction newDir = getSpawnDirection(type);
			if (newDir != null) {
				rc.spawn(newDir, type);
			}
		}
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
	 * Senses all enemy robots in radius of this robot.
	 * @return array of enemy robots
	 */
	public RobotInfo[] getNearbyEnemies() {
		return rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, theirTeam);
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

	public void moveAround() {

	}
	
	private void transferSuppliesTolowest() throws GameActionException {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
				GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
		double lowestSupply = rc.getSupplyLevel();
		double transferAmount = 0;
		MapLocation supplyTarget = null;
		for (RobotInfo ri : nearbyAllies) {
			if (ri.supplyLevel < lowestSupply) {
				lowestSupply = ri.supplyLevel;
				transferAmount = (rc.getSupplyLevel() - lowestSupply) / 2;
				supplyTarget = ri.location;
			}
		}
		if (supplyTarget != null) {
			rc.transferSupplies((int) transferAmount, supplyTarget);
		}

	}
	
	
	
	// RUN

	/**
	 * Called at the beginning of the turn
	 * @throws GameActionException
	 */
	public void beginingOfTurn() throws GameActionException {

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
}
