package chok.util.jasper;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

public class JasperUtil
{
	static Logger log = LoggerFactory.getLogger(JasperUtil.class);
	
	/**
	 * 根据json生成报表
	 * json 格式：[[{"code":"主行d1c1","name":"主行d1n1"}],[{"dt_code":"明细行d2c1","dt_name":"明细行d2n1"},{"dt_code":"明细行d2c2","dt_name":"明细行d2n2"}]]
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public static void genWebReportByJson(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String name = request.getParameter("name");
		String format = request.getParameter("format");
		String json = request.getParameter("json");
		if(log.isInfoEnabled()) 
		{
			log.info(" ==> name: "+name);
			log.info(" ==> format: "+format);
			log.info(" ==> json: "+json);
		}
		// 获取Json数据
		List<List<Map<String, ?>>> datas=JSON.parseObject(json, new TypeReference<List<List<Map<String, ?>>>>(){});
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
		JasperCompileManager.compileReportToFile(request.getServletContext().getRealPath("/WEB-INF/report/jasper/"+name+".jrxml"));
		File reportFile = new File(request.getServletContext().getRealPath("/WEB-INF/report/jasper/"+name+".jasper"));
		byte[] bytes = null;
		switch (format)
		{
			case "pdf":
				bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), param, mainDs);
				response.setContentType("application/"+format);
				break;
			case "html":
				String reportPath =JasperRunManager.runReportToHtmlFile(reportFile.getPath(), param, mainDs);
		        File reportHtmlFile = new File(reportPath);
		        FileInputStream fis = new FileInputStream(reportHtmlFile);
		        bytes =  new byte[(int)reportHtmlFile.length()];
		        fis.read(bytes);
		        response.setContentType("text/"+format);
		        reportHtmlFile.delete();// 无效？
				break;
		}
		// 生成文件
		ServletOutputStream ouputStream = response.getOutputStream();
		ouputStream.write(bytes, 0, bytes.length);
		ouputStream.flush();
		ouputStream.close();
	}
}
