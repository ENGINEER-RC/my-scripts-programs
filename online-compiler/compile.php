<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);


if (!isset($_POST['pass']) || $_POST['pass'] != "Tomrock")
	{
	header("HTTP/1.1 401 Unauthorized");
	echo "<center><h1>Wrong Password</h1><hr></center>";
	exit;
	}

function deleteDirectory($dir)
	{
	if (!file_exists($dir))
		{
		return true;
		}

	if (!is_dir($dir))
		{
		return unlink($dir);
		}

	foreach(scandir($dir) as $item)
		{
		if ($item == '.' || $item == '..')
			{
			continue;
			}

		if (!deleteDirectory($dir . DIRECTORY_SEPARATOR . $item))
			{
			return false;
			}
		}

	return rmdir($dir);
	}

//Makes a Random Folder name (Alpha)
$foldername = 'c' . substr(str_shuffle("0123456789abcdefghijklmnopqrstuvwxyz") , 0, 10);

//Select Compiler path
$compiler = '"C:\Program Files\Java\jdk1.8.0_101\bin';


if (isset($_FILES['javafile']))
	{
	$file_name = $_FILES['javafile']['name'];
	$file_size = $_FILES['javafile']['size'];
	$filedata = file_get_contents($_FILES['javafile']['tmp_name']);
	$jname = substr($_FILES['javafile']['name'],0,strrpos($_FILES['javafile']['name'],"."));
	$file_ext = substr($_FILES['javafile']['name'],strrpos($_FILES['javafile']['name'],"."));
	
	
	$expensions = array(
		".java"
	);
	
	if (in_array($file_ext, $expensions) === false || $file_size > 2097152)
		{
		header("location:upload.php");
		exit;
		}


		mkdir($foldername);
		file_put_contents($foldername . "/" .$_FILES['javafile']['name'], $filedata);
		
		
		//Executing The Compilation
		$errors = shell_exec('cd ' . $foldername . ' && ' . $compiler . '\javac.exe" ' . $_FILES['javafile']['name'].' 2>&1');
		
		

		//if Errors Display else compile and output file
		if (isset($errors))
			{
			$errora = str_replace("\n", "<br />", $errors);
			deleteDirectory($foldername);
			}
		  else
			{
			//Make a jar file out of it so that
			shell_exec("cd " . $foldername . " && ". $compiler. '\jar.exe" cvfe ' . $foldername .".jar " . $jname . "  *.class ");
			
			
			//Path to downloadable file
			$compiled_file=$foldername . "/" . $foldername . ".jar";
			
			
			//Parse and output the compiled file
			header('Content-Description: File Transfer');
			header('Content-Type: application/octet-stream');
			header('Content-Disposition: attachment; filename="' . basename($foldername . ".jar") . '"');
			header('Expires: 0');
			header('Cache-Control: must-revalidate');
			header('Pragma: public');
			header('Content-Length: ' . filesize($compiled_file));
			echo file_get_contents($compiled_file);
			
			
			deleteDirectory($foldername);
			exit;
			
			}

	}
	else
	{
	header("location:upload.php");
	exit;	
	}
  

?>

<html>
	<head>
	<title>Compliation Errors</title>
		<style>
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
		<script>
		function goBack() {
			window.history.back();
		}
		</script>
	</head>
	<body style="text-align:center;">
	<div class="console"><?php echo $errora;?></div>
	<button onclick="goBack()">Go Back</button>
	</body>
</html>
