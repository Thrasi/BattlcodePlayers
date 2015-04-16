package T102;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

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
		
		int height = rc.readBroadcast(RobotPlayer.MAPHEIGHT);
		int width = rc.readBroadcast(RobotPlayer.MAPWIDTH);
		int xs = rc.readBroadcast(RobotPlayer.TOPLEFTX);
		int ys = rc.readBroadcast(RobotPlayer.TOPLEFTY);
		
		double xc = (myHQ.x + theirHQ.x) / 2.0;
		double yc = (myHQ.y + theirHQ.y) / 2.0;
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				MapLocation loc = new MapLocation(x + xs, y + ys);
				if (loc.distanceSquaredTo(myHQ) <= loc.distanceSquaredTo(theirHQ)) {
					TerrainTile tile = rc.senseTerrainTile(loc);
					if (tile == TerrainTile.NORMAL) {
						rc.broadcast(RobotPlayer.MAPFIRST + y * width + x, 1);
					}
				} else {
					if (isRotationSym()) {
						
						double xdiff = loc.x - xc;
						double ydiff = loc.y - yc;
						MapLocation l = new MapLocation((int) (xc - xdiff), (int) (yc - ydiff));
						//System.out.println(l);
						TerrainTile tile = rc.senseTerrainTile(l);
						if (tile == TerrainTile.NORMAL) {
							rc.broadcast(RobotPlayer.MAPFIRST + y * width + x, 1);
						}
					}
				}
			}
		}
		
		set(RobotPlayer.MAPBROADCASTED);
		rc.yield();
		
		
		while (!isSet(RobotPlayer.FLOODSET)) {
			//TODO waiting here
			rc.yield();
		}
		
		// here flood
		int xStart = rc.readBroadcast(RobotPlayer.FLOODX);
		int yStart = rc.readBroadcast(RobotPlayer.FLOODY);
		rc.yield();
		
		
		while (true) {
			rc.yield();
		}
	}

}
