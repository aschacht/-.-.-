package Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Box.Box.Box;
import Box.Box.Util;
import Box.Grouper.ContainerIndexes;
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
	private Util util;

	@SuppressWarnings("javadoc")
	public ParserTest(List<Token> tokens, boolean forward, boolean backward) {
		tracker = new TokensToTrack((ArrayList<Token>) tokens, 0);
		util = new Util();
		util.setTokens(tokens);
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

		Expr expression = expressionnoisserpxe();

		Expr newExpr = newExp(expression);
		newExpr.reverse();

		return new Stmt.Expression(expression, newExpr);
	}

	private Expr newExp(Expr expression) {
		if (expression instanceof Expr.Assignment) {
			return new Expr.Assignment(((Expr.Assignment) expression));
		} else if (expression instanceof Expr.Contains) {
			return new Expr.Contains(((Expr.Contains) expression));
		} else if (expression instanceof Expr.Containssniatnoc) {
			return new Expr.Containssniatnoc(((Expr.Containssniatnoc) expression));
		} else if (expression instanceof Expr.Binary) {
			return new Expr.Binary(((Expr.Binary) expression));
		} else if (expression instanceof Expr.Mono) {
			return new Expr.Mono(((Expr.Mono) expression));
		} else if (expression instanceof Expr.Log) {
			return new Expr.Log(((Expr.Log) expression));
		} else if (expression instanceof Expr.Factorial) {
			return new Expr.Factorial(((Expr.Factorial) expression));
		} else if (expression instanceof Expr.Unary) {
			return new Expr.Unary(((Expr.Unary) expression));
		} else if (expression instanceof Expr.Call) {
			return new Expr.Call(((Expr.Call) expression));
		} else if (expression instanceof Expr.Get) {
			return new Expr.Get(((Expr.Get) expression));
		} else if (expression instanceof Expr.Set) {
			return new Expr.Set(((Expr.Set) expression));
		} else if (expression instanceof Expr.Knot) {
			return new Expr.Knot(((Expr.Knot) expression));
		} else if (expression instanceof Expr.Cup) {
			return new Expr.Cup(((Expr.Cup) expression));
		} else if (expression instanceof Expr.Template) {
			return new Expr.Template(((Expr.Template) expression));
		} else if (expression instanceof Expr.Link) {
			return new Expr.Link(((Expr.Link) expression));
		} else if (expression instanceof Expr.Pocket) {
			return new Expr.Pocket(((Expr.Pocket) expression));
		} else if (expression instanceof Expr.Box) {
			return new Expr.Box(((Expr.Box) expression));
		} else if (expression instanceof Expr.Monoonom) {
			return new Expr.Monoonom(((Expr.Monoonom) expression));
		} else if (expression instanceof Expr.Binaryyranib) {
			return new Expr.Binaryyranib(((Expr.Binaryyranib) expression));
		} else if (expression instanceof Expr.Loggol) {
			return new Expr.Loggol(((Expr.Loggol) expression));
		} else if (expression instanceof Expr.Callllac) {
			return new Expr.Callllac(((Expr.Callllac) expression));
		} else if (expression instanceof Expr.Expressiontmts) {
			return new Expr.Expressiontmts(((Expr.Expressiontmts) expression));
		} else if (expression instanceof Expr.Assignmenttnemgissa) {
			return new Expr.Assignmenttnemgissa(((Expr.Assignmenttnemgissa) expression));
		} else if (expression instanceof Expr.Swap) {
			return new Expr.Swap(((Expr.Swap) expression));
		} else if (expression instanceof Expr.Variable) {
			return new Expr.Variable(((Expr.Variable) expression));
		} else if (expression instanceof Expr.LiteralChar) {
			return new Expr.LiteralChar(((Expr.LiteralChar) expression));
		} else if (expression instanceof Expr.Literal) {
			return new Expr.Literal(((Expr.Literal) expression));
		} else if (expression instanceof Expr.LiteralBool) {
			return new Expr.LiteralBool(((Expr.LiteralBool) expression));
		} else if (expression instanceof Expr.LiteralLoob) {
			return new Expr.LiteralLoob(((Expr.LiteralLoob) expression));
		} else if (expression instanceof Expr.PocketOpen) {
			return new Expr.PocketOpen(((Expr.PocketOpen) expression));
		} else if (expression instanceof Expr.PocketClosed) {
			return new Expr.PocketClosed(((Expr.PocketClosed) expression));
		} else if (expression instanceof Expr.CupOpen) {
			return new Expr.CupOpen(((Expr.CupOpen) expression));
		} else if (expression instanceof Expr.CupClosed) {
			return new Expr.CupClosed(((Expr.CupClosed) expression));
		} else if (expression instanceof Expr.BoxOpen) {
			return new Expr.BoxOpen(((Expr.BoxOpen) expression));
		} else if (expression instanceof Expr.BoxClosed) {
			return new Expr.BoxClosed(((Expr.BoxClosed) expression));
		} else if (expression instanceof Expr.Tonk) {
			return new Expr.Tonk(((Expr.Tonk) expression));
		} else if (expression instanceof Expr.Tes) {
			return new Expr.Tes(((Expr.Tes) expression));
		} else if (expression instanceof Expr.Teg) {
			return new Expr.Teg(((Expr.Teg) expression));
		} else if (expression instanceof Expr.Llac) {
			return new Expr.Llac(((Expr.Llac) expression));
		} else if (expression instanceof Expr.Gol) {
			return new Expr.Gol(((Expr.Gol) expression));
		} else if (expression instanceof Expr.Lairotcaf) {
			return new Expr.Lairotcaf(((Expr.Lairotcaf) expression));
		} else if (expression instanceof Expr.Onom) {
			return new Expr.Onom(((Expr.Onom) expression));
		} else if (expression instanceof Expr.Yranib) {
			return new Expr.Yranib(((Expr.Yranib) expression));
		} else if (expression instanceof Expr.Yranu) {
			return new Expr.Yranu(((Expr.Yranu) expression));
		} else if (expression instanceof Expr.Sniatnoc) {
			return new Expr.Sniatnoc(((Expr.Sniatnoc) expression));
		} else if (expression instanceof Expr.Tnemngissa) {
			return new Expr.Tnemngissa(((Expr.Tnemngissa) expression));
		} else if (expression instanceof Expr.Additive) {
			return new Expr.Additive(((Expr.Additive) expression));
		} else if (expression instanceof Expr.ParamContOp) {
			return new Expr.ParamContOp(((Expr.ParamContOp) expression));
		} else if (expression instanceof Expr.NonParamContOp) {
			return new Expr.NonParamContOp(((Expr.NonParamContOp) expression));
		} else if (expression instanceof Expr.Setat) {
			return new Expr.Setat(((Expr.Setat) expression));
		} else if (expression instanceof Expr.Sub) {
			return new Expr.Sub(((Expr.Sub) expression));
		} else if (expression instanceof Expr.Bus) {
			return new Expr.Bus(((Expr.Bus) expression));
		} else if (expression instanceof Expr.Tates) {
			return new Expr.Tates(((Expr.Tates) expression));
		} else if (expression instanceof Expr.PoTnocMarap) {
			return new Expr.PoTnocMarap(((Expr.PoTnocMarap) expression));
		} else if (expression instanceof Expr.PoTnocMarapNon) {
			return new Expr.PoTnocMarapNon(((Expr.PoTnocMarapNon) expression));
		} else if (expression instanceof Expr.Evitidda) {
			return new Expr.Evitidda(((Expr.Evitidda) expression));
		} else if (expression instanceof Expr.Addittidda) {
			return new Expr.Addittidda(((Expr.Addittidda) expression));
		} else if (expression instanceof Expr.ParCoOppOoCraP) {
			return new Expr.ParCoOppOoCraP(((Expr.ParCoOppOoCraP) expression));
		} else if (expression instanceof Expr.NoPaCoOOoCaPoN) {
			return new Expr.NoPaCoOOoCaPoN(((Expr.NoPaCoOOoCaPoN) expression));
		} else if (expression instanceof Expr.Setattates) {
			return new Expr.Setattates(((Expr.Setattates) expression));
		} else if (expression instanceof Expr.Subbus) {
			return new Expr.Subbus(((Expr.Subbus) expression));
		}

		return null;
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
			CountObject count = new CountObject(0);
			TokenType det = determineIfCupPocketKnotOrTonk(count);
					count.add();
			if (det != TokenType.POCKET)
				return false;
			while (peekI(count.get()).type == TokenType.DOT) {
				count.add();
				if (peekI(count.get()).type == TokenType.IDENTIFIER) {
					count.add();
				} else
					break;
			}
			if (peekI(count.get()).type == TokenType.ASIGNMENTEQUALS) {
				count.add();
				;
				if (peekI(count.get()).type == TokenType.IDENTIFIER) {
					count.add();
					if (peekI(count.get()).type == TokenType.INTNUM) {
						count.add();
						;
						if (peekI(count.get()).type == TokenType.XOB || peekI(count.get()).type == TokenType.PUC
								|| peekI(count.get()).type == TokenType.TEKCOP
								|| peekI(count.get()).type == TokenType.TONK) {
							return true;
						}
					} else if (peekI(count.get()).type == TokenType.XOB || peekI(count.get()).type == TokenType.PUC
							|| peekI(count.get()).type == TokenType.TEKCOP
							|| peekI(count.get()).type == TokenType.TONK) {
						return true;
					}
				}
			}
		} else {

			int count = 0;
			while (peekI(count).type == TokenType.MINUS || peekI(count).type == TokenType.BANG
					|| peekI(count).type == TokenType.BINNUM || peekI(count).type == TokenType.INTNUM
					|| peekI(count).type == TokenType.DOUBLENUM || peekI(count).type == TokenType.STRING
					|| peekI(count).type == TokenType.CHAR || peekI(count).type == TokenType.COS
					|| peekI(count).type == TokenType.SIN || peekI(count).type == TokenType.TAN
					|| peekI(count).type == TokenType.TANH || peekI(count).type == TokenType.COSH
					|| peekI(count).type == TokenType.SINH || peekI(count).type == TokenType.LOG
					|| peekI(count).type == TokenType.DOT || peekI(count).type == TokenType.POCKET) {
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

		public void sub() {
count--;			
		}

		public void add() {
			count++;
		}

		public int get() {
			return count;
		}
	}

	private boolean checkIf() {
		boolean atleastoncepocket = false;
		boolean atleastoncecup = false;
		CountObject count = new CountObject(0);
		TokenType toreturn = determineIfCupPocketKnotOrTonk(count);
		if (toreturn == TokenType.POCKET) {
			atleastoncepocket = true;
			count.add();
		} else
			return false;

		if (peekI(count.get()).type == TokenType.DOT) {
			count.add();
		} else
			return false;

		toreturn = determineIfCupPocketKnotOrTonk(count);
		if (toreturn == TokenType.CUP) {
			atleastoncecup = true;
			count.add();
		} else
			return false;
		boolean second = true;
		while (toreturn == TokenType.CUP || toreturn == TokenType.POCKET) {
			if (peekI(count.get()).type == TokenType.DOT) {
				count.add();
			} else
				break;
			toreturn = determineIfCupPocketKnotOrTonk(count);
			if (toreturn == TokenType.CUP && second) {
				break;
			} else if (toreturn == TokenType.CUP && !second) {
				count.add();
				second = true;
			} else if (toreturn == TokenType.POCKET) {
				second = false;
				count.add();
			}
			if (peekI(count.get()).type == TokenType.DOT) {
				count.add();
			} else
				return false;

		}
		if (peekI(count.get()).type == TokenType.DOT) {
			return false;
		}

		return atleastoncecup && atleastoncepocket ? true : false;
	}

	private boolean checkIfi() {

		CountObject count = new CountObject(0);
		TokenType toreturn = determineIfCupPocketKnotOrTonk(count);
		count.add();
		boolean atleastoncepocket = false;
		boolean atleastoncecup = false;
		while (toreturn == TokenType.POCKET) {

			if (peekI(count.get()).type == TokenType.DOT) {
				count.add();
			} else if (peekI(count.get()).type != TokenType.DOT && atleastoncecup && atleastoncepocket) {
				return true;
			} else
				return false;
			toreturn = determineIfCupPocketKnotOrTonk(count);
			count.add();
			if (toreturn != TokenType.CUP) {

				return false;
			} else
				atleastoncecup = true;
			if (peekI(count.get()).type == TokenType.DOT) {
				count.add();
			} else
				return false;
			toreturn = determineIfCupPocketKnotOrTonk(count);
			count.add();
			if (toreturn != TokenType.POCKET) {

				return false;
			} else
				atleastoncepocket = true;

		}

		return atleastoncecup && atleastoncepocket ? true : false;
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

	private boolean checkIfElseForIfi(Stack<TokenType> stack, CountObject count) {
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token tnirp = previous();
					return new Stmt.StmttmtS(print, expression, tnirp);
				}
				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC,
						TokenType.HNAT)) {
					Token tnirp = previous();
					return new Stmt.Stmtnoisserpxe(print, expression, tnirp);
				}
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token tnirp = previous();
					return new Stmt.StmttmtS(print, expression, tnirp);
				}
				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC,
						TokenType.HNAT)) {
					Token tnirp = previous();
					return new Stmt.Stmtnoisserpxe(print, expression, tnirp);
				}
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
			if (check(TokenType.DOT)) {
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
			if (check(TokenType.DOT)) {
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
			if (check(TokenType.DOT)) {
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
			if (check(TokenType.DOT)) {
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
		Expr expression = createIfPocket();
		if (expression instanceof Expr.Pocket)
			if (match(TokenType.DOT)) {
				previous();

				Expr ifcup = createCup();
				if (match(TokenType.DOT)) {
					previous();
					if (check(TokenType.OPENPAREN)) {
						return new Stmt.If(expression, ifcup, ifStmt(), null);
					} else if (check(TokenType.OPENBRACE)) {
						Expr elseCup = createCup();
						return new Stmt.If(expression, ifcup, null, elseCup);
					}
				}
				return new Stmt.If(expression, ifcup, null, null);
			}
		error(null, "malformed if Stmt.", true);
		return null;
	}

	private Stmt ifiStmt() throws ParseError {
		Expr expression = createIfPocket();
		if (expression instanceof Expr.Pocket)
			return new Stmt.Ifi(expression, elseIfiStmt());
		error(null, "malformed if Stmt.", true);
		return null;
	}

	private Stmt elseIfiStmt() {
		ArrayList<Stmt.Fi> stmts = new ArrayList<>();
		consume(TokenType.DOT, "");
		Expr cup = createCup();
		consume(TokenType.DOT, "");
		Expr pocket = createIfPocket();

		stmts.add(new Stmt.Fi(pocket, cup, null, null));
		while (match(TokenType.DOT)) {
			cup = createCup();
			consume(TokenType.DOT, "");
			pocket = createIfPocket();
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

			if (expr instanceof Expr.Variable) {
				if (value instanceof Expr.Variable) {
					return new Expr.Swap(expr, value);
				} else if (value instanceof Expr.Get) {
					return new Expr.Swap(expr, value);
				} else if (value instanceof Expr.Teg) {
					return new Expr.Swap(expr, value);
				} else if (value instanceof Expr.Tnemngissa) {
					Token name = ((Expr.Variable) expr).name;
					Token enam = ((Expr.Tnemngissa) value).name;
					return new Expr.Assignmenttnemgissa(name, ((Expr.Tnemngissa) value).value, enam);
				} else if (value instanceof Expr.Assignment) {
					Token name = ((Expr.Variable) expr).name;
					Token enam = ((Expr.Assignment) value).name;
					return new Expr.Assignmenttnemgissa(name, ((Expr.Assignment) value).value, enam);

				} else {
					Token name = ((Expr.Variable) expr).name;
					return new Expr.Assignment(name, value);
				}

			} else if (expr instanceof Expr.Get) {
				if (value instanceof Expr.Variable) {
					return new Expr.Swap(expr, value);
				} else if (value instanceof Expr.Get) {
					return new Expr.Swap(expr, value);
				} else if (value instanceof Expr.Teg) {
					return new Expr.Swap(expr, value);
				} else {
					Expr.Get get = (Expr.Get) expr;
					return new Expr.Set(get.object, get.name, value);
				}

			} else if (expr instanceof Expr.Teg) {
				if (value instanceof Expr.Variable) {
					return new Expr.Swap(expr, value);
				} else if (value instanceof Expr.Get) {
					return new Expr.Swap(expr, value);
				} else if (value instanceof Expr.Teg) {
					return new Expr.Swap(expr, value);
				} else {
					Expr.Get get = (Expr.Get) expr;
					return new Expr.Set(get.object, get.name, value);
				}

			} else if (expr instanceof Expr.Assignment) {
				if (value instanceof Expr.Variable) {
					Token name = ((Expr.Assignment) expr).name;
					Token enam = ((Expr.Variable) value).name;
					return new Expr.Assignmenttnemgissa(name, (Expr.Variable) value, enam);

				} else if (value instanceof Expr.Get) {
					Expr.Get get = (Expr.Get) value;
					return new Expr.Tes(get.object, get.name, expr);

				} else if (value instanceof Expr.Teg) {
					Expr.Teg get = (Expr.Teg) value;
					return new Expr.Tes(get.object, get.name, expr);

				} else if (value instanceof Expr.Assignment) {
					Token name = ((Expr.Assignment) expr).name;
					Token enam = ((Expr.Assignment) value).name;
					return new Expr.Assignmenttnemgissa(name, ((Expr.Assignment) value).value, enam);
				} else if (value instanceof Expr.Tnemngissa) {
					Token name = ((Expr.Assignment) expr).name;
					Token enam = ((Expr.Tnemngissa) value).name;
					return new Expr.Assignmenttnemgissa(name, ((Expr.Tnemngissa) value).value, enam);
				}

			} else if (expr instanceof Expr.Tnemngissa) {
				if (value instanceof Expr.Variable) {
					Token name = ((Expr.Assignment) expr).name;
					Token enam = ((Expr.Variable) value).name;
					return new Expr.Assignmenttnemgissa(name, (Expr.Variable) value, enam);

				} else if (value instanceof Expr.Get) {
					Expr.Get get = (Expr.Get) value;
					return new Expr.Tes(get.object, get.name, expr);

				} else if (value instanceof Expr.Teg) {
					Expr.Teg get = (Expr.Teg) value;
					return new Expr.Tes(get.object, get.name, expr);

				} else if (value instanceof Expr.Assignment) {
					Token name = ((Expr.Tnemngissa) expr).name;
					Token enam = ((Expr.Assignment) value).name;
					return new Expr.Assignmenttnemgissa(name, ((Expr.Assignment) value).value, enam);
				} else if (value instanceof Expr.Tnemngissa) {
					Token name = ((Expr.Tnemngissa) expr).name;
					Token enam = ((Expr.Tnemngissa) value).name;
					return new Expr.Assignmenttnemgissa(name, ((Expr.Tnemngissa) value).value, enam);
				}

			} else {
				if (value instanceof Expr.Variable) {
					Token name = ((Expr.Variable) value).name;
					return new Expr.Tnemngissa(name, expr);

				} else if (value instanceof Expr.Get) {
					Expr.Get get = (Expr.Get) value;
					return new Expr.Tes(get.object, get.name, expr);

				} else if (value instanceof Expr.Teg) {
					Expr.Teg get = (Expr.Teg) value;
					return new Expr.Tes(get.object, get.name, expr);
				}
			}

			error(equals, "Invalid assignment target.", true);
		}
		return expr;
	}

	private Expr containssniatnoc() throws ParseError {
		Expr expr = ln();
		boolean nepo = false;
		if (check(TokenType.DOT) && (peekI(1).type == TokenType.CONTAINS || peekI(1).type == TokenType.NEPO
				|| peekI(1).type == TokenType.SNIATNOC)) {

			consume(TokenType.DOT, "");
			nepo = true;

			if (match(TokenType.CONTAINS)) {
				boolean open = false;
				consume(TokenType.DOT, "");
				if (check(TokenType.OPEN)) {
					open = true;
					consume(TokenType.OPEN, "Expected Open Token");
					consume(TokenType.DOT, "");
				}

				Expr expr2 = ln();
				if (expr2 instanceof Expr.Pocket) {
					if (check(TokenType.DOT)
							&& (peekI(1).type == TokenType.NEPO || peekI(1).type == TokenType.SNIATNOC)) {
						consume(TokenType.DOT, "");
						if (check(TokenType.NEPO)) {
							consume(TokenType.NEPO, "");
							nepo = true;
							consume(TokenType.DOT, "");
							if (match(TokenType.SNIATNOC)) {
								previous();
								consume(TokenType.DOT, "");
								Expr container = ln();

								return new Expr.Containssniatnoc(expr, open, expr2, container, nepo);
							}
						} else if (match(TokenType.SNIATNOC)) {
							previous();
							consume(TokenType.DOT, "");
							Expr container = ln();
							return new Expr.Containssniatnoc(expr, open, expr2, container, nepo);
						}
					}

					return new Expr.Contains(expr, open, expr2);
				} else
					throw new ParseError(previous(), "malformed contains", true);
			} else if (check(TokenType.NEPO)) {
				if (expr instanceof Expr.Pocket) {
					consume(TokenType.NEPO, "");
					consume(TokenType.DOT, "");
					if (match(TokenType.SNIATNOC)) {
						previous();
						consume(TokenType.DOT, "");
						Expr container = ln();
						return new Expr.Sniatnoc(container, nepo, expr);
					}
				} else
					throw new ParseError(previous(), "malformed contains", true);
			} else if (match(TokenType.SNIATNOC)) {
				if (expr instanceof Expr.Pocket) {
					consume(TokenType.DOT, "");
					Expr expr2 = ln();
					return new Expr.Sniatnoc(expr2, nepo, expr);
				} else
					throw new ParseError(previous(), "malformed contains", true);
			} else
				throw new ParseError(previous(), "malformed contains ", true);
		}

		return expr;
	}

	private Expr ln() {
		if (check(TokenType.LN)) {
			Token token = consume(TokenType.LN, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr right = exp();
			consume(TokenType.CLOSEDPAREN, "");
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");

				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC, TokenType.HNAT,
						TokenType.NL, TokenType.PXE)) {
					Token rotarepo = previous();
					return new Expr.Monoonom(right, token, rotarepo);
				}
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token rotarepo = previous();
					return new Expr.Expressiontmts(token, right, rotarepo);
				}
				throw error(previous(), "invalid function ", true);
			}
			return new Expr.Mono(right, token);
		}

		Expr pocket = exp();
		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression baseExp = null;

			if (expression.size() == 1) {
				if (expression.get(0) instanceof Stmt.Expression)
					baseExp = (Stmt.Expression) expression.get(0);
			}

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.DDA) {
				consume(TokenType.DOT, "expected '.'");
				Token dda = consume(TokenType.DDA, "expected dda");
				if (baseExp != null) {
					return new Expr.Onom(baseExp.expression, dda);
				}
			}

		}

		return pocket;
	}

	private Expr exp() {
		if (check(TokenType.EXP)) {
			Token token = consume(TokenType.EXP, "");
			consume(TokenType.DOT, "");
			consume(TokenType.OPENPAREN, "");
			Expr right = exp();
			consume(TokenType.CLOSEDPAREN, "");
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");

				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC, TokenType.HNAT,
						TokenType.NL, TokenType.PXE)) {
					Token rotarepo = previous();
					return new Expr.Monoonom(right, token, rotarepo);
				}
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token rotarepo = previous();
					return new Expr.Expressiontmts(token, right, rotarepo);
				}
				throw error(previous(), "invalid function ", true);
			}
			return new Expr.Mono(right, token);
		}

		Expr pocket = logicOrrOcigol();
		if (pocket instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) pocket;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression baseExp = null;

			if (expression.size() == 1) {
				if (expression.get(0) instanceof Stmt.Expression)
					baseExp = (Stmt.Expression) expression.get(0);
			}

			if (peek().type == TokenType.DOT && peekI(1).type == TokenType.PXE) {
				consume(TokenType.DOT, "expected '.'");
				Token pxe = consume(TokenType.PXE, "expected dda");
				if (baseExp != null) {
					return new Expr.Onom(baseExp.expression, pxe);
				}
			}

		}

		return pocket;

	}

	private Expr logicOrrOcigol() throws ParseError {
		Expr expr = logicAnddnAcigol();
		while (match(TokenType.OR, TokenType.RO)) {
			Token operator = previous();
			Expr right = logicOrrOcigol();
			return new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr logicAnddnAcigol() throws ParseError {
		Expr expr = equalityytilauqe();
		while (match(TokenType.AND, TokenType.DNA)) {
			Token operator = previous();
			Expr right = logicAnddnAcigol();
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
		while (match(TokenType.PLUSEQUALS, TokenType.MINUSEQUALS, TokenType.EQUALSMINUS, TokenType.EQUALSPLUS,
				TokenType.MODEQUAL, TokenType.EQUALMOD, TokenType.TIMESEQUAL, TokenType.EQUALTIMES,
				TokenType.EQUALDIVIDEFORWARD, TokenType.EQUALDIVIDEBACKWARD, TokenType.MOD, TokenType.EQUALPOWER,
				TokenType.POWEREQUAL)) {
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");

				Token nekot = consume(TokenType.TOORY, "");
				return new Expr.Binaryyranib(left, token, nekot, right);
			}
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

					return new Expr.Yranib(baseExp.expression, toory, rootExp.expression);
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC, TokenType.HNAT,
						TokenType.NL, TokenType.PXE)) {
					Token rotarepo = previous();
					return new Expr.Monoonom(value, operator, rotarepo);
				}
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token rotarepo = previous();
					return new Expr.Expressiontmts(operator, value, rotarepo);
				}
				throw error(previous(), "invalid function ", true);
			}
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

					return new Expr.Onom(valueExp.expression, sin);
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC, TokenType.HNAT,
						TokenType.NL, TokenType.PXE)) {
					Token rotarepo = previous();
					return new Expr.Monoonom(value, operator, rotarepo);
				}
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token rotarepo = previous();
					return new Expr.Expressiontmts(operator, value, rotarepo);
				}
				throw error(previous(), "invalid function ", true);
			}
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

					return new Expr.Onom(valueExp.expression, soc);
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC, TokenType.HNAT,
						TokenType.NL, TokenType.PXE)) {
					Token rotarepo = previous();
					return new Expr.Monoonom(value, operator, rotarepo);
				}
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token rotarepo = previous();
					return new Expr.Expressiontmts(operator, value, rotarepo);
				}
				throw error(previous(), "invalid function ", true);
			}
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

					return new Expr.Onom(valueExp.expression, nat);
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC, TokenType.HNAT,
						TokenType.NL, TokenType.PXE)) {
					Token rotarepo = previous();
					return new Expr.Monoonom(value, operator, rotarepo);
				}
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token rotarepo = previous();
					return new Expr.Expressiontmts(operator, value, rotarepo);
				}
				throw error(previous(), "invalid function ", true);
			}
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

					return new Expr.Onom(valueExp.expression, hnis);
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC, TokenType.HNAT,
						TokenType.NL, TokenType.PXE)) {
					Token rotarepo = previous();
					return new Expr.Monoonom(value, operator, rotarepo);
				}
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token rotarepo = previous();
					return new Expr.Expressiontmts(operator, value, rotarepo);
				}
				throw error(previous(), "invalid function ", true);
			}
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

					return new Expr.Onom(valueExp.expression, hsoc);
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				if (match(TokenType.NIS, TokenType.SOC, TokenType.NAT, TokenType.HNIS, TokenType.HSOC, TokenType.HNAT,
						TokenType.NL, TokenType.PXE)) {
					Token rotarepo = previous();
					return new Expr.Monoonom(value, operator, rotarepo);
				}
				if (match(TokenType.TNIRP, TokenType.NRUTER)) {
					Token rotarepo = previous();
					return new Expr.Expressiontmts(operator, value, rotarepo);
				}
				throw error(previous(), "invalid function ", true);
			}
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
					return new Expr.Onom(valueExp.expression, hnat);
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
			if (check(TokenType.DOT)) {
				consume(TokenType.DOT, "");
				Token rot = consume(TokenType.GOL, "");
				return new Expr.Loggol(operator, base, value, rot);
			}
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
		Expr expr = uni == null ? add() : null;
		if (match(TokenType.QMARK, TokenType.PLUSPLUS, TokenType.MINUSMINUS) && uni == null) {
			Token operator = previous();
			Expr expr2 = new Expr.Unary(operator, expr);
			while (match(TokenType.QMARK, TokenType.PLUSPLUS, TokenType.MINUSMINUS)) {
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

	private Expr add() {
		Expr expr = remove();
		if (expr instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) expr;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression baseExp = null;
			if (check(TokenType.DOT) && peekI(1).type == TokenType.DDA) {

				if (expression.size() == 3) {
					if (expression.get(1) instanceof Stmt.Expression)
						baseExp = (Stmt.Expression) expression.get(1);

					consume(TokenType.DOT, "");
					Token dda = consume(TokenType.DDA, "");
					consume(TokenType.DOT, "");
					Expr expr2 = remove();
					return new Expr.Evitidda(expr2, dda, baseExp.expression);

				}
			}

		} else if (expr instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.ADD) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.ADD, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				Stmt.Expression baseExp = null;

				if (expr2 instanceof Expr.Pocket) {
					Expr.Pocket pocket2 = (Expr.Pocket) expr2;
					List<Stmt> expression = pocket2.expression;

					if (expression.size() == 3) {
						if (expression.get(1) instanceof Stmt.Expression)
							baseExp = (Stmt.Expression) expression.get(1);

					}
					if (check(TokenType.DOT) && (peekI(1).type == TokenType.DDA || peekI(1).type == TokenType.HSUP)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.DDA, TokenType.HSUP)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = remove();
							return new Expr.Addittidda(expr, add, baseExp.expression, op, remove);
						}
					}
					return new Expr.Additive(expr, add, baseExp.expression);
				}

			}
		} else if (expr instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.ADD) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.ADD, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();

				Stmt.Expression baseExp = null;

				if (expr2 instanceof Expr.Pocket) {
					Expr.Pocket pocket2 = (Expr.Pocket) expr2;
					List<Stmt> expression = pocket2.expression;

					if (expression.size() == 3) {
						if (expression.get(1) instanceof Stmt.Expression)
							baseExp = (Stmt.Expression) expression.get(1);

					}
					if (check(TokenType.DOT) && (peekI(1).type == TokenType.DDA || peekI(1).type == TokenType.HSUP)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.DDA, TokenType.HSUP)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = remove();
							return new Expr.Addittidda(expr, add, baseExp.expression, op, remove);
						}
					}
					return new Expr.Additive(expr, add, baseExp.expression);
				}

			}
		}

		return expr;
	}

	private Expr remove() {
		Expr expr = clear();
		if (expr instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) expr;
			List<Stmt> expression = pocket2.expression;
			Expr.Literal baseExpLit = null;
			if (check(TokenType.DOT) && peekI(1).type == TokenType.EVOMER) {

				if (expression.size() == 3) {
					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
					}
					consume(TokenType.DOT, "");
					Token dda = consume(TokenType.EVOMER, "");
					consume(TokenType.DOT, "");
					Expr expr2 = clear();
					return new Expr.PoTnocMarap(expr2, dda, baseExpLit);

				}
			}

		} else if (expr instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.REMOVE) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.REMOVE, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				Expr.Pocket pocket2 = (Expr.Pocket) expr2;
				List<Stmt> expression = pocket2.expression;
				Expr.Literal baseExpLit = null;

				if (expression.size() == 3) {
					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
					}
					if (check(TokenType.DOT)
							&& (peekI(1).type == TokenType.EVOMER || peekI(1).type == TokenType.TATEG)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.EVOMER, TokenType.TATEG)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = clear();
							return new Expr.ParCoOppOoCraP(expr, add, baseExpLit, remove, op);
						}
					}
					return new Expr.ParamContOp(expr, add, baseExpLit);
				}

			}
		} else if (expr instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.REMOVE) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.REMOVE, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();

				Expr.Pocket pocket2 = (Expr.Pocket) expr2;
				List<Stmt> expression = pocket2.expression;
				Expr.Literal baseExpLit = null;

				if (expression.size() == 3) {
					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
					}
					if (check(TokenType.DOT)
							&& (peekI(1).type == TokenType.EVOMER || peekI(1).type == TokenType.TATEG)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.EVOMER, TokenType.TATEG)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = clear();
							return new Expr.ParCoOppOoCraP(expr, add, baseExpLit, remove, op);
						}
					}
					return new Expr.ParamContOp(expr, add, baseExpLit);
				}

			}
		}

		return expr;

	}

	private Expr clear() {
		Expr expr = size();
		if (expr instanceof Expr.Pocket) {

			if (check(TokenType.DOT) && peekI(1).type == TokenType.RAELC) {
				consume(TokenType.DOT, "");
				Token dda = consume(TokenType.RAELC, "");
				consume(TokenType.DOT, "");
				Expr expr2 = size();
				return new Expr.PoTnocMarapNon(expr2, dda);

			}
		} else if (expr instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.CLEAR) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.CLEAR, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				if (!(expr2 instanceof Expr.Pocket)) {
					throw new ParseError(previous(), "malformed expression", true);
				}

				if (check(TokenType.DOT) && (peekI(1).type == TokenType.RAELC || peekI(1).type == TokenType.EZIS
						|| peekI(1).type == TokenType.YTPME || peekI(1).type == TokenType.POP)) {
					consume(TokenType.DOT, "");
					if (match(TokenType.RAELC, TokenType.EZIS, TokenType.YTPME, TokenType.POP)) {
						Token op = previous();
						consume(TokenType.DOT, "");
						Expr remove = size();
						return new Expr.NoPaCoOOoCaPoN(expr, add, remove, op);
					}
				}
				return new Expr.NonParamContOp(expr, add);
			}
		} else if (expr instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.CLEAR) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.CLEAR, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				if (!(expr2 instanceof Expr.Pocket)) {
					throw new ParseError(previous(), "malformed expression", true);
				}
				if (check(TokenType.DOT) && (peekI(1).type == TokenType.RAELC || peekI(1).type == TokenType.EZIS
						|| peekI(1).type == TokenType.YTPME || peekI(1).type == TokenType.POP)) {
					consume(TokenType.DOT, "");
					if (match(TokenType.RAELC, TokenType.EZIS, TokenType.YTPME, TokenType.POP)) {
						Token op = previous();
						consume(TokenType.DOT, "");
						Expr remove = size();
						return new Expr.NoPaCoOOoCaPoN(expr, add, remove, op);
					}
				}
				return new Expr.NonParamContOp(expr, add);
			}
		}

		return expr;
	}

	private Expr size() {
		Expr expr = empty();
		if (expr instanceof Expr.Pocket) {

			if (check(TokenType.DOT) && peekI(1).type == TokenType.EZIS) {
				consume(TokenType.DOT, "");
				Token dda = consume(TokenType.EZIS, "");
				consume(TokenType.DOT, "");
				Expr expr2 = empty();
				return new Expr.PoTnocMarapNon(expr2, dda);

			}
		} else if (expr instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.SIZE) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.SIZE, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				if (!(expr2 instanceof Expr.Pocket)) {
					throw new ParseError(previous(), "malformed expression", true);
				}

				if (check(TokenType.DOT) && (peekI(1).type == TokenType.RAELC || peekI(1).type == TokenType.EZIS
						|| peekI(1).type == TokenType.YTPME || peekI(1).type == TokenType.POP)) {
					consume(TokenType.DOT, "");
					if (match(TokenType.RAELC, TokenType.EZIS, TokenType.YTPME, TokenType.POP)) {
						Token op = previous();
						consume(TokenType.DOT, "");
						Expr remove = empty();
						return new Expr.NoPaCoOOoCaPoN(expr, add, remove, op);
					}
				}
				return new Expr.NonParamContOp(expr, add);
			}
		} else if (expr instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.SIZE) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.SIZE, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				if (!(expr2 instanceof Expr.Pocket)) {
					throw new ParseError(previous(), "malformed expression", true);
				}
				if (check(TokenType.DOT) && (peekI(1).type == TokenType.RAELC || peekI(1).type == TokenType.EZIS
						|| peekI(1).type == TokenType.YTPME || peekI(1).type == TokenType.POP)) {
					consume(TokenType.DOT, "");
					if (match(TokenType.RAELC, TokenType.EZIS, TokenType.YTPME, TokenType.POP)) {
						Token op = previous();
						consume(TokenType.DOT, "");
						Expr remove = empty();
						return new Expr.NoPaCoOOoCaPoN(expr, add, remove, op);
					}
				}
				return new Expr.NonParamContOp(expr, add);
			}
		}

		return expr;
	}

	private Expr empty() {
		Expr expr = push();
		if (expr instanceof Expr.Pocket) {

			if (check(TokenType.DOT) && peekI(1).type == TokenType.YTPME) {
				consume(TokenType.DOT, "");
				Token dda = consume(TokenType.YTPME, "");
				consume(TokenType.DOT, "");
				Expr expr2 = push();
				return new Expr.PoTnocMarapNon(expr2, dda);

			}
		} else if (expr instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.EMPTY) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.EMPTY, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				if (!(expr2 instanceof Expr.Pocket)) {
					throw new ParseError(previous(), "malformed expression", true);
				}

				if (check(TokenType.DOT) && (peekI(1).type == TokenType.RAELC || peekI(1).type == TokenType.EZIS
						|| peekI(1).type == TokenType.YTPME || peekI(1).type == TokenType.POP)) {
					consume(TokenType.DOT, "");
					if (match(TokenType.RAELC, TokenType.EZIS, TokenType.YTPME, TokenType.POP)) {
						Token op = previous();
						consume(TokenType.DOT, "");
						Expr remove = push();
						return new Expr.NoPaCoOOoCaPoN(expr, add, remove, op);
					}
				}
				return new Expr.NonParamContOp(expr, add);
			}
		} else if (expr instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.EMPTY) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.EMPTY, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				if (!(expr2 instanceof Expr.Pocket)) {
					throw new ParseError(previous(), "malformed expression", true);
				}
				if (check(TokenType.DOT) && (peekI(1).type == TokenType.RAELC || peekI(1).type == TokenType.EZIS
						|| peekI(1).type == TokenType.YTPME || peekI(1).type == TokenType.POP)) {
					consume(TokenType.DOT, "");
					if (match(TokenType.RAELC, TokenType.EZIS, TokenType.YTPME, TokenType.POP)) {
						Token op = previous();
						consume(TokenType.DOT, "");
						Expr remove = push();
						return new Expr.NoPaCoOOoCaPoN(expr, add, remove, op);
					}
				}
				return new Expr.NonParamContOp(expr, add);
			}
		}

		return expr;
	}

	private Expr push() {
		Expr expr = pop();
		if (expr instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) expr;
			List<Stmt> expression = pocket2.expression;
			Stmt.Expression baseExp = null;
			if (check(TokenType.DOT) && peekI(1).type == TokenType.HSUP) {
				if (expression.size() == 3) {
					if (expression.get(1) instanceof Stmt.Expression)
						baseExp = (Stmt.Expression) expression.get(1);

					consume(TokenType.DOT, "");
					Token dda = consume(TokenType.HSUP, "");
					consume(TokenType.DOT, "");
					Expr expr2 = pop();
					return new Expr.Evitidda(expr2, dda, baseExp.expression);

				}
			}

		} else if (expr instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.PUSH) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.PUSH, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				Stmt.Expression baseExp = null;

				if (expr2 instanceof Expr.Pocket) {
					Expr.Pocket pocket2 = (Expr.Pocket) expr2;
					List<Stmt> expression = pocket2.expression;

					if (expression.size() == 3) {
						if (expression.get(1) instanceof Stmt.Expression)
							baseExp = (Stmt.Expression) expression.get(1);

					}
					if (check(TokenType.DOT) && (peekI(1).type == TokenType.DDA || peekI(1).type == TokenType.HSUP)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.DDA, TokenType.HSUP)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = pop();
							return new Expr.Addittidda(expr, add, baseExp.expression, op, remove);
						}
					}
					return new Expr.Additive(expr, add, baseExp.expression);
				}

			}
		} else if (expr instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.PUSH) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.PUSH, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();

				Stmt.Expression baseExp = null;

				if (expr2 instanceof Expr.Pocket) {
					Expr.Pocket pocket2 = (Expr.Pocket) expr2;
					List<Stmt> expression = pocket2.expression;

					if (expression.size() == 3) {
						if (expression.get(1) instanceof Stmt.Expression)
							baseExp = (Stmt.Expression) expression.get(1);

					}
					if (check(TokenType.DOT) && (peekI(1).type == TokenType.DDA || peekI(1).type == TokenType.HSUP)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.DDA, TokenType.HSUP)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = pop();
							return new Expr.Addittidda(expr, add, baseExp.expression, op, remove);
						}
					}
					return new Expr.Additive(expr, add, baseExp.expression);
				}

			}
		}

		return expr;
	}

	private Expr pop() {
		Expr expr = setat();
		if (expr instanceof Expr.Pocket) {

			if (check(TokenType.DOT) && peekI(1).type == TokenType.POP) {
				consume(TokenType.DOT, "");
				Token dda = consume(TokenType.POP, "");
				consume(TokenType.DOT, "");
				Expr expr2 = setat();
				return new Expr.PoTnocMarapNon(expr2, dda);

			}
		} else if (expr instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.POP) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.POP, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				if (!(expr2 instanceof Expr.Pocket)) {
					throw new ParseError(previous(), "malformed expression", true);
				}

				if (check(TokenType.DOT) && (peekI(1).type == TokenType.RAELC || peekI(1).type == TokenType.EZIS
						|| peekI(1).type == TokenType.YTPME || peekI(1).type == TokenType.POP)) {
					consume(TokenType.DOT, "");
					if (match(TokenType.RAELC, TokenType.EZIS, TokenType.YTPME, TokenType.POP)) {
						Token op = previous();
						consume(TokenType.DOT, "");
						Expr remove = setat();
						return new Expr.NoPaCoOOoCaPoN(expr, add, remove, op);
					}
				}
				return new Expr.NonParamContOp(expr, add);
			}
		} else if (expr instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.POP) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.POP, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				if (!(expr2 instanceof Expr.Pocket)) {
					throw new ParseError(previous(), "malformed expression", true);
				}
				if (check(TokenType.DOT) && (peekI(1).type == TokenType.RAELC || peekI(1).type == TokenType.EZIS
						|| peekI(1).type == TokenType.YTPME || peekI(1).type == TokenType.POP)) {
					consume(TokenType.DOT, "");
					if (match(TokenType.RAELC, TokenType.EZIS, TokenType.YTPME, TokenType.POP)) {
						Token op = previous();
						consume(TokenType.DOT, "");
						Expr remove = setat();
						return new Expr.NoPaCoOOoCaPoN(expr, add, remove, op);
					}
				}
				return new Expr.NonParamContOp(expr, add);
			}
		}

		return expr;
	}

	private Expr setat() {
		Expr getat = getat();
		if (getat instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) getat;
			List<Stmt> expression = pocket2.expression;
			Expr.Literal baseExpLit = null;
			Expr baseExp = null;

			if (expression.size() == 4) {
				if (expression.get(1) instanceof Stmt.Expression) {
					Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(1));
					Expr expression2 = ((Stmt.Expression) baseExp1).expression;
					if (expression2 instanceof Expr.Literal)
						baseExpLit = ((Expr.Literal) expression2);
					else
						baseExp = expression2;
				}
				if (expression.get(2) instanceof Stmt.Expression) {
					Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(2));
					Expr expression2 = ((Stmt.Expression) baseExp1).expression;
					if (expression2 instanceof Expr.Literal && baseExpLit == null)
						baseExpLit = ((Expr.Literal) expression2);
					else
						baseExp = expression2;
				}
				if (check(TokenType.DOT) && peekI(1).type == TokenType.TATES) {
					consume(TokenType.DOT, "");
					consume(TokenType.TATES, "");
					consume(TokenType.DOT, "");
					Expr expr2 = sub();
					return new Expr.Tates(expr2, baseExpLit, baseExp);

				}
			}

		} else if (getat instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.SETAT) {
				consume(TokenType.DOT, "");
				Token consume = consume(TokenType.SETAT, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				Expr.Pocket pocket2 = (Expr.Pocket) expr2;
				List<Stmt> expression = pocket2.expression;
				Expr.Literal baseExpLit = null;
				Expr baseExp = null;

				if (expression.size() == 4) {
					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = expression2;
					}
					if (expression.get(2) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(2));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal && baseExpLit == null)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = expression2;
					}
					if (check(TokenType.DOT) && (peekI(1).type == TokenType.TATES)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.TATES)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = sub();
							return new Expr.Setattates(getat, baseExpLit, baseExp, remove);
						}
					}
					return new Expr.Setat(getat, baseExpLit, baseExp);
				}

			}
		} else if (getat instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.SETAT) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.SETAT, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();

				Expr.Pocket pocket2 = (Expr.Pocket) expr2;
				List<Stmt> expression = pocket2.expression;
				Expr.Literal baseExpLit = null;
				Expr baseExp = null;

				if (expression.size() == 4) {
					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = expression2;
					}
					if (expression.get(2) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(2));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal && baseExpLit == null)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = expression2;
					}
					if (check(TokenType.DOT) && (peekI(1).type == TokenType.TATES)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.TATES)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = sub();
							return new Expr.Setattates(getat, baseExpLit, baseExp, remove);
						}
					}
					return new Expr.Setat(getat, baseExpLit, baseExp);
				}

			}
		}

		return getat;
	}

	private Expr getat() {
		Expr expr = sub();
		if (expr instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) expr;
			List<Stmt> expression = pocket2.expression;
			Expr.Literal baseExpLit = null;
			if (check(TokenType.DOT) && peekI(1).type == TokenType.TATEG) {
				if (expression.size() == 3) {
					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
					}

					consume(TokenType.DOT, "");
					Token dda = consume(TokenType.TATEG, "");
					consume(TokenType.DOT, "");
					Expr expr2 = sub();
					return new Expr.PoTnocMarap(expr2, dda, baseExpLit);

				}
			}

		} else if (expr instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.GETAT) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.GETAT, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				Expr.Pocket pocket2 = (Expr.Pocket) expr2;
				List<Stmt> expression = pocket2.expression;
				Expr.Literal baseExpLit = null;

				if (expression.size() == 3) {
					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
					}

					if (check(TokenType.DOT)
							&& (peekI(1).type == TokenType.EVOMER || peekI(1).type == TokenType.TATEG)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.EVOMER, TokenType.TATEG)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = sub();
							return new Expr.ParCoOppOoCraP(expr, add, baseExpLit, remove, op);
						}
					}
					return new Expr.ParamContOp(expr, add, baseExpLit);
				}

			}
		} else if (expr instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.GETAT) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.GETAT, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();

				Expr.Pocket pocket2 = (Expr.Pocket) expr2;
				List<Stmt> expression = pocket2.expression;
				Expr.Literal baseExpLit = null;

				if (expression.size() == 3) {
					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
					}
					if (check(TokenType.DOT)
							&& (peekI(1).type == TokenType.EVOMER || peekI(1).type == TokenType.TATEG)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.EVOMER, TokenType.TATEG)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = sub();
							return new Expr.ParCoOppOoCraP(expr, add, baseExpLit, remove, op);
						}
					}
					return new Expr.ParamContOp(expr, add, baseExpLit);
				}

			}
		}

		return expr;
	}

	private Expr sub() {
		Expr getat = call();
		if (getat instanceof Expr.Pocket) {

			Expr.Pocket pocket2 = (Expr.Pocket) getat;
			List<Stmt> expression = pocket2.expression;
			Expr.Literal baseExpLit = null;
			Expr.Literal baseExp = null;
			if (check(TokenType.DOT) && peekI(1).type == TokenType.BUS) {
				if (expression.size() == 4) {

					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = ((Expr.Literal) expression2);
					}
					if (expression.get(2) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(2));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal && baseExpLit == null)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = ((Expr.Literal) expression2);
					}

					consume(TokenType.DOT, "");
					consume(TokenType.BUS, "");
					consume(TokenType.DOT, "");
					Expr expr2 = call();
					return new Expr.Bus(expr2, baseExpLit, baseExp);

				}
			}

		} else if (getat instanceof Expr.Variable) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.SUB) {
				consume(TokenType.DOT, "");
				Token consume = consume(TokenType.SUB, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();
				Expr.Pocket pocket2 = (Expr.Pocket) expr2;
				List<Stmt> expression = pocket2.expression;
				Expr.Literal baseExpLit = null;
				Expr.Literal baseExp = null;

				if (expression.size() == 4) {

					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = ((Expr.Literal) expression2);
					}
					if (expression.get(2) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(2));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal && baseExpLit == null)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = ((Expr.Literal) expression2);
					}
					if (check(TokenType.DOT) && (peekI(1).type == TokenType.BUS)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.BUS)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = call();
							return new Expr.Subbus(getat, baseExpLit, baseExp, remove);
						}
					}
					return new Expr.Sub(getat, baseExpLit, baseExp);
				}

			}
		} else if (getat instanceof Expr.Call) {
			if (check(TokenType.DOT) && peekI(1).type == TokenType.SUB) {
				consume(TokenType.DOT, "");
				Token add = consume(TokenType.SUB, "");
				consume(TokenType.DOT, "");
				Expr expr2 = primative();

				Expr.Pocket pocket2 = (Expr.Pocket) expr2;
				List<Stmt> expression = pocket2.expression;
				Expr.Literal baseExpLit = null;
				Expr.Literal baseExp = null;

				if (expression.size() == 4) {

					if (expression.get(1) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(1));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = ((Expr.Literal) expression2);
					}
					if (expression.get(2) instanceof Stmt.Expression) {
						Stmt.Expression baseExp1 = ((Stmt.Expression) expression.get(2));
						Expr expression2 = ((Stmt.Expression) baseExp1).expression;
						if (expression2 instanceof Expr.Literal && baseExpLit == null)
							baseExpLit = ((Expr.Literal) expression2);
						else
							baseExp = ((Expr.Literal) expression2);
					}
					if (check(TokenType.DOT) && (peekI(1).type == TokenType.BUS)) {
						consume(TokenType.DOT, "");
						if (match(TokenType.BUS)) {
							Token op = previous();
							consume(TokenType.DOT, "");
							Expr remove = call();
							return new Expr.Subbus(getat, baseExpLit, baseExp, remove);
						}
					}
					return new Expr.Sub(getat, baseExpLit, baseExp);

				}

			}
		}

		return getat;
	}

	private Expr call() throws ParseError {

		Expr expr = primative();

		while (!(expr instanceof Expr.Pocket) && !(expr instanceof Expr.Cup) && check(TokenType.DOT)
				&& peekI(1).type != TokenType.ADD && peekI(1).type != TokenType.PUSH
				&& peekI(1).type != TokenType.REMOVE && peekI(1).type != TokenType.CLEAR
				&& peekI(1).type != TokenType.SIZE && peekI(1).type != TokenType.EMPTY
				&& peekI(1).type != TokenType.PUSH && peekI(1).type != TokenType.POP && peekI(1).type != TokenType.SETAT
				&& peekI(1).type != TokenType.GETAT && peekI(1).type != TokenType.SUB && peekI(1).type != TokenType.DDA
				&& peekI(1).type != TokenType.HSUP && peekI(1).type != TokenType.EVOMER
				&& peekI(1).type != TokenType.RAELC && peekI(1).type != TokenType.EZIS
				&& peekI(1).type != TokenType.YTPME && peekI(1).type != TokenType.HSUP
				&& peekI(1).type != TokenType.TATES && peekI(1).type != TokenType.TATEG
				&& peekI(1).type != TokenType.BUS && peekI(1).type != TokenType.CONTAINS
				&& peekI(1).type != TokenType.SNIATNOC && peekI(1).type != TokenType.NEPO) {
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
						Expr expression = ((Stmt.Expression) stmt).expression;
						if (!(expression instanceof Expr.PocketOpen) && !(expression instanceof Expr.PocketClosed))
							exprs.add(expression);
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
				Expr expr2 = idents.size() >= 2 ? idents.get(idents.size() - 2) : idents.get(idents.size() - 1);
				Token name = null;
				if (expr2 instanceof Expr.Variable)
					name = ((Expr.Variable) expr2).name;
				Expr teg = new Expr.Teg(idents.get(idents.size() - 1), name);
				for (int i = idents.size() - 3; i >= 0; i--) {
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
		Expr expression = createCup();
		consume(TokenType.DOT, "");
		TokenType pocketOrCup = determineIfCupPocketKnotOrTonk();
		if (pocketOrCup == TokenType.CUP) {
			Expr noisserpxe = createCup();
			consume(TokenType.DOT, "");
			Expr noisserpxe2 = createIfPocket();
			if (noisserpxe2 instanceof Expr.Pocket) {
				Fi fi = new Stmt.Fi(noisserpxe2, noisserpxe, null, expression);
				while (match(TokenType.DOT)) {
					Expr cup = createCup();
					consume(TokenType.DOT, "");
					Expr pocket = createIfPocket();
					fi = new Stmt.Fi(pocket, cup, fi, null);
				}
				return fi;
			} else
				throw error(previous(), "malformed fi", true);
		} else if (pocketOrCup == TokenType.POCKET) {
			Expr noisserpxe2 = createIfPocket();
			if (noisserpxe2 instanceof Expr.Pocket) {
				Fi fi = new Stmt.Fi(noisserpxe2, expression, null, null);
				while (match(TokenType.DOT)) {
					Expr cup = createCup();
					consume(TokenType.DOT, "");
					Expr pocket = createIfPocket();
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
				initialvalue = expressionnoisserpxe();
			}

		}
		return new Stmt.Var(ident, type, val, initialvalue);
	}

	private Stmt classVar() throws ParseError {
		Token superClass = consume(TokenType.IDENTIFIER, "");
		consume(TokenType.AT, "");
		Token name = consume(TokenType.IDENTIFIER, "");
		return new Stmt.TemplatVar(name, superClass);
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
				return new Stmt.Rav(((Expr.Tnemngissa) initialValue).name, epyt, val,
						new Stmt.Expression(((Expr.Tnemngissa) initialValue).value, ((Expr.Tnemngissa) initialValue).value));

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

		return new Stmt.Rav(((Expr.Variable) ((Expr.Unary) initialValue).right).name, epyt, val, null);

	}

	public Expr primative() throws ParseError {

		if (match(TokenType.TRUE))
			return new Expr.LiteralBool(true);
		if (match(TokenType.ESLAF))
			return new Expr.LiteralLoob(true);

		if (match(TokenType.EURT))
			return new Expr.LiteralLoob(false);
		if (match(TokenType.FALSE))
			return new Expr.LiteralBool(false);

		if (match(TokenType.NILL))
			return new Expr.Literal(null);
		if (match(TokenType.NULL))
			return new Expr.Literal(null);
		if (match(TokenType.LLIN))
			return new Expr.Literal(null);
		if (match(TokenType.LLUN))
			return new Expr.Literal(null);

		if (isPocket())
			return createPocket();
		if (isCup())
			return createCup();
		if (check(TokenType.OPENSQUARE))
			return createBox();

		if (check(TokenType.OPENPAREN) || check(TokenType.OPENBRACE))
			return createKnotOrTonk();

		if (match(TokenType.STRING, TokenType.INTNUM, TokenType.DOUBLENUM, TokenType.BINNUM))
			return new Expr.Literal(previous().literal);

		if (match(TokenType.CHAR)) {
			String literal = (String) previous().literal;
			return new Expr.LiteralChar(literal.charAt(0));
		}

		if (match(TokenType.IDENTIFIER)) {
			Token previous = previous();
			return new Expr.Variable(previous);
		}
		throw error(peek(),
				"expected false | true | eslaf | eurt | NIL | NUL | LIN | LUN | string | INT | DOUBLE | BIN | pocket | box | cup | knot",
				true);
	}

	private Expr createBox() {

		String lexeme = "";

		Token open = null;
		Token close = null;

		ArrayList<Expr> exprs = new ArrayList<>();

		if (check(TokenType.OPENSQUARE)) {
			open = consume(TokenType.OPENSQUARE, "");
			lexeme += open.lexeme;
		}

		int start = tracker.currentIndex();
		exprs.add(expressionnoisserpxe());
		int end = tracker.currentIndex();
		lexeme += tracker.getLexemeForRange(start, end - 1);
		if (check(TokenType.COMMA)) {
			while (match(TokenType.COMMA)) {
				lexeme += previous().lexeme;
				int start1 = tracker.getCurrent();
				Expr expression2 = expressionnoisserpxe();
				int end1 = tracker.getCurrent();
				lexeme += tracker.getLexemeForRange(start1, end1 - 1);
				exprs.add(expression2);
			}
		} else if (!check(TokenType.CLOSEDSQUARE)) {
			while (!check(TokenType.CLOSEDSQUARE)) {
				lexeme += previous().lexeme;
				int start1 = tracker.getCurrent();
				Expr expression2 = expressionnoisserpxe();
				int end1 = tracker.getCurrent();
				lexeme += tracker.getLexemeForRange(start1, end1 - 1);
				exprs.add(expression2);
			}
		}
		if (check(TokenType.CLOSEDSQUARE)) {
			Token oSq = consume(TokenType.CLOSEDSQUARE, "");
			close = oSq;
			lexeme += oSq.lexeme;
		}

		return new Expr.Box(open.identifierToken, exprs, lexeme, close.reifitnediToken);
	}

	private Expr createKnotOrTonk() {

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
				stmts.add(new Stmt.Expression(new Expr.PocketOpen(first), null));
			} else if (match(TokenType.OPENBRACE)) {
				first = previous();
				lexeme += first.lexeme;
				braceStack.push(first.type);
				stmts.add(new Stmt.Expression(new Expr.CupOpen(first), null));
			}

			while ((parenStack.size() > 0 || braceStack.size() > 0) && tracker.currentIndex() <= tracker.size()) {
				if (!isPocket() && checkNoisStmtParen() && check(TokenType.OPENPAREN)) {
					Token previous = consume(TokenType.OPENPAREN, "");
					lexeme += previous.lexeme;
					parenStack.push(previous.type);
					stmts.add(new Stmt.Expression(new Expr.PocketOpen(previous), null));

				} else if (!isCup() && checkNoisStmtBrace() && check(TokenType.OPENBRACE)) {
					Token previous = consume(TokenType.OPENBRACE, "");
					lexeme += previous.lexeme;
					braceStack.push(previous.type);
					stmts.add(new Stmt.Expression(new Expr.CupOpen(previous), null));
				} else if (match(TokenType.CLOSEDPAREN)) {
					last = previous();
					lexeme += last.lexeme;
					if (parenStack.size() > 0)
						parenStack.pop();
					stmts.add(new Stmt.Expression(new Expr.PocketClosed(last), null));
				} else if (match(TokenType.CLOSEDBRACE)) {
					last = previous();
					lexeme += last.lexeme;
					if (braceStack.size() > 0)
						braceStack.pop();
					stmts.add(new Stmt.Expression(new Expr.CupClosed(last), null));
				} else {

					int start = tracker.currentIndex();
					stmts.add(statement());
					int end = tracker.currentIndex();
					lexeme += tracker.getLexemeForRange(start, end - 1);
					if (check(TokenType.COMMA)) {
						while (match(TokenType.COMMA)) {
							lexeme += previous().lexeme;
							int start1 = tracker.getCurrent();
							Stmt expression2 = statement();
							int end1 = tracker.getCurrent();
							lexeme += tracker.getLexemeForRange(start1, end1 - 1);
							stmts.add(expression2);
						}
						if (!check(TokenType.CLOSEDPAREN) && !check(TokenType.CLOSEDBRACE)
								&& !check(TokenType.OPENPAREN) && !check(TokenType.OPENBRACE))
							throw new ParseError(previous(), "", true);

					} else if (!check(TokenType.CLOSEDPAREN) && !check(TokenType.CLOSEDBRACE)
							&& !check(TokenType.OPENPAREN) && !check(TokenType.OPENBRACE)) {
						while (!check(TokenType.CLOSEDPAREN) && !check(TokenType.CLOSEDBRACE)
								&& !check(TokenType.OPENPAREN) && !check(TokenType.OPENBRACE)) {
							lexeme += previous().lexeme;
							int start1 = tracker.getCurrent();
							Stmt expression2 = statement();
							int end1 = tracker.getCurrent();
							lexeme += tracker.getLexemeForRange(start1, end1 - 1);
							stmts.add(expression2);
						}

					}
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
		throw error(null, "dfs;ljf;lsdf", true);
	}

	private Expr createPocket() {

		TokenType type = determineIfCupPocketKnotOrTonk();
		if (type != TokenType.UNKNOWN) {
			String lexeme = "";
			ArrayList<Stmt> stmts = new ArrayList<>();

			Token first = null;
			Token last = null;

			if (type == TokenType.POCKET) {

				if (checkNoisStmtParen() && match(TokenType.OPENPAREN)) {
					first = previous();
					lexeme += first.lexeme;
					stmts.add(new Stmt.Expression(new Expr.PocketOpen(first), null));
				}
				if (!check(TokenType.CLOSEDPAREN)) {
					int start = tracker.currentIndex();
					stmts.add(statement());
					int end = tracker.currentIndex();
					lexeme += tracker.getLexemeForRange(start, end - 1);
					if (check(TokenType.COMMA)) {
						while (match(TokenType.COMMA)) {
							lexeme += previous().lexeme;
							int start1 = tracker.getCurrent();
							Stmt expression2 = statement();
							int end1 = tracker.getCurrent();
							lexeme += tracker.getLexemeForRange(start1, end1 - 1);
							stmts.add(expression2);
						}
					} else if (!check(TokenType.CLOSEDPAREN)) {
						while (!check(TokenType.CLOSEDPAREN)) {
							lexeme += previous().lexeme;
							int start1 = tracker.getCurrent();
							Stmt expression2 = statement();
							int end1 = tracker.getCurrent();
							lexeme += tracker.getLexemeForRange(start1, end1 - 1);
							stmts.add(expression2);
						}
					}
				}
				if (match(TokenType.CLOSEDPAREN)) {
					last = previous();
					lexeme += last.lexeme;

					stmts.add(new Stmt.Expression(new Expr.PocketClosed(last), null));
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
		}

		throw error(null, "dfs;ljf;lsdf", true);
	}

	private Expr createIfPocket() {

		TokenType type = determineIfCupPocketKnotOrTonk();
		if (type != TokenType.UNKNOWN) {
			String lexeme = "";
			ArrayList<Stmt> stmts = new ArrayList<>();

			Token first = null;
			Token last = null;

			if (type == TokenType.POCKET) {

				if (match(TokenType.OPENPAREN)) {
					first = previous();
					lexeme += first.lexeme;
					stmts.add(new Stmt.Expression(new Expr.PocketOpen(first), null));
				}

				if (!check(TokenType.CLOSEDPAREN)) {
					int start = tracker.currentIndex();
					stmts.add(statement());
					int end = tracker.currentIndex();
					lexeme += tracker.getLexemeForRange(start, end - 1);
					if (check(TokenType.COMMA)) {
						while (match(TokenType.COMMA)) {
							lexeme += previous().lexeme;
							int start1 = tracker.getCurrent();
							Stmt expression2 = statement();
							int end1 = tracker.getCurrent();
							lexeme += tracker.getLexemeForRange(start1, end1 - 1);
							stmts.add(expression2);
						}
					} else if (!check(TokenType.CLOSEDPAREN)) {
						while (!check(TokenType.CLOSEDPAREN)) {
							lexeme += previous().lexeme;
							int start1 = tracker.getCurrent();
							Stmt expression2 = statement();
							int end1 = tracker.getCurrent();
							lexeme += tracker.getLexemeForRange(start1, end1 - 1);
							stmts.add(expression2);
						}
					}
				}
				if (match(TokenType.CLOSEDPAREN)) {
					last = previous();
					lexeme += last.lexeme;

					stmts.add(new Stmt.Expression(new Expr.PocketClosed(last), null));
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
		}

		throw error(null, "dfs;ljf;lsdf", true);
	}

	private Expr createCup() {

		String lexeme = "";
		ArrayList<Declaration> decs = new ArrayList<>();

		Token first = null;
		Token last = null;

		if (match(TokenType.OPENBRACE)) {
			first = previous();
			lexeme += first.lexeme;
			decs.add(new Stmt.Expression(new Expr.CupOpen(first), null));
		}

		if (!check(TokenType.CLOSEDBRACE)) {
			int start = tracker.currentIndex();
			decs.add(declaration());
			int end = tracker.currentIndex();
			lexeme += tracker.getLexemeForRange(start, end - 1);

			if (check(TokenType.COMMA)) {
				while (match(TokenType.COMMA)) {
					lexeme += previous().lexeme;
					int start1 = tracker.getCurrent();
					Declaration expression2 = declaration();
					int end1 = tracker.getCurrent();
					lexeme += tracker.getLexemeForRange(start1, end1 - 1);
					decs.add(expression2);
				}
			} else if (!check(TokenType.CLOSEDBRACE)) {
				while (!check(TokenType.CLOSEDBRACE)) {
					lexeme += previous().lexeme;
					int start1 = tracker.getCurrent();
					Declaration expression2 = declaration();
					int end1 = tracker.getCurrent();
					lexeme += tracker.getLexemeForRange(start1, end1 - 1);
					decs.add(expression2);
				}
			}
		}
		if (match(TokenType.CLOSEDBRACE)) {
			last = previous();
			lexeme += last.lexeme;

			decs.add(new Stmt.Expression(new Expr.CupClosed(last), null));
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

	}

	private boolean isPocket() {
		Stack<TokenType> parenStack = new Stack<>();
		Stack<TokenType> braceStack = new Stack<>();
		int count = 0;
		boolean ran = false;
		if (peekI(count).type == TokenType.OPENPAREN) {
			parenStack.push(peekI(count).type);
			ran = true;
		}
		count++;
		while (parenStack.size() > 0 && tracker.currentIndex() + count < tracker.size()) {
			if (peekI(count).type == TokenType.OPENPAREN) {
				parenStack.push(peekI(count).type);

			} else if (peekI(count).type == TokenType.OPENBRACE) {
				braceStack.push(peekI(count).type);
			} else if (peekI(count).type == TokenType.CLOSEDPAREN) {
				if (parenStack.size() > 0)
					parenStack.pop();
				if (parenStack.size() <= 0)
					break;
			} else if (peekI(count).type == TokenType.CLOSEDBRACE) {
				if (braceStack.size() <= 0) {
					ran = false;
					break;
				}
				if (braceStack.size() > 0)
					braceStack.pop();
			}
			count++;
		}

		if (braceStack.size() > 0 || tracker.currentIndex() + count >= tracker.size())
			return false;

		if (ran)
			return true;
		return false;
	}

	private boolean isCup() {
		Stack<TokenType> parenStack = new Stack<>();
		Stack<TokenType> braceStack = new Stack<>();
		int count = 0;
		boolean ran = false;
		if (peekI(count).type == TokenType.OPENBRACE) {
			braceStack.push(peekI(count).type);
			ran = true;
		}
		count++;
		while (braceStack.size() > 0 && tracker.currentIndex() + count < tracker.size()) {
			if (peekI(count).type == TokenType.OPENPAREN) {
				parenStack.push(peekI(count).type);

			} else if (peekI(count).type == TokenType.OPENBRACE) {
				braceStack.push(peekI(count).type);
			} else if (peekI(count).type == TokenType.CLOSEDPAREN) {

				if (parenStack.size() <= 0) {
					ran = false;
					break;
				}

				if (parenStack.size() > 0)
					parenStack.pop();
			} else if (peekI(count).type == TokenType.CLOSEDBRACE) {
				if (braceStack.size() > 0)
					braceStack.pop();
				if (braceStack.size() <= 0)
					break;
			}
			count++;
		}

		if (parenStack.size() > 0 || tracker.currentIndex() + count >= tracker.size())
			return false;

		if (ran)
			return true;
		return false;
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

		return TokenType.POCKET;

	}

	private int determineSize(CountObject count) {
		final Stack<TokenType> paren = new Stack<>();
		final Stack<TokenType> brace = new Stack<>();
		boolean first = true;
		int size = 0;
		while (tracker.getCurrent() + count.get() < tracker.size()) {
			if (peekI(count.get()).type == TokenType.OPENPAREN) {

				paren.push(peekI(count.get()).type);
				if (first) {
					first = false;
				}
			} else if (peekI(count.get()).type == TokenType.OPENBRACE) {

				brace.push(peekI(count.get()).type);
				if (first) {
					first = false;
				}
			} else if (peekI(count.get()).type == TokenType.CLOSEDPAREN) {

				if (paren.size() > 0) {
					paren.pop();
				}
				if (paren.size() == 0 && brace.size() == 0) {
					break;
				}
			} else if (peekI(count.get()).type == TokenType.CLOSEDBRACE) {

				if (brace.size() > 0) {
					brace.pop();
				}
				if (paren.size() == 0 && brace.size() == 0) {
					break;
				}
			}
			count.add();
			size++;
		}
		return size;
	}

	private TokenType determineIfCupPocketKnotOrTonk(CountObject count) {
		util.setTokens(tracker.getcurrentTokens());
		int size = determineSize(count);
		int i = (count.get() - size) - 1 >= 0 ? (count.get() - size) - 1 : 0;
		int start = tracker.getCurrent() + i;
		int finish = tracker.getCurrent() + count.get() + 1 < tracker.size() ? tracker.getCurrent() + count.get() + 1
				: tracker.getCurrent() + count.get();
		ArrayList<ContainerIndexes> findContainers = util.findContainers(start, finish);

		for (ContainerIndexes containerIndexes : findContainers) {
			if (containerIndexes.getStart() == tracker.getCurrent() + (count.get() - size)
					&& containerIndexes.getEnd() == tracker.getCurrent() + count.get())
				if (containerIndexes.isKnot() && peekI((count.get() - size)).type == TokenType.OPENPAREN)
					return TokenType.TONK;
			if (containerIndexes.isKnot() && peekI((count.get() - size)).type == TokenType.OPENBRACE)
				return TokenType.KNOT;

			if (!containerIndexes.isKnot() && peekI((count.get() - size)).type == TokenType.OPENPAREN)
				return TokenType.POCKET;
			if (!containerIndexes.isKnot() && peekI((count.get() - size)).type == TokenType.OPENBRACE)
				return TokenType.CUP;

		}
		return TokenType.UNKNOWN;

	}

	private ParseError error(Token token, String message, boolean report) {

		return new ParseError(token, message, report);
	}

}
