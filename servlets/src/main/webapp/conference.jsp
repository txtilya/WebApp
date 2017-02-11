<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<?xml version="1.0" encoding="UTF-8"?>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>WebApp</title>
    <style type="text/css">

        table {
            width: 100%
        }

        #col1 {
            vertical-align: top;
            width: 15%;
        }

        #col2 {
            text-align: left;
            vertical-align: top;
            width: 65%;
        }

        #col3 {
            vertical-align: top;
            width: 20%;
        }

        input#message {
            margin: 0 auto;
            width: 88%
        }

        #content-container {
            width: 95%
        }

        #content {
            border: 1px solid #CCCCCC;
            border-right-color: #999999;
            border-bottom-color: #999999;
            overflow-y: scroll;
            height: 350px;
            padding: 5px;
            width: 100%;
        }

        #content p {
            padding: 0;
            margin: 0;
        }

        a:visited {
            color: blue;
        }

    </style>

    <script src="_scripts/Conference.js"></script>
    <script>
        addEventListener("DOMContentLoaded", () => new Conference());
    </script>
</head>

<body>

<h1 id="headerName">.</h1>
<div>
    <table>
        <tr>
            <td id="col1">
                <a href="info.html">Info</a><br>
                <a href="messages.html">Messages</a><br>
                <a href="friends.html">Friends</a><br><br>
                <a href="/logout"> Logout</a>
            </td>
            <td id="col2">
                <div>
                    <div id="content-container">
                        <div id="content"></div>
                    </div>
                    <form action="">
                        <input id="message" name="message" placeholder="type and press enter to chat" autocomplete="off" autofocus>
                        <input type="submit" value="Send">
                    </form>

                </div>
            </td>
            <td id="col3">
                <div>
                    <a id="c3showMoreMessages" href></a><br>
                    <a id="c3addFriend" href></a><br>
                </div>
            </td>
        </tr>
    </table>
</div>



<% String id = request.getParameter("id"); %>
<p><input id="conferenceId" type="hidden" name="conferenceId" value="<%=id%>"></p>
</body>
</html>