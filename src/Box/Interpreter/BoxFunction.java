package Box.Interpreter;

import java.util.List;

import Box.Syntax.Expr;
import Box.Syntax.Expr.Variable;
import Box.Syntax.Stmt;
import Box.Syntax.Stmt.Function;
import Box.Token.Token;
import Box.Token.TokenType;

public class BoxFunction extends BoxCallable {

	private final Environment closure;
	private boolean isInitilizer;
	private List<Expr> params;
	private String name;
	private Expr body;
	private Token type;

	public BoxFunction(Expr body, String name, List<Expr> params, Environment closure, boolean isInitilizer) {
		this.body = body;
		this.name = name;
		this.params = params;
		String paramsString = "";
		for (int i = 0; i < params.size(); i++) {
			if (i < params.size() - 1) {
				if (params.get(i) instanceof Expr.Variable)
					paramsString += ((Expr.Variable) params.get(i)).name.lexeme + " , ";
				if (params.get(i) instanceof Expr.Elbairav)
					paramsString += ((Expr.Elbairav) params.get(i)).name.lexeme + " , ";
			} else {
				if (params.get(i) instanceof Expr.Variable)
					paramsString += ((Expr.Variable) params.get(i)).name.lexeme;
				if (params.get(i) instanceof Expr.Elbairav)
					paramsString += ((Expr.Elbairav) params.get(i)).name.lexeme;
			}
		}
		String bodyString = "";
		if (body instanceof Expr.Cup) {
			bodyString = ((Expr.Cup) body).lexeme;
		}
		if (body instanceof Expr.Knot) {
			bodyString = ((Expr.Cup) body).lexeme;
		}

		this.type = new Token(TokenType.FUN, name + "( " + paramsString + " )"+bodyString,null,null,-1,-1,-1,-1);
		this.closure = closure;
		this.isInitilizer = isInitilizer;

	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		
		Environment environment1 = new Environment(closure);
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				if (params.get(i) instanceof Expr.Variable) {
					environment1.define(((Expr.Variable) params.get(i)).name.lexeme,((Expr.Variable)params).name, arguments.get(i));
				}
				if (params.get(i) instanceof Expr.Elbairav) {
					environment1.define(((Expr.Elbairav) params.get(i)).name.lexeme,((Expr.Variable)params).name, arguments.get(i));
				}
			}
		}
		try {
			if (body instanceof Expr.Knot)
				interpreter.executeBlock(((Expr.Knot) body).expression, environment1);
			else if (body instanceof Expr.Cup)
				interpreter.executeBlock(((Expr.Cup) body).expression, environment1);
			

		} catch (Returns returnValue) {
			if (isInitilizer)
				return closure.getAt(0, "this");

			return returnValue.value;
		}
		if (isInitilizer)
			return closure.getAt(0, "this");

		return null;
	}

	@Override
	public int arity() {

		return params.size();
	}

	@Override
	public String toString() {

		return "<fn " + name + ">";

	}

	public String getName() {

		return name;
	}

	public BoxFunction bind(BoxInstance boxInstance) {
		Environment environment = new Environment(closure);

		environment.define(name, getType(),boxInstance);

		return new BoxFunction(body, name, params, environment, isInitilizer);
	}

	public Token getType() {
		return type;
	}

}
