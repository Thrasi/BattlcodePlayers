package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Computer extends BaseBot {

	public Computer(RobotController rc) {
		super(rc);
	}
	
	@Override
	public void execute() throws GameActionException {
		while (rc.readBroadcast(Channels.expDRONE1DONE) != 1
				|| rc.readBroadcast(Channels.expDRONE2DONE) != 1
				|| rc.readBroadcast(Channels.expDRONE3DONE) != 1
				|| rc.readBroadcast(Channels.expDRONE4DONE) != 1) {
			
			// TODO waiting here
			rc.yield();
		}
		
		MapInfo.reconstructMap();
		
		//System.out.println("Started flooding "
		//+ Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		
		MapInfo.floodAndServe();
		
		//System.out.println("Finished flooding "
		//+ Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		
		// Serve forever
		while (true) {
			MapInfo.serve();
			rc.yield();
		}
	}
	
}
