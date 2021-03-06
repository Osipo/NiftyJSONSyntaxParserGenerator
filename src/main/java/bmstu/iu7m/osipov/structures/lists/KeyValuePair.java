package bmstu.iu7m.osipov.structures.lists;

import java.util.Map;

/* Implements Map.Entry. */
public class KeyValuePair<K,V> implements Map.Entry<K,V> {

    private K key;
    private V val;

    public KeyValuePair(K key, V val){
        this.key = key;
        this.val = val;
    }
    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return val;
    }

    @Override
    public V setValue(V value) {
        this.val = value;
        return value;
    }

    public void setKey(K key){
        this.key = key;
    }
}
