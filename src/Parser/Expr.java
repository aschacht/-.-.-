package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;

public abstract class Expr extends Declaration {
public static class Assignment extends Expr {
	 public Assignment(Token name , Expr value) {
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitAssignmentExpr(this);
	}

	public  Token name;
	public  Expr value;
	}
public static class Contains extends Expr {
	 public Contains(Expr container , boolean open , Expr contents) {
	this.container = container;
	this.open = open;
	this.contents = contents;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitContainsExpr(this);
	}

	public  Expr container;
	public  boolean open;
	public  Expr contents;
	}
public static class Binary extends Expr {
	 public Binary(Expr left , Token operator , Expr right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBinaryExpr(this);
	}

	public  Expr left;
	public  Token operator;
	public  Expr right;
	}
public static class Mono extends Expr {
	 public Mono(Expr value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitMonoExpr(this);
	}

	public  Expr value;
	public  Token operator;
	}
public static class Log extends Expr {
	 public Log(Token operator , Expr valueBase , Expr value) {
	this.operator = operator;
	this.valueBase = valueBase;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLogExpr(this);
	}

	public  Token operator;
	public  Expr valueBase;
	public  Expr value;
	}
public static class Factorial extends Expr {
	 public Factorial(Expr value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFactorialExpr(this);
	}

	public  Expr value;
	public  Token operator;
	}
public static class Unary extends Expr {
	 public Unary(Token operator , Expr right) {
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitUnaryExpr(this);
	}

	public  Token operator;
	public  Expr right;
	}
public static class Call extends Expr {
	 public Call(Expr callee , Token calleeToken , List<Expr> arguments) {
	this.callee = callee;
	this.calleeToken = calleeToken;
	this.arguments = arguments;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCallExpr(this);
	}

	public  Expr callee;
	public  Token calleeToken;
	public  List<Expr> arguments;
	}
public static class Get extends Expr {
	 public Get(Expr object , Token name) {
	this.object = object;
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitGetExpr(this);
	}

	public  Expr object;
	public  Token name;
	}
public static class Set extends Expr {
	 public Set(Expr object, Token name, Expr value) {
	this.object = object;
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSetExpr(this);
	}

	public  Expr object;
	public  Token name;
	public  Expr value;
	}
public static class Knot extends Expr {
	 public Knot(Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi) {
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
	public  List<Stmt> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}
public static class Cup extends Expr {
	 public Cup(Token identifier , List<Declaration> expression , String lexeme, Token reifitnedi) {
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
	public  List<Declaration> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}
public static class Template extends Expr {
	 public Template(Expr container) {
	this.container = container;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTemplateExpr(this);
	}

	public  Expr container;
	}
public static class Link extends Expr {
	 public Link(Expr container) {
	this.container = container;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLinkExpr(this);
	}

	public  Expr container;
	}
public static class Pocket extends Expr {
	 public Pocket(Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi) {
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
	public  List<Stmt> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}
public static class Box extends Expr {
	 public Box(Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi) {
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
	public  List<Stmt> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}
public static class Swap extends Expr {
	 public Swap(Expr swap1 , Expr Swap2) {
	this.swap1 = swap1;
	this.Swap2 = Swap2;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSwapExpr(this);
	}

	public  Expr swap1;
	public  Expr Swap2;
	}
public static class Variable extends Expr {
	 public Variable(Token name) {
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitVariableExpr(this);
	}

	public  Token name;
	}
public static class LiteralChar extends Expr {
	 public LiteralChar(char value) {
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLiteralCharExpr(this);
	}

	public  char value;
	}
public static class Literal extends Expr {
	 public Literal(Object value) {
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLiteralExpr(this);
	}

	public  Object value;
	}
public static class PocketOpen extends Expr {
	 public PocketOpen(Token ctrl) {
	this.ctrl = ctrl;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPocketOpenExpr(this);
	}

	public  Token ctrl;
	}
public static class PocketClosed extends Expr {
	 public PocketClosed(Token ctrl) {
	this.ctrl = ctrl;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPocketClosedExpr(this);
	}

	public  Token ctrl;
	}
public static class CupOpen extends Expr {
	 public CupOpen(Token ctrl) {
	this.ctrl = ctrl;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCupOpenExpr(this);
	}

	public  Token ctrl;
	}
public static class CupClosed extends Expr {
	 public CupClosed(Token ctrl) {
	this.ctrl = ctrl;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCupClosedExpr(this);
	}

	public  Token ctrl;
	}
public static class BoxOpen extends Expr {
	 public BoxOpen(Token ctrl) {
	this.ctrl = ctrl;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBoxOpenExpr(this);
	}

	public  Token ctrl;
	}
public static class BoxClosed extends Expr {
	 public BoxClosed(Token ctrl) {
	this.ctrl = ctrl;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBoxClosedExpr(this);
	}

	public  Token ctrl;
	}
public static class Tonk extends Expr {
	 public Tonk(Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi) {
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
	public  List<Stmt> expression;
	public  String lexeme;
	public  Token reifitnedi;
	}
public static class Tes extends Expr {
	 public Tes(Expr object, Token name, Expr value) {
	this.object = object;
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTesExpr(this);
	}

	public  Expr object;
	public  Token name;
	public  Expr value;
	}
public static class Teg extends Expr {
	 public Teg(Expr object , Token name) {
	this.object = object;
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTegExpr(this);
	}

	public  Expr object;
	public  Token name;
	}
public static class Llac extends Expr {
	 public Llac(Expr callee , Token calleeToken , List<Expr> arguments) {
	this.callee = callee;
	this.calleeToken = calleeToken;
	this.arguments = arguments;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLlacExpr(this);
	}

	public  Expr callee;
	public  Token calleeToken;
	public  List<Expr> arguments;
	}
public static class Gol extends Expr {
	 public Gol(Token operator , Expr valueBase , Expr value) {
	this.operator = operator;
	this.valueBase = valueBase;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitGolExpr(this);
	}

	public  Token operator;
	public  Expr valueBase;
	public  Expr value;
	}
public static class Lairotcaf extends Expr {
	 public Lairotcaf(Expr value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLairotcafExpr(this);
	}

	public  Expr value;
	public  Token operator;
	}
public static class Onom extends Expr {
	 public Onom(Expr value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitOnomExpr(this);
	}

	public  Expr value;
	public  Token operator;
	}
public static class Yranib extends Expr {
	 public Yranib(Expr left , Token operator , Expr right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitYranibExpr(this);
	}

	public  Expr left;
	public  Token operator;
	public  Expr right;
	}
public static class Yranu extends Expr {
	 public Yranu(Token operator , Expr right) {
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitYranuExpr(this);
	}

	public  Token operator;
	public  Expr right;
	}
public static class Sniatnoc extends Expr {
	 public Sniatnoc(Expr container , boolean open , Expr contents) {
	this.container = container;
	this.open = open;
	this.contents = contents;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSniatnocExpr(this);
	}

	public  Expr container;
	public  boolean open;
	public  Expr contents;
	}
public static class Tnemngissa extends Expr {
	 public Tnemngissa(Token name , Expr value) {
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTnemngissaExpr(this);
	}

	public  Token name;
	public  Expr value;
	}

}
