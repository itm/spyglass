// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS;

// --------------------------------------------------------------------------------
/**
 * @author Sebastian Ebers
 * 
 */
public class StatisticalOperationTest {

	private static StatisticalOperation so;

	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		so = new StatisticalOperation(10, STATISTICAL_OPERATIONS.SUM);
		so.addValue(1);
		so.addValue(4);
		so.addValue(7);
		so.addValue(5);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#addValue(float)}
	 * .
	 */
	@Test
	public final void testAddValueSUM1() {
		final StatisticalOperation statisticalOperation = new StatisticalOperation(10, STATISTICAL_OPERATIONS.SUM);
		statisticalOperation.addValue(1);
		statisticalOperation.addValue(2);
		statisticalOperation.addValue(3);
		statisticalOperation.addValue(4);
		statisticalOperation.addValue(5);
		statisticalOperation.addValue(6);
		statisticalOperation.addValue(7);
		statisticalOperation.addValue(8);
		statisticalOperation.addValue(9);
		assertEquals(55, statisticalOperation.addValue(10));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#addValue(float)}
	 * .
	 */
	@Test
	public final void testAddValueSUM2() {
		final StatisticalOperation statisticalOperation = new StatisticalOperation(10, STATISTICAL_OPERATIONS.SUM);
		statisticalOperation.addValue(1);
		statisticalOperation.addValue(2);
		statisticalOperation.addValue(3);
		statisticalOperation.addValue(4);
		statisticalOperation.addValue(5);
		statisticalOperation.addValue(6);
		statisticalOperation.addValue(7);
		statisticalOperation.addValue(8);
		statisticalOperation.addValue(9);
		statisticalOperation.addValue(10);
		assertEquals(65, statisticalOperation.addValue(11));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#addValue(float)}
	 * .
	 */
	@Test
	public final void testAddValueAVG() {
		final StatisticalOperation statisticalOperation = new StatisticalOperation(10, STATISTICAL_OPERATIONS.AVG);
		statisticalOperation.addValue(1);
		assertEquals(2, statisticalOperation.addValue(3));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#addValue(float)}
	 * .
	 */
	@Test
	public final void testAddValueMAX() {
		final StatisticalOperation statisticalOperation = new StatisticalOperation(10, STATISTICAL_OPERATIONS.MAX);
		statisticalOperation.addValue(1);
		assertEquals(3, statisticalOperation.addValue(3));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#addValue(float)}
	 * .
	 */
	@Test
	public final void testAddValueMIN() {
		final StatisticalOperation statisticalOperation = new StatisticalOperation(10, STATISTICAL_OPERATIONS.MIN);
		statisticalOperation.addValue(1);
		assertEquals(1, statisticalOperation.addValue(3));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#addValue(float)}
	 * .
	 */
	@Test
	public final void testAddValueMEDIAN1() {
		final StatisticalOperation statisticalOperation = new StatisticalOperation(10, STATISTICAL_OPERATIONS.MEDIAN);
		statisticalOperation.addValue(1);
		assertEquals(1, statisticalOperation.addValue(3));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#addValue(float)}
	 * .
	 */
	@Test
	public final void testAddValueMEDIAN2() {
		final StatisticalOperation statisticalOperation = new StatisticalOperation(10, STATISTICAL_OPERATIONS.MEDIAN);
		statisticalOperation.addValue(1);
		statisticalOperation.addValue(4);
		statisticalOperation.addValue(7);
		statisticalOperation.addValue(5);
		assertEquals(4, statisticalOperation.addValue(3));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#addValue(float)}
	 * .
	 */
	@Test
	public final void testAddValueMEDIAN3() {
		final StatisticalOperation statisticalOperation = new StatisticalOperation(10, STATISTICAL_OPERATIONS.MEDIAN);
		statisticalOperation.addValue(1);
		statisticalOperation.addValue(1);
		statisticalOperation.addValue(5);
		statisticalOperation.addValue(6);
		statisticalOperation.addValue(20);
		statisticalOperation.addValue(1000);
		statisticalOperation.addValue(5); // this is the first item which counts again
		statisticalOperation.addValue(1);
		statisticalOperation.addValue(9);
		statisticalOperation.addValue(2);
		statisticalOperation.addValue(7);
		statisticalOperation.addValue(8);
		statisticalOperation.addValue(1);
		statisticalOperation.addValue(4);
		statisticalOperation.addValue(7);
		assertEquals(5, statisticalOperation.addValue(6));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#getValue(de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS)}
	 * .
	 */
	@Test
	public final void testGetValueSUM() {
		assertEquals(17, so.getValue(STATISTICAL_OPERATIONS.SUM));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#getValue(de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS)}
	 * .
	 */
	@Test
	public final void testGetValueAVG() {
		assertEquals(17 / 4, so.getValue(STATISTICAL_OPERATIONS.AVG));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#getValue(de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS)}
	 * .
	 */
	@Test
	public final void testGetValueMIN() {
		assertEquals(1, so.getValue(STATISTICAL_OPERATIONS.MIN));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#getValue(de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS)}
	 * .
	 */
	@Test
	public final void testGetValueMAX() {
		assertEquals(7, so.getValue(STATISTICAL_OPERATIONS.MAX));
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation#getValue(de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS)}
	 * .
	 */
	@Test
	public final void testGetValueMEDIAN() {
		assertEquals(4, so.getValue(STATISTICAL_OPERATIONS.MEDIAN));
	}

}
