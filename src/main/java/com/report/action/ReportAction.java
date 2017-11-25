package com.report.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import chok.devwork.BaseController;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

@Scope("prototype")
@Controller
@RequestMapping("/")
public class ReportAction extends BaseController<Object>
{
	@RequestMapping("/rpt1")
	public void rpt1()
	{
		try
		{
			System.out.println(req.getParameter("json"));
			
			JSONObject json = JSON.parseObject(req.getParameter("json"));

	        for (Map.Entry<String, Object> entry : json.entrySet()) 
	        {
	            System.out.println(entry.getKey() + ":" + entry.getValue());
	        }
			
			
			/** 获取报表模板 */
			JasperCompileManager.compileReportToFile(req.getServletContext().getRealPath("/WEB-INF/report/jasper/report1.jrxml"));
			File reportFile = new File(req.getServletContext().getRealPath("/WEB-INF/report/jasper/report1.jasper"));
			/** 组织数据源JRBeanCollectionDataSource */
			// header
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("plu", "plu1");
			data.put("style", "style1");
			data.put("brand", "brand1");
			data.put("band", "band1");
			List headerList = new ArrayList();
			headerList.add(data);
			JRDataSource headerJRDataSource = new JRMapCollectionDataSource(headerList);

			// detail
			List detailList = new ArrayList();
			for(int i=0; i<2; i++)
			{
				Map<String, String> r1 = new LinkedHashMap<String, String>();
				r1.put("code", "r1c"+i);
				r1.put("name", "r1c"+i);
				r1.put("code1", "r11c"+i);
				r1.put("name1", "r11c"+i);
				detailList.add(r1);
			}
			JRDataSource detailJRDataSource = new JRBeanCollectionDataSource(detailList);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("detail", detailJRDataSource);
			
			/** 填充报表 */
			byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), param, headerJRDataSource);
			/** 生成PDF文件 */
			response.setContentType("application/pdf");
			ServletOutputStream ouputStream = response.getOutputStream();
			ouputStream.write(bytes, 0, bytes.length);
			ouputStream.flush();
			ouputStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
//	@RequestMapping("/rpt1")
//	public void rpt1()
//	{
//		try
//		{
//			System.out.println(req.getParameter("json"));
//			
//			JSONObject json = JSON.parseObject(req.getParameter("json"));
//			
//			for (Map.Entry<String, Object> entry : json.entrySet()) 
//			{
//				System.out.println(entry.getKey() + ":" + entry.getValue());
//			}
//			
//			
//			/** 获取报表模板 */
//			JasperCompileManager.compileReportToFile(req.getServletContext().getRealPath("/WEB-INF/report/jasper/report1.jrxml"));
//			File reportFile = new File(req.getServletContext().getRealPath("/WEB-INF/report/jasper/report1.jasper"));
//			/** 组织数据源JRBeanCollectionDataSource */
//			// header
//			Map<String, Object> data = new HashMap<String, Object>();
//			data.put("plu", "plu1");
//			data.put("style", "style1");
//			data.put("brand", "brand1");
//			data.put("band", "band1");
//			List headerList = new ArrayList();
//			headerList.add(data);
//			JRDataSource headerJRDataSource = new JRBeanCollectionDataSource(headerList);
//			// detail
//			List detailList = new ArrayList();
//			for(int i=0; i<2; i++)
//			{
//				Map<String, String> r1 = new LinkedHashMap<String, String>();
//				r1.put("r1c1", "r1c"+i);
//				r1.put("r1c2", "r1c"+i);
//				r1.put("r1c3", "r1c"+i);
//				detailList.add(r1);
//			}
//			JRDataSource detailJRDataSource = new JRBeanCollectionDataSource(detailList);
//			
//			
//			Map<String, Object> param = new HashMap<String, Object>();
//			param.put("detail", detailJRDataSource);
//			
//			/** 填充报表 */
//			byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), param, headerJRDataSource);
//			/** 生成PDF文件 */
//			response.setContentType("application/pdf");
//			ServletOutputStream ouputStream = response.getOutputStream();
//			ouputStream.write(bytes, 0, bytes.length);
//			ouputStream.flush();
//			ouputStream.close();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}

//	@RequestMapping("/rpt2")
//	public void rpt2()
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