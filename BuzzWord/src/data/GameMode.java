package data;

/**
 * @author Tejas
 */
public enum GameMode {
	DICTIONARY, PLACES, ANIMALS, NAMES;


	@Override
	public String toString() {
		switch(this){
			case DICTIONARY:
				return "Dictionary";
			case PLACES:
				return "Places";
			case ANIMALS:
				return "Animals";
			case NAMES:
				return "Names";
			default:
				return null;
		}
	}
}
