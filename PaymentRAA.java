package DCS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;

class PaymentProcessor {
    private volatile boolean[] requesting;
    private volatile boolean[] deferred;
    private volatile int numNodes;
    private volatile int repliedCount;
    private volatile boolean processingPayment;
    private volatile List<Integer> replyDeferredQueue;
    private volatile int holdingNodeId;

    public PaymentProcessor(int numNodes) {
        this.numNodes = numNodes;
        requesting = new boolean[numNodes];
        deferred = new boolean[numNodes];
        replyDeferredQueue = new ArrayList<>();
        repliedCount = 0;
        processingPayment = false;
        holdingNodeId = -1;
        for (int i = 0; i < numNodes; i++) {
            requesting[i] = false;
            deferred[i] = false;
        }
    }

    public synchronized void processPayment(int id) {
        requesting[id] = true;

        for (int j = 0; j < numNodes; j++) {
            if (j != id) {
                while (requesting[j] || (deferred[j])) {
                    // Wait until request can be sent
                }
            }
        }

        replyDeferredQueue.clear();
        repliedCount = 0;
        for (int j = 0; j < numNodes; j++) {
            if (j != id) {
                if (requesting[j]) {
                    // Send request
                    System.out.println("Node " + id + " sending request to Node " + j);
                    // Simulating network delay
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        while (repliedCount < (numNodes - 1)) {
            // Wait for replies
        }

        // Access the payment processor
        synchronized (this) {
            processingPayment = true;
            holdingNodeId = id;
            System.out.println("Node " + id + " is processing payment");
            // Simulate payment processing time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processingPayment = false;
            holdingNodeId = -1;
        }

        // Release deferred requests
        for (Integer i : replyDeferredQueue) {
            synchronized (this) {
                deferred[i] = false;
            }
        }

        requesting[id] = false;
    }

    public synchronized void replyRequest(int fromNodeId) {
        if (processingPayment || (fromNodeId != holdingNodeId)) {
            System.out.println("Node " + holdingNodeId + " is deferring reply to Node " + fromNodeId);
            replyDeferredQueue.add(fromNodeId);
            deferred[fromNodeId] = true;
        } else {
            // Send reply
            System.out.println("Node " + holdingNodeId + " sending reply to Node " + fromNodeId);
            repliedCount++;
        }
    }
}

class Node extends Thread {
    private int id;
    private PaymentProcessor paymentProcessor;

    public Node(int id, PaymentProcessor paymentProcessor) {
        this.id = id;
        this.paymentProcessor = paymentProcessor;
    }

    public void run() {
        // Simulate payment processing
        paymentProcessor.processPayment(id);
    }
}

public class PaymentRAA {
    public static void main(String[] args) {
        final int numNodes = 5;
        PaymentProcessor paymentProcessor = new PaymentProcessor(numNodes);
        Node[] nodes = new Node[numNodes];

        // Create and start nodes
        for (int i = 0; i < numNodes; i++) {
            nodes[i] = new Node(i, paymentProcessor);
            nodes[i].start();
        }

        // Simulate network delay
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop a node to simulate failure
        nodes[2].interrupt();

        // Wait for all nodes to finish processing payments
        for (int i = 0; i < numNodes; i++) {
            try {
                nodes[i].join();
            } catch (InterruptedException e) {
                // Ignore interrupted exception for failed nodes
            }
        }
    }
}

