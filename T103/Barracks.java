package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Barracks extends BaseBot {

	private static int maxTANKFACTORIES;
	
	public Barracks(RobotController rc) {
		super(rc);
		maxTANKFACTORIES = HQ.maxTANKFACTORIESC[mapClass]; 
	}
	
	@Override
	public void execute() throws GameActionException {
		if (rc.readBroadcast(Channels.numTANKFACTORY) < maxTANKFACTORIES) {
			tryBuild(RobotType.TANKFACTORY);
		} else {
			trySpawn(RobotType.SOLDIER);
		}
		rc.yield();
	}

}
