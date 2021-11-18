import com.google.common.collect.Sets;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryComparableString;
import com.t0ugh.server.storage.MemorySortedSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.NavigableSet;
import java.util.Set;

import static org.junit.Assert.*;

public class MemorySortedSetTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAdd() throws Exception {
        MemorySortedSet memorySortedSet = new MemorySortedSet();
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hello").score(5).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hi").score(3).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Ha").score(1).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Ha").score(2).build());
        assertEquals(3, memorySortedSet.backdoorM().size());
        assertEquals(2, memorySortedSet.backdoorM().get("Ha").getScore(), 0.0);
        System.out.println();
    }

    @Test
    public void testRank() throws Exception {
        MemorySortedSet memorySortedSet = new MemorySortedSet();
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hello").score(5).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hi").score(3).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Ha").score(1).build());
        assertEquals(0, (int) memorySortedSet.rank("Hello").orElse(-1));
        assertEquals(1, (int) memorySortedSet.rank("Hi").orElse(-1));
        assertEquals(2, (int) memorySortedSet.rank("Ha").orElse(-1));
        assertEquals(-1, (int) memorySortedSet.rank("He").orElse(-1));
    }

    @Test
    public void testCount() throws Exception {
        MemorySortedSet memorySortedSet = new MemorySortedSet();
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hello").score(5).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hi").score(3).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Ha").score(1).build());
        assertEquals(3, memorySortedSet.count(1, 5));
        assertEquals(3, memorySortedSet.count(0, 6));
        assertEquals(1, memorySortedSet.count(2, 4));
    }

    @Test
    public void testRange() throws Exception {
        MemorySortedSet memorySortedSet = new MemorySortedSet();
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hello").score(5).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hi").score(3).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Ha").score(1).build());
        assertEquals(3, memorySortedSet.range(0, -1).size());
        assertEquals(3, memorySortedSet.range(0, 100).size());
        assertEquals(0, memorySortedSet.range(-100, 100).size());
        assertEquals(1, memorySortedSet.range(2, 100).size());
        assertEquals(3, memorySortedSet.range(0, 2).size());
        assertEquals(1, memorySortedSet.range(0, 0).size());
        NavigableSet<MemoryComparableString> s = memorySortedSet.range(0, 1);
        assertEquals(5, s.first().getScore(), 0.0);
        assertEquals("Hello", s.first().getStringValue());
        assertEquals(3, s.last().getScore(), 0.0);
        assertEquals("Hi", s.last().getStringValue());
    }

    @Test
    public void testRangeByScore() throws Exception {
        MemorySortedSet memorySortedSet = new MemorySortedSet();
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hello").score(5).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hi").score(3).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Ha").score(1).build());
        assertEquals(3, memorySortedSet.rangeByScore(1, 5).size());
        assertEquals(3, memorySortedSet.rangeByScore(0, 6).size());
        assertEquals(1, memorySortedSet.rangeByScore(2, 4).size());
        NavigableSet<MemoryComparableString> m = memorySortedSet.rangeByScore(2, 4);
        assertEquals("Hi", m.first().getStringValue());
        assertEquals(3, m.first().getScore(), 0.0);
    }

    @Test
    public void testRemoveAll() throws Exception {
        MemorySortedSet memorySortedSet = new MemorySortedSet();
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hello").score(5).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Hi").score(3).build());
        memorySortedSet.add(MemoryComparableString.builder().stringValue("Ha").score(1).build());
        Set<String> ss = Sets.newHashSet("Hi", "Ha", "He");
        assertEquals(2, memorySortedSet.removeAll(ss));
        assertEquals(1, memorySortedSet.card());
    }
}