import searchSystem.SearchSystem;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author e1327191@student.tuwien.ac.at
 *         Created on: 26.11.2015
 */
public class Main {

    public static void main(String [] args) {
        launchCli();
    }

    private static void launchCli() {
        Scanner scanner = new Scanner(System.in);

        SearchSystem searchSystem = null;

        // Expecting valid path
        System.out.println("Enter path to newsgroups(data): ");
        while(scanner.hasNext()) {
            String pathNewsgroups = scanner.next();

            // check if pathNewsgroups is valid (and a directory)
            try {
                checkFilePathValid(pathNewsgroups);
                searchSystem = new SearchSystem(pathNewsgroups);
                break;
            } catch (IOException e) {
                System.out.println("Could not find newsgroup-path, please enter a valid path!");
            }
        }

        while(scanner.hasNext()) {
            // Expecting <path to topicfile> -searchtype=<bagofwords|biword>
            System.out.println("Enter path to topic-file: ");

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
