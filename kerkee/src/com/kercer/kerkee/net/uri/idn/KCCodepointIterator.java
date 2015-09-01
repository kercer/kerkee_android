package com.kercer.kerkee.net.uri.idn;

import java.util.Iterator;

/**
 * This CodepointIterator can be used to iterate over all code points in a string sequence. Code points
 * can be between 1 and 3 bytes and this iterator takes care of advancing to the next code point.
 * 
 * @author zihong
 */
public class KCCodepointIterator implements Iterator<Integer>
{
    private final String mSequence;
    private int mIndex = 0;

    public KCCodepointIterator(String sequence)
    {
        this.mSequence = sequence;
    }

    public boolean hasNext()
    {
        return mIndex < mSequence.length();
    }

    public Integer next()
    {
        int codepoint = mSequence.codePointAt(mIndex);
        mIndex += Character.charCount(codepoint);
        return codepoint;
    }

    public void reset()
    {
        mIndex = 0;
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    public int size()
    {
        return mSequence.codePointCount(0, mSequence.length() - 1);
    }
}
