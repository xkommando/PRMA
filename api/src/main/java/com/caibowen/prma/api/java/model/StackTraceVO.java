package com.caibowen.prma.api.java.model;

/**
 *
 * @author BowenCai
 * @since 20/12/2014.
 */
public class StackTraceVO {

    // non-null
    private String file;
    // non-null
    private String className;
    // non-null
    private String function;
    // non-null
    private int    line;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StackTraceVO)) return false;

        StackTraceVO that = (StackTraceVO) o;

        if (line != that.line) return false;
        if (!className.equals(that.className)) return false;
        if (!file.equals(that.file)) return false;
        if (!function.equals(that.function)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + className.hashCode();
        result = 31 * result + function.hashCode();
        result = 31 * result + line;
        return result;
    }

    @Override
    public String toString() {
        return "com.caibowen.prma.api.java.model.StackTraceVO{" +
                "file='" + file + '\'' +
                ", className='" + className + '\'' +
                ", function='" + function + '\'' +
                ", line=" + line +
                '}';
    }
}
