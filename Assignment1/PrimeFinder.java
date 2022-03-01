import java.util.concurrent.atomic.AtomicInteger;

public class PrimeFinder implements Runnable{
    private int max = 100000000;
    private AtomicInteger num;
    private AtomicInteger count;
    private boolean[] psum;

    public PrimeFinder(AtomicInteger num, AtomicInteger count, boolean[] psum){
        this.num = num;
        this.count = count;
        this.psum = psum;
    }

    @Override
    public void run(){

        while(num.get() <= max){
            // Gets the next number value, then increments the number value          
            int testNum = num.getAndIncrement();

            // If testNum is Prime, increment and get the count of primes
            // add prime value to the sum
            if(Primes(testNum)){
                count.getAndIncrement();
                psum[testNum] = true; 
            }
        }
    }

    // Primes is a thread function
    // each PrimeFinder thread can calculate independently without more references to main function
    public static boolean Primes(double n){
        // Optimization method from Wikipedia
        if( n == 2 || n == 3 ){
            return true;
        }

        if( n <= 1 || n % 2 == 0 || n % 3 == 0 ){
            return false;
        }

        for ( int i = 5; i * i <= n; i += 6 ){
            if( n % i == 0 || n % ( i + 2 ) == 0 ){
                return false;
            }

        }

        return true;
    }
}
