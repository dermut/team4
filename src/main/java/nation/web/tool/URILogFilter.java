package nation.web.tool;
 
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
 
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
 
public class URILogFilter implements Filter {
  private String charSet = null;
 
  @Override
  public void init(FilterConfig config) throws ServletException {
    // /WEB-INF/web.xml������ <init-param>�±��� ��
    charSet = config.getInitParameter("charSet");
 
    System.out.println("������������������������");
    System.out.println(" URI Logger start...");
    System.out.println("������������������������");
  }
 
  // ��û�� ���� �ڵ� ����
  public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    DBOpen dbopen = null;
    DBClose dbclose = null;
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    Connection con = null;
    PreparedStatement pstmt = null;
    request.setCharacterEncoding(charSet); // �ѱ� ���ڼ� ����
    String uri = request.getRequestURI(); // ��û uri ���� �κ�
    boolean login = false;
 
    // ------------------------------------------------------------------------------
    // URL DBMS ���
    // ------------------------------------------------------------------------------
    String ip = request.getRemoteAddr();
    System.out.println("ip: " + ip);
 
    try {
      dbopen = new DBOpen();
      dbclose = new DBClose();
      con = dbopen.getConnection();
 
      StringBuffer sql = new StringBuffer();
      sql.append(" INSERT INTO uriLog(urilogno, id, ip, uri, uridate)");
      sql.append(" VALUES((SELECT NVL(MAX(urilogno), 0)+1 as no FROM uriLog),");
      sql.append(" ?, ?, ?, TO_CHAR(sysdate, 'YYYY-MM-DD HH:MI:SS'))");
 
      pstmt = con.prepareStatement(sql.toString());
      pstmt.setString(1, "guest"); // session���� �����ϼ���.
      pstmt.setString(2, ip);
      pstmt.setString(3, uri);
 
      pstmt.executeUpdate();
    } catch (Exception e) {
      System.out.println(e.toString());
    } finally {
      dbclose.close(con, pstmt);
    }
 
    // ------------------------------------------------------------------------------
    // ���� ���� URL üũ
    // ------------------------------------------------------------------------------
    ArrayList<String> guest_list = UriList.getGuest_list();
    for (int index = 0; index < guest_list.size(); index++) {
      if (guest_list.equals(uri)) {
        login = true;
      }
    }
    // ------------------------------------------------------------------------------
 
    // ------------------------------------------------------------------------------
    // session�� ��� ��
    // ------------------------------------------------------------------------------
    // ���ο� ������ ���������ʰ� ������ ���� ��ü�� ��ȯ
    /*
     * HttpSession session = request.getSession(false);
     * 
     * if (session != null) { if (session.getAttribute("s_id") != null &&
     * session.getAttribute("s_memlevel").equals("AA")) { login = true; } }
     * 
     * if (login) { chain.doFilter(request, response); // �ٸ� ���� ����, ��û JSP ���� }
     * else { // �մ��̸� �α��� �������� �̵� RequestDispatcher dispatcher =
     * request.getRequestDispatcher("/admin_login_form.jsp");
     * dispatcher.forward(request, response); }
     */
    // ------------------------------------------------------------------------------
 
    // �ٸ� ���� ����, ��û JSP ����
    // chain.doFilter(request, response);
  }
 
  public void destroy() {
 
  }
}