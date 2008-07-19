package de.uniluebeck.itm.spyglass.xmlconfig;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

public abstract class PluginXMLConfig {
	
	@Element
	private boolean isActive;
	
	@Element
	private boolean isVisible;
	
	@Element
	private String name;
	
	@Element
	private int priority;
	
	@ElementArray
	private int[] semanticTypes;
	
	public PluginXMLConfig() {
		
	}
	
	@Override
	public void finalize() throws Throwable {
		
	}
	
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * @return the isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/**
	 * @param isVisible
	 *            the isVisible to set
	 */
	public void setVisible(final boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(final int priority) {
		this.priority = priority;
	}
	
	/**
	 * @return the semanticTypes
	 */
	public int[] getSemanticTypes() {
		return semanticTypes;
	}
	
	/**
	 * @param semanticTypes
	 *            the semanticTypes to set
	 */
	public void setSemanticTypes(final int[] semanticTypes) {
		this.semanticTypes = semanticTypes;
	}
	
}