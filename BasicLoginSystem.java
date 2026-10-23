
import java.util.Scanner;
class BasicLoginSystem {
    public static void main(String[] args) {
        Scanner login = new Scanner(System.in);
        
        String user, password;
        System.out.print("Enter user:");
        user = login.nextLine();
        System.out.print("Enter password:");
        password = login.nextLine();
        
        if(user.equals("Louie") && (password.equals("pogi"))){
            System.out.println("Welcome, " + user + "!");
      
         System.out.println("Is there anything i  could help you?");
        }
        login.close();
    }
}