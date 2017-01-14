package data;

import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * @author Tejas
 */
public class PathList {
	private ArrayList<LetterPath> list;

	public PathList(){
		list = new ArrayList<>();
	}

	public void reset(){
		list = new ArrayList<>();
	}

	public void startList(int x, int y){
		LetterPath newPath = new LetterPath();
		newPath.addLetter(x, y);
		list.add(newPath);
	}

	public boolean isEmpty(){
		return list.isEmpty();
	}

	public ArrayList<LetterPath> getList(){
		return list;
	}

	public boolean searchLetter(String letter, Text[][] grid) {
		boolean letterFound = false;
		ArrayList<LetterPath> deletePaths = new ArrayList<>();
		int startingSize = list.size();
		LetterPath lastPath = list.get(startingSize - 1);
		for (int i = 0; i < list.size(); i++) {
			LetterPath path = list.get(i);

			letter = letter.toUpperCase();
			int startingX = ((Integer) path.peek().getKey());
			int startingY = ((Integer) path.peek().getValue());

			if ((startingX + 1) < 4 && (startingX + 1) >= 0 && startingY < 4 && startingY >= 0) {
				if (grid[startingX + 1][startingY].getText().toUpperCase().equals(letter) && !path.hasVisited(startingX + 1, startingY)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX + 1, startingY);
					list.add(newPath);
				}
			}
			if ((startingX - 1) < 4 && (startingX - 1) >= 0 && startingY < 4 && startingY >= 0) {
				if (grid[startingX - 1][startingY].getText().toUpperCase().equals(letter) && !path.hasVisited(startingX - 1, startingY)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX - 1, startingY);
					list.add(newPath);
				}
			}
			if (startingX < 4 && startingX >= 0 && (startingY + 1) < 4 && (startingY + 1) >= 0) {
				if (grid[startingX][startingY + 1].getText().toUpperCase().equals(letter) && !path.hasVisited(startingX, startingY + 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX, startingY + 1);
					list.add(newPath);
				}
			}
			if (startingX < 4 && startingX >= 0 && (startingY - 1) < 4 && (startingY - 1) >= 0) {
				if (grid[startingX][startingY - 1].getText().toUpperCase().equals(letter) && !path.hasVisited(startingX, startingY - 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX, startingY - 1);
					list.add(newPath);
				}
			}
			if ((startingX + 1) < 4 && (startingX + 1) >= 0 && (startingY + 1) < 4 && (startingY + 1) >= 0) {
				if (grid[startingX + 1][startingY + 1].getText().toUpperCase().equals(letter) && !path.hasVisited(startingX + 1, startingY + 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX + 1, startingY + 1);
					list.add(newPath);
				}
			}
			if ((startingX + 1) < 4 && (startingX + 1) >= 0 && (startingY - 1) < 4 && (startingY - 1) >= 0) {
				if (grid[startingX + 1][startingY - 1].getText().toUpperCase().equals(letter) && !path.hasVisited(startingX + 1, startingY - 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX + 1, startingY - 1);
					list.add(newPath);
				}
			}
			if ((startingX - 1) < 4 && (startingX - 1) >= 0 && (startingY - 1) < 4 && (startingY - 1) >= 0) {
				if (grid[startingX - 1][startingY - 1].getText().toUpperCase().equals(letter) && !path.hasVisited(startingX - 1, startingY - 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX - 1, startingY - 1);
					list.add(newPath);
				}
			}
			if ((startingX - 1) < 4 && (startingX - 1) >= 0 && (startingY + 1) < 4 && (startingY + 1) >= 0) {
				if (grid[startingX - 1][startingY + 1].getText().toUpperCase().equals(letter) && !path.hasVisited(startingX - 1, startingY + 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX - 1, startingY + 1);
					list.add(newPath);
				}
			}
			deletePaths.add(path);
			if(path == lastPath)
				break;
		}

		for(LetterPath path : deletePaths){
			list.remove(path);
		}

		return letterFound;
	}

	public boolean searchLetter(String letter, char[][] grid) {
		boolean letterFound = false;
		ArrayList<LetterPath> deletePaths = new ArrayList<>();
		int startingSize = list.size();
		LetterPath lastPath = list.get(startingSize - 1);
		for (int i = 0; i < list.size(); i++) {
			LetterPath path = list.get(i);

			letter = letter.toUpperCase();
			int startingX = ((Integer) path.peek().getKey());
			int startingY = ((Integer) path.peek().getValue());

			if ((startingX + 1) < 4 && (startingX + 1) >= 0 && startingY < 4 && startingY >= 0) {
				if (Character.toString(grid[startingX + 1][startingY]).toUpperCase().equals(letter) && !path.hasVisited(startingX + 1, startingY)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX + 1, startingY);
					list.add(newPath);
				}
			}
			if ((startingX - 1) < 4 && (startingX - 1) >= 0 && startingY < 4 && startingY >= 0) {
				if (Character.toString(grid[startingX - 1][startingY]).toUpperCase().equals(letter) && !path.hasVisited(startingX - 1, startingY)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX - 1, startingY);
					list.add(newPath);
				}
			}
			if (startingX < 4 && startingX >= 0 && (startingY + 1) < 4 && (startingY + 1) >= 0) {
				if (Character.toString(grid[startingX][startingY + 1]).toUpperCase().equals(letter) && !path.hasVisited(startingX, startingY + 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX, startingY + 1);
					list.add(newPath);
				}
			}
			if (startingX < 4 && startingX >= 0 && (startingY - 1) < 4 && (startingY - 1) >= 0) {
				if (Character.toString(grid[startingX][startingY - 1]).toUpperCase().equals(letter) && !path.hasVisited(startingX, startingY - 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX, startingY - 1);
					list.add(newPath);
				}
			}
			if ((startingX + 1) < 4 && (startingX + 1) >= 0 && (startingY + 1) < 4 && (startingY + 1) >= 0) {
				if (Character.toString(grid[startingX + 1][startingY + 1]).toUpperCase().equals(letter) && !path.hasVisited(startingX + 1, startingY + 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX + 1, startingY + 1);
					list.add(newPath);
				}
			}
			if ((startingX + 1) < 4 && (startingX + 1) >= 0 && (startingY - 1) < 4 && (startingY - 1) >= 0) {
				if (Character.toString(grid[startingX + 1][startingY - 1]).toUpperCase().equals(letter) && !path.hasVisited(startingX + 1, startingY - 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX + 1, startingY - 1);
					list.add(newPath);
				}
			}
			if ((startingX - 1) < 4 && (startingX - 1) >= 0 && (startingY - 1) < 4 && (startingY - 1) >= 0) {
				if (Character.toString(grid[startingX - 1][startingY - 1]).toUpperCase().equals(letter) && !path.hasVisited(startingX - 1, startingY - 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX - 1, startingY - 1);
					list.add(newPath);
				}
			}
			if ((startingX - 1) < 4 && (startingX - 1) >= 0 && (startingY + 1) < 4 && (startingY + 1) >= 0) {
				if (Character.toString(grid[startingX - 1][startingY + 1]).toUpperCase().equals(letter) && !path.hasVisited(startingX - 1, startingY + 1)) {
					letterFound = true;
					LetterPath newPath = path.clone();
					newPath.addLetter(startingX - 1, startingY + 1);
					list.add(newPath);
				}
			}
			deletePaths.add(path);
			if(path == lastPath)
				break;
		}

		for(LetterPath path : deletePaths){
			list.remove(path);
		}

		return letterFound;
	}
}
