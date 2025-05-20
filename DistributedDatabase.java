package DCS;

import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class RicartAgrawala {
    private int id;
    private List<Integer> requestQueue;
    private boolean[] replies;

    public RicartAgrawala(int id) {
        this.id = id;
        this.requestQueue = new ArrayList<>();
        this.replies = new boolean[5]; // Assuming 5 processes
    }

    public synchronized void requestCriticalSection(int clientId) {
        // Add client to the request queue
        requestQueue.add(clientId);

        // Send request messages to other processes
        for (int i = 0; i < 5; i++) { // Assuming 5 processes
            if (i != id) {
                sendRequest(id, clientId, i);
            }
        }

        // Wait until all replies received
        while (!allRepliesReceived()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void releaseCriticalSection(int clientId) {
        // Remove client from the request queue
        requestQueue.remove(Integer.valueOf(clientId));

        // Reset reply flags
        for (int i = 0; i < replies.length; i++) {
            replies[i] = false;
        }

        // Notify waiting threads
        notifyAll();
    }

    private void sendRequest(int senderId, int clientId, int receiverId) {
        // Simulate sending a request message
        System.out.println("Client " + senderId + " sent request message to Client " + receiverId);
    }

    private boolean allRepliesReceived() {
        for (boolean reply : replies) {
            if (!reply) {
                return false;
            }
        }
        return true;
    }

    public synchronized void receiveReply(int senderId) {
        // Mark reply from sender
        replies[senderId] = true;
        // Notify waiting threads if all replies received
        if (allRepliesReceived()) {
            notifyAll();
        }
    }
}




//Shared resource (database)
class Database {
 private Semaphore semaphore;

 public Database() {
     semaphore = new Semaphore(1); // Semaphore with initial permit count of 1 for mutual exclusion
 }

 public void acquireWriteLock(int clientId) throws InterruptedException {
     semaphore.acquire();
     System.out.println("Client " + clientId + " acquired write lock.");
 }

 public void releaseWriteLock(int clientId) {
     semaphore.release();
     System.out.println("Client " + clientId + " released write lock.");
 }

 public void read(int clientId) {
     System.out.println("Client " + clientId + " is reading from the database.");
     // Simulate database read operation
     try {
         Thread.sleep(1000); // Simulate read operation time
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
 }

 public void write(int clientId) {
     System.out.println("Client " + clientId + " is writing to the database.");
     // Simulate database write operation
     try {
         Thread.sleep(2000); // Simulate write operation time
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
 }
}

//Lamport's Algorithm for mutual exclusion
class LamportClient implements Runnable {

 private Database database;
 private LamportClock clock;

 public LamportClient( Database database, LamportClock clock) {
     this.database = database;
     this.clock = clock;
 }

 @Override
 public void run() {
	  
     try {
         for(int i=1; i<12;i++){
        	 System.out.println("----Inside LamportClient.");
        	 clock.tick();
             // Request write lock
        	 Random rand = new Random();
        	 int id = rand.nextInt(5);
        	 database.read(id);
             database.acquireWriteLock(id);
             // Critical section: Write to the database
             database.write(id);
             // Release write lock
             database.releaseWriteLock(id);
             // Sleep before next write operation
             Thread.sleep(1000); // Sleep for 1 seconds before next write operation
             System.out.println("---- LamportClient ends for iteration:"+i);
         }
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
 }
}

//Ricart-Agrawala Algorithm for mutual exclusion
class RicartAgrawalaClient implements Runnable {

 private Database database;
 public RicartAgrawalaClient(Database database ) {
     this.database = database;
 }

 @Override
 public void run() {
	 
     try {
    	 for(int i=1; i<12;i++) {
    		 System.out.println("----Inside RicartAgrawalaClient.");
    		 Random rand = new Random();
        	 int id = rand.nextInt(5);
        	 database.read(id);
             // Request write lock
             database.acquireWriteLock(id);
             // Critical section: Write to the database
             database.write(id);
             // Release write lock
             database.releaseWriteLock(id);
             // Sleep before next write operation
             Thread.sleep(1000); // Sleep for 1 seconds before next write operation
             System.out.println("---- RicartAgrawalaClient Ends for iteration:"+i);
         }
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
 }
}

public class DistributedDatabase {
 public static void main(String[] args) {
     // Initialize shared database
     Database database = new Database();

     // Initialize Lamport's Algorithm
    LamportClock clock = new LamportClock();
    LamportClient lamportClient = new LamportClient(database, clock);

     // Initialize Ricart-Agrawala Algorithm
     RicartAgrawalaClient ricartAgrawalaClient = new RicartAgrawalaClient(database);

     // Start Lamport's Algorithm client thread
    new Thread(lamportClient).start();

     // Start Ricart-Agrawala Algorithm client thread
     new Thread(ricartAgrawalaClient).start();
 }
}
