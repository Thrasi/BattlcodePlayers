package player1;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer {
	

	/**
	 * Skeleton function to run all the shit.
	 * @param rc robot controller
	 */
	public static void run(RobotController rc) {
		RobotType rcType = rc.getType();
		
		BaseBot bot = null;
		if (rcType == RobotType.HQ) {
			bot = new HQ(rc);
		} else if (rcType == RobotType.BEAVER) {
			bot = new Beaver(rc);
		} else if (rcType == RobotType.TOWER) {
			bot = new Tower(rc);
		} else if (rcType == RobotType.BARRACKS) {
			bot = new Barracks(rc);
		} else if (rcType == RobotType.SOLDIER) {
			bot = new Soldier(rc);
		} else if (rcType == RobotType.MINERFACTORY) {
			bot = new MinerFactory(rc);
		} else if (rcType == RobotType.MINER) {
			bot = new Miner(rc);
		} else if (rcType == RobotType.TANKFACTORY) {
			bot = new TankFactory(rc);
		} else if (rcType == RobotType.TANK) {
			bot = new Tank(rc);
		} else if (rcType == RobotType.TECHNOLOGYINSTITUTE) {
			bot = new TechnologyInstitute(rc);
		}
		
		while (true) {
			try {
				bot.execute();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}

}
