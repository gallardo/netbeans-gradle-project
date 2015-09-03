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

public final class FilterRules implements Serializable {

    private static final long serialVersionUID = 1L;

    public static FilterRules ALLOW_ALL = new FilterRules(
            FilterPatterns.ALLOW_ALL);

    private final FilterPatterns filterPatterns;

    private FilterRules(FilterPatterns filterPatterns) {
        ExceptionHelper.checkNotNullArgument(filterPatterns, "filterPatterns");
        this.filterPatterns = filterPatterns;
    }

    private static FilterRules create(FilterPatterns filterPatterns) {
        if (filterPatterns.isAllowAll()) {
            return ALLOW_ALL;
        }

        return new FilterRules(filterPatterns);
    }

    public static FilterRules create(JavaSourceGroup sourceGroup) {
        return create(sourceGroup.getFilterPatterns());
    }

    public boolean isAllowAll() {
        return filterPatterns.isAllowAll();
    }

    public boolean isIncluded(Path rootPath, FileObject fileObject) {
        ExceptionHelper.checkNotNullArgument(rootPath, "rootPath");
        ExceptionHelper.checkNotNullArgument(fileObject, "fileObject");

        if (isAllowAll()) {
            return true;
        }

        Path filePath = GradleFileUtils.toPath(fileObject);
        return filePath != null ? isIncluded(rootPath, filePath) : true; // XXX: (null==path)=> true? 
    }

    public boolean isIncluded(Path rootPath, File file) {
        ExceptionHelper.checkNotNullArgument(rootPath, "rootPath");
        ExceptionHelper.checkNotNullArgument(file, "file");

        if (isAllowAll()) {
            return true;
        }

        return isIncluded(rootPath, file.toPath());
    }

    public boolean isIncluded(Path rootPath, Path filePath) {
        ExceptionHelper.checkNotNullArgument(rootPath, "rootPath");
        ExceptionHelper.checkNotNullArgument(filePath, "filePath");

        if (isAllowAll()) {
            return true;
        }

        return ExcludeInclude.isFileIncludedUnderRootpath(
                filePath,
                rootPath,
                filterPatterns.getExcludePatterns(),
                filterPatterns.getIncludePatterns());
    }

    @Override
    public String toString() {
        return "FilterRules{" + filterPatterns + '}';
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.filterPatterns);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (getClass() != obj.getClass()) return false;

        final FilterRules other = (FilterRules)obj;
        return Objects.equals(this.filterPatterns, other.filterPatterns);
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

        public SerializedFormat(FilterRules source) {
            this.sourceIncludePatterns = source.filterPatterns;
        }

        private Object readResolve() throws ObjectStreamException {
            return FilterRules.create(sourceIncludePatterns);
        }
    }
}
