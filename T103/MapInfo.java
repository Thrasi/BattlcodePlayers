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
	

	
	// Flood constants
	private static final int MAXFLOODS = 10;
	private static final int MINBYTECODE = 500;
	
	// Flood data
	private static int[][] floods;
	
	// Flood flags
	private static boolean[] floodCreated;
	
	/**
	 * Sets the queue. Broadcasts the queue. Can be called only once
	 * @param locations locations to broadcast into queue
	 * @throws GameActionException incorrect channels
	 */
	public static void setQueue(MapLocation... locations) throws GameActionException {
		if (isSet(RobotPlayer.FLOODQUEUESET)) {
			throw new RobotException("Can set queue only once.");
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
	
	/**
	 * Tests whether a flood with given index is active.
	 * @param idx index of the flood
	 * @return true if it is active, false otherwise
	 * @throws GameActionException incorrect channels
	 */
	public static boolean isActive(int idx) throws GameActionException {
		return isSet(RobotPlayer.FLOODACTIVE)
				&& rc.readBroadcast(RobotPlayer.FLOODACTIVEINDEX) == idx;
	}
	
	/**
	 * Floods whatever is given in flooding queue and server every now and then.
	 * @throws GameActionException incorrect channels
	 */
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
			floodCreated[i] = true;
		}
	}
	
	/**
	 * Function used for flooding the map and finding directions. It will also serve 2-3
	 * times at the beginning of each turn. It looks like shit but it's the only
	 * way to make to run fast.
	 * @param flood flood to fill
	 * @param xStart flood source x
	 * @param yStart flood source y
	 * @throws GameActionException incorrect channels
	 */
	private static void flood(int[] flood, int xStart, int yStart) throws GameActionException {
		readParameters();
		
		// Open "queue"
		int[] xx = new int[width*height];
		int[] yy = new int[width*height];
		
		xx[0] = xStart-xtl;
		yy[0] = yStart-ytl;
		int i = 0, j = 1;		// Head and tail of the queue

		while (i < j) {
			// This part is called 2-3 times on the beginning of each turn
			if (Clock.getBytecodesLeft() < MINBYTECODE) {
				serve();
			}
			
			// Dequeue
			int xg = xx[i];
			int yg = yy[i];
			i++;
			
			// All 8 directions to move (Succ)
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
					&& flood[y1*width+x1] == 0 && map[y1*width+x1]) {
				xx[j] = x1;		// Enqueue
				yy[j] = y1;		// Enqueue
				j++;			// Enqueue
				flood[y1*width + x1] = 1;
			}
			if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height
					&& flood[y2*width+x2] == 0 && map[y2*width+x2]) {
				xx[j] = x2;
				yy[j] = y2;
				j++;
				flood[y2*width + x2] = 2;
			}
			if (x3 >= 0 && x3 < width && y3 >= 0 && y3 < height
					&& flood[y3*width+x3] == 0 && map[y3*width+x3]) {
				xx[j] = x3;
				yy[j] = y3;
				j++;
				flood[y3*width + x3] = 3;
			}
			if (x4 >= 0 && x4 < width && y4 >= 0 && y4 < height
					&& flood[y4*width+x4] == 0 && map[y4*width+x4]) {
				xx[j] = x4;
				yy[j] = y4;
				j++;
				flood[y4*width + x4] = 4;
			}
			if (x5 >= 0 && x5 < width && y5 >= 0 && y5 < height
					&& flood[y5*width+x5] == 0 && map[y5*width+x5]) {
				xx[j] = x5;
				yy[j] = y5;
				j++;
				flood[y5*width + x5] = 5;
			}
			if (x6 >= 0 && x6 < width && y6 >= 0 && y6 < height
					&& flood[y6*width+x6] == 0 && map[y6*width+x6]) {
				xx[j] = x6;
				yy[j] = y6;
				j++;
				flood[y6*width + x6] = 6;
			}
			if (x7 >= 0 && x7 < width && y7 >= 0 && y7 < height
					&& flood[y7*width+x7] == 0 && map[y7*width+x7]) {
				xx[j] = x7;
				yy[j] = y7;
				j++;
				flood[y7*width + x7] = 7;
			}
			if (x8 >= 0 && x8 < width && y8 >= 0 && y8 < height
					&& flood[y8*width+x8] == 0 && map[y8*width+x8]) {
				xx[j] = x8;
				yy[j] = y8;
				j++;
				flood[y8*width + x8] = 8;
			}
			
		}//End while
		
		// Set flood source to 0 (don't move)
		flood[yy[0]*width + xx[0]] = 0;
		
	}//End flood
	
	
	/**
	 * Serves the flood if the request is set.
	 * @throws GameActionException incorrect channels
	 */
	public static void serve() throws GameActionException {
		if (!isSet(RobotPlayer.FLOODREQUEST)) {
			return;
		}
		
		readParameters();
		
		// Check if flood has been created
		int idx = rc.readBroadcast(RobotPlayer.FLOODINDEX);
		if (!floodCreated[idx]) {
			return;
		}

		//System.out.println("Started serve "
		//+ Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		
		int[] flood = floods[idx];
		int size = flood.length;
		reset(RobotPlayer.FLOODACTIVE);						// Disable reading flood
		for (int i = 0; i < size; i++) {
			rc.broadcast(RobotPlayer.FLOODFIRST + i, flood[i]);
		}
		rc.broadcast(RobotPlayer.FLOODACTIVEINDEX, idx);	// Set active flood index
		set(RobotPlayer.FLOODACTIVE);						// Enable reading flood
		reset(RobotPlayer.FLOODREQUEST);					// Can set new request
		
		//System.out.println("Finished serve "
		//+ Clock.getBytecodeNum() + " " + Clock.getRoundNum());
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
	
	
	
	// FOR DEBUGGING
	
	/**
	 * Prints map to console. Used only for debugging.
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
	
	/**
	 * Marks in map whatever is in the flood channels if it is active.
	 * Used for debugging.
	 */
	public static void markFloodFromChannels() throws GameActionException {
		readParameters();
		
		if (!isSet(RobotPlayer.FLOODACTIVE)) {
			throw new RobotException("Flood is not active.");
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
