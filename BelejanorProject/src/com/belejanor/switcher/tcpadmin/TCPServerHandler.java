package com.belejanor.switcher.tcpadmin;

import io.netty.channel.ChannelHandler.Sharable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.AdminConcurrentMap;
import com.belejanor.switcher.cscoreswitch.ContainerIsoQueue;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@Sharable
public class TCPServerHandler extends ChannelInboundHandlerAdapter {
	
	private Logger log;
	public TCPServerHandler(){
		
		log = new Logger();
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
		
		ByteBuf in = (ByteBuf) msg;
	    String msgIso = in.toString(io.netty.util.CharsetUtil.US_ASCII);
	    log.WriteLogMonitor("[TCP] Recibido!!! ====>>>>   " + msgIso , TypeMonitor.monitor, null);
	    String secuencial = msgIso.substring(35, 49) + "_" + msgIso.substring(29,35);
		ContainerIsoQueue<Object> cont = new ContainerIsoQueue<>(msgIso ,"127.0.0.1");
		Queue queue = new Queue();
		queue.SendMessage(typeMessage.initialMessage, cont, 1, 0);
		
		final Response res = new Response();
		final CountDownLatch semaphore = new CountDownLatch(1);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				
				while (true) {
					synchronized(MemoryGlobal.concurrentIso) {
						if(MemoryGlobal.concurrentIso.containsKey(secuencial)){
							res.setMessResponse((String) MemoryGlobal.concurrentIso.get(secuencial));
							log.WriteLogMonitor("Encontrado ====>>>>   " + res.getMessResponse() + " --> Sec: " + secuencial , TypeMonitor.monitor, null);
							@SuppressWarnings("unused")
							AdminConcurrentMap map = new AdminConcurrentMap(secuencial);
							semaphore.countDown();
							break;
						}
					}
				}
			}
		});
		t.start();
		if(!semaphore.await(120000, TimeUnit.MILLISECONDS)){
			res.setMessResponse("TIMEOUT-ERROR");
		}
		log.WriteLogMonitor("A enviar!!! ====>>>>   " + res.getMessResponse() + " --> Sec: " + secuencial , TypeMonitor.monitor, null);
	    ctx.write(Unpooled.copiedBuffer(res.getMessResponse(), CharsetUtil.US_ASCII));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        ctx.close();
    }
    
    
}
