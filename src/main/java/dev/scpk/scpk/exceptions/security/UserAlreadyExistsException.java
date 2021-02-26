package dev.scpk.scpk.exceptions.security;

import dev.scpk.scpk.dao.UserDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserAlreadyExistsException extends Exception{
}
