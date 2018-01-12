package br.com.uol.pagseguro.plugpag;

import br.com.uol.pagseguro.plugpag.log.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathAdapter {

    // -----------------------------------------------------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------------------------------------------------

    public static final String JAVA_LIBRARY_PATH = "java.library.path";
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");
    public static final String CURRENT_WORKING_DIRECTORY = "user.dir";
    public static final String DIRECTORY_SEPARATOR = File.separator;

    // -----------------------------------------------------------------------------------------------------------------
    // System path manipulation
    // -----------------------------------------------------------------------------------------------------------------

    public void addPathRelativeToApp(String pathToAdd) {
        List<String> pathTokens = null;
        String cwd = null;
        String newPath = null;
        Field sysPathsField = null;

        cwd = this.getCurrentWorkingDirectory();

        if (!cwd.endsWith(PathAdapter.DIRECTORY_SEPARATOR)) {
            cwd += PathAdapter.DIRECTORY_SEPARATOR;
        }

        if (pathToAdd.endsWith(PathAdapter.DIRECTORY_SEPARATOR)) {
            newPath = pathToAdd;
        } else {
            newPath = pathToAdd + PathAdapter.DIRECTORY_SEPARATOR;
        }

        pathTokens = this.getPathTokens();
        pathTokens.add(cwd + newPath);
        System.setProperty(PathAdapter.JAVA_LIBRARY_PATH, this.join(pathTokens, PathAdapter.PATH_SEPARATOR));

        try {
            sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (Exception e) {

        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Current working directory
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the current working directory.
     *
     * @return Current working directory.
     */
    private String getCurrentWorkingDirectory() {
        return System.getProperty(PathAdapter.CURRENT_WORKING_DIRECTORY);
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Path retrieval
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the PATH tokens.
     *
     * @return PATH tokens.
     */
    private List<String> getPathTokens() {
        List<String> tokens = null;
        String pathString = null;

        pathString = System.getProperty(PathAdapter.JAVA_LIBRARY_PATH);
        tokens = new ArrayList<>(Arrays.asList(pathString.split(PathAdapter.PATH_SEPARATOR)));

        return tokens;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Join Strings
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Joins a List of Strings into a single String.
     *
     * @param tokens Tokens to be joined.
     * @param glue   Glue used to join the tokens.
     * @return Joined String tokens.
     */
    private String join(List<String> tokens, String glue) {
        StringBuilder builder = null;

        builder = new StringBuilder();

        if (tokens != null && glue != null) {
            for (int i = 0; i < tokens.size() - 1; i++) {
                builder.append(tokens.get(i) + glue);
            }

            builder.append(tokens.get(tokens.size() - 1));
        }

        return builder.toString();
    }

}
