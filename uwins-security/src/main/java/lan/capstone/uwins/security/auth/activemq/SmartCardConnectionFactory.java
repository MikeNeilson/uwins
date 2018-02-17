/*    */ package lan.capstone.uwins.security.auth.activemq;
/*    */ 
/*    */ import java.security.KeyStore;
/*    */ import javax.net.ssl.KeyManager;
/*    */ import javax.net.ssl.KeyManagerFactory;
/*    */ import org.apache.activemq.ActiveMQSslConnectionFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SmartCardConnectionFactory
/*    */   extends ActiveMQSslConnectionFactory
/*    */ {
/*    */   public SmartCardConnectionFactory(String url)
/*    */   {
/* 43 */     super(url);
/*    */   }
/*    */   
/*    */   protected KeyManager[] createKeyManager() throws Exception
/*    */   {
/* 48 */     if (!getKeyStoreType().equalsIgnoreCase("windows-my")) {
/* 49 */       return super.createKeyManager();
/*    */     }
/* 51 */     KeyStore ks = KeyStore.getInstance("windows-my");
/* 52 */     KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
/* 53 */     ks.load(null, null);
/* 54 */     kmf.init(ks, null);
/* 55 */     return kmf.getKeyManagers();
/*    */   }
/*    */ }


/* Location:              C:\Users\mike\Documents\recover project\uwins-security-1.0-SNAPSHOT.jar!\lan\capstone\uwins\security\auth\activemq\SmartCardConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */