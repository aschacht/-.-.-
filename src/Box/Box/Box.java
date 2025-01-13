package Box.Box;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import Box.Grouper.Grouper;
import Box.Interpreter.Interpreter;
import Box.Interpreter.RuntimeError;
import Box.Scanner.Scanner;
import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Declaration;
import Parser.ParserTest;
import resolver.Resolver;

public class Box extends Thread {
	private  final Interpreter interpreter = new Interpreter();
	static boolean hadError = false;
	static boolean hadRuntimeError = false;
	private static  ByteArrayOutputStream baos;
	private  Observer promptOb;

	public static void main(String[] args) {

		baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		Box box = new Box(baos);
		StringBuilder content = new StringBuilder();

		try (java.util.Scanner scanner = new java.util.Scanner(
				new java.io.File("/home/wes/Wisper Tech 1.0/THEORY/GAMES/PBC/TEST/TEST"))) {
			while (scanner.hasNextLine()) {
				content.append(scanner.nextLine()).append(System.lineSeparator());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (content.toString().length() > 0)
			box.run(content.toString(), false);

	}

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
//				boolean forward = true;box
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

	public  void runPrompt() throws IOException {

		System.out.flush();
		String string = baos.toString();
		baos.flush();
		baos.reset();
		if (string.length() > 0)
			promptOb.notify(string);
		System.out.flush();

	}

	private  void runFile(String string, boolean forward, boolean backward) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(string));
		run(new String(bytes, Charset.defaultCharset()), forward);
		if (hadError)
			System.exit(65);
		if (hadRuntimeError)
			System.exit(70);
	}

	public void runJson(String string, boolean forward) {
		Scanner scanner = new Scanner(string);
		List<Token> tokens = scanner.scanTokensFirstPass();

		Grouper grouper = new Grouper((ArrayList<Token>) tokens);
		ArrayList<Token> toks = grouper.scanTokensSecondPass();

		ParserTest parser = new ParserTest(toks, true, false);
		List<Declaration> statements = parser.parse();


		//interpreter.setForward(!forward);
		interpreter.setForward(forward);
		Resolver resolver = new Resolver(interpreter);
		resolver.resolve(statements);

		if (hadError)
			return;

		interpreter.interpret(statements);

	}
	public void run(String string, boolean forward) {
		Scanner scanner = new Scanner(string);
		List<Token> tokens = scanner.scanTokensFirstPass();
		
		Grouper grouper = new Grouper((ArrayList<Token>) tokens);
		ArrayList<Token> toks = grouper.scanTokensSecondPass();
		
		ParserTest parser = new ParserTest(toks, true, false);
		List<Declaration> statements = parser.parse();
		
		
		ObjectMapper om = new ObjectMapper();
		try {
            String jsonResult = om.writeValueAsString(statements);
            System.out.println(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public static  void error(int column, int line, String message, boolean report) {
		report(column, line, "", message, report);
	}

	public static  void error(Token token, String message, boolean report) {
		if (token != null) {
			if (token.type == TokenType.EOF)
				report(token.column, token.line, " at end", message, report);
			else
				report(token.column, token.line, " at '" + token.lexeme + "'", message, report);
		} else
			report(-1, -1, " at -1", message, report);

	}

	private static  void report(int column, int line, String where, String message, boolean report) {
		if (report) {
			System.err.println("[column " + column + ", line " + line + "] Error " + where + ": " + message);
			System.out.println("[column " + column + ", line " + line + "] Error " + where + ": " + message);
			hadError = true;
		}
	}
	
	public static  void resetHadError() {
		hadError = false;
	}

	public static void runtimeError(RuntimeError e) {
		System.err.println(e.getMessage() + " \n[line: " + e.token.line + " column: " + e.token.column + "]");
		System.out.println(e.getMessage() + " \n[line: " + e.token.line + " column: " + e.token.column + "]");
		hadRuntimeError = true;
	}

	public void addObserver(Observer promptOb) {

		this.promptOb = promptOb;

	}

	public void notify(String string) {
		run(string, true);
		System.out.flush();
		hadError = false;

	}

}
