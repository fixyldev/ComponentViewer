/* 
 * MIT License
 * 
 * Copyright (c) 2024 fixyldev
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.fixyl.componentviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentFormatter {
    final private static int INITIAL_LINES_CAPACITY = 16;
    final private static int INITIAL_LINE_CAPACITY = 16;

    private int indentSize;
    private String indent;
	private Map<Integer, String> indentCache = new HashMap<>();

    public ComponentFormatter(int indentSize) {
        this.indentSize = indentSize;

        this.calculateIndent();
    }

    private void calculateIndent() {
        StringBuilder indentBuilder = new StringBuilder(this.indentSize);

        for (int i = 0; i < this.indentSize; i++)
        indentBuilder.append(' ');

        this.indent = indentBuilder.toString();
    }

	private String getIndentFromLevel(int indentLevel) {
		if (indentLevel <= 0)
			return "";

		if (this.indentCache.containsKey(indentLevel))
			return this.indentCache.get(indentLevel);

		return this.indentCache.put(indentLevel, indent.repeat(indentLevel));
	}

    public List<String> format(String componentValue) {
		List<String> lines = new ArrayList<String>(ComponentFormatter.INITIAL_LINES_CAPACITY);
		StringBuilder line = new StringBuilder(ComponentFormatter.INITIAL_LINE_CAPACITY);
		int indent = 0;
		char closingBracket = ' ';
		boolean emptyBrackets = false;
		boolean trim = false;
		char inString = ' ';

		for (int index = 0; index < componentValue.length(); index++) {
			char character = componentValue.charAt(index);

			if (emptyBrackets) {
				line.append(character);
				emptyBrackets = false;
				continue;
			}

			if (trim && character == ' ') 
				continue;
			trim = false;

			if (inString == ' ' && (character == ',' || character == ';')) {
				lines.add(line.toString() + character);
				line.setLength(0);
			} else if (inString == ' ' && (character == '[' || character == '{')) {
				if (index + 1 < componentValue.length() && (componentValue.charAt(index + 1) == ']' || componentValue.charAt(index + 1) == '}')) {
					line.append(character);
					emptyBrackets = true;
					continue;
				}
				indent++;
				lines.add(line.toString() + character);
				line.setLength(0);
			} else if (inString == ' ' && (character == ']' || character == '}')) {
				indent--;
				closingBracket = character;
				lines.add(line.toString());
				line.setLength(0);
			} else if (character == '"' || character == '\'') {
				if (inString == ' ')
					inString = character;
				else if (character == inString && componentValue.charAt(index - 1) != '\\')
					inString = ' ';
				line.append(character);
			} else
				line.append(character);

			if (line.length() == 0) {
				line.append(this.getIndentFromLevel(indent));

				if (closingBracket != ' ') {
					line.append(closingBracket);
					closingBracket = ' ';
				}

				trim = true;
			}
		}

		if (line.length() != 0)
			lines.add(line.toString());

		return lines;
    }
}
