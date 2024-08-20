package Box.Syntax;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import Box.Parser.LogicalOrStorage;

public abstract class FunOLD extends DeclarationOLD {
public static class Function extends FunOLD {
	 public Function(Token forwardIdentifier , ArrayList<Token> forwardPrametersType , ArrayList<Token> forwardPrametersNames , ExprOLD sharedCupOrPocketOrKnot , ArrayList<Token> backwardPrametersType , ArrayList<Token> backwardPrametersNames , Token backwardIdentifier) {
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
	public  ExprOLD sharedCupOrPocketOrKnot;
	public  ArrayList<Token> backwardPrametersType;
	public  ArrayList<Token> backwardPrametersNames;
	public  Token backwardIdentifier;
	}

}
