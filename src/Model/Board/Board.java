package Model.Board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
		
		if(_positions.containsKey(cords))
			return _positions.get(cords) == PositionStatus.Open;
		else
			return false;
	}
	
	public boolean placedConnected(Pair<Integer, Integer> cord)
	{
		var connectedCords = new ArrayList<Pair<Integer,Integer>>();
		
		connectedCords.add(new Pair<Integer,Integer>(cord.getKey() -1, cord.getValue()));
		connectedCords.add(new Pair<Integer,Integer>(cord.getKey() +1, cord.getValue()));
		
		connectedCords.add(new Pair<Integer,Integer>(cord.getKey(), cord.getValue() -1));
		connectedCords.add(new Pair<Integer,Integer>(cord.getKey(), cord.getValue() +1));
		
		if(connectedCords.size() != 0)
		{
			for(Iterator<Pair<Integer, Integer>> iter = connectedCords.iterator(); iter.hasNext();)
			{
				var coordinate = iter.next();
				
				System.out.println(coordinate + " " + canPlace(coordinate));
				
				if(canPlace(coordinate) || !_positions.containsKey(coordinate))
					iter.remove();
			}
		}
		
		return connectedCords.size() != 0;
	}
	
	private void fillBoard() {
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				_positions.put(new Pair<Integer, Integer>(i, j), PositionStatus.Open);
			}
		}
	}
}
