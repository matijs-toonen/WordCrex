package Model.Board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.util.Pair;

public class Board {
	private HashMap<Pair<Integer, Integer>, PositionStatus> _positions;
	private ArrayList<Pair<Integer, Integer>> _occupiedPositions;
	
	public Board() {
		_positions = new HashMap<Pair<Integer,Integer>, PositionStatus>();
		_occupiedPositions = new ArrayList<Pair<Integer, Integer>>();
		fillBoard();
	}
	
	public void updateStatus(Pair<Integer, Integer> cords, PositionStatus status) {
		
		if(status == PositionStatus.Open)
			_occupiedPositions.remove(cords);
		else
			_occupiedPositions.add(cords);
		
		_positions.put(cords, status);
	}
	
	public ArrayList<Pair<Integer,Integer>> getOccupiedPositions()
	{
		return _occupiedPositions;
	}
	
	public boolean canPlace(Pair<Integer, Integer> cords) {
		return _positions.get(cords) == PositionStatus.Open;
	}
	
	private void fillBoard() {
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				_positions.put(new Pair<Integer, Integer>(i, j), PositionStatus.Open);
			}
		}
	}
}
