// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.AbstractPosition;
import de.uniluebeck.itm.spyglass.positions.AbstractRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * @author dariush
 *
 */
public class DrawingAreaTests {

	private static Logger log = SpyglassLoggerFactory.getLogger(DrawingAreaTests.class);

	private Display display;
	private Shell shell;
	private Spyglass app;
	private DrawingArea da;

	private PixelRectangle pxRect;

	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		display = new Display();
		shell = new Shell(display);

		app = new Spyglass();
		final AppWindow appWindow = new AppWindow(app, shell);
		app.start();

		da = appWindow.getGui().getDrawingArea();

		// expect no zoom at the beginning
		Assert.assertEquals(1.0, da.getZoom());

		pxRect = da.getDrawingRectangle();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		app.shutdown();
		display.dispose();
	}

	/**
	 * Test method for {@link de.uniluebeck.itm.spyglass.gui.view.DrawingArea#absPoint2PixelPoint(de.uniluebeck.itm.spyglass.positions.AbsolutePosition)}.
	 */
	@Test
	public void testAbsPoint2PixelPoint() {
		final AbsolutePosition pos = new AbsolutePosition(0,0);
		final PixelPosition expect = new PixelPosition(0,0);
		fuzzyEquals(expect,da.absPoint2PixelPoint(pos));
	}

	@Test
	public void testAbsPoint2PixelPoint2() {
		final AbsolutePosition pos = new AbsolutePosition(150,0);
		final PixelPosition expect = new PixelPosition(150,0);
		fuzzyEquals(expect,da.absPoint2PixelPoint(pos));
	}

	@Test
	public void testAbsPoint2PixelPoint3() {
		final AbsolutePosition pos = new AbsolutePosition(0,150);
		final PixelPosition expect = new PixelPosition(0,-150);
		fuzzyEquals(expect,da.absPoint2PixelPoint(pos));
	}

	@Test
	public void testAbsPoint2PixelPoint4() {
		final AbsolutePosition pos = new AbsolutePosition(150,150);
		final PixelPosition expect = new PixelPosition(150,-150);
		fuzzyEquals(expect,da.absPoint2PixelPoint(pos));
	}

	@Test
	public void testAbsPoint2PixelPoint5() {
		final AbsolutePosition pos = new AbsolutePosition(-500,200);
		final PixelPosition expect = new PixelPosition(-500,-200);
		fuzzyEquals(expect,da.absPoint2PixelPoint(pos));
	}

	/**
	 * Test method for {@link de.uniluebeck.itm.spyglass.gui.view.DrawingArea#absRect2PixelRect(de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle)}.
	 */
	@Test
	public void testAbsRect2PixelRect() {
		final AbsoluteRectangle rect = new AbsoluteRectangle(0,0,100,100);
		final PixelRectangle exp = new PixelRectangle(0,-100,100,100);
		fuzzyEquals(exp,da.absRect2PixelRect(rect));
	}

	@Test
	public void testAbsRect2PixelRect2() {
		final AbsoluteRectangle rect = new AbsoluteRectangle(0,-50,100,100);
		final PixelRectangle exp = new PixelRectangle(0,-50,100,100);
		fuzzyEquals(exp,da.absRect2PixelRect(rect));
	}

	@Test
	public void testAbsRect2PixelRect3() {
		final AbsoluteRectangle rect = new AbsoluteRectangle(0,150,100,100);
		final PixelRectangle exp = new PixelRectangle(0,-250,100,100);
		fuzzyEquals(exp,da.absRect2PixelRect(rect));
	}

	/**
	 * fuzzy equals to count in rounding errors
	 */
	private void fuzzyEquals(final AbstractRectangle rect1, final AbstractRectangle rect2) {
		Assert.assertNotNull(rect1);
		Assert.assertNotNull(rect2);
		Assert.assertNotNull(rect1.rectangle);
		Assert.assertNotNull(rect2.rectangle);
		Assert.assertTrue(rect1.rectangle.x +  " vs. " +  rect2.rectangle.x, Math.abs(rect1.rectangle.x - rect2.rectangle.x) <= 1);
		Assert.assertTrue(rect1.rectangle.y +  " vs. " +  rect2.rectangle.y, Math.abs(rect1.rectangle.y - rect2.rectangle.y) <= 1);
		Assert.assertTrue(rect1.rectangle.height +  " vs. " +  rect2.rectangle.height, Math.abs(rect1.rectangle.height - rect2.rectangle.height) <= 1);
		Assert.assertTrue(rect1.rectangle.width +  " vs. " +  rect2.rectangle.width, Math.abs(rect1.rectangle.width - rect2.rectangle.width) <= 1);
	}

	private void fuzzyEquals(final AbstractPosition pos1, final AbstractPosition pos2) {
		Assert.assertNotNull(pos1);
		Assert.assertNotNull(pos2);
		Assert.assertTrue(pos1.x + " vs. " + pos2.x, Math.abs(pos1.x - pos2.x) <= 1);
		Assert.assertTrue(pos1.y + " vs. " + pos2.y, Math.abs(pos1.y - pos2.y) <= 1);
	}

	/**
	 * Test method for {@link de.uniluebeck.itm.spyglass.gui.view.DrawingArea#pixelPoint2AbsPoint(de.uniluebeck.itm.spyglass.positions.PixelPosition)}.
	 */
	@Test
	public void testPixelPoint2AbsPoint() {
		final PixelPosition pos = new PixelPosition(0,0);
		final AbsolutePosition expect = new AbsolutePosition(0,0);
		fuzzyEquals(expect,da.pixelPoint2AbsPoint(pos));
	}

	@Test
	public void testPixelPoint2AbsPoint2() {
		final PixelPosition pos = new PixelPosition(100,0);
		final AbsolutePosition expect = new AbsolutePosition(100,0);
		fuzzyEquals(expect,da.pixelPoint2AbsPoint(pos));
	}

	@Test
	public void testPixelPoint2AbsPoint3() {
		final PixelPosition pos = new PixelPosition(0,100);
		final AbsolutePosition expect = new AbsolutePosition(0,-100);
		fuzzyEquals(expect,da.pixelPoint2AbsPoint(pos));
	}

	@Test
	public void testPixelPoint2AbsPoint4() {
		final PixelPosition pos = new PixelPosition(0,-200);
		final AbsolutePosition expect = new AbsolutePosition(0,200);
		fuzzyEquals(expect,da.pixelPoint2AbsPoint(pos));
	}

	/**
	 * Test method for {@link de.uniluebeck.itm.spyglass.gui.view.DrawingArea#pixelRect2AbsRect(de.uniluebeck.itm.spyglass.positions.PixelRectangle)}.
	 */
	@Test
	public void testPixelRect2AbsRect() {
		final PixelRectangle rect = new PixelRectangle(0,150,100,100);
		final AbsoluteRectangle exp = new AbsoluteRectangle(0,-250,100,100);
		fuzzyEquals(exp,da.pixelRect2AbsRect(rect));
	}

	@Test
	public void testPixelRect2AbsRect2() {
		final PixelRectangle rect = new PixelRectangle(0,0,100,100);
		final AbsoluteRectangle exp = new AbsoluteRectangle(0,-100,100,100);
		fuzzyEquals(exp,da.pixelRect2AbsRect(rect));
	}

	@Test
	public void testPixelRect2AbsRect3() {
		final PixelRectangle rect = new PixelRectangle(0,-50,100,100);
		final AbsoluteRectangle exp = new AbsoluteRectangle(0,-50,100,100);
		fuzzyEquals(exp,da.pixelRect2AbsRect(rect));
	}

}
