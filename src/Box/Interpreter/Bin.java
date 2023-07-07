package Box.Interpreter;

import java.math.BigInteger;

public class Bin {

	private Integer bin;

	public Bin(String bin) {
		String max = "0111111111111111111111111111111";
		if (bin.length() <= max.length())
			this.bin = Integer.parseInt(bin, 2);
		else {
			StringBuilder sb = new StringBuilder(bin);
			String reversedResult = sb.reverse().toString();
			boolean areAllOne = true;
			for (int i = reversedResult.length() - 1; i > 30; i--) {
				if (reversedResult.charAt(i) == 0) {
					areAllOne = false;
				}
			}

			if (areAllOne)
				reversedResult = reversedResult.substring(0, 30);
			StringBuilder sb2 = new StringBuilder(reversedResult);
			String reversedResult2 = sb2.reverse().toString();
			this.bin = Integer.parseInt(reversedResult2, 2);

		}

	}

	public Bin(Integer integer) {

		this.bin = integer;

	}

	public static Bin valueOf(String bin) {
		return new Bin(bin);
	}

	public static Bin add(Bin left, Bin right) {

		return new Bin(left.bin + right.bin);
	}

	public static Bin subtract(Bin left, Bin right) {

		return new Bin(left.bin - right.bin);
	}

	public Integer toInteger() {

		return bin;
	}

	public Bin negate() {

		return new Bin(-bin);
	}

	public String toString() {
		return Integer.toBinaryString(bin);
	}

	public Double toDouble() {
		
		return Double.valueOf(bin.doubleValue());
	}

	public static boolean lessThenEquals(Bin theLeft, Bin theRight) {
		
		return theLeft.bin <= theRight.bin;
	}

	public static boolean lessThen(Bin theLeft, Bin theRight) {
		
		return theLeft.bin < theRight.bin;
	}

	public static boolean greaterThenEquals(Bin theLeft, Bin theRight) {
		
		return theLeft.bin >= theRight.bin;
	}

	public static boolean greaterThen(Bin theLeft, Bin theRight) {
		
		return theLeft.bin > theRight.bin;
	}

	public static Double divide(Bin theLeft, Bin theRight) {
		
		return Double.valueOf(theLeft.bin / theRight.bin);
	}

	public static Integer times(Bin theLeft, Bin theRight) {
		
		return theLeft.bin * theRight.bin;
	}

	public boolean isValueEqualTo(Bin bin2) {
		
		return bin == bin2.bin;
	}




}
