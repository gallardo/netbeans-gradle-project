package org.netbeans.gradle.project.util;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.netbeans.gradle.model.java.FilterPatterns;

/**
 *
 * @author alberto
 * @since 2015-09-02
 */
@RunWith(Parameterized.class)
public class FilterRulesTest {

    @Parameters(name = "Case {index} - rootPath \"{0}\"; file \"{1}\"; inc:[{2}]; exc:[{3}] -> Expected {4}")
    public static Iterable<Object[]> data() {
        // Cases marked with (*) are the special cases where the tested file should not be pruned despite only
        // matching the begginning of the glob pattern
        return Arrays.asList(new Object[][] {
             { "/A", "/", null, null, true }        // 0
            ,{ "/A", "/A", null, null, true }        
            ,{ "/A", "/B", null, null, true }
            ,{ "/A/A", "/", null, null, true }
            ,{ "/A/A", "/A", null, null, true }
            ,{ "/A/A", "/A/A", null, null, true }   // 5
            ,{ "/A/A", "/A/B", null, null, true } 
            ,{ "/A/A", "/B/A", null, null, true }

                
            ,{ "/A", "/", "**", null, false } 
            ,{ "/A", "/A", "**", null, true }
            ,{ "/A", "/B", "**", null, false }      // 10
            ,{ "/A", "/A/A", "**", null, true }
            ,{ "/A", "/A/B", "**", null, true }
            ,{ "/A", "/B/A", "**", null, false }
            ,{ "/A", "/A/A/A", "**", null, true }
                
            ,{ "/A", "/", "/A/**", null, false }    // 15
            ,{ "/A", "/A", "/A/**", null, true }    // (*)
            ,{ "/A", "/B", "/A/**", null, false }
            ,{ "/A", "/A/A", "/A/**", null, true }  // (*)
            ,{ "/A", "/A/B", "/A/**", null, false }
            ,{ "/A", "/B/A", "/A/**", null, false }    // 20
            ,{ "/A", "/B/B", "/A/**", null, false }
            ,{ "/A", "/A/A/A", "/A/**", null, true }   
            ,{ "/A", "/A/A/B", "/A/**", null, true }
            ,{ "/A", "/A/B/A", "/A/**", null, false }
                
            ,{ "/A", "/", "/A/A/**", null, false }     // 25
            ,{ "/A", "/A", "/A/A/**", null, true }     // (*)
            ,{ "/A", "/B", "/A/A/**", null, false }    
            ,{ "/A", "/A/A", "/A/A/**", null, true }   // (*)
            ,{ "/A", "/A/B", "/A/A/**", null, false }
            ,{ "/A", "/B/A", "/A/A/**", null, false }  // 30
            ,{ "/A", "/A/A/A", "/A/A/**", null, true } // (*)
            ,{ "/A", "/A/A/B", "/A/A/**", null, false } 
            ,{ "/A", "/A/B/A", "/A/A/**", null, false }
            ,{ "/A", "/A/A/A/A", "/A/A/**", null, true }
                
            ,{ "/A", "/", "/A**", null, false }       // 35
            ,{ "/A", "/A", "/A**", null, true }       // (*)
            ,{ "/A", "/B", "/A**", null, false }
            ,{ "/A", "/A/A", "/A**", null, true }
            ,{ "/A", "/A/B", "/A**", null, false }
            ,{ "/A", "/A/AA", "/A**", null, true }    // 40
            ,{ "/A", "/A/AB", "/A**", null, true }
            ,{ "/A", "/A/BA", "/A**", null, false }
            ,{ "/A", "/B/A", "/A**", null, false }    
            ,{ "/A", "/B/B", "/A**", null, false }
            ,{ "/A", "/A/A/A", "/A**", null, true }   // 45
            ,{ "/A", "/A/A/B", "/A**", null, true }
            ,{ "/A", "/A/B/A", "/A**", null, false }
            ,{ "/A", "/A/AA/A", "/A**", null, true }   
            ,{ "/A", "/A/AB/B", "/A**", null, true }
            ,{ "/A", "/A/BA/A", "/A**", null, false } // 50
        });
    }
    
    private static final Collection<String> EMPTY_COLLECTION = new ArrayList<>();
    private final String rootPathName;
    private final String fileName;
    private final Collection<String> includes;
    private final Collection<String> excludes;
    private final boolean expected;

    public FilterRulesTest(String rootPathName, String fileName, String includes, String excludes, boolean expected) {
        this.rootPathName = rootPathName;
        this.fileName = fileName;
        this.includes = (null==includes)?EMPTY_COLLECTION:Arrays.asList(includes.split(","));
        this.excludes = (null==excludes)?EMPTY_COLLECTION:Arrays.asList(excludes.split(","));
        this.expected = expected;
    }
    
    
    /**
     * Test of {@link FilterRules#isIncluded(java.nio.file.Path, java.nio.file.Path)}
     */
    @Test
    public void testIsIncluded() {
        System.err.println("testIsIncluded");
        Path rootPath = new File(rootPathName).toPath();
        Path filePath = new File(fileName).toPath();
        
        FilterRules filterRules = FilterRules.create(FilterPatterns.create(excludes,includes));
        assertEquals("Failed using rules: " + filterRules,
                expected, filterRules.isIncluded(rootPath, filePath));
    }

}
