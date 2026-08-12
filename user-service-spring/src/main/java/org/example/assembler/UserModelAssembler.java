package org.example.assembler;

import org.example.controller.UserController;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, UserResponseDto> {

    @Override
    public UserResponseDto toModel(User user) {

        UserResponseDto dto = new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );

        dto.add(
                linkTo(methodOn(UserController.class)
                        .getUser(user.getId()))
                        .withSelfRel()
        );

        dto.add(
                linkTo(methodOn(UserController.class)
                        .getUsers())
                        .withRel("users")
        );

        dto.add(
                linkTo(methodOn(UserController.class)
                        .updateUser(user.getId(), null))
                        .withRel("update")
        );

        dto.add(
                linkTo(methodOn(UserController.class)
                        .deleteUser(user.getId()))
                        .withRel("delete")
        );

        return dto;
    }
}