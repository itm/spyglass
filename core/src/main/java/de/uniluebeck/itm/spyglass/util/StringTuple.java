package de.uniluebeck.itm.spyglass.util;

import org.simpleframework.xml.Element;

/**
 * Tuple of Strings
 *
 * @author jens kluttig
 */
public class StringTuple{
    @Element
    public String first;
    @Element
    public String second;

    public StringTuple() {
        super();
    }

    public StringTuple(String s, String s1) {
        first = s;
        second = s1;
    }
}
