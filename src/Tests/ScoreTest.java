package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import Controller.BoardController;
import Controller.DatabaseController;
import Model.Symbol;
import javafx.util.Pair;

class ScoreTest {
	
	private BoardController _boardCont;
	private DatabaseController _db;
						
	@Test
	void test() {
		
		_db = new DatabaseController();
		
		_boardCont = new BoardController();
		
		_boardCont.initializeTest();
				
		var letterStr = "               ";
		
		var letters = letterStr.toCharArray();
		
		if(letters.length != 15)
			fail("Row is not 15 long");
		
		var row = 1;
		var start = 1;
		var end = 2;
		
		addWordStartEnd(letters, start, end, "eb".toCharArray());
		
		placeWord(letters, row, start, end);
		
		var ebTest = _boardCont.getPlacedWordFromChars(letters, new Pair<Integer, Integer>(1,row), true, true);
		System.out.println(ebTest.getKey() + " " + ebTest.getValue());		
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
