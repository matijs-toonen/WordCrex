package Model.Board;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;

public class Board {
	private HashMap<Point, PositionStatus> _positions;
	private ArrayList<Pair<Integer, Integer>> _occupiedPositions;
	
	public Board() {
		_positions = new HashMap<Point, PositionStatus>();
		_occupiedPositions = new ArrayList<Pair<Integer, Integer>>();
		fillBoard();
	}
	
	public void updateStatus(Point cords, PositionStatus status) {
		
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
	
	public boolean canPlace(Point cords) {
		return _positions.get(cords) == PositionStatus.Open;
	}
	
	public ArrayList<Point> getOccupiedPositions(){
		var positions = new ArrayList<Point>();
		_positions.entrySet().stream()
				.filter(pair -> !canPlace(pair.getKey()))
				.forEach(pair -> positions.add(pair.getKey()));
		return positions;
	}
	
	private void fillBoard() {
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				_positions.put(new Point(i, j), PositionStatus.Open);
			}
		}
	}
}
