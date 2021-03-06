/*
 * Copyright (c) 2011-2012 Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.extended;

import java.io.Serializable;

import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.ISODateTimeRule;

/**
 * Internal class supplying the rules for the additional date and time objects.
 * <p>
 * {@code ExtendedCalendricalRule} provides the rules for classes like {@code Year}
 * and {@code MonthDay}. This class is package private. Rules should be accessed
 * using the {@code rule()} method on each type, such as {@code YearMonth.rule()}.
 * <p>
 * Normally, a rule would be written as a small static nested class within the main class.
 * This class exists to avoid writing those separate classes, centralizing the singleton
 * pattern and enhancing performance via an {@code int} ordinal and package scope.
 * Thus, this design is an optimization and should not necessarily be considered best practice.
 * <p>
 * This class is final, immutable and thread-safe.
 *
 * @param <T> the rule type
 * @author Stephen Colebourne
 */
final class ExtendedCalendricalRule<T> extends CalendricalRule<T> implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ordinal for performance and serialization.
     */
    final int ordinal;

    /**
     * Constructor used to create a rule.
     *
     * @param type  the type, not null
     * @param ordinal  the ordinal, not null
     */
    protected ExtendedCalendricalRule(Class<T> type, int ordinal) {
        super(type, type.getSimpleName());
        this.ordinal = ordinal;
    }

    /**
     * Deserialize singletons.
     * 
     * @return the resolved value, not null
     */
    private Object readResolve() {
        return RULE_CACHE[ordinal];
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    protected T deriveFrom(CalendricalEngine engine) {
        switch (ordinal) {
            case YEAR_ORDINAL: return (T) deriveYear(engine);
            case YEAR_MONTH_ORDINAL: return (T) deriveYearMonth(engine);
            case MONTH_DAY_ORDINAL: return (T) deriveMonthDay(engine);
            case QUARTER_OF_YEAR_ORDINAL: return (T) deriveQoy(engine);
        }
        return null;
    }

    /**
     * Obtains an instance of {@code Year} from the engine.
     *
     * @param engine  the calendrical engine, not null
     * @return the derived object, null if unable to obtain
     */
    static Year deriveYear(CalendricalEngine engine) {
        DateTimeField field = engine.getFieldDerived(ISODateTimeRule.YEAR, true);
        return (field != null ? Year.of(field.getValidIntValue()) : null);
    }

    /**
     * Obtains an instance of {@code YearMonth} from the engine.
     *
     * @param engine  the calendrical engine, not null
     * @return the derived object, null if unable to obtain
     */
    static YearMonth deriveYearMonth(CalendricalEngine engine) {
        DateTimeField year = engine.getFieldDerived(ISODateTimeRule.YEAR, true);
        DateTimeField moy = engine.getFieldDerived(ISODateTimeRule.MONTH_OF_YEAR, true);
        if (year == null || moy == null) {
            return null;
        }
        return YearMonth.of(year.getValidIntValue(), moy.getValidIntValue());
    }

    /**
     * Obtains an instance of {@code MonthDay} from the engine.
     *
     * @param engine  the calendrical engine, not null
     * @return the derived object, null if unable to obtain
     */
    static MonthDay deriveMonthDay(CalendricalEngine engine) {
        DateTimeField moy = engine.getFieldDerived(ISODateTimeRule.MONTH_OF_YEAR, true);
        DateTimeField dom = engine.getFieldDerived(ISODateTimeRule.DAY_OF_MONTH, true);
        if (moy == null || dom == null) {
            return null;
        }
        return MonthDay.of(moy.getValidIntValue(), dom.getValidIntValue());
    }

    /**
     * Obtains an instance of {@code QuarterOfYear} from the engine.
     *
     * @param engine  the calendrical engine, not null
     * @return the derived object, null if unable to obtain
     */
    static QuarterOfYear deriveQoy(CalendricalEngine engine) {
        DateTimeField field = engine.getFieldDerived(ISODateTimeRule.QUARTER_OF_YEAR, true);
        return (field != null ? QuarterOfYear.of(field.getValidIntValue()) : null);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExtendedCalendricalRule<?>) {
            return ordinal == ((ExtendedCalendricalRule<?>) obj).ordinal;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ExtendedCalendricalRule.class.hashCode() + ordinal;
    }

    //-----------------------------------------------------------------------
    private static final int YEAR_ORDINAL = 0;
    private static final int YEAR_MONTH_ORDINAL = 1;
    private static final int MONTH_DAY_ORDINAL = 2;
    private static final int QUARTER_OF_YEAR_ORDINAL = 3;

    //-----------------------------------------------------------------------
    /**
     * The rule for {@code Year}.
     */
    static final CalendricalRule<Year> YEAR = new ExtendedCalendricalRule<Year>(Year.class, YEAR_ORDINAL);
    /**
     * The rule for {@code YearMonth}.
     */
    static final CalendricalRule<YearMonth> YEAR_MONTH = new ExtendedCalendricalRule<YearMonth>(YearMonth.class, YEAR_MONTH_ORDINAL);
    /**
     * The rule for {@code MonthDay}.
     */
    static final CalendricalRule<MonthDay> MONTH_DAY = new ExtendedCalendricalRule<MonthDay>(MonthDay.class, MONTH_DAY_ORDINAL);
    /**
     * The rule for {@code QuarterOfYear}.
     */
    static final CalendricalRule<QuarterOfYear> QUARTER_OF_YEAR = new ExtendedCalendricalRule<QuarterOfYear>(QuarterOfYear.class, QUARTER_OF_YEAR_ORDINAL);

    /**
     * Cache of rules for deserialization.
     * Indices must match ordinal passed to rule constructor.
     */
    private static final CalendricalRule<?>[] RULE_CACHE = new CalendricalRule<?>[] {
        YEAR, YEAR_MONTH, MONTH_DAY, QUARTER_OF_YEAR,
    };

}
