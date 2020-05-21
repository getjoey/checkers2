package com.joseph.boot.checkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Checkers {


	private final int MAX_DEPTH = 6;
	
	private String[][] initialBoard = {
			{"-","W","-","W","-","W","-","W"},
			{"W","-","W","-","W","-","W","-"},
			{"-","W","-","W","-","W","-","W"},
			{"-","-","-","-","-","-","-","-"},
			{"-","-","-","-","-","-","-","-"},
			{"B","-","B","-","B","-","B","-"},
			{"-","B","-","B","-","B","-","B"},
			{"B","-","B","-","B","-","B","-"}};
	
	private String[][] board;
	private Move aiMoveToDo = null;	
	
	private List<Move> movesItCanDo;
	
	public Checkers() {
		resetBoard();
	}

	public void resetBoard() {
		board = new String[8][8];
	    for(int i=0; i<8; i++) {
	    	for(int j=0; j<8; j++) {
	    		board[i][j] = initialBoard[i][j];
		    }	
	    }
	}
	
	public String[][] getBoard() {
		return board;
	}

	public void print() {
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				System.out.print(board[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}


	public boolean add(int x1, int y1, int x2, int y2) {
		Move move = isMoveValid(x1,y1,x2,y2,board);
		System.out.println("Doing a move! "+move);
		if(move == null) {
			System.out.println("no good");
			return false;
		}
		String piece = board[y1][x1];
		if(piece.equals("W") && y2 == 7) {
			piece = "KW";
		}
		else if(piece.equals("B") && y2 == 0) {
			piece = "KB";
		}
		board[y2][x2] = piece;
		board[y1][x1] = "-";
		
		if(move.getA1() != null && move.getA2() != null) {
			board[move.a2][move.a1] = "-";
		}
		
		String color = piece;
		if(color.length() == 2) {
			color = ""+color.charAt(1);
		}
		
		movesItCanDo = shouldGoAgain(board, move, color);
		return true;
	}
	
	
	public List<Move> shouldGoAgain(String[][] boardToUse, Move moveJustDid, String turnColor) {
		List<Move> moveList = getAllMoves(boardToUse, turnColor);
		List<Move> movesItCanDo = new ArrayList<>();
		
		for(Move move : moveList) {
			if(moveJustDid.getA1() != null && moveJustDid.x2 == move.x1 && moveJustDid.y2 == move.y1 && move.getA1() != null) {
				movesItCanDo.add(move);
			}
		}
		
		return movesItCanDo;
	}
	
	public void doBestMoveAI() {
		
	    String[][] boardCopy = new String[8][8];
	    for(int i=0; i<8; i++) {
	    	for(int j=0; j<8; j++) {
	    		boardCopy[i][j] = board[i][j];
		    }	
	    }
	    
	    //initial minimax to decide starting move
	    miniMax(boardCopy, 0, true,  0, 0, "W");
	    
	    if(aiMoveToDo != null) {
	    	add(aiMoveToDo.x1,aiMoveToDo.y1, aiMoveToDo.x2,aiMoveToDo.y2);
	    	aiMoveToDo = null;
	    }
	    else {
	    	System.out.println("NO MOVE FOR AI!!!");
	    }
	}
	
	public String getOtherPlayerTurn(String currentPlayerTurn) {
		if(currentPlayerTurn.equals("W")) {
			return "B";
		}
		return "W";
	}
	
	public int miniMax(String[][] boardCopy, int depth, boolean isMaximizingPlayer, int alpha, int beta, String turnColor) {
		
		if(depth == MAX_DEPTH) { //or isgameComplete
			//return board evaluation
			int result = evaluateBoard(boardCopy);
			return result;
		}
		
		if(isMaximizingPlayer) {
			int maxEval = -1000000;
			List<Move> moveList = getAllMoves(boardCopy, turnColor);
			
			for(Move move : moveList) {
				String pieceType = boardCopy[move.y1][move.x1];
				String color = pieceType.length() == 2 ? ""+pieceType.charAt(1) : pieceType;
				String eatenPieceType = null;
				boolean wasTurnedKing = false;
				
				//do move
				if(pieceType.equals("W") && move.y2 == 7) {
					pieceType = "KW";
					wasTurnedKing = true;
				}
				else if(pieceType.equals("B") && move.y2 == 0) {
					pieceType = "KB";
					wasTurnedKing = true;
				}
				
				
				boardCopy[move.y1][move.x1] = "-"; //piece has moved and no longer there.
				boardCopy[move.y2][move.x2] = pieceType; //piece is now here.
				if (move.getA1() != null && move.getA2() != null) {
					eatenPieceType = boardCopy[move.getA2()][move.getA1()];
					boardCopy[move.getA2()][move.getA1()] = "-";// remove eaten piece
				}
				
				//List<Move> movesItCanDoAgain = shouldGoAgain(board, move, color);
				
				
				int score = miniMax(boardCopy, depth +1, false, alpha, beta, getOtherPlayerTurn(turnColor));
				
				//undo move
				if(wasTurnedKing) {
					pieceType = ""+pieceType.charAt(1);
				}
				boardCopy[move.y1][move.x1] = pieceType; 
				boardCopy[move.y2][move.x2] = "-"; 
				if(eatenPieceType != null) {
					boardCopy[move.getA2()][move.getA1()] = eatenPieceType;
				}
				
				//conclude
				if(score > maxEval) {
					maxEval = score;
					if(depth == 0) {
						aiMoveToDo = move;
					}
				}
				 if (alpha > maxEval) {
                     alpha = maxEval;
                 }
                 if (beta <= alpha) {
                     break;
                 }
			}
			return maxEval;
		}
		else {
			//minimizingPlayer
			
			int minEval = 1000000;
			List<Move> moveList = getAllMoves(boardCopy, turnColor);
			
			for(Move move : moveList) {
				String pieceType = boardCopy[move.y1][move.x1];
				String eatenPieceType = null;
				boolean wasTurnedKing = false;
				
				//do move
				if(pieceType.equals("W") && move.y2 == 7) {
					pieceType = "KW";
					wasTurnedKing = true;
				}
				else if(pieceType.equals("B") && move.y2 == 0) {
					pieceType = "KB";
					wasTurnedKing = true;
				}
				
				boardCopy[move.y1][move.x1] = "-"; //piece has moved and no longer there.
				boardCopy[move.y2][move.x2] = pieceType; //piece is now here.
				if (move.getA1() != null && move.getA2() != null) {
					eatenPieceType = boardCopy[move.getA2()][move.getA1()];
					boardCopy[move.getA2()][move.getA1()] = "-";// remove eaten piece
				}
				
				int score = miniMax(boardCopy, depth +1, true, alpha, beta, getOtherPlayerTurn(turnColor));
				
				//undo move
				if(wasTurnedKing) {
					pieceType = ""+pieceType.charAt(1);
				}
				boardCopy[move.y1][move.x1] = pieceType; 
				boardCopy[move.y2][move.x2] = "-"; 
				if(eatenPieceType != null) {
					boardCopy[move.getA2()][move.getA1()] = eatenPieceType;
				}
				
				//conclude
				if(score < minEval) {
					minEval = score;
				}
				if (beta < minEval) {
					beta = minEval;
				}
				if (beta <= alpha) {
					break;
				}
			}
			return minEval;
		}
	}

	public Move isMoveValid(int x1, int y1, int x2, int y2, String[][] boardToUse) {

		String valueOfPlaceSelected = boardToUse[y1][x1];
		String valueOfPlaceToBe = boardToUse[y2][x2];

		if(valueOfPlaceToBe != "-") {
			return null;
		}

		boolean isRegularPiece = valueOfPlaceSelected.length() == 1;
		String turnColor = valueOfPlaceSelected;
		if(turnColor.length() == 2) {
			turnColor = ""+ turnColor.charAt(1);
		}

		int xDif = x2-x1;
		int yDif = y2-y1;
		int xMid = x1 + xDif/2;
		int yMid = y1 + yDif/2;

		if(isRegularPiece) {
			if(turnColor.equals("W") && yDif == 1 && Math.abs(xDif) == 1) {
				return new Move(x1,y1,x2,y2);
			}
			else if (turnColor.equals("B") && yDif == -1 && Math.abs(xDif) == 1) {
				return new Move(x1,y1,x2,y2);
			}
			//killing move check
			if(turnColor.equals("W") && yDif == 2 && Math.abs(xDif) == 2) {
				//check space in between
				if(boardToUse[yMid][xMid].equals("B")) {
					return new Move(x1,y1,x2,y2,xMid,yMid);
				}
				else if(boardToUse[yMid][xMid].equals("KB")) {
					return new Move(x1,y1,x2,y2,xMid,yMid);
				}
			}
			else if(turnColor.equals("B") && yDif == -2 && Math.abs(xDif) == 2) {
				if(boardToUse[yMid][xMid].equals("W")) {
					return new Move(x1,y1,x2,y2,xMid,yMid);
				}
				else if(boardToUse[yMid][xMid].equals("KW")) {
					return new Move(x1,y1,x2,y2,xMid,yMid);
				}
			}
		}
		else {
			//king piece movement

			if(Math.abs(yDif) == 1 && Math.abs(xDif) == 1) {
				return new Move(x1,y1,x2,y2);
			}
			//killing move check
			if(Math.abs(yDif) == 2 && Math.abs(xDif) == 2) {
				if(turnColor.equals("B")) {
					if(boardToUse[yMid][xMid].equals("W")) {
						return new Move(x1,y1,x2,y2,xMid,yMid);
					}
					else if(boardToUse[yMid][xMid].equals("KW")) {
						return new Move(x1,y1,x2,y2,xMid,yMid);
					}
				}
				else { //white turn
					if(boardToUse[yMid][xMid].equals("B")) {
						return new Move(x1,y1,x2,y2,xMid,yMid);
					}
					else if(boardToUse[yMid][xMid].equals("KB")) {
						return new Move(x1,y1,x2,y2,xMid,yMid);
					}
				}
			}

		}

		return null;
	}
	
	public class Move {
		int x1;
		int y1;
		int x2;
		int y2;
		
		Integer a1;
		Integer a2;
		
		public Move(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		public Move(int x1, int y1, int x2, int y2, int a1, int a2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.a1 = a1;
			this.a2 = a2;
		}
		public Integer getA1() {
			return a1;
		}
		public Integer getA2() {
			return a2;
		}
		@Override
		public String toString() {
			return "Move [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + ", a1=" + a1 + ", a2=" + a2 + "]";
		}
		
	}
	
	 public List<Move> getAllMoves(String[][] boardToUse, String colorToCheck) {
         List<Move> moveList = new ArrayList<>();
         
         //a single move will contain its [currentPos,[all possible positions it can go to]]
         for(int i=0; i<8; i++) {
             for(int j=0; j<8; j++) {

                 String piece = boardToUse[i][j];
                 int[] currPosDex = {j,i};
                 boolean isKing = piece.length() == 2;
                 if(isKing) {
                     piece = ""+piece.charAt(1);
                 }
                 if(piece.equals(colorToCheck)) {
                     
                	 int incPosXToBe = i+1;
                     int decPosXToBe = i-1;
                     int incPosYToBe = j+1;
                     int decPosYToBe = j-1;
                     int incJumpPosXToBe = i+2;
                     int decJumpPosXToBe = i-2;
                     int incJumpPosYToBe = j+2;
                     int decJumpPosYToBe = j-2;

                     //right 1 up 1 
                     if(incPosXToBe <= 7 && incPosYToBe <= 7) {
                    	 Move move = isMoveValid(currPosDex[0], currPosDex[1], incPosYToBe, incPosXToBe, boardToUse);
                         if(move != null) {
                        	 moveList.add(move);
                         }
                     }
                    
                     //left 1 up 1 
                     if(decPosXToBe >=0 && incPosYToBe <= 7) {
                    	 Move move = isMoveValid(currPosDex[0], currPosDex[1], incPosYToBe, decPosXToBe, boardToUse);
                    	 if(move != null) {
                        	 moveList.add(move);
                         }
                     }
                     
                     //right 1 down 1 
                     if(incPosXToBe <= 7 && decPosYToBe >= 0) {
                    	 Move move = isMoveValid(currPosDex[0], currPosDex[1], decPosYToBe, incPosXToBe, boardToUse);
                    	 if(move != null) {
                        	 moveList.add(move);
                         }
                     }
                     //left 1 down 1 
                     if(decPosXToBe >= 0 && decPosYToBe >= 0) {
                    	 Move move = isMoveValid(currPosDex[0], currPosDex[1], decPosYToBe, decPosXToBe, boardToUse);
                    	 if(move != null) {
                        	 moveList.add(move);
                         }
                     }
                     
                     //2move ate
                      //right 2 up 2 
                      if(incJumpPosXToBe <= 7 && incJumpPosYToBe <= 7) {
                    	  Move move = isMoveValid(currPosDex[0], currPosDex[1], incJumpPosYToBe, incJumpPosXToBe, boardToUse);
                    	  if(move != null) {
                         	 moveList.add(move);
                          }
                     }
                    
                     //left 2 up 2 
                     if(decJumpPosXToBe >=0 && incJumpPosYToBe <= 7) {
                    	 Move move = isMoveValid(currPosDex[0], currPosDex[1], incJumpPosYToBe, decJumpPosXToBe, boardToUse);
                    	 if(move != null) {
                        	 moveList.add(move);
                         }
                     }
                     //right 2 down 2 
                     if(incJumpPosXToBe <= 7 && decJumpPosYToBe >= 0) {
                    	 Move move = isMoveValid(currPosDex[0], currPosDex[1], decJumpPosYToBe, incJumpPosXToBe, boardToUse);
                    			 if(move != null) {
                                	 moveList.add(move);
                                 }
                     }
                     //left 2 down 2 
                     if(decJumpPosXToBe >= 0 && decJumpPosYToBe >= 0) {
                    	 Move move = isMoveValid(currPosDex[0], currPosDex[1], decJumpPosYToBe, decJumpPosXToBe, boardToUse);
                    			 if(move != null) {
                                	 moveList.add(move);
                                 }
                     }
                     

                 }
             } 
         }
         return moveList;
     }

	 
	 //returns how many more pieces white has, king coutns as 2.
	 public int evaluateBoard(String[][] boardToEvaluate) {
         int piecesW = 0;
         int piecesB = 0;
         
         for(int i=0; i<8; i++) {
             for(int j=0; j<8; j++) {

                 String piece = boardToEvaluate[i][j];
                 int mod = 0;
                 if(piece.length() == 2) { //king
                     piece = ""+piece.charAt(1);
                     mod+=1;
                 }
                 if(piece.equals("W")) {
                     piecesW = piecesW + 1 + mod;
                 }
                 else if(piece.equals("B")){
                     piecesB = piecesB + 1 + mod;
                 }
             }
         }
         return piecesW-piecesB;
     }



}
