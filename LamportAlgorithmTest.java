package DCS;

import java.util.concurrent.TimeUnit;
import java.util.Random;

//Lamport's Algorithm testing with network partition, node failures, and communication delays
public class LamportAlgorithmTest {
 public static void main(String[] args) {
     int numNodes = 15;
     LamportClock[] clocks = new LamportClock[numNodes];
     LamportProcess[] processes = new LamportProcess[numNodes];

     // Create Lamport clocks and processes
     for (int i = 0; i < numNodes; i++) {
         clocks[i] = new LamportClock();
         processes[i] = new LamportProcess(i, clocks);
     }

     // Start threads for Lamport processes
     for (int i = 0; i < numNodes; i++) {
         new Thread(processes[i]).start();
     }

     // Simulate network partition
     try {
         TimeUnit.SECONDS.sleep(2); // Allow processes to run for 2 seconds
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
     System.out.println("======Simulating network partition======>");
     for (int i = 0; i < numNodes / 2; i++) {
         processes[i].setNetworkPartition(true);
         System.out.println("Node:"+i);
     }
     if((numNodes%2)==0) {
         System.out.println("Number of Nodes after network partition==["+numNodes / 2+","+(numNodes / 2)+"]");
     }
     else {
         System.out.println("Number of Nodes after network partition==["+numNodes / 2+","+((numNodes / 2)+1)+"]");
 
     }
     // Simulate node failure
     try {
         TimeUnit.SECONDS.sleep(2); // Allow processes to run for 2 seconds
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
     Random rand = new Random();
     System.out.println("======Simulating node failure======>");
     //processes[0].setNodeFailed(true);
     int rand_int = rand.nextInt(numNodes);
     processes[rand_int].setNodeFailed(true);
     System.out.println("#Node failure for Process:"+ rand_int);
     System.out.println("#Nodes working for Processes:");
     for (int i = 0; i < numNodes; i++) {
         if (i != rand_int) {
             System.out.print(i+",");
         }
     }
     System.out.println("");
     // Simulate communication delay
     try {
         TimeUnit.SECONDS.sleep(2); // Allow processes to run for 2 seconds
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
     System.out.println("======Simulating communication delay======>");
   //  processes[1].setCommunicationDelay(500); // Set communication delay for process 1 to 500 milliseconds
     int rand_new = rand.nextInt(numNodes);
     if(rand_new!=rand_int) {
     processes[rand_new].setCommunicationDelay(500); // Set communication delay for process 1 to 500 milliseconds
     System.out.println("#Communication Delay Set for Process:"+ rand_new);

     }
     // Wait for processes to finish
     try {
         TimeUnit.SECONDS.sleep(5); // Allow processes to run for another 5 seconds
     } catch (InterruptedException e) {
         e.printStackTrace();
     }

     // Check if clocks are synchronized
     boolean synchronizedClocks = true;
     for (int i = 1; i < numNodes; i++) {
         if (clocks[i].getTime() != clocks[0].getTime()) {
             synchronizedClocks = false;
            // processes[0].setprintValue(false);
            // processes[i].setprintValue(false);
             break;
         }
     }

     if (synchronizedClocks) {
         System.out.println("######Lamport's Algorithm: Clocks are synchronized.######");
     } else {
         System.out.println("######Lamport's Algorithm: Clocks are not synchronized.######");
     }
     
    
 }
}

 