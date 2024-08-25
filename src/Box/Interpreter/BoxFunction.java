package Box.Interpreter;

import java.util.List;


import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Expr;
import Parser.Stmt;

public class BoxFunction extends BoxCallable {

	private final Environment closure;
	private boolean isInitilizer;
	private List<Token> params;
	private String name;
	private Expr body;
	private Token type;
	private List<Token> paramsTypes;

	public BoxFunction(Expr body, String name, List<Token> paramsTypes,List<Token> paramsNames, Environment closure, boolean isInitilizer) {
		this.body = body;
		this.name = name;
		this.paramsTypes = paramsTypes;
		this.params = paramsNames;
		String paramsString = "";
		if(params!=null)
		for (int i = 0; i < params.size(); i++) {
			if (i < params.size() - 1) {
				
					paramsString += paramsTypes.get(i)+" "+params.get(i).lexeme+ " , ";
				
			} else {
				paramsString += paramsTypes.get(i)+" "+params.get(i).lexeme;
				
			}
		}
		String bodyString = "";
		if (body instanceof Expr.Cup) {
			bodyString = ((Expr.Cup) body).lexeme;
		}
		if (body instanceof Expr.Knot) {
			bodyString = ((Expr.Cup) body).lexeme;
		}

		this.type = new Token(TokenType.FUN, name + "( " + paramsString + " )"+bodyString,null,null,null,-1,-1,-1,-1);
		this.closure = closure;
		this.isInitilizer = isInitilizer;

	}



	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		
		Environment environment1 = new Environment(closure);
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
					environment1.define(params.get(i).lexeme,paramsTypes.get(i), arguments.get(i));
			
				
			}
		}
		try {
//			if (body instanceof Expr.Knot)
//				interpreter.executeBlock(((Expr.Knot) body).expression, environment1);
//			else if (body instanceof Expr.Cup)
//				interpreter.executeBlock(((Expr.Cup) body).expression, environment1);
//			

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

		return new BoxFunction(body, name,paramsTypes, params, environment, isInitilizer);
	}

	public Token getType() {
		return type;
	}

}
