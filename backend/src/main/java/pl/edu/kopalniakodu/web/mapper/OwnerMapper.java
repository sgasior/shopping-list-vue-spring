package pl.edu.kopalniakodu.web.mapper;

import org.mapstruct.Mapper;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.web.mapper.CustomMappers.SetMapper;
import pl.edu.kopalniakodu.web.model.OwnerDto;

@Mapper(uses = SetMapper.class)
public interface OwnerMapper {

    OwnerDto ownerToOwnerDto(Owner owner);

    Owner ownerDtoToOwner(OwnerDto dto);
}
