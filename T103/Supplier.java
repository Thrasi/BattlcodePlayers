package T103;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

/**
 * 
 * @author magnus
 *
 */
public class Supplier extends BaseBot {
	public Supplier(RobotController rc) {
		super(rc);
	}
	/**
	 * The supplier finds the robot on the front of the queue and moves to supply it.
	 * When it transfers the supplies it moves the start of the queue
	 */
	public void execute() throws GameActionException {
        int queueStart = rc.readBroadcast(Channels.SUPPLYQSTART);
        int queueEnd = rc.readBroadcast(Channels.SUPPLYQEND);
        if (rc.isCoreReady()) {
            if (queueStart != queueEnd && rc.getSupplyLevel() > 1000) {
                RobotInfo[] allies = getAllAllies();
                
                int target = rc.readBroadcast(queueStart);

                for (int i=0; i<allies.length; ++i) {
                    if (allies[i].ID == target) {
                        if (rc.getLocation().distanceSquaredTo(allies[i].location) 
                        		<= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
                            rc.transferSupplies(10000, allies[i].location);
                            rc.broadcast(Channels.SUPPLYQSTART, queueStart+1);
                        }
                        else {
                            boolean moved = tryMoveTo( allies[i].location );
                            if (!moved) {
                                // do something? or nothing
                            }
                        }
                        break;
                    }
                }
            }
            if (rc.getSupplyLevel() <= 1000) {
            	boolean moved = tryMoveTo( BaseBot.myHQ );
                if (!moved) {
                    // do something? or nothing
                }
            }
        }
        
		rc.yield();
	}
}
