package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sec.project.domain.Signup;
import sec.project.domain.Account;
import sec.project.repository.SignupRepository;
import sec.project.repository.AccountRepository;

@Controller
public class SignupController {
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }
    
    @RequestMapping(value = "/signups", method = RequestMethod.GET)
    public String list(Authentication authentication, Model model) {
        model.addAttribute("signups", accountRepository.findByUsername(authentication.getName()).getSignups());
        return "signups";
    }

    /**
     * 
     * @param authentication
     * @param id
     * @return 
     */
    @RequestMapping(value = "/signups/{id}", method = RequestMethod.GET)
    @ResponseBody //Use Thymeleaf template to prevent injection. Comment out. FIX_FLAW : Flaw_Xss
    public String viewInvitation(Authentication authentication, Model model, @PathVariable Long id) {
        
        Signup su = signupRepository.findOne(id);
        /*
        https://www.owasp.org/index.php/Top_10_2007-Insecure_Direct_Object_Reference
        FIX_FLAW : Flaw_Print_Others_Invitation
        if (!authorizeInvitationAccess(su, authentication)) {
            return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
        }
        */
        
        //Developer's note : I don't like templates! What can go wrong...
        String name = su.getAccount().getUsername();
        String eventName = su.getName();
        String eventAddress = su.getAddress();
        String html = "";
        html += "<!DOCTYPE HTML>" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">" +
            "    <head>" +
            "        <title>Invitation</title>" +
            "        <meta charset=\"UTF-8\" />" +
            "    </head>" +
            "    <body>" +
            "       <a href=\"/signups\">My Events</a>" +
            "       <h2>Invitation</h2>" + 
            "       <p>Dear " + name + ",</p>" + 
            "       <p>You are invited to the event : " + eventName + ".</p>" + 
            "       <p>The event takes place at " + eventAddress + ".</p>" + 
            "    </body></html>";
        return html;//Use Thymeleaf template to prevent injection. Comment out. FIX_FLAW : Flaw_Xss
        
        
        //Use Thymeleaf template to prevent injection. FIX_FLAW : Flaw_Xss
        /*
        model.addAttribute("signup", su);
        return "invitation";  
        */
    }
    
    /**
     * Delete an individual signup.
     * Developer's note : 
     * Once there was a 'delete' button somewhere in the UI for deleting a signup. 
     * The button is gone, but let's leave the code here as it might be useful later.
     * Users cannot access it anyways...
     * @param authentication
     * @param id
     * @return 
     */
    @RequestMapping(value = "/signups/{id}", method = RequestMethod.DELETE)
    public String delete(Authentication authentication, @PathVariable Long id) {
        Signup su = signupRepository.findOne(id);
//      if (authorizeInvitationAccess(su, authentication)) {//Function level access control. FIX_FLAW : Flaw_Delete_Signup
            signupRepository.delete(id);            
//      }

        return "redirect:/signups";
    }
    
    /**
     * Function level access control for invitations(to signed up events)
     * @param su
     * @param auth
     * @return 
     */
    private boolean authorizeInvitationAccess(Signup su, Authentication auth) {
        if (su == null || !su.getAccount().getUsername().equals(auth.getName())) {// non-existing invitation and unauthorized invitation lead to same application behaviour
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(Authentication authentication, @RequestParam String name, @RequestParam String address) {
        Account account = accountRepository.findByUsername(authentication.getName());
        Signup su = new Signup(name, address);
        su.setAccount(account);
        signupRepository.save(su);
        return "done";
    }

}
