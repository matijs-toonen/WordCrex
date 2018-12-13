package Model.Board;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;

public class Board {
	private HashMap<Point, PositionStatus> _positions;
	
	public Board() {
		_positions = new HashMap<Point, PositionStatus>();
		fillBoard();
	}
	
	public void updateStatus(Point cords, PositionStatus status) {		
		_positions.put(cords, status);
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
