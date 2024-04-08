import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Minutes {
    private static final String FILE_PATH = "src/Minutes.txt";

    public static String readMinutes() {
        try {
            Path path = Paths.get(FILE_PATH);
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading file.";
        }
    }

    public static void clearFile() {
        try {
            Path path = Paths.get(FILE_PATH);
            Files.write(path, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error clearing file.");
        }
    }

    public static void appendMinutes(String text) {
        try {
            Path path = Paths.get(FILE_PATH);
            Files.write(path, (text + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error writing to file.");
        }
    }
}