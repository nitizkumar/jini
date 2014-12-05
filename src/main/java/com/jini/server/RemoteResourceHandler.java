package com.jini.server;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.eclipse.jetty.server.Request;
import org.json.JSONObject;

import com.jini.FileUtils;

public class RemoteResourceHandler {
	private File dir = null;

	private static int counter = 0;
	
	public void setDir(File dir) {
		this.dir = dir;
	}

	public void handle(String path, Request arg1, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String appPath = (String) MainServer.appProp.get("API_PATH");
		String resourcePath = (String) MainServer.appProp
				.get("API_RESOURCE_PATH");

		String remoteServerURL = (String) MainServer.appProp
				.get("API_REMOTE_SERVER_URL");

		String remoteResourceURL = (String) MainServer.appProp
				.get("API_REMOTE_RESOURCE_URL");

		Boolean useCache = Boolean.valueOf((String) MainServer.appProp
				.get("CACHE_SERVER"));
		
		if (path.startsWith(resourcePath)) {
			String originalPath = path;

			File workDir = new File(this.dir, ".work");

			String queryString = request.getQueryString();
			String replace = null;
			if(queryString != null && !queryString.isEmpty())
			{
				replace = originalPath.replace("/", "_") + "/" + queryString.replace("&", "_").replace("=", "__").replace("\\", "___").replace("/", "___");
			}
			else{
				replace = originalPath.replace("/", "_") ;
			}
			
			File cachedFile = new File(workDir, replace);
			cachedFile.getParentFile().mkdirs();

			if ((cachedFile.exists())) {
				ServletOutputStream os = response.getOutputStream();
				ImageIO.write(ImageIO.read(cachedFile), "png", os);
				return;
			}

			path = path.replace(resourcePath, remoteResourceURL);

			if (queryString != null && !queryString.isEmpty()) {
				path = path + "?" + queryString;
			}

			try {
				BufferedImage image = ImageIO.read(new URL(path));
				ImageIO.write(image, "png", cachedFile);
				
				ImageIO.write(image, "png", response.getOutputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (path.startsWith(appPath)) {
			
			JSONObject jsonObject = new JSONObject();
			
			String originalPath = path;
			path = path.replace(appPath, remoteServerURL);

			File workDir = new File(this.dir, ".work");
			
			String queryString = request.getQueryString();
			
			String replace = null;
			
			int currentCounter = counter++;
			if(queryString != null && !queryString.isEmpty())
			{	
				replace = originalPath.replace("/", "_") + "_" + queryString.replace("&", "_").replace("=", "__").replace("\\", "___").replace("/", "___");
				jsonObject.put("url", path + "?" + queryString);
			}
			else
			{
				replace = originalPath.replace("/", "_");
				jsonObject.put("url", path );
			}
			
			
			File cachedFile = new File(workDir, replace
					+ "_" + arg1.getMethod());
			
			
			if ((cachedFile.exists()) && (useCache.booleanValue())) {
				ServletOutputStream os = response.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bsw = new BufferedWriter(osw);
				bsw.write(FileUtils.readToString(cachedFile));
				bsw.close();
				response.flushBuffer();
				return;
			}
			BufferedReader reader = arg1.getReader();
			String line = null;
			StringBuffer stringBuffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
//				System.out.println(line);
				stringBuffer.append(line);
			}
			
			jsonObject.put("params", stringBuffer);
			jsonObject.put("method", arg1.getMethod());
			
			if (queryString != null && !queryString.isEmpty()) {
				path = path + "?" + queryString;
			}
			
			
			HttpClient client = new HttpClient();
			if (arg1.getMethod().toLowerCase().equals("get")) {
				HttpMethod method = new GetMethod(path);
				setHeaders(arg1, method);
				int status = client.executeMethod(method);				
				String responseBodyAsString = method.getResponseBodyAsString();
				System.out.println(responseBodyAsString);
				
				if(status != 200){
					response.setStatus(status);
				}
				
				ServletOutputStream os = response.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bsw = new BufferedWriter(osw);
				bsw.write(responseBodyAsString);
				bsw.close();
				response.flushBuffer();
				try {
					FileUtils.writeToFile(cachedFile, responseBodyAsString);
					jsonObject.put("response",responseBodyAsString);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (arg1.getMethod().toLowerCase().equals("post")) {			
				PostMethod method = new PostMethod(path);
				RequestEntity entity = new StringRequestEntity(
						stringBuffer.toString());

				method.setRequestEntity(entity);
				setHeaders(arg1, method);
				int status = client.executeMethod(method);
				System.out.println(path);
				
				if(status != 200){
					response.setStatus(status);
				}
				
				InputStreamReader in2 = new InputStreamReader(
						method.getResponseBodyAsStream(), "UTF-8");

				StringWriter sw = new StringWriter();
				int x;
				while ((x = in2.read()) != -1) {
					sw.write(x);
				}
				in2.close();
				String responseAsString = sw.toString();
				ServletOutputStream os = response.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bsw = new BufferedWriter(osw);
				bsw.write(responseAsString);
//				System.out.println(responseAsString);
				bsw.close();
				response.flushBuffer();
				try {
					FileUtils.writeToFile(cachedFile, responseAsString);
					jsonObject.put("response",responseAsString);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (arg1.getMethod().toLowerCase().equals("put")) {
				PutMethod method = new PutMethod(path);
				RequestEntity entity = new StringRequestEntity(
						stringBuffer.toString());
				
				method.setRequestEntity(entity);
				setHeaders(arg1, method);
				int status = client.executeMethod(method);
				
				if(status != 200){
					response.setStatus(status);
				}
				
				String responseBodyAsString = method.getResponseBodyAsString();
				System.out.println(responseBodyAsString);
				ServletOutputStream os = response.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bsw = new BufferedWriter(osw);
				bsw.write(responseBodyAsString);
				bsw.close();
				response.flushBuffer();
				try {
					FileUtils.writeToFile(cachedFile, responseBodyAsString);
					jsonObject.put("response",responseBodyAsString);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (arg1.getMethod().toLowerCase().equals("delete")) {
				DeleteMethod method = new DeleteMethod(path);
				setHeaders(arg1, method);
				int status = client.executeMethod(method);
				
				if(status != 200){
					response.setStatus(status);
				}
				
				String responseBodyAsString = method.getResponseBodyAsString();
				System.out.println(responseBodyAsString);
				ServletOutputStream os = response.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bsw = new BufferedWriter(osw);
				bsw.write(responseBodyAsString);
				bsw.close();
				response.flushBuffer();
				try {
					FileUtils.writeToFile(cachedFile, responseBodyAsString);
					jsonObject.put("response",responseBodyAsString);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			CustomLogger.getInstance().log(jsonObject.toString());
			System.out.println(path);
		}
	}

	private File getCachedFile(String originalPath, File workDir) {
		String replace = originalPath.replace("/", "_");
		replace = replace.replace("?", "_").replace("&", "_");
		return new File(workDir, replace);
	}

	private void setHeaders(Request arg1, HttpMethod method) {
		if (arg1.getHeader("Content-Type") == null) {
			method.addRequestHeader(new Header("Content-Type",
					"application/json"));
		} else {
			method.addRequestHeader(new Header("Content-Type", arg1
					.getHeader("Content-Type")));
		}
		if (arg1.getHeader("Cookie") != null) {
			method.addRequestHeader(new Header("Cookie", arg1
					.getHeader("Cookie")));
		}
		String headerParams = (String) MainServer.appProp.get("HEADER_PARAMS");
		if (headerParams != null) {
			String[] split = headerParams.split(",");
			if (split.length > 0) {
				for (int i = 0; i < split.length; i++) {
					method.addRequestHeader(new Header(split[i], arg1
							.getHeader(split[i])));
				}
			}
		}
	}
}
