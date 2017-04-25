package fulltextsearch.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.pojos.InterItem;
import fulltextsearch.pojos.ObjectItem;

public class FtpAndSecret {
	FTPClient ftpClient = null;
	
	private final String KEY_ALGORITHM = "AES";
	
	private final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
	
	public byte[] getRawFtpFile(String fileId, 
			String fileVer,
			String docExt) {
 
		if(ftpClient == null) {
			ftpClient = new FTPClient();
		}
		
		String remoteFilePath = AppConfig.getFtpDocumentsDir() 
				+ fileId + "$" + fileVer + "." + docExt;
        
		ByteArrayOutputStream btOutStream = new ByteArrayOutputStream();
		
        try {
            ftpClient.connect(AppConfig.getFtpAddress(), 
            		AppConfig.getFtpAddressPort());
            ftpClient.login(AppConfig.getFtpusername(),
            		AppConfig.getFtppassword());
            
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
           
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);
            
            if( inputStream != null ) {
    	        byte[] bytesArray = new byte[4096];
    	        int bytesRead = -1;
    	        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
    	            btOutStream.write(bytesArray, 0, bytesRead);
    	        }
     
                if (ftpClient.completePendingCommand()) {
                    System.out.println(remoteFilePath + " has been downloaded successfully.");
                }
                inputStream.close();
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return btOutStream.toByteArray();
	}
	
	public byte[] getRawFtpFile(InterItem interItem) {
		return getRawFtpFile(interItem.getIdPdm(),
				interItem.getVerPdm(),
				interItem.getDocformat());
	}
	
	public byte[] getRawFtpFile(ObjectItem interItem) {
		return getRawFtpFile(interItem.getId(),
				interItem.getVer(),
				interItem.getFormat());
	}
	
	private byte[] parseHexStr2Byte(String hexStr) {
    	if (hexStr.length() < 1) {
	      return null;
	    }
	    byte[] result = new byte[hexStr.length() / 2];
	    for (int i = 0; i < hexStr.length() / 2; i++) {
	      int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
	      int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
	      result[i] = (byte) (high * 16 + low);
	    }
	    return result;
	}
    
	private Key toKey(byte[] key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		return secretKey;
	}
	
	public byte[] decryptStream(byte[] data, String keyValue) {
    	//String EKYSTR = "EC902BD04D87D0DBFAFFA7392C5D5E8A";
    	byte[] encodAndDecodeKey = parseHexStr2Byte(keyValue); 
    	
		try {
			Key k = toKey(encodAndDecodeKey);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		    cipher.init(Cipher.DECRYPT_MODE, k);
		    byte[] decodedByte = cipher.doFinal(data);
	    	return decodedByte;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] getFtpFile(InterItem interItem) {
		byte[] rawFile = getRawFtpFile(interItem);
		
		if(rawFile == null || rawFile.length == 0) {
			return null;
		}
		
		byte[] originFile = decryptStream(rawFile, AppConfig.getFtpPrivateKey());
		
		return originFile;
	}
	
	public byte[] getFtpFile(ObjectItem interItem) {
		byte[] rawFile = getRawFtpFile(interItem);
		
		if(rawFile == null || rawFile.length == 0) {
			return null;
		}
		
		byte[] originFile = decryptStream(rawFile, AppConfig.getFtpPrivateKey());
		
		return originFile;
	}
	
	public String getFtpFileEncodeBase64(InterItem interItem) {
		byte[] orginFile = getFtpFile(interItem);
		if(orginFile == null) {
			return "";
		}
		return Base64.getEncoder().encodeToString(orginFile);
	}
}
