/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml2lua;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author egor
 */
public class xml2lua
{
	static String escapeUTF8(String str)
	{
		String esc="";
		byte[] buf=null;
		try
		{
			buf = str.getBytes("utf-8");
			for(int i=0;i<buf.length;i++)
			{
				int v=(( buf[i]  & 0xFF ));
				esc+="\\"+Integer.toString(v);
			}
		}
		catch (UnsupportedEncodingException ex)
		{
			Logger.getLogger(TranslationConvertor.class.getName()).log(Level.SEVERE, null, ex);
		}
		return esc;
	}

	public static void main(String[] args)
	{
		Properties translation=new Properties();
		FileInputStream fs = null;
		BufferedWriter os=null;
		try
		{
			os=new BufferedWriter(new FileWriter(args[0]));
		}
		catch (IOException ex)
		{
			Logger.getLogger(TranslationConvertor.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}

		try
		{
			fs = new FileInputStream(args[1]);
			translation.loadFromXML(fs);

			os.write("-- GENERATED FILE\n");
			os.write("return{\n");
			os.write("\tunits={\n");
			
			Set<Entry<Object,Object>> set=translation.entrySet();
			for(Entry<Object,Object> e:set)
			{
				String name=(String)e.getKey();
				String data=(String)e.getValue();
				String[] lines=data.split("\n");
				if(lines.length>=3)
				{
					os.write("\t\t"+name+"={\n");
					os.write("\t\t\tdescription=\""+escapeUTF8(lines[1])+"\",\n");
					os.write("\t\t\thelptext=\""+escapeUTF8(lines[2])+"\"\n");
					os.write("\t\t},\n");
				}
				else
				{
					System.out.printf("Shit happens! Text: %s\n", data);
				}
			}
			
			os.write("\t}\n");
			os.write("}\n");
			os.close();
		}
		catch (IOException ex)
		{
			Logger.getLogger(TranslationConvertor.class.getName()).log(Level.SEVERE, null, ex);
		}
		finally
		{
			try
			{
				fs.close();
			}
			catch (IOException ex)
			{
				Logger.getLogger(TranslationConvertor.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
	}
}
