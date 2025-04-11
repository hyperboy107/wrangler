package io.cdap.wrangler.api.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple visitor to validate and extract arguments based on UsageDefinition.
 */
public class TokensVisitor {

  private final UsageDefinition usage;

  public TokensVisitor(UsageDefinition usage) {
    this.usage = usage;
  }

  public Arguments visit(List<String> tokens) throws DirectiveParseException {
    List<TokenDefinition> expectedTokens = usage.getTokens();
    List<Argument> args = new ArrayList<>();

    int requiredCount = expectedTokens.size() - usage.getOptionalTokensCount();
    if (tokens.size() < requiredCount || tokens.size() > expectedTokens.size()) {
      throw new DirectiveParseException("Invalid number of arguments for directive: " + usage.getDirectiveName());
    }

    for (int i = 0; i < expectedTokens.size(); i++) {
      TokenDefinition expected = expectedTokens.get(i);

      String value = i < tokens.size() ? tokens.get(i) : null;

      if (value == null && !expected.optional()) {
        throw new DirectiveParseException("Missing required argument: " + expected.name());
      }

      args.add(new Argument(expected.name(), value));
    }

    return new Arguments(args);
  }
}
