/*
 * @(#)RuleBasedCollator.java	1.26 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)RuleBasedCollator.java	1.26 01/11/29
 *
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996-1998 - All Rights Reserved
 *
 * Portions copyright (c) 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package java.text;

import java.util.Vector;

/**
 * The <code>RuleBasedCollator</code> class is a concrete subclass of
 * <code>Collator</code> that provides a simple, data-driven, table
 * collator.  With this class you can create a customized table-based
 * <code>Collator</code>.  <code>RuleBasedCollator</code> maps
 * characters to sort keys.
 *
 * <p>
 * <code>RuleBasedCollator</code> has the following restrictions
 * for efficiency (other subclasses may be used for more complex languages) :
 * <ol>
 * <li>If a French secondary ordering is specified it applies to the
 *     whole collator object.
 * <li>All non-mentioned Unicode characters are at the end of the
 *     collation order.
 * </ol>
 *
 * <p>
 * The collation table is composed of a list of collation rules, where each
 * rule is of three forms:
 * <pre>
 *    < modifier >
 *    < relation > < text-argument >
 *    < reset > < text-argument >
 * </pre>
 * The following demonstrates how to create your own collation rules:
 * <UL Type=disc>
 *    <LI><strong>Text-Argument</strong>: A text-argument is any sequence of
 *        characters, excluding special characters (that is, common
 *        whitespace characters [0009-000D, 0020] and rule syntax characters
 *        [0021-002F, 003A-0040, 005B-0060, 007B-007E]). If those
 *        characters are desired, you can put them in single quotes
 *        (e.g. ampersand => '&'). Note that unquoted white space characters
 *        are ignored; e.g. <code>b c</code> is treated as <code>bc</code>.
 *    <LI><strong>Modifier</strong>: There is a single modifier
 *        which is used to specify that all accents (secondary differences) are
 *        backwards.
 *        <p>'@' : Indicates that accents are sorted backwards, as in French.
 *    <LI><strong>Relation</strong>: The relations are the following:
 *        <UL Type=square>
 *            <LI>'<' : Greater, as a letter difference (primary)
 *            <LI>';' : Greater, as an accent difference (secondary)
 *            <LI>',' : Greater, as a case difference (tertiary)
 *            <LI>'=' : Equal
 *        </UL>
 *    <LI><strong>Reset</strong>: There is a single reset
 *        which is used primarily for contractions and expansions, but which
 *        can also be used to add a modification at the end of a set of rules.
 *        <p>'&' : Indicates that the next rule follows the position to where
 *            the reset text-argument would be sorted.
 * </UL>
 *
 * <p>
 * This sounds more complicated than it is in practice. For example, the
 * following are equivalent ways of expressing the same thing:
 * <blockquote>
 * <pre>
 * a < b < c
 * a < b & b < c
 * a < c & a < b
 * </pre>
 * </blockquote>
 * Notice that the order is important, as the subsequent item goes immediately
 * after the text-argument. The following are not equivalent:
 * <blockquote>
 * <pre>
 * a < b & a < c
 * a < c & a < b
 * </pre>
 * </blockquote>
 * Either the text-argument must already be present in the sequence, or some
 * initial substring of the text-argument must be present. (e.g. "a < b & ae <
 * e" is valid since "a" is present in the sequence before "ae" is reset). In
 * this latter case, "ae" is not entered and treated as a single character;
 * instead, "e" is sorted as if it were expanded to two characters: "a"
 * followed by an "e". This difference appears in natural languages: in
 * traditional Spanish "ch" is treated as though it contracts to a single
 * character (expressed as "c < ch < d"), while in traditional German
 * "a" (a-umlaut) is treated as though it expanded to two characters
 * (expressed as "a,A < b,B ... & ae,a & AE,A").
 * <p>
 * <strong>Ignorable Characters</strong>
 * <p>
 * For ignorable characters, the first rule must start with a relation (the
 * examples we have used above are really fragments; "a < b" really should be
 * "< a < b"). If, however, the first relation is not "<", then all the all
 * text-arguments up to the first "<" are ignorable. For example, ", - < a < b"
 * makes "-" an ignorable character, as we saw earlier in the word
 * "black-birds". In the samples for different languages, you see that most
 * accents are ignorable.
 *
 * <p><strong>Normalization and Accents</strong>
 * <p>
 * <code>RuleBasedCollator</code> automatically processes its rule table to
 * include both pre-composed and combining-character versions of
 * accented characters.  Even if the provided rule string contains only
 * base characters and separate combining accent characters, the pre-composed
 * accented characters matching all canonical combinations of characters from
 * the rule string will be entered in the table.
 * <p>
 * This allows you to use a RuleBasedCollator to compare accented strings
 * even when the collator is set to NO_DECOMPOSITION.  There are two caveats,
 * however.  First, if the strings to be collated contain combining
 * sequences that may not be in canonical order, you should set the collator to
 * CANONICAL_DECOMPOSITION or FULL_DECOMPOSITION to enable sorting of
 * combining sequences.  Second, if the strings contain characters with
 * compatibility decompositions (such as full-width and half-width forms),
 * you must use FULL_DECOMPOSITION, since the rule tables only include
 * canonical mappings.
 * For more information, see
 * <A HREF="http://www.aw.com/devpress">The Unicode Standard, Version 2.0</A>.)
 *
 * <p><strong>Errors</strong>
 * <p>
 * The following are errors:
 * <UL Type=disc>
 *     <LI>A text-argument contains unquoted punctuation symbols
 *        (e.g. "a < b-c < d").
 *     <LI>A relation or reset character not followed by a text-argument
 *        (e.g. "a < , b").
 *     <LI>A reset where the text-argument (or an initial substring of the
 *         text-argument) is not already in the sequence.
 *         (e.g. "a < b & e < f")
 * </UL>
 * If you produce one of these errors, a <code>RuleBasedCollator</code> throws
 * a <code>ParseException</code>.
 *
 * <p><strong>Examples</strong>
 * <p>Simple:     "< a < b < c < d"
 * <p>Norwegian:  "< a,A< b,B< c,C< d,D< e,E< f,F< g,G< h,H< i,I< j,J
 *                 < k,K< l,L< m,M< n,N< o,O< p,P< q,Q< r,R< s,S< t,T
 *                 < u,U< v,V< w,W< x,X< y,Y< z,Z
 *                 < \u00E5=a\u030A,\u00C5=A\u030A
 *                 ;aa,AA< \u00E6,\u00C6< \u00F8,\u00D8"
 *
 * <p>
 * Normally, to create a rule-based Collator object, you will use
 * <code>Collator</code>'s factory method <code>getInstance</code>.
 * However, to create a rule-based Collator object with specialized
 * rules tailored to your needs, you construct the <code>RuleBasedCollator</code>
 * with the rules contained in a <code>String</code> object. For example:
 * <blockquote>
 * <pre>
 * String Simple = "< a < b < c < d";
 * RuleBasedCollator mySimple = new RuleBasedCollator(Simple);
 * </pre>
 * </blockquote>
 * Or:
 * <blockquote>
 * <pre>
 * String Norwegian = "< a,A< b,B< c,C< d,D< e,E< f,F< g,G< h,H< i,I< j,J" +
 *                 "< k,K< l,L< m,M< n,N< o,O< p,P< q,Q< r,R< s,S< t,T" +
 *                 "< u,U< v,V< w,W< x,X< y,Y< z,Z" +
 *                 "< \u00E5=a\u030A,\u00C5=A\u030A" +
 *                 ";aa,AA< \u00E6,\u00C6< \u00F8,\u00D8";
 * RuleBasedCollator myNorwegian = new RuleBasedCollator(Norwegian);
 * </pre>
 * </blockquote>
 *
 * <p>
 * Combining <code>Collator</code>s is as simple as concatenating strings.
 * Here's an example that combines two <code>Collator</code>s from two
 * different locales:
 * <blockquote>
 * <pre>
 * // Create an en_US Collator object
 * RuleBasedCollator en_USCollator = (RuleBasedCollator)
 *     Collator.getInstance(new Locale("en", "US", ""));
 * // Create a da_DK Collator object
 * RuleBasedCollator da_DKCollator = (RuleBasedCollator)
 *     Collator.getInstance(new Locale("da", "DK", ""));
 * // Combine the two
 * // First, get the collation rules from en_USCollator
 * String en_USRules = en_USCollator.getRules();
 * // Second, get the collation rules from da_DKCollator
 * String da_DKRules = da_DKCollator.getRules();
 * RuleBasedCollator newCollator =
 *     new RuleBasedCollator(en_USRules + da_DKRules);
 * // newCollator has the combined rules
 * </pre>
 * </blockquote>
 *
 * <p>
 * Another more interesting example would be to make changes on an existing
 * table to create a new <code>Collator</code> object.  For example, add
 * "& C < ch, cH, Ch, CH" to the <code>en_USCollator</code> object to create
 * your own:
 * <blockquote>
 * <pre>
 * // Create a new Collator object with additional rules
 * String addRules = "& C < ch, cH, Ch, CH";
 * RuleBasedCollator myCollator =
 *     new RuleBasedCollator(en_USCollator + addRules);
 * // myCollator contains the new rules
 * </pre>
 * </blockquote>
 *
 * <p>
 * The following example demonstrates how to change the order of
 * non-spacing accents,
 * <blockquote>
 * <pre>
 * // old rule
 * String oldRules = "=\u0301;\u0300;\u0302;\u0308"    // main accents
 *                 + ";\u0327;\u0303;\u0304;\u0305"    // main accents
 *                 + ";\u0306;\u0307;\u0309;\u030A"    // main accents
 *                 + ";\u030B;\u030C;\u030D;\u030E"    // main accents
 *                 + ";\u030F;\u0310;\u0311;\u0312"    // main accents
 *                 + "< a , A ; ae, AE ; \u00e6 , \u00c6"
 *                 + "< b , B < c, C < e, E & C < d, D";
 * // change the order of accent characters
 * String addOn = "& \u0300 ; \u0308 ; \u0302";
 * RuleBasedCollator myCollator = new RuleBasedCollator(oldRules + addOn);
 * </pre>
 * </blockquote>
 *
 * <p>
 * The last example shows how to put new primary ordering in before the
 * default setting. For example, in Japanese <code>Collator</code>, you
 * can either sort English characters before or after Japanese characters,
 * <blockquote>
 * <pre>
 * // get en_US Collator rules
 * RuleBasedCollator en_USCollator = (RuleBasedCollator)Collator.getInstance(Locale.US);
 * // add a few Japanese character to sort before English characters
 * // suppose the last character before the first base letter 'a' in
 * // the English collation rule is \u2212
 * String jaString = "& \u2212 < \u3041, \u3042 < \u3043, \u3044";
 * RuleBasedCollator myJapaneseCollator = new
 *     RuleBasedCollator(en_USCollator.getRules() + jaString);
 * </pre>
 * </blockquote>
 *
 * @see        Collator
 * @see        CollationElementIterator
 * @version    1.26 11/29/01
 * @author     Helena Shih
 */
public class RuleBasedCollator extends Collator{
    //===========================================================================================
    //  The following diagram shows the data structure of the RuleBasedCollator object.
    //  Suppose we have the rule, where 'o-umlaut' is the unicode char 0x00F6.
    //  "a, A < b, B < c, C, ch, cH, Ch, CH < d, D ... < o, O; 'o-umlaut'/E, 'O-umlaut'/E ...".
    //  What the rule says is, sorts 'ch'ligatures and 'c' only with tertiary difference and
    //  sorts 'o-umlaut' as if it's always expanded with 'e'.
    //
    // mapping table                     contracting list           expanding list
    // (contains all unicode char
    //  entries)                   ___    ____________       _________________________
    //  ________                +>|_*_|->|'c' |v('c') |  +>|v('o')|v('umlaut')|v('e')|
    // |_\u0001_|-> v('\u0001') | |_:_|  |------------|  | |-------------------------|
    // |_\u0002_|-> v('\u0002') | |_:_|  |'ch'|v('ch')|  | |             :           |
    // |____:___|               | |_:_|  |------------|  | |-------------------------|
    // |____:___|               |        |'cH'|v('cH')|  | |             :           |
    // |__'a'___|-> v('a')      |        |------------|  | |-------------------------|
    // |__'b'___|-> v('b')      |        |'Ch'|v('Ch')|  | |             :           |
    // |____:___|               |        |------------|  | |-------------------------|
    // |____:___|               |        |'CH'|v('CH')|  | |             :           |
    // |___'c'__|----------------         ------------   | |-------------------------|
    // |____:___|                                        | |             :           |
    // |o-umlaut|----------------------------------------  |_________________________|
    // |____:___|
    //
    // Noted by Helena Shih on 6/23/97
    //============================================================================================

    /**
     * RuleBasedCollator constructor.  This takes the table rules and builds
     * a collation table out of them.  Please see RuleBasedCollator class
     * description for more details on the collation rule syntax.
     * @see java.util.Locale
     * @param rules the collation rules to build the collation table from.
     * @exception ParseException A format exception
     * will be thrown if the build process of the rules fails. For
     * example, build rule "a < ? < d" will cause the constructor to
     * throw the ParseException because the '?' is not quoted.
     */
    public RuleBasedCollator(String rules) throws ParseException {
        setStrength(Collator.TERTIARY);
        build(rules);
    }

    /**
     * RuleBasedCollator constructor.  This takes the table rules and builds
     * a collation table out of them.  Please see RuleBasedCollator class
     * description for more details on the collation rule syntax.
     * @see java.util.Locale
     * @param rules the collation rules to build the collation table from.
     * @param decomp the decomposition strength used to build the
     * collation table and to perform comparisons.
     * @exception ParseException A format exception
     * will be thrown if the build process of the rules fails. For
     * example, build rule "a < ? < d" will cause the constructor to
     * throw the ParseException because the '?' is not quoted.
     */
    RuleBasedCollator(String rules, int decomp) throws ParseException {
        setStrength(Collator.TERTIARY);
        setDecomposition(decomp);
        build(rules);
    }

    /**
     * Gets the table-based rules for the collation object.
     * @return returns the collation rules that the table collation object
     * was created from.
     */
    public String getRules()
    {
        if (ruleTable == null) {
            ruleTable = mPattern.emitPattern();
            mPattern = null;
        }
        return ruleTable;
    }


    /**
     * Return a CollationElementIterator for the given String.
     * @see java.text.CollationElementIterator
     */
    public CollationElementIterator getCollationElementIterator(String source) {
        return new CollationElementIterator( source, this );
    }

    /**
     * Return a CollationElementIterator for the given String.
     * @see java.text.CollationElementIterator
     */
    public CollationElementIterator getCollationElementIterator(
                                                CharacterIterator source) {
        return new CollationElementIterator( source, this );
    }

    /**
     * Compares the character data stored in two different strings based on the
     * collation rules.  Returns information about whether a string is less
     * than, greater than or equal to another string in a language.
     * This can be overriden in a subclass.
     */
    public int compare(String source, String target)
    {
        // The basic algorithm here is that we use CollationElementIterators
        // to step through both the source and target strings.  We compare each
        // collation element in the source string against the corresponding one
        // in the target, checking for differences.
        //
        // If a difference is found, we set <result> to LESS or GREATER to
        // indicate whether the source string is less or greater than the target.
        //
        // However, it's not that simple.  If we find a tertiary difference
        // (e.g. 'A' vs. 'a') near the beginning of a string, it can be
        // overridden by a primary difference (e.g. "A" vs. "B") later in
        // the string.  For example, "AA" < "aB", even though 'A' > 'a'.
        //
        // To keep track of this, we use strengthResult to keep track of the
        // strength of the most significant difference that has been found
        // so far.  When we find a difference whose strength is greater than
        // strengthResult, it overrides the last difference (if any) that
        // was found.

        int result = Collator.EQUAL;
        strengthResult = Collator.IDENTICAL;
        
        if (sourceCursor == null) {
            sourceCursor = getCollationElementIterator(source);
        } else {
            sourceCursor.setText(source);
        }
        if (targetCursor == null) {
            targetCursor = getCollationElementIterator(target);
        } else {
            targetCursor.setText(target);
        }
        
        int sOrder = 0, tOrder = 0;
        
        boolean initialCheckSecTer = getStrength() >= Collator.SECONDARY;
        boolean checkSecTer = initialCheckSecTer;
        boolean checkTertiary = getStrength() >= Collator.TERTIARY;

        boolean gets = true, gett = true;
        
        while(true) {
            // Get the next collation element in each of the strings, unless
            // we've been requested to skip it.
            if (gets) sOrder = sourceCursor.next(); else gets = true;
            if (gett) tOrder = targetCursor.next(); else gett = true;

            // If we've hit the end of one of the strings, jump out of the loop
            if ((sOrder == CollationElementIterator.NULLORDER)||
                (tOrder == CollationElementIterator.NULLORDER))
                break;

            // When we hit the end of one of the strings, we're going to need to remember
            // the last element in each string, in order to decide if there
            //savedSOrder = sOrder;
            //savedTOrder = tOrder;

            int pSOrder = CollationElementIterator.primaryOrder(sOrder);
            int pTOrder = CollationElementIterator.primaryOrder(tOrder);

            // If there's no difference at this position, we can skip it
            if (sOrder == tOrder) {
                if (isFrenchSec && pSOrder != 0) {
                    if (!checkSecTer) {
                        // in french, a secondary difference more to the right is stronger,
                        // so accents have to be checked with each base element
                        checkSecTer = initialCheckSecTer;
                        // but tertiary differences are less important than the first 
                        // secondary difference, so checking tertiary remains disabled
                        checkTertiary = false;
                    }
                }
                continue;
            }

            // Compare primary differences first.
            if ( pSOrder != pTOrder )
            {
                if (sOrder == 0) {
                    // The entire source element is ignorable.
                    // Skip to the next source element, but don't fetch another target element.
                    gett = false;
                    continue;
                }
                if (tOrder == 0) {
                    gets = false;
                    continue;
                }

                // The source and target elements aren't ignorable, but it's still possible
                // for the primary component of one of the elements to be ignorable....

                if (pSOrder == 0)  // primary order in source is ignorable
                {
                    // The source's primary is ignorable, but the target's isn't.  We treat ignorables
                    // as a secondary difference, so remember that we found one.
                    if (checkSecTer) {
                        result = Collator.GREATER;  // (strength is SECONDARY)
                        checkSecTer = false;
                    }
                    // Skip to the next source element, but don't fetch another target element.
                    gett = false;
                }
                else if (pTOrder == 0)
                {
                    // record differences - see the comment above.
                    if (checkSecTer) {
                        result = Collator.LESS;  // (strength is SECONDARY)
                        checkSecTer = false;
                    }
                    // Skip to the next source element, but don't fetch another target element.
                    gets = false;
                } else {
                    // Neither of the orders is ignorable, and we already know that the primary
                    // orders are different because of the (pSOrder != pTOrder) test above.
                    // Record the difference and stop the comparison.
                    if (pSOrder < pTOrder) {
                        return Collator.LESS;  // (strength is PRIMARY)
                    } else {
                        return Collator.GREATER;  // (strength is PRIMARY)
                    }
                }
            } else { // else of if ( pSOrder != pTOrder )
                // primary order is the same, but complete order is different. So there
                // are no base elements at this point, only ignorables (Since the strings are
                // normalized)

                if (checkSecTer) {
                    // a secondary or tertiary difference may still matter
                    short secSOrder = CollationElementIterator.secondaryOrder(sOrder);
                    short secTOrder = CollationElementIterator.secondaryOrder(tOrder);
                    if (secSOrder != secTOrder) {
                        // there is a secondary difference
                        result = (secSOrder < secTOrder) ? Collator.LESS : Collator.GREATER;
                                                // (strength is SECONDARY)
                        checkSecTer = false; 
                        // (even in french, only the first secondary difference within
                        //  a base character matters)
                    } else {
                        if (checkTertiary) {
                            // a tertiary difference may still matter
                            short terSOrder = CollationElementIterator.tertiaryOrder(sOrder);
                            short terTOrder = CollationElementIterator.tertiaryOrder(tOrder);
                            if (terSOrder != terTOrder) {
                                // there is a tertiary difference
                                result = (terSOrder < terTOrder) ? Collator.LESS : Collator.GREATER;
                                                // (strength is TERTIARY)
                                checkTertiary = false;
                            }
                        }
                    }
                } // if (checkSecTer)

            }  // if ( pSOrder != pTOrder )
        } // while()

        if (sOrder != CollationElementIterator.NULLORDER) {
            // (tOrder must be CollationElementIterator::NULLORDER,
            //  since this point is only reached when sOrder or tOrder is NULLORDER.)
            // The source string has more elements, but the target string hasn't.
            do {
                if (CollationElementIterator.primaryOrder(sOrder) != 0) {
                    // We found an additional non-ignorable base character in the source string.
                    // This is a primary difference, so the source is greater
                    return Collator.GREATER; // (strength is PRIMARY)
                }
                else if (CollationElementIterator.secondaryOrder(sOrder) != 0) {
                    // Additional secondary elements mean the source string is greater
                    if (checkSecTer) {
                        result = Collator.GREATER;  // (strength is SECONDARY)
                        checkSecTer = false;
                    }
                } 
            } while ((sOrder = sourceCursor.next()) != CollationElementIterator.NULLORDER);
        }
        else if (tOrder != CollationElementIterator.NULLORDER) {
            // The target string has more elements, but the source string hasn't.
            do {
                if (CollationElementIterator.primaryOrder(tOrder) != 0)
                    // We found an additional non-ignorable base character in the target string.
                    // This is a primary difference, so the source is less
                    return Collator.LESS; // (strength is PRIMARY)
                else if (CollationElementIterator.secondaryOrder(tOrder) != 0) {
                    // Additional secondary elements in the target mean the source string is less
                    if (checkSecTer) {
                        result = Collator.LESS;  // (strength is SECONDARY)
                        checkSecTer = false;
                    }
                } 
            } while ((tOrder = targetCursor.next()) != CollationElementIterator.NULLORDER);
        }

        // For IDENTICAL comparisons, we use a bitwise character comparison
        // as a tiebreaker if all else is equal
        if (result == 0 && getStrength() == IDENTICAL) {
            result = Normalizer.decompose(source,getDecomposition())
                .compareTo(Normalizer.decompose(target,getDecomposition()));
        }
        return result;
    }

    /**
     * Transforms the string into a series of characters that can be compared
     * with CollationKey.compareTo. This overrides java.text.Collator.getCollationKey.
     * It can be overriden in a subclass.
     */
    public CollationKey getCollationKey(String source)
    {
        //
        // The basic algorithm here is to find all of the collation elements for each
        // character in the source string, convert them to a char representation,
        // and put them into the collation key.  But it's trickier than that.
        // Each collation element in a string has three components: primary (A vs B),
        // secondary (A vs A-acute), and tertiary (A' vs a); and a primary difference
        // at the end of a string takes precedence over a secondary or tertiary
        // difference earlier in the string.
        //
        // To account for this, we put all of the primary orders at the beginning of the
        // string, followed by the secondary and tertiary orders, separated by nulls.
        //
        // Here's a hypothetical example, with the collation element represented as
        // a three-digit number, one digit for primary, one for secondary, etc.
        //
        // String:              A     a     B   \u00e9 <--(e-acute)
        // Collation Elements: 101   100   201  510
        //
        // Collation Key:      1125<null>0001<null>1010
        //
        // To make things even trickier, secondary differences (accent marks) are compared
        // starting at the *end* of the string in languages with French secondary ordering.
        // But when comparing the accent marks on a single base character, they are compared
        // from the beginning.  To handle this, we reverse all of the accents that belong
        // to each base character, then we reverse the entire string of secondary orderings
        // at the end.  Taking the same example above, a French collator might return
        // this instead:
        //
        // Collation Key:      1125<null>1000<null>1010
        //
        if (source == null)
            return null;
        
        if (primResult == null) {
            primResult = new StringBuffer();
            secResult = new StringBuffer();
            terResult = new StringBuffer();
        } else {
            primResult.setLength(0);
            secResult.setLength(0);
            terResult.setLength(0);
        }
        int order = 0;
        boolean compareSec = (getStrength() >= Collator.SECONDARY);
        boolean compareTer = (getStrength() >= Collator.TERTIARY);
        int secOrder = CollationElementIterator.NULLORDER;
        int terOrder = CollationElementIterator.NULLORDER;
        int preSecIgnore = 0;

        if (sourceCursor == null) {
            sourceCursor = getCollationElementIterator(source);
        } else {
            sourceCursor.setText(source);
        }

        // walk through each character
        while ((order = sourceCursor.next()) !=
               CollationElementIterator.NULLORDER)
        {
            secOrder = CollationElementIterator.secondaryOrder(order);
            terOrder = CollationElementIterator.tertiaryOrder(order);
            if (!CollationElementIterator.isIgnorable(order))
            {
                primResult.append((char) (CollationElementIterator.primaryOrder(order)
                                    + COLLATIONKEYOFFSET));

                if (compareSec) {
                    //
                    // accumulate all of the ignorable/secondary characters attached
                    // to a given base character
                    //
                    if (isFrenchSec && preSecIgnore < secResult.length()) {
                        //
                        // We're doing reversed secondary ordering and we've hit a base
                        // (non-ignorable) character.  Reverse any secondary orderings
                        // that applied to the last base character.  (see block comment above.)
                        //
                        reverse(secResult, preSecIgnore, secResult.length());
                    }
                    // Remember where we are in the secondary orderings - this is how far
                    // back to go if we need to reverse them later.
                    secResult.append((char)(secOrder+ COLLATIONKEYOFFSET));
                    preSecIgnore = secResult.length();
                }
                if (compareTer) {
                    terResult.append((char)(terOrder+ COLLATIONKEYOFFSET));
                }
            }
            else
            {
                if (compareSec && secOrder != 0)
                    secResult.append((char)
                        (secOrder+maxSecOrder+ COLLATIONKEYOFFSET));
                if (compareTer && terOrder != 0)
                    terResult.append((char)
                        (terOrder+maxTerOrder+ COLLATIONKEYOFFSET));
            }
        }
        if (isFrenchSec)
        {
            if (preSecIgnore < secResult.length()) {
                // If we've accumlated any secondary characters after the last base character,
                // reverse them.
                reverse(secResult, preSecIgnore, secResult.length());
            }
            // And now reverse the entire secResult to get French secondary ordering.
            reverse(secResult, 0, secResult.length());
        }
        primResult.append((char)0);
        secResult.append((char)0);
        secResult.append(terResult.toString());
        primResult.append(secResult.toString());

        if (getStrength() == IDENTICAL) {
            primResult.append((char)0);
            primResult.append(Normalizer.decompose(source,getDecomposition()));
        }
        return new CollationKey(source, primResult.toString());
    }
    /**
     * Standard override; no change in semantics.
     */
    public Object clone() {
        RuleBasedCollator other = (RuleBasedCollator) super.clone();
        other.primResult = null;
        other.secResult = null;
        other.terResult = null;
        other.sourceCursor = null;
        other.targetCursor = null;
        other.key = new StringBuffer(MAXKEYSIZE);
        return other;
    }

    /**
     * Compares the equality of two collation objects.
     * @param obj the table-based collation object to be compared with this.
     * @return true if the current table-based collation object is the same
     * as the table-based collation object obj; false otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!super.equals(obj)) return false;  // super does class check
        RuleBasedCollator other = (RuleBasedCollator) obj;
        // all other non-transient information is also contained in rules.
        return (getRules().equals(other.getRules()));
    }
    /**
     * Generates the hash code for the table-based collation object
     */
    public int hashCode() {
        return getRules().hashCode();
    }

    // ==============================================================
    // private
    // ==============================================================

    /**
     * Create a table-based collation object with the given rules.
     * @see java.util.RuleBasedCollator#RuleBasedCollator
     * @exception ParseException If the rules format is incorrect.
     */
    private void build(String pattern) throws ParseException
    {
        boolean isSource = true;
        int i = 0;
        String expChars;
        String groupChars;
        if (pattern.length() == 0)
            throw new ParseException("Build rules empty.", 0);

        // This array maps Unicode characters to their collation ordering
        mapping = new CompactIntArray((int)UNMAPPED);

        // Normalize the build rules.  Find occurances of all decomposed characters
        // and normalize the rules before feeding into the builder.  By "normalize",
        // we mean that all precomposed Unicode characters must be converted into
        // a base character and one or more combining characters (such as accents).
        // When there are multiple combining characters attached to a base character,
        // the combining characters must be in their canonical order
        //
        pattern = Normalizer.decompose(pattern, getDecomposition());

        // Build the merged collation entries
        // Since rules can be specified in any order in the string
        // (e.g. "c , C < d , D < e , E .... C < CH")
        // this splits all of the rules in the string out into separate
        // objects and then sorts them.  In the above example, it merges the
        // "C < CH" rule in just before the "C < D" rule.
        //
        mPattern = new MergeCollation(pattern);
        int order = 0;
        
        // Now walk though each entry and add it to my own tables
        for (i = 0; i < mPattern.getCount(); ++i)
        {
            PatternEntry entry = mPattern.getItemAt(i);
            if (entry != null) {
                groupChars = entry.getChars();
                if ((groupChars.length() > 1) &&
                    (groupChars.charAt(groupChars.length()-1) == '@')) {
                    isFrenchSec = true;
                    groupChars = groupChars.substring(0, groupChars.length()-1);
                }

                order = increment(entry.getStrength(), order);
                expChars = entry.getExtension();

                if (expChars.length() != 0) {
                    addExpandOrder(groupChars, expChars, order);
                } else if (groupChars.length() > 1) {
                    addContractOrder(groupChars, order);
                } else {
                    char ch = groupChars.charAt(0);
                    addOrder(ch, order);
                }
            }
        }
        
        addComposedChars();
        
        commit();
        mapping.compact();
    }

    /** Add expanding entries for pre-composed unicode characters so that this
     * collator can be used reasonably well with decomposition turned off.
     */
    private void addComposedChars() throws ParseException {
        StringBuffer buf = new StringBuffer(1);

        // Iterate through all of the pre-composed characters in Unicode
        Normalizer.DecompIterator iter =
                        Normalizer.getDecompositions(CANONICAL_DECOMPOSITION);
        
        while (iter.hasNext()) {
            char c = iter.next();
            
            if (getCharOrder(c) == UNMAPPED) {
                // 
                // We don't already have an ordering for this pre-composed character.
                //
                // First, see if the decomposed string is already in our
                // tables as a single contracting-string ordering.
                // If so, just map the precomposed character to that order.
                //
                // TODO: What we should really be doing here is trying to find the
                // longest initial substring of the decomposition that is present
                // in the tables as a contracting character sequence, and find its
                // ordering.  Then do this recursively with the remaining chars
                // so that we build a list of orderings, and add that list to
                // the expansion table. 
                // That would be more correct but also significantly slower, so
                // I'm not totally sure it's worth doing.
                //
                String s = iter.decomposition();
                int contractOrder = getContractOrder(s);
                if (contractOrder != UNMAPPED) {
                    addOrder(c, contractOrder);
                } else {
                    //
                    // We don't have a contracting ordering for the entire string
                    // that results from the decomposition, but if we have orders
                    // for each individual character, we can add an expanding
                    // table entry for the pre-composed character 
                    //
                    boolean allThere = true;
                    for (int i = 0; i < s.length(); i++) {
                        if (getCharOrder(s.charAt(i)) == UNMAPPED) {
                            allThere = false;
                            break;
                        }
                    }
                    if (allThere) {
                        buf.setLength(0);
                        buf.append(c);
                        addExpandOrder(buf.toString(), s, UNMAPPED);
                    }
                }
            }
        }
    }
    
    /**
     * Look up for unmapped values in the expanded character table.
     *
     * When the expanding character tables are built by addExpandOrder,
     * it doesn't know what the final ordering of each character
     * in the expansion will be.  Instead, it just puts the raw character
     * code into the table, adding CHARINDEX as a flag.  Now that we've
     * finished building the mapping table, we can go back and look up
     * that character to see what its real collation order is and
     * stick that into the expansion table.  That lets us avoid doing
     * a two-stage lookup later.
     */
    private final void commit()
    {
        if (expandTable != null) {
            for (int i = 0; i < expandTable.size(); i++) {
                int[] valueList = (int [])expandTable.elementAt(i);
                for (int j = 0; j < valueList.length; j++) {
                    int order = valueList[j];
                    if (order < EXPANDCHARINDEX && order > CHARINDEX) {
                        // found a expanding character that isn't filled in yet
                        char ch = (char)(order - CHARINDEX);

                        // Get the real values for the non-filled entry
                        int realValue = getCharOrder(ch);

                        if (realValue == UNMAPPED) {
                            // The real value is still unmapped, maybe it's ignorable
                            valueList[j] = IGNORABLEMASK & ch;
                        } else {
                            // just fill in the value
                            valueList[j] = realValue;
                        }
                    }
                }
            }
        }
    }
    /**
     *  Increment of the last order based on the comparison level.
     */
    private final int increment(int aStrength, int lastValue)
    {
        switch(aStrength)
        {
        case Collator.PRIMARY:
            // increment priamry order  and mask off secondary and tertiary difference
            lastValue += PRIMARYORDERINCREMENT;
            lastValue &= PRIMARYORDERMASK;
            isOverIgnore = true;
            break;
        case Collator.SECONDARY:
            // increment secondary order and mask off tertiary difference
            lastValue += SECONDARYORDERINCREMENT;
            lastValue &= SECONDARYDIFFERENCEONLY;
            // record max # of ignorable chars with secondary difference
            if (!isOverIgnore)
                maxSecOrder++;
            break;
        case Collator.TERTIARY:
            // increment tertiary order
            lastValue += TERTIARYORDERINCREMENT;
            // record max # of ignorable chars with tertiary difference
            if (!isOverIgnore)
                maxTerOrder++;
            break;
        }
        return lastValue;
    }

    /**
     *  Adds a character and its designated order into the collation table.
     */
    private final void addOrder(char ch,
                                int anOrder)
    {
        // See if the char already has an order in the mapping table
        int order = mapping.elementAt(ch);

        if (order >= CONTRACTCHARINDEX) {
            // There's already an entry for this character that points to a contracting
            // character table.  Instead of adding the character directly to the mapping
            // table, we must add it to the contract table instead.

            key.setLength(0);
            key.append(ch);
            addContractOrder(key.toString(), anOrder);
        } else {
            // add the entry to the mapping table,
            // the same later entry replaces the previous one
            mapping.setElementAt(ch, anOrder);
        }
    }

    private final void addContractOrder(String groupChars, int anOrder) {
        addContractOrder(groupChars, anOrder, true);
    }
    
    /**
     *  Adds the contracting string into the collation table.
     */
    private final void addContractOrder(String groupChars, int anOrder,
                                          boolean fwd)
    {
        if (contractTable == null) {
            contractTable = new Vector(INITIALTABLESIZE);
        }

        // See if the initial character of the string already has a contract table.
        int entry = mapping.elementAt(groupChars.charAt(0));
        Vector entryTable = getContractValues(entry - CONTRACTCHARINDEX);
        
        if (entryTable == null) {
            // We need to create a new table of contract entries for this base char
            int tableIndex = CONTRACTCHARINDEX + contractTable.size();
            entryTable = new Vector(INITIALTABLESIZE);
            contractTable.addElement(entryTable);

            // Add the initial character's current ordering first. then
            // update its mapping to point to this contract table
            entryTable.addElement(new EntryPair(groupChars.substring(0,1), entry));
            mapping.setElementAt(groupChars.charAt(0), tableIndex);
        }
        
        // Now add (or replace) this string in the table
        int index = getEntry(entryTable, groupChars, fwd);
        if (index != UNMAPPED) {
            EntryPair pair = (EntryPair) entryTable.elementAt(index);
            pair.value = anOrder;
        } else {
            entryTable.addElement(new EntryPair(groupChars, anOrder, fwd));
        }
        
        // If this was a forward mapping for a contracting string, also add a
        // reverse mapping for it, so that CollationElementIterator.previous
        // can work right
        if (fwd) {
            addContractOrder(new StringBuffer(groupChars).reverse().toString(),
                             anOrder, false);
        }
    }

    /**
     * If the given string has been specified as a contracting string
     * in this collation table, return its ordering.
     * Otherwise return UNMAPPED.
     */
    private int getContractOrder(String groupChars)
    {
        int result = UNMAPPED;
        if (contractTable != null) {
            Vector entryTable = getContractValues(groupChars.charAt(0));
            if (entryTable != null) {
                int index = getEntry(entryTable, groupChars, true);
                if (index != UNMAPPED) {
                    EntryPair pair = (EntryPair) entryTable.elementAt(index);
                    result = pair.value;
                }
            }
        }
        return result;
    }
    
    final static int getEntry(Vector list, String name, boolean fwd) {
        for (int i = 0; i < list.size(); i++) {
            EntryPair pair = (EntryPair)list.elementAt(i);
            if (pair.fwd == fwd && pair.entryName.equals(name)) {
                return i;
            }
        }
        return UNMAPPED;
    }
    
    /**
     *  Get the entry of hash table of the contracting string in the collation
     *  table.
     *  @param ch the starting character of the contracting string
     */
    Vector getContractValues(char ch)
    {
        int index = mapping.elementAt(ch);
        return getContractValues(index - CONTRACTCHARINDEX);
    }

    Vector getContractValues(int index)
    {
        if (index >= 0)
        {
            return (Vector)contractTable.elementAt(index);
        }
        else // not found
        {
            return null;
        }
    }

    private final int getCharOrder(char ch) {
        int order = mapping.elementAt(ch);
        
        if (order >= CONTRACTCHARINDEX) {
            Vector groupList = getContractValues(order - CONTRACTCHARINDEX);
            EntryPair pair = (EntryPair)groupList.firstElement();
            order = pair.value;
        }
        return order;
    }
    
    /**
     *  Adds the expanding string into the collation table.
     */
    private final void addExpandOrder(String contractChars,
                                String expandChars,
                                int anOrder) throws ParseException
    {
        // Create an expansion table entry
        int tableIndex = addExpansion(anOrder, expandChars);
        
        // And add its index into the main mapping table
        if (contractChars.length() > 1) {
            addContractOrder(contractChars, tableIndex);
        } else {
            addOrder(contractChars.charAt(0), tableIndex);
        }
    }

    /**
     * Create a new entry in the expansion table that contains the orderings
     * for the given characers.  If anOrder is valid, it is added to the
     * beginning of the expanded list of orders.
     */
    private int addExpansion(int anOrder, String expandChars) {
        if (expandTable == null) {
            expandTable = new Vector(INITIALTABLESIZE);
        }
        
        // If anOrder is valid, we want to add it at the beginning of the list
        int offset = (anOrder == UNMAPPED) ? 0 : 1;
        
        int[] valueList = new int[expandChars.length() + offset];
        if (offset == 1) {
            valueList[0] = anOrder;
        }

        for (int i = 0; i < expandChars.length(); i++) {
            char ch = expandChars.charAt(i);
            int mapValue = getCharOrder(ch);
            
            if (mapValue != UNMAPPED) {
                valueList[i+offset] = mapValue;
            } else {
                // can't find it in the table, will be filled in by commit().
                valueList[i+offset] = CHARINDEX + (int)ch;
            }
        }

        // Add the expanding char list into the expansion table.
        int tableIndex = EXPANDCHARINDEX + expandTable.size();
        expandTable.addElement(valueList);
        
        return tableIndex;
    }
    
    /**
      * Return the maximum length of any expansion sequences that end
      * with the specified comparison order.
      *
      * @param order a collation order returned by previous or next.
      * @return the maximum length of any expansion seuences ending
      *         with the specified order.
      *
      * @see CollationElementIterator#getMaxExpansion
      */
    int getMaxExpansion(int order)
    {
        int result = 1;
        
        if (expandTable != null) {
            // Right now this does a linear search through the entire
            // expandsion table.  If a collator had a large number of expansions,
            // this could cause a performance problem, but in practise that
            // rarely happens
            for (int i = 0; i < expandTable.size(); i++) {
                int[] valueList = (int [])expandTable.elementAt(i);
                int length = valueList.length;
                
                if (length > result && valueList[length-1] == order) {
                    result = length;
                }
            }
        }

        return result;
    }
    
    /**
     *  Get the entry of hash table of the expanding string in the collation
     *  table.
     *  @param idx the index of the expanding string value list
     */
    final int[] getExpandValueList(int order) {
        return (int[])expandTable.elementAt(order - EXPANDCHARINDEX);
    }

    /**
     *  Get the comarison order of a character from the collation table.
     *  @return the comparison order of a character.
     */
    final int getUnicodeOrder(char ch)
    {
        return mapping.elementAt(ch);
    }

    /**
     * Reverse a string.
     */
    private final void reverse (StringBuffer result, int from, int to)
    {
        int i = from;
        char swap;

        int j = to - 1;
        while (i < j) {
            swap =  result.charAt(i);
            result.setCharAt(i, result.charAt(j));
            result.setCharAt(j, swap);
            i++;
            j--;
        }
    }

    // Proclaim compatibility with 1.1
    static final long serialVersionUID = 2822366911447564107L;

    final static int CHARINDEX = 0x70000000;  // need look up in .commit()
    final static int EXPANDCHARINDEX = 0x7E000000; // Expand index follows
    final static int CONTRACTCHARINDEX = 0x7F000000;  // contract indexes follow
    final static int UNMAPPED = 0xFFFFFFFF;

    private final static int PRIMARYORDERINCREMENT = 0x00010000;
    private final static int MAXIGNORABLE = 0x00010000;
    private final static int SECONDARYORDERINCREMENT = 0x00000100;
    private final static int TERTIARYORDERINCREMENT = 0x00000001;
    final static int PRIMARYORDERMASK = 0xffff0000;
    final static int SECONDARYORDERMASK = 0x0000ff00;
    final static int TERTIARYORDERMASK = 0x000000ff;
    final static int PRIMARYDIFFERENCEONLY = 0xffff0000;
    final static int SECONDARYDIFFERENCEONLY = 0xffffff00;
    private final static int SECONDARYRESETMASK = 0x0000ffff;
    private final static int IGNORABLEMASK = 0x0000ffff;
    private final static int INITIALTABLESIZE = 20;
    private final static int MAXKEYSIZE = 5;
    final static int PRIMARYORDERSHIFT = 16;
    final static int SECONDARYORDERSHIFT = 8;
    private final static int MAXTOKENLEN = 256;
    private final static int COLLATIONKEYOFFSET = 1;

    // these data members are reconstructed by readObject()
    private transient boolean isFrenchSec = false;
    private transient String ruleTable = null;

    private transient CompactIntArray mapping = null;
    private transient Vector   contractTable = null;
    private transient Vector   expandTable = null;

    // transients, only used in build or processing
    private transient MergeCollation mPattern = null;
    private transient boolean isOverIgnore = false;
    private transient short maxSecOrder = 0;
    private transient short maxTerOrder = 0;
    private transient StringBuffer key = new StringBuffer(MAXKEYSIZE);
    private transient int strengthResult = Collator.IDENTICAL;
    
    // Internal objects that are cached across calls so that they don't have to
    // be created/destroyed on every call to compare() and getCollationKey()
    private transient StringBuffer primResult = null;
    private transient StringBuffer secResult = null;
    private transient StringBuffer terResult = null;
    private transient CollationElementIterator sourceCursor = null;
    private transient CollationElementIterator targetCursor = null;
}

