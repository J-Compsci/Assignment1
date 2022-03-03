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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        q.add(num);

        // Each thread loops until it does not requeue, make sure its not empty just in case
        while (!q.isEmpty() && q.contains(num)){

            //check if thread is now the head of the queue, if it is try to acquire the room lock
            if(!q.isEmpty() && q.peek() == num){
                showroom.lock();

                try{
                    //System.out.println("Thread" + num + " has entered the room");
                    q.poll();
                    //decide if they want to requeue
                    if(rnd.nextDouble() > 0.4){
                        CrystalVase.req(num);
                    }

                } finally{
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

    public static void main(String [] args){

        Scanner user_in = new Scanner(System.in);
        System.out.println("Enter number of guests:");
        guest_num = user_in.nextInt();
        user_in.close();

        CrystalVase view = new CrystalVase();
        roomLine = new ConcurrentLinkedQueue<Integer>();
        view.showroom = new ReentrantLock();
        gThreads = new ArrayList<Thread>();
        
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("Queue is now empty.");
    }
}
