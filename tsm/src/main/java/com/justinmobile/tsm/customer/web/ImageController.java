package com.justinmobile.tsm.customer.web;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.encode.EncodeUtils;
import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.core.utils.web.FileUploadUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.ServletUtils;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.customer.constant.City;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.manager.CustomerManager;

@Controller("ImageController")
@RequestMapping("/image/")
public class ImageController {
	private static final Logger log = LoggerFactory
			.getLogger(ImageController.class);
	@Autowired
	private CustomerManager customerManager;

	/**
	 * 客户端裁剪头像图片，然后交由后台来处理
	 * 
	 * @param request
	 * @param response
	 * @return JsonMessage对象<br/>
	 *         如果裁剪处理成功<br/>
	 *         message.tempDir 表示临时目录在服务器的绝对路径，用于后续请求删除临时目录<br/>
	 *         message.tempFileRalPath
	 *         表示临时文件在服务器的相对路径路径，用于前台使用临时文件，不包括ServletContext<br/>
	 *         message.tempFileAbsPath 表示临时文件在服务器的相对路径路径，用于后续请求处理临时文件
	 * 
	 */
	@RequestMapping
	public void cut(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JsonMessage result = new JsonMessage();
		try {
			String servletPath = request.getSession().getServletContext()
					.getRealPath("/");
			String srcPath = servletPath
					+ "temp"
					+ File.separator
					+ ServletRequestUtils.getStringParameter(request,
							"filename");
			int y = ServletRequestUtils.getDoubleParameter(request, "top")
					.intValue();
			int x = ServletRequestUtils.getDoubleParameter(request, "left")
					.intValue();
			int width = ServletRequestUtils.getDoubleParameter(request, "imgW")
					.intValue();
			int height = ServletRequestUtils
					.getDoubleParameter(request, "imgH").intValue();
			String avatarPath = servletPath + "images/headicon"
					+ File.separator;
			byte[] logo = cutImage(avatarPath, srcPath, x, y, width, height);
			if (log.isDebugEnabled()) {
				log.debug("\nservletPath: " + servletPath + "\n");
			}
			String userName = SpringSecurityUtils.getCurrentUserName();
			Customer customer = customerManager.getCustomerByUserName(userName);
			customer.setPcIcon(logo);
			customerManager.addCustomer(customer);
			Map<String, Object> message = new HashMap<String, Object>(2);
			message.put("avatarName", SpringSecurityUtils.getCurrentUserName()
					+ ".jpg");
			result.setSuccess(Boolean.TRUE);
			result.setMessage(message);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		String jsonString = JsonBinder.buildNormalBinder().toJson(result);
		if (log.isDebugEnabled()) {
			log.debug("\njsonString: " + jsonString + "\n");
		}
		ServletUtils.sendMessage(response, EncodeUtils.DEFAULT_URL_ENCODING,
				ServletUtils.HTML_TYPE, jsonString);
	}

	private byte[] cutImage(String avatarPath, String srcPath, int x, int y,
			int width, int height) throws IOException {
		File srcFile = new File(srcPath);
		BufferedImage image = ImageIO.read(srcFile);
		int srcWidth = image.getWidth(null);
		int srcHeight = image.getHeight(null);
		if (width == 0 && height == 0) {
			width = srcWidth;
			height = srcHeight;
		}
		int newWidth = 0, newHeight = 0;
		double scale_w = (double) width / srcWidth;
		double scale_h = (double) height / srcHeight;
		// 按原比例缩放图片
		if (scale_w < scale_h) {
			newHeight = height;
			newWidth = (int) (srcWidth * scale_h);
			x = x + (newWidth - width) / 2;
		} else {
			newHeight = (int) (srcHeight * scale_w);
			newWidth = width;
			y = y + (newHeight - height) / 2;
		}
		BufferedImage newImage = new BufferedImage(newWidth, newHeight,
				BufferedImage.TYPE_INT_RGB);
		newImage.getGraphics()
				.drawImage(
						image.getScaledInstance(newWidth, newHeight,
								Image.SCALE_SMOOTH), 0, 0, null);
		// 保存缩放后的图片
		String fileSufix = srcFile.getName().substring(
				srcFile.getName().lastIndexOf(".") + 1);
		String avatarName = SpringSecurityUtils.getCurrentUserName() + "."
				+ fileSufix;
		File avatarPathF = new File(avatarPath);
		if (!avatarPathF.exists()) {
			avatarPathF.mkdirs();
		}
		avatarPathF.createNewFile();
		File destFile = new File(avatarPath, avatarName);
		// 保存裁剪后的图片
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (y > (newHeight - 90))
			y = (newHeight - 90);
		if (x > (newWidth - 90))
			x = (newWidth - 90);
		ImageIO.write(newImage.getSubimage(x, y, 90, 90), fileSufix, destFile);
		byte[] logo = null;
		if (!StringUtils.isBlank(avatarPath)) {
			logo = ConvertUtils.file2ByteArray(avatarPath + avatarName);
		}
		destFile.delete();
		return logo;
	}

	/**
	 * 上传文件，保存在临时文件夹
	 * 
	 * @param request
	 * @param file
	 *            上传的文件
	 * @return JsonMessage对象<br/>
	 *         如果上传成功<br/>
	 *         message.tempDir 表示临时目录在服务器的绝对路径，用于后续请求删除临时目录<br/>
	 *         message.tempFileRalPath
	 *         表示临时文件在服务器的相对路径路径，用于前台使用临时文件，不包括ServletContext<br/>
	 *         message.tempFileAbsPath 表示临时文件在服务器的相对路径路径，用于后续请求处理临时文件
	 * 
	 */
	@RequestMapping
	public void upload(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("Filedata") MultipartFile file) {
		JsonMessage result = new JsonMessage();
		try {
			if (file.getBytes().length > 2000000) {
				result.setSuccess(Boolean.FALSE);
				result.setMessage("上传图片文件必须小于2M");
			} else {
				String servletPath = request.getSession().getServletContext()
						.getRealPath("/");
				String tempDir = "temp" + File.separator;// 临时目录相对路径
				File tempDirF = new File(tempDir);
				if (!tempDirF.exists()) {
					try {
						tempDirF.createNewFile();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				String fileName = CalendarUtils.parsefomatCalendar(
						Calendar.getInstance(), "yyyyMMddHHmmssSSS");
				String tempFileRalPath = FileUploadUtils.write(file,
						servletPath, tempDir, fileName);// 临时文件相对路径
				tempFileRalPath = StringUtils.replace(tempFileRalPath,
						File.separator, "/");// 将临时文件相对路径分隔符转换为URL分隔符
				Map<String, Object> message = new HashMap<String, Object>(2);
				message.put("tempRalFilePath", "/" + tempFileRalPath);
				message.put("filename", fileName + ".jpg");
				result.setMessage(message);
			}
		} catch (PlatformException e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception ex) {
			result.setMessage(Boolean.FALSE);
			result.setMessage(ex.getMessage());
		}
		String jsonString = JsonBinder.buildNormalBinder().toJson(result);
		ServletUtils.sendMessage(response, jsonString);
	}

	/**
	 * @Title: cityChoose
	 * @Description: 城市选择列表
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage cityChoose(HttpServletRequest request,
			HttpServletResponse response, @RequestParam String province)
			throws Exception {
		JsonMessage result = new JsonMessage();
		String[] city = City.area.get(province);
		result.setMessage(city);
		return result;
	}
}
