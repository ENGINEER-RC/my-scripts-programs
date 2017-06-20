<!DOCTYPE html>
<html>
	<head>
	<title>Compile Your Java File</title>
	<style type="text/css">
		.console {
		  font-family:Courier;
		 color: #CCCCCC;
		  background: #000000;
		  border: 3px double #CCCCCC;
		  padding: 10px;
		  width:800px;
		  text-align:left;
		  margin:50px auto;
		}
		body{
			background:url('http://orig00.deviantart.net/4bbd/f/2010/317/3/1/circuit_board_1_by_rls0812-d32rsa2.jpg');
		}
	</style>
	</head>
   <body>
      <div class="console">
	  Upload Your Java File<br><br>
      <form action="compile.php" method="POST" enctype="multipart/form-data">
         <input type="file" name="javafile" /><br><br>
         Password: <input type="password" name="pass"/><br><br>
		 <input type="submit" /><br>
      </form></div>
      
   </body>
</html>