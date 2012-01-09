package com.justinmobile.tsm.fee.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.transformer.XLSTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.DateUtils;
import com.justinmobile.core.utils.MimeUtils;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.tsm.fee.domain.FeeRuleFunction;
import com.justinmobile.tsm.fee.domain.FeeStat;
import com.justinmobile.tsm.fee.manager.FeeRuleFunctionManager;
import com.justinmobile.tsm.fee.manager.FeeStatManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;

@Controller("FeeStatControler")
@RequestMapping("/feestat/")
public class FeeStatController {
	@Autowired
	private SpBaseInfoManager spManager;
	@Autowired
	private FeeStatManager fsManager;
	@Autowired
	private FeeRuleFunctionManager frfManager;
	private Logger logger = LoggerFactory.getLogger(FeeStatController.class);

	/**
	 * @Title: getFeeStat
	 * @Description: 获取应用提供商的计费报表
	 * @param request
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping
	public ModelAndView getFeeStat(HttpServletRequest request) {
		logger.info("insert into getFeeStat method");
		ModelAndView result = new ModelAndView();
		String viewName = "/admin/fee/feeStatResult.jsp";
		String xlsTemplate = "template/feeStat.xls";
		List<FeeStat> functionList, spaceList;
		try {
			Long spId = ServletRequestUtils.getLongParameter(request, "spId");
			SpBaseInfo sp = spManager.load(spId);
			String yearAndMonth = ServletRequestUtils
					.getRequiredStringParameter(request, "date");
			String start = yearAndMonth + "01";
			String end = yearAndMonth
					+ DateUtils.maxDay(
							new Integer(yearAndMonth.substring(0, 4)),
							new Integer(yearAndMonth.substring(4))) + "";
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date d_start = format.parse(start);
			Date d_end = format.parse(end);
			// 进行SP功能计费规则判断
			FeeRuleFunction frf = frfManager.getPerFrf(spId);
			if (frf != null) {
				functionList = fsManager.getFeeStat(spId, d_start, d_end,
						FeeStat.TYPE_FUNCTION);
			} else {
				long count = fsManager.getCounthasBilled(spId, start, end);
				frf = fsManager.getMonthFrfBySpAndSize(spId, count);
				if (frf != null) {
					// 计费规则为包月
					functionList = fsManager
							.getFunctionBilled(spId, start, end);
					double price = (double)frf.getPrice()/ 100;
					for (FeeStat st : functionList) {
						st.setPrice(price);
					}
					viewName="/admin/fee/feeStatMonthResult.jsp";
					xlsTemplate = "template/feeStatMonth.xls";
					result.addObject("number",count);
					result.addObject("price",price);
					
				} else {
					functionList = fsManager
							.getFeeStat(spId, d_start, d_end,FeeStat.TYPE_FUNCTION);
				}
			}
			spaceList = fsManager.getFeeStat(spId, d_start, d_end,
					FeeStat.TYPE_SPACE);
			Map<String,Object> beans = genBeans(sp,start,end,functionList,spaceList);
			beans.put("spId", spId);
			result.addAllObjects(beans);
			XLSTransformer transformer = new XLSTransformer();
			StringBuilder fileName = new StringBuilder();
			fileName.append(sp.getName()).append("-").append(start).append("-")
					.append(end).append(".xls");
			File xls = new File(fileName.toString());
			Resource r = new ClassPathResource(xlsTemplate);
			transformer.transformXLS(r.getFile().getPath(), beans,
					xls.getPath());
		} catch (PlatformException e) {
			e.printStackTrace();
			result.addObject("message", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.addObject("message", e.getMessage());
		}
		result.setViewName(viewName);
		return result;
	}

	/**
	 * @Title: genExcel
	 * @Description: 生成Excel报表供用户下载
	 * @param request
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping
	public void genExcel(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		JsonResult result = new JsonResult();
		ServletOutputStream stream = response.getOutputStream();
		BufferedInputStream buf = null;
		String xlsTemplate="template/feeStat.xls";
		List<FeeStat> functionList, spaceList;
			try {
				Long spId = ServletRequestUtils.getLongParameter(request, "spId");
				SpBaseInfo sp = spManager.load(spId);
				String start = ServletRequestUtils
						.getStringParameter(request, "start");
				String end = ServletRequestUtils.getStringParameter(request, "end");
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				Date d_start = format.parse(start);
				Date d_end = format.parse(end);
				// 进行SP功能计费规则判断
				FeeRuleFunction frf = frfManager.getPerFrf(spId);
				if (frf != null) {
					functionList = fsManager.getFeeStat(spId, d_start, d_end,
							FeeStat.TYPE_FUNCTION);
				} else {
					long count = fsManager.getCounthasBilled(spId, start, end);
					frf = fsManager.getMonthFrfBySpAndSize(spId, count);
					if (frf != null) {
						// 计费规则为包月
						functionList = fsManager
								.getFunctionBilled(spId, start, end);
						double price = (double)frf.getPrice()/ 100;
						for (FeeStat st : functionList) {
							st.setPrice(price);
						}
						xlsTemplate = "template/feeStatMonth.xls";
					} else {
						functionList = fsManager
								.getFunctionBilled(spId, start, end);
					}
				}
				spaceList = fsManager.getFeeStat(spId, d_start, d_end,
						FeeStat.TYPE_SPACE);
			StringBuilder fileName = new StringBuilder();
			fileName.append(sp.getName()).append("-").append(start).append("-")
					.append(end).append(".xls");
			File xls = new File(fileName.toString());
			response.setContentType("application/msexcel");
			response.addHeader("Content-Disposition", "attachment; filename="
					+ MimeUtils.encodeFileName(request, fileName.toString()));
			response.setContentLength((int) xls.length());
			XLSTransformer transformer = new XLSTransformer();
			Map<String,Object> beans = genBeans(sp,start,end,functionList,spaceList);
			Resource r = new ClassPathResource(xlsTemplate);
			transformer.transformXLS(r.getFile().getPath(), beans,
					xls.getPath());
			buf = new BufferedInputStream(new FileInputStream(xls));
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = buf.read(buff, 0, buff.length))) {
				stream.write(buff, 0, bytesRead);
			}
			stream.flush();
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(ioe.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} finally {
			if (stream != null)
				stream.close();
			if (buf != null)
				buf.close();
		}
	}

	private double getTotal(List<FeeStat> list) {
		double total = 0.00;
		for (FeeStat fs : list) {
			total = total + fs.getPrice();
		}
		return total;
	}
    private Map<String,Object> genBeans(SpBaseInfo sp,String start,String end,List<FeeStat> funList,List<FeeStat> spaceList){
    	Map<String, Object> beans = new HashMap<String, Object>();
    	double funTotal = getTotal(funList);
    	double spaceTotal = getTotal(spaceList);
		beans.put("name", sp.getName());
		beans.put("start", start);
		beans.put("end", end);
		beans.put("space", spaceList);
		beans.put("function", funList);
		beans.put("funTotal", funTotal);
		beans.put("spaceTotal", spaceTotal);
		beans.put("total", funTotal+spaceTotal);
		return beans;
    }
}
