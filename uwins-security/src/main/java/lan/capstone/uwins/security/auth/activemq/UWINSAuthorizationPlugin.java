/*    */ package lan.capstone.uwins.security.auth.activemq;
/*    */ 
/*    */ import org.apache.activemq.broker.Broker;
/*    */ import org.apache.activemq.broker.BrokerPlugin;
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
/*    */ public class UWINSAuthorizationPlugin
/*    */   implements BrokerPlugin
/*    */ {
/*    */   public Broker installPlugin(Broker broker)
/*    */     throws Exception
/*    */   {
/* 29 */     return new UWINSAuthenticationBroker(broker);
/*    */   }
/*    */ }


/* Location:              C:\Users\mike\Documents\recover project\uwins-security-1.0-SNAPSHOT.jar!\lan\capstone\uwins\security\auth\activemq\UWINSAuthorizationPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */