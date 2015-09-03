package org.netbeans.gradle.model.java;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.netbeans.gradle.model.util.CollectionUtils;

public class FilterPatterns implements Serializable {
    private static final long serialVersionUID = 1L;

    public static FilterPatterns ALLOW_ALL = new FilterPatterns(
            Collections.<String>emptySet(),
            Collections.<String>emptySet()) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isAllowAll() {
            return true;
        }};

    private final Set<String> excludePatterns;
    private final Set<String> includePatterns;

    private FilterPatterns(
            Collection<? extends String> excludePatterns,
            Collection<? extends String> includePatterns) {

        this.excludePatterns = CollectionUtils.copyToLinkedHashSet(excludePatterns);
        this.includePatterns = CollectionUtils.copyToLinkedHashSet(includePatterns);

        CollectionUtils.checkNoNullElements(excludePatterns, "excludePatterns");
        CollectionUtils.checkNoNullElements(includePatterns, "includePatterns");
    }

    /**
     * 
     * @param excludePatterns cannot be <tt>null</tt> nor contain <tt>null</tt> elements
     * @param includePatterns cannot be <tt>null</tt> nor contain <tt>null</tt> elements
     * @return if both <tt>excludePatterns</tt> and <tt>includePatterns</tt> are empty, the
     *      <tt>FilePatterns.ALLOW_ALL</tt>. If not, a new <tt>FilePatterns</tt> instance. Notice that
     *      <ul>
     *          <li><tt>create(Collections.&lt;String&gt;emptyList(),Collections.&lt;String&gt;emptyList())</tt></li>
     *          <li>and <tt>create("","")</tt></li>
     *      </ul>
     *      return different <tt>FilterPatterns</tt> instances: the former, the <tt>FilePatterns.ALLOW_ALL</tt>;
     *      the latter, a <tt>FilterPatterns</tt> instance with two empty strings (<tt>""</tt>) patterns.
     *      
     * @throws NullPointerException if any of <tt>excludePatterns</tt> or <tt>includePatterns</tt> is <tt>null</tt>,
     *      or if they contain any <tt>null</tt> element.
     */
    public static FilterPatterns create(
            Collection<? extends String> excludePatterns,
            Collection<? extends String> includePatterns) {
        if (excludePatterns.isEmpty() && includePatterns.isEmpty()) {
            return ALLOW_ALL;
        }

        return new FilterPatterns(excludePatterns, includePatterns);
    }

    public boolean isAllowAll() {
        return excludePatterns.isEmpty() && includePatterns.isEmpty();
    }

    public Set<String> getExcludePatterns() {
        return excludePatterns;
    }

    public Set<String> getIncludePatterns() {
        return includePatterns;
    }

    @Override
    public String toString() {
        return "FilterPatterns{exc:" + excludePatterns + ", inc:" + includePatterns + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + excludePatterns.hashCode();
        hash = 89 * hash + includePatterns.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (getClass() != obj.getClass()) return false;

        final FilterPatterns other = (FilterPatterns)obj;
        return excludePatterns.equals(other.excludePatterns)
                && includePatterns.equals(other.includePatterns);
    }

    private Object writeReplace() {
        return new SerializedFormat(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Use proxy.");
    }

    private static final class SerializedFormat implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Set<String> excludePatterns;
        private final Set<String> includePatterns;

        public SerializedFormat(FilterPatterns source) {
            this.excludePatterns = source.excludePatterns;
            this.includePatterns = source.includePatterns;
        }

        private Object readResolve() throws ObjectStreamException {
            return FilterPatterns.create(excludePatterns, includePatterns);
        }
    }
}
