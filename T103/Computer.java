package T103;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;
import static T103.BaseBot.rc;
import static T103.MapInfo.map;

public class Computer extends BaseBot {

	public Computer(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
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
		rc.yield();
		
		
		//TODO this is for testing
		//rc.broadcast(RobotPlayer.FLOODX, theirHQ.x);
		//rc.broadcast(RobotPlayer.FLOODY, theirHQ.y);
		//set(RobotPlayer.FLOODSET);
		MapInfo.setQueue(theirHQ);
		
//		while (!isSet(RobotPlayer.FLOODSET) && !isSet(RobotPlayer.MAPBROADCASTED)) {
//			//TODO waiting here
//			rc.yield();
//		}
		
		
		
		System.out.println("started " + Clock.getBytecodeNum() + " " +  Clock.getRoundNum());
		// here flood
		rc.broadcast(RobotPlayer.FLOODINDEX, 0);
		rc.broadcast(RobotPlayer.FLOODREQUEST, 1);
		MapInfo.floodAndServe();
		//reset(RobotPlayer.FLOODSET);
		
		
		System.out.println("finished " + Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		rc.yield();
		
		// TODO there should be while (true) serve
		MapInfo.serve();
		
		while (!MapInfo.isActive(0)) {
			rc.yield();
		}
		
		while (true) {
			MapInfo.markFloodFromChannels(0);
			rc.yield();
		}
	}
	
}
