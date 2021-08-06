package parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
/*
 * Nous avons implémenté notre propre parseur pour les URL avant 
 * des utilisent les parseurs fournis par swing qui est plus complet que la nôtre
 */
public class ParseUrlDeprecated {
	static long filsize;
	public static StringLexer parsehmtl(String html) throws IOException {
		StringLexer lexer= new StringLexer(html);
		HtmlValue htmvalue=null;
		try {
			htmvalue=HtmlValue.parseValue(lexer);
		} catch ( HtmlParseException e1) {
			e1.printStackTrace();
		}
		while(htmvalue!=null) {
			try {
				htmvalue=HtmlValue.parseValue(lexer);
			} catch ( HtmlParseException e) {
				//e.printStackTrace();
			}catch (java.lang.NullPointerException e) {
				//e.printStackTrace();
			}
		}
		//affiche(lexer);
		return lexer;

	}
	public static String  parses(File f) {
		StringBuilder html = new StringBuilder();
		try  {
			FileInputStream fi = new FileInputStream(f);
			long FSize =f.length();
			String line="";

			while (FSize > 0 &&line!=null) {
				line=readLine(fi);
				if(line!=null)FSize-= line.length();
				if(line!=null)html.append(line);
			}
			fi.close();
			parsehmtl(html.toString());

		} catch (IOException ex ) {
			//ex.printStackTrace();
		}catch (java.lang.NullPointerException e) {
			//e.printStackTrace();
		}
		return html.toString();

	}
	public static String readLine(FileInputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		int b;
		while ((b = in.read()) >= 0) {
			if (b == '\n') break;
			if (b != '\r') sb.append((char) b);
		}
		if (b == -1 && sb.length() == 0) return null;
		return sb.toString();
	}
	public static File downloadfile(String Url) {
		File f=null;
		try {
			URL u = new URL(Url);
			InputStream is = u.openStream();
			f= new File("test.txt"); //File.createTempFile("test", "txt");
			filsize=f.length();
			FileOutputStream  os = new  FileOutputStream (f);
			int b;
			while((b=is.read())!=-1) {
				os.write((char)b);
			}
			is.close();
			os.close();

		} catch (IOException e) {
			//e.printStackTrace();
		}catch (java.lang.NullPointerException e) {
			//e.printStackTrace();
		}
		return f;

	}

	public static void affiche(StringLexer lexer) {
		System.out.println("taile de la liste url  "+ lexer.getListDurl().size());
		System.out.println("taile de la liste  mot trouve "+ lexer.getListmot().size());
		List<String> listmot=lexer.getListmot();
		for (int j = 0; j < listmot.size(); j++) {
			System.out.println("mot "+listmot.get(j));
		}

		List<String> listurl=lexer.getListDurl();
		for (int j = 0; j < listurl.size(); j++) {
			System.out.println("url "+listurl.get(j));
		}
	}
	public long getFilsise() {
		return filsize;
	}
}
