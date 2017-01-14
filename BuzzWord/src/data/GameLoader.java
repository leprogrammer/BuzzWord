package data;

import ui.AppMessageDialogSingleton;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author Tejas
 */
public class GameLoader {
	public static final int NUMBER_OF_GENERAL_WORDS = 1484;
	public static final int NUMBER_OF_ANIMAL_WORDS = 212;
	public static final int NUMBER_OF_PLACES_WORDS = 63;
	public static final int NUMBER_OF_PEOPLE_WORDS = 4913;

	public GameLoader(){}

	public GameData loadData(GameMode gameMode, int level) throws IOException {
		GameData gameData = GameData.getSingleton();
		URL wordsResource = getClass().getClassLoader().getResource("words/general.txt");

		int toSkip;
		int randomBound = 0;

		switch(gameMode){
			case ANIMALS:
				wordsResource = getClass().getClassLoader().getResource("words/animals.txt");
				assert wordsResource != null;
				randomBound = NUMBER_OF_ANIMAL_WORDS;
				break;
			case NAMES:
				wordsResource = getClass().getClassLoader().getResource("words/people.txt");
				assert wordsResource != null;
				randomBound = NUMBER_OF_PEOPLE_WORDS;
				break;
			case DICTIONARY:
				wordsResource = getClass().getClassLoader().getResource("words/general.txt");
				assert wordsResource != null;
				randomBound = NUMBER_OF_GENERAL_WORDS;
				break;
			case PLACES:
				wordsResource = getClass().getClassLoader().getResource("words/places.txt");
				assert wordsResource != null;
				randomBound = NUMBER_OF_PLACES_WORDS;
				break;
		}

		int circlesFilled = 0;
		int currentCircleX = new Random().nextInt(4);
		int currentCircleY = new Random().nextInt(4);
		int targetScore = 0;
		ArrayList<String> wordList = new ArrayList<>();
		char[][] letters = new char[4][4];

		do {
			try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
				toSkip = new Random().nextInt(randomBound);
				String word = lines.skip(toSkip).findFirst().get().toUpperCase();
				boolean negativeCheck = false, positiveCheck = false;

				if (word.length() <= (16 - circlesFilled)) {
					for (int i = 0; i < word.length(); i++) {
						boolean letterAdded = false;
						negativeCheck = false;
						positiveCheck = false;
						do {
							if(positiveCheck && negativeCheck)
								break;
							int randomAddOrSub = new Random().nextInt(2);
							randomAddOrSub = (randomAddOrSub == 0) ? -1 : 1;

							if(currentCircleX < 4 && currentCircleX >= 0 && currentCircleY < 4 && currentCircleY >= 0) {
								if (letters[currentCircleX][currentCircleY] == Character.MIN_VALUE) {
									letters[currentCircleX][currentCircleY] = word.charAt(i);
									letterAdded = true;
									circlesFilled++;
									continue;
								}
							}

							if (currentCircleX < 4 && currentCircleX >= 0 && (currentCircleY + randomAddOrSub) < 4 && (currentCircleY + randomAddOrSub) >= 0) {
								if (letters[currentCircleX][currentCircleY + randomAddOrSub] == Character.MIN_VALUE) {
									letters[currentCircleX][currentCircleY + randomAddOrSub] = word.charAt(i);
									currentCircleY += randomAddOrSub;
									letterAdded = true;
									circlesFilled++;
									continue;
								}
							}

							if ((currentCircleX + randomAddOrSub) < 4 && (currentCircleX + randomAddOrSub) >= 0 && (currentCircleY + randomAddOrSub) < 4 && (currentCircleY + randomAddOrSub) >= 0) {
								if (letters[currentCircleX + randomAddOrSub][currentCircleY + randomAddOrSub] == Character.MIN_VALUE) {
									letters[currentCircleX + randomAddOrSub][currentCircleY + randomAddOrSub] = word.charAt(i);
									currentCircleX += randomAddOrSub;
									currentCircleY += randomAddOrSub;
									letterAdded = true;
									circlesFilled++;
									continue;
								}
							}
							if ((currentCircleX + randomAddOrSub) < 4 && (currentCircleX + randomAddOrSub) >= 0 && (currentCircleY - randomAddOrSub) < 4 && (currentCircleY - randomAddOrSub) >= 0) {
								if (letters[currentCircleX + randomAddOrSub][currentCircleY - randomAddOrSub] == Character.MIN_VALUE) {
									letters[currentCircleX + randomAddOrSub][currentCircleY - randomAddOrSub] = word.charAt(i);
									currentCircleX += randomAddOrSub;
									currentCircleY -= randomAddOrSub;
									letterAdded = true;
									circlesFilled++;
									continue;
								}
							}
							if ((currentCircleX - randomAddOrSub) < 4 && (currentCircleX - randomAddOrSub) >= 0 && (currentCircleY - randomAddOrSub) < 4 && (currentCircleY - randomAddOrSub) >= 0) {
								if (letters[currentCircleX - randomAddOrSub][currentCircleY - randomAddOrSub] == Character.MIN_VALUE) {
									letters[currentCircleX - randomAddOrSub][currentCircleY - randomAddOrSub] = word.charAt(i);
									currentCircleX -= randomAddOrSub;
									currentCircleY -= randomAddOrSub;
									letterAdded = true;
									circlesFilled++;
									continue;
								}
							}
							if ((currentCircleX + randomAddOrSub) < 4 && (currentCircleX + randomAddOrSub) >= 0 && currentCircleY < 4 && currentCircleY >= 0) {
								if (letters[currentCircleX + randomAddOrSub][currentCircleY] == Character.MIN_VALUE) {
									letters[currentCircleX + randomAddOrSub][currentCircleY] = word.charAt(i);
									currentCircleX += randomAddOrSub;
									letterAdded = true;
									circlesFilled++;
									continue;
								}
							}


							if ((currentCircleX - randomAddOrSub) < 4 && (currentCircleX - randomAddOrSub) >= 0 && (currentCircleY + randomAddOrSub) < 4 && (currentCircleY + randomAddOrSub) >= 0) {
								if (letters[currentCircleX - randomAddOrSub][currentCircleY + randomAddOrSub] == Character.MIN_VALUE) {
									letters[currentCircleX - randomAddOrSub][currentCircleY + randomAddOrSub] = word.charAt(i);
									currentCircleX -= randomAddOrSub;
									currentCircleY += randomAddOrSub;
									letterAdded = true;
									circlesFilled++;
									continue;
								}
							}
							if(randomAddOrSub == -1)
								negativeCheck = true;
							if(randomAddOrSub == 1)
								positiveCheck = true;
						} while (!letterAdded);
						if(positiveCheck && negativeCheck)
							break;
					}
					if(positiveCheck && negativeCheck) {
						currentCircleX = new Random().nextInt(4);
						currentCircleY = new Random().nextInt(4);
					}
					else {
						targetScore += word.length() * 2;
						wordList.add(word);
					}
				}

			}
			catch (IOException | URISyntaxException e) {
				AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
				dialog.show("Grid Generation Error", "There was an error while trying to generate the grid.");
			}
		}while(circlesFilled < 14);

		char[] letterList = new char[16];
		for (int i = 0, a = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++, a++) {
				if (letters[i][j] == Character.MIN_VALUE)
					letters[i][j] = (char) (new Random().nextInt(26) + 97);
				letterList[a] = letters[i][j];
			}
		}

		gameData.gridSolver(letters);
		if(targetScore == 0)
			targetScore = (int)(gameData.getTotalScore() / 2.0);

		for(String word : wordList){
			gameData.addToAllWords(word);
		}
		gameData.setLetter(letterList);
		gameData.setTargetScore(targetScore);
		gameData.setGameMode(gameMode);

		return gameData;
	}
}
