import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;

public class BlobTester {

    public static void main(String[] args) throws IOException {
        String testFileName = "sample.txt";
        createTestFile(testFileName, "asdoiafiasdfioasdfpiuaspdoifnpansdfiopasdfno");
        Blob.generateNewBlob(testFileName);
        verifyBlob(testFileName);
        resetFiles();
    }

    private static void createTestFile(String fileName, String content) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        writer.write(content);
        writer.close();
        System.out.println("Test file '" + fileName + "' created.");
    }

    private static void verifyBlob(String testFileName) throws IOException {
        String textFromFile = Blob.readFile(testFileName);
        String expectedHash = Blob.createName(textFromFile);
        File generatedFile = new File("git" + File.separator + "objects" + File.separator + expectedHash);
        if (generatedFile.exists()) {
            System.out.println("Blob file created");
        } else {
            System.out.println("Blob file missing");
        }
        File indexFile = new File("git" + File.separator + "index");
        BufferedReader reader = new BufferedReader(new FileReader(indexFile));
        String line;
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(expectedHash) && line.endsWith(testFileName)) {
                found = true;
                break;
            }
        }
        reader.close();
        if (found) {
            System.out.println("Entry found in index file: PASS");
        } else {
            System.out.println("Entry missing in index file: FAIL");
        }
    }

    private static void resetFiles() throws IOException {
        File indexFile = new File("git" + File.separator + "index");
        if (indexFile.exists()) {
            FileWriter writer = new FileWriter(indexFile, false); // Overwrite mode
            writer.write("");
            writer.close();
            System.out.println("Index file reset to empty.");
        }
        File objectsDir = new File("git" + File.separator + "objects");
        if (objectsDir.exists() && objectsDir.isDirectory()) {
            File[] files = objectsDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.delete()) {
                        System.out.println("Deleted blob object");
                    } else {
                        System.out.println("Failed to delete blob");
                    }
                }
            }
        }
        File testFile = new File("sample.txt");
        if (testFile.exists() && testFile.delete()) {
            System.out.println("Deleted test file: " + testFile.getPath());
        } else {
            System.out.println("Failed to delete test file: " + testFile.getPath());
        }
    }
}
