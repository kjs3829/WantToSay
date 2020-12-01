<?php
	$con = mysqli_connect("localhost","jieun959","wldms95900","jieun959");

	$userID = $_POST["userID"];
	$userPassword = $_POST["userPassword"];

	$statement = mysqli_prepare($con, "SELECT * FROM USER WHERE userID = ? AND userPassword = ?");
	mysqli_stmt_bind_param($statement, "ss",$userID, $userPassword);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $userID, $userPassword);

	$response = array();
	
	if(mysqli_stmt_fetch($statement)){
		$response["success"] = true;
		$response["userID"] = $userID;
		$response["userPassword"] = $userPassword;
	} else{
		$response["success"] = false;
	}

	echo json_encode($response);
?>