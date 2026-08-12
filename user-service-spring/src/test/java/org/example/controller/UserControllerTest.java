package org.example.controller;

import org.example.assembler.UserModelAssembler;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.exception.UserNotFoundException;
import org.example.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserModelAssembler assembler;

    /* =====================
     getUsers()
     =====================*/


    @Test
    @DisplayName("показать список пользователей")
    public void getUsers_ShouldReturnUsers() throws Exception {
        List<User> users = List.of(createUser());
        UserResponseDto dto = createDto();
        when(assembler.toModel(users.getFirst())).thenReturn(dto);
        when(userService.findAll()).thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Denis"))
                .andExpect(jsonPath("$[0].email").value("den@gmail.com"))
                .andExpect(jsonPath("$[0].age").value(26));
        verify(userService).findAll();
        verify(assembler).toModel(users.getFirst());
        verifyNoMoreInteractions(userService, assembler);
    }

    @Test
    @DisplayName("пустой список")
    public void getUsers_ShouldReturnEmptyList() throws Exception {
        when(userService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(userService).findAll();
        verifyNoMoreInteractions(userService);
    }

    /* =====================
     getUser()
     =====================*/

    @Test
    @DisplayName("показать пользователя")
    public void findById_ShouldReturnUser() throws Exception {
        User user = createUser();
        UserResponseDto dto = createDto();
        when(userService.findById(1L)).thenReturn(user);
        when(assembler.toModel(user)).thenReturn(dto);
        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Denis"))
                .andExpect(jsonPath("$.email").value("den@gmail.com"))
                .andExpect(jsonPath("$.age").value(26));
        verify(userService).findById(1L);
        verify(assembler).toModel(user);
        verifyNoMoreInteractions(userService, assembler);
    }

    @Test
    @DisplayName("вернуть 404 если пользователь не найден")
    public void findById_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(userService.findById(1L)).thenThrow(new UserNotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь не найден"))
                .andExpect(jsonPath("$.status").value(404));
        verify(userService).findById(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("должен вернуть 400 при неверном id")
    public void findById_ShouldReturn400_WhenIdIsInvalid() throws Exception {
        when(userService.findById(0L)).thenThrow(new IllegalArgumentException("Неверный Id"));
        mockMvc.perform(get("/users/{id}", 0L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Неверный Id"))
                .andExpect(jsonPath("$.status").value(400));
        verify(userService).findById(0L);
        verifyNoMoreInteractions(userService);
    }


    /* =====================
     createUser()
     =====================*/

    @Test
    @DisplayName("создать пользователя")
    public void createUser_ShouldSaveUser() throws Exception {
        UserRequestDto request = new UserRequestDto("Denis", "den@gmail.com", 26);
        User user = createUser();
        UserResponseDto dto = createDto();
        when(userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getAge()
        )).thenReturn(user);
        when(assembler.toModel(user)).thenReturn(dto);
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/users")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Denis"))
                .andExpect(jsonPath("$.email").value("den@gmail.com"))
                .andExpect(jsonPath("$.age").value(26));
        verify(userService).createUser(
                request.getName(),
                request.getEmail(),
                request.getAge()
        );
        verify(assembler).toModel(user);
        verifyNoMoreInteractions(userService, assembler);
    }

    @Test
    @DisplayName("должен вернуть 400, если имя пустое")
    public void createUser_ShouldReturn400_WhenNameIsBlank() throws Exception {
        UserRequestDto request = new UserRequestDto(" ", "den@gmail.com", 26);
        when(userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getAge()
        )).thenThrow(new IllegalArgumentException("Имя не может быть пустым"));
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/users")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Имя не может быть пустым"))
                .andExpect(jsonPath("$.status").value(400));
        verify(userService).createUser(
                request.getName(),
                request.getEmail(),
                request.getAge());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("должен вернуть 400, если email пустой")
    public void createUser_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        UserRequestDto request = new UserRequestDto("Denis", " ", 26);
        when(userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getAge()
        )).thenThrow(new IllegalArgumentException("Неверный email"));
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Неверный email"))
                .andExpect(jsonPath("$.status").value(400));
        verify(userService).createUser(
                request.getName(),
                request.getEmail(),
                request.getAge());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("должен вернуть 400, если email имеет неверный формат")
    void createUser_ShouldReturn400_WhenEmailHasWrongFormat() throws Exception {
        UserRequestDto request = new UserRequestDto("Denis", "den@gmail", 26);
        when(userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getAge()
        )).thenThrow(new IllegalArgumentException("Неверный email"));
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/users")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Неверный email"))
                .andExpect(jsonPath("$.status").value(400));
        verify(userService).createUser(
                request.getName(),
                request.getEmail(),
                request.getAge());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("должен вернуть 400, если возраст пустой")
    public void createUser_ShouldReturn400_WhenAgeIsInvalid() throws Exception {
        UserRequestDto request = new UserRequestDto("Denis", "den@gmail.com", null);
        when(userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getAge()
        )).thenThrow(new IllegalArgumentException("Неверный возраст"));
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Неверный возраст"))
                .andExpect(jsonPath("$.status").value(400));
        verify(userService).createUser(
                request.getName(),
                request.getEmail(),
                request.getAge());
        verifyNoMoreInteractions(userService);
    }

    /* =====================
     updateUser()
     =====================*/

    @Test
    @DisplayName("обновить пользователя")
    public void updateUser_ShouldUpdateUser() throws Exception {
        UserRequestDto request = new UserRequestDto("Denis", "den@gmail.com", 26);
        User user = createUser();
        UserResponseDto dto = createDto();
        when(userService.updateUser(
                1L,
                request.getName(),
                request.getEmail(),
                request.getAge()
        )).thenReturn(user);
        when(assembler.toModel(user)).thenReturn(dto);
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(put("/users/{id}", 1L)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Denis"))
                .andExpect(jsonPath("$.email").value("den@gmail.com"))
                .andExpect(jsonPath("$.age").value(26));
        verify(userService).updateUser(
                1L,
                request.getName(),
                request.getEmail(),
                request.getAge()
        );
        verify(assembler).toModel(user);
        verifyNoMoreInteractions(userService, assembler);
    }

    @Test
    @DisplayName("должен вернуть 404, если пользователь не найден при обновлении")
    public void updateUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        UserRequestDto request = new UserRequestDto("Denis", "den@gmail.com", 26);
        when(userService.updateUser(1L,
                request.getName(),
                request.getEmail(),
                request.getAge()
        )).thenThrow(new UserNotFoundException("Пользователь не найден"));
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(put("/users/{id}", 1L)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь не найден"))
                .andExpect(jsonPath("$.status").value(404));
        verify(userService).updateUser(
                1L,
                request.getName(),
                request.getEmail(),
                request.getAge());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("должен вернуть 400 при ошибке валидации")
    public void updateUser_ShouldReturn400_WhenValidationFails() throws Exception {
        UserRequestDto request = new UserRequestDto(" ", "den@gmail.com", 26);
        when(userService.updateUser(
                1L,
                request.getName(),
                request.getEmail(),
                request.getAge()
        )).thenThrow(new IllegalArgumentException("Имя не может быть пустым"));
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(put("/users/{id}", 1L)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Имя не может быть пустым"))
                .andExpect(jsonPath("$.status").value(400));
        verify(userService).updateUser(
                1L,
                request.getName(),
                request.getEmail(),
                request.getAge());
        verifyNoMoreInteractions(userService);
    }

    /* =====================
     deleteUser()
     =====================*/

    @Test
    @DisplayName("удаление пользователя")
    public void deleteUser_ShouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(userService).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("должен вернуть 404, если пользователь не найден при удалении")
    public void deleteUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("Пользователь не найден"))
                .when(userService)
                .deleteUser(1L);
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь не найден"))
                .andExpect(jsonPath("$.status").value(404));
        verify(userService).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }

    private UserResponseDto createDto() {
        return new UserResponseDto(
                1L,
                "Denis",
                "den@gmail.com",
                26,
                null
        );
    }

    private User createUser() {
        return new User(
                "Denis",
                "den@gmail.com",
                26
        );
    }

}
