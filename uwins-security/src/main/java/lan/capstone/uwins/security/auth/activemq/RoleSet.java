/*    */ package lan.capstone.uwins.security.auth.activemq;
/*    */ 
/*    */ import java.security.Principal;
/*    */ import java.util.Date;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
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
/*    */ class RoleSet
/*    */ {
/*    */   private Set<String> roles;
/*    */   public Date queried_time;
/*    */   
/*    */   public RoleSet()
/*    */   {
/* 35 */     this.roles = new HashSet();
/*    */   }
/*    */   
/*    */   void addRole(String role) {
/* 39 */     this.roles.add(role);
/*    */   }
/*    */   
/*    */   void clear() {
/* 43 */     this.roles.clear();
/*    */   }
/*    */   
/*    */   public boolean setHasMatchingRole(Set<Principal> other_roles) {
/*    */     try {
/* 48 */       for (localIterator1 = other_roles.iterator(); localIterator1.hasNext();) { p = (Principal)localIterator1.next();
/* 49 */         boolean inner_foundit = false;
/* 50 */         for (String r : this.roles)
/* 51 */           if (r.equalsIgnoreCase(p.getName()))
/* 52 */             throw new FoundItException(null);
/*    */       }
/*    */     } catch (FoundItException e) {
/*    */       Iterator localIterator1;
/*    */       Principal p;
/* 57 */       return true;
/*    */     }
/*    */     
/* 60 */     return false;
/*    */   }
/*    */   
/*    */   private class FoundItException
/*    */     extends Exception
/*    */   {
/*    */     private FoundItException() {}
/*    */   }
/*    */ }


/* Location:              C:\Users\mike\Documents\recover project\uwins-security-1.0-SNAPSHOT.jar!\lan\capstone\uwins\security\auth\activemq\RoleSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */