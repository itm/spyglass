package de.uniluebeck.itm.spyglass.layer;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.danbim.swtquadtree.ISWTQuadTree;
import de.uniluebeck.itm.spyglass.drawing.BoundingBoxChangeListener;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

class QuadTree implements Layer, BoundingBoxChangeListener {

	private class DrawingObjectComparator implements Comparator<DrawingObject> {

		@Override
		public int compare(final DrawingObject o1, final DrawingObject o2) {
			final long long1 = insertionOrder.get(o1);
			final long long2 = insertionOrder.get(o2);
			return (long1 > long2) ? 1 : (long1 < long2) ? -1 : 0;
		}
	}

	private final Map<DrawingObject, Long> insertionOrder;

	private long insertionOrderLargest;

	private long insertionOrderSmallest;

	private final Object lock = new Object();

	private final DrawingObjectComparator sorter;

	private final boolean threadSafe;

	private final ISWTQuadTree<DrawingObject> tree;

	public QuadTree(final int originX, final int originY, final int totalSideLength, final int minSideLength, final int capacity,
			final boolean threadSafe) {

		final ISWTQuadTree.Factory<DrawingObject> factory = new ISWTQuadTree.Factory<DrawingObject>();
		this.threadSafe = threadSafe;
		insertionOrder = Collections.synchronizedMap(new HashMap<DrawingObject, Long>());
		insertionOrderLargest = 0;
		insertionOrderSmallest = 0;
		tree = factory.create(originX, originY, totalSideLength, minSideLength, capacity, threadSafe);
		sorter = new DrawingObjectComparator();

	}

	@Override
	public void add(final DrawingObject d) {
		if (threadSafe) {
			synchronized (lock) {
				d.addBoundingBoxChangedListener(this);
				insertionOrder.put(d, ++insertionOrderLargest);
				tree.insertItem(d, d.getBoundingBox().rectangle);
			}
		} else {
			d.addBoundingBoxChangedListener(this);
			insertionOrder.put(d, ++insertionOrderLargest);
			tree.insertItem(d, d.getBoundingBox().rectangle);
		}
	}

	@Override
	public void bringToFront(final DrawingObject dob) {
		if (threadSafe) {
			synchronized (lock) {
				insertionOrder.put(dob, ++insertionOrderLargest);
			}
		} else {
			insertionOrder.put(dob, ++insertionOrderLargest);
		}
	}

	@Override
	public void clear() {
		if (threadSafe) {
			synchronized (lock) {
				final Set<DrawingObject> dos = tree.searchItems();
				for (final DrawingObject dob : dos) {
					dob.removeBoundingBoxChangeListener(this);
				}
				tree.clear();
				insertionOrder.clear();
				insertionOrderLargest = 0;
				insertionOrderSmallest = 0;
			}
		} else {
			final Set<DrawingObject> dos = tree.searchItems();
			for (final DrawingObject dob : dos) {
				dob.removeBoundingBoxChangeListener(this);
			}
			tree.clear();
			insertionOrder.clear();
			insertionOrderLargest = 0;
			insertionOrderSmallest = 0;
		}
	}

	@Override
	public Set<DrawingObject> getDrawingObjects() {
		return getDrawingObjects(false);
	}

	@Override
	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect) {
		return (SortedSet<DrawingObject>) getDrawingObjects(rect, true);
	}

	@Override
	public Set<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect, final boolean sorted) {
		if (threadSafe) {
			synchronized (lock) {
				if (sorted) {
					final TreeSet<DrawingObject> set = new TreeSet<DrawingObject>(sorter);
					set.addAll(tree.searchItems(rect.rectangle));
					return set;
				}
				return tree.searchItems(rect.rectangle);
			}
		}

		if (sorted) {
			final TreeSet<DrawingObject> set = new TreeSet<DrawingObject>(sorter);
			set.addAll(tree.searchItems(rect.rectangle));
			return set;
		}
		return tree.searchItems(rect.rectangle);

	}

	@Override
	public Set<DrawingObject> getDrawingObjects(final boolean sorted) {
		if (threadSafe) {
			synchronized (lock) {
				if (sorted) {
					final TreeSet<DrawingObject> set = new TreeSet<DrawingObject>(sorter);
					set.addAll(tree.searchItems());
					return set;
				}
				return tree.searchItems();
			}
		}

		if (sorted) {
			final TreeSet<DrawingObject> set = new TreeSet<DrawingObject>(sorter);
			set.addAll(tree.searchItems());
			return set;
		}
		return tree.searchItems();

	}

	@Override
	public void onBoundingBoxChanged(final DrawingObject updatedDrawingObject, final AbsoluteRectangle oldBox) {
		if (threadSafe) {
			synchronized (lock) {
				tree.moveItem(updatedDrawingObject, oldBox.rectangle, updatedDrawingObject.getBoundingBox().rectangle);
			}
		} else {
			tree.moveItem(updatedDrawingObject, oldBox.rectangle, updatedDrawingObject.getBoundingBox().rectangle);
		}

	}

	@Override
	public void pushBack(final DrawingObject object) {
		if (threadSafe) {
			synchronized (lock) {
				insertionOrder.put(object, --insertionOrderSmallest);
			}
		} else {
			insertionOrder.put(object, --insertionOrderSmallest);
		}
	}

	@Override
	public void remove(final DrawingObject d) {
		if (threadSafe) {
			synchronized (lock) {
				tree.removeItem(d, d.getBoundingBox().rectangle);
				d.removeBoundingBoxChangeListener(this);
				insertionOrder.remove(d);
			}
		} else {
			tree.removeItem(d, d.getBoundingBox().rectangle);
			d.removeBoundingBoxChangeListener(this);
			insertionOrder.remove(d);
		}

	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#removeAll(java.util.Set)
	 */
	@Override
	public void removeAll(final Collection<DrawingObject> dos) {
		if (threadSafe) {
			synchronized (lock) {
				for (final DrawingObject dob : dos) {
					remove(dob);
				}
			}
		} else {
			for (final DrawingObject dob : dos) {
				remove(dob);
			}
		}

	}
}
