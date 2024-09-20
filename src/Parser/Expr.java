package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import java.util.Objects;
import Box.Token.TokenType;
import com.fasterxml.jackson.annotation.*;

public abstract class Expr extends Declaration {
public abstract void reverse();
@Override
	public boolean equals(Object obj) {
		if (this instanceof Variable) {
			return obj instanceof Variable && ((Variable) this).name.lexeme.equals(((Variable) obj).name.lexeme)
					&& ((Variable) this).name.type == ((Variable) obj).name.type
					&& ((Variable) this).name.line == ((Variable) obj).name.line
					&& ((Variable) this).name.column == ((Variable) obj).name.column
					&& ((Variable) this).name.start == ((Variable) obj).name.start
					&& ((Variable) this).name.finish == ((Variable) obj).name.finish;
		} else if (this instanceof Pocket) {
			return obj instanceof Pocket && ((Pocket) this).lexeme.equals(((Pocket) obj).lexeme)
					&& ((Pocket) this).identifier.type == ((Pocket) obj).identifier.type
					&& ((Pocket) this).identifier.line == ((Pocket) obj).identifier.line
					&& ((Pocket) this).identifier.column == ((Pocket) obj).identifier.column
					&& ((Pocket) this).identifier.start == ((Pocket) obj).identifier.start
					&& ((Pocket) this).identifier.finish == ((Pocket) obj).identifier.finish;
		}else if (this instanceof Cup) {
			return obj instanceof Cup && ((Cup) this).lexeme.equals(((Cup) obj).lexeme)
					&& ((Cup) this).identifier.type == ((Cup) obj).identifier.type
					&& ((Cup) this).identifier.line == ((Cup) obj).identifier.line
					&& ((Cup) this).identifier.column == ((Cup) obj).identifier.column
					&& ((Cup) this).identifier.start == ((Cup) obj).identifier.start
					&& ((Cup) this).identifier.finish == ((Cup) obj).identifier.finish;
		}else if (this instanceof Box) {
			return obj instanceof Box && ((Box) this).lexeme.equals(((Box) obj).lexeme)
					&& ((Box) this).identifier.type == ((Box) obj).identifier.type
					&& ((Box) this).identifier.line == ((Box) obj).identifier.line
					&& ((Box) this).identifier.column == ((Box) obj).identifier.column
					&& ((Box) this).identifier.start == ((Box) obj).identifier.start
					&& ((Box) this).identifier.finish == ((Box) obj).identifier.finish;
		}else if (this instanceof Knot) {
			return obj instanceof Knot && ((Knot) this).lexeme.equals(((Knot) obj).lexeme)
					&& ((Knot) this).identifier.type == ((Knot) obj).identifier.type
					&& ((Knot) this).identifier.line == ((Knot) obj).identifier.line
					&& ((Knot) this).identifier.column == ((Knot) obj).identifier.column
					&& ((Knot) this).identifier.start == ((Knot) obj).identifier.start
					&& ((Knot) this).identifier.finish == ((Knot) obj).identifier.finish;
		}else if (this instanceof Tonk) {
			return obj instanceof Tonk && ((Tonk) this).lexeme.equals(((Tonk) obj).lexeme)
					&& ((Tonk) this).identifier.type == ((Tonk) obj).identifier.type
					&& ((Tonk) this).identifier.line == ((Tonk) obj).identifier.line
					&& ((Tonk) this).identifier.column == ((Tonk) obj).identifier.column
					&& ((Tonk) this).identifier.start == ((Tonk) obj).identifier.start
					&& ((Tonk) this).identifier.finish == ((Tonk) obj).identifier.finish;
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		if (this instanceof Variable) {
			return Objects.hash(((Variable) this).name.lexeme, ((Variable) this).name.type, ((Variable) this).name.line,
					((Variable) this).name.column, ((Variable) this).name.start, ((Variable) this).name.finish);
		} else if (this instanceof Pocket) {
			return Objects.hash(((Pocket) this).identifier.lexeme, ((Pocket) this).identifier.type, ((Pocket) this).identifier.line,
					((Pocket) this).identifier.column, ((Pocket) this).identifier.start, ((Pocket) this).identifier.finish);
		} else if (this instanceof Cup) {
			return Objects.hash(((Cup) this).identifier.lexeme, ((Cup) this).identifier.type, ((Cup) this).identifier.line,
					((Cup) this).identifier.column, ((Cup) this).identifier.start, ((Cup) this).identifier.finish);
		}else if (this instanceof Box) {
			return Objects.hash(((Box) this).identifier.lexeme, ((Box) this).identifier.type, ((Box) this).identifier.line,
					((Box) this).identifier.column, ((Box) this).identifier.start, ((Box) this).identifier.finish);
		}else if (this instanceof Knot) {
			return Objects.hash(((Knot) this).identifier.lexeme, ((Knot) this).identifier.type, ((Knot) this).identifier.line,
					((Knot) this).identifier.column, ((Knot) this).identifier.start, ((Knot) this).identifier.finish);
		}else if (this instanceof Tonk) {
			return Objects.hash(((Tonk) this).identifier.lexeme, ((Tonk) this).identifier.type, ((Tonk) this).identifier.line,
					((Tonk) this).identifier.column, ((Tonk) this).identifier.start, ((Tonk) this).identifier.finish);
		}
		return super.hashCode();
	}

public static class Assignment extends Expr {
	 public Assignment(Token name , Expr value) {
	this.name = name;
	this.value = value;
	}

	public  Assignment(Assignment other) {
	this.name = other.name;
	this.value = other.value;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitAssignmentExpr(this);
	}

	@Override
	public void reverse() {
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.name != null)				str += this.name.lexeme + "  " ;			if (this.value != null)				str += this.value.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	}
public static class Contains extends Expr {
	 public Contains(Expr container , boolean open , Expr contents) {
	this.container = container;
	this.open = open;
	this.contents = contents;
	}

	public  Contains(Contains other) {
	this.container = other.container;
	this.open = other.open;
	this.contents = other.contents;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitContainsExpr(this);
	}

	@Override
	public void reverse() {
	this.container.reverse();
	this.contents.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.container != null)				str += this.container.toString() + "  " ;			if (this.contents != null)				str += this.contents.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr container;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  boolean open;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr contents;
	}
public static class Additive extends Expr {
	 public Additive(Expr callee , Token operator , Expr toadd) {
	this.callee = callee;
	this.operator = operator;
	this.toadd = toadd;
	}

	public  Additive(Additive other) {
	this.callee = other.callee;
	this.operator = other.operator;
	this.toadd = other.toadd;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitAdditiveExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	this.toadd.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.toadd != null)				str += this.toadd.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr toadd;
	}
public static class ParamContOp extends Expr {
	 public ParamContOp(Expr callee , Token operator , Expr.Literal index) {
	this.callee = callee;
	this.operator = operator;
	this.index = index;
	}

	public  ParamContOp(ParamContOp other) {
	this.callee = other.callee;
	this.operator = other.operator;
	this.index = other.index;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitParamContOpExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.index != null)				str += this.index.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal index;
	}
public static class NonParamContOp extends Expr {
	 public NonParamContOp(Expr callee , Token operator) {
	this.callee = callee;
	this.operator = operator;
	}

	public  NonParamContOp(NonParamContOp other) {
	this.callee = other.callee;
	this.operator = other.operator;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitNonParamContOpExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	}
public static class Setat extends Expr {
	 public Setat(Expr callee , Expr.Literal index , Expr toset) {
	this.callee = callee;
	this.index = index;
	this.toset = toset;
	}

	public  Setat(Setat other) {
	this.callee = other.callee;
	this.index = other.index;
	this.toset = other.toset;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSetatExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	this.toset.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.index != null)				str += this.index.toString() + "  " ;			if (this.toset != null)				str += this.toset.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal index;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr toset;
	}
public static class Sub extends Expr {
	 public Sub(Expr callee , Expr.Literal start , Expr.Literal end) {
	this.callee = callee;
	this.start = start;
	this.end = end;
	}

	public  Sub(Sub other) {
	this.callee = other.callee;
	this.start = other.start;
	this.end = other.end;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSubExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.start != null)				str += this.start.toString() + "  " ;			if (this.end != null)				str += this.end.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal start;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal end;
	}
public static class Binary extends Expr {
	 public Binary(Expr left , Token operator , Expr right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	public  Binary(Binary other) {
	this.left = other.left;
	this.operator = other.operator;
	this.right = other.right;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBinaryExpr(this);
	}

	@Override
	public void reverse() {
if(this.operator.type == TokenType.AND || this.operator.type == TokenType.DNA||this.operator.type == TokenType.OR || this.operator.type == TokenType.RO){ Expr temp = left;		this.left = this.right;		this.right = temp;this.left.reverse();this.right.reverse();}
	}
@Override		public String toString() {			String str = " " ;			if (this.left != null)				str += this.left.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.right != null)				str += this.right.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr left;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr right;
	}
public static class Mono extends Expr {
	 public Mono(Expr value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	public  Mono(Mono other) {
	this.value = other.value;
	this.operator = other.operator;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitMonoExpr(this);
	}

	@Override
	public void reverse() {
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.value != null)				str += this.value.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	}
public static class Log extends Expr {
	 public Log(Token operator , Expr valueBase , Expr value) {
	this.operator = operator;
	this.valueBase = valueBase;
	this.value = value;
	}

	public  Log(Log other) {
	this.operator = other.operator;
	this.valueBase = other.valueBase;
	this.value = other.value;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLogExpr(this);
	}

	@Override
	public void reverse() {
	this.valueBase.reverse();
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.valueBase != null)				str += this.valueBase.toString() + "  " ;			if (this.value != null)				str += this.value.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr valueBase;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	}
public static class Factorial extends Expr {
	 public Factorial(Expr value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	public  Factorial(Factorial other) {
	this.value = other.value;
	this.operator = other.operator;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFactorialExpr(this);
	}

	@Override
	public void reverse() {
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.value != null)				str += this.value.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	}
public static class Unary extends Expr {
	 public Unary(Token operator , Expr right) {
	this.operator = operator;
	this.right = right;
	}

	public  Unary(Unary other) {
	this.operator = other.operator;
	this.right = other.right;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitUnaryExpr(this);
	}

	@Override
	public void reverse() {
	this.right.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.right != null)				str += this.right.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr right;
	}
public static class Call extends Expr {
	 public Call(Expr callee , Token calleeToken , List<Expr> arguments) {
	this.callee = callee;
	this.calleeToken = calleeToken;
	this.arguments = arguments;
	}

	public  Call(Call other) {
	this.callee = other.callee;
	this.calleeToken = other.calleeToken;
	this.arguments = other.arguments;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCallExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.calleeToken != null)				str += this.calleeToken.lexeme + "  " ;			if (this.arguments != null)				str += this.arguments.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token calleeToken;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  List<Expr> arguments;
	}
public static class Get extends Expr {
	 public Get(Expr object , Token name) {
	this.object = object;
	this.name = name;
	}

	public  Get(Get other) {
	this.object = other.object;
	this.name = other.name;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitGetExpr(this);
	}

	@Override
	public void reverse() {
	this.object.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.object != null)				str += this.object.toString() + "  " ;			if (this.name != null)				str += this.name.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr object;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	}
public static class Set extends Expr {
	 public Set(Expr object, Token name, Expr value) {
	this.object = object;
	this.name = name;
	this.value = value;
	}

	public  Set(Set other) {
	this.object = other.object;
	this.name = other.name;
	this.value = other.value;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSetExpr(this);
	}

	@Override
	public void reverse() {
	this.object.reverse();
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.object != null)				str += this.object.toString() + "  " ;			if (this.name != null)				str += this.name.lexeme + "  " ;			if (this.value != null)				str += this.value.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr object;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	}
public static class Knot extends Expr {
	 public Knot(Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	public  Knot(Knot other) {
	this.identifier = other.identifier;
	this.expression = other.expression;
	this.lexeme = other.lexeme;
	this.reifitnedi = other.reifitnedi;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitKnotExpr(this);
	}

	@Override
	public void reverse() {
	}
@Override		public String toString() {			String str = " " ;			if (this.identifier != null)				str += this.identifier.lexeme + "  " ;			if (this.expression != null)				str += this.expression.toString() + "  " ;			if (this.lexeme != null)				str += this.lexeme.toString() + "  " ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token identifier;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  List<Stmt> expression;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  String lexeme;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token reifitnedi;
	}
public static class Cup extends Expr {
	 public Cup(Token identifier , List<Declaration> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	public  Cup(Cup other) {
	this.identifier = other.identifier;
	this.expression = other.expression;
	this.lexeme = other.lexeme;
	this.reifitnedi = other.reifitnedi;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCupExpr(this);
	}

	@Override
	public void reverse() {
	}
	@Override
		public boolean equals(Object obj) {

			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				if (stmt instanceof Stmt.Expression) {
					Expr expr = ((Stmt.Expression) stmt).expression;
					if (expr instanceof Expr.Cup) {
						List<Declaration> list = ((Cup) expr).expression;
						List<Declaration> list2 = this.expression;
						if (list.size() != list2.size())
							return false;

						for (int i = 0; i < list.size(); i++) {
							if (!(list.get(i).equals(list2.get(i))))
								return false;
						}
						return true;
					}

				}
				return false;
			} else if (obj instanceof Stmt.Expression) {
					Expr expr = ((Stmt.Expression) obj).expression;
					if (expr instanceof Expr.Cup) {
						List<Declaration> list = ((Cup) expr).expression;
						List<Declaration> list2 = this.expression;
						if (list.size() != list2.size())
							return false;

						for (int i = 0; i < list.size(); i++) {
							if (!list.get(i).equals(list2.get(i)))
								return false;
						}
						return true;
					}

				
				return false;	
			
			
			} else if(obj instanceof Cup) {
				List<Declaration> list = ((Cup) obj).expression;
				List<Declaration> list2 = this.expression;
				if (list.size() != list2.size())
					return false;

				for (int i = 0; i < list.size(); i++) {
					if (!list.get(i).equals(list2.get(i)))
						return false;
				}
				return true;
				
				
			}else
				return false;
		}

@Override		public String toString() {			String str = " " ;			if (this.identifier != null)				str += this.identifier.lexeme + "  " ;			if (this.expression != null)				str += this.expression.toString() + "  " ;			if (this.lexeme != null)				str += this.lexeme.toString() + "  " ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token identifier;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  List<Declaration> expression;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  String lexeme;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token reifitnedi;
	}
public static class Template extends Expr {
	 public Template(Expr container) {
	this.container = container;
	}

	public  Template(Template other) {
	this.container = other.container;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTemplateExpr(this);
	}

	@Override
	public void reverse() {
	this.container.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.container != null)				str += this.container.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr container;
	}
public static class Link extends Expr {
	 public Link(Expr container) {
	this.container = container;
	}

	public  Link(Link other) {
	this.container = other.container;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLinkExpr(this);
	}

	@Override
	public void reverse() {
	this.container.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.container != null)				str += this.container.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr container;
	}
public static class Pocket extends Expr {
	 public Pocket(Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	public  Pocket(Pocket other) {
	this.identifier = other.identifier;
	this.expression = other.expression;
	this.lexeme = other.lexeme;
	this.reifitnedi = other.reifitnedi;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPocketExpr(this);
	}

	@Override
	public void reverse() {
	}
	
		@Override
		public boolean equals(Object obj) {

		 if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				if (stmt instanceof Stmt.Expression) {
					Expr expr = ((Stmt.Expression) stmt).expression;
					if (expr instanceof Expr.Pocket) {
						List<Stmt> list = ((Pocket) expr).expression;
						List<Stmt> list2 = this.expression;
						if (list.size() != list2.size())
							return false;

						for (int i = 0; i < list.size(); i++) {
							if (!(list.get(i).equals(list2.get(i))))
								return false;
						}
						return true;
					}

				}
				return false;
			}else if (obj instanceof Stmt.Expression) {
				Expr expr = ((Stmt.Expression) obj).expression;
				if (expr instanceof Expr.Pocket) {
					List<Stmt> list = ((Pocket) expr).expression;
					List<Stmt> list2 = this.expression;
					if (list.size() != list2.size())
						return false;

					for (int i = 0; i < list.size(); i++) {
						if (!(list.get(i).equals(list2.get(i))))
							return false;
					}
					return true;
				}

			
			return false;	
		
		
		}else if(obj instanceof Pocket) {
			List<Stmt> list = ((Pocket) obj).expression;
			List<Stmt> list2 = this.expression;
			if (list.size() != list2.size())
				return false;

			for (int i = 0; i < list.size(); i++) {
				if (!(list.get(i).equals(list2.get(i))))
					return false;
			}
			return true;
			
			
		} else
				return false;
		}

@Override		public String toString() {			String str = " " ;			if (this.identifier != null)				str += this.identifier.lexeme + "  " ;			if (this.expression != null)				str += this.expression.toString() + "  " ;			if (this.lexeme != null)				str += this.lexeme.toString() + "  " ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token identifier;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  List<Stmt> expression;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  String lexeme;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token reifitnedi;
	}
public static class Box extends Expr {
	 public Box(Token identifier , List<Expr> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	public  Box(Box other) {
	this.identifier = other.identifier;
	this.expression = other.expression;
	this.lexeme = other.lexeme;
	this.reifitnedi = other.reifitnedi;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBoxExpr(this);
	}

	@Override
	public void reverse() {
	}
@Override		public String toString() {			String str = " " ;			if (this.identifier != null)				str += this.identifier.lexeme + "  " ;			if (this.expression != null)				str += this.expression.toString() + "  " ;			if (this.lexeme != null)				str += this.lexeme.toString() + "  " ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token identifier;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  List<Expr> expression;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  String lexeme;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token reifitnedi;
	}
public static class Monoonom extends Expr {
	 public Monoonom(Expr value , Token operatorForward , Token operatorBackward) {
	this.value = value;
	this.operatorForward = operatorForward;
	this.operatorBackward = operatorBackward;
	}

	public  Monoonom(Monoonom other) {
	this.value = other.value;
	this.operatorForward = other.operatorForward;
	this.operatorBackward = other.operatorBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitMonoonomExpr(this);
	}

	@Override
	public void reverse() {
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.value != null)				str += this.value.toString() + "  " ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + "  " ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorBackward;
	}
public static class Addittidda extends Expr {
	 public Addittidda(Expr calleeForward , Token operatorForward , Expr toadd , Token operatorBackward , Expr calleeBackward) {
	this.calleeForward = calleeForward;
	this.operatorForward = operatorForward;
	this.toadd = toadd;
	this.operatorBackward = operatorBackward;
	this.calleeBackward = calleeBackward;
	}

	public  Addittidda(Addittidda other) {
	this.calleeForward = other.calleeForward;
	this.operatorForward = other.operatorForward;
	this.toadd = other.toadd;
	this.operatorBackward = other.operatorBackward;
	this.calleeBackward = other.calleeBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitAddittiddaExpr(this);
	}

	@Override
	public void reverse() {
	this.calleeForward.reverse();
	this.toadd.reverse();
	this.calleeBackward.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + "  " ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + "  " ;			if (this.toadd != null)				str += this.toadd.toString() + "  " ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + "  " ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr toadd;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorBackward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeBackward;
	}
public static class ParCoOppOoCraP extends Expr {
	 public ParCoOppOoCraP(Expr calleeForward , Token operatorForward , Expr.Literal index , Expr calleeBackward , Token operatorBackward) {
	this.calleeForward = calleeForward;
	this.operatorForward = operatorForward;
	this.index = index;
	this.calleeBackward = calleeBackward;
	this.operatorBackward = operatorBackward;
	}

	public  ParCoOppOoCraP(ParCoOppOoCraP other) {
	this.calleeForward = other.calleeForward;
	this.operatorForward = other.operatorForward;
	this.index = other.index;
	this.calleeBackward = other.calleeBackward;
	this.operatorBackward = other.operatorBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitParCoOppOoCraPExpr(this);
	}

	@Override
	public void reverse() {
	this.calleeForward.reverse();
	this.calleeBackward.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + "  " ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + "  " ;			if (this.index != null)				str += this.index.toString() + "  " ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + "  " ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal index;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeBackward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorBackward;
	}
public static class NoPaCoOOoCaPoN extends Expr {
	 public NoPaCoOOoCaPoN(Expr calleeForward , Token operatorForward , Expr calleeBackward , Token operatorBackward) {
	this.calleeForward = calleeForward;
	this.operatorForward = operatorForward;
	this.calleeBackward = calleeBackward;
	this.operatorBackward = operatorBackward;
	}

	public  NoPaCoOOoCaPoN(NoPaCoOOoCaPoN other) {
	this.calleeForward = other.calleeForward;
	this.operatorForward = other.operatorForward;
	this.calleeBackward = other.calleeBackward;
	this.operatorBackward = other.operatorBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitNoPaCoOOoCaPoNExpr(this);
	}

	@Override
	public void reverse() {
	this.calleeForward.reverse();
	this.calleeBackward.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + "  " ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + "  " ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + "  " ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeBackward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorBackward;
	}
public static class Setattates extends Expr {
	 public Setattates(Expr calleeForward , Expr.Literal index , Expr toset , Expr calleeBackward) {
	this.calleeForward = calleeForward;
	this.index = index;
	this.toset = toset;
	this.calleeBackward = calleeBackward;
	}

	public  Setattates(Setattates other) {
	this.calleeForward = other.calleeForward;
	this.index = other.index;
	this.toset = other.toset;
	this.calleeBackward = other.calleeBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSetattatesExpr(this);
	}

	@Override
	public void reverse() {
	this.calleeForward.reverse();
	this.toset.reverse();
	this.calleeBackward.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + "  " ;			if (this.index != null)				str += this.index.toString() + "  " ;			if (this.toset != null)				str += this.toset.toString() + "  " ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal index;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr toset;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeBackward;
	}
public static class Subbus extends Expr {
	 public Subbus(Expr calleeForward , Expr.Literal start , Expr.Literal end , Expr calleeBackward) {
	this.calleeForward = calleeForward;
	this.start = start;
	this.end = end;
	this.calleeBackward = calleeBackward;
	}

	public  Subbus(Subbus other) {
	this.calleeForward = other.calleeForward;
	this.start = other.start;
	this.end = other.end;
	this.calleeBackward = other.calleeBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSubbusExpr(this);
	}

	@Override
	public void reverse() {
	this.calleeForward.reverse();
	this.calleeBackward.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + "  " ;			if (this.start != null)				str += this.start.toString() + "  " ;			if (this.end != null)				str += this.end.toString() + "  " ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal start;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal end;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeBackward;
	}
public static class Binaryyranib extends Expr {
	 public Binaryyranib(Expr left , Token operatorForward , Token operatorBackward , Expr right) {
	this.left = left;
	this.operatorForward = operatorForward;
	this.operatorBackward = operatorBackward;
	this.right = right;
	}

	public  Binaryyranib(Binaryyranib other) {
	this.left = other.left;
	this.operatorForward = other.operatorForward;
	this.operatorBackward = other.operatorBackward;
	this.right = other.right;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBinaryyranibExpr(this);
	}

	@Override
	public void reverse() {
	this.left.reverse();
	this.right.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.left != null)				str += this.left.toString() + "  " ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + "  " ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + "  " ;			if (this.right != null)				str += this.right.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr left;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorBackward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr right;
	}
public static class Loggol extends Expr {
	 public Loggol(Token operatorForward , Expr valueBase , Expr value , Token operatorBackward) {
	this.operatorForward = operatorForward;
	this.valueBase = valueBase;
	this.value = value;
	this.operatorBackward = operatorBackward;
	}

	public  Loggol(Loggol other) {
	this.operatorForward = other.operatorForward;
	this.valueBase = other.valueBase;
	this.value = other.value;
	this.operatorBackward = other.operatorBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLoggolExpr(this);
	}

	@Override
	public void reverse() {
	this.valueBase.reverse();
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.operatorForward != null)				str += this.operatorForward.lexeme + "  " ;			if (this.valueBase != null)				str += this.valueBase.toString() + "  " ;			if (this.value != null)				str += this.value.toString() + "  " ;			if (this.operatorBackward != null)				str += this.operatorBackward.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr valueBase;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operatorBackward;
	}
public static class Callllac extends Expr {
	 public Callllac(Expr calleeForward , Token calleeTokenForward , Expr calleeBackward , Token calleeTokenBackward , List<Expr> arguments) {
	this.calleeForward = calleeForward;
	this.calleeTokenForward = calleeTokenForward;
	this.calleeBackward = calleeBackward;
	this.calleeTokenBackward = calleeTokenBackward;
	this.arguments = arguments;
	}

	public  Callllac(Callllac other) {
	this.calleeForward = other.calleeForward;
	this.calleeTokenForward = other.calleeTokenForward;
	this.calleeBackward = other.calleeBackward;
	this.calleeTokenBackward = other.calleeTokenBackward;
	this.arguments = other.arguments;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCallllacExpr(this);
	}

	@Override
	public void reverse() {
	this.calleeForward.reverse();
	this.calleeBackward.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.calleeForward != null)				str += this.calleeForward.toString() + "  " ;			if (this.calleeTokenForward != null)				str += this.calleeTokenForward.lexeme + "  " ;			if (this.calleeBackward != null)				str += this.calleeBackward.toString() + "  " ;			if (this.calleeTokenBackward != null)				str += this.calleeTokenBackward.lexeme + "  " ;			if (this.arguments != null)				str += this.arguments.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token calleeTokenForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr calleeBackward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token calleeTokenBackward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  List<Expr> arguments;
	}
public static class Expressiontmts extends Expr {
	 public Expressiontmts(Token expressionToken , Expr expression , Token tnemetatsToken) {
	this.expressionToken = expressionToken;
	this.expression = expression;
	this.tnemetatsToken = tnemetatsToken;
	}

	public  Expressiontmts(Expressiontmts other) {
	this.expressionToken = other.expressionToken;
	this.expression = other.expression;
	this.tnemetatsToken = other.tnemetatsToken;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitExpressiontmtsExpr(this);
	}

	@Override
	public void reverse() {
	this.expression.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.expressionToken != null)				str += this.expressionToken.lexeme + "  " ;			if (this.expression != null)				str += this.expression.toString() + "  " ;			if (this.tnemetatsToken != null)				str += this.tnemetatsToken.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token expressionToken;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr expression;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token tnemetatsToken;
	}
public static class Assignmenttnemgissa extends Expr {
	 public Assignmenttnemgissa(Token nameForward , Expr value , Token nameBackward) {
	this.nameForward = nameForward;
	this.value = value;
	this.nameBackward = nameBackward;
	}

	public  Assignmenttnemgissa(Assignmenttnemgissa other) {
	this.nameForward = other.nameForward;
	this.value = other.value;
	this.nameBackward = other.nameBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitAssignmenttnemgissaExpr(this);
	}

	@Override
	public void reverse() {
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.nameForward != null)				str += this.nameForward.lexeme + "  " ;			if (this.value != null)				str += this.value.toString() + "  " ;			if (this.nameBackward != null)				str += this.nameBackward.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token nameForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token nameBackward;
	}
public static class Swap extends Expr {
	 public Swap(Expr swap1 , Expr Swap2) {
	this.swap1 = swap1;
	this.Swap2 = Swap2;
	}

	public  Swap(Swap other) {
	this.swap1 = other.swap1;
	this.Swap2 = other.Swap2;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSwapExpr(this);
	}

	@Override
	public void reverse() {
	this.swap1.reverse();
	this.Swap2.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.swap1 != null)				str += this.swap1.toString() + "  " ;			if (this.Swap2 != null)				str += this.Swap2.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr swap1;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr Swap2;
	}
public static class Variable extends Expr {
	 public Variable(Token name) {
	this.name = name;
	}

	public  Variable(Variable other) {
	this.name = other.name;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitVariableExpr(this);
	}

	@Override
	public void reverse() {
	}
@Override		public String toString() {			String str = " " ;			if (this.name != null)				str += this.name.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	}
public static class LiteralChar extends Expr {
	 public LiteralChar(char value) {
	this.value = value;
	}

	public  LiteralChar(LiteralChar other) {
	this.value = other.value;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLiteralCharExpr(this);
	}

	@Override
	public void reverse() {
	}
@Override		public String toString() {			String str = " " ;		str += this.value + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  char value;
	}
public static class Literal extends Expr {
	 public Literal(Object value) {
	this.value = value;
	}

	public  Literal(Literal other) {
	this.value = other.value;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLiteralExpr(this);
	}

	@Override
	public void reverse() {
	}
			@Override
		public boolean equals(Object obj) {

			boolean equals = value.equals(((Literal) obj).value);
			return obj instanceof Literal && equals;
		}
@Override		public String toString() {			String str = " " ;			if (this.value != null)				str += this.value.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Object value;
	}
public static class PocketOpen extends Expr {
	 public PocketOpen(Token ctrl) {
	this.ctrl = ctrl;
	}

	public  PocketOpen(PocketOpen other) {
	this.ctrl = other.ctrl;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPocketOpenExpr(this);
	}

	@Override
	public void reverse() {
	}
	@Override
		public boolean equals(Object obj) {
			return obj instanceof PocketOpen;
		}
@Override		public String toString() {			String str = " " ;			if (this.ctrl != null)				str += this.ctrl.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token ctrl;
	}
public static class PocketClosed extends Expr {
	 public PocketClosed(Token ctrl) {
	this.ctrl = ctrl;
	}

	public  PocketClosed(PocketClosed other) {
	this.ctrl = other.ctrl;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPocketClosedExpr(this);
	}

	@Override
	public void reverse() {
	}
	@Override
		public boolean equals(Object obj) {
			return obj instanceof PocketClosed;
		}
@Override		public String toString() {			String str = " " ;			if (this.ctrl != null)				str += this.ctrl.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token ctrl;
	}
public static class CupOpen extends Expr {
	 public CupOpen(Token ctrl) {
	this.ctrl = ctrl;
	}

	public  CupOpen(CupOpen other) {
	this.ctrl = other.ctrl;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCupOpenExpr(this);
	}

	@Override
	public void reverse() {
	}
	@Override
public boolean equals(Object obj) {
	return obj instanceof CupOpen;
}
@Override		public String toString() {			String str = " " ;			if (this.ctrl != null)				str += this.ctrl.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token ctrl;
	}
public static class CupClosed extends Expr {
	 public CupClosed(Token ctrl) {
	this.ctrl = ctrl;
	}

	public  CupClosed(CupClosed other) {
	this.ctrl = other.ctrl;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitCupClosedExpr(this);
	}

	@Override
	public void reverse() {
	}
	@Override
		public boolean equals(Object obj) {
			return obj instanceof CupClosed;
		}
@Override		public String toString() {			String str = " " ;			if (this.ctrl != null)				str += this.ctrl.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token ctrl;
	}
public static class BoxOpen extends Expr {
	 public BoxOpen(Token ctrl) {
	this.ctrl = ctrl;
	}

	public  BoxOpen(BoxOpen other) {
	this.ctrl = other.ctrl;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBoxOpenExpr(this);
	}

	@Override
	public void reverse() {
	}
	@Override
		public boolean equals(Object obj) {
			return obj instanceof BoxOpen;
		}
@Override		public String toString() {			String str = " " ;			if (this.ctrl != null)				str += this.ctrl.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token ctrl;
	}
public static class BoxClosed extends Expr {
	 public BoxClosed(Token ctrl) {
	this.ctrl = ctrl;
	}

	public  BoxClosed(BoxClosed other) {
	this.ctrl = other.ctrl;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBoxClosedExpr(this);
	}

	@Override
	public void reverse() {
	}
	@Override
		public boolean equals(Object obj) {
			return obj instanceof BoxClosed;
		}
@Override		public String toString() {			String str = " " ;			if (this.ctrl != null)				str += this.ctrl.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token ctrl;
	}
public static class Tonk extends Expr {
	 public Tonk(Token identifier , List<Stmt> expression , String lexeme, Token reifitnedi) {
	this.identifier = identifier;
	this.expression = expression;
	this.lexeme = lexeme;
	this.reifitnedi = reifitnedi;
	}

	public  Tonk(Tonk other) {
	this.identifier = other.identifier;
	this.expression = other.expression;
	this.lexeme = other.lexeme;
	this.reifitnedi = other.reifitnedi;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTonkExpr(this);
	}

	@Override
	public void reverse() {
	}
@Override		public String toString() {			String str = " " ;			if (this.identifier != null)				str += this.identifier.lexeme + "  " ;			if (this.expression != null)				str += this.expression.toString() + "  " ;			if (this.lexeme != null)				str += this.lexeme.toString() + "  " ;			if (this.reifitnedi != null)				str += this.reifitnedi.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token identifier;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  List<Stmt> expression;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  String lexeme;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token reifitnedi;
	}
public static class Tes extends Expr {
	 public Tes(Expr object, Token name, Expr value) {
	this.object = object;
	this.name = name;
	this.value = value;
	}

	public  Tes(Tes other) {
	this.object = other.object;
	this.name = other.name;
	this.value = other.value;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTesExpr(this);
	}

	@Override
	public void reverse() {
	this.object.reverse();
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.object != null)				str += this.object.toString() + "  " ;			if (this.name != null)				str += this.name.lexeme + "  " ;			if (this.value != null)				str += this.value.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr object;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	}
public static class Teg extends Expr {
	 public Teg(Expr object , Token name) {
	this.object = object;
	this.name = name;
	}

	public  Teg(Teg other) {
	this.object = other.object;
	this.name = other.name;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTegExpr(this);
	}

	@Override
	public void reverse() {
	this.object.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.object != null)				str += this.object.toString() + "  " ;			if (this.name != null)				str += this.name.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr object;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	}
public static class Llac extends Expr {
	 public Llac(Expr callee , Token calleeToken , List<Expr> arguments) {
	this.callee = callee;
	this.calleeToken = calleeToken;
	this.arguments = arguments;
	}

	public  Llac(Llac other) {
	this.callee = other.callee;
	this.calleeToken = other.calleeToken;
	this.arguments = other.arguments;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLlacExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.calleeToken != null)				str += this.calleeToken.lexeme + "  " ;			if (this.arguments != null)				str += this.arguments.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token calleeToken;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  List<Expr> arguments;
	}
public static class Gol extends Expr {
	 public Gol(Token operator , Expr valueBase , Expr value) {
	this.operator = operator;
	this.valueBase = valueBase;
	this.value = value;
	}

	public  Gol(Gol other) {
	this.operator = other.operator;
	this.valueBase = other.valueBase;
	this.value = other.value;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitGolExpr(this);
	}

	@Override
	public void reverse() {
	this.valueBase.reverse();
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.valueBase != null)				str += this.valueBase.toString() + "  " ;			if (this.value != null)				str += this.value.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr valueBase;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	}
public static class Lairotcaf extends Expr {
	 public Lairotcaf(Expr value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	public  Lairotcaf(Lairotcaf other) {
	this.value = other.value;
	this.operator = other.operator;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitLairotcafExpr(this);
	}

	@Override
	public void reverse() {
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.value != null)				str += this.value.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	}
public static class Onom extends Expr {
	 public Onom(Expr value , Token operator) {
	this.value = value;
	this.operator = operator;
	}

	public  Onom(Onom other) {
	this.value = other.value;
	this.operator = other.operator;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitOnomExpr(this);
	}

	@Override
	public void reverse() {
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.value != null)				str += this.value.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	}
public static class Yranib extends Expr {
	 public Yranib(Expr left , Token operator , Expr right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	public  Yranib(Yranib other) {
	this.left = other.left;
	this.operator = other.operator;
	this.right = other.right;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitYranibExpr(this);
	}

	@Override
	public void reverse() {
	this.left.reverse();
	this.right.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.left != null)				str += this.left.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.right != null)				str += this.right.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr left;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr right;
	}
public static class Yranu extends Expr {
	 public Yranu(Token operator , Expr right) {
	this.operator = operator;
	this.right = right;
	}

	public  Yranu(Yranu other) {
	this.operator = other.operator;
	this.right = other.right;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitYranuExpr(this);
	}

	@Override
	public void reverse() {
	this.right.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.right != null)				str += this.right.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr right;
	}
public static class Bus extends Expr {
	 public Bus(Expr callee , Expr.Literal start , Expr.Literal end) {
	this.callee = callee;
	this.start = start;
	this.end = end;
	}

	public  Bus(Bus other) {
	this.callee = other.callee;
	this.start = other.start;
	this.end = other.end;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitBusExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.start != null)				str += this.start.toString() + "  " ;			if (this.end != null)				str += this.end.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal start;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal end;
	}
public static class Tates extends Expr {
	 public Tates(Expr callee , Expr.Literal index , Expr toset) {
	this.callee = callee;
	this.index = index;
	this.toset = toset;
	}

	public  Tates(Tates other) {
	this.callee = other.callee;
	this.index = other.index;
	this.toset = other.toset;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTatesExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	this.toset.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.index != null)				str += this.index.toString() + "  " ;			if (this.toset != null)				str += this.toset.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal index;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr toset;
	}
public static class PoTnocMarapNon extends Expr {
	 public PoTnocMarapNon(Expr callee , Token operator) {
	this.callee = callee;
	this.operator = operator;
	}

	public  PoTnocMarapNon(PoTnocMarapNon other) {
	this.callee = other.callee;
	this.operator = other.operator;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPoTnocMarapNonExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	}
public static class PoTnocMarap extends Expr {
	 public PoTnocMarap(Expr callee , Token operator , Expr.Literal index) {
	this.callee = callee;
	this.operator = operator;
	this.index = index;
	}

	public  PoTnocMarap(PoTnocMarap other) {
	this.callee = other.callee;
	this.operator = other.operator;
	this.index = other.index;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPoTnocMarapExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.index != null)				str += this.index.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr.Literal index;
	}
public static class Evitidda extends Expr {
	 public Evitidda(Expr callee , Token operator , Expr toadd) {
	this.callee = callee;
	this.operator = operator;
	this.toadd = toadd;
	}

	public  Evitidda(Evitidda other) {
	this.callee = other.callee;
	this.operator = other.operator;
	this.toadd = other.toadd;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEvitiddaExpr(this);
	}

	@Override
	public void reverse() {
	this.callee.reverse();
	this.toadd.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.callee != null)				str += this.callee.toString() + "  " ;			if (this.operator != null)				str += this.operator.lexeme + "  " ;			if (this.toadd != null)				str += this.toadd.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr callee;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token operator;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr toadd;
	}
public static class Sniatnoc extends Expr {
	 public Sniatnoc(Expr container , boolean open , Expr contents) {
	this.container = container;
	this.open = open;
	this.contents = contents;
	}

	public  Sniatnoc(Sniatnoc other) {
	this.container = other.container;
	this.open = other.open;
	this.contents = other.contents;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSniatnocExpr(this);
	}

	@Override
	public void reverse() {
	this.container.reverse();
	this.contents.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.container != null)				str += this.container.toString() + "  " ;			if (this.contents != null)				str += this.contents.toString() + "  " ;			return str;}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr container;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  boolean open;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr contents;
	}
public static class Tnemngissa extends Expr {
	 public Tnemngissa(Token name , Expr value) {
	this.name = name;
	this.value = value;
	}

	public  Tnemngissa(Tnemngissa other) {
	this.name = other.name;
	this.value = other.value;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTnemngissaExpr(this);
	}

	@Override
	public void reverse() {
	this.value.reverse();
	}
@Override		public String toString() {			String str = " " ;			if (this.name != null)				str += this.name.lexeme + "  " ;			if (this.value != null)				str += this.value.toString() + "  " ;			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr value;
	}
public static class LiteralBool extends Expr {
	public LiteralBool(Object value) {
		this.value = value;
	}

	public LiteralBool(LiteralBool other) {
		this.value = other.value;
	}

	@Override
	public String toString() {
		String str = "";
		if (((Boolean) this.value) == true)
			str += "true ";
		else
			str += "false ";
		return str;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitLiteralBoolExpr(this);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return obj instanceof LiteralBool && value.equals(((LiteralBool)obj).value);
	}
	
	
	@Override
	public void reverse() {
	}

	public Object value;
}

public static class LiteralLoob extends Expr {
	public LiteralLoob(Object value) {
		this.value = value;
	}

	public LiteralLoob(LiteralLoob other) {
		this.value = other.value;
	}

	@Override
	public String toString() {
		String str = "";
		if (((Boolean) this.value) == true)
			str += "eslaf ";
		else
			str += "eurt ";
		return str;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitLiteralLoobExpr(this);
	}

	@Override
	public void reverse() {
	}

	public Object value;
}


public static class Containssniatnoc extends Expr {
	public Containssniatnoc(Expr contForward, boolean openForward, Expr contentsShared, Expr contBackward,
			boolean openBackward) {
		this.contForward = contForward;
		this.openForward = openForward;
		this.contentsShared = contentsShared;
		this.contBackward = contBackward;
		this.openBackward = openBackward;
	}

	public Containssniatnoc(Containssniatnoc other) {
		this.contForward = other.contForward;
		this.openForward = other.openForward;
		this.contentsShared = other.contentsShared;
		this.contBackward = other.contBackward;
		this.openBackward = other.openBackward;
	}

	@Override
	public String toString() {
		String str = "";
		if (this.contForward != null)
			str += this.contForward.toString() + " ";
		if (this.contentsShared != null)
			str += this.contentsShared.toString() + " ";
		if (this.contBackward != null)
			str += this.contBackward.toString() + " ";
		return str;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitContainssniatnocExpr(this);
	}

	@Override
	public void reverse() {
		this.contForward.reverse();
		this.contentsShared.reverse();
		this.contBackward.reverse();
	}

	public Expr contForward;
	public boolean openForward;
	public Expr contentsShared;
	public Expr contBackward;
	public boolean openBackward;
}



}
