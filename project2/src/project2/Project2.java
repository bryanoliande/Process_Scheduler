package project2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Project2 {

	static Integer MAX_PROCESSES = 20;
	static private Integer NUM_PROCESSES = new Integer(0);
	static private Integer arrivalTimes[] = new Integer[MAX_PROCESSES];//ai
	static private Integer totalServiceTimes[] = new Integer[MAX_PROCESSES]; //ti
	static private Integer startTimes[] = new Integer[MAX_PROCESSES];
	static private Integer endTimes[] = new Integer[MAX_PROCESSES];
	static private Integer waitTimes[] = new Integer[MAX_PROCESSES];
	static private double averageTurnAroundTime;
	static private Process[] processArray = new Process[MAX_PROCESSES];
	static ArrayList<Process> processList = new ArrayList<Process>();
	static ArrayList<Process> processToRunList = new ArrayList<Process>();

	static private Integer realTimes[] = new Integer[MAX_PROCESSES]; //ri = ti + waitingTimes[i]
	//OR ri = endTimes[i] - arrivalTimes[i]
	
	public static void main(String[] args)
	{
	
	
		
		Scanner userInputScanner = new Scanner(System.in);
		System.out.print("Enter input file path: ");
		
		String inFilePath = userInputScanner.nextLine();
		inFilePath.trim();
		System.out.println("inFilePath: " + inFilePath);
		File inFile = new File(inFilePath);
		
		
		Scanner inScanner = null;
		try {
			inScanner = new Scanner(inFile);
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't open file");
		}
		
		String outFilePath = inFilePath.substring(0, inFilePath.length() - 10); //chop off "/input.txt"
		outFilePath = outFilePath.trim();
		outFilePath = outFilePath.concat("/13179240.txt");
		System.out.println("outFilePath: " + outFilePath);

		final File outFile = new File(outFilePath);
		
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(outFile.getAbsoluteFile(), true);
		} catch (IOException e) {
			System.out.println("Couldnt open filewriter");
		}
		
		final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	
		for(int i = 0; inScanner.hasNextInt(); i++)
		{
			arrivalTimes[i] = inScanner.nextInt();
			totalServiceTimes[i] = inScanner.nextInt();
			Process newProcess = new Process(i + 1, arrivalTimes[i], totalServiceTimes[i]);
			processArray[i] = newProcess;
			processList.add(newProcess);
			++NUM_PROCESSES;
		}
		
		printProcessList(); //for debugging
		
		
		fifo();
		averageTurnAroundTime = computeAverageTurnAroundTime();
		writeLineToOutputFile(bufferedWriter);
	
		System.out.println("JUST RAN FIFO");
		
		resetProcessToRunList();
		shortestJobFirst();
		averageTurnAroundTime = computeAverageTurnAroundTime();
		writeLineToOutputFile(bufferedWriter);
		
		resetProcessToRunList();
		shortestRemainingTime();
		averageTurnAroundTime = computeAverageTurnAroundTime();
		writeLineToOutputFile(bufferedWriter);
		
		resetProcessToRunList();
		multiLevelFeedback();
		averageTurnAroundTime = computeAverageTurnAroundTime();
		writeLineToOutputFile(bufferedWriter);
		
		
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println("Couldnt close bufferedWriter ERROR");
		}
		
	}

	//prints "T r1 r2 r3"
	public static void writeLineToOutputFile(BufferedWriter bufferedWriter)
	{
		System.out.println("In writeLineToOutputFile");
		
		DecimalFormat df = new DecimalFormat("#.00");
		df.setRoundingMode(RoundingMode.DOWN);
		
		try {
			bufferedWriter.write(df.format(averageTurnAroundTime) + " " + getRealTimesFormatted() + "\n");
		} catch (IOException e) {
			System.out.println("Something went wrong with writeLineToOutputFile()");
		}
	}
	
	//returns T
	public static double computeAverageTurnAroundTime()
	{
		System.out.println("ComputeAverageTurnAroundTime()");
		System.out.println("numProcesses = " + NUM_PROCESSES.toString());
		
		float sum = 0;
		for(int i = 1; i <= NUM_PROCESSES; ++i)
		{
			sum += realTimes[i];
		}
		
		System.out.println("sum = " + sum);
		return sum / NUM_PROCESSES;
		
	}
	
	//processList is sorted according to what scheduling algorithm is running
	//endTimes[i], realTimes[i] store values for the specific PROCESS i
	//so the process at processlist(i) does not necessarily correspond to 
	//the process at endTimes[i]
	public static Integer runProcesses(Integer timeCounter)
	{
		System.out.println("RUNPROCESSES FUNCtiON");
		printProcessToRunList();
		for(int i = 0; i < processToRunList.size(); i++)
		{
	
			Integer currProcessNumber =  (processToRunList.get(i).getProcessNumber());
			endTimes[currProcessNumber] = timeCounter + processToRunList.get(i).getTotalServiceTime();
			timeCounter +=  processToRunList.get(i).getTotalServiceTime();
			realTimes[currProcessNumber] = endTimes[currProcessNumber] - processToRunList.get(i).getArrivalTime();
			
			System.out.println("For process " + currProcessNumber.toString()
					+ " endTime = " + endTimes[currProcessNumber].toString()
					+ " timeCounter = " + timeCounter.toString() 
					+ " realTimes = " + realTimes[currProcessNumber].toString());
		}
		return timeCounter;
	}

	public static Integer runSingleProcess(Integer timeCounter)
	{
		System.out.println("RUN SINGLE PROCESS FUNCtiON");
		printProcessToRunList();
		for(int i = 0; i < 1; i++)
		{
	
			Integer currProcessNumber =  (processToRunList.get(i).getProcessNumber());
			endTimes[currProcessNumber] = timeCounter + processToRunList.get(i).getTotalServiceTime();
			timeCounter +=  processToRunList.get(i).getTotalServiceTime();
			realTimes[currProcessNumber] = endTimes[currProcessNumber] - processToRunList.get(i).getArrivalTime();
			
			System.out.println("For process " + currProcessNumber.toString()
					+ " endTime = " + endTimes[currProcessNumber].toString()
					+ " timeCounter = " + timeCounter.toString() 
					+ " realTimes = " + realTimes[currProcessNumber].toString());
		}
		return timeCounter;
	}
	
	public static void fifo()
	{
		//start time off at the first process's arrival time
		//on the discussion board the professor stated that the processes are listed in order of arrival time
		
		
		System.out.println("In fifo function");
		printProcessList();
		
		for(int i = 0; i < processList.size(); i++)
		{
			processToRunList.add(processList.get(i));
		}
		//run the process
		runProcesses(processList.get(0).getArrivalTime());
	
	}
	
	
	
	public static void shortestJobFirst()
	{
	
		System.out.println("In SJF function");
	
		//Collections.sort(processList, Process.getProcessBySJFOrder());
		//System.out.println("In SJF function - just sorted by SJFOrder");
		printProcessList();
		
		
		
		Integer timeCounter = processList.get(0).getArrivalTime(); 
		int numProcessed = 0;
		Integer hasProcessBeenRan[] = new Integer[NUM_PROCESSES + 1]; //start at 1
		
		for(int i = 0; i < NUM_PROCESSES + 1; i++)
		{
			hasProcessBeenRan[i] = 0;
		}
		
		while(numProcessed < NUM_PROCESSES - 1) //-1 bc it needs to be
		{
			if(timeCounter < processList.get(numProcessed).getArrivalTime())
			{
				timeCounter = processList.get(numProcessed).getArrivalTime();
			}
			
			//gets the window of processes
			for(int i = 0; (i < NUM_PROCESSES && timeCounter >= processList.get(i).getArrivalTime()); i++)
			{
				if(hasProcessBeenRan[processList.get(i).getProcessNumber()] == 0)
				{
					processToRunList.add(processList.get(i));
					System.out.println("Added to process list");
					System.out.println("Timecounter = " + timeCounter.toString());
					System.out.println("processList.get(i).getArrivalTime() = " + processList.get(i).getArrivalTime().toString());;
					System.out.println("i = " +  i);
					System.out.println("numProcesses = " + NUM_PROCESSES.toString());
				}
				
			}
			
			
			System.out.println("out of for loop");
			printProcessToRunList();
			Collections.sort(processToRunList, Process.getProcessBySJFOrder());
			if(processToRunList.size() != 0)
			{
				System.out.println("TRYING TO ACCESS INDEX: " + processToRunList.get(0).getProcessNumber());
				hasProcessBeenRan[processToRunList.get(0).getProcessNumber()] = 1;
				timeCounter = runSingleProcess(timeCounter);
				++numProcessed;
			}
			resetProcessToRunList();
			
			System.out.println("NUMPROCESSED = " + numProcessed);
		}
		
		//run the process
		
		
		
	}
	
	
	static void shortestRemainingTime()
	{
		 //arrivalTimes[] 
		 //totalServiceTimes[] 
		 HashMap<Integer, Integer> remainingTimes = new HashMap<Integer, Integer>(); // (ProcessNumber, remainingTime)
		 
		Integer shortestProcessID = null;
		int SRT = 1000000000;
		for(int quantum = 0, numProcessesServiced = 0; numProcessesServiced < NUM_PROCESSES ; ++quantum)
		{
			for(int i = 0; i < NUM_PROCESSES; i++)
			{
				//if a new process has arrived
				if(processList.get(i).getArrivalTime() == quantum  )
				{
					//add process and its service time to hashmap
					remainingTimes.put(processList.get(i).getProcessNumber(), processList.get(i).getTotalServiceTime());
					System.out.println(processList.get(i).getProcessNumber() + " Just arrived");
				}
					
			}
	
			//find the process with the shortest remaining time in the dictionary 
			//assuming no process has a greater runtime than 100000000
			
			
			for (Integer key : remainingTimes.keySet()) {
			    Integer value = remainingTimes.get(key);
			    
			    if(value < SRT)
			    {
			    	shortestProcessID = key;
			    	SRT = value;
				    System.out.println("At quantum = " + quantum + " shortestProcessID= " + key + ", SRT = " + value);

			    }
			}
			
			//and run it for one quantum
			//Dictionary[processWithShortestRemainingTime] -= 1
			SRT = SRT - 1;
			remainingTimes.put(shortestProcessID, SRT);
			System.out.println("Quantum: " + quantum + " Just ran: " + shortestProcessID );
			
		    //if process is done running, remove it from the dictionary and mark its endTime
			if(remainingTimes.get(shortestProcessID) == 0)
			{
				remainingTimes.remove(shortestProcessID);
				endTimes[shortestProcessID] = quantum + 1; //b/c it starts at 0
				++numProcessesServiced;
				System.out.println("Just finished running " + shortestProcessID + " at quantum+1 = " + (quantum+1)); 
				SRT = 1000000000; //need to reset the SRT
			}
		}
		
		//calculate realtimes
		for(int i = 0; i < NUM_PROCESSES; ++i)
		{
			System.out.println("OK i = " + i);
			realTimes[i+1] = endTimes[i+1] - processArray[i].getArrivalTime();
			System.out.println("realTimes[i+1] = " + realTimes[i+1] + 
					"endTimes[i+1] = " + endTimes[i+1] + "processArray[i].getArrivalTime() = "
							+ processArray[i].getArrivalTime()); 
		}
	}
	
	
	
	
	static void multiLevelFeedback()
	{
		/*
		Levels:
		5----------------------(1 unit) **After one unit total time move down
		4----------------------(2 units) **After three unit total time move down
		3----------------------(4 units) **After seven unit total time move down
		2----------------------(8 units) **After 15 unit total time move down
		1----------------------(16 units)
		
		Used the pseudo-code for the algorithm:
		https://en.wikipedia.org/wiki/Multilevel_feedback_queue
		*/
		
		 HashMap<Integer, Integer> remainingTimes = new HashMap<Integer, Integer>(); // (ProcessNumber, remainingTime)
		 HashMap<Integer, Integer> timeGiven = new HashMap<Integer, Integer>(); //(ProcessNumber, quantumsRan)
		 MLF MLFQueue = new MLF();
			
		 //run all the processes
			for(int quantum = 0, numProcessesServiced = 0; numProcessesServiced < NUM_PROCESSES ; ++quantum)
			{
				for(int i = 0; i < NUM_PROCESSES; i++)
				{
					//if a new process has arrived
					if(processList.get(i).getArrivalTime() == quantum  )
					{
						//add process and its service time to hashmap and add it to the top level of the MLFQueue
						remainingTimes.put(processList.get(i).getProcessNumber(), processList.get(i).getTotalServiceTime());
						MLFQueue.addToMLFQueue(processList.get(i));
						timeGiven.put(processList.get(i).getProcessNumber(), 0);
						System.out.println(processList.get(i).getProcessNumber() + " Just arrived");
						
					}
						
				}
				if(!remainingTimes.isEmpty())
				{
					//find the process with the highest priority and run it for one quantum
					Process highestPriorityProcess = MLFQueue.findHighestPriorityProcess();
					Integer remainingTimeForHPP = remainingTimes.get(highestPriorityProcess.getProcessNumber()) - 1;
					Integer quantumsGivenForHPP = timeGiven.get(highestPriorityProcess.getProcessNumber());
					
					System.out.println("highest priority process = " + highestPriorityProcess.getProcessNumber()
										+ "quantumsGivenForHPP = " + quantumsGivenForHPP
										+ "current QUantum = " + quantum);
					
					remainingTimes.put(highestPriorityProcess.getProcessNumber(), remainingTimeForHPP);
					Integer quantumsGiven =  (quantumsGivenForHPP +1);
					timeGiven.put(highestPriorityProcess.getProcessNumber(), quantumsGiven);
					System.out.println("Quantum: " + quantum + " Just ran: " + highestPriorityProcess.getProcessNumber() );
					
				    //if process is done running, remove it from the dictionary&MLFQueue and mark its endTime
					if(remainingTimes.get(highestPriorityProcess.getProcessNumber()) == 0)
					{
						remainingTimes.remove(highestPriorityProcess.getProcessNumber());
						timeGiven.remove(highestPriorityProcess.getProcessNumber());
						MLFQueue.removeFromMLFQueue(highestPriorityProcess.getProcessNumber());
						endTimes[highestPriorityProcess.getProcessNumber()] = quantum + 1; //b/c it starts at 0
						++numProcessesServiced;
						System.out.println("Just finished running " + highestPriorityProcess.getProcessNumber() + " at quantum+1 = " + (quantum+1)); 
					}
					
					MLFQueue.moveProcessesDownIfNeedBe(timeGiven, processArray);
				}
			}
			
			//calculate realtimes
			for(int i = 0; i < NUM_PROCESSES; ++i)
			{
				System.out.println("OK i = " + i);
				realTimes[i+1] = endTimes[i+1] - processArray[i].getArrivalTime();
				System.out.println("realTimes[i+1] = " + realTimes[i+1] + 
						"endTimes[i+1] = " + endTimes[i+1] + "processArray[i].getArrivalTime() = "
								+ processArray[i].getArrivalTime()); 
			}
		}
		
	
	
	
	public static void printProcessList()
	{
		System.out.println("processList:");
		System.out.println("numprocesses = " + processList.size());
		for(int i = 0; i < processList.size(); i++)
		{
			processList.get(i).printAllVariables();
			System.out.println();
		}
	}
	
	public static void printProcessToRunList()
	{
		System.out.println("processToRunList:");
		System.out.println("numprocesses = " + processToRunList.size());
		for(int i = 0; i < processToRunList.size(); i++)
		{
			processToRunList.get(i).printAllVariables();
			System.out.println();
		}
	}
	
	public static void resetProcessToRunList()
	{
		processToRunList = new ArrayList<Process>();
		//for(int i = 0; i < NUM_PROCESSES; ++i)
		//{
			//processToRunList.add(processArray[i]);
		//}
	}
	
	public static String getRealTimesFormatted()
	{
		String realTimesFormatted = "";
		System.out.println("GETREALTIMESFORMATTED");
		for(int i = 1; i <= NUM_PROCESSES; i++)
		{
			realTimesFormatted += realTimes[i].toString() + " ";
		}
		
		return realTimesFormatted;
	}
}
