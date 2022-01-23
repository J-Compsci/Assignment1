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
    public ConcurrentLinkedQueue tenPrimes;

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

        set.end = System.currentTimeMillis();
        set.time = set.end - set.st;
        System.out.println("< " + set.time + " ms > < " + set.count + " > < "+ set.sum + " >");
    }
}
