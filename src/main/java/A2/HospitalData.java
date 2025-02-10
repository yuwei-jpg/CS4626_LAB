package A2;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class HospitalData {
	
	private final int numOps; //number of operations
	private final int numRooms; //number of rooms
	private final int lastTime; //last time slot any operation can be active in
	private final int[] reqAssistants; //number of assistants required for each op
	private final int[] durations; //duration of each op
	private final int[] last; //last active time for each op
	private final int[] cleaning; //cleaning time required after each op
	private final int[] reqSurgeons; //number of surgeons required for each op
	private final int[] assistantsOnDuty; //number of assistants on duty at each time slot
	private final int[] surgeonsOnDuty; //number of surgeons on duty at each time point
	
    public HospitalData(String filename) throws IOException {
    	/*
    	 * Assumes data is in file in the following format:
    	 *    number-of-item-types  max-wgt-capacity
    	 * followed by a sequence of the right number of lines, with each line being
    	 *    weight profit stock
    	 * for the corresponding item type.
    	 */
	    Scanner scanner = new Scanner(new File(filename));
	    numOps = scanner.nextInt();
	    numRooms = scanner.nextInt();
	    lastTime = scanner.nextInt();
	    reqAssistants = new int[numOps];
	    durations = new int[numOps];
	    last = new int[numOps];
	    cleaning  = new int[numOps];
	    reqSurgeons  = new int[numOps];
	    assistantsOnDuty = new int[lastTime+1];
	    surgeonsOnDuty = new int[lastTime+1];
	    for (int i=0;i<numOps;i++){
	        reqAssistants[i] = scanner.nextInt();
	    }
	    for (int i=0;i<numOps;i++){
	        durations[i] = scanner.nextInt();
	    }
	    for (int i=0;i<numOps;i++){
	        last[i] = scanner.nextInt();
	    }
	    for (int i=0;i<numOps;i++){
	        cleaning[i] = scanner.nextInt();
	    }
	    for (int i=0;i<numOps;i++){
	        reqSurgeons[i] = scanner.nextInt();
	    }
	    for (int i=0;i<lastTime;i++){
	        assistantsOnDuty[i] = scanner.nextInt();
	    }
	    for (int i=0;i<lastTime;i++){
	        surgeonsOnDuty[i] = scanner.nextInt();
	    }
	    scanner.close();
    }
    
    public int getNumOps() {
    	return numOps;
    }
    
    public int getNumRooms() {
    	return numRooms;
    }
    
    public int getLastTime() {
    	return lastTime;
    }
    
    public int[] getReqAssistants() {
    	return reqAssistants;
    }
    
    public int[] getDurations() {
    	return durations;
    }
    
    public int[] getLast() {
    	return last;
    }
    
    public int[] getCleaning() {
    	return cleaning;
    }
    
    public int[] getReqSurgeons() {
    	return reqSurgeons;
    }

    public int[] getAssistantsOnDuty() {
    	return assistantsOnDuty;
    }

    public int[] getSurgeonsOnDuty() {
    	return surgeonsOnDuty;
    }
}
