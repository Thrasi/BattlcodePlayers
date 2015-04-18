package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class TankFactory extends BaseBot {

	public TankFactory(RobotController rc) {
		super(rc);
	}
	
	public void execute() throws GameActionException{
		trySpawn(RobotType.TANK);
		rc.yield();
	}

}
