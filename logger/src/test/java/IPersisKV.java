import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 *
 * Persistent key / value map.
 *
 *
 * @author BowenCai
 * @since 22-10-2014.
 */
public interface IPersisKV<K, V> {

    boolean
    hasKey(@Nonnull K key);

    boolean
    hasVal(@Nonnull V val);

    @Nullable V
    get(@Nonnull K key);

    @Nonnull Set<K>
    keys();

    @Nonnull Set<V>
    values();

    @Nonnull V
    putIfAbsent(@Nonnull K key, @Nonnull V value);

    @Nonnull V
    update(@Nonnull K key, @Nonnull V value);

    @Nullable V
    remove(@Nonnull K key, boolean returnVal);
}






