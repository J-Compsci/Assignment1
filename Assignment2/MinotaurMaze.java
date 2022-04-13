import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantLock;

class GuestThreads implements Runnable{

    private boolean eaten;
    private int counter;
    private int num;
    private Lock cakeLock;
    private Lock mazeLock;

    public AtomicBoolean cupcake;
    public AtomicBoolean end;
    public int guests;

    public GuestThreads(int num, AtomicBoolean end, int counter, Lock mazeLock, Lock cakeLock, AtomicBoolean cupcake){
        this.cakeLock = cakeLock;
        this.counter = counter;
        this.cupcake = cupcake;
        this.eaten = false;
        this.end = end;
        // guests other than designated leader aka n-1
        this.guests = MinotaurMaze.guests - 1;
        this.mazeLock = mazeLock;
        this.num = num;
    }

    @Override
    public void run(){

        while(end.get() == false){
            // attempt to get into the maze, the scheduler chooses (random)
            // block or do nothing until thread's turn
            mazeLock.lock();
            try{
                //check if its the leader guest thread no.1 and the cupcake is gone, someone new ate it 
                if(num == 1 && cupcake.get() == false && counter < guests){

                    // If the cupcake's gone the leader requests the cup(cakeLock) to get a new one
                    cakeLock.lock();

                    try{
                        // update the counter for the additional guest visit
                        // if the the number matches the number of guests the leader calls the end (tells the minotaur)
                        // otherwise set and leave the cupcake
            
                        counter++;

                        if(counter == guests){ 

                            // All guests have been accounted for
                            end.set(true);

                        } else {

                            // Replace the cupcake
                            cupcake.set(true);
                        }

                    } finally {

                        cakeLock.unlock();
                        // System.out.println("released cakeLock");
                    }

                }else {

                    // For the non-leader guests, if the cupcake is there and they have not eaten one yet, they can eat it
                    if(num != 1 && cupcake.get() == true && eaten == false){
                        // System.out.println("Guest " + num + " finished the maze and is eating the cupcake.");

                        // No cupcake
                        cupcake.set(false);

                        // This guest has eaten
                        eaten = true;
                    }
                    //If there isn't a cupcake or they have already eaten, leave
                }

            } finally {
                mazeLock.unlock();
                // System.out.println("released mazeLock");
            }
        }
    }
}

public class MinotaurMaze{

    public AtomicBoolean cupcake;
    public AtomicBoolean end;
    public Lock cakeLock;
    public Lock mazeLock;
    public int counter;
    public static int guests;

    public static void main(int nguests){
        MinotaurMaze maze = new MinotaurMaze();
        maze.cakeLock = new ReentrantLock();
        maze.mazeLock = new ReentrantLock();
        maze.end = new AtomicBoolean(false);
        maze.cupcake = new AtomicBoolean(true);
        maze.counter = 0;

        guests = nguests;
        ArrayList<Thread> gThreads = new ArrayList<>();

        Thread th;

        for(int i = 0; i < guests; i++){
            GuestThreads gTh = new GuestThreads(i+1, maze.end, maze.counter, maze.mazeLock, maze.cakeLock, maze.cupcake);
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

        System.out.println("All guests have visited the maze.");

    }
}