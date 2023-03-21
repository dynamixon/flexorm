package io.github.dynamixon.flexorm.pojo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BeanClassFieldNameKey {
    private final Class<?> beanClass;

    private final String fieldName;

    public BeanClassFieldNameKey(Class<?> beanClass, String fieldName) {
        this.beanClass = beanClass;
        this.fieldName = fieldName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BeanClassFieldNameKey that = (BeanClassFieldNameKey) o;

        return new EqualsBuilder().append(beanClass, that.beanClass).append(fieldName, that.fieldName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(beanClass).append(fieldName).toHashCode();
    }

    @Override
    public String toString() {
        return "BeanClassFieldNameKey{" +
            "beanClass=" + beanClass +
            ", fieldName='" + fieldName + '\'' +
            '}';
    }
}
