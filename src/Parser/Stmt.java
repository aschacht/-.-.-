package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import java.util.Objects;
import Box.Token.TokenType;

public abstract class Stmt extends Declaration {
public abstract void reverse();
public static class Expression extends Stmt {
	 public Expression(Expr expression , Expr noisserpxe) {
	this.expression = expression;
	this.noisserpxe = noisserpxe;
	}

	public  Expression(Expression other) {
	this.expression = other.expression;
	this.noisserpxe = other.noisserpxe;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitExpressionStmt(this);
	}

	@Override
	public void reverse() {
	this.expression.reverse();
	this.noisserpxe.reverse();
	}

	public  Expr expression;
	public  Expr noisserpxe;
	}
public static class If extends Stmt {
	 public If(Expr ifPocket , Expr ifCup , Stmt elseIfStmt , Expr elseCup) {
	this.ifPocket = ifPocket;
	this.ifCup = ifCup;
	this.elseIfStmt = elseIfStmt;
	this.elseCup = elseCup;
	}

	public  If(If other) {
	this.ifPocket = other.ifPocket;
	this.ifCup = other.ifCup;
	this.elseIfStmt = other.elseIfStmt;
	this.elseCup = other.elseCup;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitIfStmt(this);
	}

	@Override
	public void reverse() {
	this.ifPocket.reverse();
	this.ifCup.reverse();
	this.elseIfStmt.reverse();
	this.elseCup.reverse();
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

	public  Print(Print other) {
	this.keyword = other.keyword;
	this.expression = other.expression;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPrintStmt(this);
	}

	@Override
	public void reverse() {
	this.expression.reverse();
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

	public  Save(Save other) {
	this.keyword = other.keyword;
	this.filePathFileName = other.filePathFileName;
	this.objecttosave = other.objecttosave;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSaveStmt(this);
	}

	@Override
	public void reverse() {
	this.filePathFileName.reverse();
	this.objecttosave.reverse();
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

	public  Read(Read other) {
	this.keyword = other.keyword;
	this.filePath = other.filePath;
	this.objectToReadInto = other.objectToReadInto;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitReadStmt(this);
	}

	@Override
	public void reverse() {
	this.filePath.reverse();
	this.objectToReadInto.reverse();
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

	public  Rename(Rename other) {
	this.keyword = other.keyword;
	this.filePathAndName = other.filePathAndName;
	this.filenewname = other.filenewname;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitRenameStmt(this);
	}

	@Override
	public void reverse() {
	this.filePathAndName.reverse();
	this.filenewname.reverse();
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

	public  Move(Move other) {
	this.keyword = other.keyword;
	this.OringialfilePathAndFile = other.OringialfilePathAndFile;
	this.newfilePath = other.newfilePath;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitMoveStmt(this);
	}

	@Override
	public void reverse() {
	this.OringialfilePathAndFile.reverse();
	this.newfilePath.reverse();
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

	public  Return(Return other) {
	this.keyword = other.keyword;
	this.expression = other.expression;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitReturnStmt(this);
	}

	@Override
	public void reverse() {
	this.expression.reverse();
	}

	public  Token keyword;
	public  Expr expression;
	}
public static class Var extends Stmt {
	 public Var(Token name , Token type, int num , Expr initilizer) {
	this.name = name;
	this.type = type;
	this.num = num;
	this.initilizer = initilizer;
	}

	public  Var(Var other) {
	this.name = other.name;
	this.type = other.type;
	this.num = other.num;
	this.initilizer = other.initilizer;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitVarStmt(this);
	}

	@Override
	public void reverse() {
	this.initilizer.reverse();
	}

	public  Token name;
	public  Token type;
	public  int num;
	public  Expr initilizer;
	}
public static class TemplatVar extends Stmt {
	 public TemplatVar(Token name, Token superclass) {
	this.name = name;
	this.superclass = superclass;
	}

	public  TemplatVar(TemplatVar other) {
	this.name = other.name;
	this.superclass = other.superclass;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTemplatVarStmt(this);
	}

	@Override
	public void reverse() {
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

	public  Expel(Expel other) {
	this.keyword = other.keyword;
	this.toExpell = other.toExpell;
	this.filePath = other.filePath;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitExpelStmt(this);
	}

	@Override
	public void reverse() {
	this.toExpell.reverse();
	this.filePath.reverse();
	}

	public  Token keyword;
	public  Expr toExpell;
	public  Expr filePath;
	}
public static class Consume extends Stmt {
	 public Consume(Token keyword , Expr boxToFill , Expr filePath) {
	this.keyword = keyword;
	this.boxToFill = boxToFill;
	this.filePath = filePath;
	}

	public  Consume(Consume other) {
	this.keyword = other.keyword;
	this.boxToFill = other.boxToFill;
	this.filePath = other.filePath;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitConsumeStmt(this);
	}

	@Override
	public void reverse() {
	this.boxToFill.reverse();
	this.filePath.reverse();
	}

	public  Token keyword;
	public  Expr boxToFill;
	public  Expr filePath;
	}
public static class Ifi extends Stmt {
	 public Ifi(Expr ifPocket , Stmt elseIf) {
	this.ifPocket = ifPocket;
	this.elseIf = elseIf;
	}

	public  Ifi(Ifi other) {
	this.ifPocket = other.ifPocket;
	this.elseIf = other.elseIf;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitIfiStmt(this);
	}

	@Override
	public void reverse() {
	this.ifPocket.reverse();
	this.elseIf.reverse();
	}

	public  Expr ifPocket;
	public  Stmt elseIf;
	}
public static class StmttmtS extends Stmt {
	 public StmttmtS(Token keywordForward , Expr expression , Token keywordBackward) {
	this.keywordForward = keywordForward;
	this.expression = expression;
	this.keywordBackward = keywordBackward;
	}

	public  StmttmtS(StmttmtS other) {
	this.keywordForward = other.keywordForward;
	this.expression = other.expression;
	this.keywordBackward = other.keywordBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitStmttmtSStmt(this);
	}

	@Override
	public void reverse() {
	this.expression.reverse();
	}

	public  Token keywordForward;
	public  Expr expression;
	public  Token keywordBackward;
	}
public static class Saveevas extends Stmt {
	 public Saveevas(Token keywordForward , Expr filePathFileName , Expr objecttosave , Token keywordBackward) {
	this.keywordForward = keywordForward;
	this.filePathFileName = filePathFileName;
	this.objecttosave = objecttosave;
	this.keywordBackward = keywordBackward;
	}

	public  Saveevas(Saveevas other) {
	this.keywordForward = other.keywordForward;
	this.filePathFileName = other.filePathFileName;
	this.objecttosave = other.objecttosave;
	this.keywordBackward = other.keywordBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSaveevasStmt(this);
	}

	@Override
	public void reverse() {
	this.filePathFileName.reverse();
	this.objecttosave.reverse();
	}

	public  Token keywordForward;
	public  Expr filePathFileName;
	public  Expr objecttosave;
	public  Token keywordBackward;
	}
public static class Readdaer extends Stmt {
	 public Readdaer(Token keywordForward , Expr filePath , Expr objectToReadInto , Token keywordBackward) {
	this.keywordForward = keywordForward;
	this.filePath = filePath;
	this.objectToReadInto = objectToReadInto;
	this.keywordBackward = keywordBackward;
	}

	public  Readdaer(Readdaer other) {
	this.keywordForward = other.keywordForward;
	this.filePath = other.filePath;
	this.objectToReadInto = other.objectToReadInto;
	this.keywordBackward = other.keywordBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitReaddaerStmt(this);
	}

	@Override
	public void reverse() {
	this.filePath.reverse();
	this.objectToReadInto.reverse();
	}

	public  Token keywordForward;
	public  Expr filePath;
	public  Expr objectToReadInto;
	public  Token keywordBackward;
	}
public static class Renameemaner extends Stmt {
	 public Renameemaner(Token keywordForward , Expr filePathAndName , Expr filenewname , Token keywordBackward) {
	this.keywordForward = keywordForward;
	this.filePathAndName = filePathAndName;
	this.filenewname = filenewname;
	this.keywordBackward = keywordBackward;
	}

	public  Renameemaner(Renameemaner other) {
	this.keywordForward = other.keywordForward;
	this.filePathAndName = other.filePathAndName;
	this.filenewname = other.filenewname;
	this.keywordBackward = other.keywordBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitRenameemanerStmt(this);
	}

	@Override
	public void reverse() {
	this.filePathAndName.reverse();
	this.filenewname.reverse();
	}

	public  Token keywordForward;
	public  Expr filePathAndName;
	public  Expr filenewname;
	public  Token keywordBackward;
	}
public static class Moveevom extends Stmt {
	 public Moveevom(Token keywordForward , Expr OringialfilePathAndFile , Expr newfilePath , Token keywordBackward) {
	this.keywordForward = keywordForward;
	this.OringialfilePathAndFile = OringialfilePathAndFile;
	this.newfilePath = newfilePath;
	this.keywordBackward = keywordBackward;
	}

	public  Moveevom(Moveevom other) {
	this.keywordForward = other.keywordForward;
	this.OringialfilePathAndFile = other.OringialfilePathAndFile;
	this.newfilePath = other.newfilePath;
	this.keywordBackward = other.keywordBackward;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitMoveevomStmt(this);
	}

	@Override
	public void reverse() {
	this.OringialfilePathAndFile.reverse();
	this.newfilePath.reverse();
	}

	public  Token keywordForward;
	public  Expr OringialfilePathAndFile;
	public  Expr newfilePath;
	public  Token keywordBackward;
	}
public static class Stmtnoisserpxe extends Stmt {
	 public Stmtnoisserpxe(Token statementToken , Expr expression , Token noisserpxeToken) {
	this.statementToken = statementToken;
	this.expression = expression;
	this.noisserpxeToken = noisserpxeToken;
	}

	public  Stmtnoisserpxe(Stmtnoisserpxe other) {
	this.statementToken = other.statementToken;
	this.expression = other.expression;
	this.noisserpxeToken = other.noisserpxeToken;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitStmtnoisserpxeStmt(this);
	}

	@Override
	public void reverse() {
	this.expression.reverse();
	}

	public  Token statementToken;
	public  Expr expression;
	public  Token noisserpxeToken;
	}
public static class Rav extends Stmt {
	 public Rav(Token name , Token type, int num , Stmt initilizer) {
	this.name = name;
	this.type = type;
	this.num = num;
	this.initilizer = initilizer;
	}

	public  Rav(Rav other) {
	this.name = other.name;
	this.type = other.type;
	this.num = other.num;
	this.initilizer = other.initilizer;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitRavStmt(this);
	}

	@Override
	public void reverse() {
	this.initilizer.reverse();
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

	public  Nruter(Nruter other) {
	this.keyword = other.keyword;
	this.expression = other.expression;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitNruterStmt(this);
	}

	@Override
	public void reverse() {
	this.expression.reverse();
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

	public  Evom(Evom other) {
	this.keyword = other.keyword;
	this.OringialfilePathAndFile = other.OringialfilePathAndFile;
	this.newfilePath = other.newfilePath;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEvomStmt(this);
	}

	@Override
	public void reverse() {
	this.OringialfilePathAndFile.reverse();
	this.newfilePath.reverse();
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

	public  Emaner(Emaner other) {
	this.keyword = other.keyword;
	this.filePathAndName = other.filePathAndName;
	this.filenewname = other.filenewname;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEmanerStmt(this);
	}

	@Override
	public void reverse() {
	this.filePathAndName.reverse();
	this.filenewname.reverse();
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

	public  Daer(Daer other) {
	this.keyword = other.keyword;
	this.filePath = other.filePath;
	this.objectToReadInto = other.objectToReadInto;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitDaerStmt(this);
	}

	@Override
	public void reverse() {
	this.filePath.reverse();
	this.objectToReadInto.reverse();
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

	public  Evas(Evas other) {
	this.keyword = other.keyword;
	this.filePathFileName = other.filePathFileName;
	this.objecttosave = other.objecttosave;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEvasStmt(this);
	}

	@Override
	public void reverse() {
	this.filePathFileName.reverse();
	this.objecttosave.reverse();
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

	public  Tnirp(Tnirp other) {
	this.keyword = other.keyword;
	this.expression = other.expression;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTnirpStmt(this);
	}

	@Override
	public void reverse() {
	this.expression.reverse();
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

	public  Fi(Fi other) {
	this.ifPocket = other.ifPocket;
	this.ifCup = other.ifCup;
	this.elseIfStmt = other.elseIfStmt;
	this.elseCup = other.elseCup;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFiStmt(this);
	}

	@Override
	public void reverse() {
	this.ifPocket.reverse();
	this.ifCup.reverse();
	this.elseIfStmt.reverse();
	this.elseCup.reverse();
	}

	public  Expr ifPocket;
	public  Expr ifCup;
	public  Stmt elseIfStmt;
	public  Expr elseCup;
	}

}
