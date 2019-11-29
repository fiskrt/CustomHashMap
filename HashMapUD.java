public class HashMapUD<K, V> {
    private static final int DEFAULT_SIZE = 10;
    private int size = 0;
    private Entry<K, V>[] hashTable;
    private int threshold;
    private float maxLoadFactor = 0.7f;

    public HashMapUD(int initSize) {
        hashTable = new Entry[initSize];
        threshold = (int) (maxLoadFactor * initSize);
    }

    public HashMapUD() {
        this(DEFAULT_SIZE);
    }

    public static void main(String[] args) {
        HashMapS<String, Integer> map = new HashMapS<>();
        map.insert("filip4", 73);
        map.insert("filip5", 83);

        System.out.println(map);
        System.out.println("found val: " + map.lookup("lol"));
        System.out.println("found val: " + map.lookup("filip5"));
    }

    private int myHash(K key) {
        return Math.floorMod(key.hashCode(), hashTable.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------\n");
        for (Entry<K, V> e : hashTable) {
            sb.append("|");
            sb.append(e);
            sb.append("    |");
            sb.append("\n-----------\n");
        }
        return sb.toString();
    }

    /**
     * Insert value into the hash table with key.
     * TODO: If a key already exists, overwrite the value
     *
     * @param key
     * @param value
     */
    public void insert(K key, V value) {
        // Check if hash table should be resized
        System.out.println("size: " + size + " thr: " + threshold);
        if (size == threshold) {
            resizeTable(hashTable.length * 2);
        }

        int hash = myHash(key);
        Entry<K, V> baseEntry = hashTable[hash];

        if (baseEntry == null) {
            hashTable[hash] = new Entry<>(hash, key, value);
            size++;
            return;
        } else if (baseEntry.key.equals(key)){
            baseEntry.val = value;
            return;
        }

        if (baseEntry.up >= baseEntry.down) {
            // go up!
            baseEntry.incDown();
            Entry<K, V> e;
            while ((e = hashTable[hash]) != null) {
                if (e.key.equals(key)) {
                    e.val = value;
                    return;
                }
                hash = (hash + 1) % hashTable.length;
            }
        } else {
            baseEntry.incUp();
            Entry<K, V> e;
            while ((e = hashTable[hash]) != null) {
                if (e.key.equals(key)) {
                    e.val = value;
                    return;
                }
                hash = Math.floorMod(hash - 1, hashTable.length);
            }
        }
        hashTable[hash] = new Entry<>(hash, key, value);
        size++;
    }

    /**
     * Find a value from a key
     * <p>
     * If key is not found before an empty entry, go the other direction.
     * <p>
     * If both directions does not find a matching key it is not found.
     * <p>
     * TODO: First go in the direction with lowest value as it is most likely
     * to be found there.
     * <p>
     * TODO: go both ways same time?
     *
     * @param key
     * @return value corresponding to key, null if not found.
     */
    public V lookup(K key) {
        int base_hash = myHash(key);
        int modHash = base_hash;
        while (hashTable[modHash] != null) {
            if (hashTable[modHash].key.equals(key)) {
                return hashTable[modHash].val;
            }
            modHash = (modHash + 1) % hashTable.length;
        }

        modHash = (base_hash - 1) % hashTable.length;
        while (hashTable[modHash] != null) {
            if (hashTable[modHash].key.equals(key)) {
                return hashTable[modHash].val;
            }
            modHash = (modHash - 1) % hashTable.length;
        }
        return null;
    }

    /**
     * Make the hash table larger. To maintain its property all entries are rehashed.
     *
     * @param newSize
     */
    private void resizeTable(int newSize) {
        System.out.println("Resizing to: " + newSize);
        Entry<K, V> oldTable[] = hashTable;
        size = 0;
        threshold = (int) (newSize * maxLoadFactor);
        hashTable = new Entry[newSize];

        for (Entry<K, V> e : oldTable) {
            if (e == null)
                continue;
            insert(e.key, e.val);
        }
    }

    private class Entry<K, V> {
        int hash;
        K key;
        V val; // TODO: equals()?
        int up;
        int down;

        Entry(int hash, K key, V val) {
            this.hash = hash;
            this.key = key;
            this.val = val;
        }

        void incUp() {
            this.up++;
        }

        void incDown() {
            this.down++;
        }

        @Override
        public String toString() {
            return "<" + key + ", " + hash + ", " + val + ">";
        }
    }
}
