/* 
 * Achi class implements all support methods for computer player
 */
public class Achi {
	/* Attributes include board_size, max_levels and actual gameboard with constants for symbols */
	private int board_size;
	private int max_levels;
	private char[][] gameBoard;
	private final char HUMAN = 'X';
	private final char COMP = 'O';
	private final char EMPTY = ' ';
	
	/*
	 * Constructor: Creates game board using inputs given and also stores inputs
	 */
	public Achi(int board_size, int max_levels) {
		this.board_size = board_size;
		this.max_levels = max_levels;
		this.gameBoard = new char[board_size][board_size];
	}
	
	/*
	 * Takes gameboard and turns it into String format to compare with ConfigData
	 *	- Loops through whole gameboard matrix and returns whats found
	 */
	private String repBoardAsStr() {
		String gameBoardStr = "";
		for (int row = 0; row < this.board_size; row++) {
			for (int col = 0; col < this.board_size; col++) {
				if (gameBoard[row][col] == 0) {
					gameBoardStr += " ";
				}
				else {
					gameBoardStr += this.gameBoard[row][col];
				}
			}
		}
		return gameBoardStr;
	}
	
	/*
	 * Creates and returns Dictionary depending on size specified
	 */
	public Dictionary createDictionary() {
		Dictionary newDict = new Dictionary(this.max_levels);
		return newDict;
	}
	
	/*
	 * Takes Dictionary configurations and current string representation
	 * of the gameBoard and tries to find it.
	 * 	- If found, returns score
	 * 	- Else return -1
	 */
	public int repeatedConfig(Dictionary configurations) {
		String gameBoardStr = repBoardAsStr();
		return configurations.find(gameBoardStr);
	}
	
	/*
	 * Takes Dictionary and current score, represents gameboard as string
	 * and then creates new ConfigData holding this all and then inserts it
	 * 	- Throws exception if already exist
	 */
	public void insertConfig(Dictionary configurations, int score) {
		String gameBoardStr = repBoardAsStr();
		ConfigData newEntry = new ConfigData(gameBoardStr, score);
		try {
			configurations.insert(newEntry);
		}
		catch (DictionaryException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * Stores symbol in gameBoard[row][col].
	 */
	public void storePlay(int row, int col, char symbol) {
		if ( (row == -1 || col == -1) || (row > board_size || col > board_size) ) {
			System.out.println("Chose wrong numbers for row and columns");
		} else {
			this.gameBoard[row][col] = symbol;
		}
	}
	
	/*
	 * Returns true if gameBoard[row][col]is 0 or empty, otherwise it returns false
	 * Note: Empty index is referred to using 0
	 */
	public boolean tileIsEmpty (int row, int col) {
		if (gameBoard[row][col] == 0 || gameBoard[row][col] == EMPTY || gameBoard[row][col] == ' ') {
			return true;
		}
		return false;
	}
	
	/*
	 *  Returns true if gameBoard[row][col] is ’O’; otherwise it returns false.
	 */
	public boolean tileIsComputer (int row, int col) {
		if (this.gameBoard[row][col] == COMP) {
			return true;
		}
		return false;
	}
	
	/*
	 *  Returns true if gameBoard[row][col] is ’X’; otherwise it returns false.
	 */
	public boolean tileIsHuman (int row, int col) {
		if (this.gameBoard[row][col] == HUMAN) {
			return true;
		}
		return false;
	}
	
	/*
	 *  Returns true if there are n adjacent tiles of type
	 *	symbol in the same row, column, or diagonal of gameBoard, where n is the size of the game
	 *	board.
	 */
	public boolean wins (char symbol) {
		if (checkDiagonal(symbol, 0) == true || checkDiagonal(symbol, this.board_size - 1) == true ) {
			return true;
		}
		for (int checkCol = 0; checkCol < this.board_size; checkCol++) {
			if (checkVertical(symbol, checkCol) == true) {
				return true;
			}
		}
		for (int checkRow = 0; checkRow < this.board_size; checkRow++) {
			if (checkHorizontal(symbol, checkRow) == true) {
				return true;
			}
		}
		return false;
	}
	
	// Checks winning conditions, checks if one symbol is covered from one corner to opposite
	private boolean checkDiagonal(char symbol, int row) {
		if (row == 0) {
			for (int i = row; i < this.board_size; i++) {
				if (this.gameBoard[i][i] != symbol) {
					return false;
				}
			}
		} else {
			int col = 0;
			for (int i = row; i >= 0; i--) {
				if (this.gameBoard[i][col] != symbol) {
					return false;
				}
				col = col++;
			}
		}
		return true;
	}
	
	// Checks vertically and checks if there is a column of symbols formed
	private boolean checkVertical(char symbol, int col) {
		if (this.gameBoard[0][col] == symbol && this.gameBoard[this.board_size - 1][col] == symbol) {
			boolean colIsConnected = checkVerticalLine(symbol, col);
			return colIsConnected;
		}
		return false;
	}
	
	// Checks the column to see if made up all same symbols
	private boolean checkVerticalLine(char symbol, int col) {
		for (int i = 0; i < this.board_size; i++) {
			if (this.gameBoard[i][col] != symbol) {
				return false;
			}
		}
		return true;
	}
	
	// Checks horizontally to see if symbol has filled it up
	private boolean checkHorizontal(char symbol, int row) {
		if (this.gameBoard[row][0] == symbol && this.gameBoard[row][this.board_size-1] == symbol) {
			boolean rowIsConnected = checkHorizontalLine(symbol, row);
			return rowIsConnected;
		}
		return false;
	}
	
	// Checks actual row to see if filled up
	private boolean checkHorizontalLine(char symbol, int row) {
		for (int i = 0; i < this.board_size; i++) {
			if (this.gameBoard[row][i] != symbol) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Returns true if the game configuration corresponding
	 * to gameBoard is a draw assuming that the player that will perform the next move uses
	 * symbols of the type specified by symbol.
	 */
	public boolean isDraw(char symbol) {
		String gameBoardStr = repBoardAsStr();
		if (reachedMaxMoves(gameBoardStr) == true) {
			for (int i = 0; i < this.board_size; i++) {
				for (int j = 1; j < this.board_size - 1; j++) {
					if (this.gameBoard[i][j] == symbol) {
						if (i == 0) {
							if (checkAdjTopHasEmpty(i,j) == true){
								return false;
							}
						} else if (i==this.board_size - 1) {
							if (checkAdjBottomHasEmpty(i,j) == true) {
								return false;
							}
						} else {
							if (checkAdjHasEmpty(i,j) == true) {
								return false;
							}
						}
					}
				}
				
			}
		} else {
			return false;
		}
		return true;		
	}
	
	// Checks if players have reached maximum amount of moves
	private boolean reachedMaxMoves(String gameBoardStr) {
		String newStr = gameBoardStr.replaceAll("\\s","");
		int maxMoves = ((this.board_size)^2) - 1;
		if (newStr.length() == maxMoves) {
			return true;
		}
		return false;
	}
	
	// Check if cell has empty cells if current cell is on top row
	private boolean checkAdjTopHasEmpty(int row, int col) {
		if(col - 1 < 0 || col + 1 > board_size ) {
			if (this.tileIsEmpty(row+1, col)) {
				return true;
			}
		}else if (this.tileIsEmpty(row, col-1)|| this.tileIsEmpty(row+1, col) || this.tileIsEmpty(row, col+1) ) {
				return true;
			}
		return false;
	}
	
	// Check if have any empty cells around
	private boolean checkAdjBottomHasEmpty(int row, int col) {
		if(col - 1 < 0 || col + 1 > board_size ) {
			if (this.tileIsEmpty(row-1, col)) {
				return true;
			}
		}
		else if (this.tileIsEmpty(row, col-1) || this.tileIsEmpty(row-1, col) || this.tileIsEmpty(row, col+1) ) {
			return true;
		}
		return false;
	}
	
	// Check if any empty cells if current cell is on bottom row
	private boolean checkAdjHasEmpty(int row, int col) {
		if(col - 1 < 0 || col + 1 > board_size ) {
			if (this.tileIsEmpty(row+1, col) || this.tileIsEmpty(row-1, col)) {
				return true;
			}
		}else if (this.tileIsEmpty(row, col-1) || this.tileIsEmpty(row-1, col) || this.tileIsEmpty(row, col+1) || this.tileIsEmpty(row+1, col)) {
			return true;
		}
		return false;
	}
	
	/*
	 * Returns 3 if computer has won, 0 if human player has won, 
	 * Returns 2 if draw and 1 if undecided
	 */
	public int evalBoard(char symbol) {
		if (wins(COMP) == true) {
			return 3;
		} else if (wins(HUMAN) == true) {
			return 0;
		} else if (isDraw(symbol) == true) {
			return 2;
		}
		return 1;	
	}
	

}
