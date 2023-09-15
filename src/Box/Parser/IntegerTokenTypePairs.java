package Box.Parser;

import Box.Token.Token;
import Box.Token.TokenType;

public class IntegerTokenTypePairs {

	private Integer amount;
	private Token type;
	

	public IntegerTokenTypePairs(Integer amount , Token type) {
		this.amount = amount;
		this.type = type;
		
		
	}

	public Integer getAmount() {
		return amount;
	}

	public Token getType() {
		return type;
	}


}
