package Box.Grouper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import Box.Token.Token;
import Box.Token.TokenType;

public class Grouper {
	ArrayList<Token> tokens = new ArrayList<Token>();

	int cupOpenPointer = 0;
	int pocketOpenPointer = 0;
	int boxOpenPointer = 0;
	int cupClosedPointer = 0;
	int cupClosedPointer2 = 0;

	int pocketClosedPointer = 0;
	int pocketClosedPointer2 = 0;

	int boxClosedPointer = 0;
	int boxClosedPointer2 = 0;

	ArrayList<BigInteger> identifiers = new ArrayList<BigInteger>();
	ArrayList<BigInteger> identifiers2 = new ArrayList<BigInteger>();

	private BigInteger resultCont;

	public Grouper(ArrayList<Token> tokens) {
		this.tokens = tokens;

		Random randCont = new Random();
		BigInteger upperLimitCont = new BigInteger("999999999");
		resultCont = new BigInteger(upperLimitCont.bitLength(), randCont);

		int count = countUpOpenBoxes(tokens) + countUpClosedBoxes(tokens) + countUpOpenCups(tokens)
				+ countUpClosedCups(tokens) + countUpOpenPockets(tokens) + countUpClosedPockets(tokens);
		count = count * 10;
		if (anyUndamedOpenOrClosed())

			for (int i = 0; i < count; i++) {

				Random rand = new Random();
				BigInteger upperLimit = new BigInteger("999999999");
				BigInteger result = new BigInteger(upperLimit.bitLength(), rand);
				Random rand2 = new Random();
				BigInteger upperLimit2 = new BigInteger("999999999");
				BigInteger result2 = new BigInteger(upperLimit2.bitLength(), rand2);
				while (identifiers.contains(result) && !identifiers.contains(result2) && identifiers2.contains(result2)
						&& !identifiers2.contains(result)) {
					rand = new Random();
					result = new BigInteger(upperLimit.bitLength(), rand);
					rand2 = new Random();
					result2 = new BigInteger(upperLimit2.bitLength(), rand2);
				}
				this.identifiers.add(result);
				this.identifiers2.add(result2);
			}
		cupOpenPointer = this.identifiers.size() - 1;
		pocketOpenPointer = this.identifiers.size() - 1;
		boxOpenPointer = this.identifiers.size() - 1;
		cupClosedPointer = this.identifiers.size() - 1;
		pocketClosedPointer = this.identifiers.size() - 1;
		boxClosedPointer = this.identifiers.size() - 1;
		cupClosedPointer2 = this.identifiers2.size() - 1;
		pocketClosedPointer2 = this.identifiers2.size() - 1;
		boxClosedPointer2 = this.identifiers2.size() - 1;

	}

	public ArrayList<Token> scanTokensSecondPass() {

		matchIdentifiersToOpenClosedParenBraceSquare(tokens);

		renameUnnamedTempLids(tokens);

		splitNamedTempLids(tokens);

		removeSpaces(tokens);

		findgroupingBox(tokens);

		findKnotsSecondAttempt(0, tokens.size() - 1, tokens);

		fixSuperclasses(tokens);

		if (tokens.size() > 0) {
			tokens.add(new Token(TokenType.EOF, "", null, null, null, tokens.get(tokens.size() - 1).column + 1,
					tokens.get(tokens.size() - 1).line, tokens.get(tokens.size() - 1).start + 1,
					tokens.get(tokens.size() - 1).finish + 1));
		}
		return tokens;
	}

	private void findCompositContainers(int start, int finish, ArrayList<Token> tokes) {

		int i = start;
		int j = i + 1;
		boolean isOpen = false;
		boolean isClosed = false;
		while (i >= start && i <= finish) {
			int k = i;

			for (; k <= tokes.size() - 1; k++) {

				if (isOpenIncludesTempLid(tokes.get(k))) {
					isOpen = true;

				}

				if (isClosedIncludesTemplid(tokes.get(k))) {
					isClosed = true;
					break;

				}
				if (k == finish)
					break;

			}
			j = k;
			if (j <= tokes.size() - 1) {

				if (tokes.get(i).identifierToken == null && tokes.get(j).reifitnediToken == null) {
					StringBuilder input1 = new StringBuilder();
					String input = resultCont + "_Container";
					Token identifier = new Token(TokenType.IDENTIFIER, input, null, null, null, 0, 0, 0, 0);
					tokes.get(i).identifierToken = identifier;
					tokes.get(i).lexeme = input + tokes.get(i).lexeme;

					input1.append(input);
					input1.reverse();

					Token reifitnedi = new Token(TokenType.IDENTIFIER, input1.toString(), null, null, null, 0, 0, 0, 0);
					tokes.get(j).reifitnediToken = reifitnedi;
					tokes.get(j).lexeme = tokes.get(j).lexeme + input1.toString();

				} else if (tokes.get(i).identifierToken != null && tokes.get(j).reifitnediToken == null) {
					StringBuilder input1 = new StringBuilder();
					String input = tokes.get(i).identifierToken.lexeme;

					input1.append(input);
					input1.reverse();

					Token reifitnedi = new Token(TokenType.IDENTIFIER, input1.toString(), null, null, null, 0, 0, 0, 0);
					tokes.get(j).reifitnediToken = reifitnedi;
					tokes.get(j).lexeme = tokes.get(j).lexeme + input1.toString();

				} else if (tokes.get(i).identifierToken == null && tokes.get(j).reifitnediToken != null) {
					StringBuilder input1 = new StringBuilder();
					String input = tokes.get(j).reifitnediToken.lexeme;
					input1.append(input);
					input1.reverse();
					Token identifier = new Token(TokenType.IDENTIFIER, input1.toString(), null, null, null, 0, 0, 0, 0);
					tokes.get(i).identifierToken = identifier;
					tokes.get(i).lexeme = input1.toString() + tokes.get(i).lexeme;

				}
				i = j + 1;
			} else {
				i++;
			}
		}
	}

	private void fixSuperclasses(ArrayList<Token> tokens) {

		for (Token token : tokens) {
			if (token.type == TokenType.OPENBRACE) {
				if (token.identifierToken != null) {
					String lexeme = token.identifierToken.lexeme;
					String[] split = lexeme.split("_");
					String superClassName = "";
					String className = "";
					if (split.length == 2) {
						superClassName = split[1];
						className = split[0];
					}
					if (!superClassName.isEmpty()) {
						Token superclassToken = new Token(TokenType.IDENTIFIER, superClassName, null, null, null,
								token.column, token.line, token.start, token.finish);
						token.identifierToken.identifierToken = superclassToken;
						token.identifierToken.lexeme = className;
						token.lexeme = token.identifierToken.lexeme + "{";
					}
				}
			} else if (token.type == TokenType.OPENPAREN) {
				if (token.identifierToken != null) {
					String lexeme = token.identifierToken.lexeme;
					String[] split = lexeme.split("_");
					String superClassName = "";
					String className = "";
					if (split.length == 2) {
						superClassName = split[1];
						className = split[0];
					}
					if (!superClassName.isEmpty()) {
						Token superclassToken = new Token(TokenType.IDENTIFIER, superClassName, null, null, null,
								token.column, token.line, token.start, token.finish);
						token.identifierToken.identifierToken = superclassToken;
						token.identifierToken.lexeme = className;
						token.lexeme = token.identifierToken.lexeme + "(";
					}
				}
			} else if (token.type == TokenType.OPENSQUARE) {
				if (token.identifierToken != null) {
					String lexeme = token.identifierToken.lexeme;
					String[] split = lexeme.split("_");
					String superClassName = "";
					String className = "";
					if (split.length == 2) {
						superClassName = split[1];
						className = split[0];
					}
					if (!superClassName.isEmpty()) {
						Token superclassToken = new Token(TokenType.IDENTIFIER, superClassName, null, null, null,
								token.column, token.line, token.start, token.finish);
						token.identifierToken.identifierToken = superclassToken;
						token.identifierToken.lexeme = className;
						token.lexeme = token.identifierToken.lexeme + "[";
					}
				}
			} else if (token.type == TokenType.CLOSEDBRACE) {
				if (token.reifitnediToken != null) {
					String lexeme = token.reifitnediToken.lexeme;
					String[] split = lexeme.split("_");
					String superClassName = "";
					String className = "";
					if (split.length == 2) {
						superClassName = split[0];
						className = split[1];
					}
					if (!superClassName.isEmpty()) {
						Token superclassToken = new Token(TokenType.IDENTIFIER, superClassName, null, null, null,
								token.column, token.line, token.start, token.finish);
						token.reifitnediToken.reifitnediToken = superclassToken;
						token.reifitnediToken.lexeme = className;
						token.lexeme = "}" + token.reifitnediToken.lexeme;
					}
				}
			} else if (token.type == TokenType.CLOSEDPAREN) {
				if (token.reifitnediToken != null) {
					String lexeme = token.reifitnediToken.lexeme;
					String[] split = lexeme.split("_");
					String superClassName = "";
					String className = "";
					if (split.length == 2) {
						superClassName = split[0];
						className = split[1];
					}
					if (!superClassName.isEmpty()) {
						Token superclassToken = new Token(TokenType.IDENTIFIER, superClassName, null, null, null,
								token.column, token.line, token.start, token.finish);
						token.reifitnediToken.reifitnediToken = superclassToken;
						token.reifitnediToken.lexeme = className;
						token.lexeme = ")" + token.reifitnediToken.lexeme;
					}
				}
			} else if (token.type == TokenType.CLOSEDSQUARE) {
				if (token.reifitnediToken != null) {
					String lexeme = token.reifitnediToken.lexeme;
					String[] split = lexeme.split("_");
					String superClassName = "";
					String className = "";
					if (split.length == 2) {
						superClassName = split[0];
						className = split[1];
					}
					if (!superClassName.isEmpty()) {
						Token superclassToken = new Token(TokenType.IDENTIFIER, superClassName, null, null, null,
								token.column, token.line, token.start, token.finish);
						token.reifitnediToken.reifitnediToken = superclassToken;
						token.reifitnediToken.lexeme = className;
						token.lexeme = "]" + token.reifitnediToken.lexeme;
					}
				}
			} else if (token.type == TokenType.KNOTCONTAINER) {

				ArrayList<Token> knotTokens = (ArrayList<Token>) token.literal;

				fixSuperclasses(knotTokens);
				ArrayList<Token> knotTokensungrouped = (ArrayList<Token>) token.literalUnGrouped;
				fixSuperclasses(knotTokensungrouped);

				String lexemeFixed = "";
				for (Token fixedToken : knotTokensungrouped) {
					lexemeFixed += fixedToken.lexeme + " ";
				}

			} else if (token.type == TokenType.CUPCONTAINER) {

				ArrayList<Token> cupTokens = (ArrayList<Token>) token.literal;

				fixSuperclasses(cupTokens);
				String lexemeFixed = "";
				for (Token fixedToken : cupTokens) {
					lexemeFixed += fixedToken.lexeme + " ";
				}
				token.lexeme = lexemeFixed;

			} else if (token.type == TokenType.POCKETCONTAINER) {

				ArrayList<Token> pocketTokens = (ArrayList<Token>) token.literal;

				fixSuperclasses(pocketTokens);
				String lexemeFixed = "";
				for (Token fixedToken : pocketTokens) {
					lexemeFixed += fixedToken.lexeme + " ";
				}
				token.lexeme = lexemeFixed;

			} else if (token.type == TokenType.BOXCONTAINER) {

				ArrayList<Token> boxTokens = (ArrayList<Token>) token.literal;

				fixSuperclasses(boxTokens);
				String lexemeFixed = "";
				for (Token fixedToken : boxTokens) {
					lexemeFixed += fixedToken.lexeme + " ";
				}
				token.lexeme = lexemeFixed;

			}
		}
	}

	private void removeSpaces(ArrayList<Token> tokens) {

		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).type == TokenType.SPACE) {
				tokens.remove(i);
				i--;
			} else if (tokens.get(i).type == TokenType.SPACERETURN) {
				tokens.remove(i);
				i--;
			} else if (tokens.get(i).type == TokenType.TAB) {
				tokens.remove(i);
				i--;
			} else if (tokens.get(i).type == TokenType.NEWLINE) {
				tokens.remove(i);
				i--;
			}

		}

	}

	private void splitNamedTempLids(ArrayList<Token> tokens) {
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).type == TokenType.TEMPLID) {
				if (tokens.get(i).identifierToken != null && tokens.get(i).reifitnediToken != null) {

					int q = i;
					for (q = i - 1; q >= 0; q--) {
						if (isOpenExcludeTempLid(tokens.get(q)) && matchIdentifiersandReifitnedi(
								tokens.get(q).identifierToken, tokens.get(i).reifitnediToken)) {
							break;
						}
					}
					int e = i;
					for (e = i + 1; e < tokens.size(); e++) {
						if (isClosed(tokens.get(e)) && matchIdentifiersandReifitnedi(tokens.get(i).identifierToken,
								tokens.get(e).reifitnediToken)) {
							break;
						}
					}

					Token reToken = new Token(TokenType.TEMPLID, "|" + tokens.get(i).reifitnediToken.lexeme, null, null,
							null, tokens.get(i).column, tokens.get(i).line, tokens.get(i).start + 2,
							tokens.get(i).reifitnediToken.finish);

					if (tokens.get(i).reifitnediToken != null)
						reToken.reifitnediToken = tokens.get(i).reifitnediToken;
					Token identToken = new Token(TokenType.TEMPLID, tokens.get(i).identifierToken.lexeme + "|", null,
							null, null, tokens.get(i).column, tokens.get(i).line, tokens.get(i).start,
							tokens.get(i).finish - 1);
					if (tokens.get(i).identifierToken != null)
						identToken.identifierToken = tokens.get(i).identifierToken;

					if (q >= 0 || e < tokens.size()) {
						tokens.remove(i);

						if (tokens.get(q).type == TokenType.OPENSQUARE
								|| tokens.get(e - 1).type == TokenType.CLOSEDSQUARE) {
							if (e - 1 < tokens.size())
								tokens.add(i, identToken);
							if (q >= 0)
								tokens.add(i, reToken);

						} else {

							if (q >= 0)
								tokens.add(i, reToken);
							if (e - 1 < tokens.size())
								tokens.add(i, identToken);
						}
					}
				}
			}

		}

	}

	private void renameUnnamedTempLids(ArrayList<Token> tokens) {
		int i = 0;
		while (true) {
			boolean brake = false;
			while (i < tokens.size()) {
				for (int k = i; k < tokens.size(); k++) {
					if (tokens.get(k).type == TokenType.TEMPLID) {
						i = k;
						brake = true;
						break;
					}
				}

				if (brake)
					break;
				i++;
			}

			if (i < tokens.size()) {

				if (tokens.get(i).identifierToken == null && tokens.get(i).reifitnediToken == null) {
					int q = i;
					for (q = i - 1; q >= 0; q--) {
						if (isOpenExcludeTempLid(tokens.get(q))) {
							break;
						}
					}
					int e = i;
					for (e = i + 1; e < tokens.size(); e++) {
						if (isClosedExcludeTempLid(tokens.get(e))) {
							break;
						}
					}

					matchUnanmedTempLid(q, e, tokens);

				}

				i++;
			} else
				break;

		}

	}

	private void matchIdentifiersToOpenClosedParenBraceSquare(ArrayList<Token> tokens) {
		int j = 0;
		int p = tokens.size() - 1;
		int OriginalSize = tokens.size();
		while (j < OriginalSize) {
			for (p = 0; p <= tokens.size() - 1; p++) {
				for (int i = p; i <= tokens.size() - 1; i++) {
					if (tokens.get(i).type == TokenType.IDENTIFIER) {
						if (i == 0 && tokens.size() > i + 1 && tokens.size() > 1) {

							if ((tokens.get(i + 1).type == TokenType.OPENBRACE
									|| tokens.get(i + 1).type == TokenType.OPENPAREN
									|| tokens.get(i + 1).type == TokenType.OPENSQUARE
									|| tokens.get(i + 1).type == TokenType.TEMPLID)
									&& tokens.get(i + 1).identifierToken == null) {
								checkIfNextTokenIsTempLid(i, tokens);
								break;
							}
						} else if (i == tokens.size() - 1 && tokens.size() > 1) {
							if ((tokens.get(i - 1).type == TokenType.CLOSEDBRACE
									|| tokens.get(i - 1).type == TokenType.CLOSEDPAREN
									|| tokens.get(i - 1).type == TokenType.CLOSEDSQUARE
									|| tokens.get(i - 1).type == TokenType.TEMPLID)
									&& tokens.get(i - 1).reifitnediToken == null) {
								checkIfPreviousTokenIsTempLid(i, tokens);
								break;
							}
						} else {
							if (tokens.size() > 1
									&& (tokens.get(i - 1).type == TokenType.CLOSEDBRACE
											|| tokens.get(i - 1).type == TokenType.CLOSEDPAREN
											|| tokens.get(i - 1).type == TokenType.CLOSEDSQUARE
											|| tokens.get(i - 1).type == TokenType.TEMPLID)
									&& tokens.get(i - 1).reifitnediToken == null) {
								checkIfPreviousTokenIsTempLid(i, tokens);
								break;
							}
							if (tokens.size() > i + 1 && tokens.size() > 1) {
								if ((tokens.get(i + 1).type == TokenType.OPENBRACE
										|| tokens.get(i + 1).type == TokenType.OPENPAREN
										|| tokens.get(i + 1).type == TokenType.OPENSQUARE
										|| tokens.get(i + 1).type == TokenType.TEMPLID)
										&& tokens.get(i + 1).identifierToken == null) {
									checkIfNextTokenIsTempLid(i, tokens);
									break;
								}
							}

						}
					}

				}
				break;
			}
			j++;
		}

	}

	private void checkIfPreviousTokenIsTempLid(int i, ArrayList<Token> tokens) {

		tokens.get(i - 1).reifitnediToken = tokens.get(i);
		tokens.get(i - 1).lexeme = tokens.get(i - 1).lexeme + tokens.get(i).lexeme;
		tokens.get(i - 1).finish = tokens.get(i).finish;
		tokens.remove(i);
	}

	private void checkIfNextTokenIsTempLid(int i, ArrayList<Token> tokens) {

		tokens.get(i + 1).identifierToken = tokens.get(i);
		tokens.get(i + 1).lexeme = tokens.get(i).lexeme + tokens.get(i + 1).lexeme;
		tokens.get(i + 1).start = tokens.get(i).start;
		tokens.remove(i);
	}

	private int countUpClosedBoxes(ArrayList<Token> tokens2) {
		int count = 0;
		for (Token token : tokens2) {
			if (token.type == TokenType.CLOSEDSQUARE || token.type == TokenType.TEMPLID)
				count++;
		}
		return count;
	}

	private int countUpClosedPockets(ArrayList<Token> tokens2) {
		int count = 0;
		for (Token token : tokens2) {
			if (token.type == TokenType.CLOSEDPAREN || token.type == TokenType.TEMPLID)
				count++;
		}
		return count;
	}

	private int countUpClosedCups(ArrayList<Token> tokens2) {
		int count = 0;
		for (Token token : tokens2) {
			if (token.type == TokenType.CLOSEDBRACE || token.type == TokenType.TEMPLID)
				count++;
		}
		return count;
	}

	private void matchUnanmedTempLid(int start, int finish, ArrayList<Token> tokens) {

		nameUnnamedTempLids(start, finish, tokens);

	}

	private void matchOpenAndClosedCupsAndPocketsAfterFirst(int start, int finish, ArrayList<Token> tokens) {

		nameOpenAndClosedNotPartOfAKnotAfterFirst(start, finish, tokens);

	}

	private void nameUnnamedTempLids(int start, int finish, ArrayList<Token> tokens) {
		int j;
		for (j = start; j <= finish; j++) {

			for (int i = j; i <= finish; i++) {

				if (tokens.get(i).type == TokenType.TEMPLID && tokens.get(i).identifierToken == null
						&& tokens.get(i).reifitnediToken == null) {
					int k = i + 1;
					while (!isClosed(tokens.get(k)) && !isOpen(tokens.get(k)) && k < tokens.size()) {
						k++;
					}

					String finalResultident = "";
					if (k < tokens.size()) {
						if (isClosedExcludeTempLid(tokens.get(k))) {
							if (findClosedAndName(k, tokens)) {
								String s = tokens.get(k).reifitnediToken.lexeme;
								StringBuilder sb = new StringBuilder(s);

								finalResultident = sb.reverse().toString();

							} else {
								String s = tokens.get(k).reifitnediToken.lexeme;
								StringBuilder sb = new StringBuilder(s);

								finalResultident = sb.reverse().toString();

							}
						}
					}

					int p = i - 1;
					if (p >= 0) {
						while (p >= 0 && !isOpen(tokens.get(p)) && !isClosed(tokens.get(p))) {
							p--;
						}
					}

					String finalResultre = "";
					if (p >= 0) {
						if (isOpenExcludeTempLid(tokens.get(p))) {
							if (findOpenAndName(p, tokens)) {
								String s = tokens.get(p).identifierToken.lexeme;
								StringBuilder sb = new StringBuilder(s);

								finalResultre = sb.reverse().toString();

							} else {
								String s = tokens.get(p).identifierToken.lexeme;
								StringBuilder sb = new StringBuilder(s);

								finalResultre = sb.reverse().toString();
							}
						}
					}
					Token identTok = null;
					if (!finalResultident.isEmpty()) {
						identTok = new Token(TokenType.IDENTIFIER, finalResultident, null, null, null,
								tokens.get(i).column, tokens.get(i).line, tokens.get(i).start, tokens.get(i).finish);
					}
					Token reTok = null;
					if (!finalResultre.isEmpty()) {
						reTok = new Token(TokenType.IDENTIFIER, finalResultre, null, null, null, tokens.get(i).column,
								tokens.get(i).line, tokens.get(i).start, tokens.get(i).finish);
					}
					Token identTempLid = new Token(TokenType.TEMPLID,
							finalResultident + tokens.get(i).lexeme + finalResultre, null, null, null,
							tokens.get(i).column, tokens.get(i).line, tokens.get(i).start - 1, tokens.get(i).finish);
					identTempLid.identifierToken = identTok;
					identTempLid.reifitnediToken = reTok;

					tokens.remove(i);
					tokens.add(i, identTempLid);
					break;

				}

			}

		}
	}

	private void nameOpenAndClosedNotPartOfAKnotAfterFirst(int start, int finish, ArrayList<Token> tokens) {
		int j;
		for (j = start; j <= finish; j++) {
			for (int i = j; i <= finish; i++) {

				if (tokens.get(i).type == TokenType.OPENBRACE && tokens.get(i).identifierToken == null) {
					BigInteger result = identifiers.get(cupOpenPointer);
					cupOpenPointer--;

					Token token = new Token(TokenType.IDENTIFIER, "cup" + result + "cup", null, null, null, -1, -1, -1,
							-1);
					tokens.get(i).identifierToken = token;
					tokens.get(i).lexeme = token.lexeme + tokens.get(i).lexeme;
					break;
				} else if (tokens.get(i).type == TokenType.OPENPAREN && tokens.get(i).identifierToken == null) {
					BigInteger result = identifiers.get(pocketOpenPointer);
					pocketOpenPointer--;

					Token token = new Token(TokenType.IDENTIFIER, "pocket" + result + "pocket", null, null, null, -1,
							-1, -1, -1);
					tokens.get(i).identifierToken = token;
					tokens.get(i).lexeme = token.lexeme + tokens.get(i).lexeme;
					break;

				} else if (tokens.get(i).type == TokenType.OPENSQUARE && tokens.get(i).identifierToken == null) {
					BigInteger result = identifiers.get(boxOpenPointer);
					boxOpenPointer--;

					Token token = new Token(TokenType.IDENTIFIER, "box" + result + "box", null, null, null, -1, -1, -1,
							-1);
					tokens.get(i).identifierToken = token;
					tokens.get(i).lexeme = token.lexeme + tokens.get(i).lexeme;
					break;

				}

			}

			for (int i = j; i <= finish; i++) {

				if (tokens.get(i).type == TokenType.CLOSEDBRACE && tokens.get(i).reifitnediToken == null) {
					BigInteger result = identifiers.get(cupClosedPointer);
					cupClosedPointer--;

					String s = result.toString();
					StringBuilder sb = new StringBuilder(s);

					String finalResult = sb.reverse().toString();
					Token token = new Token(TokenType.IDENTIFIER, "puc" + finalResult + "puc", null, null, null, -1, -1,
							-1, -1);
					tokens.get(i).reifitnediToken = token;
					tokens.get(i).lexeme = tokens.get(i).lexeme + token.lexeme;
					;
					break;
				} else if (tokens.get(i).type == TokenType.CLOSEDPAREN && tokens.get(i).reifitnediToken == null) {
					BigInteger result = identifiers.get(pocketClosedPointer);
					pocketClosedPointer--;

					String s = result.toString();
					StringBuilder sb = new StringBuilder(s);

					String finalResult = sb.reverse().toString();
					Token token = new Token(TokenType.IDENTIFIER, "tekcop" + finalResult + "tekcop", null, null, null,
							-1, -1, -1, -1);
					tokens.get(i).reifitnediToken = token;
					tokens.get(i).lexeme = tokens.get(i).lexeme + token.lexeme;
					break;

				} else if (tokens.get(i).type == TokenType.CLOSEDSQUARE && tokens.get(i).reifitnediToken == null) {
					BigInteger result = identifiers.get(boxClosedPointer);
					boxClosedPointer--;

					String s = result.toString();
					StringBuilder sb = new StringBuilder(s);

					String finalResult = sb.reverse().toString();
					Token token = new Token(TokenType.IDENTIFIER, "xob" + finalResult + "xob", null, null, null, -1, -1,
							-1, -1);
					tokens.get(i).reifitnediToken = token;
					tokens.get(i).lexeme = tokens.get(i).lexeme + token.lexeme;
					break;

				}

			}

		}
	}

	private boolean findOpenAndName(int i, ArrayList<Token> tokens) {
		if (i < tokens.size()) {
			if (tokens.get(i).type == TokenType.OPENBRACE && tokens.get(i).identifierToken == null) {
				BigInteger result = identifiers.get(cupOpenPointer);
				cupOpenPointer--;
				cupClosedPointer--;
				String s = result.toString();

				Token token = new Token(TokenType.IDENTIFIER, "cup" + s + "cup", null, null, null, -1, -1, -1, -1);
				tokens.get(i).identifierToken = token;
				tokens.get(i).lexeme = token.lexeme + tokens.get(i).lexeme;
				return true;
			} else if (tokens.get(i).type == TokenType.OPENPAREN && tokens.get(i).identifierToken == null) {
				BigInteger result = identifiers.get(pocketOpenPointer);
				pocketOpenPointer--;
				pocketClosedPointer--;
				String s = result.toString();

				Token token = new Token(TokenType.IDENTIFIER, "pocket" + s + "pocket", null, null, null, -1, -1, -1,
						-1);
				tokens.get(i).identifierToken = token;
				tokens.get(i).lexeme = token.lexeme + tokens.get(i).lexeme;
				return true;

			} else if (tokens.get(i).type == TokenType.OPENSQUARE && tokens.get(i).identifierToken == null) {
				BigInteger result = identifiers.get(boxOpenPointer);
				boxOpenPointer--;
				boxClosedPointer--;
				String s = result.toString();

				Token token = new Token(TokenType.IDENTIFIER, "box" + s + "box", null, null, null, -1, -1, -1, -1);
				tokens.get(i).identifierToken = token;
				tokens.get(i).lexeme = token.lexeme + tokens.get(i).lexeme;
				return true;

			}
		}
		return false;
	}

	private boolean findClosedAndName(int i, ArrayList<Token> tokens) {
		if (i < tokens.size()) {
			if (tokens.get(i).type == TokenType.CLOSEDBRACE && tokens.get(i).reifitnediToken == null) {
				BigInteger result = identifiers2.get(cupClosedPointer2);
				cupClosedPointer2--;
				cupClosedPointer--;
				cupOpenPointer--;
				String s = result.toString();
				StringBuilder sb = new StringBuilder(s);

				String finalResult = sb.reverse().toString();
				Token token = new Token(TokenType.IDENTIFIER, "puc" + finalResult + "puc", null, null, null, -1, -1, -1,
						-1);
				tokens.get(i).reifitnediToken = token;
				tokens.get(i).lexeme = tokens.get(i).lexeme + token.lexeme;
				return true;
			} else if (tokens.get(i).type == TokenType.CLOSEDPAREN && tokens.get(i).reifitnediToken == null) {
				BigInteger result = identifiers2.get(pocketClosedPointer2);
				pocketClosedPointer2--;
				pocketClosedPointer--;
				pocketOpenPointer--;
				String s = result.toString();
				StringBuilder sb = new StringBuilder(s);

				String finalResult = sb.reverse().toString();
				Token token = new Token(TokenType.IDENTIFIER, "tekcop" + finalResult + "tekcop", null, null, null, -1,
						-1, -1, -1);
				tokens.get(i).reifitnediToken = token;
				tokens.get(i).lexeme = tokens.get(i).lexeme + token.lexeme;
				return true;

			} else if (tokens.get(i).type == TokenType.CLOSEDSQUARE && tokens.get(i).reifitnediToken == null) {
				BigInteger result = identifiers2.get(boxClosedPointer2);
				boxClosedPointer2--;
				boxClosedPointer--;
				boxOpenPointer--;
				String s = result.toString();
				StringBuilder sb = new StringBuilder(s);

				String finalResult = sb.reverse().toString();
				Token token = new Token(TokenType.IDENTIFIER, "xob" + finalResult + "xob", null, null, null, -1, -1, -1,
						-1);
				tokens.get(i).reifitnediToken = token;
				tokens.get(i).lexeme = tokens.get(i).lexeme + token.lexeme;
				return true;

			}
		}
		return false;
	}

	private boolean anyUndamedOpenOrClosed() {
		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			if (isOpen(token)) {
				if (token.identifierToken == null && token.reifitnediToken == null)
					return true;
			}
			if (isClosed(token)) {
				if (token.identifierToken == null && token.reifitnediToken == null)
					return true;
			}

		}
		return false;
	}

	private int countUpOpenBoxes(ArrayList<Token> tokens2) {
		int count = 0;
		for (Token token : tokens2) {
			if (token.type == TokenType.OPENSQUARE || token.type == TokenType.TEMPLID)
				count++;
		}
		return count;
	}

	private int countUpOpenCups(ArrayList<Token> tokens2) {
		int count = 0;
		for (Token token : tokens2) {
			if (token.type == TokenType.OPENBRACE || token.type == TokenType.TEMPLID)
				count++;
		}
		return count;
	}

	private int countUpOpenPockets(ArrayList<Token> tokens2) {
		int count = 0;
		for (Token token : tokens2) {
			if (token.type == TokenType.OPENPAREN || token.type == TokenType.TEMPLID)
				count++;
		}
		return count;
	}

	private void findgroupingBox(ArrayList<Token> tokes) {

		int i = 0;
		int j = i + 1;
		while (true) {
			int k = i;
			boolean first = true;
			ArrayList<Token> pushList = new ArrayList<Token>();
			for (; k <= tokes.size() - 1; k++) {
				if (isOpenSquareIncludesTempLid(tokes.get(k))) {
					if (tokes.get(k).type == TokenType.TEMPLID) {

						for (int m = k; m < tokes.size(); m++) {
							if (matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(k), tokes.get(m))) {
								if (tokes.get(m).type == TokenType.CLOSEDSQUARE) {
									if (first) {
										first = false;
										i = k;
									}
								}
							}
						}

					} else {
						if (first) {
							first = false;
							i = k;
						}
					}
					pushList.add(tokes.get(k));
				}

				if (isClosedSquareIncludesTemplid(tokes.get(k))) {

					boolean wasRemoved = false;
					if (pushList.size() > 0) {
						pushList.remove(pushList.size() - 1);
						wasRemoved = true;
					}

					if (tokes.get(k).type == TokenType.TEMPLID) {
						boolean breakOut = false;
						for (int m = k; m >= 0; m--) {
							if (matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(m), tokes.get(k))) {
								if (tokes.get(m).type == TokenType.OPENSQUARE) {
									if (pushList.size() == 0 && wasRemoved && shouldBreakBox(tokes, i, k) && !first) {
										breakOut = true;
									} else if (pushList.size() == 0 && wasRemoved && !shouldBreakBox(tokes, i, k)
											&& !first) {
										first = true;
									} else if (pushList.size() == 0 && wasRemoved && !shouldBreakBox(tokes, i, k)
											&& first) {
										first = true;
									} else if (pushList.size() == 0 && !wasRemoved && !shouldBreakBox(tokes, i, k)
											&& first) {
										first = true;
									} else if (pushList.size() == 0 && !wasRemoved && !shouldBreakBox(tokes, i, k)
											&& !first) {
										first = true;
									} else if (pushList.size() > 0 && !wasRemoved && !shouldBreakBox(tokes, i, k)
											&& !first) {
										first = true;
										pushList.clear();
									}

								}
							}
						}
						if (breakOut)
							break;

					} else {
						if (pushList.size() == 0 && wasRemoved && shouldBreakBox(tokes, i, k) && !first) {
							break;
						} else if (pushList.size() == 0 && wasRemoved && !shouldBreakBox(tokes, i, k) && !first) {
							first = true;
						} else if (pushList.size() == 0 && wasRemoved && !shouldBreakBox(tokes, i, k) && first) {
							first = true;
						} else if (pushList.size() == 0 && !wasRemoved && !shouldBreakBox(tokes, i, k) && first) {
							first = true;
						} else if (pushList.size() == 0 && !wasRemoved && !shouldBreakBox(tokes, i, k) && !first) {
							first = true;
						} else if (pushList.size() > 0 && !wasRemoved && !shouldBreakBox(tokes, i, k) && !first) {
							first = true;
							pushList.clear();
						}
					}
				}

			}
			j = k;
			if (j < tokes.size() && i < j) {

				if ((tokes.get(i).type == TokenType.OPENSQUARE
						|| (tokes.get(i).type == TokenType.TEMPLID && tokes.get(i).identifierToken != null))
						&& (tokes.get(j).type == TokenType.CLOSEDSQUARE
								|| (tokes.get(j).type == TokenType.TEMPLID && tokes.get(j).reifitnediToken != null))) {
					TokenType container = TokenType.BOXCONTAINER;
					if (tokes.get(i).type == TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
						j = findSubGroupsAndKnotsAndNameThenBuildGroupForBox(i, j, tokes, container);
					} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type == TokenType.TEMPLID) {
						j = findSubGroupsAndKnotsAndNameThenBuildGroupForBox(i, j, tokes, container);
					} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
						j = findSubGroupsAndKnotsAndNameThenBuildGroupForBox(i, j, tokes, container);
					}

				}
				j++;
				i = j;
			} else {
				break;
			}

			if (i >= tokes.size() && j >= tokes.size())
				break;
		}

	}

	private void findgroupingCupPocket(ArrayList<Token> tokes) {

		int i = 0;
		int j = i + 1;
		while (true) {
			int k = i;
			boolean first = true;
			ArrayList<Token> pushList = new ArrayList<Token>();
			for (; k <= tokes.size() - 1; k++) {
				if (isOpenIncludeTempLidExcludeSquare(tokes, k)) {

					if (first) {
						first = false;
						i = k;
					}
					pushList.add(tokes.get(k));

				}

				if (isClosedIncludeTempLidExcludeSquare(tokes, k)) {

					if (pushList.size() > 0) {
						pushList.remove(pushList.size() - 1);

					}

					if (pushList.size() == 0 && shouldBreak(tokes, i, k) && !first) {
						break;
					} else if (pushList.size() == 0 && !shouldBreak(tokes, i, k) && !first) {
						first = true;
					}

				}

			}
			j = k;
			if (j < tokes.size() && i < j) {

				if ((tokes.get(i).type == TokenType.OPENPAREN
						|| (tokes.get(i).type == TokenType.TEMPLID && tokes.get(i).identifierToken != null))
						&& (tokes.get(j).type == TokenType.CLOSEDPAREN
								|| (tokes.get(j).type == TokenType.TEMPLID && tokes.get(j).reifitnediToken != null))) {

					TokenType container = TokenType.POCKETCONTAINER;

					if (tokes.get(i).type == TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
						j = findSubGroupsAndKnotsAndNameThenBuildGroup(i, j, tokes, container);
					} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type == TokenType.TEMPLID) {
						j = findSubGroupsAndKnotsAndNameThenBuildGroup(i, j, tokes, container);
					} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
						j = findSubGroupsAndKnotsAndNameThenBuildGroup(i, j, tokes, container);
					}

				} else if ((tokes.get(i).type == TokenType.OPENBRACE
						|| (tokes.get(i).type == TokenType.TEMPLID && tokes.get(i).identifierToken != null))
						&& (tokes.get(j).type == TokenType.CLOSEDBRACE
								|| (tokes.get(j).type == TokenType.TEMPLID && tokes.get(j).reifitnediToken != null))) {
					TokenType container = TokenType.CUPCONTAINER;
					if (tokes.get(i).type == TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
						j = findSubGroupsAndKnotsAndNameThenBuildGroup(i, j, tokes, container);
					} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type == TokenType.TEMPLID) {
						j = findSubGroupsAndKnotsAndNameThenBuildGroup(i, j, tokes, container);
					} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
						j = findSubGroupsAndKnotsAndNameThenBuildGroup(i, j, tokes, container);

					}

				}
				j++;
				i = j;
			} else {
				break;
			}

			if (i >= tokes.size() && j >= tokes.size())
				break;
		}

	}

	private boolean shouldBreakBox(ArrayList<Token> tokes, int i, int k) {

		if (allMatchedBox(i, k, tokes) && matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))
				&& isSameGroupWithLids(tokes.get(i), tokes.get(k))) {
			return true;
		}
		return false;
	}

	private boolean shouldBreak(ArrayList<Token> tokes, int i, int k) {
		int tLOpenCount = countTempLidOpenBetweenIandK(i, k, tokes);
		int tLClosedCount = countTempLidClosedBetweenIandK(i, k, tokes);
		int bOpenCount = countBraceOpenBetweenIandK(i, k, tokes);
		int bClosedCount = countBraceClosedBetweenIandK(i, k, tokes);
		int pOpenCount = countParenOpenBetweenIandK(i, k, tokes);
		int pClosedCount = countParenClosedBetweenIandK(i, k, tokes);
		if (allMatched(i, k, tokes)) {
			if ((tLOpenCount + tLClosedCount + bOpenCount + bClosedCount + pOpenCount + pClosedCount) % 2 == 0) {

				int bObC = bOpenCount + bClosedCount;
				int pOpC = pOpenCount + pClosedCount;
				int tLOpC = tLOpenCount + pClosedCount;
				int pOtLC = pOpenCount + tLClosedCount;
				int tLObC = tLOpenCount + bClosedCount;
				int bOtLC = bOpenCount + tLClosedCount;
				int tLOtLC = tLOpenCount + tLClosedCount;

				if (bObC % 2 == 0 && pOpC % 2 == 0 && tLOpC % 2 == 0 && pOtLC % 2 == 0 && tLObC % 2 == 0
						&& bOtLC % 2 == 0 && tLOtLC % 2 == 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 == 0 && pOpC % 2 == 0 && tLOpC % 2 != 0 && pOtLC % 2 != 0 && tLObC % 2 != 0
						&& bOtLC % 2 != 0 && tLOtLC % 2 == 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 == 0 && pOpC % 2 == 0 && tLOpC % 2 != 0 && pOtLC % 2 != 0 && tLObC % 2 == 0
						&& bOtLC % 2 == 0 && tLOtLC % 2 == 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 == 0 && pOpC % 2 == 0 && tLOpC % 2 == 0 && pOtLC % 2 == 0 && tLObC % 2 != 0
						&& bOtLC % 2 != 0 && tLOtLC % 2 == 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 == 0 && tLOpC % 2 != 0 && pOtLC % 2 != 0 && tLObC % 2 != 0
						&& bOtLC % 2 == 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 == 0 && tLOpC % 2 != 0 && pOtLC % 2 != 0 && tLObC % 2 != 0
						&& bOtLC % 2 != 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 == 0 && pOpC % 2 != 0 && tLOpC % 2 != 0 && pOtLC % 2 != 0 && tLObC % 2 != 0
						&& bOtLC % 2 == 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 == 0 && pOpC % 2 != 0 && tLOpC % 2 != 0 && pOtLC % 2 != 0 && tLObC % 2 == 0
						&& bOtLC % 2 != 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 == 0 && tLOpC % 2 != 0 && pOtLC % 2 == 0 && tLObC % 2 == 0
						&& bOtLC % 2 == 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 == 0 && tLOpC % 2 == 0 && pOtLC % 2 != 0 && tLObC % 2 == 0
						&& bOtLC % 2 == 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 == 0 && tLOpC % 2 == 0 && pOtLC % 2 != 0 && tLObC % 2 != 0
						&& bOtLC % 2 != 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 == 0 && tLOpC % 2 != 0 && pOtLC % 2 == 0 && tLObC % 2 != 0
						&& bOtLC % 2 != 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 == 0 && pOpC % 2 != 0 && tLOpC % 2 == 0 && pOtLC % 2 == 0 && tLObC % 2 != 0
						&& bOtLC % 2 == 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 == 0 && pOpC % 2 != 0 && tLOpC % 2 == 0 && pOtLC % 2 == 0 && tLObC % 2 == 0
						&& bOtLC % 2 != 0 && tLOtLC % 2 != 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 != 0 && tLOpC % 2 != 0 && pOtLC % 2 == 0 && tLObC % 2 == 0
						&& bOtLC % 2 != 0 && tLOtLC % 2 == 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 != 0 && tLOpC % 2 == 0 && pOtLC % 2 != 0 && tLObC % 2 != 0
						&& bOtLC % 2 == 0 && tLOtLC % 2 == 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 != 0 && tLOpC % 2 != 0 && pOtLC % 2 == 0 && tLObC % 2 != 0
						&& bOtLC % 2 == 0 && tLOtLC % 2 == 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				} else if (bObC % 2 != 0 && pOpC % 2 != 0 && tLOpC % 2 == 0 && pOtLC % 2 != 0 && tLObC % 2 == 0
						&& bOtLC % 2 != 0 && tLOtLC % 2 == 0) {

					if (isSameGroupWithLids(tokes.get(i), tokes.get(k))
							&& matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(i), tokes.get(k))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean allMatchedBox(int i, int k, ArrayList<Token> tokes) {
		ArrayList<Token> pushList = new ArrayList<Token>();
		int j = i + 1;
		boolean hasBeenAddedTo = false;
		for (; j < k; j++) {
			if (tokes.get(j).type == TokenType.OPENSQUARE
					|| (tokes.get(j).type == TokenType.TEMPLID && tokes.get(j).identifierToken != null)) {
				if (tokes.get(j).type == TokenType.TEMPLID) {

					for (int m = j; m < tokes.size(); m++) {
						if (matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(j), tokes.get(m))) {
							if (tokes.get(m).type == TokenType.CLOSEDSQUARE) {
								pushList.add(tokes.get(j));
								hasBeenAddedTo = true;
							}
						}
					}

				} else {
					pushList.add(tokes.get(j));
					hasBeenAddedTo = true;
				}
			}
			if (tokes.get(j).type == TokenType.CLOSEDSQUARE
					|| (tokes.get(j).type == TokenType.TEMPLID && tokes.get(j).reifitnediToken != null)) {
				if (pushList.size() == 0 && hasBeenAddedTo) {
					break;
				} else {
					if (tokes.get(j).type == TokenType.TEMPLID) {

						for (int m = j; m >= 0; m--) {
							if (matchIdentifierTokenandReifitnediTokenLexeme(tokes.get(m), tokes.get(j))) {
								if (tokes.get(m).type == TokenType.OPENSQUARE) {
									pushList.remove(pushList.size() - 1);

								}
							}
						}

					} else {
						pushList.remove(pushList.size() - 1);
					}
				}
			}
		}
		if (pushList.size() == 0 && j == k) {
			return true;
		}

		return false;
	}

	private boolean allMatched(int i, int k, ArrayList<Token> tokes) {
		ArrayList<Token> pushList = new ArrayList<Token>();
		int j = i + 1;
		for (; j < k; j++) {
			if (tokes.get(j).type == TokenType.OPENBRACE || tokes.get(j).type == TokenType.OPENPAREN
					|| (tokes.get(j).type == TokenType.TEMPLID && tokes.get(j).identifierToken != null)) {
				pushList.add(tokes.get(j));
			}
			if (tokes.get(j).type == TokenType.CLOSEDBRACE || tokes.get(j).type == TokenType.CLOSEDPAREN
					|| (tokes.get(j).type == TokenType.TEMPLID && tokes.get(j).reifitnediToken != null)) {
				if (pushList.size() == 0) {
					break;
				} else {
					pushList.remove(pushList.size() - 1);
				}
			}
		}
		if (pushList.size() == 0 && j == k) {
			return true;
		}

		return false;
	}

	private int countTempLidOpenBetweenIandK(int i, int k, ArrayList<Token> tokes) {
		int count = 0;
		for (int j = i + 1; j < k; j++) {
			if (tokes.get(j).type == TokenType.TEMPLID && tokes.get(j).identifierToken != null) {
				count++;
			}
		}
		return count;
	}

	private int countBraceOpenBetweenIandK(int i, int k, ArrayList<Token> tokes) {
		int count = 0;
		for (int j = i + 1; j < k; j++) {
			if (tokes.get(j).type == TokenType.OPENBRACE) {
				count++;
			}
		}
		return count;
	}

	private int countParenOpenBetweenIandK(int i, int k, ArrayList<Token> tokes) {
		int count = 0;
		for (int j = i + 1; j < k; j++) {
			if (tokes.get(j).type == TokenType.OPENPAREN) {
				count++;
			}
		}
		return count;
	}

	private int countTempLidClosedBetweenIandK(int i, int k, ArrayList<Token> tokes) {
		int count = 0;
		for (int j = i + 1; j < k; j++) {
			if (tokes.get(j).type == TokenType.TEMPLID && tokes.get(j).reifitnediToken != null) {
				count++;
			}
		}
		return count;
	}

	private int countBraceClosedBetweenIandK(int i, int k, ArrayList<Token> tokes) {
		int count = 0;
		for (int j = i + 1; j < k; j++) {
			if (tokes.get(j).type == TokenType.CLOSEDBRACE) {
				count++;
			}
		}
		return count;
	}

	private int countParenClosedBetweenIandK(int i, int k, ArrayList<Token> tokes) {
		int count = 0;
		for (int j = i + 1; j < k; j++) {
			if (tokes.get(j).type == TokenType.CLOSEDPAREN) {
				count++;
			}
		}
		return count;
	}

	private boolean isClosedIncludeTempLidExcludeSquare(ArrayList<Token> tokes, int k) {
		return tokes.get(k).type == TokenType.CLOSEDBRACE || tokes.get(k).type == TokenType.CLOSEDPAREN
				|| (tokes.get(k).type == TokenType.TEMPLID && tokes.get(k).reifitnediToken != null);
	}

	private boolean isOpenIncludeTempLidExcludeSquare(ArrayList<Token> tokes, int k) {
		return tokes.get(k).type == TokenType.OPENBRACE || tokes.get(k).type == TokenType.OPENPAREN
				|| (tokes.get(k).type == TokenType.TEMPLID && tokes.get(k).identifierToken != null);
	}

	private int findSubGroupsAndKnotsAndNameThenBuildGroupForBox(int i, int j, ArrayList<Token> tokes,
			TokenType container) {
		int before = tokes.size();
		findBoxSubGroups(i, j, tokes);
		int after = tokes.size();
		j = j - (before - after);

		before = tokes.size();
		findPocketAndCupSubGroups(i, j, tokes);
		after = tokes.size();
		j = j - (before - after);

		before = tokes.size();
		findKnotsAndReplace(tokes, i, j);

		after = tokes.size();
		j = j - (before - after);

		before = tokes.size();
		findKnotsAndReplaceIncludesIandJ(tokes, i, j);
		after = tokes.size();
		j = j - (before - after);

		if (isOpenSquareIncludesTempLid(tokes.get(i)) && isClosedSquareIncludesTemplid(tokes.get(j))) {
			before = tokes.size();
			nameUnamedOpenClosedLidBetweenIAndJ(tokes, i, j);
			after = tokes.size();
			j = j - (before - after);

			recalibratePointers();

			before = tokes.size();
			buildGrouping(i, j, container, tokes);
			after = tokes.size();
			j = j - (before - after);
		}
		return j;
	}

	private void recalibratePointers() {
		if (cupOpenPointer < cupClosedPointer) {
			cupClosedPointer = cupOpenPointer;
		} else {
			cupOpenPointer = cupClosedPointer;

		}
		if (pocketOpenPointer < pocketClosedPointer) {
			pocketClosedPointer = pocketOpenPointer;
		} else {
			pocketOpenPointer = pocketClosedPointer;

		}
		if (boxOpenPointer < boxClosedPointer) {
			boxClosedPointer = boxOpenPointer;
		} else {
			boxOpenPointer = boxClosedPointer;

		}
	}

	private int findSubGroupsAndKnotsAndNameThenBuildGroup(int i, int j, ArrayList<Token> tokes, TokenType container) {
		int before = tokes.size();
		findPocketAndCupSubGroups(i, j, tokes);
		int after = tokes.size();
		j = j - (before - after);

		before = tokes.size();
		findKnotsAndReplace(tokes, i, j);

		after = tokes.size();
		j = j - (before - after);

		before = tokes.size();
		findKnotsAndReplaceIncludesIandJ(tokes, i, j);
		after = tokes.size();
		j = j - (before - after);

		if (isOpenIncludesTempLid(tokes.get(i)) && isClosedIncludesTemplid(tokes.get(j))
				&& !containsUnclosed(i, j, tokes)) {
			before = tokes.size();
			nameUnamedOpenClosedLidBetweenIAndJ(tokes, i, j);
			after = tokes.size();
			j = j - (before - after);
			recalibratePointers();

			before = tokes.size();
			buildGrouping(i, j, container, tokes);
			after = tokes.size();
			j = j - (before - after);
		}
		return j;
	}

	private int findSubGroupsAndKnotsAndNameThenBuildGroupSecondAttempt(int i, int j, ArrayList<Token> tokes,
			TokenType container) {

		int before = tokes.size();
		findKnotsAndReplace(tokes, i, j);

		int after = tokes.size();
		j = j - (before - after);

		before = tokes.size();
		findKnotsAndReplaceIncludesIandJ(tokes, i, j);
		after = tokes.size();
		j = j - (before - after);

		if (isOpenIncludesTempLid(tokes.get(i)) && isClosedIncludesTemplid(tokes.get(j))
				&& !containsUnclosed(i, j, tokes)) {
			before = tokes.size();
			nameUnamedOpenClosedLidBetweenIAndJ(tokes, i, j);
			after = tokes.size();
			j = j - (before - after);
			recalibratePointers();

			before = tokes.size();
			buildGrouping(i, j, container, tokes);
			after = tokes.size();
			j = j - (before - after);
		}
		return j;
	}

	private boolean containsUnclosed(int i, int j, ArrayList<Token> tokes) {
		ArrayList<Token> pushList = new ArrayList<Token>();

		for (int k = i + 1; k < j; k++) {
			if (isOpenIncludesTempLid(tokes.get(k))) {
				pushList.add(tokes.get(k));
			}

			if (isClosedIncludesTemplid(tokes.get(k))) {
				if (pushList.size() > 0) {
					pushList.remove(pushList.size() - 1);
				} else
					return true;

			}
		}

		if (pushList.size() == 0)
			return false;

		return true;
	}

	private boolean isSameGroupWithLids(Token type, Token type2) {

		if (type.type == TokenType.OPENBRACE && type2.type == TokenType.CLOSEDBRACE) {
			return true;
		} else if (type.type == TokenType.OPENBRACE
				&& (type2.type == TokenType.TEMPLID && type2.reifitnediToken != null)) {
			return true;
		} else if ((type.type == TokenType.TEMPLID && type.identifierToken != null)
				&& type2.type == TokenType.CLOSEDBRACE) {
			return true;
		} else if (type.type == TokenType.OPENPAREN && type2.type == TokenType.CLOSEDPAREN) {
			return true;
		} else if ((type.type == TokenType.TEMPLID && type.identifierToken != null)
				&& type2.type == TokenType.CLOSEDPAREN) {
			return true;
		} else if (type.type == TokenType.OPENPAREN
				&& (type2.type == TokenType.TEMPLID && type2.reifitnediToken != null)) {
			return true;
		} else if (type.type == TokenType.OPENSQUARE && type2.type == TokenType.CLOSEDSQUARE) {
			return true;
		} else if ((type.type == TokenType.TEMPLID && type.identifierToken != null)
				&& type2.type == TokenType.CLOSEDSQUARE) {
			return true;
		} else if (type.type == TokenType.OPENSQUARE
				&& (type2.type == TokenType.TEMPLID && type2.reifitnediToken != null)) {
			return true;
		}

		return false;
	}

	private void nameUnamedOpenClosedLidBetweenIAndJ(ArrayList<Token> tokes, int i, int j) {
		ArrayList<Token> subTokes = new ArrayList<Token>();
		for (int j2 = i; j2 <= j; j2++) {
			subTokes.add(tokes.get(j2));
		}

		matchOpenAndClosedCupsAndPocketsAfterFirst(0, subTokes.size() - 1, subTokes);

		for (int j2 = j; j2 >= i; j2--) {
			tokes.remove(j2);
		}

		if (j >= i)
			tokes.addAll(i, subTokes);
	}

	private void findKnotsAndReplace(ArrayList<Token> tokes, int i, int j) {
		ArrayList<Token> toInspectForKnots = new ArrayList<Token>();
		for (int k = i + 1; k <= j - 1; k++) {
			toInspectForKnots.add(tokes.get(k));
		}

		if (knots(0, toInspectForKnots.size() - 1, toInspectForKnots)) {
			findKnotsSecondAttempt(0, toInspectForKnots.size() - 1, toInspectForKnots);
		}
		for (int k = j - 1; k >= i + 1; k--) {
			tokes.remove(k);
		}
		tokes.addAll(i + 1, toInspectForKnots);
	}

	private void findKnotsAndReplaceIncludesIandJ(ArrayList<Token> tokes, int i, int j) {
		ArrayList<Token> toInspectForKnots = new ArrayList<Token>();
		for (int k = i; k <= j; k++) {
			toInspectForKnots.add(tokes.get(k));
		}

		if (knots(0, toInspectForKnots.size() - 1, toInspectForKnots)) {
			nameUnamedOpenClosedLidBetweenIAndJ(toInspectForKnots, 0, toInspectForKnots.size() - 1);
			findKnotsSecondAttempt(0, toInspectForKnots.size() - 1, toInspectForKnots);
		}
		for (int k = j; k >= i; k--) {
			tokes.remove(k);
		}
		tokes.addAll(i, toInspectForKnots);
	}

	private void findBoxSubGroups(int i, int j, ArrayList<Token> tokes) {

		ArrayList<Token> subTokes = new ArrayList<Token>();
		for (int j2 = i + 1; j2 <= j - 1; j2++) {
			subTokes.add(tokes.get(j2));
		}
		if (subTokes.size() > 0)
			findgroupingBox(subTokes);

		for (int j2 = j - 1; j2 >= i + 1; j2--) {
			tokes.remove(j2);
		}

		if (j - 1 >= i + 1)
			tokes.addAll(i + 1, subTokes);

	}

	private void findPocketAndCupSubGroups(int i, int j, ArrayList<Token> tokes) {

		ArrayList<Token> subTokes = new ArrayList<Token>();
		for (int j2 = i + 1; j2 <= j - 1; j2++) {
			subTokes.add(tokes.get(j2));
		}
		if (subTokes.size() > 0)
			findgroupingCupPocket(subTokes);

		for (int j2 = j - 1; j2 >= i + 1; j2--) {
			tokes.remove(j2);
		}

		if (j - 1 >= i + 1)
			tokes.addAll(i + 1, subTokes);

	}

	private void findKnotsSecondAttempt(int start, int finish, ArrayList<Token> tokes) {
		ArrayList<ArrayList<TokenIndexAndCount>> visitedOpen = new ArrayList<ArrayList<TokenIndexAndCount>>();
		ArrayList<ArrayList<TokenIndexAndCount>> pushList = new ArrayList<ArrayList<TokenIndexAndCount>>();
		ArrayList<ArrayList<TokenIndexAndCount>> visitedClosed = new ArrayList<ArrayList<TokenIndexAndCount>>();
		;
		int count = 0;
		int i = start;
		int j = i + 1;

		while (i >= start && i <= finish) {
			int k = i;
			pushList.clear();
			visitedClosed.clear();
			visitedOpen.clear();
			boolean isKnot = false;
			boolean isGroup = false;
			for (; k <= tokes.size() - 1; k++) {

				if (isOpenIncludesTempLid(tokes.get(k))) {
					addToVisitedOpen(tokes, visitedOpen, count, k);
					addToPushList(tokes, pushList, count, k);
					count++;
				}

				if (isClosedIncludesTemplid(tokes.get(k))) {

					addToVisitedClosed(tokes, visitedClosed, count, k);

					for (int w = 0; w < pushList.size(); w++) {
						TokenIndexAndCount remove = removeFromPushList(pushList, w);

						if (pushList.get(w).size() == 0) {
							pushList.remove(pushList.get(w));

							if (checkIfGroup(tokes, k, remove)) {

								i = remove.getIndex();
								isGroup = true;
								break;

							} else {

								boolean isresolved = isThisAResolvedKnot(remove.getIndex(), k, tokes);
								String lexeme = "";
								if (tokes.get(remove.getIndex()).identifierToken != null)
									lexeme = tokes.get(remove.getIndex()).identifierToken.lexeme;
								String lexeme2 = "";
								if (tokes.get(k).reifitnediToken != null)
									lexeme2 = tokes.get(k).reifitnediToken.lexeme;
								if (findAnyOpenInbetweenIandJIncludingSquare(remove.getIndex(), k, tokes)) {
									if (isresolved) {
										i = remove.getIndex();
										isKnot = true;
										break;
									}
								} else {
									break;
								}
							}
						}
					}
					count++;
				}

				if (isKnot || isGroup) {
					break;
				}
				if (k == finish)
					break;

			}
			j = k;
			if (j <= tokes.size() - 1) {

				if (isKnot) {

					ArrayList<Token> knotgrouping = new ArrayList<Token>();
					for (int p = i + 1; p <= j; p++) {
						knotgrouping.add(tokes.get(p));
					}

					int before = knotgrouping.size();
					findKnotsSecondAttempt(0, knotgrouping.size() - 1, knotgrouping);
					int after = knotgrouping.size();

					for (int p = j; p >= i + 1; p--) {
						tokes.remove(p);
					}
					tokes.addAll(i + 1, knotgrouping);
					j = j - (before - after);

					knotgrouping.clear();
					for (int p = i; p <= j; p++) {
						knotgrouping.add(tokes.get(p));
					}
					nameUnamedOpenClosedLidBetweenIAndJ(knotgrouping, 0, knotgrouping.size() - 1);

					for (int p = j; p >= i; p--) {
						tokes.remove(p);
					}

					tokes.addAll(i, knotgrouping);

					ArrayList<Token> knot = new ArrayList<Token>();

					for (int p = i; p <= j; p++) {
						knot.add(tokes.get(p));
					}
					ArrayList<Token> ungroupedKnot = new ArrayList<Token>(knot);
					ArrayList<Token> groupedBackwards = new ArrayList<Token>(knot);
					ArrayList<String> NamesUsed = new ArrayList<String>();
//					findgroupingForward(knot, NamesUsed);
//					findgroupingBackward(groupedBackwards, NamesUsed);
					String lexeme = "";
					for (int p = i; p <= j; p++) {
						lexeme = lexeme + " " + tokes.get(p).lexeme;
					}
					lexeme += " ";
					Token token = new Token(TokenType.KNOTCONTAINER, lexeme, knot, ungroupedKnot, groupedBackwards,
							tokes.get(i).column, tokes.get(i).line, tokes.get(i).start, tokes.get(j).finish);
					for (int p = j; p >= i; p--) {
						tokes.remove(p);
					}

					tokes.add(i, token);
					pushList.clear();
					visitedClosed.clear();
					visitedOpen.clear();
					i = 0;

				} else if (isGroup) {
					if ((tokes.get(i).type == TokenType.OPENPAREN
							|| (tokes.get(i).type == TokenType.TEMPLID && tokes.get(i).identifierToken != null))
							&& (tokes.get(j).type == TokenType.CLOSEDPAREN || (tokes.get(j).type == TokenType.TEMPLID
									&& tokes.get(j).reifitnediToken != null))) {

						TokenType container = TokenType.POCKETCONTAINER;

						if (tokes.get(i).type == TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
							findSubGroupsAndKnotsAndNameThenBuildGroupSecondAttempt(i, j, tokes, container);
						} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type == TokenType.TEMPLID) {
							findSubGroupsAndKnotsAndNameThenBuildGroupSecondAttempt(i, j, tokes, container);
						} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
							findSubGroupsAndKnotsAndNameThenBuildGroupSecondAttempt(i, j, tokes, container);
						}

					} else if ((tokes.get(i).type == TokenType.OPENBRACE
							|| (tokes.get(i).type == TokenType.TEMPLID && tokes.get(i).identifierToken != null))
							&& (tokes.get(j).type == TokenType.CLOSEDBRACE || (tokes.get(j).type == TokenType.TEMPLID
									&& tokes.get(j).reifitnediToken != null))) {
						TokenType container = TokenType.CUPCONTAINER;
						if (tokes.get(i).type == TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
							findSubGroupsAndKnotsAndNameThenBuildGroupSecondAttempt(i, j, tokes, container);
						} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type == TokenType.TEMPLID) {
							findSubGroupsAndKnotsAndNameThenBuildGroupSecondAttempt(i, j, tokes, container);
						} else if (tokes.get(i).type != TokenType.TEMPLID && tokes.get(j).type != TokenType.TEMPLID) {
							findSubGroupsAndKnotsAndNameThenBuildGroupSecondAttempt(i, j, tokes, container);

						}

					}
					pushList.clear();
					visitedClosed.clear();
					visitedOpen.clear();
					i = 0;
				} else
					i++;
			} else {
				i++;
			}
		}

	}

	private boolean checkIfGroup(ArrayList<Token> tokes, int k, TokenIndexAndCount remove) {
		return matchIdentifierTokenandReifitnediTokenLexeme(remove.getToken(), tokes.get(k))
				&& isSameGroupWithLids(remove.getToken(), tokes.get(k))
				&& !findAnyOpenInbetweenIandJIncludingSquare(remove.getIndex(), k, tokes);
	}

	private TokenIndexAndCount removeFromPushList(ArrayList<ArrayList<TokenIndexAndCount>> pushList, int w) {
		TokenIndexAndCount remove = pushList.get(w).remove(pushList.get(w).size() - 1);
		return remove;
	}

	private void addToPushList(ArrayList<Token> tokes, ArrayList<ArrayList<TokenIndexAndCount>> pushList, int count,
			int k) {
		pushList.add(new ArrayList<TokenIndexAndCount>());
		for (ArrayList<TokenIndexAndCount> arrayList : pushList) {
			arrayList.add(new TokenIndexAndCount(tokes.get(k), k, count));
		}
	}

	private void addToVisitedOpen(ArrayList<Token> tokes, ArrayList<ArrayList<TokenIndexAndCount>> visitedOpen,
			int count, int k) {
		addToPushList(tokes, visitedOpen, count, k);
	}

	private void addToVisitedClosed(ArrayList<Token> tokes, ArrayList<ArrayList<TokenIndexAndCount>> visitedClosed,
			int count, int k) {
		addToVisitedOpen(tokes, visitedClosed, count, k);
	}

	private boolean isThisAResolvedKnot(int i, int k, ArrayList<Token> tokes) {
		ArrayList<String> pushList = new ArrayList<String>();
		ArrayList<String> popList = new ArrayList<String>();
		boolean wasPopulated = false;
		for (int j = i; j <= k; j++) {
			if (isOpen(tokes.get(j))) {
				if (tokes.get(j).identifierToken != null)
					pushList.add(tokes.get(j).identifierToken.lexeme);
				else
					pushList.add("");

				wasPopulated = true;
			}
		}
		for (int j = i; j <= k; j++) {
			if (isClosed(tokes.get(j))) {
				if (tokes.get(j).reifitnediToken != null)
					popList.add(tokes.get(j).reifitnediToken.lexeme);
				else
					popList.add("");
				wasPopulated = true;
			}
		}

		for (int j = 0; j < pushList.size(); j++) {

			StringBuilder sb = new StringBuilder(pushList.get(j));
			StringBuilder reversed = sb.reverse();
			if (popList.contains(reversed.toString())) {
				popList.remove(reversed.toString());
				pushList.remove(j);
				j--;
			}

		}

		if (pushList.size() == 0 && popList.size() == 0 && wasPopulated)
			return true;
		return false;
	}

	private boolean knots(int start, int finish, ArrayList<Token> tokes) {
		ArrayList<TokenIndexAndCount> nameOpen = new ArrayList<TokenIndexAndCount>();
		ArrayList<TokenIndexAndCount> nameClosed = new ArrayList<TokenIndexAndCount>();
		ArrayList<Token> pushList = new ArrayList<Token>();
		ArrayList<TokenPair> pushKnotList = new ArrayList<TokenPair>();
		int count = 0;
		int i = start;
		boolean doesitlooklikeaknot = false;
		while (i >= start && i <= finish) {

			if (isClosedIncludeTempLidExcludeSquare(tokes, i)) {
				nameClosed.add(new TokenIndexAndCount(tokes.get(i), i, count));
				Token removed = null;
				if (pushList.size() > 0)
					removed = pushList.remove(pushList.size() - 1);
				if (removed != null) {
					if (!isSameGroupWithLids(removed, tokes.get(i))) {
						pushKnotList.add(new TokenPair(removed, tokes.get(i)));
					}
				}
				count++;
			}
			if (isOpenIncludeTempLidExcludeSquare(tokes, i)) {
				nameOpen.add(new TokenIndexAndCount(tokes.get(i), i, count));
				pushList.add(tokes.get(i));
				count++;
			}
			boolean isresolved = resolveVisitedListsIncreasing(nameOpen, nameClosed);
			if (nameOpen.size() == 0 && nameClosed.size() == 0) {
				isresolved = false;
			}

			if (pushList.size() == 0 && pushKnotList.size() > 0)
				doesitlooklikeaknot = true;

			if (isresolved && doesitlooklikeaknot) {
				return true;
			} else
				i++;
		}
		return false;
	}

	private class TokenPair {

		private Token token1;
		private Token token2;

		public TokenPair(Token token1, Token token2) {
			this.token1 = token1;
			this.token2 = token2;

		}
	}

	private boolean resolveVisitedListsIncreasing(ArrayList<TokenIndexAndCount> visitedOpen,
			ArrayList<TokenIndexAndCount> visitedClosed) {
		HashMap<String, Integer> open = new HashMap<String, Integer>();
		HashMap<String, Integer> closed = new HashMap<String, Integer>();

		int openBrace = 0;
		int openParen = 0;
		int closedBrace = 0;
		int closedParen = 0;
		for (int j2 = 0; j2 < visitedOpen.size(); j2++) {
			if (isOpenExcludeSquare(visitedOpen.get(j2).getToken()))
				if (visitedOpen.get(j2).getToken().identifierToken != null)
					open.put(visitedOpen.get(j2).getToken().identifierToken.lexeme, visitedOpen.get(j2).getCount());
				else if (visitedOpen.get(j2).getToken().type == TokenType.OPENBRACE) {
					open.put("brace" + openBrace, visitedOpen.get(j2).getCount());
					openBrace++;
				} else if (visitedOpen.get(j2).getToken().type == TokenType.OPENPAREN) {
					open.put("paren" + openParen, visitedOpen.get(j2).getCount());
					openParen++;
				}

		}
		for (int j2 = 0; j2 < visitedClosed.size(); j2++) {
			if (isClosedExcludeSquare(visitedClosed.get(j2).getToken()))
				if (visitedClosed.get(j2).getToken().reifitnediToken != null) {
					String lexeme = visitedClosed.get(j2).getToken().reifitnediToken.lexeme;
					StringBuilder sb = new StringBuilder(lexeme);

					closed.put(sb.reverse().toString(), visitedClosed.get(j2).getCount());
				} else {
					if (visitedClosed.get(j2).getToken().type == TokenType.CLOSEDBRACE) {
						closed.put("brace" + closedBrace, visitedClosed.get(j2).getCount());
						closedBrace++;
					} else if (visitedClosed.get(j2).getToken().type == TokenType.CLOSEDPAREN) {
						closed.put("paren" + closedParen, visitedClosed.get(j2).getCount());
						closedParen++;
					}
				}
		}

		HashMap<String, Integer> open2 = new HashMap<String, Integer>(open);
		HashMap<String, Integer> closed2 = new HashMap<String, Integer>(closed);

		for (String token : open2.keySet()) {
			if (closed.keySet().contains(token)) {

				if (open2.get(token) < closed.get(token)) {
					closed.remove(token);
				}

			}
		}
		for (String token : closed2.keySet()) {
			if (open.keySet().contains(token)) {
				if (open.get(token) < closed2.get(token)) {
					open.remove(token);
				}

			}
		}

		if (closed.size() == 0 && open.size() == 0)
			return true;

		return false;
	}

	private boolean isClosed(Token token) {
		if (token.type == TokenType.CLOSEDSQUARE || token.type == TokenType.CLOSEDBRACE
				|| token.type == TokenType.CLOSEDPAREN || (token.type == TokenType.TEMPLID))
			return true;
		return false;
	}

	private boolean isClosedSquareIncludesTemplid(Token token) {
		if (token.type == TokenType.CLOSEDSQUARE || (token.type == TokenType.TEMPLID && token.reifitnediToken != null))
			return true;
		return false;
	}

	private boolean isClosedIncludesTemplid(Token token) {
		if (token.type == TokenType.CLOSEDSQUARE || token.type == TokenType.CLOSEDBRACE
				|| token.type == TokenType.CLOSEDPAREN
				|| (token.type == TokenType.TEMPLID && token.reifitnediToken != null))
			return true;
		return false;
	}

	private boolean isClosedExcludeTempLid(Token token) {
		if (token.type == TokenType.CLOSEDSQUARE || token.type == TokenType.CLOSEDBRACE
				|| token.type == TokenType.CLOSEDPAREN)
			return true;
		return false;
	}

	private boolean isClosedExcludeSquare(Token token) {
		if (token.type == TokenType.CLOSEDBRACE || token.type == TokenType.CLOSEDPAREN
				|| (token.type == TokenType.TEMPLID && token.reifitnediToken != null))
			return true;
		return false;
	}

	private boolean isOpen(Token token) {
		if (token.type == TokenType.OPENSQUARE || token.type == TokenType.OPENBRACE || token.type == TokenType.OPENPAREN
				|| (token.type == TokenType.TEMPLID))
			return true;
		return false;
	}

	private boolean isOpenSquareIncludesTempLid(Token token) {
		if (token.type == TokenType.OPENSQUARE || (token.type == TokenType.TEMPLID && token.identifierToken != null))
			return true;
		return false;
	}

	private boolean isOpenIncludesTempLid(Token token) {
		if (token.type == TokenType.OPENSQUARE || token.type == TokenType.OPENBRACE || token.type == TokenType.OPENPAREN
				|| (token.type == TokenType.TEMPLID && token.identifierToken != null))
			return true;
		return false;
	}

	private boolean isOpenExcludeTempLid(Token token) {
		if (token.type == TokenType.OPENSQUARE || token.type == TokenType.OPENBRACE
				|| token.type == TokenType.OPENPAREN)
			return true;
		return false;
	}

	private boolean isOpenExcludeSquare(Token token) {
		if (token.type == TokenType.OPENBRACE || token.type == TokenType.OPENPAREN
				|| (token.type == TokenType.TEMPLID && token.identifierToken != null))
			return true;
		return false;
	}

	private class TokenIndexAndCount {

		private Token token;
		private int index;
		private int count;

		public TokenIndexAndCount(Token token, int index, int count) {
			this.token = token;
			this.index = index;
			this.count = count;
		}

		public int getIndex() {
			return index;
		}

		public Token getToken() {
			return token;
		}

		public int getCount() {
			return count;
		}

	}

	private void buildGrouping(int i, int j, TokenType grouptype, ArrayList<Token> tokes) {
		ArrayList<Token> group = new ArrayList<Token>();
		for (int p = i; p <= j; p++) {
			group.add(tokes.get(p));
		}

		String lexeme = "";

		for (int q = i; q <= j; q++) {
			lexeme += tokes.get(q).lexeme + " ";
		}
		if (i < tokes.size() && j < tokes.size() && group.size() > 0 && i != j) {
			Token token = new Token(grouptype, lexeme, group, null, null, tokes.get(j).column, tokes.get(j).line,
					tokes.get(i).start, tokes.get(j).finish);
			token.identifierToken = group.get(0).identifierToken;
			token.reifitnediToken = group.get(group.size() - 1).reifitnediToken;
			for (int p = j; p >= i; p--) {
				tokes.remove(p);
			}
			tokes.add(i, token);
		}
	}

	private ArrayList<Token> buildGroupingKnot(int i, int j, TokenType grouptype, ArrayList<Token> tokes) {
		ArrayList<Token> group = new ArrayList<Token>();
		ArrayList<Token> tok = new ArrayList<Token>();
		for (int p = i; p <= j; p++) {
			group.add(tokes.get(p));
		}

		String lexeme = "";

		for (int q = i; q <= j; q++) {
			lexeme += tokes.get(q).lexeme + " ";
		}
		if (i < tokes.size() && j < tokes.size() && group.size() > 0) {
			Token token = new Token(grouptype, lexeme, group, null, null, tokes.get(j).column, tokes.get(j).line,
					tokes.get(i).start, tokes.get(j).finish);
			token.identifierToken = group.get(0).identifierToken;
			token.reifitnediToken = group.get(group.size() - 1).reifitnediToken;

			tok.add(token);
		}
		return tok;
	}

	private boolean findAnyOpenInbetweenIandJIncludingSquare(int start, int finish, ArrayList<Token> tokes) {
		ArrayList<Token> pushList = new ArrayList<Token>();
		ArrayList<Token> popList = new ArrayList<Token>();

		for (int i = start + 1; i < finish; i++) {
			if (tokes.get(i).type == TokenType.OPENBRACE || tokes.get(i).type == TokenType.OPENPAREN
					|| tokes.get(i).type == TokenType.OPENSQUARE) {
				pushList.add(tokens.get(i));
			}
			if (tokes.get(i).type == TokenType.CLOSEDBRACE || tokes.get(i).type == TokenType.CLOSEDPAREN
					|| tokes.get(i).type == TokenType.CLOSEDSQUARE) {
				boolean wasremoved = false;
				for (int j = 0; j < pushList.size(); j++) {
					if (matchGrouping(tokes.get(i).lexeme, pushList.get(j).lexeme)) {
						pushList.remove(j);
						wasremoved = true;
						break;
					}
				}
				if (!wasremoved)
					popList.add(tokes.get(i));
			}

		}

		if (pushList.size() > 0 || popList.size() > 0)
			return true;
		return false;

	}

	private void findgroupingForward(ArrayList<Token> tokes, ArrayList<String> NamesUsed) {
		ArrayList<Token> tok = new ArrayList<Token>();
		findGroups(tokes, tok);
		ArrayList<Token> tok2 = new ArrayList<Token>();
		ArrayList<Token> tok3 = new ArrayList<Token>();

		for (Token token : tok) {
			findConditionalGroupsForward((ArrayList<Token>) token.literal, tok2, NamesUsed);
			tok3.add(token);
			tok3.addAll(tok2);
			tok2.clear();
		}
		tokes.clear();

		tokes.addAll(tok3);

	}

	private void findgroupingBackward(ArrayList<Token> tonk, ArrayList<String> namesUsed) {
		ArrayList<Token> tok = new ArrayList<Token>();
		findGroups(tonk, tok);
		ArrayList<Token> tok2 = new ArrayList<Token>();
		ArrayList<Token> tok3 = new ArrayList<Token>();
		for (int i = tok.size() - 1; i >= 0; i--) {
			findConditionalGroupsBackward((ArrayList<Token>) tok.get(i).literal, tok2, namesUsed);
			tok3.add(tok.get(i));
			tok3.addAll(tok2);
			tok2.clear();

		}

		tonk.clear();
		tonk.addAll(tok3);
	}

	private void findConditionalGroupsBackward(ArrayList<Token> tokes, ArrayList<Token> tok2,
			ArrayList<String> namesUsed) {
		int end = tokes.size() - 2;
		int start = tokes.size() - 2;
		int current = tokes.size() - 2;
		int second = tokes.size() - 2;
		boolean isRunning = false;
		boolean isSecond = false;
		while (current >= 0) {
			if (tokes.get(current).type == TokenType.CLOSEDBRACE && !isRunning) {
				end = current;
				isRunning = true;
			} else if (tokes.get(current).type == TokenType.CLOSEDPAREN && !isRunning) {
				end = current;
				isRunning = true;

			} else if (tokes.get(current).type == TokenType.TEMPLID && !isRunning) {
				end = current;
				isRunning = true;
			} else if (tokes.get(current).type == TokenType.CLOSEDBRACE && isRunning && !isSecond) {
				second = current;
				isSecond = true;
			} else if (tokes.get(current).type == TokenType.CLOSEDPAREN && isRunning && !isSecond) {
				second = current;
				isSecond = true;
			} else if (tokes.get(current).type == TokenType.TEMPLID && isRunning && !isSecond) {
				second = current;
				isSecond = true;
			}

			boolean skip = false;

			if (tokes.get(current).type == TokenType.OPENBRACE && isRunning) {
				start = current;
				if (isSecond) {
					current = second;
					skip = true;
				}
				isSecond = false;
				isRunning = false;
				if (tokes.get(end).type == TokenType.CLOSEDBRACE) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.CUPCONTAINER, tokes, namesUsed));
				} else if (tokes.get(end).type == TokenType.CLOSEDPAREN) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.COCKETCONTAINER, tokes, namesUsed));
				} else if (tokes.get(end).type == TokenType.TEMPLID) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.CIDCONTAINER, tokes, namesUsed));
				}

			} else if (tokes.get(current).type == TokenType.OPENPAREN && isRunning) {
				start = current;
				if (isSecond) {
					current = second;
					skip = true;
				}
				isSecond = false;
				isRunning = false;
				if (tokes.get(end).type == TokenType.CLOSEDBRACE) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.PUPCONTAINER, tokes, namesUsed));
				} else if (tokes.get(end).type == TokenType.CLOSEDPAREN) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.POCKETCONTAINER, tokes, namesUsed));
				} else if (tokes.get(end).type == TokenType.TEMPLID) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.PIDCONTAINER, tokes, namesUsed));
				}
			} else if (tokes.get(current).type == TokenType.TEMPLID && isRunning) {
				start = current;
				if (isSecond) {
					current = second;
					skip = true;
				}
				isSecond = false;
				isRunning = false;
				if (tokes.get(end).type == TokenType.CLOSEDBRACE) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.LUPCONTAINER, tokes, namesUsed));
				} else if (tokes.get(end).type == TokenType.CLOSEDPAREN) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.LOCKETCONTAINER, tokes, namesUsed));
				} else if (tokes.get(end).type == TokenType.TEMPLID) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.LILCONTAINER, tokes, namesUsed));
				}
			}

			if (!skip)
				current--;
		}
	}

	private Collection<? extends Token> buildConditionalGroupingKnot(int i, int j, TokenType grouptype,
			ArrayList<Token> tokes, ArrayList<String> namesUsed) {
		ArrayList<Token> group = new ArrayList<Token>();
		ArrayList<Token> tok = new ArrayList<Token>();
		for (int p = i; p <= j; p++) {
			group.add(tokes.get(p).clone());
		}

		String lexeme = "";
		renameTransitionalGroups(grouptype, group, namesUsed);
		for (int q = 0; q < group.size(); q++) {
			lexeme += group.get(q).lexeme + " ";
		}
		if (i < tokes.size() && j < tokes.size() && group.size() > 0) {

			Token token = new Token(grouptype, lexeme, group, null, null, tokes.get(j).column, tokes.get(j).line,
					tokes.get(i).start, tokes.get(j).finish);
			token.identifierToken = group.get(0).identifierToken;
			token.reifitnediToken = group.get(group.size() - 1).reifitnediToken;

			tok.add(token);
		}
		return tok;
	}

	private void renameTransitionalGroups(TokenType grouptype, ArrayList<Token> group, ArrayList<String> namesUsed) {
		Token token2 = group.get(0).clone();
		Token token3 = group.get(group.size() - 1).clone();
		if (grouptype == TokenType.PUPCONTAINER) {
			String identName = "PUP.";
			String reifName = ".PUP";
			renameAddToGroup(group, token2, token3, identName, reifName, namesUsed);
		} else if (grouptype == TokenType.LUPCONTAINER) {
			String identName = "LUP.";
			String reifName = ".PUL";
			renameAddToGroup(group, token2, token3, identName, reifName, namesUsed);
		} else if (grouptype == TokenType.LOCKETCONTAINER) {
			String identName = "LOCKET.";
			String reifName = ".TEKCOL";
			renameAddToGroup(group, token2, token3, identName, reifName, namesUsed);
		} else if (grouptype == TokenType.COCKETCONTAINER) {
			String identName = "COCKET.";
			String reifName = ".TEKCOC";
			renameAddToGroup(group, token2, token3, identName, reifName, namesUsed);
		} else if (grouptype == TokenType.LILCONTAINER) {
			String identName = "LIL.";
			String reifName = ".LIL";
			renameAddToGroup(group, token2, token3, identName, reifName, namesUsed);
		} else if (grouptype == TokenType.PIDCONTAINER) {
			String identName = "PID.";
			String reifName = ".DIP";
			renameAddToGroup(group, token2, token3, identName, reifName, namesUsed);
		} else if (grouptype == TokenType.CIDCONTAINER) {
			String identName = "CID.";
			String reifName = ".DIC";
			renameAddToGroup(group, token2, token3, identName, reifName, namesUsed);
		} else if (grouptype == TokenType.CUPCONTAINER) {
			String identName = "CUPCOND.";
			String reifName = ".DNOCPUC";
			renameAddToGroup(group, token2, token3, identName, reifName, namesUsed);
		} else if (grouptype == TokenType.POCKETCONTAINER) {
			String identName = "POCKETCOND.";
			String reifName = ".DNOCTEKCOP";
			renameAddToGroup(group, token2, token3, identName, reifName, namesUsed);
		}
	}

	private void renameAddToGroup(ArrayList<Token> group, Token token2, Token token3, String identName, String reifName,
			ArrayList<String> namesUsed) {
		String identString = identName + token2.identifierToken.lexeme;
		String reifString = token3.reifitnediToken.lexeme + reifName;

		boolean matchedIdent = true;
		boolean matchedReif = true;
		int count = 1;
		while (namesUsed.contains(identString) || namesUsed.contains(reifString)) {
			count++;
			identString = identName + count + token2.identifierToken.lexeme;
			reifString = token3.reifitnediToken.lexeme + count + reifName;
		}
		namesUsed.add(identString);
		namesUsed.add(reifString);

		token2.identifierToken.lexeme = identString;
		token2.lexeme = identName + token2.lexeme;
		token3.reifitnediToken.lexeme = reifString;
		token3.lexeme = token3.lexeme + reifName;
		group.remove(0);
		group.add(0, token2);
		group.remove(group.size() - 1);
		group.add(token3);
	}

	private void findConditionalGroupsForward(ArrayList<Token> tokes, ArrayList<Token> tok2,
			ArrayList<String> namesUsed) {
		int start = 1;
		int end = 1;
		int current = 1;
		int second = 1;
		boolean isRunning = false;
		boolean isSecond = false;
		while (current < tokes.size()) {
			if (tokes.get(current).type == TokenType.OPENBRACE && !isRunning) {
				start = current;
				isRunning = true;
			} else if (tokes.get(current).type == TokenType.OPENPAREN && !isRunning) {
				start = current;
				isRunning = true;

			} else if (tokes.get(current).type == TokenType.TEMPLID && !isRunning) {
				start = current;
				isRunning = true;
			} else if (tokes.get(current).type == TokenType.OPENBRACE && isRunning && !isSecond) {
				second = current;
				isSecond = true;
			} else if (tokes.get(current).type == TokenType.OPENPAREN && isRunning && !isSecond) {
				second = current;
				isSecond = true;
			} else if (tokes.get(current).type == TokenType.TEMPLID && isRunning && !isSecond) {
				second = current;
				isSecond = true;
			}

			boolean skip = false;

			if (tokes.get(current).type == TokenType.CLOSEDBRACE && isRunning) {
				end = current;
				if (isSecond) {
					current = second;
					skip = true;
				}
				isSecond = false;
				isRunning = false;
				if (tokes.get(start).type == TokenType.OPENBRACE) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.CUPCONTAINER, tokes, namesUsed));
				} else if (tokes.get(start).type == TokenType.OPENPAREN) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.PUPCONTAINER, tokes, namesUsed));
				} else if (tokes.get(start).type == TokenType.TEMPLID) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.LUPCONTAINER, tokes, namesUsed));
				}

			} else if (tokes.get(current).type == TokenType.CLOSEDPAREN && isRunning) {
				end = current;
				if (isSecond) {
					current = second;
					skip = true;
				}
				isSecond = false;
				isRunning = false;
				if (tokes.get(start).type == TokenType.OPENBRACE) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.COCKETCONTAINER, tokes, namesUsed));
				} else if (tokes.get(start).type == TokenType.OPENPAREN) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.POCKETCONTAINER, tokes, namesUsed));
				} else if (tokes.get(start).type == TokenType.TEMPLID) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.LOCKETCONTAINER, tokes, namesUsed));
				}
			} else if (tokes.get(current).type == TokenType.TEMPLID && isRunning) {
				end = current;
				if (isSecond) {
					current = second;
					skip = true;
				}
				isSecond = false;
				isRunning = false;
				if (tokes.get(start).type == TokenType.OPENBRACE) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.CIDCONTAINER, tokes, namesUsed));
				} else if (tokes.get(start).type == TokenType.OPENPAREN) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.PIDCONTAINER, tokes, namesUsed));
				} else if (tokes.get(start).type == TokenType.TEMPLID) {
					tok2.addAll(buildConditionalGroupingKnot(start, end, TokenType.LILCONTAINER, tokes, namesUsed));
				}
			}

			if (!skip)
				current++;
		}
	}

	private void findGroups(ArrayList<Token> tokes, ArrayList<Token> tok) {
		int j = 1;
		for (int i = 0; i < tokes.size(); i++) {
			if (isOpenIncludeTempLidExcludeSquare(tokes, i)) {
				if (i < tokes.size() - 1) {

					for (; j < tokes.size(); j++) {
						if (matchIdentifiersandReifitnedi(tokes.get(i).identifierToken, tokes.get(j).reifitnediToken)) {
							break;
						}
					}
					if (j < tokes.size()) {
						if (tokes.get(i).type == TokenType.OPENBRACE) {
							tok.addAll(buildGroupingKnot(i, j, TokenType.CUPCONTAINER, tokes));

							i = findAnyOpenInbetweenIandJReturnFirstOpen(i, j, tokes);

							if (i == -1) {
								i = j;
								j = i + 1;
							} else {

								i = i - 1;
								j = i + 1;
							}
						} else if (tokes.get(i).type == TokenType.OPENPAREN) {
							tok.addAll(buildGroupingKnot(i, j, TokenType.POCKETCONTAINER, tokes));
							i = findAnyOpenInbetweenIandJReturnFirstOpen(i, j, tokes);
							if (i == -1) {
								i = j;
								j = i + 1;
							} else {

								i = i - 1;
								j = i + 1;
							}
						} else if (tokes.get(i).type == TokenType.OPENSQUARE) {
							tok.addAll(buildGroupingKnot(i, j, TokenType.BOXCONTAINER, tokes));
							i = findAnyOpenInbetweenIandJReturnFirstOpen(i, j, tokes);
							if (i == -1) {
								i = j;
								j = i + 1;
							} else {

								i = i - 1;
								j = i + 1;
							}

						} else if (tokes.get(i).type == TokenType.TEMPLID) {
							TokenType container = TokenType.BOXCONTAINER;
							if (tokes.get(j).type == TokenType.CLOSEDPAREN)
								container = TokenType.POCKETCONTAINER;
							else if (tokes.get(j).type == TokenType.CLOSEDBRACE)
								container = TokenType.CUPCONTAINER;

							tok.addAll(buildGroupingKnot(i, j, container, tokes));
							i = findAnyOpenInbetweenIandJReturnFirstOpen(i, j, tokes);
							if (i == -1) {
								i = j;
								j = i + 1;
							} else {

								i = i - 1;
								j = i + 1;
							}

						}
					}
				}
			}

		}
	}

	private boolean matchIdentifiersandReifitnedi(Token identifier, Token reifitnedi) {
		String idToken = "";
		String diToken2 = "";
		if (identifier != null) {
			idToken = identifier.lexeme;
		}
		if (reifitnedi != null) {
			diToken2 = reifitnedi.lexeme;
		}

		String reverseditoken2 = "";
		for (int i = diToken2.length() - 1; i >= 0; i--) {
			reverseditoken2 += diToken2.charAt(i);
		}
		diToken2 = reverseditoken2;

		boolean wasThereAMatch = false;
		if (idToken.isEmpty() && diToken2.isEmpty())
			wasThereAMatch = true;
		if (!idToken.isEmpty() && !diToken2.isEmpty())
			if (idToken.equalsIgnoreCase(diToken2))
				wasThereAMatch = true;

		return wasThereAMatch;
	}

	private boolean matchIdentifierTokenandReifitnediTokenLexeme(Token identifier, Token reifitnedi) {
		String idToken = "";
		String diToken2 = "";
		if (identifier.identifierToken != null) {
			idToken = identifier.identifierToken.lexeme;
		}
		if (reifitnedi.reifitnediToken != null) {
			diToken2 = reifitnedi.reifitnediToken.lexeme;
		}

		String reverseditoken2 = "";
		for (int i = diToken2.length() - 1; i >= 0; i--) {
			reverseditoken2 += diToken2.charAt(i);
		}
		diToken2 = reverseditoken2;

		boolean wasThereAMatch = false;
		if (idToken.isEmpty() && diToken2.isEmpty())
			wasThereAMatch = true;
		if (!idToken.isEmpty() && !diToken2.isEmpty())
			if (idToken.equalsIgnoreCase(diToken2))
				wasThereAMatch = true;

		return wasThereAMatch;
	}

	private boolean matchGrouping(String open, String closed) {
		String reversePeek = "";

		String[] split = open.split("\\s+");
		String cleaned = "";
		for (int i = 0; i < split.length; i++) {
			cleaned += split[i];
		}

		String[] split2 = closed.split("\\s+");
		String cleaned2 = "";
		for (int i = 0; i < split2.length; i++) {
			cleaned2 += split2[i];
		}

		for (int i = cleaned.length() - 1; i >= 0; i--) {
			reversePeek += cleaned.charAt(i);
		}

		String replace = reversePeek.replace('{', '}');
		replace = replace.replace('(', ')');
		replace = replace.replace('[', ']');
		if (replace.equalsIgnoreCase(cleaned2))
			return true;

		return false;
	}

	private int findAnyOpenInbetweenIandJReturnFirstOpen(int start, int finish, ArrayList<Token> tokes) {
		ArrayList<Token> pushList = new ArrayList<Token>();
		ArrayList<Integer> pushListInt = new ArrayList<Integer>();
		ArrayList<Token> popList = new ArrayList<Token>();
		ArrayList<Integer> popListInt = new ArrayList<Integer>();

		for (int i = start + 1; i < finish; i++) {
			if (tokes.get(i).type == TokenType.OPENBRACE || tokes.get(i).type == TokenType.OPENPAREN
					|| tokes.get(i).type == TokenType.TEMPLID) {
				if (tokes.get(i).identifierToken != null) {
					pushList.add(tokens.get(i));
					pushListInt.add(i);
				}
			}
			if (tokes.get(i).type == TokenType.CLOSEDBRACE || tokes.get(i).type == TokenType.CLOSEDPAREN
					|| tokes.get(i).type == TokenType.TEMPLID) {
				boolean wasremoved = false;
				if (tokes.get(i).reifitnediToken != null) {
					for (int j = 0; j < pushList.size(); j++) {
						if (matchGrouping(tokes.get(i).lexeme, pushList.get(j).lexeme)) {
							pushList.remove(j);
							pushListInt.remove(j);
							wasremoved = true;
							break;
						}
					}
					if (!wasremoved) {
						popList.add(tokes.get(i));
						popListInt.add(i);
					}
				}
			}

		}
		Integer smallest = tokes.size() - 1;
		for (Integer token : popListInt) {
			if (token < smallest)
				smallest = token;
		}
		for (Integer token : pushListInt) {
			if (token < smallest)
				smallest = token;
		}
		if (pushList.size() == 0 && popList.size() == 0)
			return -1;
		return smallest;

	}

}
