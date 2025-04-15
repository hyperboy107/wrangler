// package io.cdap.directives.aggregates;
 
 // import io.cdap.wrangler.api.Arguments;
 // import io.cdap.wrangler.api.ExecutorContext;
 // import io.cdap.wrangler.api.Row;
 // import io.cdap.wrangler.api.parser.Token;
 // import org.junit.Before;
 // import org.junit.Test;
 // import org.mockito.Mockito;
 
 // import java.util.Arrays;
 // import java.util.List;
 
 // import static org.junit.Assert.*;
 
 // public class AggregateSizeTimeTest {
 
 //     private AggregateSizeTime directive;
 //     private Arguments arguments;
 //     private ExecutorContext context;
 
 //     @Before
 //     public void setUp() {
 //         directive = new AggregateSizeTime();
 
 //         arguments = Mockito.mock(Arguments.class);
 //         context = Mockito.mock(ExecutorContext.class);
 
 //         Mockito.when(arguments.value("srcSizeCol")).thenReturn(mockToken("inputSize"));
 //         Mockito.when(arguments.value("srcTimeCol")).thenReturn(mockToken("inputTime"));
 //         Mockito.when(arguments.value("dstSizeCol")).thenReturn(mockToken("outputSize"));
 //         Mockito.when(arguments.value("dstTimeCol")).thenReturn(mockToken("outputTime"));
 //         Mockito.when(arguments.contains("outputSizeUnit")).thenReturn(true);
 //         Mockito.when(arguments.value("outputSizeUnit")).thenReturn(mockToken("MB"));
 //         Mockito.when(arguments.contains("aggregationType")).thenReturn(true);
 //         Mockito.when(arguments.value("aggregationType")).thenReturn(mockToken("avg"));
 
 //         directive.initialize(arguments);
 //     }
 
 //     private Token mockToken(String value) {
 //         Token token = Mockito.mock(Token.class);
 //         Mockito.when(token.value()).thenReturn(value);
 //         return token;
 //     }
     
 //     @Test
 //     public void testExecute_AvgAggregation_MB() {
 //         List<Row> rows = Arrays.asList(
 //                 new Row().add("inputSize", "1048576").add("inputTime", "1000ms"), // 1 MB
 //                 new Row().add("inputSize", "2097152").add("inputTime", "3000ms") // 2 MB
 //         );
 
 //         List<Row> result = directive.execute(rows, context);
 
 //         assertEquals(1, result.size());
 
 //         Row outputRow = result.get(0);
 //         double outputSize = (double) outputRow.getValue("outputSize");
 //         double outputTime = (double) outputRow.getValue("outputTime");
 
 //         assertEquals(1.5, outputSize, 0.01); // avg of 1MB and 2MB
 //         assertEquals(2000.0, outputTime, 0.01); // avg of 1000 and 3000
 //     }
 // }
 
 package io.cdap.directives.aggregates;
 
 import io.cdap.wrangler.api.Arguments;
 import io.cdap.wrangler.api.ExecutorContext;
 import io.cdap.wrangler.api.Row;
 import io.cdap.wrangler.api.parser.Token;
 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.Mockito;
 
 import java.util.Arrays;
 import java.util.List;
 
 import static org.junit.Assert.*;
 
 public class AggregateSizeTimeTest {
 
     private AggregateSizeTime directive;
     private Arguments arguments;
     private ExecutorContext context;
 
     @Before
     public void setUp() {
         directive = new AggregateSizeTime();
         arguments = Mockito.mock(Arguments.class);
         context = Mockito.mock(ExecutorContext.class);
 
         // Use your actual implementation's method of handling arguments
         Token inputSizeToken = mockToken("inputSize");
         Token inputTimeToken = mockToken("inputTime");
         Token outputSizeToken = mockToken("outputSize");
         Token outputTimeToken = mockToken("outputTime");
         Token unitToken = mockToken("MB");
         Token aggregationToken = mockToken("avg");
 
         Mockito.when(arguments.value("srcSizeCol")).thenReturn(inputSizeToken);
         Mockito.when(arguments.value("srcTimeCol")).thenReturn(inputTimeToken);
         Mockito.when(arguments.value("dstSizeCol")).thenReturn(outputSizeToken);
         Mockito.when(arguments.value("dstTimeCol")).thenReturn(outputTimeToken);
         Mockito.when(arguments.contains("outputSizeUnit")).thenReturn(true);
         Mockito.when(arguments.value("outputSizeUnit")).thenReturn(unitToken);
         Mockito.when(arguments.contains("aggregationType")).thenReturn(true);
         Mockito.when(arguments.value("aggregationType")).thenReturn(aggregationToken);
 
         directive.initialize(arguments);
     }
 
     private Token mockToken(String value) {
         Token token = Mockito.mock(Token.class);
         Mockito.when(token.toString()).thenReturn(value);
         return token;
     }
     
     @Test
     public void testExecute_AvgAggregation_MB() {
         // Create test rows with proper format for ByteSize and TimeDuration
         Row row1 = new Row();
         row1.add("inputSize", "1MB"); // Correctly formatted size with unit
         row1.add("inputTime", "1000ms");
         
         Row row2 = new Row();
         row2.add("inputSize", "2MB"); // Correctly formatted size with unit
         row2.add("inputTime", "3000ms");
         
         List<Row> rows = Arrays.asList(row1, row2);
 
         // Execute the directive
         List<Row> result = directive.execute(rows, context);
 
         // Verify result
         assertNotNull("Result should not be null", result);
         assertEquals("Should produce exactly one row", 1, result.size());
 
         Row outputRow = result.get(0);
         
         // Get output values
         try {
             Object sizeValue = outputRow.getValue("outputSize");
             Object timeValue = outputRow.getValue("outputTime");
             
             assertNotNull("outputSize should not be null", sizeValue);
             assertNotNull("outputTime should not be null", timeValue);
             
             double outputSize = ((Number) sizeValue).doubleValue();
             double outputTime = ((Number) timeValue).doubleValue();
             
             assertEquals("Average size should be 1.5 MB", 1.5, outputSize, 0.01);
             assertEquals("Average time should be 2000 ms", 2000.0, outputTime, 0.01);
         } catch (Exception e) {
             fail("Failed to access output values: " + e.getMessage());
         }
     }
 }