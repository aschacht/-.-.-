package Box.Syntax;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import Box.Parser.LogicalOrStorage;

public abstract class FunextendsDeclaration {
	public interface Visitor<R> {
	R visitFunctionCupFun(FunctionCup fun);
	R visitFunctionPocketFun(FunctionPocket fun);
	R visitFunctionKnotFun(FunctionKnot fun);
	}
public static class FunctionCup extends Fun {
	 public FunctionCup(Token forwardIdentifier , PramMap<Token,Token> forwardPrameters, Expr.Cup sharedCupOrPocketOrKnot ,  PramMap<Token,Token> backwardPrameters , Token backwardIdentifier) {
	this.forwardIdentifier = forwardIdentifier;
	this.forwardPrameters = forwardPrameters;
	this.sharedCupOrPocketOrKnot = sharedCupOrPocketOrKnot;
for( temp: backwardPrameters){
	this.backwardPrameters.add(temp);
}
	this.backwardIdentifier = backwardIdentifier;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionCupFun(this);
	}

	public  Token forwardIdentifier;
	public  PramMap<Token,Token> forwardPrameters;
	public  Box.Box.Cup sharedCupOrPocketOrKnot;
	public  ArrayList<> backwardPrameters=new ArrayList<>();
	public  Token backwardIdentifier;
	}
public static class FunctionPocket extends Fun {
	 public FunctionPocket(Token forwardIdentifier , PramMap<Token,Token> forwardPrameters, Expr.Pocket sharedCupOrPocketOrKnot ,  PramMap<Token,Token> backwardPrameters , Token backwardIdentifier) {
	this.forwardIdentifier = forwardIdentifier;
	this.forwardPrameters = forwardPrameters;
	this.sharedCupOrPocketOrKnot = sharedCupOrPocketOrKnot;
for( temp: backwardPrameters){
	this.backwardPrameters.add(temp);
}
	this.backwardIdentifier = backwardIdentifier;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionPocketFun(this);
	}

	public  Token forwardIdentifier;
	public  PramMap<Token,Token> forwardPrameters;
	public  Box.Box.Pocket sharedCupOrPocketOrKnot;
	public  ArrayList<> backwardPrameters=new ArrayList<>();
	public  Token backwardIdentifier;
	}
public static class FunctionKnot extends Fun {
	 public FunctionKnot(Token forwardIdentifier , PramMap<Token,Token> forwardPrameters, Expr.Knot sharedCupOrPocketOrKnot ,  PramMap<Token,Token> backwardPrameters , Token backwardIdentifier) {
	this.forwardIdentifier = forwardIdentifier;
	this.forwardPrameters = forwardPrameters;
	this.sharedCupOrPocketOrKnot = sharedCupOrPocketOrKnot;
for( temp: backwardPrameters){
	this.backwardPrameters.add(temp);
}
	this.backwardIdentifier = backwardIdentifier;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionKnotFun(this);
	}

	public  Token forwardIdentifier;
	public  PramMap<Token,Token> forwardPrameters;
	public  Box.Box.Knot sharedCupOrPocketOrKnot;
	public  ArrayList<> backwardPrameters=new ArrayList<>();
	public  Token backwardIdentifier;
	}

 public abstract <R> R accept(Visitor<R> visitor);
}
