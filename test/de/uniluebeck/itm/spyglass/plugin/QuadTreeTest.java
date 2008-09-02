package de.uniluebeck.itm.spyglass.plugin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Rectangle;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

public class QuadTreeTest {
	
	private QuadTree tree;
	
	private Rectangle rectangle1;
	private Rectangle rectangle2;
	
	private Rectangle rectangle3;
	
	private Rectangle rectangle4;
	
	private static final int upperLeftX = -500;
	private static final int upperLeftY = -500;
	
	private static final int lowerLeftX = -500;
	private static final int lowerLeftY = +500;
	
	private static final int lowerRightX = +500;
	private static final int lowerRightY = +500;
	
	private static final int upperRightX = +500;
	private static final int upperRightY = -500;
	
	private static final int width = 1000;
	private static final int height = 1000;
	
	private static final int rectWidth = 5;
	private static final int rectHeight = 5;
	
	@Before
	public void setUp() throws Exception {
		
		tree = new QuadTree(new AbsoluteRectangle(upperLeftX, upperLeftY, 1000, 1000));
		
		rectangle1 = createRect(upperLeftX, upperLeftY);
		rectangle2 = createRect(lowerRightX - rectWidth, lowerRightY - rectHeight);
		rectangle3 = createRect(upperLeftX + (width / 2) - rectWidth, upperLeftY + (height / 2) - rectHeight);
		rectangle4 = createRect(upperLeftX + (width / 2), upperLeftY + (height / 2));
		
	}
	
	private Rectangle createRect(final int upperLeftX, final int upperLeftY) {
		final Rectangle rectangle = new Rectangle();
		rectangle.setWidth(rectWidth);
		rectangle.setHeight(rectHeight);
		rectangle.setPosition(new AbsolutePosition(upperLeftX, upperLeftY, 0));
		return rectangle;
	}
	
	@After
	public void tearDown() throws Exception {
		
		tree = null;
		
		rectangle1 = null;
		rectangle2 = null;
		rectangle3 = null;
		rectangle4 = null;
		
	}
	
	@Test
	public void testContains() {
		
		addAllRectangles();
		
		assertTrue(tree.contains(rectangle1));
		assertTrue(tree.contains(rectangle2));
		assertTrue(tree.contains(rectangle3));
		assertTrue(tree.contains(rectangle4));
		
	}
	
	private void addAllRectangles() {
		
		assertTrue(tree.insert(rectangle1));
		assertTrue(tree.insert(rectangle2));
		assertTrue(tree.insert(rectangle3));
		assertTrue(tree.insert(rectangle4));
		
	}
	
	private static final AbsoluteRectangle upperLeftQuadrant = new AbsoluteRectangle(upperLeftX, upperLeftY, width / 2, height / 2);
	private static final AbsoluteRectangle upperRightQuadrant = new AbsoluteRectangle(upperLeftX + (width / 2), upperLeftY, width / 2, height / 2);
	private static final AbsoluteRectangle lowerLeftQuadrant = new AbsoluteRectangle(upperLeftX, upperLeftY + (height / 2), width / 2, height / 2);
	private static final AbsoluteRectangle lowerRightQuadrant = new AbsoluteRectangle(upperLeftX + (width / 2), upperLeftY + (height / 2), width / 2,
			height / 2);
	
	@Test
	public void testGetCollisionEntities() {
		
		addAllRectangles();
		
		List<DrawingObject> collisionEntities;
		
		// get all entities from entire box
		collisionEntities = tree.getCollisionEntities(new AbsoluteRectangle(upperLeftX, upperLeftY, width, height));
		assertTrue(collisionEntities.contains(rectangle1));
		assertTrue(collisionEntities.contains(rectangle2));
		assertTrue(collisionEntities.contains(rectangle3));
		assertTrue(collisionEntities.contains(rectangle4));
		
		// get all entities from upper left quadrant
		collisionEntities = tree.getCollisionEntities(upperLeftQuadrant);
		assertTrue(collisionEntities.size() == 2);
		assertTrue(collisionEntities.contains(rectangle1));
		assertTrue(collisionEntities.contains(rectangle3));
		
		// get all entities from lower left quadrant
		collisionEntities = tree.getCollisionEntities(lowerLeftQuadrant);
		assertTrue(collisionEntities.size() == 0);
		
		// get all entities from upper right quadrant
		collisionEntities = tree.getCollisionEntities(upperRightQuadrant);
		assertTrue(collisionEntities.size() == 0);
		
		// get all entities from lower right quadrant
		collisionEntities = tree.getCollisionEntities(lowerRightQuadrant);
		assertTrue(collisionEntities.size() == 2);
		assertTrue(collisionEntities.contains(rectangle2));
		assertTrue(collisionEntities.contains(rectangle4));
		
	}
	
	@Test
	public void testGetObjectsRecursive() {
		
		addAllRectangles();
		
		final List<DrawingObject> list = tree.getObjectsRecursive();
		
		assertTrue(list.contains(rectangle1));
		assertTrue(list.contains(rectangle2));
		assertTrue(list.contains(rectangle3));
		assertTrue(list.contains(rectangle4));
		
	}
	
	@Test
	public void testGetObjectCount() {
		
		assertSame(0, tree.getObjectCount());
		
		tree.insert(rectangle1);
		assertSame(1, tree.getObjectCount());
		
		tree.insert(rectangle2);
		assertSame(2, tree.getObjectCount());
		
		tree.insert(rectangle3);
		assertSame(3, tree.getObjectCount());
		
		tree.insert(rectangle4);
		assertSame(4, tree.getObjectCount());
		
		tree.remove(rectangle1);
		assertSame(3, tree.getObjectCount());
		
		tree.remove(rectangle2);
		assertSame(2, tree.getObjectCount());
		
		tree.remove(rectangle3);
		assertSame(1, tree.getObjectCount());
		
		tree.remove(rectangle4);
		assertSame(0, tree.getObjectCount());
		
	}
	
	@Test
	public void testInsert() {
		
		tree.insert(rectangle1);
		assertTrue(tree.contains(rectangle1));
		
		tree.insert(rectangle2);
		assertTrue(tree.contains(rectangle1));
		assertTrue(tree.contains(rectangle2));
		
		tree.insert(rectangle3);
		assertTrue(tree.contains(rectangle1));
		assertTrue(tree.contains(rectangle2));
		assertTrue(tree.contains(rectangle3));
		
		tree.insert(rectangle4);
		assertTrue(tree.contains(rectangle1));
		assertTrue(tree.contains(rectangle2));
		assertTrue(tree.contains(rectangle3));
		assertTrue(tree.contains(rectangle4));
		
	}
	
	@Test
	public void testMoveDrawingObjectIntInt() {
		
		// place into upper left quadrant
		tree.insert(rectangle1);
		assertObjectInArea(rectangle1, upperLeftQuadrant);
		
		// move to upper right quadrant
		tree.move(rectangle1, width / 2, 0);
		assertAreaEmpty(upperLeftQuadrant);
		assertObjectInArea(rectangle1, upperRightQuadrant);
		
		// move to lower right quadrant
		tree.move(rectangle1, 0, height / 2);
		assertAreaEmpty(upperRightQuadrant);
		assertObjectInArea(rectangle1, lowerRightQuadrant);
		
		// move to lower left quadrant
		tree.move(rectangle1, -(width / 2), 0);
		assertAreaEmpty(lowerRightQuadrant);
		assertObjectInArea(rectangle1, lowerLeftQuadrant);
		
		// move to upper left quadrant
		tree.move(rectangle1, 0, -(height / 2));
		assertAreaEmpty(lowerLeftQuadrant);
		assertObjectInArea(rectangle1, upperLeftQuadrant);
		
	}
	
	private void assertObjectInArea(final DrawingObject object, final AbsoluteRectangle area) {
		final List<DrawingObject> list = tree.getCollisionEntities(area);
		assertTrue(list.contains(object));
	}
	
	private void assertAreaEmpty(final AbsoluteRectangle area) {
		final List<DrawingObject> list = tree.getCollisionEntities(area);
		assertTrue(list.size() == 0);
	}
	
	@Test
	public void testMoveDrawingObjectAbsoluteRectangle() {
		
		// place into upper left quadrant
		tree.insert(rectangle1);
		assertObjectInArea(rectangle1, upperLeftQuadrant);
		
		// move to upper right quadrant
		tree.move(rectangle1, new AbsoluteRectangle(upperRightQuadrant.getUpperLeft(), 10, 10));
		assertAreaEmpty(upperLeftQuadrant);
		assertObjectInArea(rectangle1, upperRightQuadrant);
		
		// move to lower right quadrant
		tree.move(rectangle1, new AbsoluteRectangle(lowerRightQuadrant.getUpperLeft(), 10, 10));
		assertAreaEmpty(upperRightQuadrant);
		assertObjectInArea(rectangle1, lowerRightQuadrant);
		
		// move to lower left quadrant
		tree.move(rectangle1, new AbsoluteRectangle(lowerLeftQuadrant.getUpperLeft(), 10, 10));
		assertAreaEmpty(lowerRightQuadrant);
		assertObjectInArea(rectangle1, lowerLeftQuadrant);
		
		// move to upper left quadrant
		tree.move(rectangle1, new AbsoluteRectangle(upperLeftQuadrant.getUpperLeft(), 10, 10));
		assertAreaEmpty(lowerLeftQuadrant);
		assertObjectInArea(rectangle1, upperLeftQuadrant);
		
	}
	
	@Test
	public void testRemove() {
		
		addAllRectangles();
		
		tree.remove(rectangle1);
		assertFalse(tree.contains(rectangle1));
		assertTrue(tree.contains(rectangle2));
		assertTrue(tree.contains(rectangle3));
		assertTrue(tree.contains(rectangle4));
		
		tree.remove(rectangle2);
		assertFalse(tree.contains(rectangle1));
		assertFalse(tree.contains(rectangle2));
		assertTrue(tree.contains(rectangle3));
		assertTrue(tree.contains(rectangle4));
		
		tree.remove(rectangle3);
		assertFalse(tree.contains(rectangle1));
		assertFalse(tree.contains(rectangle2));
		assertFalse(tree.contains(rectangle3));
		assertTrue(tree.contains(rectangle4));
		
		tree.remove(rectangle4);
		assertFalse(tree.contains(rectangle1));
		assertFalse(tree.contains(rectangle2));
		assertFalse(tree.contains(rectangle3));
		assertFalse(tree.contains(rectangle4));
		
	}
	
	@Test
	public void testSearchDrawingObject() {
		
		addAllRectangles();
		
		assertNotNull(tree.search(rectangle1));
		assertNotNull(tree.search(rectangle2));
		assertNotNull(tree.search(rectangle3));
		assertNotNull(tree.search(rectangle4));
		
		tree.remove(rectangle1);
		assertNull(tree.search(rectangle1));
		
		tree.remove(rectangle2);
		assertNull(tree.search(rectangle2));
		
		tree.remove(rectangle3);
		assertNull(tree.search(rectangle3));
		
		tree.remove(rectangle4);
		assertNull(tree.search(rectangle4));
		
	}
	
	@Test
	public void testSearchPoint() {
		
		addAllRectangles();
		
		Collection<DrawingObject> search;
		
		// search for center point of rectangle1
		search = tree.search(new Point(upperLeftX + (rectWidth / 2), upperLeftY + (rectHeight / 2)));
		assertTrue(search.contains(rectangle1));
		
		// search for center point of rectangle2
		search = tree.search(new Point(lowerRightX - (rectWidth / 2), lowerRightY - (rectHeight / 2)));
		assertTrue(search.contains(rectangle2));
		
		// search for center point of rectangle3
		search = tree.search(new Point(upperLeftX + (width / 2) - (rectWidth / 2), upperLeftY + (height / 2) - (rectHeight / 2)));
		assertTrue(search.contains(rectangle3));
		
		// search for center point of rectangle4
		search = tree.search(new Point(upperLeftX + (width / 2) + (rectWidth / 2), upperLeftY + (height / 2) + (rectHeight / 2)));
		assertTrue(search.contains(rectangle4));
		
		// search for center point of upper left quadrant
		search = tree.search(new Point(upperLeftX + (width * 1 / 4), upperLeftY + (height * 1 / 4)));
		
		// search for center point of lower left quadrant
		search = tree.search(new Point(upperLeftX + (width * 3 / 4), upperLeftY + (height * 1 / 4)));
		assertTrue(search.isEmpty());
		
		// search for center point of upper right quadrant
		search = tree.search(new Point(upperLeftX + (width * 1 / 4), upperLeftY + (height * 3 / 4)));
		assertTrue(search.isEmpty());
		
		// search for center point of lower right quadrant
		search = tree.search(new Point(upperLeftX + (width * 3 / 4), upperLeftY + (height * 3 / 4)));
		assertTrue(search.isEmpty());
		
		// set all rectangles on the same spot and see if they are found
		rectangle2.setBoundingBox(rectangle1.getBoundingBox());
		rectangle3.setBoundingBox(rectangle1.getBoundingBox());
		rectangle4.setBoundingBox(rectangle1.getBoundingBox());
		tree.addOrUpdate(rectangle2);
		tree.addOrUpdate(rectangle3);
		tree.addOrUpdate(rectangle4);
		search = tree.search(new Point(upperLeftX + (rectWidth / 2), upperLeftY + (rectHeight / 2)));
		assertTrue(search.contains(rectangle1));
		assertTrue(search.contains(rectangle2));
		assertTrue(search.contains(rectangle3));
		assertTrue(search.contains(rectangle4));
		
	}
	
	@Test
	public void testAddOrUpdate() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testBringToFront() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testPushBack() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testClear() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetDrawingObjectsAbsoluteRectangle() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetDrawingObjects() {
		fail("Not yet implemented");
	}
	
}
