package com.t0ugh.server.storage;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgressiveReHashMap<K,V> extends AbstractMap<K,V> implements Map<K,V> {

    /**
     * rehash 的下标
     *
     * 如果 rehashIndex != -1，说明正在进行 rehash
     */
    private int rehashIndex = -1;

    /**
     * 容量
     * 默认为 8
     */
    private int capacity;

    /**
     * 处于 rehash 状态的容量
     */
    private int rehashCapacity;

    /**
     * 统计大小的信息
     */
    private int size = 0;

    /**
     * 阈值
     * 阈值=容量*factor
     * 暂时不考虑最大值的问题
     * 当达到这个阈值的时候，直接进行两倍的容量扩充+rehash。
     */
    private final double factor = 1.0;

    /**
     * 用来存放信息的 table 数组。
     * 数组：数组的下标是一个桶，桶对应的元素 hash 值相同。
     * 桶里放置的是一个链表。
     *
     * 可以理解为 table 是一个 ArrayList
     * arrayList 中每一个元素，都是一个 DoubleLinkedList
     */
    private List<List<Entry<K, V>>> table;

    /**
     * 渐进式 rehash 时，用来存储元素信息使用。
     *
     */
    private List<List<Entry<K, V>>> rehashTable;

    public ProgressiveReHashMap() {
        this(8);
    }

    public ProgressiveReHashMap(int capacity) {
        this.capacity = capacity;
        // 初始化最大为容量的个数，如果 hash 的非常完美的话。
        this.table = new ArrayList<>(capacity);
        // 初始化为空列表
        for(int i = 0; i < capacity; i++) {
            this.table.add(i, new LinkedList<>());
        }
        this.rehashIndex = -1;
        this.rehashCapacity = -1;
        this.rehashTable = null;
    }

    /**
     * put 一个值
     *
     * 1 如果不处于 rehash 阶段
     * 1.1 判断是否为 table 更新，如果是，则进行更新
     * 1.2 如果不是更新，则进行插入
     *
     * 1.3 插入的时候可能触发 rehash
     *
     * 2 如果处于 rehash 阶段
     *
     * 2.0 执行一次渐进式 rehash 的动作
     *
     * 2.1 判断是否为更新，需要遍历 table 和 rehashTable
     * 如果是，执行更新
     *
     * 2.2 如果不是，则执行插入
     * 插入到 rehashTable 中
     *
     * @param key 键
     * @param value 值
     * @return 值
     */
    @Override
    public V put(K key, V value) {
        boolean isInRehash = isInReHash();
        if(!isInRehash) {
            // 1.1 是否为更新
            Entry<Boolean, V> pair = updateTableInfo(key, value, this.table, this.capacity);
            if(pair.getKey()) {
                return pair.getValue();
            } else {
                // 1.2 插入
                return this.createNewEntry(key, value);
            }
        } else {
            // 2.0 执行一个附加操作，进行渐进式 rehash 处理
            rehashToNew();
            //2.1 是否为 table 更新
            Entry<Boolean, V> pair = updateTableInfo(key, value, this.table, this.capacity);
            if(pair.getKey()) {
                return pair.getValue();
            }
            // 2.2 是否为 rehashTable 更新
            Entry<Boolean, V> pair2 = updateTableInfo(key, value, this.rehashTable, this.rehashCapacity);
            if(pair2.getKey()) {
                return pair2.getValue();
            }
            // 2.3 插入
            return this.createNewEntry(key, value);
        }
    }

    /**
     * 是否处于 rehash 阶段, 就是判断 rehashIndex 是否为 -1
     */
    private boolean isInReHash() {
        return rehashIndex != -1;
    }

    /**
     * 是否为更新信息, 这里为了复用，对方法进行了抽象。可以同时使用到 table 和 rehashTable 中
     * @param key key
     * @param value value
     * @param table table 信息
     * @param tableCapacity table 的容量（使用 size 也可以，因为都默认初始化了。）
     * @return 更新结果
     */
    private Entry<Boolean, V> updateTableInfo(K key, V value, final List<List<Entry<K,V>>> table,
                                             final int tableCapacity) {
        // 计算 index 值
        int hash = ProgressiveReHashMap.hash(key);
        int index = ProgressiveReHashMap.indexFor(hash, tableCapacity);
        // 判断是否为替换
        if(index >= table.size()){
            return new SimpleEntry<>(false, null);
        }
        // 遍历
        for(Entry<K,V> entry : table.get(index)) {
            // 二者的 key 都为 null，或者二者的 key equals()
            final K entryKey = entry.getKey();
            if(Objects.equals(key, entryKey)) {
                final V oldValue = entry.getValue();
                // 更新新的 value
                entry.setValue(value);
                return new SimpleEntry<>(true, oldValue);
            }
        }
        return new SimpleEntry<>(false, null);
    }

    /**
     * 创建一个新的Entry
     *
     * （1）如果处于渐进式 rehash 中，则设置到 rehashTable 中
     * （2）如果不是，则判断是否需要扩容
     *
     * 2.1 如果扩容，则直接放到 rehashTable 中。
     * 因为我们每次扩容内存翻倍，一次只处理一个 index 的信息，所以不会直接 rehash 结束，直接放到新的 rehashTable 中即可
     * 2.2 如果不扩容，则放入 table 中
     *
     * @param key key
     * @param value value
     * @return value
     */
    private V createNewEntry(final K key,
                             final V value) {
        Entry<K,V> entry = new SimpleEntry<>(key, value);
        // 重新计算 tableIndex
        int hash = ProgressiveReHashMap.hash(key);
        //是否处于 rehash 中？
        if(isInReHash()) {
            int index = ProgressiveReHashMap.indexFor(hash, this.rehashCapacity);
            List<Entry<K,V>> list = this.rehashTable.get(index);
            list.add(entry);
        }
        // 是否需要扩容 && 不处于渐进式 rehash
        // rehash 一定是扩容 rehashTable
        // 如果发生了 rehash，元素是直接放到 rehashTable 中的
        if(isNeedExpand()) {
            rehash();
            // 放入到 rehashTable 中
            int index = ProgressiveReHashMap.indexFor(hash, this.rehashCapacity);
            List<Entry<K,V>> list = this.rehashTable.get(index);
            list.add(entry);
        } else {
            // 目前不处于 rehash 中，元素直接插入到 table 中
            int index = ProgressiveReHashMap.indexFor(hash, this.capacity);
            List<Entry<K,V>> list = this.table.get(index);
            list.add(entry);
        }
        this.size++;
        return value;
    }

    static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    static int indexFor(int hash, int capacity){
        return hash % capacity;
    }

    /**
     * 是否需要扩容
     *
     * 比例满足，且不处于渐进式 rehash 中
     * @return 是否
     */
    private boolean isNeedExpand() {
        // 验证比例
        double rate = size * 1.0 / capacity;
        return rate >= factor && !isInReHash();
    }

    /**
     * 直接 rehash 的流程
     *
     * （1）如果处于 rehash 中，直接返回
     * （2）初始化 rehashTable，并且更新 rehashIndex=0;
     * （3）获取 table[0]，rehash 到 rehashTable 中
     * （4）设置 table[0] = new ArrayList();
     *
     */
    private void rehash() {
        if(isInReHash()) {
            return;
        }
        // 初始化 rehashTable
        this.rehashCapacity = 2 * capacity;
        // 一开始就把所有slot都创建好
        this.rehashTable = new ArrayList<>(this.rehashCapacity);
        for(int i = 0; i < rehashCapacity; i++) {
            rehashTable.add(i, new LinkedList<>());
        }

        // 遍历第一个元素，其他的进行渐进式更新。
        rehashToNew();
    }

    /**
     * 将信息从旧的 table 迁移到新的 table 中
     *
     * （1）table[rehashIndex] 重新 rehash 到 rehashTable 中
     * （2）设置 table[rehashIndex] = new ArrayList();
     * （3）判断是否完成渐进式 rehash
     */
    private void rehashToNew() {
        rehashIndex ++;

        List<Entry<K, V>> list = table.get(rehashIndex);
        for(Entry<K, V> entry : list) {
            int hash = ProgressiveReHashMap.hash(entry);
            int index = ProgressiveReHashMap.indexFor(hash, rehashCapacity);
            //  添加元素
            List<Entry<K,V>> newList = rehashTable.get(index);
            // 添加元素到列表
            // 元素不存在重复，所以不需要考虑更新
            newList.add(entry);
        }

        // 清空 index 处的信息
        table.set(rehashIndex, new LinkedList<>());

        // 判断大小是否完成 rehash
        // 验证是否已经完成
        if(rehashIndex == (table.size() - 1)) {
            this.capacity = this.rehashCapacity;
            this.table = this.rehashTable;
            this.rehashIndex = -1;
            this.rehashCapacity = -1;
            this.rehashTable = null;
        }
    }

    /**
     * 查询方法
     * （1）如果处于渐进式 rehash 状态，额外执行一次 rehashToNew()
     * （2）判断 table 中是否存在元素
     * （3）判断 rehashTable 中是否存在元素
     * @param key key
     * @return 结果
     */
    @Override
    public V get(Object key) {

        if(isInReHash()) {
            rehashToNew();
        }

        //1. 判断 table 中是否存在
        V result = getValue(key, this.table);
        if(result != null) {
            return result;
        }

        //2. 是否处于渐进式 rehash
        if(isInReHash()) {
            return getValue(key, this.rehashTable);
        }
        return null;
    }

    /**
     * 获取value, 这里为了复用，对方法进行了抽象。可以同时使用到 table 和 rehashTable 中
     * @param key key
     * @param table table 信息
     * @return value 如果不存在返回null
     */
    private V getValue(Object key, List<List<Entry<K, V>>> table){
        int hash = ProgressiveReHashMap.hash(key);
        int idx = ProgressiveReHashMap.indexFor(hash, table.size());
        List<Entry<K, V>> list = table.get(idx);
        for(Entry<K, V> entry : list) {
            if (entry.getKey().equals(key)){
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 执行一次渐进式rehash操作, 并且返回所有entry
     * @return set 返回map中所有的entry
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        if(isInReHash()) {
            rehashToNew();
        }

        Stream<Entry<K, V>> tableStream = table.stream().flatMap(Collection::stream);
        if(!isInReHash()){
            return tableStream.collect(Collectors.toSet());
        }

        Stream<Entry<K, V>> reHashStream = rehashTable.stream().flatMap(Collection::stream);
        return Stream.concat(tableStream, reHashStream).collect(Collectors.toSet());
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Map.super.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Map.super.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return Map.super.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }
}
