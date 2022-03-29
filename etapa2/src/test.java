
import java.util.Scanner;

public class test {

	public static void main (String[] argv){

		Scanner sc = new Scanner(System.in);
		String str = sc.nextLine();

		String[] splited = str.split(" ");

		for(String a: splited)
			System.out.println(a);

	}

}
