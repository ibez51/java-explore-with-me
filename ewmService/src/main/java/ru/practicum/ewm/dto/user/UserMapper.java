package ru.practicum.ewm.dto.user;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.model.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequestDto newUserRequestDto);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.name", target = "name")
    UserShortDto toUserShortDto(User user);
}
