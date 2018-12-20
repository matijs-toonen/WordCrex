package Model.Board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Board {
	private HashMap<Point, PositionStatus> _positions;
	private Point _middle;
	private int _connected;
	
	public Board() {
		_positions = new HashMap<Point, PositionStatus>();
		_middle = new Point(7,7);
		fillBoard();
	}
	
	public Point getMiddle()
	{
		return _middle;
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
	
	private boolean placedConnected(Point cord)
	{
		return getAdjacentCords(cord).size() != 0;
	}
	
	public ArrayList<Point> getOccupiedPositions(){
		var positions = new ArrayList<Point>();
		_positions.entrySet().stream()
				.filter(pair -> !canPlace(pair.getKey()))
				.forEach(pair -> positions.add(pair.getKey()));
		return positions;
	}
	
	public boolean allChainedToMiddle()
	{
		var occPos = getOccupiedPositions();
		var reqConn = occPos.size() -1;
		_connected = 0;
				
		for(var pos : occPos)
		{
			if(!placedConnected(pos))
				return false;
			
			if(pos.equals(_middle))
				continue;
			
			LinkedList<Point> visited = new LinkedList<Point>();
			visited.add(pos);
			
			findPath(visited, pos);
		}
		
		return _connected == reqConn;
	}
				
	private void findPath(LinkedList<Point> visited, Point start)
    {
        LinkedList<Point> nodes = getAdjacentCords(visited.getLast());
 
        for (Point node : nodes)
        {
            if (visited.contains(node))
                continue;
            
            if (node.equals(_middle))
            {
                visited.add(node);
                _connected++;
                break;
            }
        }
 
        for (Point node : nodes)
        {
            if (visited.contains(node) || node.equals(_middle))
                continue;
            
            visited.addLast(node);
            findPath(visited, start);
        }
    }
		
	public LinkedList<Point> getAdjacentCords(Point cord)
	{
		var connectedCords = new LinkedList<Point>();
		
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
		
		return connectedCords;
	}
	
	private void fillBoard() {
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				_positions.put(new Point(i, j), PositionStatus.Open);
			}
		}
	}
}
