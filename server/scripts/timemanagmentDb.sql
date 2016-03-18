DROP DATABASE IF EXISTS timemanagement;
CREATE DATABASE IF NOT EXISTS timemanagement;
DROP USER 'timemanagement'@'localhost';
CREATE USER 'timemanagement'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES on timemanagement.* to 'timemanagement'@'localhost';
FLUSH PRIVILEGES;