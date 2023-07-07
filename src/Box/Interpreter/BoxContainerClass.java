package Box.Interpreter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import Box.Syntax.Expr;
import Box.Syntax.Expr.Literal;
import Box.Token.TokenType;

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
		BoxInstance instance = new BoxInstance(this);
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
			if(expr instanceof BoxInstance) {
				if((((BoxInstance)expr).boxClass) instanceof BoxClass) {
					if(((BoxClass)((BoxInstance)expr).boxClass).name.equalsIgnoreCase(lexeme)) {
						return expr;
					}
				}else if((((BoxInstance)expr).boxClass) instanceof BoxContainerClass) {
					if(((BoxContainerClass)((BoxInstance)expr).boxClass).name.equalsIgnoreCase(lexeme)) {
						return expr;
					}
				}else if((((BoxInstance)expr).boxClass) instanceof BoxKnotClass) {
					if(((BoxKnotClass)((BoxInstance)expr).boxClass).name.equalsIgnoreCase(lexeme)) {
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
		Literal literal = new Expr.Literal(data);
		contents.add(literal);
	}


	
	
	
	
}
