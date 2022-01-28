// Creating/Writing files
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
// Variables for concurrent operations
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class PrimalityTest {

    public AtomicInteger num;
    public ArrayList<Thread> pThreads;
    public AtomicInteger count;
    public long sum;
    public boolean psum[];
    public long st;
    public long time;
    public long[] q;
    public static int max = 100000000;

    public int getNum(){
        return num.get();
    }
    
    public int incrNum(){
        return num.getAndIncrement();
    }

    public int incrCount(){
        return count.incrementAndGet();
    }

    public static void main(String[] args){
        PrimalityTest set = new PrimalityTest();

        set.num = new AtomicInteger(0);
        set.count = new AtomicInteger(0);
        set.sum = 0;
        set.psum = new boolean[max + 1];
        set.q = new long[10];

        //ArrayList to keep track of thread completion
        set.pThreads = new ArrayList<>();

        Arrays.fill(set.psum, false);
        Arrays.fill(set.q, 0);

        Thread th;
        set.st = System.currentTimeMillis();

        for(int i = 1; i < 9; i++){
            PrimeFinder pTh = new PrimeFinder(set.num, set.count, set.psum);
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
        set.time = System.currentTimeMillis()-set.st;
        System.out.println(set.time);

        for(int i = 2; i < max; i++){
            if(set.psum[i]){
                set.sum += i;
            }
        }

        System.out.println("Sum calculted");

        int c = 9;
        for(int i = max; i > max/10; i--){
            
            if(set.psum[i]){
                set.q[c] = i;
                System.out.println(i);
                c--;
            }

            if(c < 0){
                break;
            }
        }

        try {
            File primes = new File("primes.txt");
            
            if (primes.createNewFile()){

                // Print to make sure file was created
                System.out.println("File created: " + primes.getName());

                // Write prime results in prime.txt
                FileWriter primeOutput = new FileWriter("primes.txt");
                primeOutput.write("< " + set.time + " ms > < " + set.count + " > < "+ set.sum + " > \n< "+ Arrays.toString(set.q) +" >");
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
