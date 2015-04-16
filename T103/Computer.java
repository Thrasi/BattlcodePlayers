package T102;

import battlecode.common.Clock;
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
		
		height = rc.readBroadcast(RobotPlayer.MAPHEIGHT);
		width = rc.readBroadcast(RobotPlayer.MAPWIDTH);
		xs = rc.readBroadcast(RobotPlayer.TOPLEFTX);
		ys = rc.readBroadcast(RobotPlayer.TOPLEFTY);
		
		double xc = (myHQ.x + theirHQ.x) / 2.0;
		double yc = (myHQ.y + theirHQ.y) / 2.0;
		
		int[] mm = new int[width*height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				MapLocation loc = new MapLocation(x + xs, y + ys);
				if (loc.distanceSquaredTo(myHQ) <= loc.distanceSquaredTo(theirHQ)) {
					TerrainTile tile = rc.senseTerrainTile(loc);
					if (tile == TerrainTile.NORMAL) {
						rc.broadcast(RobotPlayer.MAPFIRST + y * width + x, 1);
						mm[y*width+x] = 1;
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
							mm[y*width+x] = 1;
						}
					}
				}
			}
		}
		
		set(RobotPlayer.MAPBROADCASTED);
		rc.yield();
		
		
		/*for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				mm[y*width+x] = rc.readBroadcast(RobotPlayer.MAPFIRST + y*width+x);
			}
		}*/
		
		//TODO this is for testing
		rc.broadcast(RobotPlayer.FLOODX, theirHQ.x);
		rc.broadcast(RobotPlayer.FLOODY, theirHQ.y);
		set(RobotPlayer.FLOODSET);
		
		while (!isSet(RobotPlayer.FLOODSET)) {
			//TODO waiting here
			rc.yield();
		}
		
		System.out.println("started " + Clock.getBytecodeNum() + " " +  Clock.getRoundNum());
		// here flood
		int xStart = rc.readBroadcast(RobotPlayer.FLOODX);
		int yStart = rc.readBroadcast(RobotPlayer.FLOODY);
		
		
		visited = new boolean[width * height];
		visited[(yStart - ys) * width + xStart - xs] = true;
		
		//flood(new MapLocation(xStart-xs, yStart-ys));
		//flood(xStart-xs, yStart-ys);
		
		//List<MapLocation> open = new LinkedList<>();
		//FastLinkedList.add(xStart-xs, yStart-ys);
		int[] xx = new int[width*height];
		int[] yy = new int[width*height];
		
		xx[0] = xStart-xs;
		yy[0] = yStart-ys;
		int i = 0, j = 1;
		
		
		
		while (i < j) {
			int xg = xx[i];
			int yg = yy[i];
			i++;
			
			int x1 = xg-1, y1 = yg-1;
			int x2 = xg+1, y2 = yg+1;
			int x3 = xg,   y3 = yg-1;
			int x4 = xg,   y4 = yg+1;
			int x5 = xg-1, y5 = yg;
			int x6 = xg+1, y6 = yg;
			int x7 = xg+1, y7 = yg-1;
			int x8 = xg-1, y8 = yg+1;
			
			if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height && !visited[y1*width+x1]
					&& mm[y1*width+x1] == 1) {
				visited[y1*width + x1] = true;
				xx[j] = x1;
				yy[j] = y1;
				j++;
			}
			if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height && !visited[y2*width+x2]
					&& mm[y2*width+x2] == 1) {
				visited[y2*width + x2] = true;
				xx[j] = x2;
				yy[j] = y2;
				j++;
			}
			if (x3 >= 0 && x3 < width && y3 >= 0 && y3 < height && !visited[y3*width+x3]
					&& mm[y3*width+x3] == 1) {
				visited[y3*width + x3] = true;
				xx[j] = x3;
				yy[j] = y3;
				j++;
			}
			if (x4 >= 0 && x4 < width && y4 >= 0 && y4 < height && !visited[y4*width+x4]
					&& mm[y4*width+x4] == 1) {
				visited[y4*width + x4] = true;
				xx[j] = x4;
				yy[j] = y4;
				j++;
			}
			if (x5 >= 0 && x5 < width && y5 >= 0 && y5 < height && !visited[y5*width+x5]
					&& mm[y5*width+x5] == 1) {
				visited[y5*width + x5] = true;
				xx[j] = x5;
				yy[j] = y5;
				j++;
			}
			if (x6 >= 0 && x6 < width && y6 >= 0 && y6 < height && !visited[y6*width+x6]
					&& mm[y6*width+x6] == 1) {
				visited[y6*width + x6] = true;
				xx[j] = x6;
				yy[j] = y6;
				j++;
			}
			if (x7 >= 0 && x7 < width && y7 >= 0 && y7 < height && !visited[y7*width+x7]
					&& mm[y7*width+x7] == 1) {
				visited[y7*width + x7] = true;
				xx[j] = x7;
				yy[j] = y7;
				j++;
			}
			if (x8 >= 0 && x8 < width && y8 >= 0 && y8 < height && !visited[y8*width+x8]
					&& mm[y8*width+x8] == 1) {
				visited[y8*width + x8] = true;
				xx[j] = x8;
				yy[j] = y8;
				j++;
			}
			
		}
		reset(RobotPlayer.FLOODSET);
		System.out.println("finished " + Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		rc.yield();
		
		
		while (true) {
			rc.yield();
		}
	}
	
	static int height, width, xs, ys;
	static boolean[] visited;
	static int[] a = { -1, 1, 0, 0, -1, 1, 1, -1};
	static int[] b = { -1, 1, -1, 1, 0, 0, -1, 1};
	
	private static void flood(int xg, int yg) {
		System.out.println(Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		int x1 = xg-1, y1 = yg-1;
		int x2 = xg+1, y2 = yg+1;
		int x3 = xg,   y3 = yg-1;
		int x4 = xg,   y4 = yg+1;
		int x5 = xg-1, y5 = yg;
		int x6 = xg+1, y6 = yg;
		int x7 = xg+1, y7 = yg-1;
		int x8 = xg-1, y8 = yg+1;
		//System.out.println(Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height && !visited[y1*width+x1]) {
			visited[y1*width + x1] = true;
			flood(x1, y1);
		}
		if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height && !visited[y2*width+x2]) {
			visited[y2*width + x2] = true;
			flood(x2, y2);
		}
		if (x3 >= 0 && x3 < width && y3 >= 0 && y3 < height && !visited[y3*width+x3]) {
			visited[y3*width + x3] = true;
			flood(x3, y3);
		}
		if (x4 >= 0 && x4 < width && y4 >= 0 && y4 < height && !visited[y4*width+x4]) {
			visited[y4*width + x4] = true;
			flood(x4, y4);
		}
		if (x5 >= 0 && x5 < width && y5 >= 0 && y5 < height && !visited[y5*width+x5]) {
			visited[y5*width + x5] = true;
			flood(x5, y5);
		}
		if (x6 >= 0 && x6 < width && y6 >= 0 && y6 < height && !visited[y6*width+x6]) {
			visited[y6*width + x6] = true;
			flood(x6, y6);
		}
		if (x7 >= 0 && x7 < width && y7 >= 0 && y7 < height && !visited[y7*width+x7]) {
			visited[y7*width + x7] = true;
			flood(x7, y7);
		}
		if (x8 >= 0 && x8 < width && y8 >= 0 && y8 < height && !visited[y8*width+x8]) {
			visited[y8*width + x8] = true;
			flood(x8, y8);
		}
		/*//System.out.println(loc);
		for (int i = 0; i < 8; i++) {
			//MapLocation l = loc.add(d);
			//MapLocation l = new MapLocation(a[i] + loc.x, b[i] + loc.y);
			//int x = l.x;// - xs;
			//int y = l.y;// - ys;
			int x = a[i] + xg, y = b[i] + yg;
			//System.out.println(x + " " + y + " " + width + " " + height + " " + visited[y * width + x]);
			if (x < 0 || y < 0 || x >= width || y >= height || visited[y * width + x]) {
				continue;
			}
			visited[y*width + x] = true;
			flood(x,y);
		}*/
	}

}
