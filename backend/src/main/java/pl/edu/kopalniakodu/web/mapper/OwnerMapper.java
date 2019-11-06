package pl.edu.kopalniakodu.web.mapper;

import org.mapstruct.Mapper;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.web.model.OwnerDto;

@Mapper
public interface OwnerMapper {

    OwnerDto ownerToOwnerDto(Owner owner);

    Owner ownerDtoToOwner(OwnerDto dto);
}
