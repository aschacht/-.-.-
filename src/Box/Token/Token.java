package Box.Token;

public class Token {

	public TokenType type;
	public String lexeme;
	public Token identifierToken=null;
	public Token reifitnediToken=null;
	public int line;
	public int column;
	public int start;
	public int finish;
	public Object literal;
	public Object literalUnGrouped;
	public Object literalGroupedBackwards;

	public Token(TokenType type, String lexeme, Object literalGrouped,Object literalUnGrouped, Object literalGroupedBackwards, int column, int line, int start, int current) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literalGrouped;
		this.literalUnGrouped = literalUnGrouped;
		this.literalGroupedBackwards = literalGroupedBackwards;
		this.column = column;
		this.line = line;
		this.start = start;
		this.finish = current;

	}
	
	@Override
	public String toString() {
		return type + " " + lexeme + " " + literal;
	}

	public Token clone() {
		
		Token token = new Token(this.type, this.lexeme, this.literal, this.literalUnGrouped, this.literalGroupedBackwards, this.column, this.line, this.start, column);
		if(this.identifierToken!=null)
		token.identifierToken=this.identifierToken.clone();
		if(this.reifitnediToken!=null)
		token.reifitnediToken = this.reifitnediToken.clone();
		return token;
	}
}
