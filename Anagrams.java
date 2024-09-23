package com.gradescope.anagrams;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

/**
 * File: Anagrams.java
 * Author: Murali Karthik Ganji
 * Description: Generates all possible anagrams from a given phrase
 * using words from a specified word list. The program finds valid words
 * that can be formed from the letters of the phrase and prints out all
 * combinations of those words that match the length of the original phrase.
 */

public class Anagrams {

    /**
     * Description: Reads a list of words from a specified file.
     * @param filename (String) - the name of the file containing valid words
     * @return HashSet<String> - a set of words read from the file
     * @throws FileNotFoundException if the file is not found
     */
    public static HashSet<String> getWordList(String filename) throws FileNotFoundException {
        HashSet<String> words = new HashSet<String>();
        // Opens the file for reading
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            words.add(scanner.nextLine().trim()); 
        }
        scanner.close(); 
        return words;
     }
    

    /**
     * Description: Converts a given phrase into a list of characters.
     * @param word (String) - the input phrase
     * @return ArrayList<Character> - a list of characters from the phrase
     */
    public static ArrayList<Character> getChars(String word) {
        ArrayList<Character> chars = new ArrayList<Character>();
        for (char c : word.toCharArray()) {
            chars.add(c); 
        }
        return chars;
    }

    /**
     * Description: Finds all valid words that can be formed from the given letters.
     * @param allChars (ArrayList<Character>) - the characters from the phrase
     * @param current (String) - the current word being formed
     * @param validWords (HashSet<String>) - the set of valid words
     * @param solutions (HashSet<String>) - the set of found solutions
     */
    public static void getCombinations(ArrayList<Character> allChars, String current, 
            HashSet<String> validWords, HashSet<String> solutions) {
        // If the current string is valid and not empty, add it to solutions
        if (!current.isEmpty() && validWords.contains(current)) {
            solutions.add(current);
        }
        
        // Generate combinations by choosing each character
        for (int i = 0; i < allChars.size(); i++) {
            char c = allChars.get(i);
            ArrayList<Character> remaining = new ArrayList<Character>(allChars);
            // Removes the chosen character
            remaining.remove(i); 
            getCombinations(remaining, current + c, validWords, solutions); 
        }
    }

    /**
     * Description: Recursively generates all anagrams from the valid words.
     * @param targetLength (int) - the length of the original phrase
     * @param validWords (ArrayList<String>) - the list of valid words
     * @param phrase (String) - the original phrase
     * @param current (ArrayList<String>) - the current combination of words
     * @param maxAnas (int) - the maximum number of words allowed in the anagram
     * @param count (int) - the current count of characters used
     * @param allResults (ArrayList<ArrayList<String>>) - the list of all found results
     */
    public static void getAnagrams(int targetLength, ArrayList<String> validWords, 
            String phrase, ArrayList<String> current, int maxAnas, int count, 
            ArrayList<ArrayList<String>> allResults) {
        // Check if the current solution exceeds the max allowed anagrams
        if (current.size() > maxAnas && maxAnas != -1) return;  
        
        // Get remaining characters after using selected words
        String remaining = getRemainingCharacters(phrase, current);
        
        // If no remaining characters and count matches the target length, store the result
        if (remaining.isEmpty() && (count == targetLength)) {
            allResults.add(new ArrayList<String>(current)); 
            return;
        }
        
        // Try to form words from the remaining characters
        for (String word : validWords) {
            if (canFormWord(remaining, word)) {
                current.add(word); 
                getAnagrams(targetLength, validWords, phrase, current, maxAnas, count + word.length(), allResults);
                // Backtrack by removing the last added word
                current.remove(current.size() - 1);  
            }
        }
    }

    /**
     * Description: Gets the remaining characters after selecting certain words.
     * @param phrase (String) - the original phrase
     * @param current (ArrayList<String>) - the currently selected words
     * @return String - the remaining characters
     */
    public static String getRemainingCharacters(String phrase, ArrayList<String> current) {
        StringBuilder sb = new StringBuilder(phrase);
        // Remove each character used in the current combination from the original phrase
        for (String word : current) {
            for (char c : word.toCharArray()) {
                int index = sb.indexOf(String.valueOf(c));
                if (index != -1) {
                	// Delete the first occurrence of the character
                    sb.deleteCharAt(index); 
                }
            }
        }
        return sb.toString(); 
    }

    /**
     * Description: Checks if a word can be formed from the remaining characters.
     * @param remaining (String) - the characters left to use
     * @param word (String) - the word to check
     * @return boolean - true if the word can be formed, false otherwise
     */
    public static boolean canFormWord(String remaining, String word) {
        // Check if each character of the word exists in the remaining characters
        for (char c : word.toCharArray()) {
            if (remaining.indexOf(c) == -1) {
                return false; // Character not found, return false
            }
        }
        return true; 
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        String wordList = args[0];
        String word = args[1];
        int maxAnas = Integer.valueOf(args[2]);
        if (maxAnas == 0) maxAnas = -1;  // set to -1 for no limit

        System.out.println("Phrase to scramble: " + word);
        
        HashSet<String> validWords = getWordList(wordList);
        HashSet<String> solutions = new HashSet<String>();
        ArrayList<Character> allChars = getChars(word);
        
        getCombinations(allChars, "", validWords, solutions);
        ArrayList<String> orderedSolution = new ArrayList<String>(solutions);
        Collections.sort(orderedSolution);
        
        System.out.println("\nAll words found in " + word + ":");
        System.out.println(orderedSolution);
        
        ArrayList<String> result = new ArrayList<String>();
        System.out.println("\nAnagrams for " + word + ":");
        ArrayList<ArrayList<String>> allResults = new ArrayList<ArrayList<String>>();
        getAnagrams(word.length(), orderedSolution, word, result, maxAnas, 0, allResults);
        for (int i = 0; i < allResults.size(); i++) System.out.println(allResults.get(i));
    }
}

