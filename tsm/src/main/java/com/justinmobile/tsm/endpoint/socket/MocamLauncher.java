package com.justinmobile.tsm.endpoint.socket;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class MocamLauncher implements InitializingBean {

	protected static Logger logger = LoggerFactory.getLogger(MocamLauncher.class);
	
	private int port = 8888;
	
//	@Autowired
//	private MocamHandler mocamHandler;
	
	public static void main(String[] args) throws Exception {
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		// Add Handler
		//acceptor.setHandler(new MocamHandler());
		//转成字符串
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("utf-8"))));
		// Create Session Configuration
		SocketSessionConfig cfg = acceptor.getSessionConfig();
		cfg.setReuseAddress(true);
		logger.info("Starting Server......");
		// Bind and be ready to listen
		acceptor.bind(new InetSocketAddress(8888));
		logger.info("Server listening on " + 8888);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		// Add Handler
		//acceptor.setHandler(mocamHandler);
		//转成字符串
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("utf-8"))));
		// Create Session Configuration
		SocketSessionConfig cfg = acceptor.getSessionConfig();
		cfg.setReuseAddress(true);
		logger.info("Starting Server......");
		// Bind and be ready to listen
		acceptor.bind(new InetSocketAddress(this.getPort()));
		logger.info("Server listening on " + this.getPort());
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
