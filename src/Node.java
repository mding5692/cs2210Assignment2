/*
 * Node class ==> Used for linkedlist of each array index of hashtable array
 */
public class Node {
	/* Attributes stores ConfigData entry and reference to next node */
	private ConfigData entry;
	private Node next;
	
	/* Constructor for Node class, takes in ConfigData */
	public Node(ConfigData entry) {
		this.entry = entry;
		this.next = null;
	}
	
	/* Gets the entry stored at Node */
	public ConfigData getNodeEntry() {
		return this.entry;
	}
	
	/* Sets the nextNode for currNode */
	public void setNextNode(Node nextNode) {
		this.next = nextNode;
	}
	
	/* Returns the next node referenced by next attribute */
	public Node getNextNode() {
		return this.next;
	}
	
}
