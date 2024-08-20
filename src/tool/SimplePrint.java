package tool;

public class SimplePrint {

	public static void main(String[] args) {
		
		for (int i = 0; i <128; i++) {
			if(i!=127)
			System.out.print(i+ "|");
			else
				System.out.print(i);
		}

	}

}
