package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;
public class ParserUrl {
	private List<String> listMot = new ArrayList<String>();
	private List<String> listUrl = new ArrayList<String>();
	private long filsize;
	public static void main(String[] args) throws Exception {
		ParserUrl u =new ParserUrl();
		u.parseurl("https://dzone.com/articles/java-io-and-nio");
		u.affiche();

	}

	public void parseurl(String url) throws FileNotFoundException, IOException {
		listMot = new ArrayList<String>();
		listUrl = new ArrayList<String>();
		ParserDelegator parserDelegator = new ParserDelegator();
		ParserCallback parserCallback = new ParserCallback() {
			public void handleText(final char[] data, final int pos) {
				listMot.add(new String(data));
			}

			public void handleStartTag(Tag tag, MutableAttributeSet attribute, int pos) {
				if (tag == Tag.A) {
					String address = (String) attribute.getAttribute(Attribute.HREF);
					listUrl.add(address);
				}

			}

			public void handleEndTag(Tag t, final int pos) {

			}

			public void handleSimpleTag(Tag t, MutableAttributeSet a, final int pos) {

			}

			public void handleComment(final char[] data, final int pos) {
			}

			public void handleError(final java.lang.String errMsg, final int pos) {
			}
		};
		FileReader f=	new FileReader(downloadfile(url));
		parserDelegator.parse(f, parserCallback, true);
	}

	public  File downloadfile(String Url) throws IOException {
		File f=null;

		URL u = new URL(Url);
		if(u!=null) {
			InputStream is = u.openStream();
			f= File.createTempFile("data", "txt"); 
			filsize=f.length();
			FileOutputStream  os = new  FileOutputStream (f);
			int b;
			while((b=is.read())!=-1) {
				os.write((char)b);
			}
			is.close();
			os.close();
		}

		return f;

	}

	public void affiche() {
		System.out.println("taille de url: " +listUrl.size());
		for (String string : listUrl) {
			System.out.println("url: "+string);
		}
		System.out.println("taille de mot: " +listMot.size());
		for (String string : listMot) {
			System.out.println("mot: "+string);
		}
	}

	public List<String> getListMot() {
		return listMot;
	}

	public List<String> getListUrl() {
		return listUrl;
	}

	public long getFilsize() {
		return filsize;
	}
}