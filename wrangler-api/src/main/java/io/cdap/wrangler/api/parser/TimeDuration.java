/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a time duration token in the directives language.
 * It handles parsing of time duration values with units like ms, s, min, hour,
 * etc.
 */
public class TimeDuration implements Token {
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "^([-]?\\d+(?:\\.\\d+)?)\\s*(ms|s|m|min|h|hour|hours|d|day|days|w|week|weeks|month|months|y|year|years)$",
            Pattern.CASE_INSENSITIVE);

    private final String rawValue;
    private final double value;
    private final String unit;
    private final long milliseconds;

    public TimeDuration(String rawValue) {
        this.rawValue = rawValue;

        // Parse the time duration string
        Matcher matcher = TIME_PATTERN.matcher(rawValue.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time duration format: " + rawValue);
        }

        this.value = Double.parseDouble(matcher.group(1));
        this.unit = matcher.group(2).toLowerCase();
        this.milliseconds = calculateMilliseconds();
    }

    /**
     * Calculates the number of milliseconds based on the value and unit.
     *
     * @return number of milliseconds
     */
    private long calculateMilliseconds() {
        switch (unit) {
            case "ms":
                return (long) value;
            case "s":
                return (long) (value * 1000);
            case "m":
            case "min":
                return (long) (value * 60 * 1000);
            case "h":
            case "hour":
            case "hours":
                return (long) (value * 60 * 60 * 1000);
            case "d":
            case "day":
            case "days":
                return (long) (value * 24 * 60 * 60 * 1000);
            case "w":
            case "week":
            case "weeks":
                return (long) (value * 7 * 24 * 60 * 60 * 1000);
            case "month":
            case "months":
                // Approximating a month as 30 days
                return (long) (value * 30 * 24 * 60 * 60 * 1000);
            case "y":
            case "year":
            case "years":
                // Approximating a year as 365 days
                return (long) (value * 365 * 24 * 60 * 60 * 1000);
            default:
                return (long) value;
        }
    }

    /**
     * @return the value as a number of milliseconds
     */
    public long getMilliseconds() {
        return milliseconds;
    }

    /**
     * @return the value as a number of seconds
     */
    public double getSeconds() {
        return milliseconds / 1000.0;
    }

    /**
     * @return the original numeric value before unit conversion
     */
    public double getValue() {
        return value;
    }

    /**
     * @return the unit part of the time duration (ms, s, min, etc.)
     */
    public String getUnit() {
        return unit;
    }

    @Override
    public Object value() {
        return rawValue;
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(rawValue);
    }
}