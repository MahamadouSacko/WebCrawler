package projetExplorateur;

public class Message implements MySerialisable {
	
	String message;
	
	static public Creator<Message> CREATOR = new Creator<Message>(){
		@Override
		public
		Message init() {
			return new Message();
		}
	};
	
	public Message() {}
	
	public Message(String message) {
		this.message=message;
	}
	
	@Override
	public String toString() {
		return message;
	}
	
	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.writeString(message);
	}
	@Override
	public void readFromBuff(SerializerBuffer ms) {
		message = ms.readString();
	}

}
