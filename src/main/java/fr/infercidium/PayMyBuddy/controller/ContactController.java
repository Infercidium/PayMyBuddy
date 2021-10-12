package fr.infercidium.PayMyBuddy.controller;

import fr.infercidium.PayMyBuddy.model.User;
import fr.infercidium.PayMyBuddy.service.UserI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/contact")
public class ContactController {

    @Autowired
    private UserI userS;

    @GetMapping
    public String contact(Model model, @RequestParam(defaultValue = "1") int page) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userS.getUser(currentPrincipalName);
        List<User> userList = user.getKnowUser().stream().sorted(Comparator.comparing(User::getUserName)).collect(Collectors.toList());
        PagedListHolder contactPage = new PagedListHolder(userList);
        contactPage.setPageSize(5);
        contactPage.setPage(page-1);
        List<Integer> pagecount = new ArrayList<>();
        for (int i = 0; i<contactPage.getPageCount(); i++) {
            pagecount.add(i+1);
        }
        model.addAttribute("connexion", contactPage.getPageList());
        model.addAttribute("pageCount", pagecount);
        model.addAttribute("previous", contactPage.getPage());
        model.addAttribute("currentPage", contactPage.getPage()+1);
        model.addAttribute("next", contactPage.getPage()+2);
        return "contact";
    }

    @GetMapping(value = "/remove{id}")
    public String removecontact(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userS.getUser(currentPrincipalName);
        User removed = userS.getUser(id);
        user.removeKnowUser(removed);
        userS.editUser(currentPrincipalName, user);
        return "redirect:/contact?remove";
    }

    @PostMapping(value = "/addCo")
    public String addcontact(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userS.getUser(currentPrincipalName);
        if (userS.getUser(email) == null) {
            return "redirect:/contact?addMiss";
        }
        User added = userS.getUser(email);
        if (user.getKnowUser().contains(added)) {
            return "redirect:/contact?addError";
        }
        user.addKnowUser(added);
        userS.editUser(currentPrincipalName, user);
        return "redirect:/contact?addCo";
    }
}
