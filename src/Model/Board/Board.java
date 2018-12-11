package Model.Board;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;

public class Board {
	private HashMap<Pair<Integer, Integer>, PositionStatus> _positions;
	
	public Board() {
		_positions = new HashMap<Pair<Integer,Integer>, PositionStatus>();
		fillBoard();
	}
	
	public void updateStatus(Pair<Integer, Integer> cords, PositionStatus status) {
		_positions.put(cords, status);
	}
	
	public boolean canPlace(Pair<Integer, Integer> cords) {
		return _positions.get(cords) == PositionStatus.Open;
	}
	
	public ArrayList<Pair<Integer, Integer>> getOccupiedPositions(){
		var positions = new ArrayList<Pair<Integer, Integer>>();
		_positions.entrySet().stream()
				.filter(pair -> !canPlace(pair.getKey()))
				.forEach(pair -> positions.add(pair.getKey()));
		return positions;
	}
	
	private void fillBoard() {
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				_positions.put(new Pair<Integer, Integer>(i, j), PositionStatus.Open);
			}
		}
	}
}
