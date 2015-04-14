package player1;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

/**
 * Base class for all bots.
 */
public abstract class BaseBot {
	
	protected static final Direction[] directions = {
		Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
		Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST
	};

	// Robot controller to use in derived classes
	protected RobotController rc;
	
	// Teams
	protected Team myTeam;
	protected Team otherTeam;
	
	// HQ locations
	protected MapLocation myHQ;
	protected MapLocation otherHQ;
	
	// Random number generator
	private Random rand;
	
	
	/** Constructor to set robot controller. */
	public BaseBot(RobotController rc) {
		super();
		this.rc = rc;
		this.myTeam = rc.getTeam();
		this.otherTeam = this.myTeam.opponent();
		this.myHQ = rc.senseHQLocation();
		this.otherHQ = rc.senseEnemyHQLocation();
		this.rand = new Random();
	}
	
	/**
	 * Execute whatever a bot needs to do. 
	 * @throws GameActionException when impossible to perform action
	 * */
	public abstract void execute() throws GameActionException;

	
	/**
	 * Tries to spawn the bot with given type and direction. Function just calls
	 * isCoreReady and canSpawn and helps to avoid all unnecessary code.
	 * @param dir direction for spawning the bot
	 * @param type type fo the bot to spawn
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
	
	/**
	 * Senses all enemy robots in radius of this robot.
	 * @return enemy robots
	 */
	protected RobotInfo[] senseNearbyEnemies() {
		return rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, otherTeam);
	}
	
	/**
	 * Senses all ally robots in radius of this robot.
	 * @return array of robots
	 */
	protected RobotInfo[] senseNearbyAllies() {
		return rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, myTeam);
	}
	
	/**
	 * Senses all ally robots in given radius.
	 * @param sqrDistance squared radius
	 * @return allied robots in range
	 */
	protected RobotInfo[] senseNearbyAllies(int sqrDistance) {
		return rc.senseNearbyRobots(sqrDistance, myTeam);
	}
	
	/**
	 * Generates random direction.
	 * @return direction
	 */
	protected Direction randomDirection() {
		int i = rand.nextInt(directions.length);
		return directions[i];
	}
	
	
	// Next functions are more complex strategies for bots to use 
	
	/**
	 * Shoots the weakest enemy in range.
	 * @throws GameActionException if not possible to shoot
	 */
	protected void tryShootWeakest() throws GameActionException {
		RobotInfo[] nearbyEnemies = senseNearbyEnemies();
		if (nearbyEnemies.length > 0) {
			// Find weakest robot
			RobotInfo weakestRobot = Collections.min(
					Arrays.asList(nearbyEnemies),
					new Comparator<RobotInfo>() {

				@Override
				public int compare(RobotInfo o1, RobotInfo o2) {
					return (int) (o1.health - o2.health);
				}
			});
			
			tryShoot(weakestRobot.location);
		}
	}

	protected boolean tryMoveTo(MapLocation loc) throws GameActionException {
		Direction dir = rc.getLocation().directionTo(loc);
		if (tryMove(dir)) { return true; }
		
		Direction left = dir.rotateLeft(), right = dir.rotateRight();
		for (int i = 0; i < 4; i++) {
			if (tryMove(left)) 	{ return true; }
			if (tryMove(right)) { return true; }
			left = left.rotateLeft();
			right = right.rotateRight();
		}
		
		return false;
	}
	
	protected boolean trySpawn(RobotType type) throws GameActionException {
		for (Direction dir : directions) {
			if (trySpawn(dir, type)) {
				return true;
			}
		}
		return false;
	}
	
	protected MapLocation closestOre() {
		final MapLocation curr = rc.getLocation();
		Set<MapLocation> visited = new HashSet<>();
		PriorityQueue<MapLocation> open = new PriorityQueue<>(
				20,
				new Comparator<MapLocation>() {

					@Override
					public int compare(MapLocation o1, MapLocation o2) {
						return o1.distanceSquaredTo(curr) - o2.distanceSquaredTo(curr);
					}
		});
		open.offer(curr);
		
		while (!open.isEmpty()) {
			MapLocation loc = open.poll();
			visited.add(loc);
			if (rc.senseOre(loc) > 0.5) {
				return loc;
			}
			for (MapLocation l : succ(loc)) {
				if (visited.contains(l)) {
					continue;
				}
				open.offer(l);
			}
		}
		return null;
	}
	
	protected MapLocation[] succ(MapLocation loc) {
		MapLocation[] adjacent = MapLocation.getAllMapLocationsWithinRadiusSq(loc, 2);
		List<MapLocation> adjValid = new LinkedList<>();
		for (MapLocation l : adjacent) {
			if (rc.senseTerrainTile(l) == TerrainTile.NORMAL) {
				adjValid.add(l);
			}
		}
		return adjValid.toArray(new MapLocation[adjValid.size()]);
	}
	
}
