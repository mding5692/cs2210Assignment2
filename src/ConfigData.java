/* ConfigData represents data stored in one entry of dictionary
 * 	- Takes in two inputs config string and integer score
 * 	- Uses config string as key
 */
public class ConfigData {
	// Attributes declared for config string and integer score
	private String config;
	private int score;
	
	// Constructor used to store config string and integer score
	public ConfigData(String config, int score) {
		this.config = config;
		this.score = score;
	}
	
	// Getter method for config string
	public String getConfig() {
		return this.config;
	}
	
	// Getter method for integer score
	public int getScore() {
		return this.score;
	}
	
}
