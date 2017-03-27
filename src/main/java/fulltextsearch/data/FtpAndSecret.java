package fulltextsearch.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import fulltextsearch.appdaemon.AppConfig;

public class FtpAndSecret {
	FTPClient ftpClient = null;
	
	public byte[] getRawFtpFile(String fileId, String fileVer) {
 
		if(ftpClient == null) {
			ftpClient = new FTPClient();
		}
        
		ByteArrayOutputStream btOutStream = new ByteArrayOutputStream();
		
        try {
            ftpClient.connect(AppConfig.getFtpAddress(), 
            		AppConfig.getFtpAddressPort());
            ftpClient.login(AppConfig.getFtpusername(),
            		AppConfig.getFtppassword());
            
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
 
            String remoteFile2 = "/DE_DOCUMENTS/00086$10.dwg";
//            File downloadFile2 = new File("D:/Downloads/song.mp3");
//            OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
//            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
//            byte[] bytesArray = new byte[4096];
//            int bytesRead = -1;
//            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
//                outputStream2.write(bytesArray, 0, bytesRead);
//            }
            
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
            
	        byte[] bytesArray = new byte[4096];
	        int bytesRead = -1;
	        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
	            btOutStream.write(bytesArray, 0, bytesRead);
	        }
 
            if (ftpClient.completePendingCommand()) {
                System.out.println(remoteFile2 + " has been downloaded successfully.");
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
	
}
