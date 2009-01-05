package de.uniluebeck.itm.spyglass.gui.databinding;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.ObservableSet;

/**
 * Wraps a Set into an ObservableSet. The original set is not copied, instead calls to an
 * WrappedObservableSet are passed through to the original wrapped set.
 * 
 * Thus changes to either the WrappedObservableSet or the original set reflect on each other.
 * 
 * @author Dariush Forouher
 */
@SuppressWarnings("unchecked")
public class WrappedObservableSet extends ObservableSet {

	public WrappedObservableSet(final Realm realm, final Set set, final Object sample) {
		super(realm, set, sample);
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.observable.set.AbstractObservableSet#add(java.lang.Object)
	 */
	@Override
	public boolean add(final Object o) {
		final boolean ret = wrappedSet.add(o);
		if (ret) {
			Object newObject = null;
			for (final Object member : wrappedSet) {
				if (member.equals(o)) {
					newObject = member;
				}
			}
			assert(newObject != null);
			fireSetChange(Diffs.createSetDiff(Collections.singleton(newObject), Collections.EMPTY_SET));
		}
		return ret;
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.observable.set.AbstractObservableSet#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(final Object o) {
		final boolean ret = wrappedSet.remove(o);
		if (ret) {
			fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET, Collections.singleton(o)));
		}
		return ret;

	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.observable.set.ObservableSet#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection c) {
		final Set additions = new HashSet();
		boolean ret = false;
		for (final Object o : c) {
			final boolean added = wrappedSet.add(o);
			ret |= added;
			if (added) {
				additions.add(o);
			}
		}
		fireSetChange(Diffs.createSetDiff(additions, Collections.EMPTY_SET));
		return ret;
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.observable.set.ObservableSet#clear()
	 */
	@Override
	public void clear() {
		final Set removals = new HashSet();
		removals.addAll(wrappedSet);
		wrappedSet.clear();
		fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET, removals));
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.observable.set.ObservableSet#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(final Collection c) {
		final Set removals = new HashSet();
		boolean ret = false;
		for (final Object o : c) {
			final boolean removed = wrappedSet.remove(o);
			ret |= removed;
			if (removed) {
				removals.add(o);
			}
		}
		fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET, removals));
		return ret;
	}

}
