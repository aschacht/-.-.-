package Box.Box;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Box.Grouper.Grouper;
import Box.Interpreter.Interpreter;
import Box.Interpreter.RuntimeError;
import Box.Parser.Parser;
import Box.Scanner.Scanner;
import Box.Syntax.Expr;
import Box.Syntax.Expr.Binary;
import Box.Syntax.Expr.Literal;
import Box.Syntax.Expr.Variable;
import Box.Syntax.Stmt;
import Box.Syntax.Stmt.Expression;
import Box.Syntax.Stmt.Function;
import Box.Syntax.Stmt.If;
import Box.Syntax.ifStatmentHolder;
import Box.Token.Token;
import Box.Token.TokenType;
import resolver.Resolver;

public class Box {
	private static final Interpreter interpreter = new Interpreter();
	static boolean hadError = false;
	static boolean hadRuntimeError = false;

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: jBox [script0]");
			System.exit(64);
		} else if (args.length == 1) {
			System.out.println("Usage arguments: " + args[0]);
			runFile(args[0]);
		} else {
			System.out.println("Usage Prompt:");
			runPrompt();
		}

	}

	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		for (;;) {
			System.out.print("> ");
			String line = reader.readLine();
			if (line == null)
				break;
			run(line);
			hadError = false;
		}

	}

	private static void runFile(String string) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(string));
		run(new String(bytes, Charset.defaultCharset()));
		if (hadError)
			System.exit(65);
		if (hadRuntimeError)
			System.exit(70);
	}

	private static void run(String string) {
		Double initialTime = (double) System.currentTimeMillis() / 1000.0;
		Scanner scanner = new Scanner(string);
		List<Token> tokens = scanner.scanTokensFirstPass();
//		for (Token token : tokens) {
//			if (token.type == TokenType.SPACE) {
//
//			} else if (token.type == TokenType.SPACERETURN) {
//
//			} else if (token.type == TokenType.TAB) {
//
//			} else if (token.type == TokenType.NEWLINE) {
//
//			} else {
//				System.out.print(token);
//				System.out.print("\t " + token.column + " ");
//				System.out.print(" " + token.line + " ");
//				System.out.println();
//			}
//
//		}
		Double finalTime = (double) System.currentTimeMillis() / 1000.0;
		Double totalTime =(finalTime-initialTime);
		System.out.println("time to run scanner "+totalTime);
		
		initialTime = (double) System.currentTimeMillis() / 1000.0;
		Grouper grouper = new Grouper((ArrayList<Token>) tokens);
		ArrayList<Token> toks = grouper.scanTokensSecondPass();
		finalTime = (double) System.currentTimeMillis() / 1000.0;
		totalTime =(finalTime-initialTime);
		System.out.println("time to run grouper "+totalTime);
		
		
		initialTime = (double) System.currentTimeMillis() / 1000.0;
		Parser parser = new Parser(toks);
		List<Stmt> statements = parser.parse();
		finalTime = (double) System.currentTimeMillis() / 1000.0;
		totalTime =(finalTime-initialTime);
		System.out.println("time to print Statements "+totalTime);
		
		
		if (hadError)
			return;
		
		initialTime = (double) System.currentTimeMillis() / 1000.0;
		Resolver resolver = new Resolver(interpreter);
		resolver.resolve(statements);
		finalTime = (double) System.currentTimeMillis() / 1000.0;
		totalTime =(finalTime-initialTime);
		System.out.println("time to resolve Statements "+totalTime);
		
		if(hadError)return;
		
		
		initialTime = (double) System.currentTimeMillis() / 1000.0;
		interpreter.interpret(statements);

		finalTime = (double) System.currentTimeMillis() / 1000.0;
		totalTime =(finalTime-initialTime);
		System.out.println("time to interpret Statements "+totalTime);
//		for (Stmt stmt : statements) {
//			if(stmt instanceof Expression) {
//				System.out.println(((Knot)((Expression) stmt).expression).identifier);
//				System.out.println(((Knot)((Expression) stmt).expression).reifitnedi);
//				
//				List<KnotGroup> knotgroups = ((Knot)((Expression) stmt).expression).knotgroups;
//				for (KnotGroup kg : knotgroups) {
//					System.out.println(kg.start);
//					System.out.println(kg.declaration);
//					System.out.println(kg.finish);
//					System.out.println("-----------------");
//				}
//				
//			}
//			
//			
//		}

	}

	public static void error(int column, int line, String message) {
		report(column, line, "", message);
	}

	public static void error(Token token, String message) {
		if (token.type == TokenType.EOF)
			report(token.column, token.line, " at end", message);
		else
			report(token.column, token.line, " at '" + token.lexeme + "'", message);
	}

	private static void report(int column, int line, String where, String message) {
		System.err.println("[column " + column + ", line " + line + "] Error " + where + ": " + message);
		hadError = true;
	}

	public static void runtimeError(RuntimeError e) {
		System.err.println(e.getMessage() + " \n[line: " + e.token.line + " column: " + e.token.column + "]");
		hadRuntimeError = true;
	}

}
