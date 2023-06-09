package io.github.dynamixon.flexorm.logic;

import io.github.dynamixon.flexorm.annotation.Column;
import io.github.dynamixon.flexorm.annotation.Primary;
import io.github.dynamixon.flexorm.annotation.Table;
import io.github.dynamixon.flexorm.dialect.regulate.DefaultEntityRegulator;
import io.github.dynamixon.flexorm.dialect.regulate.EntityRegulator;
import io.github.dynamixon.flexorm.dialect.typemapping.DefaultTypeMapper;
import io.github.dynamixon.flexorm.dialect.typemapping.TypeMapper;
import io.github.dynamixon.flexorm.misc.DBException;
import io.github.dynamixon.flexorm.misc.PlatformUtils;
import io.github.dynamixon.flexorm.pojo.Table2JavaMeta;
import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Table2Java {
    private static final Logger logger = LoggerFactory.getLogger(Table2Java.class);
    private static TypeMapper typeMapper;
    private static EntityRegulator entityRegulator;
    private static Table2JavaMeta meta;

    private static final Map<String,String> TYPE_MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private static void initDialect(){
        String dbType = StringUtils.trimToEmpty(new PlatformUtils().determineDatabaseType(meta.getDriver(), meta.getDbUrl()));
        boolean typeMapperMatch = false;
        ServiceLoader<TypeMapper> typeMappers = ServiceLoader.load(TypeMapper.class);
        for (TypeMapper typeMapperInner : typeMappers) {
            if(typeMapperInner.match(dbType)){
                typeMapper = typeMapperInner;
                typeMapperMatch = true;
                break;
            }
        }
        if(!typeMapperMatch){
            typeMapper = new DefaultTypeMapper();
        }

        boolean entityRegulatorMatch = false;
        ServiceLoader<EntityRegulator> entityRegulators = ServiceLoader.load(EntityRegulator.class);
        for (EntityRegulator entityRegulatorInner : entityRegulators) {
            if(entityRegulatorInner.match(dbType)){
                entityRegulator = entityRegulatorInner;
                entityRegulatorMatch = true;
                break;
            }
        }
        if(!entityRegulatorMatch){
            entityRegulator = new DefaultEntityRegulator();
        }

    }
    
    public static void generateByMeta(Table2JavaMeta table2JavaMeta) throws Exception {
        initMeta(table2JavaMeta);
        generate();
    }
    
    public static void generateByMetaFile(String metaFilePath) throws Exception {
        initMeta(metaFilePath);
        generate();
    }
    
    public static void initMeta(Table2JavaMeta table2JavaMeta){
        meta = table2JavaMeta;
        initDialect();
        TYPE_MAP.putAll(typeMapper.getTypeMap());
        Map<String, String> table2ClassMap = meta.getTable2ClassMap();
        for(String key:table2ClassMap.keySet()){
            String value = table2ClassMap.get(key);
            if(StringUtils.isBlank(value)){
                throw new DBException("Java bean name for "+key+" is empty!");
            }
        }
        Map<String, String> extraTypeMap = meta.getExtraTypeMap();
        if(MapUtils.isNotEmpty(extraTypeMap)){
            TYPE_MAP.putAll(extraTypeMap);
        }
    }

    private static void initMeta(String metaFilePath){
        try {
            if(StringUtils.isBlank(metaFilePath)){
                metaFilePath = "Table2JavaMeta.json";
            }
            File metaFile = new File(metaFilePath);
            if(metaFile.exists()){
                initMeta(new Gson().fromJson(FileUtils.readFileToString(metaFile,"utf-8"), Table2JavaMeta.class));
            }else {
                throw new DBException("metaFilePath:"+metaFilePath+" dosen't exist!");
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

    private static void generate() throws Exception {
        String url = meta.getDbUrl();
        if(StringUtils.isNotBlank(meta.getDriver())){
            Class.forName(meta.getDriver());
        }
        try (Connection conn = DriverManager.getConnection(url, meta.getDbUser(), meta.getDbPass())) {
            initDir();
            initTables(conn);
            initDomains(conn);
        }
    }

    private static String getFullDir(){
        String domainPackageDir = meta.getDomainPackage().replaceAll("\\.","/");
        String projectRootPath = meta.getProjectRoot();
        if(StringUtils.isBlank(projectRootPath)){
            projectRootPath = new File("./").getAbsolutePath();
        }
        return projectRootPath+"/"+meta.getSrcRoot() +"/"+domainPackageDir;
    }

    private static void initDir() throws Exception {
        String fullDir = getFullDir();
        logger.info("making dir:"+fullDir);
        FileUtils.forceMkdir(new File(fullDir));
    }

    private static void initTables(Connection conn) throws Exception {
        List<String> tableCreateSqls = meta.getTableCreateSqls();
        if(CollectionUtils.isEmpty(tableCreateSqls)){
            return;
        }
        for (String sql : tableCreateSqls) {
            try(PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.execute();
            }
        }
    }
    private static void initDomains(Connection conn)throws Exception{
        String fullDir = getFullDir();
        DatabaseMetaData md = conn.getMetaData();
        Map<String,String> table2ClassMap;
        if(MapUtils.isEmpty(meta.getTable2ClassMap())) {
            return;
        }else {
            table2ClassMap = meta.getTable2ClassMap();
        }
        table2ClassMap.forEach((tableName, className) -> {
            try {
                String beanContent = getBeanContentFromTable(md, tableName, className);
                String javaFileName = fullDir+"/"+className+".java";
                File javaFile = new File(javaFileName);
                boolean fileExist = javaFile.exists();
                boolean needGen = true;

                if(fileExist){
                    String fileContent = FileUtils.readFileToString(javaFile, UTF_8);
                    if(fileContent.equals(beanContent)){
                        needGen = false;
                    }
                }

                if(needGen) {
                    FileUtils.writeStringToFile(javaFile, beanContent, "utf-8", false);
                    logger.info(" (" + (fileExist ? "updated" : "*created") + ") " + tableName + " --> " + className);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static String getBeanContentFromTable(DatabaseMetaData md ,String tableName,String className) throws Exception {
        String tableComment = getTableComment(md, tableName);
        String beanContent = "";
        String packagePart = "package "+meta.getDomainPackage()+";\n";
        List<ColumnMeta> metas = getColumnMeta(md,tableName);
        Set<String> importPartSet = new LinkedHashSet<>();
        if(meta.isLombokMode()){
            importPartSet.add("lombok.Data");
        }
        StringBuilder importPart = new StringBuilder("import java.io.Serializable;\nimport " + Table.class.getCanonicalName() + ";\n");
        if(MapUtils.isNotEmpty(meta.getPrimaryColumnMap())&&CollectionUtils.isNotEmpty(meta.getPrimaryColumnMap().get(tableName))){
            importPart.append("import ").append(Primary.class.getCanonicalName()).append(";\n");
        }
        if(!meta.isAutoColumnDetection()) {
            importPart.append("import ").append(Column.class.getCanonicalName()).append(";\n");
        }
        StringBuilder fieldsPart = new StringBuilder("\n  private static final long serialVersionUID = -1L;\n");
        StringBuilder getSetPart = new StringBuilder();

        String noArgConstructor = "  public "+className+"(){\n  }\n";
        StringBuilder builderConstructor = new StringBuilder("  private " + className + "(Builder builder) {\n");
        String staticBuilderMethod = "  public static Builder builder(){\n" +
            "    return new Builder();\n" +
            "  }\n\n";
        String builderClass = "  public static final class Builder {\n";
        StringBuilder builderFieldsPart = new StringBuilder();
        StringBuilder builderGetSetPart = new StringBuilder();

        for (ColumnMeta columnMeta : metas) {
            String colName = columnMeta.getName();
            String javaVarName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, colName.toUpperCase());
            String colType = columnMeta.getType();
            String comment = columnMeta.getComment();
            String javaType = TYPE_MAP.get(StringUtils.strip(colType.toUpperCase().replaceAll("\\s+UNSIGNED","")));
            if(StringUtils.isBlank(javaType)){
                javaType = columnMeta.getClassName();
            }
            if(StringUtils.isBlank(javaType)){
                throw new DBException("type:"+colType+" has no corresponding java type!");
            }
            String simpleJavaType = getSimpleJavaType(javaType);
            if(javaType.contains(".")){
                importPartSet.add(javaType);
            }
            fieldsPart.append("\n");
            if(StringUtils.isNotBlank(comment)) {
                fieldsPart.append("  /** ").append(comment).append(" */\n");
            }
            if(meta.isPrimaryColumn(tableName,colName)){
                fieldsPart.append("  @Primary\n");
            }
            if(!meta.isAutoColumnDetection()) {
                fieldsPart.append("  @Column(\"").append(colName).append("\")\n");
            }
            fieldsPart.append("  private ").append(simpleJavaType).append(" ").append(javaVarName).append(";\n");
            builderFieldsPart.append("    private ").append(simpleJavaType).append(" ").append(javaVarName).append(";\n");
            getSetPart.append("  public ").append(simpleJavaType).append(" get").append(StringUtils.capitalize(javaVarName)).append("() {\n    return ").append(javaVarName).append(";\n  }\n\n");
            getSetPart.append("  public void set").append(StringUtils.capitalize(javaVarName)).append("( ").append(simpleJavaType).append(" ").append(javaVarName).append(" ) {\n    this.").append(javaVarName).append(" = ").append(javaVarName).append(";\n  }\n\n");

            builderGetSetPart.append("    public Builder ").append(javaVarName).append("(").append(simpleJavaType).append(" ").append(javaVarName).append(") {\n");
            builderGetSetPart.append("      this.").append(javaVarName).append(" = ").append(javaVarName).append(";\n");
            builderGetSetPart.append("      return this;\n");
            builderGetSetPart.append("    }\n\n");

            builderConstructor.append("    set").append(StringUtils.capitalize(javaVarName)).append("(builder.").append(javaVarName).append(");\n");
        }
        builderConstructor.append("  }\n\n");

        builderClass += builderFieldsPart+"\n";
        builderClass += "    public Builder() {}\n\n";
        builderClass += builderGetSetPart;
        builderClass += "    public "+className+" build() {\n";
        builderClass += "      return new "+className+"(this);\n";
        builderClass += "    }\n";
        builderClass += "  }\n";

        for (String importType : importPartSet) {
            importPart.append("import ").append(importType).append(";\n");
        }

        beanContent += packagePart+"\n";
        beanContent += importPart+"\n";
        if(StringUtils.isNotBlank(tableComment)){
            beanContent += "/** "+tableComment+" */\n";
        }
        if(meta.isAutoColumnDetection()) {
            beanContent += "@Table(value = \"" + tableName + "\", autoColumnDetection = true)\n";
        }else {
            beanContent += "@Table(\"" + tableName + "\")\n";
        }
        if(meta.isLombokMode()){
            beanContent += "@Data\n";
        }
        beanContent += "public class "+className+" implements Serializable {\n";
        beanContent += fieldsPart+"\n";
        if(!meta.isLombokMode()) {
            beanContent += noArgConstructor + "\n";
            beanContent += builderConstructor + "\n";
            beanContent += getSetPart;
            beanContent += staticBuilderMethod;
            beanContent += builderClass;
        }
//        beanContent += tableNamePart;
        beanContent += "}";


        return beanContent;
    }

    private static String getSimpleJavaType(String javaType){
        String simpleJavaType = javaType;
        if(javaType.contains(".")){
            String[] javaTypeArr = javaType.split("\\.");
            int length = javaTypeArr.length;
            simpleJavaType = javaTypeArr[length-1];
        }
        return simpleJavaType;
    }

    private static String getTableComment(DatabaseMetaData md , String table) throws Exception {
        String comment = "";
        table = entityRegulator.simpleTable(table);
        String catalog = StringUtils.isNotBlank(meta.getCatalog())? meta.getCatalog():md.getConnection().getCatalog();
        ResultSet resultSet = md.getTables(catalog, null, table, null);
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            comment = resultSet.getString("REMARKS");
            if(tableName.equalsIgnoreCase(table)){
                break;
            }
        }
        return comment;
    }


    private static List<ColumnMeta> getColumnMeta(DatabaseMetaData md , String table) throws Exception {
        List<ColumnMeta> metas = new ArrayList<>();
        table = entityRegulator.simpleTable(table);
        String catalog = StringUtils.isNotBlank(meta.getCatalog())? meta.getCatalog():md.getConnection().getCatalog();
        ResultSet resultSet = md.getColumns(catalog, null, table, null);
        while (resultSet.next()) {
            String name = resultSet.getString("COLUMN_NAME");
            String type = resultSet.getString("TYPE_NAME").toUpperCase();
            String comment = resultSet.getString("REMARKS");
            metas.add(new ColumnMeta(name,type,comment));
        }
        if(CollectionUtils.isEmpty(metas)){
            throw new DBException("Table "+table+" doesn't exist!");
        }
        Connection conn = md.getConnection();
        try(PreparedStatement ps = conn.prepareStatement("select * from "+table+" where 1=2")) {
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            Map<String,String> columnName2classNameMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            for (int i=1;i<=count;i++){
                String columnName = metaData.getColumnName(i);
                String columnClassName = metaData.getColumnClassName(i);
                columnName2classNameMap.put(columnName,columnClassName.replace("java.lang.",""));
            }
            if(MapUtils.isNotEmpty(columnName2classNameMap)){
                metas.forEach(meta -> {
                    if(columnName2classNameMap.containsKey(meta.getName())){
                        meta.setClassName(columnName2classNameMap.get(meta.getName()));
                    }
                });
            }
        }
        return metas;
    }


    private static class ColumnMeta{
        private String name;
        private String type;
        private String comment;
        private String className;

        public ColumnMeta() {
        }

        public ColumnMeta(String name, String type, String comment) {
            this.name = name;
            this.type = type;
            this.comment = comment;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static void main(String[] args) {
        try {
            generateByMetaFile((args!=null&&args.length>0)?args[0]:null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
    }
}
