package fulltextsearch.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.pojos.InterItem;

public class FtpAndSecret {
	FTPClient ftpClient = null;
	
	private final String KEY_ALGORITHM = "AES";
	
	private final String KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding";
	
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
            
	        byte[] bytesArray = new byte[4096];
	        int bytesRead = -1;
	        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
	            btOutStream.write(bytesArray, 0, bytesRead);
	        }
 
            if (ftpClient.completePendingCommand()) {
                System.out.println(remoteFilePath + " has been downloaded successfully.");
            }
            inputStream.close();
 
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
	
	public Key getKey(byte[] keyValue) {
		Key secretKey = new SecretKeySpec(keyValue, KEY_ALGORITHM);
		return secretKey;
	}
	
	public byte[] decryptStream(byte[] data, byte[] keyValue) {
		Key Secretkey = getKey(keyValue);
		try {
			Cipher cipher = Cipher.getInstance(KEY_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, Secretkey);
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public byte[] getFtpFile(InterItem interItem) {
		byte[] rawFile = getRawFtpFile(interItem);
		byte[] originFile = decryptStream(rawFile, AppConfig.getFtpPrivateKey().getBytes());
		
		return originFile;
	}
	
	public String getFtpFileEncodeBase64(InterItem interItem) {
		return Base64.getEncoder().encodeToString(getFtpFile(interItem));
	}
}
