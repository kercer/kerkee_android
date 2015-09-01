package com.kercer.kerkee.net.uri.idn;

import java.util.Iterator;

/**
 * This Iterable class can be used to iterate more conveniently over all code points of a string sequence, e.g.
 * 
 * <code>
 *   CodepointIteratable sequence = new CodepointIterable(label)
 *   for (int codepoint : sequence) {
 *      // ...
 *   }
 * </code>
 *
 * @author zihong
 */
public class KCCodepointIteratable implements Iterable<Integer>
{
    private final String mSequence;

    public KCCodepointIteratable(String aSequence)
    {
        this.mSequence = aSequence;
    }

    public Iterator<Integer> iterator()
    {
        return new KCCodepointIterator(this.mSequence);
    }
}
