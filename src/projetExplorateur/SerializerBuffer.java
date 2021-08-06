package projetExplorateur;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SerializerBuffer {
	final static Charset charset = Charset.forName("UTF-8");
	public ByteBuffer byteBuffer;
	private Map<Integer , Integer> objMap = new HashMap<>();
	private ArrayList<MySerialisable> objArray = new ArrayList<>();

	@Override
	public String toString() {
		return byteBuffer.toString();
	}

	public SerializerBuffer(ByteBuffer bb) {
		this.byteBuffer =bb;
	}

	public void writeString(String s) {
		ByteBuffer bs = charset.encode(s);
		byteBuffer.putInt(bs.remaining());
		byteBuffer.put(bs);
	}

	public String readString() {
		int n = byteBuffer.getInt();
		int lim = byteBuffer.limit();
		byteBuffer.limit(byteBuffer.position()+n);
		String s = charset.decode(byteBuffer).toString();
		//System.out.println("ssssssssssssss "+s);
		byteBuffer.limit(lim);
		return s;		
	}

	public void writeMySerialisable ( MySerialisable m) {
		if(m==null) {
			byteBuffer.putInt(-2);
		}else {
			Integer bind = objMap.get(System.identityHashCode(m));
			
			if(bind==null) {
				//System.out.println("bind 3"+bind);
				objArray.add(m);
				objMap.put(System.identityHashCode(m), objArray.size()-1);
//				System.out.println("byteBuffer "+byteBuffer.toString());
//				System.out.println("byteBuffer capacytu "+byteBuffer.capacity() );
				byteBuffer.putInt(-1);
				m.writeToBuff(this);
			}else {
				//System.out.println("bind 4"+bind);
				byteBuffer.putInt(bind);
			}
		}
	};

	@SuppressWarnings("unchecked")
	public <T extends MySerialisable> T readMySerialisable ( Creator<T> c) {
		int bind = byteBuffer.getInt();
		if(bind==-2) {
			return null;
		}
		if(bind==-1) {
			//System.out.println("bind"+bind);
			T m =c.init();
			objArray.add(m);
			objMap.put(System.identityHashCode(m), objArray.size()-1);
			m.readFromBuff(this);
			return m;
		}else {
			//System.out.println("bind"+bind);
			return (T) objArray.get(bind);
		}
	};
}
