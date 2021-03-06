// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

// --------------------------------------------------------------------------------
/**
 * @author Dariush Forouher
 * 
 */
public class PluginXMLConfigTests {
	
	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig#getAllSemanticTypes()}.
	 */
	@Test
	public void testIsAllSemanticTypes() {
		final PluginXMLConfig config = new PluginXMLConfigStub();
		// config.setSemanticTypes(PluginXMLConfig.ALL_SEMANTIC_TYPES);
		config.setSemanticTypes(new int[] { -1, 0, 10 });
		Assert.assertTrue(config.isAllSemanticTypes());
	}
	
	@Test
	public void testIsAllSemanticTypes2() {
		final PluginXMLConfig config = new PluginXMLConfigStub();
		config.setSemanticTypes(new int[] { 1, 2, 3, 4, 5 });
		Assert.assertTrue(!config.isAllSemanticTypes());
	}
	
	@Test
	public void testOverwriteWith() {
		final PluginXMLConfigStub config = new PluginXMLConfigStub();
		final PluginXMLConfigStub config2 = new PluginXMLConfigStub();
		
		config.setParameterX(10);
		config2.overwriteWith(config);
		
		org.junit.Assert.assertEquals(config.getParameterX(), config2.getParameterX());
		
	}
	
}
