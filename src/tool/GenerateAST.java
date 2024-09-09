package tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import Parser.Expr.Box;
import Parser.Expr.Cup;
import Parser.Expr.Knot;
import Parser.Expr.Pocket;
import Parser.Expr.Tonk;
import Parser.Expr.Variable;

public class GenerateAST {

	public static void main(String[] args) throws IOException {

		String outputDir = "/home/wes/Wisper Tech 1.0/THEORY/GAMES/PBC/src/Parser";

		List<String> exprDefinition = Arrays.asList(
				"Assignment		: Token name , Expr value",
				"Contains		: Expr container , boolean open , Expr contents",
				"Additive		: Expr callee , Token operator , Expr toadd", //add,push
				"ParamContOp	: Expr callee , Token operator , Expr.Literal index",//Remove getAt
				"NonParamContOp	: Expr callee , Token operator",//clear,size ,empty,pop
				"Setat			: Expr callee , Expr.Literal index , Expr toset ",
				"Sub			: Expr callee , Expr.Literal start , Expr.Literal end ",
				"Binary			: Expr left , Token operator , Expr right", // logical - yroot
				"Mono			: Expr value , Token operator", // sin -tanh
				"Log			: Token operator , Expr valueBase , Expr value", 
				"Factorial		: Expr value , Token operator",
				"Unary			: Token operator , Expr right ",
				"Call 			: Expr callee , Token calleeToken , List<Expr> arguments",
				"Get 			: Expr object , Token name",
				"Set 			: Expr object, Token name, Expr value",
				"Knot 			: Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi ",

				"Cup 			: Token identifier , List<Declaration> expression , String lexeme, Token reifitnedi",
				"Template 		: Expr container",
				"Link			: Expr container",
				"Pocket 		: Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi ",
				"Box 			: Token identifier , List<Expr> expression , String lexeme, Token reifitnedi ",
				
				"Monoonom		:Expr value , Token operatorForward , Token operatorBackward",
				"Addittidda		: Expr calleeForward , Token operatorForward , Expr toadd , Token operatorBackward , Expr calleeBackward",
				"ParCoOppOoCraP	: Expr calleeForward , Token operatorForward , Expr.Literal index , Expr calleeBackward , Token operatorBackward",//Remove getAt
				"NoPaCoOOoCaPoN	: Expr calleeForward , Token operatorForward , Expr calleeBackward , Token operatorBackward  ",//clear,size ,empty,pop
				"Setattates		: Expr calleeForward , Expr.Literal index , Expr toset , Expr calleeBackward ",
				"Subbus			: Expr calleeForward , Expr.Literal start , Expr.Literal end , Expr calleeBackward  ",
				"Binaryyranib	: Expr left , Token operatorForward , Token operatorBackward , Expr right",
				"Loggol			: Token operatorForward , Expr valueBase , Expr value , Token operatorBackward", 
				"Callllac 		: Expr calleeForward , Token calleeTokenForward , Expr calleeBackward , Token calleeTokenBackward , List<Expr> arguments ",
				
				"Expressiontmts	: Token expressionToken , Expr expression , Token tnemetatsToken",
				"Assignmenttnemgissa		: Token nameForward , Expr value , Token nameBackward ",
				
				
				
				"Swap			: Expr swap1 , Expr Swap2",
				"Variable 		: Token name",
				"LiteralChar	: char value",
				"Literal		: Object value",
				"PocketOpen		: Token ctrl",
				"PocketClosed	: Token ctrl",
				"CupOpen		: Token ctrl",
				"CupClosed		: Token ctrl",
				"BoxOpen		: Token ctrl",
				"BoxClosed		: Token ctrl",

				"Tonk 			: Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi ",
				"Tes 			: Expr object, Token name, Expr value",
				"Teg 			: Expr object , Token name",
				"Llac 			: Expr callee , Token calleeToken , List<Expr> arguments",
				"Gol			: Token operator , Expr valueBase , Expr value",
				"Lairotcaf		: Expr value , Token operator",
				"Onom			: Expr value , Token operator", // sin -tanh
				"Yranib			: Expr left , Token operator , Expr right", // logical - yroot
				"Yranu			: Token operator , Expr right ",
				"Bus			: Expr callee , Expr.Literal start , Expr.Literal end ",
				"Tates			: Expr callee , Expr.Literal index , Expr toset ",
				"PoTnocMarapNon	: Expr callee , Token operator",//clear,size ,empty,pop
				"PoTnocMarap	: Expr callee , Token operator , Expr.Literal index",//Remove getAt
				"Evitidda		: Expr callee , Token operator , Expr toadd", //add,push
				"Sniatnoc		: Expr container , boolean open , Expr contents",
				"Tnemngissa		: Token name , Expr value"
);

		List<String> stmtDefintion = Arrays.asList(
				"Expression : Expr expression , Expr noisserpxe ",

				"If				: Expr ifPocket , Expr ifCup , Stmt elseIfStmt , Expr elseCup",
				"Print			: Token keyword , Expr expression", 
				"Save			: Token keyword , Expr filePathFileName , Expr objecttosave",
				"Read			: Token keyword , Expr filePath , Expr objectToReadInto",
				"Rename			: Token keyword , Expr filePathAndName , Expr filenewname",
				"Move			: Token keyword , Expr OringialfilePathAndFile , Expr newfilePath",
				"Return 		: Token keyword , Expr expression",
				"Var 			: Token name , Token type, int num , Expr initilizer",

				"TemplatVar		: Token name, Token superclass",
				"Expel			: Token keyword , Expr toExpell , Expr filePath",
				"Consume		: Token keyword , Expr boxToFill , Expr filePath",

				"Ifi			: Expr ifPocket , Stmt elseIf",
				"StmttmtS		: Token keywordForward , Expr expression , Token keywordBackward",
				"Saveevas		: Token keywordForward , Expr filePathFileName , Expr objecttosave , Token keywordBackward",
				"Readdaer		: Token keywordForward , Expr filePath , Expr objectToReadInto , Token keywordBackward",
				"Renameemaner	: Token keywordForward , Expr filePathAndName , Expr filenewname , Token keywordBackward",
				"Moveevom		: Token keywordForward , Expr OringialfilePathAndFile , Expr newfilePath , Token keywordBackward",
				
				
				"Stmtnoisserpxe	: Token statementToken , Expr expression , Token noisserpxeToken",
				
				"Rav 			: Token name , Token type, int num , Stmt initilizer",
				"Nruter 		: Token keyword , Expr expression",
				"Evom			: Token keyword , Expr OringialfilePathAndFile , Expr newfilePath",
				"Emaner			: Token keyword , Expr filePathAndName , Expr filenewname",
				"Daer			: Token keyword , Expr filePath , Expr objectToReadInto",
				"Evas			: Token keyword , Expr filePathFileName , Expr objecttosave",
				"Tnirp			: Token keyword , Expr expression",
				"Fi				: Expr ifPocket , Expr ifCup , Stmt elseIfStmt , Expr elseCup"
				);
		List<String> funDefintion = Arrays.asList(
				"Function 		: Token forwardIdentifier , ArrayList<Token> forwardPrametersType , ArrayList<Token> forwardPrametersNames , Expr sharedCup , ArrayList<Token> backwardPrametersType , ArrayList<Token> backwardPrametersNames , Token backwardIdentifier",
				"FunctionLink 	: Token forwardIdentifier , ArrayList<Token> forwardPrametersType , ArrayList<Token> forwardPrametersNames , ArrayList<Token> backwardPrametersType , ArrayList<Token> backwardPrametersNames , Token backwardIdentifier ");
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

		writer.println("package Parser;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println("import java.util.ArrayList;");
		writer.println("import Box.Token.Token;");
		
		for (String name : subBaseName) {
				writer.println("import Parser."+name+".*;");
	
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

		writer.println("package Parser;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println("import java.util.ArrayList;");
		writer.println("import Box.Token.Token;");
		writer.println("import java.util.Objects;");
		writer.println("import Box.Token.TokenType;");
		writer.println();
		writer.println("public abstract class " + sBaseName + " extends " + baseName + " {");

		
		writer.println("public abstract void reverse();");
		if(sBaseName.equals("Expr"))
		writer.println("@Override\n"
				+ "	public boolean equals(Object obj) {\n"
				+ "		if (this instanceof Variable) {\n"
				+ "			return obj instanceof Variable && ((Variable) this).name.lexeme.equals(((Variable) obj).name.lexeme)\n"
				+ "					&& ((Variable) this).name.type == ((Variable) obj).name.type\n"
				+ "					&& ((Variable) this).name.line == ((Variable) obj).name.line\n"
				+ "					&& ((Variable) this).name.column == ((Variable) obj).name.column\n"
				+ "					&& ((Variable) this).name.start == ((Variable) obj).name.start\n"
				+ "					&& ((Variable) this).name.finish == ((Variable) obj).name.finish;\n"
				+ "		} else if (this instanceof Pocket) {\n"
				+ "			return obj instanceof Pocket && ((Pocket) this).lexeme.equals(((Pocket) obj).lexeme)\n"
				+ "					&& ((Pocket) this).identifier.type == ((Pocket) obj).identifier.type\n"
				+ "					&& ((Pocket) this).identifier.line == ((Pocket) obj).identifier.line\n"
				+ "					&& ((Pocket) this).identifier.column == ((Pocket) obj).identifier.column\n"
				+ "					&& ((Pocket) this).identifier.start == ((Pocket) obj).identifier.start\n"
				+ "					&& ((Pocket) this).identifier.finish == ((Pocket) obj).identifier.finish;\n"
				+ "		}else if (this instanceof Cup) {\n"
				+ "			return obj instanceof Cup && ((Cup) this).lexeme.equals(((Cup) obj).lexeme)\n"
				+ "					&& ((Cup) this).identifier.type == ((Cup) obj).identifier.type\n"
				+ "					&& ((Cup) this).identifier.line == ((Cup) obj).identifier.line\n"
				+ "					&& ((Cup) this).identifier.column == ((Cup) obj).identifier.column\n"
				+ "					&& ((Cup) this).identifier.start == ((Cup) obj).identifier.start\n"
				+ "					&& ((Cup) this).identifier.finish == ((Cup) obj).identifier.finish;\n"
				+ "		}else if (this instanceof Box) {\n"
				+ "			return obj instanceof Box && ((Box) this).lexeme.equals(((Box) obj).lexeme)\n"
				+ "					&& ((Box) this).identifier.type == ((Box) obj).identifier.type\n"
				+ "					&& ((Box) this).identifier.line == ((Box) obj).identifier.line\n"
				+ "					&& ((Box) this).identifier.column == ((Box) obj).identifier.column\n"
				+ "					&& ((Box) this).identifier.start == ((Box) obj).identifier.start\n"
				+ "					&& ((Box) this).identifier.finish == ((Box) obj).identifier.finish;\n"
				+ "		}else if (this instanceof Knot) {\n"
				+ "			return obj instanceof Knot && ((Knot) this).lexeme.equals(((Knot) obj).lexeme)\n"
				+ "					&& ((Knot) this).identifier.type == ((Knot) obj).identifier.type\n"
				+ "					&& ((Knot) this).identifier.line == ((Knot) obj).identifier.line\n"
				+ "					&& ((Knot) this).identifier.column == ((Knot) obj).identifier.column\n"
				+ "					&& ((Knot) this).identifier.start == ((Knot) obj).identifier.start\n"
				+ "					&& ((Knot) this).identifier.finish == ((Knot) obj).identifier.finish;\n"
				+ "		}else if (this instanceof Tonk) {\n"
				+ "			return obj instanceof Tonk && ((Tonk) this).lexeme.equals(((Tonk) obj).lexeme)\n"
				+ "					&& ((Tonk) this).identifier.type == ((Tonk) obj).identifier.type\n"
				+ "					&& ((Tonk) this).identifier.line == ((Tonk) obj).identifier.line\n"
				+ "					&& ((Tonk) this).identifier.column == ((Tonk) obj).identifier.column\n"
				+ "					&& ((Tonk) this).identifier.start == ((Tonk) obj).identifier.start\n"
				+ "					&& ((Tonk) this).identifier.finish == ((Tonk) obj).identifier.finish;\n"
				+ "		}\n"
				+ "		return super.equals(obj);\n"
				+ "	}\n"
				+ "\n"
				+ "	@Override\n"
				+ "	public int hashCode() {\n"
				+ "		if (this instanceof Variable) {\n"
				+ "			return Objects.hash(((Variable) this).name.lexeme, ((Variable) this).name.type, ((Variable) this).name.line,\n"
				+ "					((Variable) this).name.column, ((Variable) this).name.start, ((Variable) this).name.finish);\n"
				+ "		} else if (this instanceof Pocket) {\n"
				+ "			return Objects.hash(((Pocket) this).identifier.lexeme, ((Pocket) this).identifier.type, ((Pocket) this).identifier.line,\n"
				+ "					((Pocket) this).identifier.column, ((Pocket) this).identifier.start, ((Pocket) this).identifier.finish);\n"
				+ "		} else if (this instanceof Cup) {\n"
				+ "			return Objects.hash(((Cup) this).identifier.lexeme, ((Cup) this).identifier.type, ((Cup) this).identifier.line,\n"
				+ "					((Cup) this).identifier.column, ((Cup) this).identifier.start, ((Cup) this).identifier.finish);\n"
				+ "		}else if (this instanceof Box) {\n"
				+ "			return Objects.hash(((Box) this).identifier.lexeme, ((Box) this).identifier.type, ((Box) this).identifier.line,\n"
				+ "					((Box) this).identifier.column, ((Box) this).identifier.start, ((Box) this).identifier.finish);\n"
				+ "		}else if (this instanceof Knot) {\n"
				+ "			return Objects.hash(((Knot) this).identifier.lexeme, ((Knot) this).identifier.type, ((Knot) this).identifier.line,\n"
				+ "					((Knot) this).identifier.column, ((Knot) this).identifier.start, ((Knot) this).identifier.finish);\n"
				+ "		}else if (this instanceof Tonk) {\n"
				+ "			return Objects.hash(((Tonk) this).identifier.lexeme, ((Tonk) this).identifier.type, ((Tonk) this).identifier.line,\n"
				+ "					((Tonk) this).identifier.column, ((Tonk) this).identifier.start, ((Tonk) this).identifier.finish);\n"
				+ "		}\n"
				+ "		return super.hashCode();\n"
				+ "	}\n"
				+ "");
		

		for (String type : types) {
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim();
			defineType(writer, sBaseName, className, fields);
		}

		writer.println();
		

		writer.println("}");
		writer.close();
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
		
		writer.println("	public  "+className+"("+className+" other) {");
		for (String field : individualFields) {
			String name;
			String[] split = field.split(" ");
			if (split.length == 3) {
				name = field.split(" ")[2];
				writer.println("	this." + name + " = other." + name + ";");
			} else if (split.length == 2) {
				name = field.split(" ")[1];
				writer.println("	this." + name + " = other." + name + ";");
			}

		}
		writer.println("	}");
		writer.println();


		writer.println();
		writer.println("	@Override");
		writer.println("	public <R> R accept(Visitor<R> visitor) {");
		writer.println("	 	return visitor.visit" + className + baseName + "(this);");
		writer.println("	}");
		writer.println();
		writer.println("	@Override");
		writer.println("	public void reverse() {");
		
		
		if(!className.equals("Binary")) {
		for (String field : individualFields) {
			String name;
			String[] split = field.split(" ");
			if (split.length == 3) {
				name = field.split(" ")[2];
				String typeName = field.split(" ")[0];

				writer.println("for(" + typeName + " temp: " + name + "){");
				
				writer.println("}");
			} else if (split.length == 2) {
				name = field.split(" ")[1];
				String type = split[0];
				if(type.equals("Expr"))
					writer.println("	this." + name + ".reverse();");
				else if(type.equals("Stmt"))
					writer.println("	this." + name + ".reverse();");
			}

		}
		}else if(className.equals("Binary")) {
								
		writer.println(""
				+ "if(this.operator.type == TokenType.AND || this.operator.type == TokenType.DNA"
				+ "||this.operator.type == TokenType.OR || this.operator.type == TokenType.RO){"
				+ " Expr temp = left;"
				+ "		this.left = this.right;"
				+ "		this.right = temp;"
				+ "this.left.reverse();"
				+ "this.right.reverse();"
				+ "}");
				

			
		}else if(className.equals("Binaryyranib")) {
		
		writer.println(""
				+ "if((this.operatorForward.type == TokenType.AND || this.operatorForward.type == TokenType.DNA"
				+ "||this.operatorForward.type == TokenType.OR || this.operatorForward.type == TokenType.RO) &&"
				+ "(this.operatorBackward.type == TokenType.AND || this.operatorBackward.type == TokenType.DNA\"\n"
				+ "				+ \"||this.operatorBackward.type == TokenType.OR || this.operatorBackward.type == TokenType.RO)){"
				
				+ "Token tempt =operatorForward;"
				+ "operatorForward = operatorBackward;"
				+ "operatorBackward= tempt;"
				+ ""
				+ " Expr temp = left;"
				+ "		this.left = this.right;"
				+ "		this.right = temp;"
				+ "this.left.reverse();"
				+ "this.right.reverse();");
		
		
		
	}else if(className.equals("Yranib")) {
		
writer.println(""
		+ "if(this.operator.type == TokenType.AND || this.operator.type == TokenType.DNA"
		+ "||this.operator.type == TokenType.OR || this.operator.type == TokenType.RO){"
		+ " Expr temp = left;"
		+ "		this.left = this.right;"
		+ "		this.right = temp;"
		+ "this.left.reverse();"
		+ "this.right.reverse();"
		+ "}");
		


}
		
		
		
		
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
