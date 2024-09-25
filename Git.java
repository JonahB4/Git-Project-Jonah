import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Git
 */
public class Git {
    public Git() {

    }

    public void makeGitDirectory() {
        File gitFile = new File("git");
        if (gitFile.exists()) {
            System.out.println("Git folder already exists");
            return;
        }
        gitFile.mkdir();
    }

    public void createObjectsDirectory() {
        File objects = new File("git" + File.separator + "objects");
        if (objects.exists()) {
            System.out.println("Objects folder already exists");
            return;
        }
        objects.mkdir();
    }

    public void makeIndexFile() throws IOException {
        File index = new File("git" + File.separator + "index");
        if (index.exists()) {
            System.out.println("Git folder doesn't exist");
            return;
        }
        index.createNewFile();
    }

    public void innit() throws IOException {
        makeGitDirectory();
        createObjectsDirectory();
        makeIndexFile();
        System.out.println("Git setup complete");
    }
}
