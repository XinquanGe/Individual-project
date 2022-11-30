package cosc310_T28_librarySystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class JokeAPI {
    JokeAPI(){}
    public void getjoke() throws IOException  {
	URL u = new URL("https://v2.jokeapi.dev/joke/Any?format=txt&safe-mode");
	InputStream in =  u.openStream();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
		byte buf[] = new byte[1024];
		int read = 0;
		while ((read = in.read(buf)) > 0) {
		out.write(buf, 0, read);
		}
		} finally {
		if ( in != null) {
		in.close();
	}
	}
	byte b[] = out.toByteArray();
	String res = new String(b, "utf-8");
	 System.out.println(res);
    
}
}
