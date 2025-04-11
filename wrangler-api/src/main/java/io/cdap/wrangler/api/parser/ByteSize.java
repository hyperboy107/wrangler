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
 * This class represents a byte size token in the directives language.
 * It handles parsing of byte size values with units like KB, MB, GB, etc.
 */
public class ByteSize implements Token {
    private static final Pattern BYTE_SIZE_PATTERN = Pattern.compile(
            "^([-]?\\d+(?:\\.\\d+)?)\\s*(k|kb|m|mb|g|gb|t|tb|p|pb|b|byte|bytes)$",
            Pattern.CASE_INSENSITIVE);

    private final String rawValue;
    private final double value;
    private final String unit;
    private final long bytes;

    public ByteSize(String rawValue) {
        this.rawValue = rawValue;

        // Parse the byte size string
        Matcher matcher = BYTE_SIZE_PATTERN.matcher(rawValue.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid byte size format: " + rawValue);
        }

        this.value = Double.parseDouble(matcher.group(1));
        this.unit = matcher.group(2).toLowerCase();
        this.bytes = calculateBytes();
    }

    /**
     * Calculates the number of bytes based on the value and unit.
     *
     * @return number of bytes
     */
    private long calculateBytes() {
        switch (unit) {
            case "k":
            case "kb":
                return (long) (value * 1024);
            case "m":
            case "mb":
                return (long) (value * 1024 * 1024);
            case "g":
            case "gb":
                return (long) (value * 1024 * 1024 * 1024);
            case "t":
            case "tb":
                return (long) (value * 1024 * 1024 * 1024 * 1024);
            case "p":
            case "pb":
                return (long) (value * 1024 * 1024 * 1024 * 1024 * 1024);
            case "b":
            case "byte":
            case "bytes":
            default:
                return (long) value;
        }
    }

    /**
     * @return the value as a number of bytes
     */
    public long getBytes() {
        return bytes;
    }

    /**
     * @return the original numeric value before unit conversion
     */
    public double getValue() {
        return value;
    }

    /**
     * @return the unit part of the byte size (kb, mb, etc.)
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
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(rawValue);
    }
}