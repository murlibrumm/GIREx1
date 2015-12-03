package searchSystem;

/**
 * @author e1327191@student.tuwien.ac.at, e1325974@student.tuwien.ac.at
 *         Created on: 26.11.2015
 */

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import static java.nio.file.FileVisitResult.*;

public class TraverseDirectory extends SimpleFileVisitor<Path> {

    private ArrayList<File> filesInDir;

    public TraverseDirectory() {
        filesInDir = new ArrayList<File>();
    }

    public ArrayList<File> getFilesInDir() {
        return filesInDir;
    }

    // Add each file to file-List
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        filesInDir.add(new File(String.valueOf(file)));
        return CONTINUE;
    }
}