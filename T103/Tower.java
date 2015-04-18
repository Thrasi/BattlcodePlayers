package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import static T103.Channels.set;
import static T103.Channels.reset;

public class Tower extends BaseBot {
	
	// Health level in previous turn
	private static double prevHealth = -1;
	
	// Local id
	private static int id;
	
	
	public Tower(RobotController rc) throws GameActionException {
		super(rc);
		
		// Set tower's local id
		for (int i = 0; i < 6; i++) {
			if (rc.readBroadcast(Channels.TOWERID + i) == rc.getID()) {
				id = i;
				break;
			}
		}
	}
	
	@Override
	public void execute() throws GameActionException {
		tryShootMissilesOrWeakest();
		
		// Scream if under attack
		boolean underAttack = prevHealth > rc.getHealth();
		prevHealth = rc.getHealth();
		if (underAttack) {
			set(Channels.TOWERUNDERATTACK + id);
		} else {
			reset(Channels.TOWERUNDERATTACK + id);
		}
		
		// Danger level
		int enemiesClose = getNearbyEnemies().length;
		rc.broadcast(Channels.TOWERDANGERLEVEL, enemiesClose);
		
		isSupplyLow = addToQueue(isSupplyLow);
		
		rc.yield();
	}

}
