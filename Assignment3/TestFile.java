import java.util.Scanner;
public class TestFile {

    public static void gift(int n){
        MinotaurPresents.main(n);
    }
    public static void alt(int n){
        MinoPresents.main(n);
    }
    public static void main(String[] args){
        int prezzies;
        Scanner user_in = new Scanner(System.in);

        System.out.println("Enter number (int) of presents:");
        prezzies = user_in.nextInt();
        
        user_in.close();

       gift(prezzies);
       alt(prezzies);
    }
}
