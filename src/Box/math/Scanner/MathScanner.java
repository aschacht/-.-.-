package Box.math.Scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import Box.Box.Box;
import Box.Interpreter.Bin;
import Box.Syntax.ExprOLD;
import Box.Token.Token;
import Box.Token.TokenType;
import Box.math.BoxMathTokenType;
import Box.math.Token.MathToken;
import Parser.Stmt;

public class MathScanner {

	private String source;
	private final ArrayList<MathToken> tokens = new ArrayList<>();
	public final ArrayList<String> identifiers = new ArrayList<>();
	private int column = 0;
	private int line = 1;
	private int start = 0;
	private int current = 0;
	private static Map<String, BoxMathTokenType> keywords = new HashMap<>();

	public MathScanner(String string) {
		keywords.put("sin", BoxMathTokenType.SIN);
		keywords.put("cos", BoxMathTokenType.COS);
		keywords.put("tan", BoxMathTokenType.TAN);
		keywords.put("e", BoxMathTokenType.E);
		keywords.put("pi", BoxMathTokenType.PI);
		keywords.put("log", BoxMathTokenType.LOG);
		keywords.put("ln", BoxMathTokenType.LN);
		keywords.put("integral", BoxMathTokenType.INTEGRAL);
		keywords.put("from", BoxMathTokenType.FROM);
		keywords.put("to", BoxMathTokenType.TO);

		this.source = string;

	}

	public ArrayList<MathToken> scanTokensFirstPass() {
		while (!isAtEnd()) {

			start = current;
			scanToken(tokens);

		}

		return tokens;
	}

	private void scanToken(ArrayList<MathToken> tokens) {
		char c = advance();

		switch (c) {
		
		case ',':
			addToken(BoxMathTokenType.COMMA, tokens);
			break;
		case '(':
			addToken(BoxMathTokenType.LEFT_PAREN, tokens);
			break;
		case ')':
			addToken(BoxMathTokenType.RIGHT_PAREN, tokens);
			break;

		case '-':
			addToken(BoxMathTokenType.MINUS, tokens);
			break;
		case '+':
			addToken(BoxMathTokenType.PLUS, tokens);
			break;
		case '*':
			addToken(BoxMathTokenType.TIMES, tokens);
			break;
		case '^':
			addToken(BoxMathTokenType.POWER, tokens);
			break;
		case '!':
			addToken(BoxMathTokenType.FACTORIAL, tokens);
			break;
		case '=':

			addToken(BoxMathTokenType.EQUALS, tokens);

			break;
		case '|':
			
			addToken(BoxMathTokenType.ABS, tokens);
			
			break;
		case 'd':
			if (match('/')) {
					if(match('d')) {
						if(isAlpha(peek())) {
							variable(tokens, c);
							addToken(BoxMathTokenType.DERIVITIVE, tokens);
						}
					}
			} else if(isAlpha(peek())){
				String variable = variable(tokens, c);
				if(match('/')) {
					if(match('d')) {
						if(isAlpha(peek())) {
							variable(tokens, c);
							MathToken function = new MathToken(BoxMathTokenType.FUN,variable.substring(1, variable.length()),null,-1,-1,-1,-1);
							addToken(BoxMathTokenType.DERIVITIVE,function, tokens);
						}
					}
				}else {
					addToken(BoxMathTokenType.DERIVITIVEVAR, tokens);
				}
				
			}
			break;
		case '<':
			if (match('=')) {
				addToken(BoxMathTokenType.LESSTHENEQUAL, tokens);
			} else {
				addToken(BoxMathTokenType.LESSTHEN, tokens);
			}
			break;
		case '>':
			if (match('=')) {
				addToken(BoxMathTokenType.GREATERTHENEQUAL, tokens);
			} else {
				addToken(BoxMathTokenType.GREATERTHEN, tokens);
			}
			break;
		case '/':
			if (match('/'))
				while (peek() != '\n' && !isAtEnd())
					advance();
			else
				addToken(BoxMathTokenType.DEVIDE, tokens);

			break;
		case ' ':
			
			break;
		case '\r':
			
			break;
		case '\t':
			
			break;
		case '\n':
			
			line++;
			column = 0;
			break;
		case '"':
			string(tokens);
			break;
		case '\'':
			addToken(BoxMathTokenType.TODERIVE, tokens);
			break;
		default:
			if (isDigit(c))
				number(tokens, c);
			else if (isAlpha(c))
				identifier(tokens, c);
			else
				Box.error(column, line, "Unexpected character " + c);
		}

	}

	private void identifier(ArrayList<MathToken> tokens, char c) {

		while (isAlphaNumeric(peek()))
			advance();

		String text = source.substring(start, current);
		BoxMathTokenType type = keywords.get(text);
		if (type == null)
			type = BoxMathTokenType.IDENTIFIER;
		addToken(type, tokens);
	}
	private String variable(ArrayList<MathToken> tokens, char c) {
		
		while (isAlpha(peek()))
			advance();
		
		String text = source.substring(start, current);
		return text;
	}

	private boolean isAlphaNumeric(char c) {

		return isAlpha(c) || isDigit(c);
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isb(char c) {
		return (c == 'b');
	}

	private void number(ArrayList<MathToken> tokens, char c) {
		while (isDigit(peek()))
			advance();
		if (peek() == '.' && isDigit(peekNext())) {
			advance();
			while (isDigit(peek()))
				advance();

		}
		addToken(BoxMathTokenType.NUMBER, Double.parseDouble(source.substring(start, current)), tokens);
	}

	private char previous() {
		if (start == 0)
			return '\0';
		return source.charAt(start - 1);

	}

	private char previousCurrent() {
		if (start == 0)
			return '\0';
		return source.charAt(current - 1);

	}

	private boolean isBinary(String text) {
		boolean isBinary = true;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '0' || text.charAt(i) == '1') {

			} else {
				isBinary = false;
			}
		}
		return isBinary;
	}

	private char peekNext() {
		if (current + 1 >= source.length())
			return '\0';
		return source.charAt(current + 1);
	}

	private boolean isDigit(char c) {

		return c >= '0' && c <= '9';
	}

	private boolean isBinary(char c) {

		return c == '0' || c == '1';
	}

	private void string(ArrayList<MathToken> tokens) {
		while (peek() != '"' && !isAtEnd()) {
			if (peek() == '\n') {
				line++;
				column = 0;
			}
			advance();
		}

		if (isAtEnd()) {
			Box.error(column, line, "Unterminated String");
			return;
		}

		advance();
		String value = source.substring(start + 1, current - 1);
		addToken(BoxMathTokenType.STRING, value, tokens);
	}

	private void character(ArrayList<MathToken> tokens) {
		if (peek() != '\'') {
			if (peek() == '\n') {
				line++;
				column = 0;
			}
			advance();
		}

		if (isAtEnd()) {
			Box.error(column, line, "Unterminated char");
			return;
		}

		advance();
		String value = source.substring(start + 1, current - 1);
		addToken(BoxMathTokenType.CHAR, value, tokens);
	}

	private char peek() {
		if (isAtEnd())
			return '\0';
		return source.charAt(current);
	}

	private boolean match(char c) {
		if (isAtEnd())
			return false;
		if (source.charAt(current) != c)
			return false;
		current++;
		column++;
		return true;
	}

	private void addToken(BoxMathTokenType type, ArrayList<MathToken> tokens) {
		addToken(type, null, tokens);

	}

	private void addToken(BoxMathTokenType type, Object literal, ArrayList<MathToken> tokens) {
		String text = source.substring(start, current);

		column = column - (current - start);
		tokens.add(new MathToken(type, text, literal, column, line, start, current));
	}


	private void addToken(BoxMathTokenType type, Object literal, int theStart, int theCurrent,
			ArrayList<MathToken> tokens) {
		String text = source.substring(theStart, theCurrent);

		column = column - (theCurrent - theStart);
		tokens.add(new MathToken(type, text, literal, column, line, theStart, theCurrent));
	}

	private char advance() {
		current++;
		column++;
		return source.charAt(current - 1);
	}

	private boolean isAtEnd() {

		return current >= source.length();
	}

	private boolean peekNextisAtEnd() {
		if (current + 1 >= source.length())
			return true;
		return source.charAt(current + 1) == '\0';
	}

}
