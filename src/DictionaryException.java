/* Extends Exception class and lets user specify error message */

public class DictionaryException extends Exception {
	public DictionaryException(String message) {
		super(message);
	}
}
