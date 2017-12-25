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

# Issue and Fixes:

## Issue: Cross-Site Scripting - (Flaw_Xss)
Note: Stored XSS.
Steps to reproduce  
1. Login with user 'alice' (attacker)
2. Sign up to a new event using the form. Fill the form as follows and submit: 
Name: Ruisrock
Address: Ruissalo <script>alert("XSS");</script>
3. Login with user 'bob' (victim)
4. Sign up for the event Ruisrock by filling the form as follows and submit:
Name: Ruisrock
Address: Ruissalo
5. As Bob, click 'Home' and then click 'My Events'. Next, click on the event named 'Ruisrock'. Alice's script runs on Bob's browser as the page loads.

How to fix : 
Prevent interpretation of user inputs as code. We could sanitize the input ourself or rely on the framework. We chose the second option, and used a template instead of generating the html ourself. See code pieces commented with 'Flaw_Xss'.


## Issue: Cross-Site Request Forgery - (Flaw_Csrf)
1. Open OWASP ZAP and launch ZAP JxBrowser. Visit http://localhost:8080.
2. Login with user 'bob' 
3. Sign up to a new event using the form. Fill the form as follows and submit: 
Name: Ruisrock
Address: Ruissalo <script>alert("XSS");</script>
4. Switch to OWASP ZAP. Locate the POST request for the submission of the sign up form in the 'History' window. Double-click on it to view the request.
5. Request body reads: 'name=ruisrock&address=ruissalo'.
6. Notice that this form request is vulnerable to forgery. Alice could prepare a forged request and send it along with Bob's SESSIONID, for example by exploiting 'Flaw_Xss'.

How to fix : 
Fix the security misconfiguration that prevents CSRF tokens from being generated. If the above steps are followed for the fixed version of the project, the request body reads:
_csrf=54a06bee-c908-4218-99b6-955eef84ce08&name=ruisrock&address=ruissalo&_csrf=54a06bee-c908-4218-99b6-955eef84ce08
Now Alice would need to know the CSRF token to be able to forge the request. See code pieces commented with 'Flaw_Csrf'.


## Issue: Insecure Direct Object References - (Flaw_Print_Others_Invitation)
Steps to reproduce
1. Login with user 'bob' (victim)
2. Sign up to a new event using the form. Fill the form as follows and submit: 
Name: Ruisrock
Address: Ruissalo
3. Login with user 'alice' (attacker)
4. Sign up to a new event using the form. Fill the form as follows and submit:
Name: Secret Meeting
Address: Jekyll Island
5. As Alice, click 'Home' and then click 'My Events'. Next, click on the event named 'Secret Meeting'.
6. Alice notices that the URL for viewing her invitation ends with '/2', possibly an identifier for invitations.
7. Alice tries the URL 'http://localhost:8080/signups/1' and views Bob's invitation. [Now Alice has free pass to Ruisrock. World domination can wait!]

How to fix : 
Exposition of the invitation ID's could be prevented, for example via an indirect reference map. Instead, we chose to authorize access at the service layer. See code pieces commented with 'Flaw_Csrf'.


## Issue: Security Misconfiguration - (Flaw_ConsoleAccess)
Steps to reproduce  
1. Login with user 'alice'
2. Try URL 'http://localhost:8080/h2-console'. Alice is an ordinary user, but she is able to access administration functionality.

How to fix : 
Fix the security misconfiguration that allows user accounts to access h2-console. See code pieces commented with 'Flaw_ConsoleAccess'.

## Issue: Missing Function Level Access Control (Flaw_Delete_Signup)
Note: Deleting a signup is not intended functionality. User interface does not allow deletion.
Steps to reproduce  
1. Open OWASP ZAP and launch ZAP JxBrowser. Visit http://localhost:8080.
2. Login with user 'alice' 
3. Sign up to a new event using the form. Fill the form as follows and submit: 
Name: Secret Meeting
Address: Jekyll Island
4. As Alice, click 'Home' and then click 'My Events'. Next, click on the event named 'Secret Meeting'. Notice that the ID of the event is shown in the URL as '1'.
5. Switch to OWASP ZAP. Locate the POST request for the submission of the sign up form in the 'History' window. Double-click on it to view the request.
6. While on the request, right-click to open the context menu and choose 'Open/Resend with Request Editor...'.
7. In the header, replace 'POST http://localhost:8080/form HTTP/1.1' with 'DELETE http://localhost:8080/signups/1 HTTP/1.1' and click 'Send' button. Signup is deleted.

How to fix : 
If deleting a signup is not intended functionality, obvious fix is not have the functionality in the code. The pretense is that UI level functionality was removed, but service layer functionality remained active accidentally. In the fixed version, we did not remove the service layer functionality. Instead we added authorization at the service layer. The point we make is that, if the function level access control were included to begin with, then Alice would not be able to delete other users' sigups, even though she possibly could delete her own. So the consequence of the forgetfullness would probably be less dire. In the fixed version, CSRF tokens pose additional challenge for forging DELETE requests.
