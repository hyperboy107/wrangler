package io.cdap.wrangler.api;

/**
 * Enum to categorize directive types (e.g., transformation, aggregation).
 */
public enum DirectiveType {
  TRANSFORMATION,
  FILTER,
  VALIDATION,
  AGGREGATE,
  OTHER
}
