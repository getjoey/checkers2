package com.joseph.boot.checkers;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CheckersModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private char[][] board = {{'-','W','-','W','-','W','-','W'},
			{'W','-','W','-','W','-','W','-'},
			{'-','W','-','W','-','W','-','W'},
			{'-','-','-','-','-','-','-','-'},
			{'-','-','-','-','-','-','-','-'},
			{'B','-','B','-','B','-','B','-'},
			{'-','B','-','B','-','B','-','B'},
			{'B','-','B','-','B','-','B','-'}};
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public char[][] getBoard() {
		return board;
	}
	public void setBoard(char[][] board) {
		this.board = board;
	}
	
}
