package protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

class Data implements Serializable {
  Map<Enthrallment.Player, Enthrallment.Player> players;
  
  Map<Enthrallment.UseridZoneid, Enthrallment.Player> onlines;
  
  Enthrallment.LightTimeout timeout;
  
  EnthrallmentConfig config;
  
  Data() {
    reset();
  }
  
  void reset() {
    this.config = new EnthrallmentConfig();
    this.players = new HashMap<Enthrallment.Player, Enthrallment.Player>();
    this.onlines = new HashMap<Enthrallment.UseridZoneid, Enthrallment.Player>();
    this.timeout = new Enthrallment.LightTimeout(this.config.getMaxTimeout() + 1, this.config.getPrecision());
  }
  
  void apply(EnthrallmentConfig paramEnthrallmentConfig) {
    EnthrallmentConfig enthrallmentConfig = this.config;
    try {
      this.config = paramEnthrallmentConfig;
      this.timeout.start(this.config.getMaxTimeout() + 1, this.config.getPrecision());
    } catch (Throwable throwable) {
      this.config = enthrallmentConfig;
      throw new RuntimeException(throwable);
    } 
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\Enthrallment$Data.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */