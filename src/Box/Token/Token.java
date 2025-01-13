package Box.Token;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class Token {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public TokenType type;
	@JsonIgnore
	public String lexeme;
	@JsonIgnore
	public Token identifierToken=null;
	@JsonIgnore
	public Token reifitnediToken=null;
	@JsonIgnore
	public int line;
	@JsonIgnore
	public int column;
	@JsonIgnore
	public int start;
	@JsonIgnore
	public int finish;
	@JsonIgnore
	public Object literal;
	@JsonIgnore
	public Object literalUnGrouped;
	@JsonIgnore
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
