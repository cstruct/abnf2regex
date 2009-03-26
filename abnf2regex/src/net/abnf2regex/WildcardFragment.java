/**
 * Copyright (c) Andrew Corporation,
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Andrew Corporation. You shall not disclose such confidential
 * information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Andrew Corporation.
 */
package net.abnf2regex;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * The ABNF construct that permits any content.
 */
public class WildcardFragment extends RuleFragment
{

    private final String txt;

    /**
     * Create a simple wildcard fragment
     */
    public WildcardFragment()
    {
        this(" wildcard "); //$NON-NLS-1$
    }

    /**
     * Create a wildcard fragment that has some sort of note in it.
     *
     * @param _txt the advisory text associated with the rule
     */
    public WildcardFragment(String _txt)
    {
        this.txt = _txt;
        this.setOccurences(null); // see {@link #setOccurs}
    }

    @Override
    public void setOccurences(OccurenceRange r)
    {
        // A wildcard has no need of repetitions. This absorbs any attempt to set these. For instance, if an enclosing
        // sequence with non-unitary repetitions is simplified, this will negate the repetitions.
        super.setOccurences(new OccurenceRange(1, 1));
    }

    /*
     * (non-Javadoc)
     *
     * @see net.abnf2regex.RuleFragment#needsAbnfParens()
     */
    @Override
    protected boolean needsAbnfParens()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.abnf2regex.RuleFragment#needsRegexParens()
     */
    @Override
    protected boolean needsRegexParens()
    {
        return false;
    }

    @Override
    public boolean append(RuleFragment frag)
    {
        return false;
    }

    @Override
    protected StringBuilder buildAbnf(StringBuilder bld)
    {
        return bld.append('<').append(this.txt).append('>');
    }

    @Override
    protected void buildRegex(PrintWriter pw, Set<String> usedNames)
    {
        pw.print(".*"); //$NON-NLS-1$
    }

    /**
     * Read this fragment from ABNF
     *
     * @param abnf the reader
     * @throws IOException on IO error, including maybe {@link java.io.EOFException}
     */
    public static WildcardFragment parse(AbnfReader abnf) throws IOException
    {
        StringBuilder bld = new StringBuilder();
        abnf.read(); // opening '<'
        while (abnf.peek() != '>' && abnf.peek() != '\n' && abnf.peek() != '\r')
        {
            bld.append((char) abnf.read());
        }
        abnf.read(); // closing '>'
        return new WildcardFragment(bld.toString());
    }

    @Override
    public Object clone()
    {
        return new WildcardFragment(this.txt);
    }
}
