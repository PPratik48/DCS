package DCS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

// Resource class represents a shared resource protected by the Ricart-Agrawala algorithm
class Resource {
    private Semaphore mutex; // Semaphore for mutual exclusion
    private List<Integer> pendingQueue; // Queue to track pending requests

    // Constructor initializes the mutex and pending queue
    public Resource() {
        mutex = new Semaphore(1); // Initialize mutex with permits = 1 (mutual exclusion)
        pendingQueue = new ArrayList<>(); // Initialize an empty pending queue
    }

    // Method for a process to request access to the critical section
    public void requestCS(int pid) {
        if (!pendingQueue.contains(pid)) {
            pendingQueue.add(pid); // Add process to the pending queue if not already present
        }

        // Iterate through the pending queue to send requests to other processes
        for (int i = 0; i < pendingQueue.size(); i++) {
            int p = pendingQueue.get(i);
            if (p != pid) {
                sendRequest(p, pid); // Send request to other processes
            }
        }

        try {
            mutex.acquire(); // Acquire the mutex semaphore for mutual exclusion
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method for a process to release access to the critical section
    public void releaseCS(int pid) {
        mutex.release(); // Release the mutex semaphore for mutual exclusion
        pendingQueue.remove((Integer) pid); // Remove the process from the pending queue
    }

    // Method to simulate sending a request message to another process
    private void sendRequest(int dest, int pid) {
        // Simulated method to send request
        System.out.println("Process " + pid + " sends request to process " + dest);
    }
}

// Process class represents a process that requests access to the critical section
class RicartAgrawalaProcess implements Runnable {
    private int id; // Process ID
    private Resource resource; // Shared resource protected by Ricart-Agrawala algorithm
    private boolean networkPartition = false;
    private boolean nodeFailed = false;
    private int communicationDelay = 0;
    private boolean isInCriticalSection;

 
    // Constructor initializes the process with an ID and the shared resource
    public RicartAgrawalaProcess(int id, Resource resource) {
        this.id = id;
        this.resource = resource;
    }
    
    public void setNetworkPartition(boolean partition) {
        this.networkPartition = partition;
    }
    

    public void setNodeFailed(boolean failed) {
        this.nodeFailed = failed;
    }
    
	public void setCommunicationDelay(int delay) {
        this.communicationDelay = delay;
    }
    public boolean isInCriticalSection() {
        return isInCriticalSection;
    }


    // Run method simulates the process requesting access to the critical section
    @Override
    public void run() {
        // Simulating some critical section requests
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep((int) (Math.random() * 1000)); // Simulate some processing time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resource.requestCS(id); // Request access to the critical section
            System.out.println("Process " + id + " is in critical section");
         //   isInCriticalSection=true;
            try {
                Thread.sleep((int) (Math.random() * 1000)); // Simulate some processing time in critical section
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resource.releaseCS(id); // Release access to the critical section
            isInCriticalSection=false;
            System.out.println("Process " + id + " is released from critical section");

        }
    }
}
/*
// Main class to run the Ricart-Agrawala Algorithm simulation
public class RicartAgrawalaAlgorithm {
    public static void main(String[] args) {
        Resource resource = new Resource(); // Create a new shared resource
        int numProcesses = 3; // Number of processes in the simulation

        // Create and start threads for each process
        for (int i = 0; i < numProcesses; i++) {
            new Thread(new RicartAgrawalaProcess(i, resource)).start();
        }
    }
}
*/


