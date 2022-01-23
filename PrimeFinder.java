public class PrimeFinder implements Runnable{
    private int threadnum;
    private int max = 100000000;
    private PrimalityTest set;

    public PrimeFinder(int threadnum, PrimalityTest set){
        this.threadnum = threadnum;
        this.set = set;
    }

    @Override
    public void run(){
        try{
            Thread.sleep(100);
        }catch(InterruptedException e){
            // TODO Auto-generated catch block
			e.printStackTrace();
        }

        while(set.getNum() <= max){
            // Gets the next number value, then increments the number value
            int testNum = set.incrNum();

            // If testNum is Prime, increment and get the count of primes
            // add prime value to the sum
            if(Primes(testNum)){
                set.incrCount();
                set.addSum((long)testNum);

                if(set.q.size() >= 10){
                    set.q.poll();
                }
                
                set.q.add((long)testNum);
            }
        }
    }

    // Primes is a thread function
    // each PrimeFinder thread can calculate independently without more references to main function
    public boolean Primes(double n){
        if (n <= 1) return false;

        // No divisors greater than the sqrt
        double upper = (Math.sqrt(n));

        for (int i = 2; i <= upper; i++){
            // Check if n is divisible by all values lower than sqrt, starting with 2 (evens)
            if(n % i == 0){
                return false;
            }
        }

        return true;
    }
}
