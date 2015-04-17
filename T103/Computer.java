package T103;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import static T103.Channels.isSet;

public class Computer extends BaseBot {

	public Computer(RobotController rc) {
		super(rc);
	}
	
	@Override
	public void execute() throws GameActionException {
		while (!allDone()) {
			
			// TODO waiting here
			rc.yield();
		}
		
		MapInfo.reconstructMap();
		
		System.out.println("Started flooding "
				+ Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		
		MapInfo.floodAndServe();
		
		System.out.println("Finished flooding "
				+ Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		
		// Serve forever
		while (true) {
			MapInfo.serve();
			rc.yield();
		}
	}
	
	private static boolean allDone() throws GameActionException {
		int count = rc.readBroadcast(Channels.expDRONECOUNT);
		for (int i = count-1; i >= 0; i--) {
			if (!isSet(Channels.expDRONEDONE + i)) {
				return false;
			}
		}
		return true;
	}
	
}
