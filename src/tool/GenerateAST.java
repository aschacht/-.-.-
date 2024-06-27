package tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;


import Box.Token.Token;

public class GenerateAST {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: generate_ast<output directory>");
			System.exit(64);

		}
		String outputDir = args[0];
		defineAST(outputDir, "Expr", Arrays.asList(
				
				"Assignment		:Token name , Expr value",
				"Contains		:Expr container , boolean open , Expr contents",
				"Binary		:Expr left , Token operator , Expr right",//logical - yroot
				"Mono		:Expr value , Token operator",//sin -tanh
				"Logical : Expr left, Token operator, Expr right",
				"Log		:Token operator , Expr valueBase , Expr value",
				"Factorial	:Expr value , Token operator",
				"Unary		: Token operator , Expr right , boolean preOrPost ",
				"UnknownnwonknU		: Expr callee , Token name",
				"Call 		: Expr callee , Token calleeToken , List<Expr> arguments",
				"Get 		: Expr object , Token name",
				"GetBoxCupPocket : Expr object , Token name",
				"Set : Expr object, Token name, Expr value",
				"Literal	: Object value",
				"LiteralChar	: char value",
				
				"Variable : Token name",
				"Pocket		:Token identifier , List<Stmt> expression , String lexeme , Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"Cup		:Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"Boxx		:Token identifier , List<Expr> primarys , String lexeme, Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"Knot		:Token identifier , List<Stmt> expression , List<Stmt> unGrouped , String lexeme , Token reifitnedi ",
				"Pup		:Token identifier , List<Stmt> expression , String lexeme , Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"Cocket		:Token identifier , List<Stmt> expression , String lexeme , Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"Locket		:Token identifier , List<Stmt> expression , String lexeme , Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"Lup		:Token identifier , List<Stmt> expression , String lexeme , Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"Lil		:Token identifier , List<Stmt> expression , String lexeme , Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"Pid		:Token identifier , List<Stmt> expression , String lexeme , Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"Cid		:Token identifier , List<Stmt> expression , String lexeme , Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce",
				"CupOpenRight : Token Literal",
				"CupOpenLeft : Token Literal",
				"PocketOpenRight : Token Literal",
				"PocketOpenLeft : Token Literal",
				"BoxOpenRight : Token Literal",
				"BoxOpenLeft : Token Literal",
				"Lash		: 	Token Literal",
				"Lid		:Token Literal",
				"Type	: 	Expr expression",
				
				"Tnemngissa		:Token name , Expr value",
				"Sniatnoc		:Expr container , boolean nepo , Expr contents",
				"Yranib		:Expr left , Token operator , Expr right",//logical - yroot
				"Onom		:Expr value , Token operator",//sin -tanh
				"Lacigol 	:Expr left, Token operator, Expr right",
				"Gol		:Token operator  , Expr value , Expr valueBase",
				"Yranu		: Token operator , Expr left",
				"Llac 		: Expr callee , Token paren , List<Expr> arguments",
				"Teg 		: Expr object , Token name",
				"TegBoxCupPocket : Expr object , Token name",
				"Tes		: Expr object , Token name , Expr value",
				"Laretil	: Object value",
				"LaretilChar	: char value",
				"Lairotcaf	:Expr value , Token operator",
				"Elbairav : Token name",
				"Epyt 	:	Expr expression",
				"Tonk		:Token identifier , List<Stmt> expression , List<Stmt> unGrouped , String lexeme , Token reifitnedi ",
				
				"Parameter	: Token parameter",
				"PassThrough: Token token",
				"UnKnown	: Expr expressionForward , Expr expressionBackward"
				
				
				));

		
		defineAST(outputDir, "Stmt", Arrays.asList(
				"Expression : Expr expression",
				"If			:Expr ifPocket , Expr ifCup , Stmt elseIfStmt , Expr elseCup",
				"Print		:Token keyword , Expr expression",
				"Return		:Token keyWord , Expr expression",
				"Save		:Token keyword , Expr filePathFileName , Expr objecttosave",
				"Expel		:Token keyword , Expr toExpell , Expr filePath",
				"Read		:Token keyword , Expr filePath , Expr objectToReadInto",
				"Consume	:Token keyword , Expr boxToFill , Expr filePath",
				"Rename		:Token keyword , Expr filePathAndName , Expr filenewname",
				"Move		:Token keyword , Expr OringialfilePathAndFile , Expr newfilePath",
				"Var 		: Token name , Token type, int num , Stmt initilizer",
				"VarFB 		: Var forward, Var backward",
				"Constructor :Token type, Expr prototype , Integer numberToBuild , boolean enforce",
				"Function	:Expr knotfun0 , Expr identifierfun0 , Expr binFun0 , List<Expr> paramsfun0, List<Expr> paramsfun1 , Expr binFun1  , Expr identifierfun1 , Expr knotfun1",
				
				
				"Noisserpxe :Expr noisserpex",
				"Fi			:Expr fiPocket , Expr fiCup , Stmt fiesleStmt , Expr esleCup",
				"Tnirp		:Token keyword , Expr expression",
				"Nruter		:Token keyword , Expr expression",
				"Evas		:Token keyword , Expr filePathFileName , Expr objecttosave",
				"Daer		:Token keyword , Expr filePath , Expr objectToReadInto",
				"Emaner		:Token keyword , Expr filePathAndName, Expr filenewname",
				"Evom		:Token keyword , Expr OringialfilePathAndFile , Expr newfilePath",
				"Rav : Token name , Expr initializer , Token type , Boolean enforce",
				
				
				"PassThrough : Expr expression",
				"UnDetermined : ArrayList<Expr> expressions"
				
				
				
				));


	}
	

	
	


	
	
	
	
	
	

	private static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
		String path = outputDir + baseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");

		writer.println("package Box.Syntax;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println("import java.util.ArrayList;");
		writer.println("import Box.Token.Token;");
		writer.println("import Box.Parser.LogicalOrStorage;");
		writer.println();
		writer.println("public abstract class " + baseName + " {");

		defineVisitor(writer,baseName,types);

		for (String type : types) {
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim();
			defineType(writer, baseName, className, fields);
		}
		
		writer.println();
		writer.println(" public abstract <R> R accept(Visitor<R> visitor);");
	
		writer.println("}");
		writer.close();
	}


	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
	
		writer.println("	public interface Visitor<R> {");
		
		for (String type : types) {
			String typeName = type.split(":")[0].trim();
			writer.println("	R visit"+typeName+baseName+"("+typeName+" "+baseName.toLowerCase()+");");
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
			if(split.length==3) {
				name = field.split(" ")[2];
				String typeName = field.split(" ")[0];
				
				writer.println("for("+typeName+" temp: "+name+"){");
				writer.println("	this." + name + ".add(temp);");
				writer.println("}");
			}else if(split.length==2){
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
			if(split.length==3) {
				name = field.split(" ")[2];
				typeName = field.split(" ")[0];
				writer.println("	public  ArrayList<" + typeName+"> "+ name+ "=new ArrayList<"+ typeName+">();");
			}else {
				name = field.split(" ")[1];
				typeName = field.split(" ")[0];
				writer.println("	public  " + typeName+" "+ name+ ";");
			}
			
		}

		writer.println("	}");

	}

}
