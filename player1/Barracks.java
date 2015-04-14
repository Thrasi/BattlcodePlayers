package player1;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Barracks extends BaseBot {
	
	private static int count = 0;

	public Barracks(RobotController rc) {
		super(rc);
	}

	@Override
	public void execute() throws GameActionException {
		trySpawn(RobotType.SOLDIER);
		//}
		rc.yield();
	}

}
