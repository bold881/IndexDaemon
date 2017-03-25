package fulltextsearch.data;

public class FtpAndSecret {
	private final String KEY_ALGORITHM = "AES"; //$NON-NLS-1$

	private final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding"; //$NON-NLS-1$

	private final int CACHESIZE = 65536;

	private String EKYSTR = "EFA63819A95EB85CC5EE0B17C2177E74"; //$NON-NLS-1$

	private static byte[] encodAndDecodeKey = null;

	private static documentModel dm;

	private static AES aes = null;

	private AES() {
	    try {
	      if (encodAndDecodeKey == null) {
	        encodAndDecodeKey = initKey();
	      }
	    }
	    catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }

	  public static AES getInstance(documentModel dm0) {
	    if (aes == null) {
	      dm = dm0;
	      aes = new AES();
	    }
	    return aes;
	  }

	  public static void main(String[] args) {
	    try {
	      if (args[0].equals("1")) { //$NON-NLS-1$
	        byte[] dbyte = getInstance(null).encrypt(args[1].getBytes(), encodAndDecodeKey);
	        System.out.println(parseByte2HexStr(dbyte));
	      }
	      else if (args[0].equals("2")) { //$NON-NLS-1$
	        byte[] dbyte = getInstance(null).decrypt(args[1].getBytes(), encodAndDecodeKey);
	        System.out.println(new String(dbyte));
	      }
	      else if (args[0].equals("3")) { //$NON-NLS-1$
	        getInstance(null).encryptFile(args[1], args[2], getInstance(null).toKey(encodAndDecodeKey));
	      }
	      else if (args[0].equals("4")) { //$NON-NLS-1$
	        getInstance(null).decryptFile(args[1], args[2], getInstance(null).toKey(encodAndDecodeKey));
	      }
	    }
	    catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }

//		×ª»»16œøÖÆ
	  private static String parseByte2HexStr(byte buf[]) {
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < buf.length; i++) {
	      String hex = Integer.toHexString(buf[i] & 0xFF);
	      if (hex.length() == 1) {
	        hex = '0' + hex;
	      }
	      sb.append(hex.toUpperCase());
	    }
	    return sb.toString();
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

	  public byte[] initKey() throws Exception {
	    if (dm != null) {
	      try { ///
	        ///ÃÜÔ¿¶ÁÈë---------------------------------------------------------------
	        String sql = "select CKeyStr from " + TableName.SYS_DES + " where ALGORITHM='AES' "; //$NON-NLS-1$ //$NON-NLS-2$
	        ArrayList rsList = dm.getResultSet(1, sql);
	        if (rsList == null || rsList.size() < 1) {
	          return null;
	        }
	        EKYSTR = String.valueOf(rsList.get(0));
	      }
	      catch (Exception ex) {
	        ex.printStackTrace();
	      }
	    }
//	          KeyGenerator kg= KeyGenerator.getInstance(KEY_ALGORITHM);
//	          kg.init(128,new SecureRandom(EKYSTR.getBytes("GBK")));
//	          SecretKey secretKey=kg.generateKey();
	    //»ñÈ¡¶þœøÖÆÃÜÔ¿±àÂëÐÎÊœ
	    return parseHexStr2Byte(EKYSTR);
	  }

	  public byte[] encrypt(byte[] data, byte[] key) throws Exception {
	    Key k = toKey(key);
	    Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
	    cipher.init(Cipher.ENCRYPT_MODE, k);
	    return cipher.doFinal(data);
	  }

	  public void encryptFile(String in, String out) throws Exception {
	    Key k = toKey(encodAndDecodeKey);
	    encryptFile(in, out, k);
	  }

	  public void encryptFile(String in, String out, Key k) throws Exception {
//	          long after = System.currentTimeMillis();
	    FileInputStream input = new FileInputStream(in);
	    FileOutputStream output = new FileOutputStream(out);
	    Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
	    cipher.init(Cipher.ENCRYPT_MODE, k);
	    byte[] buffer = new byte[CACHESIZE];
	    CipherOutputStream cipherOut = new CipherOutputStream(output, cipher);
	    int i = 0;
	    while ( (i = input.read(buffer)) != -1) {
	      cipherOut.write(buffer, 0, i);
	      output.flush();
	    }
	    cipherOut.close();
	    input.close();
	    output.close();
//	          System.out.println("ŒÓÃÜ:" + (System.currentTimeMillis() - after));
	  }

	  public void decryptFile(String in, String out) throws Exception {
	    Key k = toKey(encodAndDecodeKey);
	    decryptFile(in, out, k);
	  }

	  public void decryptFile(String in, String out, Key k) throws Exception {
//	          long after = System.currentTimeMillis();
	    FileInputStream input = new FileInputStream(in);
	    FileOutputStream output = new FileOutputStream(out);
	    Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
	    cipher.init(Cipher.DECRYPT_MODE, k);

	    byte[] buffer = new byte[CACHESIZE];
	    CipherOutputStream cipherOut = new CipherOutputStream(output, cipher);
	    int i = 0;
	    while ( (i = input.read(buffer)) != -1) {
	      cipherOut.write(buffer, 0, i);
	      cipherOut.flush();
	    }
	    cipherOut.close();
	    input.close();
	    output.close();
//	          System.out.println("œâÃÜ:" + (System.currentTimeMillis() - after));
	  }

	  public byte[] decrypt(byte[] data, byte[] key) throws Exception {
	    //»¶Ó­ÃÜÔ¿
	    Key k = toKey(key);
	    Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
	    //³õÊŒ»¯£¬ÉèÖÃÎªœâÃÜÄ£Êœ
	    cipher.init(Cipher.DECRYPT_MODE, k);
	    //ÖŽÐÐ²Ù×÷
	    return cipher.doFinal(data);
	  }

	  public Key toKey(byte[] key) throws Exception {
	    //ÊµÀý»¯DESÃÜÔ¿
	    //Éú³ÉÃÜÔ¿
	    SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
	    return secretKey;
	  }

	  /**
	   * ŒÓÃÜ
	   *
	   * @param content
	   *            ŽýŒÓÃÜÄÚÈÝ
	   * @param key
	   *            ŒÓÃÜµÄÃÜÔ¿
	   * @return
	   */
	  public String encrypt(String content) {
	    try {
	      KeyGenerator kgen = KeyGenerator.getInstance("AES");
	      kgen.init(128, new SecureRandom(EKYSTR.getBytes()));
	      SecretKey secretKey = kgen.generateKey();
	      byte[] enCodeFormat = secretKey.getEncoded();
	      SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
	      Cipher cipher = Cipher.getInstance("AES");
	      byte[] byteContent = content.getBytes("utf-8");
	      cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
	      byte[] byteRresult = cipher.doFinal(byteContent);
	      StringBuffer sb = new StringBuffer();
	      for (int i = 0; i < byteRresult.length; i++) {
	        String hex = Integer.toHexString(byteRresult[i] & 0xFF);
	        if (hex.length() == 1) {
	          hex = '0' + hex;
	        }
	        sb.append(hex.toUpperCase());
	      }
	      return sb.toString();
	    }
	    catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }
	    catch (NoSuchPaddingException e) {
	      e.printStackTrace();
	    }
	    catch (InvalidKeyException e) {
	      e.printStackTrace();
	    }
	    catch (UnsupportedEncodingException e) {
	      e.printStackTrace();
	    }
	    catch (IllegalBlockSizeException e) {
	      e.printStackTrace();
	    }
	    catch (BadPaddingException e) {
	      e.printStackTrace();
	    }
	    return null;
	  }

	  /**
	   * œâÃÜ
	   *
	   * @param content
	   *            ŽýœâÃÜÄÚÈÝ
	   * @param key
	   *            œâÃÜµÄÃÜÔ¿
	   * @return
	   */
	  public String decrypt(String content) {
	    if (content.length() < 1) {
	      return null;
	    }
	    byte[] byteRresult = new byte[content.length() / 2];
	    for (int i = 0; i < content.length() / 2; i++) {
	      int high = Integer.parseInt(content.substring(i * 2, i * 2 + 1), 16);
	      int low = Integer.parseInt(content.substring(i * 2 + 1, i * 2 + 2),
	                                 16);
	      byteRresult[i] = (byte) (high * 16 + low);
	    }
	    try {
	      KeyGenerator kgen = KeyGenerator.getInstance("AES");
	      kgen.init(128, new SecureRandom(EKYSTR.getBytes()));
	      SecretKey secretKey = kgen.generateKey();
	      byte[] enCodeFormat = secretKey.getEncoded();
	      SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
	      Cipher cipher = Cipher.getInstance("AES");
	      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
	      byte[] result = cipher.doFinal(byteRresult);
	      return new String(result);
	    }
	    catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }
	    catch (NoSuchPaddingException e) {
	      e.printStackTrace();
	    }
	    catch (InvalidKeyException e) {
	      e.printStackTrace();
	    }
	    catch (IllegalBlockSizeException e) {
	      e.printStackTrace();
	    }
	    catch (BadPaddingException e) {
	      e.printStackTrace();
	    }
	    return null;
	  }
}
