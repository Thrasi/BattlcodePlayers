package player1;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Miner extends BaseBot {

	private static Direction dir;
	
	public Miner(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws GameActionException {
		MapLocation l = closestOre();
		rc.setIndicatorLine(rc.getLocation(), l, 250, 20, 20);
		rc.setIndicatorDot(l, 250, 250, 8);
		rc.setIndicatorString(0, ""+l);
		
		while (rc.senseOre(rc.getLocation()) > 0) {
			tryMine();
			rc.yield();
		}
		tryMove(randomDirection());
			tryMoveTo(l);
			//tryMoveTo(rc.getLocation().add(myHQ.directionTo(rc.getLocation())));
		rc.yield();
	}

}
