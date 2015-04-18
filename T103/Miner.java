package T103;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Miner extends BaseBot {

	private MapLocation oreLoc;
	
	public Miner(RobotController rc) {
		super(rc);
		this.oreLoc = null;
	}
	
	@Override
	public void execute() throws GameActionException {
		if (rc.getSupplyLevel() < 200) {
			MapLocation lastLocation = rc.getLocation();
			rc.setIndicatorString(1, lastLocation + "");
			while (rc.getLocation().distanceSquaredTo(myHQ) > GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
				tryMoveTo(myHQ);
				rc.yield();
			}
			while (rc.getSupplyLevel() < 200) {
				rc.yield();
			}
			rc.setIndicatorString(2, lastLocation+" " + rc.getLocation().distanceSquaredTo(lastLocation));
			while (rc.getLocation().distanceSquaredTo(lastLocation) > 2) {
				rc.setIndicatorString(0, lastLocation + " " + rc.getLocation().distanceSquaredTo(lastLocation));
				tryMoveTo(lastLocation);
				rc.yield();
			}
		}
		if (rc.senseOre(rc.getLocation()) > 0) {
			oreLoc = null;
			tryMine();
		} else {
			oreLoc = closestOre();
			if (oreLoc == null) {
				tryMoveTo(rc.getLocation().add(getRandomDirection()));
			} else {
				tryMoveTo(oreLoc);
			}
		}
		rc.yield();
	}

}
