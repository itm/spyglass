// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import org.eclipse.core.runtime.Assert;
import org.junit.Before;
import org.junit.Test;

// --------------------------------------------------------------------------------
/**
 * @author dariush
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
	 * {@link de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig#isAllSemanticTypes()}.
	 */
	@Test
	public void testIsAllSemanticTypes() {
		final PluginXMLConfig config = new PluginXMLConfigStub();
		config.setSemanticTypes(PluginXMLConfig.ALL_SEMANTIC_TYPES);
		Assert.isTrue(config.isAllSemanticTypes());
	}
	
	@Test
	public void testIsAllSemanticTypes2() {
		final PluginXMLConfig config = new PluginXMLConfigStub();
		config.setSemanticTypes(new int[] { 1, 2, 3, 4, 5 });
		Assert.isTrue(!config.isAllSemanticTypes());
	}
	
}
