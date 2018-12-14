package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import Controller.BoardController;
import Controller.DatabaseController;
import Model.Game;
import Model.Symbol;
import javafx.util.Pair;
import junit.framework.Assert;

class ScoreTest {
	
	private BoardController _boardCont;
	private DatabaseController _db;
	private char[] _letters;
	
	void init()
	{
		_db = new DatabaseController();
		
		_boardCont = new BoardController(new Game(0));
		
		_boardCont.initializeTest();
		
		var letterStr = "               ";
		
		_letters = letterStr.toCharArray();
		
		if(_letters.length != 15)
			fail("Row is not 15 long");
	}
						
	@Test
	void TestWord1() {
		
		init();
						
		var row = 1;
		var start = 1;
		var end = 2;
		
		var expectedScore = 7;
		
		var word = "eb";
		
		addWordStartEnd(_letters, start, end, word.toCharArray());
		
		placeWord(_letters, row, start, end);
		
		var ebTest = runTest(start, row);
		
		Assert.assertEquals(word, ebTest.getKey());
		Assert.assertEquals(expectedScore, (int)ebTest.getValue());	
	}
	
	// Always horizontal
	Pair<String, Integer> runTest(int start, int row)
	{
		return _boardCont.getPlacedWordFromChars(_letters, new Pair<Integer, Integer>(start,row), true, true);
	}
	
	void addWordStartEnd(char[] arr, int start, int end, char[] word)
	{		
		int wordIndex = 0;
		for(int i = start; i <= end; i++)
		{
			if(i == start)
			{
				arr[i] = word[wordIndex];
				wordIndex += 1;
			}
			else
				arr[i] = word[wordIndex];
		}
	}
	
	void placeWord(char[] arr, int row, int start, int end)
	{
		for(int i = start; i <= end; i++)
		{
			try 
			{
				var letterValue = _db.SelectCount(String.format("SELECT `value` FROM symbol WHERE symbol = '%s'", arr[i]));
								
				_boardCont.placeTest(new Point(i,row), new Symbol(arr[i], letterValue));
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}