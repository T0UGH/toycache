package com.t0ugh.client;

import com.google.common.collect.Sets;
import com.t0ugh.client.Exception.TransactionClosedException;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Transaction {

    private boolean closed;
    private GlobalContext context;
    private Proto.MultiRequest.Builder requestBuilder;

    public Transaction(GlobalContext context) {
        this.context = context;
        requestBuilder = Proto.MultiRequest.newBuilder();
    }

    public void del(String key) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Del)
                .setDelRequest(Proto.DelRequest.newBuilder().setKey(key).build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void set(String key, String value) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder().setValue(key).setValue(value).build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void expire(String key, long expireTime) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Expire)
                .setExpireRequest(Proto.ExpireRequest.newBuilder().setKey(key).setExpireTime(expireTime).build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void sAdd(String key, Set<String> setValue) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SAdd)
                .setSAddRequest(Proto.SAddRequest.newBuilder()
                        .setKey(key)
                        .addAllSetValue(setValue)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void sPop(String key, int count) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SPop)
                .setSPopRequest(Proto.SPopRequest.newBuilder()
                        .setKey(key)
                        .setCount(count)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void sRem(String key, Set<String> members) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SRem)
                .setSRemRequest(Proto.SRemRequest.newBuilder()
                        .setKey(key)
                        .addAllMembers(members)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void lPush(String key, List<String> values) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LPush)
                .setLPushRequest(Proto.LPushRequest.newBuilder()
                        .setKey(key)
                        .addAllValue(values)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void lPop(String key) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LPop)
                .setLPopRequest(Proto.LPopRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void lTrim(String key, int start, int end) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LTrim)
                .setLTrimRequest(Proto.LTrimRequest.newBuilder()
                        .setKey(key)
                        .setStart(start)
                        .setEnd(end)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void lSet(String key, int index, String value) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LSet)
                .setLSetRequest(Proto.LSetRequest.newBuilder()
                        .setKey(key)
                        .setIndex(index)
                        .setNewValue(value)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void hSet(String key, String field, String value) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HSet)
                .setHSetRequest(Proto.HSetRequest.newBuilder()
                        .setKey(key)
                        .setField(field)
                        .setValue(value)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void hDel(String key, Set<String> fields) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HDel)
                .setHDelRequest(Proto.HDelRequest.newBuilder()
                        .setKey(key)
                        .addAllFields(fields)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void zAdd(String key, List<DBProto.ComparableString> values) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZAdd)
                .setZAddRequest(Proto.ZAddRequest.newBuilder()
                        .setKey(key)
                        .addAllValues(values)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void zRem(String key, Set<String> members) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRem)
                .setZRemRequest(Proto.ZRemRequest.newBuilder()
                        .setKey(key)
                        .addAllMembers(members)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkExists(String key, boolean exists){
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckExists)
                .setCheckExistsRequest(Proto.CheckExistsRequest.newBuilder()
                        .setKey(key)
                        .setExists(exists)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkGet(String key, String value) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckGet)
                .setCheckGetRequest(Proto.CheckGetRequest.newBuilder()
                        .setKey(key)
                        .setValue(value)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkLIndex(String key, int index, String value) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckLIndex)
                .setCheckLIndexRequest(Proto.CheckLIndexRequest.newBuilder()
                        .setKey(key)
                        .setIndex(index)
                        .setValue(value)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkLLen(String key, int len) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckLLen)
                .setCheckLLenRequest(Proto.CheckLLenRequest.newBuilder()
                        .setKey(key)
                        .setCount(len)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkLRange(String key, int start, int end, List<String> values) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckLRange)
                .setCheckLRangeRequest(Proto.CheckLRangeRequest.newBuilder()
                        .setKey(key)
                        .setStart(start)
                        .setEnd(end)
                        .addAllValues(values)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkHExists(String key, String field, boolean exists) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckHExists)
                .setCheckHExistsRequest(Proto.CheckHExistsRequest.newBuilder()
                        .setKey(key)
                        .setField(field)
                        .setExists(exists)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkHGet(String key, String field, String value) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckHGet)
                .setCheckHGetRequest(Proto.CheckHGetRequest.newBuilder()
                        .setKey(key)
                        .setField(field)
                        .setValue(value)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkHGetAll(String key, Map<String, String> kvs) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckHGetAll)
                .setCheckHGetAllRequest(Proto.CheckHGetAllRequest.newBuilder()
                        .setKey(key)
                        .putAllKvs(kvs)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkHKeys(String key, Set<String> fields) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckHKeys)
                .setCheckHKeysRequest(Proto.CheckHKeysRequest.newBuilder()
                        .setKey(key)
                        .addAllFields(fields)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkHLen(String key, int len) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckHLen)
                .setCheckHLenRequest(Proto.CheckHLenRequest.newBuilder()
                        .setKey(key)
                        .setLen(len)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkSIsMember(String key, String member, boolean isMember) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckSIsMember)
                .setCheckSIsMemberRequest(Proto.CheckSIsMemberRequest.newBuilder()
                        .setKey(key)
                        .setMember(member)
                        .setIsMember(isMember)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkSMembers(String key, Set<String> members) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckSMembers)
                .setCheckSMembersRequest(Proto.CheckSMembersRequest.newBuilder()
                        .setKey(key)
                        .addAllSetValue(members)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkZCard(String key, int count) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckZCard)
                .setCheckZCardRequest(Proto.CheckZCardRequest.newBuilder()
                        .setKey(key)
                        .setCount(count)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkZCount(String key, double min, double max, int count) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckZCount)
                .setCheckZCountRequest(Proto.CheckZCountRequest.newBuilder()
                        .setKey(key)
                        .setMax(max)
                        .setMin(min)
                        .setCount(count)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkZRange(String key, int start, int end, List<DBProto.ComparableString> values) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckZRange)
                .setCheckZRangeRequest(Proto.CheckZRangeRequest.newBuilder()
                        .setKey(key)
                        .setStart(start)
                        .setEnd(end)
                        .addAllValues(values)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkZRangeByScore(String key, double min, double max, List<DBProto.ComparableString> values) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckZRangeByScore)
                .setCheckZRangeByScoreRequest(Proto.CheckZRangeByScoreRequest.newBuilder()
                        .setKey(key)
                        .setMin(min)
                        .setMax(max)
                        .addAllValues(values)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public void checkZRank(String key, String member, boolean exists, int rank) {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.CheckZRank)
                .setCheckZRankRequest(Proto.CheckZRankRequest.newBuilder()
                        .setKey(key)
                        .setMember(member)
                        .setExists(exists)
                        .setRank(rank)
                        .build())
                .build();
        requestBuilder.addRequests(request);
    }

    public boolean commit() throws InterruptedException {
        assertTransactionClosed();
        Proto.Request request = Proto.Request.newBuilder()
                .setClientTId(context.getDispatcher().generateClientTId())
                .setMessageType(Proto.MessageType.Multi)
                .setMultiRequest(requestBuilder.build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        this.closed = true;
        return response.getMultiResponse().getPass();
    }

    public void rollBack(){
        assertTransactionClosed();
        this.closed = true;
    }

    private void assertTransactionClosed(){
        if (closed)
            throw new TransactionClosedException();
    }
}
