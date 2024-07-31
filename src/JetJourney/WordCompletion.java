package JetJourney;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class WordCompletion {
    private TrieNode rootNode = new TrieNode();

    // Reads words from a file, builds a Trie data structure, and searches for word
    // completions
    public void processWordCompletions(String prefixToComplete) throws FileNotFoundException {
        buildTrieFromFile();
        TrieNode prefixNode = findPrefixNode(prefixToComplete.toLowerCase().replaceAll("\\s", ""));
        if (prefixNode != null) {
            System.out.print("\n-----Word Completion-----");
            System.out.println("\nCompletions for the word \"" + prefixToComplete + "\" are...");
            printWordCompletions(prefixNode, prefixToComplete);
        } else {
            System.out.print("\n-----Word Completion-----");
            System.out.println("\n No completions found for \"" + prefixToComplete + "\" ");
        }
    }

    // Builds the Trie data structure by inserting words from the file
    private void buildTrieFromFile() throws FileNotFoundException {
        String citiesFile = JetJourney.SRC_FOLDER_PATH + "cities.txt";
        try (Scanner fileScanner = new Scanner(new FileReader(citiesFile))) {
            while (fileScanner.hasNext()) {
                String word = fileScanner.nextLine().toLowerCase().replaceAll("\\s", "");
                insertWordInTrie(word);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error reading dictionary file: " + e.getMessage());
            System.exit(1);
        }
    }

    // Inserts a word into the Trie
    private void insertWordInTrie(String word) {
        TrieNode currentNode = rootNode;
        for (char c : word.toCharArray()) {
            int index = getCharIndex(c);
            if (index >= 0) {
                if (currentNode.children[index] == null) {
                    currentNode.children[index] = new TrieNode();
                }
                currentNode = currentNode.children[index];
            }
        }
        currentNode.isEndOfWord = true;
    }

    // Finds the Trie node representing the last character of the given prefix
    private TrieNode findPrefixNode(String prefix) {
        TrieNode currentNode = rootNode;
        for (char c : prefix.toCharArray()) {
            int index = getCharIndex(c);
            if (index >= 0 && currentNode.children[index] != null) {
                currentNode = currentNode.children[index];
            } else {
                return null;
            }
        }
        return currentNode;
    }

    // Recursively prints all word completions starting from the given Trie node
    private void printWordCompletions(TrieNode node, String prefix) {
        if (node == null) {
            return;
        }

        if (node.isEndOfWord) {
            System.out.println(prefix);
        }

        for (char c : node.getChildren()) {
            TrieNode childNode = node.children[getCharIndex(c)];
            if (childNode != null) {
                printWordCompletions(childNode, prefix + c);
            }
        }
    }

    // Maps a character to its corresponding index in the Trie node's children array
    private int getCharIndex(char c) {
        return (c >= 'a' && c <= 'z') ? c - 'a' : (c >= 'A' && c <= 'Z') ? c - 'A' + 26 : -1;
    }

    private static class TrieNode {
        private TrieNode[] children = new TrieNode[52];
        private boolean isEndOfWord;

        public TrieNode getChild(char c) {
            int index = getCharIndex(c);
            return (index >= 0 && index < 52) ? children[index] : null;
        }

        public char[] getChildren() {
            ArrayList<Character> chars = new ArrayList<>();
            for (int i = 0; i < 52; i++) {
                if (children[i] != null) {
                    chars.add((i < 26) ? (char) ('a' + i) : (char) ('A' + i - 26));
                }
            }
            char[] charArray = new char[chars.size()];
            for (int i = 0; i < chars.size(); i++) {
                charArray[i] = chars.get(i);
            }
            return charArray;
        }

        private int getCharIndex(char c) {
            return (c >= 'a' && c <= 'z') ? c - 'a' : (c >= 'A' && c <= 'Z') ? c - 'A' + 26 : -1;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        WordCompletion wordCompletion = new WordCompletion();
        wordCompletion.processWordCompletions("lon");
    }
}