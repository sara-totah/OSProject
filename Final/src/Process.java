import java.util.ArrayList;

public class Process {
    private int PID; //The process ID
    private int arrivalTime; //Time of arrival for the process
    private ArrayList<Integer> CPUBurst; //Array of cpuBursts of the process
    private ArrayList<Integer> IOBurst; //Array of ioBursts of the process
    private int remainingCPUTime; //remaining time for the current CPU Burst
    private int remainingIOTime; //remaining time for the current IO Burst
    private int currentQueue;
    private int maximumAllowedTimeInRR;
    private double waitingTime; //the total waiting time for the whole process
    private double finalTime; //finishing time of the process
    private int qCount;
    private int preemptiveCount;
    private double estimatedRemainingTime;
    private int startTimeInQueue3;
    private int preemptedBefore;
    private int startTime;
    private int CPUSize;
    private int totalRunningTime;
    int cpuBurstsTotal = 0;
    int ioBurstsTotal = 0;

    public Process(int PID, int arrivalTime, ArrayList<Integer> CPUBurst, ArrayList<Integer> IOBurst) {
        this.PID = PID;
        this.arrivalTime = arrivalTime;
        this.CPUBurst = CPUBurst;
        this.IOBurst = IOBurst;
        this.remainingCPUTime = CPUBurst.get(0);
        if (IOBurst.size() > 0) {
            this.remainingIOTime = IOBurst.get(0);
        } else {
            this.remainingIOTime = 0;
        }
        this.waitingTime = 0;
        this.finalTime = 0;
        this.maximumAllowedTimeInRR = 0;
        this.currentQueue = 0;
        this.estimatedRemainingTime = 0;
        this.preemptiveCount = 0;
        this.startTimeInQueue3 = 0;
        this.startTime = 0;
        this.CPUSize = CPUBurst.size();
        this.totalRunningTime = 0;

        for (int cpuburst : CPUBurst) {
            cpuBurstsTotal = cpuBurstsTotal + cpuburst;
        }
        for (int ioburst : IOBurst) {
            ioBurstsTotal = ioBurstsTotal + ioburst;
        }

    }

    public int getTotalRunningTime() {
        return totalRunningTime;
    }

    public void setTotalRunningTime(int totalRunningTime) {
        this.totalRunningTime = totalRunningTime;
    }

    public void updateTotalRunningTime(){
        this.totalRunningTime = this.totalRunningTime + 1;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getCPUSize() {
        return CPUSize;
    }


    public int getPreemptedBefore() {
        return preemptedBefore;
    }

    public void setPreemptedBefore(int preemptedBefore) {
        this.preemptedBefore = preemptedBefore;
    }

    public void setStartTimeInQueue3(int timeStartedInQueue3) {
        this.startTimeInQueue3 = timeStartedInQueue3;
    }

    public int getPID() {
        return PID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public ArrayList<Integer> getCPUBurst() {
        return CPUBurst;
    }

    public ArrayList<Integer> getIOBurst() {
        return IOBurst;
    }

    public int getRemainingCPUTime() {
        return remainingCPUTime;
    }

    public void setRemainingCPUTime(int remainingCPUTime) {
        this.remainingCPUTime = remainingCPUTime;
    }

    public int getRemainingIOTime() {
        return remainingIOTime;
    }

    public void setRemainingIOTime(int remainingIOTime) {
        this.remainingIOTime = remainingIOTime;
    }

    public void updateRemainingCPUTime() {
        if (CPUBurst.size() > 0) {
            this.CPUBurst.remove(0);
            this.remainingCPUTime = CPUBurst.get(0);
        }
    }

    public void updateRemainingIOTime() {
        if (IOBurst.size() > 0) {
            this.IOBurst.remove(0);
            this.remainingIOTime = IOBurst.get(0);
        }
    }


    public int getCurrentQueue() {
        return currentQueue;
    }

    public void setCurrentQueue(int currentQueue) {
        this.currentQueue = currentQueue;
    }

    public double getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(double finalTime) {
        this.finalTime = finalTime;
    }

    public int getqCount() {
        return qCount;
    }

    public void setqCount(int qCount) {
        this.qCount = qCount;
    }

    public int getMaximumAllowedTimeInRR() {
        return maximumAllowedTimeInRR;
    }

    public void setMaximumAllowedTimeInRR(int maximumAllowedTimeInRR) {
        this.maximumAllowedTimeInRR = maximumAllowedTimeInRR;
    }

    public int getPreemptiveCount() {
        return preemptiveCount;
    }

    public void setPreemptiveCount(int preemptiveCount) {
        this.preemptiveCount = preemptiveCount;
    }

    public double getEstimatedRemainingTime() {
        return estimatedRemainingTime;
    }

    public void setEstimatedRemainingTime(double estimatedRemainingTime) {
        this.estimatedRemainingTime = estimatedRemainingTime;
    }



    public void updateEstimatedRemainingTime(double alpha) {
        this.estimatedRemainingTime = alpha * this.getRemainingCPUTime() + (1 - alpha) * this.getEstimatedRemainingTime();
    }


}
