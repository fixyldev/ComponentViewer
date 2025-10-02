package dev.fixyl.componentviewer.util;

import java.util.ArrayList;
import java.util.List;

public final class Lists {

    private Lists() {}

    /**
     * Check whether a given List is mutable.
     * <p>
     * Note: {@code false} is also returned if the given list
     * doesn't permit {@code null} elements or the
     * {@code addLast} operation.
     *
     * @param <T> the type of the list
     * @param list to check for
     * @return {@code true} if mutable, {@code false} otherwise
     */
    public static <T> boolean isMutable(List<T> list) {
        try {
            list.addLast(null);
        } catch (UnsupportedOperationException | NullPointerException e) {
            return false;
        }

        list.removeLast();
        return true;
    }

    /**
     * Returns a mutable {@link ArrayList} using the copy-constructor
     * if the given list is immutable. If the given list is already
     * mutable, the same instance is returned.
     *
     * @param <T> the type of the list
     * @param list either mutable or immutable
     * @return a mutable list with the same contents
     */
    public static <T> List<T> makeMutable(List<T> list) {
        return (Lists.isMutable(list)) ? list : new ArrayList<>(list);
    }
}
