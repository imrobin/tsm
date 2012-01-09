/**  
 * Filename:    MobileTypeManagerTest.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-5-24 下午03:13:17  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-5-24     jinghua.hao             1.0        1.0 Version  
 */  


package com.justinmobile.tsm.customer.manager;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.customer.domain.MobileType;



public class MobileTypeManagerTest extends BaseAbstractTest{
	@Autowired
	private MobileTypeManager mobileTypeManager;
	/*//@Test
	public void testGetAll(){
		List<MobileType> list = mobileTypeManager.getAll();
		System.out.println(list.size());	
	}
	@Test
	public void testGetAllBrand(){
		List<String> list = mobileTypeManager.getAllBrand();
		System.out.println(list.size());	
	}
	@Test
	public void testGetAllMobileTypeByBrand(){
		Page<MobileType> list = mobileTypeManager.getMobileByBrand("三星");
		System.out.println(list.getTotalCount());
	}
	@Test
	public void testGetMobileByKeyword(){
		Page<MobileType> list = mobileTypeManager.getMobileByKeyword("super");
		System.out.println(list.getTotalCount());
		Page<MobileType> list2 = mobileTypeManager.getMobileByKeyword("超级手机");
		System.out.println(list2.getTotalCount());
		Page<MobileType> list3 = mobileTypeManager.getMobileByKeyword("668");
		System.out.println(list3.getTotalCount());
		
	}
	@Test
	public void testGetMobileByBrandAndType(){
		MobileType list = mobileTypeManager.getMobileByBrandAndType("联想","P668");
		System.out.println(list);
		
	}*/
	//@Test
	public void testAddImageMobile(){
		List<MobileType> list = mobileTypeManager.getAll();
		System.out.println(list.size());
		String[] brand = new String[11];
		String[] type = new String[11];
		File f;
		MobileType mt;
		for(int i=0;i<list.size();i++){
			mt = list.get(i);
			brand[i] = mt.getBrandChs();
			type[i]  = mt.getType();
			f = new File("pic/"+mt.getType()+".jpg");
			System.out.println(f.getName()+f.length());
			mt.setIcon(ConvertUtils.file2ByteArray(f));
			mobileTypeManager.saveOrUpdate(mt);
		}
	}
	@Test
	public void testKeywordSuggest(){
		List<String> list = mobileTypeManager.getSuggestByKeyword("HT");
		System.out.println(list.size());
	}

}



