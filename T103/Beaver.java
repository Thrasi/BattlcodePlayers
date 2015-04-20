package T103;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Beaver extends BaseBot {
	
	
	private static boolean movedOnce = false;
	
	public Beaver(RobotController rc) throws GameActionException {
		super(rc);
		
	}
	
	
	
	@Override
	public void execute() throws GameActionException {
		if (rc.getID() == rc.readBroadcast(Channels.CORNERBEAVER)) {
			//cornerBeaver();
		}
		
		
		if (!movedOnce) {
			movedOnce = tryMove(rc.getLocation().directionTo(myHQ).opposite());
		}
		// TODO is this a good strategy?
		//if (gridScore2(rc.getLocation()) <= 0) {
		if (gridScore2(rc.getLocation()) == 0) {
			//tryMoveTo(BuildingStrategies.openSpotForBuilding());
			MapLocation moveToThis = gridSpot();
			if (moveToThis == null) {
				tryMove(getRandomDirection());
			} else {
				tryMoveTo(moveToThis);
			}
			//rc.yield();
		} else if (shouldMove()) {
			tryMove(getRandomDirection());
		}
		
		
		boolean hasBuilt = false;
		int queueStart = rc.readBroadcast(Channels.BUILDQSTART);
		int queueEnd = rc.readBroadcast(Channels.BUILDQEND);
		if (queueStart < queueEnd) {
			int typeID = rc.readBroadcast(queueStart);
			hasBuilt = tryBuildOnGrid(HQ.TYPES[typeID]);
			if (hasBuilt) {
				queueStart++;
			}
			rc.broadcast(Channels.BUILDQSTART, queueStart);
		}
		
		rc.yield();
		
	}
	
	private static boolean shouldMove() throws GameActionException {
		MapLocation current = rc.getLocation();
		for (Direction d : directions) {
			MapLocation loc = current.add(d);
			RobotInfo ri = rc.senseRobotAtLocation(loc);
			if (ri != null && !ri.type.isBuilding) {
				return true;
			}
		}
		return false;
	}

	private boolean tryBuildOnGrid(RobotType type) throws GameActionException {
		MapLocation current = rc.getLocation();
		int bestScore = Integer.MIN_VALUE;
		Direction best = null;
		for (Direction dir : directions) {
			MapLocation loc = current.add(dir);
			int score = gridScore(loc);
			if (!isNormal(loc) || isOccupied(loc) || score == 0) {		// Cannot build here
				continue;
			}
			
			if (score > bestScore) {
				bestScore = score;
				best = dir;
			}
		}
		if (best == null) {
			return false;
		}
		return tryBuild(best, type);
	}


	static Direction[] dirs = {Direction.EAST, Direction.WEST, Direction.NORTH,
			Direction.SOUTH};

	private static int gridScore(MapLocation loc) throws GameActionException {
		RobotInfo ri = rc.senseRobotAtLocation(loc);
		if ((Math.abs(loc.x-myHQ.x) + Math.abs(loc.y-myHQ.y)) % 2 == 1
				|| (ri != null && ri.type.isBuilding)) {
			return 0;
		}
		return 1;
	}
	
	static Direction[] dirs2 = {Direction.SOUTH_EAST, Direction.SOUTH_WEST,
		Direction.NORTH_EAST, Direction.NORTH_WEST};
	
	private static int gridScore2(MapLocation loc) throws GameActionException {
		RobotInfo ri = null;
		try {
			ri = rc.senseRobotAtLocation(loc);
		} catch (GameActionException e) {
			return 0;		// TODO ???
		}
		if ((Math.abs(loc.x-myHQ.x) + Math.abs(loc.y-myHQ.y)) % 2 == 0
				|| (ri != null && ri.ID != rc.getID())) {
			return 0;
		}
		int count = 0;
		for (Direction d : dirs) {
			try {
				ri = rc.senseRobotAtLocation(loc.add(d));
			} catch (GameActionException e) {
				return 0;	// TODO OR 1 ??????????????????????????????
			}
			if (ri != null && ri.type.isBuilding) {
				count++;
			}
		}
		if (count == 4) {
			return 0;
		}
		return 1;
	}
	
	private static MapLocation gridSpot() throws GameActionException {
		MapLocation current = rc.getLocation();
		MapLocation[] potLoc =
				MapLocation.getAllMapLocationsWithinRadiusSq(current, 100);

		int bestScore = Integer.MIN_VALUE;//gridScore2(current);
		MapLocation best = null;
		for (MapLocation l : potLoc) {
			if (!isNormal(l) || isOccupied(l)) {
				continue;
			}
			int s = gridScore2(l);
			rc.setIndicatorString(0, s+"");
			if (s == 0) {
				continue;
			}
			s = -l.distanceSquaredTo(myHQ);
			if (s > bestScore) {
				bestScore = s;
				best = l;
			}
		}
		return best;

	}

}
