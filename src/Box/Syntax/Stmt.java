package Box.Syntax;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import Box.Parser.LogicalOrStorage;

public abstract class Stmt {
	public interface Visitor<R> {
	R visitExpressionStmt(Expression stmt);
	R visitIfStmt(If stmt);
	R visitPrintStmt(Print stmt);
	R visitReturnStmt(Return stmt);
	R visitSaveStmt(Save stmt);
	R visitExpelStmt(Expel stmt);
	R visitReadStmt(Read stmt);
	R visitConsumeStmt(Consume stmt);
	R visitRenameStmt(Rename stmt);
	R visitMoveStmt(Move stmt);
	R visitVarStmt(Var stmt);
	R visitConstructorStmt(Constructor stmt);
	R visitFunctionStmt(Function stmt);
	R visitNoisserpxeStmt(Noisserpxe stmt);
	R visitFiStmt(Fi stmt);
	R visitTnirpStmt(Tnirp stmt);
	R visitNruterStmt(Nruter stmt);
	R visitEvasStmt(Evas stmt);
	R visitDaerStmt(Daer stmt);
	R visitEmanerStmt(Emaner stmt);
	R visitEvomStmt(Evom stmt);
	R visitRavStmt(Rav stmt);
	R visitPassThroughStmt(PassThrough stmt);
	}
public static class Expression extends Stmt {
	 public Expression(Expr expression) {
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitExpressionStmt(this);
	}

	public  Expr expression;
	}
public static class If extends Stmt {
	 public If(Expr ifPocket , Expr ifCup , Stmt elseIfStmt , Expr elseCup) {
	this.ifPocket = ifPocket;
	this.ifCup = ifCup;
	this.elseIfStmt = elseIfStmt;
	this.elseCup = elseCup;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitIfStmt(this);
	}

	public  Expr ifPocket;
	public  Expr ifCup;
	public  Stmt elseIfStmt;
	public  Expr elseCup;
	}
public static class Print extends Stmt {
	 public Print(Token keyword , Expr expression) {
	this.keyword = keyword;
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPrintStmt(this);
	}

	public  Token keyword;
	public  Expr expression;
	}
public static class Return extends Stmt {
	 public Return(Token keyWord , Expr expression) {
	this.keyWord = keyWord;
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitReturnStmt(this);
	}

	public  Token keyWord;
	public  Expr expression;
	}
public static class Save extends Stmt {
	 public Save(Token keyword , Expr filePathFileName , Expr objecttosave) {
	this.keyword = keyword;
	this.filePathFileName = filePathFileName;
	this.objecttosave = objecttosave;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSaveStmt(this);
	}

	public  Token keyword;
	public  Expr filePathFileName;
	public  Expr objecttosave;
	}
public static class Expel extends Stmt {
	 public Expel(Token keyword , Expr toExpell , Expr filePath) {
	this.keyword = keyword;
	this.toExpell = toExpell;
	this.filePath = filePath;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitExpelStmt(this);
	}

	public  Token keyword;
	public  Expr toExpell;
	public  Expr filePath;
	}
public static class Read extends Stmt {
	 public Read(Token keyword , Expr filePath , Expr objectToReadInto) {
	this.keyword = keyword;
	this.filePath = filePath;
	this.objectToReadInto = objectToReadInto;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitReadStmt(this);
	}

	public  Token keyword;
	public  Expr filePath;
	public  Expr objectToReadInto;
	}
public static class Consume extends Stmt {
	 public Consume(Token keyword , Expr boxToFill , Expr filePath) {
	this.keyword = keyword;
	this.boxToFill = boxToFill;
	this.filePath = filePath;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitConsumeStmt(this);
	}

	public  Token keyword;
	public  Expr boxToFill;
	public  Expr filePath;
	}
public static class Rename extends Stmt {
	 public Rename(Token keyword , Expr filePathAndName , Expr filenewname) {
	this.keyword = keyword;
	this.filePathAndName = filePathAndName;
	this.filenewname = filenewname;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitRenameStmt(this);
	}

	public  Token keyword;
	public  Expr filePathAndName;
	public  Expr filenewname;
	}
public static class Move extends Stmt {
	 public Move(Token keyword , Expr OringialfilePathAndFile , Expr newfilePath) {
	this.keyword = keyword;
	this.OringialfilePathAndFile = OringialfilePathAndFile;
	this.newfilePath = newfilePath;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitMoveStmt(this);
	}

	public  Token keyword;
	public  Expr OringialfilePathAndFile;
	public  Expr newfilePath;
	}
public static class Var extends Stmt {
	 public Var(Token name , Expr initializer , Token type , Boolean enforce) {
	this.name = name;
	this.initializer = initializer;
	this.type = type;
	this.enforce = enforce;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitVarStmt(this);
	}

	public  Token name;
	public  Expr initializer;
	public  Token type;
	public  Boolean enforce;
	}
public static class Constructor extends Stmt {
	 public Constructor(Token type, Expr prototype , Integer numberToBuild , boolean enforce) {
	this.type = type;
	this.prototype = prototype;
	this.numberToBuild = numberToBuild;
	this.enforce = enforce;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitConstructorStmt(this);
	}

	public  Token type;
	public  Expr prototype;
	public  Integer numberToBuild;
	public  boolean enforce;
	}
public static class Function extends Stmt {
	 public Function(Expr knotfun0 , Expr identifierfun0 , Expr binFun0 , List<Expr> paramsfun0, List<Expr> paramsfun1 , Expr binFun1  , Expr identifierfun1 , Expr knotfun1) {
	this.knotfun0 = knotfun0;
	this.identifierfun0 = identifierfun0;
	this.binFun0 = binFun0;
	this.paramsfun0 = paramsfun0;
	this.paramsfun1 = paramsfun1;
	this.binFun1 = binFun1;
	this.identifierfun1 = identifierfun1;
	this.knotfun1 = knotfun1;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunctionStmt(this);
	}

	public  Expr knotfun0;
	public  Expr identifierfun0;
	public  Expr binFun0;
	public  List<Expr> paramsfun0;
	public  List<Expr> paramsfun1;
	public  Expr binFun1;
	public  Expr identifierfun1;
	public  Expr knotfun1;
	}
public static class Noisserpxe extends Stmt {
	 public Noisserpxe(Expr noisserpex) {
	this.noisserpex = noisserpex;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitNoisserpxeStmt(this);
	}

	public  Expr noisserpex;
	}
public static class Fi extends Stmt {
	 public Fi(Expr fiPocket , Expr fiCup , Stmt fiesleStmt , Expr esleCup) {
	this.fiPocket = fiPocket;
	this.fiCup = fiCup;
	this.fiesleStmt = fiesleStmt;
	this.esleCup = esleCup;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFiStmt(this);
	}

	public  Expr fiPocket;
	public  Expr fiCup;
	public  Stmt fiesleStmt;
	public  Expr esleCup;
	}
public static class Tnirp extends Stmt {
	 public Tnirp(Token keyword , Expr expression) {
	this.keyword = keyword;
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTnirpStmt(this);
	}

	public  Token keyword;
	public  Expr expression;
	}
public static class Nruter extends Stmt {
	 public Nruter(Token keyword , Expr expression) {
	this.keyword = keyword;
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitNruterStmt(this);
	}

	public  Token keyword;
	public  Expr expression;
	}
public static class Evas extends Stmt {
	 public Evas(Token keyword , Expr filePathFileName , Expr objecttosave) {
	this.keyword = keyword;
	this.filePathFileName = filePathFileName;
	this.objecttosave = objecttosave;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEvasStmt(this);
	}

	public  Token keyword;
	public  Expr filePathFileName;
	public  Expr objecttosave;
	}
public static class Daer extends Stmt {
	 public Daer(Token keyword , Expr filePath , Expr objectToReadInto) {
	this.keyword = keyword;
	this.filePath = filePath;
	this.objectToReadInto = objectToReadInto;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitDaerStmt(this);
	}

	public  Token keyword;
	public  Expr filePath;
	public  Expr objectToReadInto;
	}
public static class Emaner extends Stmt {
	 public Emaner(Token keyword , Expr filePathAndName, Expr filenewname) {
	this.keyword = keyword;
	this.filePathAndName = filePathAndName;
	this.filenewname = filenewname;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEmanerStmt(this);
	}

	public  Token keyword;
	public  Expr filePathAndName;
	public  Expr filenewname;
	}
public static class Evom extends Stmt {
	 public Evom(Token keyword , Expr OringialfilePathAndFile , Expr newfilePath) {
	this.keyword = keyword;
	this.OringialfilePathAndFile = OringialfilePathAndFile;
	this.newfilePath = newfilePath;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEvomStmt(this);
	}

	public  Token keyword;
	public  Expr OringialfilePathAndFile;
	public  Expr newfilePath;
	}
public static class Rav extends Stmt {
	 public Rav(Token name , Expr initializer , Token type , Boolean enforce) {
	this.name = name;
	this.initializer = initializer;
	this.type = type;
	this.enforce = enforce;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitRavStmt(this);
	}

	public  Token name;
	public  Expr initializer;
	public  Token type;
	public  Boolean enforce;
	}
public static class PassThrough extends Stmt {
	 public PassThrough(Expr expression) {
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPassThroughStmt(this);
	}

	public  Expr expression;
	}

 public abstract <R> R accept(Visitor<R> visitor);
}
