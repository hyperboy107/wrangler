package io.cdap.directives.aggregates;
 
 import io.cdap.wrangler.api.Arguments;
 import io.cdap.wrangler.api.Directive;
 import io.cdap.wrangler.api.ExecutorContext;
 import io.cdap.wrangler.api.Row;
 import io.cdap.wrangler.api.annotations.Description;
 import io.cdap.wrangler.api.annotations.Name;
 import io.cdap.wrangler.api.annotations.Syntax;
 import io.cdap.wrangler.api.parser.ByteSize;
 import io.cdap.wrangler.api.parser.TimeDuration;
 import io.cdap.wrangler.api.parser.UsageDefinition;
 import io.cdap.wrangler.api.parser.TokenType;
 
 import java.util.ArrayList;
 import java.util.List;
 
 @Name("aggregate-size-time")
 @Description("Aggregates byte sizes and time durations into target columns, supports total or average")
 @Syntax("aggregate-size-time <srcSizeCol> <srcTimeCol> <dstSizeCol> <dstTimeCol> [MB|GB] [avg|total]")
 public class AggregateSizeTime implements Directive {
 
     private String srcSizeCol;
     private String srcTimeCol;
     private String dstSizeCol;
     private String dstTimeCol;
     private String unitSize = "B";
     private String aggregationType = "total";
 
     private long totalBytes = 0;
     private long totalDurationMs = 0;
     private long count = 0;
 
     @Override
     public UsageDefinition define() {
         return UsageDefinition.of("srcSizeCol", TokenType.COLUMN_NAME, "Source column for byte size")
                 .with("srcTimeCol", TokenType.COLUMN_NAME, "Source column for time duration")
                 .with("dstSizeCol", TokenType.COLUMN_NAME, "Target column for aggregated size")
                 .with("dstTimeCol", TokenType.COLUMN_NAME, "Target column for aggregated time")
                 .with("outputSizeUnit", TokenType.TEXT, "Optional output unit (e.g. MB, GB)")
                 .with("aggregationType", TokenType.TEXT, "Optional aggregation type (avg or total)");
     }
 
     @Override
     public void initialize(Arguments args) {
         this.srcSizeCol = String.valueOf(args.value("srcSizeCol"));
         this.srcTimeCol = String.valueOf(args.value("srcTimeCol"));
         this.dstSizeCol = String.valueOf(args.value("dstSizeCol"));
         this.dstTimeCol = String.valueOf(args.value("dstTimeCol"));
 
         if (args.contains("outputSizeUnit")) {
             this.unitSize = String.valueOf(args.value("outputSizeUnit")).toUpperCase();
         }
         if (args.contains("aggregationType")) {
             this.aggregationType = String.valueOf(args.value("aggregationType")).toLowerCase();
         }
     }
 
     @Override
     public List<Row> execute(List<Row> rows, ExecutorContext context) {
         for (Row row : rows) {
             Object sizeObj = row.getValue(srcSizeCol);
             Object timeObj = row.getValue(srcTimeCol);
 
             if (sizeObj != null && timeObj != null) {
                 long sizeInBytes = new ByteSize(sizeObj.toString()).getBytes();
                 long timeInMs = new TimeDuration(timeObj.toString()).getMilliseconds();
 
                 totalBytes += sizeInBytes;
                 totalDurationMs += timeInMs;
                 count++;
             }
         }
 
         double sizeResult = aggregationType.equals("avg") ? (double) totalBytes / count : totalBytes;
         double timeResult = aggregationType.equals("avg") ? (double) totalDurationMs / count : totalDurationMs;
 
         if (unitSize.equalsIgnoreCase("MB")) {
             sizeResult /= (1024.0 * 1024);
         } else if (unitSize.equalsIgnoreCase("GB")) {
             sizeResult /= (1024.0 * 1024 * 1024);
         }
 
         List<Row> result = new ArrayList<>();
         result.add(new Row()
                 .add(dstSizeCol, sizeResult)
                 .add(dstTimeCol, timeResult));
         return result;
     }
 
     @Override
     public void destroy() {
         // No resource cleanup needed here
     }
 }