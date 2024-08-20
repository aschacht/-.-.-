package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;

public abstract class Fun extends Declaration {
public static class Function extends Fun {
	 public Function(Token forwardIdentifier , ArrayList<Token> forwardPrametersType , ArrayList<Token> forwardPrametersNames , Expr sharedCupOrPocketOrKnot , ArrayList<Token> backwardPrametersType , ArrayList<Token> backwardPrametersNames , Token backwardIdentifier) {
	this.forwardIdentifier = forwardIdentifier;
	this.forwardPrametersType = forwardPrametersType;
	this.forwardPrametersNames = forwardPrametersNames;
	this.sharedCupOrPocketOrKnot = sharedCupOrPocketOrKnot;
	this.backwardPrametersType = backwardPrametersType;
	this.backwardPrametersNames = backwardPrametersNames;
	this.backwardIdentifier = backwardIdentifier;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionFun(this);
	}

	public  Token forwardIdentifier;
	public  ArrayList<Token> forwardPrametersType;
	public  ArrayList<Token> forwardPrametersNames;
	public  Expr sharedCupOrPocketOrKnot;
	public  ArrayList<Token> backwardPrametersType;
	public  ArrayList<Token> backwardPrametersNames;
	public  Token backwardIdentifier;
	}

}
