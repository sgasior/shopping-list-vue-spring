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
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.kopalniakodu.domain.Owner;
import pl.edu.kopalniakodu.service.OwnerService;
import pl.edu.kopalniakodu.web.mapper.OwnerMapper;
import pl.edu.kopalniakodu.web.model.OwnerDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriPort = 8088)
@WebMvcTest(OwnerController.class)
@Log4j2
@ComponentScan(basePackages = "pl.edu.kopalniakodu.web.mapper")
class OwnerControllerTest {

    private static final String OWNER_NAME_1 = "John";
    private static final String OWNER_NAME_2 = "Edd";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OwnerService ownerService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OwnerMapper ownerMapper;

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
                .andExpect(jsonPath("$.name", is(ownerDto_1.getName())))
                .andDo(document("v1/{method-name}",
                        ownerPathParametersSnippet(),
                        ownerResponseFieldsSnippet()));
    }

    @Test
    public void findOneOwnerShouldReturnErrorIfNotFound() throws Exception {
        Mockito.when(ownerService.findDtoById(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/owner/{ownerId}", "-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is("Owner with this id: -1 does not exists.")))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("owner.notfound")))
                .andDo(document("v1/{method-name}",
                        ownerPathParametersSnippet(),
                        apiError()));
    }


    @Test
    public void findAllOwners() throws Exception {

        List<OwnerDto> ownerDtoList = Arrays.asList(ownerDto_1, ownerDto_2);
        Mockito.when(ownerService.findAllOwners()).thenReturn(ownerDtoList);

        mockMvc.perform(get("/api/v1/owner")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", is(ownerDto_1.getName())))
                .andExpect(jsonPath("$.[1].name", is(ownerDto_2.getName())))
                .andExpect(header().longValue("X-Owners-Total", 2L))
                .andDo(document("v1/{method-name}", ownerPageHeadersSnippet(), ownerCollectionResponseFieldsSnippet()));
    }

    @Test
    public void addOwnerShouldReturnOwnerDtoInBodyResponse() throws Exception {

        Mockito.doNothing().when(ownerService).add(any(OwnerDto.class));
        String ownerDtoJson = objectMapper.writeValueAsString(ownerDto_1);
        mockMvc.perform(post("/api/v1/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ownerDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.name", is(ownerDto_1.getName())))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), ownerResponseFieldsSnippet()));
    }

    @Test
    public void addOwnerShouldReturnErrorWhenNameIsEmpty() throws Exception {
        Mockito.doNothing().when(ownerService).add(any(OwnerDto.class));
        String ownerDtoJson = objectMapper.writeValueAsString(OwnerDto.builder().name("").build());
        mockMvc.perform(post("/api/v1/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ownerDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("must not be blank")))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void addOwnerShouldReturnErrorWhenNameIsNull() throws Exception {
        Mockito.doNothing().when(ownerService).add(any(OwnerDto.class));
        String ownerDtoJson = objectMapper.writeValueAsString(OwnerDto.builder().name(null).build());
        mockMvc.perform(post("/api/v1/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ownerDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("must not be blank")))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void addOwnerShouldReturnErrorWhenNameIsTooShort() throws Exception {
        Mockito.doNothing().when(ownerService).add(any(OwnerDto.class));
        String ownerDtoJson = objectMapper.writeValueAsString(OwnerDto.builder().name("ab").build());
        mockMvc.perform(post("/api/v1/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ownerDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 20")))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void addOwnerShouldReturnErrorWhenNameIsToLong() throws Exception {
        Mockito.doNothing().when(ownerService).add(any(OwnerDto.class));
        String ownerDtoJson = objectMapper.writeValueAsString(OwnerDto.builder().name("TO_LOOOOOOOOOOOOOOONG_NAME").build());
        mockMvc.perform(post("/api/v1/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ownerDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 20")))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void updateOwnerShouldReturnOwnerDtoInBodyResponse() throws Exception {

        Mockito.when(ownerService.findById(any(String.class))).thenReturn(Optional.of(ownerMapper.ownerDtoToOwner(ownerDto_1)));
        Mockito.doNothing().when(ownerService).update(any(Owner.class), any(OwnerDto.class));
        String updatedOwner = objectMapper.writeValueAsString(ownerDto_1);
        mockMvc.perform(put("/api/v1/owner/{ownerId}", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedOwner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.name", is(ownerDto_1.getName())))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), ownerResponseFieldsSnippet(), ownerPathParametersSnippet()));
    }

    @Test
    public void updateOwnerShouldReturnErrorWhenNameIsEmpty() throws Exception {

        ownerDto_1.setName("");
        Mockito.when(ownerService.findById(any(String.class))).thenReturn(Optional.of(ownerMapper.ownerDtoToOwner(ownerDto_1)));
        Mockito.doNothing().when(ownerService).update(any(Owner.class), any(OwnerDto.class));
        String updatedOwner = objectMapper.writeValueAsString(ownerDto_1);
        mockMvc.perform(put("/api/v1/owner/{ownerId}", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedOwner))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("must not be blank")))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), apiError(), ownerPathParametersSnippet()));
    }

    @Test
    public void updateOwnerShouldReturnErrorWhenNameIsNull() throws Exception {

        ownerDto_1.setName(null);
        Mockito.when(ownerService.findById(any(String.class))).thenReturn(Optional.of(ownerMapper.ownerDtoToOwner(ownerDto_1)));
        Mockito.doNothing().when(ownerService).update(any(Owner.class), any(OwnerDto.class));
        String updatedOwner = objectMapper.writeValueAsString(ownerDto_1);
        mockMvc.perform(put("/api/v1/owner/{ownerId}", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedOwner))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("must not be blank")))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), apiError(), ownerPathParametersSnippet()));
    }

    @Test
    public void updateOwnerShouldReturnErrorWhenNameIsTooShort() throws Exception {

        ownerDto_1.setName("ab");
        Mockito.when(ownerService.findById(any(String.class))).thenReturn(Optional.of(ownerMapper.ownerDtoToOwner(ownerDto_1)));
        Mockito.doNothing().when(ownerService).update(any(Owner.class), any(OwnerDto.class));
        String updatedOwner = objectMapper.writeValueAsString(ownerDto_1);
        mockMvc.perform(put("/api/v1/owner/{ownerId}", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedOwner))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 20")))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), apiError(), ownerPathParametersSnippet()));
    }

    @Test
    public void updateOwnerShouldReturnErrorWhenNameIsToLong() throws Exception {

        ownerDto_1.setName("TO_LOOOOOOOOOOOOOOONG_NAME");
        Mockito.when(ownerService.findById(any(String.class))).thenReturn(Optional.of(ownerMapper.ownerDtoToOwner(ownerDto_1)));
        Mockito.doNothing().when(ownerService).update(any(Owner.class), any(OwnerDto.class));
        String updatedOwner = objectMapper.writeValueAsString(ownerDto_1);
        mockMvc.perform(put("/api/v1/owner/{ownerId}", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedOwner))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 20")))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), apiError(), ownerPathParametersSnippet()));
    }

    @Test
    public void updateOwnerShouldReturnErrorIfNotFound() throws Exception {

        Mockito.when(ownerService.findById(any(String.class))).thenReturn(Optional.empty());
        Mockito.doNothing().when(ownerService).update(any(Owner.class), any(OwnerDto.class));
        String updatedOwner = objectMapper.writeValueAsString(ownerDto_1);
        mockMvc.perform(put("/api/v1/owner/{ownerId}", "-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedOwner))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is("Owner with this id: -1 does not exists.")))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("owner.notfound")))
                .andDo(document("v1/{method-name}", ownerRequestFieldsSnippet(), apiError(), ownerPathParametersSnippet()));
    }

    @Test
    public void deleteOwner() throws Exception {
        Mockito.when(ownerService.findById(any(String.class))).thenReturn(Optional.of(ownerMapper.ownerDtoToOwner(ownerDto_1)));
        Mockito.doNothing().when(ownerService).delete(any(Owner.class));

        mockMvc.perform(delete("/api/v1/owner/{ownerId}", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("v1/{method-name}",
                        ownerPathParametersSnippet()));

    }

    @Test
    public void deleteOwnerShouldReturnErrorIfNotFound() throws Exception {
        Mockito.when(ownerService.findById(any(String.class))).thenReturn(Optional.empty());
        Mockito.doNothing().when(ownerService).delete(any(Owner.class));

        mockMvc.perform(delete("/api/v1/owner/{ownerId}", "-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is("Owner with this id: -1 does not exists.")))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("owner.notfound")))
                .andDo(document("v1/{method-name}",
                        ownerPathParametersSnippet(), apiError()));

    }

    private ResponseFieldsSnippet apiError() {
        return responseFields(
                fieldWithPath("[].codes").description("A list of technical codes describing the error"),
                fieldWithPath("[].message").description("A message describing the error").optional());
    }

    private RequestFieldsSnippet ownerRequestFieldsSnippet() {
        ConstraintDescriptions constraintDescriptions = new ConstraintDescriptions(OwnerDto.class);
        return requestFields(
                fieldWithPath("name").description("Name of the owner")
                        .attributes(key("constraints")
                                .value(constraintDescriptions
                                        .descriptionsForProperty("name"))));
    }

    private ResponseFieldsSnippet ownerResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("name").description("Name of the owner")
        );
    }

    private ResponseFieldsSnippet ownerCollectionResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("[].name").description("Name of the owner")
        );
    }

    private PathParametersSnippet ownerPathParametersSnippet() {
        return pathParameters(
                parameterWithName("ownerId").description("The unique identifier of the user")
        );
    }

    private ResponseHeadersSnippet ownerPageHeadersSnippet() {
        return responseHeaders(headerWithName("X-Owners-Total").description("The total amount of owners"));
    }

}