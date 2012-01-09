package com.em;
/*
 * author: 		guizy
 * Date:		2008-06-24
 * Last Modify:	2008-06-24
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnionSocket {
	private final int TIME_OUT=10*1000;

	private Socket sock;
	private InputStream is;
	private OutputStream os;
	private int iTimeOut=0;
	private boolean TimeOutFlag;
	private UnionUtil util=new UnionUtil();
	private boolean IsConn;

	private Log logger = LogFactory.getLog("com.em.UnionSocket");
	public UnionSocket()
	{
		sock=null;
		is=null;
		os=null;
		IsConn = false;
		iTimeOut=TIME_OUT;
		TimeOutFlag=false;
	}
	/**
	 *
	 * @param ip
	 * @param port
	 * @return
	 */
	protected boolean connectHSM(String ip,int port)
	{
		try{
			sock=new Socket();
			//sock.setTcpNoDelay(false);
			//sock.setSoLinger(true, 10);
			sock.connect( new InetSocketAddress(ip,port),iTimeOut);
			is=sock.getInputStream();
			os=sock.getOutputStream();
			IsConn=true;

		}catch(InterruptedIOException iioe){
			logger.error("连接超时:"+iioe.getMessage());
			TimeOutFlag=true;
			IsConn=false;
		}catch (Exception e){
			IsConn = false;
			logger.error("连接远程主机异常:"+e.getMessage());
		}finally{
			return IsConn;
		}
	}
	/**
	 *
	 * @param in
	 * @return
	 */
	protected String ExchangeData(String in)
	{
		String outstr=null;
		try{
			if(SendToHSM(in))
			{
				outstr = RecvFromHSM();
			}else{
				return null;
			}
		}catch (Exception e ){
			return null;
		}
		return outstr;
	}

	/**
	 *
	 * @param str
	 */
	private boolean SendToHSM ( String str ) throws IOException
	{
		try{
			logger.info("Send Length :["+str.length()+"]");
			logger.info("Send:   "+UnionUtil.Bytes2HexString(str.getBytes("ISO-8859-1")));
			os.write(str.getBytes("ISO-8859-1"));
			os.flush();
			return true;
		}catch (IOException e){
			logger.error("Send Error："+e.getMessage());
	 		return false;
	 	}
 	}
	/**
	 *
	 * @return
	 */
	private String RecvFromHSM()
	{
		String out= null;
		byte[] out1=null;
		byte[] out2=null;
		int i=0;
		try{
			out1=  new byte[2048];
			i=is.read(out1);
			out2=new byte[i-2];

			if(i>0){
				System.arraycopy(out1, 2, out2, 0, i-2);
                logger.info("Recieveout2.length: ["+out2.length+"]");
				out=new String(out2,"ISO-8859-1");
                logger.info("Recieve--- out Length: ["+out.length()+"] bytelength=["+out.getBytes("ISO-8859-1").length + "]");
				logger.info("Recieve Length: ["+i+"]");
				logger.info("Recieve: "+UnionUtil.Bytes2HexString(out.getBytes("ISO-8859-1")));
			}else{
				out=null;
				out1=null;
				out2=null;
			}

		}catch(Exception e){
			logger.error("接收数据异常:"+e.getMessage());
			return null;
		}
                logger.info("Recieve out Length: ["+out.length()+"]");
		return out;
	}
	/**
	 *
	 */
	protected void Close()
	 {
	   try{
		   os.close() ;
	       is.close() ;
	       sock.close();
	       this.IsConn=false;
	     }catch(Exception e){
	    	 logger.error("关闭连接发生异常");
	    	 this.IsConn=false;
	     }
	 }

	protected boolean IsConnected()
	{
		 return this.IsConn;
	}

	protected void setTimeOut(int iVal)
	{
		if(iVal>=0)
			this.iTimeOut=iVal;
		else
			this.iTimeOut=this.TIME_OUT;
	}

	protected boolean getTimeOutFlag()
	{
		return this.TimeOutFlag;
	}
}
