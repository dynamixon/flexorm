package io.github.dynamixon.flexorm.pojo;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-18
 */
public class MethodRef {
    private String methodName;
    private Class<?> clazz;

    public MethodRef() {
    }

    public MethodRef(String methodName, Class<?> clazz) {
        this.methodName = methodName;
        this.clazz = clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "MethodRef{" +
            "methodName='" + methodName + '\'' +
            ", clazz=" + clazz +
            '}';
    }
}
