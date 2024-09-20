package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import java.util.Objects;
import Box.Token.TokenType;
import com.fasterxml.jackson.annotation.*;

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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Expression && this.expression.equals(((Expression) stmt).expression);
			}else if (obj instanceof Stmt.Expression) {
				return this.expression.equals(((Expression)obj).expression);
			} else
				return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.expression != null)				str += this.expression.toString() + " ";			if (this.noisserpxe != null)str += this.noisserpxe.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr expression;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
			@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof If) {
		            If other = (If) stmt;

		            // Check for null values
		            boolean ifPocketEqual = ifPocket == null ? other.ifPocket == null : ifPocket.equals(other.ifPocket);
		            boolean ifCupEqual = ifCup == null ? other.ifCup == null : ifCup.equals(other.ifCup);
		            boolean elseIfStmtEqual = elseIfStmt == null ? other.elseIfStmt == null : elseIfStmt.equals(other.elseIfStmt);
		            boolean elseCupEqual = elseCup == null ? other.elseCup == null : elseCup.equals(other.elseCup);

		            return ifPocketEqual && ifCupEqual && elseIfStmtEqual && elseCupEqual;
		        }
		    }
		    return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.ifPocket != null)				str += this.ifPocket.toString() + " ";			if (this.ifCup != null)				str += this.ifCup.toString() + " ";			if (this.elseIfStmt != null)				str += this.elseIfStmt.toString() + " ";			if (this.elseCup != null)				str += this.elseCup.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr ifPocket;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr ifCup;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Stmt elseIfStmt;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Print && this.expression.equals(((Print) stmt).expression);
			} else
				return obj instanceof Print && this.expression.equals(((Print) obj).expression);
		}
@Override			public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.expression != null)				str += this.expression.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
			@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Save && this.filePathFileName.equals(((Save) stmt).filePathFileName)&&
						this.objecttosave.equals(((Save) stmt).objecttosave)&&
						this.keyword.lexeme.equals(((Save) stmt).keyword.lexeme);
			} else
				return super.equals(obj);
		}
		
@Override			public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";if (this.filePathFileName != null)				str += this.filePathFileName.toString() + " ";			if (this.objecttosave != null)				str += this.objecttosave.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filePathFileName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Read && this.filePath.equals(((Read) stmt).filePath)&&
						this.objectToReadInto.equals(((Read) stmt).objectToReadInto)&&
						this.keyword.lexeme.equals(((Read) stmt).keyword.lexeme);
			} else
				return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.filePath != null)				str += this.filePath.toString() + " ";			if (this.objectToReadInto != null)				str += this.objectToReadInto.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filePath;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Rename && this.filenewname.equals(((Rename) stmt).filenewname)&&
						this.filePathAndName.equals(((Rename) stmt).filePathAndName)&&
						this.keyword.lexeme.equals(((Rename) stmt).keyword.lexeme);
			} else
				return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.filePathAndName != null)				str += this.filePathAndName.toString() + " ";			if (this.filenewname != null)				str += this.filenewname.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filePathAndName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Move && this.newfilePath.equals(((Move) stmt).newfilePath)&&
						this.OringialfilePathAndFile.equals(((Move) stmt).OringialfilePathAndFile)&&
						this.keyword.lexeme.equals(((Move) stmt).keyword.lexeme);
			} else
				return super.equals(obj);
		}
		
@Override			public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.OringialfilePathAndFile != null)				str += this.OringialfilePathAndFile.toString() + " ";			if (this.newfilePath != null)				str += this.newfilePath.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr OringialfilePathAndFile;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Return && this.expression.equals(((Return) stmt).expression);
			} else
				return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.expression != null)				str += this.expression.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Var && this.initilizer.equals(((Var) stmt).initilizer)&&
						this.name.lexeme.equals(((Var) stmt).name.lexeme)&&
						this.type.lexeme.equals(((Var) stmt).type)&&this.num==((Var)stmt).num;
			} else
				return super.equals(obj);
		}
		
@Override			public String toString() {			String str = "";			if (this.name != null)				str += this.name.lexeme + " ";			if (this.type != null)				str += this.type.lexeme + " ";			str += this.num + " ";			if (this.initilizer != null)				str += this.initilizer.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token type;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  int num;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof TemplatVar && this.name.lexeme.equals(((TemplatVar) stmt).name.lexeme)&&
						this.superclass.lexeme.equals(((TemplatVar) stmt).superclass.lexeme);
			} else
				return super.equals(obj);
		}
		
@Override			public String toString() {			String str = "";			if (this.name != null)				str += this.name.lexeme + " ";			if (this.superclass != null)				str += this.superclass.lexeme + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Expel && this.filePath.equals(((Expel) stmt).filePath)&&
						this.toExpell.equals(((Expel) stmt).toExpell)&&
						this.keyword.lexeme.equals(((Expel) stmt).keyword.lexeme);
			} else
				return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.toExpell != null)				str += this.toExpell.toString() + " ";			if (this.filePath != null)				str += this.filePath.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr toExpell;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Consume && this.filePath.equals(((Consume) stmt).filePath)&&
						this.boxToFill.equals(((Consume) stmt).boxToFill)&&
						this.keyword.lexeme.equals(((Consume) stmt).keyword.lexeme);
			} else
				return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.boxToFill != null)				str += this.boxToFill.toString() + " ";			if (this.filePath != null)				str += this.filePath.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr boxToFill;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
	    public boolean equals(Object obj) {
	        if (obj instanceof StmtDecl) {
	            Stmt stmt = ((StmtDecl) obj).statement;
	            if (stmt instanceof Ifi) {
	                Ifi other = (Ifi) stmt;
	                return this.ifPocket.equals(other.ifPocket) && this.elseIf.equals(other.elseIf);
	            }
	        }
	        return super.equals(obj);
	    }
@Override			public String toString() {			String str = "";			if (this.ifPocket != null)				str += this.ifPocket.toString() + " ";			if (this.elseIf != null)				str += this.elseIf.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr ifPocket;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof StmttmtS) {
		            StmttmtS other = (StmttmtS) stmt;
		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&
		                   expression.equals(other.expression) &&
		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);
		        }
		    }
		    return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + " ";			if (this.expression != null)				str += this.expression.toString() + " ";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keywordForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr expression;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Saveevas) {
		            Saveevas other = (Saveevas) stmt;
		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&
		                   filePathFileName.equals(other.filePathFileName) &&
		                   objecttosave.equals(other.objecttosave) &&
		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);
		        }
		    }
		    return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + " ";			if (this.filePathFileName != null)				str += this.filePathFileName.toString() + " ";			if (this.objecttosave != null)				str += this.objecttosave.toString() + " ";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keywordForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filePathFileName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr objecttosave;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Readdaer) {
		            Readdaer other = (Readdaer) stmt;
		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&
		                   filePath.equals(other.filePath) &&
		                   objectToReadInto.equals(other.objectToReadInto) &&
		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);
		        }
		    }
		    return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + " ";			if (this.filePath != null)				str += this.filePath.toString() + " ";			if (this.objectToReadInto != null)				str += this.objectToReadInto.toString() + " ";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keywordForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filePath;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr objectToReadInto;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Renameemaner) {
		            Renameemaner other = (Renameemaner) stmt;
		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&
		                   filePathAndName.equals(other.filePathAndName) &&
		                   filenewname.equals(other.filenewname) &&
		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);
		        }
		    }
		    return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + " ";			if (this.filePathAndName != null)				str += this.filePathAndName.toString() + " ";			if (this.filenewname != null)				str += this.filenewname.toString() + " ";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + " ";			return str;}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keywordForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filePathAndName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filenewname;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Moveevom) {
		            Moveevom other = (Moveevom) stmt;
		            return keywordForward.lexeme.equals(other.keywordForward.lexeme) &&
		                   OringialfilePathAndFile.equals(other.OringialfilePathAndFile) &&
		                   newfilePath.equals(other.newfilePath) &&
		                   keywordBackward.lexeme.equals(other.keywordBackward.lexeme);
		        }
		    }
		    return super.equals(obj);
		}
@Override			public String toString() {			String str = "";			if (this.keywordForward != null)				str += this.keywordForward.lexeme + " ";			if (this.OringialfilePathAndFile != null)				str += this.OringialfilePathAndFile.toString() + " ";			if (this.newfilePath != null)				str += this.newfilePath.toString() + " ";			if (this.keywordBackward != null)				str += this.keywordBackward.lexeme + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keywordForward;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr OringialfilePathAndFile;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr newfilePath;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Stmtnoisserpxe) {
		            Stmtnoisserpxe other = (Stmtnoisserpxe) stmt;
		            return statementToken.lexeme.equals(other.statementToken.lexeme) &&
		                   expression.equals(other.expression) &&
		                   noisserpxeToken.equals(other.noisserpxeToken);
		        }
		    }
		    return super.equals(obj);
		}
		
@Override			public String toString() {			String str = "";			if (this.statementToken != null)				str += this.statementToken.lexeme + " ";			if (this.expression != null)str += this.expression.toString() + " ";			if (this.noisserpxeToken != null)				str += this.noisserpxeToken.lexeme + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token statementToken;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr expression;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Rav) {
		            Rav other = (Rav) stmt;
		            return name.equals(other.name) &&
		                   type.equals(other.type) &&
		                   num == other.num &&
		                		   initilizer.equals(other.initilizer);
		        }
		    }
		    return super.equals(obj);
		}
		
@Override			public String toString() {			String str = "";			if (this.name != null)				str += this.name.lexeme + " ";			if (this.type != null)				str += this.type.lexeme + " ";			str += this.num + " ";			if (this.initilizer != null)				str += this.initilizer.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token type;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  int num;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
			@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Nruter && this.expression.equals(((Nruter) stmt).expression);
			} else
				return super.equals(null);
		}

@Override			public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.expression != null)				str += this.expression.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Evom) {
		            Evom other = (Evom) stmt;
		            return keyword.lexeme.equals(other.keyword.lexeme) &&
		                   OringialfilePathAndFile.equals(other.OringialfilePathAndFile) &&
		                   newfilePath.equals(other.newfilePath);
		        }
		    }
		    return super.equals(obj);
		}
@Override		public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.OringialfilePathAndFile != null)				str += this.OringialfilePathAndFile.toString() + " ";			if (this.newfilePath != null)				str += this.newfilePath.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr OringialfilePathAndFile;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Emaner) {
		            Emaner other = (Emaner) stmt;
		            return keyword.lexeme.equals(other.keyword.lexeme) &&
		                   filePathAndName.equals(other.filePathAndName) &&
		                   filenewname.equals(other.filenewname);
		        }
		    }
		    return super.equals(obj);
		}
@Override		public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.filePathAndName != null)				str += this.filePathAndName.toString() + " ";			if (this.filenewname != null)				str += this.filenewname.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filePathAndName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Daer) {
		            Daer other = (Daer) stmt;
		            return keyword.lexeme.equals(other.keyword.lexeme) &&
		                   filePath.equals(other.filePath) &&
		                   objectToReadInto.equals(other.objectToReadInto);
		        }
		    }
		    return super.equals(obj);
		}
@Override		public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.filePath != null)				str += this.filePath.toString() + " ";			if (this.objectToReadInto != null)				str += this.objectToReadInto.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filePath;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Evas) {
		            Evas other = (Evas) stmt;
		            return keyword.lexeme.equals(other.keyword.lexeme) &&
		                   filePathFileName.equals(other.filePathFileName) &&
		                   objecttosave.equals(other.objecttosave);
		        }
		    }
		    return super.equals(obj);
		}
@Override		public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.filePathFileName != null)				str += this.filePathFileName.toString() + " ";			if (this.objecttosave != null)				str += this.objecttosave.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr filePathFileName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
			if (obj instanceof StmtDecl) {
				Stmt stmt = ((StmtDecl) obj).statement;
				return stmt instanceof Tnirp && this.expression.equals(((Tnirp) stmt).expression);
			} else
				return super.equals(null);
		}
@Override		public String toString() {			String str = "";			if (this.keyword != null)				str += this.keyword.lexeme + " ";			if (this.expression != null)				str += this.expression.toString() + " ";			return str;		}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Token keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
	@Override
		public boolean equals(Object obj) {
		    if (obj instanceof StmtDecl) {
		        Stmt stmt = ((StmtDecl) obj).statement;
		        if (stmt instanceof Fi) {
		            Fi other = (Fi) stmt;
		            return ifPocket.equals(other.ifPocket) &&
		                   ifCup.equals(other.ifCup) &&
		                   elseIfStmt.equals(other.elseIfStmt) &&
		                   elseCup.equals(other.elseCup);
		        }
		    }
		    return super.equals(obj);
		}
		@Override
		public String toString() {
			String str = "";
			if (this.ifPocket != null)
				str += this.ifPocket.toString() + " ";
			if (this.ifCup != null)
				str += this.ifCup.toString() + " ";
			if (this.elseIfStmt != null)
				str += this.elseIfStmt.toString() + " ";
			if (this.elseCup != null)
				str += this.elseCup.toString() + " ";
			return str;
		}


	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr ifPocket;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr ifCup;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Stmt elseIfStmt;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Expr elseCup;
	}

}
