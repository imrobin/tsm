package com.justinmobile.tsm.endpoint.webservice.log;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;

import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

@NoJSR250Annotations
public class LoggingFormatInInterceptor extends AbstractLoggingInterceptor {

	public LoggingFormatInInterceptor() {
		super(Phase.RECEIVE);
	}

	public LoggingFormatInInterceptor(String phase) {
		super(phase);
	}

	public LoggingFormatInInterceptor(int lim) {
		this();
		limit = lim;
	}

	public LoggingFormatInInterceptor(PrintWriter w) {
		this();
		this.writer = w;
	}

	public void handleMessage(Message message) throws Fault {
		if (writer != null || LOG.isLoggable(Level.INFO)) {
			logging(message);
		}
	}

	protected void logging(Message message) throws Fault {
		if (message.containsKey(LoggingMessage.ID_KEY)) {
			return;
		}
		String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
		if (id == null) {
			id = LoggingMessage.nextId();
			message.getExchange().put(LoggingMessage.ID_KEY, id);
		}
		message.put(LoggingMessage.ID_KEY, id);
		final LoggingMessage buffer = new LoggingMessage("Inbound Message\n----------------------------", id);

		Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
		if (responseCode != null) {
			buffer.getResponseCode().append(responseCode);
		}

		String encoding = (String) message.get(Message.ENCODING);

		if (encoding != null) {
			buffer.getEncoding().append(encoding);
		}
		String ct = (String) message.get(Message.CONTENT_TYPE);
		if (ct != null) {
			buffer.getContentType().append(ct);
		}
		Object headers = message.get(Message.PROTOCOL_HEADERS);

		if (headers != null) {
			buffer.getHeader().append(headers);
		}
		String uri = (String) message.get(Message.REQUEST_URI);
		if (uri != null) {
			buffer.getAddress().append(uri);
		}

		InputStream is = message.getContent(InputStream.class);
		if (is != null) {
			CachedOutputStream bos = new CachedOutputStream();
			try {
				IOUtils.copy(is, bos);

				bos.flush();
				is.close();

				message.setContent(InputStream.class, bos.getInputStream());
				if (bos.getTempFile() != null) {
					// large thing on disk...
					buffer.getMessage().append("\nMessage (saved to tmp file):\n");
					buffer.getMessage().append("Filename: " + bos.getTempFile().getAbsolutePath() + "\n");
				}
				if (bos.size() > limit) {
					buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
				}
				StringBuilder payload = new StringBuilder();
				writePayload(payload, bos, encoding, ct);
				bos.close();
				buffer.getPayload().append(formatXml(payload.toString()));
			} catch (Exception e) {
				throw new Fault(e);
			}
		}
		log(buffer.toString());
	}
	
	protected String formatXml(String src) throws Exception {
		Document document = null;
		document = DocumentHelper.parseText(src);
		// 格式化输出格式
		OutputFormat format = OutputFormat.createPrettyPrint();
		StringWriter writer = new StringWriter();
		// 格式化输出流
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		// 将document写入到输出流
		xmlWriter.write(document);
		xmlWriter.close();
		return writer.toString();
	}
}
