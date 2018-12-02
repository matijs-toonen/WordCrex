package Model;

public class Tile {
	private int _x;
	private int _y;
	private TileType _tileType;
	
	public Tile(int x, int y) {
		_x = x;
		_y = y;
	}
	
	public Tile(int x, int y, TileType tileType) {
		this(x, y);
		_tileType = tileType;
	}
}
