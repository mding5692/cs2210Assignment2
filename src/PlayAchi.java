import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class PlayAchi extends JFrame {
     private final char COMPUTER = 'O';
     private final char HUMAN    = 'X';
     private final char EMPTY = ' ';
     private final int MIN_DELAY = 400;   // Minimum amount of time in milliseconds, before
                                          // computer shows its move
     private final int THINKING = 10000;  // Number of moves considered by the computer before
                                          // it prints a '.' for the user to know the program
                                          // is still working

     private JButton [][] gameDisplay;    // Game board displayed on the screen
     private Achi game;       
     private int board_size;    
     private int max_level;              // Maximum level of the game tree that
                                         // will be explored                    

     private int from_row = -1, from_col = -1; // Position from where a tile will be shifted
     private int to_row = -1, to_col = -1;     // Position to where a tile will be shifted
     private int numCalls = 0;
     private boolean isThinking = false;

     private Dictionary configurations;


    /* Constructor. Creates a panel to represent the game board and destroys
       the panel when its window is closed.                                 */
    /* -------------------------------------------- */
    public PlayAchi(int size, int depth)
    /* -------------------------------------------- */
    {
        Container c = getContentPane();
	c.setLayout(new GridLayout(size,size));  
	gameDisplay = new JButton[size][size];
	Icon emptySquare = new ImageIcon("empty.gif");
	ClickHandler handler = new ClickHandler(size);

	/* Board is represented as a grid of clickable buttons */
        for(int i = 0; i < size; i++)
           for(int j = 0; j < size; j++) {
               gameDisplay[i][j] = new JButton("",emptySquare);
	       gameDisplay[i][j].setEnabled(true);
               add(gameDisplay[i][j]);
               gameDisplay[i][j].addActionListener(handler);
           }
               
	board_size = size;
	max_level = depth;
        game = new Achi(size,depth); /* User code needed to play */
    }

    
    /* To run the program type: java PlayAchi size depth, where 
       size is the size of the board and depth is the maximum level of
       recursive calls the program will make. The larger the depth, the 
       better the program plays, but the slower it is.                  */
    /* -------------------------------------------- */
    public static void main(String [] args)
    /* -------------------------------------------- */
    {
        /* Check that the number of arguments is the correct one */
	if (args.length != 2) {
	    System.out.println
		("Usage: java PlayAchi board-size depth");
	    System.exit(0);
	}

        /* Size of the game board */
	int size = Integer.parseInt(args[0]);

        /* Number of positions marked by the same player in the same row, 
	   column, or diagonal, required to win */
	int depth = Integer.parseInt(args[1]);

	/* Create the game board and start the game */
        JFrame f = new PlayAchi(size,depth);

        f.setSize(size*100,size*100);
        f.setVisible(true);

        f.addWindowListener(new WindowAdapter( ) {
            public void windowClosing(WindowEvent event) {
                System.exit( 0 );
            }                 
        });
    }


    /* Panel to represent the game board. It contains methods for detecting
       the position selected by the human player.                           */

    /* ------------------------------------------------- */
    private class ClickHandler implements ActionListener {
    /* ------------------------------------------------- */
	private int board_size;
	private boolean game_ended = false;

	/* Constructor. Save board size in instance variable */
       /* ------------------------------ */
	public ClickHandler(int size) {
       /* ------------------------------ */
	    board_size = size;
	}

	/* When the user has selected a play, this method is invoked to 
	   process the selected play */
       /* --------------------------------------------- */
        public void actionPerformed(ActionEvent event) {
       /* --------------------------------------------- */
            if(event.getSource() instanceof JButton) { /* Some position of the 
							  board was selected */
		int row = -1, col = -1;
		PosPlay pos;

		if (game_ended) System.exit(0);
		/* Find out which position was selected by th eplayer */
                for (int i = 0; i < board_size; i++) {
                    for (int j = 0; j < board_size; j++)
                        if(event.getSource() == gameDisplay[i][j]) {
			    row = i;
			    col = j;
			    break;
			}
		    if (row != -1) break;
		}

		if (numberEmptyTiles() == 1)
		    if (from_row == -1) {
			if (game.tileIsHuman(row,col)) {
			    from_row = row;
			    from_col = col;

			    /* Mark position selected */
			    gameDisplay[from_row][from_col].setIcon(new ImageIcon("marked.gif"));
			    gameDisplay[from_row][from_col].paint(gameDisplay
                                                              [from_row][from_col].getGraphics());
			}
		    }
		    else {
			to_row = row;
			to_col = col;
		    }

		if (validPlay(row,col)) {
		    /* Valid play, mark it on the board */

		    if (numberEmptyTiles() > 1) {
			gameDisplay[row][col].setIcon(new ImageIcon("human.gif"));
			gameDisplay[row][col].paint(gameDisplay[row][col].getGraphics());
			game.storePlay(row,col,HUMAN);
		    }
		    else {
			gameDisplay[to_row][to_col].setIcon(new ImageIcon("human.gif"));
			gameDisplay[from_row][from_col].setIcon(new ImageIcon("empty.gif"));
			gameDisplay[to_row][to_col].paint(gameDisplay[to_row][to_col].getGraphics());
			gameDisplay[from_row][from_col].paint(gameDisplay[from_row][from_col].getGraphics());
			game.storePlay(to_row,to_col,HUMAN);
			game.storePlay(from_row,from_col,EMPTY);
		    }

		    if (game.wins(HUMAN)) endGame("Human wins"); 
		    else {
			if (game.isDraw(COMPUTER)) endGame("Game is a draw"); 
			else {
			    to_row = -1;
			    to_col = -1;
			    from_row = -1;
			    from_col = -1;  // Forget previous position selected by human player

			    Date d = new Date();
			    long start = d.getTime();
			    numCalls = 0;
			    isThinking = false;
			    pos = computerPlay(COMPUTER,-1,4,0);
			    if (isThinking) System.out.println("");
			    long end = d.getTime();
			    try {
				if (end-start < MIN_DELAY) 
				    Thread.sleep(MIN_DELAY-end+start);
			    }
			    catch (Exception e) {
				System.out.println("Something is wrong with timer");
			    }


			    if (numberEmptyTiles() > 1) {
				// Set down a new tile
				game.storePlay(pos.getRow(),pos.getCol(),COMPUTER);
				gameDisplay[pos.getRow()][pos.getCol()].setIcon(
						      new ImageIcon("computer.gif"));
				gameDisplay[pos.getRow()][pos.getCol()].paint(gameDisplay
					   [pos.getRow()][pos.getCol()].getGraphics());
			    }
			    else {
				// Shift tiles
				int empty_row = -1, empty_col = -1;

				// Find empty tile
				for (int i = 0; i < board_size; ++i)
				    for (int j = 0; j < board_size; ++j)
					if (game.tileIsEmpty(i,j)) {
					    empty_row = i;
					    empty_col = j;
					    break;
					}

				// Make the computer's move
				game.storePlay(pos.getRow(),pos.getCol(),EMPTY);
				game.storePlay(empty_row,empty_col,COMPUTER);
				gameDisplay[empty_row][empty_col].setIcon(
						      new ImageIcon("computer.gif"));
				gameDisplay[pos.getRow()][pos.getCol()].setIcon(
						      new ImageIcon("empty.gif"));
				gameDisplay[empty_row][empty_col].paint(gameDisplay
					   [empty_row][empty_col].getGraphics());
				gameDisplay[pos.getRow()][pos.getCol()].paint(gameDisplay
					   [pos.getRow()][pos.getCol()].getGraphics());
			    }
			    if (game.wins(COMPUTER)) endGame("Computer wins");
			    else if (game.isDraw(HUMAN)) endGame("Game is a draw");
			}
		    }
		}
		else 
		    if (to_row != -1 || from_row == -1) {
			if (from_row != -1) {
			    /* Mark selected position */
			    gameDisplay[from_row][from_col].setIcon(new ImageIcon("human.gif"));
			    gameDisplay[from_row][from_col].paint(gameDisplay
                                                              [from_row][from_col].getGraphics());
			}

			to_row = -1;        /* Forget invalid play */
			from_row = -1;
			to_col = -1;
			from_col = -1;
			System.out.println("Invalid play");
		    }

            }
        }


       /* --------------------------------------------- */
	private int numberEmptyTiles() {	  
       /* --------------------------------------------- */  

	    int emptytiles = 0;

	    for (int i = 0; i < board_size; i++) 
		for (int j = 0; j < board_size; j++)
		    if (game.tileIsEmpty(i,j)) ++ emptytiles;

	    return emptytiles;
	}

	/* Returns true if the human player selected a valid play and return false otherwise */
       /* --------------------------------------------- */
	private boolean validPlay(int row, int col) {	  
       /* --------------------------------------------- */  
	    if (numberEmptyTiles() > 1)
		if (game.tileIsEmpty(row,col)) return true;
		else return false;
	    else 
		if (to_row == -1 || from_row == -1) return false;
		else 
		    if ((game.tileIsHuman(from_row,from_col)) && (game.tileIsEmpty(to_row,to_col)))
			if (adjacent(from_row,from_col,to_row,to_col)) return true;
			else return false;
		    else 
			return false;
	}

       /* Returns true if the given positions are adjacent in the game board */
       /* ----------------------------------------------------------------- */
	private boolean adjacent (int row1, int col1, int row2, int col2) {
       /* ----------------------------------------------------------------- */
	    if (Math.abs(row1 - row2) <= 1 && Math.abs(col1 - col2) <= 1)
		return true;
	    else return false;
	}


	/* Explore the game tree and choose the best move for the computer */
       /* ----------------------------------------------------------------- */
	private PosPlay computerPlay(char symbol, int highest_score, 
                                     int lowest_score, int level) {
       /* ----------------------------------------------------------------- */

	char opponent;           // Opponent's symbol
        PosPlay reply;           // Opponent's best reply
 
        int bestRow = -1;
        int bestColumn = -1;     // Position of best play

        int value;
	int lookupVal;
	int row;
	int column;

	if (level == 0)   /* Create new hash table */
	    configurations = game.createDictionary();    

        if( symbol == COMPUTER ) {
            opponent = HUMAN; value = -1;
        }
        else {
            opponent = COMPUTER; value = 4;
        }

	if (++numCalls == THINKING) {
	    System.out.print("Thinking ..");
	    isThinking = true;
	}
	else if ((numCalls % THINKING) == 0) System.out.print(".");


	if (numberEmptyTiles() > 1) {
	  // Play into an empty position of the board
          for(row = 0; row < board_size; row++)
             for(column = 0; column < board_size; column++) {
                if(game.tileIsEmpty(row,column)) {     // Empty position
                    game.storePlay(row,column,symbol);   // Store next play
		    if (game.wins(symbol)||game.isDraw(opponent)||(level >= max_level))
                        // Game ending situation or max number of levels reached 
			reply = new PosPlay(game.evalBoard(symbol),row,column);
		    else {
			lookupVal = game.repeatedConfig(configurations);
			if (lookupVal != -1) 
			    reply = new PosPlay(lookupVal,row,column);
			else {
			    reply = computerPlay(opponent, highest_score, 
                                         lowest_score, level + 1);
			    game.insertConfig(configurations,reply.getScore());
			}
		    }
		    game.storePlay(row,column,EMPTY);
                    
		    if((symbol == COMPUTER && reply.getScore() > value) ||
		       (symbol == HUMAN && reply.getScore() < value)) {
			 bestRow = row; 
			 bestColumn = column;
			 value = reply.getScore();

			 /* Alpha/beta cut */
			 if (symbol == COMPUTER && value > highest_score) 
			     highest_score = value;
			 else if (symbol == HUMAN && value < lowest_score) 
			     lowest_score = value;

			 if (highest_score >= lowest_score) 
			     return new PosPlay(value, bestRow, bestColumn);
		    }
		    
		}
	     }
	  }
	else {
	    // Shift a tile into the empty spot
	    int empty_row=-1, empty_col=-1, 
		first_row=-1, last_row=-1, 
		first_col=-1, last_col=-1;

	    // Find position of empty spot
	    for (int i = 0; i < board_size; ++i) {
		for (int j = 0; j < board_size; ++j) 
		    if(game.tileIsEmpty(i,j)) {
			empty_row = i;
			empty_col = j;
			break;
		    }
	    }

	    // Consider moving tiles in positions adjacent to empty spot
	    if (empty_row > 0) first_row = empty_row - 1;
	    else first_row = empty_row;
	    if (empty_row == board_size - 1) last_row = empty_row;
	    else last_row = empty_row + 1;

	    if (empty_col > 0) first_col = empty_col - 1;
	    else first_col = empty_col;
	    if (empty_col == board_size - 1) last_col = empty_col;
	    else last_col = empty_col + 1;

	    // Check all positions adjacent to the empty spot
	    for (row = first_row; row <= last_row; ++row)
		for (column = first_col; column <= last_col; ++column) {
		    if ((symbol == COMPUTER && game.tileIsComputer(row,column))||
			(symbol == HUMAN && game.tileIsHuman(row,column))) {
			game.storePlay(row,column,EMPTY);   // Store next play
			game.storePlay(empty_row, empty_col,symbol);
		        if (game.wins(symbol)||game.isDraw(opponent)||(level >= max_level))
                        // Game ending situation or max number of levels reached 
			  reply = new PosPlay(game.evalBoard(symbol),row,column);
		        else {
			  lookupVal = game.repeatedConfig(configurations);
			  if (lookupVal != -1) 
			    reply = new PosPlay(lookupVal,row,column);
			  else {
			    reply = computerPlay(opponent, highest_score, 
                                         lowest_score, level + 1);
			    game.insertConfig(configurations,reply.getScore());
			  }
		        }
		        game.storePlay(row,column,symbol);
			game.storePlay(empty_row,empty_col,EMPTY);
                    
		        if((symbol == COMPUTER && reply.getScore() > value) ||
		           (symbol == HUMAN && reply.getScore() < value)) {
			  bestRow = row; 
			  bestColumn = column;
			  value = reply.getScore();

			  /* Alpha/beta cut */
			  if (symbol == COMPUTER && value > highest_score) 
			     highest_score = value;
			  else if (symbol == HUMAN && value < lowest_score) 
			     lowest_score = value;

			  if (highest_score >= lowest_score) 
			     return new PosPlay(value, bestRow, bestColumn);
		        }

		    }
		}
	}
        return new PosPlay(value, bestRow, bestColumn);
        }


	/* Prompt the user for a key to terminate the game */
       /* ---------------------------------- */
	private void endGame(String mssg) {
       /* ---------------------------------- */
	    System.out.println(mssg);
	    System.out.println("");
	    System.out.println("Click on board to terminate game");
	    game_ended = true;
	}

    }
}