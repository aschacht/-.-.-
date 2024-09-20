package tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import Parser.Declaration;
import Parser.Expr;
import Parser.Stmt;
import Parser.Declaration.StmtDecl;
import Parser.Expr.Box;
import Parser.Expr.BoxClosed;
import Parser.Expr.BoxOpen;
import Parser.Expr.Cup;
import Parser.Expr.CupClosed;
import Parser.Expr.CupOpen;
import Parser.Expr.Knot;
import Parser.Expr.Literal;
import Parser.Expr.Pocket;
import Parser.Expr.PocketClosed;
import Parser.Expr.PocketOpen;
import Parser.Expr.Tonk;
import Parser.Expr.Variable;
import Parser.Stmt.Consume;
import Parser.Stmt.Daer;
import Parser.Stmt.Emaner;
import Parser.Stmt.Evas;
import Parser.Stmt.Evom;
import Parser.Stmt.Expel;
import Parser.Stmt.Expression;
import Parser.Stmt.Fi;
import Parser.Stmt.If;
import Parser.Stmt.Ifi;
import Parser.Stmt.Move;
import Parser.Stmt.Moveevom;
import Parser.Stmt.Nruter;
import Parser.Stmt.Print;
import Parser.Stmt.Rav;
import Parser.Stmt.Read;
import Parser.Stmt.Readdaer;
import Parser.Stmt.Rename;
import Parser.Stmt.Renameemaner;
import Parser.Stmt.Return;
import Parser.Stmt.Save;
import Parser.Stmt.Saveevas;
import Parser.Stmt.Stmtnoisserpxe;
import Parser.Stmt.StmttmtS;
import Parser.Stmt.TemplatVar;
import Parser.Stmt.Tnirp;
import Parser.Stmt.Var;

public class GenerateAST {

	public static void main(String[] args) throws IOException {

		String outputDir = "/home/wes/Wisper Tech 1.0/THEORY/GAMES/PBC/src/Parser";

		List<String> exprDefinition = Arrays.asList("Assignment		: Token name , Expr value",
				"Contains		: Expr container , boolean open , Expr contents",
				"Additive		: Expr callee , Token operator , Expr toadd", // add,push
				"ParamContOp	: Expr callee , Token operator , Expr.Literal index", // Remove getAt
				"NonParamContOp	: Expr callee , Token operator", // clear,size ,empty,pop
				"Setat			: Expr callee , Expr.Literal index , Expr toset ",
				"Sub			: Expr callee , Expr.Literal start , Expr.Literal end ",
				"Binary			: Expr left , Token operator , Expr right", // logical - yroot
				"Mono			: Expr value , Token operator", // sin -tanh
				"Log			: Token operator , Expr valueBase , Expr value",
				"Factorial		: Expr value , Token operator", "Unary			: Token operator , Expr right ",
				"Call 			: Expr callee , Token calleeToken , List<Expr> arguments",
				"Get 			: Expr object , Token name", "Set 			: Expr object, Token name, Expr value",
				"Knot 			: Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi ",

				"Cup 			: Token identifier , List<Declaration> expression , String lexeme, Token reifitnedi",
				"Template 		: Expr container", "Link			: Expr container",
				"Pocket 		: Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi ",
				"Box 			: Token identifier , List<Expr> expression , String lexeme, Token reifitnedi ",

				"Monoonom		:Expr value , Token operatorForward , Token operatorBackward",
				"Addittidda		: Expr calleeForward , Token operatorForward , Expr toadd , Token operatorBackward , Expr calleeBackward",
				"ParCoOppOoCraP	: Expr calleeForward , Token operatorForward , Expr.Literal index , Expr calleeBackward , Token operatorBackward", // Remove
																																					// getAt
				"NoPaCoOOoCaPoN	: Expr calleeForward , Token operatorForward , Expr calleeBackward , Token operatorBackward  ", // clear,size
																																// ,empty,pop
				"Setattates		: Expr calleeForward , Expr.Literal index , Expr toset , Expr calleeBackward ",
				"Subbus			: Expr calleeForward , Expr.Literal start , Expr.Literal end , Expr calleeBackward  ",
				"Binaryyranib	: Expr left , Token operatorForward , Token operatorBackward , Expr right",
				"Loggol			: Token operatorForward , Expr valueBase , Expr value , Token operatorBackward",
				"Callllac 		: Expr calleeForward , Token calleeTokenForward , Expr calleeBackward , Token calleeTokenBackward , List<Expr> arguments ",

				"Expressiontmts	: Token expressionToken , Expr expression , Token tnemetatsToken",
				"Assignmenttnemgissa		: Token nameForward , Expr value , Token nameBackward ",

				"Swap			: Expr swap1 , Expr Swap2", "Variable 		: Token name", "LiteralChar	: char value",
				"Literal		: Object value", "PocketOpen		: Token ctrl", "PocketClosed	: Token ctrl",
				"CupOpen		: Token ctrl", "CupClosed		: Token ctrl", "BoxOpen		: Token ctrl",
				"BoxClosed		: Token ctrl",

				"Tonk 			: Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi ",
				"Tes 			: Expr object, Token name, Expr value", "Teg 			: Expr object , Token name",
				"Llac 			: Expr callee , Token calleeToken , List<Expr> arguments",
				"Gol			: Token operator , Expr valueBase , Expr value",
				"Lairotcaf		: Expr value , Token operator", "Onom			: Expr value , Token operator", // sin
																												// -tanh
				"Yranib			: Expr left , Token operator , Expr right", // logical - yroot
				"Yranu			: Token operator , Expr right ",
				"Bus			: Expr callee , Expr.Literal start , Expr.Literal end ",
				"Tates			: Expr callee , Expr.Literal index , Expr toset ",
				"PoTnocMarapNon	: Expr callee , Token operator", // clear,size ,empty,pop
				"PoTnocMarap	: Expr callee , Token operator , Expr.Literal index", // Remove getAt
				"Evitidda		: Expr callee , Token operator , Expr toadd", // add,push
				"Sniatnoc		: Expr container , boolean open , Expr contents",
				"Tnemngissa		: Token name , Expr value");

		List<String> stmtDefintion = Arrays.asList("Expression : Expr expression , Expr noisserpxe ",

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
				"Fi				: Expr ifPocket , Expr ifCup , Stmt elseIfStmt , Expr elseCup");
		List<String> funDefintion = Arrays.asList(
				"Function 		: Token forwardIdentifier , ArrayList<Token> forwardPrametersType , ArrayList<Token> forwardPrametersNames , Expr sharedCup , ArrayList<Token> backwardPrametersType , ArrayList<Token> backwardPrametersNames , Token backwardIdentifier",
				"FunctionLink 	: Token forwardIdentifier , ArrayList<Token> forwardPrametersType , ArrayList<Token> forwardPrametersNames , ArrayList<Token> backwardPrametersType , ArrayList<Token> backwardPrametersNames , Token backwardIdentifier ");
		List<String> seclarationDefintion = Arrays.asList("FunDecl : Fun function", "StmtDecl : Stmt statement");
		defineCombinedAST(outputDir + "/", "Declaration", Arrays.asList("Declaration", "Fun", "Stmt", "Expr"),
				Arrays.asList(seclarationDefintion, funDefintion, stmtDefintion, exprDefinition),
				Arrays.asList(seclarationDefintion), Arrays.asList("Fun", "Stmt", "Expr"),
				Arrays.asList(funDefintion, stmtDefintion, exprDefinition));

	}

	private static void defineCombinedAST(String outputDir, String baseName, List<String> visitorNames,
			List<List<String>> subDefinitions0, List<List<String>> baseDefinitions, List<String> subBaseName,
			List<List<String>> subDefinitions) throws IOException {

		defineAST0(outputDir, baseName, subDefinitions0, visitorNames, baseDefinitions, subBaseName);

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
		writer.println("import com.fasterxml.jackson.annotation.*;");

		for (String name : subBaseName) {
			writer.println("import Parser." + name + ".*;");

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

	private static void defineVisitor0(PrintWriter writer, List<String> subBaseNames,
			List<List<String>> subDefinitions0) {

		writer.println("	public interface Visitor<R> {");
		for (int i = 0; i < subBaseNames.size(); i++) {

			List<String> list = subDefinitions0.get(i);
			for (String string : list) {

				String typeName = string.split(":")[0].trim();
				writer.println("	R visit" + typeName + subBaseNames.get(i) + "(" + typeName + " "
						+ subBaseNames.get(i).toLowerCase() + ");");
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
		writer.println("import com.fasterxml.jackson.annotation.*;");
		writer.println();
		writer.println("public abstract class " + sBaseName + " extends " + baseName + " {");

		writer.println("public abstract void reverse();");
		if (sBaseName.equals("Expr"))
			writer.println("@Override\n" + "	public boolean equals(Object obj) {\n"
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
					+ "		}\n" + "		return super.equals(obj);\n" + "	}\n" + "\n" + "	@Override\n"
					+ "	public int hashCode() {\n" + "		if (this instanceof Variable) {\n"
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
					+ "		}\n" + "		return super.hashCode();\n" + "	}\n" + "");

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

		writer.println("	public  " + className + "(" + className + " other) {");
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

		writeReverse(writer, className, individualFields);

		writeEquals(writer, className, individualFields);
		
		writeToString(writer,className);

		writer.println();
		for (String field : individualFields) {
			String name;
			String typeName;
			String[] split = field.split(" ");
			if (split.length == 3) {
				name = field.split(" ")[2];
				typeName = field.split(" ")[0];
				writer.println("	@JsonInclude(JsonInclude.Include.NON_NULL)");
				writer.println(
						"	public  ArrayList<" + typeName + "> " + name + "=new ArrayList<" + typeName + ">();");
			} else {
				name = field.split(" ")[1];
				typeName = field.split(" ")[0];
				writer.println("	@JsonInclude(JsonInclude.Include.NON_NULL)");
				writer.println("	public  " + typeName + " " + name + ";");
			}

		}

		writer.println("	}");

	}

	private static void writeToString(PrintWriter writer, String className) {

		if (className.equals( "Expression")){
			
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.expression != null)				str += this.expression.toString() + \" \";			if (this.noisserpxe != null)str += this.noisserpxe.toString() + \" \";			return str;		}");


		}else if (className.equals("If")){

			writer.println("@Override			public String toString() {			String str = \"\";			if (this.ifPocket != null)				str += this.ifPocket.toString() + \" \";			if (this.ifCup != null)				str += this.ifCup.toString() + \" \";			if (this.elseIfStmt != null)				str += this.elseIfStmt.toString() + \" \";			if (this.elseCup != null)				str += this.elseCup.toString() + \" \";			return str;		}");

		}else if (className.equals("Print")){
			
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.expression != null)				str += this.expression.toString() + \" \";			return str;		}");


		}else if (className.equals("Save")){


			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";if (this.filePathFileName != null)				str += this.filePathFileName.toString() + \" \";			if (this.objecttosave != null)				str += this.objecttosave.toString() + \" \";			return str;		}");

		}else if (className.equals("Read")){
			

			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.filePath != null)				str += this.filePath.toString() + \" \";			if (this.objectToReadInto != null)				str += this.objectToReadInto.toString() + \" \";			return str;		}");

		}else if (className.equals("Rename")){
		
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.filePathAndName != null)				str += this.filePathAndName.toString() + \" \";			if (this.filenewname != null)				str += this.filenewname.toString() + \" \";			return str;		}");

		}else if (className.equals("Move")){
		
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.OringialfilePathAndFile != null)				str += this.OringialfilePathAndFile.toString() + \" \";			if (this.newfilePath != null)				str += this.newfilePath.toString() + \" \";			return str;		}");

		}else if (className.equals("Return")){
		
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.expression != null)				str += this.expression.toString() + \" \";			return str;		}");

			
			
		}else if (className.equals("Var")){

			
			
			
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.name != null)				str += this.name.lexeme + \" \";			if (this.type != null)				str += this.type.lexeme + \" \";			str += this.num + \" \";			if (this.initilizer != null)				str += this.initilizer.toString() + \" \";			return str;		}");

		}else if (className.equals("TemplatVar")){

			writer.println("@Override			public String toString() {			String str = \"\";			if (this.name != null)				str += this.name.lexeme + \" \";			if (this.superclass != null)				str += this.superclass.lexeme + \" \";			return str;		}");

		}else if (className.equals("Expel")){

			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.toExpell != null)				str += this.toExpell.toString() + \" \";			if (this.filePath != null)				str += this.filePath.toString() + \" \";			return str;		}");

		}else if (className.equals("Consume")){
			
			
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.boxToFill != null)				str += this.boxToFill.toString() + \" \";			if (this.filePath != null)				str += this.filePath.toString() + \" \";			return str;		}");

		
		}else if (className.equals("Ifi")){
			

			writer.println("@Override			public String toString() {			String str = \"\";			if (this.ifPocket != null)				str += this.ifPocket.toString() + \" \";			if (this.elseIf != null)				str += this.elseIf.toString() + \" \";			return str;		}");

		}else if (className.equals("StmttmtS")){
		
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + \" \";			if (this.expression != null)				str += this.expression.toString() + \" \";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + \" \";			return str;		}");

		}else if (className.equals("Saveevas")){
			
			

			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + \" \";			if (this.filePathFileName != null)				str += this.filePathFileName.toString() + \" \";			if (this.objecttosave != null)				str += this.objecttosave.toString() + \" \";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + \" \";			return str;		}");

		}else if (className.equals("Readdaer")){
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + \" \";			if (this.filePath != null)				str += this.filePath.toString() + \" \";			if (this.objectToReadInto != null)				str += this.objectToReadInto.toString() + \" \";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + \" \";			return str;		}");

		}else if (className.equals("Renameemaner")){
			
			
			
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + \" \";			if (this.filePathAndName != null)				str += this.filePathAndName.toString() + \" \";			if (this.filenewname != null)				str += this.filenewname.toString() + \" \";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + \" \";			return str;}");

		}else if (className.equals("Moveevom")){
		
			
			
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + \" \";			if (this.OringialfilePathAndFile != null)				str += this.OringialfilePathAndFile.toString() + \" \";			if (this.newfilePath != null)				str += this.newfilePath.toString() + \" \";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + \" \";			return str;		}");

		}else if (className.equals("Stmtnoisserpxe")){
		
			
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.statementToken != null)				str += this.statementToken.lexeme + \" \";			if (this.expression != null)str += this.expression.toString() + \" \";			if (this.noisserpxeToken != null)				str += this.noisserpxeToken.lexeme + \" \";			return str;		}");

		}else if (className.equals("Rav")){
			

		
			
			
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.name != null)				str += this.name.lexeme + \" \";			if (this.type != null)				str += this.type.lexeme + \" \";			str += this.num + \" \";			if (this.initilizer != null)				str += this.initilizer.toString() + \" \";			return str;		}");

		}else if (className.equals("Nruter")){
			
			writer.println("@Override			public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.expression != null)				str += this.expression.toString() + \" \";			return str;		}");

		}else if (className.equals("Evom")){
			
			writer.println("@Override		public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.OringialfilePathAndFile != null)				str += this.OringialfilePathAndFile.toString() + \" \";			if (this.newfilePath != null)				str += this.newfilePath.toString() + \" \";			return str;		}");
		}else if (className.equals("Emaner")){
			
			writer.println("@Override		public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.filePathAndName != null)				str += this.filePathAndName.toString() + \" \";			if (this.filenewname != null)				str += this.filenewname.toString() + \" \";			return str;		}");
		}else if (className.equals("Daer")){
			
			writer.println("@Override		public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.filePath != null)				str += this.filePath.toString() + \" \";			if (this.objectToReadInto != null)				str += this.objectToReadInto.toString() + \" \";			return str;		}");
		}else if (className.equals("Evas")){
		
			writer.println("@Override		public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.filePathFileName != null)				str += this.filePathFileName.toString() + \" \";			if (this.objecttosave != null)				str += this.objecttosave.toString() + \" \";			return str;		}");
		
		}else if (className.equals("Tnirp")){
			
			writer.println("@Override		public String toString() {			String str = \"\";			if (this.keyword != null)				str += this.keyword.lexeme + \" \";			if (this.expression != null)				str += this.expression.toString() + \" \";			return str;		}");

			
		}else if (className.equals("Fi")){
			
			writer.println("		@Override\n"
					+ "		public String toString() {\n"
					+ "			String str = \"\";\n"
					+ "			if (this.ifPocket != null)\n"
					+ "				str += this.ifPocket.toString() + \" \";\n"
					+ "			if (this.ifCup != null)\n"
					+ "				str += this.ifCup.toString() + \" \";\n"
					+ "			if (this.elseIfStmt != null)\n"
					+ "				str += this.elseIfStmt.toString() + \" \";\n"
					+ "			if (this.elseCup != null)\n"
					+ "				str += this.elseCup.toString() + \" \";\n"
					+ "			return str;\n"
					+ "		}\n"
					+ "");
			
		}
		else if (className.equals("Assignment")){

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.name != null)				str += this.name.lexeme + \"  \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			return str;		}");


		}else if (className.equals("Contains")){
		
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.container != null)				str += this.container.toString() + \"  \" ;			if (this.contents != null)				str += this.contents.toString() + \"  \" ;			return str;		}");

			
		}else if (className.equals("Additive")){

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.toadd != null)				str += this.toadd.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("ParamContOp")){
		
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.index != null)				str += this.index.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("NonParamContOp")){
		

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			return str;		}");

		
		}else if (className.equals("Setat")){
		

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.index != null)				str += this.index.toString() + \"  \" ;			if (this.toset != null)				str += this.toset.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Sub")){
		
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.start != null)				str += this.start.toString() + \"  \" ;			if (this.end != null)				str += this.end.toString() + \"  \" ;			return str;		}");

		
		}else if (className.equals("Binary")){

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.left != null)				str += this.left.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.right != null)				str += this.right.toString() + \"  \" ;			return str;		}");


		}else if (className.equals("Mono")){
		
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			return str;		}");


		}else if (className.equals("Log")){
		
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.valueBase != null)				str += this.valueBase.toString() + \"  \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			return str;		}");


		}else if (className.equals("Factorial")){

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			return str;		}");


		}else if (className.equals("Unary")){


			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.right != null)				str += this.right.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Call")){


			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.calleeToken != null)				str += this.calleeToken.lexeme + \"  \" ;			if (this.arguments != null)				str += this.arguments.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Get")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.object != null)				str += this.object.toString() + \"  \" ;			if (this.name != null)				str += this.name.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Set")){
		
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.object != null)				str += this.object.toString() + \"  \" ;			if (this.name != null)				str += this.name.lexeme + \"  \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			return str;		}");

		
		}else if (className.equals("Knot")){
		
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.identifier != null)				str += this.identifier.lexeme + \"  \" ;			if (this.expression != null)				str += this.expression.toString() + \"  \" ;			if (this.lexeme != null)				str += this.lexeme.toString() + \"  \" ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Cup")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.identifier != null)				str += this.identifier.lexeme + \"  \" ;			if (this.expression != null)				str += this.expression.toString() + \"  \" ;			if (this.lexeme != null)				str += this.lexeme.toString() + \"  \" ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + \"  \" ;			return str;		}");

			
		}else if (className.equals("Template")){
		
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.container != null)				str += this.container.toString() + \"  \" ;			return str;		}");

			
		}else if (className.equals("Link")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.container != null)				str += this.container.toString() + \"  \" ;			return str;		}");

			
		}else if (className.equals("Pocket")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.identifier != null)				str += this.identifier.lexeme + \"  \" ;			if (this.expression != null)				str += this.expression.toString() + \"  \" ;			if (this.lexeme != null)				str += this.lexeme.toString() + \"  \" ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Box")){
		

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.identifier != null)				str += this.identifier.lexeme + \"  \" ;			if (this.expression != null)				str += this.expression.toString() + \"  \" ;			if (this.lexeme != null)				str += this.lexeme.toString() + \"  \" ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + \"  \" ;			return str;		}");

		
		}else if (className.equals("Monoonom")){
		

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + \"  \" ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Containssniatnoc")){
		

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.contForward != null)				str += this.contForward.toString() + \"  \" ;			if (this.contentsShared != null)				str += this.contentsShared.toString() + \"  \" ;			if (this.contBackward != null)				str += this.contBackward.toString() + \"  \" ;			return str;		}");

		
		}else if (className.equals("Addittidda")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + \"  \" ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + \"  \" ;			if (this.toadd != null)				str += this.toadd.toString() + \"  \" ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + \"  \" ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("ParCoOppOoCraP")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + \"  \" ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + \"  \" ;			if (this.index != null)				str += this.index.toString() + \"  \" ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + \"  \" ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + \"  \" ;			return str;		}");


		}else if (className.equals("NoPaCoOOoCaPoN")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + \"  \" ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + \"  \" ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + \"  \" ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Setattates")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + \"  \" ;			if (this.index != null)				str += this.index.toString() + \"  \" ;			if (this.toset != null)				str += this.toset.toString() + \"  \" ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Subbus")){
		

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + \"  \" ;			if (this.start != null)				str += this.start.toString() + \"  \" ;			if (this.end != null)				str += this.end.toString() + \"  \" ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + \"  \" ;			return str;		}");


		}else if (className.equals("Binaryyranib")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.left != null)				str += this.left.toString() + \"  \" ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + \"  \" ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + \"  \" ;			if (this.right != null)				str += this.right.toString() + \"  \" ;			return str;		}");

			
		}else if (className.equals("Loggol")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + \"  \" ;			if (this.valueBase != null)				str += this.valueBase.toString() + \"  \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + \"  \" ;			return str;		}");

			
		}else if (className.equals("Callllac")){
		

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + \"  \" ;			if (this.calleeTokenForward != null)				str += this.calleeTokenForward.lexeme + \"  \" ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + \"  \" ;			if (this.calleeTokenBackward != null)				str += this.calleeTokenBackward.lexeme + \"  \" ;			if (this.arguments != null)				str += this.arguments.toString() + \"  \" ;			return str;		}");

			
		}else if (className.equals("Expressiontmts")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.expressionToken != null)				str += this.expressionToken.lexeme + \"  \" ;			if (this.expression != null)				str += this.expression.toString() + \"  \" ;			if (this.tnemetatsToken != null)				str += this.tnemetatsToken.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Assignmenttnemgissa")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.nameForward != null)				str += this.nameForward.lexeme + \"  \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			if (this.nameBackward != null)				str += this.nameBackward.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Swap")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.swap1 != null)				str += this.swap1.toString() + \"  \" ;			if (this.Swap2 != null)				str += this.Swap2.toString() + \"  \" ;			return str;		}");

		
		}else if (className.equals("Variable")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.name != null)				str += this.name.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("LiteralChar")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;		str += this.value + \"  \" ;			return str;		}");

		}else if (className.equals("Literal")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("LiteralBool")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (((Boolean) this.value) == true)				str += \" true \" ;			else				str += \" false \" ;			return str;		}");

		}else if (className.equals("LiteralLoob")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (((Boolean) this.value) == true)				str += \" eslaf \" ;			else				str += \" eurt \" ;			return str;		}");

		}else if (className.equals("PocketOpen")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.ctrl != null)				str += this.ctrl.lexeme + \"  \" ;			return str;		}");
			
		}else if (className.equals("PocketClosed")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.ctrl != null)				str += this.ctrl.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("CupOpen")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.ctrl != null)				str += this.ctrl.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("CupClosed")){
		
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.ctrl != null)				str += this.ctrl.lexeme + \"  \" ;			return str;		}");

			
		}else if (className.equals("BoxOpen")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.ctrl != null)				str += this.ctrl.lexeme + \"  \" ;			return str;		}");
			
		}else if (className.equals("BoxClosed")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.ctrl != null)				str += this.ctrl.lexeme + \"  \" ;			return str;		}");
			
		}else if (className.equals("Tonk")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.identifier != null)				str += this.identifier.lexeme + \"  \" ;			if (this.expression != null)				str += this.expression.toString() + \"  \" ;			if (this.lexeme != null)				str += this.lexeme.toString() + \"  \" ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + \"  \" ;			return str;		}");

			
		}else if (className.equals("Tes")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.object != null)				str += this.object.toString() + \"  \" ;			if (this.name != null)				str += this.name.lexeme + \"  \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Teg")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.object != null)				str += this.object.toString() + \"  \" ;			if (this.name != null)				str += this.name.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Llac")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.calleeToken != null)				str += this.calleeToken.lexeme + \"  \" ;			if (this.arguments != null)				str += this.arguments.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Gol")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.valueBase != null)				str += this.valueBase.toString() + \"  \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Lairotcaf")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Onom")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("Yranib")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.left != null)				str += this.left.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.right != null)				str += this.right.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Yranu")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.right != null)				str += this.right.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Bus")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.start != null)				str += this.start.toString() + \"  \" ;			if (this.end != null)				str += this.end.toString() + \"  \" ;			return str;		}");

			
		}else if (className.equals("Tates")){
			

			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.index != null)				str += this.index.toString() + \"  \" ;			if (this.toset != null)				str += this.toset.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("PoTnocMarapNon")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			return str;		}");

		}else if (className.equals("PoTnocMarap")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.index != null)				str += this.index.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Evitidda")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.callee != null)				str += this.callee.toString() + \"  \" ;			if (this.operator != null)				str += this.operator.lexeme + \"  \" ;			if (this.toadd != null)				str += this.toadd.toString() + \"  \" ;			return str;		}");

		}else if (className.equals("Sniatnoc")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.container != null)				str += this.container.toString() + \"  \" ;			if (this.contents != null)				str += this.contents.toString() + \"  \" ;			return str;}");

		}else if (className.equals("Tnemngissa")){
			
			writer.println("@Override		public String toString() {			String str = \" \" ;			if (this.name != null)				str += this.name.lexeme + \"  \" ;			if (this.value != null)				str += this.value.toString() + \"  \" ;			return str;		}");

			
		}





		
	}

	private static void writeEquals(PrintWriter writer, String className, String[] individualFields) {
		if (className.equals("Cup")) {

			writer.println("	@Override\n" + "		public boolean equals(Object obj) {\n" + "\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				if (stmt instanceof Stmt.Expression) {\n"
					+ "					Expr expr = ((Stmt.Expression) stmt).expression;\n"
					+ "					if (expr instanceof Expr.Cup) {\n"
					+ "						List<Declaration> list = ((Cup) expr).expression;\n"
					+ "						List<Declaration> list2 = this.expression;\n"
					+ "						if (list.size() != list2.size())\n"
					+ "							return false;\n" + "\n"
					+ "						for (int i = 0; i < list.size(); i++) {\n"
					+ "							if (!(list.get(i).equals(list2.get(i))))\n"
					+ "								return false;\n" + "						}\n"
					+ "						return true;\n" + "					}\n" + "\n" + "				}\n"
					+ "				return false;\n" + "			} else if (obj instanceof Stmt.Expression) {\n"
					+ "					Expr expr = ((Stmt.Expression) obj).expression;\n"
					+ "					if (expr instanceof Expr.Cup) {\n"
					+ "						List<Declaration> list = ((Cup) expr).expression;\n"
					+ "						List<Declaration> list2 = this.expression;\n"
					+ "						if (list.size() != list2.size())\n"
					+ "							return false;\n" + "\n"
					+ "						for (int i = 0; i < list.size(); i++) {\n"
					+ "							if (!list.get(i).equals(list2.get(i)))\n"
					+ "								return false;\n" + "						}\n"
					+ "						return true;\n" + "					}\n" + "\n" + "				\n"
					+ "				return false;	\n" + "			\n" + "			\n"
					+ "			} else if(obj instanceof Cup) {\n"
					+ "				List<Declaration> list = ((Cup) obj).expression;\n"
					+ "				List<Declaration> list2 = this.expression;\n"
					+ "				if (list.size() != list2.size())\n" + "					return false;\n" + "\n"
					+ "				for (int i = 0; i < list.size(); i++) {\n"
					+ "					if (!list.get(i).equals(list2.get(i)))\n"
					+ "						return false;\n" + "				}\n" + "				return true;\n"
					+ "				\n" + "				\n" + "			}else\n" + "				return false;\n"
					+ "		}\n" + "");

		} else if (className.equals("Pocket")) {

			writer.println("	\n" + "		@Override\n" + "		public boolean equals(Object obj) {\n" + "\n"
					+ "		 if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				if (stmt instanceof Stmt.Expression) {\n"
					+ "					Expr expr = ((Stmt.Expression) stmt).expression;\n"
					+ "					if (expr instanceof Expr.Pocket) {\n"
					+ "						List<Stmt> list = ((Pocket) expr).expression;\n"
					+ "						List<Stmt> list2 = this.expression;\n"
					+ "						if (list.size() != list2.size())\n"
					+ "							return false;\n" + "\n"
					+ "						for (int i = 0; i < list.size(); i++) {\n"
					+ "							if (!(list.get(i).equals(list2.get(i))))\n"
					+ "								return false;\n" + "						}\n"
					+ "						return true;\n" + "					}\n" + "\n" + "				}\n"
					+ "				return false;\n" + "			}else if (obj instanceof Stmt.Expression) {\n"
					+ "				Expr expr = ((Stmt.Expression) obj).expression;\n"
					+ "				if (expr instanceof Expr.Pocket) {\n"
					+ "					List<Stmt> list = ((Pocket) expr).expression;\n"
					+ "					List<Stmt> list2 = this.expression;\n"
					+ "					if (list.size() != list2.size())\n" + "						return false;\n"
					+ "\n" + "					for (int i = 0; i < list.size(); i++) {\n"
					+ "						if (!(list.get(i).equals(list2.get(i))))\n"
					+ "							return false;\n" + "					}\n"
					+ "					return true;\n" + "				}\n" + "\n" + "			\n"
					+ "			return false;	\n" + "		\n" + "		\n" + "		}else if(obj instanceof Pocket) {\n"
					+ "			List<Stmt> list = ((Pocket) obj).expression;\n"
					+ "			List<Stmt> list2 = this.expression;\n" + "			if (list.size() != list2.size())\n"
					+ "				return false;\n" + "\n" + "			for (int i = 0; i < list.size(); i++) {\n"
					+ "				if (!(list.get(i).equals(list2.get(i))))\n" + "					return false;\n"
					+ "			}\n" + "			return true;\n" + "			\n" + "			\n" + "		} else\n"
					+ "				return false;\n" + "		}\n" + "");
		} else if (className.equals("Literal")) {

			writer.println("			@Override\n" + "		public boolean equals(Object obj) {\n" + "\n"
					+ "			boolean equals = value.equals(((Literal) obj).value);\n"
					+ "			return obj instanceof Literal && equals;\n" + "		}");
		} else if (className.equals("LiteralBool")) {

			writer.println("	@Override\n" + "		public boolean equals(Object obj) {\n"
					+ "			// TODO Auto-generated method stub\n"
					+ "			return obj instanceof LiteralBool && value.equals(((LiteralBool)obj).value);\n"
					+ "		}");
		} else if (className.equals("PocketOpen")) {

			writer.println("	@Override\n" + "		public boolean equals(Object obj) {\n"
					+ "			return obj instanceof PocketOpen;\n" + "		}");
		} else if (className.equals("PocketClosed")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			return obj instanceof PocketClosed;\n"
					+ "		}");
		} else if (className.equals("CupOpen")) {

			writer.println("	@Override\n"
					+ "public boolean equals(Object obj) {\n"
					+ "	return obj instanceof CupOpen;\n"
					+ "}");
		} else if (className.equals("CupClosed")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			return obj instanceof CupClosed;\n"
					+ "		}");
		} else if (className.equals("BoxOpen")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			return obj instanceof BoxOpen;\n"
					+ "		}");
		} else if (className.equals("BoxClosed")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			return obj instanceof BoxClosed;\n"
					+ "		}");
		} else if (className.equals("Expression")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Expression && this.expression.equals(((Expression) stmt).expression);\n"
					+ "			}else if (obj instanceof Stmt.Expression) {\n"
					+ "				return this.expression.equals(((Expression)obj).expression);\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("If")) {

			writer.println("			@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof If) {\n"
					+ "		            If other = (If) stmt;\n"
					+ "\n"
					+ "		            // Check for null values\n"
					+ "		            boolean ifPocketEqual = ifPocket == null ? other.ifPocket == null : ifPocket.equals(other.ifPocket);\n"
					+ "		            boolean ifCupEqual = ifCup == null ? other.ifCup == null : ifCup.equals(other.ifCup);\n"
					+ "		            boolean elseIfStmtEqual = elseIfStmt == null ? other.elseIfStmt == null : elseIfStmt.equals(other.elseIfStmt);\n"
					+ "		            boolean elseCupEqual = elseCup == null ? other.elseCup == null : elseCup.equals(other.elseCup);\n"
					+ "\n"
					+ "		            return ifPocketEqual && ifCupEqual && elseIfStmtEqual && elseCupEqual;\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Print")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Print && this.expression.equals(((Print) stmt).expression);\n"
					+ "			} else\n"
					+ "				return obj instanceof Print && this.expression.equals(((Print) obj).expression);\n"
					+ "		}");
		} else if (className.equals("Save")) {

			writer.println("			@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Save && this.filePathFileName.equals(((Save) stmt).filePathFileName)&&\n"
					+ "						this.objecttosave.equals(((Save) stmt).objecttosave)&&\n"
					+ "						this.keyword.lexeme.equals(((Save) stmt).keyword.lexeme);\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}\n"
					+ "		");
		} else if (className.equals("Read")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Read && this.filePath.equals(((Read) stmt).filePath)&&\n"
					+ "						this.objectToReadInto.equals(((Read) stmt).objectToReadInto)&&\n"
					+ "						this.keyword.lexeme.equals(((Read) stmt).keyword.lexeme);\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Rename")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Rename && this.filenewname.equals(((Rename) stmt).filenewname)&&\n"
					+ "						this.filePathAndName.equals(((Rename) stmt).filePathAndName)&&\n"
					+ "						this.keyword.lexeme.equals(((Rename) stmt).keyword.lexeme);\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Move")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Move && this.newfilePath.equals(((Move) stmt).newfilePath)&&\n"
					+ "						this.OringialfilePathAndFile.equals(((Move) stmt).OringialfilePathAndFile)&&\n"
					+ "						this.keyword.lexeme.equals(((Move) stmt).keyword.lexeme);\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}\n"
					+ "		");
		} else if (className.equals("Return")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Return && this.expression.equals(((Return) stmt).expression);\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Var")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Var && this.initilizer.equals(((Var) stmt).initilizer)&&\n"
					+ "						this.name.lexeme.equals(((Var) stmt).name.lexeme)&&\n"
					+ "						this.type.lexeme.equals(((Var) stmt).type)&&this.num==((Var)stmt).num;\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}\n"
					+ "		");
		} else if (className.equals("TemplatVar")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof TemplatVar && this.name.lexeme.equals(((TemplatVar) stmt).name.lexeme)&&\n"
					+ "						this.superclass.lexeme.equals(((TemplatVar) stmt).superclass.lexeme);\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}\n"
					+ "		");
		} else if (className.equals("Expel")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Expel && this.filePath.equals(((Expel) stmt).filePath)&&\n"
					+ "						this.toExpell.equals(((Expel) stmt).toExpell)&&\n"
					+ "						this.keyword.lexeme.equals(((Expel) stmt).keyword.lexeme);\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Consume")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Consume && this.filePath.equals(((Consume) stmt).filePath)&&\n"
					+ "						this.boxToFill.equals(((Consume) stmt).boxToFill)&&\n"
					+ "						this.keyword.lexeme.equals(((Consume) stmt).keyword.lexeme);\n"
					+ "			} else\n"
					+ "				return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Ifi")) {

			writer.println("	@Override\n"
					+ "	    public boolean equals(Object obj) {\n"
					+ "	        if (obj instanceof StmtDecl) {\n"
					+ "	            Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "	            if (stmt instanceof Ifi) {\n"
					+ "	                Ifi other = (Ifi) stmt;\n"
					+ "	                return this.ifPocket.equals(other.ifPocket) && this.elseIf.equals(other.elseIf);\n"
					+ "	            }\n"
					+ "	        }\n"
					+ "	        return super.equals(obj);\n"
					+ "	    }");
		} else if (className.equals("StmttmtS")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof StmttmtS) {\n"
					+ "		            StmttmtS other = (StmttmtS) stmt;\n"
					+ "		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&\n"
					+ "		                   expression.equals(other.expression) &&\n"
					+ "		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Saveevas")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Saveevas) {\n"
					+ "		            Saveevas other = (Saveevas) stmt;\n"
					+ "		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&\n"
					+ "		                   filePathFileName.equals(other.filePathFileName) &&\n"
					+ "		                   objecttosave.equals(other.objecttosave) &&\n"
					+ "		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Readdaer")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Readdaer) {\n"
					+ "		            Readdaer other = (Readdaer) stmt;\n"
					+ "		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&\n"
					+ "		                   filePath.equals(other.filePath) &&\n"
					+ "		                   objectToReadInto.equals(other.objectToReadInto) &&\n"
					+ "		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Renameemaner")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Renameemaner) {\n"
					+ "		            Renameemaner other = (Renameemaner) stmt;\n"
					+ "		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&\n"
					+ "		                   filePathAndName.equals(other.filePathAndName) &&\n"
					+ "		                   filenewname.equals(other.filenewname) &&\n"
					+ "		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Moveevom")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Moveevom) {\n"
					+ "		            Moveevom other = (Moveevom) stmt;\n"
					+ "		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&\n"
					+ "		                   OringialfilePathAndFile.equals(other.OringialfilePathAndFile) &&\n"
					+ "		                   newfilePath.equals(other.newfilePath) &&\n"
					+ "		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Stmtnoisserpxe")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Stmtnoisserpxe) {\n"
					+ "		            Stmtnoisserpxe other = (Stmtnoisserpxe) stmt;\n"
					+ "		            return statementToken.lexeme.equals(other.statementToken.lexeme) &&\n"
					+ "		                   expression.equals(other.expression) &&\n"
					+ "		                   noisserpxeToken.equals(other.noisserpxeToken);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}\n"
					+ "		");
		} else if (className.equals("Rav")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Rav) {\n"
					+ "		            Rav other = (Rav) stmt;\n"
					+ "		            return name.equals(other.name) &&\n"
					+ "		                   type.equals(other.type) &&\n"
					+ "		                   num == other.num &&\n"
					+ "		                		   initilizer.equals(other.initilizer);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}\n"
					+ "		");
		} else if (className.equals("Nruter")) {

			writer.println("			@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Nruter && this.expression.equals(((Nruter) stmt).expression);\n"
					+ "			} else\n"
					+ "				return super.equals(null);\n"
					+ "		}\n"
					+ "");
		} else if (className.equals("Evom")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Evom) {\n"
					+ "		            Evom other = (Evom) stmt;\n"
					+ "		            return keyword.lexeme.equals(other.keyword.lexeme) &&\n"
					+ "		                   OringialfilePathAndFile.equals(other.OringialfilePathAndFile) &&\n"
					+ "		                   newfilePath.equals(other.newfilePath);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Emaner")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Emaner) {\n"
					+ "		            Emaner other = (Emaner) stmt;\n"
					+ "		            return keyword.lexeme.equals(other.keyword.lexeme) &&\n"
					+ "		                   filePathAndName.equals(other.filePathAndName) &&\n"
					+ "		                   filenewname.equals(other.filenewname);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Daer")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Daer) {\n"
					+ "		            Daer other = (Daer) stmt;\n"
					+ "		            return keyword.lexeme.equals(other.keyword.lexeme) &&\n"
					+ "		                   filePath.equals(other.filePath) &&\n"
					+ "		                   objectToReadInto.equals(other.objectToReadInto);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Evas")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Evas) {\n"
					+ "		            Evas other = (Evas) stmt;\n"
					+ "		            return keyword.lexeme.equals(other.keyword.lexeme) &&\n"
					+ "		                   filePathFileName.equals(other.filePathFileName) &&\n"
					+ "		                   objecttosave.equals(other.objecttosave);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		} else if (className.equals("Tnirp")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "			if (obj instanceof StmtDecl) {\n"
					+ "				Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "				return stmt instanceof Tnirp && this.expression.equals(((Tnirp) stmt).expression);\n"
					+ "			} else\n"
					+ "				return super.equals(null);\n"
					+ "		}");
		} else if (className.equals("Fi")) {

			writer.println("	@Override\n"
					+ "		public boolean equals(Object obj) {\n"
					+ "		    if (obj instanceof StmtDecl) {\n"
					+ "		        Stmt stmt = ((StmtDecl) obj).statement;\n"
					+ "		        if (stmt instanceof Fi) {\n"
					+ "		            Fi other = (Fi) stmt;\n"
					+ "		            return ifPocket.equals(other.ifPocket) &&\n"
					+ "		                   ifCup.equals(other.ifCup) &&\n"
					+ "		                   elseIfStmt.equals(other.elseIfStmt) &&\n"
					+ "		                   elseCup.equals(other.elseCup);\n"
					+ "		        }\n"
					+ "		    }\n"
					+ "		    return super.equals(obj);\n"
					+ "		}");
		}
	}

	private static void writeReverse(PrintWriter writer, String className, String[] individualFields) {
		writer.println("	@Override");
		writer.println("	public void reverse() {");

		if (!className.equals("Binary")) {
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
					if (type.equals("Expr"))
						writer.println("	this." + name + ".reverse();");
					else if (type.equals("Stmt"))
						writer.println("	this." + name + ".reverse();");
				}

			}
		} else if (className.equals("Binary")) {

			writer.println("" + "if(this.operator.type == TokenType.AND || this.operator.type == TokenType.DNA"
					+ "||this.operator.type == TokenType.OR || this.operator.type == TokenType.RO){"
					+ " Expr temp = left;" + "		this.left = this.right;" + "		this.right = temp;"
					+ "this.left.reverse();" + "this.right.reverse();" + "}");

		} else if (className.equals("Binaryyranib")) {

			writer.println(""
					+ "if((this.operatorForward.type == TokenType.AND || this.operatorForward.type == TokenType.DNA"
					+ "||this.operatorForward.type == TokenType.OR || this.operatorForward.type == TokenType.RO) &&"
					+ "(this.operatorBackward.type == TokenType.AND || this.operatorBackward.type == TokenType.DNA\"\n"
					+ "				+ \"||this.operatorBackward.type == TokenType.OR || this.operatorBackward.type == TokenType.RO)){"

					+ "Token tempt =operatorForward;" + "operatorForward = operatorBackward;"
					+ "operatorBackward= tempt;" + "" + " Expr temp = left;" + "		this.left = this.right;"
					+ "		this.right = temp;" + "this.left.reverse();" + "this.right.reverse();");

		} else if (className.equals("Yranib")) {

			writer.println("" + "if(this.operator.type == TokenType.AND || this.operator.type == TokenType.DNA"
					+ "||this.operator.type == TokenType.OR || this.operator.type == TokenType.RO){"
					+ " Expr temp = left;" + "		this.left = this.right;" + "		this.right = temp;"
					+ "this.left.reverse();" + "this.right.reverse();" + "}");

		}
		writer.println("	}");
	}

}
