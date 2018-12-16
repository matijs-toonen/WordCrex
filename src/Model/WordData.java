package Model;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;

import javafx.util.Pair;

public class WordData {
	
	private String _word;
	private int _score;
	private HashMap<Integer, Pair<Character, Point>> _letters;
	private LinkedList<Integer> _letterIds;
	
	public WordData(String word, int score, HashMap<Integer, Pair<Character, Point>> letters)
	{
		_word = word;
		_score = score;
		_letters = letters;
		
		_letterIds = new LinkedList<Integer>();
		
		for(Integer id : _letters.keySet())
		{
			_letterIds.add(id);
		}
	}
	
	public String getWord()
	{
		return _word;
	}
	
	public int getScore()
	{
		return _score;
	}
	
	public HashMap<Integer, Pair<Character, Point>> getLetters()
	{
		return _letters;
	}
	
	public LinkedList<Integer> getLetterIds()
	{
		return _letterIds;
	}
	
	public boolean hasSameLetterIds(LinkedList<Integer> letterIds)
	{
		return _letterIds.equals(letterIds);
	}

}
