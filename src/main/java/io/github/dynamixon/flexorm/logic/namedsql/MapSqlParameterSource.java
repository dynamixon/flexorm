package io.github.dynamixon.flexorm.logic.namedsql;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SqlParameterSource} implementation that holds a given Map of parameters.
 *
 * <p>The {@code addValue} methods on this class will make adding several values
 * easier. The methods return a reference to the {@link MapSqlParameterSource}
 * itself, so you can chain several method calls together within a single statement.
 */
public class MapSqlParameterSource implements SqlParameterSource {

    private final Map<String, Object> values = new LinkedHashMap<>();

    /**
     * Create a new MapSqlParameterSource based on a Map.
     * @param values a Map holding existing parameter values (can be {@code null})
     */
    public MapSqlParameterSource(Map<String, ?> values) {
        addValues(values);
    }

    /**
     * Add a Map of parameters to this parameter source.
     * @param values a Map holding existing parameter values (can be {@code null})
     * @return a reference to this parameter source,
     * so it's possible to chain several calls together
     */
    public MapSqlParameterSource addValues(Map<String, ?> values) {
        if (values != null) {
            this.values.putAll(values);
        }
        return this;
    }

    @Override
    public boolean hasValue(String paramName) {
        return this.values.containsKey(paramName);
    }

    @Override
    public Object getValue(String paramName) {
        if (!hasValue(paramName)) {
            throw new IllegalArgumentException("No value registered for key '" + paramName + "'");
        }
        return this.values.get(paramName);
    }

}
