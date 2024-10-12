import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Git
 */
public class Git implements GitInterface {
    private List<File> commits = new ArrayList<>();

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
            System.out.println("Index file already exists");
            return;
        }
        index.createNewFile();
    }

    public void createHeadFile() throws IOException {
        File head = new File("git" + File.separator + "HEAD");
        if (head.exists()) {
            System.out.println("HEAD file already exists");
            return;
        }
        head.createNewFile();
    }

    public void updateHeadFile(String commitHash) throws IOException {
        File headFile = new File("git" + File.separator + "HEAD");
        try (FileWriter writer = new FileWriter(headFile, false)) {
            writer.write(commitHash);
        }
    }

    public String commit(String message, String author) throws IOException {
        String rootTreeHash = Tree.generateRootTree();
        String commitContents = "tree: " + rootTreeHash + "\n";
        String parentCommitHash = "";
        if (!commits.isEmpty()) {
            File lastCommitFile = commits.getLast();
            parentCommitHash = lastCommitFile.getName();
            commitContents += "parent: " + parentCommitHash + "\n";
        } else {
            commitContents += "parent: \n";
        }
        commitContents += "author: " + author + "\n";
        commitContents += "date: " + new Date().toString() + "\n";
        commitContents += "message: " + message + "\n";
        String commitHash = Blob.createName(commitContents);
        File commitFile = new File("git" + File.separator + "objects" + File.separator + commitHash);
        try (FileWriter commitWriter = new FileWriter(commitFile)) {
            commitWriter.write(commitContents);
        }
        commits.add(commitFile);
        updateHeadFile(commitHash);
        return commitHash;
    }

    public void stage(String path) throws IOException {
        File fileToStage = new File(path);
        if (!fileToStage.exists()) {
            System.out.println("File or directory does not exist.");
            return;
        }
        List<String> currentIndexEntries = readIndex();
        if (fileToStage.isFile()) {
            String fileContent = Blob.readFile(path);
            String fileHash = Blob.createName(fileContent);
            updateIndexEntries(currentIndexEntries, "blob", fileHash, path);
        } else if (fileToStage.isDirectory()) {
            Tree.generateTree(path);
            String treeContent = Blob.readFile("tree_" + fileToStage.getName() + ".txt");
            String treeHash = Blob.createName(treeContent);
            updateIndexEntries(currentIndexEntries, "tree", treeHash, path);
        }
        writeIndex(currentIndexEntries);
    }

    private List<String> readIndex() throws IOException {
        File indexFile = new File("git" + File.separator + "index");
        List<String> indexEntries = new ArrayList<>();

        if (indexFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(indexFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    indexEntries.add(line);
                }
            }
        }
        return indexEntries;
    }

    private void updateIndexEntries(List<String> indexEntries, String type, String hash, String path) {
        String newEntry = type + " " + hash + " " + path;
        boolean entryUpdated = false;
        for (int i = 0; i < indexEntries.size(); i++) {
            String[] entryParts = indexEntries.get(i).split(" ", 3);
            if (entryParts.length == 3 && entryParts[2].equals(path)) {
                indexEntries.set(i, newEntry);
                entryUpdated = true;
                break;
            }
        }
        if (!entryUpdated) {
            indexEntries.add(newEntry);
        }
    }

    private void writeIndex(List<String> indexEntries) throws IOException {
        File indexFile = new File("git" + File.separator + "index");
        try (FileWriter indexWriter = new FileWriter(indexFile, false)) {
            for (String entry : indexEntries) {
                indexWriter.write(entry + System.lineSeparator());
            }
        }
    }

    public void innit() throws IOException {
        makeGitDirectory();
        createObjectsDirectory();
        makeIndexFile();
        createHeadFile();
        System.out.println("Git setup complete");
    }
}
