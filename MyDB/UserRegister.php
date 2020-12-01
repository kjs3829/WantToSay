<?php
	$con = mysqli_connect("localhost", "jieun959", "wldms95900", "jieun959");
	$userName = $_POST["userName"];
	$userID = $_POST["userID"];
	$userPassword = $_POST["userPassword"];
	$statement = mysqli_prepare($con,"INSERT INTO USER VALUES (?, ?, ?)");
	mysqli_stmt_bind_param($statement, "sss", $userName, $userID, $userPassword);
	mysqli_stmt_execute($statement);

	$response = array();
	$response["success"] = true;

	echo json_encode($response);
?>