package io.github.dynamixon.flexorm.logic.namedsql;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.dynamixon.flexorm.misc.DBException;
import io.github.dynamixon.flexorm.misc.DzConst;
import io.github.dynamixon.flexorm.pojo.SqlPreparedBundle;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Jianfeng.Mao2
 * @date 23-8-30
 */
public class NamedParamSqlUtil {

    private static final LoadingCache<String,ParsedSql> NAMED_PARAM_SQL_PARSE_CACHE = CacheBuilder.newBuilder().maximumSize(cacheCount()).build(
        new CacheLoader<String, ParsedSql>() {
            @Override
            public ParsedSql load(@Nonnull String namedParamSql){
                return NamedParameterUtils.parseSqlStatement(namedParamSql);
            }
        }
    );

    public static long cacheCount(){
        String cacheCountStr = System.getProperty(DzConst.NAMED_PARAM_SQL_PARSE_CACHE_COUNT);
        if(StringUtils.isBlank(cacheCountStr)){
            return 4096L;
        }
        return Long.parseLong(cacheCountStr);
    }

    public static SqlPreparedBundle fromNamed(String namedParamSql, Map<String,?> paramMap){
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(paramMap);
        ParsedSql parsedSql;
        try {
            parsedSql = NAMED_PARAM_SQL_PARSE_CACHE.get(namedParamSql);
        } catch (ExecutionException e) {
            throw new DBException(e);
        }
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, mapSqlParameterSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, mapSqlParameterSource);
        return new SqlPreparedBundle(sqlToUse,params);
    }
}
