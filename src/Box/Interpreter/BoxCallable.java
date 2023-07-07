package Box.Interpreter;

import java.lang.reflect.Field;
import java.util.List;

import Box.Token.TokenType;

public abstract class BoxCallable {

	public List<Object> contents;
	public TokenType type;
	Object call(Interpreter interpreter, List<Object> arguments) {
		return null;
	}

	int arity() {
		return 0;
	}
	
	
	
	
	public boolean compairPrimarys(BoxCallable boxClass) {
		boolean contains = false;
			for (Object object : boxClass.contents) {
				 
				 for (Object object2 : contents) {
					  try {
						  Field field =null;
						  Object object3 =null;
						if(!(object2 instanceof BoxInstance)) {
							field = object2.getClass().getField("value");
							object3 = field.get(object2);
						}
						Field field2 = object.getClass().getField("value");
						Object object4 = field2.get(object);
						if(object3 != null) {
							if(object3.equals(object4))
								contains = true;
						}
					} catch (NoSuchFieldException e) {
						
						e.printStackTrace();
					} catch (SecurityException e) {
						
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						
						e.printStackTrace();
					}
					  
					 if(!contains)break;
				}
				 if(!contains)break;
			}
		return contains;
	}	

}

