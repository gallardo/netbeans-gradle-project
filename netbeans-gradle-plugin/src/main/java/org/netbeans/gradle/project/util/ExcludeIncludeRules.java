package org.netbeans.gradle.project.util;

import java.io.File;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Objects;
import org.jtrim.utils.ExceptionHelper;
import org.netbeans.gradle.model.java.JavaSourceGroup;
import org.netbeans.gradle.model.java.FilterPatterns;
import org.openide.filesystems.FileObject;

public final class ExcludeIncludeRules implements Serializable {
    private static final long serialVersionUID = 1L;

    public static ExcludeIncludeRules ALLOW_ALL = new ExcludeIncludeRules(
            FilterPatterns.ALLOW_ALL);

    private final FilterPatterns sourceIncludePatterns;

    private ExcludeIncludeRules(FilterPatterns sourceIncludePatterns) {
        ExceptionHelper.checkNotNullArgument(sourceIncludePatterns, "sourceIncludePatterns");
        this.sourceIncludePatterns = sourceIncludePatterns;
    }

    private static ExcludeIncludeRules create(FilterPatterns filterPatterns) {
        if (filterPatterns.isAllowAll()) {
            return ALLOW_ALL;
        }

        return new ExcludeIncludeRules(filterPatterns);
    }

    public static ExcludeIncludeRules create(JavaSourceGroup sourceGroup) {
        return create(sourceGroup.getFilterPatterns());
    }

    public boolean isAllowAll() {
        return sourceIncludePatterns.isAllowAll();
    }

    public boolean isIncluded(Path rootPath, FileObject file) {
        ExceptionHelper.checkNotNullArgument(rootPath, "rootPath");
        ExceptionHelper.checkNotNullArgument(file, "file");

        if (isAllowAll()) {
            return true;
        }

        Path path = GradleFileUtils.toPath(file);
        return path != null ? isIncluded(rootPath, path) : true; // XXX: (null==path)=> true? 
    }

    public boolean isIncluded(Path rootPath, File file) {
        ExceptionHelper.checkNotNullArgument(rootPath, "rootPath");
        ExceptionHelper.checkNotNullArgument(file, "file");

        if (isAllowAll()) {
            return true;
        }

        return isIncluded(rootPath, file.toPath());
    }

    public boolean isIncluded(Path rootPath, Path file) {
        ExceptionHelper.checkNotNullArgument(rootPath, "rootPath");
        ExceptionHelper.checkNotNullArgument(file, "file");

        if (isAllowAll()) {
            return true;
        }

        return ExcludeInclude.isFileIncludedUnderRootpath(
                file,
                rootPath,
                sourceIncludePatterns.getExcludePatterns(),
                sourceIncludePatterns.getIncludePatterns());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.sourceIncludePatterns);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (getClass() != obj.getClass()) return false;

        final ExcludeIncludeRules other = (ExcludeIncludeRules)obj;
        return Objects.equals(this.sourceIncludePatterns, other.sourceIncludePatterns);
    }

    private Object writeReplace() {
        return new SerializedFormat(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Use proxy.");
    }

    private static final class SerializedFormat implements Serializable {
        private static final long serialVersionUID = 1L;

        private final FilterPatterns sourceIncludePatterns;

        public SerializedFormat(ExcludeIncludeRules source) {
            this.sourceIncludePatterns = source.sourceIncludePatterns;
        }

        private Object readResolve() throws ObjectStreamException {
            return ExcludeIncludeRules.create(sourceIncludePatterns);
        }
    }
}
