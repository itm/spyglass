package de.uniluebeck.itm.spyglass.util;

/*
 * (C) 2004 - Geotechnical Software Services
 *
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.
 */
/**
 * Collection of geometry utility methods. All methods are static.
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public final class Geometry {

	// --------------------------------------------------------------------------------
	/**
	 * Return true if c is between a and b.
	 */
	private static boolean isBetween(final double a, final double b, final double c) {
		return b > a ? (c >= a) && (c <= b) : (c >= b) && (c <= a);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if two double precision numbers are "equal", i.e. close enough to a given limit.
	 * 
	 * @param a
	 *            First number to check
	 * @param b
	 *            Second number to check
	 * @param limit
	 *            The definition of "equal".
	 * @return True if the twho numbers are "equal", false otherwise
	 */
	private static boolean equals(final double a, final double b, final double limit) {
		return Math.abs(a - b) < limit;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if two double precision numbers are "equal", i.e. close enough to a prespecified limit.
	 * 
	 * @param a
	 *            First number to check
	 * @param b
	 *            Second number to check
	 * @return True if the twho numbers are "equal", false otherwise
	 */
	private static boolean equals(final double a, final double b) {
		return equals(a, b, 1.0e-5);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Return smallest of four numbers.
	 * 
	 * @param a
	 *            First number to find smallest among.
	 * @param b
	 *            Second number to find smallest among.
	 * @param c
	 *            Third number to find smallest among.
	 * @param d
	 *            Fourth number to find smallest among.
	 * @return Smallest of a, b, c and d.
	 */
	private static double min(final double a, final double b, final double c, final double d) {
		return Math.min(Math.min(a, b), Math.min(c, d));
	}

	// --------------------------------------------------------------------------------
	/**
	 * Return largest of four numbers.
	 * 
	 * @param a
	 *            First number to find largest among.
	 * @param b
	 *            Second number to find largest among.
	 * @param c
	 *            Third number to find largest among.
	 * @param d
	 *            Fourth number to find largest among.
	 * @return Largest of a, b, c and d.
	 */
	private static double max(final double a, final double b, final double c, final double d) {
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if a specified point is inside a specified rectangle.
	 * 
	 * @param x0
	 *            x-coordinate of the upper left corner of rectangle (inclusive)
	 * @param y0
	 *            y-coordinate of the upper left corner of rectangle (inclusive)
	 * @param x1
	 *            x-coordinate of the lower right corner of rectangle (inclusive)
	 * @param y1
	 *            y-coordinate of the lower right corner of rectangle (inclusive)
	 * @param x
	 *            x-coordinate of the point to check
	 * 
	 * @param y
	 *            y-coordinate of the point to check
	 * @return True if the point is inside the rectangle, false otherwise.
	 */
	public static boolean isPointInsideRectangle(final int x0, final int y0, final int x1, final int y1, final int x, final int y) {
		return (x >= x0) && (x < x1) && (y >= y0) && (y < y1);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if a given point is inside a given (complex) polygon.
	 * 
	 * @param x
	 *            the polygon's x-coordinates
	 * @param y
	 *            the polygon's y-coordinates
	 * @param pointX
	 *            the point's x-coordinates
	 * @param pointY
	 *            the point's y-coordinates
	 * @return True if the given point is inside the polygon, false otherwise.
	 */
	public static boolean isPointInsidePolygon(final double[] x, final double[] y, final double pointX, final double pointY) {
		boolean isInside = false;
		final int nPoints = x.length;

		int j = 0;
		for (int i = 0; i < nPoints; i++) {
			j++;
			if (j == nPoints) {
				j = 0;
			}

			if (((y[i] < pointY) && (y[j] >= pointY)) || ((y[j] < pointY) && (y[i] >= pointY))) {
				if (x[i] + (pointY - y[i]) / (y[j] - y[i]) * (x[j] - x[i]) < pointX) {
					isInside = !isInside;
				}
			}
		}

		return isInside;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if a given point is inside a given polygon. Integer domain.
	 * 
	 * @param x
	 *            the polygon's x-coordinates
	 * @param y
	 *            the polygon's y-coordinates
	 * @param pointX
	 *            the point's x-coordinates
	 * @param pointY
	 *            the point's y-coordinates
	 * @return True if the given point is inside the polygon, false otherwise.
	 */
	public static boolean isPointInsidePolygon(final int[] x, final int[] y, final int pointX, final int pointY) {
		boolean isInside = false;
		final int nPoints = x.length;

		int j = 0;
		for (int i = 0; i < nPoints; i++) {
			j++;
			if (j == nPoints) {
				j = 0;
			}

			if (((y[i] < pointY) && (y[j] >= pointY)) || ((y[j] < pointY) && (y[i] >= pointY))) {
				if (x[i] + (double) (pointY - y[i]) / (double) (y[j] - y[i]) * (x[j] - x[i]) < pointX) {
					isInside = !isInside;
				}
			}
		}

		return isInside;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Find the point on the line p0,p1 [x,y,z] a given fraction from p0. Fraction of 0.0 whould
	 * give back p0, 1.0 give back p1, 0.5 returns midpoint of line p0,p1 and so on. F raction can
	 * be >1 and it can be negative to return any point on the line specified by p0,p1.
	 * 
	 * @param p0
	 *            First coordinate of line [x,y,z].
	 * @param p1
	 *            Second coordinate of line [x,y,z].
	 * @param fractionFromP0
	 *            Point we are looking for coordinates of
	 * @return Coordinate of point we are looking for
	 */
	public static double[] computePointOnLine(final double[] p0, final double[] p1, final double fractionFromP0) {
		final double[] p = new double[3];

		p[0] = p0[0] + fractionFromP0 * (p1[0] - p0[0]);
		p[1] = p0[1] + fractionFromP0 * (p1[1] - p0[1]);
		p[2] = p0[2] + fractionFromP0 * (p1[2] - p0[2]);

		return p;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Find the point on the line defined by x0,y0,x1,y1 a given fraction from x0,y0. 2D version of
	 * method above..
	 * 
	 * @param x0
	 *            First point defining the line
	 * @param y0
	 *            First point defining the line
	 * @param x1
	 *            Second point defining the line
	 * @param y1
	 *            Second point defining the line
	 * @param fractionFrom0
	 *            Distance from (x0,y0)
	 * @return x, y Coordinate of point we are looking for
	 */
	public static double[] computePointOnLine(final double x0, final double y0, final double x1, final double y1, final double fractionFrom0) {
		final double[] p0 = { x0, y0, 0.0 };
		final double[] p1 = { x1, y1, 0.0 };

		final double[] p = Geometry.computePointOnLine(p0, p1, fractionFrom0);

		final double[] r = { p[0], p[1] };
		return r;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Extend a given line segment to a specified length.
	 * 
	 * @param p0
	 *            Line segment to extend [x,y,z].
	 * @param p1
	 *            Line segment to extend [x,y,z].
	 * @param toLength
	 *            Length of new line segment.
	 * @param anchor
	 *            Specifies the fixed point during extension. If anchor is 0.0, p0 is fixed and p1
	 *            is adjusted. If anchor is 1.0, p1 is fixed and p0 is adjusted. If anchor is 0.5,
	 *            the line is adjusted equally in each direction and so on.
	 */
	public static void extendLine(final double[] p0, final double[] p1, final double toLength, final double anchor) {
		final double[] p = Geometry.computePointOnLine(p0, p1, anchor);

		final double length0 = toLength * anchor;
		final double length1 = toLength * (1.0 - anchor);

		Geometry.extendLine(p, p0, length0);
		Geometry.extendLine(p, p1, length1);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Extend a given line segment to a given length and holding the first point of the line as
	 * fixed.
	 * 
	 * @param p0
	 *            Line segment to extend. p0 is fixed during extension
	 * @param p1
	 *            Line segment to extend.
	 * @param toLength
	 *            Length of new line segment.
	 */
	public static void extendLine(final double[] p0, final double[] p1, final double toLength) {
		final double oldLength = Geometry.length(p0, p1);
		final double lengthFraction = oldLength != 0.0 ? toLength / oldLength : 0.0;

		p1[0] = p0[0] + (p1[0] - p0[0]) * lengthFraction;
		p1[1] = p0[1] + (p1[1] - p0[1]) * lengthFraction;
		p1[2] = p0[2] + (p1[2] - p0[2]) * lengthFraction;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Return the length of a vector.
	 * 
	 * @param v
	 *            Vector to compute length of [x,y,z].
	 * @return Length of vector.
	 */
	public static double length(final double[] v) {
		return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute distance between two points.
	 * 
	 * @param p0
	 *            Point [x,y,z].
	 * @param p1
	 *            Point [x,y,z].
	 * @return Distance between points.
	 */
	public static double length(final double[] p0, final double[] p1) {
		final double[] v = Geometry.createVector(p0, p1);
		return length(v);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute the length of the line from (x0,y0) to (x1,y1)
	 * 
	 * @param x0
	 *            First line end point.
	 * @param y0
	 *            First line end point.
	 * @param x1
	 *            Second line end point.
	 * @param y1
	 *            Second line end point.
	 * @return Length of line from (x0,y0) to (x1,y1).
	 */
	public static double length(final int x0, final int y0, final int x1, final int y1) {
		return Geometry.length((double) x0, (double) y0, (double) x1, (double) y1);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute the length of the line from (x0,y0) to (x1,y1)
	 * 
	 * @param x0
	 *            First line end point.
	 * @param y0
	 *            First line end point.
	 * @param x1
	 *            Second line end point.
	 * @param y1
	 *            Second line end point.
	 * @return Length of line from (x0,y0) to (x1,y1).
	 */
	public static double length(final double x0, final double y0, final double x1, final double y1) {
		final double dx = x1 - x0;
		final double dy = y1 - y0;

		return Math.sqrt(dx * dx + dy * dy);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute the length of a polyline.
	 * 
	 * @param x
	 *            Arrays of x coordinates
	 * @param y
	 *            Arrays of y coordinates
	 * @param isClosed
	 *            True if this is a closed polygon, false otherwise
	 * @return Length of polyline defined by x, y and nPoints.
	 */
	public static double length(final int[] x, final int[] y, final boolean isClosed) {
		double length = 0.0;

		final int nPoints = x.length;
		for (int i = 0; i < nPoints - 1; i++) {
			length += Geometry.length(x[i], y[i], x[i + 1], y[i + 1]);
		}

		// Add last leg if this is a polygon
		if (isClosed && (nPoints > 1)) {
			length += Geometry.length(x[nPoints - 1], y[nPoints - 1], x[0], y[0]);
		}

		return length;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Return distance between the line defined by (x0,y0) and (x1,y1) and the point (x,y). Ref:
	 * http://astronomy.swin.edu.au/pbourke/geometry/pointline/ The 3D case should be similar.
	 * 
	 * @param x0
	 *            First point of line.
	 * @param y0
	 *            First point of line.
	 * @param x1
	 *            Second point of line.
	 * @param y1
	 *            Second point of line.
	 * @param x
	 *            Point to consider.
	 * @param y
	 *            , Point to consider.
	 * @return Distance from x,y down to the (extended) line defined by x0, y0, x1, y1.
	 */
	public static double distance(final int x0, final int y0, final int x1, final int y1, final int x, final int y) {
		// If x0,y0,x1,y1 is same point, we return distance to that point
		double length = Geometry.length(x0, y0, x1, y1);
		if (length == 0.0) {
			return Geometry.length(x0, y0, x, y);
		}

		// If u is [0,1] then (xp,yp) is on the line segment (x0,y0),(x1,y1).
		final double u = ((x - x0) * (x1 - x0) + (y - y0) * (y1 - y0)) / (length * length);

		// This is the intersection point of the normal.
		final double xp = x0 + u * (x1 - x0);
		final double yp = y0 + u * (y1 - y0);

		length = Geometry.length(xp, yp, x, y);
		return length;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Find the angle between three points. P0 is center point
	 * 
	 * @param p0
	 *            Point 1 [x,y,z].
	 * @param p1
	 *            Point 2 [x,y,z].
	 * @param p2
	 *            Point 3 [x,y,z].
	 * @return Angle (in radians) between given points.
	 */
	public static double computeAngle(final double[] p0, final double[] p1, final double[] p2) {
		final double[] v0 = Geometry.createVector(p0, p1);
		final double[] v1 = Geometry.createVector(p0, p2);

		final double dotProduct = Geometry.computeDotProduct(v0, v1);

		final double length1 = Geometry.length(v0);
		final double length2 = Geometry.length(v1);

		final double denominator = length1 * length2;

		final double product = denominator != 0.0 ? dotProduct / denominator : 0.0;

		final double angle = Math.acos(product);

		return angle;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute the dot product (a scalar) between two vectors.
	 * 
	 * @param v0
	 *            Vector 1 [x,y,z].
	 * @param v1
	 *            Vector 2 [x,y,z].
	 * @return Dot product of given vectors.
	 */
	public static double computeDotProduct(final double[] v0, final double[] v1) {
		return v0[0] * v1[0] + v0[1] * v1[1] + v0[2] * v1[2];
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute the cross product (a vector) of two vectors.
	 * 
	 * @param v0
	 *            Vector 1 [x,y,z].
	 * @param v1
	 *            Vector 2 [x,y,z].
	 * @return the cross product (a vector) of two vectors.
	 */
	public static double[] computeCrossProduct(final double[] v0, final double[] v1) {
		final double crossProduct[] = new double[3];

		crossProduct[0] = v0[1] * v1[2] - v0[2] * v1[1];
		crossProduct[1] = v0[2] * v1[0] - v0[0] * v1[2];
		crossProduct[2] = v0[0] * v1[1] - v0[1] * v1[0];

		return crossProduct;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Construct the vector specified by two points.
	 * 
	 * @param p0
	 *            Point 1 [x,y,z].
	 * @param p1
	 *            Point 2 [x,y,z].
	 * @return v Vector from p0 to p1 [x,y,z].
	 */
	public static double[] createVector(final double[] p0, final double[] p1) {
		final double v[] = { p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2] };
		return v;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if two points are on the same side of a given line. Algorithm from Sedgewick page 350.
	 * 
	 * @param x0
	 *            , y0, x1, y1 The line.
	 * @param px0
	 *            , py0 First point.
	 * @param px1
	 *            , py1 Second point.
	 * @return <0 if points on opposite sides. =0 if one of the points is exactly on the line >0 if
	 *         points on same side.
	 */
	private static int sameSide(final double x0, final double y0, final double x1, final double y1, final double px0, final double py0,
			final double px1, final double py1) {
		int sameSide = 0;

		final double dx = x1 - x0;
		final double dy = y1 - y0;
		final double dx1 = px0 - x0;
		final double dy1 = py0 - y0;
		final double dx2 = px1 - x1;
		final double dy2 = py1 - y1;

		// Cross product of the vector from the endpoint of the line to the point
		final double c1 = dx * dy1 - dy * dx1;
		final double c2 = dx * dy2 - dy * dx2;

		if ((c1 != 0) && (c2 != 0)) {
			sameSide = (c1 < 0) != (c2 < 0) ? -1 : 1;
		} else if ((dx == 0) && (dx1 == 0) && (dx2 == 0)) {
			sameSide = !isBetween(y0, y1, py0) && !isBetween(y0, y1, py1) ? 1 : 0;
		} else if ((dy == 0) && (dy1 == 0) && (dy2 == 0)) {
			sameSide = !isBetween(x0, x1, px0) && !isBetween(x0, x1, px1) ? 1 : 0;
		}

		return sameSide;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if two points are on the same side of a given line. Integer domain.
	 * 
	 * @param x0
	 *            , y0, x1, y1 The line.
	 * @param px0
	 *            , py0 First point.
	 * @param px1
	 *            , py1 Second point.
	 * @return <0 if points on opposite sides. =0 if one of the points is exactly on the line >0 if
	 *         points on same side.
	 */
	private static int sameSide(final int x0, final int y0, final int x1, final int y1, final int px0, final int py0, final int px1, final int py1) {
		return sameSide((double) x0, (double) y0, (double) x1, (double) y1, (double) px0, (double) py0, (double) px1, (double) py1);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if two line segments intersects. Integer domain.
	 * 
	 * @param x0
	 *            End points of first line to check
	 * @param y0
	 *            End points of first line to check
	 * @param x1
	 *            End points of first line to check.
	 * @param y1
	 *            End points of first line to check.
	 * 
	 * @param x2
	 *            End points of second line to check.
	 * @param y2
	 *            End points of second line to check.
	 * @param x3
	 *            End points of second line to check.
	 * @param y3
	 *            End points of second line to check.
	 * @return True if the two lines intersects.
	 */
	public static boolean isLineIntersectingLine(final int x0, final int y0, final int x1, final int y1, final int x2, final int y2, final int x3,
			final int y3) {
		final int s1 = Geometry.sameSide(x0, y0, x1, y1, x2, y2, x3, y3);
		final int s2 = Geometry.sameSide(x2, y2, x3, y3, x0, y0, x1, y1);

		return (s1 <= 0) && (s2 <= 0);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if a specified line intersects a specified rectangle. Integer domain.
	 * 
	 * @param lx0
	 *            1st end point of line
	 * @param ly0
	 *            1st end point of line
	 * @param lx1
	 *            2nd end point of line
	 * @param ly1
	 *            2nd end point of line
	 * @param x0
	 *            Upper left corner of rectangle (inclusive).
	 * @param y0
	 *            Upper left corner of rectangle (inclusive).
	 * @param x1
	 *            Lower right corner of rectangle (inclusive).
	 * @param y1
	 *            Lower right corner of rectangle (inclusive).
	 * @return True if the line intersects the rectangle, false otherwise.
	 */
	public static boolean isLineIntersectingRectangle(final int lx0, final int ly0, final int lx1, final int ly1, final int x0, final int y0,
			final int x1, final int y1) {
		// Is one of the line endpoints inside the rectangle
		if (Geometry.isPointInsideRectangle(x0, y0, x1, y1, lx0, ly0) || Geometry.isPointInsideRectangle(x0, y0, x1, y1, lx1, ly1)) {
			return true;
		}

		// If it intersects it goes through. Need to check three sides only.

		// Check against top rectangle line
		if (Geometry.isLineIntersectingLine(lx0, ly0, lx1, ly1, x0, y0, x1, y0)) {
			return true;
		}

		// Check against left rectangle line
		if (Geometry.isLineIntersectingLine(lx0, ly0, lx1, ly1, x0, y0, x0, y1)) {
			return true;
		}

		// Check against bottom rectangle line
		if (Geometry.isLineIntersectingLine(lx0, ly0, lx1, ly1, x0, y1, x1, y1)) {
			return true;
		}

		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if a specified polyline intersects a specified rectangle. Integer domain.
	 * 
	 * @param x
	 *            Polyline to check.
	 * @param y
	 *            Polyline to check.
	 * @param x0
	 *            Upper left corner of rectangle (inclusive).
	 * @param y0
	 *            Upper left corner of rectangle (inclusive).
	 * @param x1
	 *            Lower left corner of rectangle (inclusive).
	 * @param y1
	 *            Lower left corner of rectangle (inclusive).
	 * @return True if the polyline intersects the rectangle, false otherwise.
	 */
	public static boolean isPolylineIntersectingRectangle(final int[] x, final int[] y, final int x0, final int y0, final int x1, final int y1) {
		if (x.length == 0) {
			return false;
		}

		if (Geometry.isPointInsideRectangle(x[0], y[0], x0, y0, x1, y1)) {
			return true;
		} else if (x.length == 1) {
			return false;
		}

		for (int i = 1; i < x.length; i++) {
			if ((x[i - 1] != x[i]) || (y[i - 1] != y[i])) {
				if (Geometry.isLineIntersectingRectangle(x[i - 1], y[i - 1], x[i], y[i], x0, y0, x1, y1)) {
					return true;
				}
			}
		}

		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Check if a specified polygon intersects a specified rectangle. Integer domain.
	 * 
	 * @param x
	 *            X coordinates of polyline.
	 * @param y
	 *            Y coordinates of polyline.
	 * @param x0
	 *            X of upper left corner of rectangle.
	 * @param y0
	 *            Y of upper left corner of rectangle.
	 * @param x1
	 *            X of lower right corner of rectangle.
	 * @param y1
	 *            Y of lower right corner of rectangle.
	 * @return True if the polyline intersects the rectangle, false otherwise.
	 */
	public static boolean isPolygonIntersectingRectangle(final int[] x, final int[] y, final int x0, final int y0, final int x1, final int y1) {
		final int n = x.length;

		if (n == 0) {
			return false;
		}

		if (n == 1) {
			return Geometry.isPointInsideRectangle(x0, y0, x1, y1, x[0], y[0]);
		}

		//
		// If the polyline constituting the polygon intersects the rectangle
		// the polygon does too.
		//
		if (Geometry.isPolylineIntersectingRectangle(x, y, x0, y0, x1, y1)) {
			return true;
		}

		// Check last leg as well
		if (Geometry.isLineIntersectingRectangle(x[n - 2], y[n - 2], x[n - 1], y[n - 1], x0, y0, x1, y1)) {
			return true;
		}

		//
		// The rectangle and polygon are now completely including each other
		// or separate.
		//
		if (Geometry.isPointInsidePolygon(x, y, x0, y0) || Geometry.isPointInsideRectangle(x0, y0, x1, y1, x[0], y[0])) {
			return true;
		}

		// Separate
		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute the area of the specfied polygon.
	 * 
	 * @param x
	 *            X coordinates of polygon.
	 * @param y
	 *            Y coordinates of polygon.
	 * @return Area of specified polygon.
	 */
	public static double computePolygonArea(final double[] x, final double[] y) {
		final int n = x.length;

		double area = 0.0;
		for (int i = 0; i < n - 1; i++) {
			area += (x[i] * y[i + 1]) - (x[i + 1] * y[i]);
		}
		area += (x[n - 1] * y[0]) - (x[0] * y[n - 1]);

		area *= 0.5;

		return area;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute the area of the specfied polygon.
	 * 
	 * @param xy
	 *            Geometry of polygon [x,y,...]
	 * @return Area of specified polygon.
	 */
	public static double computePolygonArea(final double[] xy) {
		final int n = xy.length;

		double area = 0.0;
		for (int i = 0; i < n - 2; i += 2) {
			area += (xy[i] * xy[i + 3]) - (xy[i + 2] * xy[i + 1]);
		}
		area += (xy[xy.length - 2] * xy[1]) - (xy[0] * xy[xy.length - 1]);

		area *= 0.5;

		return area;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute centorid (center of gravity) of specified polygon.
	 * 
	 * @param x
	 *            X coordinates of polygon.
	 * @param y
	 *            Y coordinates of polygon.
	 * @return Centroid [x,y] of specified polygon.
	 */
	public static double[] computePolygonCentroid(final double[] x, final double[] y) {
		double cx = 0.0;
		double cy = 0.0;

		final int n = x.length;
		for (int i = 0; i < n - 1; i++) {
			final double a = x[i] * y[i + 1] - x[i + 1] * y[i];
			cx += (x[i] + x[i + 1]) * a;
			cy += (y[i] + y[i + 1]) * a;
		}
		final double a = x[n - 1] * y[0] - x[0] * y[n - 1];
		cx += (x[n - 1] + x[0]) * a;
		cy += (y[n - 1] + y[0]) * a;

		final double area = Geometry.computePolygonArea(x, y);

		cx /= 6 * area;
		cy /= 6 * area;

		return new double[] { cx, cy };
	}

	// --------------------------------------------------------------------------------
	/**
	 * Find the 3D extent of a polyline.
	 * 
	 * @param x
	 *            X coordinates of polyline.
	 * @param y
	 *            Y coordinates of polyline.
	 * @param z
	 *            Z coordinates of polyline. May be null if this is a 2D case.
	 * @param xExtent
	 *            Will upon return contain [xMin,xMax].
	 * @param yExtent
	 *            Will upon return contain [xMin,xMax].
	 * @param zExtent
	 *            Will upon return contain [xMin,xMax]. Unused (may be set to null) if z is null.
	 */
	public static void findPolygonExtent(final double[] x, final double[] y, final double[] z, final double[] xExtent, final double[] yExtent,
			final double[] zExtent) {
		double xMin = +Double.MAX_VALUE;
		double xMax = -Double.MAX_VALUE;

		double yMin = +Double.MAX_VALUE;
		double yMax = -Double.MAX_VALUE;

		double zMin = +Double.MAX_VALUE;
		double zMax = -Double.MAX_VALUE;

		for (int i = 0; i < x.length; i++) {
			if (x[i] < xMin) {
				xMin = x[i];
			}
			if (x[i] > xMax) {
				xMax = x[i];
			}

			if (y[i] < yMin) {
				yMin = y[i];
			}
			if (y[i] > yMax) {
				yMax = y[i];
			}

			if (z != null) {
				if (z[i] < zMin) {
					zMin = z[i];
				}
				if (z[i] > zMax) {
					zMax = z[i];
				}
			}
		}

		xExtent[0] = xMin;
		xExtent[1] = xMax;

		yExtent[0] = yMin;
		yExtent[1] = yMax;

		if (z != null) {
			zExtent[0] = zMin;
			zExtent[1] = zMax;
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Find the extent of a polygon.
	 * 
	 * @param x
	 *            X coordinates of polygon.
	 * @param y
	 *            Y coordinates of polygon.
	 * @param xExtent
	 *            Will upon return contain [xMin, xMax]
	 * @param yExtent
	 *            Will upon return contain [yMin, yMax]
	 */
	public static void findPolygonExtent(final int[] x, final int[] y, final int[] xExtent, // xMin,
			// xMax
			final int[] yExtent) // yMin, yMax
	{
		int xMin = +Integer.MAX_VALUE;
		int xMax = -Integer.MAX_VALUE;

		int yMin = +Integer.MAX_VALUE;
		int yMax = -Integer.MAX_VALUE;

		for (int i = 0; i < x.length; i++) {
			if (x[i] < xMin) {
				xMin = x[i];
			}
			if (x[i] > xMax) {
				xMax = x[i];
			}

			if (y[i] < yMin) {
				yMin = y[i];
			}
			if (y[i] > yMax) {
				yMax = y[i];
			}
		}

		xExtent[0] = xMin;
		xExtent[1] = xMax;

		yExtent[0] = yMin;
		yExtent[1] = yMax;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compute the intersection between two line segments, or two lines of infinite length.
	 * 
	 * @param x0
	 *            X coordinate first end point first line segment.
	 * @param y0
	 *            Y coordinate first end point first line segment.
	 * @param x1
	 *            X coordinate second end point first line segment.
	 * @param y1
	 *            Y coordinate second end point first line segment.
	 * @param x2
	 *            X coordinate first end point second line segment.
	 * @param y2
	 *            Y coordinate first end point second line segment.
	 * @param x3
	 *            X coordinate second end point second line segment.
	 * @param y3
	 *            Y coordinate second end point second line segment.
	 * @param intersection
	 *            [2] Preallocated by caller to double[2]
	 * @return -1 if lines are parallel (x,y unset), -2 if lines are parallel and overlapping (x, y
	 *         center) 0 if intesrection outside segments (x,y set) +1 if segments intersect (x,y
	 *         set)
	 */
	public static int findLineSegmentIntersection(final double x0, final double y0, final double x1, final double y1, final double x2,
			final double y2, final double x3, final double y3, final double[] intersection) {

		final double LIMIT = 1e-5;
		final double INFINITY = 1e10;

		double x, y;

		//
		// Convert the lines to the form y = ax + b
		//

		// Slope of the two lines
		final double a0 = Geometry.equals(x0, x1, LIMIT) ? INFINITY : (y0 - y1) / (x0 - x1);
		final double a1 = Geometry.equals(x2, x3, LIMIT) ? INFINITY : (y2 - y3) / (x2 - x3);

		final double b0 = y0 - a0 * x0;
		final double b1 = y2 - a1 * x2;

		// Check if lines are parallel
		if (Geometry.equals(a0, a1)) {
			if (!Geometry.equals(b0, b1)) {
				return -1; // Parallell non-overlapping
			}
			if (Geometry.equals(x0, x1)) {
				if ((Math.min(y0, y1) < Math.max(y2, y3)) || (Math.max(y0, y1) > Math.min(y2, y3))) {
					final double twoMiddle = y0 + y1 + y2 + y3 - Geometry.min(y0, y1, y2, y3) - Geometry.max(y0, y1, y2, y3);
					y = (twoMiddle) / 2.0;
					x = (y - b0) / a0;
				} else {
					return -1; // Parallell non-overlapping
				}
			} else {
				if ((Math.min(x0, x1) < Math.max(x2, x3)) || (Math.max(x0, x1) > Math.min(x2, x3))) {
					final double twoMiddle = x0 + x1 + x2 + x3 - Geometry.min(x0, x1, x2, x3) - Geometry.max(x0, x1, x2, x3);
					x = (twoMiddle) / 2.0;
					y = a0 * x + b0;
				} else {
					return -1;
				}
			}

			intersection[0] = x;
			intersection[1] = y;
			return -2;
		}

		// Find correct intersection point
		if (Geometry.equals(a0, INFINITY)) {
			x = x0;
			y = a1 * x + b1;
		} else if (Geometry.equals(a1, INFINITY)) {
			x = x2;
			y = a0 * x + b0;
		} else {
			x = -(b0 - b1) / (a0 - a1);
			y = a0 * x + b0;
		}

		intersection[0] = x;
		intersection[1] = y;

		// Then check if intersection is within line segments
		double distanceFrom1;
		if (Geometry.equals(x0, x1)) {
			if (y0 < y1) {
				distanceFrom1 = y < y0 ? Geometry.length(x, y, x0, y0) : y > y1 ? Geometry.length(x, y, x1, y1) : 0.0;
			} else {
				distanceFrom1 = y < y1 ? Geometry.length(x, y, x1, y1) : y > y0 ? Geometry.length(x, y, x0, y0) : 0.0;
			}
		} else {
			if (x0 < x1) {
				distanceFrom1 = x < x0 ? Geometry.length(x, y, x0, y0) : x > x1 ? Geometry.length(x, y, x1, y1) : 0.0;
			} else {
				distanceFrom1 = x < x1 ? Geometry.length(x, y, x1, y1) : x > x0 ? Geometry.length(x, y, x0, y0) : 0.0;
			}
		}

		double distanceFrom2;
		if (Geometry.equals(x2, x3)) {
			if (y2 < y3) {
				distanceFrom2 = y < y2 ? Geometry.length(x, y, x2, y2) : y > y3 ? Geometry.length(x, y, x3, y3) : 0.0;
			} else {
				distanceFrom2 = y < y3 ? Geometry.length(x, y, x3, y3) : y > y2 ? Geometry.length(x, y, x2, y2) : 0.0;
			}
		} else {
			if (x2 < x3) {
				distanceFrom2 = x < x2 ? Geometry.length(x, y, x2, y2) : x > x3 ? Geometry.length(x, y, x3, y3) : 0.0;
			} else {
				distanceFrom2 = x < x3 ? Geometry.length(x, y, x3, y3) : x > x2 ? Geometry.length(x, y, x2, y2) : 0.0;
			}
		}

		return Geometry.equals(distanceFrom1, 0.0) && Geometry.equals(distanceFrom2, 0.0) ? 1 : 0;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Find the intersections between a polygon and a straight line.
	 * 
	 * NOTE: This method is only guaranteed to work if the polygon is first preprocessed so that
	 * "unneccesary" vertices are removed (i.e vertices on the straight line between its
	 * neighbours).
	 * 
	 * @param x
	 *            X coordinates of polygon.
	 * @param y
	 *            Y coordinates of polygon.
	 * @param x0
	 *            X first end point of line.
	 * @param y0
	 *            Y first end point of line.
	 * @param x1
	 *            X second end point of line.
	 * @param y1
	 *            Y second end point of line.
	 * @return Intersections [x,y,x,y...].
	 */
	public static double[] findLinePolygonIntersections(final double[] x, final double[] y, final double x0, final double y0, final double x1,
			final double y1) {
		double x2, y2, x3, y3;
		final int nPoints = x.length;

		int nIntersections = 0;
		double[] intersections = new double[24]; // Result vector x,y,x,y,...
		final double[] intersection = new double[2]; // Any given intersection x,y

		for (int i = 0; i < nPoints; i++) {
			final int next = i == nPoints - 1 ? 0 : i + 1;

			// The line segment of the polyline to check
			x2 = x[i];
			y2 = y[i];
			x3 = x[next];
			y3 = y[next];

			boolean isIntersecting = false;

			// Ignore segments of zero length
			if (Geometry.equals(x2, x3) && Geometry.equals(y2, y3)) {
				continue;
			}

			final int type = Geometry.findLineSegmentIntersection(x0, y0, x1, y1, x2, y2, x3, y3, intersection);

			if (type == -2) { // Overlapping
				final int p1 = i == 0 ? nPoints - 1 : i - 1;
				final int p2 = next == nPoints - 1 ? 0 : next + 1;

				final int side = Geometry.sameSide(x0, y0, x1, y1, x[p1], y[p1], x[p2], y[p2]);

				if (side < 0) {
					isIntersecting = true;
				}
			} else if (type == 1) {
				isIntersecting = true;
			}

			// Add the intersection point
			if (isIntersecting) {

				// Reallocate if necessary
				if (nIntersections << 1 == intersections.length) {
					final double[] newArray = new double[nIntersections << 2];
					System.arraycopy(intersections, 0, newArray, 0, intersections.length);
					intersections = newArray;
				}

				// Then add
				intersections[nIntersections << 1 + 0] = intersection[0];
				intersections[nIntersections << 1 + 1] = intersection[1];

				nIntersections++;
			}
		}

		if (nIntersections == 0) {
			return null;
		}

		// Reallocate result so array match number of intersections
		final double[] finalArray = new double[nIntersections << 2];
		System.arraycopy(intersections, 0, finalArray, 0, finalArray.length);

		return finalArray;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Return the geometry of an ellipse based on its four top points. Integer domain. The method
	 * use the generic createEllipse() method for the main task, and then transforms this according
	 * to any rotation or skew defined by the given top points.
	 * 
	 * @param x
	 *            X array of four top points of ellipse.
	 * @param y
	 *            Y array of four top points of ellipse.
	 * @return Geometry of ellipse [x,y,x,y...].
	 */
	public static int[] createEllipse(final int[] x, final int[] y) {
		// Center of ellipse
		final int x0 = (x[0] + x[2]) / 2;
		final int y0 = (y[0] + y[2]) / 2;

		// Angle between axis define skew
		final double[] p0 = { x0, y0, 0.0 };
		final double[] p1 = { x[0], y[0], 0.0 };
		final double[] p2 = { x[1], y[1], 0.0 };

		final double axisAngle = Geometry.computeAngle(p0, p1, p2);

		// dx / dy
		final double dx = Geometry.length(x0, y0, x[1], y[1]);
		final double dy = Geometry.length(x0, y0, x[0], y[0]) * Math.sin(axisAngle);

		// Create geometry for unrotated / unsheared ellipse
		final int[] ellipse = createEllipse(x0, y0, (int) Math.round(dx), (int) Math.round(dy));
		final int nPoints = ellipse.length / 2;

		// Shear if neccessary. If angle is close to 90 there is no shear.
		// If angle is close to 0 or 180 shear is infinite, and we set
		// it to zero as well.
		if (!Geometry.equals(axisAngle, Math.PI / 2.0, 0.1) && !Geometry.equals(axisAngle, Math.PI, 0.1) && !Geometry.equals(axisAngle, 0.0, 0.1)) {
			final double xShear = 1.0 / Math.tan(axisAngle);
			for (int i = 0; i < nPoints; i++) {
				ellipse[i * 2 + 0] += Math.round((ellipse[i * 2 + 1] - y0) * xShear);
			}
		}

		// Rotate
		final int ddx = x[1] - x0;
		final int ddy = y0 - y[1];

		double angle;
		if ((ddx == 0) && (ddy == 0)) {
			angle = 0.0;
		} else if (ddx == 0) {
			angle = Math.PI / 2.0;
		} else {
			angle = Math.atan((double) ddy / (double) ddx);
		}

		final double cosAngle = Math.cos(angle);
		final double sinAngle = Math.sin(angle);

		for (int i = 0; i < nPoints; i++) {
			final int xr = (int) Math.round(x0 + (ellipse[i * 2 + 0] - x0) * cosAngle - (ellipse[i * 2 + 1] - y0) * sinAngle);
			final int yr = (int) Math.round(y0 - (ellipse[i * 2 + 1] - y0) * cosAngle - (ellipse[i * 2 + 0] - x0) * sinAngle);

			ellipse[i * 2 + 0] = xr;
			ellipse[i * 2 + 1] = yr;
		}

		return ellipse;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create the geometry for an unrotated, unskewed ellipse. Integer domain.
	 * 
	 * @param x0
	 *            X center of ellipse.
	 * @param y0
	 *            Y center of ellipse.
	 * @param dx
	 *            X ellipse radius.
	 * @param dy
	 *            Y ellipse radius.
	 * @return Ellipse geometry [x,y,x,y,...].
	 */
	public static int[] createEllipse(final int x0, final int y0, int dx, int dy) {
		// Make sure deltas are positive
		dx = Math.abs(dx);
		dy = Math.abs(dy);

		// This is an approximate number of points we need to make a smooth
		// surface on a quater of the ellipse
		int nPoints = dx > dy ? dx : dy;
		nPoints /= 2;
		if (nPoints < 1) {
			nPoints = 1;
		}

		// Allocate arrays for holding the complete set of vertices around
		// the ellipse. Note that this is a complete ellipse: First and last
		// point coincide.
		final int[] ellipse = new int[nPoints * 8 + 2];

		// Compute some intermediate results to save time in the inner loop
		final int dxdy = dx * dy;
		final int dx2 = dx * dx;
		final int dy2 = dy * dy;

		// Handcode the entries in the four "corner" points of the ellipse,
		// i.e. at point 0, 90, 180, 270 and 360 degrees
		ellipse[nPoints * 0 + 0] = x0 + dx;
		ellipse[nPoints * 0 + 1] = y0;

		ellipse[nPoints * 8 + 0] = x0 + dx;
		ellipse[nPoints * 8 + 1] = y0;

		ellipse[nPoints * 2 + 0] = x0;
		ellipse[nPoints * 2 + 1] = y0 - dy;

		ellipse[nPoints * 4 + 0] = x0 - dx;
		ellipse[nPoints * 4 + 1] = y0;

		ellipse[nPoints * 6 + 0] = x0;
		ellipse[nPoints * 6 + 1] = y0 + dy;

		// Find the angle step
		final double angleStep = nPoints > 0 ? Math.PI / 2.0 / nPoints : 0.0;

		// Loop over angles from 0 to 90. The rest of the ellipse can be derrived
		// from this first quadrant. For each angle set the four corresponding
		// ellipse points.
		double a = 0.0;
		for (int i = 1; i < nPoints; i++) {
			a += angleStep;

			final double t = Math.tan(a);

			final double x = dxdy / Math.sqrt(t * t * dx2 + dy2);
			final double y = x * t;

			final int xi = (int) (x + 0.5);
			final int yi = (int) (y + 0.5);

			ellipse[(nPoints * 0 + i) * 2 + 0] = x0 + xi;
			ellipse[(nPoints * 2 - i) * 2 + 0] = x0 - xi;
			ellipse[(nPoints * 2 + i) * 2 + 0] = x0 - xi;
			ellipse[(nPoints * 4 - i) * 2 + 0] = x0 + xi;

			ellipse[(nPoints * 0 + i) * 2 + 1] = y0 - yi;
			ellipse[(nPoints * 2 - i) * 2 + 1] = y0 - yi;
			ellipse[(nPoints * 2 + i) * 2 + 1] = y0 + yi;
			ellipse[(nPoints * 4 - i) * 2 + 1] = y0 + yi;
		}

		return ellipse;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create the geometry for an unrotated, unskewed ellipse. Floating point domain.
	 * 
	 * @param x0
	 *            X center of ellipse.
	 * @param y0
	 *            Y center of ellipse.
	 * @param dx
	 *            X ellipse radius.
	 * @param dy
	 *            Y ellipse radius.
	 * @return Ellipse geometry [x,y,x,y,...].
	 */
	public static double[] createEllipse(final double x0, final double y0, double dx, double dy) {
		// Make sure deltas are positive
		dx = Math.abs(dx);
		dy = Math.abs(dy);

		// As we don't know the resolution of the appliance of the ellipse
		// we create one vertex per 2nd degree. The nPoints variable holds
		// number of points in a quater of the ellipse.
		final int nPoints = 45;

		// Allocate arrays for holding the complete set of vertices around
		// the ellipse. Note that this is a complete ellipse: First and last
		// point coincide.
		final double[] ellipse = new double[nPoints * 8 + 2];

		// Compute some intermediate results to save time in the inner loop
		final double dxdy = dx * dy;
		final double dx2 = dx * dx;
		final double dy2 = dy * dy;

		// Handcode the entries in the four "corner" points of the ellipse,
		// i.e. at point 0, 90, 180, 270 and 360 degrees
		ellipse[nPoints * 0 + 0] = x0 + dx;
		ellipse[nPoints * 0 + 1] = y0;

		ellipse[nPoints * 8 + 0] = x0 + dx;
		ellipse[nPoints * 8 + 1] = y0;

		ellipse[nPoints * 2 + 0] = x0;
		ellipse[nPoints * 2 + 1] = y0 - dy;

		ellipse[nPoints * 4 + 0] = x0 - dx;
		ellipse[nPoints * 4 + 1] = y0;

		ellipse[nPoints * 6 + 0] = x0;
		ellipse[nPoints * 6 + 1] = y0 + dy;

		// Find the angle step
		final double angleStep = nPoints > 0 ? Math.PI / 2.0 / nPoints : 0.0;

		// Loop over angles from 0 to 90. The rest of the ellipse can be derrived
		// from this first quadrant. For each angle set the four corresponding
		// ellipse points.
		double a = 0.0;
		for (int i = 1; i < nPoints; i++) {
			a += angleStep;

			final double t = Math.tan(a);

			final double x = dxdy / Math.sqrt(t * t * dx2 + dy2);
			final double y = x * t + 0.5;

			ellipse[(nPoints * 0 + i) * 2 + 0] = x0 + x;
			ellipse[(nPoints * 2 - i) * 2 + 0] = x0 - x;
			ellipse[(nPoints * 2 + i) * 2 + 0] = x0 - x;
			ellipse[(nPoints * 4 - i) * 2 + 0] = x0 + x;

			ellipse[(nPoints * 0 + i) * 2 + 1] = y0 - y;
			ellipse[(nPoints * 2 - i) * 2 + 1] = y0 - y;
			ellipse[(nPoints * 2 + i) * 2 + 1] = y0 + y;
			ellipse[(nPoints * 4 - i) * 2 + 1] = y0 + y;
		}

		return ellipse;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create geometry for a circle. Integer domain.
	 * 
	 * @param x0
	 *            X center of circle.
	 * @param y0
	 *            Y center of circle.
	 * @param radius
	 *            Radius of circle.
	 * @return Geometry of circle [x,y,...]
	 */
	public static int[] createCircle(final int x0, final int y0, final int radius) {
		return createEllipse(x0, y0, radius, radius);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create geometry for a circle. Floating point domain.
	 * 
	 * @param x0
	 *            X center of circle.
	 * @param y0
	 *            Y center of circle.
	 * @param radius
	 *            Radius of circle.
	 * @return Geometry of circle [x,y,...]
	 */
	public static double[] createCircle(final double x0, final double y0, final double radius) {
		return createEllipse(x0, y0, radius, radius);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create the geometry of a sector of an ellipse.
	 * 
	 * @param x0
	 *            X coordinate of center of ellipse.
	 * @param y0
	 *            Y coordinate of center of ellipse.
	 * @param dx
	 *            X radius of ellipse.
	 * @param dy
	 *            Y radius of ellipse.
	 * @param angle0
	 *            First angle of sector (in radians).
	 * @param angle1
	 *            Second angle of sector (in radians).
	 * @return Geometry of secor [x,y,...]
	 */
	public static int[] createSector(final int x0, final int y0, final int dx, final int dy, final double angle0, final double angle1) {
		// Determine a sensible number of points for arc
		final double angleSpan = Math.abs(angle1 - angle0);
		final double arcDistance = Math.max(dx, dy) * angleSpan;
		final int nPoints = (int) Math.round(arcDistance / 15);
		final double angleStep = angleSpan / (nPoints - 1);

		final int[] xy = new int[nPoints * 2 + 4];

		int index = 0;
		for (int i = 0; i < nPoints; i++) {
			final double angle = angle0 + angleStep * i;
			final double x = dx * Math.cos(angle);
			final double y = dy * Math.sin(angle);

			xy[index + 0] = x0 + (int) Math.round(x);
			xy[index + 1] = y0 - (int) Math.round(y);

			index += 2;
		}

		// Start and end geometry at center of ellipse to make it a closed polygon
		xy[nPoints * 2 + 0] = x0;
		xy[nPoints * 2 + 1] = y0;
		xy[nPoints * 2 + 2] = xy[0];
		xy[nPoints * 2 + 3] = xy[1];

		return xy;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create the geometry of a sector of a circle.
	 * 
	 * @param x0
	 *            X coordinate of center of ellipse.
	 * @param y0
	 *            Y coordinate of center of ellipse.
	 * @param radius
	 *            radius of ellipse.
	 * @param angle0
	 *            First angle of sector (in radians).
	 * @param angle1
	 *            Second angle of sector (in radians).
	 * @return Geometry of sector [x,y,...]
	 */
	public static int[] createSector(final int x0, final int y0, final int radius, final double angle0, final double angle1) {
		return createSector(x0, y0, radius, radius, angle0, angle1);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create the geometry of an arrow. The arrow is positioned at the end (last point) of the
	 * specified polyline, as follows:
	 * 
	 * 0,4--, \ --, \ --, \ --, \ --, -------------------------3-----------1 / --' / --' / --' / --'
	 * 2--'
	 * 
	 * @param x
	 *            X coordinates of polyline of where arrow is positioned in the end. Must contain at
	 *            least two points.
	 * @param y
	 *            Y coordinates of polyline of where arrow is positioned in the end.
	 * @param length
	 *            Length along the main axis from point 1 to the projection of point 0.
	 * @param angle
	 *            Angle between the main axis and the line 1,0 (and 1,2) in radians.
	 * @param inset
	 *            Specification of point 3 [0.0-1.0], 1.0 will put point 3 at distance length from
	 *            1, 0.0 will put it at point 1.
	 * @return Array of the five coordinates [x,y,...].
	 */
	public static int[] createArrow(final int[] x, final int[] y, final double length, final double angle, final double inset) {
		final int[] arrow = new int[10];

		final int x0 = x[x.length - 1];
		final int y0 = y[y.length - 1];

		arrow[2] = x0;
		arrow[3] = y0;

		// Find position of interior of the arrow along the polyline
		final int[] pos1 = new int[2];
		Geometry.findPolygonPosition(x, y, length, pos1);

		// Angles
		final double dx = x0 - pos1[0];
		final double dy = y0 - pos1[1];

		// Polyline angle
		double v = dx == 0.0 ? Math.PI / 2.0 : Math.atan(Math.abs(dy / dx));

		v = (dx > 0.0) && (dy <= 0.0) ? Math.PI + v : (dx > 0.0) && (dy >= 0.0) ? Math.PI - v : (dx <= 0.0) && (dy < 0.0) ? -v : (dx <= 0.0)
				&& (dy > 0.0) ? +v : 0.0;

		final double v0 = v + angle;
		final double v1 = v - angle;

		final double edgeLength = length / Math.cos(angle);

		arrow[0] = x0 + (int) Math.round(edgeLength * Math.cos(v0));
		arrow[1] = y0 - (int) Math.round(edgeLength * Math.sin(v0));

		arrow[4] = x0 + (int) Math.round(edgeLength * Math.cos(v1));
		arrow[5] = y0 - (int) Math.round(edgeLength * Math.sin(v1));

		final double c1 = inset * length;

		arrow[6] = x0 + (int) Math.round(c1 * Math.cos(v));
		arrow[7] = y0 - (int) Math.round(c1 * Math.sin(v));

		// Close polygon
		arrow[8] = arrow[0];
		arrow[9] = arrow[1];

		return arrow;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create geometry for an arrow along the specified line and with tip at x1,y1. See general
	 * method above.
	 * 
	 * @param x0
	 *            X first end point of line.
	 * @param y0
	 *            Y first end point of line.
	 * @param x1
	 *            X second end point of line.
	 * @param y1
	 *            Y second end point of line.
	 * @param length
	 *            Length along the main axis from point 1 to the projection of point 0.
	 * @param angle
	 *            Angle between the main axis and the line 1,0 (and 1.2)
	 * @param inset
	 *            Specification of point 3 [0.0-1.0], 1.0 will put point 3 at distance length from
	 *            1, 0.0 will put it at point 1.
	 * @return Array of the four coordinates [x,y,...].
	 */
	public static int[] createArrow(final int x0, final int y0, final int x1, final int y1, final double length, final double angle,
			final double inset) {
		final int[] x = { x0, x1 };
		final int[] y = { y0, y1 };

		return createArrow(x, y, length, angle, inset);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create geometry for a rectangle. Returns a closed polygon; first and last points matches.
	 * Integer domain.
	 * 
	 * @param x0
	 *            X corner of rectangle.
	 * @param y0
	 *            Y corner of rectangle.
	 * @param width
	 *            Width (may be negative to indicate leftwards direction)
	 * @param height
	 *            Height (may be negative to indicate upwards direction)
	 * @return a closed polygon
	 */
	public static int[] createRectangle(final int x0, final int y0, final int width, final int height) {
		return new int[] { x0, y0, x0 + (width - 1), y0, x0 + (width - 1), y0 + (height - 1), x0, y0 + (height - 1), x0, y0 };
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create geometry for a rectangle. Returns a closed polygon; first and last points matches.
	 * Floating point domain.
	 * 
	 * @param x0
	 *            X corner of rectangle.
	 * @param y0
	 *            Y corner of rectangle.
	 * @param width
	 *            Width (may be negative to indicate leftwards direction)
	 * @param height
	 *            Height (may be negative to indicate upwards direction)
	 * @return a closed polygon
	 */
	public static double[] createRectangle(final double x0, final double y0, final double width, final double height) {
		return new double[] { x0, y0, x0 + width, y0, x0 + width, y0 + height, x0, y0 + height, x0, y0 };
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create geometry of a star. Integer domain.
	 * 
	 * @param x0
	 *            X center of star.
	 * @param y0
	 *            Y center of star.
	 * @param innerRadius
	 *            Inner radius of arms.
	 * @param outerRadius
	 *            Outer radius of arms.
	 * @param nArms
	 *            Number of arms.
	 * @return Geometry of star [x,y,x,y,...].
	 */
	public static int[] createStar(final int x0, final int y0, final int innerRadius, final int outerRadius, final int nArms) {
		final int nPoints = nArms * 2 + 1;

		final int[] xy = new int[nPoints * 2];

		final double angleStep = 2.0 * Math.PI / nArms / 2.0;

		for (int i = 0; i < nArms * 2; i++) {
			final double angle = i * angleStep;
			final double radius = (i % 2) == 0 ? innerRadius : outerRadius;

			final double x = x0 + radius * Math.cos(angle);
			final double y = y0 + radius * Math.sin(angle);

			xy[i * 2 + 0] = (int) Math.round(x);
			xy[i * 2 + 1] = (int) Math.round(y);
		}

		// Close polygon
		xy[nPoints * 2 - 2] = xy[0];
		xy[nPoints * 2 - 1] = xy[1];

		return xy;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create geometry of a star. Floating point domain.
	 * 
	 * @param x0
	 *            X center of star.
	 * @param y0
	 *            Y center of star.
	 * @param innerRadius
	 *            Inner radius of arms.
	 * @param outerRadius
	 *            Outer radius of arms.
	 * @param nArms
	 *            Number of arms.
	 * @return Geometry of star [x,y,x,y,...].
	 */
	public static double[] createStar(final double x0, final double y0, final double innerRadius, final double outerRadius, final int nArms) {
		final int nPoints = nArms * 2 + 1;

		final double[] xy = new double[nPoints * 2];

		final double angleStep = 2.0 * Math.PI / nArms / 2.0;

		for (int i = 0; i < nArms * 2; i++) {
			final double angle = i * angleStep;
			final double radius = (i % 2) == 0 ? innerRadius : outerRadius;

			xy[i * 2 + 0] = x0 + radius * Math.cos(angle);
			xy[i * 2 + 1] = y0 + radius * Math.sin(angle);
		}

		// Close polygon
		xy[nPoints * 2 - 2] = xy[0];
		xy[nPoints * 2 - 1] = xy[1];

		return xy;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Return the x,y position at distance "length" into the given polyline.
	 * 
	 * @param x
	 *            X coordinates of polyline
	 * @param y
	 *            Y coordinates of polyline
	 * @param length
	 *            Requested position
	 * @param position
	 *            Preallocated to int[2]
	 * @return True if point is within polyline, false otherwise
	 */
	public static boolean findPolygonPosition(final int[] x, final int[] y, final double length, final int[] position) {
		if (length < 0) {
			return false;
		}

		double accumulatedLength = 0.0;
		for (int i = 1; i < x.length; i++) {
			final double legLength = Geometry.length(x[i - 1], y[i - 1], x[i], y[i]);
			if (legLength + accumulatedLength >= length) {
				final double part = length - accumulatedLength;
				final double fraction = part / legLength;
				position[0] = (int) Math.round(x[i - 1] + fraction * (x[i] - x[i - 1]));
				position[1] = (int) Math.round(y[i - 1] + fraction * (y[i] - y[i - 1]));
				return true;
			}

			accumulatedLength += legLength;
		}

		// Length is longer than polyline
		return false;
	}
}