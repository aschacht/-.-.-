package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import java.util.Objects;
import Box.Token.TokenType;

public abstract class Fun extends Declaration {
public abstract void reverse();
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

	public  Function(Function other) {
	this.forwardIdentifier = other.forwardIdentifier;
	this.forwardPrametersType = other.forwardPrametersType;
	this.forwardPrametersNames = other.forwardPrametersNames;
	this.sharedCup = other.sharedCup;
	this.backwardPrametersType = other.backwardPrametersType;
	this.backwardPrametersNames = other.backwardPrametersNames;
	this.backwardIdentifier = other.backwardIdentifier;
	}


	@Override
	public String toString() {
		String str = "";
if (this.forwardIdentifier!=null)
	str+=this.forwardIdentifier.lexeme+" ";
if (this.forwardPrametersType!=null)
	str+=this.forwardPrametersType.toString()+" ";
if (this.forwardPrametersNames!=null)
	str+=this.forwardPrametersNames.toString()+" ";
if (this.sharedCup!=null)
	str+=this.sharedCup.toString()+" ";
if (this.backwardPrametersType!=null)
	str+=this.backwardPrametersType.toString()+" ";
if (this.backwardPrametersNames!=null)
	str+=this.backwardPrametersNames.toString()+" ";
if (this.backwardIdentifier!=null)
	str+=this.backwardIdentifier.lexeme+" ";
	return str;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionFun(this);
	}

	@Override
	public void reverse() {
	this.sharedCup.reverse();
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

	public  FunctionLink(FunctionLink other) {
	this.forwardIdentifier = other.forwardIdentifier;
	this.forwardPrametersType = other.forwardPrametersType;
	this.forwardPrametersNames = other.forwardPrametersNames;
	this.backwardPrametersType = other.backwardPrametersType;
	this.backwardPrametersNames = other.backwardPrametersNames;
	this.backwardIdentifier = other.backwardIdentifier;
	}


	@Override
	public String toString() {
		String str = "";
if (this.forwardIdentifier!=null)
	str+=this.forwardIdentifier.lexeme+" ";
if (this.forwardPrametersType!=null)
	str+=this.forwardPrametersType.toString()+" ";
if (this.forwardPrametersNames!=null)
	str+=this.forwardPrametersNames.toString()+" ";
if (this.backwardPrametersType!=null)
	str+=this.backwardPrametersType.toString()+" ";
if (this.backwardPrametersNames!=null)
	str+=this.backwardPrametersNames.toString()+" ";
if (this.backwardIdentifier!=null)
	str+=this.backwardIdentifier.lexeme+" ";
	return str;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionLinkFun(this);
	}

	@Override
	public void reverse() {
	}

	public  Token forwardIdentifier;
	public  ArrayList<Token> forwardPrametersType;
	public  ArrayList<Token> forwardPrametersNames;
	public  ArrayList<Token> backwardPrametersType;
	public  ArrayList<Token> backwardPrametersNames;
	public  Token backwardIdentifier;
	}

}
