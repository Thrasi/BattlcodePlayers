package T101;
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
	// COMMUNICATION CHANNELS:
	static int numBEAVERS=2, numMINERS=3, numSOLDIERS=4,
			numBASHERS=5, numBARRACKS=6, numMINERFACTORY=7,
			numTANKFACTORY=8, numTANKS=9, numSUPPLYDEPOT=10;
	
	// MAXIMUM AMOUNT OF UNITS OF A SPECIFIC TYPE:
	static int MAXSOLDIERS=20, MAXBASHERS=20, MAXMINERS=15,
			MAXBEAVERS=3, MAXTANKS=30;
	
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
		
		public Direction getDirectionAwayFrom(MapLocation target) {
			Direction toTarget = rc.getLocation().directionTo(target);
			return toTarget.opposite();
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
		 * Attempt to spawn a robot of type.
		 * 
		 * @param type The type of robot to spawn
		 * @throws GameActionException
		 */
		public void tryToSpawn(RobotType type) throws GameActionException {
			if (type != null) {
				Direction newDir = getSpawnDirection(type);
				if (newDir != null) {
					rc.spawn(newDir, type);
				}
			}
		}
		
		/**
		 * Attempts to return a direction to enemy HQ, then tries the other directions.
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
		}

		public Direction getBuildDirection(RobotType type){
			Direction[] dirs = getDirectionToward(this.myHQ);
			for(Direction d : dirs){
				System.out.println("type: "+type);
				System.out.println("d: "+d);
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

		public void beginingOfTurn() throws GameActionException{

			if(rc.senseEnemyHQLocation() != null){
				this.theirHQ = rc.senseEnemyHQLocation();
			}
			transferSupplies();
			
//			int supplyLevel = rc.getSupplyLevel();
//			if (rc.)
		}

		private void transferSupplies() throws GameActionException {
			RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
			double lowestSupply = rc.getSupplyLevel();
			double transferAmount = 0;
			MapLocation supplyTarget = null;
			for (RobotInfo ri : nearbyAllies) {
				if (ri.supplyLevel < lowestSupply) {
					lowestSupply = ri.supplyLevel;
					transferAmount = (rc.getSupplyLevel() - lowestSupply )/2;
					supplyTarget = ri.location;
				}
			}
			if (supplyTarget != null) {
				rc.transferSupplies((int)transferAmount, supplyTarget);
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
			countRobots();
			
			int numBeavers = rc.readBroadcast(numBEAVERS);
			if (rc.isCoreReady() && rc.getTeamOre() >= 100  && numBeavers < MAXBEAVERS) {
				tryToSpawn(RobotType.BEAVER);
			}
			
			assignRallyPoints();
			
			rc.yield();
		}
		
		/**
		 * Here we decide where to rally our troops
		 * @throws GameActionException
		 */
		private void assignRallyPoints() throws GameActionException {
			// Decide the rally point
			MapLocation rallyPoint;
			if ( Clock.getRoundNum() < 1600 ) {
				rallyPoint = new MapLocation( (3*this.myHQ.x + this.theirHQ.x ) / 4 ,
											 (3*this.myHQ.y + this.theirHQ.y ) / 4);
			} else {
				rallyPoint = this.theirHQ;
			}
			rc.broadcast(0, rallyPoint.x);
			rc.broadcast(1, rallyPoint.y);
		}
		
		/**
		 * Counts the number of robots and broadcasts them for others to use.
		 * @throws GameActionException
		 */
		private void countRobots() throws GameActionException {
			RobotInfo[] myRobots = rc.senseNearbyRobots(999999, myTeam);
			int numSoldiers = 0;
			int numBashers = 0;
			int numBeavers = 0;
			int numBarracks = 0;
			int numMiners = 0;
			int numMinerFactory = 0;
			int numTankFactory = 0;
			int numTanks = 0;
			int numSupplyDepot = 0;
			for (RobotInfo r : myRobots) {
				RobotType type = r.type;
				if (type == RobotType.SOLDIER) {
					numSoldiers++;
				} else if (type == RobotType.BASHER) {
					numBashers++;
				} else if (type == RobotType.BEAVER) {
					numBeavers++;
				} else if (type == RobotType.BARRACKS) {
					numBarracks++;
				} else if (type == RobotType.MINER) {
					numMiners++;
				} else if (type == RobotType.MINER) {
					numMiners++;
				} else if (type == RobotType.MINERFACTORY) {
					numMinerFactory++;
				} else if (type == RobotType.TANKFACTORY) {
					numTankFactory++;
				} else if (type == RobotType.TANK) {
					numTanks++;
				} else if (type == RobotType.SUPPLYDEPOT) {
					numSupplyDepot++;
				}
			}
			rc.broadcast(numBEAVERS, numBeavers);
			rc.broadcast(numSOLDIERS, numSoldiers);
			rc.broadcast(numBASHERS, numBashers);
			rc.broadcast(numBARRACKS, numBarracks);
			rc.broadcast(numMINERS, numMiners);
			rc.broadcast(numMINERFACTORY, numMinerFactory);
			rc.broadcast(numTANKFACTORY, numTankFactory);
			rc.broadcast(numTANKS, numTanks);
			rc.broadcast(numSUPPLYDEPOT, numSupplyDepot);

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
//						System.out.println("mined, ore: "+ rc.senseOre(rc.getLocation()));
						rc.mine();
					} else {
						// move away from HQ
//						Direction dir = getDirectionAwayFrom(this.myHQ); 
						Direction dir = getRandomDirection();
						dir = getMoveDir(rc.getLocation().add(dir));
						if (dir != null) {
							rc.move(dir);
						}
					}
				} else {
					// build
					RobotType building = getNextBuilding();
					if (building == null) {
						// TODO: do something
					} else {
						Direction newDir = getBuildDirection(building);
						if (newDir != null) {
							rc.build(newDir,  building);
						}
					}
				}
			} 
			rc.yield();
		}
		
		private RobotType getNextBuilding() throws GameActionException {
			RobotType type = null;;
//			System.out.println("numOfMINERFACTORIES: "+rc.readBroadcast(numMINERFACTORY));
			if (rc.readBroadcast(numMINERFACTORY) < 1) {
				type = RobotType.MINERFACTORY;
			} else if (rc.readBroadcast(numBARRACKS) < 1) {
				type = RobotType.BARRACKS;
			} else if (rc.readBroadcast(numTANKFACTORY) < 1) {
				type = RobotType.TANKFACTORY;
			} else if (rc.readBroadcast(numSUPPLYDEPOT) < 2) {
				type = RobotType.SUPPLYDEPOT;
			}
				
			return type;
		}
	}

	public static class Barracks extends BaseBot{
		public Barracks(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			if (rc.isCoreReady() && rc.getTeamOre() > 200){
				RobotType type = null;
				if ( rc.readBroadcast(numSOLDIERS) < MAXSOLDIERS ) {
					type = RobotType.SOLDIER;
				} 
				else if ( rc.readBroadcast(numBASHERS) < MAXBASHERS ) {
					type = RobotType.BASHER;
				}
				tryToSpawn(type);
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
			if ( rc.isCoreReady()) {
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

	public static class TankFactory extends BaseBot{
		public TankFactory(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			if (rc.isCoreReady() && rc.getTeamOre() > 250){
				RobotType type = null;
				if ( rc.readBroadcast(numTANKS) < MAXTANKS ) {
					type = RobotType.TANK;
				}
				tryToSpawn(type);
			}
			rc.yield();
		}
	}

	public static class Tank extends BaseBot{
		public Tank(RobotController rc){
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
			int numMiners = rc.readBroadcast(numMINERS);
//			System.out.println("executes minerfactory");
			if (rc.isCoreReady() && rc.getTeamOre() >= 60  && numMiners < MAXMINERS) {
				tryToSpawn(RobotType.MINER);
			}
			rc.yield();
		}
	}

	public static class Miner extends BaseBot{
		public Miner(RobotController rc){
			super(rc);
		}

		public void execute() throws GameActionException{
			mine();
			rc.yield();
		}
		
		private void mine() throws GameActionException {
			if ( rc.isCoreReady() ) {
				if (rc.senseOre(rc.getLocation()) > 1) {
					rc.mine();
				} else {
					Direction dir = getRandomDirection();
					dir = getMoveDir(rc.getLocation().add(dir));
					if (dir != null) {
						rc.move(dir);
					}
				}
			}
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
			if (rc.isWeaponReady()) {
				RobotInfo[] enemies = getEnemiesInAttackingRange();
				if ( enemies.length > 0 ) {
					attackLeastHealthEnemies(enemies);
				}
			}
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
