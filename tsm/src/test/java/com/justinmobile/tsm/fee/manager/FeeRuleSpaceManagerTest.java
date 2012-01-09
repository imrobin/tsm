package com.justinmobile.tsm.fee.manager;



import java.util.ArrayList;


import java.util.HashSet;
import java.util.List;

import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.fee.domain.FeeRuleSpace;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.core.utils.DateUtils;
import com.justinmobile.core.utils.web.KeyValue;

public class FeeRuleSpaceManagerTest extends BaseAbstractTest {
	@Autowired
	private FeeRuleSpaceManager frpManager;
	@Autowired
	private SpBaseInfoManager spManager;
	@Autowired
	private ApplicationManager appManager;

	//private static String templateFileName = "src/main/resources/template/feeSpaceStat.xls";
	//private static String destFileName = "FeeSpace_output.xls";

	// @Test
	public void testAdd() throws Exception {
		SpBaseInfo sp = spManager.load(new Long(1721));
		FeeRuleSpace frp = new FeeRuleSpace();
		frp = new FeeRuleSpace();
		frp.setSp(sp);
		frp.setGranularity(1024);
		frp.setPrice(100);
		frpManager.saveOrUpdate(frp);
	}

	// @Test
	public void list() throws Exception {
		List<FeeRuleSpace> list = frpManager.getAll();
		System.out.println(list.size());
	}

	// @Test
	public void getSpHasSd() throws Exception {
		List<KeyValue> list = frpManager.getSpNameHasSd();
		System.out.println(list.size());
	}

	// @Test
	public void getSdBySp() throws Exception {
		List<KeyValue> list = frpManager.getSdNameBySp(new Long(1721));
		System.out.println(list.size());
	}

	// @Test
	public void testEnum() throws Exception {
		Operation[] o = Operation.values();
		for (Operation o1 : o) {
			System.out.println(o1);
		}

	}

	// @Test
	public void getNameBySp() throws Exception {
		List<KeyValue> list = frpManager.getAppNameBySp(1721L);
		System.out.println(list.size());
	}

	//@Test
	/*public void genStatReport() throws Exception {
		Long spId = 1721L;
		String start = "20110701";
		String end = "20110731";
		Map<String,Object> beans = new HashMap<String,Object>();
		SpBaseInfo sp = spManager.load(spId);
		FeeSpaceResult fsr = genFeeSpaceResult(spId,start,end);
		fsr.setSpName(sp.getName());
		beans.put("fsr", fsr);
		beans.put("total", fsr.getTotal());
		Resource r = new ClassPathResource("template/feeSpaceStat.xls");
		XLSTransformer transformer = new XLSTransformer();
		transformer.transformXLS(r.getFile().getAbsolutePath(), beans, destFileName);

	}*/
	//@Test
	public void getProperties()throws Exception{
		Resource r = new ClassPathResource("config/funcode.properties");
		Properties p = PropertiesLoaderUtils.loadProperties(r);
		System.out.println(p.getProperty("DELETE_APP"));
	}
	//@Test
	public void testUnionCollection() throws Exception{
		System.out.println(PlatformMessage.SUCCESS.getCode());
	}
	//@Test
	public void testCalendar() throws Exception{
		String[] date = "2009-02".split("-");
		if(date[1].startsWith("0")){
			date[1] =date[1].substring(1);
		}
		String end = DateUtils.maxDay(new Integer(date[0]),new Integer(date[1]))+"";
		System.out.println(end);
		//String start = "01";
	}
	 /* private FeeSpaceResult genFeeSpaceResult(Long spId,String start,String end){
	    	FeeSpaceResult fsr = new FeeSpaceResult();
			fsr.setAppRecord(genAppStatList(spId,start,end));
			fsr.setSdRecord(genSdStatList(spId,start,end));
			fsr.setSpId(spId.toString());
			fsr.setEnd(end);
			fsr.setStart(start);
			return fsr;
		}*/
		/*private List<FeeSpaceStat> genAppStatList(Long spId, String start,
				String end) {
			FeeSpaceStat fss = null;
			Double price = null;
			Long size = null;
			Long count = null;
			// 根据应用空间大小模式计费
			List<Application> appList = frpManager.getAppBySp(spId);
			logger.info("appSize()=="+appList.size());
			// 判断应用所属安全域是否为签约空间管理模式
			List<FeeSpaceStat> appStatList = new ArrayList<FeeSpaceStat>();
			for (Application app : appList) {
				if (null != app.getSd() && !app.getSd().isSpaceFixed()) {
					for (ApplicationVersion appVer : app.getVersions()) {
						// 先查处计费规则 ，再查询出应用大小进行应用价格计算
						FeeRuleSpace frp = frpManager.getFrpByAidAndVersion(
								app.getAid(), appVer.getVersionNo());
						size = frpManager.getAppVerSize(appVer);
						if(null==size){
							size = 0L;
						}
						if (null != frp) {
							// 判断是否为一口价模式
							if (frp.getGranularity() == 0) {
								price = (double)frp.getPrice()/100;
							} else {
								price = (double)size * frp.getPrice()
										/ (frp.getGranularity()*100);
							}
						}else{
							price = 0.00;
						}
						// 计算应用的使用人数
						count = frpManager.getCountByAppAndDate(app, start,
								end);
						fss = new FeeSpaceStat(app.getName(),
								appVer.getVersionNo(), price, count,price*count);
						appStatList.add(fss);
					}
				}
			}
			return appStatList;
		}
*/
/*		private List<FeeSpaceStat> genSdStatList(Long spId, String start, String end) {
			// 根据签约空间模式进行计费
			List<FeeSpaceStat> sdStatList = new ArrayList<FeeSpaceStat>();
			List<SecurityDomain> sdList = frpManager.getSdBySp(spId);
			logger.info("sdList.size()=="+sdList.size());
			FeeSpaceStat fss = null;
			Long count = null;
			Double price = null;
			for (SecurityDomain sd : sdList) {
				if (sd.isSpaceFixed()) {
					// 获取安全域的计费规则
					FeeRuleSpace frp = frpManager.getFrpByAidAndVersion(
							sd.getAid(), null);
					// 获取安全域的大小
					Long sdSize = sd.getManagedNoneVolatileSpace()
							+ sd.getManagedVolatileSpace();
					// 计算安全域的单价
					if (null != frp) {
						price = (double) (sdSize * frp.getPrice())
								/ (frp.getGranularity())/100;
					} else {
						price = 0.00;
					}
					// 计算该安全域的使用人数
					// 计算该安全域的使用人数
					List<String> cardNoLt = frpManager.getCardNoCreateSD(sd.getAid(), start, end);
					List<String> cardNoSh = new ArrayList<String>();
					Set<String> cardNo = new HashSet<String>();
					//根据应用获取该应用的用户订购数
					for(Application app:frpManager.getAppBySd(sd.getId())){
						cardNoSh.addAll(frpManager.getCardNoByAppAndDate(app, start, end));
					}
					cardNo.addAll(cardNoLt);
					cardNo.addAll(cardNoSh);
					count = (long)cardNo.size();
					fss = new FeeSpaceStat(sd.getSdName(), "", price, count,price*count);
					sdStatList.add(fss);
					logger.info("sdStatList.size()=="+sdStatList.size());
				}
			}
			return sdStatList;
		}*/
		//@Test
		public void testSet() throws Exception{
			List<String> a = new ArrayList<String>();
			a.add("a");
			a.add("b");
			a.add("c");
			a.add("d");
			List<String> b = new ArrayList<String>();
			b.add("a");
			b.add("b");
			b.add("e");
			b.add("f");
			Set<String> c = new HashSet<String>();
			c.addAll(a);
			c.addAll(b);
			for(String s:c){
				System.out.println(s);
			}
		}
		//@Test
		public void testgetCount() throws Exception{
			List<Application> app = frpManager.getAppBySd(new Long(1));
			System.out.println(app.size());
			List<String> s = frpManager.getCardNoByAppAndDate(app.get(2), "20110601", "20110930");
			System.out.println(s.size());
		}
		//@Test
		public void testAppCount() throws Exception{
			Application app = appManager.load(826L);
			Long r = frpManager.getCountByAppAndDate(app, "20110601","20110901");
			System.out.println(r);
		}
		@Test
		public void testString() throws Exception{
			String source ="9999999999-1.0.0-测试应用10个9";
			String[] s = source.split("-");
			String s2 = source.substring((s[0]+"-"+s[1]+"-").length(),source.length());
			System.out.println(s2);
			
		}
		
	

	

}
