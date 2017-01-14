package gui;

import apptemplate.AppTemplate;
import data.GameData;
import data.GameLoader;
import data.GameMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import propertymanager.PropertyManager;
import ui.AppMessageDialogSingleton;

import java.io.IOException;

import static gui.BuzzWordProperties.*;

/**
 * @author Tejas
 */
public class BuzzWordGraphics {
	private AppTemplate appTemplate;
	private GameData gameData;
	private GameLoader gameLoader;
    private VBox scoringArea;
    private ListView wordList;
    private HBox buttonArea;

	private Circle[][] circles;
	private Text[][] text;
	private Rectangle wordBox;
	private Rectangle currentWordBox;
	private Rectangle scoreBox;
    private Rectangle timerBox;
    private Rectangle targetBox;
    private Label timeLabel;
    private Label currentScoreLabel;
    private Label targetScoreLabel;
	private Text currentWord;
	private ObservableList currentWordList;

	private int timeLimit;

	private Button restartLevel;
	private Button nextLevel;
	private Button pauseGame;
	private Button playGame;

	public BuzzWordGraphics(AppTemplate appTemplate){
		this.appTemplate = appTemplate;
		PropertyManager propertyManager = PropertyManager.getManager();
		gameData = GameData.getSingleton();
		gameLoader = new GameLoader();

		circles = new Circle[4][4];
		text = new Text[4][4];
        wordList = new ListView();
		wordList.setOrientation(Orientation.VERTICAL);
		wordList.setEditable(false);
		buttonArea = new HBox(30);
		buttonArea.setAlignment(Pos.CENTER);
		buttonArea.setPadding(new Insets(0, 0, 50, 0));
        timeLabel = new Label();
        currentScoreLabel = new Label();
        targetScoreLabel = new Label();
		currentWord = new Text("");
		currentWord.setFont(new Font(15));
		currentWordList = FXCollections.observableArrayList();

        timeLabel.setFont(new Font(20));
        currentScoreLabel.setFont(new Font(15));
        targetScoreLabel.setFont(new Font(15));
        timeLimit = 120;

		setupButtons();
	}

	public void renderGameScreen(Pane gamePane, GameMode mode, int level) throws IOException{
		currentWord = new Text("");
		currentWord.setFont(new Font(15));
		do {
			gameData = gameLoader.loadData(mode, level);
		}while(gameData.getAllWords().isEmpty());
		playGame.setDisable(false);
		playGame.setVisible(true);
		pauseGame.setDisable(true);
		pauseGame.setVisible(false);

		int targetScore = gameData.getTargetScore();

		switch(level){
			case 1:
				timeLimit = 120;
				break;
			case 2:
				timeLimit = 115;
				break;
			case 3:
				timeLimit = 110;
				break;
			case 4:
				timeLimit = 100;
				break;
			case 5:
				timeLimit = 90;
				break;
			case 6:
				timeLimit = 70;
				break;
			case 7:
				timeLimit = 50;
				break;
			case 8:
				timeLimit = 30;
				break;
		}

		/*for(String x : gameData.getAllWords())
			currentWordList.add(x);*/

		wordList.setItems(currentWordList);

		GridPane letterTiles = new GridPane();
		letterTiles.setPadding(new Insets(0, 50, 0, 50));
		letterTiles.setAlignment(Pos.CENTER);
		letterTiles.setVgap(20);
		letterTiles.setHgap(20);

		for (int i = 0, a = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++, a++) {
				StackPane letterCircle = new StackPane();
				circles[i][j] = new Circle(50, Color.WHITE);
				circles[i][j].setStroke(Color.BLACK);
				text[i][j] = new Text(Character.toString(gameData.getLetterArray()[a]));
				text[i][j].setText(text[i][j].getText().toUpperCase());
				text[i][j].setFont(Font.font(15));
				text[i][j].setVisible(false);
				letterCircle.getChildren().addAll(circles[i][j], text[i][j]);
				letterTiles.add(letterCircle, j, i);
			}
		}

		scoringArea = new VBox(30);
		scoringArea.setPadding(new Insets(0, 75, 0, 0));
		scoringArea.setAlignment(Pos.CENTER);
		StackPane timerStack = new StackPane();
		StackPane currentWordStack = new StackPane();
		StackPane scoreStack = new StackPane();
		StackPane wordStack = new StackPane();
        StackPane targetStack = new StackPane();

		timerBox = new Rectangle(275, 70, Color.LIGHTSLATEGRAY);
		currentWordBox = new Rectangle(200, 70, Color.LIGHTSLATEGRAY);
		scoreBox = new Rectangle(200, 70, Color.LIGHTSLATEGRAY);
		wordBox = new Rectangle(200, 200, Color.LIGHTSLATEGRAY);
        targetBox = new Rectangle(125, 75, Color.LIGHTSLATEGRAY);
		currentWordBox.setStroke(Color.BLACK);
        targetBox.setStroke(Color.BLACK);
        timerBox.setStroke(Color.BLACK);
        scoreBox.setStroke(Color.BLACK);
        wordBox.setStroke(Color.BLACK);

		wordList.setMaxSize(wordBox.getWidth() - 10, wordBox.getHeight() - 10);

        timeLabel.setText(Integer.toString(timeLimit));
        currentScoreLabel.setText("0");
        targetScoreLabel.setText("Target: " + targetScore + " points");

		Label timeText = new Label("Time Remaining: ");
		timeText.setFont(new Font(20));
		Label seconds = new Label(" seconds");
		seconds.setFont(new Font(20));
		Label totalText = new Label("Total: ");
		totalText.setFont(new Font(15));

		HBox layoutTime = new HBox();
		HBox layoutScore = new HBox();
		layoutTime.setAlignment(Pos.CENTER);
		layoutScore.setAlignment(Pos.CENTER);
        layoutTime.getChildren().addAll(timeText, timeLabel, seconds);
        layoutScore.getChildren().addAll(totalText, currentScoreLabel);

		currentWordStack.setAlignment(Pos.CENTER);
		currentWordStack.getChildren().addAll(currentWordBox, currentWord);
        scoreStack.getChildren().addAll(scoreBox, layoutScore);
        timerStack.getChildren().addAll(timerBox, layoutTime);
        wordStack.getChildren().addAll(wordBox, wordList);
        targetStack.getChildren().addAll(targetBox, targetScoreLabel);

		VBox wordArea = new VBox();
		wordArea.getChildren().addAll(currentWordStack, wordStack, scoreStack);

		scoringArea.getChildren().addAll(timerStack, wordArea, targetStack);
		scoringArea.setAlignment(Pos.CENTER_RIGHT);

        Label modeDetails = new Label("Game Mode: " + mode.toString());
		modeDetails.setFont(new Font(20));
		Label levelDetails = new Label("Level: " + level);
		levelDetails.setFont(new Font(20));

		VBox gameDetails = new VBox();
		gameDetails.setAlignment(Pos.CENTER);
		gameDetails.setPadding(new Insets(50, 0, 0, 0));
		gameDetails.getChildren().addAll(modeDetails, levelDetails);

		((BorderPane) gamePane).setCenter(letterTiles);
		((BorderPane) gamePane).setRight(scoringArea);
		((BorderPane) gamePane).setBottom(buttonArea);
		((BorderPane) gamePane).setTop(gameDetails);
	}

	public void reset(){
		resetCircles();
		resetCurrentWord();
		currentWordList.clear();
		wordList.getItems().clear();
		gameData.resetWords();
	}

	public void resetCircles(){
		for(Circle x[] : circles){
			for(Circle y : x){
				y.setStroke(Color.BLACK);
				y.setEffect(null);
			}
		}
	}

	public void resetCurrentWord(){
		currentWord.setText("");
	}

    public Circle[][] getCircles() {
        return circles;
    }

    public Text[][] getText() {
        return text;
    }

    public Text getCurrentWord(){
		return currentWord;
	}

	public int getTimeLimit(){
    	return timeLimit;
	}

	public Label getCurrentScoreLabel(){
		return currentScoreLabel;
	}

	public Label getTimeLabel(){
    	return timeLabel;
	}

	public Button getPlayGame(){
    	return playGame;
	}

	public Button getPauseGame(){
		return pauseGame;
	}

	public Button getRestartLevel() {
		return restartLevel;
	}

	public Button getNextLevel() {
		return nextLevel;
	}

	public ObservableList getCurrentWordList(){
		return currentWordList;
	}

	private void setupButtons(){
		try {
			StackPane pausePlay = new StackPane();
			restartLevel = appTemplate.getGUI().initializeChildButtonWithIcon(buttonArea, RESTART_ICON.toString(), "Restart Game", false);
			pauseGame = appTemplate.getGUI().initializeChildButtonWithIcon(pausePlay, PAUSE_ICON.toString(), "Pause Game", false);
			playGame = appTemplate.getGUI().initializeChildButtonWithIcon(pausePlay, PLAY_ICON.toString(), "Play Game", false);
			pauseGame.setVisible(false);
			playGame.setVisible(true);
			buttonArea.getChildren().add(pausePlay);

			nextLevel = appTemplate.getGUI().initializeChildButtonWithIcon(buttonArea, NEXT_LEVEL_ICON.toString(), "Next Level", false);
		} catch (IOException e) {
			AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
			dialog.show("Graphics Button Creation Error", "There was an error while trying to create buttons in the game scene.");
		}
	}

}
