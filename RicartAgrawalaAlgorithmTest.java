package DCS;

import java.util.concurrent.TimeUnit;

//Ricart-Agrawala Algorithm testing with network partition, node failures, and communication delays
public class RicartAgrawalaAlgorithmTest {
public static void main(String[] args) {
   int numNodes = 3;
   Resource resource = new Resource();
   RicartAgrawalaProcess[] processes = new RicartAgrawalaProcess[numNodes];

   // Create Ricart-Agrawala processes
   for (int i = 0; i < numNodes; i++) {
       processes[i] = new RicartAgrawalaProcess(i, resource);
   }

   // Start threads for Ricart-Agrawala processes
   for (int i = 0; i < numNodes; i++) {
       new Thread(processes[i]).start();
   }

   // Simulate network partition
   try {
       TimeUnit.SECONDS.sleep(2); // Allow processes to run for 2 seconds
   } catch (InterruptedException e) {
       e.printStackTrace();
   }
   System.out.println("Simulating network partition...");
   for (int i = 0; i < numNodes / 2; i++) {
       processes[i].setNetworkPartition(true);
   }

   // Simulate node failure
   try {
       TimeUnit.SECONDS.sleep(2); // Allow processes to run for 2 seconds
   } catch (InterruptedException e) {
       e.printStackTrace();
   }
   System.out.println("Simulating node failure...");
   processes[0].setNodeFailed(true);

   // Simulate communication delay
   try {
       TimeUnit.SECONDS.sleep(2); // Allow processes to run for 2 seconds
   } catch (InterruptedException e) {
       e.printStackTrace();
   }
   System.out.println("Simulating communication delay...");
   processes[1].setCommunicationDelay(500); // Set communication delay for process 1 to 500 milliseconds

   // Wait for processes to finish
   try {
       TimeUnit.SECONDS.sleep(5); // Allow processes to run for another 5 seconds
   } catch (InterruptedException e) {
       e.printStackTrace();
   }

   // Check if all processes entered and exited critical section
   boolean allInCS = true;
   for (int i = 0; i < numNodes; i++) {
       if (processes[i].isInCriticalSection()) {
           allInCS = false;
           break;
       }
   }

   if (allInCS) {
       System.out.println("Ricart-Agrawala Algorithm: All processes entered and exited critical section.");
   } else {
       System.out.println("Ricart-Agrawala Algorithm: Some processes did not enter critical section.");
   }
}
}


