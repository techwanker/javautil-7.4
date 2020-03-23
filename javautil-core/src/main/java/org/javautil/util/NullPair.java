package org.javautil.util;

import java.util.Comparator;

/**
 * 
 * @author jjs
 * 
 */
public class NullPair implements Comparator {
	boolean nullCollatesLow = false;

	public NullPair() {
		this(true);
	}

	public NullPair(final boolean nullCollatesLow) {
		this.nullCollatesLow = nullCollatesLow;
	}

	// TODO use in ArrayComparator
	@Override
	@SuppressWarnings("unchecked")
	public int compare(final Object c1, final Object c2) {
		int retval = 0;
		Comparable comp1 = null;
		if (c1 != null) {
			comp1 = (Comparable) c1;
		}
		if (c1 == null) {
			if (c2 != null) {
				retval = nullCollatesLow ? -1 : 1;
			}
		} else {
			if (c2 == null) {
				retval = nullCollatesLow ? 1 : -1;
			} else {
				try {
					retval = comp1.compareTo(c2);
				} catch (final ClassCastException cce) {
					throw new IllegalArgumentException("'" + cce.getMessage() + "' " + c1 + " compareTo " + c2);
				}
			}
		}

		return retval;
	}

	public boolean isNullSortsLow() {
		return nullCollatesLow;
	}

	public void setNullSortsLow(final boolean val) {
		nullCollatesLow = val;
	}
}
