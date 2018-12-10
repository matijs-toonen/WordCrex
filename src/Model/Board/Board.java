package Model.Board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
	
	public boolean areConnected(ArrayList<Pair<Integer, Integer>> cords)
	{		
		for(int i = 0; i < cords.size(); i++)
		{ 
			var connectedRight = i+1 < cords.size() ? areConnected(cords.get(i), cords.get(i+1)) : true;
			var connectedLeft = i-1 >= 0 ? areConnected(cords.get(i), cords.get(i-1)) : true;
			
			if(!connectedRight || !connectedLeft)
				return false;
		}
	
		return true;
	}
	
	private boolean areConnected(Pair<Integer, Integer> cordA, Pair<Integer, Integer> cordB)
	{
		var connectedCords = getConnectedCords(cordA);
		for(var connectedCoordinate : connectedCords)
		{
			if(connectedCoordinate.equals(cordB))
				return true;
		}
	
		return false;
	}
	
	private ArrayList<Pair<Integer, Integer>> getConnectedCords(Pair<Integer, Integer> cord)
	{
		ArrayList<Pair<Integer, Integer>> connectedCords = new ArrayList<Pair<Integer, Integer>>();
		connectedCords.add(new Pair<>(cord.getKey() -1, cord.getValue()));
		connectedCords.add(new Pair<>(cord.getKey(), cord.getValue() -1));
		connectedCords.add(new Pair<>(cord.getKey() +1, cord.getValue()));
		connectedCords.add(new Pair<>(cord.getKey(), cord.getValue() +1));
		
		for(Iterator<Pair<Integer, Integer>> iterator = connectedCords.iterator(); iterator.hasNext();)
		{	
			var coordinate = iterator.next();
			if(_positions.get(coordinate) == null)
				iterator.remove();
		}
		
		return connectedCords;
	}
	
	private void fillBoard() {
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				_positions.put(new Pair<Integer, Integer>(i, j), PositionStatus.Open);
			}
		}
	}
}
