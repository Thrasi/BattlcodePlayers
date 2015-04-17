package T103;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Soldier extends BaseBot {

	private static boolean firstWave = false;
	private static boolean secondWave = false;
	private static boolean thirdWave = false;

	public Soldier(RobotController rc) {
		super(rc);
		if (Clock.getRoundNum() > 1200) {
			thirdWave = true;
		} else if (Clock.getRoundNum() > 600) {
			secondWave = true;
		} else {
			firstWave = true;
		}
	}

	@Override
	public void execute() throws GameActionException {
		tryShootWeakest();
		
		if (firstWave && Clock.getRoundNum() > 600) {
			MapLocation[] otherTowers = rc.senseEnemyTowerLocations();
			if (otherTowers.length > 0) {
				tryMoveTo(otherTowers[0]);
			} else {
				tryMoveTo(theirHQ);
			}
		}
		
		if (secondWave && Clock.getRoundNum() > 1200) {
			MapLocation[] otherTowers = rc.senseEnemyTowerLocations();
			if (otherTowers.length > 0) {
				tryMoveTo(otherTowers[0]);
			} else {
				tryMoveTo(theirHQ);
			}
		}
		
		if (thirdWave && Clock.getRoundNum() > 1800) {
			MapLocation[] otherTowers = rc.senseEnemyTowerLocations();
			if (otherTowers.length > 0) {
				tryMoveTo(otherTowers[0]);
			} else {
				tryMoveTo(theirHQ);
			}
		}
		
		MapLocation[] myTowers = rc.senseTowerLocations();
		if (myTowers.length > 0) {
			tryMoveTo(myTowers[0]);
		} else {
			Direction dir = myHQ.directionTo(theirHQ);
			// TODO fix these squared distances
			int dist = (int) (Math.sqrt(myHQ.distanceSquaredTo(theirHQ)) / 3);
			
			tryMoveTo(myHQ.add(dir, dist));
		}
		rc.yield();
	}

}
