/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.period;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import javax.time.MathUtils;
import javax.time.calendar.PeriodRule;

/**
 * An period of time measured using a single period rule, such as '3 days' or '65 seconds'.
 * <p>
 * PeriodField is an immutable period that stores an amount of human-scale
 * time for a single rule. For example, humans measure periods of time in years,
 * months, days, hours, minutes and seconds. These concepts are defined by period
 * rules in the chronology classes, and this class allows an amount to be specified
 * for one of the rules, such as '3 days' or '65 seconds'.
 * <p>
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * PeriodField is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class PeriodField
        implements PeriodProvider, Serializable {

    /**
     * The serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     *  The amount of the period of time.
     */
    private final long amount;
    /**
     * The rule defining the period of time.
     */
    private final PeriodRule rule;

    /**
     * Obtains a period field from an amount and rule.
     * <p>
     * The parameters represent the two parts of a phrase like '6 days'.
     *
     * @param amount  the amount of the period
     * @param rule  the rule that the period is measured in, validated not null
     */
    public static PeriodField of(long amount, PeriodRule rule) {
        PeriodFields.checkNotNull(rule, "PeriodRule must not be null");
        return new PeriodField(amount, rule);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param amount  the amount of the period
     * @param rule  the rule that the period is measured in, not null
     */
    private PeriodField(long amount, PeriodRule rule) {
        super();
        this.amount = amount;
        this.rule = rule;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the single rule as a <code>Set</code> defining the what the period
     * field represents.
     *
     * @return the period rule as an unmodifiable set of size 1, never null
     */
    public Set<PeriodRule> periodRules() {
        return Collections.singleton(rule);
    }

    /**
     * Gets the amount of time stored for the specified rule.
     * <p>
     * Zero is returned if no amount is stored for the rule.
     *
     * @param rule  the rule to get, not null
     * @return the amount of time stored in this period for the rule
     */
    public long periodAmount(PeriodRule rule) {
        PeriodFields.checkNotNull(rule, "PeriodRule must not be null");
        if (this.rule.equals(rule)) {
            return amount;
        }
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of time in this period.
     *
     * @return the amount of time of this period, may be negative
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Returns a copy of this field with a different amount of time.
     *
     * @param amount  the amount of time to set in the returned period field, may be negative
     * @return a new period field, never null
     */
    public PeriodField withAmount(long amount) {
        if (amount == this.amount) {
            return this;
        }
        return new PeriodField(amount, rule);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule defining the what the period represents.
     *
     * @return the period rule, never null
     */
    public PeriodRule getRule() {
        return rule;
    }

    /**
     * Returns a copy of this field with a different rule defining what the period represents.
     *
     * @param rule  the rule to set in the returned period field, may be negative
     * @return a new period field, never null
     */
    public PeriodField withRule(PeriodRule rule) {
        PeriodFields.checkNotNull(rule, "PeriodRule must not be null");
        if (rule.equals(this.rule)) {
            return this;
        }
        return new PeriodField(amount, rule);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this field with the specified amount of time added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount of time to add, may be negative
     * @return the new period field plus the specified amount of time, never null
     * @throws ArithmeticException if the result overflows a <code>long</code>
     */
    public PeriodField plus(long amount) {
        if (amount == 0) {
            return this;
        }
        return withAmount(MathUtils.safeAdd(this.amount, amount));
    }

    /**
     * Returns a copy of this field with the specified amount of time subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount of time to take away, may be negative
     * @return the new period minus the specified amount of time, never null
     * @throws ArithmeticException if the result overflows a <code>long</code>
     */
    public PeriodField minus(long amount) {
        if (amount == 0) {
            return this;
        }
        return withAmount(MathUtils.safeSubtract(this.amount, amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this field with the amount multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar, never null
     * @throws ArithmeticException if the result overflows a <code>long</code>
     */
    public PeriodField multipliedBy(long scalar) {
        if (scalar == 1) {
            return this;
        }
        return withAmount(MathUtils.safeMultiply(this.amount, scalar));
    }

    /**
     * Returns a copy of this field with the amount divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor, never null
     * @throws ArithmeticException if the divisor is zero
     */
    public PeriodField dividedBy(long divisor) {
        if (divisor == 1) {
            return this;
        }
        return withAmount(getAmount() / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the amount negated.
     *
     * @return the new period with the negated amount, never null
     * @throws ArithmeticException if the amount is <code>Long.MIN_VALUE</code>
     */
    public PeriodField negated() {
        return withAmount(MathUtils.safeNegate(getAmount()));
    }

    /**
     * Returns a new instance with the amount negated.
     *
     * @return the new period with abs positive amount, never null
     * @throws ArithmeticException if the amount is <code>Long.MIN_VALUE</code>
     */
    public PeriodField abs() {
        if (amount >= 0) {
            return this;
        }
        return negated();
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param obj  the other amount of time, null returns false
     * @return true if this amount of time is the same as that specified
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
           return true;
        }
        if (obj instanceof PeriodField) {
            PeriodField other = (PeriodField) obj;
            return this.amount == other.amount &&
                    this.rule.equals(other.rule);
        }
        return false;
    }

    /**
     * Returns the hash code for this period field.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return rule.hashCode() ^ (int)( amount ^ (amount >>> 32));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the amount of time, such as '6 Days'.
     *
     * @return a descriptive representation of the period field, not null
     */
    @Override
    public String toString() {
        return amount + " " + rule.getName();
    }

}