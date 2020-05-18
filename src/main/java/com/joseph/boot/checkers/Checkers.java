package com.joseph.boot.checkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Checkers {


	private String[][] board = {
			{"-","W","-","W","-","W","-","W"},
			{"W","-","W","-","W","-","W","-"},
			{"-","W","-","W","-","W","-","W"},
			{"-","-","-","-","-","-","-","-"},
			{"-","-","-","-","-","-","-","-"},
			{"B","-","KB","-","B","-","B","-"},
			{"-","B","-","B","-","B","-","B"},
			{"B","-","B","-","B","-","B","-"}};

	private ArrayList<Integer> atePiece = new ArrayList<>();
	
	public Checkers() {

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
		if(move == null) {
			System.out.println("no good");
			return false;
		}
		board[y2][x2] = board[y1][x1];
		board[y1][x1] = "-";
		
		if(move.getA1() != null && move.getA2() != null) {
			board[move.a2][move.a1] = "-";
		}

		return true;
	}
	
	public void doAiTurn() {
		List<Move> allMoves = getAllMoves(board, "W");
		if(allMoves.size() > 0) {
			System.out.println("Doing a move!");
			Move move = allMoves.get(0);
			add(move.x1,move.y1, move.x2, move.y2);
		}
		else {
			System.out.println("no moves left");
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
					//return [true, "ate", [yMid, xMid], valueGainedFromRegularPiece];
				}
				else if(boardToUse[yMid][xMid].equals("KB")) {
					return new Move(x1,y1,x2,y2,xMid,yMid);
					//return [true, "ate", [yMid, xMid], valueGainedFromKingPiece];
				}
			}
			else if(turnColor.equals("B") && yDif == -2 && Math.abs(xDif) == 2) {
				if(boardToUse[yMid][xMid].equals("W")) {
					return new Move(x1,y1,x2,y2,xMid,yMid);
					//return [true, "ate", [yMid, xMid], valueGainedFromRegularPiece];
				}
				else if(boardToUse[yMid][xMid].equals("KW")) {
					return new Move(x1,y1,x2,y2,xMid,yMid);
					//return [true, "ate", [yMid, xMid], valueGainedFromKingPiece];
				}
			}
		}
		else {
			//king piece movement

			if(Math.abs(yDif) == 1 && Math.abs(xDif) == 1) {
				return new Move(x1,y1,x2,y2);
				//return [true, "", null, 0];
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
		
		
		
	}
	
	 public List<Move> getAllMoves(String[][] boardToUse, String colorToCheck) {
         List<Move> moveList = new ArrayList<>();
         
         //go through all pieces.. if this is one of your pieces. find every move it can do.
         //if its a move that eats +1, if king +3; otherwise 0.
         //TODO
         
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




}
