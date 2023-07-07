package Box.math.Parser;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import Box.Syntax.Expr;
import Box.Token.Token;
import Box.Token.TokenType;
import Box.math.BoxMathTokenType;
import Box.math.Syntax.*;
import Box.math.Syntax.Term.Function;
import Box.math.Syntax.Term.Variable;
import Box.math.Token.MathToken;
public class MathParser {

		private static class ParseError extends RuntimeException {

			private static final long serialVersionUID = 2715202794403784452L;
		}

		TokensToTrack tracker;

		boolean callPrint = false;
		int callPrintCount = 0;
		boolean callReturn = false;
		int callReturnCount = 0;

		private boolean isNoisserpxe = false;
		private boolean saveStatement = false;
		private int setbackFunctionDetermination;

		private int setbackFunctionDeterminationBuild;

		private class TokensToTrack {
			List<ArrayList<MathToken>> stack = new ArrayList<ArrayList<MathToken>>();
			List<Integer> currentStack = new ArrayList<Integer>();

			TokensToTrack(ArrayList<MathToken> baseTokens, int baseCurrent) {
				stack.add(baseTokens);
				currentStack.add(baseCurrent);

			}

			public void addSubTokens(ArrayList<MathToken> subTokens) {
				stack.add(subTokens);
				currentStack.add(0);
			}

			public boolean removeSubTokens() {
				if (stack.size() > 1) {
					stack.remove(stack.size() - 1);
					currentStack.remove(currentStack.size() - 1);
					return true;
				}
				return false;
			}

			public MathToken getToken() {
				int currentLocal = currentStack.get(currentStack.size() - 1);
				return (stack.get(stack.size() - 1)).get(currentLocal);
			}

			public void advance() {
				int currentLocal = currentStack.get(currentStack.size() - 1);
				currentLocal++;
				currentStack.remove(currentStack.size() - 1);
				currentStack.add(currentLocal);
			}

			public int getCurrent() {
				return currentStack.get(currentStack.size() - 1);
			}

			public void setSize(int currentToSet) {
				currentStack.remove(currentStack.size() - 1);
				currentStack.add(currentToSet);
			}

			public int size() {
				return (stack.get(stack.size() - 1)).size();
			}

			public MathToken getPrevious() {
				int currentLocal = currentStack.get(currentStack.size() - 1);
				return (stack.get(stack.size() - 1)).get(currentLocal - 1);
			}

			public MathToken getPeekNext() {
				int currentLocal = currentStack.get(currentStack.size() - 1);
				return (stack.get(stack.size() - 1)).get(currentLocal + 1);
			}

		}

		public MathParser(List<MathToken> tokens) {

			tracker = new TokensToTrack((ArrayList<MathToken>) tokens, 0);
		}

		public List<Term> parse() {

			List<Term> statements = new ArrayList<>();

			while (!isAtEnd()) {
				statements.add(declaration());
			}

			return statements;

		}


		



		private Term declaration() {
			

			try {

				
				Term term = expression();
				

					return term;

				

			} catch (ParseError error) {
				synchronize();
				return null;
			}
		}

		

		private void synchronize() {
			advance();
			while (!isAtEnd()) {
				
				switch (peek().type) {
				
				default:
					break;
				}
				advance();

			}

		}


		

		private Term expression() {
			
			return assignment();
		}

		


		private Term assignment() {

			Term term = term();

			if (match(BoxMathTokenType.EQUALS)) {
				MathToken equals = previous();
				Term value = assignment();
				if (term instanceof Term.Variable) {
					Variable name = ((Term.Variable) term);
					return new Term.Assignment(name, value);
				} else if (term instanceof Term.Function) {
					Function name = ((Term.Function) term);
					return new Term.Assignment(name, value);

				}
				error(equals, "Invalid assignment target.");

			}

			return term;
		}







		private Term term() {
			Term term = factor();

			while (match(BoxMathTokenType.MINUS, BoxMathTokenType.PLUS)) {
				MathToken operator = previous();
				Term right = factor();
				term = new Term.Binary(term, operator, right);

			}
			
			return term;
		}

		private Term factor() {
			Term term = power();

			while (match(BoxMathTokenType.DEVIDE, BoxMathTokenType.TIMES)) {
				MathToken operator = previous();
				Term right = power();
				term = new Term.Binary(term, operator, right);
			}

			
			return term;
		}

		private Term power() {
			Term term = yroot();

			while (match(BoxMathTokenType.POWER)) {
				MathToken operator = previous();
				Term right = yroot();
				term = new Term.Binary(term, operator, right);
			}

			return term;
		}

		private Term yroot() {
			if (check(BoxMathTokenType.YROOT)) {
				MathToken yroot = consume(BoxMathTokenType.YROOT, "expected yroot");
				consume(BoxMathTokenType.LEFT_PAREN, "expected (");
				Term baseExp = expression();
				consume(BoxMathTokenType.COMMA, "expected ',' ");
				Term rootExp = expression();
				consume(BoxMathTokenType.RIGHT_PAREN, "expected )");
				return new Term.Binary(baseExp, yroot, rootExp);
			}
			Term pocket = sin();

			return pocket;

		}

		private Term sin() {
			if (check(BoxMathTokenType.SIN)) {
				MathToken sin = consume(BoxMathTokenType.SIN, "expected sin");
				consume(BoxMathTokenType.LEFT_PAREN, "expected (");
				Term tofind = expression();
				consume(BoxMathTokenType.RIGHT_PAREN, "expected )");
				return new Term.Mono(tofind, sin);
				
			}
			Term pocket = cos();

			

			return pocket;

		}

		private Term cos() {
			if (check(BoxMathTokenType.COS)) {
				MathToken cos = consume(BoxMathTokenType.COS, "expected cos");
				consume(BoxMathTokenType.LEFT_PAREN, "expected (");
				Term tofind = expression();
				consume(BoxMathTokenType.RIGHT_PAREN, "expected )");
				return new Term.Mono(tofind, cos);
				
			}
			Term pocket = tan();

			

			return pocket;

		}

		private Term tan() {
			if (check(BoxMathTokenType.TAN)) {
				MathToken tan = consume(BoxMathTokenType.TAN, "expected tan");

				consume(BoxMathTokenType.LEFT_PAREN, "expected (");
				Term tofind = expression();
				consume(BoxMathTokenType.RIGHT_PAREN, "expected )");
				return new Term.Mono(tofind, tan);
			}
			Term pocket = sinh();

			

			return pocket;

		}

		private Term sinh() {
			if (check(BoxMathTokenType.SINH)) {
				MathToken sinh = consume(BoxMathTokenType.SINH, "expected sinh");
				consume(BoxMathTokenType.LEFT_PAREN, "expected (");
				Term tofind = expression();
				consume(BoxMathTokenType.RIGHT_PAREN, "expected )");
				return new Term.Mono(tofind, sinh);

			}
			Term pocket = cosh();

			

			return pocket;

		}

		private Term cosh() {
			if (check(BoxMathTokenType.COSH)) {
				MathToken cosh = consume(BoxMathTokenType.COSH, "expected cosh");
				consume(BoxMathTokenType.LEFT_PAREN, "expected (");
				Term tofind = expression();
				consume(BoxMathTokenType.RIGHT_PAREN, "expected )");
				return new Term.Mono(tofind, cosh);
				
			}
			Term pocket = tanh();

			

			return pocket;
		}

		private Term tanh() {
			if (check(BoxMathTokenType.TANH)) {
				MathToken tanh = consume(BoxMathTokenType.TANH, "expected tanh");
				consume(BoxMathTokenType.LEFT_PAREN, "expected (");
				Term tofind = expression();
				consume(BoxMathTokenType.RIGHT_PAREN, "expected )");
				return new Term.Mono(tofind, tanh);
				
			}
			Term pocket = log();

			

			return pocket;

		}

		private Term log() {
			if (check(BoxMathTokenType.LOG)) {
				MathToken log = consume(BoxMathTokenType.LOG, "expected log");
				consume(BoxMathTokenType.LEFT_PAREN, "expected (");
				Term baseTerm = expression();
				consume(BoxMathTokenType.COMMA, "expected ',' ");
				Term valueTerm = expression();
				consume(BoxMathTokenType.RIGHT_PAREN, "expected )");
				return new Term.Log(log, baseTerm, valueTerm);
				
			}
			if (check(BoxMathTokenType.LN)) {
				MathToken log = consume(BoxMathTokenType.LN, "expected log");
				consume(BoxMathTokenType.LEFT_PAREN, "expected (");
				Term baseTerm = expression();
				consume(BoxMathTokenType.RIGHT_PAREN, "expected )");
				return new Term.Ln(log, baseTerm);
				
			}
			return factorial();
		}

		private Term factorial() {
			Term term = unary();

				while (match(BoxMathTokenType.FACTORIAL)) {
					MathToken operator = previous();
					term = new Term.Factorial(term, operator);
				}

		
			return term;
		}

		private Term unary() {
			if (match( BoxMathTokenType.MINUS)) {
				MathToken operator = previous();
				Term right = unary();
				return new Term.Unary(operator, right);
			}
			return integral();

		}
		private Term integral() {
			
			Term function = null;
			if (match( BoxMathTokenType.INTEGRAL)) {
				consume(BoxMathTokenType.FROM,"expected from") ;
				Term from = expression();
				consume(BoxMathTokenType.TO,"expected to") ;
				Term to = expression();
				
				if (match( BoxMathTokenType.LEFT_PAREN)) {
					function =expression();
					if(match(BoxMathTokenType.RIGHT_PAREN)) {
						MathToken toIntegrateOver = consume(BoxMathTokenType.DERIVITIVEVAR,"expected derivitiveterm");
						return new Term.Integral(from, to, toIntegrateOver, function);
					}
					
				}
				throw error(null, "Error Parsing Integral");
			}else {
			return toDerive();
			}
		}
		private Term toDerive() {
			
			Term left = null;
			if (match( BoxMathTokenType.LEFT_PAREN)) {
				left =expression();
				if(match(BoxMathTokenType.RIGHT_PAREN)) {
					
					while(match(BoxMathTokenType.TODERIVE)) {
						left =new Term.ToDerive(left,previous());
					}
				}
				return left;
			}else {
				return primary();
			}
		}


	






		@SuppressWarnings("unchecked")
		public Term primary() throws ParseError {

			

			if (match(BoxMathTokenType.E))
				return new Term.E();
			if (match(BoxMathTokenType.PI))
				return new Term.PI();
			if (match(BoxMathTokenType.STRING, BoxMathTokenType.NUMBER))
				return new Term.Literal(previous().literal);

			if(check(BoxMathTokenType.IDENTIFIER)&& checkNext(BoxMathTokenType.LEFT_PAREN)) {
				MathToken previous = consume(BoxMathTokenType.IDENTIFIER,"expected name of Function .");
				consume(BoxMathTokenType.LEFT_PAREN,"expected ( .");
				Term term = expression();
				consume(BoxMathTokenType.RIGHT_PAREN,"expected ) .");
				return new Term.Function(previous,term);				
			}
			if(check(BoxMathTokenType.DERIVITIVE)&& checkNext(BoxMathTokenType.LEFT_PAREN)) {
				MathToken previous = consume(BoxMathTokenType.DERIVITIVE,"expected derivitive .");
				MathToken functionName = (MathToken) previous.literal;
				
				consume(BoxMathTokenType.LEFT_PAREN,"expected ( .");
				Term term = expression();
				consume(BoxMathTokenType.RIGHT_PAREN,"expected ) .");
				Term function = new Term.Function(functionName,term);	
				return new Term.Derivitive(function);
			}
			if(check(BoxMathTokenType.DERIVITIVE)&& checkNext(BoxMathTokenType.IDENTIFIER)) {
				MathToken previous = consume(BoxMathTokenType.DERIVITIVE,"expected derivitive .");
				MathToken functionName = consume(BoxMathTokenType.IDENTIFIER,"expected name of Function .");
				
				consume(BoxMathTokenType.LEFT_PAREN,"expected ( .");
				Term term = expression();
				consume(BoxMathTokenType.RIGHT_PAREN,"expected ) .");
				Term function = new Term.Function(functionName,term);	
				return new Term.Derivitive(function);
			}
			
			
			if (match(BoxMathTokenType.IDENTIFIER))
				return new Term.Variable(previous());
			
			throw error(peek(),
					"expected false |true | NILL | NULL | string | INT | DOUBLE | pocket | box | cup | knot | '(' | ')' | '{' | '}' | '[' | ']' |',' .");
		}

		private boolean isAtEnd() {
			return peek().type == BoxMathTokenType.EOF;
		}

		
		
		private boolean checkNext(BoxMathTokenType tokenType) {
			if (isAtEnd())
				return false;
			return peekNext().type == tokenType;
		}
		
		

		
		
		private MathToken peek() {
			if (tracker.getCurrent() >= tracker.size())
				return null;
			return tracker.getToken();
		}

		private MathToken peekNext() {
			if (tracker.getCurrent() >= tracker.size() - 1)
				return null;
			return tracker.getPeekNext();
		}

		private MathToken previous() {
			if (tracker.getCurrent() == 0)
				return null;
			return tracker.getPrevious();
		}

		private boolean match(BoxMathTokenType... tokenTypes) {
			for (BoxMathTokenType tokenType : tokenTypes) {
				if (check(tokenType)) {
					advance();
					return true;
				}
			}
			return false;
		}

		private MathToken advance() {
			if (!isAtEnd()) {
				tracker.advance();
			}
			return previous();

		}

		private boolean check(BoxMathTokenType tokenType) {
			if (isAtEnd())
				return false;
			return peek().type == tokenType;
		}

		private MathToken consume(BoxMathTokenType type, String message) throws ParseError {
			if (check(type))
				return advance();
			throw error(peek(), message);
			
		}

		
		public  ParseError error(MathToken token, String message) {
			if (token.type == BoxMathTokenType.EOF)
				report(token.column, token.line, " at end", message);
			else
				report(token.column, token.line, " at '" + token.lexeme + "'", message);
			throw new ParseError();
		}

		private static void report(int column, int line, String where, String message) {
			System.err.println("[column " + column + ", line " + line + "] Error " + where + ": " + message);
			
		}
		
		
		
		

	}


