import searchSystem.IndexType;
import searchSystem.SearchSystem;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author e1327191@student.tuwien.ac.at, e1325974@student.tuwien.ac.at
 *         Created on: 26.11.2015
 */
public class Main {

    public static void main(String [] args) {
        launchCli();
    }

    private static void launchCli() {
        Scanner scanner = new Scanner(System.in);

        SearchSystem searchSystem = null;

        boolean isStemming = false;
        // Vocabulary
        System.out.println("Enter type of normalization for Vocabulary (s for stemming or f for case folding): ");
        while(scanner.hasNext()) {

            String vocabInput = scanner.next();
            boolean validInput = false;

            // check if input is valid
            switch(vocabInput) {
                case "s":
                    isStemming = true;
                    validInput = true;
                    break;
                case "f":
                    isStemming = false;
                    validInput = true;
                    break;
            }
            if ( !validInput) {
                System.out.println("Please enter valid type for the vocabulary normalization!");
            } else {
                break;
            }
        }

        IndexType indexType = null;
        // Vocabulary
        System.out.println("Enter type of indexing (bag for bag-of-words or bi for biword): ");
        while(scanner.hasNext()) {

            String indexingInput = scanner.next();
            boolean validInput = false;

            // check if input is valid
            switch(indexingInput) {
                case "bag":
                    indexType = IndexType.BagOfWords;
                    validInput = true;
                    break;
                case "bi":
                    indexType = IndexType.Biword;
                    validInput = true;
                    break;
            }
            if ( !validInput) {
                System.out.println("Please enter valid type for the indexing!");
            } else {
                break;
            }
        }

        // expecting valid path
        System.out.println("Enter path to newsgroups(data): ");
        // todo: remove comment to get newsgroup path from System.in #####################################
        searchSystem = new SearchSystem("D:\\Wolfi\\workspace_intellij\\gir-ex1\\data_test", isStemming, indexType);
        // for debugging purposes
        //searchSystem = new SearchSystem("../Data/test_data/", isStemming);
        // ###############################################################################################

        /*while(scanner.hasNext()) {
            String pathNewsgroups = scanner.next();

            // check if pathNewsgroups is valid (and a directory)
            try {
                checkFilePathValid(pathNewsgroups);
                searchSystem = new SearchSystem(pathNewsgroups, isStemming, indexType);
                searchSystem.traverseDirectory();
                break;
            } catch (IOException e) {
                System.out.println("Could not find newsgroup-path, please enter a valid path!");
            }
        }*/
        System.out.println("Indexing...");

        // expecting <path to topicfile> -searchtype=<bagofwords|biword>
        System.out.println("Enter path to topic-file: ");
        while(scanner.hasNext()) {

            String pathTopicFile = scanner.next();

            // check if pathNewsgroups is valid (and a directory)
            try {
                checkFilePathValid(pathTopicFile);
                searchSystem.searchTopicFile(pathTopicFile);
                // TODO print search-results in loop (nicely formatted :>)
            } catch (IOException e) {
                System.out.println("Could not find topic-path, please enter a valid path!");
            }
        }
    }

    // checks if path is valid (and a directory)
    private static void checkFilePathValid(String path) throws IOException {
        File file = new File(path);
        if(!file.exists() || !file.isDirectory()) {
            throw new IOException("Folder does not exist");
        }
    }
}
