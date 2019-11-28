package pl.edu.kopalniakodu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.edu.kopalniakodu.domain.Product;
import pl.edu.kopalniakodu.domain.Task;
import pl.edu.kopalniakodu.exceptions.ProductNotFoundException;
import pl.edu.kopalniakodu.repository.ProductRepository;
import pl.edu.kopalniakodu.web.mapper.ProductMapper;
import pl.edu.kopalniakodu.web.model.ProductDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final TaskService taskService;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public List<ProductDto> findAllProductsOfTask(String ownerId, int taskNumber) {

        List<Product> productsEntities = taskService.getTaskByOwnerIdAndTaskNumber(ownerId, taskNumber).getProducts();
        return productsEntities.stream()
                .map(productMapper::productToProductDto).collect(Collectors.toList());
    }

    public Product findProduct(String ownerId, int taskNumber, int productNumber) {
        Task taskEntity = taskService.getTaskByOwnerIdAndTaskNumber(ownerId, taskNumber);
        return findProduct(taskEntity, productNumber);
    }

    public ProductDto findProductDto(String ownerId, int taskNumber, int productNumber) {
        Product productEntity = findProduct(ownerId, taskNumber, productNumber);
        return productMapper.productToProductDto(productEntity);
    }

    private Product findProduct(Task task, int productNumber) {
        if (task.getProducts().size() < productNumber || productNumber <= 0) {
            throw new ProductNotFoundException(productNumber);
        }
        return task.getProducts().get(productNumber - 1);
    }

    public ProductDto add(ProductDto productDto, int taskNumber, String ownerId) {

        Task savedTask = taskService.saveProductInTask(productMapper.productDtoToProduct(productDto), taskNumber, ownerId);
        int indexOfLastProduct = savedTask.getProducts().size() - 1;
        return productMapper.productToProductDto(savedTask.getProducts().get(indexOfLastProduct));
    }

    public ProductDto update(String ownerId, int taskNumber, int productNumber, ProductDto updatedProduct) {

        Product productEntity = findProduct(ownerId, taskNumber, productNumber);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedProduct, productEntity);
        Product savedProduct = productRepository.save(productEntity);
        return productMapper.productToProductDto(savedProduct);
    }

    public void delete(String ownerId, int taskNumber, int productNumber) {
        Product productEntity = findProduct(ownerId, taskNumber, productNumber);
        productRepository.delete(productEntity);
    }
}
