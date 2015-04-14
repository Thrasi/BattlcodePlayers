package T102;

/* This is built on a template for a player we found from robotplayerNPHard. 
 * That template had errors, but is a fine framework.  T100 is currently a shitty
 * swarm bot as described in the swarmbot video lectures.
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
	static int numBEAVERS = 2, numMINERS = 3, numSOLDIERS = 4, numBASHERS = 5,
			numBARRACKS = 6, numMINERFACTORY = 7, numTANKFACTORY = 8, numTANKS = 9,
			numSUPPLYDEPOT = 10;

	// MAXIMUM AMOUNT OF UNITS OF A SPECIFIC TYPE:
	static int MAXSOLDIERS = 20, MAXBASHERS = 20, MAXMINERS = 15, MAXBEAVERS = 3,
			MAXTANKS = 30;

	public static void run(RobotController SkyNet) {
		BaseBot myself = null;
		rc = SkyNet;

		rand = new Random(rc.getID());
		RobotType type = rc.getType();

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
			myself = new Drone(rc);
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

		while (true) {
			try {
				myself.go();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
}
