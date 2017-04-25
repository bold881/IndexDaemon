package fulltextsearch.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Security;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 1997</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class DES {

  //Զ�̽ӿ�
//  private documentModel RemoteInterface = DocSql.getInstance().getDocumentModel();
  //���ܺͽ��ܵ�Կ��
  private byte[] EncodAndDecodeKey = null;
  //�ӽ����㷨
  private static String strAlgorithm = "DESede";

  public DES() {
	  
  }

//  public void GenerateKey() {
//    try {
//      //����SunJCE provider
//      Security.addProvider(new com.sun.crypto.provider.SunJCE());
//      //��Կ������
//      KeyGenerator KeyGen = KeyGenerator.getInstance("DESede");
//      //��Կ
//      SecretKey theKey = KeyGen.generateKey();
//      //��Կ�ֽ���
//
//      //Ҫ���������keyencoded
//      byte[] keyencoded = theKey.getEncoded();
//      String keyStr = "";
//      for (int i = 0; i < keyencoded.length; i++) {
//        keyStr += (char) keyencoded[i];
//      }
//      this.RemoteInterface.executeSql("delete from " + TableName.SYS_DES);
//      String sql = "Insert into " + TableName.SYS_DES + "(CKeyStr) values('" +
//          keyStr + "')";
//      this.RemoteInterface.executeSql(sql);
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//    }
//  }

  //------------------------------------
  //��Կ���ɺ��� GenerateKey
  //�������:
  //   strKeyFile: ��Կ�����ļ�(��������)
  //------------------------------------
  public void GenerateKey(String strKeyFile) {
    try {

      //����SunJCE provider
      Security.addProvider(new com.sun.crypto.provider.SunJCE());
      //��Կ������
      KeyGenerator KeyGen = KeyGenerator.getInstance("DESede");
      //��Կ
      SecretKey theKey = KeyGen.generateKey();
      //��Կ�ֽ���

      //Ҫ���������keyencoded
      byte[] keyencoded = theKey.getEncoded();

      int keylen = keyencoded.length;

      FileOutputStream outkey = new FileOutputStream(strKeyFile);
      outkey.write(keylen);
      outkey.write(keyencoded);
      outkey.close();

    }
    catch (java.security.NoSuchAlgorithmException e1) {
      e1.printStackTrace();
    }
    catch (java.io.IOException e2) {
      e2.printStackTrace();
    }

  }

  //----------------------------------
  //�ļ����ܺ��� EncryptData
  //�������:
  //  strSourceFile:�����ļ�
  //  strEnDataFile:�����ļ�(��������)
  //  strKeyFile:   ��Կ�ļ�
  //----------------------------------

  public void EncryptData(String strSourceFile, String strEnDataFile,
                          String strKeyFile) {

    try {

      ///��Կ����----------------------------------------------------------------
      FileInputStream inkey = new FileInputStream(strKeyFile);
      int keylen = inkey.read();
      byte[] keyencoded = new byte[keylen];
      inkey.read(keyencoded);

      javax.crypto.spec.SecretKeySpec destmp = new javax.crypto.spec.
          SecretKeySpec(keyencoded, "DESede");
      SecretKey theKey = destmp;
      ///-----------------------------------------------------------------------

      ///����ϵͳ��ʼ��-----------------------------------------------------------

      //��ʼ������
      IvParameterSpec ivp = new IvParameterSpec(new byte[] {12, 34, 56, 78, 90,
                                                87, 65, 43});

      //�����ض�����������
      Cipher cipher = Cipher.getInstance("DESede");

      //��ʼ��������
      cipher.init(Cipher.ENCRYPT_MODE, theKey, ivp);

      ///------------------------------------------------------------------------
      FileInputStream in = new FileInputStream(strSourceFile);
      FileOutputStream out = new FileOutputStream(strEnDataFile);

      ///����--------------------------------------------------------------------

      byte[] Data = new byte[128];
      int bytes = 0;
      while ( (bytes = in.read(Data)) > 0) {
        if (bytes == 128) {
          byte[] encryptedtext = cipher.doFinal(Data);
          int len = encryptedtext.length;
          out.write(len);
          out.write(encryptedtext);
        }
        else {
          byte[] DataEnd = new byte[bytes];
          //for(int i=0;i<bytes;i++)DataEnd[i]=Data[i];
          System.arraycopy(Data, 0, DataEnd, 0, bytes);
          byte[] encryptedtext = cipher.doFinal(DataEnd);
          int len = encryptedtext.length;
          out.write(len);
          out.write(encryptedtext);
        }
      }

      ///------------------------------------------------------------------------

      in.close();
      out.close();

    }
    catch (java.security.NoSuchAlgorithmException e1) {
      e1.printStackTrace();
    }
    catch (javax.crypto.NoSuchPaddingException e2) {
      e2.printStackTrace();
    }
    catch (java.lang.Exception e3) {
      e3.printStackTrace();
    }
    catch (java.lang.NoSuchMethodError e4) {
      e4.printStackTrace();
    }
  }

//----------------------------------
  //�ļ����ܺ��� EncryptData
  //�������:
  //  strSourceFile:�����ļ�
  //  strEnDataFile:�����ļ�(��������)
  //modified by yangbo 2003.5.15
  //----------------------------------

  public boolean EncryptData(String strSourceFile, String strEnDataFile) {
    try {
      ///��Կ����---------------------------------------------------------------
      SecretKeySpec destmp = new SecretKeySpec(EncodAndDecodeKey, "DESede");
      SecretKey theKey = destmp;
      ///-----------------------------------------------------------------------
      ///����ϵͳ��ʼ��-----------------------------------------------------------

      //��ʼ������
//      IvParameterSpec ivp = new IvParameterSpec(new byte[] {12, 34, 56, 78, 90,
//                                                87, 65, 43});

      //�����ض�����������
      Cipher cipher = Cipher.getInstance("DESede");

      //��ʼ��������
      cipher.init(Cipher.ENCRYPT_MODE, theKey) /*, ivp)*/;

      ///------------------------------------------------------------------------
      FileInputStream in = new FileInputStream(strSourceFile);
      FileOutputStream out = new FileOutputStream(strEnDataFile);

      ///����--------------------------------------------------------------------

      byte[] Data = new byte[128];
      int bytes = 0;
      while ( (bytes = in.read(Data)) > 0) {
        if (bytes == 128) {
          byte[] encryptedtext = cipher.doFinal(Data);
          int len = encryptedtext.length;
          out.write(len);
          out.write(encryptedtext);
        }
        else {
          byte[] DataEnd = new byte[bytes];
          //for(int i=0;i<bytes;i++)DataEnd[i]=Data[i];
          System.arraycopy(Data, 0, DataEnd, 0, bytes);
          byte[] encryptedtext = cipher.doFinal(DataEnd);
          int len = encryptedtext.length;
          out.write(len);
          out.write(encryptedtext);
        }
      }

      ///------------------------------------------------------------------------

      in.close();
      out.close();
      return true;
    }
    catch (java.io.FileNotFoundException fnfex) {
      JOptionPane.showMessageDialog(null, "ϵͳ�Ҳ���ָ�����ļ�:" + strSourceFile, "��ʾ",
                                    JOptionPane.ERROR_MESSAGE);
//      fnfex.printStackTrace();
    }
    catch (java.security.NoSuchAlgorithmException e1) {
//      e1.printStackTrace();
    }
    catch (javax.crypto.NoSuchPaddingException e2) {
//      e2.printStackTrace();
    }
    catch (java.lang.Exception e3) {
//      e3.printStackTrace();
    }
    catch (java.lang.NoSuchMethodError e4) {
//      e4.printStackTrace();
    }
    return false;
  }

  //----------------------------------
  //�ļ����ܺ��� DecryptData
  //�������:
  //  strEnDataFile:�����ļ�
  //  strDeDataFile:�����ļ�(��������)
  //  strKeyFile:   ��Կ�ļ�
  //----------------------------------

  public void DecryptData(String strEnDataFile, String strDeDataFile,
                          String strKeyFile) {
    try {
      //��Կ����----------------------------------------------------------------
      FileInputStream inkey = new FileInputStream(strKeyFile);
      int keylen = inkey.read();
      byte[] keyencoded = new byte[keylen];
      inkey.read(keyencoded);
      javax.crypto.spec.SecretKeySpec destmp = new javax.crypto.spec.
          SecretKeySpec(keyencoded, "DESede");
      SecretKey theKey = destmp;
      ///------------------------------------------------------------------------
      //��ʼ������
      IvParameterSpec ivp = new IvParameterSpec(new byte[] {12, 34, 56, 78, 90,
                                                87, 65, 43});
      //�����ض�����������
      Cipher cipher = Cipher.getInstance("DESede");
      //��ʼ��������
      cipher.init(Cipher.DECRYPT_MODE, theKey, ivp);
      FileInputStream in = new FileInputStream(strEnDataFile);
      FileOutputStream out = new FileOutputStream(strDeDataFile);
      ///����--------------------------------------------------------------------
      int len;
      while ( (len = in.read()) > 0) {
        byte[] Data = new byte[len];
        in.read(Data);
        byte[] decryptedtext = cipher.doFinal(Data);
        out.write(decryptedtext);
      }

      ///------------------------------------------------------------------------

      in.close();
      out.close();
    }
    catch (java.security.NoSuchAlgorithmException e1) {
      e1.printStackTrace();
    }
    catch (javax.crypto.NoSuchPaddingException e2) {
      e2.printStackTrace();
    }
    catch (java.lang.Exception e3) {
      e3.printStackTrace();
    }
    catch (java.lang.NoSuchMethodError e4) {
      e4.printStackTrace();
    }
  }

  //----------------------------------
  //�ļ����ܺ��� DecryptData
  //�������:
  //  strEnDataFile:�����ļ�
  //  strDeDataFile:�����ļ�(��������)
  //----------------------------------

  public void DecryptData(String strEnDataFile, String strDeDataFile) {
    try {
      ///��Կ����---------------------------------------------------------------
      SecretKeySpec destmp = new SecretKeySpec(EncodAndDecodeKey, "DESede");
      SecretKey theKey = destmp;
      ///------------------------------------------------------------------------
//      //��ʼ������
//      IvParameterSpec ivp = new IvParameterSpec(new byte[] {12, 34, 56, 78, 90,
//                                                87, 65, 43});
      //�����ض�����������
      Cipher cipher = Cipher.getInstance("DESede");
      //��ʼ��������
      cipher.init(Cipher.DECRYPT_MODE, theKey); //, ivp);
      FileInputStream in = new FileInputStream(strEnDataFile);
      FileOutputStream out = new FileOutputStream(strDeDataFile);
      ///����--------------------------------------------------------------------
      int len;
      while ( (len = in.read()) > 0) {
        byte[] Data = new byte[len];
        in.read(Data);
        byte[] decryptedtext = cipher.doFinal(Data);
        out.write(decryptedtext);
      }

      ///------------------------------------------------------------------------
      in.close();
      out.close();
    }
    catch (java.security.NoSuchAlgorithmException e1) {
      e1.printStackTrace();
    }
    catch (javax.crypto.NoSuchPaddingException e2) {
      e2.printStackTrace();
    }
    catch (java.lang.Exception e3) {
      e3.printStackTrace();
    }
    catch (java.lang.NoSuchMethodError e4) {
      e4.printStackTrace();
    }
  }

  //����
  public static String EncryptString(String strSource) {
    if (strSource == null) {
      return null;
    }
    String strKey = "??Qb1?????????C???Qb1???";
    int size = strKey.length();
    byte[] temKey = strKey.getBytes();
    byte[] EncodAndDecodeKey = new byte[size];
    for (int i = 0; i < size; i++) {
      EncodAndDecodeKey[i] = temKey[i];
    }

    Security.addProvider(new com.sun.crypto.provider.SunJCE());
    try {
      SecretKeySpec tmpkey = new SecretKeySpec(EncodAndDecodeKey, strAlgorithm);
      SecretKey deskey = tmpkey;

      Cipher c1 = Cipher.getInstance(strAlgorithm);
      c1.init(Cipher.ENCRYPT_MODE, deskey);
      byte[] cipherByte = c1.doFinal(strSource.getBytes());

      //���ֽ���ת��Ϊ�ַ�����������new String(cipherByte)����.
//      String strEncrypt = "";
//      for (int i = 0; i < cipherByte.length; i++) {
//        strEncrypt += (char) cipherByte[i];
//      }
      String strEncrypt = "";
      String strTest = "";
      for (int i = 0; i < cipherByte.length; i++) {
        if (i != cipherByte.length - 1) {
          strEncrypt = strEncrypt + cipherByte[i] + ",";
        }
        else {
          strEncrypt = strEncrypt + cipherByte[i];
        }
      }

      return strEncrypt;
    }
    catch (java.security.NoSuchAlgorithmException e1) {
      e1.printStackTrace();
      return null;
    }
    catch (javax.crypto.NoSuchPaddingException e2) {
      e2.printStackTrace();
      return null;
    }
    catch (java.lang.Exception e3) {
      e3.printStackTrace();
      return null;
    }
  }

  /*******************************************************
   * �۸�����㷨������
   *******************************************************/
  @SuppressWarnings("restriction")
public static String DecryptString(String strEncrypt) {
    if (strEncrypt == null || strEncrypt.equals("")) {
      return "";
    }
    String strKey = "??Qb1?????????C???Qb1???";
    int size = strKey.length();
    byte[] temKey = strKey.getBytes();
    byte[] EncodAndDecodeKey = new byte[size];
    for (int i = 0; i < size; i++) {
      EncodAndDecodeKey[i] = temKey[i];
    }

    Security.addProvider(new com.sun.crypto.provider.SunJCE());
    try {
      SecretKeySpec tmpkey = new SecretKeySpec(EncodAndDecodeKey, strAlgorithm);
      SecretKey deskey = tmpkey;

      Cipher c1 = Cipher.getInstance(strAlgorithm);
      c1 = Cipher.getInstance(strAlgorithm);
      c1.init(Cipher.DECRYPT_MODE, deskey);


      ArrayList altGetBytes = new ArrayList();
      for (int i = 0; i < strEncrypt.length(); i++) {
        int iStart = i;
        i = strEncrypt.indexOf(",", i);
        if (i < 0 || i >= strEncrypt.length()) {
          i = strEncrypt.length();
        }
        String strGetedByte = strEncrypt.substring(iStart, i);
        altGetBytes.add(strGetedByte);
      }
      byte[] bEncrypt = new byte[altGetBytes.size()];
      for (int i = 0; i < altGetBytes.size(); i++) {
        try {
          int ii = Integer.valueOf(altGetBytes.get(i).toString()).intValue();
          bEncrypt[i] = (byte) ii;
        }
        catch (Exception ex) {
          return strEncrypt;
        }
      }
      byte[] cipherByte = c1.doFinal(bEncrypt);
      String strDecrypt = new String(cipherByte);
      return strDecrypt;
    }
    catch (java.security.NoSuchAlgorithmException e1) {
      e1.printStackTrace();
      return null;
    }
    catch (javax.crypto.NoSuchPaddingException e2) {
      e2.printStackTrace();
      return null;
    }
    catch (java.lang.Exception e3) {
      e3.printStackTrace();
      return null;
    }
    catch (java.lang.NoSuchMethodError e4) {
      e4.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) {
	
	  String pswd = "29,-87,-100,117,35,33,58,85";
	  
	  System.out.println(DecryptString(pswd));
	  
	  String str = "admin";
	  System.out.println(EncryptString(str));
	  
}
  
  
  
}
