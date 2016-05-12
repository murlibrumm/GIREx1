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
            switch (vocabInput) {
                case "s":
                    isStemming = true;
                    validInput = true;
                    break;
                case "f":
                    isStemming = false;
                    validInput = true;
                    break;
            }
            if (!validInput) {
                System.out.println("ERROR: Please enter valid type for the vocabulary normalization!");
                System.out.println("Enter type of normalization for Vocabulary (s for stemming or f for case folding): ");
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
            switch (indexingInput) {
                case "bag":
                    indexType = IndexType.BagOfWords;
                    validInput = true;
                    break;
                case "bi":
                    indexType = IndexType.Biword;
                    validInput = true;
                    break;
            }
            if (!validInput) {
                System.out.println("ERROR: Please enter valid type for the indexing!");
                System.out.println("Enter type of indexing (bag for bag-of-words or bi for biword): ");
            } else {
                break;
            }
        }

        boolean considerDocumentLength = false;
        // Vocabulary
        System.out.println("Enter if document-length should be considered by scoring-algorithm (y for yes or n for no): ");
        while(scanner.hasNext()) {

            String scoringInput = scanner.next();
            boolean validInput = false;

            // check if input is valid
            switch (scoringInput) {
                case "y":
                    considerDocumentLength = true;
                    validInput = true;
                    break;
                case "n":
                    considerDocumentLength = false;
                    validInput = true;
                    break;
            }
            if (!validInput) {
                System.out.println("ERROR: Please enter valid command!");
                System.out.println("Enter if document-length should be considered by scoring-algorithm (y for yes or n for no): ");
            } else {
                break;
            }
        }

        // expecting valid path
        System.out.println("Enter path to newsgroups(data): ");
        // todo: remove (for debugging purposes) ########################################################
        // searchSystem = new SearchSystem("D:\\Wolfi\\workspace_intellij\\gir-ex1\\data_test", isStemming, indexType);
        // searchSystem = new SearchSystem("../Data/test_data/", isStemming);
        // ###############################################################################################

        while(scanner.hasNext()) {
            String pathNewsgroups = scanner.next();

            // check if pathNewsgroups is valid (and a directory)
            try {
                checkFilePathValid(pathNewsgroups, true);
                searchSystem = new SearchSystem(pathNewsgroups, isStemming, indexType, considerDocumentLength);
                break;
            } catch (IOException e) {
                System.out.println("ERROR: Could not find newsgroup-path, please enter a valid path!");
                System.out.println("Enter path to newsgroups(data): ");
            }
        }

        // expecting valid path
        System.out.println("Enter path to topic-file: ");
        while(scanner.hasNext()) {
            String pathTopicFile = scanner.next();

            // check if pathTopicFile is valid (and not a directory)
            try {
                checkFilePathValid(pathTopicFile, false);
                searchSystem.searchTopicFile(pathTopicFile);
                System.out.println("\nEnter path to topic-file: ");
            } catch (IOException e) {
                System.out.println("ERROR: Could not find topic-path, please enter a valid path!");
                System.out.println("Enter path to topic-file: ");
            }
        }
    }

    // checks if path is valid (file exists and type is according to boolean directory)
    private static void checkFilePathValid(String path, boolean directory) throws IOException {
        File file = new File(path);
        if (!file.exists() || !((directory && file.isDirectory()) || (!directory && !file.isDirectory()))) {
            throw new IOException("Folder does not exist");
        }
    }
}
