package T103;

import static T103.BaseBot.myHQ;
import static T103.BaseBot.theirHQ;
import static T103.BaseBot.rc;
import static T103.Channels.isSet;
import static T103.Channels.reset;
import static T103.Channels.set;

import java.util.LinkedList;
import java.util.List;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;

//TODO request flood

/**
 * !! NOTE !!
 * Functions reconstruct map and floodAndServe must be executed in the same robot.
 */
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
	
	// Step used for explore points
	private static final int EXP_STEP = 3;
	
	
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
						double ydiff = loc.y - yc;
						loc = new MapLocation(loc.x, (int) (yc - ydiff));
					} else if (isVerticalSym()) {
						double xdiff = loc.x - xc;
						loc = new MapLocation((int) (xc - xdiff), loc.y);
					} else {
						throw new RobotException("Don't know which symmetry.");
					}
				}
				
				// Test tile
				TerrainTile tile = rc.senseTerrainTile(loc);
				if (tile == TerrainTile.NORMAL) {
					map[y * width + x] = true;
				}
				
			}//End for x
		}//End for y
		
	}//End reconstructMap
	
	
	/**
	 * From map info creates points that drones need to explore.
	 * @throws GameActionException incorrect channels
	 */
	public static void decideExploringPoints() throws GameActionException {
		readParameters();

		List<MapLocation> exploreLocations = new LinkedList<>();
		final int ybl = ytl + height;
		final int xtr = xtl + width;
		final int dStep = EXP_STEP << 1;
		
		// Set explore locations
		for (int y = ytl; y < ybl; y += dStep) {
			for (int x = xtl; x < xtr; x += EXP_STEP) {
				MapLocation loc = new MapLocation(x, y);
				if (loc.distanceSquaredTo(myHQ) <= loc.distanceSquaredTo(theirHQ)) {
					exploreLocations.add(loc);
				}
			}
			for (int x = xtl + width; x >= xtl; x -= EXP_STEP) {
				MapLocation loc = new MapLocation(x, y+EXP_STEP);
				if (loc.distanceSquaredTo(myHQ) <= loc.distanceSquaredTo(theirHQ)) {
					exploreLocations.add(loc);
				}
			}
		}
		
		// Broadcast locations
		int offset = 0;
		for (MapLocation loc : exploreLocations) {
			if (Channels.expLOCFIRST + offset < Channels.expLOCLAST) {
				rc.broadcast(Channels.expLOCFIRST + offset, loc.x);
				rc.broadcast(Channels.expLOCFIRST + offset + 1, loc.y);
				offset += 2;
			}
		}
		
		// Set offsets
		int count = rc.readBroadcast(Channels.expDRONECOUNT);
		int diff = (exploreLocations.size() / count) << 1;
		for (int i = 0; i < count; i++) {
			rc.broadcast(Channels.expOFFSET + i, Channels.expLOCFIRST + diff*i);
		}
		rc.broadcast(		// Set last offset
				Channels.expOFFSET + count,
				Channels.expLOCFIRST + exploreLocations.size()*2
		);
		
		rc.broadcast(Channels.expLOCCOUNT, exploreLocations.size());	// Set point count
		rc.broadcast(Channels.expSTARTED, 1);							// Done
	}
	
	
	/**
	 * Check whether the map is rotationally symmetric.
	 * @return true if it is, false otherwise
	 */
	public static boolean isRotationSym() {
		return myHQ.x != theirHQ.x && myHQ.y != theirHQ.y;
	}

	/**
	 * Check whether the map is horizontally symmetric.
	 * @return true if it is, false otherwise
	 */
	public static boolean isHorizontalSym() {
		return myHQ.x == theirHQ.x;
	}

	/**
	 * Check whether the map is vertically symmetric.
	 * @return true if it is, false otherwise
	 */
	public static boolean isVerticalSym() {
		return myHQ.y == theirHQ.y;
	}
	
	/**
	 * Decides whether location is corner or not.
	 * @param loc location to check
	 * @return true if is corner, false otherwise
	 */
	public static boolean isCorner(MapLocation loc) {
		boolean n = rc.senseTerrainTile(loc.add(Direction.NORTH)) == TerrainTile.OFF_MAP;
		boolean s = rc.senseTerrainTile(loc.add(Direction.SOUTH)) == TerrainTile.OFF_MAP;
		boolean e = rc.senseTerrainTile(loc.add(Direction.EAST)) == TerrainTile.OFF_MAP;
		boolean w = rc.senseTerrainTile(loc.add(Direction.WEST)) == TerrainTile.OFF_MAP;
		boolean curr = rc.senseTerrainTile(loc) == TerrainTile.OFF_MAP;
		return (n && e || n && w || s && e || s && w) && !curr;
	}
	
	/**
	 * Calculates all corners using given info.
	 * @param height map height
	 * @param width map width
	 * @param tlx upper left x
	 * @param tly upper left y
	 * @return return an array of all corners
	 */
	public static MapLocation[] corners(int height, int width, int tlx, int tly) {
		MapLocation tl = new MapLocation(tlx, tly);
		MapLocation tr = tl.add(Direction.EAST, width - 1);
		MapLocation bl = tl.add(Direction.SOUTH, height - 1);
		MapLocation br = bl.add(Direction.EAST, width - 1);
		return new MapLocation[] { tl, tr, bl, br };
	}
	
	/**
	 * Calculates all corners using the data from channels. If the parameters
	 * are not broadcasted, it will throw exception.
	 * @return an array of all corners
	 * @throws GameActionException incorrect channels
	 */
	public static MapLocation[] corners() throws GameActionException {
		readParameters();
		return corners(height, width, xtl, ytl);
	}


	
	// Flood constants
	private static final int MAXFLOODS = 10;
	private static final int MINBYTECODE = 500;
	
	// Flood data
	private static int[][] floods;
	
	// Flood flags
	private static boolean[] floodCreated;
	
	
	/**
	 * Request flood to be broadcasted.
	 * @param idx index of the flood in the queue
	 * @throws GameActionException incorrect channels
	 */
	public static void requestFlood(int idx) throws GameActionException {
		if (isActive(idx)) {
			return;
		}
		rc.broadcast(Channels.FLOODINDEX, idx);
		rc.broadcast(Channels.FLOODREQUEST, 1);
	}
	
	/**
	 * Returns direction of the given coordinates in the given flood.
	 * @param idx index of the flood in the queue
	 * @param x transformed coordinate x
	 * @param y transformed coordinate y
	 * @return int direction
	 * @throws GameActionException incorrect channels 
	 */
	public static int get(int idx, int x, int y) throws GameActionException {
		int first = getActiveFirst(idx);
		readParameters();
		if (first == Channels.FLOODACTIVEINDEX1) {
			rc.broadcast(Channels.FLOODLASTUSED1, Clock.getRoundNum());
		} else if (first == Channels.FLOODACTIVEINDEX2) {
			rc.broadcast(Channels.FLOODLASTUSED2, Clock.getRoundNum());
		} else {
			rc.broadcast(Channels.FLOODLASTUSED3, Clock.getRoundNum());
		}
		return rc.readBroadcast(first + y * width + height);
	}
	
	/**
	 * Sets the queue. Broadcasts the queue. Can be called only once
	 * @param locations locations to broadcast into queue
	 * @throws GameActionException incorrect channels
	 */
	public static void setQueue(MapLocation... locations) throws GameActionException {
		if (isSet(Channels.FLOODQUEUESET)) {
			throw new RobotException("Can set queue only once.");
		}
		
		// Set queue size
		int size = locations.length;
		if (size > MAXFLOODS) {
			throw new RobotException("Queue is too large.");
		}
		rc.broadcast(Channels.FLOODQUEUECOUNT, size);
		
		// Set queue locations
		for (int i = 0; i < size; i++) {
			int idx = i << 1;
			rc.broadcast(Channels.FLOODQUEUEFIRST + idx, locations[i].x);
			rc.broadcast(Channels.FLOODQUEUEFIRST + idx + 1, locations[i].y);
		}
		
		// Queue is set
		set(Channels.FLOODQUEUESET);
	}
	
	/**
	 * Tests whether a flood with given index is active.
	 * @param idx index of the flood
	 * @return true if it is active, false otherwise
	 * @throws GameActionException incorrect channels
	 */
	public static boolean isActive(int idx) throws GameActionException {
		return (	isSet(Channels.FLOODACTIVE1)
						&& rc.readBroadcast(Channels.FLOODACTIVEINDEX1) == idx)
					||
					(isSet(Channels.FLOODACTIVE2)
						&& rc.readBroadcast(Channels.FLOODACTIVEINDEX2) == idx)
					||
					(isSet(Channels.FLOODACTIVE3)
						&& rc.readBroadcast(Channels.FLOODACTIVEINDEX3) == idx);
	}
	
	/**
	 * Returns start location of the idx flood in the broadcast channels.
	 * @param idx index of the flood in the queue
	 * @return starting channel
	 * @throws GameActionException incorrect channels
	 */
	public static int getActiveFirst(int idx) throws GameActionException {
		if (rc.readBroadcast(Channels.FLOODACTIVEINDEX1) == idx) {
			return Channels.FLOODFIRST1;
		}
		if (rc.readBroadcast(Channels.FLOODACTIVEINDEX2) == idx) {
			return Channels.FLOODFIRST2;
		}
		if (rc.readBroadcast(Channels.FLOODACTIVEINDEX3) == idx) {
			return Channels.FLOODFIRST3;
		}
		throw new RobotException("Flood: " + idx + " is not active.");
	}
	
	/**
	 * Floods whatever is given in flooding queue and server every now and then.
	 * @throws GameActionException incorrect channels
	 */
	public static void floodAndServe() throws GameActionException {
		readParameters();
		
		if (!isSet(Channels.FLOODQUEUESET)) {
			throw new RobotException("Flood queue not set.");
		}
		
		int size = rc.readBroadcast(Channels.FLOODQUEUECOUNT);
		floods = new int[size][width*height];
		floodCreated = new boolean[size];
		
		for (int i = 0; i < size; i++) {
			int idx = i << 1;
			int xStart = rc.readBroadcast(Channels.FLOODQUEUEFIRST + idx);
			int yStart = rc.readBroadcast(Channels.FLOODQUEUEFIRST + idx + 1);
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
		if (!isSet(Channels.FLOODREQUEST)) {
			return;
		}
		
		readParameters();
		
		// Check if flood has been created
		int idx = rc.readBroadcast(Channels.FLOODINDEX);
		if (!floodCreated[idx] || isActive(idx)) {
			return;
		}
		
		int flood1 = rc.readBroadcast(Channels.FLOODLASTUSED1);
		int flood2 = rc.readBroadcast(Channels.FLOODLASTUSED2);
		int flood3 = rc.readBroadcast(Channels.FLOODLASTUSED3);
		int floodActive, floodFirst, floodActiveIdx, floodLastUsed;
		if (flood1 < flood2 && flood1 < flood3) {
			floodActive = Channels.FLOODACTIVE1;
			floodFirst = Channels.FLOODFIRST1;
			floodActiveIdx = Channels.FLOODACTIVEINDEX1;
			floodLastUsed = Channels.FLOODLASTUSED1;
		} else if (flood2 < flood3) {
			floodActive = Channels.FLOODACTIVE2;
			floodFirst = Channels.FLOODFIRST2;
			floodActiveIdx = Channels.FLOODACTIVEINDEX2;
			floodLastUsed = Channels.FLOODLASTUSED2;
		} else {
			floodActive = Channels.FLOODACTIVE3;
			floodFirst = Channels.FLOODFIRST3;
			floodActiveIdx = Channels.FLOODACTIVEINDEX3;
			floodLastUsed = Channels.FLOODLASTUSED3;
		}

		System.out.println("Started serve "
				+ Clock.getBytecodeNum() + " " + Clock.getRoundNum());
		
		int[] flood = floods[idx];
		int size = flood.length;
		reset(floodActive);								// Disable reading flood
		for (int i = 0; i < size; i++) {
			rc.broadcast(floodFirst + i, flood[i]);
		}
		rc.broadcast(floodActiveIdx, idx);				// Set active flood index
		rc.broadcast(floodLastUsed, Clock.getRoundNum());
		set(floodActive);								// Enable reading flood
		reset(Channels.FLOODREQUEST);					// Can set new request
		
		System.out.println("Finished serve "
				+ Clock.getBytecodeNum() + " " + Clock.getRoundNum());
	}

	/**
	 * Read all parameters from broadcast if possible
	 */
	private static void readParameters() throws GameActionException {
		if (!isSet(Channels.MAPSET)) {
			throw new RobotException("Map parameters are not set");
		}
		height = rc.readBroadcast(Channels.MAPHEIGHT);
		width = rc.readBroadcast(Channels.MAPWIDTH);
		xtl = rc.readBroadcast(Channels.TOPLEFTX);
		ytl = rc.readBroadcast(Channels.TOPLEFTY);
	}
	
	
	
	// FOR DEBUGGING
	
	// Colors used to color shit when debugging
	private static final int[] colors = {
		10, 250, 10,
		250, 10, 250,
		10, 10, 250,
		250, 250, 10,
		250, 10, 250,
		10, 250, 250,
		140, 10, 140,
		10, 140, 140,
		140, 140, 10,
		140, 10, 10,
		10, 140, 10,
		10, 10, 140
	};
	
	/**
	 * Prints map to console. Used only for debugging.
	 */
	public static void printMap() throws GameActionException {
		readParameters();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (map[y*width + x]) {
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
				if (d == 0) {
					continue;
				}
				
				MapLocation loc = new MapLocation(x+xtl, y+ytl);
				int r = d * 3;
				int g = d * 3 + 1;
				int b = d * 3 + 2;
				rc.setIndicatorDot(loc, colors[r], colors[g], colors[b]);
			}
		}
	}
	
	/**
	 * Marks in map whatever is in the flood channels if it is active.
	 * Used for debugging.
	 */
	public static void markFloodFromChannels(int idx) throws GameActionException {
		readParameters();
		
		if (!isActive(idx)) {
			throw new RobotException("Flood is not active.");
		}

		int first = getActiveFirst(idx); 
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int d = rc.readBroadcast(first + y*width + x);
				if (d == 0) {
					continue;
				}
				
				MapLocation loc = new MapLocation(x+xtl, y+ytl);
				int r = d * 3;
				int g = d * 3 + 1;
				int b = d * 3 + 2;
				rc.setIndicatorDot(loc, colors[r], colors[g], colors[b]);
			}
		}
	}
	
	/**
	 * Marks locations for exploring in the map. Used for debugging.
	 * @throws GameActionException incorrect channels
	 */
	public static void markExploreLocations() throws GameActionException {
		int count = rc.readBroadcast(Channels.expDRONECOUNT);
		for (int i = 0; i < count; i++) {
			int start = rc.readBroadcast(Channels.expOFFSET + i);
			int end = rc.readBroadcast(Channels.expOFFSET + i+1);
			for (int j = start; j < end; j += 2) {
				MapLocation loc = new MapLocation(
						rc.readBroadcast(j),
						rc.readBroadcast(j+1)
				);
				rc.setIndicatorDot(loc, colors[3*i], colors[3*i+1], colors[3*i+2]);
			}
		}
	}
	
}//End class
