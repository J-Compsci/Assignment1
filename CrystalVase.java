import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantLock;

class Guests implements Runnable{
    private int num;
    public ConcurrentLinkedQueue<Integer> q;
    public Lock showroom;

    public Guests(int num, Lock showroom, ConcurrentLinkedQueue<Integer> q){
        this.num = num;
        this.q = q;
        this.showroom = showroom;
    };

    @Override
    public void run(){
        Random rnd = new Random();

        // "Random enqueueing"
        try {
            Thread.sleep(rnd.nextInt(50));
        } catch (InterruptedException e) {
            // Catch block
            e.printStackTrace();
        }

        q.add(num);

        // Each thread loops until it does not requeue, make sure its not empty just in case
        while (!q.isEmpty() && q.contains(num)){

            // check if thread is now the head of the queue, if it is try to acquire the room lock
            if(!q.isEmpty() && q.peek() == num){
                showroom.lock();

                // If they acquire the lock they can leave the queue to enter the room
                try{
                    q.poll();
                    // Decide if they want to requeue, y/n
                    if(rnd.nextDouble() > 0.5){
                        CrystalVase.req(num);
                    }

                } finally{
                    // When done release the lock for the current head of the queue to enter
                    showroom.unlock();
                }
            }
        }
    }
}

public class CrystalVase {
    public static ArrayList<Thread> gThreads;
    public static ConcurrentLinkedQueue<Integer> roomLine;
    public Lock showroom;
    public static int guest_num;

    public static void req(int num){
        //System.out.println("Thread" + num + " requeued");
        roomLine.add(num);
    }

    public static void main(int nguests){

        CrystalVase view = new CrystalVase();
        view.showroom = new ReentrantLock();

        roomLine = new ConcurrentLinkedQueue<Integer>();
        gThreads = new ArrayList<Thread>();
        guest_num = nguests;
        
        Thread th;

        for(int i = 0; i < guest_num; i++){
            Guests gTh = new Guests(i+1, view.showroom, roomLine);
            th = new Thread(gTh);
            gThreads.add(th);
            th.start();
        }

        for(Thread t : gThreads){
            
            try {
                t.join();
            } catch(InterruptedException e){
                // Catch block
                e.printStackTrace();
            }
        }
        System.out.println("Vase queue is now empty.");
    }
}
