package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;

public abstract class Fun extends Declaration {
public static class Function extends Fun {
	 public Function(Token forwardIdentifier , ArrayList<Token> forwardPrametersType , ArrayList<Token> forwardPrametersNames , Expr sharedCup , ArrayList<Token> backwardPrametersType , ArrayList<Token> backwardPrametersNames , Token backwardIdentifier) {
	this.forwardIdentifier = forwardIdentifier;
	this.forwardPrametersType = forwardPrametersType;
	this.forwardPrametersNames = forwardPrametersNames;
	this.sharedCup = sharedCup;
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
	public  Expr sharedCup;
	public  ArrayList<Token> backwardPrametersType;
	public  ArrayList<Token> backwardPrametersNames;
	public  Token backwardIdentifier;
	}
public static class FunctionLink extends Fun {
	 public FunctionLink(Token forwardIdentifier , ArrayList<Token> forwardPrametersType , ArrayList<Token> forwardPrametersNames , ArrayList<Token> backwardPrametersType , ArrayList<Token> backwardPrametersNames , Token backwardIdentifier) {
	this.forwardIdentifier = forwardIdentifier;
	this.forwardPrametersType = forwardPrametersType;
	this.forwardPrametersNames = forwardPrametersNames;
	this.backwardPrametersType = backwardPrametersType;
	this.backwardPrametersNames = backwardPrametersNames;
	this.backwardIdentifier = backwardIdentifier;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionLinkFun(this);
	}

	public  Token forwardIdentifier;
	public  ArrayList<Token> forwardPrametersType;
	public  ArrayList<Token> forwardPrametersNames;
	public  ArrayList<Token> backwardPrametersType;
	public  ArrayList<Token> backwardPrametersNames;
	public  Token backwardIdentifier;
	}

}
