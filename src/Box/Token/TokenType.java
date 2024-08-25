package Box.Token;

import java.util.LinkedHashMap;
import java.util.Map;

public enum TokenType implements TokenTypeEnum{
//TokenTypes that have keywords Associated with them
	TRUE,
	EURT,
	FALSE,
	ESLAF,
	PRINT,
	TNIRP,
	RETURN,
	NRUTER,
	SAVE,
	EVAS,
	READ,
	DAER,
	INTO,
	OTNI,
	RENAME,
	EMANER,
	TO,
	OT,
	MOVE,
	EVOM,
	YROOT,
	TOORY,
	SIN,
	NIS,
	COS,
	SOC,
	TAN,
	NAT,
	SINH,
	HNIS,
	COSH,
	HSOC,
	TANH,
	HNAT,
	LOG,
	GOL,
	CONTAINS,
	SNIATNOC,
	OPEN,
	NEPO,
	AND,
	DNA,
	OR,
	RO,
	FUN,
	NUF,
	DOUBLE,
	INT,
	BIN,
	NULL,NILL,
	LLUN, LLIN,
	NOT,
	KNOT,
	CUP,
	POCKET,
	BOX,
	
	
	
	
	///userdefined strings ,chars, or numbers
	IDENTIFIER,
	DOUBLENUM,
	INTNUM,
	BINNUM,
	UNKNOWN,
	CHAR, 
	STRING,

	///tokenTypes that have a symbol associated with them
DOT,
COMMA,
ASIGNMENTEQUALS,
EQUALSEQUALS,
EQUALSNOT,
EQUALSPLUS,
EQUALSMINUS,
NOTEQUALS,
PLUSEQUALS,
MINUSEQUALS,
GREATERTHEN,
LESSTHEN,
GREATERTHENEQUAL,
EQUALGREATERTHEN,
LESSTHENEQUAL,
EQUALLESSTHEN,
PLUS,
PLUSPLUS,
MINUS,
MINUSMINUS,
TIMES,
POWER,
BANG,
QMARK,
TEMPLID,
SINGLEAND,
OPENSQUARE,CLOSEDSQUARE,
OPENPAREN,CLOSEDPAREN,
OPENBRACE,CLOSEDBRACE,
FORWARDSLASH,
BACKSLASH,
EXPELL,
CONSUME,
SEMICOLON,

 


//compound tokens 

BOXCONTAINER,
CUPCONTAINER,
POCKETCONTAINER,
KNOTCONTAINER,

PUPCONTAINER,
COCKETCONTAINER,

LUPCONTAINER,
LOCKETCONTAINER,
LILCONTAINER,

PIDCONTAINER,
CIDCONTAINER,
///end of file


EOF, BOXXX, SPACE, SPACERETURN, TAB, NEWLINE, TONK, PUC, TEKCOP, XOB, EPYT, TYPE, UNDERSCORE, HATTAG , GATTAH, LEFTPAREN, RIGHTPAREN, NUMBER, AT, HASH;


@Override
public Integer returnTokenType(TokenTypeEnum enumToFindOrdinal) {
	
	return null;
}
	

	
	
	
}
