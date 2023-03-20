import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        WorkloadGenerator.main(args);

        //get the queue parameters from the user
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter time quantum for Queue1: ");
        int q1 = scanner.nextInt();

        System.out.println("Enter time quantum for Queue2: ");
        int q2 = scanner.nextInt();

        System.out.println("Enter alpha for Queue3: ");
        double alpha = scanner.nextDouble();

        scanner.close();

        ArrayList<Process> FileProcesses = new ArrayList<>(); //Add the processes in the file here

        // Read the processes from the output.txt file
        try (BufferedReader br = new BufferedReader(new FileReader("output.txt")))
        //try (BufferedReader br = new BufferedReader(new FileReader("case.txt")))
        {
            //represents each line in the file (each process)
            String s;
            while ((s = br.readLine()) != null) {
                String[] values = s.split(" "); //divide the string in the line to the values representing the process

                //get the parameters into different variables from the file
                int pid = Integer.parseInt(values[0]);  //process id
                int arrivalTime = Integer.parseInt(values[1]); //arrival time

                ArrayList<Integer> CPUBurst = new ArrayList<>(); //CPU Burst array
                int CPUBurstNum = 0; //number of CPU Bursts

                ArrayList<Integer> IOBurst = new ArrayList<>();
                int IOBurstNum = 0;

                //take the CPU Bursts from the file for each process
                // and add it to the CPUBurt array of the process
                //Same thing applies for the IO
                for (int i = 2; i <= values.length - 1; i = i + 2) {
                    CPUBurst.add(Integer.parseInt(values[i]));
                }

                for (int i = 3; i <= values.length - 1; i = i + 2) {
                    IOBurst.add(Integer.parseInt(values[i]));
                }

                CPUBurstNum = CPUBurst.size();
                IOBurstNum = IOBurst.size();


                Process p = new Process(pid, arrivalTime, CPUBurst, IOBurst);
                FileProcesses.add(p);

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Simulator simulator = new Simulator(q1, q2, alpha, FileProcesses);
        simulator.runSimulation();
    }
}