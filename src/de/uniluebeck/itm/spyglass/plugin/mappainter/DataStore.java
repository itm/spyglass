// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * A Store for DataPoints with a k-Nearest-Neighbor implementation
 * 
 * @author Dariush Forouher
 * 
 */
public class DataStore extends HashSet<DataPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8466264495244878677L;

	/**
	 * Performs a k-nearest-neighbor search on the DataStore.
	 * 
	 * @param k
	 *            number of points to return (must be at least 1)
	 * @param pos
	 *            a position
	 * @return the k points nearest to the given position (or less points, if there are not enough
	 *         in the store)
	 */
	public List<DataPoint> kNN(final AbsolutePosition pos, final int k) {
		if (k < 1) {

			throw new IllegalArgumentException("k must be at least 1!");

		} else if (k > this.size()) {

			return new ArrayList<DataPoint>(this);

		} else {

			final ArrayList<DataPoint> list = new ArrayList<DataPoint>();

			// TODO: do some profiling and replace this with an O(log n) algorithm
			// if necessary.
			for (final DataPoint dataPoint : this) {
				final double distance = pos.getEuclideanDistance(dataPoint.position);
				for (int i = 0; i < k; i++) {
					if ((list.size() < i + 1) || (list.get(i).position.getEuclideanDistance(pos) > distance)) {
						list.add(i, dataPoint);
					}
				}
			}

			return k == 1 ? Collections.singletonList(list.get(0)) : list.subList(0, k - 1);
		}
	}

}
