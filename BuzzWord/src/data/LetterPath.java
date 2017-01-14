package data;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * @author Tejas
 */
public class LetterPath {
	private Stack path;
	private boolean[][] visitedLetters;
	
	public LetterPath(){
		visitedLetters = new boolean[4][4];
		path = new Stack();
	}
	
	public void addLetter(int x, int y){
		path.push(new Pair<>(x, y));
		visitedLetters[x][y] = true;
	}
	public Pair peek(){
		return ((Pair) path.peek());
	}



	public ArrayList<Pair> getPath() {
		ArrayList<Pair> pairs = new ArrayList<>();
		Stack temp = ((Stack) path.clone());
		while(!temp.empty()){
			pairs.add(((Pair) temp.pop()));
		}
		return pairs;
	}

	public boolean hasVisited(int x, int y){
		return visitedLetters[x][y];
	}

	public void setPath(Stack path) {
		this.path = path;
	}

	public void setVisitedLetters(boolean[][] visitedLetters) {
		this.visitedLetters = visitedLetters;
	}

	@Override
	public LetterPath clone(){
		Stack newPath = new Stack();
		Stack temp = ((Stack) path.clone());
		Stack holding = new Stack();
		while(!temp.empty()){
			Pair tempPair = ((Pair) temp.pop());
			Pair newPair = new Pair<>(tempPair.getKey(), tempPair.getValue());
			holding.push(newPair);
		}
		while(!holding.empty()){
			newPath.push(holding.pop());
		}

		boolean[][] visited = new boolean[4][4];
		for(int i = 0; i < visited.length; i++){
			visited[i] = Arrays.copyOf(visitedLetters[i], 4);
		}

		LetterPath newLetterPath = new LetterPath();
		newLetterPath.setPath(newPath);
		newLetterPath.setVisitedLetters(visited);

		return newLetterPath;
	}
}
