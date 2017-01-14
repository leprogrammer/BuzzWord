package data;

import apptemplate.AppTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import components.AppDataComponent;
import ui.AppMessageDialogSingleton;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Tejas
 */
public class AccountData implements AppDataComponent{
    private String username;
    private String password;

    private int highestLevelDictionary; // 0
    private int highestLevelNames; // 1
    private int highestLevelAnimal; // 2
    private int highestLevelPlaces; // 3
    private int[][] highScores;

    @JsonIgnore
    private AppTemplate appTemplate;
    @JsonIgnore
    static AccountData singleton = null;

    private AccountData(){
        this.appTemplate = appTemplate;
        highestLevelAnimal = 1;
        highestLevelDictionary = 1;
        highestLevelNames = 1;
        highestLevelPlaces = 1;
        highScores = new int[4][8];
        for(int i = 0; i < 4; i++)
            for(int j = 0; j < 8; j++){
                highScores[i][j] = (j == 0) ? 0 : -1;
            }
    }

    public static AccountData getSingleton(){
        if(singleton == null)
            singleton = new AccountData();
        return singleton;
    }

    public boolean updateHighScore(GameMode mode, int level, int score){
        switch(mode){
            case DICTIONARY:
                if(highScores[0][level - 1] < score){
                    highScores[0][level - 1] = score;
                    return true;
                }
                break;
            case NAMES:
                if(highScores[1][level - 1] < score){
                    highScores[1][level - 1] = score;
                    return true;
                }
                break;
            case ANIMALS:
                if(highScores[2][level - 1] < score){
                    highScores[2][level - 1] = score;
                    return true;
                }
                break;
            case PLACES:
                if(highScores[3][level - 1] < score){
                    highScores[3][level - 1] = score;
                    return true;
                }
                break;

        }
        return false;
    }

    public boolean updateHighestLevelProgress(GameMode mode, int level){
        switch(mode){
            case DICTIONARY:
                if(level == 8) {
                    highestLevelDictionary = 8;
                    return true;
                }
                else if(highestLevelDictionary <= level){
                    highestLevelDictionary = level + 1;
                    highScores[0][level] = 0;
                    return true;
                }
                break;
            case NAMES:
                if(level == 8) {
                    highestLevelNames = 8;
                    return true;
                }
                else if(highestLevelNames <= level){
                    highestLevelNames = level + 1;
                    highScores[1][level] = 0;
                    return true;
                }
                break;
            case ANIMALS:
                if(level == 8) {
                    highestLevelAnimal = 8;
                    return true;
                }
                else if(highestLevelAnimal <= level){
                    highestLevelAnimal = level + 1;
                    highScores[2][level] = 0;
                    return true;
                }
                break;
            case PLACES:
                if(level == 8) {
                    highestLevelPlaces = 8;
                    return true;
                }
                else if(highestLevelPlaces <= level){
                    highestLevelPlaces = level + 1;
                    highScores[3][level] = 0;
                    return true;
                }
                break;

        }
        return false;
    }

    public int getHighestLevel(GameMode mode){
        switch(mode){
            case DICTIONARY:
                return highestLevelDictionary;
            case PLACES:
                return highestLevelPlaces;
            case NAMES:
                return highestLevelNames;
            case ANIMALS:
                return highestLevelAnimal;
        }

        return -1;
    }

    public int getHighScore(GameMode gameMode, int level){
        switch(gameMode){
            case DICTIONARY:
                return highScores[0][level - 1];
            case NAMES:
                return highScores[1][level - 1];
            case ANIMALS:
                return highScores[2][level - 1];
            case PLACES:
                return highScores[3][level - 1];

        }
        return -1;
    }

    public int getHighScore(int gameMode, int level){
        switch(gameMode){
            case 0:
                return highScores[0][level - 1];
            case 1:
                return highScores[1][level - 1];
            case 2:
                return highScores[2][level - 1];
            case 3:
                return highScores[3][level - 1];

        }
        return -1;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setAndEncryptPassword(String password) {
        try {
            MessageDigest encrypt = MessageDigest.getInstance("MD5");
            encrypt.update(password.getBytes());

            byte[] byteArray = encrypt.digest();
            this.password = new BigInteger(1, byteArray).toString(16);
        }
        catch (NoSuchAlgorithmException e) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show("Encryption Error", "There was an error while trying to encrypt your password.");
        }
    }

    public void setHighestLevelDictionary(int highestLevelDictionary) {
        this.highestLevelDictionary = highestLevelDictionary;
    }

    public void setHighestLevelNames(int highestLevelNames) {
        this.highestLevelNames = highestLevelNames;
    }

    public void setHighestLevelAnimal(int highestLevelAnimal) {
        this.highestLevelAnimal = highestLevelAnimal;
    }

    public void setHighestLevelPlaces(int highestLevelPlaces) {
        this.highestLevelPlaces = highestLevelPlaces;
    }

    public void setHighScores(int[][] highScores) {
        this.highScores = highScores;
    }

    public String getUsername(){
        return username;
    }

    public int getHighestLevelDictionary() {
        return highestLevelDictionary;
    }

    public int getHighestLevelNames() {
        return highestLevelNames;
    }

    public int getHighestLevelAnimal() {
        return highestLevelAnimal;
    }

    public int getHighestLevelPlaces() {
        return highestLevelPlaces;
    }

    public String getPassword() {
        return password;
    }

    public int[][] getHighScores() {
        return highScores;
    }

    @Override
    public void reset() {
        username = null;
        password = null;
        highestLevelAnimal = 1;
        highestLevelDictionary = 1;
        highestLevelNames = 1;
        highestLevelPlaces = 1;
        highScores = new int[4][8];
        for(int i = 0; i < 4; i++)
            for(int j = 0; j < 8; j++){
                highScores[i][j] = (j == 0) ? 0 : -1;
            }
    }
}
