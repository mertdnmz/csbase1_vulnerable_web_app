# Vulnerable WEB Application - Course Project 1

General Notes:

* Starter template is used. Project can be run the same way TMC course assignments are run.

  Flaw Identifiers are : 
  * Flaw_Xss
  * Flaw_Csrf
  * Flaw_ConsoleAccess
  * Flaw_Delete_Signup
  * Flaw_Print_Others_Invitation

* The flaws introduced into the project correspond to the following categories from the OWASP list (https://www.owasp.org/index.php/Top_10_2013-Top_10) : 
 	
  * A1-Injection (Flaw_Xss, Flaw_Csrf)
  * A3-Cross-Site Scripting (Flaw_Xss)
  * A8-Cross-Site Request Forgery (Flaw_Csrf)
  * A2-Broken Authentication and Session Management (Flaw_ConsoleAccess)
  * A4-Insecure Direct Object References (Flaw_Print_Others_Invitation)
  * A5-Security Misconfiguration (Flaw_ConsoleAccess, Flaw_Csrf)
  * A7-Missing Function Level Access Control (Flaw_Delete_Signup)

* The following two users are defined :
  * user: alice, password: alicepw
  * user: bob, password: bobpw

* When two users have to be logged in in order to reproduce a case, it might help to use two different browsers. (e.g. Alice logs in from Firefox, Bob from Chrome)
