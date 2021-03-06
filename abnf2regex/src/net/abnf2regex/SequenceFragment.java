package net.abnf2regex;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Set;

/**
 * A {@link GroupFragment} that contains a sequence of fragments - the most
 * basic building block of ABNF.
 */
public class SequenceFragment extends GroupFragment
{
    @Override
    public boolean append(RuleFragment frag)
    {
        if (frag instanceof SequenceFragment)
        {
            SequenceFragment seq = (SequenceFragment) frag;
            if (!this.appendAll(seq))
            {
                this.fragments.addLast(frag);
            }
        }
        else if ((frag instanceof ChoiceFragment) || !mergeWithLast(frag))
        {
            this.fragments.addLast(frag);
        }
        return true;
    }

    /**
     * Merge the given fragment with the last fragment. The fragment is appended
     * to the last instance if they are both of the same type.
     *
     * @param frag the fragment to append.
     * @return true iff the merge was successful
     */
    private boolean mergeWithLast(RuleFragment frag)
    {
        if (this.fragments.size() > 0)
        {
            RuleFragment last = this.fragments.getLast();
            // can't append to a choice because choices are different
            if (!(last instanceof ChoiceFragment) && last.getOccurences().equals(frag.getOccurences()))
            {
                RuleFragment copy = (RuleFragment) frag.clone();
                copy.setOccurences(OccurrenceRange.ONCE);
                return last.append(copy);
            }
        }
        return false;
    }

    @Override
    protected StringBuilder buildAbnf(StringBuilder bld, Set<String> usedNames)
    {
        boolean started = false;
        for (RuleFragment frag : this.fragments)
        {
            if (started)
            {
                bld.append(' ');
            }
            started = true;
            bld.append(frag.toAbnf(usedNames));
        }
        return bld;
    }

    @Override
    protected boolean needsAbnfParens()
    {
        // if there is more than one, or if the one has a different number of
        // Occurrences to this
        return (this.length() > 1)
                || (this.length() == 1 && !this.getOccurences().isOnce() && !this.fragments.peekFirst().getOccurences()
                        .isOnce());
    }

    @Override
    protected boolean needsRegexParens()
    {
        return (this.length() != 1) && super.needsRegexParens();
    }

    @Override
    protected void buildRegex(PrintWriter pw, Set<String> usedNames) throws RuleResolutionException
    {
        for (RuleFragment frag : this.fragments)
        {
            frag.writeRegex(pw, usedNames);
        }
    }

    /**
     * Replaces all of the fragments from this with all the fragments in 'last'.
     * Resets last to be empty.
     *
     * @param extractFrom the sequence to extract all fragments from
     */
    public void extractAll(SequenceFragment extractFrom)
    {
        this.fragments = extractFrom.fragments;
        extractFrom.fragments = new ArrayDeque<RuleFragment>();
    }
}
