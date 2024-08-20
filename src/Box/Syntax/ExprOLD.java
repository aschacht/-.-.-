package Box.Syntax;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import Box.Parser.LogicalOrStorage;

public abstract class ExprOLD extends DeclarationOLD {
public static class Assignment extends ExprOLD {
	 public Assignment(Token name , ExprOLD value) {
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitAssignmentExpr(this);
	}

	public  Token name;
	public  ExprOLD value;
	}
public static class Contains extends ExprOLD {
	 public Contains(ExprOLD container , boolean open , ExprOLD contents) {
	this.container = container;
	this.open = open;
	this.contents = contents;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitContainsExpr(this);
	}

	public  ExprOLD container;
	public  boolean open;
	public  ExprOLD contents;
	}
public static class Binary extends ExprOLD {
	 public Binary(ExprOLD left , Token operator , ExprOLD right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBinaryExpr(this);
	}

	public  ExprOLD left;
	public  Token operator;
	public  ExprOLD right;
	}
public static class Mono extends ExprOLD {
	 public Mono(ExprOLD value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitMonoExpr(this);
	}

	public  ExprOLD value;
	public  Token operator;
	}
public static class Log extends ExprOLD {
	 public Log(Token operator , ExprOLD valueBase , ExprOLD value) {
	this.operator = operator;
	this.valueBase = valueBase;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLogExpr(this);
	}

	public  Token operator;
	public  ExprOLD valueBase;
	public  ExprOLD value;
	}
public static class Factorial extends ExprOLD {
	 public Factorial(ExprOLD value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFactorialExpr(this);
	}

	public  ExprOLD value;
	public  Token operator;
	}
public static class Unary extends ExprOLD {
	 public Unary(Token operator , ExprOLD right) {
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitUnaryExpr(this);
	}

	public  Token operator;
	public  ExprOLD right;
	}
public static class Call extends ExprOLD {
	 public Call(ExprOLD callee , Token calleeToken , List<ExprOLD> arguments) {
	this.callee = callee;
	this.calleeToken = calleeToken;
	this.arguments = arguments;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCallExpr(this);
	}

	public  ExprOLD callee;
	public  Token calleeToken;
	public  List<ExprOLD> arguments;
	}
public static class Get extends ExprOLD {
	 public Get(ExprOLD object , Token name) {
	this.object = object;
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitGetExpr(this);
	}

	public  ExprOLD object;
	public  Token name;
	}
public static class Set extends ExprOLD {
	 public Set(ExprOLD object, Token name, ExprOLD value) {
	this.object = object;
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSetExpr(this);
	}

	public  ExprOLD object;
	public  Token name;
	public  ExprOLD value;
	}
public static class Tnemngissa extends ExprOLD {
	 public Tnemngissa(Token name , ExprOLD value) {
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTnemngissaExpr(this);
	}

	public  Token name;
	public  ExprOLD value;
	}
public static class Sniatnoc extends ExprOLD {
	 public Sniatnoc(ExprOLD container , boolean open , ExprOLD contents) {
	this.container = container;
	this.open = open;
	this.contents = contents;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSniatnocExpr(this);
	}

	public  ExprOLD container;
	public  boolean open;
	public  ExprOLD contents;
	}
public static class Yranib extends ExprOLD {
	 public Yranib(ExprOLD left , Token operator , ExprOLD right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitYranibExpr(this);
	}

	public  ExprOLD left;
	public  Token operator;
	public  ExprOLD right;
	}
public static class Onom extends ExprOLD {
	 public Onom(ExprOLD value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitOnomExpr(this);
	}

	public  ExprOLD value;
	public  Token operator;
	}
public static class Gol extends ExprOLD {
	 public Gol(Token operator , ExprOLD valueBase , ExprOLD value) {
	this.operator = operator;
	this.valueBase = valueBase;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitGolExpr(this);
	}

	public  Token operator;
	public  ExprOLD valueBase;
	public  ExprOLD value;
	}
public static class Lairotcaf extends ExprOLD {
	 public Lairotcaf(ExprOLD value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLairotcafExpr(this);
	}

	public  ExprOLD value;
	public  Token operator;
	}
public static class Yranu extends ExprOLD {
	 public Yranu(Token operator , ExprOLD right) {
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitYranuExpr(this);
	}

	public  Token operator;
	public  ExprOLD right;
	}
public static class Llac extends ExprOLD {
	 public Llac(ExprOLD callee , Token calleeToken , List<ExprOLD> arguments) {
	this.callee = callee;
	this.calleeToken = calleeToken;
	this.arguments = arguments;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLlacExpr(this);
	}

	public  ExprOLD callee;
	public  Token calleeToken;
	public  List<ExprOLD> arguments;
	}
public static class Teg extends ExprOLD {
	 public Teg(ExprOLD object , Token name) {
	this.object = object;
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTegExpr(this);
	}

	public  ExprOLD object;
	public  Token name;
	}
public static class Tes extends ExprOLD {
	 public Tes(ExprOLD object, Token name, ExprOLD value) {
	this.object = object;
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTesExpr(this);
	}

	public  ExprOLD object;
	public  Token name;
	public  ExprOLD value;
	}
public static class Variable extends ExprOLD {
	 public Variable(Token name) {
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitVariableExpr(this);
	}

	public  Token name;
	}
public static class Literal extends ExprOLD {
	 public Literal(Object value) {
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLiteralExpr(this);
	}

	public  Object value;
	}
public static class LiteralChar extends ExprOLD {
	 public LiteralChar(char value) {
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLiteralCharExpr(this);
	}

	public  char value;
	}
public static class Cup extends ExprOLD {
	 public Cup(Token identifier , List<DeclarationOLD> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCupExpr(this);
	}

	public  Token identifier;
	public  List<DeclarationOLD> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}
public static class Pocket extends ExprOLD {
	 public Pocket(Token identifier , List<StmtOLD> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPocketExpr(this);
	}

	public  Token identifier;
	public  List<StmtOLD> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}
public static class Knot extends ExprOLD {
	 public Knot(Token identifier , List<DeclarationOLD> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitKnotExpr(this);
	}

	public  Token identifier;
	public  List<DeclarationOLD> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}
public static class Tonk extends ExprOLD {
	 public Tonk(Token identifier , List<DeclarationOLD> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTonkExpr(this);
	}

	public  Token identifier;
	public  List<DeclarationOLD> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}
public static class Box extends ExprOLD {
	 public Box(Token identifier , List<StmtOLD> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBoxExpr(this);
	}

	public  Token identifier;
	public  List<StmtOLD> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}

}
