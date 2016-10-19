import java.util.*;

/* Dictionary Class for using to store the game, configuration and scores 
 * - Implements the DictionaryADT interface
 */

public class Dictionary implements DictionaryADT {
	Node[] hashtable = null; // Uses an array of Nodes with each Node able to hold ConfigData
	int size = 0; // Specifies size of hashtable
	
	
	/*
	 * Constructor: Takes in input of integer size
	 * - Creates empty dictionary of specified size using ArrayList
	 */
	public Dictionary(int size) {
		this.size = size;
		hashtable = new Node[size];
	}
	
	/*
	 * Returns integer based on the unicode position of character existing in ConfigData, excluding
	 * whitespace 
	 */
	private int hashFunction(String configStr) {
		configStr = configStr.replaceAll(" ", ""); // replaces whitespace 
		int key = 0;
		for (int i = 0; i < configStr.length();i++) {
			key += ( ((int) configStr.charAt(i))% size); // figures out unicode position of char and mods by size
		}
		key %= size; // mods the key in case it gets bigger than alloted size for hashtable
		return key;
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
		
		// Creates key based on how many characters are in string, removes whitespace
		int key = hashFunction(pair.getConfig());
		
		// Creates newNode to hold ConfigData and tests if its empty in hashtable entry
		// if it is, stick in newNode, else keep moving through next of newNode to get nextNode to stick
		// in newNode
		Node newNode = new Node(pair);
		if (hashtable[key] == null) { // if array index is empty, stick it in
			hashtable[key] = newNode; 
		} else { 
			Node currNode = hashtable[key]; // else goes to nextNode in array index (array index is linkedlist)
			while (currNode.getNextNode() != null) {
				currNode = currNode.getNextNode();
			}
			currNode.setNextNode(newNode);
		}
		return 0;
	}

	/*
	 * Remove ConfigData specified by config string taken in as input
	 *  - Throws Exception if specified ConfigData is not found
	 * (non-Javadoc)
	 * @see DictionaryADT#remove(java.lang.String)
	 */
	public void remove(String config) throws DictionaryException {
		if (this.find(config) == -1) { // uses find method to check if its there
			throw new DictionaryException("Remove failed: Not found");
		}
		
		int key = hashFunction(config);
		if (hashtable[key].getNodeEntry().getConfig().equals(config)) {
			hashtable[key] = null;
		} else {
			Node currNode = hashtable[key];
			while (true) {
				if (currNode.getNextNode() == null) {
					break;
				} else if (currNode.getNextNode().getNodeEntry().getConfig().equals(config) && currNode.getNextNode().getNextNode() != null) {
					currNode.setNextNode(currNode.getNextNode().getNextNode());
				} else if (currNode.getNextNode().getNodeEntry().getConfig().equals(config) && currNode.getNextNode().getNextNode() == null) {
					currNode.setNextNode(null);
				}
				currNode = currNode.getNextNode();
			}
		
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
		// Searches for key in Node array and finds the
		// config that matches the config string input given
		int key = hashFunction(config);	
		if (hashtable[key] == null) {
			return -1;
		}
		if (hashtable[key].getNodeEntry().getConfig().equals(config)){
			return hashtable[key].getNodeEntry().getScore();
		} else {
			Node currNode = hashtable[key];
			while (currNode.getNextNode() != null) {
				currNode = currNode.getNextNode();
				if (currNode.getNodeEntry().getConfig().equals(config)) {
					result = currNode.getNodeEntry().getScore();
					break;
				}
			}
		}
		return result;
	}

}
