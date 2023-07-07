package Box.math.Token;

import Box.math.BoxMathTokenType;

public class MathToken {

	public BoxMathTokenType type;
	public String lexeme;
	public Object literal;
	public int line;
	public int column;
	public int start;
	public int finish;

	public MathToken(BoxMathTokenType type, String lexeme,Object literalUnGrouped, int column, int line, int start, int current) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literalUnGrouped;
		this.column = column;
		this.line = line;
		this.start = start;
		this.finish = current;

	}
	
	@Override
	public String toString() {
		return type + " " + lexeme + " " + literal;
	}

}
