package data;

import java.io.Serializable;

/**
 * @author Tejas
 */
public class TrieNode implements Serializable{
    private static final long serialVersionUID = 1L;

    private TrieNode[] childNodes;
    private char letter;
    private boolean isWord;

    public TrieNode(){
        childNodes = new TrieNode[26];
        isWord = false;
        letter = Character.MIN_VALUE;
    }

    public TrieNode getNode(char character){
        return childNodes[Character.toUpperCase(character) - 'A'];
    }

    public TrieNode addLetter(char character, boolean isEndOfWord){
        childNodes[Character.toUpperCase(character) - 'A'] = new TrieNode();
        childNodes[Character.toUpperCase(character) - 'A'].setLetter(character);
        childNodes[Character.toUpperCase(character) - 'A'].setIsWord(isEndOfWord);

        return childNodes[Character.toUpperCase(character) - 'A'];
    }

    public boolean isWord(){
        return isWord;
    }

    public void setIsWord(boolean value){
        isWord = value;
    }

    private void setLetter(char character){
        this.letter = character;
    }
}
