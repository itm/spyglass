// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;


// --------------------------------------------------------------------------------
/**
 * A Store for DataPoints with a k-Nearest-Neighbor implementation
 *
 * @author Dariush Forouher
 *
 */
public class DataStore extends AbstractDataStore<DataPoint> {

	/**
	 *
	 */
	private static final long serialVersionUID = -8466264495244878677L;


	@Override
	public DataStore clone() {
		final DataStore store = new DataStore();

		store.addAll(this);

		return store;
	}

}
