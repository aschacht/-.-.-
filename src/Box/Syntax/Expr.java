package Box.Syntax;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import Box.Parser.LogicalOrStorage;

public abstract class Expr {
	public interface Visitor<R> {
	R visitAssignmentExpr(Assignment expr);
	R visitContainsExpr(Contains expr);
	R visitBinaryExpr(Binary expr);
	R visitMonoExpr(Mono expr);
	R visitLogicalExpr(Logical expr);
	R visitLogExpr(Log expr);
	R visitFactorialExpr(Factorial expr);
	R visitUnaryExpr(Unary expr);
	R visitCallExpr(Call expr);
	R visitGetExpr(Get expr);
	R visitGetBoxCupPocketExpr(GetBoxCupPocket expr);
	R visitSetExpr(Set expr);
	R visitSetBoxCupPocketExpr(SetBoxCupPocket expr);
	R visitLiteralExpr(Literal expr);
	R visitLiteralCharExpr(LiteralChar expr);
	R visitVariableExpr(Variable expr);
	R visitPocketExpr(Pocket expr);
	R visitCupExpr(Cup expr);
	R visitBoxxExpr(Boxx expr);
	R visitKnotExpr(Knot expr);
	R visitCupOpenRightExpr(CupOpenRight expr);
	R visitCupOpenLeftExpr(CupOpenLeft expr);
	R visitPocketOpenRightExpr(PocketOpenRight expr);
	R visitPocketOpenLeftExpr(PocketOpenLeft expr);
	R visitBoxOpenRightExpr(BoxOpenRight expr);
	R visitBoxOpenLeftExpr(BoxOpenLeft expr);
	R visitLashExpr(Lash expr);
	R visitLidExpr(Lid expr);
	R visitTypeExpr(Type expr);
	R visitTnemngissaExpr(Tnemngissa expr);
	R visitSniatnocExpr(Sniatnoc expr);
	R visitYranibExpr(Yranib expr);
	R visitOnomExpr(Onom expr);
	R visitLacigolExpr(Lacigol expr);
	R visitGolExpr(Gol expr);
	R visitYranuExpr(Yranu expr);
	R visitLlacExpr(Llac expr);
	R visitTegExpr(Teg expr);
	R visitTegBoxCupPocketExpr(TegBoxCupPocket expr);
	R visitTesExpr(Tes expr);
	R visitTesBoxCupPocketExpr(TesBoxCupPocket expr);
	R visitLaretilExpr(Laretil expr);
	R visitLaretilCharExpr(LaretilChar expr);
	R visitLairotcafExpr(Lairotcaf expr);
	R visitElbairavExpr(Elbairav expr);
	R visitEpytExpr(Epyt expr);
	R visitParameterExpr(Parameter expr);
	}
public static class Assignment extends Expr {
	 public Assignment(Token name , Expr value) {
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitAssignmentExpr(this);
	}

	public final Token name;
	public final Expr value;
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

	public final Expr container;
	public final boolean open;
	public final Expr contents;
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

	public final Expr left;
	public final Token operator;
	public final Expr right;
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

	public final Expr value;
	public final Token operator;
	}
public static class Logical extends Expr {
	 public Logical(Expr left, Token operator, Expr right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLogicalExpr(this);
	}

	public final Expr left;
	public final Token operator;
	public final Expr right;
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

	public final Token operator;
	public final Expr valueBase;
	public final Expr value;
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

	public final Expr value;
	public final Token operator;
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

	public final Token operator;
	public final Expr right;
	}
public static class Call extends Expr {
	 public Call(Expr callee , Token paren , List<Expr> arguments) {
	this.callee = callee;
	this.paren = paren;
	this.arguments = arguments;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCallExpr(this);
	}

	public final Expr callee;
	public final Token paren;
	public final List<Expr> arguments;
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

	public final Expr object;
	public final Token name;
	}
public static class GetBoxCupPocket extends Expr {
	 public GetBoxCupPocket(Expr object , Token name) {
	this.object = object;
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitGetBoxCupPocketExpr(this);
	}

	public final Expr object;
	public final Token name;
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

	public final Expr object;
	public final Token name;
	public final Expr value;
	}
public static class SetBoxCupPocket extends Expr {
	 public SetBoxCupPocket(Expr object, Token name, Expr value) {
	this.object = object;
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSetBoxCupPocketExpr(this);
	}

	public final Expr object;
	public final Token name;
	public final Expr value;
	}
public static class Literal extends Expr {
	 public Literal(Object value) {
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLiteralExpr(this);
	}
	
	@Override
	public String toString() {
		
		return value.toString();
	}

	public final Object value;
	}
public static class LiteralChar extends Expr {
	 public LiteralChar(char value) {
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLiteralCharExpr(this);
	}
	@Override
	public String toString() {
		
		return value+"";
	}
	
	public final char value;
	}
public static class Variable extends Expr {
	 public Variable(Token name) {
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitVariableExpr(this);
	}

	public final Token name;
	}
public static class Pocket extends Expr implements Contain {
	 public Pocket(Token identifier , List<Stmt> expression , String lexeme , Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	this.typeToEnforce = typeToEnforce;
	this.prototype = prototype;
	this.amount = amount;
	this.enforce = enforce;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPocketExpr(this);
	}

	public final Token identifier;
	public final List<Stmt> expression;
	public final String lexeme;
	public final Token reifitnedi;
	public final Token typeToEnforce;
	public final Expr prototype;
	public final Integer amount;
	public final boolean enforce;
	}
public static class Cup extends Expr implements Contain  {
	 public Cup(Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	this.typeToEnforce = typeToEnforce;
	this.prototype = prototype;
	this.amount = amount;
	this.enforce = enforce;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCupExpr(this);
	}

	public final Token identifier;
	public final List<Stmt> expression;
	public final String lexeme;
	public final Token reifitnedi;
	public final Token typeToEnforce;
	public final Expr prototype;
	public final Integer amount;
	public final boolean enforce;
	}
public static class Boxx extends Expr implements BaseContain  {
	 public Boxx(Token identifier , List<Expr> primarys , String lexeme, Token reifitnedi , Token typeToEnforce , Expr prototype , Integer amount , boolean enforce) {
	this.identifier = identifier;
	this.primarys = primarys;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	this.typeToEnforce = typeToEnforce;
	this.prototype = prototype;
	this.amount = amount;
	this.enforce = enforce;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBoxxExpr(this);
	}

	public final Token identifier;
	public final List<Expr> primarys;
	public final String lexeme;
	public final Token reifitnedi;
	public final Token typeToEnforce;
	public final Expr prototype;
	public final Integer amount;
	public final boolean enforce;
	}
public static class Knot extends Expr implements Contain {
	 public Knot(Token identifier , List<Stmt> expression , List<Stmt> unGrouped , String lexeme , Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.unGrouped = unGrouped;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitKnotExpr(this);
	}

	public final Token identifier;
	public final List<Stmt> expression;
	public final List<Stmt> unGrouped;
	public final String lexeme;
	public final Token reifitnedi;
	}
public static class CupOpenRight extends Expr {
	 public CupOpenRight(Token Literal) {
	this.Literal = Literal;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCupOpenRightExpr(this);
	}
	@Override
	public String toString() {
		
		return Literal.lexeme;
	}
	public final Token Literal;
	}
public static class CupOpenLeft extends Expr {
	 public CupOpenLeft(Token Literal) {
	this.Literal = Literal;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCupOpenLeftExpr(this);
	}
	@Override
	public String toString() {
		
		return Literal.lexeme;
	}
	public final Token Literal;
	}
public static class PocketOpenRight extends Expr {
	 public PocketOpenRight(Token Literal) {
	this.Literal = Literal;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPocketOpenRightExpr(this);
	}
	
	@Override
	public String toString() {
		
		return Literal.lexeme;
	}

	public final Token Literal;
	}
public static class PocketOpenLeft extends Expr {
	 public PocketOpenLeft(Token Literal) {
	this.Literal = Literal;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPocketOpenLeftExpr(this);
	}
	@Override
	public String toString() {
		
		return Literal.lexeme;
	}
	public final Token Literal;
	}
public static class BoxOpenRight extends Expr {
	 public BoxOpenRight(Token Literal) {
	this.Literal = Literal;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBoxOpenRightExpr(this);
	}
	@Override
	public String toString() {
		
		return Literal.lexeme;
	}
	public final Token Literal;
	}
public static class BoxOpenLeft extends Expr {
	 public BoxOpenLeft(Token Literal) {
	this.Literal = Literal;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBoxOpenLeftExpr(this);
	}
	@Override
	public String toString() {
		
		return Literal.lexeme;
	}
	public final Token Literal;
	}
public static class Lash extends Expr {
	 public Lash(Token Literal) {
	this.Literal = Literal;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLashExpr(this);
	}
	@Override
	public String toString() {
		
		return Literal.lexeme;
	}
	public final Token Literal;
	}
public static class Lid extends Expr {
	 public Lid(Token Literal) {
	this.Literal = Literal;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLidExpr(this);
	}
	@Override
	public String toString() {
		
		return Literal.lexeme;
	}
	public final Token Literal;
	}
public static class Type extends Expr {
	 public Type(Expr expression) {
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTypeExpr(this);
	}

	public final Expr expression;
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

	public final Token name;
	public final Expr value;
	}
public static class Sniatnoc extends Expr {
	 public Sniatnoc(Expr container , boolean nepo , Expr contents) {
	this.container = container;
	this.nepo = nepo;
	this.contents = contents;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSniatnocExpr(this);
	}

	public final Expr container;
	public final boolean nepo;
	public final Expr contents;
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

	public final Expr left;
	public final Token operator;
	public final Expr right;
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

	public final Expr value;
	public final Token operator;
	}
public static class Lacigol extends Expr {
	 public Lacigol(Expr left, Token operator, Expr right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLacigolExpr(this);
	}

	public final Expr left;
	public final Token operator;
	public final Expr right;
	}
public static class Gol extends Expr {
	 public Gol(Token operator , Expr value, Expr valueBase ) {
	this.operator = operator;
	this.valueBase = valueBase;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitGolExpr(this);
	}

	public final Token operator;
	public final Expr valueBase;
	public final Expr value;
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

	public final Token operator;
	public final Expr right;
	}
public static class Llac extends Expr {
	 public Llac(Expr callee , Token paren , List<Expr> arguments) {
	this.callee = callee;
	this.paren = paren;
	this.arguments = arguments;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLlacExpr(this);
	}

	public Expr callee;
	public final Token paren;
	public final List<Expr> arguments;
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

	public final Expr object;
	public final Token name;
	}
public static class TegBoxCupPocket extends Expr {
	 public TegBoxCupPocket(Expr object , Token name) {
	this.object = object;
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTegBoxCupPocketExpr(this);
	}

	public final Expr object;
	public final Token name;
	}
public static class Tes extends Expr {
	 public Tes(Expr object , Token name , Expr value) {
	this.object = object;
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTesExpr(this);
	}

	public final Expr object;
	public final Token name;
	public final Expr value;
	}
public static class TesBoxCupPocket extends Expr {
	 public TesBoxCupPocket(Expr object , Token name , Expr value) {
	this.object = object;
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTesBoxCupPocketExpr(this);
	}

	public final Expr object;
	public final Token name;
	public final Expr value;
	}
public static class Laretil extends Expr {
	 public Laretil(Object value) {
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLaretilExpr(this);
	}
	@Override
	public String toString() {
		
		return value.toString();
	}
	

	public final Object value;
	}
public static class LaretilChar extends Expr {
	 public LaretilChar(char value) {
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLaretilCharExpr(this);
	}

	@Override
	public String toString() {
		
		return value+"";
	}
	
	
	public final char value;
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

	public final Expr value;
	public final Token operator;
	}
public static class Elbairav extends Expr {
	 public Elbairav(Token name) {
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitElbairavExpr(this);
	}

	public final Token name;
	}
public static class Epyt extends Expr {
	 public Epyt(Expr expression) {
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEpytExpr(this);
	}

	public final Expr expression;
	}
public static class Parameter extends Expr {
	 public Parameter(Token parameter) {
	this.parameter = parameter;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitParameterExpr(this);
	}

	public final Token parameter;
	}

 public abstract <R> R accept(Visitor<R> visitor);
}
