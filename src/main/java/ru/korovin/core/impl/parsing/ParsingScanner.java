package ru.korovin.core.impl.parsing;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParsingScanner {
    private final String input;
    private StringBuilder buffer = new StringBuilder();
    private int pointer;

    public Token getCommandToken() {
        clearBuffer();
        skipSpacesAndTabs();
        readNonSpaceToken();
        return new Token(buffer.toString(), TokenType.COMMAND);
    }

    public Token nextToken() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot parse next token, because input has been already parsed fully");
        }
        clearBuffer();
        skipSpacesAndTabs();
        if (isEmpty()) {
            throw new IllegalStateException("No tokens left");
        }
        TokenType type;
        if (input.charAt(pointer) == '"') {
            type = TokenType.PARAM_VALUE;
            readQuotedToken();
        } else if (input.charAt(pointer) == '-') {
            type = TokenType.PARAM;
            readNonSpaceToken();
        } else {
            type = TokenType.PARAM_VALUE;
            readNonSpaceToken();
        }
        return new Token(buffer.toString(), type);
    }

    public boolean hasToken() {
        int savedPointer = pointer;
        skipSpacesAndTabs();
        boolean result = !isEmpty();
        pointer = savedPointer;
        return result;
    }

    private void clearBuffer() {
        buffer.setLength(0);
    }

    public boolean isEmpty() {
        return input.length() <= pointer;
    }

    private void readNonSpaceToken() {
        while (pointer != input.length() &&
               !Character.isWhitespace(input.charAt(pointer)) ) {
            buffer.append(input.charAt(pointer++));
        }
    }

    private void readQuotedToken() {
        if (input.charAt(pointer) != '"') {
            throw new IllegalArgumentException("Token must start with quote");
        }
        pointer++;

        while (pointer < input.length()) {
            char c = input.charAt(pointer);
            if (c == '"') {
                pointer++;
                return;
            }
            if (c == '\\' && pointer + 1 < input.length() && input.charAt(pointer + 1) == '"') {
                buffer.append('"');
                pointer += 2;
            } else {
                buffer.append(c);
                pointer++;
            }
        }
        throw new IllegalArgumentException("Unclosed quoted string at position " + pointer);
    }

    private void skipSpacesAndTabs() {
        while (pointer < input.length() && Character.isWhitespace(input.charAt(pointer))) {
            pointer++;
        }
    }

}