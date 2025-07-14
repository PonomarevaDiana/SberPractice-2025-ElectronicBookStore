package com.example.demo.controller;

import com.example.demo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {
    private final BookService bookService;

   @GetMapping("/add-book")
    public String showAddBookForm(Model model, @AuthenticationPrincipal User currentUser) throws IOException {

       Resource[] resources = new PathMatchingResourcePatternResolver()
               .getResources("classpath:static/images/*.{jpg,jpeg,png,gif}");

       List<String> images = Arrays.stream(resources)
               .map(Resource::getFilename)
               .collect(Collectors.toList());
        model.addAttribute("book", new Book());
        model.addAttribute("languages", new String[]{"Русский", "Английский", "Французский", "Немецкий", "Испанский"});
        model.addAttribute("genres", new String[]{"Детектив", "Художественная литература", "Учебная литература", "Фантастика", "Фэнтези", "Роман"});
        model.addAttribute("images", images);
       model.addAttribute("currentUser", currentUser);
       model.addAttribute("isAuthenticated", true);
        return "add-book";
    }

    @PostMapping("/add-book")
    public String addBook(@ModelAttribute Book book) {
        bookService.saveBook(book);
        return "redirect:/";
    }
}
