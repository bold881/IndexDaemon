package fulltextsearch.data;

public class EnDecrypt
  extends AESclass
{
  public String strEncrypt(String content, byte[][] key)
  {
    this.allkey = key(key);
    this.enbyte = content.getBytes();
    en();
    String ciphertext = parseByte2HexStr(this.debyte);
    return ciphertext;
  }
  
  public void en()
  {
    byte[][] temp = new byte[4][4];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        temp[i][j] = this.enbyte[(j * 4 + i)];
      }
    }
    temp = add(temp, this.allkey[0]);
    for (int i = 1; i < 12; i++) {
      temp = add(mix(shift(subbyte(temp))), this.allkey[i]);
    }
    temp = add(shift(subbyte(temp)), this.allkey[12]);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        this.debyte[(i * 4 + j)] = temp[j][i];
      }
    }
  }
  
  public String strDecrypt(String content, byte[][] key)
  {
    this.allkey = key(key);
    this.debyte = parseHexStr2Byte(content);
    de();
    String text = new String(this.enbyte);
    return text;
  }
  
  public void de()
  {
    byte[][] temp = new byte[4][4];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        temp[i][j] = this.debyte[(j * 4 + i)];
      }
    }
    temp = add(temp, this.allkey[12]);
    for (int i = 1; i < 12; i++) {
      temp = mix(add(subbyte(shift(temp, -1), -1), this.allkey[(12 - i)]), -1);
    }
    temp = add(subbyte(shift(temp, -1), -1), this.allkey[0]);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        this.enbyte[(i * 4 + j)] = temp[j][i];
      }
    }
  }
  
  public static String parseByte2HexStr(byte[] buf)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < buf.length; i++)
    {
      String hex = Integer.toHexString(buf[i] & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      sb.append(hex.toUpperCase());
    }
    return sb.toString();
  }
  
  public static byte[] parseHexStr2Byte(String hexStr)
  {
    int index = hexStr.length() / 2;
    
    byte[] b = new byte[index];
    for (int i = 0; i < index; i++) {
      b[i] = ((byte)Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 
        16));
    }
    return b;
  }
}
