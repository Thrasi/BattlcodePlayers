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
	
	protected static final Direction[] directions = {
		Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
		Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST
	};
	
	protected RobotController rc;
	protected MapLocation myHQ, theirHQ;
	protected Team myTeam, theirTeam;
	private Random rand;

	public BaseBot(RobotController rc) {
		this.rc = rc;
		this.myHQ = rc.senseHQLocation();
		this.theirHQ = rc.senseEnemyHQLocation();
		this.myTeam = rc.getTeam();
		this.theirTeam = this.myTeam.opponent();
		this.rand = new Random();
	}

	public Direction getRandomDirection() {
		return directions[rand.nextInt(8)];
	}

	public Direction[] getDirectionToward(MapLocation target) {
		Direction toTarget = rc.getLocation().directionTo(target);
		Direction dirs[] = { toTarget, toTarget.rotateLeft(), toTarget.rotateRight(),
				toTarget.rotateLeft().rotateLeft(),
				toTarget.rotateRight().rotateRight() };
		return dirs;
	}

	public Direction getDirectionAwayFrom(MapLocation target) {
		Direction toTarget = rc.getLocation().directionTo(target);
		return toTarget.opposite();
	}

	public Direction getMoveDir(MapLocation target) {
		Direction[] dirs = getDirectionToward(target);
		for (Direction d : dirs) {
			if (rc.canMove(d)) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Attempt to spawn a robot of type.
	 * 
	 * @param type
	 *            The type of robot to spawn
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

	/**
	 * Attempts to return a direction to enemy HQ, then tries the other directions.
	 * 
	 * @param type
	 * @return Direction to spawn to or null if all is occupied.
	 */
	public Direction getSpawnDirection(RobotType type) {
		Direction dir = rc.getLocation().directionTo(this.theirHQ);
		for (int i = 0; i < 8; i++) {
			if (rc.canSpawn(dir, type)) {
				return dir;
			}
			dir = dir.rotateRight();
		}
		return null;
	}

	public Direction getBuildDirection(RobotType type) {
		Direction[] dirs = getDirectionToward(this.myHQ);
		for (Direction d : dirs) {
			System.out.println("type: " + type);
			System.out.println("d: " + d);
			if (rc.canBuild(d, type)) {
				return d;
			}
		}
		return null;
	}

	public RobotInfo[] getAllies() {
		RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, this.myTeam);
		return allies;
	}

	public RobotInfo[] getEnemiesInAttackingRange() {
		RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,
				this.theirTeam);
		return enemies;
	}

	public void attackLeastHealthEnemies(RobotInfo[] enemies)
			throws GameActionException {
		if (enemies.length == 0) {
			return;
		}
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

	public void beginingOfTurn() throws GameActionException {

		if (rc.senseEnemyHQLocation() != null) {
			this.theirHQ = rc.senseEnemyHQLocation();
		}
		transferSupplies();

		//			int supplyLevel = rc.getSupplyLevel();
		//			if (rc.)
	}

	private void transferSupplies() throws GameActionException {
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

	public void execute() throws GameActionException {
		rc.yield();
	}

	public void endOfTurn() {
	}

	public void go() throws GameActionException {
		beginingOfTurn();
		execute();
		endOfTurn();
	}
}
