package protocol;

import java.util.TimerTask;

class DelayTask extends TimerTask {
  private final int action;
  
  private final int userid;
  
  private final int zoneid;
  
  private int areaid = 0;
  
  protected DelayTask(int paramInt1, int paramInt2, int paramInt3) {
    this.action = paramInt1;
    this.userid = paramInt2;
    this.zoneid = paramInt3;
  }
  
  protected DelayTask(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this(paramInt1, paramInt2, paramInt4);
    this.areaid = paramInt3;
  }
  
  public void run() {
    try {
      cancel();
      synchronized (Enthrallment.access$000()) {
        dispatch();
      } 
    } catch (Exception exception) {
      System.out.println(this);
      exception.printStackTrace();
    } 
  }
  
  public String toString() {
    return "action=" + this.action + " userid=" + this.userid + " zoneid=" + this.zoneid;
  }
  
  private void dispatch() {
    if (4 == this.action) {
      doLogoutByZoneid(this.zoneid);
      return;
    } 
    Enthrallment.Player player = null;
    switch (this.action) {
      case 1:
        player = Enthrallment.access$100(Enthrallment.this, this.userid, this.areaid, this.zoneid);
        break;
      case 2:
      case 3:
        player = (Enthrallment.access$200(Enthrallment.this)).onlines.get(new Enthrallment.UseridZoneid(this.userid, this.zoneid));
        break;
    } 
    if (null == player)
      return; 
    GAuthServer.GetLog().info("Enthrallment " + this + " player=" + player);
    switch (this.action) {
      case 1:
        player = doLogin(player);
        if (null != player) {
          GAuthServer.GetLog().info("Enthrallment 'logout lost and idcard changed' " + this + " player=" + player);
          doLogout(player);
        } 
        break;
      case 2:
        doLogout(player);
        (Enthrallment.access$200(Enthrallment.this)).onlines.remove(new Enthrallment.UseridZoneid(this.userid, this.zoneid));
        break;
      case 3:
        doRemove(player);
        break;
    } 
  }
  
  private Enthrallment.Player doLogin(Enthrallment.Player paramPlayer) {
    Enthrallment.Player player1 = (Enthrallment.access$200(Enthrallment.this)).players.get(paramPlayer);
    if (null != player1) {
      paramPlayer = player1;
    } else {
      (Enthrallment.access$200(Enthrallment.this)).players.put(paramPlayer, paramPlayer);
    } 
    paramPlayer.login(this.userid, this.zoneid);
    Enthrallment.Player player2 = (Enthrallment.access$200(Enthrallment.this)).onlines.put(new Enthrallment.UseridZoneid(this.userid, this.zoneid), paramPlayer);
    return (player2 == paramPlayer) ? null : player2;
  }
  
  private void doLogout(Enthrallment.Player paramPlayer) {
    paramPlayer = (Enthrallment.access$200(Enthrallment.this)).players.get(paramPlayer);
    if (null != paramPlayer)
      paramPlayer.logout(this.userid, this.zoneid); 
  }
  
  private void doLogoutByZoneid(int paramInt) {
    for (Enthrallment.Player player : (Enthrallment.Player[])(Enthrallment.access$200(Enthrallment.this)).players.keySet().toArray((Object[])new Enthrallment.Player[0]))
      player.logoutByZoneid(paramInt); 
  }
  
  private void doRemove(Enthrallment.Player paramPlayer) {
    paramPlayer = (Enthrallment.access$200(Enthrallment.this)).players.get(paramPlayer);
    if (null != paramPlayer) {
      paramPlayer.cancel();
      (Enthrallment.access$200(Enthrallment.this)).players.remove(paramPlayer);
      for (Enthrallment.Login login : (paramPlayer.getLogins()).data.values())
        (Enthrallment.access$200(Enthrallment.this)).onlines.remove(login); 
    } 
    (Enthrallment.access$200(Enthrallment.this)).onlines.remove(new Enthrallment.UseridZoneid(this.userid, this.zoneid));
  }
}


/* Location:              D:\UserData\Desktop\authd\!\protocol\Enthrallment$DelayTask.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */