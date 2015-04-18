package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class TankFactory extends BaseBot {

	public TankFactory(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	
	public void execute() throws GameActionException{
//		if (rc.isCoreReady() && rc.getTeamOre() > 250){
//			RobotType type = null;
//			if ( rc.readBroadcast(RobotPlayer.numTANKS) < RobotPlayer.MAXTANKS ) {
//				type = RobotType.TANK;
//			}
//			trySpawn(type);
//		}
		trySpawn(RobotType.TANK);
		rc.yield();
	}

}
