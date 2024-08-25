package Box.Grouper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Stack;

import Box.Token.Token;
import Box.Token.TokenType;

public class Grouper {
	private static class GroupError extends RuntimeException {
		private static final long serialVersionUID = -8769723875601956837L;

	}

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

	ArrayList<BigInteger> identifiersParen = new ArrayList<BigInteger>();
	int identParenCount = 0;
	ArrayList<BigInteger> identifiersBrace = new ArrayList<BigInteger>();
	int identBraceCount = 0;

	public Grouper(ArrayList<Token> tokens) {
		this.tokens = tokens;

		Random randCont = new Random();
		BigInteger upperLimitCont = new BigInteger("999999999");
		new BigInteger(upperLimitCont.bitLength(), randCont);

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
				while (identifiersParen.contains(result) && !identifiersParen.contains(result2)
						&& identifiersBrace.contains(result2) && !identifiersBrace.contains(result)) {
					rand = new Random();
					result = new BigInteger(upperLimit.bitLength(), rand);
					rand2 = new Random();
					result2 = new BigInteger(upperLimit2.bitLength(), rand2);
				}
				this.identifiersParen.add(result);
				this.identifiersBrace.add(result2);
			}
		cupOpenPointer = this.identifiersParen.size() - 1;
		pocketOpenPointer = this.identifiersParen.size() - 1;
		boxOpenPointer = this.identifiersParen.size() - 1;
		cupClosedPointer = this.identifiersParen.size() - 1;
		pocketClosedPointer = this.identifiersParen.size() - 1;
		boxClosedPointer = this.identifiersParen.size() - 1;
		cupClosedPointer2 = this.identifiersBrace.size() - 1;
		pocketClosedPointer2 = this.identifiersBrace.size() - 1;
		boxClosedPointer2 = this.identifiersBrace.size() - 1;

	}

	public ArrayList<Token> scanTokensSecondPass() {

		addHashTagsToStrings(tokens);
		matchIdentifiersToOpenClosedParenBraceSquare(tokens);
		removeSpaces(tokens);

		ArrayList<ContainerIndexes> containerIndexes = findContainers(tokens);
		renameNonBoxContainers(tokens, containerIndexes);
		renameBoxes(tokens);

		if (tokens.size() > 0) {
			tokens.add(new Token(TokenType.EOF, "", null, null, null, tokens.get(tokens.size() - 1).column + 1,
					tokens.get(tokens.size() - 1).line, tokens.get(tokens.size() - 1).start + 1,
					tokens.get(tokens.size() - 1).finish + 1));
		}
		return tokens;
	}

	private void addHashTagsToStrings(ArrayList<Token> tokens2) {

		for (int i = 0; i < tokens2.size(); i++) {
			if(i>=0&& i+3 < tokens2.size() - 1) {
				if (tokens2.get(i).type==TokenType.IDENTIFIER &&  tokens2.get(i+1).type==TokenType.HASH &&tokens2.get(i+2).type == TokenType.AT && tokens2.get(i +3).type == TokenType.IDENTIFIER) {
					String hash  = (String)tokens2.get(i+1).lexeme;
					String at  = (String)tokens2.get(i+2).lexeme;
					String str = tokens2.get(i).lexeme + hash;
					tokens2.get(i ).lexeme = str;
					tokens2.get(i).reifitnediToken = tokens2.get(i+3);
					tokens2.get(i ).literal = str;
					tokens2.remove(i+1);
					tokens2.remove(i+1);
					tokens2.remove(i+1);
					
					
				}else if (tokens2.get(i).type==TokenType.IDENTIFIER &&  tokens2.get(i+1).type==TokenType.AT &&tokens2.get(i+2).type == TokenType.HASH && tokens2.get(i +3).type == TokenType.IDENTIFIER) {
					String at  = (String)tokens2.get(i+1).lexeme;
					String hash  = (String)tokens2.get(i+2).lexeme;
					String str = hash +tokens2.get(i+3).lexeme;
					tokens2.get(i+3 ).lexeme = str;
					tokens2.get(i+3 ).identifierToken = tokens2.get(i);
					tokens2.get(i+3 ).literal = str;
					tokens2.remove(i);
					tokens2.remove(i);
					tokens2.remove(i);
					
					
				}else if ( tokens2.get(i).type == TokenType.HASH && tokens2.get(i + 1).type == TokenType.IDENTIFIER ) {
					String str = (String) tokens2.get(i +1).lexeme;
					str = "#" + str;
					tokens2.get(i + 1).lexeme = str;
					tokens2.get(i + 1).literal = str;
					tokens2.remove(i);
					
				}else if ( tokens2.get(i).type == TokenType.IDENTIFIER && tokens2.get(i + 1).type == TokenType.HASH ) {
					String str = (String) tokens2.get(i).lexeme;
					str =  str+"#";
					tokens2.get(i).lexeme = str;
					tokens2.get(i).literal = str;
					tokens2.remove(i+1);
					
				}
			}else if(i>=0&&i+1<tokens2.size()-1) {
				if ( tokens2.get(i).type == TokenType.HASH && tokens2.get(i + 1).type == TokenType.IDENTIFIER ) {
					String str = (String) tokens2.get(i +1).lexeme;
					str = "#" + str;
					tokens2.get(i + 1).lexeme = str;
					tokens2.get(i + 1).literal = str;
					tokens2.remove(i);
					
				}else if ( tokens2.get(i).type == TokenType.IDENTIFIER && tokens2.get(i + 1).type == TokenType.HASH ) {
					String str = (String) tokens2.get(i +1).lexeme;
					str =  str+"#";
					tokens2.get(i).lexeme = str;
					tokens2.get(i).literal = str;
					tokens2.remove(i+1);
					
				}
			}
		}



		
	}

	private void renameBoxes(ArrayList<Token> tok) {
		ArrayList<ContainerIndexes> boxes = checkIfContainsBox(tok, 0, tok.size());
		for (ContainerIndexes box : boxes) {
			if (tok.get(box.getStart()).identifierToken != null) {
				if (tok.get(box.getEnd()).reifitnediToken != null) {
					// dont do annything
				} else {

					String reverseReif = reverse(tok.get(box.getStart()).identifierToken.lexeme);
					Token reif = new Token(TokenType.IDENTIFIER, "", null, null, null, -1, -1, -1, -1);
					reif.lexeme = reverseReif;
					reif.column = tok.get(box.getStart()).column;
					reif.line = tok.get(box.getStart()).line;
					reif.start = tok.get(box.getStart()).start;
					reif.finish = tok.get(box.getStart()).start;
					tok.get(box.getEnd()).reifitnediToken = reif;
					tok.get(box.getEnd()).lexeme = tok.get(box.getEnd()).lexeme + reverseReif;

				}

			} else {
				if (tok.get(box.getEnd()).reifitnediToken != null) {
					String reverseReif = reverse(tok.get(box.getEnd()).reifitnediToken.lexeme);
					Token ident = new Token(TokenType.IDENTIFIER, "", null, null, null, -1, -1, -1, -1);
					ident.lexeme = reverseReif;
					ident.column = tok.get(box.getEnd()).column;
					ident.line = tok.get(box.getEnd()).line;
					ident.start = tok.get(box.getEnd()).start;
					ident.finish = tok.get(box.getEnd()).start;
					tok.get(box.getStart()).identifierToken = ident;
					tok.get(box.getStart()).lexeme = reverseReif + tok.get(box.getStart()).lexeme;

				} else {
					renameOpenClose(tok, new ContainerIndexes(box.getStart(), box.getEnd(), false),
							TokenType.OPENSQUARE);

				}
			}

		}

	}

	private void renameNonBoxContainers(ArrayList<Token> tok, ArrayList<ContainerIndexes> containerIndexes) {
		ArrayList<ContainerIndexes> exclude = new ArrayList<ContainerIndexes>();
		for (ContainerIndexes containerIndexes2 : containerIndexes) {

			renameSub(tok, containerIndexes, containerIndexes2, exclude);
			exclude.clear();
		}

	}

	private void renameSub(ArrayList<Token> tok, ArrayList<ContainerIndexes> containerIndexes, ContainerIndexes crI,
			ArrayList<ContainerIndexes> exclude) {
		ArrayList<ContainerIndexes> exclude0 = new ArrayList<ContainerIndexes>(exclude);
		buildInclude(containerIndexes, crI, exclude0);
		;
		exclude.clear();
		for (ContainerIndexes containerIndexes2 : exclude0) {
			buildInclude(containerIndexes, containerIndexes2, exclude);
			renameSub(tok, containerIndexes, containerIndexes2, exclude);

			rename(tok, containerIndexes2, TokenType.OPENPAREN);
			rename(tok, containerIndexes2, TokenType.OPENBRACE);
			exclude.clear();
		}
		rename(tok, crI, TokenType.OPENPAREN);
		rename(tok, crI, TokenType.OPENBRACE);
	}

	private void buildInclude(ArrayList<ContainerIndexes> contIndexes, ContainerIndexes containerIndexes,
			ArrayList<ContainerIndexes> exclude) {

		for (ContainerIndexes containerIndexes1 : contIndexes) {
			if (containerIndexes.getStart() < containerIndexes1.getStart()
					&& containerIndexes.getEnd() > containerIndexes1.getEnd()) {
				exclude.add(containerIndexes1);
			}
		}
	}

	private void rename(ArrayList<Token> tok, ContainerIndexes containerIndexes2, TokenType open) {
		if (tok.get(containerIndexes2.getStart()).type == open && !containerIndexes2.isKnot()) {
			if (tok.get(containerIndexes2.getStart()).identifierToken != null) {
				if (tok.get(containerIndexes2.getEnd()).reifitnediToken != null) {

				} else {
					String reverseReif = reverseTokenLexeme(tok.get(containerIndexes2.getStart()).identifierToken);
					Token reif = tok.get(containerIndexes2.getStart()).identifierToken;
					reif.lexeme = reverseReif;
					reif.column = tok.get(containerIndexes2.getEnd()).column;
					reif.line = tok.get(containerIndexes2.getEnd()).line;
					reif.start = tok.get(containerIndexes2.getEnd()).start;
					reif.finish = tok.get(containerIndexes2.getEnd()).start;
					tok.get(containerIndexes2.getEnd()).reifitnediToken = reif;
					tok.get(containerIndexes2.getEnd()).lexeme = tok.get(containerIndexes2.getEnd()).lexeme
							+ reverseReif;
				}
			} else {
				if (tok.get(containerIndexes2.getEnd()).reifitnediToken != null) {
					String reverseReif = reverseTokenLexeme(tok.get(containerIndexes2.getEnd()).reifitnediToken);
					Token ident = tok.get(containerIndexes2.getEnd()).reifitnediToken;
					ident.lexeme = reverseReif;
					ident.column = tok.get(containerIndexes2.getStart()).column;
					ident.line = tok.get(containerIndexes2.getStart()).line;
					ident.start = tok.get(containerIndexes2.getStart()).start;
					ident.finish = tok.get(containerIndexes2.getStart()).start;
					tok.get(containerIndexes2.getStart()).identifierToken = ident;
					tok.get(containerIndexes2.getStart()).lexeme = reverseReif
							+ tok.get(containerIndexes2.getStart()).lexeme;
				} else {
					if (tok.get(containerIndexes2.getStart()).type == open)
						renameOpenClose(tok, containerIndexes2, open);
				}
			}
		} else {
			if (tok.get(containerIndexes2.getStart()).identifierToken != null) {
				if (tok.get(containerIndexes2.getEnd()).reifitnediToken != null) {

				} else {
					String reverseReif = reverseTokenLexeme(tok.get(containerIndexes2.getStart()).identifierToken);
					Token reif = tok.get(containerIndexes2.getStart()).identifierToken;
					reif.lexeme = reverseReif;
					reif.column = tok.get(containerIndexes2.getEnd()).column;
					reif.line = tok.get(containerIndexes2.getEnd()).line;
					reif.start = tok.get(containerIndexes2.getEnd()).start;
					reif.finish = tok.get(containerIndexes2.getEnd()).start;
					tok.get(containerIndexes2.getEnd()).reifitnediToken = reif;
					tok.get(containerIndexes2.getEnd()).lexeme = tok.get(containerIndexes2.getEnd()).lexeme
							+ reverseReif;
					renameBetweenStartAndEnd(containerIndexes2.getStart(), containerIndexes2.getEnd(), reif.lexeme,
							tok);
				}
			} else {
				if (tok.get(containerIndexes2.getEnd()).reifitnediToken != null) {
					String reverseReif = reverseTokenLexeme(tok.get(containerIndexes2.getEnd()).reifitnediToken);
					Token ident = tok.get(containerIndexes2.getEnd()).reifitnediToken;
					ident.lexeme = reverseReif;
					ident.column = tok.get(containerIndexes2.getStart()).column;
					ident.line = tok.get(containerIndexes2.getStart()).line;
					ident.start = tok.get(containerIndexes2.getStart()).start;
					ident.finish = tok.get(containerIndexes2.getStart()).start;
					tok.get(containerIndexes2.getStart()).identifierToken = ident;
					tok.get(containerIndexes2.getStart()).lexeme = reverseReif
							+ tok.get(containerIndexes2.getStart()).lexeme;
					renameBetweenStartAndEnd(containerIndexes2.getStart(), containerIndexes2.getEnd(), ident.lexeme,
							tok);
				} else {

					if (containerIndexes2.isKnot()) {
						if (tok.get(containerIndexes2.getStart()).type == open) {
							renameOpenCloseKnot(tok, containerIndexes2, open);
							renameBetweenStartAndEnd(containerIndexes2.getStart(), containerIndexes2.getEnd(),
									tok.get(containerIndexes2.getStart()).identifierToken.lexeme, tok);
						}
					} else {
						if (tok.get(containerIndexes2.getStart()).type == open)
							renameOpenClose(tok, containerIndexes2, open);
					}
				}
			}
		}
	}

	private void renameBetweenStartAndEnd(int start, int end, String lexeme, ArrayList<Token> tok) {
		int count = 1;
		for (int i = start + 1; i < end; i++) {
			if (tok.get(i).type == TokenType.OPENPAREN) {
				if (tok.get(i).identifierToken == null) {
					String lexeme2 = lexeme + "_" + count;
					Token ident = new Token(TokenType.IDENTIFIER, lexeme2, null, null, null, -1, -1, -1, -1);
					ident.column = tok.get(i).column;
					ident.finish = tok.get(i).start;
					ident.start = tok.get(i).start;
					ident.line = tok.get(i).line;
					tok.get(i).identifierToken = ident;
					tok.get(i).lexeme = lexeme2;
					count++;
				}
			} else if (tok.get(i).type == TokenType.OPENBRACE) {
				if (tok.get(i).identifierToken == null) {
					String lexeme2 = lexeme + "_" + count;
					Token ident = new Token(TokenType.IDENTIFIER, lexeme2, null, null, null, -1, -1, -1, -1);
					ident.column = tok.get(i).column;
					ident.finish = tok.get(i).start;
					ident.start = tok.get(i).start;
					ident.line = tok.get(i).line;
					tok.get(i).identifierToken = ident;
					tok.get(i).lexeme = lexeme2;
					count++;
				}

			} else if (tok.get(i).type == TokenType.CLOSEDPAREN) {
				if (tok.get(i).reifitnediToken == null) {
					String reverse = reverse(lexeme);
					String lexeme2 = count + "_" + reverse;
					Token ident = new Token(TokenType.IDENTIFIER, lexeme2, null, null, null, -1, -1, -1, -1);
					ident.column = tok.get(i).column;
					ident.finish = tok.get(i).start;
					ident.start = tok.get(i).start;
					ident.line = tok.get(i).line;
					tok.get(i).reifitnediToken = ident;
					tok.get(i).lexeme = lexeme2;
					count++;
				}

			} else if (tok.get(i).type == TokenType.CLOSEDBRACE) {
				if (tok.get(i).reifitnediToken == null) {
					String reverse = reverse(lexeme);
					String lexeme2 = count + "_" + reverse;
					Token ident = new Token(TokenType.IDENTIFIER, lexeme2, null, null, null, -1, -1, -1, -1);
					ident.column = tok.get(i).column;
					ident.finish = tok.get(i).start;
					ident.start = tok.get(i).start;
					ident.line = tok.get(i).line;
					tok.get(i).reifitnediToken = ident;
					tok.get(i).lexeme = lexeme2;
					count++;
				}

			}

		}

	}

	private void renameOpenClose(ArrayList<Token> tok, ContainerIndexes containerIndexes2, TokenType open) {
		Token ident = null;
		String identLexeme = "";
		String reverseReif = "";
		String reifLexeme = "";
		if (open == TokenType.OPENPAREN) {
			reverseReif = reverse(identifiersParen.get(identParenCount) + "");
			identLexeme = "pocket" + identifiersParen.get(identParenCount);
			reifLexeme = reverseReif + "tekcop";
			identParenCount++;
			renameIdentReif(tok, containerIndexes2, identLexeme, reifLexeme);
		} else if (open == TokenType.OPENBRACE) {
			reverseReif = reverse(identifiersBrace.get(identBraceCount) + "");
			identLexeme = "cup" + identifiersBrace.get(identBraceCount);
			reifLexeme = reverseReif + "puc";
			identBraceCount++;
			renameIdentReif(tok, containerIndexes2, identLexeme, reifLexeme);
		} else if (open == TokenType.OPENSQUARE) {
			reverseReif = reverse(identifiersBrace.get(identBraceCount) + "");
			identLexeme = "box" + identifiersBrace.get(identBraceCount);
			reifLexeme = reverseReif + "xob";
			identBraceCount++;

			renameIdentReif(tok, containerIndexes2, identLexeme, reifLexeme);
		}

	}

	private void renameOpenCloseKnot(ArrayList<Token> tok, ContainerIndexes containerIndexes2, TokenType open) {
		Token ident = null;
		String identLexeme = "";
		String reverseReif = "";
		String reifLexeme = "";

		reverseReif = reverse(identifiersParen.get(identParenCount) + "");
		identLexeme = "knot" + identifiersParen.get(identParenCount);
		reifLexeme = reverseReif + "tonk";
		identParenCount++;
		renameIdentReif(tok, containerIndexes2, identLexeme, reifLexeme);

	}

	private void renameIdentReif(ArrayList<Token> tok, ContainerIndexes containerIndexes2, String identLexeme,
			String reifLexeme) {
		Token ident;
		ident = new Token(TokenType.IDENTIFIER, identLexeme, null, null, null, -1, -1, -1, -1);
		ident.column = tok.get(containerIndexes2.getStart()).column;
		ident.line = tok.get(containerIndexes2.getStart()).line;
		ident.start = tok.get(containerIndexes2.getStart()).start;
		ident.finish = tok.get(containerIndexes2.getStart()).start;
		tok.get(containerIndexes2.getStart()).lexeme = identLexeme + tok.get(containerIndexes2.getStart()).lexeme;
		tok.get(containerIndexes2.getStart()).identifierToken = ident;
		Token reif = new Token(TokenType.IDENTIFIER, reifLexeme, null, null, null, -1, -1, -1, -1);
		reif.column = tok.get(containerIndexes2.getEnd()).column;
		reif.line = tok.get(containerIndexes2.getEnd()).line;
		reif.start = tok.get(containerIndexes2.getEnd()).start;
		reif.finish = tok.get(containerIndexes2.getEnd()).start;
		tok.get(containerIndexes2.getEnd()).reifitnediToken = reif;
		tok.get(containerIndexes2.getEnd()).lexeme = tok.get(containerIndexes2.getEnd()).lexeme + reifLexeme;
	}

	private String reverseTokenLexeme(Token reifitnediToken) {
		String str = reifitnediToken.lexeme, nstr = "";
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i); // extracts each character
			nstr = ch + nstr; // adds each character in front of the existing string
		}
		return nstr;
	}

	private String reverse(String str) {
		String nstr = "";
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i); // extracts each character
			nstr = ch + nstr; // adds each character in front of the existing string
		}
		return nstr;
	}

	private ArrayList<ContainerIndexes> findContainers(ArrayList<Token> tok) {

		ArrayList<ContainerIndexes> contIndexes = checkIfContainsKnot(tokens, 0, tok.size());
		for (int i = 0; i < contIndexes.size(); i++) {
			ArrayList<ContainerIndexes> exclude = new ArrayList<>();
			buildExclude(contIndexes, contIndexes.get(i), exclude);
			ArrayList<ContainerIndexes> checkIfContainsKnotExclude = checkIfContainsKnotExclude(tokens,
					contIndexes.get(i).getStart(), contIndexes.get(i).getEnd(), exclude);

			for (ContainerIndexes containerIndexes : contIndexes) {
				for (ContainerIndexes containerIndexes0 : checkIfContainsKnotExclude) {
					if (containerIndexes.getStart() == containerIndexes0.getStart()
							&& containerIndexes.getEnd() == containerIndexes0.getEnd()) {
						containerIndexes.setKnot(containerIndexes0.isKnot());
					}
				}
			}
		}
		return contIndexes;
	}

	private ArrayList<ContainerIndexes> findBoxes(ArrayList<Token> tok) {

		ArrayList<ContainerIndexes> contIndexes = checkIfContainsKnot(tokens, 0, tok.size());
		for (int i = 0; i < contIndexes.size(); i++) {
			ArrayList<ContainerIndexes> exclude = new ArrayList<>();
			buildExclude(contIndexes, contIndexes.get(i), exclude);
			ArrayList<ContainerIndexes> checkIfContainsKnotExclude = checkIfContainsKnotExclude(tokens,
					contIndexes.get(i).getStart(), contIndexes.get(i).getEnd(), exclude);

			for (ContainerIndexes containerIndexes : contIndexes) {
				for (ContainerIndexes containerIndexes0 : checkIfContainsKnotExclude) {
					if (containerIndexes.getStart() == containerIndexes0.getStart()
							&& containerIndexes.getEnd() == containerIndexes0.getEnd()) {
						containerIndexes.setKnot(containerIndexes0.isKnot());
					}
				}
			}
		}
		return contIndexes;
	}

	private void buildExclude(ArrayList<ContainerIndexes> contIndexes, ContainerIndexes containerIndexes,
			ArrayList<ContainerIndexes> exclude) {

		for (ContainerIndexes containerIndexes1 : contIndexes) {
			if (containerIndexes.getStart() < containerIndexes1.getStart()
					&& containerIndexes.getEnd() > containerIndexes1.getEnd()) {
				exclude.add(containerIndexes1);
			}
		}
	}

	private ArrayList<ContainerIndexes> checkIfContainsKnot(ArrayList<Token> tok, int sta, int end) {
		boolean start = true;
		int startIndex = sta;
		int endIndex = startIndex;
		ArrayList<ContainerIndexes> contIndexes = new ArrayList<>();
		Stack<TokenType> stack = new Stack<>();
		Stack<TokenType> paren = new Stack<>();
		Stack<TokenType> brace = new Stack<>();
		int count = 0;
		boolean isKnotOrContainsKnot = false;

		while (count < end) {
			int countOpenClose = 0;
			for (int i = startIndex; i < end; i++) {
				if (tok.get(i).type == TokenType.OPENPAREN) {
					if (start) {
						start = false;
						startIndex = i;
					}
					stack.push(tok.get(i).type);
					paren.push(tok.get(i).type);
					countOpenClose++;
				} else if (tok.get(i).type == TokenType.OPENBRACE) {
					if (start) {
						start = false;
						startIndex = i;
					}
					stack.push(tok.get(i).type);
					brace.push(tok.get(i).type);
					countOpenClose++;
				} else if (tok.get(i).type == TokenType.CLOSEDBRACE) {
					endIndex = i;
					if (stack.size() > 0) {
						if (stack.peek() == TokenType.OPENBRACE)
							stack.pop();
						else
							isKnotOrContainsKnot = true;
					}
					if (brace.size() > 0) {
						brace.pop();
					}

					countOpenClose++;

					if (brace.size() == 0 && paren.size() == 0) {
						break;
					}
				} else if (tok.get(i).type == TokenType.CLOSEDPAREN) {
					endIndex = i;
					if (stack.size() > 0) {
						if (stack.peek() == TokenType.OPENPAREN)
							stack.pop();
						else
							isKnotOrContainsKnot = true;
					}
					if (paren.size() > 0) {
						paren.pop();
					}
					countOpenClose++;
					if (brace.size() == 0 && paren.size() == 0) {
						break;
					}
				}
				count++;
			}
			if (countOpenClose >= 2 && countOpenClose % 2 == 0 && isKnotOrContainsKnot && paren.size() == 0
					&& brace.size() == 0 && stack.size() > 0) {
				contIndexes.add(new ContainerIndexes(startIndex, endIndex, isKnotOrContainsKnot));
			} else if (countOpenClose >= 2 && !isKnotOrContainsKnot && paren.size() == 0 && brace.size() == 0) {
				contIndexes.add(new ContainerIndexes(startIndex, endIndex, isKnotOrContainsKnot));
			}
			paren.clear();
			brace.clear();
			stack.clear();
			count = startIndex;
			startIndex++;
			start = true;
			isKnotOrContainsKnot = false;
		}
		return contIndexes;
	}

	private ArrayList<ContainerIndexes> checkIfContainsBox(ArrayList<Token> tok, int sta, int end) {
		boolean start = true;
		int startIndex = sta;
		int endIndex = startIndex;
		ArrayList<ContainerIndexes> contIndexes = new ArrayList<>();
		Stack<TokenType> stack = new Stack<>();
		int count = 0;

		while (count < end) {
			int countOpenClose = 0;
			for (int i = startIndex; i < end; i++) {
				if (tok.get(i).type == TokenType.OPENSQUARE) {
					if (start) {
						start = false;
						startIndex = i;
					}
					stack.push(tok.get(i).type);
					countOpenClose++;
				} else if (tok.get(i).type == TokenType.CLOSEDSQUARE) {
					endIndex = i;
					if (stack.size() > 0) {
						stack.pop();

					}
					countOpenClose++;
				}
				count++;
				if (stack.size() == 0)
					break;
			}
			if (countOpenClose >= 2 && stack.size() == 0) {
				contIndexes.add(new ContainerIndexes(startIndex, endIndex, false));

			}
			stack.clear();
			count = startIndex;
			startIndex++;
			start = true;
		}
		return contIndexes;
	}

	private ArrayList<ContainerIndexes> checkIfContainsKnotExclude(ArrayList<Token> tok, int sta, int end,
			ArrayList<ContainerIndexes> exclude) {
		boolean start = true;
		int startIndex = sta;
		int endIndex = startIndex;
		ArrayList<ContainerIndexes> contIndexes = new ArrayList<>();
		Stack<TokenType> stack = new Stack<>();
		Stack<TokenType> paren = new Stack<>();
		Stack<TokenType> brace = new Stack<>();
		int count = 0;
		boolean isKnotOrContainsKnot = false;

		while (count < end) {
			int countOpenClose = 0;
			for (int i = startIndex; i <= end; i++) {

				if (tok.get(i).type == TokenType.OPENPAREN) {
					boolean dontskip = true;
					if (exclude != null) {
						for (ContainerIndexes containerIndexes : exclude) {
							if (containerIndexes.getStart() <= i && containerIndexes.getEnd() >= i) {
								dontskip = false;
								break;
							}
						}
					}
					if (dontskip) {
						if (start) {
							start = false;
							startIndex = i;
						}
						stack.push(tok.get(i).type);
						paren.push(tok.get(i).type);
						countOpenClose++;
					}

				} else if (tok.get(i).type == TokenType.OPENBRACE) {
					boolean dontskip = true;
					if (exclude != null) {
						for (ContainerIndexes containerIndexes : exclude) {
							if (containerIndexes.getStart() <= i && containerIndexes.getEnd() >= i) {
								dontskip = false;
								break;
							}
						}
					}
					if (dontskip) {
						if (start) {
							start = false;
							startIndex = i;
						}
						stack.push(tok.get(i).type);
						brace.push(tok.get(i).type);
						countOpenClose++;
					}

				} else if (tok.get(i).type == TokenType.CLOSEDBRACE) {
					boolean dontskip = true;
					if (exclude != null) {
						for (ContainerIndexes containerIndexes : exclude) {
							if (containerIndexes.getStart() <= i && containerIndexes.getEnd() >= i) {
								dontskip = false;
								break;
							}
						}
					}
					if (dontskip) {
						endIndex = i;
						if (stack.size() > 0) {
							if (stack.peek() == TokenType.OPENBRACE)
								stack.pop();
							else
								isKnotOrContainsKnot = true;
						}
						if (brace.size() > 0) {
							brace.pop();
						}

						countOpenClose++;
					}
					if (brace.size() == 0 && paren.size() == 0) {
						break;
					}
				} else if (tok.get(i).type == TokenType.CLOSEDPAREN) {
					boolean dontskip = true;
					if (exclude != null) {
						for (ContainerIndexes containerIndexes : exclude) {
							if (containerIndexes.getStart() <= i && containerIndexes.getEnd() >= i) {
								dontskip = false;
								break;
							}
						}
					}
					if (dontskip) {
						endIndex = i;
						if (stack.size() > 0) {
							if (stack.peek() == TokenType.OPENPAREN)
								stack.pop();
							else
								isKnotOrContainsKnot = true;
						}
						if (paren.size() > 0) {
							paren.pop();
						}
						countOpenClose++;
						if (brace.size() == 0 && paren.size() == 0) {
							break;
						}
					}
				}
				count++;
			}
			if (countOpenClose >= 4 && countOpenClose % 2 == 0 && isKnotOrContainsKnot && paren.size() == 0
					&& brace.size() == 0 && stack.size() > 0) {
				contIndexes.add(new ContainerIndexes(startIndex, endIndex, isKnotOrContainsKnot));
			} else if (countOpenClose == 2 && !isKnotOrContainsKnot && paren.size() == 0 && brace.size() == 0) {
				contIndexes.add(new ContainerIndexes(startIndex, endIndex, isKnotOrContainsKnot));

			}
			paren.clear();
			brace.clear();
			stack.clear();
			count = startIndex;
			startIndex++;
			start = true;
			isKnotOrContainsKnot = false;
		}
		return contIndexes;
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

	private void matchIdentifiersToOpenClosedParenBraceSquare(ArrayList<Token> tokens) {
		int j = 0;
		int p = tokens.size() - 1;
		int OriginalSize = tokens.size();
		while (j < OriginalSize) {
			for (p = 0; p <= tokens.size() - 1;) {
				for (int i = p; i <= tokens.size() - 1; i++) {
					if (tokens.get(i).type == TokenType.IDENTIFIER) {
						if (i == 0 && tokens.size() > i + 1 && tokens.size() > 1) {

							if ((tokens.get(i + 1).type == TokenType.OPENBRACE
									|| tokens.get(i + 1).type == TokenType.OPENPAREN
									|| tokens.get(i + 1).type == TokenType.OPENSQUARE
									|| tokens.get(i + 1).type == TokenType.TEMPLID)
									&& tokens.get(i + 1).identifierToken == null) {
								checkIfNextTokenIsIdentifier(i, tokens);
								break;
							}
						} else if (i == tokens.size() - 1 && tokens.size() > 1) {
							if ((tokens.get(i - 1).type == TokenType.CLOSEDBRACE
									|| tokens.get(i - 1).type == TokenType.CLOSEDPAREN
									|| tokens.get(i - 1).type == TokenType.CLOSEDSQUARE
									|| tokens.get(i - 1).type == TokenType.TEMPLID)
									&& tokens.get(i - 1).reifitnediToken == null) {
								checkIfPreviousTokenIsIdentifier(i, tokens);
								break;
							}
						} else {
							if (tokens.size() > 1
									&& (tokens.get(i - 1).type == TokenType.CLOSEDBRACE
											|| tokens.get(i - 1).type == TokenType.CLOSEDPAREN
											|| tokens.get(i - 1).type == TokenType.CLOSEDSQUARE
											|| tokens.get(i - 1).type == TokenType.TEMPLID)
									&& tokens.get(i - 1).reifitnediToken == null) {
								checkIfPreviousTokenIsIdentifier(i, tokens);
								break;
							}
							if (tokens.size() > i + 1 && tokens.size() > 1) {
								if ((tokens.get(i + 1).type == TokenType.OPENBRACE
										|| tokens.get(i + 1).type == TokenType.OPENPAREN
										|| tokens.get(i + 1).type == TokenType.OPENSQUARE
										|| tokens.get(i + 1).type == TokenType.TEMPLID)
										&& tokens.get(i + 1).identifierToken == null) {
									checkIfNextTokenIsIdentifier(i, tokens);
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

	private void checkIfPreviousTokenIsIdentifier(int i, ArrayList<Token> tokens) {

		tokens.get(i - 1).reifitnediToken = tokens.get(i);
		tokens.get(i - 1).lexeme = tokens.get(i - 1).lexeme + tokens.get(i).lexeme;
		tokens.get(i - 1).finish = tokens.get(i).finish;
		tokens.remove(i);
	}

	private void checkIfNextTokenIsIdentifier(int i, ArrayList<Token> tokens) {

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
			if (token.type == TokenType.OPENSQUARE)
				count++;
		}
		return count;
	}

	private int countUpOpenCups(ArrayList<Token> tokens2) {
		int count = 0;
		for (Token token : tokens2) {
			if (token.type == TokenType.OPENBRACE)
				count++;
		}
		return count;
	}

	private int countUpOpenPockets(ArrayList<Token> tokens2) {
		int count = 0;
		for (Token token : tokens2) {
			if (token.type == TokenType.OPENPAREN)
				count++;
		}
		return count;
	}

	private boolean isClosed(Token token) {
		if (token.type == TokenType.CLOSEDSQUARE || token.type == TokenType.CLOSEDBRACE
				|| token.type == TokenType.CLOSEDPAREN)
			return true;
		return false;
	}

	private boolean isOpen(Token token) {
		if (token.type == TokenType.OPENSQUARE || token.type == TokenType.OPENBRACE
				|| token.type == TokenType.OPENPAREN)
			return true;
		return false;
	}

}
