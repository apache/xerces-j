/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.xerces.impl.xpath.regex;

import java.text.CharacterIterator;
import java.util.Locale;
import java.util.Stack;

import org.apache.xerces.util.IntStack;

/**
 * A regular expression matching engine using Non-deterministic Finite Automaton (NFA).
 * This engine does not conform to the POSIX regular expression.
 *
 * <h1>How to use</h1>
 *
 * <dl>
 *   <dt>A. Standard way
 *   <dd>
 * <pre>
 * {@code
 * RegularExpression re = new RegularExpression(regex);
 * if (re.matches(text)) { ... }
 * }
 * </pre>
 *
 *   <dt>B. Capturing groups
 *   <dd>
 * <pre>
 * {@code
 * RegularExpression re = new RegularExpression(regex);
 * Match match = new Match();
 * if (re.matches(text, match)) {
 *     ... // You can refer captured texts with methods of the <code>Match</code> class.
 * }
 * }
 * </pre>
 *
 * </dl>
 *
 * <h2>Case-insensitive matching</h2>
 * <pre>
 * {@code
 * RegularExpression re = new RegularExpression(<var>regex</var>, "i");
 * if (re.matches(text) >= 0) { ...}
 * }
 * </pre>
 *
 * <h2>Options</h2>
 * <p>You can specify options to {@link #RegularExpression(String, String)} or {@link #setPattern(String, String)}.</p>
 * <p>This <code>options</code> parameter consists of the following characters:</p>
 * <ul>
 *   <li><code>i</code> : This option indicates case-insensitive matching.</li>
 *   <li><code>m</code> : <code>^</code> and <code>$</code> consider the EOL characters within the text.</li>
 *   <li><code>s</code> : <code>.</code> matches any one character.</li>
 *   <li><code>u</code> : Redefines <code>\d \D \w \W \s \S \b \B \&lt; \></code> as being Unicode.</li>
 *   <li><code>w</code> : With this option, <code>\b \B \&lt; \></code> are processed with the method of 'Unicode Regular Expression Guidelines' Revision 4. When "w" and "u" are specified at the same time, <code>\b \B \&lt; \></code> are processed for the "w" option.</li>
 *   <li><code>,</code> : The parser treats a comma in a character class as a range separator.
 *   <ul>
 *       <li><code>[a,b]</code> matches <code>a</code> or <code>,</code> or <code>b</code> without this option.</li>
 *       <li><code>[a,b]</code> matches <code>a</code> or <code>b</code> with this option.</li>
 *   </ul>
 *   </li>
 *   <li><code>X</code> : With this option, the engine conforms to <a href="https://www.w3.org/TR/2000/WD-xmlschema-2-20000407/#regexs">XML Schema: Regular Expression</a>. The <code>match()</code> method does not do substring matching but entire string matching.</li>
 * </ul>
 *
 * <h1>Syntax</h1>
 *
 * <h2>Differences from Perl 5 regular expression</h2>
 * <ul>
 *  <li>There is 6-digit hexadecimal character representation (<code>\vHHHHHH</code>).
 *  <li>Supports subtraction, union, and intersection operations for character classes.
 *  <li>Not supported:
 *  <ul>
 *    <li><code>\ooo</code> (Octal character representations)</li>
 *    <li><code>\G</code>, <code>\C</code>, <code>\lc</code></li>
 *    <li><code>\u005cuc</code>, <code>\L</code>, <code>\U</code></li>
 *    <li><code>\E</code>, <code>\Q</code>, <code>\N{name}</code></li>
 *    <li><code>(?{code})</code>, <code>(??{code})</code></li>
 *  </ul>
 * </ul>
 *
 * <p>Meta characters are <code>. * + ? { [ ( ) | \ ^ $</code>.</p>
 * <ul>
 *   <li>Character
 *     <dl>
 *       <dt class="REGEX"><code>.</code> (A period)
 *       <dd>Matches any one character except the following characters.
 *       <dd>LINE FEED (U+000A), CARRIAGE RETURN (U+000D), PARAGRAPH SEPARATOR (U+2029), LINE SEPARATOR (U+2028)
 *       <dd>This expression matches one code point in Unicode. It can match a pair of surrogates.
 *       <dd>When <a href="#S_OPTION">the "s" option</a> is specified,
 *           it matches any character including the above four characters.
 *
 *       <dt class="REGEX"><code>\e \f \n \r \t</code>
 *       <dd>Matches ESCAPE (U+001B), FORM FEED (U+000C), LINE FEED (U+000A),
 *           CARRIAGE RETURN (U+000D), HORIZONTAL TABULATION (U+0009)
 *
 *       <dt class="REGEX"><code>\cC</code>
 *       <dd>Matches a control character.
 *           The <var>C</var> must be one of '<code>@</code>', '<code>A</code>'-'<code>Z</code>',
 *           '<code>[</code>', '<code>\</code>', '<code>]</code>', '<code>^</code>', '<code>_</code>'.
 *           It matches a control character of which the character code is less than the character code of
 *           the <var>C</var> by 0x0040.
 *       <dd class="REGEX">For example, a <code>\cJ</code> matches a LINE FEED (U+000A),
 *           and a <code>\c[</code> matches an ESCAPE (U+001B).
 *
 *       <dt class="REGEX">a non-meta character
 *       <dd>Matches the character.
 *
 *       <dt class="REGEX"><code>\</code> + a meta character
 *       <dd>Matches the meta character.
 *
 *       <dt class="REGEX"><code>\xHH</code> <code>\x{HHHH}</code>
 *       <dd>Matches a character of which code point is <var>HH</var> (Hexadecimal) in Unicode. You can write
 *           just 2 digits for <code>\xHH</code>, and variable length digits for <code>\x{HHHH}</code>.
 *
 *       <dt><code>\vHHHHHH</code>
 *       <dd>Matches a character of which code point is <var>HHHHHH</var> (Hexadecimal) in Unicode.
 *
 *       <dt class="REGEX"><code>\g</code>
 *       <dd>Matches a grapheme.
 *       <dd class="REGEX">It is equivalent to <code>(?[\p{ASSIGNED}]-[\p{M}\p{C}])?(?:\p{M}|[\x{094D}\x{09CD}\x{0A4D}\x{0ACD}\x{0B3D}\x{0BCD}\x{0C4D}\x{0CCD}\x{0D4D}\x{0E3A}\x{0F84}]\p{L}|[\x{1160}-\x{11A7}]|[\x{11A8}-\x{11FF}]|[\x{FF9E}\x{FF9F}])*</code>
 *
 *       <dt class="REGEX"><code>\X</code>
 *       <dd class="REGEX">Matches a combining character sequence. It is equivalent to <code>(?:\PM\pM*)</code>
 *     </dl>
 *   </li>
 *
 *   <li>Character class
 *     <dl>
+ *      <dt class="REGEX">[<var>R<sub>1</sub></var><var>R<sub>2</sub></var><var>...</var><var>R<sub>n</sub></var>] (without a {@link #SPECIAL_COMMA} option)</dt>
+ *      <dt class="REGEX">[<var>R<sub>1</sub></var>,<var>R<sub>2</sub></var>,<var>...</var>,<var>R<sub>n</sub></var>] (with a {@link #SPECIAL_COMMA} option)</dt>
 *       <dd>Positive character class.  It matches a character in ranges.
 *       <dd><var>R<sub>n</sub></var>:
 *       <ul>
 *         <li class="REGEX">A character (including <code>\e \f \n \r \t \xHH \x{HHHH} \vHHHHHH</code>)
 *             <p>This range matches the character.</p>
 *         </li>
 *         <li class="REGEX"><var>C<sub>1</sub></var>-<var>C<sub>2</sub></var>
 *             <p>This range matches a character which has a code point that is >= <var>C1</var>'s code point and &lt;= <var>C2</var>'s code point.</p>
 *         </li>
+ *        <li class="REGEX">A POSIX character class: <code>[:alpha:] [:alnum:] [:ascii:] [:cntrl:] [:digit:] [:graph:] [:lower:] [:print:] [:punct:] [:space:] [:upper:] [:xdigit:]</code>,
+ *             and negative POSIX character classes in Perl like <code>[:^alpha:]</code></li>
 *         <li class="REGEX"><code>\d \D \s \S \w \W \p{name} \P{name}</code>
 *             <p>These expressions specify the same ranges as the following expressions.</p>
 *         </li>
 *       </ul>
 *       <p>Enumerated ranges are merged (union operation). <code>[a-ec-z]</code> is equivalent to <code>[a-z]</code></p>
 *       </dd>
 *
 *       <dt class="REGEX">[^<var>R<sub>1</sub></var><var>R<sub>2</sub></var><var>...</var><var>R<sub>n</sub></var>] (without a {@link #SPECIAL_COMMA} option)</dt>
 *       <dt class="REGEX">[^<var>R<sub>1</sub></var>,<var>R<sub>2</sub></var>,<var>...</var>,<var>R<sub>n</sub></var>] (with a {@link #SPECIAL_COMMA} option)</dt>
 *       <dd>Negative character class. It matches a character not in ranges.</dd>
 *
 *       <dt class="REGEX"><code>(?[ranges]op[ranges]op[ranges] ... )</code>
 *       (where <var>op</var> is <code>-</code>, <code>+</code> or <code>&amp;</code>.)
 *       </dt>
 *       <dd>Subtraction or union or intersection for character classes.
 *       <p>For example, <code>(?[A-Z]-[CF])</code> is equivalent to <code>[A-BD-EG-Z]</code>, and <code>(?[0x00-0x7f]-[K]&amp;[\p{Lu}])</code> is equivalent to <code>[A-JL-Z]</code>.</p>
 *       <p>The result of this operation is a <u>positive character class</u>
 *           even if an expression includes any negative character classes.
 *           You have to take care of this in case-insensitive matching.
 *           For instance, <code>(?[^b])</code> is equivalent to <code>[\x00-ac-\x{10ffff}]</code>,
 *           which is equivalent to <code>[^b]</code> in case-sensitive matching.
 *           But, in case-insensitive matching, <code>(?[^b])</code> matches any character because
 *           it includes '<code>B</code>' and '<code>B</code>' matches '<code>b</code>'
 *           though <code>[^b]</code> is processed as <code>[^Bb]</code>.</p>
 *       </dd>
 *
 *       <dt class="REGEX">[<var>R<sub>1</sub></var><var>R<sub>2</sub></var><var>...</var>-[<var>R<sub>n</sub></var><var>R<sub>n+1</sub>...</var>]] (with an <code>X</code> option; {@link #XMLSCHEMA_MODE})</dt>
 *       <dd>Character class subtraction for the XML Schema.
 *           You can use this syntax when you specify an <code>X</code> option ({@link #XMLSCHEMA_MODE}).
 *       </dd>
 *
 *       <dt class="REGEX"><code>\d</code></dt>
 *       <dd class="REGEX">Equivalent to <code>[0-9]</code>.
 *       <p>When a <code>u</code> ({@link #USE_UNICODE_CATEGORY}) option is set, it is equivalent to
 *           <code>\p{Nd}</code>.</p>
 *       </dd>
 *
 *       <dt class="REGEX"><code>\D</code></dt>
 *       <dd class="REGEX">Equivalent to <code>[^0-9]</code>
 *       <p>When a <code>u</code> ({@link #USE_UNICODE_CATEGORY}) option is set, it is equivalent to
 *           <code>\P{Nd}</code>.</p>
 *       </dd>
 *
 *       <dt class="REGEX"><code>\s</code></dt>
 *       <dd class="REGEX">Equivalent to <code>[ \f\n\r\t]</code>
 *       <dd>When a <code>u</code> ({@link #USE_UNICODE_CATEGORY}) option is set, it is equivalent to
 *           <code>[ \f\n\r\t\p{Z}]</code>.
 *
 *       <dt class="REGEX"><code>\S</code></dt>
 *       <dd class="REGEX">Equivalent to <code>[^ \f\n\r\t]</code>
 *       <p>When a <code>u</code> ({@link #USE_UNICODE_CATEGORY}) option is set, it is equivalent to
 *           <code>[^ \f\n\r\t\p{Z}]</code>.</p>
 *       </dd>
 *
 *       <dt class="REGEX"><code>\w</code></dt>
 *       <dd class="REGEX">Equivalent to <code>[a-zA-Z0-9_]</code>
 *       <p>When a <code>u</code> ({@link #USE_UNICODE_CATEGORY}) option is set, it is equivalent to
 *           <code>[\p{Lu}\p{Ll}\p{Lo}\p{Nd}_]</code>.</p>
 *       </dd>
 *
 *       <dt class="REGEX"><code>\W</code></dt>
 *       <dd class="REGEX">Equivalent to <code>[^a-zA-Z0-9_]</code>
 *       <p>When a <code>u</code> ({@link #USE_UNICODE_CATEGORY}) option is set, it is equivalent to
 *           <code>[^\p{Lu}\p{Ll}\p{Lo}\p{Nd}_]</code>.</p>
 *       </dd>
 *
 *       <dt class="REGEX"><code>\p{name}</code></dt>
 *       <dd>Matches one character in the specified General Category (the second field in <a href="ftp://ftp.unicode.org/Public/UNIDATA/UnicodeData.txt">UnicodeData.txt</a>) or the specified <a href="ftp://ftp.unicode.org/Public/UNIDATA/Blocks.txt">Block</a>.</dd>
 *       <dd>The following names are available:
 *       <dl>
 *         <dt>Unicode General Categories:</dt>
 *         <dd><code>L, M, N, Z, C, P, S, Lu, Ll, Lt, Lm, Lo, Mn, Me, Mc, Nd, Nl, No, Zs, Zl, Zp, Cc, Cf, Cn,
 *         Co, Cs, Pd, Ps, Pe, Pc, Po, Sm, Sc, Sk, So</code>
 *         </dd>
 *         <dd>(Currently the Cn category includes U+10000-U+10FFFF characters)</dd>
 *         <dt>Unicode Blocks:</dt>
 *         <dd>
 *       Basic Latin, Latin-1 Supplement, Latin Extended-A, Latin Extended-B,
 *       IPA Extensions, Spacing Modifier Letters, Combining Diacritical Marks, Greek,
 *       Cyrillic, Armenian, Hebrew, Arabic, Devanagari, Bengali, Gurmukhi, Gujarati,
 *       Oriya, Tamil, Telugu, Kannada, Malayalam, Thai, Lao, Tibetan, Georgian,
 *       Hangul Jamo, Latin Extended Additional, Greek Extended, General Punctuation,
 *       Superscripts and Subscripts, Currency Symbols, Combining Marks for Symbols,
 *       Letterlike Symbols, Number Forms, Arrows, Mathematical Operators,
 *       Miscellaneous Technical, Control Pictures, Optical Character Recognition,
 *       Enclosed Alphanumerics, Box Drawing, Block Elements, Geometric Shapes,
 *       Miscellaneous Symbols, Dingbats, CJK Symbols and Punctuation, Hiragana,
 *       Katakana, Bopomofo, Hangul Compatibility Jamo, Kanbun,
 *       Enclosed CJK Letters and Months, CJK Compatibility, CJK Unified Ideographs,
 *       Hangul Syllables, High Surrogates, High Private Use Surrogates, Low Surrogates,
 *       Private Use, CJK Compatibility Ideographs, Alphabetic Presentation Forms,
 *       Arabic Presentation Forms-A, Combining Half Marks, CJK Compatibility Forms,
 *       Small Form Variants, Arabic Presentation Forms-B, Specials,
 *       Halfwidth and Fullwidth Forms
 *         </dd>
 *         <dt>Others:</dt>
 *         <dd><code>ALL</code> (Equivalent to <code>[\u0000-\v10FFFF]</code>)</dd>
 *         <dd><code>ASSIGNED</code> (<code>\p{ASSIGNED}</code> is equivalent to <code>\P{Cn}</code>)</dd>
 *         <dd><code>UNASSIGNED</code> (<code>\p{UNASSIGNED}</code> is equivalent to <code>\p{Cn}</code>)</dd>
 *       </dl>
 *
 *       <dt class="REGEX"><code>\P{name}</code></dt>
 *       <dd>Matches one character not in the specified General Category or the specified Block.</dd>
 *     </dl>
 *   </li>
 *
 *   <li>Selection and Quantifier
 *     <ul>
 *       <li><code>X | Y</code> matches either X or Y</li>
 *       <li><code>X*</code> matches 0 or more of X</li>
 *       <li><code>X+</code> matches 0 or more of X</li>
 *       <li><code>X?</code> matches 0 or one of X</li>
 *       <li><code>X{number}</code> matches <i>number</i> or more of X</li>
 *       <li><code>X{min,}</code> matches <i>min</i> or more of X</li>
 *       <li><code>X{min,max}</code> matches between <i>min</i> and <i>max</i> of X</li>
 *       <li>Non-greedy equivalent of above
 *         <ul>
 *           <li><code>X*?</code> non-greedy</li>
 *           <li><code>X+?</code> non-greedy</li>
 *           <li><code>X??</code> non-greedy</li>
 *           <li><code>X{min,}?</code></li>
 *           <li><code>X{min,max}?</code></li>
 *         </ul>
 *       </li>
 *     </ul>
 *   </li>
 *
 *   <li>Grouping, Capturing, and Back-reference
 *     <ul>
 *       <li><code>(?:X)</code> Grouping. <code>foo+</code> matches <code>foo</code> or <code>foooo</code>.
 *       <p>If you want it matches <code>foofoo</code> or <code>foofoofoo</code>, you have to write <code>(?:foo)+</code>.</p>
 *       </li>
 *       <li><code>(X)</code> Grouping with capturing.
 *       <p>It makes a capturing group know where in target text a group matched with methods of a <code>Match</code> instance after {@link #matches(String, Match)}.</p>
 *       <p>The 0th group means whole of this regular expression.</p>
 *       <p>The <i>N</i>th group is the inside of the <i>N</i>th left parenthesis.</p>
 *       <p>For instance, with a regular expression of <code> *([^&lt;:]*) +&lt;([^&gt;]*)&gt; *</code> and target text of</p>
 *       <pre>From: TAMURA Kent &lt;kent@trl.ibm.co.jp&gt;</pre>
 *       <p>The result should be as followed:</p>
 *       <ul>
 *         <li><code>Match.getCapturedText(0)</code> : "<code> TAMURA Kent &lt;kent@trl.ibm.co.jp&gt;</code>"</li>
 *         <li><code>Match.getCapturedText(1)</code> : "<code>TAMURA Kent</code>"</li>
 *         <li><code>Match.getCapturedText(2)</code> : "<code>kent@trl.ibm.co.jp</code>"</li>
 *       </ul>
 *       </li>
 *       <li><code>\1 \2 \3 \4 \5 \6 \7 \8 \9</code></li>
 *       <li><code>(?>X)</code> Independent expression group. ................</li>
 *       <li><code>(?options:X)</code> or <code>(?options-options2:X)</code> The <i>options</i> or the <i>options2</i>
 *       consists of 'i' 'm' 's' 'w'. Note that it can not contain 'u'.</li>
 *       <li><code>(?options)</code> or <code>(?options-options2)</code> These expressions must be at the beginning of a group.</li>
 *     </ul>
 *   </li>
 *
 *   <li>Anchor
 *     <dl>
 *       <dt class="REGEX"><code>\A</code>
 *       <dd>Matches the beginning of the text.
 *
 *       <dt class="REGEX"><code>\Z</code>
 *       <dd>Matches the end of the text, or before an EOL character at the end of the text,
 *           or CARRIAGE RETURN + LINE FEED at the end of the text.
 *
 *       <dt class="REGEX"><code>\z</code>
 *       <dd>Matches the end of the text.
 *
 *       <dt class="REGEX"><code>^</code>
 *       <dd>Matches the beginning of the text.  It is equivalent to <code>\A</code>.
 *       <dd>When the <code>m</code> ({@link #MULTIPLE_LINES}) option is set,
 *           it matches the beginning of the text, or after one of EOL characters (
 *           LINE FEED (U+000A), CARRIAGE RETURN (U+000D), LINE SEPARATOR (U+2028),
 *           PARAGRAPH SEPARATOR (U+2029).)
 *
 *       <dt class="REGEX"><code>$</code>
 *       <dd>Matches the end of the text, or before an EOL character at the end of the text,
 *           or CARRIAGE RETURN + LINE FEED at the end of the text.
 *       <dd>When the <code>m</code> ({@link #MULTIPLE_LINES}) option is set,
 *           it matches the end of the text, or before an EOL character.
 *
 *       <dt class="REGEX"><code>\b</code>
 *       <dd>Matches word boundary. (See {@link #UNICODE_WORD_BOUNDARY})
 *
 *       <dt class="REGEX"><code>\B</code>
 *       <dd>Matches non word boundary. (See {@link #UNICODE_WORD_BOUNDARY})
 *
 *       <dt class="REGEX"><code>\&lt;</code>
 *       <dd>Matches the beginning of a word. (See {@link #UNICODE_WORD_BOUNDARY})
 *
 *       <dt class="REGEX"><code>\&gt;</code>
 *       <dd>Matches the end of a word. (See {@link #UNICODE_WORD_BOUNDARY})
 *     </dl>
 *   </li>
 *   <li>Lookahead and lookbehind
 *     <dl>
 *       <dt class="REGEX"><code>(?=X)</code>
 *       <dd>Lookahead.
 *
 *       <dt class="REGEX"><code>(?!X)</code>
 *       <dd>Negative lookahead.
 *
 *       <dt class="REGEX"><code>(?&lt;=X)</code>
 *       <dd>Lookbehind.
 *       <dd>(Note for text capturing......)
 *
 *       <dt class="REGEX"><code>(?&lt;!X)</code>
 *       <dd>Negative lookbehind.
 *     </dl>
 *   </li>
 *
 *   <li>Misc.
 *     <dl>
 *       <dt class="REGEX"><code>(?(condition)yes-pattern|no-pattern)</code>,
 *       <dt class="REGEX"><code>(?(condition)yes-pattern)</code>
 *       <dd>......
 *       <dt class="REGEX"><code>(?#comment)</code> Comment
 *       <dd>A comment string consists of characters except '<code>)</code>'.
 *           You can not write comments in character classes and before quantifiers.
 *     </dl>
 *   </li>
 * </ul>
 *
 * <h1>BNF grammar for the regular expression</h1>
 * <pre>
 * regex ::= ('(?' options ')')? term ('|' term)*
 * term ::= factor+
 * factor ::= anchors | atom (('*' | '+' | '?' | minmax ) '?'? )? | '(?#' [^)]* ')'
 * minmax ::= '{' ([0-9]+ | [0-9]+ ',' | ',' [0-9]+ | [0-9]+ ',' [0-9]+) '}'
 * atom ::= char | '.' | char-class | '(' regex ')' | '(?:' regex ')' | '\' [0-9]
 *          | '\w' | '\W' | '\d' | '\D' | '\s' | '\S' | category-block | '\X'
 *          | '(?>' regex ')' | '(?' options ':' regex ')'
 *          | '(?' ('(' [0-9] ')' | '(' anchors ')' | looks) term ('|' term)? ')'
 * options ::= [imsw]* ('-' [imsw]+)?
 * anchors ::= '^' | '$' | '\A' | '\Z' | '\z' | '\b' | '\B' | '\&lt;' | '\>'
 * looks ::= '(?=' regex ')'  | '(?!' regex ')' | '(?&lt;=' regex ')' | '(?&lt;!' regex ')'
 * char ::= '\\' | '\' [efnrtv] | '\c' [@-_] | code-point | character-1
 * category-block ::= '\' [pP] category-symbol-1
 *                    | ('\p{' | '\P{') (category-symbol | block-name | other-properties) '}'
 * category-symbol-1 ::= 'L' | 'M' | 'N' | 'Z' | 'C' | 'P' | 'S'
 * category-symbol ::= category-symbol-1 | 'Lu' | 'Ll' | 'Lt' | 'Lm' | Lo'
 *                     | 'Mn' | 'Me' | 'Mc' | 'Nd' | 'Nl' | 'No'
 *                     | 'Zs' | 'Zl' | 'Zp' | 'Cc' | 'Cf' | 'Cn' | 'Co' | 'Cs'
 *                     | 'Pd' | 'Ps' | 'Pe' | 'Pc' | 'Po'
 *                     | 'Sm' | 'Sc' | 'Sk' | 'So'
 * block-name ::= (See above)
 * other-properties ::= 'ALL' | 'ASSIGNED' | 'UNASSIGNED'
 * character-1 ::= (any character except meta-characters)
 *
 * char-class ::= '[' ranges ']' | '(?[' ranges ']' ([-+&amp;] '[' ranges ']')? ')'
 * ranges ::= '^'? (range <a href="#COMMA_OPTION">','?</a>)+
 * range ::= '\d' | '\w' | '\s' | '\D' | '\W' | '\S' | category-block
 *           | range-char | range-char '-' range-char
 * range-char ::= '\[' | '\]' | '\\' | '\' [,-efnrtv] | code-point | character-2
 * code-point ::= '\x' hex-char hex-char
 *                | '\x{' hex-char+ '}'
 *                | '\v' hex-char hex-char hex-char hex-char hex-char hex-char
 * hex-char ::= [0-9a-fA-F]
 * character-2 ::= (any character except \[]-,)
 * </pre>
 *
 * <h1>Reference</h1>
 * <a href="http://www.unicode.org/unicode/reports/tr18/">Unicode Regular Expression Guidelines</a>
 * 
 * @xerces.internal
 *
 * @author TAMURA Kent <a href="mailto:kent@trl.ibm.co.jp">kent@trl.ibm.co.jp</a>
 * @version $Id$
 */
public class RegularExpression implements java.io.Serializable {
    
    private static final long serialVersionUID = 6242499334195006401L;

    static final boolean DEBUG = false;

    /**
     * Compiles a token tree into an operation flow.
     */
    private synchronized void compile(Token tok) {
        if (this.operations != null)
            return;
        this.numberOfClosures = 0;
        this.operations = this.compile(tok, null, false);
    }

    /**
     * Converts a token to an operation.
     */
    private Op compile(Token tok, Op next, boolean reverse) {
        Op ret;
        switch (tok.type) {
        case Token.DOT:
            ret = Op.createDot();
            ret.next = next;
            break;

        case Token.CHAR:
            ret = Op.createChar(tok.getChar());
            ret.next = next;
            break;

        case Token.ANCHOR:
            ret = Op.createAnchor(tok.getChar());
            ret.next = next;
            break;

        case Token.RANGE:
        case Token.NRANGE:
            ret = Op.createRange(tok);
            ret.next = next;
            break;

        case Token.CONCAT:
            ret = next;
            if (!reverse) {
                for (int i = tok.size()-1;  i >= 0;  i --) {
                    ret = compile(tok.getChild(i), ret, false);
                }
            } else {
                for (int i = 0;  i < tok.size();  i ++) {
                    ret = compile(tok.getChild(i), ret, true);
                }
            }
            break;

        case Token.UNION:
            Op.UnionOp uni = Op.createUnion(tok.size());
            for (int i = 0;  i < tok.size();  i ++) {
                uni.addElement(compile(tok.getChild(i), next, reverse));
            }
            ret = uni;                          // ret.next is null.
            break;

        case Token.CLOSURE:
        case Token.NONGREEDYCLOSURE:
            Token child = tok.getChild(0);
            int min = tok.getMin();
            int max = tok.getMax();
            if (min >= 0 && min == max) { // {n}
                ret = next;
                for (int i = 0; i < min;  i ++) {
                    ret = compile(child, ret, reverse);
                }
                break;
            }
            if (min > 0 && max > 0)
                max -= min;
            if (max > 0) {
                // X{2,6} -> XX(X(X(XX?)?)?)?
                ret = next;
                for (int i = 0;  i < max;  i ++) {
                    Op.ChildOp q = Op.createQuestion(tok.type == Token.NONGREEDYCLOSURE);
                    q.next = next;
                    q.setChild(compile(child, ret, reverse));
                    ret = q;
                }
            } else {
                Op.ChildOp op;
                if (tok.type == Token.NONGREEDYCLOSURE) {
                    op = Op.createNonGreedyClosure();
                } else {                        // Token.CLOSURE
                    op = Op.createClosure(this.numberOfClosures++);
                }
                op.next = next;
                op.setChild(compile(child, op, reverse));
                ret = op;
            }
            if (min > 0) {
                for (int i = 0;  i < min;  i ++) {
                    ret = compile(child, ret, reverse);
                }
            }
            break;

        case Token.EMPTY:
            ret = next;
            break;

        case Token.STRING:
            ret = Op.createString(tok.getString());
            ret.next = next;
            break;

        case Token.BACKREFERENCE:
            ret = Op.createBackReference(tok.getReferenceNumber());
            ret.next = next;
            break;

        case Token.PAREN:
            if (tok.getParenNumber() == 0) {
                ret = compile(tok.getChild(0), next, reverse);
            } else if (reverse) {
                next = Op.createCapture(tok.getParenNumber(), next);
                next = compile(tok.getChild(0), next, reverse);
                ret = Op.createCapture(-tok.getParenNumber(), next);
            } else {
                next = Op.createCapture(-tok.getParenNumber(), next);
                next = compile(tok.getChild(0), next, reverse);
                ret = Op.createCapture(tok.getParenNumber(), next);
            }
            break;

        case Token.LOOKAHEAD:
            ret = Op.createLook(Op.LOOKAHEAD, next, compile(tok.getChild(0), null, false));
            break;
        case Token.NEGATIVELOOKAHEAD:
            ret = Op.createLook(Op.NEGATIVELOOKAHEAD, next, compile(tok.getChild(0), null, false));
            break;
        case Token.LOOKBEHIND:
            ret = Op.createLook(Op.LOOKBEHIND, next, compile(tok.getChild(0), null, true));
            break;
        case Token.NEGATIVELOOKBEHIND:
            ret = Op.createLook(Op.NEGATIVELOOKBEHIND, next, compile(tok.getChild(0), null, true));
            break;

        case Token.INDEPENDENT:
            ret = Op.createIndependent(next, compile(tok.getChild(0), null, reverse));
            break;

        case Token.MODIFIERGROUP:
            ret = Op.createModifier(next, compile(tok.getChild(0), null, reverse),
                                    ((Token.ModifierToken)tok).getOptions(),
                                    ((Token.ModifierToken)tok).getOptionsMask());
            break;

        case Token.CONDITION:
            Token.ConditionToken ctok = (Token.ConditionToken)tok;
            int ref = ctok.refNumber;
            Op condition = ctok.condition == null ? null : compile(ctok.condition, null, reverse);
            Op yes = compile(ctok.yes, next, reverse);
            Op no = ctok.no == null ? null : compile(ctok.no, next, reverse);
            ret = Op.createCondition(next, ref, condition, yes, no);
            break;

        default:
            throw new RuntimeException("Unknown token type: "+tok.type);
        } // switch (tok.type)
        return ret;
    }


//Public

    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
     *
     * @return true if the target is matched to this regular expression.
     */
    public boolean matches(char[]  target) {
        return this.matches(target, 0,  target .length , (Match)null);
    }

    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern
     * in specified range or not.
     *
     * @param start Start offset of the range.
     * @param end  End offset +1 of the range.
     * @return true if the target is matched to this regular expression.
     */
    public boolean matches(char[]  target, int start, int end) {
        return this.matches(target, start, end, (Match)null);
    }

    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
     *
     * @param match A Match instance for storing matching result
     * @return Offset of the start position in <var>target</var>; or -1 if not match
     */
    public boolean matches(char[]  target, Match match) {
        return this.matches(target, 0,  target .length , match);
    }


    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern
     * in specified range or not.
     *
     * @param start Start offset of the range
     * @param end  End offset +1 of the range
     * @param match A Match instance for storing matching result
     * @return Offset of the start position in <var>target</var>; or -1 if not match
     */
    public boolean matches(char[] target, int start, int end, Match match) {

        synchronized (this) {
            if (this.operations == null)
                this.prepare();
            if (this.context == null)
                this.context = new Context();
        }
        Context con = null;
        synchronized (this.context) {
            con = this.context.inuse ? new Context() : this.context;
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
            // Need not to call setSource() because
            // a caller can not access this match instance.
        }
        con.match = match;

        if (RegularExpression.isSet(this.options, XMLSCHEMA_MODE)) {
            int matchEnd = this. match(con, this.operations, con.start, 1, this.options);
            //System.err.println("DEBUG: matchEnd="+matchEnd);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.setInUse(false);
                return true;
            }
            return false;
        }

        /*
         * The pattern has only fixed string.
         * The engine uses Boyer-Moore.
         */
        if (this.fixedStringOnly) {
            //System.err.println("DEBUG: fixed-only: "+this.fixedString);
            int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o);
                    con.match.setEnd(0, o+this.fixedString.length());
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }

        /*
         * The pattern contains a fixed string.
         * The engine checks with Boyer-Moore whether the text contains the fixed string or not.
         * If not, it return with false.
         */
        if (this.fixedString != null) {
            int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o < 0) {
                //System.err.println("Non-match in fixed-string search.");
                con.setInUse(false);
                return false;
            }
        }

        int limit = con.limit-this.minlength;
        int matchStart;
        int matchEnd = -1;

        /*
         * Checks whether the expression starts with ".*".
         */
        if (this.operations != null
            && this.operations.type == Op.CLOSURE && this.operations.getChild().type == Op.DOT) {
            if (isSet(this.options, SINGLE_LINE)) {
                matchStart = con.start;
                matchEnd = this. match(con, this.operations, con.start, 1, this.options);
            } else {
                boolean previousIsEOL = true;
                for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                    int ch =  target [  matchStart ] ;
                    if (isEOLChar(ch)) {
                        previousIsEOL = true;
                    } else {
                        if (previousIsEOL) {
                            if (0 <= (matchEnd = this. match(con, this.operations,
                                                             matchStart, 1, this.options)))
                                break;
                        }
                        previousIsEOL = false;
                    }
                }
            }
        }

        /*
         * Optimization against the first character.
         */
        else if (this.firstChar != null) {
            //System.err.println("DEBUG: with firstchar-matching: "+this.firstChar);
            RangeToken range = this.firstChar;
            for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                int ch =  target [matchStart] ;
                if (REUtil.isHighSurrogate(ch) && matchStart+1 < con.limit) {
                    ch = REUtil.composeFromSurrogates(ch, target[matchStart+1]);
                }
                if (!range.match(ch))  {
                    continue;
                }
                if (0 <= (matchEnd = this. match(con, this.operations,
                                                 matchStart, 1, this.options))) {
                        break;
                }
            }
        }

        /*
         * Straightforward matching.
         */
        else {
            for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                if (0 <= (matchEnd = this. match(con, this.operations, matchStart, 1, this.options)))
                    break;
            }
        }

        if (matchEnd >= 0) {
            if (con.match != null) {
                con.match.setBeginning(0, matchStart);
                con.match.setEnd(0, matchEnd);
            }
            con.setInUse(false);
            return true;
        } else {
            con.setInUse(false);
            return false;
        }
    }

    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
     *
     * @return true if the target is matched to this regular expression.
     */
    public boolean matches(String  target) {
        return this.matches(target, 0,  target .length() , (Match)null);
    }

    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern
     * in specified range or not.
     *
     * @param start Start offset of the range.
     * @param end  End offset +1 of the range.
     * @return true if the target is matched to this regular expression.
     */
    public boolean matches(String  target, int start, int end) {
        return this.matches(target, start, end, (Match)null);
    }

    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
     *
     * @param match A Match instance for storing matching result
     * @return Offset of the start position in <var>target</var>; or -1 if not match
     */
    public boolean matches(String  target, Match match) {
        return this.matches(target, 0,  target .length() , match);
    }

    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern
     * in specified range or not.
     *
     * @param start Start offset of the range
     * @param end  End offset +1 of the range
     * @param match A Match instance for storing matching result
     * @return Offset of the start position in <var>target</var>; or -1 if not match
     */
    public boolean matches(String  target, int start, int end, Match match) {

        synchronized (this) {
            if (this.operations == null)
                this.prepare();
            if (this.context == null)
                this.context = new Context();
        }
        Context con = null;
        synchronized (this.context) {
            con = this.context.inuse ? new Context() : this.context;
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
            // Need not to call setSource() because
            // a caller can not access this match instance.
        }
        con.match = match;

        if (RegularExpression.isSet(this.options, XMLSCHEMA_MODE)) {
            if (DEBUG) {
                System.err.println("target string="+target);
            }
            int matchEnd = this. match(con, this.operations, con.start, 1, this.options);
            if (DEBUG) {
                System.err.println("matchEnd="+matchEnd);
                System.err.println("con.limit="+con.limit);
            }
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.setInUse(false);
                return true;
            }
            return false;
        }

        /*
         * The pattern has only fixed string.
         * The engine uses Boyer-Moore.
         */
        if (this.fixedStringOnly) {
            //System.err.println("DEBUG: fixed-only: "+this.fixedString);
            int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o);
                    con.match.setEnd(0, o+this.fixedString.length());
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }

        /*
         * The pattern contains a fixed string.
         * The engine checks with Boyer-Moore whether the text contains the fixed string or not.
         * If not, it return with false.
         */
        if (this.fixedString != null) {
            int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o < 0) {
                //System.err.println("Non-match in fixed-string search.");
                con.setInUse(false);
                return false;
            }
        }

        int limit = con.limit-this.minlength;
        int matchStart;
        int matchEnd = -1;

        /*
         * Checks whether the expression starts with ".*".
         */
        if (this.operations != null
            && this.operations.type == Op.CLOSURE && this.operations.getChild().type == Op.DOT) {
            if (isSet(this.options, SINGLE_LINE)) {
                matchStart = con.start;
                matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            } else {
                boolean previousIsEOL = true;
                for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                    int ch =  target .charAt(  matchStart ) ;
                    if (isEOLChar(ch)) {
                        previousIsEOL = true;
                    } else {
                        if (previousIsEOL) {
                            if (0 <= (matchEnd = this.match(con, this.operations,
                                                            matchStart, 1, this.options)))
                                break;
                        }
                        previousIsEOL = false;
                    }
                }
            }
        }

        /*
         * Optimization against the first character.
         */
        else if (this.firstChar != null) {
            //System.err.println("DEBUG: with firstchar-matching: "+this.firstChar);
            RangeToken range = this.firstChar;
            for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                int ch =  target .charAt(  matchStart ) ;
                if (REUtil.isHighSurrogate(ch) && matchStart+1 < con.limit) {
                    ch = REUtil.composeFromSurrogates(ch, target.charAt(matchStart+1));
                }
                if (!range.match(ch)) {
                    continue;
                }
                if (0 <= (matchEnd = this.match(con, this.operations,
                                                matchStart, 1, this.options))) {
                        break;
                }                
            }
        }

        /*
         * Straightforward matching.
         */
        else {
            for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                if (0 <= (matchEnd = this.match(con, this.operations, matchStart, 1, this.options)))
                    break;
            }
        }

        if (matchEnd >= 0) {
            if (con.match != null) {
                con.match.setBeginning(0, matchStart);
                con.match.setEnd(0, matchEnd);
            }
            con.setInUse(false);
            return true;
        } else {
            con.setInUse(false);
            return false;
        }
    }

    /**
     * @return -1 when not match; offset of the end of matched string when match.
     */
    private int match(Context con, Op op, int offset, int dx, int opts) {
        final ExpressionTarget target = con.target;
        final Stack opStack = new Stack();
        final IntStack dataStack = new IntStack();
        final boolean isSetIgnoreCase = isSet(opts, IGNORE_CASE);
        int retValue = -1;
        boolean returned = false;

        for (;;) {
            if (op == null || offset > con.limit || offset < con.start) {
                if (op == null) {
                    retValue = isSet(opts, XMLSCHEMA_MODE) && offset != con.limit ? -1 : offset;
                }
                else {
                   retValue = -1; 
                }
                returned = true;
            }
            else  {
                retValue = -1;
                // dx value is either 1 or -1
                switch (op.type) {
                case Op.CHAR:
                    {
                        final int o1 = (dx > 0) ? offset : offset -1;
                        if (o1 >= con.limit || o1 < 0 || !matchChar(op.getData(), target.charAt(o1), isSetIgnoreCase)) {
                            returned = true;
                            break;
                        }
                        offset += dx;
                        op = op.next;
                    }
                    break;

                case Op.DOT:
                    {
                        int o1 = (dx > 0) ? offset : offset - 1;
                        if (o1 >= con.limit || o1 < 0) {
                            returned = true;
                            break;
                        }
                        if (isSet(opts, SINGLE_LINE)) {
                            if (REUtil.isHighSurrogate(target.charAt(o1)) && o1+dx >= 0 && o1+dx < con.limit) {
                                o1 += dx;
                            }
                        }
                        else {
                            int ch = target.charAt(o1);
                            if (REUtil.isHighSurrogate(ch) && o1+dx >= 0 && o1+dx < con.limit) {
                                o1 += dx;
                                ch = REUtil.composeFromSurrogates(ch, target.charAt(o1));
                            }
                            if (isEOLChar(ch)) {
                                returned = true;
                                break;
                            }
                        }
                        offset = (dx > 0) ? o1 + 1 : o1;
                        op = op.next;
                    }
                    break;

                case Op.RANGE:
                case Op.NRANGE:
                    {
                        int o1 = (dx > 0) ? offset : offset -1;
                        if (o1 >= con.limit || o1 < 0) {
                            returned = true;
                            break;
                        }
                        int ch = target.charAt(offset);
                        if (REUtil.isHighSurrogate(ch) && o1+dx < con.limit && o1+dx >=0) {
                            o1 += dx;
                            ch = REUtil.composeFromSurrogates(ch, target.charAt(o1));
                        }
                        final RangeToken tok = op.getToken();
                        if (!tok.match(ch)) {
                            returned = true;
                            break;
                        }
                        offset = (dx > 0) ? o1+1 : o1;
                        op = op.next;
                    }
                    break;

                case Op.ANCHOR:
                    {
                        if (!matchAnchor(target, op, con, offset, opts)) {
                            returned = true;
                            break;
                        }
                        op = op.next;
                    }
                    break;

                case Op.BACKREFERENCE:
                    {
                        int refno = op.getData();
                        if (refno <= 0 || refno >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: "+refno);
                        }
                        if (con.match.getBeginning(refno) < 0 || con.match.getEnd(refno) < 0) {
                            returned = true;
                            break;
                        }
                        int o2 = con.match.getBeginning(refno);
                        int literallen = con.match.getEnd(refno)-o2;
                        if (dx > 0) {
                            if (!target.regionMatches(isSetIgnoreCase, offset, con.limit, o2, literallen)) {
                                returned = true;
                                break;
                            }
                            offset += literallen;
                        }
                        else {
                            if (!target.regionMatches(isSetIgnoreCase, offset-literallen, con.limit, o2, literallen)) {
                                returned = true;
                                break;
                            }
                            offset -= literallen;
                        }
                        op = op.next;
                    }
                    break;

                case Op.STRING:
                    {
                        String literal = op.getString();
                        int literallen = literal.length();
                        if (dx > 0) {
                            if (!target.regionMatches(isSetIgnoreCase, offset, con.limit, literal, literallen)) {
                                returned = true;
                                break;
                            }
                            offset += literallen;
                        }
                        else {
                            if (!target.regionMatches(isSetIgnoreCase, offset-literallen, con.limit, literal, literallen)) {
                                returned = true;
                                break;
                            }
                            offset -= literallen;
                        }
                        op = op.next;
                    }
                    break;

                case Op.CLOSURE:
                    {
                        // Saves current position to avoid zero-width repeats.
                        final int id = op.getData();
                        if (con.closureContexts[id].contains(offset)) {
                            returned = true;
                            break;
                        }
                        
                        con.closureContexts[id].addOffset(offset);
                    }
                    // fall through

                case Op.QUESTION:
                    {
                        opStack.push(op);
                        dataStack.push(offset);
                        op = op.getChild();
                    }
                    break;

                case Op.NONGREEDYCLOSURE:
                case Op.NONGREEDYQUESTION:
                    {
                        opStack.push(op);
                        dataStack.push(offset);
                        op = op.next;
                    }
                    break;

                case Op.UNION:
                    if (op.size() == 0) {
                        returned = true;
                    }
                    else {
                        opStack.push(op);
                        dataStack.push(0);
                        dataStack.push(offset);
                        op = op.elementAt(0);
                    }
                    break;

                case Op.CAPTURE:
                    {
                        final int refno = op.getData();
                        if (con.match != null) {
                            if (refno > 0) {
                                dataStack.push(con.match.getBeginning(refno));
                                con.match.setBeginning(refno, offset);
                            }
                            else {
                                final int index = -refno;
                                dataStack.push(con.match.getEnd(index));
                                con.match.setEnd(index, offset);
                            }
                            opStack.push(op);
                            dataStack.push(offset);
                        }
                        op = op.next;
                    }
                    break;

                case Op.LOOKAHEAD:
                case Op.NEGATIVELOOKAHEAD:
                case Op.LOOKBEHIND:
                case Op.NEGATIVELOOKBEHIND:
                    {
                        opStack.push(op);
                        dataStack.push(dx);
                        dataStack.push(offset);
                        dx = (op.type == Op.LOOKAHEAD || op.type == Op.NEGATIVELOOKAHEAD) ? 1 : -1;
                        op = op.getChild();
                    }
                    break;

                case Op.INDEPENDENT:
                    {
                        opStack.push(op);
                        dataStack.push(offset);
                        op = op.getChild();
                    }
                    break;

                case Op.MODIFIER:
                    {
                        int localopts = opts;
                        localopts |= op.getData();
                        localopts &= ~op.getData2();
                        opStack.push(op);
                        dataStack.push(opts);
                        dataStack.push(offset);
                        opts = localopts;
                        op = op.getChild();
                    }
                    break;

                case Op.CONDITION:
                    {
                        Op.ConditionOp cop = (Op.ConditionOp)op;
                        if (cop.refNumber > 0) {
                            if (cop.refNumber >= this.nofparen) {
                                throw new RuntimeException("Internal Error: Reference number must be more than zero: "+cop.refNumber);
                            }
                            if (con.match.getBeginning(cop.refNumber) >= 0
                                    && con.match.getEnd(cop.refNumber) >= 0) {
                                op = cop.yes;
                            }
                            else if (cop.no != null) {
                                op = cop.no;
                            }
                            else {
                                op = cop.next;
                            }
                        }
                        else {
                            opStack.push(op);
                            dataStack.push(offset);
                            op = cop.condition;
                        }
                    }
                    break;

                default:
                    throw new RuntimeException("Unknown operation type: " + op.type);
                }
            }

            // handle recursive operations
            while (returned) {
                // exhausted all the operations
                if (opStack.isEmpty()) {
                    return retValue;
                }

                op = (Op) opStack.pop();
                offset = dataStack.pop();

                switch (op.type) {
                case Op.CLOSURE:
                case Op.QUESTION:
                    if (retValue < 0) {
                        op = op.next;
                        returned = false;
                    }
                    break;

                case Op.NONGREEDYCLOSURE:
                case Op.NONGREEDYQUESTION:
                    if (retValue < 0) {
                        op = op.getChild();
                        returned = false;
                    }
                    break;

                case Op.UNION:
                    {
                        int unionIndex = dataStack.pop();
                        if (DEBUG) {
                            System.err.println("UNION: "+unionIndex+", ret="+retValue);
                        }

                        if (retValue < 0) {
                            if (++unionIndex < op.size()) {
                                opStack.push(op);
                                dataStack.push(unionIndex);
                                dataStack.push(offset);
                                op = op.elementAt(unionIndex);
                                returned = false;
                            }
                            else {
                                retValue = -1;
                            }
                        }
                    }
                    break;

                case Op.CAPTURE:
                    final int refno = op.getData();
                    final int saved = dataStack.pop();
                    if (retValue < 0) {
                        if (refno > 0) {
                            con.match.setBeginning(refno, saved);
                        }
                        else {
                            con.match.setEnd(-refno, saved);
                        }
                    }
                    break;
                    
                case Op.LOOKAHEAD:
                case Op.LOOKBEHIND:
                    {
                        dx = dataStack.pop();
                        if (0 <= retValue) {
                            op = op.next;
                            returned = false;
                        }
                        retValue = -1;
                    }
                    break;

                case Op.NEGATIVELOOKAHEAD:
                case Op.NEGATIVELOOKBEHIND:
                    {
                        dx = dataStack.pop();
                        if (0 > retValue)  {
                            op = op.next;
                            returned = false;
                        }
                        retValue = -1;
                    }
                    break;

                case Op.MODIFIER:
                    opts = dataStack.pop();
                    // fall through

                case Op.INDEPENDENT:
                    if (retValue >= 0)  {
                        offset = retValue;
                        op = op.next;
                        returned = false;
                    }
                    break;

                case Op.CONDITION:
                    {
                        final Op.ConditionOp cop = (Op.ConditionOp)op;
                        if (0 <= retValue) {
                            op = cop.yes;
                        }
                        else if (cop.no != null) {
                            op = cop.no;
                        }
                        else {
                            op = cop.next;
                        }
                    }
                    returned = false;
                    break;

                default:
                    break;
                }
            }
        }
    }

    private boolean matchChar(int ch, int other, boolean ignoreCase) {
        return (ignoreCase) ? matchIgnoreCase(ch, other) : ch == other;
    }

    boolean matchAnchor(ExpressionTarget target, Op op, Context con, int offset, int opts) {
        boolean go = false;
        switch (op.getData()) {
        case '^':
            if (isSet(opts, MULTIPLE_LINES)) {
                if (!(offset == con.start
                      || offset > con.start && offset < con.limit && isEOLChar(target.charAt(offset-1))))
                    return false;
            } else {
                if (offset != con.start)
                    return false;
            }
            break;

        case '@':                         // Internal use only.
            // The @ always matches line beginnings.
            if (!(offset == con.start
                  || offset > con.start && isEOLChar(target.charAt(offset-1))))
                return false;
            break;

        case '$':
            if (isSet(opts, MULTIPLE_LINES)) {
                if (!(offset == con.limit
                      || offset < con.limit && isEOLChar(target.charAt(offset))))
                    return false;
            } else {
                if (!(offset == con.limit
                      || offset+1 == con.limit && isEOLChar(target.charAt(offset))
                      || offset+2 == con.limit &&  target.charAt(offset) == CARRIAGE_RETURN
                      &&  target.charAt(offset+1) == LINE_FEED))
                    return false;
            }
            break;

        case 'A':
            if (offset != con.start)  return false;
            break;

        case 'Z':
            if (!(offset == con.limit
                  || offset+1 == con.limit && isEOLChar(target.charAt(offset))
                  || offset+2 == con.limit &&  target.charAt(offset) == CARRIAGE_RETURN
                  &&  target.charAt(offset+1) == LINE_FEED))
                return false;
            break;

        case 'z':
            if (offset != con.limit)  return false;
            break;

        case 'b':
            if (con.length == 0) 
                return false;
            {
                int after = getWordType(target, con.start, con.limit, offset, opts);
                if (after == WT_IGNORE)  return false;
                int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                if (after == before)  return false;
            }
            break;

        case 'B':
            if (con.length == 0)
                go = true;
            else {
                int after = getWordType(target, con.start, con.limit, offset, opts);
                go = after == WT_IGNORE
                     || after == getPreviousWordType(target, con.start, con.limit, offset, opts);
            }
            if (!go)  return false;
            break;

        case '<':
            if (con.length == 0 || offset == con.limit)  return false;
            if (getWordType(target, con.start, con.limit, offset, opts) != WT_LETTER
                || getPreviousWordType(target, con.start, con.limit, offset, opts) != WT_OTHER)
                return false;
            break;

        case '>':
            if (con.length == 0 || offset == con.start)  return false;
            if (getWordType(target, con.start, con.limit, offset, opts) != WT_OTHER
                || getPreviousWordType(target, con.start, con.limit, offset, opts) != WT_LETTER)
                return false;
            break;
        } // switch anchor type
        
        return true;
    }

    private static final int getPreviousWordType(ExpressionTarget target, int begin, int end,
                                                 int offset, int opts) {
        int ret = getWordType(target, begin, end, --offset, opts);
        while (ret == WT_IGNORE)
            ret = getWordType(target, begin, end, --offset, opts);
        return ret;
    }

    private static final int getWordType(ExpressionTarget target, int begin, int end,
                                         int offset, int opts) {
        if (offset < begin || offset >= end)  return WT_OTHER;
        return getWordType0(target.charAt(offset) , opts);
    }


    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
     *
     * @return true if the target is matched to this regular expression.
     */
    public boolean matches(CharacterIterator target) {
        return this.matches(target, (Match)null);
    }


    /**
     * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
     *
     * @param match A Match instance for storing matching result
     * @return Offset of the start position in <var>target</var>; or -1 if not match
     */
    public boolean matches(CharacterIterator  target, Match match) {
        int start = target.getBeginIndex();
        int end = target.getEndIndex();



        synchronized (this) {
            if (this.operations == null)
                this.prepare();
            if (this.context == null)
                this.context = new Context();
        }
        Context con = null;
        synchronized (this.context) {
            con = this.context.inuse ? new Context() : this.context;
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
            // Need not to call setSource() because
            // a caller can not access this match instance.
        }
        con.match = match;

        if (RegularExpression.isSet(this.options, XMLSCHEMA_MODE)) {
            int matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            //System.err.println("DEBUG: matchEnd="+matchEnd);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.setInUse(false);
                return true;
            }
            return false;
        }

        /*
         * The pattern has only fixed string.
         * The engine uses Boyer-Moore.
         */
        if (this.fixedStringOnly) {
            //System.err.println("DEBUG: fixed-only: "+this.fixedString);
            int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o);
                    con.match.setEnd(0, o+this.fixedString.length());
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }

        /*
         * The pattern contains a fixed string.
         * The engine checks with Boyer-Moore whether the text contains the fixed string or not.
         * If not, it return with false.
         */
        if (this.fixedString != null) {
            int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o < 0) {
                //System.err.println("Non-match in fixed-string search.");
                con.setInUse(false);
                return false;
            }
        }

        int limit = con.limit-this.minlength;
        int matchStart;
        int matchEnd = -1;

        /*
         * Checks whether the expression starts with ".*".
         */
        if (this.operations != null
            && this.operations.type == Op.CLOSURE && this.operations.getChild().type == Op.DOT) {
            if (isSet(this.options, SINGLE_LINE)) {
                matchStart = con.start;
                matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            } else {
                boolean previousIsEOL = true;
                for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                    int ch =  target .setIndex(  matchStart ) ;
                    if (isEOLChar(ch)) {
                        previousIsEOL = true;
                    } else {
                        if (previousIsEOL) {
                            if (0 <= (matchEnd = this.match(con, this.operations,
                                                            matchStart, 1, this.options)))
                                break;
                        }
                        previousIsEOL = false;
                    }
                }
            }
        }

        /*
         * Optimization against the first character.
         */
        else if (this.firstChar != null) {
            //System.err.println("DEBUG: with firstchar-matching: "+this.firstChar);
            RangeToken range = this.firstChar;
            for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                int ch =  target .setIndex(  matchStart ) ;
                if (REUtil.isHighSurrogate(ch) && matchStart+1 < con.limit) {
                    ch = REUtil.composeFromSurrogates(ch, target.setIndex(matchStart+1));
                }
                if (!range.match(ch)) {
                    continue;
                }
                if (0 <= (matchEnd = this.match(con, this.operations,
                                                matchStart, 1, this.options))) {
                    break;
                }
            }
        }

        /*
         * Straightforward matching.
         */
        else {
            for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                if (0 <= (matchEnd = this. match(con, this.operations, matchStart, 1, this.options)))
                    break;
            }
        }

        if (matchEnd >= 0) {
            if (con.match != null) {
                con.match.setBeginning(0, matchStart);
                con.match.setEnd(0, matchEnd);
            }
            con.setInUse(false);
            return true;
        } else {
            con.setInUse(false);
            return false;
        }
    }

    // ================================================================

    /**
     * A regular expression.
     * @serial
     */
    String regex;
    /**
     * @serial
     */
    int options;

    /**
     * The number of parenthesis in the regular expression.
     * @serial
     */
    int nofparen;
    /**
     * Internal representation of the regular expression.
     * @serial
     */
    Token tokentree;

    boolean hasBackReferences = false;

    transient int minlength;
    transient Op operations = null;
    transient int numberOfClosures;
    transient Context context = null;
    transient RangeToken firstChar = null;

    transient String fixedString = null;
    transient int fixedStringOptions;
    transient BMPattern fixedStringTable = null;
    transient boolean fixedStringOnly = false;

    static abstract class ExpressionTarget {
        abstract char charAt(int index);
        abstract boolean regionMatches(boolean ignoreCase, int offset, int limit, String part, int partlen);
        abstract boolean regionMatches(boolean ignoreCase, int offset, int limit, int offset2, int partlen);
    }
    
    static final class StringTarget extends ExpressionTarget {
        
        private String target;
        
        StringTarget(String target) {
            this.target = target;
        }
        
        final void resetTarget(String target) {
            this.target = target;
        }
        
        final char charAt(int index) {
            return target.charAt(index);
        }
        
        final boolean regionMatches(boolean ignoreCase, int offset, int limit,
                              String part, int partlen) {
            if (limit-offset < partlen) {
                return false;
            }
            return (ignoreCase) ? target.regionMatches(true, offset, part, 0, partlen) : target.regionMatches(offset, part, 0, partlen);
        }

        final boolean regionMatches(boolean ignoreCase, int offset, int limit,
                                    int offset2, int partlen) {
            if (limit-offset < partlen) {
                return false;
            }
            return (ignoreCase) ? target.regionMatches(true, offset, target, offset2, partlen)
                                : target.regionMatches(offset, target, offset2, partlen);
        }
    }
    
    static final class CharArrayTarget extends ExpressionTarget {
        
        char[] target;
        
        CharArrayTarget(char[] target) {
            this.target = target; 
        }

        final void resetTarget(char[] target) {
            this.target = target;
        }

        char charAt(int index) {
            return target[index];
        }
        
        final boolean regionMatches(boolean ignoreCase, int offset, int limit,
                String part, int partlen) {
            if (offset < 0 || limit-offset < partlen)  {
                return false;
            }
            return (ignoreCase) ? regionMatchesIgnoreCase(offset, limit, part, partlen)
                                : regionMatches(offset, limit, part, partlen);
        }

        private final boolean regionMatches(int offset, int limit, String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                if (target[offset++] != part.charAt(i++)) {
                    return false;
                }
            }
            return true;
        }

        private final boolean regionMatchesIgnoreCase(int offset, int limit, String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                final char ch1 = target[offset++] ;
                final char ch2 = part.charAt(i++);
                if (ch1 == ch2) {
                    continue;
                }
                final char uch1 = Character.toUpperCase(ch1);
                final char uch2 = Character.toUpperCase(ch2);
                if (uch1 == uch2) {
                    continue;
                }
                if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            }
            return true;
        }

        final boolean regionMatches(boolean ignoreCase, int offset, int limit, int offset2, int partlen) {
            if (offset < 0 || limit-offset < partlen) {
                return false;
            }
            return (ignoreCase) ? regionMatchesIgnoreCase(offset, limit, offset2, partlen)
                                : regionMatches(offset, limit, offset2, partlen);
        }

        private final boolean regionMatches(int offset, int limit, int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                if ( target [  offset++ ]  !=  target [  i++ ] )
                    return false;
            }
            return true;
        }

        private final boolean regionMatchesIgnoreCase(int offset, int limit, int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                final char ch1 =  target[offset++] ;
                final char ch2 =  target[i++] ;
                if (ch1 == ch2) {
                    continue;
                }
                final char uch1 = Character.toUpperCase(ch1);
                final char uch2 = Character.toUpperCase(ch2);
                if (uch1 == uch2) {
                    continue;
                }
                if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            }
            return true;
        }
    }

    static final class CharacterIteratorTarget extends ExpressionTarget {
        CharacterIterator target;
        
        CharacterIteratorTarget(CharacterIterator target) {
            this.target = target; 
        }

        final void resetTarget(CharacterIterator target) {
            this.target = target;
        }

        final char charAt(int index) {
            return target.setIndex(index);
        }

        final boolean regionMatches(boolean ignoreCase, int offset, int limit,
                String part, int partlen) {
            if (offset < 0 || limit-offset < partlen)  {
                return false;
            }
            return (ignoreCase) ? regionMatchesIgnoreCase(offset, limit, part, partlen)
                                : regionMatches(offset, limit, part, partlen);
        }
        
        private final boolean regionMatches(int offset, int limit, String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                if (target.setIndex(offset++) != part.charAt(i++)) {
                    return false;
                }
            }
            return true;
        }
        
        private final boolean regionMatchesIgnoreCase(int offset, int limit, String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                final char ch1 = target.setIndex(offset++) ;
                final char ch2 = part.charAt(i++);
                if (ch1 == ch2) {
                    continue;
                }
                final char uch1 = Character.toUpperCase(ch1);
                final char uch2 = Character.toUpperCase(ch2);
                if (uch1 == uch2) {
                    continue;
                }
                if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            }
            return true;
        }

        final boolean regionMatches(boolean ignoreCase, int offset, int limit, int offset2, int partlen) {
            if (offset < 0 || limit-offset < partlen) {
                return false;
            }
            return (ignoreCase) ? regionMatchesIgnoreCase(offset, limit, offset2, partlen)
                                : regionMatches(offset, limit, offset2, partlen);
        }

        private final boolean regionMatches(int offset, int limit, int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                if (target.setIndex(offset++) != target.setIndex(i++)) {
                    return false;
                }
            }
            return true;
        }

        private final boolean regionMatchesIgnoreCase(int offset, int limit, int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                final char ch1 = target.setIndex(offset++) ;
                final char ch2 = target.setIndex(i++) ;
                if (ch1 == ch2) {
                    continue;
                }
                final char uch1 = Character.toUpperCase(ch1);
                final char uch2 = Character.toUpperCase(ch2);
                if (uch1 == uch2) {
                    continue;
                }
                if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            }
            return true;
        }
    }

    static final class ClosureContext {
        
        int[] offsets = new int[4];
        int currentIndex = 0;
        
        boolean contains(int offset) {
            for (int i=0; i<currentIndex;++i) {
                if (offsets[i] == offset) {
                    return true;
                }
            }
            return false;
        }
        
        void reset() {
            currentIndex = 0;
        }

        void addOffset(int offset) {
            // We do not check for duplicates, caller is responsible for that
            if (currentIndex == offsets.length) {
                offsets = expandOffsets();
            }
            offsets[currentIndex++] = offset;
        }
        
        private int[] expandOffsets() {
            final int len = offsets.length;
            final int newLen = len << 1;
            int[] newOffsets = new int[newLen];
            
            System.arraycopy(offsets, 0, newOffsets, 0, currentIndex);
            return newOffsets;
        }
    }
    
    static final class Context {
        int start;
        int limit;
        int length;
        Match match;
        boolean inuse = false;
        ClosureContext[] closureContexts;
        
        private StringTarget stringTarget; 
        private CharArrayTarget charArrayTarget;
        private CharacterIteratorTarget characterIteratorTarget;

        ExpressionTarget target;

        Context() {
        }

        private void resetCommon(int nofclosures) {
            this.length = this.limit-this.start;
            setInUse(true);
            this.match = null;
            if (this.closureContexts == null || this.closureContexts.length != nofclosures) {
                this.closureContexts = new ClosureContext[nofclosures];
            }
            for (int i = 0;  i < nofclosures;  i ++)  {
                if (this.closureContexts[i] == null) {
                    this.closureContexts[i] = new ClosureContext();
                }
                else {
                    this.closureContexts[i].reset();
                }
            }
        }

        void reset(CharacterIterator target, int start, int limit, int nofclosures) {
            if (characterIteratorTarget == null) {
                characterIteratorTarget = new CharacterIteratorTarget(target);
            }
            else {
                characterIteratorTarget.resetTarget(target);
            }
            this.target = characterIteratorTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }

        void reset(String target, int start, int limit, int nofclosures) {
            if (stringTarget == null) {
                stringTarget = new StringTarget(target);
            }
            else {
                stringTarget.resetTarget(target);
            }
            this.target = stringTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }

        void reset(char[] target, int start, int limit, int nofclosures) {
            if (charArrayTarget == null) {
                charArrayTarget = new CharArrayTarget(target);
            }
            else {
                charArrayTarget.resetTarget(target);
            }
            this.target = charArrayTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }
        synchronized void setInUse(boolean inUse) {
            this.inuse = inUse;
        }
    }

    /**
     * Prepares for matching.  This method is called just before starting matching.
     */
    void prepare() {
        if (Op.COUNT)  Op.nofinstances = 0;
        this.compile(this.tokentree);
        /*
        if  (this.operations.type == Op.CLOSURE && this.operations.getChild().type == Op.DOT) { // .*
            Op anchor = Op.createAnchor(isSet(this.options, SINGLE_LINE) ? 'A' : '@');
            anchor.next = this.operations;
            this.operations = anchor;
        }
        */
        if (Op.COUNT)  System.err.println("DEBUG: The number of operations: "+Op.nofinstances);

        this.minlength = this.tokentree.getMinLength();

        this.firstChar = null;
        if (!isSet(this.options, PROHIBIT_HEAD_CHARACTER_OPTIMIZATION)
            && !isSet(this.options, XMLSCHEMA_MODE)) {
            RangeToken firstChar = Token.createRange();
            int fresult = this.tokentree.analyzeFirstCharacter(firstChar, this.options);
            if (fresult == Token.FC_TERMINAL) {
                firstChar.compactRanges();
                this.firstChar = firstChar;
                if (DEBUG)
                    System.err.println("DEBUG: Use the first character optimization: "+firstChar);
            }
        }

        if (this.operations != null
            && (this.operations.type == Op.STRING || this.operations.type == Op.CHAR)
            && this.operations.next == null) {
            if (DEBUG)
                System.err.print(" *** Only fixed string! *** ");
            this.fixedStringOnly = true;
            if (this.operations.type == Op.STRING)
                this.fixedString = this.operations.getString();
            else if (this.operations.getData() >= 0x10000) { // Op.CHAR
                this.fixedString = REUtil.decomposeToSurrogates(this.operations.getData());
            } else {
                char[] ac = new char[1];
                ac[0] = (char)this.operations.getData();
                this.fixedString = new String(ac);
            }
            this.fixedStringOptions = this.options;
            this.fixedStringTable = new BMPattern(this.fixedString, 256,
                                                  isSet(this.fixedStringOptions, IGNORE_CASE));
        } else if (!isSet(this.options, PROHIBIT_FIXED_STRING_OPTIMIZATION)
                   && !isSet(this.options, XMLSCHEMA_MODE)) {
            Token.FixedStringContainer container = new Token.FixedStringContainer();
            this.tokentree.findFixedString(container, this.options);
            this.fixedString = container.token == null ? null : container.token.getString();
            this.fixedStringOptions = container.options;
            if (this.fixedString != null && this.fixedString.length() < 2)
                this.fixedString = null;
            // This pattern has a fixed string of which length is more than one.
            if (this.fixedString != null) {
                this.fixedStringTable = new BMPattern(this.fixedString, 256,
                                                      isSet(this.fixedStringOptions, IGNORE_CASE));
                if (DEBUG) {
                    System.err.println("DEBUG: The longest fixed string: "+this.fixedString.length()
                                       +"/" //+this.fixedString
                                       +"/"+REUtil.createOptionString(this.fixedStringOptions));
                    System.err.print("String: ");
                    REUtil.dumpString(this.fixedString);
                }
            }
        }
    }

    /*
     * An option.
     * If you specify this option, <span><code>(X)</code></span>
     * captures matched text, and <span><code>(:?X)</code></span>
     * does not capture.
     *
     * @see #RegularExpression(java.lang.String,int)
     * @see #setPattern(java.lang.String,int)
    static final int MARK_PARENS = 1<<0;
     */

    /**
     * "i"
     */
    static final int IGNORE_CASE = 1<<1;

    /**
     * "s"
     */
    static final int SINGLE_LINE = 1<<2;

    /**
     * "m"
     */
    static final int MULTIPLE_LINES = 1<<3;

    /**
     * "x"
     */
    static final int EXTENDED_COMMENT = 1<<4;

    /**
     * This option redefines <span><code>\d \D \w \W \s \S</code></span>.
     *
     * @see #RegularExpression(String,String)
     * @see #setPattern(String,int,Locale)
     * @see #UNICODE_WORD_BOUNDARY
     */
    static final int USE_UNICODE_CATEGORY = 1<<5; // "u"

    /**
     * An option.
     * This enables to process locale-independent word boundary for <span><code>\b \B \&lt; \></code></span>.
     * <p>By default, the engine considers a position between a word character
     * (<span><code>\w</code></span>) and a non word character
     * is a word boundary.
     * <p>By this option, the engine checks word boundaries with the method of
     * 'Unicode Regular Expression Guidelines' Revision 4.
     *
     * @see #RegularExpression(String,String)
     * @see #setPattern(String,int,Locale)
     */
    static final int UNICODE_WORD_BOUNDARY = 1<<6; // "w"

    /**
     * "H"
     */
    static final int PROHIBIT_HEAD_CHARACTER_OPTIMIZATION = 1<<7;
    /**
     * "F"
     */
    static final int PROHIBIT_FIXED_STRING_OPTIMIZATION = 1<<8;
    /**
     * "X". XML Schema mode.
     */
    static final int XMLSCHEMA_MODE = 1<<9;
    /**
     * ",".
     */
    static final int SPECIAL_COMMA = 1<<10;


    private static final boolean isSet(int options, int flag) {
        return (options & flag) == flag;
    }

    /**
     * Creates a new RegularExpression instance.
     *
     * @param regex a regular expression
     * @throws ParseException if regex is not conforming to the syntax
     */
    public RegularExpression(String regex) throws ParseException {
        this(regex, null);
    }

    /**
     * Creates a new RegularExpression instance with options.
     *
     * @param regex a regular expression
     * @param options a string of options consisted of "i" "m" "s" "u" "w" "," "X" or null
     * @throws ParseException if regex is not conforming to the syntax
     */
    public RegularExpression(String regex, String options) throws ParseException {
        this.setPattern(regex, options);
    }
    
    /**
     * Creates a new RegularExpression instance with options.
     *
     * @param regex a regular expression pattern
     * @param options a string of options consisted of "i" "m" "s" "u" "w" "," "X" or null
     * @param locale value of the desired locale or null
     * @throws ParseException if regex is not conforming to the syntax
     * @see Locale
     */
    public RegularExpression(String regex, String options, Locale locale) throws ParseException {
        this.setPattern(regex, options, locale);
    }

    RegularExpression(String regex, Token tok, int parens, boolean hasBackReferences, int options) {
        this.regex = regex;
        this.tokentree = tok;
        this.nofparen = parens;
        this.options = options;
        this.hasBackReferences = hasBackReferences;
    }

    /**
     * Set a new regular expression pattern with the default Locale.
     *
     * @param newPattern a new regular expression pattern
     * @throws ParseException if regex is not conforming to the syntax
     */
    public void setPattern(String newPattern) throws ParseException {
        this.setPattern(newPattern, Locale.getDefault());
    }

    /**
     * Set a new regular expression pattern with the provided Locale.
     *
     * @param newPattern a new regular expression pattern
     * @param locale value of the desired locale or null
     * @throws ParseException if regex is not conforming to the syntax
     * @see Locale
     */
    public void setPattern(String newPattern, Locale locale) throws ParseException {
        this.setPattern(newPattern, this.options, locale);
    }

    /**
     * Set a new regular expression pattern with the provided options and Locale.
     *
     * @param newPattern a new regular expression pattern
     * @param options an int value representation of regular expression options
     * @param locale value of the desired locale or null
     * @throws ParseException if regex is not conforming to the syntax
     * @see Locale
     */
    private void setPattern(String newPattern, int options, Locale locale) throws ParseException {
        this.regex = newPattern;
        this.options = options;
        RegexParser rp = RegularExpression.isSet(this.options, RegularExpression.XMLSCHEMA_MODE)
                         ? new ParserForXMLSchema(locale) : new RegexParser(locale);
        this.tokentree = rp.parse(this.regex, this.options);
        this.nofparen = rp.parennumber;
        this.hasBackReferences = rp.hasBackReferences;

        this.operations = null;
        this.context = null;
    }

    /**
     * Set a new regular expression pattern with the provided options and the default locale.
     *
     * @param newPattern a new regular expression pattern
     * @param options a string of options consisted of "i" "m" "s" "u" "w" "," "X" or null
     * @throws ParseException if regex is not conforming to the syntax
     * @see Locale#getDefault()
     */
    public void setPattern(String newPattern, String options) throws ParseException {
        this.setPattern(newPattern, options, Locale.getDefault());
    }

    /**
     * Set a new regular expression pattern with the provided options and Locale.
     *
     * @param newPattern a new regular expression pattern
     * @param options a string of options consisted of "i" "m" "s" "u" "w" "," "X" or null
     * @param locale value of the desired locale or null
     * @throws ParseException if regex is not conforming to the syntax
     * @see Locale
     */
    public void setPattern(String newPattern, String options, Locale locale) throws ParseException {
        this.setPattern(newPattern, REUtil.parseOptions(options), locale);
    }

    /**
     * Returns the regular expression pattern.
     *
     * @return the regular expression pattern
     */
    public String getPattern() {
        return this.regex;
    }

    /**
     * Represents this instance in String.
     */
    public String toString() {
        return this.tokentree.toString(this.options);
    }

    /**
     * Returns a string representation of the regular expression's options.
     *
     * <p>The order of letters in it may be different from a string specified
     * in a constructor or <code>setPattern()</code>.</p>
     *
     * @return a string representation of the regular expression's options
     * @see #RegularExpression(java.lang.String,java.lang.String)
     * @see #setPattern(java.lang.String,java.lang.String)
     */
    public String getOptions() {
        return REUtil.createOptionString(this.options);
    }

    /**
     * Return true if patterns are the same and the options are equivalent.
     *
     * @return true if patterns are the same and the options are equivalent
     */
    public boolean equals(Object obj) {
        if (obj == null)  return false;
        if (!(obj instanceof RegularExpression))
            return false;
        RegularExpression r = (RegularExpression)obj;
        return this.regex.equals(r.regex) && this.options == r.options;
    }

    boolean equals(String pattern, int options) {
        return this.regex.equals(pattern) && this.options == options;
    }

    /**
     *
     */
    public int hashCode() {
        return (this.regex+"/"+this.getOptions()).hashCode();
    }

    /**
     * Return the number of regular expression groups.
     * This method returns 1 when the regular expression has no capturing-parenthesis.
     *
     */
    public int getNumberOfGroups() {
        return this.nofparen;
    }

    // ================================================================

    private static final int WT_IGNORE = 0;
    private static final int WT_LETTER = 1;
    private static final int WT_OTHER = 2;
    private static final int getWordType0(char ch, int opts) {
        if (!isSet(opts, UNICODE_WORD_BOUNDARY)) {
            if (isSet(opts, USE_UNICODE_CATEGORY)) {
                return (Token.getRange("IsWord", true).match(ch)) ? WT_LETTER : WT_OTHER;
            }
            return isWordChar(ch) ? WT_LETTER : WT_OTHER;
        }

        switch (Character.getType(ch)) {
        case Character.UPPERCASE_LETTER:      // L
        case Character.LOWERCASE_LETTER:      // L
        case Character.TITLECASE_LETTER:      // L
        case Character.MODIFIER_LETTER:       // L
        case Character.OTHER_LETTER:          // L
        case Character.LETTER_NUMBER:         // N
        case Character.DECIMAL_DIGIT_NUMBER:  // N
        case Character.OTHER_NUMBER:          // N
        case Character.COMBINING_SPACING_MARK: // Mc
            return WT_LETTER;

        case Character.FORMAT:                // Cf
        case Character.NON_SPACING_MARK:      // Mn
        case Character.ENCLOSING_MARK:        // Mc
            return WT_IGNORE;

        case Character.CONTROL:               // Cc
            switch (ch) {
            case '\t':
            case '\n':
            case '\u000B':
            case '\f':
            case '\r':
                return WT_OTHER;
            default:
                return WT_IGNORE;
            }

        default:
            return WT_OTHER;
        }
    }

    // ================================================================

    static final int LINE_FEED = 0x000A;
    static final int CARRIAGE_RETURN = 0x000D;
    static final int LINE_SEPARATOR = 0x2028;
    static final int PARAGRAPH_SEPARATOR = 0x2029;

    private static final boolean isEOLChar(int ch) {
        return ch == LINE_FEED || ch == CARRIAGE_RETURN || ch == LINE_SEPARATOR
        || ch == PARAGRAPH_SEPARATOR;
    }

    private static final boolean isWordChar(int ch) { // Legacy word characters
        if (ch == '_')  return true;
        if (ch < '0')  return false;
        if (ch > 'z')  return false;
        if (ch <= '9')  return true;
        if (ch < 'A')  return false;
        if (ch <= 'Z')  return true;
        if (ch < 'a')  return false;
        return true;
    }

    private static final boolean matchIgnoreCase(int chardata, int ch) {
        if (chardata == ch)  return true;
        if (chardata > 0xffff || ch > 0xffff)  return false;
        char uch1 = Character.toUpperCase((char)chardata);
        char uch2 = Character.toUpperCase((char)ch);
        if (uch1 == uch2)  return true;
        return Character.toLowerCase(uch1) == Character.toLowerCase(uch2);
    }
}
