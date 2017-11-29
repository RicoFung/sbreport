package chok.util.jasper;

import java.io.File;
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

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class JasperUtil
{
	static Logger log = LoggerFactory.getLogger(JasperUtil.class);
	
	/**
	 * 根据json生成报表
	 * @see  JSON参考格式：[[{"code":"主行d1c1","name":"主行d1n1"}],[{"dt_group":"A","dt_code":"明细行d2c1","dt_name":"明细行d2n1","dt_qty":1},{"dt_group":"B","dt_code":"明细行d2c2","dt_name":"明细行d2n2","dt_qty":2},{"dt_group":"A","dt_code":"明细行d2c1","dt_name":"明细行d2n1","dt_qty":1},{"dt_group":"B","dt_code":"明细行d2c2","dt_name":"明细行d2n2","dt_qty":2}]]
	 * @param request HTTP Request中需赋值以下参数：name, format, json
	 * @param response
	 * @throws Exception
	 */
	public static void genWebReportByJson(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// 获取参数
		String name = request.getParameter("name");
		String format = request.getParameter("format");
		String json = request.getParameter("json");
		if(log.isInfoEnabled()) 
		{
			log.info("name   ==> "+name);
			log.info("format ==> "+format);
			log.info("json   ==> "+json);
		}
		// 解析Json
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
		// 编译报表模板
		JasperCompileManager.compileReportToFile(request.getServletContext().getRealPath("/WEB-INF/report/jasper/"+name+".jrxml"));
		File reportFile = new File(request.getServletContext().getRealPath("/WEB-INF/report/jasper/"+name+".jasper"));
		// 按格式生成
		switch (format)
		{
			case "pdf":
				pdf(response, reportFile.getPath(), param, mainDs);
				break;
			case "html":
				html(response, reportFile.getPath(), param, mainDs);
				break;
			case "xlsx":
				xlsx(response, name, reportFile.getPath(), param, mainDs);
				break;
		}
	}
	
	/**
	 * 生成PDF
	 * @param response
	 * @param reportFilePath
	 * @param param
	 * @param mainDs
	 * @throws Exception
	 */
	private static void pdf(HttpServletResponse response, String reportFilePath, Map<String, Object> param, JRDataSource mainDs) throws Exception
	{
		byte[] bytes = JasperRunManager.runReportToPdf(reportFilePath, param, mainDs);
		response.reset();// 清空输出流
		response.setContentType("application/pdf;charset=UTF-8");
		ServletOutputStream ouputStream = response.getOutputStream();
		ouputStream.write(bytes, 0, bytes.length);
		ouputStream.flush();
		ouputStream.close();
	}
	
	/**
	 * 生成HTML
	 * @param response
	 * @param reportFilePath
	 * @param param
	 * @param mainDs
	 * @throws Exception
	 */
	private static void html(HttpServletResponse response, String reportFilePath, Map<String, Object> param, JRDataSource mainDs) throws Exception
	{
		response.reset();// 清空输出流
		response.setContentType("text/html;charset=UTF-8");
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportFilePath, param, mainDs);
		HtmlExporter exporter = new HtmlExporter(DefaultJasperReportsContext.getInstance());
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleHtmlExporterOutput(response.getWriter()));
		exporter.exportReport();
	}
	
	/**
	 * 生成XLSX
	 * @param response
	 * @param name
	 * @param reportFilePath
	 * @param param
	 * @param mainDs
	 * @throws Exception
	 */
	private static void xlsx(HttpServletResponse response, String name, String reportFilePath, Map<String, Object> param, JRDataSource mainDs) throws Exception
	{
		response.reset();// 清空输出流
		response.setHeader("Content-disposition", "attachment; filename=" + name + "." +"xlsx");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportFilePath, param, mainDs);
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		configuration.setOnePagePerSheet(false);
		exporter.setConfiguration(configuration);
		exporter.exportReport();
	}
}
