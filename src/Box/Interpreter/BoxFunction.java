package Box.Interpreter;

import java.util.List;


import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Expr;
import Parser.Stmt;

public class BoxFunction implements BoxCallable {

	private final Environment closure;
	private boolean isInitilizer;
	private List<Token> params;
	private String name;
	private Expr body;
	private Token type;
	private List<Token> paramsTypes;
	private boolean isLink;

	public BoxFunction(Expr body, String name, List<Token> paramsTypes,List<Token> paramsNames, Environment closure, boolean isInitilizer, boolean isLink) {
		this.body = body;
		this.name = name;
		this.paramsTypes = paramsTypes;
		this.params = paramsNames;
		this.isLink = isLink;
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
	public Object call(Interpreter interpreter,List<Object> arguments) {
		
		Environment environment1 = new Environment(closure);
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
					environment1.define(params.get(i).lexeme,paramsTypes.get(i), arguments.get(i));
			
				
			}
		}
		
		Environment previous = interpreter.environment;
		Object evaluate = null;
		try {
			if (body instanceof Expr.Cup)
				interpreter.executeCupExpr(((Expr.Cup) body),environment1);
			

		} catch (Returns returnValue) {
			if (isInitilizer)
				return closure.getAt(0, name);

			
			if(returnValue.value instanceof Expr) {
			Expr value = (Expr)returnValue.value;
			evaluate = interpreter.evaluate(value);
			}else {
				evaluate = returnValue.value;
				
			}
			
			
		}finally {
			 interpreter.environment = previous;
		}
		

		return evaluate;
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

	public BoxFunction bind(Instance boxInstance) {
		Environment environment = new Environment(closure);

		environment.define(name, getType(),boxInstance);

		return new BoxFunction(body, name,paramsTypes, params, environment, isInitilizer,isLink);
	}

	public Token getType() {
		return type;
	}



	@Override
	public BoxFunction findMethod(String lexeme) {
		// TODO Auto-generated method stub
		return null;
	}



}
