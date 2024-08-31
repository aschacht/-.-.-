package Box.Interpreter;

import java.util.ArrayList;
import java.util.List;

import Box.Token.TokenType;

public class BoxKnotClass extends BoxCallable{
	public String name;
	
	private boolean enforce;

	private TypesOfObject typesOfObject;
	
	
	public BoxKnotClass(String name, ArrayList<Object> boxPrimarys,TokenType type,boolean enforce, TypesOfObject typesOfObject) {
		this.name = name;
		this.contents = boxPrimarys;
		this.type = type;
		this.typesOfObject = typesOfObject;
		this.setEnforce(enforce);
		
	}

	
	
	@Override
	public String toString() {
		String contents = "";
		contents ="{==(KNOT)==(";
		
		for (Object object : this.contents) {
			contents += object.toString()+" ";
		}
		
		
			contents += " )==(KNOT)==}";
		return contents;
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
		Object object = contents.get(integer);
		if(object instanceof Instance) {
			((Instance)object).setAt(value, 0);
		}else {
			contents.set(integer, value);			
		}
		
		
		
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
				}
			}
		}
		return null;
	}


	public Object get(int i) {
		
		return contents.get(i);
	}



	public void setPrimaryAtEnd(String data) {
		Object object = contents.get(contents.size()-1);
		if(object instanceof Instance) {
			((Instance)object).setAtEnd(data);
		}else {
			contents.set(contents.size()-1, data);
		}
	}
	
	
	
	
}