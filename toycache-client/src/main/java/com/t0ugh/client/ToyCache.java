package com.t0ugh.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.callback.Callback;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class ToyCache {

    private GlobalContext context;
    private String serverIp;
    private int serverPort;

    public ToyCache(String serverIp, int serverPort){
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        init();
    }

    private void init(){
        context = GlobalContext.builder().build();
        context.setDispatcher(new Dispatcher(context));
        NettyClient nettyClient = new NettyClient(serverIp, serverPort, context);
        Channel channel = nettyClient.connect();
        context.setChannel(channel);
    }

    public boolean exists(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Exists)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setExistsRequest(Proto.ExistsRequest.newBuilder().setKey(key).build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getExistsResponse().getExists();
    }

    public boolean del(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Del)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setDelRequest(Proto.DelRequest.newBuilder().setKey(key).build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getDelResponse().getOk();
    }

    public boolean set(String key, String value) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setSetRequest(Proto.SetRequest.newBuilder().setValue(key).setValue(value).build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getSetResponse().getOk();
    }

    public String get(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Get)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setGetRequest(Proto.GetRequest.newBuilder().setKey(key).build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getGetResponse().getValue();
    }

    public boolean expire(String key, long expireTime) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Expire)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setExpireRequest(Proto.ExpireRequest.newBuilder().setKey(key).setExpireTime(expireTime).build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getExpireResponse().getOk();
    }

    public boolean save() throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Save)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setSaveRequest(Proto.SaveRequest.newBuilder().build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getSaveResponse().getOk();
    }

    public boolean rewriteLog() throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.RewriteLog)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setRewriteLogRequest(Proto.RewriteLogRequest.newBuilder().build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getRewriteLogResponse().getOk();
    }

    public int sAdd(String key, Set<String> setValue) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SAdd)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setSAddRequest(Proto.SAddRequest.newBuilder()
                        .setKey(key)
                        .addAllSetValue(setValue)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getSAddResponse().getAdded();
    }

    public int sCard(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SCard)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setSCardRequest(Proto.SCardRequest.newBuilder().setKey(key).build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getSCardResponse().getSize();
    }

    public boolean sIsMember(String key, String member) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SIsMember)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setSIsMemberRequest(Proto.SIsMemberRequest.newBuilder()
                        .setKey(key)
                        .setMember(member)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getSIsMemberResponse().getIsMember();
    }

    public Set<String> sMembers(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SMembers)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setSMembersRequest(Proto.SMembersRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return Sets.newHashSet(response.getSMembersResponse().getSetValueList());
    }

    public Set<String> sPop(String key, int count) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SPop)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setSPopRequest(Proto.SPopRequest.newBuilder()
                        .setKey(key)
                        .setCount(count)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return Sets.newHashSet(response.getSPopResponse().getSetValueList());
    }

    public int sRem(String key, Set<String> members) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SRem)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setSRemRequest(Proto.SRemRequest.newBuilder()
                        .setKey(key)
                        .addAllMembers(members)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getSRemResponse().getDeleted();
    }

    public Set<String> sRandMember(String key, int count) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SRandMember)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setSRandMemberRequest(Proto.SRandMemberRequest.newBuilder()
                        .setKey(key)
                        .setCount(count)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return Sets.newHashSet(response.getSRandMemberResponse().getSetValueList());
    }

    public int lPush(String key, List<String> values) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LPush)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setLPushRequest(Proto.LPushRequest.newBuilder()
                        .setKey(key)
                        .addAllValue(values)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getLPushResponse().getSize();
    }

    public String lIndex(String key, int index) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LIndex)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setLIndexRequest(Proto.LIndexRequest.newBuilder()
                        .setKey(key)
                        .setIndex(index)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getLIndexResponse().getValue();
    }

    public int lLen(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LLen)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setLLenRequest(Proto.LLenRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getLLenResponse().getCount();
    }

    public String lPop(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LPop)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setLPopRequest(Proto.LPopRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getLPopResponse().getValue();
    }

    public List<String> lRange(String key, int start, int end) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LRange)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setLRangeRequest(Proto.LRangeRequest.newBuilder()
                        .setKey(key)
                        .setStart(start)
                        .setEnd(end)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return Lists.newArrayList(response.getLRangeResponse().getValuesList());
    }

    public boolean lTrim(String key, int start, int end) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LTrim)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setLTrimRequest(Proto.LTrimRequest.newBuilder()
                        .setKey(key)
                        .setStart(start)
                        .setEnd(end)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getLTrimResponse().getOk();
    }

    public boolean lSet(String key, int index, String value) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LSet)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setLSetRequest(Proto.LSetRequest.newBuilder()
                        .setKey(key)
                        .setIndex(index)
                        .setNewValue(value)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getLSetResponse().getOk();
    }

    public boolean hSet(String key, String field, String value) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HSet)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setHSetRequest(Proto.HSetRequest.newBuilder()
                        .setKey(key)
                        .setField(field)
                        .setValue(value)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getHSetResponse().getOk();
    }

    public boolean hExists(String key, String field) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HExists)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setHExistsRequest(Proto.HExistsRequest.newBuilder()
                        .setKey(key)
                        .setField(field)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getHExistsResponse().getOk();
    }

    public String hGet(String key, String field) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HGet)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setHGetRequest(Proto.HGetRequest.newBuilder()
                        .setKey(key)
                        .setField(field)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getHGetResponse().getValue();
    }

    public Map<String, String> hGetAll(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HGetAll)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setHGetAllRequest(Proto.HGetAllRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getHGetAllResponse().getKvsMap();
    }

    public Set<String> hKeys(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HKeys)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setHKeysRequest(Proto.HKeysRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return Sets.newHashSet(response.getHKeysResponse().getFieldsList());
    }

    public int hLen(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HLen)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setHLenRequest(Proto.HLenRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getHLenResponse().getLen();
    }

    public int hDel(String key, Set<String> fields) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HDel)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setHDelRequest(Proto.HDelRequest.newBuilder()
                        .setKey(key)
                        .addAllFields(fields)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getHDelResponse().getDeleted();
    }

    public int zAdd(String key, List<DBProto.ComparableString> values) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZAdd)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setZAddRequest(Proto.ZAddRequest.newBuilder()
                        .setKey(key)
                        .addAllValues(values)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getZAddResponse().getAdded();
    }

    public int zCard(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZCard)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setZCardRequest(Proto.ZCardRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getZCardResponse().getCount();
    }

    public int zCount(String key, double max, double min) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZCount)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setZCountRequest(Proto.ZCountRequest.newBuilder()
                        .setKey(key)
                        .setMax(max)
                        .setMin(min)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getZCardResponse().getCount();
    }

    public Optional<Integer> zRank(String key, String member) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRank)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setZRankRequest(Proto.ZRankRequest.newBuilder()
                        .setKey(key)
                        .setMember(member)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        if (!response.getZRankResponse().getExists()){
            return Optional.empty();
        }
        return Optional.of(response.getZRankResponse().getRank());
    }

    public List<DBProto.ComparableString> zRange(String key, int start, int end) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRange)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setZRangeRequest(Proto.ZRangeRequest.newBuilder()
                        .setKey(key)
                        .setStart(start)
                        .setEnd(end)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getZRangeResponse().getValuesList();
    }

    public List<DBProto.ComparableString> zRangeByScore(String key, double max, double min) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRangeByScore)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setZRangeByScoreRequest(Proto.ZRangeByScoreRequest.newBuilder()
                        .setKey(key)
                        .setMax(max)
                        .setMin(min)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getZRangeResponse().getValuesList();
    }

    public int zRem(String key, Set<String> members) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRem)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setZRemRequest(Proto.ZRemRequest.newBuilder()
                        .setKey(key)
                        .addAllMembers(members)
                        .build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getZRemResponse().getDeleted();
    }

    public Transaction transaction() {
        return new Transaction(context);
    }

    public void talkAsync(Proto.Request request, List<Callback> callbacks){
        context.getDispatcher().talkAsync(request, callbacks);
    }

    public Proto.Response talkSync(Proto.Request request) throws InterruptedException {
        return context.getDispatcher().talkSync(request);
    }

}
