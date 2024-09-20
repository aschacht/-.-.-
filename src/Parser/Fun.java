package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import java.util.Objects;
import Box.Token.TokenType;
import com.fasterxml.jackson.annotation.*;

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
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionFun(this);
	}

	@Override
	public void reverse() {
	this.sharedCup.reverse();
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token forwardIdentifier;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  ArrayList<Token> forwardPrametersType;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  ArrayList<Token> forwardPrametersNames;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr sharedCup;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  ArrayList<Token> backwardPrametersType;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  ArrayList<Token> backwardPrametersNames;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionLinkFun(this);
	}

	@Override
	public void reverse() {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token forwardIdentifier;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  ArrayList<Token> forwardPrametersType;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  ArrayList<Token> forwardPrametersNames;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  ArrayList<Token> backwardPrametersType;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  ArrayList<Token> backwardPrametersNames;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token backwardIdentifier;
	}

}
