package io.github.dynamixon.flexorm.pojo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class Table2JavaMeta {
    private String driver;
    private String dbUrl;
    private String dbUser;
    private String dbPass;
    private String catalog;
    private String srcRoot;
    private String projectRoot;
    private String domainPackage;
    private boolean lombokMode;
    private boolean autoColumnDetection;
    private List<String> tableCreateSqls;
    private Map<String,String> table2ClassMap;
    private Map<String,String> extraTypeMap;
    private Map<String,List<String>> primaryColumnMap;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSrcRoot() {
        return srcRoot;
    }

    public void setSrcRoot(String srcRoot) {
        this.srcRoot = srcRoot;
    }

    public String getProjectRoot() {
        return projectRoot;
    }

    public void setProjectRoot(String projectRoot) {
        this.projectRoot = projectRoot;
    }

    public String getDomainPackage() {
        return domainPackage;
    }

    public void setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
    }

    public boolean isLombokMode() {
        return lombokMode;
    }

    public void setLombokMode(boolean lombokMode) {
        this.lombokMode = lombokMode;
    }

    public boolean isAutoColumnDetection() {
        return autoColumnDetection;
    }

    public void setAutoColumnDetection(boolean autoColumnDetection) {
        this.autoColumnDetection = autoColumnDetection;
    }

    public List<String> getTableCreateSqls() {
        return tableCreateSqls;
    }

    public void setTableCreateSqls(List<String> tableCreateSqls) {
        this.tableCreateSqls = tableCreateSqls;
    }

    public Map<String, String> getTable2ClassMap() {
        return table2ClassMap;
    }

    public void setTable2ClassMap(Map<String, String> table2ClassMap) {
        this.table2ClassMap = table2ClassMap;
    }

    public Map<String, String> getExtraTypeMap() {
        return extraTypeMap;
    }

    public void setExtraTypeMap(Map<String, String> extraTypeMap) {
        this.extraTypeMap = extraTypeMap;
    }

    public Map<String, List<String>> getPrimaryColumnMap() {
        return primaryColumnMap;
    }

    public void setPrimaryColumnMap(Map<String, List<String>> primaryColumnMap) {
        this.primaryColumnMap = primaryColumnMap;
    }

    public boolean isPrimaryColumn(String table,String columnName){
        return MapUtils.isNotEmpty(primaryColumnMap) &&
            StringUtils.isNotBlank(columnName) &&
            CollectionUtils.isNotEmpty(primaryColumnMap.get(table)) &&
            primaryColumnMap.get(table).contains(columnName);
    }
}
