import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class WorkloadGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int numProcesses = scanner.nextInt();
        System.out.print("Enter maximum arrival time: ");
        int maxArrivalTime = scanner.nextInt();
        System.out.print("Enter maximum number of CPU bursts: ");
        int maxCPUBurst = scanner.nextInt();
        System.out.print("Enter minimum IO burst duration: ");
        int minIO = scanner.nextInt();
        System.out.print("Enter maximum IO burst duration: ");
        int maxIO = scanner.nextInt();
        System.out.print("Enter minimum CPU burst duration: ");
        int minCPU = scanner.nextInt();
        System.out.print("Enter maximum CPU burst duration: ");
        int maxCPU = scanner.nextInt();
        System.out.print("Enter file name: ");
        String fileName = scanner.next();

        Random rand = new Random();

        try {

            FileWriter writer = new FileWriter(fileName);
            for (int i = 0; i < numProcesses; i++) {
                int arrivalTime = rand.nextInt(maxArrivalTime);
                int numCPUBurst = rand.nextInt(maxCPUBurst) + 1;

                writer.write(i + " " + arrivalTime + " ");
                for (int j = 0; j < numCPUBurst; j++) {
                    int cpuBurst = rand.nextInt(maxCPU - minCPU + 1) + minCPU;
                    writer.write(cpuBurst + " ");
                    if (j != numCPUBurst - 1) {
                        int ioBurst = rand.nextInt(maxIO - minIO + 1) + minIO;
                        writer.write(ioBurst + " ");
                    }
                }
                writer.write("\n");
            }
            writer.close();
            System.out.println("Workload generated successfully and saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

// ----------------------------------------------------------------------------------------------------------------------------------------------------//
// ----------------------------------------------------------------------------------------------------------------------------------------------------//
// ----------------------------------------------------------------------------------------------------------------------------------------------------//
    }
}
