package data;

import ui.AppMessageDialogSingleton;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author Tejas
 */
public class Trie implements Serializable{
    private static final long serialVersionUID = 1L;
    private TrieNode rootNode;

    public Trie(){
        rootNode = new TrieNode();
    }

    public void populateTrie(GameMode gameMode){
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

        int counter = 0;
        do {
            try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
                String word = lines.skip(counter).findFirst().get().toUpperCase();

                TrieNode cursor = rootNode;

                for(int i = 0; i < word.length(); i++){
                    if(cursor.getNode(word.charAt(i)) == null) {
                        boolean isWord = i == (word.length() - 1);
                        cursor = cursor.addLetter(word.charAt(i), isWord);
                    }
                    else {
                        cursor = cursor.getNode(word.charAt(i));
                        if(i == (word.length() - 1)){
                            cursor.setIsWord(true);
                        }
                    }
                }
                counter++;
            }
            catch (IOException | URISyntaxException e) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show("Tree Creation Error", "There was an error while trying to create the tree.");
            }
        }while(counter < maxBound);
    }

    public boolean searchForWord(String word){
        TrieNode cursor = rootNode;
        for(int i = 0; i < word.length(); i++){
            if(cursor.getNode(word.charAt(i)) == null)
                return false;
            else {
                cursor = cursor.getNode(word.charAt(i));
                if((i == (word.length() - 1)) && cursor.isWord())
                    return true;
            }
        }
        return false;
    }

    public boolean isPrefix(String word){
        TrieNode cursor = rootNode;
        for(int i = 0; i < word.length(); i++){
            if(cursor.getNode(word.charAt(i)) == null)
                return false;
            else {
                cursor = cursor.getNode(word.charAt(i));
                if((i == (word.length() - 1)))
                    return true;
            }
        }
        return false;
    }


}
