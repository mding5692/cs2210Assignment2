import java.util.*;

/* Dictionary Class for using to store the game, configuration and scores 
 * - Implements the DictionaryADT interface
 */

public class Dictionary implements DictionaryADT {
	// Uses an ArrayList from Java's Util package to store the ConfigData entries
	private ArrayList<ConfigData> hashTable = null;
	
	/*
	 * Constructor: Takes in input of integer size
	 * - Creates empty dictionary of specified size using ArrayList
	 */
	public Dictionary(int size) {
		hashTable = new ArrayList<ConfigData>(size);
	}

	/* Inserts ConfigData into Dictionary and takes ConfigData as input
	 *  - Throws Exception if it already exists and returns 1
	 * (non-Javadoc)
	 * @see DictionaryADT#insert(ConfigData)
	 */
	public int insert(ConfigData pair) throws DictionaryException {
		// Uses find method and if found, throws DictionaryException
		if (this.find(pair.getConfig()) != -1) {
			throw new DictionaryException("Can't be inserted: Entry already exists in Dictionary");
			//return 1;
		}
		this.hashTable.add(pair);
		return 0;
	}

	/*
	 * Remove ConfigData specified by config string taken in as input
	 *  - Throws Exception if specified ConfigData is not found
	 * (non-Javadoc)
	 * @see DictionaryADT#remove(java.lang.String)
	 */
	public void remove(String config) throws DictionaryException {
		// Uses find method and if not found throws DictionaryException
		if (this.find(config) == -1) {
			throw new DictionaryException("Can't be removed: Entry not found in Dictionary");
		}
		
	}

	/*
	 * Finds ConfigData based on config string key
	 * - Returns score if found in Dictionary
	 * - Returns -1 if not found in Dictionary
	 * (non-Javadoc)
	 * @see DictionaryADT#find(java.lang.String)
	 */
	public int find(String config) {
		int result = -1;
		// Searches through the HashTable ArrayList and finds the
		// config key that matches the config string input given
		for (ConfigData entry : this.hashTable) {
			if (entry.getConfig().equals(config)) {
				result = entry.getScore();
				break;
			}
		}
		return result;
	}

}
