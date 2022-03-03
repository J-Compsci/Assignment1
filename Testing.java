import java.util.Scanner;

public class Testing {
    public static void minotaur(int n){
        MinotaurMaze.main(n);
    }
    public static void vase(int n){
        CrystalVase.main(n);
    }
    public static void main(String[] args){
        int mGuests;
        int vVisitors;
        Scanner user_in = new Scanner(System.in);

        System.out.println("Enter number of maze guests:");
        mGuests = user_in.nextInt();
        System.out.println("Enter number of vase visitors:");
        vVisitors = user_in.nextInt();
        user_in.close();
        
        minotaur(mGuests);
        vase(vVisitors);
    }
}
