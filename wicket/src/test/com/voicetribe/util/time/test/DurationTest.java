///////////////////////////////////////////////////////////////////////////////////
//
// Created Jun 2, 2004
//
// Copyright 2004, Jonathan W. Locke
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.voicetribe.util.time.test;

import java.text.NumberFormat;
import junit.framework.Assert;
import junit.framework.TestCase;

import com.voicetribe.util.string.StringValueConversionException;
import com.voicetribe.util.time.Duration;


/**
 * Test cases for this object
 * @author Jonathan Locke
 */
public final class DurationTest extends TestCase
{
    public void testValues() throws StringValueConversionException
    {
        Assert.assertEquals(Duration.milliseconds(3000), Duration.seconds(3));
        Assert.assertEquals(Duration.seconds(120), Duration.minutes(2));
        Assert.assertEquals(Duration.minutes(1440), Duration.hours(24));
        Assert.assertEquals(Duration.hours(48), Duration.days(2));
        Assert.assertEquals(Duration.minutes(90), Duration.valueOf("90 minutes"));
        Assert.assertEquals(Duration.days(9), Duration.valueOf("9 days"));
        Assert.assertEquals(Duration.hours(1), Duration.valueOf("1 hour"));
        Assert.assertTrue(9 == Duration.days(9).getDays());
        Assert.assertTrue(11 == Duration.hours(11).getHours());
        Assert.assertTrue(21 == Duration.minutes(21).getMinutes());
        Assert.assertTrue(51 == Duration.seconds(51).getSeconds());
    }

    public void testOperations()
    {
        Assert.assertTrue(Duration.milliseconds(3001).greaterThan(Duration.seconds(3)));
        Assert.assertTrue(Duration.milliseconds(2999).lessThan(Duration.seconds(3)));
        Assert.assertEquals(-1, Duration.milliseconds(2999).compareTo(Duration.seconds(3)));
        Assert.assertEquals(1, Duration.milliseconds(3001).compareTo(Duration.seconds(3)));
        Assert.assertEquals(0, Duration.milliseconds(3000).compareTo(Duration.seconds(3)));
        Assert.assertEquals(Duration.minutes(10), Duration.minutes(4).add(Duration.minutes(6)));
        Assert.assertEquals(Duration.ONE_HOUR, Duration.minutes(90).subtract(Duration.minutes(30)));
        String value = NumberFormat.getNumberInstance().format(1.5);
        Assert.assertEquals(value + " minutes", Duration.seconds(90).toString());
        Assert.assertEquals("12 hours", Duration.days(0.5).toString());
    }

    public void testSleep()
    {
        Assert.assertTrue(Duration.seconds(0.5).lessThan(Duration.benchmark(new Runnable()
        {
            public void run()
            {
                Duration.seconds(1.5).sleep();
            }
        })));

        Assert.assertTrue(Duration.seconds(1).greaterThan(Duration.benchmark(new Runnable()
        {
            public void run()
            {
                Duration.hours(-1).sleep();
            }
        })));
    }
}
///////////////////////////////// End of File /////////////////////////////////
