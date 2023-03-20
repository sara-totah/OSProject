
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.*;

public class Simulator {
    private static ArrayList<Process> queue1;
    private static ArrayList<Process> queue2;
    private static ArrayList<Process> queue3;
    private static ArrayList<Process> queue4;
    private static ArrayList<Process> Processes;
    private static ArrayList<Process> processesInIO;
    private static ArrayList<Process> completedProcesses;
    private static int currentTime;
    private int q1;
    private int q2;
    private double alpha;
    int timeInQueue3;
    int previousProcessID;
    boolean sameProcess = false;
    private ArrayList<Process> array;
    private Process runningProcess;
    int finishingTime = 0;



    public Simulator(int q1, int q2, double alpha, ArrayList<Process> Processes) {
        this.queue1 = new ArrayList<Process>();
        this.queue2 = new ArrayList<Process>();
        this.queue3 = new ArrayList<Process>();
        this.queue4 = new ArrayList<Process>();
        this.completedProcesses = new ArrayList<Process>();
        this.Processes = Processes;
        this.processesInIO = new ArrayList<>();
        this.currentTime = 0;
        this.q1 = q1;
        this.q2 = q2;
        this.alpha = alpha;
        this.previousProcessID = -1;
        this.array = new ArrayList<Process>();
        this.runningProcess = null;
    }


    public void runSimulation() throws IOException, InterruptedException {
        //while there are still processes in the system
        while (!Processes.isEmpty() || !queue1.isEmpty() || !queue2.isEmpty() || !queue3.isEmpty() || !queue4.isEmpty() || !processesInIO.isEmpty()) {

            //print the current time
            System.out.println();
            System.out.println("Current Time == " + currentTime);

            //check the state of each process
            if (!Processes.isEmpty()) {
                for (Process process : Processes) {
                    //Process process = Processes.get(i);
                    if (process.getArrivalTime() == currentTime) {
                        array.add(process);
                        queue1.add(process);
                        process.setCurrentQueue(1);
                        System.out.println("Process with ID of " + process.getPID() + " Entered Queue number " + process.getCurrentQueue());
                    }
                }
            }

            if (!array.isEmpty()) {
                for (Process p : array) {
                    Processes.remove(p);
                }
            }

            if (!processesInIO.isEmpty()) {

                for (int i = 0; i < processesInIO.size(); i++) {
                    Process process = processesInIO.get(i);
                    //System.out.println("IO Remaining time for process " + process.getPID() + " : " + process.getRemainingIOTime());
                    if (process.getRemainingIOTime() == 0) {
                        if (process.getIOBurst().size() > 1) {
                            process.updateRemainingIOTime();
                        } else {
                            processesInIO.remove(process);
                            if (process.getCurrentQueue() == 1) {
                                queue1.add(process);
                                process.setqCount(0);
                                System.out.println("Process with ID " + process.getPID() + " has returned from I/O to Queue 1 ");
                            } else if (process.getCurrentQueue() == 2) {
                                queue2.add(process);
                                System.out.println("Process with ID " + process.getPID() + " has returned from I/O to Queue 2 ");
                            } else if (process.getCurrentQueue() == 3) {
                                queue3.add(process);
                                System.out.println("Process with ID " + process.getPID() + " has returned from I/O to Queue 3 ");
                            } else if (process.getCurrentQueue() == 4) {
                                queue4.add(process);
                                System.out.println("Process with ID " + process.getPID() + " has returned from I/O to Queue 4 ");
                            }
                        }

                        schedulePriority();
                    } else {
                        //System.out.println("Process with ID of " + process.getPID() + " IO Time " + process.getRemainingIOTime());
                        process.setRemainingIOTime(process.getRemainingIOTime() - 1);
                    }
                }
            }



            //Move processes from queue1 to queue2 if they exceed 10 time quanta
            if (!queue1.isEmpty()) {
                for (int i = 0; i < queue1.size(); i++) {
                    Process process = queue1.get(i);
                    if (process.getMaximumAllowedTimeInRR() >= 100) {
                        process.setMaximumAllowedTimeInRR(0);
                        process.setqCount(0);
                        queue1.remove(process);
                        queue2.add(process);
                        process.setCurrentQueue(2);
                        System.out.println("Process with ID of " + process.getPID() + " Moved from Queue 1 to Queue 2");
                    }
                }
            }
            //Move processes from queue2 to queue3 if they exceed 10 time quanta
            if (!queue2.isEmpty()) {
                for (int i = 0; i < queue2.size(); i++) {
                    Process process = queue2.get(i);
                    if (process.getMaximumAllowedTimeInRR() >= 100) {
                        process.setMaximumAllowedTimeInRR(0);
                        process.setqCount(0);
                        process.setPreemptiveCount(0);
                        queue2.remove(process);
                        queue3.add(process);
                        process.setCurrentQueue(3);
                        System.out.println("Process with ID of " + process.getPID() + " Moved from Queue 2 to Queue 3");
                    }
                }
            }
            //Move processes from queue3 to queue4 if they were preempted 3 times
            if (!queue3.isEmpty()) {
                for (int i = 0; i < queue3.size(); i++) {
                    Process process = queue3.get(i);
                    if (process.getPreemptiveCount() == 3) {
                        process.setPreemptiveCount(0);
                        queue3.remove(process);
                        queue4.add(process);
                        process.setCurrentQueue(4);
                        System.out.println("Process with ID of " + process.getPID() + " Moved from Queue 3 to Queue 4");
                    }
                }
            }

            // Check if there's a process running, if not, get the next process from the appropriate queue
            //the first queue has the highest priority
            schedulePriority();


            currentTime++;

            if (Processes.isEmpty() && queue1.isEmpty() && queue2.isEmpty() && queue3.isEmpty() && queue4.isEmpty() && processesInIO.isEmpty()) {
                finishingTime = currentTime;
                double totalWaitingTime = 0;
                double totalRunningTimeForAllProcesses = 0;

                for (int i = 0; i<completedProcesses.size(); i++) {
                    Process process = completedProcesses.get(i);
                    totalRunningTimeForAllProcesses += process.getTotalRunningTime();

                    totalWaitingTime += (process.getFinalTime() -process.getArrivalTime() - process.cpuBurstsTotal - process.ioBurstsTotal);
                }
                double cpuUtil = (totalRunningTimeForAllProcesses /(double)  (finishingTime-1));

                System.out.println("CPU Scheduling Statistics: ");
                System.out.println("CPU Utilization = " + cpuUtil);
                System.out.println("Average Waiting Time: " + (totalWaitingTime / completedProcesses.size()));
                displayGanttChart();
            }
        }
    }

    public void schedulePriority() {
        if (!queue1.isEmpty()) {
            Queue1Scheduling();
        } else if (!queue2.isEmpty()) {
            Queue2Scheduling();
        } else if (!queue3.isEmpty()) {
            Queue3Scheduling();
        } else if (!queue4.isEmpty()) {
            Queue4Scheduling();
        }
    }


    public void Queue1Scheduling() {
        if (!queue1.isEmpty()) {
            Process process = queue1.get(0); //take the first process
            if (process.getRemainingCPUTime() == 0) { //check the remaining time for this process if zero
                //check if this was the last CPU burst
                if (process.getCPUBurst().size() == 1) {  //if yes
                    queue1.remove(process);
                    completedProcesses.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished all its bursts and moved to completed processes list");
                    process.setFinalTime(currentTime);
                    schedulePriority();
                } else { // if no
                    process.setqCount(0);
                    process.setMaximumAllowedTimeInRR(0);
                    process.updateRemainingCPUTime();
                    queue1.remove(process);
                    processesInIO.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished its current burst and moved to IO list");
                    queue1.remove(process);
                    schedulePriority();
                }
            } else {
                if (process.getqCount() < q1) {
                    if (process.getCPUSize() == process.getCPUBurst().size() && process.getStartTime()==0) {
                        process.setStartTime(currentTime);
                    }
                    process.setTotalRunningTime(process.getTotalRunningTime() + 1);
                    process.setRemainingCPUTime(process.getRemainingCPUTime() - 1);
                    process.setqCount(process.getqCount() + 1);
                    process.setMaximumAllowedTimeInRR(process.getMaximumAllowedTimeInRR() + 1);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " is currently running");

                    process.updateTotalRunningTime();
                    runningProcess = process;
                    queue1.remove(process);
                    queue1.add(0, process);

                } else if (process.getqCount() == q1) { //qCount > q1
                    queue1.remove(process);
                    queue1.add(process);
                    process.setqCount(0);
                    schedulePriority();
                }
                if(process.getRemainingCPUTime() == 0 && process.getCPUBurst().size() > 1){
                    process.setqCount(0);
                    process.setMaximumAllowedTimeInRR(0);
                    process.updateRemainingCPUTime();
                    queue1.remove(process);
                    processesInIO.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished its current burst and moved to IO list");
                    queue1.remove(process);
                    schedulePriority();
                }
            }
        }
    }

    public void Queue2Scheduling() {
        if (!queue2.isEmpty()) {
            Process process = queue2.get(0); //take the first process
            if (process.getRemainingCPUTime() == 0) { //check the remaining time for this process if zero
                //check if this was the last CPU burst
                if (process.getCPUBurst().size() == 1) {  //if yes
                    queue2.remove(process);
                    completedProcesses.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished all its bursts and moved to completed processes list");
                    process.setFinalTime(currentTime);
                    schedulePriority();
                } else { // if no
                    process.setqCount(0);
                    process.setMaximumAllowedTimeInRR(0);
                    process.updateRemainingCPUTime();
                    queue2.remove(process);
                    processesInIO.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished its current burst and moved to IO list");
                    queue2.remove(process);
                    schedulePriority();
                }
            } else {

                if (process.getqCount() < q2) {
                    if (process.getCPUSize() == process.getCPUBurst().size() && process.getStartTime()==0) {
                        process.setStartTime(currentTime);
                    }
                    process.setTotalRunningTime(process.getTotalRunningTime() + 1);
                    process.setRemainingCPUTime(process.getRemainingCPUTime() - 1);
                    process.setqCount(process.getqCount() + 1);
                    process.setMaximumAllowedTimeInRR(process.getMaximumAllowedTimeInRR() + 1);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " is currently running");

                    process.updateTotalRunningTime();
                    runningProcess = process;
                    queue2.remove(process);
                    queue2.add(0, process);
                } else if (process.getqCount() == q2) { //qCount > q1
                    queue2.remove(process);
                    queue2.add(process);
                    process.setqCount(0);
                    schedulePriority();
                }
                if(process.getRemainingCPUTime() == 0 && process.getCPUBurst().size() > 1){
                    process.setqCount(0);
                    process.setMaximumAllowedTimeInRR(0);
                    process.updateRemainingCPUTime();
                    queue2.remove(process);
                    processesInIO.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished its current burst and moved to IO list");
                    queue2.remove(process);
                    schedulePriority();
                }
            }
        }
    }

    public void Queue3Scheduling() {
        if (!queue3.isEmpty()) {
            for (Process p : queue3) { //Update the estimated Remaining Time for the process
                p.updateEstimatedRemainingTime(alpha);
            }
            //Sort the processes
            Collections.sort(queue3, (p1, p2) -> Double.compare(p1.getEstimatedRemainingTime(), p2.getEstimatedRemainingTime()));
            Process process = queue3.get(0);
            int currentProcessID = process.getPID();

            if (process.getRemainingCPUTime() == 0) { //check the remaining time for this process if zero
                //check if this was the last CPU burst
                if (process.getCPUBurst().size() == 1) {  //if yes
                    completedProcesses.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished all its bursts and moved to completed processes list");
                    queue3.remove(process);
                    process.setFinalTime(currentTime);
                    schedulePriority();
                } else { // if no
                    process.updateRemainingCPUTime();
                    queue3.remove(process);
                    processesInIO.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished its current burst and moved to IO list");
                    process.setStartTimeInQueue3(currentTime + process.getRemainingIOTime());
                    schedulePriority();
                }
            } else {
                if (previousProcessID == -1 || (previousProcessID == currentProcessID)) {
                    if (process.getCPUSize() == process.getCPUBurst().size() && process.getStartTime()==0) {
                        process.setStartTime(currentTime);
                    }
                    process.setTotalRunningTime(process.getTotalRunningTime() + 1);
                    process.setRemainingCPUTime(process.getRemainingCPUTime() - 1);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " is currently running");

                    process.updateTotalRunningTime();
                    runningProcess = process;
                    previousProcessID = process.getPID();
                    process.setPreemptedBefore(0);
                } else {
                    if (process.getPreemptedBefore() == 0) {
                        process.setPreemptiveCount(process.getPreemptiveCount() + 1);
                        System.out.println("Process with ID : " + process.getPID() + "got Preempted for " + process.getPreemptiveCount() + " times till now");
                        process.setPreemptedBefore(1);
                    }
                    process.updateTotalRunningTime();
                    if (process.getPreemptiveCount() < 3) {
                        process.setRemainingCPUTime(process.getRemainingCPUTime() - 1);
                        System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " is currently running");

                    }

                }
                if(process.getRemainingCPUTime() == 0 && process.getCPUBurst().size() > 1){
                    process.setqCount(0);
                    process.setMaximumAllowedTimeInRR(0);
                    process.updateRemainingCPUTime();
                    queue3.remove(process);
                    processesInIO.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished its current burst and moved to IO list");
                    queue3.remove(process);
                    schedulePriority();
                }
            }
        }
    }

    public void Queue4Scheduling() {
        if (!queue4.isEmpty()) {
            Process process = queue4.get(0);
            if (process.getRemainingCPUTime() == 0) { //check the remaining time for this process if zero
                //check if this was the last CPU burst
                if (process.getCPUBurst().size() == 1) {  //if yes
                    completedProcesses.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished all its bursts and moved to completed processes list");
                    queue4.remove(process);
                    process.setFinalTime(currentTime);
                } else { // if no
                    process.updateRemainingCPUTime();
                    queue4.remove(process);
                    processesInIO.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished its current burst and moved to IO list");
                }
            } else {
                if (process.getCPUSize() == process.getCPUBurst().size()) {
                    process.setStartTime(currentTime);
                }
                process.setTotalRunningTime(process.getTotalRunningTime() + 1);
                process.setRemainingCPUTime(process.getRemainingCPUTime() - 1);
                System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " is currently running");

                process.updateTotalRunningTime();
                runningProcess = process;
                queue4.add(0, process);

                if(process.getRemainingCPUTime() == 0 && process.getCPUBurst().size() > 1){
                    process.setqCount(0);
                    process.setMaximumAllowedTimeInRR(0);
                    process.updateRemainingCPUTime();
                    queue4.remove(process);
                    processesInIO.add(process);
                    System.out.println("Process with ID of " + process.getPID() + " from Queue number " + process.getCurrentQueue() + " has finished its current burst and moved to IO list");
                    queue4.remove(process);
                    schedulePriority();
                }
            }
        }
    }
    public void displayQueuesStatus() {
        System.out.println("Current Time: " + currentTime);
        System.out.println("Running Process: " + runningProcess);
        System.out.println("Processes in IO: ");
        printProcess(processesInIO);
        System.out.println("Processes in Queue 1: ");
        printProcess(queue1);
        System.out.println("Processes in Queue 2: ");
        printProcess(queue2);
        System.out.println("Processes in Queue 3: ");
        printProcess(queue3);
        System.out.println("Processes in Queue 4: ");
        printProcess(queue4);
    }

    public void printProcess(ArrayList<Process> queue) {
        System.out.println("ID      ArrivalTime");
        System.out.println("===     ===========");
        for (Process p : queue) {
            System.out.println(p.getPID() + "         " + p.getArrivalTime());
        }
    }

    public void displayGanttChart() {
        System.out.println();
        System.out.println("****************************************************************************");
        System.out.println();
        System.out.println("Gantt Chart:");
        for (Process process : completedProcesses) {
            System.out.println("ID : " +process.getPID() + " start time: " + process.getStartTime() + " end time: " + process.getFinalTime());
        }
    }



    public void displayStatistics() {


    }

}
