package parser;


public class HtmlTextContent extends HtmlValue{
	String str;
	StringBuilder word = new StringBuilder();
	private char  Balise_auvrante='<';
	private char  Balise_ferment='>';
	public HtmlTextContent() {

	}
	public HtmlTextContent(String s) {
		this.str=s;

	}
	@Override
	public void parse(Lexer l) throws  HtmlParseException {
		word = new StringBuilder();
		l.skipWhiteSpace();
		l.check_next_char(Balise_ferment);	
		l.next();
		while(l.current()!=Balise_auvrante) {
			word.append(l.get());
		}
		if(word.toString().length()>0) {
			if(l.isIgnore_nex_word()==true) {
				l.setIgnore_nex_word(false);
			}else {
				l.getListmot().add(word.toString());
			}
		}
		l.skipWhiteSpace();
	}
}
