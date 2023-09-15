package Box.Token;

public class DynamicEnumTokenType implements TokenTypeEnum {

	
	
	private final String name;
	private final Integer ordinal;

	public DynamicEnumTokenType(String name,Integer ordinal) {
		this.name = name;
		this.ordinal = ordinal;
		
	}
	
	@Override
	public Integer returnTokenType(TokenTypeEnum enumToFindOrdinal) {

		return null;
	}

	public String getName() {
		return name;
	}

	public Integer getOrdinal() {
		return ordinal;
	}

}
