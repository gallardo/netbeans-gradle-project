package org.netbeans.gradle.project.util;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;

public final class ExcludeInclude {
    /**
     * 
     * Test if {@code file} should be considered according to the filter patterns provided.
     * @param filePath to test
     * @param rootPath {@code file} must be under this path
     * @param excludePatterns will be translated into {@code glob:} patterns
     * @param includePatterns will be translated into {@code glob:} patterns
     * @return {@code true} if
     * <ul>
     *      <li>{@code file} is under {@code rootPath}</li>
     *      <li><strong>and</strong> doesn't match any {@code excludePatterns}</li>
     *      <li><strong>and</strong> matches at least one {@code includePatterns} <strong>or</strong>
     *          {@code includePatterns} is empty.</li>
     * </ul>
     */
    public static boolean isFileIncludedUnderRootpath(
            Path filePath,
            Path rootPath,
            Collection<String> excludePatterns,
            Collection<String> includePatterns) {

        Path absoluteRoot = rootPath.toAbsolutePath();
        Path testedPath = filePath.toAbsolutePath();

        if (!testedPath.startsWith(absoluteRoot)) {
            return false;
        }

        Path relTestedPath = absoluteRoot.relativize(testedPath);

        if (!includePatterns.isEmpty()) {
            if (!matchesAnyAntPattern(relTestedPath, includePatterns)) {
                return false;
            }
        }

        return !matchesAnyAntPattern(relTestedPath, excludePatterns);
    }

    private static boolean matchesAnyAntPattern(
            Path path,
            Collection<String> patterns) {

        for (String pattern: patterns) {
            if (matchesAntPattern(path, pattern)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesAntPattern(Path path, String pattern) {
        FileSystem fileSystem = path.getFileSystem();
        PathMatcher matcher = fileSystem.getPathMatcher(toMatchStr(pattern));
        return matcher.matches(path);
    }

    private static String toMatchStr(String pattern) {
        String normPattern = pattern.replace("\\\\", "/");

        // 7 = "glob:".length() + "**".length()
        StringBuilder result = new StringBuilder(pattern.length() + 7);
        result.append("glob:");

        String normedDirMatches = normPattern;
        if (normedDirMatches.startsWith("/")) {
            normedDirMatches = normedDirMatches.substring(1);
        }

        normedDirMatches = normedDirMatches.replace("/**/", "{/**/,/}");
        if (normedDirMatches.startsWith("**/")) {
            normedDirMatches = "{**/,}" + normedDirMatches.substring(3);
        }

        result.append(normedDirMatches);
        if (normPattern.endsWith("/")) {
            result.append("**");
        }
        return result.toString();
    }

    private ExcludeInclude() {
        throw new AssertionError();
    }
}
