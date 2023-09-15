package Box.Token;

import java.util.LinkedHashMap;
import java.util.Map;

public class TTDynamic{

	private static Map< String, TokenTypeEnum> map = new LinkedHashMap<>();
	private static int count;
	private static TTDynamic instance = null;
	
	
	
	
	private TTDynamic() {
		TokenType[] values = TokenType.values();
		 this.count = values.length;
		 for (TokenType tokenType : values) {
			map.put(tokenType.toString(),tokenType);
		}
		 
	}

	public static synchronized TTDynamic getInstance() {
		if(instance == null) {
			instance = new TTDynamic();
		}
		return instance;
	}
	
	public static synchronized void addType(String string) {
		map.put(string, new DynamicEnumTokenType(string, count));
		count++;
	}


	
}
