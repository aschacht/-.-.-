package Box.Interpreter;

import java.lang.reflect.Field;
import java.util.List;

import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Expr;
import Parser.Expr.Pocket;

public abstract class BoxCallable {

	public List<Object> contents;
	public TokenType type;
	public Object call(Interpreter interpreter){
		
		return null;
	}
	public Instance instance(Interpreter interp) {
		return null;
		
	}

	int arity() {
		return 0;
	}
	
	
	
	



}

