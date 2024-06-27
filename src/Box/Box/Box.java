package Box.Box;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
import Box.Syntax.Stmt;
import Box.Token.Token;
import Box.Token.TokenType;
import resolver.Resolver;

public class Box extends Thread {
	private static final Interpreter interpreter = new Interpreter();
	static boolean hadError = false;
	static boolean hadRuntimeError = false;
	private static ByteArrayOutputStream baos;
	private static Observer promptOb;

	public Box(ByteArrayOutputStream baos2) {
		baos = baos2;

	}

	public void run() {

		try {
			runPrompt();

		} catch (Exception e) {
			// Throwing an exception
			System.out.println("Exception is caught");
		}
	}

//	
//	
//	public static void main(String[] args) throws IOException {
//		if (args.length > 1) {
//			if(args.length == 2) {
//				String path = args[0];
//				String tbd = args[1];
//				if(tbd.equals("fon")) {
//					System.out.println("Usage arguments: " + args[0]);
//					System.out.println("Usage arguments: " + args[1]);
//					runFile(args[0],true,false);
//				}else if(tbd.equals("foff")) {
//					System.out.prinSystem.out.flush();tln("Forward and Backward Interpretation turned off there is nothing to do.");
//					System.exit(64);
//				}else if(tbd.equals("bon")) {
//					System.out.println("Usage arguments: " + args[0]);
//					System.out.println("Usage arguments: " + args[1]);
//					runFile(args[0],true,true);
//				}else if(tbd.equals("boff")) {
//					System.out.println("Usage arguments: " + args[0]);
//					System.out.println("Usage arguments: " + args[1]);
//					runFile(args[0],true,false);
//				}else {
//					System.out.println("Usage: jBox [script0]");
//					System.exit(64);
//				}
//			}else if (args.length==3) {
//				String path = args[0];
//				String tbd = args[1];
//				String tbd1 = args[2];
//				boolean forward = true;
//				boolean backward = false;
//				if(tbd.equals("fon")) {
//					forward=true;
//				}else if(tbd.equals("foff")) {
//					forward=false;
//				}else if(tbd.equals("bon")) {
//					backward=true;
//				}else if(tbd.equals("boff")) {
//					backward=falsBox.Box.PromptObservere;
//				}else {
//					System.out.println("Usage: jBox [script0]");
//					System.exit(64);
//				}
//				
//				
//				if(tbd1.equals("fon")) {
//					forward=true;
//				}else if(tbd1.equals("foff")) {
//					forward=false;
//				}else if(tbd1.equals("bon")) System.out.println("hello");{
//					backward=true;
//				}else if(tbd1.equals("boff")) {
//					backward=false;
//				}else {
//					System.out.println("Usage: jBox [script0]");
//					System.exit(64);
//				}
//				
//				if(forward && backward) {
//					System.out.println("Usage arguments: " + args[0]);
//					System.out.println("Usage arguments: " + args[1]);
//					System.out.println("Running Forward and Backward Interpretation");
//					runFile(args[0],true,true);
//				}else if(!forward && backward) {
//					System.out.println("Usage arguments: " + args[0]);
//					promptObSystem.out.println("Usage arguments: " + args[1]);
//					System.out.println("Running Backward Interpretation");
//					runFile(args[0],false,true);
//				}else if(forward && !backward) {
//					System.out.println("Usage arguments: " + args[0]);
//					System.out.println("Usage arguments: " + args[1]);
//					System.out.println("Running Forward Interpretation");
//					runFile(args[0],true,false);
//				}else {
//					System.out.println("Forward and Backward Interpretation turned off there is nothing to do.");
//					System.exit(64);
//				}
//				
//				
//				
//			}else {
//				System.out.println("Usage: jBox [script0]");
//				System.exit(64);
//			}
//
//		} else if (args.length == 1) {
//			System.out.println("Usage arguments: " + args[0]);
//			runFile(args[0],true,false);
//		} else {
//			System.out.println("Usage Prompt:");
//			runPrompt();
//		}
//
//	}
//
//

	public static void runPrompt() throws IOException {

			System.out.flush();
			String string = baos.toString();
			baos.flush();
			baos.reset();
			if(string.length()>0)
				promptOb.notify(string);
			System.out.flush();

	}

	private static void runFile(String string, boolean forward, boolean backward) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(string));
		run(new String(bytes, Charset.defaultCharset()), forward, backward);
		if (hadError)
			System.exit(65);
		if (hadRuntimeError)
			System.exit(70);
	}

	private static void run(String string, boolean forward, boolean backward) {
		Scanner scanner = new Scanner(string);
		List<Token> tokens = scanner.scanTokensFirstPass();

		Grouper grouper = new Grouper((ArrayList<Token>) tokens);
		ArrayList<Token> toks = grouper.scanTokensSecondPass();

		Parser parser = new Parser(toks, forward, backward);
		List<List<Stmt>> statements = parser.parse();

		if (hadError)
			return;

		interpreter.setForward(forward);
		interpreter.setBackward(backward);

		Resolver resolver = new Resolver(interpreter);
		resolver.resolve(statements);

		if (hadError)
			return;

		interpreter.interpret(statements);

	}

	public static void error(int column, int line, String message) {
		report(column, line, "", message);
	}

	public static void error(Token token, String message) {
		if (token != null) {
			if (token.type == TokenType.EOF)
				report(token.column, token.line, " at end", message);
			else
				report(token.column, token.line, " at '" + token.lexeme + "'", message);
		} else
			report(-1, -1, " at -1", message);

	}

	private static void report(int column, int line, String where, String message) {
		System.err.println("[column " + column + ", line " + line + "] Error " + where + ": " + message);
		hadError = true;
	}

	public static void runtimeError(RuntimeError e) {
		System.err.println(e.getMessage() + " \n[line: " + e.token.line + " column: " + e.token.column + "]");
		hadRuntimeError = true;
	}

	public void addObserver(Observer promptOb) {

		this.promptOb = promptOb;

	}

	public void notify(String string) {
		run(string, true, false);
		System.out.flush();
		hadError = false;
		
	}

}
