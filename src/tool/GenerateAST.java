package tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class GenerateAST {

	public static void main(String[] args) throws IOException {

		String outputDir = "/home/wes/Wisper Tech 1.0/THEORY/GAMES/PBC/src/Box/Syntax";

		List<String> exprDefinition = Arrays.asList(
				"Assignment		:Token name , Expr value",
				"Contains		:Expr container , boolean open , Expr contents",
				"Binary		:Expr left , Token operator , Expr right", // logical - yroot
				"Mono		:Expr value , Token operator", // sin -tanh
				"Log		:Token operator , Expr valueBase , Expr value", "Factorial	:Expr value , Token operator",
				"Unary		: Token operator , Expr right ",
				"Call 		: Expr callee , Token calleeToken , List<Expr> arguments",
				"Get 		: Expr object , Token name", "Set 		: Expr object, Token name, Expr value",

				"Swap		: Expr swap1 , Expr Swap2",
				
				"Tnemngissa		:Token name , Expr value",
				"Sniatnoc		:Expr container , boolean open , Expr contents",
				"Yranib		:Expr left , Token operator , Expr right", // logical - yroot
				"Onom		:Expr value , Token operator", // sin -tanh
				"Gol		:Token operator , Expr valueBase , Expr value", "Lairotcaf	:Expr value , Token operator",
				"Yranu		: Token operator , Expr right ",
				"Llac 		: Expr callee , Token calleeToken , List<Expr> arguments",
				"Teg 		: Expr object , Token name", "Tes 		: Expr object, Token name, Expr value",
				
				"Variable : Token name",
				"Literal	: Object value",
				"LiteralChar	: char value",
				"Cup : Token identifier , List<Declaration> expression , String lexeme, Token reifitnedi",
				"Pocket : Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi ",
				"Knot :Token identifier , List<Declaration> expression , String lexeme, Token reifitnedi ",
				"Tonk :Token identifier , List<Declaration> expression , String lexeme, Token reifitnedi ",
				"Box :Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi "

		);

		List<String> stmtDefintion = Arrays.asList(
				"Expression : Expr expression , Expr noisserpxe",
				"If			:Expr ifPocket , Expr ifCup , Stmt elseIfStmt , Expr elseCup",
				"Print		:Token keyword , Expr expression", "Return		:Token keyWord , Expr expression",
				"Save		:Token keyword , Expr filePathFileName , Expr objecttosave",
				"Expel		:Token keyword , Expr toExpell , Expr filePath",
				"Read		:Token keyword , Expr filePath , Expr objectToReadInto",
				"Consume	:Token keyword , Expr boxToFill , Expr filePath",
				"Rename		:Token keyword , Expr filePathAndName , Expr filenewname",
				"Move		:Token keyword , Expr OringialfilePathAndFile , Expr newfilePath",
				"Fi			:Expr ifPocket , Expr ifCup , Stmt elseIfStmt , Expr elseCup",
				"Tnirp		:Token keyword , Expr expression", "Nruter		:Token keyWord , Expr expression",
				"Evas		:Token keyword , Expr filePathFileName , Expr objecttosave",
				"Daer		:Token keyword , Expr filePath , Expr objectToReadInto",
				"Emaner		:Token keyword , Expr filePathAndName , Expr filenewname",
				"Evom		:Token keyword , Expr OringialfilePathAndFile , Expr newfilePath",
				"Var 		: Token name , Token type, int num , Stmt initilizer",
				"Rav 		: Token name , Token type, int num , Stmt initilizer");
		List<String> funDefintion = Arrays.asList(
				"Function : Token forwardIdentifier , ArrayList<Token> forwardPrametersType , ArrayList<Token> forwardPrametersNames , Expr sharedCupOrPocketOrKnot , ArrayList<Token> backwardPrametersType , ArrayList<Token> backwardPrametersNames , Token backwardIdentifier");
		List<String> seclarationDefintion = Arrays.asList("FunDecl : Fun function", "StmtDecl : Stmt statement");
		defineCombinedAST(
				outputDir + "/",
				"Declaration", 
				Arrays.asList("Declaration","Fun", "Stmt", "Expr"),
				Arrays.asList(seclarationDefintion, funDefintion, stmtDefintion, exprDefinition),
				Arrays.asList(seclarationDefintion),
				Arrays.asList("Fun", "Stmt", "Expr"),
				Arrays.asList(funDefintion, stmtDefintion, exprDefinition));

	}

	private static void defineCombinedAST(String outputDir, String baseName,List<String> visitorNames,
			List<List<String>> subDefinitions0, List<List<String>> baseDefinitions, List<String> subBaseName,
			List<List<String>> subDefinitions) throws IOException {

		
			defineAST0(outputDir, baseName, subDefinitions0,visitorNames, baseDefinitions ,subBaseName);
		

		int count = 0;
		for (String sBaseName : subBaseName) {
			defineAST(outputDir, baseName, sBaseName, subDefinitions.get(count));
			count++;
		}

	}

	private static void defineAST0(String outputDir, String sBaseName, List<List<String>> subDefinitions0,
			List<String> subBaseNames, List<List<String>> baseDefinitions, List<String> subBaseName)
			throws IOException {
		String path = outputDir + sBaseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");

		writer.println("package Box.Syntax;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println("import java.util.ArrayList;");
		writer.println("import Box.Token.Token;");
		writer.println("import Box.Parser.LogicalOrStorage;");
		
		for (String name : subBaseName) {
				writer.println("import Box.Syntax."+name+".*;");
	
		}
		
		
		
		
		writer.println();
		writer.println("public abstract class " + sBaseName + " {");
			defineVisitor0(writer, subBaseNames, subDefinitions0);			

			for (List<String> types : baseDefinitions) {
				for (String type : types) {
					String className = type.split(":")[0].trim();
					String fields = type.split(":")[1].trim();
					defineType(writer, sBaseName, className, fields);
				}
			}

		writer.println();
		writer.println(" public abstract <R> R accept(Visitor<R> visitor);");

		writer.println("}");
		writer.close();
	}
	private static void defineVisitor0(PrintWriter writer, List<String> subBaseNames, List<List<String>> subDefinitions0) {

		writer.println("	public interface Visitor<R> {");
		for (int i = 0; i<subBaseNames.size();i++) {
			
				List<String> list = subDefinitions0.get(i);
				for (String string : list) {
					
					String typeName = string.split(":")[0].trim();
					writer.println(
							"	R visit" + typeName + subBaseNames.get(i) + "(" + typeName + " " + subBaseNames.get(i).toLowerCase() + ");");
				}
		}
		writer.println("	}");

	}
	private static void defineAST(String outputDir, String baseName, String sBaseName, List<String> types)
			throws IOException {
		String path = outputDir + sBaseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");

		writer.println("package Box.Syntax;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println("import java.util.ArrayList;");
		writer.println("import Box.Token.Token;");
		writer.println("import Box.Parser.LogicalOrStorage;");
		writer.println();
		writer.println("public abstract class " + sBaseName + " extends " + baseName + " {");

		//defineVisitor(writer, sBaseName, types);

		for (String type : types) {
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim();
			defineType(writer, sBaseName, className, fields);
		}

		writer.println();
		//writer.println(" public abstract <R> R accept(Visitor<R> visitor);");

		writer.println("}");
		writer.close();
	}



	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {

		writer.println("	public interface Visitor<R> {");

		for (String type : types) {
			String typeName = type.split(":")[0].trim();
			writer.println("	R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
		}

		writer.println("	}");

	}

	private static void defineType(PrintWriter writer, String baseName, String className, String fields) {

		writer.println("public static class " + className + " extends " + baseName + " {");
		writer.println("	 public " + className + "(" + fields + ") {");
		String[] individualFields = fields.split(", ");

		for (String field : individualFields) {
			String name;
			String[] split = field.split(" ");
			if (split.length == 3) {
				name = field.split(" ")[2];
				String typeName = field.split(" ")[0];

				writer.println("for(" + typeName + " temp: " + name + "){");
				writer.println("	this." + name + ".add(temp);");
				writer.println("}");
			} else if (split.length == 2) {
				name = field.split(" ")[1];
				writer.println("	this." + name + " = " + name + ";");
			}

		}
		writer.println("	}");

		writer.println();
		writer.println("	@Override");
		writer.println("	public <R> R accept(Visitor<R> visitor) {");
		writer.println("	 	return visitor.visit" + className + baseName + "(this);");
		writer.println("	}");

		writer.println();
		for (String field : individualFields) {
			String name;
			String typeName;
			String[] split = field.split(" ");
			if (split.length == 3) {
				name = field.split(" ")[2];
				typeName = field.split(" ")[0];
				writer.println(
						"	public  ArrayList<" + typeName + "> " + name + "=new ArrayList<" + typeName + ">();");
			} else {
				name = field.split(" ")[1];
				typeName = field.split(" ")[0];
				writer.println("	public  " + typeName + " " + name + ";");
			}

		}

		writer.println("	}");

	}

}
