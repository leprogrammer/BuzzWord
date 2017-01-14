package controller;

import apptemplate.AppTemplate;
import data.*;
import gui.ApplicationStateHandler;
import gui.BuzzWordGraphics;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import ui.AppMessageDialogSingleton;
import ui.YesNoCancelDialogSingleton;

import java.io.IOException;

/**
 * @author Tejas
 */
public class BuzzWordController{

    private AppTemplate appTemplate;
    private AccountData accountData;
    private AccountLoader accountLoader;
    private BuzzWordGraphics graphics;
    private ApplicationStateHandler stateHandler;
    private GameData gameData;
    private Timeline timer;
    private AnimationTimer animationTimer;

    private IntegerProperty timeLeft;
    private IntegerProperty currentScore;
    private int score;
    private int currentNodeX;
    private int currentNodeY;
    private int time;
    private boolean gameInProgress = false;
    private boolean gameFinished = false;
    private boolean wordFinished = false;
    private boolean abruptEnd = false;
    private boolean letterAdded[][];
    private PathList pathList;

    public BuzzWordController(ApplicationStateHandler handler){
        stateHandler = handler;
        appTemplate = stateHandler.getAppTemplate();
        graphics = stateHandler.getGraphics();
        accountData = AccountData.getSingleton();
        accountLoader = stateHandler.getAccountLoader();
        gameData = GameData.getSingleton();
        letterAdded = new boolean[4][4];
        pathList = new PathList();
        timeLeft = new SimpleIntegerProperty(time);
        currentScore = new SimpleIntegerProperty(score);
    }

    public void PlayGame(){
        abruptEnd = false;
        time = graphics.getTimeLimit();
        currentScore.set(score);
        setupBinding();
        timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);
        timeLeft.set(time);
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(time + 1), new KeyValue(timeLeft, 0)));
        pathList.reset();
        graphics.resetCurrentWord();
        graphics.resetCircles();
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int i = 0, j;
                for(Circle x[] : graphics.getCircles()){
                    j = 0;
                    for(Circle changeCircle : x){
                        int finalI = i;
                        int finalJ = j;
                        changeCircle.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
                            currentNodeX = finalI;
                            currentNodeY = finalJ;
                            changeCircle.startFullDrag();
                        });
                        changeCircle.setOnMouseDragged(event -> {
                            if(isAdjacent(currentNodeX, currentNodeY, finalI, finalJ)) {
                                changeCircle.setStroke(Color.GREEN);
                                changeCircle.setEffect(new DropShadow(30, Color.FORESTGREEN));
                                if (!letterAdded[finalI][finalJ]) {
                                    graphics.getCurrentWord().setText(graphics.getCurrentWord().getText() + graphics.getText()[finalI][finalJ].getText().toUpperCase());
                                    letterAdded[finalI][finalJ] = true;

                                    currentNodeX = finalI;
                                    currentNodeY = finalJ;
                                }
                            }
                        });
                        changeCircle.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, event -> {
                            if(isAdjacent(currentNodeX, currentNodeY, finalI, finalJ)) {
                                changeCircle.setStroke(Color.GREEN);
                                changeCircle.setEffect(new DropShadow(30, Color.FORESTGREEN));
                                if(!letterAdded[finalI][finalJ]) {
                                    graphics.getCurrentWord().setText(graphics.getCurrentWord().getText() + graphics.getText()[finalI][finalJ].getText().toUpperCase());
                                    letterAdded[finalI][finalJ] = true;

                                    currentNodeX = finalI;
                                    currentNodeY = finalJ;
                                }
                            }
                        });
                        changeCircle.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, event -> {
                            if(isAdjacent(currentNodeX, currentNodeY, finalI, finalJ)) {
                                changeCircle.setStroke(Color.GREEN);
                                changeCircle.setEffect(new DropShadow(30, Color.FORESTGREEN));
                                if(!letterAdded[finalI][finalJ]) {
                                    graphics.getCurrentWord().setText(graphics.getCurrentWord().getText() + graphics.getText()[finalI][finalJ].getText().toUpperCase());
                                    letterAdded[finalI][finalJ] = true;
                                }
                                wordFinished = true;
                            }
                        });
                        j++;
                    }
                    i++;
                }

                if(timeLeft.get() <= 0)
                    stop();

                appTemplate.getGUI().getPrimaryScene().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
                    if(!graphics.getCurrentWord().getText().equals(""))
                        wordFinished = true;
                });

                appTemplate.getGUI().getPrimaryScene().setOnKeyPressed(e ->{
                    if(e.getCode().equals(KeyCode.ENTER))
                        wordFinished = true;
                });

                appTemplate.getGUI().getPrimaryScene().setOnKeyTyped((KeyEvent e) ->{
                    if(Character.isAlphabetic(e.getCharacter().toUpperCase().charAt(0))) {
                        String guess = e.getCharacter().toUpperCase();
                        if (graphics.getCurrentWord().getText().equals("")) {
                            pathList.reset();
                            graphics.resetCurrentWord();
                            graphics.resetCircles();
                            int textX = 0, textY = 0;
                            for (Text[] x : graphics.getText()) {
                                textY = 0;
                                for (Text y : x) {
                                    if (y.getText().toUpperCase().equals(guess)) {
                                        pathList.startList(textX, textY);
                                        letterAdded[textX][textY] = true;
                                        addCircleEffect(graphics.getCircles()[textX][textY]);
                                    }
                                    textY++;
                                }
                                textX++;
                        }
                        if (!pathList.isEmpty())
                            graphics.getCurrentWord().setText(graphics.getCurrentWord().getText() + guess);
                        }
                        else {
                            if (pathList.searchLetter(guess, graphics.getText())) {
                                graphics.getCurrentWord().setText(graphics.getCurrentWord().getText() + guess);
                                graphics.resetCircles();

                                if (!pathList.isEmpty()) {
                                    for (LetterPath path : pathList.getList()) {
                                        for (Pair<Integer, Integer> coordinate : path.getPath()) {
                                            addCircleEffect(graphics.getCircles()[coordinate.getKey()][coordinate.getValue()]);
                                        }
                                    }
                                }
                            }
                            else{
                                pathList.reset();
                                graphics.resetCurrentWord();
                                graphics.resetCircles();
                            }
                        }
                    }
                });

                if(wordFinished) {
                    boolean addWord = gameData.checkWord(graphics.getCurrentWord().getText());
                    if(addWord) {
                        String currentWord = graphics.getCurrentWord().getText();
                        graphics.getCurrentWordList().add(String.format("%-15s\t%2s", currentWord, (currentWord.length() * 2)));
                        score += currentWord.length() * 2;
                        currentScore.set(score);
                    }
                    graphics.resetCircles();
                    graphics.resetCurrentWord();
                    letterAdded = new boolean[4][4];
                    wordFinished = false;
                }
            }

            @Override
            public void stop(){
                super.stop();
                if(!abruptEnd)
                    endGame();
            }
        };
        gameInProgress = true;
        timer.playFromStart();
        animationTimer.start();
    }

    public void setupHandlers(){
        graphics.getPauseGame().setOnMouseClicked(e -> handlePause());

        graphics.getPlayGame().setOnMouseClicked(e ->{
            graphics.getPauseGame().setVisible(true);
            graphics.getPauseGame().setDisable(false);
            graphics.getPlayGame().setVisible(false);
            for(Text[] x: graphics.getText())
                for(Text y: x)
                    y.setVisible(true);

            if(!gameInProgress) {
                PlayGame();
                gameInProgress = true;
            }
            else if(gameFinished){
                e.consume();
            }
            else
                timer.play();
        });

        graphics.getRestartLevel().setOnMouseClicked(e ->{
            if(gameInProgress) {
                abruptPauseGame();
                YesNoCancelDialogSingleton dialog = YesNoCancelDialogSingleton.getSingleton();
                dialog.show("Exit?", "Do you want to quit the game and start another one?");
                if (dialog.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
                    abruptEndGame();
                    graphics.reset();
                    unbindLabels();
                    stateHandler.loadGameScreen();
                } else {
                    e.consume();
                    abruptContinueGame();
                }
            }
            else {
                graphics.reset();
                unbindLabels();
                stateHandler.loadGameScreen();
            }
        });

        graphics.getNextLevel().setOnMouseClicked(e ->{
            AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
            if(gameInProgress) {
                abruptPauseGame();
                YesNoCancelDialogSingleton dialog = YesNoCancelDialogSingleton.getSingleton();
                dialog.show("Exit?", "Do you want to quit the game and go to the next level?");
                if (dialog.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
                    if (gameData.getLevel() == 8) {
                        messageDialog.show("Highest Level", "You are on the highest level possible.");
                    } else if (gameData.getLevel() == accountData.getHighestLevel(gameData.getGameMode())) {
                        messageDialog.show("Locked Level", "The level you want to access is locked. Unlock it by beating this level");
                    } else if (gameData.getLevel() < accountData.getHighestLevel(gameData.getGameMode())) {
                        abruptEndGame();
                        unbindLabels();
                        graphics.reset();
                        gameData.setLevel(gameData.getLevel() + 1);
                        stateHandler.loadGameScreen();
                    } else
                        e.consume();
                } else {
                    e.consume();
                    abruptContinueGame();
                }
            }
            else {
                if (gameData.getLevel() == 8) {
                    messageDialog.show("Highest Level", "You are on the highest level possible.");
                } else if (gameData.getLevel() == accountData.getHighestLevel(gameData.getGameMode())) {
                    messageDialog.show("Locked Level", "The level you want to access is locked. Unlock it by beating this level");
                } else if (gameData.getLevel() < accountData.getHighestLevel(gameData.getGameMode())) {
                    unbindLabels();
                    graphics.reset();
                    gameData.setLevel(gameData.getLevel() + 1);
                    stateHandler.loadGameScreen();
                } else
                    e.consume();
            }
        });
    }

    public void abruptEndGame(){
        abruptEnd = true;
        animationTimer.stop();
        timer.stop();
        gameInProgress = false;
        graphics.reset();
        unbindLabels();
    }

    public void abruptPauseGame(){
        for(Text[] x: graphics.getText())
            for(Text y: x)
                y.setVisible(false);

        timer.pause();
    }

    public void abruptContinueGame(){
        for(Text[] x: graphics.getText())
            for(Text y: x)
                y.setVisible(true);

        timer.play();
    }

    private void endGame(){
        unbindLabels();
        graphics.resetCircles();
        graphics.resetCurrentWord();
        timer.stop();
        gameFinished = true;
        gameInProgress = false;
        boolean needToUpdate = false;
        int target = gameData.getTargetScore();
        String message = "";
        for(Text[] x: graphics.getText())
            for(Text y: x)
                y.setVisible(true);

        if(score > target){
            message += "You cleared this level!\n";
            if(gameData.getLevel() == 8) {
                message += "You beat the highest level in this mode!\n";
                needToUpdate = true;
            }
            else if(accountData.updateHighestLevelProgress(gameData.getGameMode(), gameData.getLevel())){
                message += "You unlocked the next level. Level " + (gameData.getLevel() + 1) + "!\n";
                needToUpdate = true;
            }

        }
        else {
            message += "You were not able to beat the target score. Try again.\n";
        }
        if(accountData.updateHighScore(gameData.getGameMode(), gameData.getLevel(), score)){
            message += "You beat your high score!\n";
            needToUpdate = true;
        }

        if(needToUpdate){
            try{
                accountLoader.saveAccount(accountData);
            }
            catch (IOException e){
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show("Save Error", "There was an error while trying to save your account.");
            }
        }

        String finalMessage = message;
        Platform.runLater(() ->{
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show("Game Over", finalMessage
                    + "\nThe list of all words possible in the grid is shown under the dotted line on the right side of the screen.");
        });

        graphics.getCurrentWordList().add("-------------");
        for(String word : gameData.getAllWords())
            graphics.getCurrentWordList().add(word);
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void handlePause(){
        graphics.getPauseGame().setVisible(false);
        graphics.getPlayGame().setVisible(true);
        graphics.getPlayGame().setDisable(false);
        for(Text[] x: graphics.getText())
            for(Text y: x)
                y.setVisible(false);
        if(!gameFinished)
            timer.pause();

    }

    private void setupBinding(){
        graphics.getCurrentScoreLabel().textProperty().bind(currentScore.asString());
        graphics.getTimeLabel().textProperty().bind(timeLeft.asString());
    }

    private void unbindLabels(){
        graphics.getTimeLabel().textProperty().unbind();
        graphics.getCurrentScoreLabel().textProperty().unbind();
    }

    private boolean isAdjacent(int currentX, int currentY, int testX, int testY){
        if(Math.abs(testX - currentX) > 1 || Math.abs(testY - currentY) > 1)
            return false;
        return true;
    }

    private void addCircleEffect(Circle circle){
        circle.setStroke(Color.GREEN);
        circle.setEffect(new DropShadow(30, Color.FORESTGREEN));
    }
}
