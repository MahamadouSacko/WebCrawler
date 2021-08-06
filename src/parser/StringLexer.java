package parser;

import java.util.ArrayList;
import java.util.List;

public class StringLexer implements Lexer{

	private String str;
	private int position;
	private int size;
	private List<String> listDurl=new ArrayList<String>();
	private List<String> listmot=new ArrayList<String>();
	private boolean ignore_nex_word;
	public StringLexer(String lexer) {
		this.str=lexer;
		this.position=0;
		this.size=lexer.length();
		this.setIgnore_nex_word(false);
	}

	@Override
	public char current() {
		return str.charAt(position);
	}

	@Override
	public char get()  {
		char c= current();
		position++;
		return c;
	}

	@Override
	public void skipWhiteSpace() {
		while(isWhitespace(current())) {
			position++;
		}
	}

	@Override
	public void next(){
		position++;
		skipWhiteSpace();
	}

	@Override
	public void check_next_char(char c) throws HtmlParseException {
		if(!(c==current())) {
			System.out.println("error found :"+current()+" expected "+c+"postion :"+position);
		}
		//throw new HtmlParseException();
	}


	public boolean isWhitespace(char c) {
		char whitespace []= {' ','\t','\n'};
		for (int i = 0; i < whitespace.length; i++) {
			if(whitespace[i]==c) {
				return true;
			}
		}
		return false;
	}

	public int getPosition() {
		return position;
	}

	public List<String> getListDurl() {
		return listDurl;
	}

	public void setListDurl(List<String> listDurl) {
		this.listDurl = listDurl;
	}

	public List<String> getListmot() {
		return listmot;
	}

	public void setListmot(List<String> listmot) {
		this.listmot = listmot;
	}

	public boolean isIgnore_nex_word() {
		return ignore_nex_word;
	}

	public void setIgnore_nex_word(boolean ignore_nex_word) {
		this.ignore_nex_word = ignore_nex_word;
	}

	public int getSize() {
		return size;
	}

}
