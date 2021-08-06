package parser;

import java.util.List;

public interface Lexer {
	/* Renvoie le prochain caractère à lire sans lemodifier
	Cette métho de n ’echoue jamais */
	public char current ( ) ;
	/* renvoie le prochain caractère à lire et incrémente la
	position du caractère courant */
	public char get ( );
	/* incrémente laposition pour atteindre un caractère
	non−blanc ( espace , tabulation , saut de ligne ) ou la fin du fichier */
	public void skipWhiteSpace ( ) ;
	/*Incrémete une foi sinconditionnellement , puis incrémente
	jusqu ’ à atteindre un caractè renon−blanc */
	public void next ( ) ;
	/* vérifie que le caractère courant est celui attendu
	ou renvoi une exception */
	public void check_next_char ( char c ) throws HtmlParseException ;
	public int getPosition();
	public List<String> getListDurl();
	public List<String> getListmot();
	public void setIgnore_nex_word(boolean ignore_nex_word);
	public boolean isIgnore_nex_word();
	public int getSize();
}
