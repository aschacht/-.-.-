package Box.Syntax;

import Box.Token.Token;

public class TokenKnot{
	Token token;
	int groupingCount;
	public TokenKnot(Token tok,int groupcount) {
		token=tok;
		groupingCount=groupcount;
	}
	public int getGroupingCount() {
		return groupingCount;
	}
	public Token getToken() {
		return token;
	}
}
