package project2;

import java.util.Comparator;

public class SJFComparator implements Comparator<Process>
{
    public int compare(Process p1, Process p2)
    {
        // Assume no nulls, and simple ordinal comparisons

        // First by arrival time
        int arrivalTimeResult = p1.getArrivalTime().compareTo(p2.getArrivalTime());
        if (arrivalTimeResult != 0)
        {
            return arrivalTimeResult;
        }

        // if they have the same arrival time, sort by totalservicetime
        int serviceTimeResult = p1.getTotalServiceTime().compareTo(p2.getTotalServiceTime());
        return serviceTimeResult;     
    }
}