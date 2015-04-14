package player1;

import static player1.Channels.*;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Beaver extends BaseBot {
	
	private static final int MINER_FACTORIES = 1;
	private static final int BARRACKS = 3;
	private static final int TECH_INSTITUTES = 0;
	int count = 0;
	
	public Beaver(RobotController rc) {
		super(rc);
	}

	@Override
	public void execute() throws GameActionException {
		tryShootWeakest();
		
		if (count == 0) {
			//if (tryBuild(randomDirection(), RobotType.TANKFACTORY)) {
			//	count = 1;
			//}
		}
		
		int minerFactoryCount = rc.readBroadcast(MINER_FACTORY_CHANNEL);
		int barracksCount = rc.readBroadcast(BARRACKS_CHANNEL);
		int techInstituteCount = rc.readBroadcast(TECH_INSTITUTE_CHANNEL);
		if (minerFactoryCount < MINER_FACTORIES) {
			if (tryBuild(randomDirection(), RobotType.MINERFACTORY)) {
				rc.broadcast(MINER_FACTORY_CHANNEL, minerFactoryCount+1);
			}
		} else if (barracksCount < BARRACKS) {
			if (tryBuild(randomDirection(), RobotType.BARRACKS)) {
				rc.broadcast(BARRACKS_CHANNEL, barracksCount+1);
			}
		} else if (techInstituteCount < TECH_INSTITUTES) {
			if (tryBuild(randomDirection(), RobotType.TECHNOLOGYINSTITUTE)) {
				rc.broadcast(TECH_INSTITUTE_CHANNEL, techInstituteCount+1);
			}
		}
		
		if (rc.getLocation().distanceSquaredTo(myHQ) < 4) {
			Direction fromHQ = myHQ.directionTo(rc.getLocation());
			tryMove(fromHQ);
		}
		
		tryMine();
		
		rc.yield();
	}
	
}
