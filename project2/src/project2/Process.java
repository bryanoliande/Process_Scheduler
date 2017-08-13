package project2;
import java.util.Comparator;



public class Process{

	private Integer processNumber;
	private Integer arrivalTime;
	private Integer totalServiceTime;
	
	Process(Integer processNumber, Integer arrivalTime, Integer totalServiceTime)
	{
		this.processNumber = processNumber;
		this.arrivalTime = arrivalTime;
		this.totalServiceTime = totalServiceTime;
	}
	
	public Integer getProcessNumber() {
		return processNumber;
	}

	public void setProcessNumber(Integer processNumber) {
		this.processNumber = processNumber;
	}

	public Integer getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Integer arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Integer getTotalServiceTime() {
		return totalServiceTime;
	}

	public void setTotalServiceTime(Integer totalServiceTime) {
		this.totalServiceTime = totalServiceTime;
	}

	public void printAllVariables()
	{
		System.out.println("processNumber: " + processNumber);
		System.out.println("arrivalTime: " + arrivalTime);
		System.out.println("totalServiceTime: " + totalServiceTime);
	}



	public static Comparator<Process> getProcessBySJFOrder()
	{   
		Comparator comp = new Comparator<Process>(){
		@Override
	     
	    	 public int compare(Process p1, Process p2)
	    	  {
	    	        // Assume no nulls, and simple ordinal comparisons

	    	        // First by arrival time
	    	        //int arrivalTimeResult = p1.getArrivalTime().compareTo(p2.getArrivalTime());
	    	        //if (arrivalTimeResult != 0)
	    	        //{
	    	          //  return arrivalTimeResult;
	    	        //}

	    	        // if they have the same arrival time, sort by totalservicetime
	    	        int serviceTimeResult = p1.getTotalServiceTime().compareTo(p2.getTotalServiceTime());
	    	        return serviceTimeResult;     
	         }
	        
	 };
	 return comp;
	}  

}
