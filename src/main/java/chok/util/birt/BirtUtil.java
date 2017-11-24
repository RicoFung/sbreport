package chok.util.birt;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.engine.api.ReportEngine;

public class BirtUtil
{
	private static IRenderOption renderOptions;
	
	public static void genWebReport(HttpServletRequest request, HttpServletResponse response, String reportName, String format, Map<String, BirtDataObj> dataObjs, Map<String, String> param)
	{

		EngineConfig config = new EngineConfig();
		config.setEngineHome("");
		ReportEngine birtEngine = new ReportEngine(config);
		try
		{
			ServletContext sc = request.getSession().getServletContext();
			if (format == null)
			{
				format = "html";
			}
			IReportRunnable runnable = null;
			runnable = birtEngine.openReportDesign(sc.getRealPath("/WEB-INF/report/birt") + "/" + reportName + ".rptdesign");
			IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(runnable);
			
			for (Map.Entry<String, BirtDataObj> entry : dataObjs.entrySet()) 
			{  
			    runAndRenderTask.addScriptableJavaObject(entry.getKey(), entry.getValue());
			}  
			runAndRenderTask.setParameterValues(param);

			response.setContentType(birtEngine.getMIMEType(format));
			IRenderOption options = null == renderOptions ? new RenderOption() : renderOptions;
			if (format.equalsIgnoreCase("html"))
			{
				HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
				htmlOptions.setOutputFormat("html");
				htmlOptions.setOutputStream(response.getOutputStream());
				htmlOptions.setImageHandler(new HTMLServerImageHandler());
				htmlOptions.setBaseImageURL(request.getContextPath() + "/images");
				htmlOptions.setImageDirectory(sc.getRealPath("/images"));
				runAndRenderTask.setRenderOption(htmlOptions);
			}
			else if (format.equalsIgnoreCase("pdf"))
			{
				PDFRenderOption pdfOptions = new PDFRenderOption(options);
				pdfOptions.setOutputFormat("pdf");
				pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.FIT_TO_PAGE_SIZE);
				pdfOptions.setOutputStream(response.getOutputStream());
				runAndRenderTask.setRenderOption(pdfOptions);
			}
			else
			{
				String att = "download." + format;
				String uReportName = reportName;
				if (uReportName.endsWith(".rptdesign"))
				{
					att = uReportName.replace(".rptdesign", "." + format);
				}
				response.setHeader("Content-Disposition", "attachment; filename=\"" + att + "\"");
				options.setOutputStream(response.getOutputStream());
				options.setOutputFormat(format);
				runAndRenderTask.setRenderOption(options);
			}
			runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request);
			runAndRenderTask.run();
			runAndRenderTask.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
