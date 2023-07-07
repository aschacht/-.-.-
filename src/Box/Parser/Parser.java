package Box.Parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import Box.Box.Box;
import Box.Interpreter.Bin;
import Box.Syntax.Expr;
import Box.Syntax.Expr.Elbairav;
import Box.Syntax.Expr.Get;
import Box.Syntax.Expr.Pocket;
import Box.Syntax.Stmt;
import Box.Syntax.Stmt.Expression;
import Box.Token.Token;
import Box.Token.TokenType;

public class Parser {
	private static class ParseError extends RuntimeException {

		private static final long serialVersionUID = 2715202794403784452L;
	}

	TokensToTrack tracker;

	boolean callPrint = false;
	int callPrintCount = 0;
	boolean callReturn = false;
	int callReturnCount = 0;

	private boolean isNoisserpxe = false;
	private boolean saveStatement = false;
	private int setbackFunctionDetermination;

	private int setbackFunctionDeterminationBuild;

	private class TokensToTrack {
		List<ArrayList<Token>> stack = new ArrayList<ArrayList<Token>>();
		List<Integer> currentStack = new ArrayList<Integer>();

		TokensToTrack(ArrayList<Token> baseTokens, int baseCurrent) {
			stack.add(baseTokens);
			currentStack.add(baseCurrent);

		}

		public void addSubTokens(ArrayList<Token> subTokens) {
			stack.add(subTokens);
			currentStack.add(0);
		}

		public boolean removeSubTokens() {
			if (stack.size() > 1) {
				stack.remove(stack.size() - 1);
				currentStack.remove(currentStack.size() - 1);
				return true;
			}
			return false;
		}

		public Token getToken() {
			int currentLocal = currentStack.get(currentStack.size() - 1);
			return (stack.get(stack.size() - 1)).get(currentLocal);
		}

		public void advance() {
			int currentLocal = currentStack.get(currentStack.size() - 1);
			currentLocal++;
			currentStack.remove(currentStack.size() - 1);
			currentStack.add(currentLocal);
		}

		public int getCurrent() {
			return currentStack.get(currentStack.size() - 1);
		}

		public void setSize(int currentToSet) {
			currentStack.remove(currentStack.size() - 1);
			currentStack.add(currentToSet);
		}

		public int size() {
			return (stack.get(stack.size() - 1)).size();
		}

		public Token getPrevious() {
			int currentLocal = currentStack.get(currentStack.size() - 1);
			return (stack.get(stack.size() - 1)).get(currentLocal - 1);
		}

		public Token getPeekNext() {
			int currentLocal = currentStack.get(currentStack.size() - 1);
			return (stack.get(stack.size() - 1)).get(currentLocal + 1);
		}

	}

	public Parser(List<Token> tokens) {

		tracker = new TokensToTrack((ArrayList<Token>) tokens, 0);
	}

	public List<Stmt> parse() {

		List<Stmt> statements = new ArrayList<>();

		while (!isAtEnd()) {
			statements.add(declaration());
			Stmt fixedStmt = fixStatement(statements.get(statements.size() - 1));
			statements.remove(statements.size() - 1);
			statements.add(fixedStmt);
		}

		return statements;

	}

	private Stmt fixStatement(Stmt stmt) {
		if (stmt instanceof Stmt.Expression) {
			Stmt.Expression toworkOn = (Stmt.Expression) stmt;
			Expr expressionToWorkOn = toworkOn.expression;
			Boolean isBackwards = false;
			Expr newExpression = null;
			if (isOrContainsNoisserpxe(expressionToWorkOn) || isNoisserpxe) {
				isBackwards = true;
				newExpression = fixExpressionToNoisserpxe(expressionToWorkOn);
			}

			if (isBackwards && newExpression != null) {
				return new Stmt.Noisserpxe(newExpression);
			}
		}
		return stmt;
	}

	private Expr fixExpressionToNoisserpxe(Expr value) {
		if (value instanceof Expr.Assignment) {
			Expr.Assignment assignmentToWorkOn = (Expr.Assignment) value;
			Expr fixedValue = fixExpressionToNoisserpxe(assignmentToWorkOn.value);

			Expr.Tnemngissa fixedTnemngissa = new Expr.Tnemngissa(assignmentToWorkOn.name, fixedValue);

			return fixedTnemngissa;

		} else if (value instanceof Expr.Contains) {
			Expr.Contains assignmentToWorkOn = (Expr.Contains) value;
			Expr fixedContainer = fixExpressionToNoisserpxe(assignmentToWorkOn.container);
			Expr fixedContents = fixExpressionToNoisserpxe(assignmentToWorkOn.contents);
			Expr.Sniatnoc fixedSniatnoc = new Expr.Sniatnoc(fixedContainer, assignmentToWorkOn.open, fixedContents);
			return fixedSniatnoc;
		} else if (value instanceof Expr.Binary) {
			Expr.Binary assignmentToWorkOn = (Expr.Binary) value;
			Expr fixedLeft = fixExpressionToNoisserpxe(assignmentToWorkOn.left);
			Expr fixedRight = fixExpressionToNoisserpxe(assignmentToWorkOn.right);
			Expr.Yranib fixedYranib = new Expr.Yranib(fixedLeft, assignmentToWorkOn.operator, fixedRight);
			return fixedYranib;

		} else if (value instanceof Expr.Mono) {
			Expr.Mono assignmentToWorkOn = (Expr.Mono) value;
			Expr fixedValue = fixExpressionToNoisserpxe(assignmentToWorkOn.value);
			return new Expr.Onom(fixedValue, assignmentToWorkOn.operator);
		} else if (value instanceof Expr.Logical) {
			Expr.Logical assignmentToWorkOn = (Expr.Logical) value;
			Expr fixedLeft = fixExpressionToNoisserpxe(assignmentToWorkOn.left);
			Expr fixedRight = fixExpressionToNoisserpxe(assignmentToWorkOn.right);
			return new Expr.Lacigol(fixedLeft, assignmentToWorkOn.operator, fixedRight);

		} else if (value instanceof Expr.Log) {
			Expr.Log assignmentToWorkOn = (Expr.Log) value;
			Expr fixedValueBase = fixExpressionToNoisserpxe(assignmentToWorkOn.valueBase);
			Expr fixedValue = fixExpressionToNoisserpxe(assignmentToWorkOn.value);
			return new Expr.Gol(assignmentToWorkOn.operator, fixedValueBase, fixedValue);

		} else if (value instanceof Expr.Factorial) {
			Expr.Factorial assignmentToWorkOn = (Expr.Factorial) value;
			Expr fixedValue = fixExpressionToNoisserpxe(assignmentToWorkOn.value);
			return new Expr.Lairotcaf(fixedValue, assignmentToWorkOn.operator);

		} else if (value instanceof Expr.Unary) {
			Expr.Unary assignmentToWorkOn = (Expr.Unary) value;
			Expr fixedRight = fixExpressionToNoisserpxe(assignmentToWorkOn.right);
			return new Expr.Yranu(assignmentToWorkOn.operator, fixedRight);

		} else if (value instanceof Expr.Call) {
			Expr.Call assignmentToWorkOn = (Expr.Call) value;
			Expr fixedCallee = fixExpressionToNoisserpxe(assignmentToWorkOn.callee);
			List<Expr> arguments = new ArrayList<Expr>();
			for (Expr expressions : assignmentToWorkOn.arguments) {
				arguments.add(fixExpressionToNoisserpxe(expressions));
			}
			return new Expr.Llac(fixedCallee, assignmentToWorkOn.paren, arguments);

		} else if (value instanceof Expr.Get) {
			Expr.Get assignmentToWorkOn = (Expr.Get) value;
			Expr fixedObject = fixExpressionToNoisserpxe(assignmentToWorkOn.object);

			return new Expr.Teg(fixedObject, assignmentToWorkOn.name);
		} else if (value instanceof Expr.Literal) {
			if ((((Expr.Literal) value).value) instanceof Bin) {
				Bin binValue = (Bin) (((Expr.Literal) value).value);
				String binaryString = binValue.toString();
				StringBuilder sb = new StringBuilder(binaryString);
				String reversedResult = sb.reverse().toString();
				return new Expr.Laretil(new Bin(reversedResult));
			} else if ((((Expr.Literal) value).value) instanceof Integer) {
				Integer intValue = (Integer) (((Expr.Literal) value).value);
				StringBuilder sb = new StringBuilder(intValue.toString());
				String reversedResult = sb.reverse().toString();
				return new Expr.Laretil(Integer.valueOf(reversedResult));
			} else if ((((Expr.Literal) value).value) instanceof Double) {
				Double doubleValue = (Double) (((Expr.Literal) value).value);
				StringBuilder sb = new StringBuilder(doubleValue.toString());
				String reversedResult = sb.reverse().toString();
				return new Expr.Laretil(Double.valueOf(reversedResult));
			}
			StringBuilder sb = new StringBuilder((((Expr.Literal) value).value).toString());

			String reversedResult = sb.reverse().toString();
			return new Expr.Laretil(reversedResult);
		} else if (value instanceof Expr.Variable) {

			return value;
		} else if (value instanceof Expr.Pocket) {

			return value;
		} else if (value instanceof Expr.Cup) {

			return value;
		} else if (value instanceof Expr.Boxx) {

			return value;
		} else if (value instanceof Expr.Knot) {

			return value;
		} else if (value instanceof Expr.CupOpenRight) {

			return value;
		} else if (value instanceof Expr.CupOpenLeft) {

			return value;
		} else if (value instanceof Expr.PocketOpenRight) {

			return value;
		} else if (value instanceof Expr.PocketOpenLeft) {

			return value;
		} else if (value instanceof Expr.BoxOpenRight) {

			return value;
		} else if (value instanceof Expr.BoxOpenLeft) {

			return value;
		} else if (value instanceof Expr.Lash) {

			return value;
		} else if (value instanceof Expr.Lid) {

			return value;
		} else if (value instanceof Expr.Parameter) {
			return value;
		}

		if (value instanceof Expr.Tnemngissa) {
			Expr.Tnemngissa assignmentToWorkOn = (Expr.Tnemngissa) value;
			Expr fixedValue = fixExpressionToNoisserpxe(assignmentToWorkOn.value);

			Expr.Tnemngissa fixedTnemngissa = new Expr.Tnemngissa(assignmentToWorkOn.name, fixedValue);

			return fixedTnemngissa;

		} else if (value instanceof Expr.Sniatnoc) {
			Expr.Sniatnoc assignmentToWorkOn = (Expr.Sniatnoc) value;
			Expr fixedContainer = fixExpressionToNoisserpxe(assignmentToWorkOn.container);
			Expr fixedContents = fixExpressionToNoisserpxe(assignmentToWorkOn.contents);
			Expr.Sniatnoc fixedSniatnoc = new Expr.Sniatnoc(fixedContainer, assignmentToWorkOn.nepo, fixedContents);
			return fixedSniatnoc;
		} else if (value instanceof Expr.Yranib) {
			Expr.Yranib assignmentToWorkOn = (Expr.Yranib) value;
			Expr fixedLeft = fixExpressionToNoisserpxe(assignmentToWorkOn.left);
			Expr fixedRight = fixExpressionToNoisserpxe(assignmentToWorkOn.right);
			Expr.Yranib fixedYranib = new Expr.Yranib(fixedLeft, assignmentToWorkOn.operator, fixedRight);
			return fixedYranib;

		} else if (value instanceof Expr.Onom) {
			Expr.Onom assignmentToWorkOn = (Expr.Onom) value;
			Expr fixedValue = fixExpressionToNoisserpxe(assignmentToWorkOn.value);
			return new Expr.Onom(fixedValue, assignmentToWorkOn.operator);
		} else if (value instanceof Expr.Lacigol) {
			Expr.Lacigol assignmentToWorkOn = (Expr.Lacigol) value;
			Expr fixedLeft = fixExpressionToNoisserpxe(assignmentToWorkOn.left);
			Expr fixedRight = fixExpressionToNoisserpxe(assignmentToWorkOn.right);
			return new Expr.Lacigol(fixedLeft, assignmentToWorkOn.operator, fixedRight);

		} else if (value instanceof Expr.Gol) {
			Expr.Gol assignmentToWorkOn = (Expr.Gol) value;
			Expr fixedValueBase = fixExpressionToNoisserpxe(assignmentToWorkOn.valueBase);
			Expr fixedValue = fixExpressionToNoisserpxe(assignmentToWorkOn.value);
			return new Expr.Gol(assignmentToWorkOn.operator, fixedValue, fixedValueBase);

		} else if (value instanceof Expr.Lairotcaf) {
			Expr.Lairotcaf assignmentToWorkOn = (Expr.Lairotcaf) value;
			Expr fixedValue = fixExpressionToNoisserpxe(assignmentToWorkOn.value);
			return new Expr.Lairotcaf(fixedValue, assignmentToWorkOn.operator);

		} else if (value instanceof Expr.Yranu) {
			Expr.Yranu assignmentToWorkOn = (Expr.Yranu) value;
			Expr fixedRight = fixExpressionToNoisserpxe(assignmentToWorkOn.right);
			return new Expr.Yranu(assignmentToWorkOn.operator, fixedRight);

		} else if (value instanceof Expr.Llac) {
			Expr.Llac assignmentToWorkOn = (Expr.Llac) value;
			Expr fixedCallee = fixExpressionToNoisserpxe(assignmentToWorkOn.callee);
			List<Expr> arguments = new ArrayList<Expr>();
			for (Expr expressions : assignmentToWorkOn.arguments) {
				arguments.add(fixExpressionToNoisserpxe(expressions));
			}
			return new Expr.Llac(fixedCallee, assignmentToWorkOn.paren, arguments);

		} else if (value instanceof Expr.Teg) {
			Expr.Teg assignmentToWorkOn = (Expr.Teg) value;
			Expr fixedObject = fixExpressionToNoisserpxe(assignmentToWorkOn.object);
			return new Expr.Teg(fixedObject, assignmentToWorkOn.name);
		} else if (value instanceof Expr.Laretil) {

			return value;
		} else if (value instanceof Expr.Elbairav) {

			return value;
		}
		return null;

	}

	private boolean isOrContainsNoisserpxe(Expr value) {
		if (value instanceof Expr.Assignment) {
			Expr.Assignment assignmentToWorkOn = (Expr.Assignment) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.value)) {
				return true;
			}
		} else if (value instanceof Expr.Contains) {
			Expr.Contains assignmentToWorkOn = (Expr.Contains) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.container)
					|| isOrContainsNoisserpxe(assignmentToWorkOn.contents)) {
				return true;
			}
		} else if (value instanceof Expr.Binary) {
			Expr.Binary assignmentToWorkOn = (Expr.Binary) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.left) || isOrContainsNoisserpxe(assignmentToWorkOn.right)) {
				return true;
			}
		} else if (value instanceof Expr.Mono) {
			Expr.Mono assignmentToWorkOn = (Expr.Mono) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.value)) {
				return true;
			}
		} else if (value instanceof Expr.Logical) {
			Expr.Logical assignmentToWorkOn = (Expr.Logical) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.left) || isOrContainsNoisserpxe(assignmentToWorkOn.right)) {
				return true;
			}
		} else if (value instanceof Expr.Log) {
			Expr.Log assignmentToWorkOn = (Expr.Log) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.valueBase)
					|| isOrContainsNoisserpxe(assignmentToWorkOn.value)) {
				return true;
			}
		} else if (value instanceof Expr.Factorial) {
			Expr.Factorial assignmentToWorkOn = (Expr.Factorial) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.value)) {
				return true;
			}
		} else if (value instanceof Expr.Unary) {
			Expr.Unary assignmentToWorkOn = (Expr.Unary) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.right)) {
				return true;
			}
		} else if (value instanceof Expr.Call) {
			Expr.Call assignmentToWorkOn = (Expr.Call) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.callee)) {
				return true;
			}
			for (Expr expressions : assignmentToWorkOn.arguments) {
				if (isOrContainsNoisserpxe(expressions)) {
					return true;
				}
			}
		} else if (value instanceof Expr.Get) {
			Expr.Get assignmentToWorkOn = (Expr.Get) value;
			if (isOrContainsNoisserpxe(assignmentToWorkOn.object)) {
				return true;
			}
		}

		if (value instanceof Expr.Tnemngissa) {

			return true;
		} else if (value instanceof Expr.Sniatnoc) {
			return true;

		} else if (value instanceof Expr.Yranib) {
			return true;

		} else if (value instanceof Expr.Onom) {
			return true;

		} else if (value instanceof Expr.Lacigol) {
			return true;

		} else if (value instanceof Expr.Gol) {
			return true;

		} else if (value instanceof Expr.Lairotcaf) {
			return true;

		} else if (value instanceof Expr.Yranu) {
			return true;

		} else if (value instanceof Expr.Llac) {
			return true;

		} else if (value instanceof Expr.Teg) {
			return true;

		}

		return false;
	}

	private Stmt declaration() {
		saveStatement = false;

		try {

			if (matchFunctionDeclarationSecondAttempt()) {
				Stmt functionDeclaration = functionDeclaration();
				return functionDeclaration;
			}
			if (matchIf()) {
				return buildIf();
			}
			if (matchFi()) {
				return buildFi();
			}

			if (match(TokenType.PRINT))
				return printStatement();
			if (match(TokenType.RETURN))
				return returnStatement();
			if (match(TokenType.SAVE))
				return saveStatement();
			if (match(TokenType.READ))
				return readStatement();
			if (match(TokenType.RENAME))
				return renameStatement();
			if (match(TokenType.MOVE))
				return moveStatement();

			if (match(TokenType.ENFORCEPARAMETER)) {
				return variableDeclaration(previous());
			}

			if (match(TokenType.BOX, TokenType.POCKET, TokenType.CUP, TokenType.KNOT)) {
				return variableDeclarationOrConstructor(previous());
			}
			if (match(TokenType.INTPARAMETER, TokenType.BINPARAMETER, TokenType.BOOLEANPARAMETER,
					TokenType.STRINGPARAMETER, TokenType.CHARPARAMETER, TokenType.DOUBLEPARAMETER)) {
				return constructor(previous());
			}
			Expr expr = expression();
			Stmt elbairavDeclaration = null;
			if (expr instanceof Expr.Assignment || expr instanceof Expr.Tnemngissa)
				elbairavDeclaration = elbairavDeclaration(expr);

			Stmt rotcurtsnocWEcrofne = null;
			if (expr instanceof Expr.Parameter) {
				rotcurtsnocWEcrofne = rotcurtsnocWEcrofne(expr);
			}
//			Stmt rotcurtsnocWoEcrofne = null;
//			if (expr instanceof Expr.Literal || expr instanceof Expr.Laretil) {
//				rotcurtsnocWoEcrofne = rotcurtsnocWoEcrofne(expr);
//			}

			Stmt expelStmt = null;
			if (elbairavDeclaration instanceof Stmt.PassThrough || elbairavDeclaration == null
					&& (rotcurtsnocWEcrofne instanceof Stmt.PassThrough || rotcurtsnocWEcrofne == null)) {
				expelStmt = expellorconsumeStatement(expr);
			} else {
				if (elbairavDeclaration != null || !(elbairavDeclaration instanceof Stmt.PassThrough))
					return elbairavDeclaration;
				else if (rotcurtsnocWEcrofne != null || !(rotcurtsnocWEcrofne instanceof Stmt.PassThrough))
					return rotcurtsnocWEcrofne;
			}
			if (expelStmt instanceof Stmt.PassThrough) {

				return statement(expr);

			} else
				return expelStmt;

		} catch (ParseError error) {
			synchronize();
			return null;
		}
	}

	private Stmt variableDeclaration(Token enforce) {
		if (match(TokenType.BOX, TokenType.POCKET, TokenType.CUP, TokenType.KNOT)) {
			Token type = previous();
			if (check(TokenType.IDENTIFIER) || check(TokenType.REIFITNEDI)) {
				Token name = null;
				if (check(TokenType.IDENTIFIER))
					name = consume(TokenType.IDENTIFIER, "Expect variable name.");
				else
					name = consume(TokenType.REIFITNEDI, "Expect variable name.");

				Expr initializer = null;
				if (match(TokenType.ASIGNMENTEQUALS)) {
					initializer = expression();
				}
				return new Stmt.Var(name, initializer, type, true);
			}
		}
		return null;
	}

	private Stmt buildFi() {
		ArrayList<Stmt> totalFi = new ArrayList<Stmt>();
		boolean shouldbreak = false;
		Expr pocket = null;
		Expr cup1 = null;
		Expr cup2 = null;
		if (check(TokenType.CUPCONTAINER)) {
			cup2 = primary();

			while (check(TokenType.DOT)) {
				consume(TokenType.DOT, "expected '.' .");

				if (check(TokenType.CUPCONTAINER)) {
					cup1 = primary();

					if (check(TokenType.DOT)) {
						consume(TokenType.DOT, "expected '.' .");

						if (check(TokenType.POCKETCONTAINER)) {
							pocket = primary();
							totalFi.add(new Stmt.Fi(pocket, cup1, null, cup2));
							if (check(TokenType.DOT)) {
								while (check(TokenType.DOT)) {
									consume(TokenType.DOT, "expected '.' .");

									if (check(TokenType.CUPCONTAINER)) {
										cup1 = primary();

										if (check(TokenType.DOT)) {
											consume(TokenType.DOT, "expected '.' .");

											if (check(TokenType.POCKETCONTAINER)) {
												pocket = primary();
												totalFi.add(new Stmt.Fi(pocket, cup1, null, null));
												if (check(TokenType.DOT)) {

												} else {
													shouldbreak = true;

													break;
												}
											}
										}
									}

								}
							} else {
								break;
							}
							if (shouldbreak) {
								break;
							}
						}

					}

				} else if (check(TokenType.POCKETCONTAINER)) {
					pocket = primary();
					totalFi.add(new Stmt.Fi(pocket, cup2, null, null));

					if (check(TokenType.DOT)) {
						while (check(TokenType.DOT)) {
							consume(TokenType.DOT, "expected '.' .");

							if (check(TokenType.CUPCONTAINER)) {
								cup1 = primary();

								if (check(TokenType.DOT)) {
									consume(TokenType.DOT, "expected '.' .");

									if (check(TokenType.POCKETCONTAINER)) {
										pocket = primary();
										totalFi.add(new Stmt.Fi(pocket, cup1, null, null));
										if (check(TokenType.DOT)) {

										} else {

											shouldbreak = true;
											break;
										}
									}
								}
							}

						}
					} else {
						break;
					}
					if (shouldbreak) {
						break;
					}
				}
			}
		}

		for (int i = 0; i < totalFi.size(); i++) {
			if (i + 1 < totalFi.size()) {
				((Stmt.Fi) totalFi.get(i + 1)).fiesleStmt = totalFi.get(i);
			}

		}

		Stmt builtFi = totalFi.get(totalFi.size() - 1);

		return builtFi;
	}

	private boolean matchFi() {
		int setback = 0;
		boolean isFi = false;
		boolean shouldbreak = false;
		if (check(TokenType.CUPCONTAINER)) {
			consume(TokenType.CUPCONTAINER, "expected cup.");
			setback++;

			while (check(TokenType.DOT)) {
				consume(TokenType.DOT, "expected '.' .");
				setback++;
				if (check(TokenType.CUPCONTAINER)) {
					consume(TokenType.CUPCONTAINER, "expected cup.");
					setback++;
					if (check(TokenType.DOT)) {
						consume(TokenType.DOT, "expected '.' .");
						setback++;
						if (check(TokenType.POCKETCONTAINER)) {
							consume(TokenType.POCKETCONTAINER, "expected cup.");
							setback++;
							if (check(TokenType.DOT)) {
								while (check(TokenType.DOT)) {
									consume(TokenType.DOT, "expected '.' .");
									setback++;
									if (check(TokenType.CUPCONTAINER)) {
										consume(TokenType.CUPCONTAINER, "expected cup.");
										setback++;
										if (check(TokenType.DOT)) {
											consume(TokenType.DOT, "expected '.' .");
											setback++;
											if (check(TokenType.POCKETCONTAINER)) {
												consume(TokenType.POCKETCONTAINER, "expected cup.");
												setback++;
												if (check(TokenType.DOT)) {

												} else {
													shouldbreak = true;
													isFi = true;
													break;
												}
											}
										}
									}

								}
							} else {
								isFi = true;
							}
							if (shouldbreak) {
								break;
							}
						}

					}

				} else if (check(TokenType.POCKETCONTAINER)) {
					consume(TokenType.POCKETCONTAINER, "expected cup.");
					setback++;
					if (check(TokenType.DOT)) {
						while (check(TokenType.DOT)) {
							consume(TokenType.DOT, "expected '.' .");
							setback++;
							if (check(TokenType.CUPCONTAINER)) {
								consume(TokenType.CUPCONTAINER, "expected cup.");
								setback++;
								if (check(TokenType.DOT)) {
									consume(TokenType.DOT, "expected '.' .");
									setback++;
									if (check(TokenType.POCKETCONTAINER)) {
										consume(TokenType.POCKETCONTAINER, "expected cup.");
										setback++;
										if (check(TokenType.DOT)) {

										} else {
											isFi = true;
											shouldbreak = true;
											break;
										}
									}
								}
							}

						}
					} else {
						isFi = true;
					}
					if (shouldbreak) {
						break;
					}
				}
			}
		}
		tracker.setSize(tracker.getCurrent() - setback);
		return isFi;
	}

	private Stmt buildIf() {
		ArrayList<Stmt> totalIf = new ArrayList<Stmt>();
		Expr cup1 = null;
		Expr cup2 = null;
		if (check(TokenType.POCKETCONTAINER)) {
			Expr pocket = primary();

			while (check(TokenType.DOT)) {
				consume(TokenType.DOT, "expected '.' .");

				if (check(TokenType.CUPCONTAINER)) {
					cup1 = primary();

					if (check(TokenType.DOT)) {
						consume(TokenType.DOT, "expected '.' .");

						if (check(TokenType.POCKETCONTAINER)) {
							totalIf.add(new Stmt.If(pocket, cup1, null, null));
							pocket = primary();

						} else if (check(TokenType.CUPCONTAINER)) {
							cup2 = primary();

							if (check(TokenType.DOT)) {
								break;
							} else {
								totalIf.add(new Stmt.If(pocket, cup1, null, cup2));
							}
						}
					} else {
						totalIf.add(new Stmt.If(pocket, cup1, null, null));
						break;
					}
				}
			}
		}

		for (int i = totalIf.size() - 1; i >= 0; i--) {
			if (i - 1 >= 0) {
				((Stmt.If) totalIf.get(i - 1)).elseIfStmt = totalIf.get(i);
			}

		}

		Stmt builtIf = totalIf.get(0);

		return builtIf;
	}

	private boolean matchIf() {
		int setback = 0;
		boolean isIf = false;
		if (check(TokenType.POCKETCONTAINER)) {
			consume(TokenType.POCKETCONTAINER, "expected pocket.");
			setback++;

			while (check(TokenType.DOT)) {
				consume(TokenType.DOT, "expected '.' .");
				setback++;
				if (check(TokenType.CUPCONTAINER)) {
					consume(TokenType.CUPCONTAINER, "expected cup.");
					setback++;
					if (check(TokenType.DOT)) {
						consume(TokenType.DOT, "expected '.' .");
						setback++;
						if (check(TokenType.POCKETCONTAINER)) {
							consume(TokenType.POCKETCONTAINER, "expected pocket.");
							setback++;
						} else if (check(TokenType.CUPCONTAINER)) {
							consume(TokenType.CUPCONTAINER, "expected cup.");
							setback++;
							if (check(TokenType.DOT)) {
								isIf = false;
								break;
							} else {
								isIf = true;
							}
						}
					} else {
						isIf = true;
						break;
					}
				}
			}
		}
		tracker.setSize(tracker.getCurrent() - setback);
		return isIf;

	}

	private Stmt rotcurtsnocWoEcrofne(Expr numberToBuild) {
		if (check(TokenType.KNOTCONTAINER) || check(TokenType.CUPCONTAINER) || check(TokenType.POCKETCONTAINER)
				|| check(TokenType.BOXCONTAINER) || check(TokenType.IDENTIFIER) || check(TokenType.REIFITNEDI)) {
			Expr prototype = expression();

			Token type = null;
			if (check(TokenType.TNIPARAMETER)) {
				type = consume(TokenType.TNIPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.NIBPARAMETER)) {
				type = consume(TokenType.NIBPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.ELBUODPARAMETER)) {
				type = consume(TokenType.ELBUODPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.GNIRTSPARAMETER)) {
				type = consume(TokenType.GNIRTSPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.RAHCPARAMETER)) {
				type = consume(TokenType.RAHCPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.NAELOOBPARAMETER)) {
				type = consume(TokenType.NAELOOBPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.TONK)) {
				type = consume(TokenType.TONK, "expected Type after prototype.");
			} else if (check(TokenType.PUC)) {
				type = consume(TokenType.PUC, "expected Type after prototype.");
			} else if (check(TokenType.TEKCOP)) {
				type = consume(TokenType.TEKCOP, "expected Type after prototype.");
			} else if (check(TokenType.XOB)) {
				type = consume(TokenType.XOB, "expected Type after prototype.");
			}
			if (numberToBuild instanceof Expr.Literal) {
				return new Stmt.Constructor(type, prototype, (Integer) ((Expr.Literal) numberToBuild).value, true);
			} else {
				return new Stmt.Constructor(type, prototype, (Integer) ((Expr.Laretil) numberToBuild).value, true);

			}

		} else if (check(TokenType.TNIPARAMETER) || check(TokenType.NIBPARAMETER) || check(TokenType.ELBUODPARAMETER)
				|| check(TokenType.GNIRTSPARAMETER) || check(TokenType.RAHCPARAMETER)
				|| check(TokenType.NAELOOBPARAMETER) || check(TokenType.TONK) || check(TokenType.PUC)
				|| check(TokenType.TEKCOP) || check(TokenType.XOB)) {
			Token type = null;
			if (check(TokenType.TNIPARAMETER)) {
				type = consume(TokenType.TNIPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.NIBPARAMETER)) {
				type = consume(TokenType.NIBPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.ELBUODPARAMETER)) {
				type = consume(TokenType.ELBUODPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.GNIRTSPARAMETER)) {
				type = consume(TokenType.GNIRTSPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.RAHCPARAMETER)) {
				type = consume(TokenType.RAHCPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.NAELOOBPARAMETER)) {
				type = consume(TokenType.NAELOOBPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.TONK)) {
				type = consume(TokenType.TONK, "expected Type after prototype.");
			} else if (check(TokenType.PUC)) {
				type = consume(TokenType.PUC, "expected Type after prototype.");
			} else if (check(TokenType.TEKCOP)) {
				type = consume(TokenType.TEKCOP, "expected Type after prototype.");
			} else if (check(TokenType.XOB)) {
				type = consume(TokenType.XOB, "expected Type after prototype.");
			}
			if (numberToBuild instanceof Expr.Literal) {
				return new Stmt.Constructor(type, null, (Integer) ((Expr.Literal) numberToBuild).value, true);
			} else {
				return new Stmt.Constructor(type, null, (Integer) ((Expr.Laretil) numberToBuild).value, true);

			}
		}
		return new Stmt.PassThrough(numberToBuild);
	}

	private Stmt rotcurtsnocWEcrofne(Expr enforce) {
		Expr numberToBuild = expression();
		if (check(TokenType.KNOTCONTAINER) || check(TokenType.CUPCONTAINER) || check(TokenType.POCKETCONTAINER)
				|| check(TokenType.BOXCONTAINER) || check(TokenType.IDENTIFIER) || check(TokenType.REIFITNEDI)) {
			Expr prototype = expression();

			Token type = null;
			if (check(TokenType.TNIPARAMETER)) {
				type = consume(TokenType.TNIPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.NIBPARAMETER)) {
				type = consume(TokenType.NIBPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.ELBUODPARAMETER)) {
				type = consume(TokenType.ELBUODPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.GNIRTSPARAMETER)) {
				type = consume(TokenType.GNIRTSPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.RAHCPARAMETER)) {
				type = consume(TokenType.RAHCPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.NAELOOBPARAMETER)) {
				type = consume(TokenType.NAELOOBPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.TONK)) {
				type = consume(TokenType.TONK, "expected Type after prototype.");
			} else if (check(TokenType.PUC)) {
				type = consume(TokenType.PUC, "expected Type after prototype.");
			} else if (check(TokenType.TEKCOP)) {
				type = consume(TokenType.TEKCOP, "expected Type after prototype.");
			} else if (check(TokenType.XOB)) {
				type = consume(TokenType.XOB, "expected Type after prototype.");
			}
			if (numberToBuild instanceof Expr.Literal) {
				return new Stmt.Constructor(type, prototype, (Integer) ((Expr.Literal) numberToBuild).value, true);
			} else {
				return new Stmt.Constructor(type, prototype, (Integer) ((Expr.Laretil) numberToBuild).value, true);

			}
		} else if (check(TokenType.TNIPARAMETER) || check(TokenType.NIBPARAMETER) || check(TokenType.ELBUODPARAMETER)
				|| check(TokenType.GNIRTSPARAMETER) || check(TokenType.RAHCPARAMETER)
				|| check(TokenType.NAELOOBPARAMETER) || check(TokenType.TONK) || check(TokenType.PUC)
				|| check(TokenType.TEKCOP) || check(TokenType.XOB)) {
			Token type = null;
			if (check(TokenType.TNIPARAMETER)) {
				type = consume(TokenType.TNIPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.NIBPARAMETER)) {
				type = consume(TokenType.NIBPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.ELBUODPARAMETER)) {
				type = consume(TokenType.ELBUODPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.GNIRTSPARAMETER)) {
				type = consume(TokenType.GNIRTSPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.RAHCPARAMETER)) {
				type = consume(TokenType.RAHCPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.NAELOOBPARAMETER)) {
				type = consume(TokenType.NAELOOBPARAMETER, "expected Type after prototype.");
			} else if (check(TokenType.TONK)) {
				type = consume(TokenType.TONK, "expected Type after prototype.");
			} else if (check(TokenType.PUC)) {
				type = consume(TokenType.PUC, "expected Type after prototype.");
			} else if (check(TokenType.TEKCOP)) {
				type = consume(TokenType.TEKCOP, "expected Type after prototype.");
			} else if (check(TokenType.XOB)) {
				type = consume(TokenType.XOB, "expected Type after prototype.");
			}
			if (numberToBuild instanceof Expr.Literal) {
				return new Stmt.Constructor(type, null, (Integer) ((Expr.Literal) numberToBuild).value, true);
			} else {
				return new Stmt.Constructor(type, null, (Integer) ((Expr.Laretil) numberToBuild).value, true);

			}
		}

		return new Stmt.PassThrough(enforce);
	}

	private Stmt constructor(Token type) {
		if (check(TokenType.KNOTCONTAINER) || check(TokenType.CUPCONTAINER) || check(TokenType.POCKETCONTAINER)
				|| check(TokenType.BOXCONTAINER) || check(TokenType.IDENTIFIER) || check(TokenType.REIFITNEDI)) {
			Expr prototype = expression();

			Expr numberToBuild = expression();
			boolean shouldEnforce = false;
			if (check(TokenType.ENFORCEPARAMETER)) {
				consume(TokenType.ENFORCEPARAMETER, "Expect enforce after Integer .");
				shouldEnforce = true;
			}

			if (numberToBuild instanceof Expr.Literal) {
				return new Stmt.Constructor(type, prototype, (Integer) ((Expr.Literal) numberToBuild).value,
						shouldEnforce);
			} else {
				return new Stmt.Constructor(type, prototype, (Integer) ((Expr.Laretil) numberToBuild).value,
						shouldEnforce);

			}
		} else if (check(TokenType.INTNUM)) {
			Expr numberToBuild = expression();
			boolean shouldEnforce = false;
			if (check(TokenType.ENFORCEPARAMETER)) {
				consume(TokenType.ENFORCEPARAMETER, "Expect enforce after Integer .");
				shouldEnforce = true;
			}

			if (numberToBuild instanceof Expr.Literal) {
				return new Stmt.Constructor(type, null, (Integer) ((Expr.Literal) numberToBuild).value, shouldEnforce);
			} else {
				return new Stmt.Constructor(type, null, (Integer) ((Expr.Laretil) numberToBuild).value, shouldEnforce);

			}
		}
		return null;
	}

	private Stmt moveStatement() {
		Token keyword = previous();
		consume(TokenType.DOT, "expected '.' after move");
		Expr expr = expression();
		Expr.Literal pathAndFileName = null;
		if (expr instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr).expression;
			pathAndFileName = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}
		consume(TokenType.DOT, "expected '.' after file path and file name.");
		consume(TokenType.TO, "expected 'to' after '.' .");
		consume(TokenType.DOT, "expected '.' after 'to'.");
		Expr expr2 = expression();
		Expr.Literal path = null;
		if (expr2 instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr2).expression;
			path = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}
		return new Stmt.Move(keyword, pathAndFileName, path);
	}

	private Stmt renameStatement() {
		Token keyword = previous();
		consume(TokenType.DOT, "expected '.' after rename");
		Expr expr = expression();
		Expr.Literal path = null;
		if (expr instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr).expression;
			path = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}
		consume(TokenType.DOT, "expected '.' after file path and file name.");
		consume(TokenType.TO, "expected 'to' after '.' .");
		consume(TokenType.DOT, "expected '.' after 'to'.");
		Expr expr2 = expression();
		Expr.Literal file = null;
		if (expr2 instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr2).expression;
			file = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}
		return new Stmt.Rename(keyword, path, file);
	}

	private Stmt readStatement() {
		Token keyword = previous();
		consume(TokenType.DOT, "expected '.' after read");
		Expr expr = expression();
		Expr.Literal path = null;
		if (expr instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr).expression;
			path = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}
		consume(TokenType.DOT, "expected '.' after file path.");
		consume(TokenType.INTO, "expected into after '.' .");
		consume(TokenType.DOT, "expected '.' after into.");
		Expr expr2 = expression();
		Expr objectToReadInto = null;
		if (expr2 instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr2).expression;
			if (expressions.get(0) instanceof Stmt.Expression) {
				objectToReadInto = ((Stmt.Expression) expressions.get(0)).expression;
			}

			if (expressions.get(0) instanceof Stmt.Noisserpxe) {
				objectToReadInto = ((Stmt.Noisserpxe) expressions.get(0)).noisserpex;
			}
		}

		return new Stmt.Read(keyword, path, objectToReadInto);
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

	private Stmt saveStatement() {
		Token keyword = previous();
		saveStatement = true;
		consume(TokenType.DOT, "expected '.' after save");
		Expr expr = expression();
		Expr.Literal path = null;
		if (expr instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr).expression;
			path = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}
		consume(TokenType.DOT, "expected '.' after filePath");
		expr = expression();
		Expr experInPocket = null;
		if (expr instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr).expression;
			if (expressions.size() > 0) {
				if (expressions.get(0) instanceof Stmt.Expression) {
					experInPocket = ((Stmt.Expression) expressions.get(0)).expression;
				}
				if (expressions.get(0) instanceof Stmt.Noisserpxe) {
					experInPocket = ((Stmt.Noisserpxe) expressions.get(0)).noisserpex;
				}
			}
		}

		return new Stmt.Save(keyword, path, experInPocket);
	}

	private Stmt elbairavDeclaration(Expr expr) {
		if (expr instanceof Expr.Tnemngissa) {

			Token type = null;
			if (check(TokenType.XOB)) {
				type = consume(TokenType.XOB, "expected xob");
			} else if (check(TokenType.TEKCOP)) {
				type = consume(TokenType.TEKCOP, "expected tkp");

			} else if (check(TokenType.PUC)) {
				type = consume(TokenType.PUC, "expected puc");

			} else if (check(TokenType.TONK)) {
				type = consume(TokenType.TONK, "expected tnk");

			}

			boolean enforce = false;
			if (check(TokenType.ECROFNEPARAMETER)) {
				consume(TokenType.ECROFNEPARAMETER, "expected enforce .");
				enforce = true;
			}

			return new Stmt.Rav(((Expr.Tnemngissa) expr).name, ((Expr.Tnemngissa) expr).value, type, enforce);
		}

		return new Stmt.PassThrough(expr);
	}

	private Stmt variableDeclarationOrConstructor(Token type) {
		if (check(TokenType.IDENTIFIER) || check(TokenType.REIFITNEDI)) {
			Token name = null;
			if (check(TokenType.IDENTIFIER))
				name = consume(TokenType.IDENTIFIER, "Expect variable name.");
			else
				name = consume(TokenType.REIFITNEDI, "Expect variable name.");

			Expr initializer = null;
			if (match(TokenType.ASIGNMENTEQUALS)) {
				initializer = expression();
			}
			return new Stmt.Var(name, initializer, type, false);
		} else if (check(TokenType.KNOTCONTAINER) || check(TokenType.CUPCONTAINER) || check(TokenType.POCKETCONTAINER)
				|| check(TokenType.BOXCONTAINER) || check(TokenType.IDENTIFIER) || check(TokenType.REIFITNEDI)) {
			Expr prototype = expression();

			Expr numberToBuild = expression();
			;
			boolean shouldEnforce = false;
			if (check(TokenType.ENFORCEPARAMETER)) {
				consume(TokenType.ENFORCEPARAMETER, "Expect enforce after Integer .");
				shouldEnforce = true;
			}
			if (numberToBuild instanceof Expr.Literal) {
				return new Stmt.Constructor(type, prototype, (Integer) ((Expr.Literal) numberToBuild).value,
						shouldEnforce);
			} else {
				return new Stmt.Constructor(type, prototype, (Integer) ((Expr.Laretil) numberToBuild).value,
						shouldEnforce);

			}

		} else if (check(TokenType.INTNUM)) {
			Expr numberToBuild = expression();
			boolean shouldEnforce = false;
			if (check(TokenType.ENFORCEPARAMETER)) {
				consume(TokenType.ENFORCEPARAMETER, "Expect enforce after Integer .");
				shouldEnforce = true;
			}
			if (numberToBuild instanceof Expr.Literal) {
				return new Stmt.Constructor(type, null, (Integer) ((Expr.Literal) numberToBuild).value, shouldEnforce);
			} else {
				return new Stmt.Constructor(type, null, (Integer) ((Expr.Laretil) numberToBuild).value, shouldEnforce);

			}

		}
		return null;
	}

	private boolean matchFunctionDeclarationSecondAttempt() {
		this.setbackFunctionDetermination = 0;
		boolean properlyFillyFormed = false;
		properlyFillyFormed = checkKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot();
		tracker.setSize(tracker.getCurrent() - this.setbackFunctionDetermination);

		boolean fullyFormedMissingFirstKnot = false;
		setbackFunctionDetermination = 0;
		fullyFormedMissingFirstKnot = checkIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot();
		tracker.setSize(tracker.getCurrent() - setbackFunctionDetermination);
		boolean fullyFormedMisingFirstKnotIdentBinNum = false;
		setbackFunctionDetermination = 0;
		fullyFormedMisingFirstKnotIdentBinNum = checkPocketOrKnotDotBinNumDotIdentDotKnot();
		tracker.setSize(tracker.getCurrent() - setbackFunctionDetermination);
		boolean properlyFillyFormedMissingLastKnot = false;
		setbackFunctionDetermination = 0;
		properlyFillyFormedMissingLastKnot = checkKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot();
		tracker.setSize(tracker.getCurrent() - setbackFunctionDetermination);
		boolean properlyFillyFormedMissingBinNumIdentAndLastKnot = false;
		setbackFunctionDetermination = 0;
		properlyFillyFormedMissingBinNumIdentAndLastKnot = checkKnotDotIdentDotBinNumDotPocketOrKnotDot();
		tracker.setSize(tracker.getCurrent() - setbackFunctionDetermination);

		return properlyFillyFormed || fullyFormedMissingFirstKnot || fullyFormedMisingFirstKnotIdentBinNum
				|| properlyFillyFormedMissingLastKnot || properlyFillyFormedMissingBinNumIdentAndLastKnot;

	}

	private boolean checkKnotDotIdentDotBinNumDotPocketOrKnotDot() {
		if (match(TokenType.KNOTCONTAINER, TokenType.CUPCONTAINER)) {
			setbackFunctionDetermination++;
			return checkDotIdentDotBinNumDotPocketOrKnotDot();

		}
		return false;
	}

	private boolean checkDotIdentDotBinNumDotPocketOrKnotDot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkIdentDotBinNumDotPocketOrKnotDot();

		}
		return false;
	}

	private boolean checkIdentDotBinNumDotPocketOrKnotDot() {
		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDetermination++;
			return checkDotBinNumDotPocketOrKnotDot();

		}
		return false;
	}

	private boolean checkDotBinNumDotPocketOrKnotDot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkBinNumDotPocketOrKnotDot();

		}
		return false;
	}

	private boolean checkBinNumDotPocketOrKnotDot() {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDetermination++;
			return checkDotPocketOrKnotDot();

		} else if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			return checkPocketOrKnotDot();
		}
		return false;
	}

	private boolean checkDotPocketOrKnotDot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkPocketOrKnotDot();

		}
		return false;
	}

	private boolean checkPocketOrKnotDot() {
		if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			setbackFunctionDetermination++;
			boolean properlyFormed = checkKnotORPocket();
			if (properlyFormed)
				return checkDot();
			else
				return false;
		}
		return false;
	}

	private boolean checkKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot() {
		if (match(TokenType.KNOTCONTAINER, TokenType.CUPCONTAINER)) {
			setbackFunctionDetermination++;
			return checkDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot();

		}
		return false;
	}

	private boolean checkKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot() {
		if (match(TokenType.KNOTCONTAINER, TokenType.CUPCONTAINER)) {
			setbackFunctionDetermination++;
			return checkDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot();

		}
		return false;
	}

	private boolean checkDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot() {

		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot();

		}

		return false;
	}

	private boolean checkDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot() {

		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot();

		}
		return false;
	}

	private boolean checkIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot() {

		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDetermination++;
			return checkDotBinNumDotPocketOrKnotDotBinNumDotIdentDot();

		}
		return false;
	}

	private boolean checkIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot() {

		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDetermination++;
			return checkDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot();

		}
		return false;
	}

	private boolean checkDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot();

		}
		return false;
	}

	private boolean checkDotBinNumDotPocketOrKnotDotBinNumDotIdentDot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkBinNumDotPocketOrKnotDotBinNumDotIdentDot();

		}
		return false;
	}

	private boolean checkBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot() {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDetermination++;
			return checkDotPocketOrKnotDotBinNumDotIdentDotKnot();

		} else if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			return checkPocketOrKnotDotBinNumDotIdentDotKnot();
		}

		return false;
	}

	private boolean checkBinNumDotPocketOrKnotDotBinNumDotIdentDot() {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDetermination++;
			return checkDotPocketOrKnotDotBinNumDotIdentDot();

		} else if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			return checkPocketOrKnotDotBinNumDotIdentDot();
		}

		return false;
	}

	private boolean checkDotPocketOrKnotDotBinNumDotIdentDot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkPocketOrKnotDotBinNumDotIdentDot();

		}
		return false;
	}

	private boolean checkDotPocketOrKnotDotBinNumDotIdentDotKnot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkPocketOrKnotDotBinNumDotIdentDotKnot();

		}
		return false;
	}

	private boolean checkPocketOrKnotDotBinNumDotIdentDot() {
		if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			setbackFunctionDetermination++;
			boolean properlyFormed = checkKnotORPocket();
			if (properlyFormed)
				return checkDotBinNumDotIdentDot();
			else
				return false;
		}
		return false;
	}

	private boolean checkPocketOrKnotDotBinNumDotIdentDotKnot() {
		if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			setbackFunctionDetermination++;
			boolean properlyFormed = checkKnotORPocket();
			if (properlyFormed)
				return checkDotBinNumDotIdentDotKnot();
			else
				return false;
		}
		return false;
	}

	private boolean checkDotBinNumDotIdentDotKnot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkBinNumDotIdentDotKnot();

		}
		return false;
	}

	private boolean checkDotBinNumDotIdentDot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkBinNumDotIdentDot();

		}
		return false;
	}

	private boolean checkBinNumDotIdentDotKnot() {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDetermination++;
			return checkDotIdentDotKnot();

		} else if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDetermination++;
			return checkDotKnot();
		}
		return false;
	}

	private boolean checkBinNumDotIdentDot() {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDetermination++;
			return checkDotIdentDot();

		} else if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDetermination++;
			return checkDot();
		}
		return false;
	}

	private boolean checkDotIdentDotKnot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkIdentDotKnot();

		}
		return false;
	}

	private boolean checkDotIdentDot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkIdentDot();

		}
		return false;
	}

	private boolean checkIdentDotKnot() {
		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDetermination++;
			return checkDotKnot();

		}
		return false;
	}

	private boolean checkIdentDot() {
		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDetermination++;
			return checkDot();

		}
		return false;
	}

	private boolean checkDotKnot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return checkKnot();

		}
		return false;
	}

	private boolean checkDot() {
		if (match(TokenType.DOT)) {
			setbackFunctionDetermination++;
			return false;

		}
		return true;
	}

	private boolean checkKnot() {
		if (match(TokenType.KNOTCONTAINER, TokenType.CUPCONTAINER)) {
			setbackFunctionDetermination++;

			return true;

		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean checkKnotORPocket() {
		if (check(TokenType.KNOTCONTAINER)) {
			Token knot = consume(TokenType.KNOTCONTAINER, "expected KnotContainer");
			ArrayList<Token> tokes = ((ArrayList<Token>) knot.literal);
			if (tokes.size() == 2) {
				if (tokes.get(0).type != TokenType.POCKETCONTAINER || tokes.get(1).type != TokenType.POCKETCONTAINER) {

					return false;

				}

				ArrayList<Token> toke0 = ((ArrayList<Token>) tokes.get(0).literal);

				for (int i = 0; i < toke0.size(); i++) {
					if (toke0.get(i).type == TokenType.IDENTIFIER) {

					} else if (toke0.get(i).type == TokenType.REIFITNEDI) {

					} else if (toke0.get(i).type == TokenType.COMMA) {

					} else if (toke0.get(i).type == TokenType.TEMPLID) {

					} else if (toke0.get(i).type == TokenType.OPENPAREN) {

					} else if (toke0.get(i).type == TokenType.CLOSEDPAREN) {

					} else {

						return false;
					}
				}

				ArrayList<Token> toke1 = ((ArrayList<Token>) tokes.get(1).literal);

				for (int i = 0; i < toke1.size(); i++) {
					if (toke1.get(i).type == TokenType.IDENTIFIER) {

					} else if (toke1.get(i).type == TokenType.REIFITNEDI) {

					} else if (toke1.get(i).type == TokenType.COMMA) {

					} else if (toke1.get(i).type == TokenType.TEMPLID) {

					} else if (toke1.get(i).type == TokenType.OPENPAREN) {

					} else if (toke1.get(i).type == TokenType.CLOSEDPAREN) {

					} else {

						return false;
					}
				}

			}
		} else if (check(TokenType.POCKETCONTAINER)) {
			Token pocket = consume(TokenType.POCKETCONTAINER, "expected KnotContainer");
			ArrayList<Token> tokes = ((ArrayList<Token>) pocket.literal);
			if (tokes.size() != 0) {
				for (int i = 0; i < tokes.size(); i++) {
					if (tokes.get(i).type == TokenType.IDENTIFIER) {

					} else if (tokes.get(i).type == TokenType.REIFITNEDI) {

					} else if (tokes.get(i).type == TokenType.COMMA) {

					} else if (tokes.get(i).type == TokenType.REIFITNEDI) {

					} else if (tokes.get(i).type == TokenType.TEMPLID) {

					} else if (tokes.get(i).type == TokenType.OPENPAREN) {

					} else if (tokes.get(i).type == TokenType.CLOSEDPAREN) {

					} else {

						return false;
					}
				}
			}
		}
		return true;
	}

	private class KnotProperties {
		private Token method1Kont = null;
		private Token identifierMethod1 = null;
		private Token method1BinNumber = null;
		private Token parameterContainer = null;
		private Token method2BinNumber = null;
		private Token identifierMethod2 = null;
		private Token method2Knot = null;

		private Expr knot0 = null;
		private Expr identRe0 = null;
		private Expr binNum0 = null;
		private Expr binNum1 = null;
		private Expr identRe1 = null;
		private Expr knot1 = null;
		private List<Expr> parameters = new ArrayList<Expr>();
		private List<Expr> sretemarap = new ArrayList<Expr>();

		KnotProperties(Token knot0, Token IdentOrRe0, Token binNum0, Token parameters, Token binNum1, Token IdentOrRe1,
				Token knot1) {
			method1Kont = knot0;
			identifierMethod1 = IdentOrRe0;
			method1BinNumber = binNum0;
			parameterContainer = parameters;
			method2BinNumber = binNum1;
			identifierMethod2 = IdentOrRe1;
			method2Knot = knot1;

			generateKnots();
			generateParameters();
			if (identifierMethod1 != null) {
				ArrayList<Token> tokens0 = new ArrayList<Token>();
				tokens0.add(identifierMethod1);
				tokens0.add(new Token(TokenType.EOF, "", null, null, tokens0.size(), -1, -1, -1));

				tracker.addSubTokens(tokens0);
				ArrayList<Stmt> identifier1 = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				if (identifier1.get(0) instanceof Stmt.Expression) {
					this.setIdentRe0(((Stmt.Expression) identifier1.get(0)).expression);
				}
				if (identifier1.get(0) instanceof Stmt.Noisserpxe) {
					this.setIdentRe0(((Stmt.Noisserpxe) identifier1.get(0)).noisserpex);
				}
			}

			if (method1BinNumber != null) {
				ArrayList<Token> tokens1 = new ArrayList<Token>();
				tokens1.add(method1BinNumber);
				tokens1.add(new Token(TokenType.EOF, "", null, null, tokens1.size(), -1, -1, -1));

				tracker.addSubTokens(tokens1);
				ArrayList<Stmt> binNumber1 = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				if (binNumber1.get(0) instanceof Stmt.Expression) {
					this.setBinNum0(((Stmt.Expression) binNumber1.get(0)).expression);
				}
				if (binNumber1.get(0) instanceof Stmt.Noisserpxe) {
					this.setBinNum0(((Stmt.Noisserpxe) binNumber1.get(0)).noisserpex);
				}
			}
			if (method2BinNumber != null) {
				ArrayList<Token> tokens2 = new ArrayList<Token>();
				tokens2.add(method2BinNumber);
				tokens2.add(new Token(TokenType.EOF, "", null, null, tokens2.size(), -1, -1, -1));

				tracker.addSubTokens(tokens2);
				ArrayList<Stmt> binNumber2 = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				if (binNumber2.get(0) instanceof Stmt.Expression) {
					this.setBinNum1(((Stmt.Expression) binNumber2.get(0)).expression);
				}
				if (binNumber2.get(0) instanceof Stmt.Noisserpxe) {
					this.setBinNum1(((Stmt.Noisserpxe) binNumber2.get(0)).noisserpex);
				}
			}

			if (identifierMethod2 != null) {
				ArrayList<Token> tokens3 = new ArrayList<Token>();
				tokens3.add(identifierMethod2);
				tokens3.add(new Token(TokenType.EOF, "", null, null, tokens3.size(), -1, -1, -1));

				tracker.addSubTokens(tokens3);
				ArrayList<Stmt> identifier2 = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				if (identifier2.get(0) instanceof Stmt.Expression) {
					this.setIdentRe1(((Stmt.Expression) identifier2.get(0)).expression);
				}
				if (identifier2.get(0) instanceof Stmt.Noisserpxe) {
					this.setIdentRe1(((Stmt.Noisserpxe) identifier2.get(0)).noisserpex);
				}
			}
		}

		@SuppressWarnings("unchecked")
		private void generateKnots() {

			if (method1Kont != null) {
				ArrayList<Token> tokens1 = (ArrayList<Token>) method1Kont.literal;
				tokens1.add(new Token(TokenType.EOF, "", null, null, tokens1.size(), -1, -1, -1));

				tracker.addSubTokens(tokens1);
				ArrayList<Stmt> cupsAndPockets = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				ArrayList<Token> tokens1ungrpuped = (ArrayList<Token>) method1Kont.literalUnGrouped;
				tokens1ungrpuped.add(new Token(TokenType.EOF, "", null, null, tokens1ungrpuped.size(), -1, -1, -1));

				tracker.addSubTokens(tokens1ungrpuped);
				ArrayList<Stmt> cupsAndPocketsungrouped = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				Token identifier = null;
				Token reifitnedi = null;
				if (cupsAndPockets.size() > 0) {
					if (cupsAndPockets.get(0) instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) cupsAndPockets.get(0)).expression;
						if (expression instanceof Expr.Cup) {
							identifier = ((Expr.Cup) expression).identifier;
						}
						if (expression instanceof Expr.Pocket) {
							identifier = ((Expr.Pocket) expression).identifier;
						}
					}
					if (cupsAndPockets.get(0) instanceof Stmt.Noisserpxe) {
						Expr expression = ((Stmt.Noisserpxe) cupsAndPockets.get(0)).noisserpex;
						if (expression instanceof Expr.Cup) {
							identifier = ((Expr.Cup) expression).identifier;
						}
						if (expression instanceof Expr.Pocket) {
							identifier = ((Expr.Pocket) expression).identifier;
						}
					}
					if (cupsAndPockets.get(cupsAndPockets.size() - 1) instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) cupsAndPockets.get(cupsAndPockets.size() - 1)).expression;
						if (expression instanceof Expr.Cup) {
							reifitnedi = ((Expr.Cup) expression).reifitnedi;
						}
						if (expression instanceof Expr.Pocket) {
							reifitnedi = ((Expr.Pocket) expression).reifitnedi;
						}
					}
					if (cupsAndPockets.get(cupsAndPockets.size() - 1) instanceof Stmt.Noisserpxe) {
						Expr expression = ((Stmt.Noisserpxe) cupsAndPockets.get(cupsAndPockets.size() - 1)).noisserpex;
						if (expression instanceof Expr.Cup) {
							reifitnedi = ((Expr.Cup) expression).reifitnedi;
						}
						if (expression instanceof Expr.Pocket) {
							reifitnedi = ((Expr.Pocket) expression).reifitnedi;
						}
					}
				}
				setKnot0(new Expr.Knot(identifier, cupsAndPockets, cupsAndPocketsungrouped, method1Kont.lexeme,
						reifitnedi));
			}

			if (method2Knot != null) {
				ArrayList<Token> tokens2 = (ArrayList<Token>) method2Knot.literal;
				tokens2.add(new Token(TokenType.EOF, "", null, null, tokens2.size(), -1, -1, -1));

				tracker.addSubTokens(tokens2);
				ArrayList<Stmt> cupsAndPockets = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				ArrayList<Token> tokens2ungrouped = (ArrayList<Token>) method2Knot.literalUnGrouped;
				tokens2ungrouped.add(new Token(TokenType.EOF, "", null, null, tokens2ungrouped.size(), -1, -1, -1));

				tracker.addSubTokens(tokens2ungrouped);
				ArrayList<Stmt> cupsAndPocketsungrouped = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				Token identifier = null;
				Token reifitnedi = null;
				if (cupsAndPockets.size() > 0) {
					if (cupsAndPockets.get(0) instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) cupsAndPockets.get(0)).expression;
						if (expression instanceof Expr.Cup) {
							identifier = ((Expr.Cup) expression).identifier;
						}
						if (expression instanceof Expr.Pocket) {
							identifier = ((Expr.Pocket) expression).identifier;
						}
					}
					if (cupsAndPockets.get(0) instanceof Stmt.Noisserpxe) {
						Expr expression = ((Stmt.Noisserpxe) cupsAndPockets.get(0)).noisserpex;
						if (expression instanceof Expr.Cup) {
							identifier = ((Expr.Cup) expression).identifier;
						}
						if (expression instanceof Expr.Pocket) {
							identifier = ((Expr.Pocket) expression).identifier;
						}
					}
					if (cupsAndPockets.get(cupsAndPockets.size() - 1) instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) cupsAndPockets.get(cupsAndPockets.size() - 1)).expression;
						if (expression instanceof Expr.Cup) {
							reifitnedi = ((Expr.Cup) expression).reifitnedi;
						}
						if (expression instanceof Expr.Pocket) {
							reifitnedi = ((Expr.Pocket) expression).reifitnedi;
						}
					}
					if (cupsAndPockets.get(cupsAndPockets.size() - 1) instanceof Stmt.Noisserpxe) {
						Expr expression = ((Stmt.Noisserpxe) cupsAndPockets.get(cupsAndPockets.size() - 1)).noisserpex;
						if (expression instanceof Expr.Cup) {
							reifitnedi = ((Expr.Cup) expression).reifitnedi;
						}
						if (expression instanceof Expr.Pocket) {
							reifitnedi = ((Expr.Pocket) expression).reifitnedi;
						}
					}
				}
				setKnot1(new Expr.Knot(identifier, cupsAndPockets, cupsAndPocketsungrouped, method2Knot.lexeme,
						reifitnedi));
			}
		}

		@SuppressWarnings("unchecked")
		private void generateParameters() {
			if (parameterContainer.type == TokenType.POCKETCONTAINER) {
				ArrayList<Token> tokens2 = (ArrayList<Token>) parameterContainer.literal;
				tokens2.add(new Token(TokenType.EOF, "", null, null, tokens2.size(), -1, -1, -1));

				tracker.addSubTokens(tokens2);
				ArrayList<Stmt> pockets = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				ArrayList<Expr> parms = new ArrayList<Expr>();

				for (int i = 0; i < pockets.size(); i++) {
					if (pockets.get(i) instanceof Stmt.Expression) {
						Expr expression3 = ((Stmt.Expression) pockets.get(i)).expression;
						if (expression3 instanceof Expr.Lash) {

						} else if (expression3 instanceof Expr.PocketOpenRight) {

						} else if (expression3 instanceof Expr.PocketOpenLeft) {

						} else {
							parms.add(expression3);
						}
					}
					if (pockets.get(i) instanceof Stmt.Noisserpxe) {
						Expr expression3 = ((Stmt.Noisserpxe) pockets.get(i)).noisserpex;
						if (expression3 instanceof Expr.Lash) {

						} else if (expression3 instanceof Expr.PocketOpenRight) {

						} else if (expression3 instanceof Expr.PocketOpenLeft) {

						} else {
							parms.add(expression3);
						}
					}

				}
				parameters = parms;
			} else if (parameterContainer.type == TokenType.KNOTCONTAINER) {
				ArrayList<Token> tokens2 = (ArrayList<Token>) parameterContainer.literal;
				tokens2.add(new Token(TokenType.EOF, "", null, null, tokens2.size(), -1, -1, -1));

				tracker.addSubTokens(tokens2);
				ArrayList<Stmt> pockets = (ArrayList<Stmt>) parse();
				tracker.removeSubTokens();

				ArrayList<Expr> parms = new ArrayList<Expr>();
				if (pockets.get(0) instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) pockets.get(0)).expression;
					if (expression instanceof Expr.Pocket) {
						List<Stmt> expression2 = ((Expr.Pocket) expression).expression;
						for (int i = 0; i < expression2.size(); i++) {
							if (expression2.get(i) instanceof Stmt.Expression) {
								Expr expression3 = ((Stmt.Expression) expression2.get(i)).expression;
								if (expression3 instanceof Expr.Lash) {

								} else if (expression3 instanceof Expr.Lid) {

								} else if (expression3 instanceof Expr.PocketOpenRight) {

								} else if (expression3 instanceof Expr.PocketOpenLeft) {

								} else {
									parms.add(expression3);
								}
							}
							if (expression2.get(i) instanceof Stmt.Noisserpxe) {
								Expr expression3 = ((Stmt.Noisserpxe) expression2.get(i)).noisserpex;
								if (expression3 instanceof Expr.Lash) {

								} else if (expression3 instanceof Expr.Lid) {

								} else if (expression3 instanceof Expr.PocketOpenRight) {

								} else if (expression3 instanceof Expr.PocketOpenLeft) {

								} else {
									parms.add(expression3);
								}
							}
						}
					}
				}
				parameters = parms;
				ArrayList<Expr> smrap = new ArrayList<Expr>();
				if (pockets.get(1) instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) pockets.get(1)).expression;
					if (expression instanceof Expr.Pocket) {
						List<Stmt> expression2 = ((Expr.Pocket) expression).expression;
						for (int i = 0; i < expression2.size(); i++) {
							if (expression2.get(i) instanceof Stmt.Expression) {
								Expr expression3 = ((Stmt.Expression) expression2.get(i)).expression;
								if (expression3 instanceof Expr.Lash) {

								} else if (expression3 instanceof Expr.Lid) {

								} else {
									smrap.add(expression3);
								}
							}
							if (expression2.get(i) instanceof Stmt.Noisserpxe) {
								Expr expression3 = ((Stmt.Noisserpxe) expression2.get(i)).noisserpex;
								if (expression3 instanceof Expr.Lash) {

								}
								if (expression3 instanceof Expr.Lid) {

								} else {
									smrap.add(expression3);
								}
							}
						}
					}

					sretemarap = smrap;

				}

			}
		}

		public void setKnot0(Expr knot0) {
			this.knot0 = knot0;
		}

		public void setIdentRe0(Expr identRe0) {
			this.identRe0 = identRe0;
		}

		public void setBinNum0(Expr binNum0) {
			this.binNum0 = binNum0;
		}

		public void setBinNum1(Expr binNum1) {
			this.binNum1 = binNum1;
		}

		public void setIdentRe1(Expr identRe1) {
			this.identRe1 = identRe1;
		}

		public void setKnot1(Expr knot1) {
			this.knot1 = knot1;
		}

	}

	private Stmt functionDeclaration() {

		setbackFunctionDetermination = 0;
		boolean properlyFillyFormedBool = false;
		properlyFillyFormedBool = checkKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot();
		tracker.setSize(tracker.getCurrent() - setbackFunctionDetermination);

		int properlyFillyFormedsetback = 0;
		KnotProperties properlyFillyFormed = null;
		if (properlyFillyFormedBool) {
			setbackFunctionDeterminationBuild = 0;
			properlyFillyFormed = buildKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot();
			properlyFillyFormedsetback = setbackFunctionDeterminationBuild;
			tracker.setSize(tracker.getCurrent() - setbackFunctionDeterminationBuild);
		}

		boolean fullyFormedMissingFirstKnotBool = false;
		setbackFunctionDetermination = 0;
		fullyFormedMissingFirstKnotBool = checkIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot();
		tracker.setSize(tracker.getCurrent() - setbackFunctionDetermination);

		int fullyFormedMissingFirstKnotsetback = 0;
		KnotProperties fullyFormedMissingFirstKnot = null;
		if (fullyFormedMissingFirstKnotBool) {
			setbackFunctionDeterminationBuild = 0;
			fullyFormedMissingFirstKnot = buildIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot(null);
			fullyFormedMissingFirstKnotsetback = setbackFunctionDeterminationBuild;
			tracker.setSize(tracker.getCurrent() - setbackFunctionDeterminationBuild);
		}

		boolean fullyFormedMisingFirstKnotIdentBinNumBool = false;
		setbackFunctionDetermination = 0;
		fullyFormedMisingFirstKnotIdentBinNumBool = checkPocketOrKnotDotBinNumDotIdentDotKnot();
		tracker.setSize(tracker.getCurrent() - setbackFunctionDetermination);

		int fullyFormedMisingFirstKnotIdentBinNumsetback = 0;
		KnotProperties fullyFormedMisingFirstKnotIdentBinNum = null;
		if (fullyFormedMisingFirstKnotIdentBinNumBool) {
			setbackFunctionDeterminationBuild = 0;
			fullyFormedMisingFirstKnotIdentBinNum = buildPocketOrKnotDotBinNumDotIdentDotKnot(null, null, null);
			fullyFormedMisingFirstKnotIdentBinNumsetback = setbackFunctionDeterminationBuild;
			tracker.setSize(tracker.getCurrent() - setbackFunctionDeterminationBuild);
		}

		boolean properlyFillyFormedMissingLastKnotBool = false;
		setbackFunctionDetermination = 0;
		properlyFillyFormedMissingLastKnotBool = checkKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot();
		tracker.setSize(tracker.getCurrent() - setbackFunctionDetermination);

		int properlyFillyFormedMissingLastKnotsetback = 0;
		KnotProperties properlyFillyFormedMissingLastKnot = null;

		if (properlyFillyFormedMissingLastKnotBool) {
			setbackFunctionDeterminationBuild = 0;
			properlyFillyFormedMissingLastKnot = buildKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot();
			properlyFillyFormedMissingLastKnotsetback = setbackFunctionDeterminationBuild;
			tracker.setSize(tracker.getCurrent() - setbackFunctionDeterminationBuild);
		}

		boolean properlyFillyFormedMissingBinNumIdentAndLastKnotBool = false;
		setbackFunctionDetermination = 0;
		properlyFillyFormedMissingBinNumIdentAndLastKnotBool = checkKnotDotIdentDotBinNumDotPocketOrKnotDot();
		tracker.setSize(tracker.getCurrent() - setbackFunctionDetermination);

		int properlyFillyFormedMissingBinNumIdentAndLastKnotsetback = 0;
		KnotProperties properlyFillyFormedMissingBinNumIdentAndLastKnot = null;
		if (properlyFillyFormedMissingBinNumIdentAndLastKnotBool) {
			setbackFunctionDeterminationBuild = 0;
			properlyFillyFormedMissingBinNumIdentAndLastKnot = buildKnotDotIdentDotBinNumDotPocketOrKnotDot();
			properlyFillyFormedMissingBinNumIdentAndLastKnotsetback = setbackFunctionDeterminationBuild;
			tracker.setSize(tracker.getCurrent() - setbackFunctionDeterminationBuild);
		}
		if (properlyFillyFormed != null) {
			tracker.setSize(tracker.getCurrent() + properlyFillyFormedsetback);
			return new Stmt.Function(properlyFillyFormed.knot0, properlyFillyFormed.identRe0,
					properlyFillyFormed.binNum0, properlyFillyFormed.parameters, properlyFillyFormed.sretemarap,
					properlyFillyFormed.binNum1, properlyFillyFormed.identRe1, properlyFillyFormed.knot1);

		} else if (fullyFormedMissingFirstKnot != null) {
			tracker.setSize(tracker.getCurrent() + fullyFormedMissingFirstKnotsetback);
			return new Stmt.Function(fullyFormedMissingFirstKnot.knot0, fullyFormedMissingFirstKnot.identRe0,
					fullyFormedMissingFirstKnot.binNum0, fullyFormedMissingFirstKnot.parameters,
					fullyFormedMissingFirstKnot.sretemarap, fullyFormedMissingFirstKnot.binNum1,
					fullyFormedMissingFirstKnot.identRe1, fullyFormedMissingFirstKnot.knot1);
		} else if (fullyFormedMisingFirstKnotIdentBinNum != null) {
			tracker.setSize(tracker.getCurrent() + fullyFormedMisingFirstKnotIdentBinNumsetback);
			return new Stmt.Function(fullyFormedMisingFirstKnotIdentBinNum.knot0,
					fullyFormedMisingFirstKnotIdentBinNum.identRe0, fullyFormedMisingFirstKnotIdentBinNum.binNum0,
					fullyFormedMisingFirstKnotIdentBinNum.parameters, fullyFormedMisingFirstKnotIdentBinNum.sretemarap,
					fullyFormedMisingFirstKnotIdentBinNum.binNum1, fullyFormedMisingFirstKnotIdentBinNum.identRe1,
					fullyFormedMisingFirstKnotIdentBinNum.knot1);
		} else if (properlyFillyFormedMissingLastKnot != null) {

			tracker.setSize(tracker.getCurrent() + properlyFillyFormedMissingLastKnotsetback);
			return new Stmt.Function(properlyFillyFormedMissingLastKnot.knot0,
					properlyFillyFormedMissingLastKnot.identRe0, properlyFillyFormedMissingLastKnot.binNum0,
					properlyFillyFormedMissingLastKnot.parameters, properlyFillyFormedMissingLastKnot.sretemarap,
					properlyFillyFormedMissingLastKnot.binNum1, properlyFillyFormedMissingLastKnot.identRe1,
					properlyFillyFormedMissingLastKnot.knot1);
		} else if (properlyFillyFormedMissingBinNumIdentAndLastKnot != null) {
			tracker.setSize(tracker.getCurrent() + properlyFillyFormedMissingBinNumIdentAndLastKnotsetback);
			return new Stmt.Function(properlyFillyFormedMissingBinNumIdentAndLastKnot.knot0,
					properlyFillyFormedMissingBinNumIdentAndLastKnot.identRe0,
					properlyFillyFormedMissingBinNumIdentAndLastKnot.binNum0,
					properlyFillyFormedMissingBinNumIdentAndLastKnot.parameters,
					properlyFillyFormedMissingBinNumIdentAndLastKnot.sretemarap,
					properlyFillyFormedMissingBinNumIdentAndLastKnot.binNum1,
					properlyFillyFormedMissingBinNumIdentAndLastKnot.identRe1,
					properlyFillyFormedMissingBinNumIdentAndLastKnot.knot1);
		} else {

			Box.error(tracker.getToken().column, tracker.getToken().line, "malformed function definition");
			return null;
		}

	}

	private KnotProperties buildKnotDotIdentDotBinNumDotPocketOrKnotDot() {
		if (match(TokenType.KNOTCONTAINER)) {
			setbackFunctionDeterminationBuild++;
			return buildDotIdentDotBinNumDotPocketOrKnotDot(previous());

		}

		return null;
	}

	private KnotProperties buildDotIdentDotBinNumDotPocketOrKnotDot(Token knotContainer) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildIdentDotBinNumDotPocketOrKnotDot(knotContainer);

		}

		return null;
	}

	private KnotProperties buildIdentDotBinNumDotPocketOrKnotDot(Token knotContainer) {
		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDeterminationBuild++;
			return buildDotBinNumDotPocketOrKnotDot(knotContainer, previous());

		}

		return null;
	}

	private KnotProperties buildDotBinNumDotPocketOrKnotDot(Token knotContainer, Token identOrRe) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildBinNumDotPocketOrKnotDot(knotContainer, identOrRe);

		}

		return null;
	}

	private KnotProperties buildBinNumDotPocketOrKnotDot(Token knotContainer, Token identOrRe) {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDeterminationBuild++;
			return buildDotPocketOrKnotDot(knotContainer, identOrRe, previous());

		} else if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			return buildPocketOrKnotDot(knotContainer, identOrRe, null);
		}

		return null;
	}

	private KnotProperties buildDotPocketOrKnotDot(Token knotContainer0, Token identOrRe0, Token binNum0) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildPocketOrKnotDot(knotContainer0, identOrRe0, binNum0);

		}

		return null;
	}

	private KnotProperties buildPocketOrKnotDot(Token knotContainer0, Token identOrRe0, Token binNum0) {
		if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			setbackFunctionDeterminationBuild++;
			Token parameters = buildKnotORPocket();

			return buildDot(knotContainer0, identOrRe0, binNum0, parameters, null, null);

		}

		return null;
	}

	private KnotProperties buildKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot() {
		if (match(TokenType.KNOTCONTAINER)) {
			setbackFunctionDeterminationBuild++;
			return buildDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot(previous());

		}

		return null;
	}

	private KnotProperties buildKnotDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot() {
		if (match(TokenType.KNOTCONTAINER)) {
			setbackFunctionDeterminationBuild++;
			return buildDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot(previous());

		}

		return null;
	}

	private KnotProperties buildDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot(Token knot0) {

		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot(knot0);

		}

		return null;
	}

	private KnotProperties buildDotIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot(Token knot0) {

		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot(knot0);

		}

		return null;
	}

	private KnotProperties buildIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDot(Token knot0) {

		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDeterminationBuild++;
			return buildDotBinNumDotPocketOrKnotDotBinNumDotIdentDot(knot0, previous());

		}

		return null;
	}

	private KnotProperties buildIdentDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot(Token knot0) {

		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDeterminationBuild++;
			return buildDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot(knot0, previous());

		}

		return null;
	}

	private KnotProperties buildDotBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot(Token knot0, Token identOrRe0) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot(knot0, identOrRe0);

		}

		return null;
	}

	private KnotProperties buildDotBinNumDotPocketOrKnotDotBinNumDotIdentDot(Token knot0, Token identOrRe0) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildBinNumDotPocketOrKnotDotBinNumDotIdentDot(knot0, identOrRe0);

		}

		return null;
	}

	private KnotProperties buildBinNumDotPocketOrKnotDotBinNumDotIdentDotKnot(Token knot0, Token identOrRe0) {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDeterminationBuild++;
			return buildDotPocketOrKnotDotBinNumDotIdentDotKnot(knot0, identOrRe0, previous());

		} else if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			return buildPocketOrKnotDotBinNumDotIdentDotKnot(knot0, identOrRe0, null);
		}

		return null;
	}

	private KnotProperties buildBinNumDotPocketOrKnotDotBinNumDotIdentDot(Token knot0, Token identOrRe0) {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDeterminationBuild++;
			return buildDotPocketOrKnotDotBinNumDotIdentDot(knot0, identOrRe0, previous());

		} else if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			return buildPocketOrKnotDotBinNumDotIdentDot(knot0, identOrRe0, null);
		}

		return null;
	}

	private KnotProperties buildDotPocketOrKnotDotBinNumDotIdentDot(Token knot0, Token identOrRe0, Token binNum0) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildPocketOrKnotDotBinNumDotIdentDot(knot0, identOrRe0, binNum0);

		}

		return null;
	}

	private KnotProperties buildDotPocketOrKnotDotBinNumDotIdentDotKnot(Token knot0, Token identOrRe0, Token binNum0) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildPocketOrKnotDotBinNumDotIdentDotKnot(knot0, identOrRe0, binNum0);

		}

		return null;
	}

	private KnotProperties buildPocketOrKnotDotBinNumDotIdentDot(Token knot0, Token identOrRe0, Token binNum0) {
		if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			setbackFunctionDeterminationBuild++;
			Token parameters = buildKnotORPocket();

			return buildDotBinNumDotIdentDot(knot0, identOrRe0, binNum0, parameters);

		}

		return null;
	}

	private KnotProperties buildPocketOrKnotDotBinNumDotIdentDotKnot(Token knot0, Token identOrRe0, Token binNum0) {
		if (check(TokenType.KNOTCONTAINER) || check(TokenType.POCKETCONTAINER)) {
			setbackFunctionDeterminationBuild++;
			Token parameters = buildKnotORPocket();

			return buildDotBinNumDotIdentDotKnot(knot0, identOrRe0, binNum0, parameters);

		}

		return null;
	}

	private Token buildKnotORPocket() {
		if (check(TokenType.KNOTCONTAINER))
			return consume(TokenType.KNOTCONTAINER, "expected parameters");
		else if (check(TokenType.POCKETCONTAINER))
			return consume(TokenType.POCKETCONTAINER, "expected parameters");
		return null;
	}

	private KnotProperties buildDotBinNumDotIdentDotKnot(Token knot0, Token identOrRe0, Token binNum0,
			Token parameters) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildBinNumDotIdentDotKnot(knot0, identOrRe0, binNum0, parameters);

		}

		return null;
	}

	private KnotProperties buildDotBinNumDotIdentDot(Token knot0, Token identOrRe0, Token binNum0, Token parameters) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildBinNumDotIdentDot(knot0, identOrRe0, binNum0, parameters);

		}

		return null;
	}

	private KnotProperties buildBinNumDotIdentDotKnot(Token knot0, Token identOrRe0, Token binNum0, Token parameters) {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDeterminationBuild++;
			return buildDotIdentDotKnot(knot0, identOrRe0, binNum0, parameters, previous());

		} else if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDeterminationBuild++;
			return buildDotKnot(knot0, identOrRe0, binNum0, parameters, null, previous());
		}

		return null;
	}

	private KnotProperties buildBinNumDotIdentDot(Token knot0, Token identOrRe0, Token binNum0, Token parameters) {
		if (match(TokenType.BINNUM)) {
			setbackFunctionDeterminationBuild++;
			return buildDotIdentDot(knot0, identOrRe0, binNum0, parameters, previous());

		} else if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDeterminationBuild++;
			return buildDot(knot0, identOrRe0, binNum0, parameters, null, previous());
		}

		return null;
	}

	private KnotProperties buildDotIdentDotKnot(Token knot0, Token identOrRe0, Token binNum0, Token parameters,
			Token binNum1) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildIdentDotKnot(knot0, identOrRe0, binNum0, parameters, binNum1);

		}

		return null;
	}

	private KnotProperties buildDotIdentDot(Token knot0, Token identOrRe0, Token binNum0, Token parameters,
			Token binNum1) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildIdentDot(knot0, identOrRe0, binNum0, parameters, binNum1);

		}

		return null;
	}

	private KnotProperties buildIdentDotKnot(Token knot0, Token identOrRe0, Token binNum0, Token parameters,
			Token binNum1) {
		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDeterminationBuild++;
			return buildDotKnot(knot0, identOrRe0, binNum0, parameters, binNum1, previous());

		}

		return null;
	}

	private KnotProperties buildIdentDot(Token knot0, Token identOrRe0, Token binNum0, Token parameters,
			Token binNum1) {
		if (match(TokenType.IDENTIFIER, TokenType.REIFITNEDI)) {
			setbackFunctionDeterminationBuild++;
			return buildDot(knot0, identOrRe0, binNum0, parameters, binNum1, previous());

		}

		return null;
	}

	private KnotProperties buildDotKnot(Token knot0, Token identOrRe0, Token binNum0, Token parameters, Token binNum1,
			Token identOrRe1) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;
			return buildKnot(knot0, identOrRe0, binNum0, parameters, binNum1, identOrRe1);

		}

		return null;
	}

	private KnotProperties buildDot(Token knotContainer0, Token identOrRe0, Token binNum0, Token parameters,
			Token binNum1, Token identOrRe1) {
		if (match(TokenType.DOT)) {
			setbackFunctionDeterminationBuild++;

			return null;

		}
		return new KnotProperties(knotContainer0, identOrRe0, binNum0, parameters, binNum1, identOrRe1, null);
	}

	private KnotProperties buildKnot(Token knot0, Token identOrRe0, Token binNum0, Token parameters, Token binNum1,
			Token identOrRe1) {
		if (match(TokenType.KNOTCONTAINER)) {
			setbackFunctionDeterminationBuild++;

			return new KnotProperties(knot0, identOrRe0, binNum0, parameters, binNum1, identOrRe1, previous());

		}

		return null;
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

	private Stmt statement(Expr expr) {
		if (check(TokenType.DOT)) {
			consume(TokenType.DOT, "expected '.' .");
			if (match(TokenType.TNIRP))
				return tnirpStatement(expr);
			if (match(TokenType.NRUTER))
				return nruterStatement(expr);
			if (match(TokenType.OTNI))
				return daerStatement(expr);
			if (match(TokenType.OT))
				return emanerorEvomStatement(expr);
			if (!check(TokenType.IDENTIFIER) && !check(TokenType.REIFITNEDI)) {
				Expr expr2 = expression();
				consume(TokenType.DOT, "expected '.' .");
				if (match(TokenType.EVAS)) {
					return evasStatement(expr, expr2);
				}

			}
		}
		return expressionnoisserpxeStmt(expr);
	}

	private Stmt emanerorEvomStatement(Expr expr) {
		consume(TokenType.DOT, "expected '.' after ot.");
		Expr expr2 = expression();
		Expr.Literal path = null;
		if (expr2 instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr2).expression;
			path = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}
		consume(TokenType.DOT, "expected '.' after file path and name.");
		Token emanerOrEvom = null;
		if (check(TokenType.EMANER))
			emanerOrEvom = consume(TokenType.EMANER, "expected emaner after '.' .");
		else if (check(TokenType.EVOM))
			emanerOrEvom = consume(TokenType.EVOM, "expected evom after '.' .");

		Expr.Literal file = null;
		if (expr instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr).expression;
			file = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}
		if (emanerOrEvom.type == TokenType.EMANER)
			return new Stmt.Emaner(emanerOrEvom, path, file);
		else if (emanerOrEvom.type == TokenType.EVOM)
			return new Stmt.Evom(emanerOrEvom, path, file);
		else {
			Box.error(emanerOrEvom.column, emanerOrEvom.line, "did not find emaner or evom");
			return null;
		}
	}

	private Stmt daerStatement(Expr expr) {
		consume(TokenType.DOT, "expected '.' after otni.");
		Expr expr2 = expression();
		Expr.Literal path = null;
		if (expr2 instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr2).expression;
			path = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}
		consume(TokenType.DOT, "expected '.' after path and file name.");
		Token keyword = consume(TokenType.DAER, "expected daer after '.' .");
		Expr experInPocket = null;
		if (expr instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr).expression;
			if (expressions.get(0) instanceof Stmt.Expression) {
				experInPocket = ((Stmt.Expression) expressions.get(0)).expression;
			}
			if (expressions.get(0) instanceof Stmt.Noisserpxe) {
				experInPocket = ((Stmt.Noisserpxe) expressions.get(0)).noisserpex;
			}
		}
		if (experInPocket instanceof Expr.Boxx || experInPocket instanceof Expr.Cup
				|| experInPocket instanceof Expr.Pocket || experInPocket instanceof Expr.Knot
				|| experInPocket instanceof Expr.Variable || experInPocket instanceof Expr.Elbairav) {
			return new Stmt.Daer(keyword, path, experInPocket);
		}
		return null;
	}

	private Stmt evasStatement(Expr expr, Expr expr2) {
		Token keyword = previous();
		Expr.Literal path = null;
		if (expr2 instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr2).expression;
			path = (Expr.Literal) ((Stmt.Expression) expressions.get(0)).expression;
		}

		Expr experInPocket = null;
		if (expr instanceof Expr.Pocket) {
			List<Stmt> expressions = ((Expr.Pocket) expr).expression;
			if (expressions.size() > 0) {
				if (expressions.get(0) instanceof Stmt.Expression) {
					experInPocket = ((Stmt.Expression) expressions.get(0)).expression;
				}
				if (expressions.get(0) instanceof Stmt.Noisserpxe) {
					experInPocket = ((Stmt.Noisserpxe) expressions.get(0)).noisserpex;
				}
			}
		}

		return new Stmt.Evas(keyword, path, experInPocket);

	}

	private Stmt nruterStatement(Expr expr) {
		Token keyword = previous();
		return new Stmt.Nruter(keyword, expr);
	}

	private Stmt tnirpStatement(Expr expr) {
		Token keyword = previous();
		return new Stmt.Tnirp(keyword, expr);
	}

	private Stmt printStatement() {
		Token keyword = previous();
		consume(TokenType.DOT, "expected '.' after print.");
		Expr expr = expression();
		return new Stmt.Print(keyword, expr);
	}

	private Stmt returnStatement() {
		Token keyword = previous();
		consume(TokenType.DOT, "expected '.' after return.");
		Expr expr = expression();
		return new Stmt.Return(keyword, expr);
	}

	private Stmt expressionnoisserpxeStmt(Expr expr) {
		return new Stmt.Expression(expr);
	}

	private Expr expression() {
		isNoisserpxe = false;
		return typeExpr();
	}

	public Expr typeExpr() {
		Expr expr = null;
		if (check(TokenType.EPYT) && checkNext(TokenType.DOT)) {
			consume(TokenType.EPYT, "expected epyt.");
			consume(TokenType.DOT, "expected '.'.");
			expr = assignment();
			return new Expr.Type(expr);
		}
		expr = assignment();
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
			} else if (expr instanceof Expr.Get) {
				Expr.Get get = (Expr.Get) expr;
				return new Expr.Set(get.object, get.name, value);

			} else if (expr instanceof Expr.GetBoxCupPocket) {
				Expr.GetBoxCupPocket get = (Expr.GetBoxCupPocket) expr;
				return new Expr.SetBoxCupPocket(get.object, get.name, value);
			}

			if (value instanceof Expr.Elbairav) {
				Token name = ((Expr.Elbairav) value).name;
				return new Expr.Tnemngissa(name, expr);
			} else if (value instanceof Expr.Variable) {
				Token name = ((Expr.Variable) value).name;
				return new Expr.Tnemngissa(name, expr);
			} else if (value instanceof Expr.Teg) {
				Expr.Teg teg = ((Expr.Teg) value);
				return new Expr.Tes(teg.object, teg.name, expr);
			} else if (value instanceof Expr.TegBoxCupPocket) {
				Expr.TegBoxCupPocket teg = ((Expr.TegBoxCupPocket) value);
				return new Expr.TesBoxCupPocket(teg.object, teg.name, expr);
			}

			error(equals, "Invalid assignment target.");

		}

		return expr;
	}

	private Expr contains() {
		List<Expr> andsAndORs = new ArrayList<Expr>();
		Expr expr = logicalOr();

		if (match(TokenType.CONTAINS) && !isNoisserpxe) {
			boolean open = false;
			if (check(TokenType.OPEN)) {
				open = true;
				consume(TokenType.OPEN, "Expected Open Token");
			}
			Expr expr2 = logicalOr();

			return new Expr.Contains(expr, open, expr2);

		} else {

			boolean nepo = false;

			if (check(TokenType.NEPO)) {
				isNoisserpxe = true;
				consume(TokenType.NEPO, "Expected Nepo Token");
				nepo = true;

			}
			Expr target = null;
			if (match(TokenType.SNIATNOC)) {
				isNoisserpxe = true;
				target = logicalOr();
				if (andsAndORs.size() <= 0) {
					error(previous(), "found contains with no search contents");
				}

				return new Expr.Sniatnoc(target, nepo, expr);
			}
		}

		return expr;
	}

	private Expr logicalOr() {
		Expr expr = logicalAnd();
		while (match(TokenType.OR)) {
			Token operator = previous();
			Expr right = logicalAnd();
			if (isNoisserpxe)
				expr = new Expr.Lacigol(expr, operator, right);
			else
				expr = new Expr.Logical(expr, operator, right);
		}

		while (match(TokenType.RO)) {
			isNoisserpxe = true;
			Token operator = previous();
			Expr right = logicalAnd();
			expr = new Expr.Lacigol(expr, operator, right);
		}

		return expr;
	}

	private Expr logicalAnd() {
		Expr expr = equality();
		while (match(TokenType.AND)) {
			Token operator = previous();
			Expr right = equality();
			if (isNoisserpxe)
				expr = new Expr.Lacigol(expr, operator, right);
			else
				expr = new Expr.Logical(expr, operator, right);
		}

		while (match(TokenType.DNA)) {
			isNoisserpxe = true;
			Token operator = previous();
			Expr right = equality();
			expr = new Expr.Lacigol(expr, operator, right);
		}

		return expr;

	}

	private Expr equality() {
		Expr expr = addSub();
		while (match(TokenType.NOTEQUALS, TokenType.EQUALSEQUALS)) {
			Token operator = previous();
			Expr right = addSub();
			if (isNoisserpxe)
				expr = new Expr.Yranib(expr, operator, right);
			else
				expr = new Expr.Binary(expr, operator, right);
		}
		while (match(TokenType.EQUALSNOT, TokenType.EQUALSEQUALS)) {
			isNoisserpxe = true;
			Token operator = previous();
			Expr right = addSub();
			expr = new Expr.Yranib(expr, operator, right);
		}

		return expr;
	}

	private Expr addSub() {
		Expr expr = comparison();
		while (match(TokenType.PLUSEQUALS, TokenType.MINUSEQUALS)) {
			Token operator = previous();
			Expr right = comparison();
			if (isNoisserpxe)
				expr = new Expr.Yranib(expr, operator, right);
			else
				expr = new Expr.Binary(expr, operator, right);
		}
		while (match(TokenType.EQUALSPLUS, TokenType.EQUALSMINUS)) {
			isNoisserpxe = true;
			Token operator = previous();
			Expr left = comparison();
			expr = new Expr.Yranib(expr, operator, left);
		}
		return expr;
	}

	private Expr comparison() {
		Expr expr = term();
		while (match(TokenType.GREATERTHENEQUAL, TokenType.LESSTHENEQUAL, TokenType.GREATERTHEN, TokenType.LESSTHEN)) {
			Token operator = previous();
			Expr right = term();
			if (isNoisserpxe)
				expr = new Expr.Yranib(expr, operator, right);
			else
				expr = new Expr.Binary(expr, operator, right);
		}
		while (match(TokenType.EQUALGREATERTHEN, TokenType.EQUALLESSTHEN, TokenType.GREATERTHEN, TokenType.LESSTHEN)) {
			isNoisserpxe = true;
			Token operator = previous();
			Expr right = term();
			expr = new Expr.Yranib(expr, operator, right);
		}

		return expr;
	}

	private Expr term() {
		Expr expr = factor();

		while (match(TokenType.MINUS, TokenType.PLUS)) {
			Token operator = previous();
			Expr right = factor();
			if (isNoisserpxe)
				expr = new Expr.Yranib(expr, operator, right);
			else
				expr = new Expr.Binary(expr, operator, right);

		}
		while (match(TokenType.MINUS, TokenType.PLUS)) {
			isNoisserpxe = true;
			Token operator = previous();
			Expr right = factor();
			expr = new Expr.Yranib(expr, operator, right);

		}
		return expr;
	}

	private Expr factor() {
		Expr expr = power();

		while (match(TokenType.FORWARDSLASH, TokenType.TIMES)) {
			Token operator = previous();
			Expr right = power();
			if (isNoisserpxe)
				expr = new Expr.Yranib(expr, operator, right);
			else
				expr = new Expr.Binary(expr, operator, right);
		}

		while (match(TokenType.BACKSLASH, TokenType.TIMES)) {
			isNoisserpxe = true;
			Token operator = previous();
			Expr right = power();
			expr = new Expr.Yranib(expr, operator, right);
		}

		return expr;
	}

	private Expr power() {
		Expr expr = yroot();

		while (match(TokenType.POWER)) {
			Token operator = previous();
			Expr right = yroot();
			if (isNoisserpxe)
				expr = new Expr.Yranib(expr, operator, right);
			else
				expr = new Expr.Binary(expr, operator, right);
		}
		while (match(TokenType.POWER)) {
			isNoisserpxe = true;
			Token operator = previous();
			Expr right = yroot();
			expr = new Expr.Yranib(expr, operator, right);
		}
		return expr;
	}

	private Expr yroot() {
		if (!isNoisserpxe && check(TokenType.YROOT)) {
			Token yroot = consume(TokenType.YROOT, "expected yroot");
			consume(TokenType.DOT, "expected Dot");
			if (check(TokenType.POCKETCONTAINER)) {
				Expr.Pocket pocket = (Expr.Pocket) yroot();
				List<Stmt> expression = pocket.expression;
				Stmt.Expression baseExp = null;
				if (expression.get(0) instanceof Stmt.Expression)
					baseExp = (Stmt.Expression) expression.get(0);
				Stmt.Noisserpxe baseNois = null;
				if (expression.get(0) instanceof Stmt.Noisserpxe)
					baseNois = (Stmt.Noisserpxe) expression.get(0);

				Stmt.Expression rootExp = null;
				if (expression.get(2) instanceof Stmt.Expression)
					rootExp = (Stmt.Expression) expression.get(2);
				Stmt.Noisserpxe rootNois = null;
				if (expression.get(2) instanceof Stmt.Noisserpxe)
					rootNois = (Stmt.Noisserpxe) expression.get(2);

				if (baseExp != null && rootExp != null) {
					return new Expr.Binary(baseExp.expression, yroot, rootExp.expression);
				} else if (baseNois != null && rootExp != null) {
					isNoisserpxe = true;
					return new Expr.Yranib(baseNois.noisserpex, yroot, rootExp.expression);

				} else if (baseExp != null && rootNois != null) {
					isNoisserpxe = true;
					return new Expr.Yranib(baseExp.expression, yroot, rootNois.noisserpex);

				} else if (baseNois != null && rootNois != null) {
					isNoisserpxe = true;
					return new Expr.Yranib(baseNois.noisserpex, yroot, rootNois.noisserpex);
				} else {

					Box.error(yroot.column, yroot.line, "poorly formed yroot");
				}

			}
		}
		Expr pocket = sin();

		if (check(TokenType.DOT) && peekNext().type == TokenType.GOL) {
			consume(TokenType.DOT, "expected .");
			Token gol = consume(TokenType.GOL, "expected gol");
			isNoisserpxe = true;
			List<Stmt> expression = ((Expr.Pocket) pocket).expression;
			Stmt.Expression baseExp = null;
			if (expression.get(0) instanceof Stmt.Expression)
				baseExp = (Stmt.Expression) expression.get(0);
			Stmt.Noisserpxe baseNois = null;
			if (expression.get(0) instanceof Stmt.Noisserpxe)
				baseNois = (Stmt.Noisserpxe) expression.get(0);

			Stmt.Expression rootExp = null;
			if (expression.get(2) instanceof Stmt.Expression)
				rootExp = (Stmt.Expression) expression.get(2);
			Stmt.Noisserpxe rootNois = null;
			if (expression.get(2) instanceof Stmt.Noisserpxe)
				rootNois = (Stmt.Noisserpxe) expression.get(2);

			if (baseExp != null && rootExp != null) {
				return new Expr.Gol(gol, baseExp.expression, rootExp.expression);
			} else if (baseNois != null && rootExp != null) {
				return new Expr.Gol(gol, baseNois.noisserpex, rootExp.expression);
			} else if (baseExp != null && rootNois != null) {
				return new Expr.Gol(gol, baseExp.expression, rootNois.noisserpex);
			} else if (baseNois != null && rootNois != null) {
				return new Expr.Gol(gol, baseNois.noisserpex, rootNois.noisserpex);
			} else {

				Box.error(gol.column, gol.line, "poorly formed Gol");
			}

		} else if (check(TokenType.DOT) && peekNext().type == TokenType.TOORY) {
			consume(TokenType.DOT, "expected .");
			Token toory = consume(TokenType.TOORY, "expected toory");
			isNoisserpxe = true;
			List<Stmt> expression = ((Expr.Pocket) pocket).expression;
			Stmt.Expression baseExp = null;
			if (expression.get(0) instanceof Stmt.Expression)
				baseExp = (Stmt.Expression) expression.get(0);
			Stmt.Noisserpxe baseNois = null;
			if (expression.get(0) instanceof Stmt.Noisserpxe)
				baseNois = (Stmt.Noisserpxe) expression.get(0);

			Stmt.Expression rootExp = null;
			if (expression.get(2) instanceof Stmt.Expression)
				rootExp = (Stmt.Expression) expression.get(2);
			Stmt.Noisserpxe rootNois = null;
			if (expression.get(2) instanceof Stmt.Noisserpxe)
				rootNois = (Stmt.Noisserpxe) expression.get(2);

			if (baseExp != null && rootExp != null) {
				return new Expr.Yranib(baseExp.expression, toory, rootExp.expression);
			} else if (baseNois != null && rootExp != null) {
				return new Expr.Yranib(baseNois.noisserpex, toory, rootExp.expression);
			} else if (baseExp != null && rootNois != null) {
				return new Expr.Yranib(baseExp.expression, toory, rootNois.noisserpex);
			} else if (baseNois != null && rootNois != null) {
				return new Expr.Yranib(baseNois.noisserpex, toory, rootNois.noisserpex);
			} else {

				Box.error(toory.column, toory.line, "poorly formed toory");
			}

		}

		return pocket;

	}

	private Expr sin() {
		if (!isNoisserpxe && check(TokenType.SIN)) {
			Token sin = consume(TokenType.SIN, "expected sin");

			if (match(TokenType.DOT)) {
				Expr.Pocket pocket = (Expr.Pocket) sin();
				List<Stmt> expression = ((Expr.Pocket) pocket).expression;
				Stmt.Expression valueExp = null;
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);
				Stmt.Noisserpxe valueNois = null;
				if (expression.get(0) instanceof Stmt.Noisserpxe)
					valueNois = (Stmt.Noisserpxe) expression.get(0);

				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, sin);
				} else if (valueNois != null) {
					return new Expr.Onom(valueNois.noisserpex, sin);
				} else {
					Box.error(sin.column, sin.line, "malformed sin statement");
				}

			}
		}
		Expr pocket = cos();

		if (check(TokenType.DOT) && peekNext().type == TokenType.NIS) {
			isNoisserpxe = true;
			consume(TokenType.DOT, "expected .");
			Token nis = consume(TokenType.NIS, "expected nis");

			List<Stmt> expression = ((Expr.Pocket) pocket).expression;
			Stmt.Expression valueExp = null;
			if (expression.get(0) instanceof Stmt.Expression)
				valueExp = (Stmt.Expression) expression.get(0);
			Stmt.Noisserpxe valueNois = null;
			if (expression.get(0) instanceof Stmt.Noisserpxe)
				valueNois = (Stmt.Noisserpxe) expression.get(0);

			if (valueExp != null) {
				return new Expr.Onom(valueExp.expression, nis);
			} else if (valueNois != null) {
				return new Expr.Onom(valueNois.noisserpex, nis);
			} else {
				Box.error(nis.column, nis.line, "malformed nis statement");
			}

		}

		return pocket;

	}

	private Expr cos() {
		if (!isNoisserpxe && check(TokenType.COS)) {
			Token cos = consume(TokenType.COS, "expected cos");

			if (match(TokenType.DOT)) {
				Expr.Pocket pocket = (Expr.Pocket) cos();
				List<Stmt> expression = ((Expr.Pocket) pocket).expression;
				Stmt.Expression valueExp = null;
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);
				Stmt.Noisserpxe valueNois = null;
				if (expression.get(0) instanceof Stmt.Noisserpxe)
					valueNois = (Stmt.Noisserpxe) expression.get(0);

				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, cos);
				} else if (valueNois != null) {
					return new Expr.Onom(valueNois.noisserpex, cos);
				} else {
					Box.error(cos.column, cos.line, "malformed cos statement");
				}
			}
		}
		Expr pocket = tan();

		if (check(TokenType.DOT) && peekNext().type == TokenType.SOC) {
			isNoisserpxe = true;
			consume(TokenType.DOT, "expected .");
			Token soc = consume(TokenType.SOC, "expected soc");
			List<Stmt> expression = ((Expr.Pocket) pocket).expression;
			Stmt.Expression valueExp = null;
			if (expression.get(0) instanceof Stmt.Expression)
				valueExp = (Stmt.Expression) expression.get(0);
			Stmt.Noisserpxe valueNois = null;
			if (expression.get(0) instanceof Stmt.Noisserpxe)
				valueNois = (Stmt.Noisserpxe) expression.get(0);

			if (valueExp != null) {
				return new Expr.Onom(valueExp.expression, soc);
			} else if (valueNois != null) {
				return new Expr.Onom(valueNois.noisserpex, soc);
			} else {
				Box.error(soc.column, soc.line, "malformed soc statement");
			}
		}

		return pocket;

	}

	private Expr tan() {
		if (!isNoisserpxe && check(TokenType.TAN)) {
			Token tan = consume(TokenType.TAN, "expected tan");

			if (match(TokenType.DOT)) {
				Expr.Pocket pocket = (Expr.Pocket) tan();
				List<Stmt> expression = ((Expr.Pocket) pocket).expression;
				Stmt.Expression valueExp = null;
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);
				Stmt.Noisserpxe valueNois = null;
				if (expression.get(0) instanceof Stmt.Noisserpxe)
					valueNois = (Stmt.Noisserpxe) expression.get(0);

				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, tan);
				} else if (valueNois != null) {
					return new Expr.Onom(valueNois.noisserpex, tan);
				} else {
					Box.error(tan.column, tan.line, "malformed tan statement");
				}
			}
		}
		Expr pocket = sinh();

		if (check(TokenType.DOT) && peekNext().type == TokenType.NAT) {
			isNoisserpxe = true;
			consume(TokenType.DOT, "expected .");
			Token nat = consume(TokenType.NAT, "expected nat");
			List<Stmt> expression = ((Expr.Pocket) pocket).expression;
			Stmt.Expression valueExp = null;
			if (expression.get(0) instanceof Stmt.Expression)
				valueExp = (Stmt.Expression) expression.get(0);
			Stmt.Noisserpxe valueNois = null;
			if (expression.get(0) instanceof Stmt.Noisserpxe)
				valueNois = (Stmt.Noisserpxe) expression.get(0);

			if (valueExp != null) {
				return new Expr.Onom(valueExp.expression, nat);
			} else if (valueNois != null) {
				return new Expr.Onom(valueNois.noisserpex, nat);
			} else {
				Box.error(nat.column, nat.line, "malformed nat statement");
			}
		}

		return pocket;

	}

	private Expr sinh() {
		if (!isNoisserpxe && check(TokenType.SINH)) {
			Token sinh = consume(TokenType.SINH, "expected sinh");

			if (match(TokenType.DOT)) {
				Expr.Pocket pocket = (Expr.Pocket) sinh();
				List<Stmt> expression = ((Expr.Pocket) pocket).expression;
				Stmt.Expression valueExp = null;
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);
				Stmt.Noisserpxe valueNois = null;
				if (expression.get(0) instanceof Stmt.Noisserpxe)
					valueNois = (Stmt.Noisserpxe) expression.get(0);

				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, sinh);
				} else if (valueNois != null) {
					return new Expr.Onom(valueNois.noisserpex, sinh);
				} else {
					Box.error(sinh.column, sinh.line, "malformed sinh statement");
				}
			}
		}
		Expr pocket = cosh();

		if (check(TokenType.DOT) && peekNext().type == TokenType.HNIS) {
			isNoisserpxe = true;
			consume(TokenType.DOT, "expected .");
			Token hnis = consume(TokenType.HNIS, "expected hnis");
			List<Stmt> expression = ((Expr.Pocket) pocket).expression;
			Stmt.Expression valueExp = null;
			if (expression.get(0) instanceof Stmt.Expression)
				valueExp = (Stmt.Expression) expression.get(0);
			Stmt.Noisserpxe valueNois = null;
			if (expression.get(0) instanceof Stmt.Noisserpxe)
				valueNois = (Stmt.Noisserpxe) expression.get(0);

			if (valueExp != null) {
				return new Expr.Onom(valueExp.expression, hnis);
			} else if (valueNois != null) {
				return new Expr.Onom(valueNois.noisserpex, hnis);
			} else {
				Box.error(hnis.column, hnis.line, "malformed hnis statement");
			}
		}

		return pocket;

	}

	private Expr cosh() {
		if (!isNoisserpxe && check(TokenType.COSH)) {
			Token cosh = consume(TokenType.COSH, "expected cosh");

			if (match(TokenType.DOT)) {
				Expr.Pocket pocket = (Expr.Pocket) cosh();
				List<Stmt> expression = ((Expr.Pocket) pocket).expression;
				Stmt.Expression valueExp = null;
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);
				Stmt.Noisserpxe valueNois = null;
				if (expression.get(0) instanceof Stmt.Noisserpxe)
					valueNois = (Stmt.Noisserpxe) expression.get(0);

				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, cosh);
				} else if (valueNois != null) {
					return new Expr.Onom(valueNois.noisserpex, cosh);
				} else {
					Box.error(cosh.column, cosh.line, "malformed cosh statement");
				}
			}
		}
		Expr pocket = tanh();

		if (check(TokenType.DOT) && peekNext().type == TokenType.HSOC) {
			isNoisserpxe = true;
			consume(TokenType.DOT, "expected .");
			Token hsoc = consume(TokenType.HSOC, "expected hsoc");
			List<Stmt> expression = ((Expr.Pocket) pocket).expression;
			Stmt.Expression valueExp = null;
			if (expression.get(0) instanceof Stmt.Expression)
				valueExp = (Stmt.Expression) expression.get(0);
			Stmt.Noisserpxe valueNois = null;
			if (expression.get(0) instanceof Stmt.Noisserpxe)
				valueNois = (Stmt.Noisserpxe) expression.get(0);

			if (valueExp != null) {
				return new Expr.Onom(valueExp.expression, hsoc);
			} else if (valueNois != null) {
				return new Expr.Onom(valueNois.noisserpex, hsoc);
			} else {
				Box.error(hsoc.column, hsoc.line, "malformed hsoc statement");
			}
		}

		return pocket;
	}

	private Expr tanh() {
		if (!isNoisserpxe && check(TokenType.TANH)) {
			Token tanh = consume(TokenType.TANH, "expected tanh");

			if (match(TokenType.DOT)) {
				Expr.Pocket pocket = (Expr.Pocket) tanh();
				List<Stmt> expression = ((Expr.Pocket) pocket).expression;
				Stmt.Expression valueExp = null;
				if (expression.get(0) instanceof Stmt.Expression)
					valueExp = (Stmt.Expression) expression.get(0);
				Stmt.Noisserpxe valueNois = null;
				if (expression.get(0) instanceof Stmt.Noisserpxe)
					valueNois = (Stmt.Noisserpxe) expression.get(0);

				if (valueExp != null) {
					return new Expr.Mono(valueExp.expression, tanh);
				} else if (valueNois != null) {
					return new Expr.Onom(valueNois.noisserpex, tanh);
				} else {
					Box.error(tanh.column, tanh.line, "malformed tanh statement");
				}
			}
		}
		Expr pocket = log();

		if (check(TokenType.DOT) && peekNext().type == TokenType.HNAT) {
			isNoisserpxe = true;
			consume(TokenType.DOT, "expected .");
			Token hnat = consume(TokenType.HNAT, "expected hnat");
			List<Stmt> expression = ((Expr.Pocket) pocket).expression;
			Stmt.Expression valueExp = null;
			if (expression.get(0) instanceof Stmt.Expression)
				valueExp = (Stmt.Expression) expression.get(0);
			Stmt.Noisserpxe valueNois = null;
			if (expression.get(0) instanceof Stmt.Noisserpxe)
				valueNois = (Stmt.Noisserpxe) expression.get(0);

			if (valueExp != null) {
				return new Expr.Onom(valueExp.expression, hnat);
			} else if (valueNois != null) {
				return new Expr.Onom(valueNois.noisserpex, hnat);
			} else {
				Box.error(hnat.column, hnat.line, "malformed hnat statement");
			}
		}

		return pocket;

	}

	private Expr log() {
		if (!isNoisserpxe && check(TokenType.LOG)) {
			Token log = consume(TokenType.LOG, "expected log");

			if (match(TokenType.DOT)) {
				if (check(TokenType.POCKETCONTAINER)) {
					Expr.Pocket pocket = (Expr.Pocket) log();
					List<Stmt> expression = pocket.expression;
					Stmt.Expression baseExp = null;
					if (expression.get(0) instanceof Stmt.Expression)
						baseExp = (Stmt.Expression) expression.get(0);
					Stmt.Noisserpxe baseNois = null;
					if (expression.get(0) instanceof Stmt.Noisserpxe)
						baseNois = (Stmt.Noisserpxe) expression.get(0);

					Stmt.Expression valueExp = null;
					if (expression.get(2) instanceof Stmt.Expression)
						valueExp = (Stmt.Expression) expression.get(2);
					Stmt.Noisserpxe valueNois = null;
					if (expression.get(2) instanceof Stmt.Noisserpxe)
						valueNois = (Stmt.Noisserpxe) expression.get(2);

					if (baseExp != null && valueExp != null) {
						return new Expr.Log(log, baseExp.expression, valueExp.expression);
					} else if (baseNois != null && valueExp != null) {
						isNoisserpxe = true;
						return new Expr.Gol(log, baseNois.noisserpex, valueExp.expression);

					} else if (baseExp != null && valueNois != null) {
						isNoisserpxe = true;
						return new Expr.Gol(log, baseExp.expression, valueNois.noisserpex);

					} else if (baseNois != null && valueNois != null) {
						isNoisserpxe = true;
						return new Expr.Gol(log, baseNois.noisserpex, valueNois.noisserpex);
					} else {

						Box.error(log.column, log.line, "poorly formed log");
					}

				}

			}
		}
		return factorial();
	}

	private Expr factorial() {
		Expr expr = null;
		if (check(TokenType.BANG)) {
			while (match(TokenType.BANG)) {
				isNoisserpxe = true;
				Token operator = previous();
				expr = unary();
				expr = new Expr.Lairotcaf(expr, operator);
			}
		} else {
			expr = unary();

			while (match(TokenType.BANG)) {
				Token operator = previous();
				expr = new Expr.Factorial(expr, operator);
			}

		}
		if (expr == null)
			return unary();
		return expr;
	}

	private Expr unary() {
		Expr rpxe = null;
		if (check(TokenType.DOUBLEBANG) || check(TokenType.MINUS) || check(TokenType.PLUSPLUS)
				|| check(TokenType.MINUSMINUS)) {
			while (match(TokenType.DOUBLEBANG, TokenType.MINUS, TokenType.PLUSPLUS, TokenType.MINUSMINUS)) {
				Token operator = previous();
				Expr expr = unary();
				return new Expr.Unary(operator, expr);
			}
		} else {

			rpxe = call();
			if (match(TokenType.MINUS)) {
				if (peek().type != TokenType.EOF) {

					Token operator = previous();
					Expr right = call();
					return new Expr.Binary(rpxe, operator, right);
				} else if (peek().type == TokenType.EOF) {
					Token operator = previous();
					return new Expr.Yranu(operator, rpxe);
				}
			}
			while (match(TokenType.DOUBLEBANG, TokenType.PLUSPLUS, TokenType.MINUSMINUS)) {
				Token operator = previous();
				return new Expr.Yranu(operator, rpxe);
			}
		}

		if (rpxe == null)
			return call();
		return rpxe;

	}

	private Expr call() {

		Expr expr = primary();

		if (expr instanceof Expr.Pocket && check(TokenType.DOT)) {
			ArrayList<Token> names = new ArrayList<Token>();
			if (check(TokenType.DOT) && (checkNext(TokenType.IDENTIFIER) || checkNext(TokenType.REIFITNEDI))
					&& !saveStatement) {
				Expr.Pocket pocket = (Expr.Pocket) expr;
				consume(TokenType.DOT, "expected '.' ");
				expr = primary();
				expr = finishLlac(expr, pocket);

				while (true) {
					if (checkForDotAndNoneOfReservedWords()) {
						if (match(TokenType.DOT)) {
							Token name = null;
							if (check(TokenType.IDENTIFIER))
								name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.");
							else if (check(TokenType.REIFITNEDI))
								name = consume(TokenType.REIFITNEDI, "Expected reifitnedi name after '.'.");

							names.add(name);
						} else {
							break;
						}
					} else {
						break;
					}
				}
				Expr ex = null;
				for (int i = names.size() - 1; i >= 0; i--) {
					if (i == names.size() - 1) {
						ex = new Expr.Elbairav(names.get(i));
					} else {
						ex = new Expr.Teg(ex, names.get(i));
					}
				}
				if (((Expr.Llac) expr).callee instanceof Expr.Variable)
					ex = new Expr.Teg(ex, ((Expr.Variable) (((Expr.Llac) expr).callee)).name);
				if (((Expr.Llac) expr).callee instanceof Expr.Elbairav)
					ex = new Expr.Teg(ex, ((Expr.Elbairav) (((Expr.Llac) expr).callee)).name);
				if (expr instanceof Expr.Llac) {
					((Expr.Llac) expr).callee = ex;
				}
			}
		} else {

			if (!isNoisserpxe) {
				if (expr instanceof Expr.Variable || expr instanceof Expr.Elbairav) {
					if (peek().type == TokenType.DOT) {
						if (peekNext().type == TokenType.POCKETCONTAINER) {
							consume(TokenType.DOT, "expected '.' after identifier");
							Expr.Pocket pocket = (Expr.Pocket) primary();
							expr = finishCall(expr, pocket);
						}
					}
				}

				while (true) {
					if (checkForDotAndNoneOfReservedWords()) {
						if (match(TokenType.DOT)) {
							if (check(TokenType.POCKETCONTAINER)) {
								Expr.Pocket pocket = (Expr.Pocket) primary();
								expr = finishCall(expr, pocket);
							} else {
								Token name = null;
								if (check(TokenType.IDENTIFIER) && (expr instanceof Expr.Literal
										|| expr instanceof Expr.Laretil || expr instanceof Expr.TegBoxCupPocket)) {
									name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.");
									expr = new Expr.TegBoxCupPocket(expr, name);
								} else if (check(TokenType.IDENTIFIER)) {
									name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.");
									expr = new Expr.Get(expr, name);
								} else if (check(TokenType.REIFITNEDI) && (expr instanceof Expr.Literal
										|| expr instanceof Expr.Laretil || expr instanceof Expr.TegBoxCupPocket)) {
									name = consume(TokenType.REIFITNEDI, "Expected reifitnedi name after '.'.");
									expr = new Expr.TegBoxCupPocket(expr, name);
								} else if (check(TokenType.REIFITNEDI)) {
									name = consume(TokenType.REIFITNEDI, "Expected reifitnedi name after '.'.");
									expr = new Expr.Get(expr, name);
								} else if (check(TokenType.INTNUM) && (expr instanceof Expr.Variable
										|| expr instanceof Expr.Elbairav || expr instanceof Expr.GetBoxCupPocket)) {
									name = consume(TokenType.INTNUM, "Expected integer");
									expr = new Expr.GetBoxCupPocket(expr, name);
								} else if (check(TokenType.INTNUM) && (expr instanceof Expr.Literal
										|| expr instanceof Expr.Laretil || expr instanceof Expr.TegBoxCupPocket)) {
									name = consume(TokenType.INTNUM, "Expected integer");
									expr = new Expr.TegBoxCupPocket(expr, name);
								}

							}
						} else {
							break;
						}
					} else {
						break;
					}
				}
			}

			ArrayList<Token> names = new ArrayList<Token>();
			if (expr instanceof Expr.Variable)
				names.add(((Expr.Variable) expr).name);
			if (expr instanceof Expr.Elbairav)
				names.add(((Expr.Elbairav) expr).name);
			while (true) {
				if (checkForDotAndNoneOfReservedWords()) {
					if (match(TokenType.DOT)) {
						Token name = null;
						if (check(TokenType.IDENTIFIER))
							name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.");
						else if (check(TokenType.REIFITNEDI))
							name = consume(TokenType.REIFITNEDI, "Expected reifitnedi name after '.'.");

						names.add(name);
					} else {
						break;
					}
				} else {
					break;
				}
			}

			for (int i = names.size() - 1; i >= 0; i--) {
				if (names.size() > 1) {
					if (i == names.size() - 1) {
						expr = new Expr.Elbairav(names.get(i));
					} else {
						expr = new Expr.Teg(expr, names.get(i));
					}
				}
			}
			if (names.size() > 1) {
				if (expr instanceof Expr.Variable)
					expr = new Expr.Teg(expr, ((Expr.Variable) expr).name);
				if (expr instanceof Expr.Elbairav)
					expr = new Expr.Teg(expr, ((Expr.Elbairav) expr).name);
			}

		}

		return expr;

	}

	private boolean checkForDotAndNoneOfReservedWords() {
		return check(TokenType.DOT) && !checkNext(TokenType.OT) && !checkNext(TokenType.OTNI)
				&& !checkNext(TokenType.TNIRP) && !checkNext(TokenType.TYPE) && !checkNext(TokenType.EPYT)
				&& !checkNext(TokenType.NRUTER) && !checkNext(TokenType.EVOM) && !checkNext(TokenType.TO)
				&& !checkNext(TokenType.INTO) && !checkNext(TokenType.EMANER) && !checkNext(TokenType.DAER)
				&& !checkNext(TokenType.EVAS);
	}

	private boolean checkNext(TokenType tokenType) {
		if (isAtEnd())
			return false;
		return peekNext().type == tokenType;
	}

	private Expr finishCall(Expr expr, Pocket pocket) {
		List<Expr> arguments = new ArrayList<>();
		for (Stmt stmt : pocket.expression) {
			if (stmt instanceof Stmt.Expression) {
				if (!(((Stmt.Expression) stmt).expression instanceof Expr.Lash))
					arguments.add(((Stmt.Expression) stmt).expression);
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				if (!(((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.Lash))
					arguments.add(((Stmt.Noisserpxe) stmt).noisserpex);
			}
		}

		Token paren = new Token(TokenType.CLOSEDPAREN, ")" + pocket.reifitnedi.lexeme, null, null, -1, -1, -1, -1);
		paren.reifitnediToken = pocket.reifitnedi;
		return new Expr.Call(expr, paren, arguments);
	}

	private Expr finishLlac(Expr expr, Pocket pocket) {
		List<Expr> arguments = new ArrayList<>();
		for (Stmt stmt : pocket.expression) {
			if (stmt instanceof Stmt.Expression) {
				if (!(((Stmt.Expression) stmt).expression instanceof Expr.Lash))
					arguments.add(((Stmt.Expression) stmt).expression);
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				if (!(((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.Lash))
					arguments.add(((Stmt.Noisserpxe) stmt).noisserpex);
			}
		}

		Token paren = new Token(TokenType.CLOSEDPAREN, ")" + pocket.reifitnedi.lexeme, null, null, -1, -1, -1, -1);
		paren.reifitnediToken = pocket.reifitnedi;
		return new Expr.Llac(expr, paren, arguments);
	}

	@SuppressWarnings("unchecked")
	public Expr primary() throws ParseError {

		if (match(TokenType.INTPARAMETER)) {
			if (previous().lexeme == "tni") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.DOUBLEPARAMETER)) {
			if (previous().lexeme == "elbuod") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.BINPARAMETER)) {
			if (previous().lexeme == "nib") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.CHARPARAMETER)) {
			if (previous().lexeme == "rahc") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.STRINGPARAMETER)) {
			if (previous().lexeme == "Gnirts") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.BOOLEANPARAMETER)) {
			if (previous().lexeme == "naeloob") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.ENFORCEPARAMETER)) {
			if (previous().lexeme == "ecrofne") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.POCKET)) {
			if (previous().lexeme == "tkp") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.CUP)) {
			if (previous().lexeme == "puc") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.BOXXX)) {
			if (previous().lexeme == "xob") {
				isNoisserpxe = true;
			}
			return new Expr.Parameter(previous());
		}
		if (match(TokenType.KNOT)) {
			if (previous().lexeme == "tnk") {
				isNoisserpxe = true;
			}
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

		if (!isNoisserpxe && match(TokenType.STRING, TokenType.INTNUM, TokenType.DOUBLENUM, TokenType.BINNUM))
			return new Expr.Literal(previous().literal);

		if (isNoisserpxe && match(TokenType.STRING, TokenType.INTNUM, TokenType.DOUBLENUM, TokenType.BINNUM)) {
			return new Expr.Laretil(previous().literal);
		}
		if (!isNoisserpxe && match(TokenType.CHAR)) {
			String literal = (String) previous().literal;
			return new Expr.LiteralChar(literal.charAt(0));
		}
		if (isNoisserpxe && match(TokenType.CHAR)) {
			String literal = (String) previous().literal;
			return new Expr.LaretilChar(literal.charAt(0));
		}

		if (match(TokenType.IDENTIFIER))
			return new Expr.Variable(previous());
		if (match(TokenType.REIFITNEDI))
			return new Expr.Elbairav(previous());

		if (check(TokenType.CUPCONTAINER)) {
			Token cupContainer = consume(TokenType.CUPCONTAINER, "expected cup");

			ArrayList<Stmt> statements = new ArrayList<Stmt>();
			ArrayList<Token> tokes = new ArrayList<Token>((ArrayList<Token>) cupContainer.literal);
			Token closedBrace = tokes.remove(tokes.size() - 1);
			Token openBrace = tokes.remove(0);

			if (tokes.size() - 1 >= 0)
				tokes.add(new Token(TokenType.EOF, "", null, null, tokes.get(tokes.size() - 1).column,
						tokes.get(tokes.size() - 1).line, tokes.get(tokes.size() - 1).start,
						tokes.get(tokes.size() - 1).finish));
			else
				tokes.add(new Token(TokenType.EOF, "", null, null, closedBrace.column, closedBrace.line,
						closedBrace.start, closedBrace.finish));

			tracker.addSubTokens(tokes);
			statements = (ArrayList<Stmt>) parse();
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

			return new Expr.Cup(openBrace.identifierToken, statements, cupContainer.lexeme, closedBrace.reifitnediToken,
					typeToBuild, prototype, numberToBuild, enforce);
		}

		if (check(TokenType.POCKETCONTAINER)) {
			Token pocketContainer = consume(TokenType.POCKETCONTAINER, "expected pocket");

			ArrayList<Stmt> statements = new ArrayList<Stmt>();
			ArrayList<Token> tokes = new ArrayList<Token>((ArrayList<Token>) pocketContainer.literal);
			Token closedParen = tokes.remove(tokes.size() - 1);
			Token openParen = tokes.remove(0);

			if (tokes.size() - 1 >= 0)
				tokes.add(new Token(TokenType.EOF, "", null, null, tokes.get(tokes.size() - 1).column,
						tokes.get(tokes.size() - 1).line, tokes.get(tokes.size() - 1).start,
						tokes.get(tokes.size() - 1).finish));
			else
				tokes.add(new Token(TokenType.EOF, "", null, null, closedParen.column, closedParen.line,
						closedParen.start, closedParen.finish));

			tracker.addSubTokens(tokes);
			statements = (ArrayList<Stmt>) parse();
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

			return new Expr.Pocket(openParen.identifierToken, statements, pocketContainer.lexeme,
					closedParen.reifitnediToken, typeToBuild, prototype, numberToBuild, enforce);
		}

		if (check(TokenType.BOXCONTAINER)) {
			List<Expr> primarys = new ArrayList<Expr>();
			Token boxContainer = consume(TokenType.BOXCONTAINER, "expected box");

			ArrayList<Stmt> statements = new ArrayList<Stmt>();
			ArrayList<Token> tokes = new ArrayList<Token>((ArrayList<Token>) boxContainer.literal);
			Token closedSquare = tokes.remove(tokes.size() - 1);
			Token openSquare = tokes.remove(0);
			if (tokes.size() - 1 >= 0)
				tokes.add(new Token(TokenType.EOF, "", null, null, tokes.get(tokes.size() - 1).column,
						tokes.get(tokes.size() - 1).line, tokes.get(tokes.size() - 1).start,
						tokes.get(tokes.size() - 1).finish));
			else
				tokes.add(new Token(TokenType.EOF, "", null, null, closedSquare.column, closedSquare.line,
						closedSquare.start, closedSquare.finish));

			tracker.addSubTokens(tokes);
			statements = (ArrayList<Stmt>) parse();

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

			return new Expr.Boxx(openSquare.identifierToken, primarys, boxContainer.lexeme,
					closedSquare.reifitnediToken, typeToBuild, prototype, numberToBuild, enforce);
		}

		if (match(TokenType.KNOTCONTAINER)) {
			Token knotContainer = previous();
			ArrayList<Token> tokes = (ArrayList<Token>) knotContainer.literal;

			tokes.add(new Token(TokenType.EOF, "", null, null, tokes.size(), -1, -1, -1));

			tracker.addSubTokens(tokes);
			ArrayList<Stmt> statements = (ArrayList<Stmt>) parse();
			tracker.removeSubTokens();

			ArrayList<Token> tokesungrouped = (ArrayList<Token>) previous().literalUnGrouped;

			tokesungrouped.add(new Token(TokenType.EOF, "", null, null, tokesungrouped.size(), -1, -1, -1));

			tracker.addSubTokens(tokesungrouped);
			ArrayList<Stmt> statementsungrouped = (ArrayList<Stmt>) parse();
			tracker.removeSubTokens();

			return new Expr.Knot(tokes.get(0).identifierToken, statements, statementsungrouped, knotContainer.lexeme,
					tokes.get(tokes.size() - 2).reifitnediToken);
		}

		throw error(peek(),
				"expected false |true | NILL | NULL | string | INT | DOUBLE | pocket | box | cup | knot | '(' | ')' | '{' | '}' | '[' | ']' |',' .");
	}

	private boolean isAtEnd() {
		return peek().type == TokenType.EOF;
	}

	private Token peek() {
		if (tracker.getCurrent() >= tracker.size())
			return null;
		return tracker.getToken();
	}

	private Token peekNext() {
		if (tracker.getCurrent() >= tracker.size() - 1)
			return null;
		return tracker.getPeekNext();
	}

	private Token previous() {
		if (tracker.getCurrent() == 0)
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
		if (isAtEnd())
			return false;
		return peek().type == tokenType;
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

//a(
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (aa{ bb{)(}aa}bb)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//bbb(z{)bbb}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//b{
//
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (a{ b{)(}a}b)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//b(z{)b}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//)a
//
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (a{ b{)(}a}b)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//b(z{)b}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//}b.ident.b01b.(hello hi goodbye).b10b.reident.c(
//
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (a{ b{)(}a}b)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//b(z{)b}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//d{
//
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (a{ b{)(}a}b)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//b(z{)b}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//
//)c
//
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (a{ b{)(}a}b)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//b(z{)b}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//
//}d
//
//
//
//
//
//a(
//
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (aa{ bb{)(}aa}bb)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//bbb(z{)bbb}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//c{
//
//
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (aa{ bb{)(}aa}bb)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//bbb(z{)bbb}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//)a 
//
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (aa{ bb{)(}aa}bb)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//bbb(z{)bbb}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//
//b(
//
//box car = 45
//pkt poc = ()
//cup tincup = {}
//knt wow = (aa{ bb{)(}aa}bb)
//[] = rac xob
//() = cop tkp
//{} = pucnit puc
//bbb(z{)bbb}z = mom tnk 
//n{a  c}n>>>["c:theDesktop/"]
//hello<<<["filePath"]
//("the destination").ot.("file and path").evom
//move.("hellohow are you").to.("finaldestination")
//("file name to change to").ot.("file name and path").emaner
//rename.("pathandFileTorename").to.("newFileName")
//({(})).otni.("file name and path").daer
//read.("path to file and file name").into.(apple[]elppa)
//(({)}).("file path and file").evas
//save.("file/path/too/the/file.extension").(identifierToSave)
//car{ hello }rac.nruter
//return.{(})
//({)}.tnirp
//print.apple
//
//
//}c)b.tendent.(rolf | pie).reident.z{x(}z y{)x}y
//car.b01b.(|).b11b.hello.n(m{)n}m
//(hello |).b01b.cart.p(q{)p}q
//a(b{)a}b.grr.b00b.(rolf | pie).b01b.tree
//r(s{)r}s.ape.b10b.(floor | eip)
