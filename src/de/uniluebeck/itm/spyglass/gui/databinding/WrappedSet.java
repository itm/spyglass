package de.uniluebeck.itm.spyglass.gui.databinding;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;

/**
 * Wraps a map into an set. Modifications to the set reflect on the Hashmap and vice-versa. This
 * class makes use of the Map.Entry class describing an entry inside a Map. In this WrappedSet each
 * Map.Entry is wrapped inside an ObservableEntry object, which offers additional property event
 * fireing methods.
 * 
 * The main use-case for this class is to connect a Map to an IObservableList to use it in JFace
 * databinding.
 * 
 * Please be aware that this set does not guaranty object-identity for ObservableEntries lying
 * inside this set. In other words: After an ObservableEntry has been added to this set, you won't
 * get the same ObservableEntry back when calling the iterator. The ObservableEntry are comparable
 * with equals(), though.
 * 
 * This also means that modifing an ObservableEntry (which has not been pulled out of this set)
 * *after* it has been added to the set will not reflect to the corresponding object inside the set!
 * 
 * This class is not thread-safe in any way.
 * 
 * @author Dariush Forouher
 * 
 * @param <K>
 *            key type of the Hashmap
 * @param <V>
 *            value type of the Hashmap
 */
public class WrappedSet<K extends Comparable<K>, V> extends
		AbstractSet<WrappedSet.ObservableEntry<K, V>> {
	
	/**
	 * 
	 * @author dariush
	 * 
	 * @param <K>
	 * @param <V>
	 */
	public static class ObservableEntry<K extends Comparable<K>, V> extends PropertyBean implements
			Comparable<ObservableEntry<K, V>> {
		
		private K key;
		private V value;
		
		private ObservableEntry() {
			//
		}
		
		public ObservableEntry(final K key, final V value) {
			this.key = key;
			this.value = value;
		}
		
		public V getValue() {
			return value;
		}
		
		public K getKey() {
			return key;
		}
		
		public void setValue(final V newValue) {
			final V oldValue = value;
			this.value = newValue;
			firePropertyChange("value", oldValue, newValue);
		}
		
		public int compareTo(final ObservableEntry<K, V> o) {
			return (this.getKey()).compareTo(o.getKey());
		}
		
		@Override
		public boolean equals(final Object o) {
			if (o instanceof ObservableEntry) {
				final ObservableEntry<?, ?> o2 = (ObservableEntry<?, ?>) o;
				return o2.getKey().equals(this.getKey()) && o2.getValue().equals(this.getValue());
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @author dariush
	 * 
	 * @param <K>
	 * @param <V>
	 */
	private static class InternalObservableEntry<K extends Comparable<K>, V> extends
			ObservableEntry<K, V> {
		
		private Entry<K, V> entry;
		
		private InternalObservableEntry(final Entry<K, V> entry) {
			this.entry = entry;
		}
		
		@Override
		public V getValue() {
			return entry.getValue();
		}
		
		@Override
		public K getKey() {
			return entry.getKey();
		}
		
		@Override
		public void setValue(final V newValue) {
			final V oldValue = entry.getValue();
			entry.setValue(newValue);
			firePropertyChange("value", oldValue, newValue);
		}
		
	}
	
	final HashMap<Entry<K, V>, ObservableEntry<K, V>> translationMap = new HashMap<Entry<K, V>, ObservableEntry<K, V>>();
	final Set<Entry<K, V>> entries;
	final Map<K, V> map;
	
	public WrappedSet(final Map<K, V> map) {
		entries = map.entrySet();
		this.map = map;
	}
	
	private ObservableEntry<K, V> mapEntry(final Entry<K, V> entry) {
		if (!translationMap.containsKey(entry)) {
			final ObservableEntry<K, V> obsEntry = new InternalObservableEntry<K, V>(entry);
			translationMap.put(entry, obsEntry);
		}
		return translationMap.get(entry);
	}
	
	@Override
	public boolean add(final ObservableEntry<K, V> e) {
		
		final boolean containsKey = map.containsKey(e.getKey());
		
		if (containsKey) {
			return false;
		} else {
			this.map.put(e.getKey(), e.getValue());
			return true;
		}
	}
	
	@Override
	public boolean addAll(final Collection<? extends ObservableEntry<K, V>> collection) {
		boolean ret = false;
		for (final ObservableEntry<K, V> e : collection) {
			ret |= this.add(e);
		}
		return ret;
	}
	
	@Override
	public void clear() {
		this.map.clear();
		
	}
	
	/**
	 * Return true iff the key exists in the map
	 */
	@Override
	public boolean contains(final Object o) {
		if (o instanceof ObservableEntry) {
			final ObservableEntry<?, ?> entry = (ObservableEntry<?, ?>) o;
			return map.containsKey(entry.getKey());
		} else {
			return false;
		}
	}
	
	@Override
	public boolean containsAll(final Collection<?> c) {
		boolean ret = true;
		for (final Object o : c) {
			ret &= this.contains(o);
		}
		return ret;
	}
	
	@Override
	public boolean isEmpty() {
		return this.entries.isEmpty();
	}
	
	@Override
	public Iterator<ObservableEntry<K, V>> iterator() {
		final Iterator<Entry<K, V>> wrappedIterator = entries.iterator();
		return new Iterator<ObservableEntry<K, V>>() {
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
			public boolean hasNext() {
				return wrappedIterator.hasNext();
			}
			
			public ObservableEntry<K, V> next() {
				return mapEntry(wrappedIterator.next());
			}
		};
	}
	
	@Override
	public boolean remove(final Object o) {
		if (o instanceof ObservableEntry) {
			final ObservableEntry<?, ?> o2 = (ObservableEntry<?, ?>) o;
			map.remove(o2.getKey());
		}
		return true;
	}
	
	@Override
	public boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int size() {
		return entries.size();
	}
	
	@Override
	public Object[] toArray() {
		final Set<ObservableEntry<K, V>> set = new TreeSet<ObservableEntry<K, V>>();
		for (final ObservableEntry<K, V> observableEntry : this) {
			set.add(observableEntry);
		}
		return set.toArray();
	}
	
	@Override
	public <T> T[] toArray(final T[] a) {
		final Set<ObservableEntry<K, V>> set = new TreeSet<ObservableEntry<K, V>>();
		for (final ObservableEntry<K, V> observableEntry : this) {
			set.add(observableEntry);
		}
		return set.toArray(a);
	}
	
}
