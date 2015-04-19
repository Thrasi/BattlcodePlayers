package T103;

import battlecode.common.GameActionException;

import static T103.BaseBot.rc;


public class Channels {

	// COMMUNICATION CHANNELS:
		public static int
				numBEAVERS = 2, 
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
				
			// CHARGE:
				SWARMIDXSOLDIER = 100,
				SWARMIDXBASHER = 101,
				SWARMIDXTANK = 102,
				SWARMIDXDRONE = 103,
				SWARMFIRSTX = 110,
				SWARMFIRSTY = 130,
				SWARMSET = 150,
				SWARMSETFLOOD = 170,
				SWARMFLOODIDX = 190,
				SWARMPRIMARY = 210,
				
			// TOWERS
				TOWERID = 230,
				TOWERDANGERLEVEL = 236,
				TOWERUNDERATTACK = 242,
				
			// SUPPLY QUEUE:
				SUPPLIERID = 296, 
				numSUPPLIERS = 297,
				SUPPLYQSTART = 298,
				SUPPLYQEND = 299,
				LOWERSUPPLYBOUND = 300,
				UPPERSUPPLYBOUND = 799,
				
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
				
				MAPSIZECLASS = 911,
				
			// BUILD QUEUE:
				BUILDQSTART = 6000,
				BUILDQEND = 6001,
				BUILDQLO = 6002,
				BUILDQHI = 6099,
				
			// FLOODING
				FLOODREQUEST = 19000,
				FLOODINDEX = 19001,
				FLOODQUEUESET = 19002,
				FLOODQUEUECOUNT = 19003,
				FLOODQUEUEFIRST = 19100,
				FLOODQUEUELAST = 19499,
				
				FLOODACTIVEINDEX1 = 19600,
				FLOODACTIVE1 = 19601,
				FLOODLASTUSED1 = 19606,
				FLOODFIRST1 = 20000,
				FLOODLAST1 = 34999,
				
				FLOODACTIVEINDEX2 = 19602,
				FLOODACTIVE2 = 19603,
				FLOODLASTUSED2 = 19607,
				FLOODFIRST2 = 35000,
				FLOODLAST2 = 49999,
						
				FLOODACTIVEINDEX3 = 19604,
				FLOODACTIVE3 = 19605,
				FLOODLASTUSED3 = 19608,
				FLOODFIRST3 = 50000,
				FLOODLAST3 = 64999
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
