package vinsearch.logic;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/** Minimal UTF-8 JSON reader/writer for the KT2 dataset; no external dependencies. */
public final class Json {
    private Json() { }
    public static Object parse(String text) { return new Parser(text).parse(); }
    public static String stringify(Object value) {
        StringBuilder out = new StringBuilder();
        write(out, value, 0);
        return out.toString();
    }
    private static void write(StringBuilder b, Object o, int indent) {
        if (o == null) { b.append("null"); return; }
        if (o instanceof String s) { writeString(b, s); return; }
        if (o instanceof Boolean || o instanceof Number) { b.append(o); return; }
        if (o instanceof Map<?, ?> m) {
            b.append('{');
            int i = 0;
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (i++ > 0) b.append(',');
                b.append('\n'); pad(b, indent + 2);
                writeString(b, (String) e.getKey()); b.append(": ");
                write(b, e.getValue(), indent + 2);
            }
            if (i > 0) { b.append('\n'); pad(b, indent); }
            b.append('}'); return;
        }
        if (o instanceof List<?> a) {
            b.append('[');
            for (int i = 0; i < a.size(); i++) {
                if (i > 0) b.append(',');
                b.append('\n'); pad(b, indent + 2);
                write(b, a.get(i), indent + 2);
            }
            if (!a.isEmpty()) { b.append('\n'); pad(b, indent); }
            b.append(']'); return;
        }
        throw new IllegalArgumentException("Unsupported JSON value type " + o.getClass());
    }
    private static void pad(StringBuilder b, int n) { b.append(" ".repeat(n)); }
    private static void writeString(StringBuilder b, String s) {
        b.append('"');
        for (char ch : s.toCharArray()) {
            switch (ch) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                default -> { if (ch < 0x20) b.append(String.format("\\u%04x", (int) ch)); else b.append(ch); }
            }
        }
        b.append('"');
    }
    private static final class Parser {
        private final String s; private int i;
        Parser(String s) { this.s = s; }
        Object parse() { Object o = value(); ws(); if (i != s.length()) fail("Extra characters"); return o; }
        void ws() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }
        void fail(String m) { throw new IllegalArgumentException(m + " at offset " + i); }
        boolean take(char c) { ws(); if (i < s.length() && s.charAt(i) == c) { i++; return true; } return false; }
        void expect(char c) { if (!take(c)) fail("Expected " + c); }
        Object value() {
            ws(); if (i == s.length()) { fail("Unexpected EOF"); }
            char c = s.charAt(i);
            if (c == '{') return object();
            if (c == '[') return array();
            if (c == '"') return string();
            if (s.startsWith("true", i)) { i += 4; return true; }
            if (s.startsWith("false", i)) { i += 5; return false; }
            if (s.startsWith("null", i)) { i += 4; return null; }
            if (c == '-' || Character.isDigit(c)) return number();
            fail("Unexpected character"); return null;
        }
        Map<String, Object> object() {
            expect('{'); Map<String,Object> m = new LinkedHashMap<>();
            if (take('}')) return m;
            do { ws(); if (i == s.length() || s.charAt(i) != '"') fail("Key must be a string");
                String k = string(); expect(':'); m.put(k, value());
            } while (take(','));
            expect('}'); return m;
        }
        List<Object> array() {
            expect('['); List<Object> a = new ArrayList<>();
            if (take(']')) return a;
            do { a.add(value()); } while (take(','));
            expect(']'); return a;
        }
        String string() {
            expect('"'); StringBuilder b = new StringBuilder();
            while (i < s.length()) {
                char c = s.charAt(i++);
                if (c == '"') return b.toString();
                if (c != '\\') { b.append(c); continue; }
                if (i == s.length()) { fail("Incomplete escape"); }
                char e = s.charAt(i++);
                switch (e) {
                    case '"' -> b.append('"');
                    case '\\' -> b.append('\\');
                    case '/' -> b.append('/');
                    case 'b' -> b.append('\b');
                    case 'f' -> b.append('\f');
                    case 'n' -> b.append('\n');
                    case 'r' -> b.append('\r');
                    case 't' -> b.append('\t');
                    case 'u' -> { if (i + 4 > s.length()) fail("Invalid unicode escape");
                        b.append((char)Integer.parseInt(s.substring(i, i + 4), 16)); i += 4; }
                    default -> fail("Invalid escape");
                }
            }
            fail("Unterminated string"); return null;
        }
        Number number() {
            int start = i;
            if (s.charAt(i) == '-') i++;
            while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
            boolean fractional = false;
            if (i < s.length() && s.charAt(i) == '.') {
                fractional = true; i++; while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
            }
            if (i < s.length() && (s.charAt(i) == 'e' || s.charAt(i) == 'E')) {
                fractional = true; i++;
                if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) i++;
                while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
            }
            String token = s.substring(start, i);
            return fractional ? new java.math.BigDecimal(token) : Integer.valueOf(token);
        }
    }
}
