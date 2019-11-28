package pl.edu.kopalniakodu.web.mapper.CustomMappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import pl.edu.kopalniakodu.domain.Product;
import pl.edu.kopalniakodu.web.model.ProductDto;

import java.util.List;

@Component
public class ProductNumberMapper {

    ////We have to use ProductDto.ProductDtoBuilder because of Lombok @Builder
    @AfterMapping
    public void calcTaskNumber(Product product, @MappingTarget ProductDto.ProductDtoBuilder productDtoBuilder) {

        List<Product> productList = product.getTask().getProducts();
        int indexOfProduct = productList.indexOf(product);
        productDtoBuilder.productNumber(++indexOfProduct);

    }
}
