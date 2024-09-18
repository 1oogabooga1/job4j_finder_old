package search;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchingFiles {

    static FilesVisitor visitor = new FilesVisitor();

    public static void find(ArgsName args) {
        String directory = args.get("d");
        String file = args.get("n");
        String type = args.get("t");
        String out = args.get("o");
        Path dir = Path.of(directory);
        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(out))) {
            if ("name".equals(type)) {
                Files.walkFileTree(dir, visitor);
                for (Path path : visitor.getPaths()) {
                    if (file.equals(path.toFile().getName())) {
                        output.write(path.toFile().getName().getBytes());
                        output.write(System.lineSeparator().getBytes());
                    }
                }
            }
            if ("mask".equals(type)) {
                if (file.contains(".")) {
                    String[] parts = file.split("\\.", 2);
                    file = String.format("%s\\.%s", parts[0], parts[1]);
                }
                if (file.contains("*")) {
                    String[] parts = file.split("\\*", 2);
                    file = String.format("%s[a-z]*%s", parts[0], parts[1]);
                }
                regex(file, dir, output);
            }

            if ("regex".equals(type)) {
                regex(file, dir, output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void regex(String file, Path dir, BufferedOutputStream output) throws IOException {
        Pattern pat = Pattern.compile(file);
        Files.walkFileTree(dir, visitor);
        for (Path path : visitor.getPaths()) {
            Matcher mat = pat.matcher(path.toFile().getName());
            while (mat.find()) {
                output.write(path.toFile().getName().getBytes());
                output.write(System.lineSeparator().getBytes());
            }
        }
    }

    public static void main(String[] args) {
        ArgsName argsName = ArgsName.of(args);
        find(argsName);
    }
}
