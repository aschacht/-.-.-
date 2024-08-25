package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;

public abstract class Stmt extends Declaration {
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
public static class Return extends Stmt {
	 public Return(Token keyword , Expr expression) {
	this.keyword = keyword;
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitReturnStmt(this);
	}

	public  Token keyword;
	public  Expr expression;
	}
public static class Var extends Stmt {
	 public Var(Token name , Token type, int num , Stmt initilizer) {
	this.name = name;
	this.type = type;
	this.num = num;
	this.initilizer = initilizer;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitVarStmt(this);
	}

	public  Token name;
	public  Token type;
	public  int num;
	public  Stmt initilizer;
	}
public static class TemplatVar extends Stmt {
	 public TemplatVar(Token name, Token superclass) {
	this.name = name;
	this.superclass = superclass;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTemplatVarStmt(this);
	}

	public  Token name;
	public  Token superclass;
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
public static class Ifi extends Stmt {
	 public Ifi(Expr ifPocket , Stmt elseIf) {
	this.ifPocket = ifPocket;
	this.elseIf = elseIf;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitIfiStmt(this);
	}

	public  Expr ifPocket;
	public  Stmt elseIf;
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
public static class Rav extends Stmt {
	 public Rav(Token name , Token type, int num , Stmt initilizer) {
	this.name = name;
	this.type = type;
	this.num = num;
	this.initilizer = initilizer;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitRavStmt(this);
	}

	public  Token name;
	public  Token type;
	public  int num;
	public  Stmt initilizer;
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
public static class Emaner extends Stmt {
	 public Emaner(Token keyword , Expr filePathAndName , Expr filenewname) {
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
public static class Fi extends Stmt {
	 public Fi(Expr ifPocket , Expr ifCup , Stmt elseIfStmt , Expr elseCup) {
	this.ifPocket = ifPocket;
	this.ifCup = ifCup;
	this.elseIfStmt = elseIfStmt;
	this.elseCup = elseCup;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFiStmt(this);
	}

	public  Expr ifPocket;
	public  Expr ifCup;
	public  Stmt elseIfStmt;
	public  Expr elseCup;
	}

}
