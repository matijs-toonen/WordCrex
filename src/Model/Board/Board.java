package Model.Board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
		if(_positions.containsKey(cords))
			return _positions.get(cords) == PositionStatus.Open;
		else
			return false;
	}
	
	public boolean placedConnected(Point cord)
	{
		var connectedCords = new ArrayList<Point>();
		
		connectedCords.add(new Point((int)cord.getX() -1, (int)cord.getY()));
		connectedCords.add(new Point((int)cord.getX() +1, (int)cord.getY()));
		
		connectedCords.add(new Point((int)cord.getX(), (int)cord.getY() -1));
		connectedCords.add(new Point((int)cord.getX(), (int)cord.getY() +1));
		
		if(connectedCords.size() != 0)
		{
			for(Iterator<Point> iter = connectedCords.iterator(); iter.hasNext();)
			{
				var coordinate = iter.next();
				
				if(canPlace(coordinate) || !_positions.containsKey(coordinate))
					iter.remove();
			}
		}
		
		return connectedCords.size() != 0;
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
