package org.netbeans.gradle.project.util;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.netbeans.gradle.model.java.FilterPatterns;
import org.netbeans.gradle.model.java.JavaSourceGroup;

/**
 *
 * @author alberto
 */
@RunWith(Parameterized.class)
public class FilterRulesTest {

    @Parameters(name = "Case {index} - rootPath \"{0}\"; file \"{1}\"; inc:[{2}]; exc:[{3}] -> Expected {4} / {5}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
             { "/A", "/", null, null, true, true }        // 0
            ,{ "/A", "/A", null, null, true, true }        
            ,{ "/A", "/B", null, null, true, true }
            ,{ "/A/A", "/", null, null, true, true }
            ,{ "/A/A", "/A", null, null, true, true }
            ,{ "/A/A", "/A/B", null, null, true, true }   // 5

                
            ,{ "/A", "/", "**", null, true, false } 
            ,{ "/A", "/A", "**", null, true, true }
            ,{ "/A", "/B", "**", null, true, false }
            ,{ "/A", "/A/A", "**", null, true, true }
            ,{ "/A", "/B/A", "**", null, true, false }
            ,{ "/A", "/A/A/A", "**", null, true, true }
                
            ,{ "/A", "/", "/A/**", null, true, false } 
            ,{ "/A", "/A", "/A/**", null, true, true }
            ,{ "/A", "/B", "/A/**", null, true, false }
            ,{ "/A", "/A/A", "/A/**", null, true, true }
            ,{ "/A", "/B/A", "/A/**", null, true, false }
            ,{ "/A", "/A/A/A", "/A/**", null, true, true }
                
            ,{ "/A", "/", "/A/A/**", null, true, false } 
            ,{ "/A", "/A", "/A/A/**", null, true, true }
            ,{ "/A", "/B", "/A/A/**", null, true, false }
            ,{ "/A", "/A/A", "/A/A/**", null, true, true }
            ,{ "/A", "/B/A", "/A/A/**", null, true, false }
            ,{ "/A", "/A/A/A", "/A/A/**", null, true, true }
            ,{ "/A", "/A/B/A", "/A/A/**", null, true, false }
            ,{ "/A", "/A/A/B", "/A/A/**", null, true, true }
        });
    }
    @Mock
    JavaSourceGroup sourceGroup;
    
    private static final Collection<String> EMPTY_COLLECTION = new ArrayList<>();
    private final String rootPathName;
    private final String fileName;
    private final Collection<String> includes;
    private final Collection<String> excludes;
    private final boolean expectedWhenAllowAll;
    private final boolean expectedWhenUsingFilters;

    public FilterRulesTest(String rootPathName, String fileName, String includes, String excludes,
            boolean expectedWhenAllowAll, boolean expectedWhenUsingFilters) {
        this.rootPathName = rootPathName;
        this.fileName = fileName;
        this.includes = (null==includes)?EMPTY_COLLECTION:Arrays.asList(includes.split(","));
        this.excludes = (null==excludes)?EMPTY_COLLECTION:Arrays.asList(excludes.split(","));
        this.expectedWhenAllowAll = expectedWhenAllowAll;
        this.expectedWhenUsingFilters = expectedWhenUsingFilters;
    }
    
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    /**
     * Test of isIncluded method, of class FilterRules.
     */
    @Test
    public void testIsIncluded_Path_Path() {
        System.err.println("testIsIncluded_for_empty_filter");
        Path rootPath = new File(rootPathName).toPath();
        Path filePath = new File(fileName).toPath();
        
        Mockito.when(sourceGroup.getFilterPatterns())
                .thenReturn(FilterPatterns.ALLOW_ALL)                  // First invokation: accept all
                .thenReturn(FilterPatterns.create(excludes,includes)); // Second invokation: use test data
        
        // TODO: Refactor constructor: why passing JavaSourceGroup, when FilterPatterns suffice?
        FilterRules filterRulesAllowAll = FilterRules.create(sourceGroup);
        assertEquals("Failed expectation with filterRulesAllowAll",
                expectedWhenAllowAll, filterRulesAllowAll.isIncluded(rootPath, filePath));
        
        FilterRules filterRulesNonEmpty = FilterRules.create(sourceGroup);
        assertEquals("Failed expectation with filterRulesNonEmpty",
                expectedWhenUsingFilters, filterRulesNonEmpty.isIncluded(rootPath, filePath));
    }

}
