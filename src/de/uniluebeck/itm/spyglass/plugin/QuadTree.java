package de.uniluebeck.itm.spyglass.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Point;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

public class QuadTree implements Layer {
	
	private static final int MIN_BOX_SIZE_X = 20;
	
	private static final int MIN_BOX_SIZE_Y = 20;
	
	private final AbsoluteRectangle box;
	
	private final ArrayList<DrawingObject> objects = new ArrayList<DrawingObject>();
	
	private boolean hasSons;
	
	private QuadTree parent;
	
	private final QuadTree root;
	
	private QuadTree[] sons = new QuadTree[4];
	
	// --------------------------------------------------------------------------------
	/**
	 * @param parent
	 * @param box
	 * @param position
	 */
	public QuadTree(final QuadTree parent, final AbsoluteRectangle box) {
		this.root = parent.root;
		this.parent = parent;
		this.box = box;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param box
	 * @param position
	 */
	public QuadTree(final AbsoluteRectangle box) {
		this.root = this;
		this.box = box;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Checks recursively if the entity is contained in this quadtree.
	 * 
	 * @param entity
	 * @return
	 */
	public boolean contains(final DrawingObject entity) {
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
	public AbsoluteRectangle getBoundingBox() {
		return box;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param pos
	 * @param entity
	 * @return
	 */
	public List<DrawingObject> getCollisionEntities(final AbsoluteRectangle box) {
		final List<DrawingObject> list = new ArrayList<DrawingObject>();
		
		// search for the quadrant where this entity
		// would be placed
		QuadTree elem = getInsertionElement(box);
		
		// first check all son nodes if there are objects we could
		// collide with
		if ((elem != null) && elem.hasSons) {
			for (final QuadTree son : sons) {
				for (final DrawingObject e : son.getObjectsRecursive()) {
					if (e.getBoundingBox().intersects(box)) {
						list.add(e);
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
				if (e.getBoundingBox().intersects(box)) {
					list.add(e);
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
	public ArrayList<DrawingObject> getObjects() {
		return new ArrayList<DrawingObject>(objects);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns all objects in this node and all objects under this node.
	 * 
	 * @return
	 */
	public List<DrawingObject> getObjectsRecursive() {
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
	public int getObjectCount() {
		int cnt = 0;
		for (int i = 0; i < sons.length; i++) {
			cnt += (sons[i] != null) ? sons[i].getObjectCount() : 0;
		}
		return objects.size() + cnt;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the QuadTree into which the Entity <code>entity</code> can be inserted.
	 * 
	 * @param entity
	 * @return
	 */
	private QuadTree getInsertionElement(final DrawingObject entity) {
		return getInsertionElement(entity.getBoundingBox());
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
	public QuadTree getParent() {
		return parent;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the son nodes of this <code>QuadTree</code> element.
	 * 
	 * @return the son nodes of this <code>QuadTree</code> element
	 */
	public QuadTree[] getSons() {
		return sons;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Inserts an entity into the QuadTree.
	 * 
	 * @param object
	 */
	public boolean insert(final DrawingObject object) {
		
		// search for the element to insert the node
		final QuadTree elem = getInsertionElement(object);
		if (elem != null) {
			elem.objects.add(object);
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
	public boolean isHasSons() {
		return hasSons;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Moves and entity as long as it is inside the root elements bounding box.
	 * 
	 * @param object
	 * @param x
	 * @param y
	 * @return
	 */
	public void move(final DrawingObject object, final int x, final int y) {
		final AbsoluteRectangle newBox = new AbsoluteRectangle(object.getBoundingBox());
		newBox.translate(x, y);
		move(object, newBox);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Moves and entity from its old position to <code>newPos</code> as long as it is inside the
	 * root elements bounding box.
	 * 
	 * @param object
	 * @param newPos
	 * @return true, if entity was moved, false otherwise
	 */
	public void move(final DrawingObject object, final AbsoluteRectangle newBox) {
		
		// entity tries to move outside the plane
		if (!root.box.contains(newBox)) {
			throw new RuntimeException("Trying to move DrawingObject outside of the plane.");
		}
		
		final QuadTree elem = search(object);
		
		if (elem != null) {
			
			elem.objects.remove(object);
			
			if (elem.parent != null) {
				elem.parent.updateSonsAfterRemove();
			}
			
			object.setBoundingBox(newBox);
			insert(object);
			elem.updateSons();
			
		}
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Removes the entity from the <code>QuadTree</code>.
	 * 
	 * @param object
	 */
	public void remove(final DrawingObject object) {
		
		final QuadTree elem = search(object);
		if (elem != null) {
			elem.objects.remove(object);
			if (elem.parent != null) {
				elem.parent.updateSonsAfterRemove();
			}
		}
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Searches for the QuadTree that contains this entity.
	 * 
	 * @param entity
	 *            the entity to search for
	 * @return the QuadTree containing <code>entity</code>.
	 */
	public QuadTree search(final DrawingObject entity) {
		
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
	public Collection<DrawingObject> search(final Point position) {
		return getCollisionEntities(new AbsoluteRectangle(position.x, position.y, 1, 1));
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param tabCount
	 *            the number of tabs to print before each line
	 * @return
	 */
	public String toString(final int tabCount) {
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
					insert(e);
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
	public void addOrUpdate(final DrawingObject d) {
		final QuadTree element = search(d);
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void bringToFront(final DrawingObject dob) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clear() {
		destroySons();
		objects.clear();
	}
	
	@Override
	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect) {
		return getCollisionEntities(rect);
	}
	
	@Override
	public List<DrawingObject> getDrawingObjects() {
		return getObjectsRecursive();
	}
	
}
