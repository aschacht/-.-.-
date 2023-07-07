package Box.Interpreter;

import Box.Token.Token;

public class RuntimeError extends RuntimeException {

	public Token token;

	public RuntimeError(Token operator, String message) {
		super(message);
		this.token = operator;
	}

}
