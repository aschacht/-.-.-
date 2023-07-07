package Box.Interpreter;

public class Returns extends RuntimeException {
	
	final Object value;
	
	
	public Returns(Object value) {
		super(null,null,false,false);
		this.value = value;
		
	}
	
	
	

}
