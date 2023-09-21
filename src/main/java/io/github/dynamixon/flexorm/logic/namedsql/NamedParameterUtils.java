package io.github.dynamixon.flexorm.logic.namedsql;


import io.github.dynamixon.flexorm.misc.DBException;

import java.util.*;

/**
 * Helper methods for named parameter parsing.
 */
public abstract class NamedParameterUtils {

    /**
     * Set of characters that qualify as comment or quotes starting characters.
     */
    private static final String[] START_SKIP = new String[] {"'", "\"", "--", "/*"};

    /**
     * Set of characters that at are the corresponding comment or quotes ending characters.
     */
    private static final String[] STOP_SKIP = new String[] {"'", "\"", "\n", "*/"};

    /**
     * Set of characters that qualify as parameter separators,
     * indicating that a parameter name in an SQL String has ended.
     */
    private static final String PARAMETER_SEPARATORS = "\"':&,;()|=+-*%/\\<>^";

    /**
     * An index with separator flags per character code.
     * Technically only needed between 34 and 124 at this point.
     */
    private static final boolean[] separatorIndex = new boolean[128];

    static {
        for (char c : PARAMETER_SEPARATORS.toCharArray()) {
            separatorIndex[c] = true;
        }
    }
    

    /**
     * Parse the SQL statement and locate any placeholders or named parameters.
     * Named parameters are substituted for a JDBC placeholder.
     * @param sql the SQL statement
     * @return the parsed statement, represented as ParsedSql instance
     */
    public static ParsedSql parseSqlStatement(final String sql) {
        if(sql==null){
            throw new DBException("SQL must not be null");
        }

        Set<String> namedParameters = new HashSet<>();
        StringBuilder sqlToUse = new StringBuilder(sql);
        List<ParameterHolder> parameterList = new ArrayList<>();

        char[] statement = sql.toCharArray();
        int namedParameterCount = 0;
        int unnamedParameterCount = 0;
        int totalParameterCount = 0;

        int escapes = 0;
        int i = 0;
        while (i < statement.length) {
            int skipToPosition = i;
            while (i < statement.length) {
                skipToPosition = skipCommentsAndQuotes(statement, i);
                if (i == skipToPosition) {
                    break;
                }
                else {
                    i = skipToPosition;
                }
            }
            if (i >= statement.length) {
                break;
            }
            char c = statement[i];
            if (c == ':' || c == '&') {
                int j = i + 1;
                if (c == ':' && j < statement.length && statement[j] == ':') {
                    // Postgres-style "::" casting operator should be skipped
                    i = i + 2;
                    continue;
                }
                String parameter = null;
                if (c == ':' && j < statement.length && statement[j] == '{') {
                    // :{x} style parameter
                    while (statement[j] != '}') {
                        j++;
                        if (j >= statement.length) {
                            throw new DBException("Non-terminated named parameter declaration " +
                                "at position " + i + " in statement: " + sql);
                        }
                        if (statement[j] == ':' || statement[j] == '{') {
                            throw new DBException("Parameter name contains invalid character '" +
                                statement[j] + "' at position " + i + " in statement: " + sql);
                        }
                    }
                    if (j - i > 2) {
                        parameter = sql.substring(i + 2, j);
                        namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
                        totalParameterCount = addNamedParameter(
                            parameterList, totalParameterCount, escapes, i, j + 1, parameter);
                    }
                    j++;
                }
                else {
                    while (j < statement.length && !isParameterSeparator(statement[j])) {
                        j++;
                    }
                    if (j - i > 1) {
                        parameter = sql.substring(i + 1, j);
                        namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
                        totalParameterCount = addNamedParameter(
                            parameterList, totalParameterCount, escapes, i, j, parameter);
                    }
                }
                i = j - 1;
            }
            else {
                if (c == '\\') {
                    int j = i + 1;
                    if (j < statement.length && statement[j] == ':') {
                        // escaped ":" should be skipped
                        sqlToUse.deleteCharAt(i - escapes);
                        escapes++;
                        i = i + 2;
                        continue;
                    }
                }
                if (c == '?') {
                    int j = i + 1;
                    if (j < statement.length && (statement[j] == '?' || statement[j] == '|' || statement[j] == '&')) {
                        // Postgres-style "??", "?|", "?&" operator should be skipped
                        i = i + 2;
                        continue;
                    }
                    unnamedParameterCount++;
                    totalParameterCount++;
                }
            }
            i++;
        }
        ParsedSql parsedSql = new ParsedSql(sqlToUse.toString());
        for (ParameterHolder ph : parameterList) {
            parsedSql.addNamedParameter(ph.getParameterName(), ph.getStartIndex(), ph.getEndIndex());
        }
        parsedSql.setNamedParameterCount(namedParameterCount);
        parsedSql.setUnnamedParameterCount(unnamedParameterCount);
        parsedSql.setTotalParameterCount(totalParameterCount);
        return parsedSql;
    }

    private static int addNamedParameter(
        List<ParameterHolder> parameterList, int totalParameterCount, int escapes, int i, int j, String parameter) {

        parameterList.add(new ParameterHolder(parameter, i - escapes, j - escapes));
        totalParameterCount++;
        return totalParameterCount;
    }

    private static int addNewNamedParameter(Set<String> namedParameters, int namedParameterCount, String parameter) {
        if (!namedParameters.contains(parameter)) {
            namedParameters.add(parameter);
            namedParameterCount++;
        }
        return namedParameterCount;
    }

    /**
     * Skip over comments and quoted names present in an SQL statement.
     * @param statement character array containing SQL statement
     * @param position current position of statement
     * @return next position to process after any comments or quotes are skipped
     */
    private static int skipCommentsAndQuotes(char[] statement, int position) {
        for (int i = 0; i < START_SKIP.length; i++) {
            if (statement[position] == START_SKIP[i].charAt(0)) {
                boolean match = true;
                for (int j = 1; j < START_SKIP[i].length(); j++) {
                    if (statement[position + j] != START_SKIP[i].charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    int offset = START_SKIP[i].length();
                    for (int m = position + offset; m < statement.length; m++) {
                        if (statement[m] == STOP_SKIP[i].charAt(0)) {
                            boolean endMatch = true;
                            int endPos = m;
                            for (int n = 1; n < STOP_SKIP[i].length(); n++) {
                                if (m + n >= statement.length) {
                                    // last comment not closed properly
                                    return statement.length;
                                }
                                if (statement[m + n] != STOP_SKIP[i].charAt(n)) {
                                    endMatch = false;
                                    break;
                                }
                                endPos = m + n;
                            }
                            if (endMatch) {
                                // found character sequence ending comment or quote
                                return endPos + 1;
                            }
                        }
                    }
                    // character sequence ending comment or quote not found
                    return statement.length;
                }
            }
        }
        return position;
    }

    /**
     * Parse the SQL statement and locate any placeholders or named parameters. Named
     * parameters are substituted for a JDBC placeholder, and any select list is expanded
     * to the required number of placeholders. Select lists may contain an array of
     * objects, and in that case the placeholders will be grouped and enclosed with
     * parentheses. This allows for the use of "expression lists" in the SQL statement
     * like: <br /><br />
     * {@code select id, name, state from table where (name, age) in (('John', 35), ('Ann', 50))}
     * <p>The parameter values passed in are used to determine the number of placeholders to
     * be used for a select list. Select lists should be limited to 100 or fewer elements.
     * A larger number of elements is not guaranteed to be supported by the database and
     * is strictly vendor-dependent.
     * @param parsedSql the parsed representation of the SQL statement
     * @param paramSource the source for named parameters
     * @return the SQL statement with substituted parameters
     * @see #parseSqlStatement
     */
    public static String substituteNamedParameters(ParsedSql parsedSql, SqlParameterSource paramSource) {
        String originalSql = parsedSql.getOriginalSql();
        List<String> paramNames = parsedSql.getParameterNames();
        if (paramNames.isEmpty()) {
            return originalSql;
        }
        StringBuilder actualSql = new StringBuilder(originalSql.length());
        int lastIndex = 0;
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            int[] indexes = parsedSql.getParameterIndexes(i);
            int startIndex = indexes[0];
            int endIndex = indexes[1];
            actualSql.append(originalSql, lastIndex, startIndex);
            if (paramSource != null && paramSource.hasValue(paramName)) {
                Object value = paramSource.getValue(paramName);
                if (value instanceof Iterable) {
                    Iterator<?> entryIter = ((Iterable<?>) value).iterator();
                    int k = 0;
                    while (entryIter.hasNext()) {
                        if (k > 0) {
                            actualSql.append(", ");
                        }
                        k++;
                        Object entryItem = entryIter.next();
                        if (entryItem instanceof Object[]) {
                            Object[] expressionList = (Object[]) entryItem;
                            actualSql.append('(');
                            for (int m = 0; m < expressionList.length; m++) {
                                if (m > 0) {
                                    actualSql.append(", ");
                                }
                                actualSql.append('?');
                            }
                            actualSql.append(')');
                        }
                        else {
                            actualSql.append('?');
                        }
                    }
                }
                else {
                    actualSql.append('?');
                }
            }
            else {
                actualSql.append('?');
            }
            lastIndex = endIndex;
        }
        actualSql.append(originalSql, lastIndex, originalSql.length());
        return actualSql.toString();
    }

    /**
     * Convert a Map of named parameter values to a corresponding array.
     * @param parsedSql the parsed SQL statement
     * @param paramSource the source for named parameters
     * (may be {@code null}). If specified, the parameter metadata will
     * be built into the value array in the form of SqlParameterValue objects.
     * @return the array of values
     */
    public static Object[] buildValueArray(
        ParsedSql parsedSql, SqlParameterSource paramSource) {

        if (parsedSql.getNamedParameterCount() > 0 && parsedSql.getUnnamedParameterCount() > 0) {
            throw new DBException(
                "Not allowed to mix named and traditional ? placeholders. You have " +
                    parsedSql.getNamedParameterCount() + " named parameter(s) and " +
                    parsedSql.getUnnamedParameterCount() + " traditional placeholder(s) in statement: " +
                    parsedSql.getOriginalSql());
        }
        List<Object> paramList = new ArrayList<>();
        List<String> paramNames = parsedSql.getParameterNames();
        for (String paramName : paramNames) {
            try {
                Object paramElement = null;
                paramElement = paramSource.getValue(paramName);
                if (paramElement instanceof Collection) {
                    paramList.addAll((Collection<?>) paramElement);
                } else {
                    paramList.add(paramElement);
                }
            } catch (IllegalArgumentException ex) {
                throw new DBException("No value supplied for the SQL parameter '" + paramName + "': " + ex.getMessage());
            }
        }
        return paramList.toArray();
    }

    /**
     * Determine whether a parameter name ends at the current position,
     * that is, whether the given character qualifies as a separator.
     */
    private static boolean isParameterSeparator(char c) {
        return (c < 128 && separatorIndex[c]) || Character.isWhitespace(c);
    }


    private static class ParameterHolder {

        private final String parameterName;

        private final int startIndex;

        private final int endIndex;

        public ParameterHolder(String parameterName, int startIndex, int endIndex) {
            this.parameterName = parameterName;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getParameterName() {
            return this.parameterName;
        }

        public int getStartIndex() {
            return this.startIndex;
        }

        public int getEndIndex() {
            return this.endIndex;
        }
    }

}
