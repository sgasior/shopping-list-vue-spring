package pl.edu.kopalniakodu.web.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.kopalniakodu.service.ProductService;
import pl.edu.kopalniakodu.web.model.ProductDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping("/api/v1/owner")
@RestController
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{ownerId}/task/{taskNumber}/product")
    public ResponseEntity<CollectionModel<ProductDto>> getProducts(
            @PathVariable("ownerId") String ownerId,
            @PathVariable("taskNumber") int taskNumber
    ) {

        List<ProductDto> products = productService.findAllProductsOfTask(ownerId, taskNumber);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Products-Total", Integer.toString(products.size()));

        addLinksToEachProductDto(products, taskNumber, ownerId);
        Link mainSelfLink = linkTo(ProductController.class).slash(ownerId).slash("task").slash(taskNumber).slash("product").withSelfRel();
        return new ResponseEntity<>(
                new CollectionModel<>(products, mainSelfLink),
                headers,
                HttpStatus.OK
        );
    }

    @GetMapping("/{ownerId}/task/{taskNumber}/product/{productNumber}")
    public ResponseEntity<?> getProduct(
            @PathVariable("ownerId") String ownerId,
            @PathVariable("taskNumber") int taskNumber,
            @PathVariable("productNumber") int productNumber
    ) {
        ProductDto productDto = productService.findProductDto(ownerId, taskNumber, productNumber);
        addBasicLinksToProductDto(productDto, taskNumber, ownerId);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @PostMapping("/{ownerId}/task/{taskNumber}/product")
    public ResponseEntity<?> addProduct(
            @PathVariable("ownerId") String ownerId,
            @PathVariable("taskNumber") int taskNumber,
            @RequestBody @Valid ProductDto productDto
    ) {
        productDto = productService.add(productDto, taskNumber, ownerId);
        addBasicLinksToProductDto(productDto, taskNumber, ownerId);
        return new ResponseEntity<>(productDto, HttpStatus.CREATED);
    }

    @PutMapping("/{ownerId}/task/{taskNumber}/product/{productNumber}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("ownerId") String ownerId,
            @PathVariable("taskNumber") int taskNumber,
            @PathVariable("productNumber") int productNumber,
            @RequestBody @Valid ProductDto updatedProduct
    ) {
        updatedProduct = productService.update(ownerId, taskNumber, productNumber, updatedProduct);
        addBasicLinksToProductDto(updatedProduct, taskNumber, ownerId);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{ownerId}/task/{taskNumber}/product/{productNumber}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable("ownerId") String ownerId,
            @PathVariable("taskNumber") int taskNumber,
            @PathVariable("productNumber") int productNumber
    ) {
        log.info("before delete");
        productService.delete(ownerId, taskNumber, productNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void addBasicLinksToProductDto(ProductDto productDto, int taskNumber, String ownerId) {
        addOtherLinksToOwnerDto(productDto, taskNumber, ownerId);
        addSelfLinkToProduct(productDto, taskNumber, ownerId);
    }

    private void addOtherLinksToOwnerDto(ProductDto productDto, int taskNumber, String ownerId) {
        productDto.add(linkTo(TaskController.class).slash(ownerId).slash("task").slash(taskNumber).slash("product").withRel("products"));
    }

    private void addSelfLinkToProduct(ProductDto productDto, int taskNumber, String ownerId) {
        productDto.add(linkTo(TaskController.class).slash(ownerId).slash("task").slash(taskNumber).slash("product").slash(productDto.getProductNumber()).withRel("self"));
    }

    private void addLinksToEachProductDto(List<ProductDto> products, int taskNumber, String ownerId) {
        products.stream().map(productDto -> {
            addSelfLinkToProduct(productDto, taskNumber, ownerId);
            return productDto.add(linkTo(TaskController.class).slash(ownerId).slash("task").slash(taskNumber).withRel("task"));
        }).collect(Collectors.toList());
    }


}
