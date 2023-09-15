package Box.Interpreter;

import Box.Syntax.Expr;
import Box.Token.Token;
import Box.Token.TokenType;

public enum RunTimeTypes {
	Int, Double, Bin, Char, String, Boolean, Naeloob, Gnirts, Rahc, Nib, Elbuod, Tni, NULL, NILL, LLUN, LLIN, Knot, Cup,
	Pocket, Box, Any, Lash, Lid, CupOpenRight, CupOpenLeft, PocketOpenRight, PocketOpenLeft, BoxOpenRight, BoxOpenLeft,
	Type, Epyt, Function;

	public static RunTimeTypes getObjectType(Object obj, Object value, Interpreter interpreter) {
		if (obj instanceof Expr.Literal) {
			if (value instanceof Integer) {
				return RunTimeTypes.Int;
			} else if (value instanceof Double) {
				return RunTimeTypes.Double;
			} else if (value instanceof Bin) {
				return RunTimeTypes.Bin;
			} else if (value instanceof String) {
				return RunTimeTypes.String;
			} else if (value instanceof Boolean) {
				return RunTimeTypes.Boolean;
			} else if (value == null) {
				return RunTimeTypes.NULL;
			}
		} else if (obj instanceof Expr.Laretil) {
			if (value instanceof Integer) {
				return RunTimeTypes.Tni;
			} else if (value instanceof Double) {
				return RunTimeTypes.Elbuod;
			} else if (value instanceof Bin) {
				return RunTimeTypes.Nib;
			} else if (value instanceof String) {
				return RunTimeTypes.Gnirts;
			} else if (value instanceof Boolean) {
				return RunTimeTypes.Naeloob;
			} else if (value == null) {
				return RunTimeTypes.LLUN;
			}
		} else if (obj instanceof Expr.Cup) {
			return RunTimeTypes.Cup;
		} else if (obj instanceof Expr.Pocket) {
			return RunTimeTypes.Pocket;
		} else if (obj instanceof Expr.Boxx) {
			return RunTimeTypes.Box;
		} else if (obj instanceof Expr.Knot) {
			return RunTimeTypes.Knot;
		} else if (obj instanceof Expr.LiteralChar) {
			return RunTimeTypes.Char;
		} else if (obj instanceof Expr.LaretilChar) {
			return RunTimeTypes.Rahc;
		} else if (obj instanceof Expr.CupOpenRight) {
			return RunTimeTypes.CupOpenRight;
		} else if (obj instanceof Expr.CupOpenLeft) {
			return RunTimeTypes.CupOpenLeft;
		} else if (obj instanceof Expr.PocketOpenRight) {
			return RunTimeTypes.PocketOpenRight;
		} else if (obj instanceof Expr.PocketOpenLeft) {
			return RunTimeTypes.PocketOpenLeft;
		} else if (obj instanceof Expr.BoxOpenRight) {
			return RunTimeTypes.BoxOpenRight;
		} else if (obj instanceof Expr.BoxOpenLeft) {
			return RunTimeTypes.BoxOpenLeft;
		} else if (obj instanceof Expr.Lash) {
			return RunTimeTypes.Lash;
		} else if (obj instanceof Expr.Lid) {
			return RunTimeTypes.Lid;
		}else if (obj instanceof Expr.Variable) {
			Object lookUpVariable = interpreter.lookUpVariable(((Expr.Variable)obj).name, (Expr.Variable)obj);
			if(lookUpVariable instanceof BoxInstance) {
				if(((BoxInstance)lookUpVariable).boxClass instanceof BoxClass) {
					if(((BoxClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.BOXCONTAINER ) {
						return RunTimeTypes.Box;
					}else if(((BoxClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.CUPCONTAINER ) {
						return RunTimeTypes.Cup;
					}else if(((BoxClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.POCKETCONTAINER ) {
						return RunTimeTypes.Pocket;
					}
				}else if(((BoxInstance)lookUpVariable).boxClass instanceof BoxContainerClass) {
					if(((BoxContainerClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.BOXCONTAINER ) {
						return RunTimeTypes.Box;
					}else if(((BoxContainerClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.CUPCONTAINER ) {
						return RunTimeTypes.Cup;
					}else if(((BoxContainerClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.POCKETCONTAINER ) {
						return RunTimeTypes.Pocket;
					}
				}
			}
			
		}else if (obj instanceof Expr.Elbairav) {
			Object lookUpVariable = interpreter.lookUpVariable(((Expr.Elbairav)obj).name, (Expr.Elbairav)obj);
			if(lookUpVariable instanceof BoxInstance) {
				if(((BoxInstance)lookUpVariable).boxClass instanceof BoxClass) {
					if(((BoxClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.BOXCONTAINER ) {
						return RunTimeTypes.Box;
					}else if(((BoxClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.CUPCONTAINER ) {
						return RunTimeTypes.Cup;
					}else if(((BoxClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.POCKETCONTAINER ) {
						return RunTimeTypes.Pocket;
					}
				}else if(((BoxInstance)lookUpVariable).boxClass instanceof BoxContainerClass) {
					if(((BoxContainerClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.BOXCONTAINER ) {
						return RunTimeTypes.Box;
					}else if(((BoxContainerClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.CUPCONTAINER ) {
						return RunTimeTypes.Cup;
					}else if(((BoxContainerClass)((BoxInstance)lookUpVariable).boxClass).type == TokenType.POCKETCONTAINER ) {
						return RunTimeTypes.Pocket;
					}
				}
			}
		}

		return RunTimeTypes.Any;
	}

	static RunTimeTypes getTypeBasedOfToken(Token type2) {
		if (type2 != null) {
			if (type2.type == TokenType.BOXXX)
				return RunTimeTypes.Box;
			else if (type2.type == TokenType.CUP)
				return RunTimeTypes.Cup;
			else if (type2.type == TokenType.POCKET)
				return RunTimeTypes.Pocket;
			else if (type2.type == TokenType.FUN)
				return RunTimeTypes.Function;
			else if (type2.type == TokenType.IDENTIFIER)
				return RunTimeTypes.Any;
			
		}
		return RunTimeTypes.Any;
	}

	static RunTimeTypes getTypeBasedOfTokenType(TokenType type2) {
		
			if (type2 == TokenType.BOXXX)
				return RunTimeTypes.Box;
			else if (type2 == TokenType.BOXCONTAINER)
				return RunTimeTypes.Box;
			else if (type2 == TokenType.CUP)
				return RunTimeTypes.Cup;
			else if (type2 == TokenType.CUPCONTAINER)
				return RunTimeTypes.Cup;
			else if (type2 == TokenType.POCKET)
				return RunTimeTypes.Pocket;
			else if (type2 == TokenType.POCKETCONTAINER)
				return RunTimeTypes.Pocket;
			else if (type2 == TokenType.FUN)
				return RunTimeTypes.Function;
			return null;
			
		
	}



}
