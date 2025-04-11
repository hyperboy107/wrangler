package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

public class ByteSizeTest {

  @Test
  public void testValidByteSizes() {
    ByteSize size = new ByteSize("2kb");
    Assert.assertEquals(2048, size.getBytes());

    size = new ByteSize("1.5mb");
    Assert.assertEquals(1572864, size.getBytes());

    size = new ByteSize("100b");
    Assert.assertEquals(100, size.getBytes());

    size = new ByteSize("2kb");
    Assert.assertEquals(2048, size.getBytes()); 
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidByteSize() {
    new ByteSize("10monkeys"); // should throw exception
  }
}
