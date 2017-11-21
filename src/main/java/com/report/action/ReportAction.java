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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import chok.devwork.BaseController;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Scope("prototype")
@Controller
@RequestMapping("/report")
public class ReportAction extends BaseController<Object>
{
	@RequestMapping("/rpt1")
	public void rpt1()
	{
		try
		{
//			Map<String, Object> p = req.getParameterValueMap(false, true);
//			System.out.println(p.get("json").toString());
			System.out.println(req.getParameter("json"));
			
			JSONObject json = JSON.parseObject(req.getParameter("json"));

	        for (Map.Entry<String, Object> entry : json.entrySet()) 
	        {
	            System.out.println(entry.getKey() + ":" + entry.getValue());
	        }
			
			
			/** 获取报表模板 */
			JasperCompileManager.compileReportToFile(req.getServletContext().getRealPath("/WEB-INF/report/report1.jrxml"));
			File reportFile = new File(req.getServletContext().getRealPath("/WEB-INF/report/report1.jasper"));
			/** 组织数据源JRBeanCollectionDataSource */
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("param1", "jack");
			// param.put("Age","19");
			Map<String, String> o = new HashMap<String, String>();
			o.put("id", "1");
			o.put("name", "usersdsdasd");
			o.put("sort", "11");
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			list.add(o);
			JRDataSource jrDataSource = new JRBeanCollectionDataSource(list);
			/** 填充报表 */
			byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), param, jrDataSource);
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
}