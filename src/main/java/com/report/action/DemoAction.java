package com.report.action;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import chok.devwork.BaseController;
import chok.util.jasper.JasperUtil;

@Scope("prototype")
@Controller
@RequestMapping("/demo")
public class DemoAction extends BaseController<Object>
{
	@RequestMapping("/jasper")
	public void jasper()
	{
		try
		{
			JasperUtil.genWebReportByJson(request, response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

//	@RequestMapping("/birt")
//	public void birt()
//	{
//		try
//		{
//			String reportName = req.getString("name");
//			String format = req.getString("format");
//
//			BirtDataObj ds1 = new BirtDataObj();
//			BirtDataObj ds2 = new BirtDataObj();
//			List<Map<String, String>> list1 = new ArrayList<Map<String, String>>();
//			List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
//			for (int i = 0; i < 10; i++)
//			{
//				Map<String, String> o = new HashMap<String, String>();
//				o.put("code", "cc1_" + i);
//				o.put("name", "nn1_" + i);
//				list1.add(o);
//			}
//			for (int i = 0; i < 10; i++)
//			{
//				Map<String, String> o = new HashMap<String, String>();
//				o.put("code", "c2_" + i);
//				o.put("name", "n2_" + i);
//				list2.add(o);
//			}
//			ds1.setResultList(list1);
//			ds2.setResultList(list2);
//			Map<String, BirtDataObj> dataObjs = new HashMap<String, BirtDataObj>();
//			dataObjs.put("ds1", ds1);
//			dataObjs.put("ds2", ds2);
//			
//			Map<String, String> param = new HashMap<String, String>();
//			param.put("user", "ole");
//			param.put("remark", "注释");
//			BirtUtil.genWebReport(request, response, reportName, format, dataObjs, param);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
}