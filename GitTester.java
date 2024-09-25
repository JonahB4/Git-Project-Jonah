import java.io.File;
import java.io.IOException;

public class GitTester {
    public static void main(String[] args) throws IOException {
        Git git = new Git();
        git.innit();
        checkSetup();
        cleanUp();
    }

    private static void checkSetup() {
        File gitDir = new File("git");
        File objectsDir = new File("git" + File.separator + "objects");
        File indexFile = new File("git" + File.separator + "index");
        if (gitDir.exists() && gitDir.isDirectory()) {
            System.out.println("Git directory exists");
        } else {
            System.out.println("Git directory doesnt exist");
        }
        if (objectsDir.exists() && objectsDir.isDirectory()) {
            System.out.println("Objects directory exists");
        } else {
            System.out.println("Objects directory doesn't");
        }
        if (indexFile.exists() && indexFile.isFile()) {
            System.out.println("Index file exist");
        } else {
            System.out.println("Index file doesn't");
        }
    }

    private static void cleanUp() {
        File indexFile = new File("git" + File.separator + "index");
        File objectsDir = new File("git" + File.separator + "objects");
        File gitDir = new File("git");
        if (indexFile.exists() && indexFile.isFile()) {
            if (indexFile.delete()) {
                System.out.println("Deleted: " + indexFile.getPath());
            } else {
                System.out.println("Failed to delete: " + indexFile.getPath());
            }
        }
        if (objectsDir.exists() && objectsDir.isDirectory()) {
            File[] files = objectsDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.delete()) {
                        System.out.println("Deleted: " + file.getPath());
                    } else {
                        System.out.println("Failed to delete: " + file.getPath());
                    }
                }
            }
            if (objectsDir.delete()) {
                System.out.println("Deleted: " + objectsDir.getPath());
            } else {
                System.out.println("Failed to delete: " + objectsDir.getPath());
            }
        }
        if (gitDir.exists() && gitDir.isDirectory()) {
            if (gitDir.delete()) {
                System.out.println("Deleted: " + gitDir.getPath());
            } else {
                System.out.println("Failed to delete: " + gitDir.getPath());
            }
        }
    }
}
