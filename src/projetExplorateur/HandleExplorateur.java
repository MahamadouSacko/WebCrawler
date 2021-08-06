package projetExplorateur;

public class HandleExplorateur {
	
	private String url=null;
	private String etat=null;
	public HandleExplorateur(String url,String etat) {
		this.etat=etat;
		this.url=url;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getEtat() {
		return etat;
	}
	public void setEtat(String etat) {
		this.etat = etat;
	}
	

}
