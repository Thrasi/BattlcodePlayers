package T103;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Computer extends BaseBot {

	public Computer(RobotController rc) {
		super(rc);
	}
	
	@Override
	public void execute() throws GameActionException {
		while (rc.readBroadcast(RobotPlayer.expDRONE1DONE) != 1
				|| rc.readBroadcast(RobotPlayer.expDRONE2DONE) != 1
				|| rc.readBroadcast(RobotPlayer.expDRONE3DONE) != 1
				|| rc.readBroadcast(RobotPlayer.expDRONE4DONE) != 1) {
			
			// TODO waiting here
			rc.yield();
		}
		
		MapInfo.reconstructMap();
		set(RobotPlayer.MAPBROADCASTED);
		
		System.out.println("Started flooding " + Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		MapInfo.floodAndServe();
		System.out.println("Finished flooding " + Clock.getBytecodeNum() + " " + Clock.getRoundNum());
				
		while (true) {
			MapInfo.serve();
			rc.yield();
		}
	}
	
}
