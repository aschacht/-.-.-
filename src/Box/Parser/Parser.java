package Box.Parser;

import java.util.ArrayList;
import java.util.List;

import Box.Box.Box;
import Box.Syntax.Expr;
import Box.Syntax.Expr.Boxx;
import Box.Syntax.Expr.Call;
import Box.Syntax.Expr.Cid;
import Box.Syntax.Expr.Cocket;
import Box.Syntax.Expr.Cup;
import Box.Syntax.Expr.Knot;
import Box.Syntax.Expr.Lil;
import Box.Syntax.Expr.Locket;
import Box.Syntax.Expr.Lup;
import Box.Syntax.Expr.Pid;
import Box.Syntax.Expr.Pocket;
import Box.Syntax.Expr.Pup;
import Box.Syntax.Expr.UnknownnwonknU;
import Box.Syntax.Stmt;
import Box.Syntax.Stmt.Daer;
import Box.Syntax.Stmt.Emaner;
import Box.Syntax.Stmt.Evas;
import Box.Syntax.Stmt.Evom;
import Box.Syntax.Stmt.Expression;
import Box.Syntax.Stmt.Nruter;
import Box.Syntax.Stmt.PassThrough;
import Box.Syntax.Stmt.Tnirp;
import Box.Syntax.Stmt.Var;
import Box.Token.TTDynamic;
import Box.Token.Token;
import Box.Token.TokenType;
import Box.Token.TokenTypeEnum;

public class Parser {
	private static class ParseError extends RuntimeException {

		private static final long serialVersionUID = 2715202794403784452L;
	}

	TokensToTrack tracker;

	private boolean forward;

	private boolean backward;

	private class TokensToTrack {
		List<ArrayList<Token>> stackForward = new ArrayList<ArrayList<Token>>();
		List<ArrayList<Token>> stackBackward = new ArrayList<ArrayList<Token>>();
		List<Integer> currentStackForward = new ArrayList<Integer>();
		List<Integer> currentStackBackward = new ArrayList<Integer>();
		private boolean parseForward = true;

		TokensToTrack(ArrayList<Token> baseTokens, int baseCurrent) {
			if (baseTokens.size() > 0) {
				Token eofToken = baseTokens.get(baseTokens.size() - 1);
				ArrayList<Token> newBaseToken = new ArrayList<Token>();

				newBaseToken.add(eofToken);
				for (int i = 0; i < baseTokens.size() - 1; i++) {
					newBaseToken.add(baseTokens.get(i));
				}
				stackForward.add(baseTokens);
				stackBackward.add(newBaseToken);

				currentStackForward.add(baseCurrent);
				currentStackBackward.add(newBaseToken.size() - 1);
			}
		}

		public void addSubTokens(ArrayList<Token> subTokens) {

			Token eofToken = null;
			if (subTokens.size() > 0)
				eofToken = subTokens.get(subTokens.size() - 1);
			else {
				eofToken = new Token(TokenType.EOF, "EOF", null, null, null, 0, 9, 0, 0);
			}

			if (isParseForward() == false) {
				ArrayList<Token> newBaseToken = new ArrayList<Token>();

				newBaseToken.add(eofToken);
				for (int i = 0; i < subTokens.size() - 1; i++) {
					newBaseToken.add(subTokens.get(i));
				}
				stackBackward.add(newBaseToken);
				currentStackBackward.add(newBaseToken.size() - 1);
			} else {

				stackForward.add(subTokens);
				currentStackForward.add(0);
			}
		}

		public boolean removeSubTokens() {
			if (isParseForward() == true) {
				if (stackForward.size() > 1) {
					stackForward.remove(stackForward.size() - 1);
					currentStackForward.remove(currentStackForward.size() - 1);
					return true;
				}
			} else {
				if (stackBackward.size() > 1) {
					stackBackward.remove(stackBackward.size() - 1);
					currentStackBackward.remove(currentStackBackward.size() - 1);
					return true;
				}
			}
			return false;
		}

		public Token getToken() {
			if (isParseForward() == true) {
				int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
				return (stackForward.get(stackForward.size() - 1)).get(currentLocal);
			} else {
				int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
				return (stackBackward.get(stackBackward.size() - 1)).get(currentLocal);
			}
		}

		public void advance() {
			if (isParseForward() == true) {
				int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
				currentLocal++;
				currentStackForward.remove(currentStackForward.size() - 1);
				currentStackForward.add(currentLocal);
			} else {
				int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
				currentLocal--;
				currentStackBackward.remove(currentStackBackward.size() - 1);
				currentStackBackward.add(currentLocal);
			}
		}

		public int getCurrent() {
			if (isParseForward() == true) {
				if (currentStackForward.size() > 0)
					return currentStackForward.get(currentStackForward.size() - 1);
				else
					return 0;
			} else {
				if (currentStackBackward.size() > 0)
					return currentStackBackward.get(currentStackBackward.size() - 1);
				else
					return 0;
			}
		}

		public int size() {
			if (isParseForward() == true) {
				if (stackForward.size() > 0)
					return (stackForward.get(stackForward.size() - 1)).size();
				else
					return 0;
			} else {
				if (stackBackward.size() > 0)
					return (stackBackward.get(stackBackward.size() - 1)).size();
				else
					return 0;
			}
		}

		public Token getPrevious() {
			if (isParseForward() == true) {
				int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
				return (stackForward.get(stackForward.size() - 1)).get(currentLocal - 1);
			} else {
				int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
				return (stackBackward.get(stackBackward.size() - 1)).get(currentLocal + 1);
			}
		}

		public Token getPeekNext() {
			if (isParseForward() == true) {
				int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
				return (stackForward.get(stackForward.size() - 1)).get(currentLocal + 1);
			} else {
				int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
				return (stackBackward.get(stackBackward.size() - 1)).get(currentLocal - 1);
			}
		}

		public void parseBackward() {
			setParseForward(false);
		}

		public void parseForward() {
			setParseForward(true);
		}

		public boolean isParseForward() {
			return parseForward;
		}

		public void setParseForward(boolean parseForward) {
			this.parseForward = parseForward;
		}

		public void regress() {
			if (isParseForward() == true) {
				int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
				currentLocal--;
				currentStackForward.remove(currentStackForward.size() - 1);
				currentStackForward.add(currentLocal);
			} else {
				int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
				currentLocal++;
				currentStackBackward.remove(currentStackBackward.size() - 1);
				currentStackBackward.add(currentLocal);
			}
		}

	}

	public Parser(List<Token> tokens, boolean forward, boolean backward) {
		this.forward = forward;
		this.backward = backward;
		tracker = new TokensToTrack((ArrayList<Token>) tokens, 0);
	}

	public List<List<Stmt>> parse() {

		List<Stmt> forwardStmt = parseForward();
		List<Stmt> forwardResolveStmt = resolveUnkonwns(forwardStmt);
		List<Stmt> backwardStmt = parseBackwards();
		List<Stmt> backwardResolveStmt = resolveUnkonwns(backwardStmt);
		List<List<Stmt>> statements = new ArrayList<List<Stmt>>();
		statements.add(forwardResolveStmt);
		statements.add(backwardResolveStmt);
		return statements;

	}

	private List<Stmt> parseBackwards() {
		List<Stmt> statements = new ArrayList<>();
		tracker.parseBackward();
		while (!isAtBegin()) {
			statements.add(noitaralced());
			fixPreviousStatmentifBackwardsDotFound(statements);
		}
		return statements;
	}

	private List<Stmt> parseForward() {
		List<Stmt> statements = new ArrayList<>();
		tracker.parseForward();
		while (!isAtEnd()) {
			statements.add(declaration());
			fixPreviousStatmentifBackwardsDotFound(statements);
		}
		return statements;
	}

	private void fixPreviousStatmentifBackwardsDotFound(List<Stmt> statements) {
		if (statements.size() > 0) {
			Stmt stmt = statements.get(statements.size() - 1);
			if (stmt instanceof Stmt.PassThrough) {
				Expr expression = ((Stmt.PassThrough) stmt).expression;
				if (expression instanceof Expr.PassThrough) {
					if (((Expr.PassThrough) expression).token.type == TokenType.DOT) {
						if (statements.size() > 1) {
							Stmt stmtprevious = statements.get(statements.size() - 2);
							if (stmtprevious instanceof Stmt.Expression) {
								PassThrough passThroughPrevious = new Stmt.PassThrough(
										((Stmt.Expression) stmtprevious).expression);
								statements.add(statements.size() - 2, passThroughPrevious);
								statements.remove(statements.size() - 2);
							}
						}
					}
				}
			}
		}
	}

	private List<Stmt> resolveUnkonwns(List<Stmt> forwardParse) {

		for (int i = forwardParse.size() - 1; i >= 0; i--) {
			Stmt stmt = checkForUnknows(forwardParse.get(i));
			forwardParse.add(i, stmt);
			forwardParse.remove(i + 1);
		}

		return forwardParse;
	}

	private Stmt checkForUnknows(Stmt stmt) {
		if (stmt instanceof Stmt.Expression) {
			Stmt stmt2 = determineWhatTypeOfBackwardsStmt(stmt);
			if (stmt2 instanceof Stmt.Expression) {
				Stmt stmt3 = checkIfFi(((Stmt.Expression) stmt));
				if (stmt3 instanceof Stmt.Expression)
					return new Stmt.Expression(checkExpressionForUnknown(((Stmt.Expression) stmt)));
				else
					return stmt3;
			} else
				return stmt2;

		}
		return stmt;
	}

	private Stmt checkIfFi(Expression expression) {
		if (expression.expression instanceof Expr.UnknownnwonknU) {
			Expr.UnknownnwonknU unknownCallOrGet = (UnknownnwonknU) expression.expression;
			if (matchesIfPattern(unknownCallOrGet)) {
				return buildIfs(unknownCallOrGet, null);

			} else if (matchesFiPattern(unknownCallOrGet)) {

				return buildFis(unknownCallOrGet, null);
			}
		}
		return expression;
	}

	private List<Stmt> determinStatementForUnknows(List<Stmt> expression) {
		List<Stmt> expressionTemp = new ArrayList<>();
		while (expression.size() > 0) {
			Stmt stmt = consumeBackwards(expression);
			Stmt stmtDetermined = determineWhatTypeOfBackwardsStmt(stmt);
			expressionTemp.add(0, stmtDetermined);
		}
		return expressionTemp;
	}

	private Stmt determineWhatTypeOfBackwardsStmt(Stmt stmt) {
		if (stmt instanceof Stmt.Expression) {
			Expr expr = ((Stmt.Expression) stmt).expression;
			if (expr instanceof Expr.UnknownnwonknU) {

				if (((Expr.UnknownnwonknU) expr).name.type == TokenType.TNIRP) {
					return checkBackwardsTnirp(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.NRUTER) {
					return checkBackwardsNruter(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.DAER) {
					return checkBackwardsDaer(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.EVAS) {
					return checkBackwardsEvas(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.EMANER) {
					return checkBackwardsEmaner(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.EVOM) {
					return checkBackwardsEvom(expr);
				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.PRINT) {
					return checkForwardsPrint(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.RETURN) {
					return checkForwardsReturn(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.READ) {
					return checkForwardsRead(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.SAVE) {
					return checkForwardSave(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.RENAME) {
					return checkForwardsRename(expr);

				} else if (((Expr.UnknownnwonknU) expr).name.type == TokenType.MOVE) {
					return checkForwardsMove(expr);
				}
			} else {
				return stmt;
			}
		}
		return stmt;
	}

	private Stmt checkForwardsMove(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 4)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few after move.");
		Expr nameOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee).callee;
		Expr nameOfFileForMoveAndRename = checkNameOfFileForMoveAndRename(nameOfFile);

		Expr intoKeyword = ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee;
		if (!(((Expr.UnknownnwonknU) intoKeyword).name.type == TokenType.TO))
			throw error(((Expr.UnknownnwonknU) intoKeyword).name, "to not found");

		Token newNamOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) expr).callee).name;
		Expr newFileNameExpr = buildParametersForfileOrPath(newNamOfFile);

		return new Stmt.Move(((Expr.UnknownnwonknU) expr).name, nameOfFileForMoveAndRename, newFileNameExpr);
	}

	private Expr checkNameOfFileForMoveAndRename(Expr nameOfFile) {
		if (!(nameOfFile instanceof Expr.Pocket)) {
			throw error(null, "file name container not of type Pocket ");
		}
		List<Stmt> contents = ((Expr.Pocket) nameOfFile).expression;
		if (contents.size() > 1) {
			throw error(null, "to many arguments for file path");
		}
		if (contents.size() < 1) {
			throw error(null, "to few arguments for file path");
		}
		if (contents.get(0) instanceof Stmt.Expression) {
			if (!(((Stmt.Expression) contents.get(0)).expression instanceof Expr.Literal)) {
				throw error(null, "argument for file name not a literal");
			} else {
				return ((Stmt.Expression) contents.get(0)).expression;
			}
		} else
			throw error(null, "argument for file name not a literal");

	}

	private Stmt checkForwardsRename(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 4)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few after rename.");
		Expr nameOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee).callee;
		Expr nameOfFileForMoveAndRename = checkNameOfFileForMoveAndRename(nameOfFile);

		Expr intoKeyword = ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee;
		if (!(((Expr.UnknownnwonknU) intoKeyword).name.type == TokenType.TO))
			throw error(((Expr.UnknownnwonknU) intoKeyword).name, "to not found");

		Token newNamOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) expr).callee).name;
		Expr newFileNameExpr = buildParametersForfileOrPath(newNamOfFile);

		return new Stmt.Rename(((Expr.UnknownnwonknU) expr).name, nameOfFileForMoveAndRename, newFileNameExpr);
	}

	private Stmt checkForwardSave(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 2)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few after save.");
		Expr nameOfFile = ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee;

		checkNameOfFile(nameOfFile);

		Token objectToReadInto = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) expr).callee).name;
		Expr objectToReadIntoExpr = buildObjectToReadInto(objectToReadInto);
		if (!(objectToReadIntoExpr instanceof Expr.Pocket || objectToReadIntoExpr instanceof Expr.Cup
				|| objectToReadIntoExpr instanceof Expr.Boxx || objectToReadIntoExpr instanceof Expr.Knot
				|| objectToReadIntoExpr instanceof Expr.Variable)) {
			throw error(null, "object to read into not of type Pocket Cup Box Knot or Variable");
		}

		return new Stmt.Save(((Expr.UnknownnwonknU) expr).name, nameOfFile, objectToReadIntoExpr);
	}

	private Stmt checkForwardsRead(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 4)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few after read");
		Expr nameOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee).callee;
		checkNameOfFile(nameOfFile);

		Expr intoKeyword = ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee;
		if (!(((Expr.UnknownnwonknU) intoKeyword).name.type == TokenType.INTO))
			throw error(((Expr.UnknownnwonknU) intoKeyword).name, "into not found");

		Token objectToReadInto = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) expr).callee).name;
		Expr objectToReadIntoExpr = buildObjectToReadInto(objectToReadInto);
		if (!(objectToReadIntoExpr instanceof Expr.Pocket || objectToReadIntoExpr instanceof Expr.Cup
				|| objectToReadIntoExpr instanceof Expr.Boxx || objectToReadIntoExpr instanceof Expr.Knot
				|| objectToReadIntoExpr instanceof Expr.Variable)) {
			throw error(null, "object to read into not of type Pocket Cup Box Knot or Variable");
		}

		return new Stmt.Read(((Expr.UnknownnwonknU) expr).name, nameOfFile, objectToReadIntoExpr);
	}

	private void checkNameOfFile(Expr nameOfFile) {
		if (!(nameOfFile instanceof Expr.Pocket)) {
			throw error(null, "file name container not of type Pocket ");
		}
		List<Stmt> contents = ((Expr.Pocket) nameOfFile).expression;
		if (contents.size() > 1) {
			throw error(null, "to many arguments for file path");
		}
		if (contents.size() < 1) {
			throw error(null, "to few arguments for file path");
		}
		if (contents.get(0) instanceof Stmt.Expression) {
			if (!(((Stmt.Expression) contents.get(0)).expression instanceof Expr.Literal)) {
				throw error(null, "argument for file name not a literal");
			}
		}
	}

	private Stmt checkForwardsReturn(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 1)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few after return.");

		Expr objectToReturn = ((Expr.UnknownnwonknU) expr).callee;

		return new Stmt.Return(((Expr.UnknownnwonknU) expr).name, objectToReturn);
	}

	private Stmt checkForwardsPrint(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 1)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few after print.");

		Expr objectToPrint = ((Expr.UnknownnwonknU) expr).callee;

		return new Stmt.Print(((Expr.UnknownnwonknU) expr).name, objectToPrint);
	}

	private Evom checkBackwardsEvom(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 4)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few before .daer");
		Token nameOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) expr).callee).name;

		Expr fileExpr = buildParametersForfileOrPath(nameOfFile);

		Expr intoKeyword = ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee;
		if (!(((Expr.UnknownnwonknU) intoKeyword).name.type == TokenType.OT))
			throw error(((Expr.UnknownnwonknU) intoKeyword).name, "otni not found");

		Expr newNamOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee).callee;
		Expr newFileNameExpr = null;
		if (newNamOfFile instanceof Expr.Pocket) {
			List<Stmt> newNameStmts = ((Expr.Pocket) newNamOfFile).expression;
			if (newNameStmts.size() != 1) {
				throw error(null, "to many or to few arguments for file path");
			}
			if (newNameStmts.get(0) instanceof Stmt.Expression) {
				newFileNameExpr = ((Stmt.Expression) newNameStmts.get(0)).expression;
				if (!(newFileNameExpr instanceof Expr.Literal)) {
					throw error(null, "argument for file name not a literal");
				}
			}
		}

		return new Stmt.Evom(((Expr.UnknownnwonknU) expr).name, fileExpr, newFileNameExpr);
	}

	private Emaner checkBackwardsEmaner(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 4)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few before .daer");
		Token nameOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) expr).callee).name;

		Expr fileExpr = buildParametersForfileOrPath(nameOfFile);

		Expr intoKeyword = ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee;
		if (!(((Expr.UnknownnwonknU) intoKeyword).name.type == TokenType.OT))
			throw error(((Expr.UnknownnwonknU) intoKeyword).name, "otni not found");

		Expr newNamOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee).callee;
		Expr newFileNameExpr = null;
		if (newNamOfFile instanceof Expr.Pocket) {
			List<Stmt> newNameStmts = ((Expr.Pocket) newNamOfFile).expression;
			if (newNameStmts.size() != 1) {
				throw error(null, "to many or to few arguments for file path");
			}
			if (newNameStmts.get(0) instanceof Stmt.Expression) {
				newFileNameExpr = ((Stmt.Expression) newNameStmts.get(0)).expression;
				if (!(newFileNameExpr instanceof Expr.Literal)) {
					throw error(null, "argument for file name not a literal");
				}
			}
		}

		return new Stmt.Emaner(((Expr.UnknownnwonknU) expr).name, fileExpr, newFileNameExpr);
	}

	@SuppressWarnings("unchecked")
	private Expr buildParametersForfileOrPath(Token nameOfFile) {
		List<Expr> parameters = new ArrayList<>();
		if (nameOfFile.type == TokenType.POCKETCONTAINER || nameOfFile.type == TokenType.CUPCONTAINER
				|| nameOfFile.type == TokenType.BOXCONTAINER || nameOfFile.type == TokenType.KNOTCONTAINER) {
			ArrayList<Token> arguments = (ArrayList<Token>) nameOfFile.literal;
			arguments.remove(arguments.size() - 1);
			arguments.remove(0);
			arguments.add(new Token(TokenType.EOF, "", null, null, null, -1, -1, -1, -1));
			tracker.addSubTokens(arguments);
			buildParameterList(parameters);
			tracker.removeSubTokens();
		} else {
			ArrayList<Token> arguments = new ArrayList<Token>();
			arguments.add(nameOfFile);
			arguments.add(new Token(TokenType.EOF, "", null, null, null, -1, -1, -1, -1));
			tracker.addSubTokens(arguments);
			buildParameterList(parameters);
			tracker.removeSubTokens();

		}
		if (parameters.size() != 1) {
			throw error(null, "to many or to few arguments for file path");
		}
		if (!(parameters.get(0) instanceof Expr.Literal || parameters.get(0) instanceof Expr.Variable)) {
			throw error(null, "argument for file name not a literal");
		}
		Expr fileExpr = parameters.get(0);
		return fileExpr;
	}

	private Expr buildObjectToReadInto(Token objectToken) {
		if (objectToken.type == TokenType.IDENTIFIER || objectToken.type == TokenType.POCKETCONTAINER
				|| objectToken.type == TokenType.CUPCONTAINER || objectToken.type == TokenType.BOXCONTAINER
				|| objectToken.type == TokenType.KNOTCONTAINER) {
			List<Expr> parameters = new ArrayList<>();
			ArrayList<Token> arguments = new ArrayList<Token>();

			arguments.add(objectToken);
			arguments.add(new Token(TokenType.EOF, "", null, null, null, -1, -1, -1, -1));
			tracker.addSubTokens(arguments);
			buildParameterList(parameters);
			tracker.removeSubTokens();

			Expr fileExpr = parameters.get(0);
			return fileExpr;
		} else
			throw error(objectToken, "object to read into not of type Pocket Cup Box Knot or Variable");

	}

	private Daer checkBackwardsDaer(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 4)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few before .daer");
		Token nameOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) expr).callee).name;

		Expr fileExpr = buildParametersForfileOrPath(nameOfFile);

		Expr intoKeyword = ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee;
		if (!(((Expr.UnknownnwonknU) intoKeyword).name.type == TokenType.OTNI))
			throw error(((Expr.UnknownnwonknU) intoKeyword).name, "otni not found");

		Expr objectToReadInto = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee).callee;
		if (!(objectToReadInto instanceof Expr.Pocket || objectToReadInto instanceof Expr.Cup
				|| objectToReadInto instanceof Expr.Boxx || objectToReadInto instanceof Expr.Knot
				|| objectToReadInto instanceof Expr.Variable)) {
			throw error(null, "object to read into not of type Pocket Cup Box Knot or Variable");
		}

		return new Stmt.Daer(((Expr.UnknownnwonknU) expr).name, fileExpr, objectToReadInto);
	}

	private int checkDepth(Expr expr, int i) {
		if (expr instanceof Expr.UnknownnwonknU) {
			i++;
			return i + checkDepth(((Expr.UnknownnwonknU) expr).callee, i);
		}
		return 1;
	}

	private Evas checkBackwardsEvas(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 4)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few before .daer");
		Token nameOfFile = ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) expr).callee).name;

		Expr fileExpr = buildParametersForfileOrPath(nameOfFile);

		Expr objectToReadInto = ((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) (((Expr.UnknownnwonknU) expr).callee)).callee)).callee;
		if (!(objectToReadInto instanceof Expr.Pocket || objectToReadInto instanceof Expr.Cup
				|| objectToReadInto instanceof Expr.Boxx || objectToReadInto instanceof Expr.Knot
				|| objectToReadInto instanceof Expr.Variable)) {
			throw error(null, "object to read into not of type Pocket Cup Box Knot or Variable");
		}

		return new Stmt.Evas(((Expr.UnknownnwonknU) expr).name, fileExpr, objectToReadInto);
	}

	private Nruter checkBackwardsNruter(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 1)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few before .nruter");

		Expr objectToReturn = ((Expr.UnknownnwonknU) expr).callee;

		return new Stmt.Nruter(((Expr.UnknownnwonknU) expr).name, objectToReturn);
	}

	private Tnirp checkBackwardsTnirp(Expr expr) {
		int depth = checkDepth(((Expr.UnknownnwonknU) expr).callee, 0);
		if (depth != 1)
			throw error(((Expr.UnknownnwonknU) expr).name, "to many of to few before .nruter");

		Expr objectToPrint = ((Expr.UnknownnwonknU) expr).callee;

		return new Stmt.Tnirp(((Expr.UnknownnwonknU) expr).name, objectToPrint);
	}

	private Stmt consumeBackwards(List<Stmt> expression) {
		return expression.remove(expression.size() - 1);
	}

	private Expr checkExpressionForUnknown(Expression expression) {
		if (expression.expression instanceof Expr.Knot) {
			return checkKnotForUnknows(((Expr.Knot) expression.expression));
		} else if (expression.expression instanceof Expr.Cup) {
			return checkCupForUnknows(((Expr.Cup) expression.expression));
		} else if (expression.expression instanceof Expr.Pocket) {
			return checkPocketForUnknows(((Expr.Pocket) expression.expression));
		} else if (expression.expression instanceof Expr.Pup) {
			return checkPupForUnknows(((Expr.Pup) expression.expression));
		} else if (expression.expression instanceof Expr.Cocket) {
			return checkCocketForUnknows(((Expr.Cocket) expression.expression));
		} else if (expression.expression instanceof Expr.Lup) {
			return checkLupForUnknows(((Expr.Lup) expression.expression));
		} else if (expression.expression instanceof Expr.Locket) {
			return checkLocketForUnknows(((Expr.Locket) expression.expression));
		} else if (expression.expression instanceof Expr.Lil) {
			return checkLilForUnknows(((Expr.Lil) expression.expression));
		} else if (expression.expression instanceof Expr.Pid) {
			return checkPidForUnknows(((Expr.Pid) expression.expression));
		} else if (expression.expression instanceof Expr.Cid) {
			return checkCidForUnknows(((Expr.Cid) expression.expression));
		} else if (expression.expression instanceof Expr.UnknownnwonknU) {
			return checkUnknownIffICallllaCGetteGForUnknows(((Expr.UnknownnwonknU) expression.expression));
		} else {
			return expression.expression;
		}
	}

	private Expr checkUnknownIffICallllaCGetteGForUnknows(UnknownnwonknU unknownCallOrGet) {

		if (checkCall(unknownCallOrGet) && !checkllaC(unknownCallOrGet)) {

			return checkCreateCall(unknownCallOrGet);

		} else if (checkllaC(unknownCallOrGet) && !checkCall(unknownCallOrGet)) {

			return checkCreateLlac(unknownCallOrGet);

		} else if (checkCall(unknownCallOrGet) && checkllaC(unknownCallOrGet)) {
			Expr llac = checkCreateLlac(unknownCallOrGet);
			Expr call = checkCreateCall(unknownCallOrGet);
			return new Expr.UnKnown(call, llac);
		} else {
			Expr get = createGets(unknownCallOrGet);
			Expr teg = createTegs(unknownCallOrGet, null);
			return new Expr.UnKnown(get, teg);
		}
	}

	private Stmt buildFis(UnknownnwonknU unknownCallOrGet, Stmt stmt) {
		if (unknownCallOrGet.name.type == TokenType.POCKETCONTAINER
				&& unknownCallOrGet.callee instanceof Expr.UnknownnwonknU) {
			if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.CUPCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.Cup) {
				Expr fiCup = buildContainer(((Expr.UnknownnwonknU) unknownCallOrGet.callee));
				Expr fiPocket = buildContainer(unknownCallOrGet);

				stmt = new Stmt.Fi(fiPocket, fiCup, stmt, ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee);
			} else if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.CUPCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.UnknownnwonknU) {
				Expr fiPocket = buildContainer(unknownCallOrGet);
				Expr fiCup = buildContainer(((Expr.UnknownnwonknU) unknownCallOrGet.callee));
				stmt = new Stmt.Fi(fiPocket, fiCup, stmt, null);
				stmt = buildFis(((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee), stmt);

			}
		} else if (unknownCallOrGet.name.type == TokenType.POCKETCONTAINER
				&& unknownCallOrGet.callee instanceof Expr.Cup) {
			Expr fiPocket = buildContainer(unknownCallOrGet);
			stmt = new Stmt.Fi(fiPocket, unknownCallOrGet.callee, stmt, null);

		}
		return stmt;
	}

	private Stmt buildIfs(UnknownnwonknU unknownCallOrGet, Stmt.If nugget) {
		if (unknownCallOrGet.name.type == TokenType.CUPCONTAINER
				&& unknownCallOrGet.callee instanceof Expr.UnknownnwonknU) {
			if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.CUPCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.Pocket) {
				Expr ifCup = buildContainer(((Expr.UnknownnwonknU) unknownCallOrGet.callee));
				Expr elseCup = buildContainer(unknownCallOrGet);

				return new Stmt.If(((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee, ifCup, nugget, elseCup);
			} else if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.POCKETCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.UnknownnwonknU) {
				Expr ifPocket = buildContainer(((Expr.UnknownnwonknU) unknownCallOrGet.callee));
				Expr ifCup = buildContainer(unknownCallOrGet);
				nugget = new Stmt.If(ifPocket, ifCup, nugget, null);
				return buildIfs(((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee), nugget);
			} else if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.CUPCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.UnknownnwonknU) {
				if (((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee).name.type == TokenType.POCKETCONTAINER
						&& ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee).callee instanceof Expr.UnknownnwonknU) {
					Expr ifPocket = buildContainer(
							((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee));
					Expr ifCup = buildContainer(((Expr.UnknownnwonknU) unknownCallOrGet.callee));
					Expr elseCup = buildContainer(unknownCallOrGet);

					nugget = new Stmt.If(ifPocket, ifCup, nugget, elseCup);
					return buildIfs(
							((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee).callee),
							nugget);
				}
			}
		} else if (unknownCallOrGet.name.type == TokenType.CUPCONTAINER
				&& unknownCallOrGet.callee instanceof Expr.Pocket) {
			Expr ifCup = buildContainer(unknownCallOrGet);
			return new Stmt.If(unknownCallOrGet.callee, ifCup, nugget, null);

		}
		return new Stmt.Expression(unknownCallOrGet);
	}

	private Expr buildContainer(UnknownnwonknU unknownCallOrGet) {
		List<Expr> parameters = new ArrayList<>();
		ArrayList<Token> arguments = new ArrayList<Token>();

		arguments.add(unknownCallOrGet.name);
		arguments.add(new Token(TokenType.EOF, "", null, null, null, -1, -1, -1, -1));
		tracker.addSubTokens(arguments);
		buildParameterList(parameters);
		tracker.removeSubTokens();
		Expr ifCup = parameters.get(0);
		return ifCup;
	}

	private boolean matchesFiPattern(UnknownnwonknU unknownCallOrGet) {
		if (unknownCallOrGet.name.type == TokenType.POCKETCONTAINER
				&& unknownCallOrGet.callee instanceof Expr.UnknownnwonknU) {
			if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.CUPCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.Cup) {
				return true;
			} else if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.CUPCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.UnknownnwonknU) {
				return matchesFiPattern(((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee));

			}
		} else if (unknownCallOrGet.name.type == TokenType.POCKETCONTAINER
				&& unknownCallOrGet.callee instanceof Expr.Cup) {

			return true;

		}
		return false;
	}

	private boolean matchesIfPattern(UnknownnwonknU unknownCallOrGet) {
		if (unknownCallOrGet.name.type == TokenType.CUPCONTAINER
				&& unknownCallOrGet.callee instanceof Expr.UnknownnwonknU) {
			if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.CUPCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.Pocket) {
				return true;
			} else if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.POCKETCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.UnknownnwonknU) {
				return matchesIfPattern(((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee));
			} else if (((Expr.UnknownnwonknU) unknownCallOrGet.callee).name.type == TokenType.CUPCONTAINER
					&& ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee instanceof Expr.UnknownnwonknU) {
				if (((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee).name.type == TokenType.POCKETCONTAINER
						&& ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee).callee instanceof Expr.UnknownnwonknU)
					return matchesIfPattern(
							((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) ((Expr.UnknownnwonknU) unknownCallOrGet.callee).callee).callee));
			}
		} else if (unknownCallOrGet.name.type == TokenType.CUPCONTAINER
				&& unknownCallOrGet.callee instanceof Expr.Pocket) {

			return true;

		}
		return false;
	}

	private boolean checkllaC(UnknownnwonknU unknownCallOrGet) {

		return findPocketInUnknownnwokuU(unknownCallOrGet.callee);
	}

	private boolean findPocketInUnknownnwokuU(Expr callee) {
		if (callee instanceof Expr.Pocket)
			return true;
		else if (callee instanceof Expr.UnknownnwonknU)
			return findPocketInUnknownnwokuU(((Expr.UnknownnwonknU) callee).callee);
		else
			return false;
	}

	private boolean checkCall(UnknownnwonknU unknownCallOrGet) {

		return unknownCallOrGet.name.type == TokenType.POCKETCONTAINER;
	}

	private Expr createTegs(Expr callee, Expr nugget) {
		if (callee instanceof Expr.Variable) {

			return new Expr.Teg(nugget, ((Expr.Variable) callee).name);
		}
		Expr createTegs = null;
		if (nugget == null) {
			nugget = new Expr.Variable(((Expr.UnknownnwonknU) callee).name);
			createTegs = createTegs(((Expr.UnknownnwonknU) callee).callee, nugget);
			return createTegs;
		} else {
			if (nugget instanceof Expr.Variable) {
				createTegs = checkCallee(callee, nugget);

			} else if (nugget instanceof Expr.Pocket) {
				createTegs = checkCallee(callee, nugget);
			} else if (nugget instanceof Expr.Boxx) {
				createTegs = checkCallee(callee, nugget);
			} else if (nugget instanceof Expr.Cup) {
				createTegs = checkCallee(callee, nugget);
			} else if (nugget instanceof Expr.Knot) {
				createTegs = checkCallee(callee, nugget);
			} else if (nugget instanceof Expr.Teg) {
				createTegs = checkCallee(callee, nugget);
			} else
				throw error(null, "expected Variable Pocket Box Cup or Knot ");

		}
		return createTegs;

	}

	private Expr checkCallee(Expr callee, Expr nugget) {
		if (callee instanceof Expr.Variable) {

			nugget = new Expr.Teg(nugget, ((Expr.Variable) callee).name);
		} else if (callee instanceof Expr.Pocket) {
			nugget = new Expr.Teg(nugget, ((Expr.Pocket) callee).identifier);

		} else if (callee instanceof Expr.Boxx) {
			callee = new Expr.Teg(nugget, ((Expr.Boxx) callee).identifier);

		} else if (callee instanceof Expr.Cup) {
			nugget = new Expr.Teg(nugget, ((Expr.Cup) callee).identifier);

		} else if (callee instanceof Expr.Knot) {
			nugget = new Expr.Teg(nugget, ((Expr.Knot) callee).identifier);

		} else if (callee instanceof Expr.UnknownnwonknU) {
			nugget = checkCallee(((Expr.UnknownnwonknU) callee).callee,
					new Expr.Teg(nugget, ((Expr.UnknownnwonknU) callee).name));
		} else
			throw error(null, "expected Variable Pocket Box Cup or Knot ");
		return nugget;
	}

	private Expr createGets(UnknownnwonknU unknownCallOrGet) {
		if (unknownCallOrGet.callee instanceof Expr.UnknownnwonknU) {
			return new Expr.Get(createGets(((Expr.UnknownnwonknU) unknownCallOrGet.callee)), unknownCallOrGet.name);
		}
		return new Expr.Get(unknownCallOrGet.callee, unknownCallOrGet.name);
	}

	private Expr checkCreateLlac(Expr callee) {
		if (callee instanceof Expr.UnknownnwonknU) {
			return new Expr.Teg(checkCreateLlac(((Expr.UnknownnwonknU) callee).callee),
					((Expr.UnknownnwonknU) callee).name);
		} else if (callee instanceof Expr.Pocket) {
			List<Stmt> expression = ((Expr.Pocket) callee).expression;
			List<Expr> exprs = new ArrayList<>();
			for (Stmt stmt : expression) {
				exprs.add(getExpr(stmt));
			}
			if (exprs.contains(null)) {
				throw error(null, "could not determine parameters");
			}
			exprs = removeLash(exprs);

			return new Expr.Llac(callee, null, exprs);
		}
		return callee;
	}

	private List<Expr> removeLash(List<Expr> exprs) {
		List<Expr> exprsLashFree = new ArrayList<>();
		for (Expr expr : exprs) {
			if (!(expr instanceof Expr.Lash))
				exprsLashFree.add(expr);
		}
		return exprsLashFree;

	}

	private Expr getExpr(Stmt stmt) {

		if (stmt instanceof Stmt.Expression) {
			return ((Stmt.Expression) stmt).expression;
		} else if (stmt instanceof Stmt.If) {
			throw error(null, "not Primary");
		} else if (stmt instanceof Stmt.Print) {
			return ((Stmt.Print) stmt).expression;
		} else if (stmt instanceof Stmt.Return) {
			return ((Stmt.Return) stmt).expression;
		} else if (stmt instanceof Stmt.Save) {
			throw error(((Stmt.Save) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Expel) {
			throw error(((Stmt.Expel) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Read) {
			throw error(((Stmt.Read) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Consume) {
			throw error(((Stmt.Consume) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Rename) {
			throw error(((Stmt.Rename) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Move) {
			throw error(((Stmt.Move) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Constructor) {
			throw error(((Stmt.Constructor) stmt).type, "not Primary");
		} else if (stmt instanceof Stmt.Function) {
			throw error(null, "not Primary");
		} else if (stmt instanceof Stmt.Noisserpxe) {
			return ((Stmt.Noisserpxe) stmt).noisserpex;
		} else if (stmt instanceof Stmt.Fi) {
			throw error(null, "not Primary");
		} else if (stmt instanceof Stmt.Tnirp) {
			return ((Stmt.Tnirp) stmt).expression;
		} else if (stmt instanceof Stmt.Nruter) {
			return ((Stmt.Nruter) stmt).expression;
		} else if (stmt instanceof Stmt.Evas) {
			throw error(((Stmt.Evas) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Daer) {
			throw error(((Stmt.Daer) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Emaner) {
			throw error(((Stmt.Emaner) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Evom) {
			throw error(((Stmt.Evom) stmt).keyword, "not Primary");
		} else if (stmt instanceof Stmt.Rav) {
			return ((Stmt.Rav) stmt).initializer;
		} else if (stmt instanceof Stmt.PassThrough) {
			return ((Stmt.PassThrough) stmt).expression;
		} else if (stmt instanceof Stmt.UnDetermined) {
			throw error(null, "not Primary");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Call checkCreateCall(UnknownnwonknU unknownCallOrGet) {
		ArrayList<Token> arguments = (ArrayList<Token>) unknownCallOrGet.name.literal;
		if (unknownCallOrGet.name.type == TokenType.POCKETCONTAINER
				|| unknownCallOrGet.name.type == TokenType.BOXCONTAINER
				|| unknownCallOrGet.name.type == TokenType.CUPCONTAINER) {
			arguments.remove(arguments.size() - 1);
			arguments.remove(0);
		}
		arguments.add(new Token(TokenType.EOF, "", null, null, null, -1, -1, -1, -1));
		tracker.addSubTokens(arguments);
		List<Expr> parameters = new ArrayList<>();
		buildParameterList(parameters);
		tracker.removeSubTokens();
		List<Expr> reverseParameters = reverseParameters(parameters);
		return new Expr.Call(checkGet(unknownCallOrGet.callee), null, reverseParameters);

	}

	private Expr checkGet(Expr callee) {
		if (callee instanceof Expr.UnknownnwonknU) {
			return new Expr.Get(checkGet(((Expr.UnknownnwonknU) callee).callee), ((Expr.UnknownnwonknU) callee).name);
		}
		return callee;
	}

	private List<Expr> reverseParameters(List<Expr> parameters) {
		List<Expr> reversed = new ArrayList<>();
		for (int i = parameters.size() - 1; i >= 0; i--) {
			reversed.add(parameters.get(i));
		}
		return reversed;
	}

	private Expr checkCidForUnknows(Cid cid) {
		List<Stmt> expression = cid.expression;
		List<Stmt> findPassThroughs = findUnknows(expression);
		cid.expression = findPassThroughs;
		return cid;
	}

	private Expr checkPidForUnknows(Pid pid) {
		List<Stmt> expression = pid.expression;
		List<Stmt> findPassThroughs = findUnknows(expression);
		pid.expression = findPassThroughs;
		return pid;
	}

	private Expr checkLilForUnknows(Lil lil) {
		List<Stmt> expression = lil.expression;
		List<Stmt> findPassThroughs = findUnknows(expression);
		lil.expression = findPassThroughs;
		return lil;
	}

	private Expr checkLocketForUnknows(Locket locket) {
		List<Stmt> expression = locket.expression;
		List<Stmt> findPassThroughs = findUnknows(expression);
		locket.expression = findPassThroughs;
		return locket;
	}

	private Expr checkLupForUnknows(Lup lup) {
		List<Stmt> expression = lup.expression;
		List<Stmt> findPassThroughs = findUnknows(expression);
		lup.expression = findPassThroughs;
		return lup;
	}

	private Expr checkCocketForUnknows(Cocket cocket) {
		List<Stmt> expression = cocket.expression;
		List<Stmt> findPassThroughs = findUnknows(expression);
		cocket.expression = findPassThroughs;
		return cocket;
	}

	private Expr checkPupForUnknows(Pup pup) {
		List<Stmt> expression = pup.expression;
		List<Stmt> findPassThroughs = findUnknows(expression);
		pup.expression = findPassThroughs;
		return pup;
	}

	private Expr checkPocketForUnknows(Pocket pocket) {
		List<Stmt> expression = pocket.expression;
		List<Stmt> findPassThroughs = findUnknows(expression);
		pocket.expression = findPassThroughs;
		return pocket;
	}

	private Expr checkCupForUnknows(Cup cup) {
		List<Stmt> expression = cup.expression;
		List<Stmt> findPassThroughs = findUnknows(expression);
		cup.expression = findPassThroughs;
		return cup;

	}

	private List<Stmt> findUnknows(List<Stmt> expression) {

		List<Stmt> tempExpression = new ArrayList<>(expression);
		return determinStatementForUnknows(tempExpression);

	}

	private Expr checkKnotForUnknows(Knot knot) {
		List<Stmt> expression = knot.expression;
		List<Stmt> expressionTemp = new ArrayList<>();
		for (Stmt stmt : expression) {
			expressionTemp.add(checkForUnknows(stmt));
		}
		knot.expression = expressionTemp;
		return knot;
	}

	private Stmt declaration() {
		try {

			if (match(TokenType.LESSTHEN)) {
				Stmt forwardVariableDeclaration = variableDeclaration();
				if (forwardVariableDeclaration != null)
					return forwardVariableDeclaration;
			}

			Expr expr = expression();

			Stmt expellorconsumeStatement = expellorconsumeStatement(expr);
			if (expellorconsumeStatement instanceof Stmt.Expel || expellorconsumeStatement instanceof Stmt.Consume) {
				return expellorconsumeStatement;
			}

			return expressionStmt(expr);
		} catch (ParseError error) {
			synchronize();
			return null;
		}
	}

	private Stmt noitaralced() {
		try {

			Expr expr = noisserpxe();

			return noisserpxeStmt(expr);
		} catch (ParseError error) {
			synchronize();
			return null;
		}
	}

	private Stmt expellorconsumeStatement(Expr expr) {

		if (check(TokenType.EXPELL)) {
			Token keyword = consume(TokenType.EXPELL, "expected >>> ");
			Expr boxWithFileName = expression();
			Expr.Literal path = null;
			if (boxWithFileName instanceof Expr.Boxx) {
				List<Expr> expressions = ((Expr.Boxx) boxWithFileName).primarys;
				path = ((Expr.Literal) expressions.get(0));
			}
			return new Stmt.Expel(keyword, expr, path);
		}
		if (check(TokenType.CONSUME)) {
			Token keyword = consume(TokenType.CONSUME, "expected <<< ");
			Expr boxWithFileName = expression();
			Expr.Literal path = null;
			if (boxWithFileName instanceof Expr.Boxx) {
				List<Expr> expressions = ((Expr.Boxx) boxWithFileName).primarys;
				path = ((Expr.Literal) expressions.get(0));
			}
			return new Stmt.Consume(keyword, expr, path);
		}
		return new Stmt.PassThrough(expr);
	}

	private Stmt variableDeclaration() {
		if (match(TokenType.BOX, TokenType.POCKET, TokenType.CUP, TokenType.KNOT, TokenType.XOB, TokenType.TEKCOP,
				TokenType.PUC, TokenType.TONK)) {
			ArrayList<IntegerTokenTypePairs> forwardPairs = new ArrayList<IntegerTokenTypePairs>();
			Token type = previous();
			if (match(TokenType.INTNUM)) {
				Token previous = previous();
				forwardPairs.add(new IntegerTokenTypePairs(((Integer) previous.literal), type));
			} else {
				forwardPairs.add(new IntegerTokenTypePairs(1, type));
			}

			while (match(TokenType.INTNUM) || matchVariableTypes()) {
				Token previous = previous();
				if (previous.type == TokenType.INTNUM) {
					if (matchVariableTypes()) {
						Token newType = previous();
						Integer value = ((Integer) previous.literal);
						forwardPairs.add(new IntegerTokenTypePairs(value, newType));
					} else
						throw error(previous, "expected int or Variable Type");
				} else {
					if (match(TokenType.INTNUM)) {
						Token value = previous();
						forwardPairs.add(new IntegerTokenTypePairs(((Integer) value.literal), previous));
					} else {
						forwardPairs.add(new IntegerTokenTypePairs(1, previous));
					}
				}
			}

			if (match(TokenType.IDENTIFIER, TokenType.BOXCONTAINER, TokenType.POCKETCONTAINER, TokenType.CUPCONTAINER,
					TokenType.KNOTCONTAINER)) {
				Var forward = null;
				Var backward = null;
				Token containerOrIdentifier = previous();
				backward = createBackwardsVar(containerOrIdentifier);

				forward = createForwardVar(forwardPairs, containerOrIdentifier);

				if (match(TokenType.GREATERTHEN)) {
					if (backward == null) {
						Var buildInitilizer = buildInitilizer(containerOrIdentifier);
						if (buildInitilizer == null) {
							Token typeInitilizer = new Token(TokenType.BOX, "box", null, null, null,
									containerOrIdentifier.column, containerOrIdentifier.line,
									containerOrIdentifier.start, containerOrIdentifier.finish);
							return new Stmt.VarFB(forward,
									new Stmt.Var(containerOrIdentifier, typeInitilizer, 1, null));
						}
						return new Stmt.VarFB(forward, buildInitilizer);
					} else
						return new Stmt.VarFB(forward, backward);
				}
			}
		} else if (match(TokenType.IDENTIFIER, TokenType.BOXCONTAINER, TokenType.POCKETCONTAINER,
				TokenType.CUPCONTAINER, TokenType.KNOTCONTAINER)) {

			Var backward = null;
			Token containerOrIdentifier = previous();
			backward = createBackwardsVar(containerOrIdentifier);

			if (match(TokenType.GREATERTHEN)) {
				Var buildInitilizer = buildInitilizer(containerOrIdentifier);
				Token type = new Token(TokenType.BOX, "box", null, null, null, containerOrIdentifier.column,
						containerOrIdentifier.line, containerOrIdentifier.start, containerOrIdentifier.finish);
				if (backward == null) {
					if (buildInitilizer == null) {
						return new Stmt.VarFB(new Stmt.Var(containerOrIdentifier, type, 1, null),
								new Stmt.Var(containerOrIdentifier, type, 1, null));
					}
					return new Stmt.VarFB(buildInitilizer, buildInitilizer);
				} else
					return new Stmt.VarFB(buildInitilizer, backward);
			}
		}
		return null;
	}

	private Var createBackwardsVar(Token containerOrIdentifier) {
		if (match(TokenType.INTNUM) || matchVariableTypes()) {

			Token type = previous();

			ArrayList<IntegerTokenTypePairs> typesAndAmountsAfterFirst = new ArrayList<IntegerTokenTypePairs>();

			if (type.type == TokenType.INTNUM) {
				if (matchVariableTypes()) {
					Token previous = previous();
					Integer forwardInteger = (Integer) type.literal;
					Integer reverseInteger = reverseTheInteger(forwardInteger);
					typesAndAmountsAfterFirst.add(new IntegerTokenTypePairs(reverseInteger, previous));
				} else
					throw error(type, "expected int of Variable Type");

			} else {
				typesAndAmountsAfterFirst.add(new IntegerTokenTypePairs(1, type));
			}

			while (match(TokenType.INTNUM) || matchVariableTypes()) {
				Token previous = previous();
				if (previous.type == TokenType.INTNUM) {
					if (matchVariableTypes()) {
						Token newType = previous();
						Integer forwardInteger = (Integer) previous.literal;
						Integer reverseInteger = reverseTheInteger(forwardInteger);
						typesAndAmountsAfterFirst.add(new IntegerTokenTypePairs(reverseInteger, newType));
					} else
						throw error(previous, "expected int of Variable Type");
				} else {
					if (match(TokenType.INTNUM)) {
						Token value = previous();
						Integer forwardInteger = (Integer) value.literal;
						Integer reverseInteger = reverseTheInteger(forwardInteger);
						typesAndAmountsAfterFirst.add(new IntegerTokenTypePairs(reverseInteger, previous));
					} else {
						typesAndAmountsAfterFirst.add(new IntegerTokenTypePairs(1, previous));
					}
				}
			}

			Var initilizer = buildInitilizer(containerOrIdentifier);
			IntegerTokenTypePairs integerTokenTypePairs = typesAndAmountsAfterFirst.get(0);
			typesAndAmountsAfterFirst.remove(0);
			Var init = null;
			if (initilizer == null) {
				init = new Stmt.Var(integerTokenTypePairs.getType(), integerTokenTypePairs.getType(), 1, null);
			} else {

				init = new Stmt.Var(null, integerTokenTypePairs.getType(), integerTokenTypePairs.getAmount(),
						initilizer);
			}

			while (typesAndAmountsAfterFirst.size() > 0) {
				IntegerTokenTypePairs pairs = typesAndAmountsAfterFirst.get(0);
				typesAndAmountsAfterFirst.remove(0);
				init = new Stmt.Var(null, pairs.getType(), pairs.getAmount(), init);
			}

			return init;

		}
		return null;
	}

	private Integer reverseTheInteger(Integer x) {

		boolean is_negative = x < 0;
		String x_string = String.valueOf(x);
		if (x_string.charAt(0) == '-') {
			x_string = x_string.substring(1);
		}

		StringBuilder sb = new StringBuilder(x_string);
		String reversed_x_string = sb.reverse().toString();
		reversed_x_string = (is_negative ? '-' + reversed_x_string : reversed_x_string);

		long reversed_number = Long.parseLong(reversed_x_string);

		if (reversed_number > Integer.MAX_VALUE || reversed_number < Integer.MIN_VALUE) {
			return 0;
		}

		return (int) reversed_number;
	}

	private Var buildInitilizer(Token containerOrIdentifier) {
		Var initilizer;
		if (containerOrIdentifier.type == TokenType.IDENTIFIER) {
			initilizer = null;
		} else if (containerOrIdentifier.type == TokenType.POCKETCONTAINER) {
			ArrayList<Token> tokens2 = new ArrayList<>();
			tokens2.add(containerOrIdentifier);
			tokens2.add(new Token(TokenType.EOF, "", null, null, null, tokens2.size(), -1, -1, -1));

			tracker.addSubTokens(tokens2);
			Stmt declaration = declaration();
			tracker.removeSubTokens();
			Pocket pocket = (Pocket) ((Stmt.Expression) declaration).expression;
			Token name = pocket.identifier;
			Token type = new Token(TokenType.POCKET, "pkt", null, null, null, pocket.identifier.column,
					pocket.identifier.line, pocket.identifier.start, pocket.reifitnedi.finish);
			initilizer = new Stmt.Var(name, type, 1, declaration);

		} else if (containerOrIdentifier.type == TokenType.CUPCONTAINER) {
			ArrayList<Token> tokens2 = new ArrayList<>();
			tokens2.add(containerOrIdentifier);
			tokens2.add(new Token(TokenType.EOF, "", null, null, null, tokens2.size(), -1, -1, -1));

			tracker.addSubTokens(tokens2);
			Stmt declaration = declaration();
			tracker.removeSubTokens();
			Cup cup = (Cup) ((Stmt.Expression) declaration).expression;
			Token name = cup.identifier;
			Token type = new Token(TokenType.CUP, "cup", null, null, null, cup.identifier.column, cup.identifier.line,
					cup.identifier.start, cup.reifitnedi.finish);
			initilizer = new Stmt.Var(name, type, 1, declaration);

		} else if (containerOrIdentifier.type == TokenType.BOXCONTAINER) {
			ArrayList<Token> tokens2 = new ArrayList<>();
			tokens2.add(containerOrIdentifier);
			tokens2.add(new Token(TokenType.EOF, "", null, null, null, tokens2.size(), -1, -1, -1));

			tracker.addSubTokens(tokens2);
			Stmt declaration = declaration();
			tracker.removeSubTokens();
			Boxx box = (Boxx) ((Stmt.Expression) declaration).expression;
			Token name = box.identifier;
			Token type = new Token(TokenType.BOX, "box", null, null, null, box.identifier.column, box.identifier.line,
					box.identifier.start, box.reifitnedi.finish);
			initilizer = new Stmt.Var(name, type, 1, declaration);

		} else {
			ArrayList<Token> tokens2 = new ArrayList<>();
			tokens2.add(containerOrIdentifier);
			tokens2.add(new Token(TokenType.EOF, "", null, null, null, tokens2.size(), -1, -1, -1));

			tracker.addSubTokens(tokens2);
			Stmt declaration = declaration();
			tracker.removeSubTokens();
			Knot knot = (Knot) ((Stmt.Expression) declaration).expression;
			Token name = knot.identifier;
			Token type = new Token(TokenType.KNOT, "knt", null, null, null, knot.identifier.column,
					knot.identifier.line, knot.identifier.start, knot.reifitnedi.finish);
			initilizer = new Stmt.Var(name, type, 1, declaration);

		}
		return initilizer;
	}

	private Var buildInitilizer(Expr containerOrIdentifier) {
		Var initilizer = null;
		if (containerOrIdentifier instanceof Expr.Variable) {
			initilizer = null;
		} else if (containerOrIdentifier instanceof Expr.Pocket) {

			Pocket pocket = (Pocket) containerOrIdentifier;
			Token name = pocket.identifier;
			Token type = new Token(TokenType.POCKET, "pkt", null, null, null, pocket.identifier.column,
					pocket.identifier.line, pocket.identifier.start, pocket.reifitnedi.finish);

			initilizer = new Stmt.Var(name, type, 1, new Stmt.Expression(containerOrIdentifier));

		} else if (containerOrIdentifier instanceof Expr.Cup) {

			Cup cup = (Cup) containerOrIdentifier;
			Token name = cup.identifier;
			Token type = new Token(TokenType.CUP, "cup", null, null, null, cup.identifier.column, cup.identifier.line,
					cup.identifier.start, cup.reifitnedi.finish);

			initilizer = new Stmt.Var(name, type, 1, new Stmt.Expression(containerOrIdentifier));

		} else if (containerOrIdentifier instanceof Expr.Boxx) {
			Boxx box = (Boxx) containerOrIdentifier;
			Token name = box.identifier;
			Token type = new Token(TokenType.BOX, "box", null, null, null, box.identifier.column, box.identifier.line,
					box.identifier.start, box.reifitnedi.finish);

			initilizer = new Stmt.Var(name, type, 1, new Stmt.Expression(containerOrIdentifier));

		} else if (containerOrIdentifier instanceof Expr.Knot) {
			Knot knot = (Knot) containerOrIdentifier;
			Token name = knot.identifier;
			Token type = new Token(TokenType.KNOT, "knt", null, null, null, knot.identifier.column,
					knot.identifier.line, knot.identifier.start, knot.reifitnedi.finish);

			initilizer = new Stmt.Var(name, type, 1, new Stmt.Expression(containerOrIdentifier));

		}
		return initilizer;
	}

	private Var createForwardVar(ArrayList<IntegerTokenTypePairs> forwardPairs, Token containerOrIdentifier) {
		Var initilizer = buildInitilizer(containerOrIdentifier);
		IntegerTokenTypePairs integerTokenTypePairs = forwardPairs.get(forwardPairs.size() - 1);
		forwardPairs.remove(forwardPairs.size() - 1);
		Var init = null;
		if (initilizer == null) {
			init = new Stmt.Var(containerOrIdentifier, integerTokenTypePairs.getType(), 1, null);
		} else {

			init = new Stmt.Var(null, integerTokenTypePairs.getType(), integerTokenTypePairs.getAmount(), initilizer);
		}

		while (forwardPairs.size() > 0) {
			IntegerTokenTypePairs pairs = forwardPairs.get(forwardPairs.size() - 1);
			forwardPairs.remove(forwardPairs.size() - 1);
			init = new Stmt.Var(null, pairs.getType(), pairs.getAmount(), init);
		}
		return init;
	}

	private boolean matchVariableTypes() {

		return match(TokenType.BOX, TokenType.POCKET, TokenType.CUP, TokenType.KNOT, TokenType.XOB, TokenType.TEKCOP,
				TokenType.PUC, TokenType.TONK);
	}

	private void synchronize() {
		advance();
		while (!isAtEnd()) {
			if (previous().type == TokenType.SEMICOLON)
				return;

			switch (peek().type) {
			case PRINT:
			case RETURN:
				return;
			default:
				break;
			}
			advance();

		}

	}

	private Stmt expressionStmt(Expr expr) {
		if (expr instanceof Expr.PassThrough)
			return new Stmt.PassThrough(expr);
		if (expr instanceof Expr.Variable) {
			Var buildInitilizer = buildInitilizer(expr);
			if (buildInitilizer == null) {
				Token name = ((Expr.Variable) expr).name;
				Var var = new Stmt.Var(name, new Token(TokenType.BOX, null, null, null, null, name.column, name.line,
						name.start, name.finish), 1, null);
				return new Stmt.VarFB(var, var);
			}
			return new Stmt.VarFB(buildInitilizer, buildInitilizer);
		}
		return new Stmt.Expression(expr);
	}

	private Stmt noisserpxeStmt(Expr expr) {
		if (expr instanceof Expr.PassThrough)
			return new Stmt.PassThrough(expr);
		if (expr instanceof Expr.Variable) {
			Var buildInitilizer = buildInitilizer(expr);
			if (buildInitilizer == null) {
				Token name = ((Expr.Variable) expr).name;
				Var var = new Stmt.Var(name, new Token(TokenType.BOX, null, null, null, null, name.column, name.line,
						name.start, name.finish), 1, null);
				return new Stmt.VarFB(var, var);
			}
			return new Stmt.VarFB(buildInitilizer, buildInitilizer);
		}
		return new Stmt.Noisserpxe(expr);
	}

	private Expr expression() {
		return typeExpr();
	}

	private Expr noisserpxe() {
		return typeRpxe();
	}

	public Expr typeRpxe() {
		Expr expr = tnemngissa();
		if (check(TokenType.TYPE) && checkNext(TokenType.DOT)) {
			consume(TokenType.TYPE, "expected type.");
			consume(TokenType.DOT, "expected '.'.");
			return new Expr.Type(expr);
		}
		return expr;

	}

	public Expr typeExpr() {
		Expr expr = assignment();
		if (check(TokenType.DOT) && checkNext(TokenType.TYPE)) {
			consume(TokenType.DOT, "expected '.'.");
			consume(TokenType.TYPE, "expected type.");
			return new Expr.Type(expr);
		}
		return expr;

	}

	private Expr assignment() {

		Expr expr = contains();

		if (match(TokenType.ASIGNMENTEQUALS)) {
			Token equals = previous();
			Expr value = assignment();

			if (expr instanceof Expr.Variable) {
				Token name = ((Expr.Variable) expr).name;
				return new Expr.Assignment(name, value);
			} else if (expr instanceof Expr.UnknownnwonknU) {
				Expr.UnknownnwonknU get = (Expr.UnknownnwonknU) expr;
				return new Expr.Set(get, get.name, value);
			}

			error(equals, "Invalid assignment target.");

		}

		return expr;
	}

	private Expr tnemngissa() {

		Expr expr = sniatnoc();

		if (match(TokenType.ASIGNMENTEQUALS)) {
			Token equals = previous();
			Expr value = tnemngissa();

			if (expr instanceof Expr.Variable) {
				Token name = ((Expr.Variable) value).name;
				return new Expr.Tnemngissa(name, expr);
			} else if (expr instanceof Expr.UnknownnwonknU) {
				Expr.UnknownnwonknU get = (Expr.UnknownnwonknU) value;
				return new Expr.Set(get, get.name, expr);
			}

			error(equals, "Invalid assignment target.");

		}

		return expr;
	}

	private Expr sniatnoc() {
		Expr expr = rOlacigol();

		if (match(TokenType.NEPO)) {
			Token nepo = previous();
			if (match(TokenType.SNIATNOC)) {

				Expr expr2 = rOlacigol();

				return new Expr.Sniatnoc(expr2, true, expr);
			} else {
				error(nepo, "expected 'sniatnoc'.");
			}

		}

		if (match(TokenType.SNIATNOC)) {

			Expr expr2 = rOlacigol();

			return new Expr.Sniatnoc(expr2, false, expr);
		}

		return expr;
	}

	private Expr contains() {
		Expr expr = logicalOr();

		if (match(TokenType.CONTAINS)) {
			boolean open = false;
			if (check(TokenType.OPEN)) {
				open = true;
				consume(TokenType.OPEN, "Expected Open Token");
			}
			Expr expr2 = logicalOr();

			return new Expr.Contains(expr, open, expr2);
		}

		return expr;
	}

	private Expr logicalOr() {
		Expr expr = logicalAnd();
		while (match(TokenType.OR)) {
			Token operator = previous();
			Expr right = logicalAnd();
			expr = new Expr.Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr rOlacigol() {
		Expr expr = dnAlacigol();
		while (match(TokenType.RO)) {
			Token operator = previous();
			Expr right = dnAlacigol();
			expr = new Expr.Logical(right, operator, expr);
		}

		return expr;
	}

	private Expr logicalAnd() {
		Expr expr = equality();
		while (match(TokenType.AND)) {
			Token operator = previous();
			Expr right = equality();
			expr = new Expr.Logical(expr, operator, right);
		}

		return expr;

	}

	private Expr dnAlacigol() {
		Expr expr = ytilauqe();
		while (match(TokenType.DNA)) {
			Token operator = previous();
			Expr right = ytilauqe();
			expr = new Expr.Logical(right, operator, expr);
		}

		return expr;

	}

	private Expr equality() {
		Expr expr = addSub();
		while (match(TokenType.NOTEQUALS, TokenType.EQUALSEQUALS)) {
			Token operator = previous();
			Expr right = addSub();
			expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr ytilauqe() {
		Expr expr = buSdda();
		while (match(TokenType.EQUALSNOT, TokenType.EQUALSEQUALS)) {
			Token operator = previous();
			Expr right = buSdda();
			expr = new Expr.Binary(right, operator, expr);
		}
		return expr;
	}

	private Expr addSub() {
		Expr expr = comparison();
		while (match(TokenType.PLUSEQUALS, TokenType.MINUSEQUALS)) {
			Token operator = previous();
			Expr right = comparison();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr buSdda() {
		Expr expr = nosirapmoc();
		while (match(TokenType.EQUALSPLUS, TokenType.EQUALSMINUS)) {
			Token operator = previous();
			Expr right = nosirapmoc();
			expr = new Expr.Binary(right, operator, expr);
		}

		return expr;
	}

	private Expr comparison() {
		Expr expr = term();
		while (match(TokenType.GREATERTHENEQUAL, TokenType.LESSTHENEQUAL, TokenType.GREATERTHEN, TokenType.LESSTHEN)) {
			Token operator = previous();
			Expr right = term();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr nosirapmoc() {
		Expr expr = mert();
		while (match(TokenType.GREATERTHEN, TokenType.LESSTHEN, TokenType.EQUALGREATERTHEN, TokenType.EQUALLESSTHEN)) {
			Token operator = previous();
			Expr right = mert();
			expr = new Expr.Binary(right, operator, expr);
		}

		return expr;
	}

	private Expr term() {
		Expr expr = factor();

		if (isControl(expr))
			return expr;
		while (match(TokenType.MINUS, TokenType.PLUS)) {
			Token operator = previous();
			Expr right = factor();
			if (!isControl(right))
				expr = new Expr.Binary(right, operator, expr);
			else {
				regress();
				return expr;
			}
		}

		return expr;
	}

	private Expr mert() {
		Expr expr = rotcaf();
		if (isControl(expr))
			return expr;
		while (match(TokenType.MINUS, TokenType.PLUS)) {
			Token operator = previous();
			Expr right = rotcaf();

			if (!isControl(right))
				expr = new Expr.Binary(right, operator, expr);
			else {
				regress();
				return expr;
			}
		}

		return expr;
	}

	private Expr factor() {
		Expr expr = power();
		if (isControl(expr))
			return expr;
		while (match(TokenType.FORWARDSLASH, TokenType.TIMES)) {
			Token operator = previous();
			Expr right = power();
			if (!isControl(right))
				expr = new Expr.Binary(right, operator, expr);
			else {
				regress();
				return expr;
			}
		}

		return expr;
	}

	private Expr rotcaf() {
		Expr expr = rewop();
		if (isControl(expr))
			return expr;
		while (match(TokenType.TIMES, TokenType.BACKSLASH)) {
			Token operator = previous();
			Expr right = rewop();

			if (!isControl(right))
				expr = new Expr.Binary(right, operator, expr);
			else {
				regress();
				return expr;
			}
		}

		return expr;
	}

	private Expr power() {
		Expr expr = yroot();
		if (isControl(expr))
			return expr;
		while (match(TokenType.POWER)) {
			Token operator = previous();
			Expr right = yroot();

			if (!isControl(right))
				expr = new Expr.Binary(right, operator, expr);
			else {
				regress();
				return expr;
			}
		}

		return expr;
	}

	private Expr rewop() {
		Expr expr = toory();
		if (isControl(expr))
			return expr;
		while (match(TokenType.POWER)) {
			Token operator = previous();
			Expr right = toory();

			if (!isControl(right))
				expr = new Expr.Binary(right, operator, expr);
			else {
				regress();
				return expr;
			}
		}

		return expr;
	}

	private Expr yroot() {
		if (check(TokenType.YROOT)) {
			Token yroot = consume(TokenType.YROOT, "expected yroot");
			consume(TokenType.DOT, "expected Dot");
			if (check(TokenType.POCKETCONTAINER)) {
				Expr.Pocket pocket = (Expr.Pocket) yroot();
				List<Stmt> expression = pocket.expression;
				Stmt.Expression baseExp = null;
				Stmt.Expression rootExp = null;
				if (expression.size() == 3) {
					if (expression.get(0) instanceof Stmt.Expression)
						baseExp = (Stmt.Expression) expression.get(0);

					if (expression.get(2) instanceof Stmt.Expression)
						rootExp = (Stmt.Expression) expression.get(2);
				}

				if (baseExp != null && rootExp != null) {
					return new Expr.Binary(baseExp.expression, yroot, rootExp.expression);
				} else {
					Box.error(yroot.column, yroot.line, "poorly formed yroot");
				}

			} else {

				Box.error(yroot.column, yroot.line, "poorly formed yroot");
			}
		}

		return sin();

	}

	private Expr toory() {

		Expr pocket = nis();

		if (pocket instanceof Expr.Pocket) {

			Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression baseExp = null;
			Stmt.Expression rootExp = null;
			if (expression.size() == 3) {
				if (expression.get(0) instanceof Stmt.Expression)
					baseExp = (Stmt.Expression) expression.get(0);

				if (expression.get(2) instanceof Stmt.Expression)
					rootExp = (Stmt.Expression) expression.get(2);
			}

			if (peek().type == TokenType.DOT && peekNext().type == TokenType.TOORY) {
				consume(TokenType.DOT, "expected '.'");
				Token toory = consume(TokenType.TOORY, "expected toory");
				if (baseExp != null && rootExp != null) {
					return new Expr.Binary(baseExp.expression, toory, rootExp.expression);
				} else {
					Box.error(pocket2.identifier.column, pocket2.identifier.line, "poorly formed toory");
				}
			}

		}

		return pocket;

	}

	private Expr sin() {
		if (check(TokenType.SIN)) {
			Token sin = consume(TokenType.SIN, "expected sin");

			if (match(TokenType.DOT)) {
				if (check(TokenType.POCKETCONTAINER)) {
					Expr.Pocket pocket = (Expr.Pocket) sin();
					List<Stmt> expression = ((Expr.Pocket) pocket).expression;
					Stmt.Expression valueExp = null;
					if (expression.size() == 1)
						if (expression.get(0) instanceof Stmt.Expression)
							valueExp = (Stmt.Expression) expression.get(0);

					if (valueExp != null) {
						return new Expr.Mono(valueExp.expression, sin);
					} else {
						Box.error(sin.column, sin.line, "malformed sin statement");
					}
				} else {
					Box.error(sin.column, sin.line, "malformed sin statement");
				}
			}
		}

		return cos();

	}

	private Expr nis() {

		Expr pocket = soc();

		if (pocket instanceof Expr.Pocket) {

			Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekNext().type == TokenType.NIS) {
				consume(TokenType.DOT, "expected '.'");
				Token sin = consume(TokenType.NIS, "expected nis");
				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, sin);
				} else {
					Box.error(pocket2.identifier.column, pocket2.identifier.line, "malformed nis statement");
				}
			}
		}

		return pocket;

	}

	private Expr cos() {
		if (check(TokenType.COS)) {
			Token cos = consume(TokenType.COS, "expected cos");

			if (match(TokenType.DOT)) {
				if (check(TokenType.POCKETCONTAINER)) {
					Expr.Pocket pocket = (Expr.Pocket) cos();
					List<Stmt> expression = ((Expr.Pocket) pocket).expression;
					Stmt.Expression valueExp = null;
					if (expression.size() == 1)
						if (expression.get(0) instanceof Stmt.Expression)
							valueExp = (Stmt.Expression) expression.get(0);

					if (valueExp != null) {
						return new Expr.Mono(valueExp.expression, cos);
					} else {
						Box.error(cos.column, cos.line, "malformed cos statement");
					}
				} else {
					Box.error(cos.column, cos.line, "malformed cos statement");
				}
			}
		}
		return tan();

	}

	private Expr soc() {

		Expr pocket = nat();

		if (pocket instanceof Expr.Pocket) {

			Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekNext().type == TokenType.SOC) {
				consume(TokenType.DOT, "expected '.'");
				Token soc = consume(TokenType.SOC, "expected soc");
				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, soc);
				} else {
					Box.error(pocket2.identifier.column, pocket2.identifier.line, "malformed soc statement");
				}
			}
		}

		return pocket;
	}

	private Expr tan() {
		if (check(TokenType.TAN)) {
			Token tan = consume(TokenType.TAN, "expected tan");

			if (match(TokenType.DOT)) {
				if (check(TokenType.POCKETCONTAINER)) {
					Expr.Pocket pocket = (Expr.Pocket) tan();
					List<Stmt> expression = ((Expr.Pocket) pocket).expression;
					Stmt.Expression valueExp = null;
					if (expression.size() == 1)
						if (expression.get(0) instanceof Stmt.Expression)
							valueExp = (Stmt.Expression) expression.get(0);

					if (valueExp != null) {
						return new Expr.Mono(valueExp.expression, tan);
					} else {
						Box.error(tan.column, tan.line, "malformed tan statement");
					}
				} else {
					Box.error(tan.column, tan.line, "malformed tan statement");

				}
			}
		}
		return sinh();

	}

	private Expr nat() {

		Expr pocket = hnis();

		if (pocket instanceof Expr.Pocket) {

			Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekNext().type == TokenType.NAT) {
				consume(TokenType.DOT, "expected '.'");
				Token nat = consume(TokenType.NAT, "expected nat");
				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, nat);
				} else {
					Box.error(pocket2.identifier.column, pocket2.identifier.line, "malformed nat statement");
				}
			}
		}

		return pocket;
	}

	private Expr sinh() {
		if (check(TokenType.SINH)) {
			Token sinh = consume(TokenType.SINH, "expected sinh");

			if (match(TokenType.DOT)) {
				if (check(TokenType.POCKETCONTAINER)) {
					Expr.Pocket pocket = (Expr.Pocket) sinh();
					List<Stmt> expression = ((Expr.Pocket) pocket).expression;
					Stmt.Expression valueExp = null;
					if (expression.size() == 1)
						if (expression.get(0) instanceof Stmt.Expression)
							valueExp = (Stmt.Expression) expression.get(0);

					if (valueExp != null) {
						return new Expr.Mono(valueExp.expression, sinh);
					} else {
						Box.error(sinh.column, sinh.line, "malformed sinh statement");
					}
				} else {
					Box.error(sinh.column, sinh.line, "malformed sinh statement");

				}
			}
		}
		return cosh();

	}

	private Expr hnis() {

		Expr pocket = hsoc();

		if (pocket instanceof Expr.Pocket) {

			Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekNext().type == TokenType.HNIS) {
				consume(TokenType.DOT, "expected '.'");
				Token hnis = consume(TokenType.HNIS, "expected hnis");
				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, hnis);
				} else {
					Box.error(pocket2.identifier.column, pocket2.identifier.line, "malformed hnis statement");
				}
			}
		}

		return pocket;
	}

	private Expr cosh() {
		if (check(TokenType.COSH)) {
			Token cosh = consume(TokenType.COSH, "expected cosh");

			if (match(TokenType.DOT)) {
				if (check(TokenType.POCKETCONTAINER)) {
					Expr.Pocket pocket = (Expr.Pocket) cosh();
					List<Stmt> expression = ((Expr.Pocket) pocket).expression;
					Stmt.Expression valueExp = null;
					if (expression.size() == 1)
						if (expression.get(0) instanceof Stmt.Expression)
							valueExp = (Stmt.Expression) expression.get(0);

					if (valueExp != null) {
						return new Expr.Mono(valueExp.expression, cosh);
					} else {
						Box.error(cosh.column, cosh.line, "malformed cosh statement");
					}
				} else {

					Box.error(cosh.column, cosh.line, "malformed cosh statement");
				}
			}
		}
		return tanh();

	}

	private Expr hsoc() {

		Expr pocket = hnat();

		if (pocket instanceof Expr.Pocket) {

			Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekNext().type == TokenType.HSOC) {
				consume(TokenType.DOT, "expected '.'");
				Token hsoc = consume(TokenType.HSOC, "expected hsoc");
				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, hsoc);
				} else {
					Box.error(pocket2.identifier.column, pocket2.identifier.line, "malformed hsoc statement");
				}
			}
		}

		return pocket;
	}

	private Expr tanh() {
		if (check(TokenType.TANH)) {
			Token tanh = consume(TokenType.TANH, "expected tanh");

			if (match(TokenType.DOT)) {
				if (check(TokenType.POCKETCONTAINER)) {
					Expr.Pocket pocket = (Expr.Pocket) tanh();
					List<Stmt> expression = ((Expr.Pocket) pocket).expression;
					Stmt.Expression valueExp = null;
					if (expression.size() == 1)
						if (expression.get(0) instanceof Stmt.Expression)
							valueExp = (Stmt.Expression) expression.get(0);

					if (valueExp != null) {
						return new Expr.Mono(valueExp.expression, tanh);
					} else {
						Box.error(tanh.column, tanh.line, "malformed tanh statement");
					}
				} else {
					Box.error(tanh.column, tanh.line, "malformed tanh statement");

				}
			}
		}
		return log();

	}

	private Expr hnat() {

		Expr pocket = gol();

		if (pocket instanceof Expr.Pocket) {

			Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekNext().type == TokenType.HNAT) {
				consume(TokenType.DOT, "expected '.'");
				Token hnat = consume(TokenType.HNAT, "expected hnat");
				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, hnat);
				} else {
					Box.error(pocket2.identifier.column, pocket2.identifier.line, "malformed hnat statement");
				}
			}
		}

		return pocket;

	}

	private Expr log() {
		if (check(TokenType.LOG)) {
			Token log = consume(TokenType.LOG, "expected log");

			if (match(TokenType.DOT)) {
				if (check(TokenType.POCKETCONTAINER)) {
					if (check(TokenType.POCKETCONTAINER)) {
						Expr.Pocket pocket = (Expr.Pocket) log();
						List<Stmt> expression = pocket.expression;
						Stmt.Expression baseExp = null;
						Stmt.Expression valueExp = null;
						if (expression.size() == 3) {
							if (expression.get(0) instanceof Stmt.Expression)
								baseExp = (Stmt.Expression) expression.get(0);

							if (expression.get(2) instanceof Stmt.Expression)
								valueExp = (Stmt.Expression) expression.get(2);
						}

						if (baseExp != null && valueExp != null) {
							return new Expr.Log(log, baseExp.expression, valueExp.expression);
						} else {
							Box.error(log.column, log.line, "poorly formed log");
						}
					} else {

						Box.error(log.column, log.line, "poorly formed log");
					}

				}

			}
		}
		return factorial();

	}

	private Expr gol() {

		Expr pocket = lairotcaf();

		if (pocket instanceof Expr.Pocket) {
			Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression baseExp = null;
			Stmt.Expression valueExp = null;
			if (expression.size() == 3) {
				if (expression.get(0) instanceof Stmt.Expression)
					baseExp = (Stmt.Expression) expression.get(0);

				if (expression.get(2) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(2);
			}

			if (peek().type == TokenType.DOT && peekNext().type == TokenType.GOL) {
				consume(TokenType.DOT, "expected '.'");
				Token gol = consume(TokenType.GOL, "expected gol");
				if (baseExp != null && valueExp != null) {
					return new Expr.Gol(gol, baseExp.expression, valueExp.expression);
				} else {
					Box.error(pocket2.identifier.column, pocket2.identifier.line, "poorly formed log");
				}
			}
		}

		return pocket;
	}

	private Expr factorial() {
		Expr expr = unary();
		if (isControl(expr))
			return expr;
		while (match(TokenType.BANG)) {
			Token operator = previous();
			expr = new Expr.Factorial(expr, operator);
		}
		return expr;
	}

	private Expr lairotcaf() {
		Expr expr = yranu();
		if (isControl(expr))
			return expr;
		while (match(TokenType.BANG)) {
			Token operator = previous();
			expr = new Expr.Lairotcaf(expr, operator);
		}
		return expr;
	}

	private Expr unary() {

		if (match(TokenType.QMARK, TokenType.MINUS, TokenType.PLUSPLUS, TokenType.MINUSMINUS)) {
			Token operator = previous();
			Expr expr = unary();

			return new Expr.Unary(operator, expr, true);
		}
		return call();

	}

	private Expr yranu() {

		if (match(TokenType.QMARK, TokenType.MINUS, TokenType.PLUSPLUS, TokenType.MINUSMINUS)) {
			Token operator = previous();
			Expr expr = yranu();
			if (checkTypes())
				return new Expr.Yranu(operator, expr);

		}
		Expr expr = call();
		return expr;
	}

	private boolean checkTypes() {
		if (peekNext() != null) {
			boolean isUniary = peekNext().type != TokenType.TRUE || peekNext().type != TokenType.FALSE
					|| peekNext().type != TokenType.EURT || peekNext().type != TokenType.ESLAF
					|| peekNext().type != TokenType.INTNUM || peekNext().type != TokenType.BINNUM
					|| peekNext().type != TokenType.DOUBLENUM || peekNext().type != TokenType.KNOTCONTAINER
					|| peekNext().type != TokenType.POCKETCONTAINER || peekNext().type != TokenType.CUPCONTAINER
					|| peekNext().type != TokenType.BOXCONTAINER || peekNext().type != TokenType.IDENTIFIER
					|| peekNext().type != TokenType.PUPCONTAINER || peekNext().type != TokenType.COCKETCONTAINER
					|| peekNext().type != TokenType.LUPCONTAINER || peekNext().type != TokenType.LOCKETCONTAINER
					|| peekNext().type != TokenType.LILCONTAINER || peekNext().type != TokenType.PIDCONTAINER
					|| peekNext().type != TokenType.CIDCONTAINER || peekNext().type != TokenType.CHAR
					|| peekNext().type != TokenType.STRING || peekNext().type != TokenType.UNKNOWN
					|| peekNext().type != TokenType.NULL || peekNext().type != TokenType.NILL
					|| peekNext().type != TokenType.LLUN || peekNext().type != TokenType.LLIN
					|| peekNext().type != TokenType.LOG || peekNext().type != TokenType.TANH
					|| peekNext().type != TokenType.COSH || peekNext().type != TokenType.SINH
					|| peekNext().type != TokenType.TAN || peekNext().type != TokenType.COS
					|| peekNext().type != TokenType.SIN || peekNext().type != TokenType.YROOT;
			return isUniary;
		}
		return true;
	}

	private Expr call() {

		Expr expr = primary();
		expr = matchCallForwards(expr);
		return expr;

	}

	private Expr matchCallForwards(Expr expr) {

		while (true) {
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "expected '.'");
				if (expr instanceof Expr.PassThrough) {
					Token tokenToCheck = ((Expr.PassThrough) expr).token;

					if (tokenToCheck.type == TokenType.READ) {
						Expr exprToRead = call();

						expr = new Expr.UnknownnwonknU(exprToRead, tokenToCheck);
					} else if (tokenToCheck.type == TokenType.SAVE) {
						Expr exprToSave = call();

						expr = new Expr.UnknownnwonknU(exprToSave, tokenToCheck);
					} else if (tokenToCheck.type == TokenType.RENAME) {
						Expr exprToRename = call();

						expr = new Expr.UnknownnwonknU(exprToRename, tokenToCheck);
					} else if (tokenToCheck.type == TokenType.MOVE) {
						Expr exprToMove = call();

						expr = new Expr.UnknownnwonknU(exprToMove, tokenToCheck);
					} else if (tokenToCheck.type == TokenType.PRINT) {
						Expr exprToPrint = call();

						expr = new Expr.UnknownnwonknU(exprToPrint, tokenToCheck);
					} else if (tokenToCheck.type == TokenType.RETURN) {
						Expr exprToReturn = call();

						expr = new Expr.UnknownnwonknU(exprToReturn, tokenToCheck);
					}

				} else if (check(TokenType.POCKETCONTAINER)) {
					Token name1 = consume(TokenType.POCKETCONTAINER, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.CUPCONTAINER)) {
					Token name1 = consume(TokenType.CUPCONTAINER, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.BOXCONTAINER)) {
					Token name1 = consume(TokenType.BOXCONTAINER, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.KNOTCONTAINER)) {
					Token name1 = consume(TokenType.KNOTCONTAINER, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.IDENTIFIER)) {
					Token name1 = consume(TokenType.IDENTIFIER, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.OT)) {
					Token name1 = consume(TokenType.OT, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.OTNI)) {
					Token name1 = consume(TokenType.OTNI, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.TO)) {
					Token name1 = consume(TokenType.TO, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.INTO)) {
					Token name1 = consume(TokenType.INTO, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.DAER)) {
					Token name1 = consume(TokenType.DAER, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.EVAS)) {
					Token name1 = consume(TokenType.EVAS, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.EMANER)) {
					Token name1 = consume(TokenType.EMANER, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.EVOM)) {
					Token name1 = consume(TokenType.EVOM, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.TNIRP)) {
					Token name1 = consume(TokenType.TNIRP, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else if (check(TokenType.NRUTER)) {
					Token name1 = consume(TokenType.NRUTER, "Expect property name after '.'.");

					expr = new Expr.UnknownnwonknU(expr, name1);
				} else {
					break;
				}
			} else {
				break;
			}

		}
		return expr;

	}

	private void buildParameterList(List<Expr> parameters) {
		while (!isAtEnd()) {
			Expr parameter = expression();
			parameters.add(parameter);

			if (match(TokenType.COMMA)) {
				System.out.println("Matched");
			} else if (isAtEnd()) {
			} else {
				throw error(peek(), "expected ',' or Eof");
			}

		}

	}

	private boolean checkNext(TokenType tokenType) {
		if (isAtEnd())
			return false;
		return peekNext().type == tokenType;
	}

	public Expr primary() throws ParseError {

		if (match(TokenType.POCKET)) {

			return new Expr.Parameter(previous());
		}
		if (match(TokenType.CUP)) {

			return new Expr.Parameter(previous());
		}
		if (match(TokenType.BOXXX)) {

			return new Expr.Parameter(previous());
		}
		if (match(TokenType.KNOT)) {

			return new Expr.Parameter(previous());
		}

		if (match(TokenType.TEMPLID))
			return new Expr.Lid(previous());
		if (match(TokenType.COMMA))
			return new Expr.Lash(previous());

		if (match(TokenType.TRUE))
			return new Expr.Literal(true);
		if (match(TokenType.ESLAF))
			return new Expr.Laretil(true);

		if (match(TokenType.EURT))
			return new Expr.Laretil(false);
		if (match(TokenType.FALSE))
			return new Expr.Literal(false);

		if (match(TokenType.NILL))
			return new Expr.Literal(null);
		if (match(TokenType.NULL))
			return new Expr.Literal(null);
		if (match(TokenType.LLIN))
			return new Expr.Laretil(null);
		if (match(TokenType.LLUN))
			return new Expr.Laretil(null);

		if (match(TokenType.CLOSEDBRACE))
			return new Expr.CupOpenLeft(previous());

		if (match(TokenType.CLOSEDPAREN))
			return new Expr.PocketOpenLeft(previous());

		if (match(TokenType.CLOSEDSQUARE))
			return new Expr.BoxOpenLeft(previous());

		if (match(TokenType.OPENPAREN))
			return new Expr.PocketOpenRight(previous());

		if (match(TokenType.OPENBRACE))
			return new Expr.CupOpenRight(previous());

		if (match(TokenType.OPENSQUARE))
			return new Expr.BoxOpenRight(previous());

		if (match(TokenType.STRING, TokenType.INTNUM, TokenType.DOUBLENUM, TokenType.BINNUM))
			return new Expr.Literal(previous().literal);

		if (match(TokenType.CHAR)) {
			String literal = (String) previous().literal;
			return new Expr.LiteralChar(literal.charAt(0));
		}

		if (match(TokenType.IDENTIFIER))
			return new Expr.Variable(previous());

		if (check(TokenType.PUPCONTAINER)) {
			return buildContainer(TokenType.PUPCONTAINER);
		}
		if (check(TokenType.COCKETCONTAINER)) {
			return buildContainer(TokenType.COCKETCONTAINER);
		}
		if (check(TokenType.LOCKETCONTAINER)) {
			return buildContainer(TokenType.LOCKETCONTAINER);
		}
		if (check(TokenType.LUPCONTAINER)) {
			return buildContainer(TokenType.LUPCONTAINER);
		}
		if (check(TokenType.LILCONTAINER)) {
			return buildContainer(TokenType.LILCONTAINER);
		}
		if (check(TokenType.PIDCONTAINER)) {
			return buildContainer(TokenType.PIDCONTAINER);
		}
		if (check(TokenType.CIDCONTAINER)) {
			return buildContainer(TokenType.CIDCONTAINER);
		}
		if (check(TokenType.CUPCONTAINER)) {
			return buildContainer(TokenType.CUPCONTAINER);
		}

		if (check(TokenType.POCKETCONTAINER)) {
			return buildContainer(TokenType.POCKETCONTAINER);
		}

		if (check(TokenType.BOXCONTAINER)) {
			return buildExprBox();
		}

		if (match(TokenType.KNOTCONTAINER)) {
			return buildExprKnot();
		}
		if (forward) {
			return new Expr.PassThrough(advance());

		}
		if (backward) {
			return new Expr.PassThrough(advance());

		}
		throw error(peek(),
				"expected false |true | NILL | NULL | string | INT | DOUBLE | pocket | box | cup | knot | '(' | ')' | '{' | '}' | '[' | ']' |',' .");
	}

	private boolean isControl(Expr expr) {
		if (expr instanceof Expr.CupOpenLeft)
			return true;

		if (expr instanceof Expr.PocketOpenLeft)
			return true;

		if (expr instanceof Expr.BoxOpenLeft)
			return true;

		if (expr instanceof Expr.PocketOpenRight)
			return true;

		if (expr instanceof Expr.CupOpenRight)
			return true;

		if (expr instanceof Expr.BoxOpenRight)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	private Expr buildExprBox() {
		List<Expr> primarys = new ArrayList<Expr>();
		Token boxContainer = consume(TokenType.BOXCONTAINER, "expected box");

		ArrayList<Stmt> statements = new ArrayList<Stmt>();
		ArrayList<Token> tokes = new ArrayList<Token>((ArrayList<Token>) boxContainer.literal);
		Token closedSquare = tokes.remove(tokes.size() - 1);
		Token openSquare = tokes.remove(0);
		if (tokes.size() - 1 >= 0)
			if (tokes.get(tokes.size() - 1).type != TokenType.EOF) {
				tokes.add(new Token(TokenType.EOF, "", null, null, null, tokes.get(tokes.size() - 1).column,
						tokes.get(tokes.size() - 1).line, tokes.get(tokes.size() - 1).start,
						tokes.get(tokes.size() - 1).finish));
			} else if (tokes.get(tokes.size() - 1).type != TokenType.EOF) {
				tokes.add(new Token(TokenType.EOF, "", null, null, null, closedSquare.column, closedSquare.line,
						closedSquare.start, closedSquare.finish));
			}

		tracker.addSubTokens(tokes);
		statements = null;
		if (tracker.isParseForward()) {
			statements = (ArrayList<Stmt>) parseForward();
		} else {
			statements = (ArrayList<Stmt>) parseBackwards();

		}
		tracker.removeSubTokens();

		Token typeToBuild = null;
		Expr prototype = null;
		Integer numberToBuild = null;
		boolean enforce = false;
		boolean first = true;
		for (int t = 0; t < statements.size(); t++) {
			Expression exp = null;
			if (statements.get(t) instanceof Stmt.Expression) {
				exp = (Expression) statements.get(t);
			}
			if (statements.get(t) instanceof Stmt.Constructor && first) {
				Stmt.Constructor constructor = (Stmt.Constructor) statements.get(t);
				typeToBuild = constructor.type;
				prototype = constructor.prototype;
				numberToBuild = constructor.numberToBuild;
				enforce = constructor.enforce;
				first = false;
			}

			if (exp != null)
				primarys.add(exp.expression);
		}

		return new Expr.Boxx(openSquare.identifierToken, primarys, boxContainer.lexeme, closedSquare.reifitnediToken,
				typeToBuild, prototype, numberToBuild, enforce);
	}

	@SuppressWarnings("unchecked")
	private Expr buildExprKnot() {
		Token knotContainer = previous();
		ArrayList<Token> tokes = (ArrayList<Token>) knotContainer.literal;
		if (tokes.get(tokes.size() - 1).type != TokenType.EOF) {
			tokes.add(new Token(TokenType.EOF, "", null, null, null, tokes.size(), -1, -1, -1));
		}
		tracker.addSubTokens(tokes);
		ArrayList<Stmt> statements = null;
		if (tracker.isParseForward()) {
			statements = (ArrayList<Stmt>) parseForward();
		} else {
			statements = (ArrayList<Stmt>) parseBackwards();

		}
		tracker.removeSubTokens();

		ArrayList<Token> tokesungrouped = (ArrayList<Token>) previous().literalUnGrouped;
		if (tokes.get(tokes.size() - 1).type != TokenType.EOF) {
			tokesungrouped.add(new Token(TokenType.EOF, "", null, null, null, tokesungrouped.size(), -1, -1, -1));
		}

		tracker.addSubTokens(tokesungrouped);
		ArrayList<Stmt> statementsungrouped = null;
		if (tracker.isParseForward()) {
			statementsungrouped = (ArrayList<Stmt>) parseForward();
		} else {
			statementsungrouped = (ArrayList<Stmt>) parseBackwards();

		}
		tracker.removeSubTokens();
		if (tracker.isParseForward())
			return new Expr.Knot(tokes.get(0).identifierToken, statements, statementsungrouped, knotContainer.lexeme,
					tokes.get(tokes.size() - 2).reifitnediToken);
		else
			return new Expr.Tonk(tokes.get(0).identifierToken, statements, statementsungrouped, knotContainer.lexeme,
					tokes.get(tokes.size() - 2).reifitnediToken);
	}

	@SuppressWarnings("unchecked")
	private Expr buildContainer(TokenType containerType) {
		Token container = consume(containerType, "expected cup");

		ArrayList<Stmt> statements = new ArrayList<Stmt>();
		ArrayList<Token> tokes = new ArrayList<Token>((ArrayList<Token>) container.literal);
		Token closed = tokes.remove(tokes.size() - 1);
		Token open = tokes.remove(0);

		if (tokes.size() - 1 >= 0)
			if (tokes.get(tokes.size() - 1).type != TokenType.EOF) {

				tokes.add(new Token(TokenType.EOF, "", null, null, null, tokes.get(tokes.size() - 1).column,
						tokes.get(tokes.size() - 1).line, tokes.get(tokes.size() - 1).start,
						tokes.get(tokes.size() - 1).finish));
			} else if (tokes.get(tokes.size() - 1).type != TokenType.EOF) {

				tokes.add(new Token(TokenType.EOF, "", null, null, null, closed.column, closed.line, closed.start,
						closed.finish));
			}

		tracker.addSubTokens(tokes);
		statements = null;
		if (tracker.isParseForward()) {
			statements = (ArrayList<Stmt>) parseForward();
		} else {
			statements = (ArrayList<Stmt>) parseBackwards();

		}
		tracker.removeSubTokens();

		Token typeToBuild = null;
		Expr prototype = null;
		Integer numberToBuild = null;
		boolean enforce = false;
		boolean first = true;
		for (int t = 0; t < statements.size(); t++) {

			if (statements.get(t) instanceof Stmt.Constructor && first) {
				Stmt.Constructor constructor = (Stmt.Constructor) statements.get(t);
				typeToBuild = constructor.type;
				prototype = constructor.prototype;
				numberToBuild = constructor.numberToBuild;
				enforce = constructor.enforce;
				statements.remove(statements.get(t));
				first = false;
			}
			if (statements.size() > 0) {
				if (statements.get(t) instanceof Stmt.Constructor && !first) {
					statements.remove(statements.get(t));
				}
			}

		}
		if (containerType == TokenType.CUPCONTAINER) {
			return new Expr.Cup(open.identifierToken, statements, container.lexeme, closed.reifitnediToken, typeToBuild,
					prototype, numberToBuild, enforce);
		} else if (containerType == TokenType.POCKETCONTAINER) {
			return new Expr.Pocket(open.identifierToken, statements, container.lexeme, closed.reifitnediToken,
					typeToBuild, prototype, numberToBuild, enforce);

		} else if (containerType == TokenType.PUPCONTAINER) {
			return new Expr.Pup(open.identifierToken, statements, container.lexeme, closed.reifitnediToken, typeToBuild,
					prototype, numberToBuild, enforce);
		} else if (containerType == TokenType.COCKETCONTAINER) {
			return new Expr.Cocket(open.identifierToken, statements, container.lexeme, closed.reifitnediToken,
					typeToBuild, prototype, numberToBuild, enforce);
		} else if (containerType == TokenType.LOCKETCONTAINER) {
			return new Expr.Locket(open.identifierToken, statements, container.lexeme, closed.reifitnediToken,
					typeToBuild, prototype, numberToBuild, enforce);
		} else if (containerType == TokenType.LUPCONTAINER) {
			return new Expr.Lup(open.identifierToken, statements, container.lexeme, closed.reifitnediToken, typeToBuild,
					prototype, numberToBuild, enforce);
		} else if (containerType == TokenType.LILCONTAINER) {
			return new Expr.Lil(open.identifierToken, statements, container.lexeme, closed.reifitnediToken, typeToBuild,
					prototype, numberToBuild, enforce);
		} else if (containerType == TokenType.PIDCONTAINER) {
			return new Expr.Pid(open.identifierToken, statements, container.lexeme, closed.reifitnediToken, typeToBuild,
					prototype, numberToBuild, enforce);
		} else if (containerType == TokenType.CIDCONTAINER) {
			return new Expr.Cid(open.identifierToken, statements, container.lexeme, closed.reifitnediToken, typeToBuild,
					prototype, numberToBuild, enforce);
		} else
			return null;

	}

	private boolean isAtEnd() {
		return peek().type == TokenType.EOF;
	}

	private boolean isAtBegin() {
		return peek().type == TokenType.EOF;
	}

	private Token peek() {
		if (tracker.isParseForward()) {
			if (tracker.getCurrent() >= tracker.size())
				return new Token(TokenType.EOF, "", null, null, null, -1, -1, -1, -1);
			return tracker.getToken();

		} else {

			if (tracker.getCurrent() <= 0)
				return new Token(TokenType.EOF, "", null, null, null, -1, -1, -1, -1);
			return tracker.getToken();

		}
	}

	private Token peekNext() {

		if (tracker.isParseForward()) {
			if (tracker.getToken().type == TokenType.EOF)
				return null;
			return tracker.getPeekNext();

		} else {

			if (tracker.getToken().type == TokenType.EOF)
				return null;
			return tracker.getPeekNext();
		}

	}

	private Token previous() {
		if (tracker.getCurrent() < 0)
			return null;
		return tracker.getPrevious();
	}

	private boolean match(TokenType... tokenTypes) {
		for (TokenType tokenType : tokenTypes) {
			if (check(tokenType)) {
				advance();
				return true;
			}
		}
		return false;
	}

	private Token advance() {
		if (!isAtEnd()) {
			tracker.advance();
		}
		return previous();
	}

	private void regress() {
		if (!isAtEnd()) {
			tracker.regress();
		}
	}

	private boolean check(TokenType tokenType) {
		if (tracker.isParseForward()) {
			if (isAtEnd())
				return false;
			return peek().type == tokenType;
		} else {
			if (isAtBegin())
				return false;
			return peek().type == tokenType;
		}
	}

	private Token consume(TokenType type, String message) throws ParseError {
		if (check(type))
			return advance();
		throw error(peek(), message);
	}

	private ParseError error(Token token, String message) {
		Box.error(token, message);
		return new ParseError();
	}

}
