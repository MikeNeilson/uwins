/*     */ package lan.capstone.uwins.security.auth.activemq;
/*     */ 
/*     */ import java.security.Principal;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Set;
/*     */ import org.apache.activemq.broker.Broker;
/*     */ import org.apache.activemq.broker.BrokerFilter;
/*     */ import org.apache.activemq.broker.ConnectionContext;
/*     */ import org.apache.activemq.broker.ProducerBrokerExchange;
/*     */ import org.apache.activemq.broker.region.Subscription;
/*     */ import org.apache.activemq.command.ActiveMQDestination;
/*     */ import org.apache.activemq.command.ConsumerInfo;
/*     */ import org.apache.activemq.command.Message;
/*     */ import org.apache.activemq.command.ProducerInfo;
/*     */ import org.apache.activemq.jaas.UserPrincipal;
/*     */ import org.apache.activemq.security.SecurityAdminMBean;
/*     */ import org.apache.activemq.security.SecurityContext;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.postgresql.ds.PGConnectionPoolDataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UWINSAuthenticationBroker
/*     */   extends BrokerFilter
/*     */   implements SecurityAdminMBean
/*     */ {
/*  48 */   public static Logger LOG = Logger.getLogger(UWINSAuthentication.class);
/*     */   
/*     */   private HashMap<String, RoleSet> write_roles;
/*     */   
/*     */   private HashMap<String, RoleSet> read_roles;
/*     */   private HashMap<String, RoleSet> admin_roles;
/*  54 */   PGConnectionPoolDataSource ds = null;
/*     */   
/*     */   public UWINSAuthenticationBroker(Broker next) {
/*  57 */     super(next);
/*  58 */     this.write_roles = new HashMap();
/*  59 */     this.read_roles = new HashMap();
/*  60 */     this.admin_roles = new HashMap();
/*     */     
/*  62 */     this.ds = new PGConnectionPoolDataSource();
/*  63 */     this.ds.setDatabaseName("uwins");
/*  64 */     this.ds.setUser("activemq");
/*  65 */     this.ds.setServerName("localhost");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void send(ProducerBrokerExchange producerExchange, Message mesg)
/*     */     throws Exception
/*     */   {
/*  73 */     SecurityContext context = producerExchange.getConnectionContext().getSecurityContext();
/*     */     
/*  75 */     String dest = mesg.getDestination().getPhysicalName();
/*  76 */     if ((!context.isBrokerContext()) && (!dest.matches("ActiveMQ.Advisory.*")) && (!isServiceAccount(context.getPrincipals())) && (!"activemq".equalsIgnoreCase(context.getUserName()))) {
/*  77 */       LOG.info(" not broker not advisory, checking roles");
/*  78 */       Set<Principal> principles = context.getPrincipals();
/*  79 */       for (Principal p : principles) {
/*  80 */         LOG.info("user role: " + p.getName());
/*     */       }
/*     */       
/*  83 */       RoleSet allowed = load_write_roles(dest);
/*  84 */       if (!allowed.setHasMatchingRole(principles)) {
/*  85 */         String user = null;
/*  86 */         for (Principal p : principles) {
/*  87 */           if ((p instanceof UserPrincipal)) {
/*  88 */             user = p.getName();
/*     */           }
/*     */         }
/*  91 */         LOG.info("user " + user + " does not have any roles that allow access");
/*  92 */         throw new SecurityException("User " + user + " does not have any roles that allow access to " + dest);
/*     */       }
/*     */     } else {
/*  95 */       LOG.info("broker or advisory, not checking");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addProducer(ConnectionContext context, ProducerInfo info)
/*     */     throws Exception
/*     */   {
/* 103 */     LOG.info("addProd Found username " + context.getSecurityContext().getUserName());
/*     */     
/*     */ 
/*     */ 
/* 107 */     super.addProducer(context, info);
/*     */   }
/*     */   
/*     */   public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception
/*     */   {
/* 112 */     LOG.info("addConsumer " + info.getDestination());
/* 113 */     SecurityContext sc = context.getSecurityContext();
/* 114 */     LOG.info("Connection from " + sc.getUserName());
/* 115 */     String user = null;
/* 116 */     for (Principal p : sc.getPrincipals()) {
/* 117 */       if ((p instanceof UserPrincipal)) {
/* 118 */         user = p.getName();
/*     */       }
/*     */     }
/*     */     
/* 122 */     String dest = info.getDestination().getPhysicalName();
/* 123 */     if ((!sc.isBrokerContext()) && (!dest.matches("^ActiveMQ.Advisory.*")) && (!isServiceAccount(sc.getPrincipals())) && (!"activemq".equalsIgnoreCase(user))) {
/* 124 */       LOG.info(" not broker not advisory, checking roles");
/* 125 */       Set<Principal> principles = sc.getPrincipals();
/* 126 */       for (Principal p : principles) {
/* 127 */         LOG.info("user role: " + p.getName());
/*     */       }
/*     */       
/* 130 */       RoleSet allowed = load_read_roles(dest);
/* 131 */       if (!allowed.setHasMatchingRole(principles))
/*     */       {
/* 133 */         LOG.info("user " + user + " does not have any roles that allow access");
/* 134 */         throw new SecurityException("User " + user + " does not have any roles that allow access to " + dest);
/*     */       }
/*     */     } else {
/* 137 */       LOG.info("broker or advisory, not checking");
/*     */     }
/* 139 */     return super.addConsumer(context, info);
/*     */   }
/*     */   
/*     */   private RoleSet load_roles(String dest, String set, RoleSet allowed) {
/* 143 */     if (allowed == null) allowed = new RoleSet();
/* 144 */     allowed.clear();
/* 145 */     try { Connection conn = this.ds.getConnection();Throwable localThrowable3 = null;
/* 146 */       try { LOG.info("roleset stale for this destination, re-retrieving");
/* 147 */         PreparedStatement stmt = conn.prepareStatement("select * from get_destination_roles(?) where lower(role) like ?");
/* 148 */         stmt.setString(1, dest);
/* 149 */         stmt.setString(2, String.format("%%%s%%", new Object[] { set }));
/* 150 */         ResultSet rs = stmt.executeQuery();
/*     */         String role;
/* 152 */         while (rs.next()) {
/* 153 */           role = rs.getString("role");
/* 154 */           LOG.info("found allowed role: " + role);
/* 155 */           allowed.addRole(role);
/*     */         }
/* 157 */         this.write_roles.put("dest", allowed);
/* 158 */         allowed.queried_time = new Date();
/* 159 */         return allowed;
/*     */       }
/*     */       catch (Throwable localThrowable1)
/*     */       {
/* 145 */         localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 160 */         if (conn != null) if (localThrowable3 != null) try { conn.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { conn.close();
/*     */           }
/*     */       }
/* 163 */       return allowed;
/*     */     }
/*     */     catch (SQLException ex)
/*     */     {
/* 161 */       LOG.error("failure in destination role retrieval" + ex.getLocalizedMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private RoleSet load_write_roles(String dest)
/*     */   {
/* 168 */     RoleSet allowed = (RoleSet)this.write_roles.get(dest);
/*     */     
/* 170 */     if ((allowed == null) || (allowed.queried_time.getTime() - new Date().getTime() > 3600000L)) {
/* 171 */       allowed = load_roles(dest, "write", allowed);
/*     */     }
/*     */     
/* 174 */     return allowed;
/*     */   }
/*     */   
/*     */   private RoleSet load_read_roles(String dest) {
/* 178 */     RoleSet allowed = (RoleSet)this.read_roles.get(dest);
/*     */     
/* 180 */     if ((allowed == null) || (allowed.queried_time.getTime() - new Date().getTime() > 3600000L)) {
/* 181 */       allowed = load_roles(dest, "read", allowed);
/*     */     }
/*     */     
/* 184 */     return allowed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRole(String arg0) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeRole(String arg0) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addUserRole(String arg0, String arg1) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeUserRole(String arg0, String arg1) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addTopicRole(String arg0, String arg1, String arg2) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeTopicRole(String arg0, String arg1, String arg2) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addQueueRole(String arg0, String arg1, String arg2) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeQueueRole(String arg0, String arg1, String arg2) {}
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isServiceAccount(Set<Principal> principals)
/*     */   {
/* 230 */     for (Principal p : principals) {
/* 231 */       if ("service_acount".equalsIgnoreCase(p.getName())) {
/* 232 */         return true;
/*     */       }
/*     */     }
/* 235 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\mike\Documents\recover project\uwins-security-1.0-SNAPSHOT.jar!\lan\capstone\uwins\security\auth\activemq\UWINSAuthenticationBroker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */