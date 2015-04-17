package T103;

import static T103.BaseBot.myHQ;
import static T103.BaseBot.theirHQ;
import static T103.BaseBot.rc;
import static T103.BaseBot.isSet;
import static T103.BaseBot.isRotationSym;
import static T103.BaseBot.isHorizontalSym;
import static T103.BaseBot.isVerticalSym;
import static T103.BaseBot.reset;
import static T103.BaseBot.set;

import T103.RobotPlayer;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;

public class MapInfo {

	// true are open spots, false are obstacles
	public static boolean[] map;

	// Map size
	private static int width;
	private static int height;

	// Top left corner
	private static int xtl;
	private static int ytl;

	// Middle point between HQs
	private static double xc;
	private static double yc;
	
	
	/**
	 * Reconstructs the map from what drones have explored.
	 * @throws GameActionException channels not correct for maps
	 */
	public static void reconstructMap() throws GameActionException {
		xc = (myHQ.x + theirHQ.x) / 2.0;
		yc = (myHQ.y + theirHQ.y) / 2.0;
		
		readParameters();
		
		map = new boolean[width*height];	// Initialized to false
		
		// Map reconstruction
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				// Location in map to test
				MapLocation loc = new MapLocation(x + xtl, y + ytl);
				
				if (loc.distanceSquaredTo(myHQ) > loc.distanceSquaredTo(theirHQ)) {
					// Locations on their side have to be transformed
					
					if (isRotationSym()) {
						double xdiff = loc.x - xc;
						double ydiff = loc.y - yc;
						loc = new MapLocation((int) (xc - xdiff), (int) (yc - ydiff));
						
					} else if (isHorizontalSym()) {
						//TODO
					} else if (isVerticalSym()) {
						//TODO
					} else {
						throw new RobotException("Don't know which symmetry.");
					}
				}
				
				// Test tile
				TerrainTile tile = rc.senseTerrainTile(loc);
				if (tile == TerrainTile.NORMAL) {
					rc.broadcast(RobotPlayer.MAPFIRST + y * width + x, 1);
					map[y * width + x] = true;
				}
				
			}//End for x
		}//End for y
		
	}//End reconstructMap
	
	
	private static final int MAXFLOODS = 10;
	private static final int MINBYTECODE = 1000;
	
	// Used for flooding
	private static boolean[] visited;
//	
	private static int[][] floods;
	private static boolean[] floodCreated;
//	
	//private static int[] fxs;
	//private static int[] fys;
	
	
	
	public static void setQueue(MapLocation... locations) throws GameActionException {
		if (isSet(RobotPlayer.FLOODQUEUESET)) {
			return;
		}
		
		// Set queue size
		int size = locations.length;
		if (size > MAXFLOODS) {
			throw new RobotException("Queue is too large.");
		}
		rc.broadcast(RobotPlayer.FLOODQUEUECOUNT, size);
		
		// Set queue locations
		for (int i = 0; i < size; i++) {
			int idx = i << 1;
			rc.broadcast(RobotPlayer.FLOODQUEUEFIRST + idx, locations[i].x);
			rc.broadcast(RobotPlayer.FLOODQUEUEFIRST + idx + 1, locations[i].y);
		}
		
		// Queue is set
		set(RobotPlayer.FLOODQUEUESET);
	}
	
	public static void floodAndServe() throws GameActionException {
		readParameters();
		
		if (!isSet(RobotPlayer.MAPBROADCASTED)) {
			throw new RobotException("Map not broadcasted.");
		}
		if (!isSet(RobotPlayer.FLOODQUEUESET)) {
			throw new RobotException("Flood queue not set.");
		}
		
		int size = rc.readBroadcast(RobotPlayer.FLOODQUEUECOUNT);
		floods = new int[size][width*height];
		floodCreated = new boolean[size];
		
		for (int i = 0; i < size; i++) {
			int idx = i << 1;
			int xStart = rc.readBroadcast(RobotPlayer.FLOODQUEUEFIRST + idx);
			int yStart = rc.readBroadcast(RobotPlayer.FLOODQUEUEFIRST + idx + 1);
			flood(floods[i], xStart, yStart);
			//set(RobotPlayer.FLOODDONEFIRST + i);
			floodCreated[i] = true;
		}
	}
	
	/**
	 * TODO flood
	 * @param flood 
	 * @throws GameActionException
	 */
	private static void flood(int[] flood, int xStart, int yStart) throws GameActionException {
		readParameters();
		
		// Visited "set"
		visited = new boolean[width * height];
		visited[(yStart - ytl) * width + xStart - xtl] = true;

		// Open "queue"
		int[] xx = new int[width*height];
		int[] yy = new int[width*height];
		
		xx[0] = xStart-xtl;
		yy[0] = yStart-ytl;
		int i = 0, j = 1;		// Head and tail of the queue
		

		while (i < j) {
			if (Clock.getBytecodesLeft() < MINBYTECODE) {
				serve();	//TODO this is not ok
			}
			
			
			int xg = xx[i];
			int yg = yy[i];
			i++;
			
			// All 8 directions to move
			int x1 = xg,   y1 = yg-1;
			int x2 = xg  , y2 = yg+1;
			int x3 = xg-1, y3 = yg;
			int x4 = xg+1, y4 = yg;
			int x5 = xg-1, y5 = yg-1;
			int x6 = xg+1, y6 = yg+1;
			int x7 = xg+1, y7 = yg-1;
			int x8 = xg-1, y8 = yg+1;
			
			// Check if location is within the borders, if it has not been
			// visited and if it is free (not obstacle)
			if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height
					&& !visited[y1*width+x1] && map[y1*width+x1]) {
				visited[y1*width + x1] = true;
				xx[j] = x1;
				yy[j] = y1;
				j++;
				flood[y1*width + x1] = 1;
			}
			if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height
					&& !visited[y2*width+x2] && map[y2*width+x2]) {
				visited[y2*width + x2] = true;
				xx[j] = x2;
				yy[j] = y2;
				j++;
				flood[y2*width + x2] = 2;
			}
			if (x3 >= 0 && x3 < width && y3 >= 0 && y3 < height
					&& !visited[y3*width+x3] && map[y3*width+x3]) {
				visited[y3*width + x3] = true;
				xx[j] = x3;
				yy[j] = y3;
				j++;
				flood[y3*width + x3] = 3;
			}
			if (x4 >= 0 && x4 < width && y4 >= 0 && y4 < height
					&& !visited[y4*width+x4] && map[y4*width+x4]) {
				visited[y4*width + x4] = true;
				xx[j] = x4;
				yy[j] = y4;
				j++;
				flood[y4*width + x4] = 4;
			}
			if (x5 >= 0 && x5 < width && y5 >= 0 && y5 < height
					&& !visited[y5*width+x5] && map[y5*width+x5]) {
				visited[y5*width + x5] = true;
				xx[j] = x5;
				yy[j] = y5;
				j++;
				flood[y5*width + x5] = 5;
			}
			if (x6 >= 0 && x6 < width && y6 >= 0 && y6 < height
					&& !visited[y6*width+x6] && map[y6*width+x6]) {
				visited[y6*width + x6] = true;
				xx[j] = x6;
				yy[j] = y6;
				j++;
				flood[y6*width + x6] = 6;
			}
			if (x7 >= 0 && x7 < width && y7 >= 0 && y7 < height
					&& !visited[y7*width+x7] && map[y7*width+x7]) {
				visited[y7*width + x7] = true;
				xx[j] = x7;
				yy[j] = y7;
				j++;
				flood[y7*width + x7] = 7;
			}
			if (x8 >= 0 && x8 < width && y8 >= 0 && y8 < height
					&& !visited[y8*width+x8] && map[y8*width+x8]) {
				visited[y8*width + x8] = true;
				xx[j] = x8;
				yy[j] = y8;
				j++;
				flood[y8*width + x8] = 8;
			}
			
		}
	}
	
	
	public static void serve() throws GameActionException {
		if (!isSet(RobotPlayer.FLOODREQUEST)) {
			return;
		}
		
		readParameters();
		
		int idx = rc.readBroadcast(RobotPlayer.FLOODINDEX);
		if (!floodCreated[idx]) {
			return;
		}

		System.out.println(Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		int[] flood = floods[idx];
		int size = flood.length;
		for (int i = 0; i < size; i++) {
			rc.broadcast(RobotPlayer.FLOODFIRST + i, flood[i]);
		}
		rc.broadcast(RobotPlayer.FLOODACTIVEINDEX, idx);
		set(RobotPlayer.FLOODACTIVE);
		reset(RobotPlayer.FLOODREQUEST);
		System.out.println(Clock.getBytecodeNum() + " " + Clock.getRoundNum());
	}

	/**
	 * Read all parameters from broadcast if possible
	 */
	private static void readParameters() throws GameActionException {
		if (!isSet(RobotPlayer.MAPSET)) {
			throw new RobotException("Map parameters are not set");
		}
		height = rc.readBroadcast(RobotPlayer.MAPHEIGHT);
		width = rc.readBroadcast(RobotPlayer.MAPWIDTH);
		xtl = rc.readBroadcast(RobotPlayer.TOPLEFTX);
		ytl = rc.readBroadcast(RobotPlayer.TOPLEFTY);
	}
	
	public static boolean isActive(int idx) throws GameActionException {
		//System.out.println(isSet(RobotPlayer.FLOODACTIVE) + " " + rc.readBroadcast(RobotPlayer.FLOODACTIVEINDEX));
		return isSet(RobotPlayer.FLOODACTIVE) && rc.readBroadcast(RobotPlayer.FLOODACTIVEINDEX) == idx;
	}
	
	
	/**
	 * Prints map to console. Used only for debugging.
	 * @throws GameActionException incorrect channels
	 */
	public static void printMap() throws GameActionException {
		readParameters();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (rc.readBroadcast(RobotPlayer.MAPFIRST + y*width + x) == 1) {
					System.out.print(" ");
				} else {
					System.out.print("#");
				}
			}
			System.out.println();
		}
	}
	
	/**
	 * Marks flooding in the map. Used for debugging purposes.
	 * @throws GameActionException
	 */
	public static void markFlood(int idx) throws GameActionException {
		readParameters();
		
		int[] flood = floods[idx];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int d = flood[y*width+x];
				MapLocation loc = new MapLocation(x+xtl, y+ytl);
				
				if (d == 1) {
					rc.setIndicatorDot(loc, 150, 250, 100);
				} else if (d == 2) {
					rc.setIndicatorDot(loc, 250, 100, 100);
				} else if (d == 3) {
					rc.setIndicatorDot(loc, 100, 100, 250);
				} else if (d == 4) {
					rc.setIndicatorDot(loc, 30, 250, 250);
				} else if (d == 5) {
					rc.setIndicatorDot(loc, 250, 250, 30);
				} else if (d == 6) {
					rc.setIndicatorDot(loc, 30, 250, 30);
				} else if (d == 7) {
					rc.setIndicatorDot(loc, 30, 30, 250);
				} else if (d == 8) {
					rc.setIndicatorDot(loc, 250, 30, 30);
				}
			}
		}
	}
	
	
	
	public static void markFloodFromChannels(int idx) throws GameActionException {
		readParameters();
		
		if (!isSet(RobotPlayer.FLOODACTIVE)
				|| rc.readBroadcast(RobotPlayer.FLOODACTIVEINDEX) != idx) {
			throw new RobotException("Incorrect flood is active.");
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int d = rc.readBroadcast(RobotPlayer.FLOODFIRST + y*width + x);
				MapLocation loc = new MapLocation(x+xtl, y+ytl);
				
				if (d == 1) {
					rc.setIndicatorDot(loc, 150, 250, 100);
				} else if (d == 2) {
					rc.setIndicatorDot(loc, 250, 100, 100);
				} else if (d == 3) {
					rc.setIndicatorDot(loc, 100, 100, 250);
				} else if (d == 4) {
					rc.setIndicatorDot(loc, 30, 250, 250);
				} else if (d == 5) {
					rc.setIndicatorDot(loc, 250, 250, 30);
				} else if (d == 6) {
					rc.setIndicatorDot(loc, 30, 250, 30);
				} else if (d == 7) {
					rc.setIndicatorDot(loc, 30, 30, 250);
				} else if (d == 8) {
					rc.setIndicatorDot(loc, 250, 30, 30);
				}
			}
		}
	}
	
}//End class
