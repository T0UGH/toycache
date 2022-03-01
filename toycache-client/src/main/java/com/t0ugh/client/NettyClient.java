package com.t0ugh.client;

import com.t0ugh.sdk.proto.Proto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
public class NettyClient {

    //服务器端业务处理器
    @AllArgsConstructor
    private class ToyCacheMessageHandler extends ChannelInboundHandlerAdapter{

        private GlobalContext globalContext;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
        {
            Proto.Response response = (Proto.Response) msg;
            globalContext.getDispatcher().dispatch(response);
        }
    }

    private int serverPort;
    private String serverIp;
    private GlobalContext globalContext;
    private EventLoopGroup workerLoopGroup;
    Bootstrap b = new Bootstrap();

    public NettyClient(String ip, int port, GlobalContext globalContext)
    {
        this.serverPort = port;
        this.serverIp = ip;
        this.globalContext = globalContext;
    }

    public Channel connect() {
        //创建reactor 线程组
        workerLoopGroup = new NioEventLoopGroup();

        try
        {
            //1 设置reactor 线程组
            b.group(workerLoopGroup);
            //2 设置nio类型的channel
            b.channel(NioSocketChannel.class);
            //3 设置监听端口
            b.remoteAddress(serverIp, serverPort);
            //4 设置通道的参数
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            //5 装配子通道流水线
            b.handler(new ChannelInitializer<SocketChannel>()
            {
                //有连接到达时会创建一个channel
                protected void initChannel(SocketChannel ch) throws Exception
                {
                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    ch.pipeline().addLast(new ProtobufDecoder(Proto.Response.getDefaultInstance()));
                    ch.pipeline().addLast(new ProtobufEncoder());
                    ch.pipeline().addLast(new ToyCacheMessageHandler(globalContext));
                }
            });
            ChannelFuture f = b.connect();
            f.addListener((ChannelFuture futureListener) ->
            {
                if (futureListener.isSuccess())
                {
                    log.info("EchoClient客户端连接成功!");

                } else
                {
                    log.info("EchoClient客户端连接失败!");
                }

            });

            // 阻塞,直到连接完成
            f.sync();
            return f.channel();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close(){
        workerLoopGroup.shutdownGracefully();
    }
}
