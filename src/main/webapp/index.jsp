<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
  <title>SocialSignin's ExFm OAuth Proxy</title>
  <link type="text/css" rel="stylesheet" href="<c:url value="/style.css"/>"/>

  <authz:authorize ifAllGranted="ROLE_USER">
    <script type='text/javascript'>
      function pictureDisplay(json) {
        for (var i = 0; i < json.photos.length; i++) {
          var photo = json.photos[i];
          document.write('<img src="photos/' + photo.id + '" alt="' + photo.name + '">');
        }
      }
    </script>
  </authz:authorize>
</head>
<body>

  <h1>SocialSignin's ExFm OAuth Proxy</h1>

  <div id="content">
    <h2>Home</h2>

    <p>This is a proxy to Ex.Fm's API, adding OAuth capabilities</p>

    <authz:authorize ifNotGranted="ROLE_USER">
      <h2>Login</h2>
      <form id="loginForm" name="loginForm" action="<c:url value="/login.do"/>" method="post">
        <p><label>ExFm Username: <input type='text' name='j_username' value=""></label></p>
        <p><label>ExFm Password: <input type='text' name='j_password' value=""></label></p>
        
        <p><input name="login" value="Login" type="submit"></p>
      </form>
    </authz:authorize>
    <authz:authorize ifAllGranted="ROLE_USER">
      <div style="text-align: center"><form action="<c:url value="/logout.do"/>"><input type="submit" value="Logout"></form></div>
      
    </authz:authorize>
  </div>

  <div id="footer"></div>


</body>
</html>
