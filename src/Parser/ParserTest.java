package Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Box.Box.Box;
import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Expr.Cup;
import Parser.Expr.Knot;
import Parser.Expr.Pocket;
import Parser.Expr.Teg;
import Parser.Expr.Tonk;
import Parser.Stmt.Fi;

public class ParserTest {

	private static class ParseError extends RuntimeException {
		private static Token token;
		private static String message;
		private static boolean report;

		public ParseError(Token token, String message, boolean report) {
			this.token = token;
			this.message = message;
			this.report = report;
			Box.error(token, message, report);
		}

		public ParseError() {
			// TODO Auto-generated constructor stub
		}

		private static final long serialVersionUID = 2715202794403784452L;
	}

	TokensToTrack tracker;

	@SuppressWarnings("javadoc")
	public ParserTest(List<Token> tokens, boolean forward, boolean backward) {
		tracker = new TokensToTrack((ArrayList<Token>) tokens, 0);
	}

	@SuppressWarnings("javadoc")
	public List<Declaration> parse() {

		List<Declaration> stmt = parseForward();
		return stmt;

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
		} else if (checkFunctionLink()) {
			return new Declaration.FunDecl(functionLink());
		} else {
			Declaration.StmtDecl stmtDecl = new Declaration.StmtDecl(statement());
			return stmtDecl;
		}
	}

	private Fun functionLink() {
		if (check(TokenType.FUN)) {
			consume(TokenType.FUN, "fun");
			consume(TokenType.DOT, "fun dot");
			Token forwaredIdent = consume(TokenType.IDENTIFIER, "fun forwardIdent");
			consume(TokenType.DOT, "fun dot");
			consume(TokenType.OPENSQUARE, "fun forward square open");

			ArrayList<Token> typesForward = new ArrayList<>();
			ArrayList<Token> identsForward = new ArrayList<>();
			if (checkTypeEpyt()) {
				matchTypeEpyt();
				typesForward.add(previous());
				identsForward.add(consume(TokenType.IDENTIFIER, ""));
				while (match(TokenType.COMMA)) {
					if (checkTypeEpyt()) {
						matchTypeEpyt();
						typesForward.add(previous());
						identsForward.add(consume(TokenType.IDENTIFIER, ""));
					} else {
						throw error(previous(), "Malformed forward parameters", true);
					}
				}
			}
			consume(TokenType.CLOSEDSQUARE, "");
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				consume(TokenType.OPENSQUARE, "fun forward square open");
				ArrayList<Token> typesbackward = new ArrayList<>();
				ArrayList<Token> identsbackward = new ArrayList<>();
				if (check(TokenType.IDENTIFIER)) {
					identsbackward.add(consume(TokenType.IDENTIFIER, ""));
					matchTypeEpyt();
					typesbackward.add(previous());
					while (match(TokenType.COMMA)) {
						if (check(TokenType.IDENTIFIER)) {
							identsbackward.add(consume(TokenType.IDENTIFIER, ""));
							matchTypeEpyt();
							typesbackward.add(previous());
						} else {
							throw error(previous(), "Malformed forward parameters", true);
						}
					}
				} else {
					consume(TokenType.CLOSEDSQUARE, "");
					consume(TokenType.DOT, "fun dot");
					Token backwardIdent = consume(TokenType.IDENTIFIER, "fun forwardIdent");
					consume(TokenType.DOT, "fun dot");
					consume(TokenType.NUF, "fun nuf");
					return new Fun.FunctionLink(forwaredIdent, typesForward, identsForward, typesbackward,
							identsbackward, backwardIdent);
				}
				consume(TokenType.CLOSEDSQUARE, "");
				consume(TokenType.DOT, "fun dot");
				Token backwardIdent = consume(TokenType.IDENTIFIER, "fun forwardIdent");
				consume(TokenType.DOT, "fun dot");
				consume(TokenType.NUF, "fun nuf");
				return new Fun.FunctionLink(forwaredIdent, typesForward, identsForward, typesbackward, identsbackward,
						backwardIdent);
			}

			return new Fun.FunctionLink(forwaredIdent, typesForward, identsForward, null, null, null);
		} else {
			consume(TokenType.OPENSQUARE, "fun forward square open");
			ArrayList<Token> typesbackward = new ArrayList<>();
			ArrayList<Token> identsbackward = new ArrayList<>();
			if (check(TokenType.IDENTIFIER)) {
				identsbackward.add(consume(TokenType.IDENTIFIER, ""));
				matchTypeEpyt();
				typesbackward.add(previous());
				while (match(TokenType.COMMA)) {
					if (check(TokenType.IDENTIFIER)) {
						identsbackward.add(consume(TokenType.IDENTIFIER, ""));
						matchTypeEpyt();
						typesbackward.add(previous());
					} else {
						throw error(previous(), "Malformed forward parameters", true);
					}
				}

				consume(TokenType.CLOSEDSQUARE, "");
				consume(TokenType.DOT, "fun dot");
				Token backwardIdent = consume(TokenType.IDENTIFIER, "fun forwardIdent");
				consume(TokenType.DOT, "fun dot");
				consume(TokenType.NUF, "fun nuf");
				return new Fun.FunctionLink(null, null, null, typesbackward, identsbackward, backwardIdent);

			} else {
				consume(TokenType.CLOSEDSQUARE, "");
				consume(TokenType.DOT, "fun dot");
				Token backwardIdent = consume(TokenType.IDENTIFIER, "fun forwardIdent");
				consume(TokenType.DOT, "fun dot");
				consume(TokenType.NUF, "fun nuf");
				return new Fun.FunctionLink(null, null, null, typesbackward, identsbackward, backwardIdent);
			}

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
			if (checkTypeEpyt()) {
				matchTypeEpyt();
				typesForward.add(previous());
				identsForward.add(consume(TokenType.IDENTIFIER, ""));
				while (match(TokenType.COMMA)) {
					if (checkTypeEpyt()) {
						matchTypeEpyt();
						typesForward.add(previous());
						identsForward.add(consume(TokenType.IDENTIFIER, ""));
					} else {
						throw error(previous(), "Malformed forward parameters", true);
					}
				}
				consume(TokenType.CLOSEDSQUARE, "");
				consume(TokenType.DOT, "fun dot");
				Expr expression = expressionnoisserpxe();
				if (expression instanceof Expr.Cup) {
					if (check(TokenType.DOT)) {
						ArrayList<Token> typesBackward = new ArrayList<>();
						ArrayList<Token> identsBackward = new ArrayList<>();
						consume(TokenType.DOT, "fun dot");
						consume(TokenType.OPENSQUARE, "fun forward square open");
						if (match(TokenType.IDENTIFIER)) {
							identsBackward.add(previous());
							if (checkTypeEpyt()) {
								matchTypeEpyt();
								typesBackward.add(previous());
							} else
								throw error(previous(), "weerere", true);
							while (match(TokenType.COMMA)) {
								if (match(TokenType.IDENTIFIER)) {
									identsBackward.add(previous());
									if (checkTypeEpyt()) {
										matchTypeEpyt();
										typesBackward.add(previous());
									} else
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
			consume(TokenType.CLOSEDSQUARE, "");
			consume(TokenType.DOT, "fun dot");
			Expr expression = expressionnoisserpxe();
			if (expression instanceof Expr.Cup) {
				if (check(TokenType.DOT)) {
					ArrayList<Token> typesBackward = new ArrayList<>();
					ArrayList<Token> identsBackward = new ArrayList<>();
					consume(TokenType.DOT, "fun dot");
					consume(TokenType.OPENSQUARE, "fun forward square open");
					if (match(TokenType.IDENTIFIER)) {
						identsBackward.add(previous());
						if (checkTypeEpyt()) {
							matchTypeEpyt();
							typesBackward.add(previous());
						} else
							throw error(previous(), "weerere", true);
						while (match(TokenType.COMMA)) {
							if (match(TokenType.IDENTIFIER)) {
								identsBackward.add(previous());
								if (checkTypeEpyt()) {
									matchTypeEpyt();
									typesBackward.add(previous());
								} else
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
						return new Fun.Function(forwaredIdent, typesForward, identsForward, expression, typesBackward,
								identsBackward, backIdent);
					}

				} else {
					return new Fun.Function(forwaredIdent, typesForward, identsForward, expression, null, null, null);
				}
			}

		} else {
			Expr expression = expressionnoisserpxe();
			if (expression instanceof Expr.Cup) {
				if (check(TokenType.DOT)) {
					ArrayList<Token> typesBackward = new ArrayList<>();
					ArrayList<Token> identsBackward = new ArrayList<>();
					consume(TokenType.DOT, "fun dot");
					consume(TokenType.OPENSQUARE, "fun forward square open");
					if (match(TokenType.IDENTIFIER)) {
						identsBackward.add(previous());
						if (checkTypeEpyt()) {
							matchTypeEpyt();
							typesBackward.add(previous());
						} else
							throw error(previous(), "weerere", true);
						while (match(TokenType.COMMA)) {
							if (match(TokenType.IDENTIFIER)) {
								identsBackward.add(previous());
								if (checkTypeEpyt()) {
									matchTypeEpyt();
									typesBackward.add(previous());
								} else
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
		throw error(previous(), "malformed function", true);
	}

	private boolean checkTypeEpyt() {
		return (check(TokenType.BOX) || check(TokenType.POCKET) || check(TokenType.CUP) || check(TokenType.KNOT)
				|| check(TokenType.XOB) || check(TokenType.TEKCOP) || check(TokenType.PUC) || check(TokenType.TONK));
	}

	private boolean matchTypeEpyt() {
		return (match(TokenType.BOX, TokenType.POCKET, TokenType.CUP, TokenType.KNOT, TokenType.XOB, TokenType.TEKCOP,
				TokenType.PUC, TokenType.TONK));
	}

	private boolean checkFunctionLink() {
		if (peekI(0).type == TokenType.FUN) {
			if (peekI(1).type == TokenType.DOT) {
				if (peekI(2).type == TokenType.IDENTIFIER) {
					if (peekI(3).type == TokenType.DOT) {
						if (peekI(4).type == TokenType.OPENSQUARE) {
							int count = 4;
							if (peekI(count + 1).type != TokenType.CLOSEDSQUARE) {
								do {
									count++;
									if (peekI(count).type != TokenType.BOX && peekI(count).type != TokenType.POCKET
											&& peekI(count).type != TokenType.CUP && peekI(count).type != TokenType.XOB
											&& peekI(count).type != TokenType.TEKCOP
											&& peekI(count).type != TokenType.PUC && peekI(count).type != TokenType.KNOT
											&& peekI(count).type != TokenType.TONK)
										return false;
									count++;

									if (peekI(count).type != TokenType.IDENTIFIER)
										return false;
									count++;
								} while (peekI(count).type == TokenType.COMMA);
							} else {
								count++;
							}

							if (peekI(count).type != TokenType.CLOSEDSQUARE)
								return false;
							else
								count++;

							if (peekI(count).type == TokenType.DOT) {
								count++;
								return checkFunctionLinkNuff(count);
							} else
								return true;
						}
					}
				}
			}
		}
		int count = 0;
		return checkFunctionLinkNuff(count);
	}

	private boolean checkFunctionDeclaration() {
		if (peekI(0).type == TokenType.FUN) {
			if (peekI(1).type == TokenType.DOT) {
				if (peekI(2).type == TokenType.IDENTIFIER) {
					if (peekI(3).type == TokenType.DOT) {
						if (peekI(4).type == TokenType.OPENSQUARE) {
							int count = 4;
							if (peekI(count + 1).type != TokenType.CLOSEDSQUARE) {
								do {
									count++;
									if (peekI(count).type != TokenType.BOX && peekI(count).type != TokenType.POCKET
											&& peekI(count).type != TokenType.CUP && peekI(count).type != TokenType.XOB
											&& peekI(count).type != TokenType.TEKCOP
											&& peekI(count).type != TokenType.PUC && peekI(count).type != TokenType.KNOT
											&& peekI(count).type != TokenType.TONK)
										return false;
									count++;

									if (peekI(count).type != TokenType.IDENTIFIER)
										return false;
									count++;
								} while (peekI(count).type == TokenType.COMMA);
							} else {
								count++;
							}
							if (peekI(count).type != TokenType.CLOSEDSQUARE)
								return false;
							else
								count++;

							if (peekI(count).type != TokenType.DOT)
								return false;
							else
								count++;
							Stack<TokenType> parenStack = new Stack<>();
							Stack<TokenType> braceStack = new Stack<>();
							if (peekI(count).type == TokenType.OPENBRACE)
								braceStack.push(peekI(count).type);
							count++;

							if (braceStack.size() == 0)
								return false;

							while (braceStack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
								if (peekI(count).type == TokenType.OPENBRACE)
									braceStack.push(peekI(count).type);
								else if (peekI(count).type == TokenType.CLOSEDBRACE) {
									if (braceStack.size() > 0) {

										braceStack.pop();
									}
								}
								count++;
							}

							if (braceStack.size() != 0 || tracker.getCurrent() + count >= tracker.size())
								return false;

							if (peekI(count).type == TokenType.DOT) {
								return checkFunctionNuff(count);
							} else
								return true;
						}
					}
				}
			}
		}
		Stack<TokenType> parenStack = new Stack<>();
		Stack<TokenType> braceStack = new Stack<>();
		int count = 0;
		if (peekI(count).type == TokenType.OPENBRACE)
			braceStack.push(peekI(count).type);
		count++;

		while ((parenStack.size() > 0 || braceStack.size() > 0) && tracker.getCurrent() + count < tracker.size()) {
			if (peekI(count).type == TokenType.OPENBRACE)
				braceStack.push(peekI(count).type);
			else if (peekI(count).type == TokenType.CLOSEDBRACE) {
				if (braceStack.size() > 0) {

					braceStack.pop();
				}
			}
			count++;
		}

		if (braceStack.size() != 0 || tracker.getCurrent() + count >= tracker.size())
			return false;
		return checkFunctionNuff(count);
	}

	private boolean checkFunctionLinkNuff(int count) {

		if (peekI(count).type != TokenType.OPENSQUARE)
			return false;
		if (peekI(count + 1).type != TokenType.CLOSEDSQUARE) {
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
		} else {
			count++;
		}

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

	private boolean checkFunctionNuff(int count) {

		if (peekI(count).type != TokenType.DOT)
			return false;
		else
			count++;
		if (peekI(count).type != TokenType.OPENSQUARE)
			return false;
		if (peekI(count + 1).type != TokenType.CLOSEDSQUARE) {
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
		} else {
			count++;
		}

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

	private boolean checkFunctionDeclarationBrace() {

		Stack<TokenType> parenStack = new Stack<>();
		Stack<TokenType> braceStack = new Stack<>();
		int count = 0;
		if (peekI(count).type == TokenType.OPENBRACE)
			braceStack.push(peekI(count).type);
		else
			return false;
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

	private boolean checkFunctionDeclarationParen() {

		Stack<TokenType> parenStack = new Stack<>();
		Stack<TokenType> braceStack = new Stack<>();
		int count = 0;
		if (peekI(count).type == TokenType.OPENPAREN)
			parenStack.push(peekI(count).type);
		else
			return false;
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
		if (checkIfi())
			return ifiStmt();
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
		if (checkClassVar())
			return classVar();
		if (checkRav())
			return rav();

		return exprStmt();

	}

	private boolean checkPrinttnirp() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkClassVar() {
		if (peekI(0).type == TokenType.IDENTIFIER)
			if (peekI(1).type == TokenType.AT)
				if (peekI(2).type == TokenType.IDENTIFIER)
					return true;
		return false;
	}

	private boolean checkNoisStmtParen() {

		return !checkTnirp() && !checkNruter() && !checkEvas() && !checkDaer() && !checkEmaner() && !checkEvom()
				&& !checkIf() && !checkRavParen() && !checkFunctionDeclarationParen();

	}

	private boolean checkNoisStmtBrace() {

		return !checkFi() && !checkRavBrace() && !checkFunctionDeclarationBrace();

	}

	private Stmt exprStmt() {
		return new Stmt.Expression(expressionnoisserpxe());
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
			} else {
				if (peekI(1).type == TokenType.INTNUM) {

					if (peekI(2).type == TokenType.XOB || peekI(2).type == TokenType.PUC
							|| peekI(2).type == TokenType.TEKCOP || peekI(2).type == TokenType.TONK) {
						return true;
					}
				} else if (peekI(1).type == TokenType.XOB || peekI(1).type == TokenType.PUC
						|| peekI(1).type == TokenType.TEKCOP || peekI(1).type == TokenType.TONK) {
					return true;
				}

			}
		} else if (peekI(0).type == TokenType.OPENSQUARE) {
			Stack<TokenType> stack = new Stack<>();
			int count = 0;
			stack.push(peekI(count).type);
			count++;
			while (stack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
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
			while (stack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
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
		} else {

			int count = 0;
			while (peekI(count).type == TokenType.MINUS || peekI(count).type == TokenType.BANG
					|| peekI(count).type == TokenType.BINNUM || peekI(count).type == TokenType.INTNUM
					||peekI(count).type == TokenType.DOUBLENUM||peekI(count).type == TokenType.STRING
					||peekI(count).type == TokenType.CHAR||peekI(count).type == TokenType.COS
					||peekI(count).type == TokenType.SIN||peekI(count).type == TokenType.TAN
					||peekI(count).type == TokenType.TANH||peekI(count).type == TokenType.COSH
					||peekI(count).type == TokenType.SINH||peekI(count).type == TokenType.LOG
					||peekI(count).type == TokenType.DOT
					||peekI(count).type == TokenType.POCKET) {
				count++;
			}
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

	private boolean checkRavParen() {
		if (peekI(0).type == TokenType.OPENPAREN) {
			Stack<TokenType> stack = new Stack<>();
			stack.push(peekI(0).type);
			int count = 1;
			while (stack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
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

	private boolean checkRavBrace() {
		if (peekI(0).type == TokenType.OPENBRACE) {
			Stack<TokenType> stack = new Stack<>();
			stack.push(peekI(0).type);
			int count = 1;
			while (stack.size() > 0 && tracker.getCurrent() + count < tracker.size()) {
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
			if (!toreturn)
				return false;
		}
		if (peekI(count.get()).type == TokenType.DOT) {
			count.add();
			if (peekI(count.get()).type == TokenType.OPENBRACE) {
				toreturn = checkElse(stack, count);
			}
		}

		return toreturn;
	}

	private boolean checkIfi() {
		Stack<TokenType> stack = new Stack<>();
		CountObject count = new CountObject(0);
		boolean toreturn = false;
		while (peekI(count.get()).type == TokenType.OPENPAREN) {
			toreturn = checkIfElse(stack, count);
			if (!toreturn)
				return false;
		}
		if (peekI(count.get()).type == TokenType.DOT) {
			count.add();
			if (peekI(count.get()).type == TokenType.OPENPAREN) {
				toreturn = checkfiParen(stack, count);
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

	private boolean checkfiParen(Stack<TokenType> stack, CountObject count) {
		if (peekI(count.get()).type == TokenType.OPENPAREN) {
			stack.push(peekI(count.get()).type);
			while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
				if (peekI(count.get()).type == TokenType.OPENPAREN)
					stack.push(peekI(count.get()).type);
				else if (peekI(count.get()).type == TokenType.CLOSEDPAREN)
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
			Stack<TokenType> pstack = new Stack<>();
			Stack<TokenType> bstack = new Stack<>();
			stack.push(peekI(count.get()).type);
			pstack.push(peekI(count.get()).type);
			count.add();
			while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
				if (peekI(count.get()).type == TokenType.OPENPAREN) {
					stack.push(peekI(count.get()).type);
					pstack.push(peekI(count.get()).type);
				} else if (peekI(count.get()).type == TokenType.CLOSEDPAREN) {
					if (stack.size() > 0)
						stack.pop();
					if (pstack.size() > 0)
						pstack.pop();
				} else if (peekI(count.get()).type == TokenType.OPENBRACE) {
					bstack.push(peekI(count.get()).type);
				} else if (peekI(count.get()).type == TokenType.CLOSEDBRACE) {

					if (bstack.size() > 0)
						bstack.pop();
				}
				count.add();
			}

			if (pstack.size() != 0 && bstack.size() != 0 && stack.size() != 0
					&& tracker.getCurrent() + count.get() >= tracker.size()) {
				return false;
			}

			if (peekI(count.get()).type == TokenType.DOT) {
				count.add();
			} else
				return false;
			if (peekI(count.get()).type == TokenType.OPENBRACE) {
				stack.push(peekI(count.get()).type);
				count.add();
				while (stack.size() > 0 && tracker.getCurrent() + count.get() < tracker.size()) {
					if (peekI(count.get()).type == TokenType.OPENPAREN) {
						stack.push(peekI(count.get()).type);
						pstack.push(peekI(count.get()).type);
					} else if (peekI(count.get()).type == TokenType.CLOSEDPAREN) {
						if (stack.size() > 0)
							stack.pop();
						if (pstack.size() > 0)
							pstack.pop();
					} else if (peekI(count.get()).type == TokenType.OPENBRACE) {
						bstack.push(peekI(count.get()).type);
					} else if (peekI(count.get()).type == TokenType.CLOSEDBRACE) {

						if (bstack.size() > 0)
							bstack.pop();
					}
					count.add();
				}
				if (pstack.size() != 0 && bstack.size() != 0 && stack.size() != 0
						&& tracker.getCurrent() + count.get() >= tracker.size())
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
			if (peekI(count).type == TokenType.OPENPAREN) {
				parenStack.push(peekI(count).type);
			} else if (peekI(count).type == TokenType.OPENBRACE) {
				braceStack.push(peekI(count).type);
			} else if (peekI(count).type == TokenType.CLOSEDPAREN) {
				if (parenStack.size() > 0)
					parenStack.pop();
			} else if (peekI(count).type == TokenType.CLOSEDBRACE) {
				if (braceStack.size() > 0)
					braceStack.pop();
			}
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
			if(check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				Token tnirp = consume(TokenType.TNIRP, "");
				return new Stmt.Printtnirp(print, expression, tnirp);
			}
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
			if(check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				Token nruter = consume(TokenType.NRUTER, "");
				return new Stmt.Returnruter(print, expression, nruter);
			}
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
			if(check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				Token evas = consume(TokenType.EVAS, "");
				return new Stmt.Saveevas(print, expression, expression2, evas);
			}
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
			if(check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				Token daer = consume(TokenType.DAER, "");
				return new Stmt.Readdaer(print, expression, expression2, daer);
			}
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
			if(check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				Token daer = consume(TokenType.EMANER, "");
				return new Stmt.Readdaer(read, expression, expression2, daer);
			}
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
			if(check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				Token evom = consume(TokenType.EVOM, "");
				return new Stmt.Moveevom(move, expression, expression2, evom);
				
			}
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
				return new Stmt.Evas(print, expression, expression2);

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

	private Stmt ifiStmt() throws ParseError {
		Expr expression = expressionnoisserpxe();
		if (expression instanceof Expr.Pocket)
			return new Stmt.Ifi(expression, elseIfiStmt());
		error(null, "malformed if Stmt.", true);
		return null;
	}

	private Stmt elseIfiStmt() {
		ArrayList<Stmt.Fi> stmts = new ArrayList<>();
		consume(TokenType.DOT, "");
		Expr cup = expressionnoisserpxe();
		consume(TokenType.DOT, "");
		Expr pocket = expressionnoisserpxe();

		stmts.add(new Stmt.Fi(pocket, cup, null, null));
		while (match(TokenType.DOT)) {
			cup = expressionnoisserpxe();
			consume(TokenType.DOT, "");
			pocket = expressionnoisserpxe();
			stmts.add(new Stmt.Fi(pocket, cup, null, null));
		}

		for (int i = 1; i < stmts.size(); i++) {
			stmts.get(i - 1).elseIfStmt = stmts.get(i);
		}

		return stmts.get(0);
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
			Expr right = null;
			try {
				right = factorrotcaf();

				expr = new Expr.Binary(expr, operator, right);
			} catch (ParseError e) {
				Box.resetHadError();
				while (match(TokenType.MINUS)) {

					Token op = previous();
					expr = new Expr.Unary(op, expr);
				}
				return expr;
			}

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

		Expr pocket = sinnis();
		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression baseExp = null;
			Stmt.Expression rootExp = null;
			if (expression.size() == 3) {
				if (expression.get(0) instanceof Stmt.Expression)
					baseExp = (Stmt.Expression) expression.get(0);

				if (expression.get(2) instanceof Stmt.Expression)
					rootExp = (Stmt.Expression) expression.get(2);
			}

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.TOORY) {
				consume(TokenType.DOT, "expected '.'");
				Token toory = consume(TokenType.TOORY, "expected toory");
				if (baseExp != null && rootExp != null) {

					return new Expr.Binary(baseExp.expression, toory, rootExp.expression);
				}
			}

		}

		return pocket;
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
		Expr pocket = cossoc();
		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.NIS) {
				consume(TokenType.DOT, "expected '.'");
				Token sin = consume(TokenType.NIS, "expected nis");
				if (valueExp != null) {

					return new Expr.Mono(valueExp.expression, sin);
				}
			}

		}

		return pocket;
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
		Expr pocket = tannat();

		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.SOC) {
				consume(TokenType.DOT, "expected '.'");
				Token soc = consume(TokenType.SOC, "expected soc");
				if (valueExp != null) {

					return new Expr.Mono(valueExp.expression, soc);
				}
			}

		}

		return pocket;
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
		Expr pocket = sinhhnis();

		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.NAT) {
				consume(TokenType.DOT, "expected '.'");
				Token nat = consume(TokenType.NAT, "expected nat");
				if (valueExp != null) {

					return new Expr.Mono(valueExp.expression, nat);
				}
			}

		}

		return pocket;
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
		Expr pocket = coshhsoc();
		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.HNIS) {
				consume(TokenType.DOT, "expected '.'");
				Token hnis = consume(TokenType.HNIS, "expected hnis");
				if (valueExp != null) {

					return new Expr.Mono(valueExp.expression, hnis);
				}
			}

		}

		return pocket;
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
		Expr pocket = tanhhnat();

		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.HSOC) {
				consume(TokenType.DOT, "expected '.'");
				Token hsoc = consume(TokenType.HSOC, "expected hsoc");
				if (valueExp != null) {

					return new Expr.Mono(valueExp.expression, hsoc);
				}
			}

		}
		return pocket;
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
		Expr pocket = loggol();

		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression valueExp = null;
			if (expression.size() == 1)
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.HNAT) {
				consume(TokenType.DOT, "expected '.'");
				Token hnat = consume(TokenType.HNAT, "expected hnat");
				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, hnat);
				}
			}

		}
		return pocket;
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
		Expr pocket = factoriallairotcaf();
		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression baseExp = null;
			Stmt.Expression valueExp = null;
			if (expression.size() == 3) {
				if (expression.get(0) instanceof Stmt.Expression)
					baseExp = (Stmt.Expression) expression.get(0);

				if (expression.get(2) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(2);
			}

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.GOL) {
				consume(TokenType.DOT, "expected '.'");
				Token gol = consume(TokenType.GOL, "expected gol");
				if (baseExp != null && valueExp != null) {
					return new Expr.Gol(gol, baseExp.expression, valueExp.expression);
				}
			}

		}
		return pocket;
	}

	private Expr factoriallairotcaf() throws ParseError {
		Expr expr = null;
		while (match(TokenType.BANG)) {
			Token operator = previous();
			Expr value = factoriallairotcaf();
			expr = new Expr.Lairotcaf(value, operator);
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
			uni = new Expr.Yranu(operator, expr);
		}
		Expr expr = uni == null ? call() : null;
		if (match(TokenType.QMARK, TokenType.PLUSPLUS, TokenType.MINUSMINUS) && uni == null) {
			Token operator = previous();
			Expr expr2 = new Expr.Unary(operator, expr);
			while(match(TokenType.QMARK, TokenType.PLUSPLUS, TokenType.MINUSMINUS)) {
				expr2 = new Expr.Unary(previous(), expr2);
			}
			return expr2;
		}
		if (match(TokenType.MINUS) && uni == null) {

			Token operator = previous();

			int start = tracker.getCurrent();
			try {
				Expr mert = termmert();
				return new Expr.Binary(expr, operator, mert);
			} catch (ParseError e) {
				Box.resetHadError();
				tracker.setTrackerToIndex(start);
				return new Expr.Unary(operator, expr);
			}
		}
		return uni == null ? expr : uni;
	}

//	private Expr yranu() {
//
//		if (match(TokenType.QMARK, TokenType.MINUS, TokenType.PLUSPLUS, TokenType.MINUSMINUS)) {
//			Token operator = previous();
//			Expr expr = yranu();
//			if (checkTypes())
//				return new Expr.Yranu(operator, expr);
//
//		}
//		Expr expr = call();
//		return expr;
//	}

	private Expr call() throws ParseError {

		Expr expr = primative();

		while (!(expr instanceof Expr.Pocket) && !(expr instanceof Expr.Cup) && check(TokenType.DOT)) {
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
		if (check(TokenType.DOT) && peekI(1).type == TokenType.IDENTIFIER) {

			consume(TokenType.DOT, "");
			if (check(TokenType.IDENTIFIER)) {

				List<Stmt> stmts = ((Expr.Pocket) expr).expression;
				ArrayList<Expr> exprs = new ArrayList<Expr>();
				for (Stmt stmt : stmts) {
					if (stmt instanceof Stmt.Expression) {
						exprs.add(((Stmt.Expression) stmt).expression);
					} else
						throw new ParseError();
				}
				String lexeme = ((Expr.Pocket) expr).identifier.lexeme + "(";
				Expr.Llac expragain = new Expr.Llac(null,
						new Token(TokenType.OPENPAREN, lexeme, null, null, null, ((Expr.Pocket) expr).identifier.column,
								((Expr.Pocket) expr).identifier.line, ((Expr.Pocket) expr).identifier.start,
								((Expr.Pocket) expr).identifier.finish),
						exprs);
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
				for (int i = idents.size() - 1; i >= 0; i--) {
					if (idents.get(i) instanceof Expr.Variable)
						name = ((Expr.Variable) idents.get(i)).name;
					else
						error(null, "malformed Teg", true);
					teg = new Expr.Teg(teg, name);
				}
				expragain.callee = teg;
				return expragain;
			}

		}

		return expr;
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
		return new Stmt.Var(ident, type, val, new Stmt.Expression(initialvalue));
	}

	private Stmt classVar() throws ParseError {
		Token superClass = consume(TokenType.IDENTIFIER, "");
		consume(TokenType.AT, "");
		Token name = consume(TokenType.IDENTIFIER, "");
		return new Stmt.TemplatVar(name, superClass);
	}

	private boolean checkPocketTonk() {
		int count = 0;
		Stack<TokenType> paren = new Stack<>();
		Stack<TokenType> brace = new Stack<>();
		if (peekI(count).type == TokenType.OPENPAREN) {
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
		Expr initialValue = expressionnoisserpxe();
		if (initialValue instanceof Expr.Tnemngissa) {
			int val = 1;
			if (check(TokenType.INTNUM)) {
				Expr num = primative();
				val = (int) (((Expr.Literal) num).value);
			}

			Token epyt = null;
			if (match(TokenType.XOB, TokenType.TEKCOP, TokenType.PUC, TokenType.TONK)) {
				epyt = previous();
			}

			if (initialValue instanceof Expr.Tnemngissa)
				return new Stmt.Rav(((Expr.Tnemngissa) initialValue).name, epyt, val, new Stmt.Expression(((Expr.Tnemngissa)initialValue).value));
			
		}

		
		int val = 1;
		if (check(TokenType.INTNUM)) {
			Expr num = primative();
			val = (int) (((Expr.Literal) num).value);
		}

		Token epyt = null;
		if (match(TokenType.XOB, TokenType.TEKCOP, TokenType.PUC, TokenType.TONK)) {
			epyt = previous();
		}

		return new Stmt.Rav(((Expr.Variable)((Expr.Unary)initialValue).right).name, epyt, val, null);

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
				stmts.add(new Stmt.Expression(new Expr.PocketOpen(first)));
			} else if (match(TokenType.OPENBRACE)) {
				first = previous();
				lexeme += first.lexeme;
				braceStack.push(first.type);
				stmts.add(new Stmt.Expression(new Expr.CupOpen(first)));
			}

			if (type == TokenType.CUP) {
				while (braceStack.size() > 0 && tracker.getCurrent() <= tracker.size()) {
					if (checkNoisStmtBrace() && match(TokenType.OPENBRACE)) {
						Token previous = previous();
						lexeme += previous.lexeme;
						braceStack.push(previous.type);
						stmts.add(new Stmt.Expression(new Expr.CupOpen(previous)));
					} else if (match(TokenType.CLOSEDBRACE)) {
						last = previous();
						lexeme += last.lexeme;
						braceStack.pop();
						stmts.add(new Stmt.Expression(new Expr.CupClosed(last)));
					} else {
						int start = tracker.currentIndex();
						decs.add(declaration());
						int end = tracker.currentIndex();
						lexeme += tracker.getLexemeForRange(start, end - 1);

					}

				}

				if (first.identifierToken.lexeme.contains("#") || last.reifitnediToken.lexeme.contains("#")) {
					String replace = first.identifierToken.lexeme.replace("#", "");
					first.identifierToken.lexeme = replace;
					first.identifierToken.literal = replace;
					String replace2 = last.reifitnediToken.lexeme.replace("#", "");
					last.reifitnediToken.lexeme = replace2;
					last.reifitnediToken.literal = replace2;
					Cup cup = new Expr.Cup(first.identifierToken, decs, lexeme, last.reifitnediToken);
					return new Expr.Template(cup);
				} else if ((first.identifierToken.lexeme.contains("!") || last.reifitnediToken.lexeme.contains("!"))) {
					String replace = first.identifierToken.lexeme.replace("!", "");
					first.identifierToken.lexeme = replace;
					first.identifierToken.literal = replace;
					String replace2 = last.reifitnediToken.lexeme.replace("!", "");
					last.reifitnediToken.lexeme = replace2;
					last.reifitnediToken.literal = replace2;
					Cup cup = new Expr.Cup(first.identifierToken, decs, lexeme, last.reifitnediToken);
					return new Expr.Link(cup);

				} else
					return new Expr.Cup(first.identifierToken, decs, lexeme, last.reifitnediToken);
			} else if (type == TokenType.POCKET) {
				while (parenStack.size() > 0 && tracker.getCurrent() <= tracker.size()) {
					if (checkNoisStmtParen() && match(TokenType.OPENPAREN)) {
						Token previous = previous();
						lexeme += previous.lexeme;
						parenStack.push(previous.type);
						stmts.add(new Stmt.Expression(new Expr.PocketOpen(previous)));
					} else if (match(TokenType.CLOSEDPAREN)) {
						last = previous();
						lexeme += last.lexeme;
						parenStack.pop();
						stmts.add(new Stmt.Expression(new Expr.PocketClosed(last)));
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
				if (first.identifierToken.lexeme.contains("#") || last.reifitnediToken.lexeme.contains("#")) {
					String replace = first.identifierToken.lexeme.replace("#", "");
					first.identifierToken.lexeme = replace;
					first.identifierToken.literal = replace;
					String replace2 = last.reifitnediToken.lexeme.replace("#", "");
					last.reifitnediToken.lexeme = replace2;
					last.reifitnediToken.literal = replace2;
					Pocket pocket = new Expr.Pocket(first.identifierToken, stmts, lexeme, last.reifitnediToken);
					return new Expr.Template(pocket);
				} else
					return new Expr.Pocket(first.identifierToken, stmts, lexeme, last.reifitnediToken);
			} else {
				while ((parenStack.size() > 0 || braceStack.size() > 0) && tracker.currentIndex() <= tracker.size()) {
					if (checkNoisStmtParen() && match(TokenType.OPENPAREN)) {
						Token previous = previous();
						lexeme += previous.lexeme;
						parenStack.push(previous.type);
						stmts.add(new Stmt.Expression(new Expr.PocketOpen(previous)));

					} else if (checkNoisStmtBrace() & match(TokenType.OPENBRACE)) {
						Token previous = previous();
						lexeme += previous.lexeme;
						braceStack.push(previous.type);
						stmts.add(new Stmt.Expression(new Expr.CupOpen(previous)));
					} else if (match(TokenType.CLOSEDPAREN)) {
						last = previous();
						lexeme += last.lexeme;
						if (parenStack.size() > 0)
							parenStack.pop();
						stmts.add(new Stmt.Expression(new Expr.PocketClosed(last)));
					} else if (match(TokenType.CLOSEDBRACE)) {
						last = previous();
						lexeme += last.lexeme;
						if (braceStack.size() > 0)
							braceStack.pop();
						stmts.add(new Stmt.Expression(new Expr.CupClosed(last)));
					} else {

						int start = tracker.currentIndex();
						stmts.add(statement());
						int end = tracker.currentIndex();
						lexeme += tracker.getLexemeForRange(start, end - 1);

					}

				}
				if (type == TokenType.KNOT) {
					if (first.identifierToken.lexeme.contains("#") || last.reifitnediToken.lexeme.contains("#")) {
						String replace = first.identifierToken.lexeme.replace("#", "");
						first.identifierToken.lexeme = replace;
						first.identifierToken.literal = replace;
						String replace2 = last.reifitnediToken.lexeme.replace("#", "");
						last.reifitnediToken.lexeme = replace2;
						last.reifitnediToken.literal = replace2;
						Knot knot = new Expr.Knot(first.identifierToken, stmts, lexeme, last.reifitnediToken);
						return new Expr.Template(knot);
					} else
						return new Expr.Knot(first.identifierToken, stmts, lexeme, last.reifitnediToken);
				} else {
					if (first.identifierToken.lexeme.contains("#") || last.reifitnediToken.lexeme.contains("#")) {
						String replace = first.identifierToken.lexeme.replace("#", "");
						first.identifierToken.lexeme = replace;
						first.identifierToken.literal = replace;
						String replace2 = last.reifitnediToken.lexeme.replace("#", "");
						last.reifitnediToken.lexeme = replace2;
						last.reifitnediToken.literal = replace2;
						Tonk knot = new Expr.Tonk(first.identifierToken, stmts, lexeme, last.reifitnediToken);
						return new Expr.Template(knot);
					} else
						return new Expr.Tonk(first.identifierToken, stmts, lexeme, last.reifitnediToken);
				}
			}
		}
		throw error(null, "dfs;ljf;lsdf", true);
	}

	private boolean checkIdentforBrace(Token first) {
		Token peekI = peekI(0);

		if (peekI.type == TokenType.OPENBRACE) {

			String firstIdent = first.identifierToken.lexeme;

			String lexeme = peekI.identifierToken.lexeme;
			return !lexeme.contains(firstIdent);

		}
		return false;
	}

	private boolean checkIdentforParen(Token first) {
		Token peekI = peekI(0);

		if (peekI.type == TokenType.OPENPAREN) {
			String firstIdent = first.identifierToken.lexeme;
			String lexeme = peekI.identifierToken.lexeme;
			return !lexeme.contains(firstIdent);

		}
		return false;
	}

	private TokenType determineIfCupPocketKnotOrTonk() {
		Stack<TokenType> stack = new Stack<>();
		Stack<TokenType> paren = new Stack<>();
		Stack<TokenType> brace = new Stack<>();
		Token open = null;
		boolean first = true;
		Token closed = null;
		int count = 0;

		while (tracker.getCurrent() + count < tracker.size()) {
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

		return new ParseError(token, message, report);
	}

}
