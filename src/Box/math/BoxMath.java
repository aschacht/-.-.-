package Box.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import Box.math.Interpreter.MathInterpreter;
import Box.math.Parser.MathParser;
import Box.math.Scanner.MathScanner;
import Box.math.Syntax.Term;
import Box.math.Token.MathToken;

public class BoxMath {
	static public boolean test(String equation) {
		MathScanner scanner = new MathScanner(equation);
		ArrayList<MathToken> scanTokensFirstPass = scanner.scanTokensFirstPass();
		for (MathToken token : scanTokensFirstPass) {
			if (token.type == BoxMathTokenType.SPACE) {

			} else if (token.type == BoxMathTokenType.SPACERETURN) {

			} else if (token.type == BoxMathTokenType.TAB) {

			} else if (token.type == BoxMathTokenType.NEWLINE) {

			} else {
				System.out.print(token);
				System.out.print("\t " + token.column + " ");
				System.out.print(" " + token.line + " ");
				System.out.println();
			}
		}
		
		scanTokensFirstPass.add(new MathToken(BoxMathTokenType.EOF, "", null, -1, -1, -1, -1));
		MathParser parser = new MathParser(scanTokensFirstPass);
		List<Term> terms = parser.parse();
		
		MathInterpreter interpreter = new MathInterpreter();
		interpreter.interpret(terms);
		BigDecimal s = interpreter.S(BigDecimal.valueOf(4.0));
		System.out.println("S: "+s);
		
		
		return true;
	}

}
