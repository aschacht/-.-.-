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
			if(args.length == 2) {
				String path = args[0];
				String tbd = args[1];
				if(tbd.equals("fon")) {
					System.out.println("Usage arguments: " + args[0]);
					System.out.println("Usage arguments: " + args[1]);
					runFile(args[0],true,false);
				}else if(tbd.equals("foff")) {
					System.out.println("Forward and Backward Interpretation turned off there is nothing to do.");
					System.exit(64);
				}else if(tbd.equals("bon")) {
					System.out.println("Usage arguments: " + args[0]);
					System.out.println("Usage arguments: " + args[1]);
					runFile(args[0],true,true);
				}else if(tbd.equals("boff")) {
					System.out.println("Usage arguments: " + args[0]);
					System.out.println("Usage arguments: " + args[1]);
					runFile(args[0],true,false);
				}else {
					System.out.println("Usage: jBox [script0]");
					System.exit(64);
				}
			}else if (args.length==3) {
				String path = args[0];
				String tbd = args[1];
				String tbd1 = args[2];
				boolean forward = true;
				boolean backward = false;
				if(tbd.equals("fon")) {
					forward=true;
				}else if(tbd.equals("foff")) {
					forward=false;
				}else if(tbd.equals("bon")) {
					backward=true;
				}else if(tbd.equals("boff")) {
					backward=false;
				}else {
					System.out.println("Usage: jBox [script0]");
					System.exit(64);
				}
				
				
				if(tbd1.equals("fon")) {
					forward=true;
				}else if(tbd1.equals("foff")) {
					forward=false;
				}else if(tbd1.equals("bon")) {
					backward=true;
				}else if(tbd1.equals("boff")) {
					backward=false;
				}else {
					System.out.println("Usage: jBox [script0]");
					System.exit(64);
				}
				
				if(forward && backward) {
					System.out.println("Usage arguments: " + args[0]);
					System.out.println("Usage arguments: " + args[1]);
					System.out.println("Running Forward and Backward Interpretation");
					runFile(args[0],true,true);
				}else if(!forward && backward) {
					System.out.println("Usage arguments: " + args[0]);
					System.out.println("Usage arguments: " + args[1]);
					System.out.println("Running Backward Interpretation");
					runFile(args[0],false,true);
				}else if(forward && !backward) {
					System.out.println("Usage arguments: " + args[0]);
					System.out.println("Usage arguments: " + args[1]);
					System.out.println("Running Forward Interpretation");
					runFile(args[0],true,false);
				}else {
					System.out.println("Forward and Backward Interpretation turned off there is nothing to do.");
					System.exit(64);
				}
				
				
				
			}else {
				System.out.println("Usage: jBox [script0]");
				System.exit(64);
			}

		} else if (args.length == 1) {
			System.out.println("Usage arguments: " + args[0]);
			runFile(args[0],true,false);
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
			run(line,true,false);
			hadError = false;
		}

	}

	
	private static void runFile(String string, boolean forward, boolean backward) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(string));
		run(new String(bytes, Charset.defaultCharset()),forward,backward);
		if (hadError)
			System.exit(65);
		if (hadRuntimeError)
			System.exit(70);
	}
	


	
	private static void run(String string, boolean forward, boolean backward) {
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
		Parser parser = new Parser(toks,forward,backward);
		List<Stmt> statements = parser.parse();
		finalTime = (double) System.currentTimeMillis() / 1000.0;
		totalTime =(finalTime-initialTime);
		System.out.println("time to print Statements "+totalTime);
		
		
		if (hadError)
			return;
		
		
		interpreter.setForward(forward);
		interpreter.setBackward(backward);
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
