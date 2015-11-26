package searchSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author e1327191@student.tuwien.ac.at
 *         Created on: 26.11.2015
 */
public class SearchSystem {

    String pathNewsgroups;
    HashMap<String, HashMap<String, ArrayList<Integer>>> dictionary;

    // expects a valid path
    public SearchSystem(String pathNewsgroups) {
        dictionary = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
        this.pathNewsgroups = pathNewsgroups;
    }

    // traverses a directory and calls indexFile() for each file
    private void traverseDirectory() {
        File directory = new File(pathNewsgroups);

        ArrayList<File> filesFromTraversedPath = new ArrayList<File>();

        File[] folders = directory.listFiles();

        // walk file tree recursively (extra class)
        // https://docs.oracle.com/javase/tutorial/essential/io/walk.html

        // traverse via for filetreeclass.getFileList, call indexFile for each file
    }

    // splits a file into lines, which are indexed by indexLine()
    private void indexFile(File file) {

    }

    // splits a line into logical modules (bagofwords (1 word) or biword (2 words))
    // and updates/saves occurences in hashmap
    private int indexLine(String line) {
        return 0;
    }

    // searches for topicFile (via indexFile() & indexLine())
    public String searchTopicFile(String pathTopicFile) {
        return null;
    }
}
