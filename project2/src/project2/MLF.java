package project2;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;




public class MLF {
	
	/*
	Levels:
	5----------------------(1 unit)
	4----------------------(2 units)
	3----------------------(4 units)
	2----------------------(8 units)
	1----------------------(16 units)
	*/
	
	private ArrayList<LinkedList<Process>> MLFQueue; // the queue itself
	
	private LinkedList<Process> levelOneList;
	private LinkedList<Process> levelTwoList;
	private LinkedList<Process> levelThreeList;
	private LinkedList<Process> levelFourList;
	private LinkedList<Process> levelFiveList;

	
	//Highest to lowest levels 5, 4, 3, 2, 1
	public MLF()
	{
		System.out.println("MLF default ctor");
		MLFQueue = new ArrayList<LinkedList<Process>>();
		levelOneList = new LinkedList<Process>();
		levelTwoList = new LinkedList<Process>();
		levelThreeList = new LinkedList<Process>();
		levelFourList = new LinkedList<Process>();
		levelFiveList = new LinkedList<Process>();

		MLFQueue.add(levelOneList);
		MLFQueue.add(levelTwoList);
		MLFQueue.add(levelThreeList);
		MLFQueue.add(levelFourList);
		MLFQueue.add(levelFiveList);

	}


	//add to level five
	public void addToMLFQueue(Process processToAdd)
	{
		MLFQueue.get(4).add(processToAdd);
		printMLFQueue();
	}
	
	
	public void removeFromMLFQueue(Integer processIdToRemove)
	{
		for(int i = 4; i >= 0; i--)
		{
			for(int j = 0; j < MLFQueue.get(i).size(); j++)
			{
				if(processIdToRemove.equals(MLFQueue.get(i).get(j).getProcessNumber()))
				{
					Process pcbToRemove = MLFQueue.get(i).get(j);
					MLFQueue.get(i).remove(pcbToRemove);
				}
			}
		}
					
	}
	
	public LinkedList<Process> getListOnLevel(Integer level)
	{
	
			if(level.equals(5)){return levelFiveList;}
			if(level.equals(4)){return levelFourList;}
			if(level.equals(3)){return levelThreeList;}
			if(level.equals(2)){return levelTwoList;}
			return levelOneList;

		
	}
	
	public Process findHighestPriorityProcess()
	{
		Process process = null;
		
		System.out.print("In findHighestPriority Process(), ");
		for(int i = 4; i >= 0; i--)
		{
			for(int j = 0; j < MLFQueue.get(i).size(); j++)
			{
				System.out.println("Encountered " + MLFQueue.get(i).get(j).getProcessNumber());
				process = MLFQueue.get(i).get(j);
				break;
			}
			if(process != null)
			{
				break;
			}
		}
		
		System.out.println("Returning: " + process.getProcessNumber());
		return process; 
	}
	//timeGiven: (ProcessNumber, quantumsRan)
	public void moveProcessesDownIfNeedBe(HashMap<Integer, Integer> timeGiven, Process[] processArray)
	{
		/*
		Levels:
			5----------------------(1 unit) **After one unit total time move down
			4----------------------(2 units) **After three unit total time move down
			3----------------------(4 units) **After seven unit total time move down
			2----------------------(8 units) **After 15 unit total time move down
			1----------------------(16 units)*/
		
		
		for (Integer processNumber : timeGiven.keySet()) {
		    Integer quantumsRan = timeGiven.get(processNumber);
		    Integer level = findLevelProcessIsOn(processNumber);
		    Integer levelBelow = level-1;
		    System.out.println("process# = " + processNumber + "quantumsRan = " + quantumsRan);
		    if(levelBelow == 0)
		    {
		    	levelBelow = 1; //just hardcoding this so it doesnt crash... very sloppy code
		    }
		    
		    LinkedList<Process> listOnLevelBelow = getListOnLevel(levelBelow);
		    
		   
		    
		    if( (level == 5) && (quantumsRan == 1) )
		    {
		    	removeFromMLFQueue(processNumber);
		    	listOnLevelBelow.add(processArray[processNumber-1]); //-1 b/c its zero indexed
		    }
		    if( (level == 4) && (quantumsRan == 3) )
		    {
		    	removeFromMLFQueue(processNumber);
		    	listOnLevelBelow.add(processArray[processNumber-1]); //-1 b/c its zero indexed
		    }
		    if( (level == 3) && (quantumsRan == 7) )
		    {
		    	removeFromMLFQueue(processNumber);
		    	listOnLevelBelow.add(processArray[processNumber-1]); //-1 b/c its zero indexed
		    }
		    if( (level == 2) && (quantumsRan == 15) )
		    {
		    	removeFromMLFQueue(processNumber);
		    	listOnLevelBelow.add(processArray[processNumber-1]); //-1 b/c its zero indexed
		    }
		}
	}
	
	Integer findLevelProcessIsOn(Integer processID)
	{
		
			Process process = null;
			Integer level = null;
			System.out.print("In findLevelProcessIsOn(), ");
			for(int i = 4; i >= 0; i--)
			{
				for(int j = 0; j < MLFQueue.get(i).size(); j++)
				{
					if(MLFQueue.get(i).get(j).getProcessNumber() == processID)
					{
						level = i + 1;
						break;
					}
				}
			
			}
			if(level != null)
			{
				System.out.println("Returning: " + level);
			}
			else
			{
				System.out.println("SOMETHING WENT HORRIBLY WRONG IN findLevelProcessIsOn()");
			}
			return level;
		}
	
	public void printMLFQueue()
	{
		for(int i = 4; i >= 0; i--)
		{
			System.out.print("Level: " + (i + 1) + ": ");
			for(int j = 0; j < MLFQueue.get(i).size(); j++)
			{
				System.out.print(MLFQueue.get(i).get(j).getProcessNumber()+ " ");
			}
			System.out.println("");
		}
	}

	
}

