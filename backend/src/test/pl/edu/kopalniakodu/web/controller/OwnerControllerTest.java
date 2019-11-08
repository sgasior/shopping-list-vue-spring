package pl.edu.kopalniakodu.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
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
class OwnerControllerTest {

    private static final String OWNER_NAME_1 = "John";
    private static final String OWNER_NAME_2 = "Edd";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OwnerService ownerService;

    @Autowired
    ObjectMapper objectMapper;

    OwnerDto ownerDto_1;
    OwnerDto ownerDto_2;

    @BeforeEach
    public void init() {

        ownerDto_1 = OwnerDto
                .builder()
                .id(UUID.randomUUID())
                .name(OWNER_NAME_1)
                .build();

        ownerDto_2 = OwnerDto
                .builder()
                .id(UUID.randomUUID())
                .name(OWNER_NAME_2)
                .build();

    }

    @Test
    public void findOneShoouldReturnOwner() throws Exception {

        Mockito.when(ownerService.findDtoById(anyString()))
                .thenReturn(Optional.of(ownerDto_1));

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

        List<OwnerDto> ownerDtoList = Arrays.asList(ownerDto_1,ownerDto_2);
        Mockito.when(ownerService.findAllOwners()).thenReturn(ownerDtoList);

        mockMvc.perform(get("/api/v1/owner")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("v1/{method-name}"));
    }
}