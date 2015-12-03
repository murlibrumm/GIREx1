package searchSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.System;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author e1327191@student.tuwien.ac.at, e1325974@student.tuwien.ac.at
 *         Created on: 26.11.2015
 */
public class SearchSystem {

    private String pathNewsgroups;
    // dictionary: word => hashmap <file, occurrence-list>
    private HashMap<String, HashMap<String, ArrayList<Integer>>> dictionary;
    // documents: file => hashmap <word, number of occurrences>
    private HashMap<String, HashMap<String, Integer>> documents;
    // topicWords: word => log(occurences)
    private HashMap<String, Double> topicWords;

    private IndexType indexType;

    private Stemmer stemmer;
    private TFIDF tfidf;

    // expects a valid path
    public SearchSystem(String pathNewsgroups, boolean isStemming, IndexType indexType) {
        dictionary = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
        documents = new HashMap<String, HashMap<String, Integer>>();
        topicWords = new HashMap<String, Double>();

        this.pathNewsgroups = pathNewsgroups;
        this.indexType = indexType;

        if (isStemming) {
            stemmer = new Stemmer();
        }

        try {
            System.out.println("Indexing...");
            traverseDirectory();
        } catch (IOException e) {
            System.out.println("I/O Error while traversing Directory: " + e.getMessage());
        }

        tfidf = new TFIDF(dictionary, documents);

        // todo: remove me! #################################################################
        // for debugging purposes
        /*for (Map.Entry<String, HashMap<String, ArrayList<Integer>>> entry : dictionary.entrySet()) {
            String key = entry.getKey();
            HashMap<String, ArrayList<Integer>> value = entry.getValue();

            System.out.println("word: " + key);
            for (Map.Entry<String, ArrayList<Integer>> innerEntry : value.entrySet()) {
                String innerKey = innerEntry.getKey();
                ArrayList<Integer> innerValue = innerEntry.getValue();

                System.out.println("   document: " + innerKey);
                System.out.print ("      ");
                for (int i : innerValue) {
                    System.out.print (i + ", ");
                }
                System.out.println();
            }
        }
        System.out.println("finished");*/
        // ####################################################################################
    }

    // traverses a directory via SimpleFileVisitor and calls indexFile() for each file
    // https://docs.oracle.com/javase/tutorial/essential/io/walk.html
    private void traverseDirectory() throws IOException{
        // walk file tree recursively (via class TraverseDirectory extends SimpleFileVisitor)
        Path directory = Paths.get(pathNewsgroups);
        TraverseDirectory traverseDirectory = new TraverseDirectory();
        Files.walkFileTree(directory, traverseDirectory);

        // call indexFile for each file in the directory
        ArrayList<File> filesInDir = traverseDirectory.getFilesInDir();
        for (File file : filesInDir) {
            indexFile(file, true);
        }
    }

    // splits a file into header and content, nomalizes the content, which is indexed by indexConcatLine()
    private void indexFile(File file, boolean forDictionary) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
            // The number of lines of content in the file
            int lines = 0;

            // Remove Email-header
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.startsWith("Lines: ")) {
                    lines = Integer.parseInt(line.split(" ")[1].trim());
                    break;
                }
            }

            // iterate over lines after header, remove special strings, special characters, multiple spaces
            String concatLines = "";
            for (int i = 0; i < lines; i++) {
                String line = br.readLine();

                if (line == null || line.isEmpty()) {
                    continue;
                }

                line = line.trim();

                // remove email-addresses
                line = line.replaceAll("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b", "");
                // remove special characters
                line = line.replaceAll("(\\.|\\?|!|\\(|\\)|<|>|,|;|:|\"|'|_|-|\\*|\\[\\])", "");
                // remove all double or more spaces
                line = line.replaceAll(" +", " ");
                // split line at occurences of white-space
                line = line.trim();

                if (line == null || line.isEmpty()) {
                    continue;
                }

                concatLines += line + " ";
            }

            indexConcatLines(concatLines, getFilePathFromParentFolder(file), forDictionary);
        }
    }

    // returns the Path from a File, relative from its parent's folder
    private String getFilePathFromParentFolder(File file) {
        return file.getParentFile().getName() + "\\" + file.getName();
    }

    // splits a the concatLines into logical modules (bagofwords (1 word) or biword (2 words))
    // and updates/saves occurrences in hashmap
    private void indexConcatLines(String concatLines, String fileName, boolean forDictionary) {
        int index = 0;
        int words = 0;

        // iterate over all words / biwords
        while (true) {
            String word;

            // get index of the first blank (second blank in case of biword), and get the word / biword
            int indexFirstBlank = index + concatLines.substring(index).indexOf(" ");
            int indexSecondBlank;
            if (indexFirstBlank < index) {
                return;
            }
            if (indexType == IndexType.Biword) {
                indexSecondBlank = indexFirstBlank + 1 + concatLines.substring(indexFirstBlank + 1).indexOf(" ");
                if (indexSecondBlank < indexFirstBlank + 1) {
                    return;
                }
                word = concatLines.substring(index, indexSecondBlank);
            } else {
                word = concatLines.substring(index, indexFirstBlank);
            }

            // stemming / folding
            if (stemmer != null) {
                if (indexType == IndexType.Biword) {
                    word = stem(word.substring(0, word.indexOf(" "))) +
                            " " + stem(word.substring(word.indexOf(" ") + 1, word.length()));
                } else {
                    word = stem(word);
                }
            } else {
                word = folding(word);
            }

            // shift index to the next word
            index = indexFirstBlank + 1;

            if (forDictionary) {
                // if the word isn't in the dictionary, add it with empty HashMap
                if (!dictionary.containsKey(word)) {
                    dictionary.put(word, new HashMap<String, ArrayList<Integer>>());
                }

                // same for the document-HashMap
                if (!documents.containsKey(fileName)) {
                    documents.put(fileName, new HashMap<String, Integer>());
                }

                // get HashMap with all occurrences of the word
                HashMap<String, ArrayList<Integer>> dictionaryMap = dictionary.get(word);

                // get HashMap with all words of the document
                HashMap<String, Integer> documentMap = documents.get(fileName);

                // if the current file is not in the dictionary-HashMap, add it
                if (!dictionaryMap.containsKey(fileName)) {
                    dictionaryMap.put(fileName, new ArrayList<Integer>());
                }

                // if the current word is not in the documents-HashMap, add it
                if (!documentMap.containsKey(fileName)) {
                    documentMap.put(word, 0);
                }

                // add the (word)-position to the dictionary-HashMap
                dictionaryMap.get(fileName).add(words++);

                // add the (word)-number to the documents-HashMap
                documentMap.put(word, documentMap.get(word) + 1);
            } else {
                if (topicWords.containsKey(word)){
                    topicWords.put(word, topicWords.get(word) + 1);
                } else {
                    topicWords.put(word, 1d);
                }
            }
        }
    }

    // stem a word via the class Stemmer
    // expectation stemmer not null
    private String stem(String word) {
        word = word.toLowerCase();

        stemmer.add(word.toCharArray(), word.length());
        stemmer.stem();
        return stemmer.toString();
    }

    // case folding a word
    private String folding(String word){
        word = word.toUpperCase();
        word = word.toLowerCase();
        return word;
    }

    // searches for topicFile (via indexFile() & indexConcatLines())
    public void searchTopicFile(String pathTopicFile) throws IOException{
        File topic = new File(pathTopicFile);
        indexFile(topic, false);

        // use logarithmic occurrences
        for (Map.Entry<String, Double> entry : topicWords.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            entry.setValue(Math.log(1d + value));
        }

        tfidf.createIFIDFForTopic(topicWords);
    }
}
