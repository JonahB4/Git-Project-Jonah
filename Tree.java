import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Tree {
    public static void generateTree(String inputPath) {

        // create the empty folder
        // manually fill it for testing purposes
        File tree = new File(inputPath);

        // Assume that the tree already has files in it for testing purposes
        File[] Files = tree.listFiles();
        for (File subFile : Files) {
            String subFileInputPath = "" + subFile;
            String type = "";
            String textFromFile = "";
            // if the file is not a folder blob it
            if (subFile.isFile()) {
                type = "blob";
                try {
                    Blob.generateNewBlob(subFileInputPath);
                    textFromFile = Blob.readFile(subFileInputPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (subFile.isDirectory()) {
                // if your file is a tree go throguh the files in it
                type = "tree";
                generateTree(subFileInputPath);
                try {
                    textFromFile = Blob.readFile("tree_" + subFile.getName() + ".txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String subHash = Blob.createName(textFromFile);

            try (FileWriter writer = new FileWriter("tree_" + tree.getName() + ".txt", true)) {
                File existingFile = new File ("tree_" + tree.getName()+".txt");
                BufferedReader reader = new BufferedReader(new FileReader(existingFile));
                if (existingFile.exists()&&reader.readLine()!=null){
                    writer.write(System.lineSeparator());
                }
                writer.write(type + " " + subHash + " " + subFileInputPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String textFromFile = "";
        try {
            textFromFile = Blob.readFile("tree_" + tree.getName() + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // enter the trees contents(what was in the tep tree text file) with the hash as
        // name into objects
        String hash = Blob.createName(textFromFile);
        File objectsDir = new File("git" + File.separator + "objects");
        if (!objectsDir.exists()) {
            objectsDir.mkdirs();
        }

        File newFile = new File(objectsDir, hash);
        try (FileWriter newFileWriter = new FileWriter(newFile)) {
            newFileWriter.write(textFromFile);
            newFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // enter (tree hash trename) into the index

        File indexFile = new File("git" + File.separator + "index");

        try (FileWriter indexWriter = new FileWriter(indexFile, true)) {
            indexWriter.write("tree" + " " + hash + " " + inputPath+System.lineSeparator());
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // -Notes: make a temporary text file that you add the index format to
        // This is what you hash to get the trees hash
        // you do this by savaing the file paths and using those to input the

    }

}
