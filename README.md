# README

ToyCache是一个分布式内存数据库。大致仿照Redis的功能。支持快照、写日志、主从、hash集群(WIP)、事务；支持5种数据对象：字符串、列表、集合、映射、有序集。

## 开发进度

- [x] 基础API的支持
  - [x] 字符串
  - [x] 列表
  - [x] 集合
  - [x] 映射
  - [x] 有序集
- [x] 快照
- [x] 写日志
- [x] 事务
- [x] 主从
- [ ] 集群



## 总体设计



总体设计如下图所示

![](.\doc\mainDesign.png)

- 首先客户端服务器之间通过`Netty`进行通信，通信格式为`Protobuf`格式。

- `NettyServer`通过`ToyCacheMessageHandler`将收到的`Request`提交个`MemoryExecutor`，它是个单线程`Executor`

  ```java
  //服务器端业务处理器
  private class ToyCacheMessageHandler extends ChannelInboundHandlerAdapter
  {
      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
      {
          Proto.Request request = (Proto.Request) msg;
          //直接向ME提交消息就行, 并且放一个Callback
          log.info(request.toString());
          globalContext.getMemoryOperationExecutor().submit(request, new SendResponseCallback(ctx));
      }
  }
  ```

- `MemoryExecutor`根据请求的类型，调用不同的`Handler`来处理请求

- 而`Handler`通过访问`Storage`来获取或更改数据以完成请求。

- `MemoryExecutor`只进行内存相关的处理，涉及到硬盘或者网络通信的时候`MemoryExecutor`会把任务提交到其他专门的`Executor`来处理，例如`RDBExecutor`用于将快照存盘，`AOFExecutor`用于将写日志存盘等等。

- 此外，系统中还有一些需要定期执行的任务，这些任务都是通过`TickDriver`和`Ticker`之间的相互配合来完成的。



## 通信设计



首先客户端服务器之间通过`Netty`进行通信，通信格式为`Protobuf`格式。由于`Protobuf`没有继承，因此采用组合的方式来设计。具体通信格式如下

```protobuf
message Request {
  MessageType messageType = 1;
  int64 writeId = 2;
  string clientTId = 3;
  int64 epoch = 4;
  ExistsRequest existsRequest = 5;
  DelRequest delRequest = 6;
  GetRequest getRequest = 7;
  SetRequest setRequest = 8;
  ....
  InnerStartSyncRequest innerStartSyncRequest = 65;
  InnerRewriteLogRequest innerRewriteLogRequest = 66;
  InnerCreateClientRequest innerCreateClientRequest = 67;
  InnerCreateFollowerToZKRequest innerCreateFollowerToZKRequest = 68;
}
```

- 每种`Request`有其对应的`MessageType`，例如`GetRequest`的`MessageType`就是`Get`，此时`GetRequest`属性也不为空。



`Response`与`Request`类似

```java
message Response {
  MessageType messageType = 1;
  ResponseCode responseCode = 2;
  int64 writeId = 3;
  string clientTId = 4;
  ExistsResponse existsResponse = 5;
  DelResponse delResponse = 6;
  GetResponse getResponse = 7;
  SetResponse setResponse = 8;
  ExpireResponse expireResponse = 9;
  SaveResponse saveResponse = 10;
    ...
}
```

- 其中多了一个`ResponseCode`属性，它用来标记命令的执行情况，如

  ```protobuf
    Unknown = 0;
    // 成功
    OK = 1;
    // 非法的参数
    InvalidParam = 2;
  ```

## `Executor`设计



`Executor`接口都是在单线程或者线程池中执行某一种任务，其中最关键的就是`MemoryExecutor`，它用来操纵内存，`NettyServer`收到的`Request`会提交给`MemoryExecutor`执行，执行完成之后，会通过`Callback`将`Response`传回`NettyServer`。一些内部的定时任务也会提交`Request`给`MemoryExecutor`执行。



我们先看`Executor`接口

```java
public interface MessageExecutor {
    void submit(Proto.Request request, Callback... callbacks);

    void submit(Proto.Request request);

    void submitAndWait(Proto.Request request, Callback... callbacks) throws Exception;

    void submitAndWait(Proto.Request request) throws Exception;
}
```

由于大多数`Executor`功能都差不多，因此抽象出来一个抽象类，来放公共逻辑。

```java
@Slf4j
public abstract class AbstractMessageExecutor implements MessageExecutor{

    private final GlobalContext globalContext;
    private final ExecutorService executorService;

    protected AbstractMessageExecutor(GlobalContext globalContext, ExecutorService executorService) {
        this.globalContext = globalContext;
        this.executorService = executorService;
    }

    public void submit(Proto.Request request, Callback... callbacks){
        beforeSubmit(request);
        executorService.submit(new RunnableCommand(request, callbacks));
    }

    public void submit(Proto.Request request){
        beforeSubmit(request);
        executorService.submit(new RunnableCommand(request));
    }

    public void submitAndWait(Proto.Request request, Callback... callbacks) throws Exception{
        beforeSubmit(request);
        executorService.submit(new RunnableCommand(request, callbacks)).get();

    }

    public void submitAndWait(Proto.Request request) throws Exception{
        beforeSubmit(request);
        executorService.submit(new RunnableCommand(request)).get();
    }

    protected GlobalContext getGlobalContext(){
        return globalContext;
    }

    protected ExecutorService getExecutorService(){
        return executorService;
    }

    protected void beforeSubmit(Proto.Request request){

    }

    public abstract Proto.Response doRequest(Proto.Request request) throws Exception;

    @RequiredArgsConstructor
    @AllArgsConstructor
    private class RunnableCommand implements Runnable {

        @NonNull
        private final Proto.Request request;
        private Callback[] callbacks = new Callback[0];

        @Override
        public void run() {
            try{
                Proto.Response response = doRequest(request);
                Arrays.stream(callbacks).forEach(callback -> {
                    callback.callback(request, response);
                });
            } catch (Exception e){
                //todo 这样处理Exception太暴力了
                log.error("RunnableCommand", e);
                e.printStackTrace();
            }

        }
    }
}
```

- 可以看到这里开了一个单线程的`ExecutorService`，它处理`Request`，处理完成之后调用`Callback`。



而具体的MemoryExecutor，只需要实现`doRequest`方法即可

```java
@Slf4j
public class MemoryOperationExecutor extends AbstractMessageExecutor {

    public MemoryOperationExecutor(GlobalContext globalContext) {
        super(globalContext, Executors.newSingleThreadExecutor());
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) {
        Handler handler = getGlobalContext().getHandlerFactory()
                .getHandler(request.getMessageType())
                .orElseThrow(UnsupportedOperationException::new);
        return handler.handle(request);
    }
}
```

- 可以看到，它只负责从`handlerFactory`中挑选合适的`Handler`来处理`Request`。

我们将在下一小节讨论`Handler`的设计。



## `Handler`设计



前文提到`MemoryExecutor`负责从`HandlerFactory`中挑选合适的`Handler`来处理`Request`。那么什么叫合适的`Handler`呢？很简单，每种`MessageType`都对应一个特定的`Handler`，例如`Get`对应`GetHandler`。

```java
@HandlerAnnotation(messageType = Proto.MessageType.Get, handlerType= HandlerType.Read)
public class GetHandler extends AbstractGenericsHandler<Proto.GetRequest, Proto.GetResponse> {

    public GetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.GetResponse doHandle(Proto.GetRequest getRequest) throws Exception {
        Proto.GetResponse.Builder getResponseBuilder = Proto.GetResponse.newBuilder();
        getGlobalContext().getStorage().get(getRequest.getKey()).ifPresent(getResponseBuilder::setValue);
        return getResponseBuilder.build();
    }
}
```



而系统启动时,`HandlerFactory`通过扫描`Handler`上的`@HandlerAnnotation`将这些`Handler`全部注册到一个`Map`中，具体可以看下面这个`registerAll()`。

```java
@Slf4j
public class HandlerFactory {

    private final Map<Proto.MessageType, Handler> m;

    ...
        
    public void registerAll(GlobalContext globalContext){
        Reflections reflections = new Reflections("com.t0ugh.server.handler");
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(HandlerAnnotation.class);
        classSet.forEach(clazz -> {
            try {
                Proto.MessageType messageType = clazz.getAnnotation(HandlerAnnotation.class).messageType();
                Constructor<?> cons = clazz.getConstructor(GlobalContext.class);
                register(messageType, (Handler) cons.newInstance(globalContext));
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                log.error("handler register failed", e);
            }
        });
    }

  ...
}
```



## 线程设计

本系统的线程设计参考了redis的线程设计思想：即将所有内存操作由一个线程来负责。系统中所有内存操作都由`MemoryExecutor`负责。然后其他费时的网络通信或者读写硬盘任务都交给特定的线程来做。这样既可以避免多线程导致的大量冲突，又将单线程的效率提升到较高水平。

目前为止，系统中大概有这些线程

1. `MemoryExecutor`: 负责内存操作
2. `WriteLogExecutor`: 负责将写日志存盘
3. `DBExecutor`: 负责将快照存盘
4. `CreateToyCacheClientExecutor`: 负责在`master`上创建`follower`的客户端，这涉及到网络连接，所以单独用一个线程处理
5. `SendSyncExecutor`: 负责与`zk`通信，更新`zk`上的元数据
6. 其他`Executor`：不一一列举，都是干杂活的基本上。
7. 其他组件使用的线程：例如`NettyServer`使用的`bossGroup`和`workerGroup`，`ZookeeperClient`使用的线程等等。



## 定时任务



系统中还有一些需要定期执行的任务，例如定期清理过期的`kv`对、定期向`zk`发心跳等，这些任务都是通过`TickDriver`来驱动的，`TickDriver`每隔固定的时间会`tick`一次，而积攒了足够数量的`tick`，对应的`Ticker`就会向对应的`Executor`提交请求来执行这些定时任务。



下面先看一下`Ticker`接口

```java
public interface Ticker {
    void tick();
}
```



然后我们举定期清理过期`kv`对的`Ticker`作为例子

```java
public class DeleteKeyTicker implements Ticker {
    private int count;
    private final ExecutorService executorService;
    private final GlobalContext globalContext;
    private final int interval;

    public DeleteKeyTicker(GlobalContext globalContext) {
        executorService = Executors.newSingleThreadExecutor();
        this.globalContext = globalContext;
        this.interval = globalContext.getConfig().getPeriodicalDeleteTick();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void shutdownNow() {
        executorService.shutdownNow();
    }

    @Override
    public void tick() {
        executorService.submit(() -> {
            count ++;
            if(count >= interval) {
                globalContext.getMemoryOperationExecutor().submit(MessageUtils.newInnerClearExpireRequest());
                count = 0;
            }
        });
    }
}
```

- 每当`count== interval`，`DeleteKeyTicker`就会向主线程提交一个`InnerClearExpireRequest`来清理过期的键。



那么问题来了，`tick()`方法是谁调用的呢

```java
public class TickDriverImpl implements TickDriver{
	...
    @Override
    public void start() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ticks.forEach(Ticker::tick);
            }
        }, 0, globalContext.getConfig().getTickInterval(), TimeUnit.MILLISECONDS);
    }
    ...
}
```

- `TickDriver`会定期调用`tick()`



而`TickDriver`和`Ticker`之间采用的是发布订阅模式

```java
TickDriverImpl tickDriver = new TickDriverImpl(globalContext);
globalContext.setTickDriver(tickDriver);
DeleteKeyTicker deleteKeyTicker = new DeleteKeyTicker(globalContext);
RewriteLogTicker rewriteLogTicker = new RewriteLogTicker(globalContext);
SyncFollowerTicker syncSlaveTicker = new SyncFollowerTicker(globalContext);
SaveTicker saveTicker = new SaveTicker(globalContext);
tickDriver.register(deleteKeyTicker);
tickDriver.register(rewriteLogTicker);
tickDriver.register(syncSlaveTicker);
tickDriver.register(saveTicker);
tickDriver.start();
```

- 当系统启动时，`BootStrap`会将所需`Ticker`全部注册到`TickDriver`上去。

对应类图如下

![](.\doc\tickClass.png)



## 事务设计



首先介绍客户端的API，以`i++`为例

```java
ToyCache toyCache = new ToyCache(ip, port);
Transaction transaction = toyCache.transcation();
int i = Integer.valueOf(toyCache.get("i"));
transaction.checkGet("i", String.parseFrom(i));
transaction.set("i", String.parseFrom(i + 1));
transaction.checkGet("i", String.parseFrom(i + 1));
transaction.set("i", String.parseFrom(i + 2));
boolean success = transaction.commit();
transaction.close();
```

这个API设计与redis不同，它是**通过`check`机制来实现**的

- 首先，提交的事务只有所有的`check`都符合，并且`set`不抛出异常才能成功，否则会失败并告知用户
- 对于每个读API，都有与之对应的checkAPI，例如`get`与`checkGet`，checkAPI用于判断在`get`到`checkGet`这段时间内数据是否发生了变更，如果不一致就会导致事务的失败。



接下来介绍一下服务端是如何实现。**服务端主要通过`RollBacker`来实现**，也就是对于事务，每提交一条写命令都会生成一个对应的`RollBacker`，它可以将系统置为这条命令没有应用之前的状态。而**当后面的`check`没有通过或者`set`抛出异常，则系统会通过`RollBacker`回滚这个事务之前的所有命令，让系统回归到应用事务之前的状态**。



`RollBacker`接口如下

```java
public interface RollBacker {
    void beforeHandle(Proto.Request request);
    void rollBack();
}
```



一个具体的`RollBacker`实现

```java
@RollBackerAnnotation(messageType = Proto.MessageType.LTrim)
public class LTrimRollBacker extends AbstractListRollBacker {

    private List<String> headList = Lists.newArrayList();
    private List<String> tailList = Lists.newArrayList();

    public LTrimRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        getGlobalContext().getStorage().lPush(getKey(), headList);
        getGlobalContext().getStorage().rPush(getKey(), tailList);
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.LTrimRequest req = request.getLTrimRequest();
        headList = getGlobalContext().getStorage().lRange(req.getKey(),0,req.getStart());
        headList.remove(headList.size() - 1);
        tailList = getGlobalContext().getStorage().lRange(req.getKey(),req.getEnd(),-1);
        tailList.remove(0);
    }
}
```



事务的具体处理是在`MultiHandler`中的，下面截取一小段关键代码

```java
        Stack<RollBacker> rollBackerStack = new Stack<>();
        Proto.MultiResponse.Builder multiResponseBuilder = Proto.MultiResponse.newBuilder();
        multiResponseBuilder.setPass(true);
        StateUtils.startTransaction(globalContext);
        for (Proto.Request currReq : multiRequest.getRequestsList()) {
            // 先判断事务支不支持这种MessageType, 如果不支持直接break
            if (!MessageUtils.isTransactionSupported(currReq.getMessageType(), globalContext.getHandlerFactory())) {
                MessageUtils.messageTypeNotSupportedMultiResponseBuilder(multiResponseBuilder, currReq);
                break;
            }
            // 如果是写命令需要创建一个RollBacker并且压入栈中
            if (MessageUtils.isWriteRequest(currReq.getMessageType(), globalContext.getHandlerFactory())) {
                RollBacker rollBacker = globalContext.getRollBackerFactory().getRollBacker(currReq.getMessageType())
                        .orElseThrow(UnsupportedOperationException::new);
                // todo: 这里就可能抛出异常，比如ValueTypeNotMatch，此时应该终止整个事务
                rollBacker.beforeHandle(currReq);
                rollBackerStack.push(rollBacker);
            }
            Proto.Response currResp = globalContext.getHandlerFactory().getHandler(currReq.getMessageType())
                    .orElseThrow(UnsupportedOperationException::new).handle(currReq);
            // 然后检查currResp是否OK, 如果不OK就break
            if (!Objects.equals(Proto.ResponseCode.OK, currResp.getResponseCode())) {
                MessageUtils.failMultiResponseBuilder(multiResponseBuilder, currReq, currResp);
                break;
            }
            // ok了就把Response添加一下
            multiResponseBuilder.addResponses(currResp);
        }
```





## 主从设计

借助zk来实现主从设计。

zk中节点设计如下

```java
- \toycache
   - \group1
     - \master: serverMata{serverId = %d, ..., epoch = %d}
	 - \followers:
		- \follower1:serverMata{serverId = %d, ..., epoch = %d}
        - \follower2:serverMata{serverId = %d, ..., epoch = %d}
        - ...
   - \group2
     - ... 
   - ...         
```

- 其中`master`是临时节点，断线就会消失
- `follower%d`也是临时节点

主节点、从节点、zk之间的通信如下图所示

![](.\doc\masterfollower.png)

首先，主节点和从节点都会定期向zk发HeartBeat来进行元数据同步，元数据如下

```protobuf
message ServerMeta {
    uint64 serverId = 1;
    uint64 epoch = 2;
    uint64 lastWriteId = 3;
    uint64 groupId = 4;
    string serverIp = 5;
    int32 serverPort = 6;
}
```

主节点会监听`\follower`路径的变化，当有节点新增、变更、删除后，主节点会更新本地的对应元数据

主节点会根据各个从节点的`lastWriteId`号来定期向从节点同步新的命令，从节点收到新命令后会进行一些逻辑判断然后会应用它们。当从节点发现已经应用的命令与主节点不同时，会回滚命令来保证与主节点完全一致。

从节点会监听`\master`路径的变化，一旦主节点掉线，从节点会获取所有其他从节点的`lastWriteId`数据然后拥有最大的`lastWriteId`并且最快到达的从节点会当选为新的主节点。



## 集群设计



还没写，先占个坑
