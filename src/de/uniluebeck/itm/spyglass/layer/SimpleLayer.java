// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.layer;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.spyglass.drawing.BoundingBoxChangeListener;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

// --------------------------------------------------------------------------------
/**
 * A naive layer implementation which can be utilized as a baseline
 * when doing performance tests against the QuadTree. 
 * 
 * internally the  SimpleLayer uses an ArrayList which guaranties fixed
 * and known runtime constrains.
 * 
 * All methods of this class have a O(n) runtime, where n denotes the
 * number of items currently residing in the layer.
 * 
 * @author Dariush
 *
 */
public class SimpleLayer implements Layer, BoundingBoxChangeListener {

	private ArrayList<DrawingObject> list = new ArrayList<DrawingObject>();
	
	protected void add(final DrawingObject d) {
		list.add(d);
		d.addBoundingBoxChangedListener(this);		
	}
	
	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#addOrUpdate(de.uniluebeck.itm.spyglass.drawing.DrawingObject)
	 */
	@Override
	public synchronized void addOrUpdate(final DrawingObject d) {
		this.remove(d);
		this.add(d);
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#bringToFront(de.uniluebeck.itm.spyglass.drawing.DrawingObject)
	 */
	@Override
	public synchronized void bringToFront(final DrawingObject dob) {
		this.remove(dob);
		list.add(0,dob);
		dob.addBoundingBoxChangedListener(this);
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#clear()
	 */
	@Override
	public synchronized void clear() {
		for (final DrawingObject d : this.list) {
			d.removeBoundingBoxChangeListener(this);
			list.remove(d);
		}
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#getDrawingObjects(de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle)
	 */
	@Override
	public synchronized List<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect) {
		final ArrayList<DrawingObject> ret = new ArrayList<DrawingObject>();
		for (final DrawingObject d : list) {
			if (rect.intersects(d.getBoundingBox())) {
				ret.add(d);
			}
		}
		return ret;
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#getDrawingObjects()
	 */
	@Override
	public synchronized List<DrawingObject> getDrawingObjects() {
		return new ArrayList<DrawingObject>(list); // O(n)
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#pushBack(de.uniluebeck.itm.spyglass.drawing.DrawingObject)
	 */
	@Override
	public synchronized void pushBack(final DrawingObject object) {
		this.remove(object);
		this.add(object);
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#remove(de.uniluebeck.itm.spyglass.drawing.DrawingObject)
	 */
	@Override
	public synchronized boolean remove(final DrawingObject d) {
		d.removeBoundingBoxChangeListener(this);
		return list.remove(d); // O(n)
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.drawing.BoundingBoxChangeListener#onBoundingBoxChanged(de.uniluebeck.itm.spyglass.drawing.DrawingObject)
	 */
	@Override
	public void onBoundingBoxChanged(final DrawingObject updatedDrawingObject) {
		this.addOrUpdate(updatedDrawingObject);
	}

}
