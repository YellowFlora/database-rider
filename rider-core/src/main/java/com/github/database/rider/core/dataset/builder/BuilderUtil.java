package com.github.database.rider.core.dataset.builder;

import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.configuration.DBUnitConfig;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.datatype.DataType;
import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;

import static com.github.database.rider.core.util.ClassUtils.isOnClasspath;
import static com.github.database.rider.core.util.EntityManagerProvider.em;
import static com.github.database.rider.core.util.EntityManagerProvider.isEntityManagerActive;

public class BuilderUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuilderUtil.class.getName());
    private static final DBUnitConfig config = DBUnitConfig.fromGlobalConfig();

    public static String convertColumnCase(Column column, DBUnitConfig config) {
        return convertCase(column.getColumnName(), config);
    }

    public static String convertCase(String value, DBUnitConfig config) {
        if (value != null && config != null && !config.isCaseSensitiveTableNames()) {
            if (Orthography.UPPERCASE == config.getCaseInsensitiveStrategy()) {
                value = value.toUpperCase();
            } else {
                value = value.toLowerCase();
            }
        }
        return value;
    }

    public static String getColumnNameFromMetaModel(Attribute column) {
        String columnName = null;
        try {
            if (isEclipseLinkOnClasspath()) {
                columnName = ((AttributeImpl) column).getMapping().getField().getName();
            } else if (isHibernateOnClasspath() && isEntityManagerActive()) {
                columnName = getHibernateColumnName(column);
            }
        } catch (Exception e) {
            LOGGER.error("Could not extract database column name from column {} and type {}", column.getName(), column.getDeclaringType().getJavaType().getName(), e);
        }
        if (columnName == null) {
            columnName = convertCase(column.getName(), config);
        }
        return columnName;
    }

    public static boolean isHibernateOnClasspath() {
        return isOnClasspath("org.hibernate.Session");
    }

    public static boolean isEclipseLinkOnClasspath() {
        return isOnClasspath("org.eclipse.persistence.mappings.DirectToFieldMapping");
    }

    private static String getHibernateColumnName(Attribute column) throws Exception {
        Object sessionFactory = em().getEntityManagerFactory().unwrap(Class.forName("org.hibernate.SessionFactory"));
        Object entityMetadata;

        try {
            Method getClassMetadata = sessionFactory.getClass().getMethod("getClassMetadata", Class.class);
            entityMetadata = getClassMetadata.invoke(sessionFactory, column.getJavaMember().getDeclaringClass());
        } catch (NoSuchMethodException ignored) {
            Object runtimeMetamodels = sessionFactory.getClass().getMethod("getRuntimeMetamodels").invoke(sessionFactory);
            Object mappingMetamodel = runtimeMetamodels.getClass().getMethod("getMappingMetamodel").invoke(runtimeMetamodels);
            entityMetadata = mappingMetamodel.getClass()
                    .getMethod("getEntityDescriptor", Class.class)
                    .invoke(mappingMetamodel, column.getJavaMember().getDeclaringClass());
        }

        String[] columnNames = (String[]) entityMetadata.getClass()
                .getMethod("getPropertyColumnNames", String.class)
                .invoke(entityMetadata, column.getName());
        return columnNames[0];
    }

    /**
     * @param value column value
     * @return resolved datatype
     * @deprecated use <code>DataType.UNKNOWN</code> instead of this method. See https://github.com/database-rider/database-rider/pull/154#issuecomment-527622138
     */
    public static DataType resolveColumnDataType(Object value) {
        DataType columnType = DataType.UNKNOWN;
        if (value == null) {
            return columnType;
        }
        if (value instanceof Integer) {
            columnType = DataType.INTEGER;
        }
        if (value instanceof Long) {
            columnType = DataType.BIGINT_AUX_LONG;
        }
        if (value instanceof Double) {
            columnType = DataType.DOUBLE;
        }
        if (value instanceof Float) {
            columnType = DataType.FLOAT;
        }
        if (value instanceof Date) {
            columnType = DataType.DATE;
        }
        if (value instanceof Boolean) {
            columnType = DataType.BOOLEAN;
        }
        if (value instanceof BigDecimal) {
            columnType = DataType.DECIMAL;
        }
        if (value instanceof Number) {
            columnType = DataType.NUMERIC;
        }
        return columnType;
    }

}
