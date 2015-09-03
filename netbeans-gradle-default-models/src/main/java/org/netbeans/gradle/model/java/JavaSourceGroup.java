package org.netbeans.gradle.model.java;

import java.io.File;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import org.netbeans.gradle.model.util.CollectionUtils;

/**
 * Defines a set of source roots of a Gradle project. For example, a source
 * group can be the group of java source files or the group of resource files
 * within a particular source set.</P>
 * In the gradle build script they can be set as shown in the code below:
 * <pre>
 * sourceSets {
 *     main.java.srcDirs = ["javadir1","javadir2"] // "Main JAVA" JavaSourceGroup
 *     main.resources.srcDirs = ["resdir1","resdir2"] // "Main RESOURCES" JavaSourceGroup
 * }
 * </pre>
 * <P>
 * Instances of this class are immutable and therefore are safe to be shared
 * across multiple threads.
 * <P>
 * The serialized format of this class is not subject to any kind of backward
 * or forward compatibility.
 */
public class JavaSourceGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private final JavaSourceGroupName groupName;
    private final Set<File> sourceRoots;

    private final FilterPatterns patternFilters;

    /**
     * Creates a new {@code JavaSourceGroup} with the given properties.
     *
     * @param groupName the name of these source roots representing the type of
     *   sources they contain. This argument cannot be {@code null}.
     * @param sourceRoots the set of source roots of this source group. This
     *   argument cannot be {@code null} and cannot contain {@code null}
     *   elements.
     *
     * @throws NullPointerException thrown if any of the arguments is
     *   {@code null}
     */
    public JavaSourceGroup(JavaSourceGroupName groupName, Collection<? extends File> sourceRoots) {
        this(groupName, sourceRoots, FilterPatterns.ALLOW_ALL);
    }

    /**
     * Creates a new {@code JavaSourceGroup} with the given properties.
     *
     * @param groupName the name of these source roots representing the type of
     *   sources they contain. This argument cannot be {@code null}.
     * @param sourceRoots the set of source roots of this source group. This
     *   argument cannot be {@code null} and cannot contain {@code null}
     *   elements.
     * @param filterPatterns the exclude and include patterns used
     *   to further exclude sources from this source group. This argument
     *   cannot be {@code null}.
     *
     * @throws NullPointerException thrown if any of the arguments is
     *   {@code null}
     */
    public JavaSourceGroup(
            JavaSourceGroupName groupName,
            Collection<? extends File> sourceRoots,
            FilterPatterns filterPatterns) {
        if (groupName == null) throw new NullPointerException("groupName");
        if (filterPatterns == null) throw new NullPointerException("filterPatterns");

        this.groupName = groupName;
        this.sourceRoots = CollectionUtils.copyToLinkedHashSet(sourceRoots);
        this.patternFilters = filterPatterns;

        CollectionUtils.checkNoNullElements(this.sourceRoots, "sourceRoots");
    }

    /**
     * Returns the name of these source roots representing the type of sources
     * they contain. That is, this method returns the name of a particular
     * {@code org.gradle.api.file.SourceDirectorySet}. For example, in the code
     * below:
     * <pre>
     * sourceSets {
     *     main {
     *         java {
     *             // ...
     *         }
     *     }
     * }
     * </pre>
     * {@code JavaSourceGroupName.JAVA} could be a name of a source group.
     *
     * @return the name of these source roots representing the type of sources
     *   they contain. This method never returns {@code null}.
     */
    public JavaSourceGroupName getGroupName() {
        return groupName;
    }

    /**
     * Returns the set of source roots of this source group.
     *
     * @return the set of source roots of this source group. This method may
     *   never return {@code null} and the returned set does not contain
     *   {@code null} elements.
     */
    public Set<File> getSourceRoots() {
        return sourceRoots;
    }

    /**
     * Returns the patterns of paths to be excluded from this source group.
     * Exclude patterns are applied after include patterns.
     *
     * @return the patterns of paths to be excluded from this source group.
     *   This method never returns {@code null}.
     */
    public FilterPatterns getFilterPatterns() {
        // The null check is there for backward compatibility.
        // That is, when this object was serialized with a previous version
        // of this class.
        return patternFilters != null
                ? patternFilters
                : FilterPatterns.ALLOW_ALL;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.groupName != null ? this.groupName.hashCode() : 0);
        hash = 11 * hash + (this.sourceRoots != null ? this.sourceRoots.hashCode() : 0);
        hash = 11 * hash + (this.patternFilters != null ? this.patternFilters.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final JavaSourceGroup other = (JavaSourceGroup)obj;
        if (this.groupName != other.groupName) return false;
        if (this.sourceRoots != other.sourceRoots && (this.sourceRoots == null || !this.sourceRoots.equals(other.sourceRoots))) return false;
        return this.patternFilters == other.patternFilters || (this.patternFilters != null && this.patternFilters.equals(other.patternFilters));
    }

    private Object readResolve() throws ObjectStreamException {
        // The null check is there for backward compatibility.
        // That is, when this object was serialized with a previous version
        // of this class.
        return patternFilters != null
                ? this
                : new JavaSourceGroup(groupName, sourceRoots);
    }
}
