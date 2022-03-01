package Assignment2;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantLock;

class GuestThreads implements Runnable{
    public int guests;
    private boolean eaten;
    private int num;
    private int counter;
    private Lock cakeLock;
    private Lock mazeLock;
    public boolean cupcake;
    public AtomicBoolean end;


    public GuestThreads(int num, AtomicBoolean end, int counter, Lock mazeLock, Lock cakeLock, boolean cupcake){
        this.num = num;
        this.end = end;
        this.guests = MinotaurMaze.guests - 1;
        this.mazeLock = mazeLock;
        this.cakeLock = cakeLock;
        this.cupcake = cupcake;
        this.counter = counter;
        this.eaten = false;
    }

    @Override
    public void run(){

        while(end.get() == false){
            //attempt to get into the maze, the scheduler chooses (random)
            //block or do nothing until thread's turn
            
            mazeLock.lock();
            System.out.println("Guest " + num + " obtained the lock");
            try{
                //check if its the leader thread no.1 and the cupcake is gone, someone new ate it 
                if(num == 1 && cupcake == false && counter < guests){
                    System.out.println("A cupcake was taken guest 1 is requesting the cakeLock");
                    //If the cupcake's gone request the cup(cakeLock) to get a new one
                    cakeLock.lock();
                    System.out.println("Lock acquired");
                    try{
                        //update the counter for the additional guest visit
                        // if the the number matches the number of guests the leader calls the end (tells the minotaur)
                        // otherwise set and leave the cupcake
                        synchronized(this){
            
                            counter++;
                            System.out.println("counter updated");
                            if(counter == guests){ 
                                System.out.println("All guests have visited"); 
                                end.set(true);
                            } else {
                                System.out.println("A new cupcake was placed");
                                cupcake = true;
                            }
                        }
                    }finally{
                        cakeLock.unlock();
                        System.out.println("released cakeLock");
                    }
                } else {
                    if(num != 1 && cupcake == true && eaten == false){
                        System.out.println("Guest " + num + " finished the maze and is eating the cupcake.");

                        synchronized(this){
                            cupcake = false;
                            eaten = true;
                        }
                    } 
                }

                //number 1 - If the cupcake is there, leave without eating or marking an additional guest
                //other guests if the cupcake is not there, or you have eaten or both leave

            }finally {
                mazeLock.unlock();
                System.out.println("released mazeLock");
            }
        }
    }
}

public class MinotaurMaze{
    public Lock cakeLock;
    public Lock mazeLock;
    public AtomicBoolean end;
    public int counter;
    public boolean cupcake;
    public static int guests;
    public static void main(String args[]){
        MinotaurMaze maze = new MinotaurMaze();
        maze.cakeLock = new ReentrantLock();
        maze.mazeLock = new ReentrantLock();
        maze.end = new AtomicBoolean(false);
        maze.cupcake = true;
        maze.counter = 0;

        //Ask for num of guests
        ArrayList<Thread> gThreads = new ArrayList<>();
        Scanner user_in = new Scanner(System.in);

        System.out.println("Enter number of guests: (max 8)\n");
        guests = user_in.nextInt();
        user_in.close();

        Thread th;

        for(int i = 0; i < guests; i++){
            GuestThreads gTh = new GuestThreads(i+1, maze.end, maze.counter, maze.mazeLock, maze.cakeLock, maze.cupcake);
            th = new Thread(gTh);
            gThreads.add(th);
            th.start();
        }

        for(Thread t : gThreads){
            System.out.println("joining threads");
            try {
                t.join();
            } catch(InterruptedException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }
}