package Box.Token;

public class Token {

	public TokenType type;
	public String lexeme;
	public Object literal;
	public Token identifierToken=null;
	public Token reifitnediToken=null;
	public int line;
	public int column;
	public int start;
	public int finish;
	public Object literalUnGrouped;

	public Token(TokenType type, String lexeme, Object literalGrouped,Object literalUnGrouped, int column, int line, int start, int current) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literalGrouped;
		this.literalUnGrouped = literalUnGrouped;
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
