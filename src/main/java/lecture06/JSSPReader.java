package lecture06;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class JSSPReader {

	private int numJobs; //number of jobs
	private int numResources; //number of resources
	private int[][][] problem;
	private int sumDurations;
	
	public JSSPReader(String filename) {
		Scanner scanner;
		try {
			scanner = new Scanner(new File(filename));
			numJobs = scanner.nextInt();
			numResources = scanner.nextInt();
			problem = new int[numJobs][numResources][2];
			sumDurations = 0;
			//System.out.println(numJobs + " jobs; " + numResources + " resources");
			for (int j=0;j<numJobs;j++){
				//System.out.println("Job " + j);
				for (int t=0;t<numResources;t++) {
					problem[j][t][0] = scanner.nextInt();
					problem[j][t][1] = scanner.nextInt();
					sumDurations += problem[j][t][1];
					/* System.out.println("   task: " + t 
					                   + "; res: " + problem[j][t][0] 
					                   + "; dur " + problem[j][t][1]);
					*/                   		    
				}
			}	
		}
		catch (IOException e) {
			System.out.println("File error:" + e);
		}
	}
		
	    
	public int getNumJobs() { return numJobs; }

	public int getNumResources() { return numResources; }

	public int[][][] getProblem() { return problem;}

	public int getSumDurations() { return sumDurations; }

	public static void main(String[] args) {
		JSSPReader reader = new JSSPReader("data\\jssp1.txt");
		System.out.println(reader.getNumJobs());
	}
}
