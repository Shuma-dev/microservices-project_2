package org.example.dto;


import io.swagger.v3.oas.annotations.media.Schema;

public class UserRequestDto {

    @Schema(description = "Имя пользователя", example = "Denis")
    private String name;

    @Schema(description = "Электронная почта", example = "denis@example.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "25")
    private Integer age;

    public UserRequestDto(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public UserRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Пользователь: " +
                "имя = '" + name + '\'' +
                ", email = '" + email + '\'' +
                ", возраст = " + age;
    }
}
