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
	
	private static int currentTarget = -1;
	
	// TODO fix when drones fly into range of attackers
	
	
	public Supplier(RobotController rc) {
		super(rc);
	}
	/**
	 * The supplier finds the robot on the front of the queue and moves to supply it.
	 * When it transfers the supplies it moves the start of the queue
	 */
	public void execute() throws GameActionException {
		tryMoveAway();
		if (currentTarget == -1) {
	        int queueStart = rc.readBroadcast(Channels.SUPPLYQSTART);
	        int queueEnd = rc.readBroadcast(Channels.SUPPLYQEND);
	        
	        int skipId = rc.readBroadcast(queueStart);
	        while (queueStart != queueEnd && (!isAlive(skipId) || rc.senseRobot(skipId).supplyLevel > 1000)) {
	        	queueStart++;
	        	if (queueStart == Channels.UPPERSUPPLYBOUND) {
	        		queueStart = Channels.LOWERSUPPLYBOUND;
	        	}
	        	skipId = rc.readBroadcast(queueStart);
	        }
	        
	        
	        if (queueStart != queueEnd && rc.getSupplyLevel() > 1000) {
	            int target = rc.readBroadcast(queueStart);
	            RobotInfo ally = rc.senseRobot(target);
	        	double mySupplies = rc.getSupplyLevel();

	        	if (rc.getLocation().distanceSquaredTo(ally.location) 
	            		<= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED
	            		&& mySupplies > 500) {
	            	
	            	int toTransfer = Math.min(4000, (int) (mySupplies-500));
//                  if (allies[i].type == RobotType.TANK) {
//                  	toTransfer = Math.min(10000, (int) (mySupplies-500));
//                  }
	                rc.transferSupplies(toTransfer, ally.location);
	            } else {
	            	currentTarget = ally.ID;
	                boolean moved = tryMoveTo( ally.location );
	                if (!moved) {
	                    // do something? or nothing
	                }
	            }
	            
	            queueStart++;
                if (queueStart == Channels.UPPERSUPPLYBOUND) {
                	queueStart = Channels.LOWERSUPPLYBOUND;
                }
                rc.broadcast(Channels.SUPPLYQSTART, queueStart);
	        }
		} else {
			if (isAlive(currentTarget)) {
				if (rc.getSupplyLevel() > 1000) {
					RobotInfo curr = rc.senseRobot(currentTarget);
		        	double mySupplies = rc.getSupplyLevel();

		        	if (rc.getLocation().distanceSquaredTo(curr.location) 
		            		<= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED
		            		&& mySupplies > 500) {
		            	
		            	int toTransfer = Math.min(4000, (int) (mySupplies-500));
//                    	if (allies[i].type == RobotType.TANK) {
//                    		toTransfer = Math.min(10000, (int) (mySupplies-500));
//                     	}
		                rc.transferSupplies(toTransfer, curr.location);
		                currentTarget = -1;
		            } else {
		                boolean moved = tryMoveTo( curr.location );
		                if (!moved) {
		                    // do something? or nothing
		                }
		            }
		        }
			} else {
				currentTarget = -1;
			}

		}

        
        if (rc.getSupplyLevel() <= 1000) {
        	boolean moved = tryMoveTo( BaseBot.myHQ );
            if (!moved) {
                // do something? or nothing
            }
        }
        
		rc.yield();
	}
}
