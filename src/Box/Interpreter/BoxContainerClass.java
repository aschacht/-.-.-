package Box.Interpreter;

import java.util.ArrayList;
import java.util.List;

import Box.Syntax.ExprOLD;
import Box.Syntax.ExprOLD.Literal;
import Box.Token.TokenType;
import Parser.Stmt;

public class BoxContainerClass extends BoxCallable{
	public String name;
	
	private boolean enforce;
	
	private TypesOfObject typesOfObject;
	
	
	public BoxContainerClass(String name, ArrayList<Object> boxPrimarys,TokenType type,boolean enforce, TypesOfObject typesOfObject) {
		this.name = name;
		this.contents = boxPrimarys;
		this.type = type;
		this.typesOfObject = typesOfObject;
		this.setEnforce(enforce);
		
	}

	@Override
	public String toString() {
		String toStringContents = "";
		StringBuilder sb = new StringBuilder(name);
		toStringContents= name+"[ ";
		for (Object object : contents) {
			toStringContents += object.toString()+" ";
		}
		toStringContents+= " ]"+sb.reverse().toString();
		return toStringContents;
	}

	public List<Object> getPrimarys() {
		return contents;
	}


	public boolean isEnforce() {
		return enforce;
	}


	public void setEnforce(boolean enforce) {
		this.enforce = enforce;
	}


	public String getName() {
		return name;
	}


	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Instance instance = new Instance(this);
		return instance;
	}


	@Override
	public int arity() {
		
		return 0;
	}


	public void setPrimaryAt(Integer integer, Object value) {
		if(integer == 0 && contents.size()-1 ==-1) {
			contents.add( value);			
		}else
			contents.set(integer, value);
	}


	public Object get(String lexeme) {
		for (Object expr : contents) {
			if(expr instanceof Instance) {
				if((((Instance)expr).boxClass) instanceof BoxClass) {
					if(((BoxClass)((Instance)expr).boxClass).name.equalsIgnoreCase(lexeme)) {
						return expr;
					}
				}else if((((Instance)expr).boxClass) instanceof BoxContainerClass) {
					if(((BoxContainerClass)((Instance)expr).boxClass).name.equalsIgnoreCase(lexeme)) {
						return expr;
					}
				}else if((((Instance)expr).boxClass) instanceof BoxKnotClass) {
					if(((BoxKnotClass)((Instance)expr).boxClass).name.equalsIgnoreCase(lexeme)) {
						return expr;
					}
				}
			}
		}
		return null;
	}


	public Object get(int i) {
		
		return contents.get(i);
	}

	public void setPrimaryAtEnd(String data) {
		Literal literal = new ExprOLD.Literal(data);
		contents.add(literal);
	}

	public int size() {
		
		return contents.size();
	}


	
	
	
	
}
