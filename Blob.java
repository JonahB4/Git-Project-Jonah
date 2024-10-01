import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Blob {
    public Blob() {

    }
    
    public static String createName(String password) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
        boolean startingLine = true;
            while ((line = reader.readLine()) != null) {
                if (startingLine){
                    stringBuilder.append(line);
                    startingLine = false;
                }else{
                    stringBuilder.append(ls);
                    stringBuilder.append(line);
                }
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }

    // 2. **Create** a new blob given a filename via the following behavior
    // 1. Generate a unique filename given the file’s data`objects` directory using
    // the unique filename
    // 2. Copy the file content into the new file *inside* the `objects` directory

    public static void generateNewBlob(String inputFilePath) throws IOException {
        String textFromFile = readFile(inputFilePath);
        String filename = createName(textFromFile); // this line gets the hash
        File objectsDir = new File("git" + File.separator + "objects");
        if (!objectsDir.exists()) {
            objectsDir.mkdirs();
        }
        File newFile = new File(objectsDir, filename);
        FileWriter newFileWriter = new FileWriter(newFile);
        newFileWriter.write(textFromFile);
        newFileWriter.close();
        File indexFile = new File("git" + File.separator + "index");
        FileWriter indexWriter = new FileWriter(indexFile, true);

        indexWriter.write("blob" + " " + filename + " " + inputFilePath +System.lineSeparator());
        indexWriter.close();
    }

}
