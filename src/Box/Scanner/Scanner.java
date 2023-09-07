package Box.Scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import Box.Box.Box;
import Box.Interpreter.Bin;
import Box.Syntax.Expr;
import Box.Syntax.Stmt;
import Box.Token.Token;
import Box.Token.TokenType;

public class Scanner {

	private String source;
	private final ArrayList<Token> tokens = new ArrayList<>();
	public final ArrayList<String> identifiers = new ArrayList<>();
	private int column = 0;
	private int line = 1;
	private int start = 0;
	private int current = 0;
	private static Map<String, TokenType> keywords = new HashMap<>();

	public Scanner(String string) {
		keywords.put("true", TokenType.TRUE);
		keywords.put("false", TokenType.FALSE);
		keywords.put("print", TokenType.PRINT);
		keywords.put("return", TokenType.RETURN);
		keywords.put("save", TokenType.SAVE);
		keywords.put("read", TokenType.READ);
		keywords.put("into", TokenType.INTO);
		keywords.put("rename", TokenType.RENAME);
		keywords.put("to", TokenType.TO);
		keywords.put("move", TokenType.MOVE);
		keywords.put("yroot", TokenType.YROOT);
		keywords.put("sin", TokenType.SIN);
		keywords.put("cos", TokenType.COS);
		keywords.put("tan", TokenType.TAN);
		keywords.put("sinh", TokenType.SINH);
		keywords.put("cosh", TokenType.COSH);
		keywords.put("tanh", TokenType.TANH);
		keywords.put("log", TokenType.LOG);
		keywords.put("contains", TokenType.CONTAINS);
		keywords.put("CONTAINS", TokenType.CONTAINS);
		keywords.put("open", TokenType.OPEN);
		keywords.put("OPEN", TokenType.OPEN);
		keywords.put("and", TokenType.AND);
		keywords.put("or", TokenType.OR);
		keywords.put("fun", TokenType.FUN);

		keywords.put("eurt", TokenType.EURT);
		keywords.put("eslaf", TokenType.ESLAF);
		keywords.put("tnirp", TokenType.TNIRP);
		keywords.put("nruter", TokenType.NRUTER);
		keywords.put("evas", TokenType.EVAS);
		keywords.put("daer", TokenType.DAER);
		keywords.put("otni", TokenType.OTNI);
		keywords.put("emaner", TokenType.EMANER);
		keywords.put("ot", TokenType.OT);
		keywords.put("evom", TokenType.EVOM);
		keywords.put("toory", TokenType.TOORY);
		keywords.put("nis", TokenType.NIS);
		keywords.put("soc", TokenType.SOC);
		keywords.put("nat", TokenType.NAT);
		keywords.put("hnis", TokenType.HNIS);
		keywords.put("hsoc", TokenType.HSOC);
		keywords.put("hnat", TokenType.HNAT);
		keywords.put("gol", TokenType.GOL);
		keywords.put("sniatnoc", TokenType.SNIATNOC);
		keywords.put("SNIATNOC", TokenType.SNIATNOC);
		keywords.put("nepo", TokenType.NEPO);
		keywords.put("NEPO", TokenType.NEPO);
		keywords.put("dna", TokenType.DNA);
		keywords.put("ro", TokenType.RO);
		keywords.put("nuf", TokenType.NUF);

		keywords.put("DOUBLE", TokenType.DOUBLE);
		keywords.put("INT", TokenType.INT);
		keywords.put("BIN", TokenType.BIN);
		keywords.put("null", TokenType.NULL);
		keywords.put("NULL", TokenType.NULL);
		keywords.put("llun", TokenType.LLUN);
		keywords.put("LLUN", TokenType.LLUN);

		keywords.put("NILL", TokenType.NILL);
		keywords.put("nill", TokenType.NILL);
		keywords.put("LLIN", TokenType.LLIN);
		keywords.put("llin", TokenType.LLIN);

		keywords.put("not", TokenType.NOT);

		keywords.put("String", TokenType.STRINGPARAMETER);
		keywords.put("char", TokenType.CHARPARAMETER);
		keywords.put("int", TokenType.INTPARAMETER);
		keywords.put("double", TokenType.DOUBLEPARAMETER);
		keywords.put("bin", TokenType.BINPARAMETER);
		keywords.put("boolean", TokenType.BOOLEANPARAMETER);
		keywords.put("enforce", TokenType.ENFORCEPARAMETER);

		keywords.put("Gnirts", TokenType.GNIRTSPARAMETER);
		keywords.put("rahc", TokenType.RAHCPARAMETER);
		keywords.put("tni", TokenType.TNIPARAMETER);
		keywords.put("elbuod", TokenType.ELBUODPARAMETER);
		keywords.put("nib", TokenType.NIBPARAMETER);
		keywords.put("naeloob", TokenType.NAELOOBPARAMETER);
		keywords.put("ecrofne", TokenType.ECROFNEPARAMETER);

		keywords.put("knt", TokenType.KNOT);
		keywords.put("cup", TokenType.CUP);
		keywords.put("pkt", TokenType.POCKET);
		keywords.put("box", TokenType.BOX);

		keywords.put("tnk", TokenType.TONK);
		keywords.put("puc", TokenType.PUC);
		keywords.put("tkp", TokenType.TEKCOP);
		keywords.put("xob", TokenType.XOB);
		keywords.put("type", TokenType.TYPE);
		keywords.put("epyt", TokenType.EPYT);

		this.source = string;

	}

	public ArrayList<Token> scanTokensFirstPass() {
		while (!isAtEnd()) {

			start = current;
			scanToken(tokens);

		}

		return tokens;
	}

	private void scanToken(ArrayList<Token> tokens) {
		char c = advance();

		switch (c) {
		case '(':
			addToken(TokenType.OPENPAREN, tokens);
			break;
		case ')':
			addToken(TokenType.CLOSEDPAREN, tokens);
			break;
		case '{':
			addToken(TokenType.OPENBRACE, tokens);

			break;
		case '}':
			addToken(TokenType.CLOSEDBRACE, tokens);
			break;
		case '[':
			addToken(TokenType.OPENSQUARE, tokens);

			break;
		case ']':
			addToken(TokenType.CLOSEDSQUARE, tokens);
			break;
		case ',':
			addToken(TokenType.COMMA, tokens);
			break;
		case '.':
			addToken(TokenType.DOT, tokens);
			break;
		case '-':
			if (match('=')) {
				addToken(TokenType.MINUSEQUALS, tokens);
			} else if (match('-')) {
				addToken(TokenType.MINUSMINUS, tokens);
			} else {
				addToken(TokenType.MINUS, tokens);
			}
			break;
		case '|':
			addToken(TokenType.TEMPLID, tokens);
			break;
		case '&':
			addToken(TokenType.SINGLEAND, tokens);
			break;
		case '+':
			if (match('=')) {
				addToken(TokenType.PLUSEQUALS, tokens);
			} else if (match('+')) {
				addToken(TokenType.PLUSPLUS, tokens);
			} else {
				addToken(TokenType.PLUS, tokens);
			}
			break;
		case '*':
			addToken(TokenType.TIMES, tokens);
			break;
		case '^':
			addToken(TokenType.POWER, tokens);
			break;
		case '!':
			if (match('=')) {
				addToken(TokenType.NOTEQUALS, tokens);
			} else if (match('!')) {
				addToken(TokenType.DOUBLEBANG, tokens);
			} else {
				addToken(TokenType.BANG, tokens);
			}
			break;
		case '=':
			if (match('=')) {
				addToken(TokenType.EQUALSEQUALS, tokens);

			} else if (match('!')) {
				addToken(TokenType.EQUALSNOT, tokens);
			} else if (match('+')) {
				addToken(TokenType.EQUALSPLUS, tokens);
			} else if (match('-')) {
				addToken(TokenType.EQUALSMINUS, tokens);
			} else if (match('>')) {
				addToken(TokenType.EQUALGREATERTHEN, tokens);
			} else if (match('<')) {
				addToken(TokenType.EQUALLESSTHEN, tokens);
			} else {
				addToken(TokenType.ASIGNMENTEQUALS, tokens);

			}

			break;
		case '<':
			if (match('=')) {
				addToken(TokenType.LESSTHENEQUAL, tokens);
			} else if (match('<')) {
				if (match('<')) {
					addToken(TokenType.CONSUME, tokens);
				} else {
					Box.error(column, line, "Unexpected character " + c);
				}
			} else {
				addToken(TokenType.LESSTHEN, tokens);
			}
			break;
		case '>':
			if (match('=')) {
				addToken(TokenType.GREATERTHENEQUAL, tokens);
			} else if (match('>')) {
				if (match('>')) {
					addToken(TokenType.EXPELL, tokens);
				} else {
					Box.error(column, line, "Unexpected character " + c);
				}
			} else {
				addToken(TokenType.GREATERTHEN, tokens);
			}
			break;
		case '/':
			if (match('/'))
				while (peek() != '\n' && !isAtEnd())
					advance();
			else
				addToken(TokenType.FORWARDSLASH, tokens);

			break;
		case '\\':
			addToken(TokenType.BACKSLASH, tokens);
			break;
		case ';':
			addToken(TokenType.SEMICOLON, tokens);
			break;
		case ' ':
			addToken(TokenType.SPACE, tokens);
			break;
		case '\r':
			addToken(TokenType.SPACERETURN, tokens);
			break;
		case '\t':
			addToken(TokenType.TAB, tokens);
			break;
		case '\n':
			addToken(TokenType.NEWLINE, tokens);
			line++;
			column = 0;
			break;
		case '"':
			string(tokens);
			break;
		case '\'':
			character(tokens);
			break;
		default:
			if (isDigit(c))
				numberreifitnediBIN(tokens, c);
			else if (isAlpha(c))
				identifierOrReifitnediOrBinaryNum(tokens, c);
			else
				Box.error(column, line, "Unexpected character " + c);
		}

	}

	private void identifierOrReifitnediOrBinaryNum(ArrayList<Token> tokens, char c) {

		if (isb(c) && isBinary(peek())) {
			while (isBinary(peek()))
				advance();
			TokenType type = null;
			String text = null;
			Bin binNum = null;
			boolean endb = false;
			if (isb(peek())) {
				advance();
				type = TokenType.BINNUM;
				endb = true;
			} else if (!isb(peek())) {
				type = TokenType.BINNUM;
			}
			if (type == TokenType.BINNUM) {
				if (endb)
					binNum = Bin.valueOf(source.substring(start + 1, current - 1));
				else
					binNum = Bin.valueOf(source.substring(start + 1, current));
			} else {
				while (isAlphaNumeric(peek()))
					advance();
				text = source.substring(start, current);
				//// hack
				if (!identifiers.contains(text)) {
					identifiers.add(text);
				}
				if (type == null)
					type = TokenType.IDENTIFIER;
			}
			if (text != null)
				addToken(type, text, tokens);
			else
				addToken(type, binNum, tokens);

		} else if (isb(c) && !isBinary(peek())) {
			while (isAlphaNumeric(peek()))
				advance();

			String text = source.substring(start, current);
			//// hack
			if (!identifiers.contains(text)) {
				identifiers.add(text);

			}
			TokenType type = keywords.get(text);
			if (type == null)
				type = TokenType.IDENTIFIER;
			addToken(type, tokens);
		} else if (!isb(c) && !isBinary(peek()) && isAlpha(c) && (isAlphaNumeric(peek()))) {
			while (isAlphaNumeric(peek()))
				advance();

			String text = source.substring(start, current);
			//// hack
			if (!identifiers.contains(text)) {
				identifiers.add(text);

			}
			TokenType type = keywords.get(text);
			if (type == null)
				type = TokenType.IDENTIFIER;
			addToken(type, tokens);
		} else if (isAlpha(c)) {
			while (isAlphaNumeric(peek()))
				advance();

			String text = source.substring(start, current);
			//// hack
			if (!identifiers.contains(text)) {
				identifiers.add(text);

			}
			TokenType type = keywords.get(text);
			if (type == null)
				type = TokenType.IDENTIFIER;
			addToken(type, tokens);
		} else if (isDigit(c) && (isAlphaNumeric(peek()))) {
			while (isAlphaNumeric(peek()))
				advance();

			String text = source.substring(start, current);
			//// hack
			if (!identifiers.contains(text)) {
				identifiers.add(text);

			}
			TokenType type = keywords.get(text);
			if (type == null)
				type = TokenType.REIFITNEDI;
			addToken(type, tokens);
		}

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

	private void numberreifitnediBIN(ArrayList<Token> tokens, char c) {
		if (previous() == '.') {
			while (isDigit(peek()))
				advance();
			addToken(TokenType.INTNUM, Integer.valueOf(source.substring(start, current)), tokens);
			return;
		}

		while (isDigit(peek()))
			advance();
		if (peek() == '.' && isDigit(peekNext())) {
			Hashtable<Integer, Integer> startsandEnds = new Hashtable<Integer, Integer>();
			ArrayList<Integer> keys = new ArrayList<Integer>();
			keys.add(start);
			startsandEnds.put(start, current);
			advance();
			while (!isAlpha(peek()) && !anythingOtherThenDigit(peek())) {
				int newStart = current;
				if (isDigit(peek())) {
					int newFinish = current;
					keys.add(newStart);
					startsandEnds.put(newStart, newFinish);
					advance();
				}else if (isAlpha(peek())) {
					int newFinish = current;
					keys.add(newStart);
					startsandEnds.put(newStart, newFinish);
					advance();
				}else {
					break;
				}
			}
			if ((isAlpha(peek()) || anythingOtherThenDigit(peek())) && previousCurrent() == '.') {
				for (int i = 0; i < keys.size(); i++) {

					Integer valueOfIntNum = Integer
							.valueOf(source.substring(keys.get(i), startsandEnds.get(keys.get(i))));
					addToken(TokenType.INTNUM, valueOfIntNum, keys.get(i), startsandEnds.get(keys.get(i)), tokens);
					if (i < keys.size() - 1)
						addToken(TokenType.DOT, source.substring(startsandEnds.get(keys.get(i)), keys.get(i + 1)),
								startsandEnds.get(keys.get(i)), keys.get(i + 1), tokens);
					else
						addToken(TokenType.DOT, source.substring(startsandEnds.get(keys.get(i)), current),
								startsandEnds.get(keys.get(i)), current, tokens);

				}

			} else {

				addToken(TokenType.DOUBLENUM, Double.valueOf(source.substring(start, current)), tokens);
			}

		} else if (isAlpha(peek()) || isDigit(peek())) {
			identifierOrReifitnediOrBinaryNum(tokens, c);

		} else {
			if (isb(peek()) && isBinary(source.substring(start, current))) {
				addToken(TokenType.BINNUM, Bin.valueOf(source.substring(start, current)), tokens);
				advance();
			} else if (isb(peek()) && !isBinary(source.substring(start, current))) {
				identifierOrReifitnediOrBinaryNum(tokens, c);
			} else
				addToken(TokenType.INTNUM, Integer.valueOf(source.substring(start, current)), tokens);
		}
	}

	private boolean anythingOtherThenDigit(char c) {
		boolean istrue = false;
		switch (c) {
		case '(':
			istrue = true;
			break;
		case ')':
			istrue = true;
			break;
		case '{':
			istrue = true;

			break;
		case '}':
			istrue = true;
			break;
		case '[':
			istrue = true;

			break;
		case ']':
			istrue = true;
			break;
		case ',':
			istrue = true;
			break;
		case '.':
			istrue = true;
			break;
		case '-':
			istrue = true;
			break;
		case '|':
			istrue = true;
			break;
		case '&':
			istrue = true;
			break;
		case '+':
			istrue = true;
			break;
		case '*':
			istrue = true;
			break;
		case '^':
			istrue = true;
			break;
		case '!':
			istrue = true;
			break;
		case '=':
			istrue = true;
			break;
		case '<':
			istrue = true;
			break;
		case '>':
			istrue = true;
			break;
		case '/':
			istrue = true;
			break;
		case '\\':
			istrue = true;
			break;
		case ';':
			istrue = true;
			break;
		case '"':
			istrue = true;
			break;
		case '\'':
			istrue = true;
			break;
		}
		return istrue;
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

	private void string(ArrayList<Token> tokens) {
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
		addToken(TokenType.STRING, value, tokens);
	}

	private void character(ArrayList<Token> tokens) {
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
		addToken(TokenType.CHAR, value, tokens);
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

	private void addToken(TokenType type, ArrayList<Token> tokens) {
		addToken(type, null, tokens);

	}

	private void addToken(TokenType type, Object literal, ArrayList<Token> tokens) {
		String text = source.substring(start, current);

		column = column - (current - start);
		tokens.add(new Token(type, text, literal, null,null, column, line, start, current));
	}

	private void addToken(TokenType type, Object literal, int theStart, int theCurrent, ArrayList<Token> tokens) {
		String text = source.substring(theStart, theCurrent);

		column = column - (theCurrent - theStart);
		tokens.add(new Token(type, text, literal, null,null, column, line, theStart, theCurrent));
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
