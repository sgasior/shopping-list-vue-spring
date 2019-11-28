package pl.edu.kopalniakodu.web.mapper;

import org.mapstruct.Mapper;
import pl.edu.kopalniakodu.domain.Product;
import pl.edu.kopalniakodu.web.mapper.CustomMappers.ProductNumberMapper;
import pl.edu.kopalniakodu.web.model.ProductDto;

@Mapper(uses = ProductNumberMapper.class)
public interface ProductMapper {

    ProductDto productToProductDto(Product product);

    Product productDtoToProduct(ProductDto dto);
}
