package app.backendservice.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Controller
public class HomeController 
{
    @GetMapping("/")
    public String index(Model model)
    {           
        return "home";
    }

}
