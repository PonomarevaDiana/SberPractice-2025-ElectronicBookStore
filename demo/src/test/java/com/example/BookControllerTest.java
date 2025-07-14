package com.example.demo;

import com.example.demo.controller.BookController;
import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public BookService bookService() {
            return Mockito.mock(BookService.class);
        }
    }

    @Test
    void getBooks_ShouldReturnBooks() throws Exception {
        // Arrange
        when(bookService.getAllBooks())
                .thenReturn(List.of(new Book(), new Book()));

        // Act & Assert
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.length()").value(2));
    }
}