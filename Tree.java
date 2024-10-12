import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Tree {
    public static void generateTree(String inputPath) {
        // Create the empty folder and manually fill it for testing purposes
        File tree = new File(inputPath);
        File[] files = tree.listFiles();

        // Ensure the tree file is created for this directory
        String treeFileName = "tree_" + tree.getName() + ".txt";
        try {
            FileWriter treeFileWriter = new FileWriter(treeFileName, false);
            for (File subFile : files) {
                String subFileInputPath = subFile.getPath();
                String type = "";
                String textFromFile = "";

                // Check if the file is not a directory blob it
                if (subFile.isFile()) {
                    type = "blob";
                    try {
                        Blob.generateNewBlob(subFileInputPath);
                        textFromFile = Blob.readFile(subFileInputPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (subFile.isDirectory()) {
                    // If your file is a tree, go through the files in it
                    type = "tree";
                    generateTree(subFileInputPath); // Recursive call
                    textFromFile = Blob.readFile("tree_" + subFile.getName() + ".txt");
                }

                String subHash = Blob.createName(textFromFile);
                treeFileWriter.write(type + " " + subHash + " " + subFileInputPath + System.lineSeparator());
            }
            treeFileWriter.close(); // Close the writer after writing all entries
        } catch (IOException e) {
            e.printStackTrace();
        }

        String textFromFile = "";
        try {
            textFromFile = Blob.readFile(treeFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Enter the tree's contents (what was in the temp tree text file) with the hash
        // as name into objects
        String hash = Blob.createName(textFromFile);
        File objectsDir = new File("git" + File.separator + "objects");
        if (!objectsDir.exists()) {
            objectsDir.mkdirs();
        }

        File newFile = new File(objectsDir, hash);
        try (FileWriter newFileWriter = new FileWriter(newFile)) {
            newFileWriter.write(textFromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File indexFile = new File("git" + File.separator + "index");
        try (FileWriter indexWriter = new FileWriter(indexFile, true)) {
            indexWriter.write("tree " + hash + " " + inputPath + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateRootTree() throws IOException {
        // have the root be the git directory
        String currentDir = "git";
        generateTree(currentDir);
        return Blob.createName(Blob.readFile("tree_" + new File(currentDir).getName() + ".txt"));
    }
}
