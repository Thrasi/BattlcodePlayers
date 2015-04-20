package T103;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Harasser extends BaseBot {

	public Harasser(RobotController rc) {
		super(rc);
	}
	
	public void execute() throws GameActionException {
//		tryMoveAway();
		RobotInfo[] enemies = getNearbyEnemies();
		
		
		RobotInfo closestMissile = null;
		double distToMissile = Double.MAX_VALUE;
		RobotInfo closestLauncher = null;
		double distToLauncher = Double.MAX_VALUE;
		for (RobotInfo ri : enemies) {
			if (ri.type == RobotType.MISSILE) {
				if (ri.location.distanceSquaredTo(rc.getLocation()) < distToMissile) {
					distToMissile = ri.location.distanceSquaredTo(rc.getLocation());
					closestMissile = ri;
				}
			} 
			else if (ri.type == RobotType.LAUNCHER) {
				if (ri.location.distanceSquaredTo(rc.getLocation()) < distToLauncher) {
					distToLauncher = ri.location.distanceSquaredTo(rc.getLocation());
					closestLauncher = ri;
				}
			}
		}
		
		if (closestMissile != null) {
			tryMoveTo(rc.getLocation().add( 
					closestMissile.location.directionTo(rc.getLocation()) ) );
		} 
		else if (closestLauncher != null) {
			if ( distToLauncher < 24 ) {
				tryMoveTo(rc.getLocation().add( 
						closestLauncher.location.directionTo(rc.getLocation()) ) );
			} else {
				tryMoveTo(closestLauncher.location );
			}
		}
		else {
			tryMoveTo(theirHQ);
		}
		rc.yield();
	}
	
	public static boolean tryMoveTo(MapLocation loc) throws GameActionException {
		
		MapLocation myloc = rc.getLocation();
		
		for (Direction dir : getDirectionsTowards(loc)) {
			MapLocation ml = myloc.add(dir);
			if ( isInRangeOfEnemies(ml) ) {
				continue;
			}
			if ( isInRangeOfTowers(ml) ) {
				continue;
			}
			if (tryMove(dir)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInRangeOfEnemies(MapLocation ml) {
		RobotInfo[] enemies = getNearbyEnemies();
		for (RobotInfo enemy : enemies) {
			if (ml.distanceSquaredTo(enemy.location) <= enemy.type.attackRadiusSquared) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInRangeOfTowers(MapLocation ml) {
		MapLocation[] towers = rc.senseEnemyTowerLocations();
		for (MapLocation tower : towers) {
			if (ml.distanceSquaredTo(tower) <= RobotType.TOWER.attackRadiusSquared) {
				return true;
			}
		}
		return false;
	}

}
