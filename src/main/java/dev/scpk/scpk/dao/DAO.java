package dev.scpk.scpk.dao;

import lombok.Data;

@Data
public class DAO {
    private Long id;

    public int hashCode(){
        return id.hashCode();
    }
}
