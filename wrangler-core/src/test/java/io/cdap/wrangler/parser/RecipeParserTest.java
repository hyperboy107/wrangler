package io.cdap.wrangler.parser;
 
 import org.antlr.v4.runtime.*;
 import org.antlr.v4.runtime.tree.ParseTree;
 import org.junit.Assert;
 import org.junit.Test;
 
 public class RecipeParserTest {
 
   @Test
   public void testByteSizeParsing() {
     String input = "limit-size 128MB;";
     ParseTree tree = parse(input);
     Assert.assertNotNull(tree);
     System.out.println("Parsed ByteSize input: " + input);
   }
 
   @Test
   public void testTimeDurationParsing() {
     String input = "wait : 30s;";
     ParseTree tree = parse(input);
     Assert.assertNotNull(tree);
     System.out.println("Parsed TimeDuration input: " + input);
   }
 
   private ParseTree parse(String input) {
     CharStream charStream = CharStreams.fromString(input);
     DirectivesLexer lexer = new DirectivesLexer(charStream);
     CommonTokenStream tokens = new CommonTokenStream(lexer);
     DirectivesParser parser = new DirectivesParser(tokens);
     return parser.recipe(); 
   }
 }