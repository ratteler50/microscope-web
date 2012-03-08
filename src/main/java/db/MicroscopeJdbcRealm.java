package db;

import model.DatabaseReads;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 * An extended version of the JDBC realm that stores the userID as well as the username in the
 * Principal
 *
 * @author dlorant
 */
public class MicroscopeJdbcRealm extends JdbcRealm {

  public MicroscopeJdbcRealm() {
    super();
    dataSource = DbConnect.getDataSource();
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(
      AuthenticationToken token) throws AuthenticationException {
    AuthenticationInfo info = super.doGetAuthenticationInfo(token);
    // The username
    String username = (String) info.getPrincipals().getPrimaryPrincipal();
    Integer userID = -1;
    try {
      userID = DatabaseReads.getUserID(username);
    } catch (DbException ignored) {

    }

    SimplePrincipalCollection princColl = new SimplePrincipalCollection();

    // Store the username
    princColl.add(username, getName());

    // Add the userID to the prinicpal
    princColl.add(userID, getName());

    return new SimpleAuthenticationInfo(princColl, info.getCredentials());
  }
}
