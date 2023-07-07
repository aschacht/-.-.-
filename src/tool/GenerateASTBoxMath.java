package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


	public class GenerateASTBoxMath {

		public static void main(String[] args) throws IOException {
			if (args.length != 1) {
				System.err.println("Usage: generate_ast<output directory>");
				System.exit(64);

			}
			String outputDir = args[0];
			defineAST(outputDir, "Term", Arrays.asList(
					
					"Integral		:Term from , Term to , MathToken toIntegrateOver , Term function",
					"Assignment		:Term name , Term value",
					"Binary		:Term left , MathToken operator , Term right",//logical - yroot
					"Mono		:Term value , MathToken operator",//sin -tanh
					"Ln         :MathToken operator , Term value ",
					"Log		:MathToken operator , Term valueBase , Term value ",
					"Factorial	:Term value , MathToken operator",
					"Unary		: MathToken operator , Term right",
					"Function	: MathToken name , Term functionBody",
					"Derivitive : Term function",
					"ToDerive	: Term left , MathToken derive",
					"Variable : MathToken name",
					"Literal	: Object literal	",
					"E: ",
					"PI: ",
					"NotDefined : MathToken name"
					
					
					
					
					
					));

			

			

		}
		

		
		


		
		
		
		
		
		

		private static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
			String path = outputDir + baseName + ".java";
			PrintWriter writer = new PrintWriter(path, "UTF-8");

			writer.println("package Box.math.Syntax;");
			writer.println();
			writer.println("import java.util.List;");
			writer.println("import java.util.ArrayList;");
			writer.println("import Box.math.Token.MathToken;");
			
			writer.println();
			writer.println("public abstract class " + baseName + " {");

			defineVisitor(writer,baseName,types);

			for (String type : types) {
				String className = type.split(":")[0].trim();
				String fields = type.split(":")[1].trim();
				defineType(writer, baseName, className, fields);
			}
			
			writer.println();
			writer.println(" public abstract <R> R accept(Visitor<R> visitor,boolean derive,boolean antiDerive);");
		
			writer.println("}");
			writer.close();
		}


		private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
		
			writer.println("	public interface Visitor<R> {");
			
			for (String type : types) {
				String typeName = type.split(":")[0].trim();
				writer.println("	R visit"+typeName+baseName+"("+typeName+" "+baseName.toLowerCase()+", boolean derive,boolean antiDerive));");
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
			writer.println("	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {");
			writer.println("	 	return visitor.visit" + className + baseName + "(this,derive,antiDerive);");
			writer.println("	}");

			writer.println();
			for (String field : individualFields) {
				String name;
				String typeName;
				String[] split = field.split(" ");
				if(split.length==3) {
					name = field.split(" ")[2];
					typeName = field.split(" ")[0];
					writer.println("	public final ArrayList<" + typeName+"> "+ name+ "=new ArrayList<"+ typeName+">();");
				}else if (split.length==2){
					name = field.split(" ")[1];
					typeName = field.split(" ")[0];
					writer.println("	public final " + typeName+" "+ name+ ";");
				}else if(split.length == 0){
				
				}
				
			}

			writer.println("	}");

		}

	}

