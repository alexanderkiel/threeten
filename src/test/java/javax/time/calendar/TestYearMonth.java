/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.period.MockPeriodProviderReturnsNull;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test YearMonth.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestYearMonth {

    private static final DateTimeFieldRule RULE_YEAR = ISOChronology.yearRule();
    private static final DateTimeFieldRule RULE_MONTH = ISOChronology.monthOfYearRule();
    private static final DateTimeFieldRule RULE_DOM = ISOChronology.dayOfMonthRule();
    private YearMonth TEST_2008_06;

    @BeforeMethod
    public void setUp() {
        TEST_2008_06 = YearMonth.yearMonth(2008, 6);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_2008_06 instanceof CalendricalProvider);
        assertTrue(TEST_2008_06 instanceof Serializable);
        assertTrue(TEST_2008_06 instanceof Comparable);
        assertTrue(TEST_2008_06 instanceof DateAdjuster);
        assertTrue(TEST_2008_06 instanceof DateMatcher);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_2008_06);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_2008_06);
    }

    public void test_immutable() {
        Class<YearMonth> cls = YearMonth.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    void check(YearMonth test, int y, int m) {
        assertEquals(test.getYear(), y);
        assertEquals(test.getMonthOfYear().getValue(), m);
    }
    
    public void factory_objects() {
        YearMonth test = YearMonth.yearMonth(Year.isoYear(2008), MonthOfYear.FEBRUARY);
        check(test, 2008, 2);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objects_nullYear() {
        YearMonth.yearMonth(null, MonthOfYear.JUNE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_objects_nullMonth() {
        YearMonth.yearMonth(Year.isoYear(2008), null);
    }

    //-----------------------------------------------------------------------
    public void factory_intsMonth() {
        YearMonth test = YearMonth.yearMonth(2008, MonthOfYear.FEBRUARY);
        check(test, 2008, 2);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_intsMonth_yearTooLow() {
        try {
            YearMonth.yearMonth(Year.MIN_YEAR - 1, MonthOfYear.JANUARY);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_YEAR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_intsMonth_dayTooHigh() {
        if (Year.MAX_YEAR == Integer.MAX_VALUE) {
            throw new IllegalCalendarFieldValueException("", RULE_YEAR);
        }
        try {
            YearMonth.yearMonth(Year.MAX_YEAR + 1, MonthOfYear.JANUARY);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_YEAR);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_intsMonth_nullMonth() {
        YearMonth.yearMonth(2008, null);
    }

    //-----------------------------------------------------------------------
    public void factory_ints() {
        YearMonth test = YearMonth.yearMonth(2008, 2);
        check(test, 2008, 2);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_yearTooLow() {
        try {
            YearMonth.yearMonth(Year.MIN_YEAR - 1, 2);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_YEAR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_dayTooHigh() {
        if (Year.MAX_YEAR == Integer.MAX_VALUE) {
            throw new IllegalCalendarFieldValueException("", RULE_YEAR);
        }
        try {
            YearMonth.yearMonth(Year.MAX_YEAR + 1, 2);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_YEAR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_monthTooLow() {
        try {
            YearMonth.yearMonth(2008, 0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_monthTooHigh() {
        try {
            YearMonth.yearMonth(2008, 13);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_MONTH);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void factory_DateProvider() {
        DateProvider provider = LocalDate.date(2008, 6, 30);
        assertEquals(YearMonth.yearMonth(provider), TEST_2008_06);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_DateProvider_null() {
        YearMonth.yearMonth(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_DateProvider_badProvider() {
        YearMonth.yearMonth(new MockDateProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    public void factory_CalendricalProvider() {
        CalendricalProvider provider = new Calendrical(RULE_YEAR, 2008, RULE_MONTH, 6);
        assertEquals(YearMonth.yearMonth(provider), TEST_2008_06);
    }

    public void factory_CalendricalProvider_otherFieldsIgnored() {
        Calendrical provider = LocalDate.date(2008, 6, 30).toCalendrical();
        assertEquals(YearMonth.yearMonth(provider), TEST_2008_06);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_CalendricalProvider_null() {
        YearMonth.yearMonth(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_CalendricalProvider_badProvider() {
        YearMonth.yearMonth(new MockCalendricalProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    public void test_getChronology() {
        assertEquals(ISOChronology.INSTANCE, TEST_2008_06.getChronology());
    }

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {2008, 1},
            {2008, 2},
            {-1, 3},
            {0, 12},
        };
    }

    @Test(dataProvider="sampleDates")
    public void test_get(int y, int m) {
        YearMonth a = YearMonth.yearMonth(y, m);
        assertEquals(a.getYear(), y);
        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
        
        assertEquals(a.toYear(), Year.isoYear(y));
        assertEquals(a.toMonthOfYear(), MonthOfYear.monthOfYear(m));
    }

    //-----------------------------------------------------------------------
    // with(Year)
    //-----------------------------------------------------------------------
    public void test_with_Year() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.with(Year.isoYear(2000)), YearMonth.yearMonth(2000, 6));
    }

    public void test_with_Year_noChange() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertSame(test.with(Year.isoYear(2008)), test);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_Year_null() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        test.with((Year) null);
    }

    //-----------------------------------------------------------------------
    // with(MonthOfYear)
    //-----------------------------------------------------------------------
    public void test_with_MonthOfYear() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.with(MonthOfYear.JANUARY), YearMonth.yearMonth(2008, 1));
    }

    public void test_with_MonthOfYear_noChange() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertSame(test.with(MonthOfYear.JUNE), test);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_MonthOfYear_null() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        test.with((MonthOfYear) null);
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.withYear(1999), YearMonth.yearMonth(1999, 6));
    }

    public void test_withYear_int_noChange() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertSame(test.withYear(2008), test);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_tooLow() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        try {
            test.withYear(Year.MIN_YEAR - 1);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_YEAR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_tooHigh() {
        if (Year.MAX_YEAR == Integer.MAX_VALUE) {
            throw new IllegalCalendarFieldValueException("", RULE_YEAR);
        }
        YearMonth test = YearMonth.yearMonth(2008, 6);
        try {
            test.withYear(Year.MAX_YEAR + 1);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_YEAR);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.withMonthOfYear(1), YearMonth.yearMonth(2008, 1));
    }

    public void test_withMonthOfYear_int_noChange() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertSame(test.withMonthOfYear(6), test);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_tooLow() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        try {
            test.withMonthOfYear(0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_tooHigh() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        try {
            test.withMonthOfYear(13);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), RULE_MONTH);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.period(1, 2, 3, 4, 5, 6, 7);
        YearMonth test = YearMonth.yearMonth(2008, 6).plus(provider);
        assertEquals(test, YearMonth.yearMonth(2009, 8));
    }

    public void test_plus_PeriodProvider_timeIgnored() {
        PeriodProvider provider = Period.period(1, 2, 3, Integer.MAX_VALUE, 5, 6, 7);
        YearMonth test = YearMonth.yearMonth(2008, 6).plus(provider);
        assertEquals(test, YearMonth.yearMonth(2009, 8));
    }

    public void test_plus_PeriodProvider_zero() {
        YearMonth base = YearMonth.yearMonth(2008, 6);
        YearMonth test = base.plus(Period.ZERO);
        assertSame(test, base);
    }

    public void test_plus_PeriodProvider_previousValidResolver_oneMonth() {
        PeriodProvider provider = Period.months(1);
        YearMonth test = YearMonth.yearMonth(2008, 6).plus(provider);
        assertEquals(test, YearMonth.yearMonth(2008, 7));
    }

    public void test_plus_PeriodProvider_previousValidResolver_oneYear() {
        PeriodProvider provider = Period.years(1);
        YearMonth test = YearMonth.yearMonth(2008, 6).plus(provider);
        assertEquals(test, YearMonth.yearMonth(2009, 6));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        YearMonth.yearMonth(2008, 6).plus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_badProvider() {
        YearMonth.yearMonth(2008, 6).plus(new MockPeriodProviderReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plus_PeriodProvider_invalidTooLarge() {
        PeriodProvider provider = Period.years(1);
        YearMonth.yearMonth(Year.MAX_YEAR, 6).plus(provider);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plus_PeriodProvider_invalidTooSmall() {
        PeriodProvider provider = Period.years(-1);
        YearMonth.yearMonth(Year.MIN_YEAR, 6).plus(provider);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears_int() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.plusYears(1), YearMonth.yearMonth(2009, 6));
    }

    public void test_plusYears_int_noChange() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertSame(test.plusYears(0), test);
    }

    public void test_plusYears_int_negative() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.plusYears(-1), YearMonth.yearMonth(2007, 6));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_invalidTooLarge() {
        YearMonth test = YearMonth.yearMonth(Year.MAX_YEAR, 6);
        test.plusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_int_invalidTooSmall() {
        YearMonth test = YearMonth.yearMonth(Year.MIN_YEAR, 6);
        test.plusYears(-1);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths_int() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.plusMonths(1), YearMonth.yearMonth(2008, 7));
    }

    public void test_plusMonths_int_noChange() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertSame(test.plusMonths(0), test);
    }

    public void test_plusMonths_int_overYears() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.plusMonths(7), YearMonth.yearMonth(2009, 1));
    }

    public void test_plusMonths_int_negative() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.plusMonths(-1), YearMonth.yearMonth(2008, 5));
    }

    public void test_plusMonths_int_negativeOverYear() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.plusMonths(-6), YearMonth.yearMonth(2007, 12));
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_int_invalidTooLarge() {
        YearMonth test = YearMonth.yearMonth(Year.MAX_YEAR, 12);
        test.plusMonths(1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_plusMonths_int_invalidTooSmall() {
        YearMonth test = YearMonth.yearMonth(Year.MIN_YEAR, 1);
        test.plusMonths(-1);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.period(1, 2, 3, 4, 5, 6, 7);
        YearMonth test = YearMonth.yearMonth(2008, 6).minus(provider);
        assertEquals(test, YearMonth.yearMonth(2007, 4));
    }

    public void test_minus_PeriodProvider_timeIgnored() {
        PeriodProvider provider = Period.period(1, 2, 3, Integer.MAX_VALUE, 5, 6, 7);
        YearMonth test = YearMonth.yearMonth(2008, 6).minus(provider);
        assertEquals(test, YearMonth.yearMonth(2007, 4));
    }

    public void test_minus_PeriodProvider_zero() {
        YearMonth base = YearMonth.yearMonth(2008, 6);
        YearMonth test = base.minus(Period.ZERO);
        assertSame(test, base);
    }

    public void test_minus_PeriodProvider_previousValidResolver_oneMonth() {
        PeriodProvider provider = Period.months(1);
        YearMonth test = YearMonth.yearMonth(2008, 6).minus(provider);
        assertEquals(test, YearMonth.yearMonth(2008, 5));
    }

    public void test_minus_PeriodProvider_previousValidResolver_oneYear() {
        PeriodProvider provider = Period.years(1);
        YearMonth test = YearMonth.yearMonth(2008, 6).minus(provider);
        assertEquals(test, YearMonth.yearMonth(2007, 6));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_null() {
        YearMonth.yearMonth(2008, 6).minus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_badProvider() {
        YearMonth.yearMonth(2008, 6).minus(new MockPeriodProviderReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minus_PeriodProvider_invalidTooLarge() {
        PeriodProvider provider = Period.years(-1);
        YearMonth.yearMonth(Year.MAX_YEAR, 6).minus(provider);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minus_PeriodProvider_invalidTooSmall() {
        PeriodProvider provider = Period.years(1);
        YearMonth.yearMonth(Year.MIN_YEAR, 6).minus(provider);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears_int() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.minusYears(1), YearMonth.yearMonth(2007, 6));
    }

    public void test_minusYears_int_noChange() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertSame(test.minusYears(0), test);
    }

    public void test_minusYears_int_negative() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.minusYears(-1), YearMonth.yearMonth(2009, 6));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_int_invalidTooLarge() {
        YearMonth test = YearMonth.yearMonth(Year.MAX_YEAR, 6);
        test.minusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_int_invalidTooSmall() {
        YearMonth test = YearMonth.yearMonth(Year.MIN_YEAR, 6);
        test.minusYears(1);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths_int() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.minusMonths(1), YearMonth.yearMonth(2008, 5));
    }

    public void test_minusMonths_int_noChange() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertSame(test.minusMonths(0), test);
    }

    public void test_minusMonths_int_overYears() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.minusMonths(6), YearMonth.yearMonth(2007, 12));
    }

    public void test_minusMonths_int_negative() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.minusMonths(-1), YearMonth.yearMonth(2008, 7));
    }

    public void test_minusMonths_int_negativeOverYear() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.minusMonths(-7), YearMonth.yearMonth(2009, 1));
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_int_invalidTooLarge() {
        YearMonth test = YearMonth.yearMonth(Year.MAX_YEAR, 12);
        test.minusMonths(-1);
    }

    @Test(expectedExceptions={CalendricalException.class})
    public void test_minusMonths_int_invalidTooSmall() {
        YearMonth test = YearMonth.yearMonth(Year.MIN_YEAR, 1);
        test.minusMonths(1);
    }

    //-----------------------------------------------------------------------
    // rollMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_rollMonthOfYear() {
        YearMonth base = YearMonth.yearMonth(2008, 7);
        assertSame(base.rollMonthOfYear(0), base);
        assertEquals(base.rollMonthOfYear(1), YearMonth.yearMonth(2008, 8));
        assertEquals(base.rollMonthOfYear(2), YearMonth.yearMonth(2008, 9));
        assertEquals(base.rollMonthOfYear(3), YearMonth.yearMonth(2008, 10));
        assertEquals(base.rollMonthOfYear(4), YearMonth.yearMonth(2008, 11));
        assertEquals(base.rollMonthOfYear(5), YearMonth.yearMonth(2008, 12));
        assertEquals(base.rollMonthOfYear(6), YearMonth.yearMonth(2008, 1));
        assertEquals(base.rollMonthOfYear(7), YearMonth.yearMonth(2008, 2));
        assertEquals(base.rollMonthOfYear(8), YearMonth.yearMonth(2008, 3));
        assertEquals(base.rollMonthOfYear(9), YearMonth.yearMonth(2008, 4));
        assertEquals(base.rollMonthOfYear(10), YearMonth.yearMonth(2008, 5));
        assertEquals(base.rollMonthOfYear(11), YearMonth.yearMonth(2008, 6));
        assertEquals(base.rollMonthOfYear(12), YearMonth.yearMonth(2008, 7));
        
        assertEquals(base.rollMonthOfYear(-1), YearMonth.yearMonth(2008, 6));
        assertEquals(base.rollMonthOfYear(-2), YearMonth.yearMonth(2008, 5));
        assertEquals(base.rollMonthOfYear(-3), YearMonth.yearMonth(2008, 4));
        assertEquals(base.rollMonthOfYear(-4), YearMonth.yearMonth(2008, 3));
        assertEquals(base.rollMonthOfYear(-5), YearMonth.yearMonth(2008, 2));
        assertEquals(base.rollMonthOfYear(-6), YearMonth.yearMonth(2008, 1));
        assertEquals(base.rollMonthOfYear(-7), YearMonth.yearMonth(2008, 12));
        assertEquals(base.rollMonthOfYear(-8), YearMonth.yearMonth(2008, 11));
        assertEquals(base.rollMonthOfYear(-9), YearMonth.yearMonth(2008, 10));
        assertEquals(base.rollMonthOfYear(-10), YearMonth.yearMonth(2008, 9));
        assertEquals(base.rollMonthOfYear(-11), YearMonth.yearMonth(2008, 8));
        assertEquals(base.rollMonthOfYear(-12), YearMonth.yearMonth(2008, 7));
    }

    //-----------------------------------------------------------------------
    // adjustDate()
    //-----------------------------------------------------------------------
    public void test_adjustDate() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        LocalDate date = LocalDate.date(2007, 1, 1);
        assertEquals(test.adjustDate(date), LocalDate.date(2008, 6, 1));
    }

    public void test_adjustDate_same() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        LocalDate date = LocalDate.date(2008, 6, 30);
        assertSame(test.adjustDate(date), date);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_null() {
        TEST_2008_06.adjustDate((LocalDate) null);
    }

    //-----------------------------------------------------------------------
    public void test_adjustDate_DateResolver() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        LocalDate date = LocalDate.date(2007, 1, 1);
        assertEquals(test.adjustDate(date, DateResolvers.nextValid()), LocalDate.date(2008, 6, 1));
    }

    public void test_adjustDate_DateResolver_same() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        LocalDate date = LocalDate.date(2008, 6, 30);
        assertSame(test.adjustDate(date, DateResolvers.nextValid()), date);
    }

    public void test_adjustDate_DateResolver_resolve() {
        YearMonth test = YearMonth.yearMonth(2008, 2);
        LocalDate date = LocalDate.date(2007, 1, 31);
        assertEquals(test.adjustDate(date, DateResolvers.nextValid()), LocalDate.date(2008, 3, 1));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_DateResolver_invalid() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        LocalDate date = LocalDate.date(2007, 1, 31);
        try {
            test.adjustDate(date, DateResolvers.strict());
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_DateResolver_nullDate() {
        TEST_2008_06.adjustDate((LocalDate) null, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_DateResolver_nullResolver() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        LocalDate date = LocalDate.date(2008, 6, 30);  // same year-month, should still NPE
        test.adjustDate(date, (DateResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_DateResolver_badResolver() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        LocalDate date = LocalDate.date(2007, 6, 30);
        test.adjustDate(date, new MockDateResolverReturnsNull());
    }

    //-----------------------------------------------------------------------
    // matchesDate()
    //-----------------------------------------------------------------------
    public void test_matchesDate() {
        assertEquals(YearMonth.yearMonth(2008, 6).matchesDate(LocalDate.date(2008, 6, 1)), true);
        assertEquals(YearMonth.yearMonth(2007, 6).matchesDate(LocalDate.date(2007, 6, 1)), true);
        assertEquals(YearMonth.yearMonth(2008, 5).matchesDate(LocalDate.date(2008, 5, 1)), true);
        
        assertEquals(YearMonth.yearMonth(2007, 6).matchesDate(LocalDate.date(2008, 6, 1)), false);
        assertEquals(YearMonth.yearMonth(2008, 5).matchesDate(LocalDate.date(2008, 6, 1)), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_null() {
        TEST_2008_06.matchesDate(null);
    }

    //-----------------------------------------------------------------------
    // atDay(DayOfMonth)
    //-----------------------------------------------------------------------
    public void test_atDay() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.atDay(DayOfMonth.dayOfMonth(30)), LocalDate.date(2008, 6, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atDay_nullYear() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        test.atDay((DayOfMonth) null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_atDay_invalidDay() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        try {
            test.atDay(DayOfMonth.dayOfMonth(31));
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // atDay(int)
    //-----------------------------------------------------------------------
    public void test_atDay_int() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.atDay(30), LocalDate.date(2008, 6, 30));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_atDay_int_invalidDay() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        try {
            test.atDay(31);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    public void test_toCalendrical() {
        YearMonth test = YearMonth.yearMonth(2008, 6);
        assertEquals(test.toCalendrical(), new Calendrical(RULE_YEAR, 2008, RULE_MONTH, 6));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_YearMonth(
            YearMonth.yearMonth(-1, 1),
            YearMonth.yearMonth(0, 1),
            YearMonth.yearMonth(0, 12),
            YearMonth.yearMonth(1, 1),
            YearMonth.yearMonth(1, 2),
            YearMonth.yearMonth(1, 12),
            YearMonth.yearMonth(2008, 1),
            YearMonth.yearMonth(2008, 6),
            YearMonth.yearMonth(2008, 12)
        );
    }

    void doTest_comparisons_YearMonth(YearMonth... localDates) {
        for (int i = 0; i < localDates.length; i++) {
            YearMonth a = localDates[i];
            for (int j = 0; j < localDates.length; j++) {
                YearMonth b = localDates[j];
                if (i < j) {
                    assertTrue(a.compareTo(b) < 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertTrue(a.compareTo(b) > 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_ObjectNull() {
        TEST_2008_06.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        TEST_2008_06.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        TEST_2008_06.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    public void test_equals() {
        YearMonth a = YearMonth.yearMonth(2008, 6);
        YearMonth b = YearMonth.yearMonth(2008, 6);
        YearMonth c = YearMonth.yearMonth(2007, 6);
        YearMonth d = YearMonth.yearMonth(2008, 5);
        
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), true);
        assertEquals(a.equals(c), false);
        assertEquals(a.equals(d), false);
        
        assertEquals(b.equals(a), true);
        assertEquals(b.equals(b), true);
        assertEquals(b.equals(c), false);
        assertEquals(b.equals(d), false);
        
        assertEquals(c.equals(a), false);
        assertEquals(c.equals(b), false);
        assertEquals(c.equals(c), true);
        assertEquals(c.equals(d), false);
        
        assertEquals(d.equals(a), false);
        assertEquals(d.equals(b), false);
        assertEquals(d.equals(c), false);
        assertEquals(d.equals(d), true);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_2008_06.equals(TEST_2008_06), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_2008_06.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_2008_06.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_hashCode(int y, int m) {
        YearMonth a = YearMonth.yearMonth(y, m);
        assertEquals(a.hashCode(), a.hashCode());
        YearMonth b = YearMonth.yearMonth(y, m);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_hashCode_unique() {
        Set<Integer> uniques = new HashSet<Integer>(201 * 12);
        for (int i = 1900; i <= 2100; i++) {
            for (int j = 1; j <= 12; j++) {
                assertTrue(uniques.add(YearMonth.yearMonth(i, j).hashCode()));
            }
        }
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 1, "2008-01"},
            {2008, 12, "2008-12"},
            {7, 5, "0007-05"},
            {0, 5, "0000-05"},
            {-1, 1, "-0001-01"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, String expected) {
        YearMonth test = YearMonth.yearMonth(y, m);
        String str = test.toString();
        assertEquals(str, expected);
    }

	//-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @DataProvider(name="goodParseData")
    Object[][] provider_goodParseData() {
    	return new Object[][] {
    			{"0000-01", YearMonth.yearMonth(0, 1)},
    			{"0000-12", YearMonth.yearMonth(0, 12)},
    			{"9999-12", YearMonth.yearMonth(9999, 12)},
    			{"2000-01", YearMonth.yearMonth(2000, 1)},
    			{"2000-02", YearMonth.yearMonth(2000, 2)},
    			{"2000-03", YearMonth.yearMonth(2000, 3)},
    			{"2000-04", YearMonth.yearMonth(2000, 4)},
    			{"2000-05", YearMonth.yearMonth(2000, 5)},
    			{"2000-06", YearMonth.yearMonth(2000, 6)},
    			{"2000-07", YearMonth.yearMonth(2000, 7)},
    			{"2000-08", YearMonth.yearMonth(2000, 8)},
    			{"2000-09", YearMonth.yearMonth(2000, 9)},
    			{"2000-10", YearMonth.yearMonth(2000, 10)},
    			{"2000-11", YearMonth.yearMonth(2000, 11)},
    			{"2000-12", YearMonth.yearMonth(2000, 12)},
    	};
    }
    
    @Test(dataProvider="goodParseData")
    public void test_successfulParse(String text, YearMonth expected) {
    	YearMonth yearMonth = YearMonth.parse(text);
    	assertEquals(yearMonth, expected);
    }
    
    @DataProvider(name="badParseData")
    Object[][] provider_badParseData() {
    	return new Object[][] {
    			{"", 0},
    			{"-00", 0},
    			{"2000-23", 5},
    			{"--01-0", 0},
    			{"A01-3", 0},
    			{"200-01", 0},
    			{"2009/12", 4},
    			{"2000-00", 5},
    			{"2000-13", 5},
    	};
    }
    
    @Test(dataProvider="badParseData", expectedExceptions=CalendricalParseException.class)
    public void test_failedParse(String text, int pos) {
    	try {
    		YearMonth.parse(text);
    		fail(String.format("Parse should have failed for %s at position %d", text, pos));
    	}
    	catch (CalendricalParseException ex) {
    		assertEquals(ex.getParsedString(), text);
    		assertEquals(ex.getErrorIndex(), pos);
    		throw ex;
    	}
    }
    
    @Test(expectedExceptions=NullPointerException.class)
    public void test_ParseNull() {
    	YearMonth.parse(null);
    }
}
