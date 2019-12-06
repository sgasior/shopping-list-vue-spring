package pl.edu.kopalniakodu.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.headers.ResponseHeadersSnippet;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.kopalniakodu.exceptions.OwnerNotFoundException;
import pl.edu.kopalniakodu.exceptions.ProductNotFoundException;
import pl.edu.kopalniakodu.exceptions.TaskNotFoundException;
import pl.edu.kopalniakodu.service.ProductService;
import pl.edu.kopalniakodu.web.model.ProductDto;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriPort = 8088)
@WebMvcTest(ProductController.class)
@Log4j2
@ComponentScan(basePackages = {"pl.edu.kopalniakodu.web.mapper"})
class ProductControllerTest {

    private static final String MESSAGE_OWNER_NOT_FOUND_EXCEPTION = "Owner with this id: -1 does not exists.";
    private static final String CODE_OWNER_NOT_FOUND_EXCEPTION = "owner.notfound";
    private static final String MESSAGE_TASK_NOT_FOUND_EXCEPTION = "Task with this number: -1 does not exists.";
    private static final String CODE_TASK_NOT_FOUND_EXCEPTION = "task.notfound";
    private static final String MESSAGE_PRODUCT_NOT_FOUND_EXCEPTION = "Product with this number: -1 does not exists.";
    private static final String CODE_PRODUCT_NOT_FOUND_EXCEPTION = "product.notfound";
    private static final String NOT_EXISTING_OWNER_ID = "-1";
    private static final Integer NOT_EXISTING_TASK_ID = -1;
    private static final Integer NOT_EXISTING_PRODUCT_ID = -1;
    private static final String MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION = "must not be blank";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @Autowired
    ObjectMapper objectMapper;

    Snippets snippets = new Snippets();

    ProductDto productDto_1;
    ProductDto productDto_2;
    String ownerUUID;
    int taskNumber;


    @BeforeEach
    public void init() {
        productDto_1 = ProductDto
                .builder()
                .name("Milk")
                .productNumber(1)
                .isDone(false)
                .build();

        productDto_2 = ProductDto
                .builder()
                .name("Water")
                .productNumber(2)
                .isDone(false)
                .build();

        taskNumber = 1;
        ownerUUID = UUID.randomUUID().toString();
    }


    @Test
    public void findAllProductsOfTheTask() throws Exception {

        List<ProductDto> products = Arrays.asList(productDto_1, productDto_2);
        Mockito.when(productService.findAllProductsOfTask(anyString(), anyInt())).thenReturn(products);

        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}/product", ownerUUID, taskNumber)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productDtoList[0].name", is(productDto_1.getName())))
                .andExpect(jsonPath("$._embedded.productDtoList[0].productNumber", is(productDto_1.getProductNumber())))
                .andExpect(jsonPath("$._embedded.productDtoList[0].isDone", is(productDto_1.getIsDone())))
                .andExpect(jsonPath("$._embedded.productDtoList[1].name", is(productDto_2.getName())))
                .andExpect(jsonPath("$._embedded.productDtoList[1].productNumber", is(productDto_2.getProductNumber())))
                .andExpect(jsonPath("$._embedded.productDtoList[1].isDone", is(productDto_2.getIsDone())))
                .andExpect(header().longValue("X-Products-Total", products.size()))
                .andDo(document("v1/product/{method-name}",
                        taskPageHeadersSnippet(),
                        productCollectionResponseFieldsSnippet(),
                        productCollectionLinksSnippet(),
                        productCollectionPathParametersSnippet()
                ));
    }

    @Test
    public void findAllProductsShouldReturnErrorIfOwnerIsNotFound() throws Exception {

        Mockito.when(productService.findAllProductsOfTask(anyString(), anyInt())).thenThrow(new OwnerNotFoundException("-1"));

        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}/product", "-1", taskNumber)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productCollectionPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void findAllProductsShouldReturnErrorIfTaskIsNotFound() throws Exception {

        Mockito.when(productService.findAllProductsOfTask(anyString(), anyInt())).thenThrow(new TaskNotFoundException(NOT_EXISTING_TASK_ID));
        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}/product", ownerUUID, NOT_EXISTING_TASK_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_TASK_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_TASK_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productCollectionPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void findOneProductShouldReturnProductDto() throws Exception {

        Mockito.when(productService.findProductDto(anyString(), anyInt(), anyInt())).thenReturn(productDto_1);
        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, productDto_1.getProductNumber())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(productDto_1.getName())))
                .andExpect(jsonPath("productNumber", is(productDto_1.getProductNumber())))
                .andExpect(jsonPath("isDone", is(productDto_1.getIsDone())))
                .andDo(document("v1/product/{method-name}",
                        productResponseFieldsSnippet(),
                        productPathParametersSnippet()
                ));
    }

    @Test
    public void findOneProductShouldReturnErrorIfOwnerNotFound() throws Exception {

        Mockito.when(productService.findProductDto(anyString(), anyInt(), anyInt())).thenThrow(new OwnerNotFoundException(NOT_EXISTING_OWNER_ID));
        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", NOT_EXISTING_OWNER_ID, taskNumber, productDto_1.getProductNumber())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void findOneProductShouldReturnErrorIfTaskNotFound() throws Exception {

        Mockito.when(productService.findProductDto(anyString(), anyInt(), anyInt())).thenThrow(new TaskNotFoundException(NOT_EXISTING_TASK_ID));
        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, NOT_EXISTING_TASK_ID, productDto_1.getProductNumber())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_TASK_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_TASK_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void findOneProductShouldReturnErrorIfProductNotFound() throws Exception {

        Mockito.when(productService.findProductDto(anyString(), anyInt(), anyInt())).thenThrow(new ProductNotFoundException(NOT_EXISTING_PRODUCT_ID));
        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, NOT_EXISTING_PRODUCT_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_PRODUCT_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_PRODUCT_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }


    @Test
    public void addProductShouldReturnProductDto() throws Exception {

        ProductDto newProduct = ProductDto
                .builder()
                .name(productDto_1.getName())
                .isDone(productDto_1.getIsDone())
                .build();

        Mockito.when(productService.add(any(ProductDto.class), anyInt(), anyString()))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(newProduct);
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task/{taskNumber}/product", ownerUUID, taskNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name", is(productDto_1.getName())))
                .andExpect(jsonPath("productNumber", is(productDto_1.getProductNumber())))
                .andExpect(jsonPath("isDone", is(productDto_1.getIsDone())))
                .andDo(document("v1/product/{method-name}",
                        productRequestFieldsSnippet(),
                        productResponseFieldsSnippet(),
                        productCollectionPathParametersSnippet()
                ));
    }

    @Test
    public void addProductShouldReturnErrorWhenProductNameIsEmpty() throws Exception {

        Mockito.when(productService.add(any(ProductDto.class), anyInt(), anyString()))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(ProductDto.builder().name("").build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task/{taskNumber}/product", ownerUUID, taskNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION)))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()));
    }

    @Test
    public void addProductShouldReturnErrorWhenProductNameIsNull() throws Exception {

        Mockito.when(productService.add(any(ProductDto.class), anyInt(), anyString()))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(ProductDto.builder().name(null).build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task/{taskNumber}/product", ownerUUID, taskNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION)))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()));
    }

    @Test
    public void addProductShouldReturnErrorWhenProductNameIsTooShort() throws Exception {

        Mockito.when(productService.add(any(ProductDto.class), anyInt(), anyString()))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(ProductDto.builder().name("aa").build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task/{taskNumber}/product", ownerUUID, taskNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 50")))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()));
    }

    @Test
    public void addProductShouldReturnErrorWhenProductNameIsTooLong() throws Exception {

        Mockito.when(productService.add(any(ProductDto.class), anyInt(), anyString()))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(ProductDto.builder().name("TOLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOONG").build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task/{taskNumber}/product", ownerUUID, taskNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 50")))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()));
    }

    @Test
    public void addProductShouldReturnErrorWhenOwnerIsNotFound() throws Exception {

        ProductDto newProduct = ProductDto
                .builder()
                .name(productDto_1.getName())
                .isDone(productDto_1.getIsDone())
                .build();

        String productDtoJson = objectMapper.writeValueAsString(newProduct);
        Mockito.when(productService.add(any(ProductDto.class), anyInt(), anyString()))
                .thenThrow(new OwnerNotFoundException(NOT_EXISTING_OWNER_ID));

        mockMvc.perform(post("/api/v1/owner/{ownerId}/task/{taskNumber}/product", NOT_EXISTING_OWNER_ID, taskNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()));
    }

    @Test
    public void addProductShouldReturnErrorWhenTaskIsNotFound() throws Exception {

        ProductDto newProduct = ProductDto
                .builder()
                .name(productDto_1.getName())
                .isDone(productDto_1.getIsDone())
                .build();

        String productDtoJson = objectMapper.writeValueAsString(newProduct);
        Mockito.when(productService.add(any(ProductDto.class), anyInt(), anyString()))
                .thenThrow(new TaskNotFoundException(NOT_EXISTING_TASK_ID));

        mockMvc.perform(post("/api/v1/owner/{ownerId}/task/{taskNumber}/product", ownerUUID, NOT_EXISTING_TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_TASK_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_TASK_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()));
    }


    @Test
    public void updateProductShouldReturnProductDto() throws Exception {

        ProductDto updatedProductDto = ProductDto
                .builder()
                .name(productDto_1.getName())
                .isDone(productDto_1.getIsDone())
                .build();

        Mockito.when(productService.update(anyString(), anyInt(), anyInt(), any(ProductDto.class)))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(updatedProductDto);
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, productDto_1.getProductNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(productDto_1.getName())))
                .andExpect(jsonPath("productNumber", is(productDto_1.getProductNumber())))
                .andExpect(jsonPath("isDone", is(productDto_1.getIsDone())))
                .andDo(document("v1/product/{method-name}",
                        productRequestFieldsSnippet(),
                        productResponseFieldsSnippet(),
                        productPathParametersSnippet()
                ));
    }

    @Test
    public void updateProductShouldReturnErrorWhenProductNameIsEmpty() throws Exception {

        Mockito.when(productService.update(anyString(), anyInt(), anyInt(), any(ProductDto.class)))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(ProductDto.builder().name("").build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, productDto_1.getProductNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void updateProductShouldReturnErrorWhenProductNameIsNull() throws Exception {

        Mockito.when(productService.update(anyString(), anyInt(), anyInt(), any(ProductDto.class)))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(ProductDto.builder().name(null).build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, productDto_1.getProductNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void updateProductShouldReturnErrorWhenProductNameIsTooShort() throws Exception {


        Mockito.when(productService.update(anyString(), anyInt(), anyInt(), any(ProductDto.class)))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(ProductDto.builder().name("ab").build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, productDto_1.getProductNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 50")))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void updateProductShouldReturnErrorWhenProductNameIsTooLong() throws Exception {


        Mockito.when(productService.update(anyString(), anyInt(), anyInt(), any(ProductDto.class)))
                .thenReturn(productDto_1);
        String productDtoJson = objectMapper.writeValueAsString(ProductDto.builder().name("").build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, productDto_1.getProductNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 50")))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void updateProductShouldReturnErrorWhenTaskIsNotFound() throws Exception {

        ProductDto updatedProductDto = ProductDto
                .builder()
                .name(productDto_1.getName())
                .isDone(productDto_1.getIsDone())
                .build();

        Mockito.when(productService.update(anyString(), anyInt(), anyInt(), any(ProductDto.class)))
                .thenThrow(new TaskNotFoundException(NOT_EXISTING_TASK_ID));
        String productDtoJson = objectMapper.writeValueAsString(updatedProductDto);

        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, NOT_EXISTING_TASK_ID, productDto_1.getProductNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_TASK_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_TASK_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()));
    }

    @Test
    public void updateProductShouldReturnErrorWhenOwnerIsNotFound() throws Exception {

        ProductDto updatedProductDto = ProductDto
                .builder()
                .name(productDto_1.getName())
                .isDone(productDto_1.getIsDone())
                .build();

        Mockito.when(productService.update(anyString(), anyInt(), anyInt(), any(ProductDto.class)))
                .thenThrow(new OwnerNotFoundException(NOT_EXISTING_OWNER_ID));
        String productDtoJson = objectMapper.writeValueAsString(updatedProductDto);

        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", NOT_EXISTING_OWNER_ID, taskNumber, productDto_1.getProductNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()));
    }

    @Test
    public void updateProductShouldReturnErrorWhenProductIsNotFound() throws Exception {

        ProductDto updatedProductDto = ProductDto
                .builder()
                .name(productDto_1.getName())
                .isDone(productDto_1.getIsDone())
                .build();

        Mockito.when(productService.update(anyString(), anyInt(), anyInt(), any(ProductDto.class)))
                .thenThrow(new ProductNotFoundException(NOT_EXISTING_PRODUCT_ID));
        String productDtoJson = objectMapper.writeValueAsString(updatedProductDto);

        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, NOT_EXISTING_PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_PRODUCT_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_PRODUCT_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}", productRequestFieldsSnippet(), snippets.apiError()));
    }


    @Test
    public void deleteProduct() throws Exception {
        Mockito.doNothing().when(productService).delete(anyString(), anyInt(), anyInt());
        mockMvc.perform(delete("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, productDto_1.getProductNumber())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet()));
    }

    @Test
    public void deleteProductShouldReturnErrorIfOwnerNotFound() throws Exception {

        doThrow(new OwnerNotFoundException(NOT_EXISTING_OWNER_ID)).when(productService).delete(anyString(), anyInt(), anyInt());
        mockMvc.perform(delete("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", NOT_EXISTING_OWNER_ID, taskNumber, productDto_1.getProductNumber())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void deleteProductShouldReturnErrorIfTaskNotFound() throws Exception {

        doThrow(new TaskNotFoundException(NOT_EXISTING_TASK_ID)).when(productService).delete(anyString(), anyInt(), anyInt());
        mockMvc.perform(delete("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, NOT_EXISTING_TASK_ID, productDto_1.getProductNumber())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_TASK_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_TASK_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }

    @Test
    public void deleteProductShouldReturnErrorIfProductNotFound() throws Exception {

        doThrow(new ProductNotFoundException(NOT_EXISTING_PRODUCT_ID)).when(productService).delete(anyString(), anyInt(), anyInt());
        mockMvc.perform(delete("/api/v1/owner/{ownerId}/task/{taskNumber}/product/{productNumber}", ownerUUID, taskNumber, NOT_EXISTING_PRODUCT_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_PRODUCT_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_PRODUCT_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/product/{method-name}",
                        productPathParametersSnippet(),
                        snippets.apiError()));
    }

    private RequestFieldsSnippet productRequestFieldsSnippet() {
        ConstraintDescriptions constraintDescriptions = new ConstraintDescriptions(ProductDto.class);
        return requestFields(
                fieldWithPath("name").description("Name of the product")
                        .attributes(key("constraints")
                                .value(constraintDescriptions
                                        .descriptionsForProperty("name"))),
                fieldWithPath("productNumber").description("The unique identifier of the product")
                        .attributes(key("constraints")
                                .value(constraintDescriptions
                                        .descriptionsForProperty("productNumber"))).ignored(),
                fieldWithPath("isDone").description("Condition to check if product is done")
                        .attributes(key("constraints")
                                .value(constraintDescriptions
                                        .descriptionsForProperty("isDone"))).optional(),
                fieldWithPath("links").description("Link <<Resources>>").ignored()
        );
    }


    private ResponseFieldsSnippet productCollectionResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("_embedded.productDtoList[].name").description("Title of the task"),
                fieldWithPath("_embedded.productDtoList[].productNumber").description("Creation date of the task"),
                fieldWithPath("_embedded.productDtoList[].isDone").description("Information about that if taks is done or not"),
                subsectionWithPath("_embedded.productDtoList[]._links").description("Product resource <<Resource>>").ignored(),
                subsectionWithPath("_links").description("Links <<Resource>>").ignored()
        );

    }

    private ResponseFieldsSnippet productResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("name").description("Name of the product"),
                fieldWithPath("productNumber").description("Numer of product in specific task"),
                fieldWithPath("isDone").description("Information about that if product is done or not"),
                subsectionWithPath("_links").description("Links <<Resource>>").ignored()
        );
    }

    private ResponseHeadersSnippet taskPageHeadersSnippet() {
        return responseHeaders(headerWithName("X-Products-Total").description("The total amount of products"));
    }

    private LinksSnippet productCollectionLinksSnippet() {
        return links(
                halLinks(),
                linkWithRel("self").description("Self <<Resource>>").ignored()
        );
    }

    private PathParametersSnippet productCollectionPathParametersSnippet() {
        return pathParameters(
                parameterWithName("ownerId").description("The unique identifier of the owner"),
                parameterWithName("taskNumber").description("Task number")
        );
    }

    private PathParametersSnippet productPathParametersSnippet() {
        return pathParameters(
                parameterWithName("ownerId").description("The unique identifier of the owner"),
                parameterWithName("taskNumber").description("The unique identifier of the taskNumber"),
                parameterWithName("productNumber").description("The unique identifier of the productNumber")
        );
    }
}