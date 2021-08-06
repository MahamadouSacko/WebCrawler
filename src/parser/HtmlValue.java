package parser;

public abstract class HtmlValue {
	public abstract void parse(Lexer l) throws HtmlParseException;
	public static  HtmlValue parseValue(Lexer l) throws HtmlParseException{
		HtmlValue htmlvalue = null;
		if((l.getPosition()+1)>=l.getSize()) {
			return null;
		}
		if(l.current()=='<') {
			htmlvalue=new HtmlAttribute();
			htmlvalue.parse(l);
		}else if(l.current()=='>') {
			htmlvalue=new HtmlTextContent();
			htmlvalue.parse(l);
		}
		
		return htmlvalue;
	}
}
