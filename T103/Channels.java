package T103;

import battlecode.common.GameActionException;

import static T103.BaseBot.rc;


public class Channels {

	// COMMUNICATION CHANNELS:
		public static int numBEAVERS = 2, 
				numMINERS = 3, 
				numSOLDIERS = 4, 
				numBASHERS = 5,
				numBARRACKS = 6, 
				numMINERFACTORY = 7, 
				numTANKFACTORY = 8, 
				numTANKS = 9,
				numSUPPLYDEPOT = 10, 
				numAEROSPACELAB = 11, 
				numCOMMANDER = 12, 
				numCOMPUTER = 13,
				numDRONE = 14, 
				numHELIPAD = 15, 
				numLAUNCHERS = 16, 
				numMISSILE = 17,
				numTECHNOLOGYINSTITUTE = 18, 
				numTOWER = 19, 
				numTRAININGFIED = 20,
		
			// SUPPLY QUEUE:
				SUPPLIERID = 296, 
				numSUPPLIERS = 297,
				SUPPLYQSTART = 298,
				SUPPLYQEND = 299,
				
			// EXPLORING ROBOTS
				CORNERBEAVER = 800,
				expDRONECOUNT = 801,
				expDRONE = 805,
				expDRONEDONE = 825,
				expOFFSET = 845,
				expLOCCOUNT = 1004,
				expSTARTED = 1005,
				expLOCFIRST = 1010,
				expLOCLAST = 5000,
				
			// MAP
				MAPWIDTH = 900,
				MAPHEIGHT = 901,
				TOPLEFTX = 902,
				TOPLEFTY = 903,
				MAPSET = 904,
				
				MAPCORNER1SET = 905,
				MAPCORNER1X = 906,
				MAPCORNER1Y = 907,
				MAPCORNER2SET = 908,
				MAPCORNER2X = 909,
				MAPCORNER2Y = 910,
				
				MAPBROADCASTED = 915,
				MAPFIRST = 20000,
				MAPLAST = 35000,

			// FLOODING
				FLOODREQUEST = 39000,
				FLOODINDEX = 39001,
				FLOODQUEUESET = 39002,
				FLOODQUEUECOUNT = 39003,
				FLOODACTIVEINDEX = 39004,
				FLOODACTIVE = 39005,
				FLOODQUEUEFIRST = 39100,
				FLOODQUEUELAST = 39499,
				FLOODFIRST = 40000,
				FLOODLAST = 55000
			;
		
		
		/**
		 * Sets the given channel to 1. Channel is supposed to act as a flag (boolean).
		 * @param channel channel to set
		 * @throws GameActionException if channel doesn't exist
		 */
		public static void set(int channel) throws GameActionException {
			rc.broadcast(channel, 1);
		}
		
		/**
		 * Sets the given channel to 0.
		 * @param channel channel to reset
		 * @throws GameActionException if channel doesn't exist
		 */
		public static void reset(int channel) throws GameActionException {
			rc.broadcast(channel, 0);
		}
		
		/**
		 * Checks whether the channel is set (== 1).
		 * @param channel channel to check
		 * @return true if channel is set, false otherwise
		 * @throws GameActionException  if channel doesn't exist
		 */
		public static boolean isSet(int channel) throws GameActionException {
			return rc.readBroadcast(channel) == 1;
		}
}
