package Box.Interpreter;

import java.util.ArrayList;
import java.util.Iterator;

import Box.Syntax.ExprOLD;
import Box.Token.Token;
import Parser.Stmt;

public class TypesOfObject {

	private RunTimeTypes runtimeTypeForObject;
	private Token containerType;
	private Object initilizer;
	private ArrayList<ExprOLD> prototype;

	TypesOfObject(Token containerType, RunTimeTypes runTimeType, Object initilizer) {
		this.containerType = containerType;
		this.runtimeTypeForObject = runTimeType;
		this.initilizer = initilizer;

	}

	public RunTimeTypes getRunTimeTypeForObject() {
		return runtimeTypeForObject;
	}

	public Token getContainerType() {
		return containerType;
	}

	

	public Object getInitilizer() {
		return initilizer;
	}

}
