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
		
		_boardCont = new BoardController(new Game(0), null);
		
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
		
		placeWord(_letters, row, start, end, true);
		
		var ebTest = runTest(start, end, row, true);
		
		Assert.assertEquals(expectedScore, (int)ebTest);	
	}
	
	@Test
	void TestWord2() {
		
		init();
						
		var row = 7;
		var start = 11;
		var end = 14;
		
		var expectedScore = 204;
		
		var word = "quiz";
		
		addWordStartEnd(_letters, start, end, word.toCharArray());
		
		placeWord(_letters, row, start, end, true);
		
		var quizTest = runTest(start, end, row, true);
		
		Assert.assertEquals(expectedScore, (int)quizTest);	
	}
	
	@Test
	int TestWord3() {
								
		var row = 10;
		var start = 1;
		var end = 4;
		
		var expectedScore = 51;
		
		var word = "brie";
		
		addWordStartEnd(_letters, start, end, word.toCharArray());
		
		placeWord(_letters, row, start, end, true);
		
		var brieTest = runTest(start, end, row, true);
		
		Assert.assertEquals(expectedScore, (int)brieTest);	
		
		return (int)brieTest;
	}
	
	@Test
	int TestWord4() {
								
		var row = 4;
		var start = 8;
		var end = 10;
		
		var expectedScore = 19;
		
		var word = "wie";
		
		addWordStartEnd(_letters, start, end, word.toCharArray());
				
		placeWord(_letters, row, start, end, false);
		
		var wieTest = runTest(start, end, row, false);
	
		Assert.assertEquals(expectedScore, (int)wieTest);	
		
		return (int)wieTest;
	}
		
	@Test
	void Test2Words() {
		
		var expectedScore = 70;
		
		init();
		
		Assert.assertEquals(expectedScore, TestWord3() + TestWord4());	
	}
	
	int runTest(int start, int end, int colro, boolean horizontal)
	{
		return _boardCont.calcScore(colro, start, end, horizontal, true);
	}
	
	void addWordStartEnd(char[] arr, int start, int end, char[] word)
	{
		int wordIndex = 0;
		for(int i = start; i <= end; i++)
		{
			arr[i] = word[wordIndex];
			wordIndex++;
		}
	}
	
	void placeWord(char[] arr, int row, int start, int end, boolean horizontal)
	{
		for(int i = start; i <= end; i++)
		{
			try 
			{
				var letterValue = _db.SelectCount(String.format("SELECT `value` FROM symbol WHERE symbol = '%s'", arr[i]));
				
				if(horizontal)
					_boardCont.placeTest(new Point(i,row), new Symbol(arr[i], letterValue));
				else
					_boardCont.placeTest(new Point(row,i), new Symbol(arr[i], letterValue));
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
			
		}
		
	}
}
