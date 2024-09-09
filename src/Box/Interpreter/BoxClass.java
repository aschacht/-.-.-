package Box.Interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Box.Syntax.ExprOLD;
import Box.Syntax.ExprOLD.Literal;
import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Declaration.FunDecl;
import Parser.Expr;
import Parser.Stmt;
import Parser.Stmt.Expression;

public class BoxClass implements BoxCallable {

	public String name;
	private Map<String, BoxFunction> methods;
	private BoxClass superclass;
	public List<Object> contents;
	public TokenType type;
	private boolean enforce;

	private TypesOfObject typesOfObject;
	Environment closure;
	Expr body;

	public BoxClass(String name, BoxClass superclass, ArrayList<Object> boxPrimarys, Map<String, BoxFunction> methods,
			TokenType type, boolean enforce, TypesOfObject typesOfObject, Environment closure, Expr body) {
		this.name = name;
		this.superclass = superclass;
		this.contents = boxPrimarys;
		this.methods = methods;
		this.type = type;
		this.enforce = enforce;

		this.typesOfObject = typesOfObject;
		this.closure = closure;
		this.body = body;

	}

	@Override
	public String toString() {
		String contents = "";
		StringBuilder sb = new StringBuilder(name);
		if (type == TokenType.CUPCONTAINER)
			contents = name + "{ ";
		if (type == TokenType.POCKETCONTAINER)
			contents = name + "( ";

		for (Object object : this.contents) {
			if (object != null) {
				contents += object.toString() + " ";
			} else
				contents += "null ";
		}
		if (type == TokenType.CUPCONTAINER)
			contents += " }" + sb.reverse().toString();
		if (type == TokenType.POCKETCONTAINER)
			contents += " )" + sb.reverse().toString();
		return contents;
	}


	@Override
	public int arity() {
		BoxFunction initilizer = findMethod("init");
		if (initilizer == null)
			return 0;
		return initilizer.arity();
	}

	public BoxFunction findMethod(String lexeme) {
		if (methods != null) {
			if (methods.containsKey(lexeme)) {
				return methods.get(lexeme);
			}
		}
		if (superclass != null)
			return superclass.findMethod(lexeme);

		return null;
	}

	public void setContentsAt(Integer integer, Object expression) {
	
	}

	public Object get(String lexeme) {
		if(methods.containsKey(lexeme)) {
			return methods.get(lexeme);
		}
		return null;
	}

	public void setContentsAtEnd(String data) {
		Literal literal = new ExprOLD.Literal(data);
		contents.add(literal);
	}

	public boolean compairPrimarys(BoxClass boxClass) {
		boolean contains = false;
		for (Object object : boxClass.contents) {
			contains = contents.contains(object);
			if (!contains)
				break;
		}
		return contains;
	}

	public boolean compairPrimarys(BoxContainerClass boxContainerClass) {
		boolean contains = false;
		
		return contains;
	}

	public int size() {

		return contents.size();
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Instance instance = null;

		if (body instanceof Expr.Knot) {
			instance = new KnotInstance(this, ((Expr.Knot) body).expression, body);

		} else if (body instanceof Expr.Cup) {
			instance = new CupInstance(this, ((Expr.Cup) body).expression, body);

		} else if (body instanceof Expr.Pocket) {
			instance = new PocketInstance(this, ((Expr.Pocket) body).expression, body);

		} else if (body instanceof Expr.Tonk) {
			instance = new KnotInstance(this, ((Expr.Tonk) body).expression, body);

		} else if (body instanceof Expr.Box) {
			instance = new BoxInstance(this, ((Expr.Box) body).expression, body);

		}
		return instance;
	}



}
