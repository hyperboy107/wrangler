package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

public class TimeDurationTest {

  @Test
  public void testValidDurations() {
    TimeDuration duration = new TimeDuration("2h");
    Assert.assertEquals(7200000L, duration.getMilliseconds());

    duration = new TimeDuration("1.5days");
    Assert.assertEquals(129600000L, duration.getMilliseconds());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDuration() {
    new TimeDuration("forever"); // should throw exception
  }
}
