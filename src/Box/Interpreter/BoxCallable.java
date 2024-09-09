package Box.Interpreter;

import java.lang.reflect.Field;
import java.util.List;

import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Expr;
import Parser.Expr.Pocket;

public interface BoxCallable {

	
	
	public Object call(Interpreter interpreter,List<Object> arguments);
	

	int arity() ;


	public BoxFunction findMethod(String lexeme);
	
	
	
	



}

