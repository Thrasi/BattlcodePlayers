package T103;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

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
        
        // TODO this wont work when queue end goes overflow
        while (queueStart != queueEnd
        		&&
        		(!isAlive(rc.readBroadcast(queueStart))
        				||
        				rc.senseRobot(rc.readBroadcast(queueStart)).supplyLevel > 1000
        				)
        		) {
        	queueStart++;
        }
        
        if (rc.isCoreReady()) {
            if (queueStart != queueEnd && rc.getSupplyLevel() > 1000) {
                RobotInfo[] allies = getAllAllies();
                
                int target = rc.readBroadcast(queueStart);

                for (int i=0; i<allies.length; ++i) {
                    if (allies[i].ID == target) {
                    	double mySupplies = rc.getSupplyLevel();
                        if (rc.getLocation().distanceSquaredTo(allies[i].location) 
                        		<= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED
                        		&& mySupplies > 500) {
                        	
                        	int toTransfer = Math.min(5000, (int) (mySupplies-500));
//                        	if (allies[i].type == RobotType.TANK) {
//                        		toTransfer = Math.min(10000, (int) (mySupplies-500));
//                        	}
                            rc.transferSupplies(toTransfer, allies[i].location);
                            queueStart++;
                            if (queueStart == Channels.SUPPLYQEND) {
                            	queueStart = Channels.SUPPLYQSTART;
                            }
                            rc.broadcast(Channels.SUPPLYQSTART, queueStart);
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
            } else {

            //if (rc.getSupplyLevel() <= 1000) {
            	boolean moved = tryMoveTo( BaseBot.myHQ );
                if (!moved) {
                    // do something? or nothing
                }
            }
        }
        
		rc.yield();
	}
}
