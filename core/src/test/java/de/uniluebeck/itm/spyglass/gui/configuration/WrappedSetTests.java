package de.uniluebeck.itm.spyglass.gui.configuration;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.spyglass.gui.databinding.WrappedSet;
import de.uniluebeck.itm.spyglass.util.StringFormatter;

/**
 * // -------------------------------------------------------------------------------- /**
 * 
 * @author Dariush Forouher
 * 
 */
public class WrappedSetTests {
	
	private HashMap<Integer, StringFormatter> map = new HashMap<Integer, StringFormatter>();
	private IObservableSet wset;
	
	private Set<WrappedSet.ObservableEntry<Integer, StringFormatter>> entrySet;
	
	@Before
	public void setUp() throws Exception {
		map.clear();
		map.put(1, new StringFormatter("aaa"));
		map.put(2, new StringFormatter("bbb"));
		map.put(3, new StringFormatter("ccc"));
		
		entrySet = new WrappedSet<Integer, StringFormatter>(map);
	}
	
	@Test
	public void testIsEmpty() {
		Assert.assertFalse(entrySet.isEmpty());
	}
	
	@Test
	public void testIsEmpty2() {
		map.clear();
		Assert.assertTrue(entrySet.isEmpty());
	}
	
	@Test
	public void testAddObservableEntryOfKV() {
		final WrappedSet.ObservableEntry<Integer, StringFormatter> newEntry = new WrappedSet.ObservableEntry<Integer, StringFormatter>(
				5, new StringFormatter("bla"));
		
		this.entrySet.add(newEntry);
		
		Assert.assertTrue(entrySet.contains(newEntry));
	}
	
	@Test
	public void testAddObservableEntryOfKV2() {
		final WrappedSet.ObservableEntry<Integer, StringFormatter> newEntry = new WrappedSet.ObservableEntry<Integer, StringFormatter>(
				5, new StringFormatter("bla"));
		
		this.entrySet.add(newEntry);
		
		Assert.assertTrue(map.containsKey(newEntry.getKey()));
		Assert.assertTrue(map.containsValue(newEntry.getValue()));
	}
	
	@Test
	public void testAddObservableEntryOfKV3() {
		final WrappedSet.ObservableEntry<Integer, StringFormatter> newEntry = new WrappedSet.ObservableEntry<Integer, StringFormatter>(
				5, new StringFormatter("bla"));
		
		this.entrySet.add(newEntry);
		
		Assert.assertTrue(map.containsKey(newEntry.getKey()));
		final StringFormatter value = map.get(newEntry.getKey());
		Assert.assertEquals(newEntry.getValue(), value);
		Assert.assertTrue(map.containsValue(newEntry.getValue()));
	}
	
}
