// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

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
	
	@Test
	public void testOverwriteWith() {
		final PluginXMLConfigStub config = new PluginXMLConfigStub();
		final PluginXMLConfigStub config2 = new PluginXMLConfigStub();
		
		config.setParameterX(10);
		config2.overwriteWith(config);
		
		org.junit.Assert.assertEquals(config.getParameterX(), config2.getParameterX());
		
	}
	
}
