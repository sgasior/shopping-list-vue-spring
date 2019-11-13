package pl.edu.kopalniakodu.web.mapper.CustomMappers;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class SetMapper {

    public <T> Set<T> mapSetCollection(Set<T> source) {
        if (source == null) {
            source = new LinkedHashSet<>();
        }
        Set<T> target = new LinkedHashSet<>(source);
        return target;
    }
}
