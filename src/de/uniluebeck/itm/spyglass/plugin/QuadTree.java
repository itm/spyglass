package de.uniluebeck.itm.spyglass.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.graphics.Point;

import de.uniluebeck.itm.spyglass.drawing.BoundingBoxChangeListener;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

public class QuadTree implements Layer, BoundingBoxChangeListener {

	private static final int MIN_BOX_SIZE_X = 20;

	private static final int MIN_BOX_SIZE_Y = 20;

	private final AbsoluteRectangle box;

	private final ArrayList<DrawingObject> objects = new ArrayList<DrawingObject>();

	private boolean hasSons;

	private QuadTree parent;

	private final QuadTree root;

	private QuadTree[] sons = new QuadTree[4];

	private HashMap<DrawingObject, Long> insertionOrder;

	private long insertionOrderLargest;

	private long insertionOrderSmallest;

	private class DrawingObjectComparator implements Comparator<DrawingObject> {

		@Override
		public int compare(final DrawingObject o1, final DrawingObject o2) {
			final long long1 = root.insertionOrder.get(o1);
			final long long2 = root.insertionOrder.get(o2);
			return (long1 > long2) ? 1 : (long1 < long2) ? -1 : 0;
		}
	}

	private DrawingObjectComparator sorter = new DrawingObjectComparator();

	public QuadTree() {
		this(new AbsoluteRectangle(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param parent
	 * @param box
	 * @param position
	 */
	QuadTree(final QuadTree parent, final AbsoluteRectangle box) {
		this.root = parent.root;
		this.parent = parent;
		this.box = box;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param box
	 * @param position
	 */
	QuadTree(final AbsoluteRectangle box) {
		this.root = this;
		this.box = box;
		this.insertionOrder = new HashMap<DrawingObject, Long>();
		this.insertionOrderLargest = 0;
		this.insertionOrderSmallest = 0;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Checks recursively if the entity is contained in this quadtree.
	 * 
	 * @param entity
	 * @return
	 */
	synchronized boolean contains(final DrawingObject entity) {
		return search(entity) != null;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Creates son nodes if the nodes to be created are larger or equal than the minimum size of
	 * <code>MIN_BOX_SIZE_X</code> x <code>MIN_BOX_SIZE_Y</code> .
	 * 
	 * @return true, if son nodes were created, false otherwise
	 */
	private boolean createSons() {

		if (!((box.getWidth() / 2 >= MIN_BOX_SIZE_X) && (box.getHeight() / 2 >= MIN_BOX_SIZE_Y))) {
			return false;
		}

		hasSons = true;

		final int shiftX = box.getWidth() / 2;
		final int shiftY = box.getHeight() / 2;

		final AbsoluteRectangle ul = new AbsoluteRectangle(box);
		ul.setSize(box.getWidth() / 2, box.getHeight() / 2);
		final AbsoluteRectangle ur = new AbsoluteRectangle(ul);
		ur.translate(shiftX, 0);
		final AbsoluteRectangle ll = new AbsoluteRectangle(ul);
		ll.translate(0, shiftY);
		final AbsoluteRectangle lr = new AbsoluteRectangle(ul);
		lr.translate(shiftX, shiftY);

		sons[0] = new QuadTree(this, ul);
		sons[1] = new QuadTree(this, ur);
		sons[2] = new QuadTree(this, ll);
		sons[3] = new QuadTree(this, lr);

		return true;

	}

	// --------------------------------------------------------------------------------
	/**
	 * Simply destroys all son nodes.
	 */
	private void destroySons() {

		hasSons = false;
		sons = new QuadTree[4];

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the bounding box of this QuadTree element.
	 * 
	 * @return the bounding box of this QuadTree element
	 */
	synchronized AbsoluteRectangle getBoundingBox() {
		return box;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param pos
	 * @param entity
	 * @return
	 */
	synchronized List<DrawingObject> getCollisionEntities(final AbsoluteRectangle box) {
		final List<DrawingObject> list = new ArrayList<DrawingObject>();

		// search for the quadrant where this entity
		// would be placed
		QuadTree elem = getInsertionElement(box);

		// first check all son nodes if there are objects we could
		// collide with
		if ((elem != null) && elem.hasSons) {
			for (final QuadTree son : sons) {
				for (final DrawingObject e : son.getObjectsRecursive()) {
					try {
						if (e.getBoundingBox().intersects(box)) {
							list.add(e);
						}
					} catch (final Exception t) {
						// XXX ???
						// System.out.println("bla" + t);
					}
				}
			}
		}

		// now check parents
		while (elem != null) {

			// now check if there are any elements inside
			// the insertion element with which there
			// could be a collision
			for (final DrawingObject e : elem.objects) {

				// check if any of the corners of entity on
				// position pos would be contained in the box
				// of e.
				try {
					if (e.getBoundingBox().intersects(box)) {
						list.add(e);
					}
				} catch (final NullPointerException ef) {
					System.err.println(ef);
				}

			}

			// now also search the parent nodes in case
			// there are boxes overlapping into elem.
			elem = elem.parent;
		}

		return list;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a list of the objects attached to this quadtree element.
	 * 
	 * @return a list of the objects attached to this quadtree element
	 */
	synchronized ArrayList<DrawingObject> getObjects() {
		return new ArrayList<DrawingObject>(objects);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns all objects in this node and all objects under this node.
	 * 
	 * @return
	 */
	synchronized List<DrawingObject> getObjectsRecursive() {
		final ArrayList<DrawingObject> list = new ArrayList<DrawingObject>();
		getObjectsRecursiveInternal(list);
		return list;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds all objects recursively to the <code>ArrayList</code> <code>list</code>.
	 * 
	 * @param list
	 *            the <code>ArrayList</code> instance to add the objects to
	 */
	private void getObjectsRecursiveInternal(final ArrayList<DrawingObject> list) {
		list.addAll(objects);
		if (hasSons) {
			for (final QuadTree son : sons) {
				son.getObjectsRecursiveInternal(list);
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the number of objects in the quadtree.
	 * 
	 * @return
	 */
	synchronized int getObjectCount() {
		int cnt = 0;
		for (int i = 0; i < sons.length; i++) {
			cnt += (sons[i] != null) ? sons[i].getObjectCount() : 0;
		}
		return objects.size() + cnt;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the QuadTree into which an Entity with position <code>pos</code> and box
	 * <code>box</code> can be inserted.
	 * 
	 * @param pos
	 * @param box
	 * @return
	 */
	private QuadTree getInsertionElement(final AbsoluteRectangle box) {

		QuadTree foundElem = null;

		if (hasSons) {

			for (int i = 0; i < 4; i++) {
				if (sons[i].box.contains(box)) {
					foundElem = sons[i].getInsertionElement(box);
				}
			}

		}

		return foundElem != null ? foundElem : this.box.contains(box) ? this : null;

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the parent element of this <code>QuadTree</code>.
	 * 
	 * @return the parent element of this <code>QuadTree</code> or <code>null</code> if this node is
	 *         the root
	 */
	synchronized QuadTree getParent() {
		return parent;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the son nodes of this <code>QuadTree</code> element.
	 * 
	 * @return the son nodes of this <code>QuadTree</code> element
	 */
	synchronized QuadTree[] getSons() {
		return sons;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Inserts an entity into the QuadTree.
	 * 
	 * @param object
	 */
	synchronized boolean insert(final DrawingObject object) {
		return insertInternal(object, true);
	}

	private boolean insertInternal(final DrawingObject object, final boolean updateInsertionTime) {
		final AbsoluteRectangle bbox = object.getBoundingBox();
		// search for the element to insert the node
		final QuadTree elem = getInsertionElement(bbox);
		if (elem != null) {
			elem.objects.add(object);
			if (updateInsertionTime) {
				root.insertionOrder.put(object, ++insertionOrderLargest);
			}
			elem.updateSons();
			return true;
		}

		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns if this <code>QuadTree</code> element has any son nodes.
	 * 
	 * @return if this <code>QuadTree</code> element has any son nodes
	 */
	synchronized boolean isHasSons() {
		return hasSons;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes the entity from the <code>QuadTree</code>.
	 * 
	 * @param object
	 */
	public synchronized boolean remove(final DrawingObject object) {
		final QuadTree elem = search(object);
		if (elem != null) {
			object.removeBoundingBoxChangeListener(this);
			elem.objects.remove(object);
			if (elem.parent != null) {
				elem.parent.updateSonsAfterRemove();
			}
			return true;
		}
		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Searches for the QuadTree that contains this entity.
	 * 
	 * @param entity
	 *            the entity to search for
	 * @return the QuadTree containing <code>entity</code>.
	 */
	synchronized QuadTree search(final DrawingObject entity) {

		if (objects.contains(entity)) {
			return this;
		}

		QuadTree foundElem = null;

		if (hasSons) {

			for (int i = 0; i < 4; i++) {
				if (sons[i].box.contains(entity.getBoundingBox())) {
					foundElem = sons[i].search(entity);
					break;
				}
			}

		}

		return foundElem;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Searches for an entity at the given position. Will return an Entity instance if
	 * <code>position</code> lies within the objects' box.
	 * 
	 * @param position
	 * @return
	 */
	synchronized Collection<DrawingObject> search(final Point position) {
		return getCollisionEntities(new AbsoluteRectangle(position.x, position.y, 1, 1));
	}

	@Override
	public synchronized String toString() {
		return toString(0);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param tabCount
	 *            the number of tabs to print before each line
	 * @return
	 */
	public synchronized String toString(final int tabCount) {
		final StringBuffer buff = new StringBuffer();
		for (final DrawingObject e : objects) {
			buff.append(e.toString(tabCount + 1));
			buff.append(",");
		}
		for (final QuadTree s : sons) {
			if (s != null) {
				buff.append("\n");
				buff.append(s.toString(tabCount + 1));
			}
		}
		return buff.toString();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Checks if there are son nodes to be created or destroyed and executes this.
	 */
	private void updateSons() {

		// if this node has no sons but more than one entity
		// this can have two senseful reasons:
		// 1. the objects overlap between the sons' boxes
		// 2. the maximum quadtree resolution is reached
		if (!hasSons && (objects.size() > 1)) {

			// if creation of son nodes is successful (maximum
			// quadtree resolution not yet reached) reorder all
			// objects
			if (createSons()) {

				final ArrayList<DrawingObject> reordered = new ArrayList<DrawingObject>(objects);
				objects.clear();
				for (final DrawingObject e : reordered) {
					insertInternal(e, false);
				}

			}

			// otherwise (else) all objects can stay at their
			// current position
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 */
	private void updateSonsAfterRemove() {

		final int cnt = getObjectCount();
		final boolean sonsHaveSons = sons[0].hasSons || sons[1].hasSons || sons[2].hasSons || sons[3].hasSons;

		// if all objects are contained in this
		// node, son nodes are no longer needed
		if (hasSons && !sonsHaveSons && (cnt < 2)) {

			// reorder found entity if there's one
			DrawingObject reorderEntity = null;
			for (final QuadTree e : sons) {
				if (e.objects.size() == 1) {
					reorderEntity = e.objects.get(0);
				}
			}

			if (reorderEntity != null) {
				objects.add(reorderEntity);
			}

			destroySons();

		}
	}

	@Override
	public synchronized void addOrUpdate(final DrawingObject d) {
		insertInternal(d, !remove(d));
		d.addBoundingBoxChangedListener(this);
	}

	// ------------------------------------------------------------------------------
	/**
	 * Sets the point order parameter of a drawing object to make it the last one in the set to be
	 * painted. This way, the drawing object will be in front of all other ones.
	 * 
	 * @param object
	 *            the drawing object to be brought to the front
	 */
	@Override
	public synchronized void bringToFront(final DrawingObject dob) {
		// TODO zahlenüberlauf behandeln
		root.insertionOrder.put(dob, ++insertionOrderLargest);
	}

	// ------------------------------------------------------------------------------
	/**
	 * Sets the point order parameter of a drawing object to make it the first one in the set to be
	 * painted. This way, the drawing object will be behind all other ones.
	 * 
	 * @param object
	 *            the drawing object to be pushed to the back
	 */
	@Override
	public synchronized void pushBack(final DrawingObject object) {
		root.insertionOrder.put(object, --insertionOrderSmallest);
	}

	@Override
	public synchronized void clear() {
		removeBoundingBoxListenersRecursive();
		destroySons();
		objects.clear();
	}

	private void removeBoundingBoxListenersRecursive() {
		for (final DrawingObject dobj : objects) {
			dobj.removeBoundingBoxChangeListener(this);
		}
		for (final QuadTree son : sons) {
			if (son != null) {
				son.removeBoundingBoxListenersRecursive();
			}
		}
	}

	@Override
	public synchronized List<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect) {
		final List<DrawingObject> list = getCollisionEntities(rect);
		Collections.sort(list, sorter);
		return list;
	}

	@Override
	public synchronized List<DrawingObject> getDrawingObjects() {
		final List<DrawingObject> list = getObjectsRecursive();
		Collections.sort(list, sorter);
		return list;
	}

	@Override
	public synchronized void onBoundingBoxChanged(final DrawingObject updatedDrawingObject) {
		addOrUpdate(updatedDrawingObject);
	}

}
