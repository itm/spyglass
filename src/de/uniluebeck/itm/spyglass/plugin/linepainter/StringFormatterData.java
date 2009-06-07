// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.linepainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uniluebeck.itm.spyglass.packet.Uint16ListPacket;
import de.uniluebeck.itm.spyglass.util.StringFormatter;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginWithStringFormatterXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Class handling all relevant information about string formatters for the line painter plug-in.<br/>
 * <br/>
 * These are:
 * <ul>
 * <li>the default string formatter string</li>
 * <li>the set of string formatters specific to some packet semantic type</li>
 * <li>
 * a buffer holding the strings parsed by string formatters for each edge of the graph constructed
 * by the line painter</li>
 * <li>a copy of the last relevant packet received for each edge and semantic type in order to
 * instantly update the strings if settings are updated</li>
 * </ul>
 * 
 * Furthermore it must react to the following events:
 * <ul>
 * <li>
 * default string formatter changes &ndash;&gt; buffer must be updated for each edge in the graph</li>
 * <li>string formatter changes for one or more semantic types &ndash;&gt; buffer must be updated
 * according to the new string formatter settings by parsing the relevant cached old packets and
 * eventually must be cleaned if a string formatter was removed for a specific semantic type</li>
 * <li>packet arrives &ndash;&gt; buffer must be updated according to the packets content and the
 * string formatter settings</li>
 * <li>edge is dropped due to timeout &ndash;&gt; buffer must be cleaned</li>
 * </ul>
 * 
 * @author Daniel Bimschas
 */
class StringFormatterData implements PropertyChangeListener {

	/**
	 * Map holding the buffered string formatter results for each edge and semantic type. Default
	 * StringFormatter uses -1 as key value for the inner map.
	 */
	private HashMap<Edge, HashMap<Integer, String>> buffer;

	/**
	 * A reference to the plug-in configuration which holds information about the default string
	 * formatter and semantic type specific string formatters.
	 */
	private LinePainterXMLConfig config;

	/**
	 * Reference to the LinePainterPlugins' graph data. Used to update the drawing objects string
	 * formatter results on the graphs' edges after parsing incoming packets.
	 */
	private GraphData graphData;

	/**
	 * Map holding the last relevant packet for each edge and semantic type.
	 */
	private HashMap<Edge, HashMap<Integer, Uint16ListPacket>> packetBuffer;

	private Object lock;

	// --------------------------------------------------------------------------------
	/**
	 * Creates a new instance.
	 * 
	 * @param config
	 *            the LinePainterPlugin config instance
	 * @param graphData
	 */
	StringFormatterData() {
		this.buffer = new HashMap<Edge, HashMap<Integer, String>>();
		this.packetBuffer = new HashMap<Edge, HashMap<Integer, Uint16ListPacket>>();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Clears all buffers and the buffer of old packets.
	 */
	public void clear() {
		buffer.clear();
		packetBuffer.clear();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the concatenated buffered StringFormatter results for the given edge <code>e</code>.
	 * 
	 * @param e
	 */
	private String getStringFormatterResult(final Edge e) {

		final StringBuffer resultBuffer = new StringBuffer();
		final HashMap<Integer, String> edgeBuffer = buffer.get(e);
		final List<Integer> sortedKeySet = new LinkedList<Integer>(edgeBuffer.keySet());
		Collections.sort(sortedKeySet);
		boolean foundDefaultStringFormatter = false;

		// check if there's a result for default string formatter and put it in the first place
		if (sortedKeySet.contains(-1)) {
			resultBuffer.append(edgeBuffer.get(-1));
			foundDefaultStringFormatter = true;
		}

		// put newline if we had default and there are more lines to add
		if (foundDefaultStringFormatter && (sortedKeySet.size() > 1)) {
			resultBuffer.append("\r\n");
		}

		// put a line for every entry (except default string formatter)
		final Iterator<Integer> iterator = sortedKeySet.iterator();
		while (iterator.hasNext()) {

			final Integer semanticType = iterator.next();
			if (semanticType != -1) {
				resultBuffer.append(edgeBuffer.get(semanticType));
			}

			if (iterator.hasNext()) {
				resultBuffer.append("\r\n");
			}

		}

		return resultBuffer.toString();

	}

	// --------------------------------------------------------------------------------
	/**
	 * Called when the default string formatter is changed in the plug-in settings. Buffer gets
	 * updated for each edge in the graph by re-parsing the relevant packets from the packet buffer.
	 */
	private void handleDefaultStringFormatterChanged() {
		handleStringFormattersChanged();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Called when one or more semantic type specific string formatters were changed. Buffer gets
	 * updated according to the new string formatter settings by parsing the relevant cached old
	 * packets and eventually gets cleaned if a string formatter was removed for a specific semantic
	 * type
	 */
	private void handleStringFormattersChanged() {

		// brute force re-parse all packets from packet buffer with the new string formatter
		// settings (sub-prime performance but easy to implement ;-)
		for (final Edge edge : buffer.keySet()) {
			edge.line.setStringFormatterResult("");
		}
		buffer.clear();

		for (final HashMap<Integer, Uint16ListPacket> packetBufferEntry : packetBuffer.values()) {
			for (final Uint16ListPacket packet : packetBufferEntry.values()) {
				parsePacket(packet);
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * To be called after line painter plug-ins' instance is initialized. Registers
	 * PropertyChangeListeners on the plug-ins' XML configuration object.
	 * 
	 * @param config
	 * @param graphData
	 * @param lock
	 */
	void init(final LinePainterXMLConfig config, final GraphData graphData, final Object lock) {

		this.config = config;
		this.graphData = graphData;
		this.lock = lock;

		this.config.addPropertyChangeListener(PluginWithStringFormatterXMLConfig.PROPERTYNAME_DEFAULT_STRING_FORMATTER, this);
		this.config.addPropertyChangeListener(PluginWithStringFormatterXMLConfig.PROPERTYNAME_STRING_FORMATTERS, this);

	}

	// --------------------------------------------------------------------------------
	/**
	 * Parses the packet and updates StringFormatter strings on the graphs' edges according the the
	 * packages contents and StringFormatters' settings.
	 * 
	 * @param packet
	 */
	void parsePacket(final Uint16ListPacket packet) {

		final int nodeId = packet.getSenderId();
		final Set<Edge> incidentEdges;
		synchronized (lock) {
			incidentEdges = graphData.getIncidentEdges(nodeId);
		}
		HashMap<Integer, Uint16ListPacket> packetBufferEntry;
		HashMap<Integer, String> bufferEntry;
		final String defaultStringFormatter = config.getDefaultStringFormatter();
		final String stringFormatter = config.getStringFormatters().get(packet.getSemanticType());
		boolean edgeNeedsUpdate;

		for (final Edge e : incidentEdges) {

			edgeNeedsUpdate = false;

			// put this packet as last received packet for the semantic type of this packet
			packetBufferEntry = packetBuffer.containsKey(e) ? packetBuffer.get(e) : new HashMap<Integer, Uint16ListPacket>();
			packetBufferEntry.put(packet.getSemanticType(), packet);
			packetBuffer.put(e, packetBufferEntry);

			// get the buffer entry for this edge
			bufferEntry = buffer.containsKey(e) ? buffer.get(e) : new HashMap<Integer, String>();
			buffer.put(e, bufferEntry);

			// check if there's a default string formatter and update the buffer according to the
			// received package (semanticType of -1 means default string formatter)
			edgeNeedsUpdate = edgeNeedsUpdate || updateBuffer(defaultStringFormatter, bufferEntry, packet, -1);

			// put updated string formatter result for the received package if there's one for the
			// semantic type of this packet
			edgeNeedsUpdate = edgeNeedsUpdate || updateBuffer(stringFormatter, bufferEntry, packet, packet.getSemanticType());

			if (edgeNeedsUpdate) {
				e.line.setStringFormatterResult(getStringFormatterResult(e));
			}

		}

	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (PluginWithStringFormatterXMLConfig.PROPERTYNAME_DEFAULT_STRING_FORMATTER.equals(evt.getPropertyName())) {
			handleDefaultStringFormatterChanged();
		} else if (PluginWithStringFormatterXMLConfig.PROPERTYNAME_STRING_FORMATTERS.equals(evt.getPropertyName())) {
			handleStringFormattersChanged();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * To be called when shutting down the line painter plug-ins' instance. Removes all
	 * PropertyChangeListeners registered on the plug-ins' XML configuration object.
	 */
	void shutdown() {

		this.config.removePropertyChangeListener(PluginWithStringFormatterXMLConfig.PROPERTYNAME_DEFAULT_STRING_FORMATTER, this);
		this.config.removePropertyChangeListener(PluginWithStringFormatterXMLConfig.PROPERTYNAME_STRING_FORMATTERS, this);

	}

	// --------------------------------------------------------------------------------
	/**
	 * Updates a buffer entry if <code>stringFormatterString</code> is not null, not empty and the
	 * parsing result of <code>packet</code> is not the same as the old.
	 * 
	 * @param stringFormatterString
	 * @param bufferEntry
	 * @param packet
	 * @param semanticType
	 * @return <code>true</code> if something was updated, <code>false</code> otherwise
	 */
	private boolean updateBuffer(final String stringFormatterString, final HashMap<Integer, String> bufferEntry, final Uint16ListPacket packet,
			final int semanticType) {

		if ((stringFormatterString != null) && !"".equals(stringFormatterString)) {

			final String parseResult = new StringFormatter(stringFormatterString).parse(packet);
			final String oldParseResult = bufferEntry.get(semanticType);

			if ((oldParseResult == null) || !oldParseResult.equals(parseResult)) {
				bufferEntry.put(semanticType, parseResult);
				return true;
			}

		}

		return false;

	}

	// --------------------------------------------------------------------------------
	/**
	 * @param e
	 */
	public void removeEdge(final Edge e) {
		buffer.remove(e);
		packetBuffer.remove(e);
	}

}
