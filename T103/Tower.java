package T103;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Tower extends BaseBot {
	
	static boolean isSupplyLow = false;
	private static double prevHealth = -1;
	
	public Tower(RobotController rc) {
		super(rc);
	}
	
	@Override
	public void execute() throws GameActionException {
		tryShootMissilesOrWeakest();
		rc.setIndicatorString(0, getNearbyEnemies().length + "");
		boolean underAttack = prevHealth > rc.getHealth();
		rc.setIndicatorString(1, underAttack + "");
		prevHealth = rc.getHealth();
		
		System.out.println("Tower ID:"+rc.getID()+": isSupplyLow: "+isSupplyLow);
		isSupplyLow = addToQueue(isSupplyLow);
		
		rc.yield();
	}

}
