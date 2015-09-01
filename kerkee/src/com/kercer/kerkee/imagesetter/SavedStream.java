package com.kercer.kerkee.imagesetter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class SavedStream {
	
    private byte[] data = new byte[0];
    
    public SavedStream(Context aContext){
    	InputStream input = null;
        try {
			input = aContext.getAssets().open("native_replace.png");
			save(input);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (input!=null) {
				try {
					input.close();
				} catch (Exception e) {
				}
			}
		}
    }
   
    private void save(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[200];
        int len = -1;
        while ((len = input.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
        data = output.toByteArray();
    }  
   
    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }
}
