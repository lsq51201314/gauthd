package RandomGen;

import java.util.Random;

public class StrGenerator {
  private Random rdgen = new Random();
  
  private static StrGenerator instance = new StrGenerator();
  
  public String Generate_Mix(int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      do {
        b = (byte)(this.rdgen.nextInt() % 64 + 48);
      } while ((b < 50 || b > 57) && (b < 65 || b > 90 || b == 73 || b == 79));
      arrayOfByte[b1] = b;
    } 
    return new String(arrayOfByte);
  }
  
  public String Generate_Num(int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    byte b = 0;
    byte b1 = 0;
    while (b1 < paramInt) {
      while (true) {
        b = (byte)(this.rdgen.nextInt() % 64 + 48);
        if (b >= 48 && b <= 57) {
          arrayOfByte[b1] = b;
          b1++;
        } 
      } 
    } 
    return new String(arrayOfByte);
  }
  
  public static StrGenerator GetInstance() {
    return instance;
  }
}


/* Location:              D:\UserData\Desktop\authd\!\lib\application.jar!\RandomGen\StrGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */