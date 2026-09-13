package com.tu.goodsbuy.controller.user.post;

import com.tu.goodsbuy.controller.param.LoginForm;
import com.tu.goodsbuy.model.dto.MemberUser;
import com.tu.goodsbuy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@SessionAttributes({"loginMember"})
@Slf4j
public class LoginController {


    private final UserService userService;

    @PostMapping("/login.do")
    public String doLogin(@Valid LoginForm loginForm, BindingResult br, RedirectAttributes rttr, Model model, jakarta.servlet.http.HttpServletRequest request) {

        if (br.hasErrors()) {
            rttr.addFlashAttribute("errors", br);
            return "redirect:/login";
        }


        MemberUser loginMember = userService.doLogin(loginForm.getUsername(), loginForm.getPassword());
        request.getSession();
        request.changeSessionId();
        model.addAttribute("loginMember", loginMember);

        return "redirect:/goodsbuy/list";
    }


    @PostMapping("/logout.do")
    public String doLogOut(SessionStatus sessionStatus, jakarta.servlet.http.HttpServletRequest request) {
        sessionStatus.setComplete();
        if (request.getSession(false) != null) request.getSession(false).invalidate();

        return "redirect:/goodsbuy/list";
    }


}
