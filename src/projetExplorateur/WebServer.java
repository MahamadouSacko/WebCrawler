package projetExplorateur;


import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class WebServer  {

	private final SocketChannel channel;
	private Charset charset = Charset.forName("UTF-8");
	private CharsetEncoder encoder = charset.newEncoder();	


	public WebServer(SocketChannel channel) throws IOException {
		this.channel = channel;
	}

	private void writeLine(String line) throws IOException {
		channel.write(encoder.encode(CharBuffer.wrap(line + "\r\n")));
	}

	public void sendResponse(HTTPResponse response) {
		response.addDefaultHeaders();
		try {
			writeLine(response.version + " " + response.responseCode + " " + response.responseReason);
			for (Map.Entry<String, String> header : response.headers.entrySet()) {
				writeLine(header.getKey() + ": " + header.getValue());
			}
			writeLine("");
			channel.write(ByteBuffer.wrap(response.content));
		} catch (IOException ex) {

		}
	}

	public HTTPResponse handle(){
		String htm2="<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">"+
				"   <link rel=\"icon\" href=\"data:,\">"+
				"	<meta charset=\"utf-8\">\r\n" + 
				"	<title> Projet Web Crawler </title>\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"	<p>\n\n\n</strong></p>\r\n" + 
				"	<p>\n\n\n</strong></p>\r\n" + 
				"	<p>\n\n\n</strong></p>\r\n" + 
				"	<p>\n\n\n</strong></p>\r\n" + 
				"	<center><strong>Bonjour</strong>, saisir l'url que vous voulez explorer\n\n</center>\r\n" + 
				"	<p>\n\n\n</strong></p>\r\n" + 
				"	<center><form >\r\n" + 
				"		<div >\r\n" + 
				"			<label for=\"url\"></label>\r\n" + 
				"<input name=\"url\" id=\"url\" value=\"\"class=\"form-control form-control-sm ml-3 w-75\" type=\"text\" placeholder=\"saisissez une url\" aria-label=\"Search\">"+
				"		</div>\r\n" + 
				"		\r\n" + 
				"	<p>\n\n\n</strong></p>\r\n" + 
				"		<center><div>\r\n" + 
				"<button type=\"submit\" class=\"btn btn-primary btn-rounded\">Send url</button>"+
				"		</div></center>\r\n" + 
				"	</form></center>\r\n" + 
				"</body>\r\n" + 
				"</html>\r\n" + 
				"";
	
		HTTPResponse response = new HTTPResponse();
		response.setContent(htm2.getBytes());
		return response;
	}

	public HTTPResponse handle2(HashSet<String> urlAlreadyCrawled) {
		String debut="<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"	<meta charset=\"utf-8\">\r\n" + 
				"	<title> Projet Web Crawler </title>\r\n" + 
				"	<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">\r\n" + 
				"\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"	<p> <strong>Crawler.fr</strong></p>\r\n" + 
				"\r\n" + 
				"	<form action=\"localhost\" method=\"get\">\r\n" + 
				"		<div class=\"md-form my-0\">\r\n" + 
				"			<center> <label for=\"recherche\">saisir le mot que vous voulait cherche</label></center>\r\n" + 
				"			<center><input name=\"mot\" id=\"mot\" value=\"\" class=\"form-control form-control-sm ml-5 w-50\" type=\"text\" placeholder=\"mot\" aria-label=\"Search\"></center>\r\n" + 
				"			<center> <button class=\"btn btn-outline-primary  btn-md my-0 ml-sm-2\" type=\"submit\">cherche</button></center>\r\n" + 
				"		</div>\r\n" + 
				"	</form>\r\n" + 
				"	<form>\r\n" + 
				"		<center><div>\r\n" + 
				"			<label for=\"url\">faire une nouvelle Exploration</label>\r\n" + 
				"			<button  class=\"btn btn-secondary\">Relance</button>\r\n" + 
				"		</div></center>\r\n" + 
				"	</form>\r\n" + 
				"	<p> </p> \r\n" + 
				"	<p> </p> \r\n" + 
				"	<p> <strong>URL TROUVE </strong></p>";
		
		String fin="";
		for (String url : urlAlreadyCrawled) {
			fin=fin+"<p> <a href=\""+url+"\" target=\"_blank\">"+url+"</a></p>";
		}
		fin=fin+"<p> </p> \r\n" + 
				"	<p> </p> \r\n" + 
				"</body>\r\n" + 
				"</html>";
		HTTPResponse response = new HTTPResponse();
		response.setContent((debut+fin).getBytes());
		return response;
	}
	
	public HTTPResponse handle4(HashSet<String> urlAlreadyCrawled) {
		String debut="<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"	<meta charset=\"utf-8\">\r\n" + 
				"	<title> Projet Web Crawler </title>\r\n" + 
				"	<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">\r\n" + 
				"\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"	<p> <strong>Crawler.fr</strong></p>\r\n" + 
				"\r\n" + 
				"	<form action=\"localhost\" method=\"get\">\r\n" + 
				"		<div class=\"md-form my-0\">\r\n" + 
				"			<center> <label for=\"recherche\">saisir le mot que vous voulait cherche</label></center>\r\n" + 
				"			<center><input name=\"mot\" id=\"mot\" value=\"\" class=\"form-control form-control-sm ml-5 w-50\" type=\"text\" placeholder=\"mot\" aria-label=\"Search\"></center>\r\n" + 
				"			<center> <button class=\"btn btn-outline-primary  btn-md my-0 ml-sm-2\" type=\"submit\">cherche</button></center>\r\n" + 
				"		</div>\r\n" + 
				"	</form>\r\n" + 
				"	<form>\r\n" + 
				"		<center><div>\r\n" + 
				"			<label for=\"url\">faire une nouvelle Exploration</label>\r\n" + 
				"			<button  class=\"btn btn-secondary\">Relance</button>\r\n" + 
				"		</div></center>\r\n" + 
				"	</form>\r\n" + 
				"	<p> </p> \r\n" + 
				"	<p> </p> \r\n" + 
				"	<p> <strong>Cette Url a deja ete explorer </strong></p>"+
				"	<p> <strong>vous pouvez rechercher les mots</strong></p>";
		
		String fin="";
		for (String url : urlAlreadyCrawled) {
			fin=fin+"<p> <a href=\""+url+"\" target=\"_blank\">"+url+"</a></p>";
		}
		fin=fin+"<p> </p> \r\n" + 
				"	<p> </p> \r\n" + 
				"</body>\r\n" + 
				"</html>";
		HTTPResponse response = new HTTPResponse();
		response.setContent((debut+fin).getBytes());
		return response;
	}
	public HTTPResponse handle3(List<String> listMot) {
		String debut="<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"	<meta charset=\"utf-8\">\r\n" + 
				"	<title> Projet Web Crawler </title>\r\n" + 
				"	<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">\r\n" + 
				"\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"	<p> <strong>Crawler.fr</strong></p>\r\n" + 
				"\r\n" + 
				"	<form action=\"localhost\" method=\"get\">\r\n" + 
				"		<div class=\"md-form my-0\">\r\n" + 
				"			<center> <label for=\"recherche\">saisir le mot que vous voulait cherche</label></center>\r\n" + 
				"			<center><input name=\"mot\" id=\"mot\" value=\"\" class=\"form-control form-control-sm ml-5 w-50\" type=\"text\" placeholder=\"mot\" aria-label=\"Search\"></center>\r\n" + 
				"			<center> <button class=\"btn btn-outline-primary  btn-md my-0 ml-sm-2\" type=\"submit\">cherche</button></center>\r\n" + 
				"		</div>\r\n" + 
				"	</form>\r\n" + 
				"	<form>\r\n" + 
				"		<center><div>\r\n" + 
				"			<label for=\"url\">faire une nouvelle Exploration</label>\r\n" + 
				"			<button  class=\"btn btn-secondary\">Relance</button>\r\n" + 
				"		</div></center>\r\n" + 
				"	</form>\r\n" + 
				"	<p> </p> \r\n" + 
				"	<p> </p> \r\n" ;
		
		String fin="<p> <strong>pages contenant le mot :"+listMot.get(listMot.size()-1)+"</strong></p>";
		for (int i = 0; i < listMot.size()-1; i++) {
			fin=fin+"<p> <a href=\""+listMot.get(i)+"\" target=\"_blank\">"+listMot.get(i)+"</a></p>";
		}
		fin=fin+"<p> </p> \r\n" + 
				"	<p> </p> \r\n" + 
				"</body>\r\n" + 
				"</html>";
		HTTPResponse response = new HTTPResponse();
		response.setContent((debut+fin).getBytes());
		return response;
	}

	public static class HTTPResponse {

		private String version = "HTTP/1.1";
		private int responseCode = 200;
		private String responseReason = "OK";
		private Map<String, String> headers = new LinkedHashMap<String, String>();
		private byte[] content;

		private void addDefaultHeaders() {
			headers.put("Date", new Date().toString());
			headers.put("Server", "Java NIO Webserver");
			headers.put("Connection", "keep-alive");
			headers.put("Content-Length", Integer.toString(content.length));
			headers.put("Content-Type", "text/html; charset=UTF-8");
		}

		public int getResponseCode() {
			return responseCode;
		}

		public String getResponseReason() {
			return responseReason;
		}

		public String getHeader(String header) {
			return headers.get(header);
		}

		public byte[] getContent() {
			return content;
		}

		public void setResponseCode(int responseCode) {
			this.responseCode = responseCode;
		}

		public void setResponseReason(String responseReason) {
			this.responseReason = responseReason;
		}

		public void setContent(byte[] content) {
			this.content = content;
		}

		public void setHeader(String key, String value) {
			headers.put(key, value);
		}
	}

}