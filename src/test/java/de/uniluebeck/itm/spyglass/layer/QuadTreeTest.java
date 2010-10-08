package de.uniluebeck.itm.spyglass.layer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.Assert;

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

	private static final int upperLeftX = -512;
	private static final int upperLeftY = -512;

	private static final int width = 1024;
	private static final int height = 1024;

	private static final int rectWidth = 5;
	private static final int rectHeight = 5;

	private static final int rectOffset = 5;

	private static final AbsoluteRectangle upperLeftQuadrant = new AbsoluteRectangle(upperLeftX, upperLeftY, width / 2, height / 2);

	private static final AbsoluteRectangle upperRightQuadrant = new AbsoluteRectangle(upperLeftX + (width / 2), upperLeftY, width / 2, height / 2);

	private static final AbsoluteRectangle lowerLeftQuadrant = new AbsoluteRectangle(upperLeftX, upperLeftY + (height / 2), width / 2, height / 2);

	private static final AbsoluteRectangle lowerRightQuadrant = new AbsoluteRectangle(upperLeftX + (width / 2), upperLeftY + (height / 2), width / 2,
			height / 2);

	private void addAllRectangles() {

		tree.add(rectangle1);
		tree.add(rectangle2);
		tree.add(rectangle3);
		tree.add(rectangle4);

	}

	private Rectangle createRect(final int upperLeftX, final int upperLeftY) {
		final Rectangle rectangle = new Rectangle();
		rectangle.setWidth(rectWidth);
		rectangle.setHeight(rectHeight);
		rectangle.setPosition(new AbsolutePosition(upperLeftX, upperLeftY, 0));
		return rectangle;
	}

	@Before
	public void setUp() throws Exception {

		tree = new QuadTree(upperLeftX, upperLeftY, 1024, 16, 1, false);

		rectangle1 = createRect(upperLeftX + rectOffset, upperLeftY + rectOffset);
		rectangle2 = createRect(upperLeftX + (width / 2) + rectOffset, upperLeftY + rectOffset);
		rectangle3 = createRect(upperLeftX + (width / 2) + rectOffset, upperLeftY + (height / 2) + rectOffset);
		rectangle4 = createRect(upperLeftX + rectOffset, upperLeftY + (height / 2) + rectOffset);

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
	public void testAdd() {

		Set<DrawingObject> set;

		addAllRectangles();

		set = tree.getDrawingObjects(upperLeftQuadrant);
		assertTrue(set.size() == 1);
		assertTrue(set.contains(rectangle1));

		// move rect1 to upper right quadrant
		rectangle1.setPosition(upperRightQuadrant.getLowerLeft());
		set = tree.getDrawingObjects(upperLeftQuadrant);
		assertTrue(set.size() == 0);
		assertTrue(!set.contains(rectangle1));

		set = tree.getDrawingObjects(upperRightQuadrant);
		assertTrue(set.size() == 2);
		assertTrue(set.contains(rectangle1));
		assertTrue(set.contains(rectangle2));

		// move rect1 to lower right
		rectangle1.setPosition(lowerRightQuadrant.getLowerLeft());
		set = tree.getDrawingObjects(upperRightQuadrant);
		assertTrue(set.size() == 1);
		assertTrue(set.contains(rectangle2));
		set = tree.getDrawingObjects(lowerRightQuadrant);
		assertTrue(set.size() == 2);
		assertTrue(set.contains(rectangle1));
		assertTrue(set.contains(rectangle3));

		// move rect1 to lower left
		rectangle1.setPosition(lowerLeftQuadrant.getLowerLeft());
		set = tree.getDrawingObjects(lowerRightQuadrant);
		assertTrue(set.size() == 1);
		assertTrue(set.contains(rectangle3));
		assertTrue(!set.contains(rectangle1));
		set = tree.getDrawingObjects(lowerLeftQuadrant);
		assertTrue(set.size() == 2);
		assertTrue(set.contains(rectangle1));
		assertTrue(set.contains(rectangle4));

	}

	@Test
	public void testBringToFront() {

		Set<DrawingObject> set;

		addAllRectangles();

		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[0] == rectangle1);
		assertTrue(set.toArray()[3] == rectangle4);

		tree.bringToFront(rectangle1);
		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[3] == rectangle1);

		tree.bringToFront(rectangle2);
		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[3] == rectangle2);

		tree.bringToFront(rectangle3);
		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[3] == rectangle3);

		tree.bringToFront(rectangle4);
		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[3] == rectangle4);

	}

	@Test
	public void testClear() {
		addAllRectangles();
		assertTrue(tree.getDrawingObjects().size() == 4);
		tree.clear();
		assertTrue(tree.getDrawingObjects().isEmpty());
	}

	@Test
	public void testContains() {

		addAllRectangles();

		assertTrue(tree.getDrawingObjects(rectangle1.getBoundingBox()).contains(rectangle1));
		assertTrue(tree.getDrawingObjects(rectangle2.getBoundingBox()).contains(rectangle2));
		assertTrue(tree.getDrawingObjects(rectangle3.getBoundingBox()).contains(rectangle3));
		assertTrue(tree.getDrawingObjects(rectangle4.getBoundingBox()).contains(rectangle4));

	}

	@Test
	public void testGetDrawingObjects() {

		addAllRectangles();

		final Set<DrawingObject> set = tree.getDrawingObjects();
		assertTrue(set.size() == 4);
		assertTrue(set.contains(rectangle1));
		assertTrue(set.contains(rectangle2));
		assertTrue(set.contains(rectangle3));
		assertTrue(set.contains(rectangle4));

	}

	@Test
	public void testgetDrawingObjectsRectangle() {

		addAllRectangles();

		Set<DrawingObject> collisionEntities;

		// get all entities from entire box
		collisionEntities = tree.getDrawingObjects(new AbsoluteRectangle(upperLeftX, upperLeftY, width, height));
		assertTrue(collisionEntities.contains(rectangle1));
		assertTrue(collisionEntities.contains(rectangle2));
		assertTrue(collisionEntities.contains(rectangle3));
		assertTrue(collisionEntities.contains(rectangle4));

		// get all entities from upper left quadrant
		collisionEntities = tree.getDrawingObjects(upperLeftQuadrant);
		assertTrue(collisionEntities.size() == 1);
		assertTrue(collisionEntities.contains(rectangle1));

		// get all entities from upper right quadrant
		collisionEntities = tree.getDrawingObjects(upperRightQuadrant);
		assertTrue(collisionEntities.size() == 1);
		assertTrue(collisionEntities.contains(rectangle2));

		// get all entities from lower right quadrant
		collisionEntities = tree.getDrawingObjects(lowerRightQuadrant);
		assertTrue(collisionEntities.size() == 1);
		assertTrue(collisionEntities.contains(rectangle3));

		// get all entities from lower left quadrant
		collisionEntities = tree.getDrawingObjects(lowerLeftQuadrant);
		assertTrue(collisionEntities.size() == 1);
		assertTrue(collisionEntities.contains(rectangle4));

	}

	@Test
	public void testGetObjectCount() {

		assertSame(0, tree.getDrawingObjects().size());

		tree.add(rectangle1);
		assertSame(1, tree.getDrawingObjects().size());

		tree.add(rectangle2);
		assertSame(2, tree.getDrawingObjects().size());

		tree.add(rectangle3);
		assertSame(3, tree.getDrawingObjects().size());

		tree.add(rectangle4);
		assertSame(4, tree.getDrawingObjects().size());

		tree.remove(rectangle1);
		assertSame(3, tree.getDrawingObjects().size());

		tree.remove(rectangle2);
		assertSame(2, tree.getDrawingObjects().size());

		tree.remove(rectangle3);
		assertSame(1, tree.getDrawingObjects().size());

		tree.remove(rectangle4);
		assertSame(0, tree.getDrawingObjects().size());

	}

	@Test
	public void testAdd2() {

		tree.add(rectangle1);
		assertTrue(tree.getDrawingObjects().contains(rectangle1));

		tree.add(rectangle2);
		assertTrue(tree.getDrawingObjects().contains(rectangle1));
		assertTrue(tree.getDrawingObjects().contains(rectangle2));

		tree.add(rectangle3);
		assertTrue(tree.getDrawingObjects().contains(rectangle1));
		assertTrue(tree.getDrawingObjects().contains(rectangle2));
		assertTrue(tree.getDrawingObjects().contains(rectangle3));

		tree.add(rectangle4);
		assertTrue(tree.getDrawingObjects().contains(rectangle1));
		assertTrue(tree.getDrawingObjects().contains(rectangle2));
		assertTrue(tree.getDrawingObjects().contains(rectangle3));
		assertTrue(tree.getDrawingObjects().contains(rectangle4));

	}

	@Test
	public void testMoveManyEntities() {

		final Random rand = new Random();
		Rectangle o;
		AbsolutePosition position;

		final List<Rectangle> rects = new ArrayList<Rectangle>();

		for (int i = 0; i < 1000; i++) {
			o = new Rectangle();
			position = new AbsolutePosition(rand.nextInt(width - 25) + upperLeftX, rand.nextInt(height - 25) + upperLeftY);
			o.setPosition(position);
			o.setWidth(10);
			o.setHeight(10);
			rects.add(o);
			tree.add(o);
		}

		for (final Rectangle rect : rects) {
			rect.setPosition(new AbsolutePosition(5, 5));
		}

		for (final Rectangle rect : rects) {
			tree.remove(rect);
			if (tree.getDrawingObjects().contains(rect)) {
				assertTrue(false);
			}
		}

		final int size = tree.getDrawingObjects().size();
		assertTrue(size == 0);

	}

	@Test
	public void testPushBack() {
		Set<DrawingObject> set;

		addAllRectangles();

		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[3] == rectangle4);
		assertTrue(set.toArray()[0] == rectangle1);

		tree.pushBack(rectangle1);
		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[0] == rectangle1);

		tree.pushBack(rectangle2);
		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[0] == rectangle2);

		tree.pushBack(rectangle3);
		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[0] == rectangle3);

		tree.pushBack(rectangle4);
		set = tree.getDrawingObjects(true);
		assertTrue(set.toArray()[0] == rectangle4);

	}

	@Test
	public void testRemove() {

		addAllRectangles();

		tree.remove(rectangle1);
		assertFalse(tree.getDrawingObjects().contains(rectangle1));
		assertTrue(tree.getDrawingObjects().contains(rectangle2));
		assertTrue(tree.getDrawingObjects().contains(rectangle3));
		assertTrue(tree.getDrawingObjects().contains(rectangle4));

		tree.remove(rectangle2);
		assertFalse(tree.getDrawingObjects().contains(rectangle1));
		assertFalse(tree.getDrawingObjects().contains(rectangle2));
		assertTrue(tree.getDrawingObjects().contains(rectangle3));
		assertTrue(tree.getDrawingObjects().contains(rectangle4));

		tree.remove(rectangle3);
		assertFalse(tree.getDrawingObjects().contains(rectangle1));
		assertFalse(tree.getDrawingObjects().contains(rectangle2));
		assertFalse(tree.getDrawingObjects().contains(rectangle3));
		assertTrue(tree.getDrawingObjects().contains(rectangle4));

		tree.remove(rectangle4);
		assertFalse(tree.getDrawingObjects().contains(rectangle1));
		assertFalse(tree.getDrawingObjects().contains(rectangle2));
		assertFalse(tree.getDrawingObjects().contains(rectangle3));
		assertFalse(tree.getDrawingObjects().contains(rectangle4));

	}

	@Test
	public void testSearchPoint() {

		addAllRectangles();

		Set<DrawingObject> search;

		final AbsolutePosition rect1pos = rectangle1.getPosition();
		final AbsolutePosition rect2pos = rectangle2.getPosition();
		final AbsolutePosition rect3pos = rectangle3.getPosition();
		final AbsolutePosition rect4pos = rectangle4.getPosition();

		// search for center point of rectangle1
		search = tree.getDrawingObjects(new AbsoluteRectangle(rect1pos.x + rectangle1.getWidth() / 2, rect1pos.y + rectangle1.getHeight() / 2, 1, 1));
		assertTrue(search.contains(rectangle1));

		// search for center point of rectangle2
		search = tree.getDrawingObjects(new AbsoluteRectangle(rect2pos.x + rectangle1.getWidth() / 2, rect2pos.y + rectangle1.getHeight() / 2, 1, 1));
		assertTrue(search.contains(rectangle2));

		// search for center point of rectangle3
		search = tree.getDrawingObjects(new AbsoluteRectangle(rect3pos.x + rectangle1.getWidth() / 2, rect3pos.y + rectangle1.getHeight() / 2, 1, 1));
		assertTrue(search.contains(rectangle3));

		// search for center point of rectangle4
		search = tree.getDrawingObjects(new AbsoluteRectangle(rect4pos.x + rectangle1.getWidth() / 2, rect4pos.y + rectangle1.getHeight() / 2, 1, 1));
		assertTrue(search.contains(rectangle4));

		// search for center point of upper left quadrant
		search = tree.getDrawingObjects(new AbsoluteRectangle(upperLeftX + (width * 1 / 4), upperLeftY + (height * 1 / 4), 1, 1));
		assertTrue(search.isEmpty());

		// search for center point of lower left quadrant
		search = tree.getDrawingObjects(new AbsoluteRectangle(upperLeftX + (width * 3 / 4), upperLeftY + (height * 1 / 4), 1, 1));
		assertTrue(search.isEmpty());

		// search for center point of upper right quadrant
		search = tree.getDrawingObjects(new AbsoluteRectangle(upperLeftX + (width * 1 / 4), upperLeftY + (height * 3 / 4), 1, 1));
		assertTrue(search.isEmpty());

		// search for center point of lower right quadrant
		search = tree.getDrawingObjects(new AbsoluteRectangle(upperLeftX + (width * 3 / 4), upperLeftY + (height * 3 / 4), 1, 1));
		assertTrue(search.isEmpty());

	}

	/**
	 * Tests if items that lie exactly on the outer border line of the QuadTrees bounding box work.
	 */
	@Test
	public void borderTest() {

		final Rectangle rectBorderLeft = new Rectangle(upperLeftX, upperLeftY + (height / 2), rectWidth, rectHeight);
		final Rectangle rectBorderTop = new Rectangle(upperLeftX + (width / 2), upperLeftY, rectWidth, rectHeight);
		final Rectangle rectBorderRight = new Rectangle(upperLeftX + width, upperLeftY + (height / 2), rectWidth, rectHeight);
		final Rectangle rectBorderBottom = new Rectangle(upperLeftX + (width / 2), upperLeftY + height, rectWidth, rectHeight);

		try {
			tree.add(rectBorderLeft);
		} catch (final RuntimeException e) {
			assertTrue(false);
		}

		try {
			tree.add(rectBorderTop);
		} catch (final RuntimeException e) {
			assertTrue(false);
		}

		try {
			tree.add(rectBorderRight);
		} catch (final RuntimeException e) {
			assertTrue(false);
		}

		try {
			tree.add(rectBorderBottom);
		} catch (final RuntimeException e) {
			assertTrue(false);
		}

	}

	@Test
	public void zoomingTest() {

		final Layer tree = Layer.Factory.createQuadTreeLayer();

		final Rectangle r = new Rectangle();
		r.setPosition(new AbsolutePosition(0, 0, 0));
		r.setWidth(1);
		r.setHeight(1);

		tree.add(r);

		final AbsoluteRectangle rect = new AbsoluteRectangle();
		boolean failed = false;
		final int max = (int) (Math.pow(2, 15));
		for (int i = 1; i < max; i++) {

			rect.setSize(2 * i, 2 * i);
			rect.setLowerLeft(new AbsolutePosition(-1 * i, -1 * i, 0));

			if (!tree.getDrawingObjects(rect).contains(r)) {
				failed = true;
				System.out.println("Failed at " + rect);
			}

		}
		if (failed) {
			Assert.fail();
		}

	}

}
