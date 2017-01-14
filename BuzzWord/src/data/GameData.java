package data;

import apptemplate.AppTemplate;
import ui.AppMessageDialogSingleton;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import static settings.InitializationParameters.APP_WORKDIR_PATH;

/**
 * @author Tejas
 */
public class GameData {
	static GameData singleton = null;

	private Trie trie = new Trie();
	private URL workDirURL = AppTemplate.class.getClassLoader().getResource(APP_WORKDIR_PATH.getParameter());
	private char[] letter;
	private ArrayList<String> usedWords;
	private ArrayList<String> allWords;
	private GameMode gameMode;
	private int level;
	private int targetScore;
	private int totalScore;

	private GameData(){
		allWords = new ArrayList<>();
		usedWords = new ArrayList<>();
		level = 1;
	}

	public static GameData getSingleton() {
		if (singleton == null)
			singleton = new GameData();
		return singleton;
	}

	public void resetWords(){
		allWords = new ArrayList<>();
		usedWords = new ArrayList<>();
	}

	public void loadTrie(){
		try{
			String mode = "";
			switch(gameMode){
				case ANIMALS:
					mode = "animals";
					break;
				case NAMES:
					mode = "names";
					break;
				case DICTIONARY:
					mode = "dictionary";
					break;
				case PLACES:
					mode = "places";
					break;
			}
			File initialDir = new File(workDirURL.getPath());
			File trieFile = new File(initialDir.getAbsolutePath() + "\\" + mode + ".obj");
			ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(trieFile));
			trie = (Trie)inStream.readObject();
			inStream.close();
		}
		catch(ClassNotFoundException | IOException e){
			trie = new Trie();
			trie.populateTrie(gameMode);
		}
	}

	public void saveTrie(){
		if(trie != null){
			try {
				String mode = "";
				switch (gameMode) {
					case ANIMALS:
						mode = "animals";
						break;
					case NAMES:
						mode = "names";
						break;
					case DICTIONARY:
						mode = "dictionary";
						break;
					case PLACES:
						mode = "places";
						break;
				}
				File initialDir = new File(workDirURL.getPath());
				File trieFile = new File(initialDir.getAbsolutePath() + "\\" + mode + ".obj");
				ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(trieFile));
				outStream.writeObject(trie);
				outStream.close();
			}
			catch (IOException e){
				AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
				dialog.show("Save Error", "There was an error while trying to save the tree of words.");
			}
		}
	}

	public char[] getLetterArray() {
		return letter;
	}

	public void setLetter(char[] letter) {
		this.letter = letter;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getTargetScore() {
		return targetScore;
	}

	public int getTotalScore(){
		return totalScore;
	}

	public void setTargetScore(int targetScore) {
		this.targetScore = targetScore;
	}

	public ArrayList<String> getAllWords(){
		return allWords;
	}

	public void addToAllWords(String word){
		if(!allWords.contains(word.toUpperCase()))
			allWords.add(word.toUpperCase());
	}

	public boolean checkWord(String word){
		if(usedWords.contains(word.toUpperCase()))
			return false;
		if(allWords.contains(word.toUpperCase()) || trie.searchForWord(word.toUpperCase())) {
			usedWords.add(word.toUpperCase());
			return true;
		}
		return false;
	}

	public void gridSolver(char[][] grid){
		URL wordsResource = getClass().getClassLoader().getResource("words/general.txt");
		int maxBound = 0;
		switch(gameMode){
			case ANIMALS:
				wordsResource = getClass().getClassLoader().getResource("words/animals.txt");
				assert wordsResource != null;
				maxBound = GameLoader.NUMBER_OF_ANIMAL_WORDS;
				break;
			case NAMES:
				wordsResource = getClass().getClassLoader().getResource("words/people.txt");
				assert wordsResource != null;
				maxBound = GameLoader.NUMBER_OF_PEOPLE_WORDS;
				break;
			case DICTIONARY:
				wordsResource = getClass().getClassLoader().getResource("words/general.txt");
				assert wordsResource != null;
				maxBound = GameLoader.NUMBER_OF_GENERAL_WORDS;
				break;
			case PLACES:
				wordsResource = getClass().getClassLoader().getResource("words/places.txt");
				assert wordsResource != null;
				maxBound = GameLoader.NUMBER_OF_PLACES_WORDS;
				break;
		}

		PathList pathList = new PathList();
		int counter = 0;
		do {
			try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
				String word = lines.skip(counter).findFirst().get().toUpperCase();
				for (int i = 0; i < word.length(); i++) {
					if (i == 0) {
						int textX = 0, textY = 0;
						for (char[] x : grid) {
							textY = 0;
							for (char y : x) {
								if (Character.toString(y).toUpperCase().equals(Character.toString(word.charAt(i)))) {
									pathList.startList(textX, textY);
								}
								textY++;
							}
							textX++;
						}
						if (pathList.isEmpty()) {
							pathList.reset();
							break;
						}
					}
					else {
						if (pathList.searchLetter(Character.toString(word.charAt(i)), grid)) {
							if(i == word.length() - 1) {
								addToAllWords(word);
								totalScore += word.length() * 2;
							}
						}
						else {
							pathList.reset();
							break;
						}
					}
				}
				counter++;
			}
			catch (IOException | URISyntaxException e) {
				AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
				dialog.show("Solver Error", "There was an error while solving the grid. Try again.");
			}
		}while(counter < maxBound);
	}
}