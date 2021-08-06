package parser;

public class HtmlAttribute extends HtmlValue {
	private StringBuilder attribue = new StringBuilder();
	private StringBuilder nom_de_la_balise = new StringBuilder();
	private StringBuilder valeur = new StringBuilder();
	private char  Balise_auvrante='<';
	private char  Balise_ferment='>';
	private char  antislash='/';
	private char  egal='=';
	private char  epsace=' ';
	private char  guillemets='\"';
	private char  exclamation='!';
	public HtmlAttribute() {
	}
	public HtmlAttribute(String str) {
	}
	@Override
	public void parse(Lexer l) throws  HtmlParseException {
		nom_de_la_balise = new StringBuilder();
		l.check_next_char(Balise_auvrante);
		l.next();
		if(l.current()==antislash) {
			l.next();
			while(l.current()!=epsace&&l.current()!=Balise_ferment) {
				nom_de_la_balise.append(l.get());
			}
		}else if(l.current()==exclamation) {
			l.next();
			while(l.current()!=Balise_ferment) {
				nom_de_la_balise.append(l.get());
			}
		}else {
			while(l.current()!=epsace&&l.current()!=Balise_ferment&&l.current()!=antislash) {
				nom_de_la_balise.append(l.get());
			}
			if(nom_de_la_balise.toString().equals("script")) {
				l.setIgnore_nex_word(true);
			}
			if(nom_de_la_balise.toString().equals("style")) {
				l.setIgnore_nex_word(true);
			}
			l.skipWhiteSpace();
			if(l.current()!=Balise_ferment) {
				Attribute_valeur(l);
			}
		}
		l.skipWhiteSpace();
	}
	
	public void Attribute_valeur(Lexer l)throws HtmlParseException {
		attribue = new StringBuilder();
		valeur = new StringBuilder();
		while(l.current()!=egal) {
			attribue.append(l.get());
			if(l.current()==epsace)l.skipWhiteSpace();
		}
		l.check_next_char(egal);
		l.next();
		l.check_next_char(guillemets);
		l.next();
		while(l.current()!=guillemets) {
			valeur.append(l.get());
		}
		l.next();
		if(l.current()==antislash){
			l.get();
		}
		if(l.current()!=Balise_ferment) {
			if(attribue.toString().equals("href")) {
				l.getListDurl().add(valeur.toString());
			};
			Attribute_valeur(l);
		}else {
			if(attribue.toString().equals("href")) {
				l.getListDurl().add(valeur.toString());
			};
		}

	}
}
