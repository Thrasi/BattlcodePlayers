package T103;

import T103.Utility.Pair;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Miner extends BaseBot {

	private MapLocation oreLoc;
	
	private static double minOre = Mining.MINOREMINER;
	
	public Miner(RobotController rc) {
		super(rc);
		this.oreLoc = null;
	}
	
	@Override
	public void execute() throws GameActionException {
		// Let's make miners not return to hq for supplies
//		if (rc.getSupplyLevel() < 200) {
//			MapLocation lastLocation = rc.getLocation();
//			rc.setIndicatorString(1, lastLocation + "");
//			while (rc.getLocation().distanceSquaredTo(myHQ) > GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
//				tryMoveTo(myHQ);
//				rc.yield();
//			}
//			while (rc.getSupplyLevel() < 200) {
//				rc.yield();
//			}
//			rc.setIndicatorString(2, lastLocation+" " + rc.getLocation().distanceSquaredTo(lastLocation));
//			while (rc.getLocation().distanceSquaredTo(lastLocation) > 2) {
//				rc.setIndicatorString(0, lastLocation + " " + rc.getLocation().distanceSquaredTo(lastLocation));
//				tryMoveTo(lastLocation);
//				rc.yield();
//			}
//		}
		if (rc.senseOre(rc.getLocation()) > minOre) {
			oreLoc = null;
			tryMine();
		} else {
			Pair<MapLocation, Double> data = Mining.closestOre();
			oreLoc = data.x;
			minOre = data.y;
			if (oreLoc == null) {
				tryMoveTo(rc.getLocation().add(getRandomDirection()));
			} else {
				//Movement.setTarget(oreLoc);;
				//Movement.tryBugMove();
				tryMoveTo(oreLoc);
			}
		}
		isSupplyLow = addToQueue(isSupplyLow);
		rc.yield();
	}

}
