package fulltextsearch.processdoc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class TikaParser {
	
	public String autoParse(byte[] data) {
	   Parser parser = new AutoDetectParser();
	   BodyContentHandler handler = new BodyContentHandler();
	   Metadata metadata = new Metadata();
	   ParseContext context = new ParseContext();
	   
	   InputStream inputStream = null;
	   inputStream = new ByteArrayInputStream(data);
	   
	   try {
		parser.parse(inputStream, handler, metadata, context);
	} catch (IOException e) {
		e.printStackTrace();
	} catch (SAXException e) {
		e.printStackTrace();
	} catch (TikaException e) {
		e.printStackTrace();
	} finally {
		if(inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	   return handler.toString();
	}
}
