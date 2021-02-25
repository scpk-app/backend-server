package dev.scpk.scpk.exceptions.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ObjectNotHashableException extends Exception{
    private Class<?> aClass;
}
