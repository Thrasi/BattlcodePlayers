package T103;

import battlecode.common.GameActionException;
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
