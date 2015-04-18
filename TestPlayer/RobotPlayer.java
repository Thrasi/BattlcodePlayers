package TestPlayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer {

	public static void run(RobotController rc) throws GameActionException {
		if (rc.getType() == RobotType.HQ) {
			int beaverCount = 0;
			while (!rc.isCoreReady() && !rc.canSpawn(Direction.OMNI, RobotType.BEAVER)) {
				rc.yield();
			}
			rc.spawn(Direction.OMNI, RobotType.BEAVER);
		}
		while (true) {
			rc.yield();
		}
	}
}
