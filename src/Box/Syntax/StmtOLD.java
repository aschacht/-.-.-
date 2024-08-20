package Box.Syntax;

import Box.Token.Token;

public abstract class StmtOLD extends DeclarationOLD {
public static class Expression extends StmtOLD {
	 public Expression(ExprOLD expression , ExprOLD noisserpxe) {
	this.expression = expression;
	this.noisserpxe = noisserpxe;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitExpressionStmt(this);
	}

	public  ExprOLD expression;
	public  ExprOLD noisserpxe;
	}
public static class If extends StmtOLD {
	 public If(ExprOLD ifPocket , ExprOLD ifCup , StmtOLD elseIfStmt , ExprOLD elseCup) {
	this.ifPocket = ifPocket;
	this.ifCup = ifCup;
	this.elseIfStmt = elseIfStmt;
	this.elseCup = elseCup;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitIfStmt(this);
	}

	public  ExprOLD ifPocket;
	public  ExprOLD ifCup;
	public  StmtOLD elseIfStmt;
	public  ExprOLD elseCup;
	}
public static class Print extends StmtOLD {
	 public Print(Token keyword , ExprOLD expression) {
	this.keyword = keyword;
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitPrintStmt(this);
	}

	public  Token keyword;
	public  ExprOLD expression;
	}
public static class Return extends StmtOLD {
	 public Return(Token keyWord , ExprOLD expression) {
	this.keyWord = keyWord;
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitReturnStmt(this);
	}

	public  Token keyWord;
	public  ExprOLD expression;
	}
public static class Save extends StmtOLD {
	 public Save(Token keyword , ExprOLD filePathFileName , ExprOLD objecttosave) {
	this.keyword = keyword;
	this.filePathFileName = filePathFileName;
	this.objecttosave = objecttosave;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitSaveStmt(this);
	}

	public  Token keyword;
	public  ExprOLD filePathFileName;
	public  ExprOLD objecttosave;
	}
public static class Expel extends StmtOLD {
	 public Expel(Token keyword , ExprOLD toExpell , ExprOLD filePath) {
	this.keyword = keyword;
	this.toExpell = toExpell;
	this.filePath = filePath;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitExpelStmt(this);
	}

	public  Token keyword;
	public  ExprOLD toExpell;
	public  ExprOLD filePath;
	}
public static class Read extends StmtOLD {
	 public Read(Token keyword , ExprOLD filePath , ExprOLD objectToReadInto) {
	this.keyword = keyword;
	this.filePath = filePath;
	this.objectToReadInto = objectToReadInto;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitReadStmt(this);
	}

	public  Token keyword;
	public  ExprOLD filePath;
	public  ExprOLD objectToReadInto;
	}
public static class Consume extends StmtOLD {
	 public Consume(Token keyword , ExprOLD boxToFill , ExprOLD filePath) {
	this.keyword = keyword;
	this.boxToFill = boxToFill;
	this.filePath = filePath;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitConsumeStmt(this);
	}

	public  Token keyword;
	public  ExprOLD boxToFill;
	public  ExprOLD filePath;
	}
public static class Rename extends StmtOLD {
	 public Rename(Token keyword , ExprOLD filePathAndName , ExprOLD filenewname) {
	this.keyword = keyword;
	this.filePathAndName = filePathAndName;
	this.filenewname = filenewname;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitRenameStmt(this);
	}

	public  Token keyword;
	public  ExprOLD filePathAndName;
	public  ExprOLD filenewname;
	}
public static class Move extends StmtOLD {
	 public Move(Token keyword , ExprOLD OringialfilePathAndFile , ExprOLD newfilePath) {
	this.keyword = keyword;
	this.OringialfilePathAndFile = OringialfilePathAndFile;
	this.newfilePath = newfilePath;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitMoveStmt(this);
	}

	public  Token keyword;
	public  ExprOLD OringialfilePathAndFile;
	public  ExprOLD newfilePath;
	}
public static class Fi extends StmtOLD {
	 public Fi(ExprOLD ifPocket , ExprOLD ifCup , StmtOLD elseIfStmt , ExprOLD elseCup) {
	this.ifPocket = ifPocket;
	this.ifCup = ifCup;
	this.elseIfStmt = elseIfStmt;
	this.elseCup = elseCup;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFiStmt(this);
	}

	public  ExprOLD ifPocket;
	public  ExprOLD ifCup;
	public  StmtOLD elseIfStmt;
	public  ExprOLD elseCup;
	}
public static class Tnirp extends StmtOLD {
	 public Tnirp(Token keyword , ExprOLD expression) {
	this.keyword = keyword;
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitTnirpStmt(this);
	}

	public  Token keyword;
	public  ExprOLD expression;
	}
public static class Nruter extends StmtOLD {
	 public Nruter(Token keyWord , ExprOLD expression) {
	this.keyWord = keyWord;
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitNruterStmt(this);
	}

	public  Token keyWord;
	public  ExprOLD expression;
	}
public static class Evas extends StmtOLD {
	 public Evas(Token keyword , ExprOLD filePathFileName , ExprOLD objecttosave) {
	this.keyword = keyword;
	this.filePathFileName = filePathFileName;
	this.objecttosave = objecttosave;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEvasStmt(this);
	}

	public  Token keyword;
	public  ExprOLD filePathFileName;
	public  ExprOLD objecttosave;
	}
public static class Daer extends StmtOLD {
	 public Daer(Token keyword , ExprOLD filePath , ExprOLD objectToReadInto) {
	this.keyword = keyword;
	this.filePath = filePath;
	this.objectToReadInto = objectToReadInto;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitDaerStmt(this);
	}

	public  Token keyword;
	public  ExprOLD filePath;
	public  ExprOLD objectToReadInto;
	}
public static class Emaner extends StmtOLD {
	 public Emaner(Token keyword , ExprOLD filePathAndName , ExprOLD filenewname) {
	this.keyword = keyword;
	this.filePathAndName = filePathAndName;
	this.filenewname = filenewname;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEmanerStmt(this);
	}

	public  Token keyword;
	public  ExprOLD filePathAndName;
	public  ExprOLD filenewname;
	}
public static class Evom extends StmtOLD {
	 public Evom(Token keyword , ExprOLD OringialfilePathAndFile , ExprOLD newfilePath) {
	this.keyword = keyword;
	this.OringialfilePathAndFile = OringialfilePathAndFile;
	this.newfilePath = newfilePath;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitEvomStmt(this);
	}

	public  Token keyword;
	public  ExprOLD OringialfilePathAndFile;
	public  ExprOLD newfilePath;
	}
public static class Var extends StmtOLD {
	 public Var(Token name , Token type, int num , StmtOLD initilizer) {
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
	public  StmtOLD initilizer;
	}
public static class Rav extends StmtOLD {
	 public Rav(Token name , Token type, int num , StmtOLD initilizer) {
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
	public  StmtOLD initilizer;
	}

}
