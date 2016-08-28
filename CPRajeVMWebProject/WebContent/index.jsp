<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Raje JSP page</title>
<style type="text/css">
h1 {color:blue;}
h2 {color:blue;}
p {color:purple;}
</style>

</head>
<h1 align="center"> <font size="4">Welcome to SJSU Manage VM Operations Portal</font></h1>
<h2 align="center"> <font size="3">Please Click on the Image Option</font></h2>
<body style="background-color:#E6E6FA">
<form action="Myservlet">

<p style="text-align:center;">
1.Power On &nbsp; <a href="Myservlet?option=poweron"><img alt="PowerOn.jpg" src="PowerOn.jpg" width="75" height="75"></a>
<br>
2.Power Off &nbsp; <a href="Myservlet?option=poweroff"><img alt="PowerOff.jpg" src="PowerOff.jpg" width="75" height="75"></a>
<br>
3.Suspend &nbsp;&nbsp;&nbsp; <a href="Myservlet?option=suspend"><img alt="Suspend.jpg" src="Suspend.jpg" width="75" height="75"></a>
<br>
4.VMDetails&nbsp; <a href="Myservlet?option=viewdetails"><img alt="viewdetails.jpg" src="viewdetails.jpg" width="75" height="75"></a>
<br>
5.Monitor &nbsp;&nbsp;&nbsp;&nbsp; <a href="Myservlet?option=monitor"><img alt="monitor.jpg" src="monitor.jpg" width="75" height="75"></a>
<br>
6.Clone &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="Myservlet?option=Clone"><img alt="clone.jpg" src="clone.jpg" width="75" height="75"></a>
<br>
7.Exit &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="Myservlet?option=exit"><img alt="Exit.jpg" src="Exit.jpg" width="75" height="75"></a>
<br>
</div>
 </p>
</form>
</body>
</html>