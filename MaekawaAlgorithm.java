package DCS;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

class Resource_Maekawa {
    private Semaphore semaphore;

    public Resource_Maekawa() {
        semaphore = new Semaphore(1); // Semaphore with initial permit count of 1 for mutual exclusion
    }

    public void request(int processId) throws InterruptedException {
        // Request access to the resource
        semaphore.acquire();
        System.out.println("Process " + processId + " acquired the resource.");
    }

    public void release(int processId) {
        // Release the resource
        semaphore.release();
        System.out.println("Process " + processId + " released the resource.");
    }
}

class Process implements Runnable {
    private int id;
    private List<Integer> quorum; // Quorum of processes that must grant permission
    private Resource_Maekawa resource;
    private boolean networkPartitioned;
    private boolean failed;
    private int communicationDelay;

    public Process(int id, List<Integer> quorum, Resource_Maekawa resource) {
        this.id = id;
        this.quorum = quorum;
        this.resource = resource;
        this.networkPartitioned = false;
        this.failed = false;
        this.communicationDelay = 0;
    }

    public void setNetworkPartitioned(boolean partitioned) {
        this.networkPartitioned = partitioned;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public void setCommunicationDelay(int delay) {
        this.communicationDelay = delay;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Simulate node failure
                if (failed) {
                    System.out.println("Process " + id + " has failed.");
                    break;
                }

                // Simulate network partition
                if (networkPartitioned && (id % 2 == 0)) {
                    System.out.println("Process " + id + " is in a network partition.");
                    Thread.sleep(1000); // Simulate waiting in partition
                    continue;
                }

                // Request permission to access the resource
                resource.request(id);

                // Critical section: Access the resource
                System.out.println("Process " + id + " is accessing the resource.");
                Thread.sleep(1000); // Simulate resource access

                // Release the resource after accessing
                resource.release(id);

                // Simulate communication delay
                Thread.sleep(communicationDelay);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class MaekawaAlgorithm {
    public static void main(String[] args) {
        // Create processes and resource
        int numProcesses = 5;
        List<Process> processes = new ArrayList<>();
        Resource_Maekawa resource = new Resource_Maekawa();

        // Define quorums for each process (example: each process selects 2 other processes as its quorum)
        List<List<Integer>> quorums = new ArrayList<>();
        for (int i = 0; i < numProcesses; i++) {
            List<Integer> quorum = new ArrayList<>();
            for (int j = 1; j <= 2; j++) {
                quorum.add((i + j) % numProcesses); // Example: process i selects (i+1)%numProcesses and (i+2)%numProcesses as its quorum
            }
            quorums.add(quorum);
        }

        // Create processes with their respective quorums
        for (int i = 0; i < numProcesses; i++) {
            Process process = new Process(i, quorums.get(i), resource);
            processes.add(process);
        }

        // Start threads for each process
        for (Process process : processes) {
            new Thread(process).start();
        }

        // Simulate network partition, node failure, and communication delay
        simulateNetworkPartition(processes.get(0));
        simulateNodeFailure(processes.get(1));
        simulateCommunicationDelay(processes.get(2), 2000);
    }

    private static void simulateNetworkPartition(Process process) {
        process.setNetworkPartitioned(true);
    }

    private static void simulateNodeFailure(Process process) {
        process.setFailed(true);
    }

    private static void simulateCommunicationDelay(Process process, int delay) {
        process.setCommunicationDelay(delay);
    }
}
