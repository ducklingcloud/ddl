/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 *
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
/*
  JSPWiki - a JSP-based WikiWiki clone.

  Copyright (C) 2001-2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 2.1 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package net.duckling.ddl.util;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;

/**
 * Contains a number of static utility methods.
 */
// FIXME3.0: Move to the "util" package
public final class TextUtil {
    static final String HEX_DIGITS = "0123456789ABCDEF";

    /**
     * Private constructor prevents instantiation.
     */
    private TextUtil() {
    }

    /**
     * java.net.URLEncoder.encode() method in JDK < 1.4 is buggy. This
     * duplicates its functionality.
     *
     * @param rs
     *            the string to encode
     * @return the URL-encoded string
     */
    protected static String urlEncode(byte[] rs) {
        StringBuffer result = new StringBuffer(rs.length * 2);

        // Does the URLEncoding. We could use the java.net one, but
        // it does not eat byte[]s.

        for (int i = 0; i < rs.length; i++) {
            char c = (char) rs[i];

            switch (c) {
                case '_':
                case '.':
                case '*':
                case '-':
                case '/':
                    result.append(c);
                    break;

                case ' ':
                    result.append('+');
                    break;

                default:
                    if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                        result.append(c);
                    } else {
                        result.append('%');
                        result.append(HEX_DIGITS.charAt((c & 0xF0) >> 4));
                        result.append(HEX_DIGITS.charAt(c & 0x0F));
                    }
            }

        } // for

        return result.toString();
    }

    /**
     * URL encoder does not handle all characters correctly. See <A HREF=
     * "http://developer.java.sun.com/developer/bugParade/bugs/4257115.html">
     * Bug parade, bug #4257115</A> for more information.
     * <P>
     * Thanks to CJB for this fix.
     *
     * @param bytes
     *            The byte array containing the bytes of the string
     * @param encoding
     *            The encoding in which the string should be interpreted
     * @return A decoded String
     *
     * @throws UnsupportedEncodingException
     *             If the encoding is unknown.
     * @throws IllegalArgumentException
     *             If the byte array is not a valid string.
     */
    protected static String urlDecode(byte[] bytes, String encoding) throws UnsupportedEncodingException,
            IllegalArgumentException {
        if (bytes == null) {
            return null;
        }

        byte[] decodeBytes = new byte[bytes.length];
        int decodedByteCount = 0;

        try {
            for (int count = 0; count < bytes.length; count++) {
                switch (bytes[count]) {
                    case '+':
                        decodeBytes[decodedByteCount++] = (byte) ' ';
                        break;

                    case '%':
                        decodeBytes[decodedByteCount++] = (byte) ((HEX_DIGITS.indexOf(bytes[++count]) << 4) + (HEX_DIGITS
                                                                                                               .indexOf(bytes[++count])));

                        break;

                    default:
                        decodeBytes[decodedByteCount++] = bytes[count];
                }
            }

        } catch (IndexOutOfBoundsException ae) {
            throw new IllegalArgumentException("Malformed UTF-8 string?");
        }

        String processedPageName = null;

        try {
            processedPageName = new String(decodeBytes, 0, decodedByteCount, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("UTF-8 encoding not supported on this platform");
        }

        return processedPageName;
    }


    /**
     * Replaces the relevant entities inside the String. All &amp; &gt;, &lt;,
     * and &quot; are replaced by their respective names.
     *
     * @since 1.6.1
     * @param src
     *            The source string.
     * @return The encoded string.
     */
    public static String replaceEntities(String src) {
        src = replaceString(src, "&", "&amp;");
        src = replaceString(src, "<", "&lt;");
        src = replaceString(src, ">", "&gt;");
        src = replaceString(src, "\"", "&quot;");

        return src;
    }

    /**
     * Replaces a string with an other string.
     *
     * @param orig
     *            Original string. Null is safe.
     * @param src
     *            The string to find.
     * @param dest
     *            The string to replace <I>src</I> with.
     * @return A string with the replacement done.
     */
    public static final String replaceString(String orig, String src, String dest) {
        if (orig == null) {
            return null;
        }
        if (src == null || dest == null) {
            throw new NullPointerException();
        }
        if (src.length() == 0) {
            return orig;
        }

        StringBuffer res = new StringBuffer(orig.length() + 20); // Pure
        // guesswork
        int start = 0;
        int end = 0;
        int last = 0;

        while ((start = orig.indexOf(src, end)) != -1) {
            res.append(orig.substring(last, start));
            res.append(dest);
            end = start + src.length();
            last = start + src.length();
        }

        res.append(orig.substring(end));

        return res.toString();
    }

    /**
     * Replaces a part of a string with a new String.
     *
     * @param start
     *            Where in the original string the replacing should start.
     * @param end
     *            Where the replacing should end.
     * @param orig
     *            Original string. Null is safe.
     * @param text
     *            The new text to insert into the string.
     * @return The string with the orig replaced with text.
     */
    public static String replaceString(String orig, int start, int end, String text) {
        if (orig == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer(orig);

        buf.replace(start, end, text);

        return buf.toString();
    }

    /**
     * Parses an integer parameter, returning a default value if the value is
     * null or a non-number.
     *
     * @param value
     *            The value to parse
     * @param defvalue
     *            A default value in case the value is not a number
     * @return The parsed value (or defvalue).
     */

    public static int parseIntParameter(String value, int defvalue) {
        int val = defvalue;

        try {
            val = Integer.parseInt(value.trim());
        } catch (Exception e) {
        }

        return val;
    }

    /**
     * Gets an integer-valued property from a standard Properties list. If the
     * value does not exist, or is a non-integer, returns defVal.
     *
     * @since 2.1.48.
     * @param props
     *            The property set to look through
     * @param key
     *            The key to look for
     * @param defVal
     *            If the property is not found or is a non-integer, returns this
     *            value.
     * @return The property value as an integer (or defVal).
     */
    public static int getIntegerProperty(Properties props, String key, int defVal) {
        String val = props.getProperty(key);

        return parseIntParameter(val, defVal);
    }

    /**
     * Gets a boolean property from a standard Properties list. Returns the
     * default value, in case the key has not been set.
     * <P>
     * The possible values for the property are "true"/"false", "yes"/"no", or
     * "on"/"off". Any value not recognized is always defined as "false".
     *
     * @param props
     *            A list of properties to search.
     * @param key
     *            The property key.
     * @param defval
     *            The default value to return.
     *
     * @return True, if the property "key" was set to "true", "on", or "yes".
     *
     * @since 2.0.11
     */
    public static boolean getBooleanProperty(Properties props, String key, boolean defval) {
        String val = props.getProperty(key);

        if (val == null) {
            return defval;
        }

        return isPositive(val);
    }

    /**
     * Fetches a String property from the set of Properties. This differs from
     * Properties.getProperty() in a couple of key respects: First, property
     * value is trim()med (so no extra whitespace back and front), and well,
     * that's it.
     *
     * @param props
     *            The Properties to search through
     * @param key
     *            The property key
     * @param defval
     *            A default value to return, if the property does not exist.
     * @return The property value.
     * @since 2.1.151
     */
    public static String getStringProperty(Properties props, String key, String defval) {
        String val = props.getProperty(key);

        if (val == null) {
            return defval;
        }

        return val.trim();
    }

    /**
     * Returns true, if the string "val" denotes a positive string. Allowed
     * values are "yes", "on", and "true". Comparison is case-insignificant.
     * Null values are safe.
     *
     * @param val
     *            Value to check.
     * @return True, if val is "true", "on", or "yes"; otherwise false.
     *
     * @since 2.0.26
     */
    public static boolean isPositive(String val) {
        if (val == null) {
            return false;
        }

        val = val.trim();

        return val.equalsIgnoreCase("true") || val.equalsIgnoreCase("on") || val.equalsIgnoreCase("yes");
    }

    /**
     * Makes sure that the POSTed data is conforms to certain rules. These rules
     * are:
     * <UL>
     * <LI>The data always ends with a newline (some browsers, such as NS4.x
     * series, does not send a newline at the end, which makes the diffs a bit
     * strange sometimes.
     * <LI>The CR/LF/CRLF mess is normalized to plain CRLF.
     * </UL>
     *
     * The reason why we're using CRLF is that most browser already return CRLF
     * since that is the closest thing to a HTTP standard.
     *
     * @param postData
     *            The data to normalize
     * @return Normalized data
     */
    public static String normalizePostData(String postData) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < postData.length(); i++) {
            switch (postData.charAt(i)) {
                case 0x0a: // LF, UNIX
                    sb.append("\r\n");
                    break;

                case 0x0d: // CR, either Mac or MSDOS
                    sb.append("\r\n");
                    // If it's MSDOS, skip the LF so that we don't add it again.
                    if (i < postData.length() - 1 && postData.charAt(i + 1) == 0x0a) {
                        i++;
                    }
                    break;

                default:
                    sb.append(postData.charAt(i));
                    break;
            }
        }

        if (sb.length() < 2 || !sb.substring(sb.length() - 2).equals("\r\n")) {
            sb.append("\r\n");
        }

        return sb.toString();
    }

    private static final int EOI = 0;
    private static final int LOWER = 1;
    private static final int UPPER = 2;
    private static final int DIGIT = 3;
    private static final int OTHER = 4;
    private static final Random RANDOM = new SecureRandom();

    private static int getCharKind(int c) {
        if (c == -1) {
            return EOI;
        }

        char ch = (char) c;

        if (Character.isLowerCase(ch)) {
            return LOWER;
        } else if (Character.isUpperCase(ch)) {
            return UPPER;
        } else if (Character.isDigit(ch)) {
            return DIGIT;
        } else {
            return OTHER;
        }
    }

    /**
     * Adds spaces in suitable locations of the input string. This is used to
     * transform a WikiName into a more readable format.
     *
     * @param s
     *            String to be beautified.
     * @return A beautified string.
     */
    public static String beautifyString(String s) {
        return beautifyString(s, " ");
    }

    /**
     * Adds spaces in suitable locations of the input string. This is used to
     * transform a WikiName into a more readable format.
     *
     * @param s
     *            String to be beautified.
     * @param space
     *            Use this string for the space character.
     * @return A beautified string.
     * @since 2.1.127
     */
    public static String beautifyString(String s, String space) {
        StringBuffer result = new StringBuffer();

        if (s == null || s.length() == 0) {
            return "";
        }

        int cur = s.charAt(0);
        int curKind = getCharKind(cur);

        int prevKind = LOWER;
        int nextKind = -1;

        int next = -1;
        int nextPos = 1;

        while (curKind != EOI) {
            next = (nextPos < s.length()) ? s.charAt(nextPos++) : -1;
            nextKind = getCharKind(next);

            if ((prevKind == UPPER) && (curKind == UPPER) && (nextKind == LOWER)) {
                result.append(space);
                result.append((char) cur);
            } else {
                result.append((char) cur);
                if (((curKind == UPPER) && (nextKind == DIGIT))
                    || ((curKind == LOWER) && ((nextKind == DIGIT) || (nextKind == UPPER)))
                    || ((curKind == DIGIT) && ((nextKind == UPPER) || (nextKind == LOWER)))) {
                    result.append(space);
                }
            }
            prevKind = curKind;
            cur = next;
            curKind = nextKind;
        }

        return result.toString();
    }

    /**
     * Creates a Properties object based on an array which contains
     * alternatively a key and a value. It is useful for generating default
     * mappings. For example:
     *
     * <pre>
     *     String[] properties = { "jspwiki.property1", "value1",
     *                             "jspwiki.property2", "value2 };
     *
     *     Properties props = TextUtil.createPropertes( values );
     *
     *     System.out.println( props.getProperty("jspwiki.property1") );
     * </pre>
     *
     * would output "value1".
     *
     * @param values
     *            Alternating key and value pairs.
     * @return Property object
     * @see java.util.Properties
     * @throws IllegalArgumentException
     *             if the property array is missing a value for a key.
     * @since 2.2.
     */

    public static Properties createProperties(String[] values) throws IllegalArgumentException {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("One value is missing.");
        }
        Properties props = new Properties();

        for (int i = 0; i < values.length; i += 2) {
            props.setProperty(values[i], values[i + 1]);
        }

        return props;
    }

    /**
     * Counts the number of sections (separated with "----") from the page.
     *
     * @param pagedata
     *            The WikiText to parse.
     * @return int Number of counted sections.
     * @since 2.1.86.
     */

    public static int countSections(String pagedata) {
        int tags = 0;
        int start = 0;

        while ((start = pagedata.indexOf("----", start)) != -1) {
            tags++;
            start += 4; // Skip this "----"
        }

        //
        // The first section does not get the "----"
        //
        return pagedata.length() > 0 ? tags + 1 : 0;
    }

    /**
     * Gets the given section (separated with "----") from the page text. Note
     * that the first section is always #1. If a page has no section markers,
     * them there is only a single section, #1.
     *
     * @param pagedata
     *            WikiText to parse.
     * @param section
     *            Which section to get.
     * @return String The section.
     * @throws IllegalArgumentException
     *             If the page does not contain this many sections.
     * @since 2.1.86.
     */
    public static String getSection(String pagedata, int section) throws IllegalArgumentException {
        int tags = 0;
        int start = 0;
        int previous = 0;

        while ((start = pagedata.indexOf("----", start)) != -1) {
            if (++tags == section) {
                return pagedata.substring(previous, start);
            }

            start += 4; // Skip this "----"

            previous = start;
        }

        if (++tags == section) {
            return pagedata.substring(previous);
        }

        throw new IllegalArgumentException("There is no section no. " + section + " on the page.");
    }

    /**
     * A simple routine which just repeates the arguments. This is useful for
     * creating something like a line or something.
     *
     * @param what
     *            String to repeat
     * @param times
     *            How many times to repeat the string.
     * @return Guess what?
     * @since 2.1.98.
     */
    public static String repeatString(String what, int times) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < times; i++) {
            sb.append(what);
        }

        return sb.toString();
    }

    /**
     * Converts a string from the Unicode representation into something that can
     * be embedded in a java properties file. All references outside the ASCII
     * range are replaced with \\uXXXX.
     *
     * @param s
     *            The string to convert
     * @return the ASCII string
     */
    public static String native2Ascii(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char aChar = s.charAt(i);
            if ((aChar < 0x0020) || (aChar > 0x007e)) {
                sb.append('\\');
                sb.append('u');
                sb.append(toHex((aChar >> 12) & 0xF));
                sb.append(toHex((aChar >> 8) & 0xF));
                sb.append(toHex((aChar >> 4) & 0xF));
                sb.append(toHex(aChar & 0xF));
            } else {
                sb.append(aChar);
            }
        }
        return sb.toString();
    }

    private static char toHex(int nibble) {
        final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        return hexDigit[nibble & 0xF];
    }

    /**
     * Generates a hexadecimal string from an array of bytes. For example, if
     * the array contains { 0x01, 0x02, 0x3E }, the resulting string will be
     * "01023E".
     *
     * @param bytes
     *            A Byte array
     * @return A String representation
     * @since 2.3.87
     */
    public static String toHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(toHex(bytes[i] >> 4));
            sb.append(toHex(bytes[i]));
        }

        return sb.toString();
    }

    /**
     * Returns true, if the argument contains a number, otherwise false. In a
     * quick test this is roughly the same speed as Integer.parseInt() if the
     * argument is a number, and roughly ten times the speed, if the argument is
     * NOT a number.
     *
     * @since 2.4
     * @param s
     *            String to check
     * @return True, if s represents a number. False otherwise.
     */

    public static boolean isNumber(String s) {
        if (s == null) {
            return false;
        }

        if (s.length() > 1 && s.charAt(0) == '-') {
            s = s.substring(1);
        }

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /** Length of password. @see #generateRandomPassword() */
    public static final int PASSWORD_LENGTH = 8;

    /**
     * Generate a random String suitable for use as a temporary password.
     *
     * @return String suitable for use as a temporary password
     * @since 2.4
     */
    public static String generateRandomPassword() {
        // Pick from some letters that won't be easily mistaken for each
        // other. So, for example, omit o O and 0, 1 l and L.
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

        String pw = "";
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            pw += letters.substring(index, index + 1);
        }
        return pw;
    }
}
