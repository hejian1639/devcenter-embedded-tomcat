<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<html>
<head>
    <title>遍历文件目录</title>
    <style type="text/css">
        ul {
            list-style: none;
        }

        li {
            float: left;
            margin-left: 20px;
            margin-top: 20px;
        }

        body {
            text-align: center;
            font-size: 15px;
        }

        a {
            font-size: 22px;
        }

        a:link, a:visited {
            color: #06C;
            text-decoration: none;
        }

        a:hover {
            color: #f00;
        }
    </style>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/jquery.js"></script>
    <script type="text/javascript">
        $(function () {
            var name = $("#names").val();
            $("#data").html("");
            $("#data").append(name)
        });
    </script>
</head>
<body>

<%!StringBuffer result = new StringBuffer();%>

<%
    String url = request.getRequestURL().toString();
    if (url.endsWith("/")) {
        url = url.substring(0, url.length());
    }
    url = url.substring(url.lastIndexOf("/") + 1);

    String path = request.getRealPath(url);
    File dir = new File(path + "/");
    String[] fileNames = null;
    if (dir.exists()) {
        if (dir.isDirectory()) {
            fileNames = dir.list();
        } else {
            fileNames = dir.getParentFile().list();
        }
    }
    result = new StringBuffer();
    for (String str : fileNames) {
        if (str != null)
            result.append("<li><a href='" + str + "'>" + str + "</a></li>");
    }
%>
<div id="main">
    <h1>文件列表</h1>
    <input type="hidden" id="names" value="<%=result%>"/>
    <ul id="data"></ul>
</div>
</body>
</html>