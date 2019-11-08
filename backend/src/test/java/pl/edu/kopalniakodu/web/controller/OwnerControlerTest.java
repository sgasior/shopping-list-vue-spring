package pl.edu.kopalniakodu.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.kopalniakodu.service.OwnerService;
import pl.edu.kopalniakodu.web.model.OwnerDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriPort = 8088)
@WebMvcTest(OwnerController.class)
class OwnerControlerTest {

    private static final String OWNER_NAME_1 = "John";
    private static final String OWNER_NAME_2 = "Edd";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OwnerService ownerService;

    @Autowired
    ObjectMapper objectMapper;



/*    @BeforeEach
    public void init() {
        task_1_dto = TaskDto.builder()
                .taskTitle("Zakupy dla Szymka")
                .bill(new Bill(BilanceType.INCOME, new BigDecimal("123")))
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .isDone(false)
                .products(new LinkedHashSet<Product>(Arrays.asList(new Product("Chleb"), new Product("Mleko"), new Product("Pomidory"))))
                .build();


        task1 = new Task("Zakupy dla Szymka",
                new Timestamp(System.currentTimeMillis())
                , false
        );
        task1.setBill(new Bill(BilanceType.INCOME, new BigDecimal("123")));
        task1.setProducts(new LinkedHashSet<Product>(Arrays.asList(
                new Product("Chleb"),
                new Product("Mleko"),
                new Product("Pomidory")
        )));
    }*/

    @Test
    public void findOneShoouldReturnOwner() throws Exception {

        Mockito.when(ownerService.findDtoById(anyString()))
                .thenReturn(Optional.of(OwnerDto
                        .builder()
                        .id(UUID.randomUUID())
                        .name(OWNER_NAME_1)
                        .build()));

        mockMvc.perform(get("/api/v1/owner/{ownerId}", UUID.randomUUID().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("v1/{method-name}",
                        pathParameters(
                                parameterWithName("ownerId").description("The unique identifier of the user")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The unique identifier of the user"),
                                fieldWithPath("name").description("Name of the owner")
                        )));
    }

    @Test
    public void findOneOwnerShouldReturnErrorIfNotFound() throws Exception {
        Mockito.when(ownerService.findDtoById(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/owner/{ownerId}", "-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("v1/{method-name}",
                        pathParameters(
                                parameterWithName("ownerId").description("The unique identifier of the owner")
                        ),
                        apiError()));
    }

    private ResponseFieldsSnippet apiError() {
        return responseFields(
                fieldWithPath("[].codes").description("A list of technical codes describing the error"),
                fieldWithPath("[].message").description("A message describing the error").optional());
    }

    @Test
    public void findAllOwners() throws Exception {

        List<OwnerDto> ownerDtoList = new ArrayList<>();
        ownerDtoList.add(OwnerDto
                .builder()
                .id(UUID.randomUUID())
                .name(OWNER_NAME_1)
                .build());

//        Arrays.asList(
//                OwnerDto
//                        .builder()
//                        .id(UUID.randomUUID())
//                        .name(OWNER_NAME_1)
//                        .build(),
//                OwnerDto
//                        .builder()
//                        .id(UUID.randomUUID())
//                        .name(OWNER_NAME_2)
//                        .build()
//        );

        Mockito.when(ownerService.findAllOwners()).thenReturn(ownerDtoList);

        mockMvc.perform(get("/api/v1/owner")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("v1/{method-name}"));
    }
}