package T100;
/* This is built on a template for a player we found from robotplayerNPHard.  That template had errors, but
 * is a fine framework.  T100 is currently a shitty swarm bot as described in the swarmbot video lectures.
 * From here we ... profit.
 * 
 * 
 * */

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	static RobotController rc;
	static Random rand;
	public static void run(RobotController SkyNet) {
		BaseBot myself = null;
		rc = SkyNet;
		rand = new Random(rc.getID());
		RobotType type = rc.getType();

		if(type == RobotType.HQ){
			myself = new HQ(rc);
		}else if (type == RobotType.AEROSPACELAB){
			myself = new AeroSpaceLab(rc);
		}else if (type == RobotType.BARRACKS){
			myself = new Barracks(rc);
		}else if (type == RobotType.BASHER){
			myself = new Basher(rc);
		}else if (type == RobotType.BEAVER){
			myself = new Beaver(rc);
		}else if (type == RobotType.COMMANDER){
			myself = new Commander(rc);
		}else if (type == RobotType.COMPUTER){
			myself = new Computer(rc);
		}else if (type == RobotType.DRONE){
			myself = new Drone(rc);
		}else if (type == RobotType.HELIPAD){
			myself = new Helipad(rc);
		}else if (type == RobotType.LAUNCHER){
			myself = new Launcher(rc);
		}else if (type == RobotType.MINER){
			myself = new Miner(rc);
		}else if (type == RobotType.MINERFACTORY){
			myself = new MinerFactory(rc);
		}else if (type == RobotType.MISSILE){
			myself = new Missle(rc);
		}else if (type == RobotType.SOLDIER){
			myself = new Soldier(rc);
		}else if (type == RobotType.TANK){
			myself = new Tank(rc);
		}else if (type == RobotType.TANKFACTORY){
			myself = new TankFactory(rc);
		}else if (type == RobotType.TECHNOLOGYINSTITUTE){
			myself = new TechnologyInstitute(rc);
		}else if (type == RobotType.TOWER){
			myself = new Tower(rc);
		}else if (type == RobotType.TRAININGFIELD){
			myself = new TrainingField(rc);
		}else{
			myself = new BaseBot(rc);
		}
		
		while(true){
			try{
				myself.go();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class BaseBot{
		protected RobotController rc;
		protected MapLocation myHQ, theirHQ;
		protected Team myTeam, theirTeam;
		
		public BaseBot(RobotController rc){
			this.rc = rc;
			this.myHQ = rc.senseHQLocation();
			this.theirHQ = rc.senseEnemyHQLocation();
			this.myTeam = rc.getTeam();
			this.theirTeam = this.myTeam.opponent();
			
		}
		
		public Direction getRandomDirection() {
			return Direction.values()[rand.nextInt(8)];
		}

		public Direction[] getDirectionToward(MapLocation target){
			Direction toTarget = rc.getLocation().directionTo(target);
			Direction dirs[] = {toTarget, toTarget.rotateLeft(),
								toTarget.rotateRight(),
								toTarget.rotateLeft().rotateLeft(), 
								toTarget.rotateRight().rotateRight()};
			return dirs;
		}

		public Direction getMoveDir(MapLocation target){
			Direction[] dirs = getDirectionToward(target);
			for (Direction d : dirs){
				if(rc.canMove(d)){
					return d;
				}
			}
			return null;
		}
		/**
		 * Attempts to return a direction to enemy HQ, then tries the other direction.
		 * @param type
		 * @return Direction to spawn to or null if all is occupied.
		 */
		public Direction getSpawnDirection(RobotType type) {
			Direction dir = rc.getLocation().directionTo(this.theirHQ);
			for (int i=0;i<8;i++) {
				if(rc.canSpawn(dir, type)){
					return dir;
				}
				dir = dir.rotateRight();
			}
			return null;
//			Direction[] dirs = getDirectionToward(this.theirHQ);
//			for(Direction d : dirs){
//				if(rc.canSpawn(d, type)){
//					return d;
//				}			
//			}
//			
//			for(Direction d : dirs){
//				if(rc.canSpawn(d.opposite(), type)){
//					return d.opposite();
//				}			
//			}
//			
//			return null;
		}

		public Direction getBuildDirection(RobotType type){
			Direction[] dirs = getDirectionToward(this.myHQ);
			for(Direction d : dirs){
				if(rc.canBuild(d, type)){
					return d;
				}
			}
			return null;
		}

		public RobotInfo[] getAllies(){
			RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, this.myTeam);
			return allies;
		}

		public RobotInfo[] getEnemiesInAttackingRange(){
			RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, this.theirTeam);
			return enemies;
		}

		public void attackLeastHealthEnemies(RobotInfo[] enemies) throws GameActionException{
			if(enemies.length == 0){
				return;
			}
			double minHealth = Integer.MAX_VALUE;
			MapLocation toAttack = null;
			for(RobotInfo info : enemies){
				if(info.health < minHealth){
					toAttack = info.location;
					minHealth = info.health;
				}
			}
			rc.attackLocation(toAttack);
		}
		
		public void moveAround() {
			
		}

		public void beginingOfTurn(){

			if(rc.senseEnemyHQLocation() != null){
				this.theirHQ = rc.senseEnemyHQLocation();
			}
		}

		public void execute() throws GameActionException{
			rc.yield();
		}

		public void endOfTurn(){		
		}

		public void go() throws GameActionException{
			beginingOfTurn();
			execute();
			endOfTurn();
		}
	}

	public static class HQ extends BaseBot{
		public HQ(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			int numBeavers = rc.readBroadcast(2);
			
			if (rc.isCoreReady() && rc.getTeamOre() >= 100  && numBeavers < 10) {
				Direction spawnDirection = getSpawnDirection(RobotType.BEAVER);
				if (spawnDirection != null) {
					rc.spawn(spawnDirection, RobotType.BEAVER);
					rc.broadcast(2,	 numBeavers+1);
				}
			}
			
			MapLocation rallyPoint;
			if ( Clock.getRoundNum() < 600 ) {
				rallyPoint = new MapLocation( (this.myHQ.x + this.theirHQ.x ) / 2 ,
											 (this.myHQ.y + this.theirHQ.y ) /2);
			} else {
				rallyPoint = this.theirHQ;
			}
			rc.broadcast(0, rallyPoint.x);
			rc.broadcast(1, rallyPoint.y);
			
			rc.yield();
		}
	}

	public static class Beaver extends BaseBot{
		public Beaver(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			if ( rc.isCoreReady() ) {
				if (rc.getTeamOre() < 500 ) {
					// mine
					if (rc.senseOre(rc.getLocation()) > 7) {
						System.out.println("mined, ore: "+ rc.senseOre(rc.getLocation()));
						rc.mine();
					} else { 
						Direction dir = getMoveDir(this.theirHQ);
						if (dir != null) {
							rc.move(dir);
						}
					}
				} else {
					// build Barracks
					Direction newDir = getBuildDirection(RobotType.BARRACKS);
					if (newDir != null) {
						rc.build(newDir,  RobotType.BARRACKS);
					}
				}
			} 
			rc.yield();
		}
	}

	public static class Barracks extends BaseBot{
		public Barracks(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			if (rc.isCoreReady() && rc.getTeamOre() > 200){
				Direction newDir = getSpawnDirection(RobotType.SOLDIER);
				if (newDir != null) {
					rc.spawn(newDir, RobotType.SOLDIER);
				}
			}
				
			rc.yield();
		}	
	}

	public static class Soldier extends BaseBot{
		public Soldier(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			RobotInfo[] enemies = getEnemiesInAttackingRange();
			if ( enemies.length > 0 ) {
				if (rc.isWeaponReady()) {
					attackLeastHealthEnemies(enemies);
				}
			} else if ( rc.isCoreReady()) {
				int rallyX = rc.readBroadcast(0);
				int rallyY = rc.readBroadcast(1);
				MapLocation rallyPoint = new MapLocation(rallyX, rallyY);
				
				Direction newDir = getMoveDir(rallyPoint);
				
				if (newDir != null) {
					rc.move(newDir);
				}
			}
			rc.yield();
		}
	}

	public static class Basher extends BaseBot{
		public Basher(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class TankFactory extends BaseBot{
		public TankFactory(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class Tank extends BaseBot{
		public Tank(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class Helipad extends BaseBot{
		public Helipad(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class Drone extends BaseBot{
		public Drone(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class AeroSpaceLab extends BaseBot{
		public AeroSpaceLab(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class Launcher extends BaseBot{
		public Launcher(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class MinerFactory extends BaseBot{
		public MinerFactory(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class Miner extends BaseBot{
		public Miner(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class TechnologyInstitute extends BaseBot{
		public TechnologyInstitute(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class Computer extends BaseBot{
		public Computer(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class TrainingField extends BaseBot{
		public TrainingField(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class Commander extends BaseBot{
		public Commander(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class Tower extends BaseBot{
		public Tower(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}

	public static class Missle extends BaseBot{
		public Missle(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			rc.yield();
		}
	}
}
