// Creating/Writing files
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
// Variables for concurrent operations
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PrimalityTest {

    private AtomicInteger num;
    public ArrayList<Thread> pThreads;
    public AtomicInteger count;
    public AtomicLong sum;
    public long st;
    public long end;
    public long time;
    public ConcurrentLinkedQueue<Long> q;

    public int getNum(){
        return num.get();
    }
    
    public int incrNum(){
        return num.getAndIncrement();
    }

    public int incrCount(){
        return count.incrementAndGet();
    }

    // sum is long incase of large sum beyond int maximum
    public long addSum(long value){
        return sum.addAndGet(value);
    }

    public static void main(String[] args){
        PrimalityTest set = new PrimalityTest();

        //The numbers to check begin at 2 as 0 and 1 are not primes
        set.num = new AtomicInteger(2);
        set.count = new AtomicInteger(0);
        set.sum = new AtomicLong(0);
        set.q = new ConcurrentLinkedQueue<>();

        //ArrayList to keep track of thread completion
        set.pThreads = new ArrayList<>();

        Thread th;
        set.st = System.currentTimeMillis();

        for(int i = 1; i < 9; i++){
            PrimeFinder pTh = new PrimeFinder(i, set);
            th = new Thread(pTh);
            th.start();
            set.pThreads.add(th);
        }

        //Checking for completion to get correct time and output
        for(Thread t : set.pThreads){
            try {
                t.join();
            } catch(InterruptedException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // Getting final time after thread completion
        set.end = System.currentTimeMillis();
        set.time = set.end - set.st;

        // Remove any earlier extras just incase more than 10 remain in the Queue
        while (set.q.size() > 10){
            set.q.poll();
        }

        try {
            File primes = new File("primes.txt");
            
            if (primes.createNewFile()){

                // Print to make sure file was created
                System.out.println("File created: " + primes.getName());

                // Write prime results in prime.txt
                FileWriter primeOutput = new FileWriter("primes.txt");
                primeOutput.write("< " + set.time + " ms > < " + set.count + " > < "+ set.sum + " > \n< "+ set.q +" >");
                primeOutput.close();

            } else {
                System.out.println("File exists.");
            }
        } catch (IOException e){
                System.out.println("An error occurred.");
                e.printStackTrace();
        }

        // Print statement for checking output
        //System.out.println("< " + set.time + " ms > < " + set.count + " > < "+ set.sum + " > \n< "+ set.q +" >");
    }
}
