package DCS;

import java.util.concurrent.TimeUnit;

//LamportClock class representing Lamport logical clock
class LamportClock {
 private int time;

 public LamportClock() {
     this.time = 0;
 }

 public int getTime() {
     return time;
 }

 public synchronized void tick() {
     time++;
 }

 public synchronized void receiveEvent(int receivedTime) {
     time = Math.max(receivedTime, time) + 1;
 }
}

class LamportProcess implements Runnable {
    private int id;
    private LamportClock[] clocks;
    private boolean networkPartition = false;
    private boolean nodeFailed = false;
    private int communicationDelay = 0;
    private boolean printVal = true;

    public LamportProcess(int id, LamportClock[] clocks) {
        this.id = id;
        this.clocks = clocks;
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
    public void setprintValue(boolean value) {
        this.printVal = value;
    }
    
    @Override
    public void run() {
        while (true) {
            // Simulate event
            clocks[id].tick();
           /* if(printVal) {
            System.out.println("Process " + id + " ticked at time " + clocks[id].getTime());
            }*/
            // Simulate sending event to other processes
            for (int i = 0; i < clocks.length; i++) {
                if (i != id && !networkPartition && !nodeFailed) {
                    try {
                        // Simulate communication delay
                        TimeUnit.MILLISECONDS.sleep(communicationDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    clocks[i].receiveEvent(clocks[id].getTime());
               /*     if(printVal) {
                    System.out.println("Process " + id + " sent event to process " + i + " at time " + clocks[id].getTime());
                    }*/
                }
            }

            // Sleep for some time before next event
            try {
                TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
