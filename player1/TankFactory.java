package player1;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class TankFactory extends BaseBot {

	public TankFactory(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws GameActionException {
		trySpawn(randomDirection(), RobotType.TANK);

	}

}
