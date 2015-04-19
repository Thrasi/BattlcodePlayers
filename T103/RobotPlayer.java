package T103;

/* This is built on a template for a player we found from robotplayerNPHard. 
 * That template had errors, but is a fine framework.  T100 is currently a shitty
 * swarm bot as described in the swarmbot video lectures.
 * From here we ... profit.
 * 
 * 
 * */


import battlecode.common.*;

public class RobotPlayer {
	
	private static RobotController rc;
	

	// MAXIMUM AMOUNT OF UNITS OF A SPECIFIC TYPE:
	static int MAXSOLDIERS = 20, MAXBASHERS = 20, MAXMINERS = 15, MAXBEAVERS = 3,
			MAXTANKS = 30;

	/**
	 * Main run function
	 * @param SkyNet killer robots
	 */
	public static void run(RobotController SkyNet) {
		BaseBot myself = null;
		rc = SkyNet;

		RobotType type = rc.getType();

		try {
			if (type == RobotType.HQ) {
				myself = new HQ(rc);
			} else if (type == RobotType.AEROSPACELAB) {
				myself = new AeroSpaceLab(rc);
			} else if (type == RobotType.BARRACKS) {
				myself = new Barracks(rc);
			} else if (type == RobotType.BASHER) {
				myself = new Basher(rc);
			} else if (type == RobotType.BEAVER) {
				myself = new Beaver(rc);
			} else if (type == RobotType.COMMANDER) {
				myself = new Commander(rc);
			} else if (type == RobotType.COMPUTER) {
				myself = new Computer(rc);
			} else if (type == RobotType.DRONE) {
				try {
					boolean isAlive = true;
					try {
						rc.senseRobot(rc.readBroadcast(Channels.SUPPLIERID));
					} catch (GameActionException e) {
						isAlive = false;
					}
					if (rc.getType() == RobotType.DRONE
							&& (needsSupplier(rc) || !isAlive)) {
						
						myself = new Supplier(rc);
						rc.broadcast(Channels.numSUPPLIERS, rc.readBroadcast(Channels.numSUPPLIERS) + 1);
					    rc.broadcast(Channels.SUPPLIERID, rc.getID());
					} else {
						myself = new Drone(rc);
					}
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else if (type == RobotType.HELIPAD) {
				myself = new Helipad(rc);
			} else if (type == RobotType.LAUNCHER) {
				myself = new Launcher(rc);
			} else if (type == RobotType.MINER) {
				myself = new Miner(rc);
			} else if (type == RobotType.MINERFACTORY) {
				myself = new MinerFactory(rc);
			} else if (type == RobotType.MISSILE) {
				myself = new Missle(rc);
			} else if (type == RobotType.SOLDIER) {
				myself = new Soldier(rc);
			} else if (type == RobotType.TANK) {
				myself = new Tank(rc);
			} else if (type == RobotType.TANKFACTORY) {
				myself = new TankFactory(rc);
			} else if (type == RobotType.TECHNOLOGYINSTITUTE) {
				myself = new TechnologyInstitute(rc);
			} else if (type == RobotType.TOWER) {
				myself = new Tower(rc);
			} else if (type == RobotType.TRAININGFIELD) {
				myself = new TrainingField(rc);
			} else {
				myself = new BaseBot(rc);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				myself.go();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean needsSupplier(RobotController rc) throws GameActionException {
		if (rc.readBroadcast(Channels.numSUPPLIERS) == 0) {
			return true;
		}
		return false;
	}

	
}
