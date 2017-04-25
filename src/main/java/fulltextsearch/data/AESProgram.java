package fulltextsearch.data;

public class AESProgram
{
  public static String strDecrypt(String cipherText)
  {
    EnDecrypt crypt = new EnDecrypt();
    if (cipherText == null) {
      return null;
    }
    if (cipherText.length() == 32) {
      return crypt.strDecrypt(cipherText, getKey()).trim();
    }
    if (cipherText.length() == 64)
    {
      String originalText1 = crypt.strDecrypt(
        cipherText.substring(0, 32), getKey()).trim();
      String originalText2 = crypt.strDecrypt(
        cipherText.substring(32, 64), getKey()).trim();
      return originalText1 + originalText2;
    }
    if (cipherText.length() == 96)
    {
      String originalText1 = crypt.strDecrypt(
        cipherText.substring(0, 32), getKey()).trim();
      String originalText2 = crypt.strDecrypt(
        cipherText.substring(32, 64), getKey()).trim();
      String originalText3 = crypt.strDecrypt(
        cipherText.substring(64, 96), getKey()).trim();
      return originalText1 + originalText2 + originalText3;
    }
    return null;
  }
  
  public static String strEncrypt(String originalText)
  {
    if ((originalText.length() > 48) || (originalText == null)) {
      return null;
    }
    String[] sss = new String[3];
    if (originalText.length() <= 16)
    {
      sss[0] = originalText.substring(0, originalText.length());
      String append = "";
      for (int i = 0; i < 16 - originalText.length(); i++) {
        append = append + " ";
      }
      int tmp85_84 = 0; String[] tmp85_83 = sss;tmp85_83[tmp85_84] = (tmp85_83[tmp85_84] + append);
    }
    else if ((originalText.length() > 16) && (originalText.length() <= 32))
    {
      sss[0] = originalText.substring(0, 16);
      String append = "";
      for (int i = 0; i < 32 - originalText.length(); i++) {
        append = append + " ";
      }
      sss[1] = (originalText.substring(16, originalText.length()) + append);
    }
    else if ((originalText.length() > 32) && (originalText.length() < 48))
    {
      sss[0] = originalText.substring(0, 16);
      sss[1] = originalText.substring(16, 32);
      String append = "";
      for (int i = 0; i < 48 - originalText.length(); i++) {
        append = append + " ";
      }
      sss[2] = (originalText.substring(32, originalText.length()) + append);
    }
    EnDecrypt crypt = new EnDecrypt();
    String cipherText = "";
    for (int i = 0; i < sss.length; i++) {
      if (sss[i] != null)
      {
        String cipherText_append = crypt.strEncrypt(sss[i], getKey());
        cipherText = cipherText + cipherText_append;
      }
    }
    return cipherText;
  }
  
  public static byte[][] getKey()
  {
    byte[][] key = new byte[4][6];
    byte[] bt = new byte[24];
    for (int i = 0; i < 12; i++)
    {
      bt[(2 * i)] = 91;
      bt[(2 * i + 1)] = 14;
    }
    int k = 0;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 6; j++)
      {
        key[i][j] = bt[k];
        k++;
      }
    }
    return key;
  }
}
