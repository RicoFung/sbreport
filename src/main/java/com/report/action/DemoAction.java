package com.report.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import chok.devwork.BaseController;
import chok.util.birt.BirtDataObj;
import chok.util.birt.BirtUtil;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

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
			System.out.println(req.getParameter("json"));
			// 获取Json数据
			List<List<Map<String, ?>>> datas=JSON.parseObject(req.getParameter("json"), new TypeReference<List<List<Map<String, ?>>>>(){});
			// 创建数据源对象
			JRDataSource mainDs = null;
			Map<String, Object> param = new HashMap<String, Object>();
			for(int i=0; i<datas.size(); i++)
			{
				if (i==0)
					mainDs = new JRMapCollectionDataSource(datas.get(i));
				else
					param.put("detailDs"+i, new JRMapCollectionDataSource(datas.get(i)));
			}
			// 获取报表模板
			JasperCompileManager.compileReportToFile(req.getServletContext().getRealPath("/WEB-INF/report/jasper/demo.jrxml"));
			File reportFile = new File(req.getServletContext().getRealPath("/WEB-INF/report/jasper/demo.jasper"));
			// 填充报表
			byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), param, mainDs);
			// 生成PDF文件
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

	@RequestMapping("/birt")
	public void birt()
	{
		try
		{
			String reportName = req.getString("name");
			String format = req.getString("format");

			BirtDataObj ds1 = new BirtDataObj();
			BirtDataObj ds2 = new BirtDataObj();
			List<Map<String, String>> list1 = new ArrayList<Map<String, String>>();
			List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
			for (int i = 0; i < 10; i++)
			{
				Map<String, String> o = new HashMap<String, String>();
				o.put("code", "cc1_" + i);
				o.put("name", "nn1_" + i);
				list1.add(o);
			}
			for (int i = 0; i < 10; i++)
			{
				Map<String, String> o = new HashMap<String, String>();
				o.put("code", "c2_" + i);
				o.put("name", "n2_" + i);
				list2.add(o);
			}
			ds1.setResultList(list1);
			ds2.setResultList(list2);
			Map<String, BirtDataObj> dataObjs = new HashMap<String, BirtDataObj>();
			dataObjs.put("ds1", ds1);
			dataObjs.put("ds2", ds2);
			
			Map<String, String> param = new HashMap<String, String>();
			param.put("user", "ole");
			param.put("remark", "注释");
			BirtUtil.genWebReport(request, response, reportName, format, dataObjs, param);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}