package Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Box.Box.Box;
import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Expr.Teg;
import Parser.Stmt.Fi;

public class ParserTest {

	private static class ParseError extends RuntimeException {
		private static final long serialVersionUID = 2715202794403784452L;
	}

	TokensToTrack tracker;

	@SuppressWarnings("javadoc")
	public ParserTest(List<Token> tokens, boolean forward, boolean backward) {
		tracker = new TokensToTrack((ArrayList<Token>) tokens, 0);
	}

	@SuppressWarnings("javadoc")
	public List<List<Declaration>> parse() {

		List<Declaration> forwardStmt = parseForward();
		List<List<Declaration>> statements = new ArrayList<List<Declaration>>();
		statements.add(forwardStmt);
		return statements;

	}

	private List<Declaration> parseForward() {
		List<Declaration> statements = new ArrayList<>();
		tracker.parseForward();
		while (!isAtEnd()) {
			statements.add(declaration());
		}
		return statements;
	}

	private Declaration declaration() {
		if (checkFunctionDeclaration()) {
			return new Declaration.FunDecl(function());
		} else {
			Declaration.StmtDecl stmtDecl = new Declaration.StmtDecl(statement());
			return stmtDecl;
		}
	}

	private Fun function() {
		if (check(TokenType.FUN)) {
			consume(TokenType.FUN, "fun");
			consume(TokenType.DOT, "fun dot");
			Token forwaredIdent = consume(TokenType.IDENTIFIER, "fun forwardIdent");
			consume(TokenType.DOT, "fun dot");
			consume(TokenType.OPENSQUARE, "fun forward square open");

			ArrayList<Token> typesForward = new ArrayList<>();
			ArrayList<Token> identsForward = new ArrayList<>();
			if (matchTypeEpyt()) {
				typesForward.add(previous());
				identsForward.add(consume(TokenType.IDENTIFIER, ""));
				while (match(TokenType.COMMA)) {
					if (matchTypeEpyt()) {
						typesForward.add(previous());
						identsForward.add(consume(TokenType.IDENTIFIER, ""));
					} else {
						throw error(previous(), "Malformed forward parameters", true);
					}
				}
				consume(TokenType.CLOSEDSQUARE, "");
				consume(TokenType.DOT, "fun dot");
				Expr expression = expressionnoisserpxe();
				if (expression instanceof Expr.Cup || expression instanceof Expr.Knot
						|| expression instanceof Expr.Tonk) {
					if (check(TokenType.DOT)) {
						ArrayList<Token> typesBackward = new ArrayList<>();
						ArrayList<Token> identsBackward = new ArrayList<>();
						consume(TokenType.DOT, "fun dot");
						consume(TokenType.OPENSQUARE, "fun forward square open");
						if (match(TokenType.IDENTIFIER)) {
							identsBackward.add(previous());
							if (matchTypeEpyt())
								typesBackward.add(previous());
							else
								throw error(previous(), "weerere", true);
							while (match(TokenType.COMMA)) {
								if (match(TokenType.IDENTIFIER)) {
									identsBackward.add(previous());
									if (matchTypeEpyt())
										typesBackward.add(previous());
									else
										throw error(previous(), "weefhgghrere", true);
								} else {
									throw error(previous(), "Malformed forward parameters", true);
								}
							}
							consume(TokenType.CLOSEDSQUARE, "");
							consume(TokenType.DOT, "fun dot");
							Token backIdent = consume(TokenType.IDENTIFIER, "fun dot");
							consume(TokenType.DOT, "fun dot");

							consume(TokenType.NUF, "nuf ");
							return new Fun.Function(forwaredIdent, typesForward, identsForward, expression,
									typesBackward, identsBackward, backIdent);
						}

					} else {
						return new Fun.Function(forwaredIdent, typesForward, identsForward, expression, null, null,
								null);
					}
				}

			}
			throw error(previous(), "malformed function", true);
		} else {
			Expr expression = expressionnoisserpxe();
			if (expression instanceof Expr.Cup || expression instanceof Expr.Knot || expression instanceof Expr.Tonk) {
				if (check(TokenType.DOT)) {
					ArrayList<Token> typesBackward = new ArrayList<>();
					ArrayList<Token> identsBackward = new ArrayList<>();
					consume(TokenType.DOT, "fun dot");
					consume(TokenType.OPENSQUARE, "fun forward square open");
					if (match(TokenType.IDENTIFIER)) {
						identsBackward.add(previous());
						if (matchTypeEpyt())
							typesBackward.add(previous());
						else
							throw error(previous(), "weerere", true);
						while (match(TokenType.COMMA)) {
							if (match(TokenType.IDENTIFIER)) {
								identsBackward.add(previous());
								if (matchTypeEpyt())
									typesBackward.add(previous());
								else
									throw error(previous(), "weefhgghrere", true);
							} else {
								throw error(previous(), "Malformed forward parameters", true);
							}
						}
						consume(TokenType.CLOSEDSQUARE, "");
						consume(TokenType.DOT, "fun dot");
						Token backIdent = consume(TokenType.IDENTIFIER, "fun dot");
						consume(TokenType.DOT, "fun dot");

						consume(TokenType.NUF, "nuf ");
						return new Fun.Function(null, null, null, expression, typesBackward, identsBackward, backIdent);
					}

				} else {
					throw error(previous(), "malformed function", true);
				}
			}
			throw error(previous(), "malformed function", true);
		}
	}

	private boolean matchTypeEpyt() {
		return match(TokenType.BOX, TokenType.POCKET, TokenType.CUP, TokenType.KNOT, TokenType.XOB, TokenType.TEKCOP,
				TokenType.PUC, TokenType.TONK);
	}

	private boolean checkFunctionDeclaration() {
		if (peekI(0).type == TokenType.FUN) {
			return true;
		}

		Stack<TokenType> parenStack = new Stack<>();
		Stack<TokenType> braceStack = new Stack<>();
		int count = 0;
		if (peekI(count).type == TokenType.OPENPAREN)
			parenStack.push(peekI(count).type);
		else if (peekI(count).type == TokenType.OPENBRACE)
			braceStack.push(peekI(count).type);
		count++;

		while ((parenStack.size() > 0 || braceStack.size() > 0) && tracker.getCurrent() + count < tracker.size()) {
			if (peekI(count).type == TokenType.OPENPAREN)
				parenStack.push(peekI(count).type);
			else if (peekI(count).type == TokenType.OPENBRACE)
				braceStack.push(peekI(count).type);
			else if (peekI(count).type == TokenType.CLOSEDPAREN) {
				if (parenStack.size() > 0) {
					parenStack.pop();
				}
			} else if (peekI(count).type == TokenType.CLOSEDBRACE) {
				if (braceStack.size() > 0) {

					braceStack.pop();
				}
			}
			count++;
		}

		if (parenStack.size() != 0 || braceStack.size() != 0 || tracker.getCurrent() + count >= tracker.size())
			return false;

		if (peekI(count).type != TokenType.DOT)
			return false;
		else
			count++;
		if (peekI(count).type != TokenType.OPENSQUARE)
			return false;

		do {
			count++;
			if (peekI(count).type != TokenType.IDENTIFIER)
				return false;
			count++;
			if (peekI(count).type != TokenType.BOX && peekI(count).type != TokenType.POCKET
					&& peekI(count).type != TokenType.CUP && peekI(count).type != TokenType.XOB
					&& peekI(count).type != TokenType.TEKCOP && peekI(count).type != TokenType.PUC
					&& peekI(count).type != TokenType.KNOT && peekI(count).type != TokenType.TONK)
				return false;

			count++;
		} while (peekI(count).type == TokenType.COMMA);

		if (peekI(count).type != TokenType.CLOSEDSQUARE)
			return false;
		else
			count++;
		if (peekI(count).type != TokenType.DOT)
			return false;
		else
			count++;
		if (peekI(count).type != TokenType.IDENTIFIER)
			return false;
		count++;
		if (peekI(count).type != TokenType.DOT)
			return false;
		else
			count++;
		if (peekI(count).type != TokenType.NUF)
			return false;
		else
			return true;
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

	private Token peekI(int index) {
		if (tracker.isParseForward()) {
			if (tracker.currentIndex() + index >= tracker.size())
				return new Token(TokenType.EOF, "", null, null, null, -1, -1, -1, -1);
			return tracker.getToken(index);

		} else {

			if (tracker.getCurrent() - index <= 0)
				return new Token(TokenType.EOF, "", null, null, null, -1, -1, -1, -1);
			return tracker.getToken(index);

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
		throw error(peek(), message, true);
	}

	private Stmt statement() {
		if (check(TokenType.MOVE))
			return move();
		if (check(TokenType.RENAME))
			return rename();
		if (check(TokenType.READ))
			return read();
		if (check(TokenType.SAVE))
			return save();
		if (check(TokenType.PRINT))
			return print();
		if (check(TokenType.RETURN))
			return returnStmt();
		if (checkTnirp())
			return tnirp();
		if (checkNruter())
			return nruter();
		if (checkEvas())
			return evas();
		if (checkDaer())
			return daer();
		if (checkEmaner())
			return emaner();
		if (checkEvom())
			return evom();
		if (checkIf())
			return ifStmt();
		if (checkFi())
			return fiStmt();
		if (checkExpel())
			return expel();
		if (checkConsume())
			return consume();
		if (checkVar())
			return var();
		if (checkRav())
			return rav();

		return exprStmt();

	}

	private Stmt exprStmt() {

		Expr expression = null;
		Expr noisserpxe = null;

		expression = expressionnoisserpxe();

		return new Stmt.Expression(expression, noisserpxe);
	}

	private boolean checkRav() {
		if (peekI(0).type == TokenType.IDENTIFIER) {
			if (peekI(1).type == TokenType.ASIGNMENTEQUALS) {
				if (peekI(2).type == TokenType.IDENTIFIER) {
					if (peekI(3).type == TokenType.INTNUM) {

						if (peekI(4).type == TokenType.XOB || peekI(4).type == TokenType.PUC
								|| peekI(4).type == TokenType.TEKCOP || peekI(4).type == TokenType.TONK) {
							return true;
						}
					} else if (peekI(3).type == TokenType.XOB || peekI(3).type == TokenType.PUC
							|| peekI(3).type == TokenType.TEKCOP || peekI(3).type == TokenType.TONK) {
						return true;
					}
				}
			}
		} else if (peekI(0).type == TokenType.OPENSQUARE) {
			Stack<TokenType> stack = new Stack<>();
			int count = 0;
			stack.push(peekI(count).type);
			while (stack.size() > 0 || tracker.getCurrent() + count < tracker.size()) {
				if (peekI(count).type == TokenType.OPENSQUARE)
					stack.push(peekI(count).type);
				else if (peekI(count).type == TokenType.CLOSEDSQUARE)
					stack.pop();
				count++;
			}
			if (stack.size() != 0 || tracker.getCurrent() + count >= tracker.size())
				return false;
			if (peekI(count).type == TokenType.ASIGNMENTEQUALS) {
				count++;
				if (peekI(count).type == TokenType.IDENTIFIER) {
					count++;
					if (peekI(count).type == TokenType.INTNUM) {
						count++;
						if (peekI(count).type == TokenType.XOB || peekI(count).type == TokenType.PUC
								|| peekI(count).type == TokenType.TEKCOP || peekI(count).type == TokenType.TONK) {
							return true;
						}
					} else if (peekI(count).type == TokenType.XOB || peekI(count).type == TokenType.PUC
							|| peekI(count).type == TokenType.TEKCOP || peekI(count).type == TokenType.TONK) {
						return true;
					}
				}
			}
		} else if (peekI(0).type == TokenType.OPENPAREN || peekI(0).type == TokenType.OPENBRACE) {
			Stack<TokenType> stack = new Stack<>();
			stack.push(peekI(0).type);
			int count = 1;
			while (stack.size() > 0 || tracker.getCurrent() + count < tracker.size()) {
				if (peekI(count).type == TokenType.OPENPAREN || peekI(count).type == TokenType.OPENBRACE)
					stack.push(peekI(count).type);
				else if (peekI(count).type == TokenType.CLOSEDPAREN || peekI(count).type == TokenType.CLOSEDBRACE)
					if (stack.size() > 0)
						stack.pop();
				count++;
			}
			if (stack.size() != 0 || tracker.getCurrent() + count >= tracker.size())
				return false;
			if (peekI(count).type == TokenType.ASIGNMENTEQUALS) {
				count++;
				if (peekI(count).type == TokenType.IDENTIFIER) {
					count++;
					if (peekI(count).type == TokenType.INTNUM) {
						count++;
						if (peekI(count).type == TokenType.XOB || peekI(count).type == TokenType.PUC
								|| peekI(count).type == TokenType.TEKCOP || peekI(count).type == TokenType.TONK) {
							return true;
						}
					} else if (peekI(count).type == TokenType.XOB || peekI(count).type == TokenType.PUC
							|| peekI(count).type == TokenType.TEKCOP || peekI(count).type == TokenType.TONK) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean checkVar() {
		if (check(TokenType.BOX) || check(TokenType.CUP) || check(TokenType.POCKET) || check(TokenType.KNOT))
			return true;
		return false;
	}

	private boolean checkConsume() {
		Stack<TokenType> stack = new Stack<>();
		CountObject count = new CountObject(0);
		if (peekI(count.get()).type == TokenType.OPENSQUARE) {
			stack.push(peekI(count.get()).type);
			count.add();
			while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
				if (peekI(count.get()).type == TokenType.OPENSQUARE)
					stack.push(peekI(count.get()).type);
				else if (peekI(count.get()).type == TokenType.CLOSEDSQUARE)
					stack.pop();
				count.add();
			}
			if (stack.size() != 0 || tracker.getCurrent() + count.get() >= tracker.size())
				return false;
			if (peekI(count.get()).type == TokenType.CONSUME) {
				count.add();
				if (peekI(count.get()).type == TokenType.OPENPAREN) {
					count.add();
					if (peekI(count.get()).type == TokenType.STRING) {
						count.add();
						if (peekI(count.get()).type == TokenType.CLOSEDPAREN) {
							return true;
						}
					}
				}
			} else
				return false;

		} else if (peekI(count.get()).type == TokenType.IDENTIFIER) {
			count.add();
			if (peekI(count.get()).type == TokenType.CONSUME) {
				count.add();
				if (peekI(count.get()).type == TokenType.OPENPAREN) {
					count.add();
					if (peekI(count.get()).type == TokenType.STRING) {
						count.add();
						if (peekI(count.get()).type == TokenType.CLOSEDPAREN) {
							return true;
						}
					}
				}
			} else
				return false;
		}
		return false;
	}

	private boolean checkExpel() {
		Stack<TokenType> stack = new Stack<>();
		CountObject count = new CountObject(0);
		if (peekI(count.get()).type == TokenType.OPENSQUARE) {
			stack.push(peekI(count.get()).type);
			count.add();
			while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
				if (peekI(count.get()).type == TokenType.OPENSQUARE)
					stack.push(peekI(count.get()).type);
				else if (peekI(count.get()).type == TokenType.CLOSEDSQUARE)
					stack.pop();
				count.add();
			}
			if (stack.size() != 0 || tracker.getCurrent() + count.get() >= tracker.size())
				return false;
			if (peekI(count.get()).type == TokenType.EXPELL) {
				count.add();
				if (peekI(count.get()).type == TokenType.OPENPAREN) {
					count.add();
					if (peekI(count.get()).type == TokenType.STRING) {
						count.add();
						if (peekI(count.get()).type == TokenType.CLOSEDPAREN) {
							return true;
						}
					}
				}
			} else
				return false;

		} else if (peekI(count.get()).type == TokenType.IDENTIFIER) {
			count.add();
			if (peekI(count.get()).type == TokenType.EXPELL) {
				count.add();
				if (peekI(count.get()).type == TokenType.OPENPAREN) {
					count.add();
					if (peekI(count.get()).type == TokenType.STRING) {
						count.add();
						if (peekI(count.get()).type == TokenType.CLOSEDPAREN) {
							return true;
						}
					}
				}
			} else
				return false;
		}
		return false;
	}

	private boolean checkFi() {
		Stack<TokenType> stack = new Stack<>();
		CountObject count = new CountObject(0);
		boolean toreturn = false;
		if (peekI(count.get()).type == TokenType.OPENBRACE) {
			toreturn = checkEsle(stack, count);
		}

		if (peekI(count.get()).type == TokenType.DOT) {
			count.add();
		} else
			return false;

		while (peekI(count.get()).type == TokenType.OPENBRACE) {
			toreturn = checkEsleFi(stack, count);
		}
		return toreturn;
	}

	private boolean checkEsleFi(Stack<TokenType> stack, CountObject count) {

		stack.push(peekI(count.get()).type);
		count.add();
		while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
			if (peekI(count.get()).type == TokenType.OPENBRACE)
				stack.push(peekI(count.get()).type);
			else if (peekI(count.get()).type == TokenType.CLOSEDBRACE)
				stack.pop();
			count.add();
		}
		if (peekI(count.get()).type == TokenType.DOT) {
			count.add();
		} else
			return false;
		if (peekI(count.get()).type == TokenType.OPENPAREN) {
			stack.push(peekI(count.get()).type);
			count.add();
			while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
				if (peekI(count.get()).type == TokenType.OPENPAREN)
					stack.push(peekI(count.get()).type);
				else if (peekI(count.get()).type == TokenType.CLOSEDPAREN)
					stack.pop();
				count.add();
			}
			if (stack.size() != 0 || tracker.getCurrent() + count.get() >= tracker.size())
				return false;

		}
		return true;
	}

	private boolean checkEsle(Stack<TokenType> stack, CountObject count) {
		if (peekI(count.get()).type == TokenType.OPENBRACE) {
			stack.push(peekI(count.get()).type);
			count.add();
			while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
				if (peekI(count.get()).type == TokenType.OPENBRACE)
					stack.push(peekI(count.get()).type);
				else if (peekI(count.get()).type == TokenType.CLOSEDBRACE)
					stack.pop();
				count.add();
			}
			if (stack.size() == 0 && tracker.getCurrent() + count.get() < tracker.size())
				return true;
		}
		return false;
	}

	private static class CountObject {
		int count;

		public CountObject(int cnt) {
			count = cnt;
		}

		public void add() {
			count++;
		}

		public int get() {
			return count;
		}
	}

	private boolean checkIf() {
		Stack<TokenType> stack = new Stack<>();
		CountObject count = new CountObject(0);
		boolean toreturn = false;
		while (peekI(count.get()).type == TokenType.OPENPAREN) {
			toreturn = checkIfElse(stack, count);
		}
		if (peekI(count.get()).type == TokenType.DOT) {
			count.add();
			if (peekI(count.get()).type == TokenType.OPENBRACE) {
				toreturn = checkElse(stack, count);
			}
		}

		return toreturn;
	}

	private boolean checkElse(Stack<TokenType> stack, CountObject count) {
		if (peekI(count.get()).type == TokenType.OPENBRACE) {
			stack.push(peekI(count.get()).type);
			while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
				if (peekI(count.get()).type == TokenType.OPENBRACE)
					stack.push(peekI(count.get()).type);
				else if (peekI(count.get()).type == TokenType.CLOSEDBRACE)
					stack.pop();
				count.add();
			}
			if (stack.size() == 0 && tracker.getCurrent() + count.get() < tracker.size())
				return true;
		}
		return false;

	}

	private boolean checkIfElse(Stack<TokenType> stack, CountObject count) {
		if (peekI(count.get()).type == TokenType.OPENPAREN) {
			stack.push(peekI(count.get()).type);
			count.add();
			while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
				if (peekI(count.get()).type == TokenType.OPENPAREN)
					stack.push(peekI(count.get()).type);
				else if (peekI(count.get()).type == TokenType.CLOSEDPAREN)
					stack.pop();
				count.add();
			}

			if (peekI(count.get()).type == TokenType.DOT) {
				count.add();
			} else
				return false;
			if (peekI(count.get()).type == TokenType.OPENBRACE) {
				stack.push(peekI(count.get()).type);
				count.add();
				while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
					if (peekI(count.get()).type == TokenType.OPENBRACE)
						stack.push(peekI(count.get()).type);
					else if (peekI(count.get()).type == TokenType.CLOSEDBRACE)
						stack.pop();
					count.add();
				}
				if (stack.size() != 0 || tracker.getCurrent() + count.get() >= tracker.size())
					return false;

			}
			return true;

		}
		return false;

	}

	private boolean checkEvom() {
		if (peekI(0).type == TokenType.OPENPAREN) {
			if (peekI(1).type == TokenType.STRING) {
				if (peekI(2).type == TokenType.CLOSEDPAREN) {
					if (peekI(3).type == TokenType.DOT) {
						if (peekI(4).type == TokenType.OT) {
							if (peekI(5).type == TokenType.DOT) {
								if (peekI(6).type == TokenType.OPENPAREN) {
									if (peekI(7).type == TokenType.STRING) {
										if (peekI(8).type == TokenType.CLOSEDPAREN) {
											if (peekI(9).type == TokenType.DOT) {
												if (peekI(10).type == TokenType.EVOM) {
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkEmaner() {
		if (peekI(0).type == TokenType.OPENPAREN) {
			if (peekI(1).type == TokenType.STRING) {
				if (peekI(2).type == TokenType.CLOSEDPAREN) {
					if (peekI(3).type == TokenType.DOT) {
						if (peekI(4).type == TokenType.OT) {
							if (peekI(5).type == TokenType.DOT) {
								if (peekI(6).type == TokenType.OPENPAREN) {
									if (peekI(7).type == TokenType.STRING) {
										if (peekI(8).type == TokenType.CLOSEDPAREN) {
											if (peekI(9).type == TokenType.DOT) {
												if (peekI(10).type == TokenType.EMANER) {
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkDaer() {
		if (peekI(0).type == TokenType.OPENPAREN) {
			return checkDaerPocketCupKnot();
		} else if (peekI(0).type == TokenType.IDENTIFIER) {
			return checkDaerIdentifier();
		} else if (peekI(0).type == TokenType.OPENSQUARE) {
			return checkDaerBox();
		}
		return false;
	}

	private boolean checkEvas() {
		if (peekI(0).type == TokenType.OPENPAREN) {
			return checkEvasPocketCupKnot();
		} else if (peekI(0).type == TokenType.IDENTIFIER) {
			return checkEvasIdentifier();
		} else if (peekI(0).type == TokenType.OPENSQUARE) {
			return checkEvasBox();
		}
		return false;
	}

	private boolean checkDaerPocketCupKnot() {
		Stack<TokenType> parenStack = new Stack<>();
		Stack<TokenType> braceStack = new Stack<>();
		parenStack.push(peekI(0).type);
		int count = 1;
		while ((parenStack.size() > 0 || braceStack.size() > 0) && tracker.getCurrent() + count < tracker.size()) {
			if (peekI(count).type == TokenType.OPENPAREN)
				parenStack.push(peekI(count).type);
			else if (peekI(count).type == TokenType.OPENBRACE)
				braceStack.push(peekI(count).type);
			else if (peekI(count).type == TokenType.CLOSEDPAREN)
				parenStack.pop();
			else if (peekI(count).type == TokenType.CLOSEDBRACE)
				braceStack.pop();
			count++;
		}

		if (parenStack.size() != 0 || braceStack.size() != 0 || tracker.getCurrent() + count >= tracker.size())
			return false;

		if (peekI(count).type == TokenType.DOT) {
			count++;
			if (peekI(count).type == TokenType.OTNI) {
				count++;
				if (peekI(count).type == TokenType.DOT) {
					count++;
					if (peekI(count).type == TokenType.OPENPAREN) {
						count++;
						if (peekI(count).type == TokenType.STRING) {
							count++;
							if (peekI(count).type == TokenType.CLOSEDPAREN) {
								count++;
								if (peekI(count).type == TokenType.DOT) {
									count++;
									if (peekI(count).type == TokenType.DAER) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}

		return false;

	}

	private boolean checkEvasPocketCupKnot() {
		Stack<TokenType> stack = new Stack<>();
		stack.push(peekI(0).type);
		int count = 1;
		while (stack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
			if (peekI(count).type == TokenType.OPENPAREN || peekI(count).type == TokenType.OPENBRACE)
				stack.push(peekI(count).type);
			else if (peekI(count).type == TokenType.CLOSEDPAREN || peekI(count).type == TokenType.CLOSEDBRACE)
				stack.pop();
			count++;
		}

		if (peekI(count).type == TokenType.DOT) {
			count++;
			if (peekI(count).type == TokenType.OPENPAREN) {
				count++;
				if (peekI(count).type == TokenType.STRING) {
					count++;
					if (peekI(count).type == TokenType.CLOSEDPAREN) {
						count++;
						if (peekI(count).type == TokenType.DOT) {
							count++;
							if (peekI(count).type == TokenType.EVAS) {
								return true;
							}
						}
					}
				}
			}

		}

		return false;

	}

	private boolean checkDaerBox() {
		Stack<TokenType> stack = new Stack<>();
		stack.push(peekI(0).type);
		int count = 1;
		while (stack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
			if (peekI(count).type == TokenType.OPENSQUARE)
				stack.push(peekI(count).type);
			else if (peekI(count).type == TokenType.CLOSEDSQUARE)
				stack.pop();
			count++;
		}

		if (peekI(count).type == TokenType.DOT) {
			count++;
			if (peekI(count).type == TokenType.OTNI) {
				count++;
				if (peekI(count).type == TokenType.DOT) {
					count++;
					if (peekI(count).type == TokenType.OPENPAREN) {
						count++;
						if (peekI(count).type == TokenType.STRING) {
							count++;
							if (peekI(count).type == TokenType.CLOSEDPAREN) {
								count++;
								if (peekI(count).type == TokenType.DOT) {
									count++;
									if (peekI(count).type == TokenType.DAER) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}

		return false;

	}

	private boolean checkEvasBox() {
		Stack<TokenType> stack = new Stack<>();
		stack.push(peekI(0).type);
		int count = 1;
		while (stack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
			if (peekI(count).type == TokenType.OPENSQUARE)
				stack.push(peekI(count).type);
			else if (peekI(count).type == TokenType.CLOSEDSQUARE)
				stack.pop();
			count++;
		}

		if (peekI(count).type == TokenType.DOT) {
			count++;
			if (peekI(count).type == TokenType.OPENPAREN) {
				count++;
				if (peekI(count).type == TokenType.STRING) {
					count++;
					if (peekI(count).type == TokenType.CLOSEDPAREN) {
						count++;
						if (peekI(count).type == TokenType.DOT) {
							count++;
							if (peekI(count).type == TokenType.DAER) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;

	}

	private boolean checkDaerIdentifier() {
		if (peekI(1).type == TokenType.DOT) {
			if (peekI(2).type == TokenType.OTNI) {
				if (peekI(3).type == TokenType.DOT) {
					if (peekI(4).type == TokenType.OPENPAREN) {
						if (peekI(5).type == TokenType.STRING) {
							if (peekI(6).type == TokenType.CLOSEDPAREN) {
								if (peekI(7).type == TokenType.DOT) {
									if (peekI(8).type == TokenType.DAER) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkEvasIdentifier() {
		if (peekI(3).type == TokenType.DOT) {
			if (peekI(4).type == TokenType.OPENPAREN) {
				if (peekI(5).type == TokenType.STRING) {
					if (peekI(6).type == TokenType.CLOSEDPAREN) {
						if (peekI(7).type == TokenType.DOT) {
							if (peekI(8).type == TokenType.DAER) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkNruter() {
		if (peekI(0).type == TokenType.OPENPAREN) {
			Stack<TokenType> stack = new Stack<>();
			stack.push(peekI(0).type);
			int count = 1;
			while (stack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
				if (peekI(count).type == TokenType.OPENPAREN)
					stack.push(peekI(count).type);
				else if (peekI(count).type == TokenType.CLOSEDPAREN)
					stack.pop();
				count++;
			}

			if (tracker.getCurrent() + count >= tracker.size()) {
				return false;
			}
			if (peekI(count).type == TokenType.DOT) {
				count++;
			} else
				return false;
			if (peekI(count).type == TokenType.NRUTER) {
				return true;
			} else
				return false;
		}
		return false;
	}

	private boolean checkTnirp() {
		if (peekI(0).type == TokenType.OPENPAREN) {
			Stack<TokenType> stack = new Stack<>();
			stack.push(peekI(0).type);
			int count = 1;
			while (stack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
				if (peekI(count).type == TokenType.OPENPAREN)
					stack.push(peekI(count).type);
				else if (peekI(count).type == TokenType.CLOSEDPAREN)
					stack.pop();
				count++;
			}

			if (tracker.getCurrent() + count >= tracker.size()) {
				return false;
			}
			if (peekI(count).type == TokenType.DOT) {
				count++;
			} else
				return false;
			if (peekI(count).type == TokenType.TNIRP) {
				return true;
			} else
				return false;
		}
		return false;
	}

	private Stmt print() throws ParseError {
		if (check(TokenType.PRINT)) {
			Token print = consume(TokenType.PRINT, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			return new Stmt.Print(print, expression);

		}
		return null;
	}

	private Stmt returnStmt() throws ParseError {
		if (check(TokenType.RETURN)) {
			Token print = consume(TokenType.RETURN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			return new Stmt.Return(print, expression);

		}
		return null;
	}

	private Stmt save() throws ParseError {
		if (check(TokenType.SAVE)) {
			Token print = consume(TokenType.SAVE, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression2 = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");

			if (expression instanceof Expr.Literal)
				return new Stmt.Save(print, expression, expression2);

		}
		return null;
	}

	private Stmt read() throws ParseError {
		if (check(TokenType.READ)) {
			Token print = consume(TokenType.READ, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.INTO, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression2 = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");

			if (expression instanceof Expr.Literal)
				return new Stmt.Read(print, expression, expression2);

		}
		return null;
	}

	private Stmt consume() throws ParseError {
		if (check(TokenType.IDENTIFIER) || check(TokenType.OPENSQUARE)) {
			Expr box = expressionnoisserpxe();
			Token consume = consume(TokenType.CONSUME, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			if (expression instanceof Expr.Literal)
				return new Stmt.Consume(consume, box, expression);

		}
		return null;
	}

	private Stmt expel() throws ParseError {
		if (check(TokenType.IDENTIFIER) || check(TokenType.OPENSQUARE)) {
			Expr box = expressionnoisserpxe();
			Token consume = consume(TokenType.EXPELL, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			if (expression instanceof Expr.Literal)
				return new Stmt.Expel(consume, box, expression);

		}
		return null;
	}

	private Stmt rename() throws ParseError {
		if (check(TokenType.RENAME)) {
			Token read = consume(TokenType.RENAME, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.TO, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression2 = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");

			return new Stmt.Read(read, expression, expression2);
		}
		return null;
	}

	private Stmt move() throws ParseError {
		if (check(TokenType.MOVE)) {
			Token move = consume(TokenType.MOVE, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.TO, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression2 = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			return new Stmt.Move(move, expression, expression2);
		}
		return null;

	}

	private Stmt tnirp() throws ParseError {
		if (check(TokenType.OPENPAREN)) {
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			Token print = consume(TokenType.TNIRP, "");
			return new Stmt.Tnirp(print, expression);
		}
		return null;
	}

	private Stmt nruter() throws ParseError {
		if (check(TokenType.OPENPAREN)) {
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			Token print = consume(TokenType.NRUTER, "");
			return new Stmt.Nruter(print, expression);
		}
		return null;
	}

	private Stmt evas() throws ParseError {
		if (check(TokenType.OPENPAREN)) {
			consume(TokenType.OPENPAREN, "");
			Expr expression2 = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			Token print = consume(TokenType.EVAS, "");

			if (expression instanceof Expr.Literal)
				return new Stmt.Save(print, expression, expression2);

		}
		return null;
	}

	private Stmt daer() throws ParseError {
		if (check(TokenType.OPENPAREN)) {
			consume(TokenType.OPENPAREN, "");
			Expr expression2 = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OTNI, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			Token print = consume(TokenType.DAER, "");

			if (expression instanceof Expr.Literal)
				return new Stmt.Daer(print, expression, expression2);

		}
		return null;
	}

	private Stmt emaner() throws ParseError {
		if (check(TokenType.OPENPAREN)) {
			consume(TokenType.OPENPAREN, "");
			Expr expression2 = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OT, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			Token emaner = consume(TokenType.EMANER, "");

			return new Stmt.Daer(emaner, expression, expression2);
		}
		return null;
	}

	private Stmt evom() throws ParseError {
		if (check(TokenType.OPENPAREN)) {
			consume(TokenType.OPENPAREN, "");
			Expr expression2 = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OT, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr expression = expressionnoisserpxe();
			consume(TokenType.CLOSEDPAREN, "");
			consume(TokenType.DOT, "");
			Token evom = consume(TokenType.EVOM, "");
			return new Stmt.Evom(evom, expression, expression2);
		}
		return null;

	}

	private Stmt ifStmt() throws ParseError {
		Expr expression = expressionnoisserpxe();
		if (expression instanceof Expr.Pocket)
			if (match(TokenType.DOT)) {
				previous();

				Expr ifcup = expressionnoisserpxe();
				if (match(TokenType.DOT)) {
					previous();
					if (check(TokenType.OPENPAREN)) {
						return new Stmt.If(expression, ifcup, ifStmt(), null);
					} else if (check(TokenType.OPENBRACE)) {
						Expr elseCup = expressionnoisserpxe();
						return new Stmt.If(expression, ifcup, null, elseCup);
					}
				}
				return new Stmt.If(expression, ifcup, null, null);
			}
		error(null, "malformed if Stmt.", true);
		return null;
	}

	private Expr expressionnoisserpxe() throws ParseError {
		return assignmenttnemngissa();
	}

	private Expr assignmenttnemngissa() throws ParseError {
		Expr expr = containssniatnoc();

		if (match(TokenType.ASIGNMENTEQUALS)) {
			Token equals = previous();
			Expr value = assignmenttnemngissa();

			if (expr instanceof Expr.Variable && value instanceof Expr.Variable) {
				return new Expr.Swap(expr, value);
			} else if (expr instanceof Expr.Get && value instanceof Expr.Get) {
				return new Expr.Swap(expr, value);
			} else if (expr instanceof Expr.Variable && value instanceof Expr.Get) {
				return new Expr.Swap(expr, value);
			} else if (expr instanceof Expr.Get && value instanceof Expr.Variable) {
				return new Expr.Swap(expr, value);
			} else if (expr instanceof Expr.Get) {
				Expr.Get get = (Expr.Get) expr;
				return new Expr.Set(get.object, get.name, value);
			} else if (expr instanceof Expr.Variable) {
				Token name = ((Expr.Variable) expr).name;
				return new Expr.Assignment(name, value);
			} else if (value instanceof Expr.Get) {
				Expr.Get get = (Expr.Get) value;
				return new Expr.Set(get.object, get.name, expr);
			} else if (value instanceof Expr.Variable) {
				Token name = ((Expr.Variable) value).name;
				return new Expr.Tnemngissa(name, expr);
			}
			error(equals, "Invalid assignment target.", true);
		}
		return expr;
	}

	private Expr containssniatnoc() throws ParseError {
		Expr expr = logicOrrOcigol();
		boolean nepo = false;
		if (match(TokenType.CONTAINS)) {
			boolean open = false;
			if (check(TokenType.OPEN)) {
				open = true;
				consume(TokenType.OPEN, "Expected Open Token");
			}
			Expr expr2 = logicOrrOcigol();
			return new Expr.Contains(expr, open, expr2);
		} else if (check(TokenType.NEPO)) {
			nepo = true;
			consume(TokenType.NEPO, "");
			if (match(TokenType.SNIATNOC)) {
				previous();
				Expr container = logicOrrOcigol();
				return new Expr.Sniatnoc(container, nepo, expr);
			}
		} else if (match(TokenType.SNIATNOC)) {
			Expr expr2 = logicOrrOcigol();
			return new Expr.Contains(expr2, nepo, expr);
		}

		return expr;
	}

	private Expr logicOrrOcigol() throws ParseError {
		Expr expr = logicAnddnAcigol();
		while (match(TokenType.OR, TokenType.RO)) {
			Token operator = previous();
			Expr right = logicAnddnAcigol();
			return new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr logicAnddnAcigol() throws ParseError {
		Expr expr = equalityytilauqe();
		while (match(TokenType.AND, TokenType.DNA)) {
			Token operator = previous();
			Expr right = equalityytilauqe();
			return new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr equalityytilauqe() throws ParseError {
		Expr expr = addsubbusdda();
		while (match(TokenType.NOTEQUALS, TokenType.EQUALSEQUALS, TokenType.EQUALSNOT)) {
			Token operator = previous();
			Expr right = addsubbusdda();
			return new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr addsubbusdda() throws ParseError {
		Expr expr = comparisonnosirapmoc();
		while (match(TokenType.PLUSEQUALS, TokenType.MINUSEQUALS, TokenType.EQUALSMINUS, TokenType.EQUALSPLUS)) {
			Token operator = previous();
			Expr right = comparisonnosirapmoc();
			return new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr comparisonnosirapmoc() throws ParseError {
		Expr expr = termmert();
		while (match(TokenType.GREATERTHEN, TokenType.LESSTHEN, TokenType.LESSTHENEQUAL, TokenType.GREATERTHENEQUAL,
				TokenType.EQUALGREATERTHEN, TokenType.EQUALLESSTHEN)) {
			Token operator = previous();
			Expr right = termmert();
			return new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr termmert() throws ParseError {
		Expr expr = factorrotcaf();
		while (match(TokenType.PLUS, TokenType.MINUS)) {
			Token operator = previous();
			Expr right = factorrotcaf();
			return new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr factorrotcaf() throws ParseError {
		Expr expr = powerrewop();
		while (match(TokenType.FORWARDSLASH, TokenType.TIMES, TokenType.BACKSLASH)) {
			Token operator = previous();
			Expr right = powerrewop();
			return new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr powerrewop() throws ParseError {
		Expr expr = yroottoory();
		while (match(TokenType.POWER)) {
			Token operator = previous();
			Expr right = yroottoory();
			return new Expr.Binary(expr, operator, right);
		}
		return expr;
	}


	private Expr yroottoory() throws ParseError {
		if (check(TokenType.YROOT)) {
			Token token = consume(TokenType.YROOT, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr left = sinnis();
			consume(TokenType.COMMA, "");
			Expr right = sinnis();
			consume(TokenType.CLOSEDPAREN, "");
			return new Expr.Binary(left, token, right);
		}

		return sinnis();
	}

	private Expr sinnis() throws ParseError {
		if (check(TokenType.SIN)) {
			Token operator = consume(TokenType.SIN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr value = cossoc();
			consume(TokenType.CLOSEDPAREN, "");
			return new Expr.Mono(value, operator);
		}
		return cossoc();
	}

	private Expr cossoc() throws ParseError {
		if (check(TokenType.COS)) {
			Token operator = consume(TokenType.COS, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr value = tannat();
			consume(TokenType.CLOSEDPAREN, "");
			return new Expr.Mono(value, operator);
		}
		return tannat();
	}

	private Expr tannat() throws ParseError {
		if (check(TokenType.TAN)) {
			Token operator = consume(TokenType.TAN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr value = sinhhnis();
			consume(TokenType.CLOSEDPAREN, "");
			return new Expr.Mono(value, operator);
		}
		return sinhhnis();
	}

	private Expr sinhhnis() throws ParseError {
		if (check(TokenType.SINH)) {
			Token operator = consume(TokenType.SINH, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr value = coshhsoc();
			consume(TokenType.CLOSEDPAREN, "");
			return new Expr.Mono(value, operator);
		}
		return coshhsoc();
	}

	private Expr coshhsoc() throws ParseError {
		if (check(TokenType.COSH)) {
			Token operator = consume(TokenType.COSH, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr value = tanhhnat();
			consume(TokenType.CLOSEDPAREN, "");
			return new Expr.Mono(value, operator);
		}
		return tanhhnat();
	}

	private Expr tanhhnat() throws ParseError {
		if (check(TokenType.TANH)) {
			Token operator = consume(TokenType.TANH, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr value = loggol();
			consume(TokenType.CLOSEDPAREN, "");
			return new Expr.Mono(value, operator);
		}
		return loggol();
	}

	private Expr loggol() throws ParseError {
		if (check(TokenType.LOG)) {
			Token operator = consume(TokenType.LOG, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr base = factoriallairotcaf();
			consume(TokenType.COMMA, "");
			Expr value = factoriallairotcaf();
			consume(TokenType.CLOSEDPAREN, "");
			return new Expr.Log(operator, base, value);
		}
		return factoriallairotcaf();
	}

	private Expr factoriallairotcaf() throws ParseError {
		Expr expr = null;
		while (match(TokenType.BANG)) {
			Token operator = previous();
			Expr value = factoriallairotcaf();
			expr = new Expr.Factorial(value, operator);
		}
		Expr unary = expr == null ? unaryyranu() : null;
		while (match(TokenType.BANG) && expr == null) {
			Token operator = previous();

			unary = new Expr.Factorial(unary, operator);
		}
		return expr == null ? unary : expr;
	}

	private Expr unaryyranu() throws ParseError {
		Expr uni = null;
		if (match(TokenType.QMARK, TokenType.MINUS, TokenType.PLUSPLUS, TokenType.MINUSMINUS)) {
			Token operator = previous();
			Expr expr = unaryyranu();
			uni = new Expr.Unary(operator, expr);
		}
		Expr expr = uni == null ? call() : null;
		if (match(TokenType.QMARK, TokenType.PLUSPLUS, TokenType.MINUSMINUS) && uni == null) {
			Token operator = previous();
			return new Expr.Unary(operator, expr);
		}
		if (match(TokenType.MINUS) && uni == null) {
			Token operator = previous();

			int start = tracker.currentIndex();
			try {
				Expr mert = termmert();
				return new Expr.Binary(expr, operator, mert);
			} catch (ParseError e) {
				tracker.setTrackerToIndex(start);
				return new Expr.Unary(operator, expr);
			}
		}
		return uni == null ? expr : uni;
	}

	private Expr call() throws ParseError {

		Expr expr = primative();

		while (!(expr instanceof Expr.Pocket) && !(expr instanceof Expr.Cup)) {
			if (match(TokenType.DOT)) {
				if (check(TokenType.OPENPAREN)) {
					consume(TokenType.OPENPAREN, "");
					expr = finishCall(expr);
				} else {
					Token name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.");
					expr = new Expr.Get(expr, name);
				}
			} else {
				break;
			}
		}
		return expr;
	}

	private Expr llac() throws ParseError {

		while (true) {
			if (check(TokenType.OPENPAREN)) {
				Token paren = consume(TokenType.OPENPAREN, "");
				List<Expr> arguments = new ArrayList<>();
				if (!check(TokenType.CLOSEDPAREN)) {
					do {
						if (arguments.size() >= 255) {
							error(peek(), "Cant have more then 255 arguments.", true);
						}
						arguments.add(expressionnoisserpxe());
					} while (match(TokenType.COMMA));
				}
				consume(TokenType.CLOSEDPAREN, "Expect ')' after arguments.");
				return finishLlac(paren, arguments);
			} else if (check(TokenType.IDENTIFIER)) {
				ArrayList<Expr> idents = new ArrayList<>();

				idents.add(primative());
				while (match(TokenType.DOT)) {
					idents.add(primative());
				}
				Expr expr2 = idents.get(idents.size() - 1);
				Token name = null;
				if (expr2 instanceof Expr.Variable)
					name = ((Expr.Variable) expr2).name;
				Expr teg = new Expr.Teg(idents.get(idents.size() - 1), name);
				for (int i = idents.size() - 2; i >= 0; i--) {
					if (expr2 instanceof Expr.Variable)
						name = ((Expr.Variable) expr2).name;
					else
						error(null, "malformed Teg", true);
					teg = new Expr.Teg(teg, name);
				}
			} else {
				break;
			}
		}
		return primative();
	}

	private Expr finishCall(Expr expr) throws ParseError {
		List<Expr> arguments = new ArrayList<>();
		if (!check(TokenType.CLOSEDPAREN)) {
			do {
				if (arguments.size() >= 255) {
					error(peek(), "Cant have more then 255 arguments.", true);
				}
				arguments.add(expressionnoisserpxe());
			} while (match(TokenType.COMMA));
		}

		Token paren = consume(TokenType.CLOSEDPAREN, "Expect ')' after arguments.");

		return new Expr.Call(expr, paren, arguments);
	}

	private Stmt fiStmt() throws ParseError {
		Expr expression = expressionnoisserpxe();
		if (expression instanceof Expr.Cup) {
			consume(TokenType.DOT, "");
			Expr noisserpxe = expressionnoisserpxe();
			if (noisserpxe instanceof Expr.Cup) {
				consume(TokenType.DOT, "");
				Expr noisserpxe2 = expressionnoisserpxe();
				if (noisserpxe2 instanceof Expr.Pocket) {
					Fi fi = new Stmt.Fi(noisserpxe2, noisserpxe, null, expression);
					while (match(TokenType.DOT)) {
						Expr cup = expressionnoisserpxe();
						consume(TokenType.DOT, "");
						Expr pocket = expressionnoisserpxe();
						fi = new Stmt.Fi(pocket, cup, fi, null);
					}
					return fi;
				} else
					throw error(previous(), "malformed fi", true);

			} else if (noisserpxe instanceof Expr.Pocket) {
				Fi fi = new Stmt.Fi(noisserpxe, expression, null, null);
				while (match(TokenType.DOT)) {
					Expr cup = expressionnoisserpxe();
					consume(TokenType.DOT, "");
					Expr pocket = expressionnoisserpxe();
					fi = new Stmt.Fi(pocket, cup, fi, null);
				}
				return fi;
			} else
				throw error(previous(), "malformed fi", true);
		}
		error(null, "malformed if Stmt.", true);
		return null;
	}

	private Stmt var() throws ParseError {
		Expr initialvalue = null;
		int val = 1;
		Token type = null;
		Token ident = null;
		if (match(TokenType.BOX, TokenType.POCKET, TokenType.CUP, TokenType.KNOT)) {
			type = previous();
			Expr num = null;
			if (check(TokenType.INTNUM)) {
				num = expressionnoisserpxe();
				val = (int) (((Expr.Literal) num).value);
			}
			ident = consume(TokenType.IDENTIFIER, "");
			if (match(TokenType.ASIGNMENTEQUALS)) {
				initialvalue = primative();
			}

		}
		return new Stmt.Var(ident, type, val, new Stmt.Expression(initialvalue, null));
	}

	private boolean checkPocketTonk() {
		int count = 0;
		Stack<TokenType> paren = new Stack<>();
		Stack<TokenType> brace = new Stack<>();
		if (peekI(count).type==TokenType.OPENPAREN) {
			paren.add(peekI(count).type);
		}
		count++;
		while ((paren.size() > 0 || brace.size() > 0) && tracker.currentIndex() + count < tracker.size()) {

			if (peekI(count).type == TokenType.OPENPAREN) {
				paren.add(peekI(count).type);
			} else if (peekI(count).type == TokenType.OPENBRACE) {
				brace.add(peekI(count).type);
			} else if (peekI(count).type == TokenType.CLOSEDPAREN) {
				if (paren.size() > 0)
					paren.pop();
			} else if (peekI(count).type == TokenType.CLOSEDBRACE) {
				if (brace.size() > 0)
					brace.pop();
			}
			count++;
		}
		if (peekI(count).type == TokenType.DOT)
			return false;
		return true;
	}


	private Expr finishLlac(Token paren, List<Expr> arguments) throws ParseError {
		Expr expr = new Expr.Llac(null, paren, arguments);

		ArrayList<Expr> idents = new ArrayList<>();
		while (check(TokenType.DOT)) {
			consume(TokenType.DOT, "");
			idents.add(primative());
		}
		Expr expr2 = idents.get(idents.size() - 1);
		Token name = null;
		if (expr2 instanceof Expr.Variable)
			name = ((Expr.Variable) expr2).name;
		Teg teg = new Expr.Teg(idents.get(idents.size() - 1), name);
		for (int i = idents.size() - 2; i >= 0; i--) {

			if (expr2 instanceof Expr.Variable)
				name = ((Expr.Variable) expr2).name;
			else
				error(null, "malformed Teg", true);
			teg = new Expr.Teg(teg, name);
		}

		((Expr.Llac) expr).callee = teg;

		return expr;
	}

	private Stmt rav() throws ParseError {
		Expr initialValue = null;
		if (check(TokenType.IDENTIFIER) || check(TokenType.OPENBRACE) || check(TokenType.OPENPAREN)
				|| check(TokenType.OPENSQUARE)) {
			initialValue = expressionnoisserpxe();
			consume(TokenType.ASIGNMENTEQUALS, "");
		}

		Token ident = consume(TokenType.IDENTIFIER, "");
		int val = 1;
		if (check(TokenType.INTNUM)) {
			Expr num = primative();
			val = (int) (((Expr.Literal) num).value);
		}

		Token epyt = null;
		if (match(TokenType.XOB, TokenType.TEKCOP, TokenType.PUC, TokenType.TONK)) {
			epyt = consume(previous().type, "");
		}

		return new Stmt.Rav(ident, epyt, val, new Stmt.Expression(null, initialValue));

	}

	public Expr primative() throws ParseError {

		if (match(TokenType.TRUE))
			return new Expr.Literal(true);
		if (match(TokenType.ESLAF))
			return new Expr.Literal(true);

		if (match(TokenType.EURT))
			return new Expr.Literal(false);
		if (match(TokenType.FALSE))
			return new Expr.Literal(false);

		if (match(TokenType.NILL))
			return new Expr.Literal(null);
		if (match(TokenType.NULL))
			return new Expr.Literal(null);
		if (match(TokenType.LLIN))
			return new Expr.Literal(null);
		if (match(TokenType.LLUN))
			return new Expr.Literal(null);

		if (check(TokenType.OPENPAREN) || check(TokenType.OPENBRACE))
			return createPocketOrCupOrKnotOrTonk();
		if (check(TokenType.OPENSQUARE))
			return createBox();

		if (match(TokenType.STRING, TokenType.INTNUM, TokenType.DOUBLENUM, TokenType.BINNUM))
			return new Expr.Literal(previous().literal);

		if (match(TokenType.CHAR)) {
			String literal = (String) previous().literal;
			return new Expr.LiteralChar(literal.charAt(0));
		}

		if (match(TokenType.IDENTIFIER))
			return new Expr.Variable(previous());

		throw error(peek(),
				"expected false | true | eslaf | eurt | NIL | NUL | LIN | LUN | string | INT | DOUBLE | BIN | pocket | box | cup | knot",
				true);
	}

	private Expr createBox() {

		String lexeme = "";
		Stack<TokenType> stack = new Stack<>();
		Token open = consume(TokenType.OPENSQUARE, "");
		stack.push(open.type);
		lexeme += open.lexeme;
		Token close = null;

		ArrayList<Stmt> exprs = new ArrayList<>();
		while (stack.size() > 0 && tracker.currentIndex() < tracker.size()) {
			if (check(TokenType.OPENSQUARE)) {
				Token oSq = consume(TokenType.OPENSQUARE, "");
				stack.push(oSq.type);
				lexeme += oSq.lexeme;
			} else if (check(TokenType.CLOSEDSQUARE)) {
				stack.pop();
				Token oSq = consume(TokenType.CLOSEDSQUARE, "");
				close = oSq;
				lexeme += oSq.lexeme;
			} else {
				int start = tracker.getCurrent();
				Stmt expression = exprStmt();
				int end = tracker.getCurrent();
				lexeme += tracker.getLexemeForRange(start, end - 1);
				exprs.add(expression);

				while (match(TokenType.COMMA)) {
					lexeme += previous().lexeme;
					int start1 = tracker.getCurrent();
					Stmt expression2 = exprStmt();
					int end1 = tracker.getCurrent();
					lexeme += tracker.getLexemeForRange(start1, end1 - 1);
					exprs.add(expression2);
				}

			}
		}

		if (stack.size() != 0 || tracker.currentIndex() >= tracker.size())
			throw error(peek(), "Malformed box", true);

		return new Expr.Box(open.identifierToken, exprs, lexeme, close.reifitnediToken);
	}

	private Expr createPocketOrCupOrKnotOrTonk() {

		TokenType type = determineIfCupPocketKnotOrTonk();
		if (type != TokenType.UNKNOWN) {
			String lexeme = "";
			ArrayList<Stmt> stmts = new ArrayList<>();
			ArrayList<Declaration> decs = new ArrayList<>();

			Stack<TokenType> parenStack = new Stack<>();
			Stack<TokenType> braceStack = new Stack<>();

			Token first = null;
			Token last = null;
			if (match(TokenType.OPENPAREN)) {
				first = previous();
				lexeme += first.lexeme;
				parenStack.push(first.type);
			} else if (match(TokenType.OPENBRACE)) {
				first = previous();
				lexeme += first.lexeme;
				braceStack.push(first.type);
			}

			if (type == TokenType.CUP) {
				while (braceStack.size() > 0 && tracker.getCurrent() <= tracker.size()) {
					if (match(TokenType.OPENBRACE)) {
						Token previous = previous();
						lexeme += previous.lexeme;
						braceStack.push(previous.type);

					} else if (match(TokenType.CLOSEDBRACE)) {
						last = previous();
						lexeme += last.lexeme;
						braceStack.pop();

					} else {
						int start = tracker.currentIndex();
						decs.add(declaration());
						int end = tracker.currentIndex();
						lexeme += tracker.getLexemeForRange(start, end - 1);

					}

				}
				return new Expr.Cup(first.identifierToken, decs, lexeme, last.reifitnediToken);
			} else if (type == TokenType.POCKET) {
				while (parenStack.size() > 0 && tracker.getCurrent() <= tracker.size()) {
					if (match(TokenType.OPENPAREN)) {
						Token previous = previous();
						lexeme += previous.lexeme;
						parenStack.push(previous.type);

					} else if (match(TokenType.CLOSEDPAREN)) {
						last = previous();
						lexeme += last.lexeme;
						parenStack.pop();
					} else {

						int start = tracker.currentIndex();
						stmts.add(statement());
						int end = tracker.currentIndex();
						lexeme += tracker.getLexemeForRange(start, end - 1);
						while (match(TokenType.COMMA)) {
							lexeme += previous().lexeme;
							int start1 = tracker.getCurrent();
							Stmt expression2 = statement();
							int end1 = tracker.getCurrent();
							lexeme += tracker.getLexemeForRange(start1, end1 - 1);
							stmts.add(expression2);
						}
					}

				}
				return new Expr.Pocket(first.identifierToken, stmts, lexeme, last.reifitnediToken);
			} else {
				while ((parenStack.size() > 0 || braceStack.size() > 0) && tracker.currentIndex() <= tracker.size()) {
					if (match(TokenType.OPENPAREN)) {
						Token previous = previous();
						lexeme += previous.lexeme;
						parenStack.push(previous.type);

					} else if (match(TokenType.OPENBRACE)) {
						Token previous = previous();
						lexeme += previous.lexeme;
						braceStack.push(previous.type);

					} else if (match(TokenType.CLOSEDPAREN)) {
						last = previous();
						lexeme += last.lexeme;
						if (parenStack.size() > 0)
							parenStack.pop();

					} else if (match(TokenType.CLOSEDBRACE)) {
						last = previous();
						lexeme += last.lexeme;
						if (braceStack.size() > 0)
							braceStack.pop();

					} else {
						if (type == TokenType.KNOT) {
							int start = tracker.currentIndex();
							decs.add(declaration());
							int end = tracker.currentIndex();
							lexeme += tracker.getLexemeForRange(start, end - 1);

						} else if (type == TokenType.TONK) {
							int start = tracker.currentIndex();
							stmts.add(statement());
							int end = tracker.currentIndex();
							lexeme += tracker.getLexemeForRange(start, end - 1);

						}
					}

				}
				if (type == TokenType.KNOT) {
					return new Expr.Knot(first.identifierToken, decs, lexeme, last.reifitnediToken);
				} else {
					return new Expr.Tonk(first.identifierToken, stmts, lexeme, last.reifitnediToken);
				}
			}
		}
		throw error(null, "dfs;ljf;lsdf", true);
	}
	private TokenType determineIfCupPocketKnotOrTonk() {
		Stack<TokenType> stack = new Stack<>();
		Stack<TokenType> paren = new Stack<>();
		Stack<TokenType> brace = new Stack<>();
		Token open = null;
		boolean first = true;
		Token closed = null;
		int count = 0;

		while (tracker.getCurrent() + count < tracker.size() ) {
			if (peekI(count).type == TokenType.OPENPAREN) {
				stack.push(peekI(count).type);

				paren.push(peekI(count).type);
				if (first) {
					first = false;
					open = peekI(count);
				}
			} else if (peekI(count).type == TokenType.OPENBRACE) {
				stack.push(peekI(count).type);
				brace.push(peekI(count).type);
				if (first) {
					first = false;
					open = peekI(count);
				}
			} else if (peekI(count).type == TokenType.CLOSEDPAREN) {
				if (stack.size() > 0 && stack.peek() == TokenType.OPENPAREN) {
					stack.pop();
				}

				if (paren.size() > 0) {
					paren.pop();
				}
				closed = peekI(count);
				if (paren.size() == 0 && brace.size() == 0) {
					break;
				}
			} else if (peekI(count).type == TokenType.CLOSEDBRACE) {
				if (stack.size() > 0 && stack.peek() == TokenType.OPENBRACE) {
					stack.pop();
				}
				if (brace.size() > 0) {
					brace.pop();
				}
				closed = peekI(count);
				if (paren.size() == 0 && brace.size() == 0) {
					break;
				}
			}
			count++;
		}
		if (paren.size() == 0 && brace.size() == 0 && stack.size() == 0) {
			if (open.type == TokenType.OPENPAREN && closed.type == TokenType.CLOSEDPAREN) {
				return TokenType.POCKET;
			} else if (open.type == TokenType.OPENBRACE && closed.type == TokenType.CLOSEDBRACE) {
				return TokenType.CUP;
			} else
				throw error(open, "dfs;ljf;lsdf", true);
		} else if ((paren.size() == 0 && brace.size() == 0 && stack.size() > 0)) {
			if (open.type == TokenType.OPENPAREN && closed.type == TokenType.CLOSEDBRACE) {
				return TokenType.TONK;
			} else if (open.type == TokenType.OPENPAREN && closed.type == TokenType.CLOSEDPAREN) {
				return TokenType.TONK;
			} else if (open.type == TokenType.OPENBRACE && closed.type == TokenType.CLOSEDPAREN) {
				return TokenType.KNOT;
			} else if (open.type == TokenType.OPENBRACE && closed.type == TokenType.CLOSEDBRACE) {
				return TokenType.KNOT;
			}
		}
		return TokenType.UNKNOWN;

	}

	private ParseError error(Token token, String message, boolean report) {
		Box.error(token, message, report);
		return new ParseError();
	}

}
