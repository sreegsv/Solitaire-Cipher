//Name: Sreegovind Vineetha, Created for project in COMP250 McGill


package Cipher
public class SolitaireCipher {
	public Deck key;
	private Deck originalKey; 

	public SolitaireCipher(Deck key) {
		this.originalKey = new Deck(key); 
		this.key = new Deck(key); 
	}

	/*
	 * Generates a keystream of the given size
	 */
	public int[] getKeystream(int size) {
		int[] keystream = new int[size];
		for (int i = 0; i < size; i++) {
			keystream[i] = key.generateNextKeystreamValue();
		}
		return keystream;
	}

	/*
	 * Encodes the input message using the algorithm described in the pdf.
	 */
	public String encode(String msg) {
		this.key = new Deck(originalKey);

		// remove non-letters and convert to uppercase
		StringBuilder cleanedMessage = new StringBuilder();
		for (char ch : msg.toCharArray()) {
			if (Character.isLetter(ch)) {
				cleanedMessage.append(Character.toUpperCase(ch));
			}
		}

		// Generate the keystream of the correct size
		int[] keystream = getKeystream(cleanedMessage.length());
		StringBuilder encodedMessage = new StringBuilder();

		// Encode the message using the keystream
		for (int i = 0; i < cleanedMessage.length(); i++) {
			char originalChar = cleanedMessage.charAt(i);
			int shift = keystream[i];
			char encodedChar = (char) ((originalChar - 'A' + shift) % 26 + 'A');
			encodedMessage.append(encodedChar);
		}

		return encodedMessage.toString();
	}

	/*
	 * Decodes the input message using the algorithm described in the pdf.
	 */
	public String decode(String msg) {
		this.key = new Deck(originalKey);

		
		int[] keystream = getKeystream(msg.length());
		StringBuilder decodedMessage = new StringBuilder();

		
		for (int i = 0; i < msg.length(); i++) {
			char encodedChar = msg.charAt(i);
			int shift = keystream[i];
			int decodedValue = (encodedChar - 'A' - shift) % 26;
			if (decodedValue < 0) {
				decodedValue += 26; // Adjust for negative values
			}
			char decodedChar = (char) (decodedValue + 'A');
			decodedMessage.append(decodedChar);
		}

		return decodedMessage.toString();
	}
}
