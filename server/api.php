<?php
set_error_handler("warning_handler", E_WARNING);

function salt_and_hash($password, $salt) {
  return md5($password . $salt);
}

function generate_salt() {
    return substr(str_shuffle("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"), 0, 10);
}


function login($conn) {
  $username = $_GET["username"];
  $password = $_GET["password"];

  pg_prepare(
    $conn,
    "get_salt",
    "SELECT salt
    FROM users
    WHERE
    username = $1");

  $result = pg_execute(
    $conn,
    "get_salt", 
    Array($username));

  $row = pg_fetch_row($result);
  $salt = $row[0];

  $salted_password = salt_and_hash($password, $salt);

  pg_prepare(
    $conn,
    "login",
    "SELECT id, currency, budget
    FROM users
    WHERE
    username = $1 AND password = $2 ");

  $result = pg_execute(
    $conn,
    "login", 
    Array($username, $salted_password));

  return $result;
}

function register($conn) {
  $username = $_GET["username"];
  $password = $_GET["password"];
  $currency = empty($_GET["currency"]) ? "SEK" : $_GET["currency"];
  $salt = generate_salt();
  $salted_password = salt_and_hash($password, $salt);

  pg_prepare(
    $conn,
    "register",
    "INSERT INTO users (username, password, salt, currency)
    VALUES ($1, $2, $3, $4)");

  $result = pg_execute(
    $conn,
    "register",
    Array($username, $salted_password, $salt, $currency));  

  if (!$result) {
    return false;
  }
  else {
    return true;
  }
}

function warning_handler($errno, $errstr) { 
  // A warning happened
  
}

$conn = pg_connect("host=psql-vt2013 dbname=pclasson user=pclasson password=esdd2lUhLc");
$func = $_GET["func"];

if ($func === "login") {
  $result = login($conn);
}
else if ($func === "register") {
  $result = register($conn);
}

echo json_encode($result);

pg_close($conn);
?>