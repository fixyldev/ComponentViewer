package dev.fixyl.componentviewer.util;

import java.util.Objects;
import java.util.function.Supplier;

public class ResultCache<T> {

    private T result;
    private int hashCode;
    private boolean empty;

    public ResultCache() {
        this.empty = true;
    }

    public T cache(Supplier<T> resultSupplier, Object... arguments) {
        int newHashCode = Objects.hash(arguments);

        if (!this.empty && newHashCode == this.hashCode) {
            return this.result;
        }

        this.result = resultSupplier.get();
        this.hashCode = newHashCode;
        this.empty = false;

        return this.result;
    }

    public void clear() {
        this.empty = true;
        this.result = null;
    }
}
