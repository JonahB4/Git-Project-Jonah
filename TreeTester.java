import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class TreeTester {
    public static void main(String[] args) throws IOException {
        String folder1Name = "folder1";
        String file1Name = "file1";
        String folder2Name = "folder2";
        String file2Name = "file2";
        String file1Contents = "one";
        String file2Contents = "two";

        String folder1Hash = "36848d6ce9b6100b1f5140ba1a313e098328d16d";
        String folder2Hash = "534a0ac056586c39a9315ae8ba3d02fbf809e262";

        String[] treeHashs = new String[] { folder1Hash, folder2Hash };

        // setUp(folder1Name, folder2Name, file1Name, file2Name, file1Contents, file2Contents);//need to already have folder 1 and 2 created
        

        Tree.generateTree(folder1Name);
        verifyTree(folder1Name, treeHashs, 0);
        resetIndexAndObjects();// properly reset everything
    }

    // create a folder
    // create 1 files
    // create a folder
    // create 1 file
    public static void setUp(String folder1, String folder2, String file1, String file2, String file1Contents,
            String file2Contents) {
        File tree1 = new File(folder1);
        File blob1 = new File(folder1 + File.separator + file1);
        File tree2 = new File(folder1 + File.separator + folder2);
        File blob2 = new File(folder1 + File.separator + folder2 + File.separator + file2);

        try (FileWriter writer = new FileWriter(blob1)) {
            writer.write(file1Contents);
            writer.close();
            System.out.println("file:" + file1 + " created, contents:" + file1Contents);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer2 = new FileWriter(blob2)) {
            writer2.write(file2Contents);
            writer2.close();
            System.out.println("file:" + file2 + " created, contents:" + file2Contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // call verify tree on the folder
    // check that the fodler exists
    // check that index has everything correct
    // check that objects has everything correct
    public static void verifyTree(String testTreeName, String[] treeHashs, int counter) {
        File tree = new File(testTreeName);

        // check that the testTree exist in object
        String expectedHash = treeHashs[counter];
        if (counter <= treeHashs.length) {
            counter++;
        }
        File generatedFile = new File("git" + File.separator + "objects" + File.separator + expectedHash);
        if (generatedFile.exists()) {
            System.out.println("Tree " + testTreeName + " is in objects");
        } else {
            System.out.println("Tree " + testTreeName + " missing from objects");
        }
        // check that testTree is in index
        File indexFile = new File("git" + File.separator + "index");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(indexFile));

            String line;
            String expectedLine = "tree " + expectedHash + " " + testTreeName;
            boolean found = false;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.equals(expectedLine)) {
                        found = true;
                        break;
                    }
                }

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (found) {
                System.out.println(testTreeName + " Entry found in index file: PASS");
            } else {
                System.out.println(testTreeName + " Entry missing in index file: FAIL");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Assume that the tree already has files in it for testing purposes
        for (File subFile : tree.listFiles()) {
            if (subFile.isFile()) {
                // check that the blobs exist in objects
                // check that it is in index
                try {
                    BlobTester.verifyBlob(""+subFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (subFile.isDirectory()) {
                verifyTree(""+subFile, treeHashs, counter);
            }
        }

    }

    public static void resetIndexAndObjects() {
        File indexFile = new File("git" + File.separator + "index");
        if (indexFile.exists()) {
            try {
                FileWriter writer = new FileWriter(indexFile, false); // Overwrite mode
                writer.write("");
                writer.close();
                System.out.println("Index file reset to empty.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File objectsDir = new File("git" + File.separator + "objects");
        if (objectsDir.exists() && objectsDir.isDirectory()) {
            File[] files = objectsDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.delete()) {
                        System.out.println("Deleted " + file + " object");
                    } else {
                        System.out.println("Failed to delete "+ file);
                    }
                }
            }
        }
        File project = new File(System.getProperty("user.dir"));
        if (project.exists()){
            File[] fileArray = project.listFiles();
            for (File file:fileArray){
                if (file.getName().endsWith(".txt")){
                    file.delete();
                    System.out.println("deleted: " + file);
                }
            }
        }
    }

}
