package pl.edu.kopalniakodu.web.mapper.CustomMappers;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDMapper {

    public UUID asUUID(String id) {
        if (id != null) {
            return UUID.fromString(id);
        } else {
            return null;
        }
    }

    public String asString(UUID id) {
        if (id != null) {
            return id.toString();
        } else {
            return null;
        }
    }

}
