package pl.edu.kopalniakodu.web.controller;

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
import org.springframework.restdocs.headers.ResponseHeadersSnippet;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.kopalniakodu.exceptions.OwnerNotFoundException;
import pl.edu.kopalniakodu.service.TaskService;
import pl.edu.kopalniakodu.web.model.OwnerDto;
import pl.edu.kopalniakodu.web.model.TaskDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriPort = 8088)
@WebMvcTest(TaskController.class)
@Log4j2
@ComponentScan(basePackages = "pl.edu.kopalniakodu.web.mapper")
class TaskControllerTest {


    private static final String OWNER_NAME_1 = "John";
    private static final String OWNER_NAME_2 = "Edd";
    private static final Long TASK_ID_1 = 1L;
    private static final Long TASK_ID_2 = 2L;
    private static final String TASK_TITLE_1 = "Zakupy dla Szymka";
    private static final String TASK_TITLE_2 = "Zakupy dla Anetki";
    private static LocalDateTime TASK_CREATION_DATE_1 = LocalDateTime.now();
    private static LocalDateTime TASK_CREATION_DATE_2 = LocalDateTime.now();
    private static final boolean TASK_IS_DONE_1 = false;
    private static final boolean TASK_IS_DONE_2 = true;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm");

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaskService taskService;

    OwnerDto ownerDto_1;

    TaskDto taskDto_1;
    TaskDto taskDto_2;

    @BeforeEach
    public void init() {

        LocalDateTime localDateTime = LocalDateTime.now();
        log.info("lol " + localDateTime);
        ownerDto_1 = OwnerDto
                .builder()
                .id(UUID.randomUUID())
                .name(OWNER_NAME_1)
                .build();

        taskDto_1 = TaskDto
                .builder()
                .taskTitle(TASK_TITLE_1)
                .createdDate(TASK_CREATION_DATE_1)
                .isDone(TASK_IS_DONE_1)
                .taskNumber(1)
                .build();

        taskDto_2 = TaskDto
                .builder()
                .taskTitle(TASK_TITLE_2)
                .createdDate(TASK_CREATION_DATE_2)
                .isDone(TASK_IS_DONE_2)
                .taskNumber(1)
                .build();

    }

    @Test
    public void findAllTasksOfTheOwner() throws Exception {

        List<TaskDto> taskDtoList = Arrays.asList(taskDto_1, taskDto_2);
        Mockito.when(taskService.findAllTasks(any(String.class))).thenReturn(taskDtoList);

        mockMvc.perform(get("/api/v1/owner/{ownerId}/task", ownerDto_1.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.taskDtoList[0].taskTitle", is(taskDto_1.getTaskTitle())))
                .andExpect(jsonPath("$._embedded.taskDtoList[0].createdDate", is(taskDto_1.getCreatedDate().format(FORMATTER))))
                .andExpect(jsonPath("$._embedded.taskDtoList[0].isDone", is(taskDto_1.getIsDone())))
                .andExpect(jsonPath("$._embedded.taskDtoList[0].taskNumber", is(taskDto_1.getTaskNumber())))
                .andExpect(jsonPath("$._embedded.taskDtoList[1].taskTitle", is(taskDto_2.getTaskTitle())))
                .andExpect(jsonPath("$._embedded.taskDtoList[1].createdDate", is(taskDto_2.getCreatedDate().format(FORMATTER))))
                .andExpect(jsonPath("$._embedded.taskDtoList[1].isDone", is(taskDto_2.getIsDone())))
                .andExpect(jsonPath("$._embedded.taskDtoList[1].taskNumber", is(taskDto_2.getTaskNumber())))
                .andExpect(header().longValue("X-Tasks-Total", taskDtoList.size()))
                .andDo(document("v1/task/{method-name}",
                        taskPageHeadersSnippet(),
                        taskCollectionResponseFieldsSnippet(),
                        taskCollectionLinksSnippet(),
                        ownerPathParametersSnippet()
                ));
    }

    @Test
    public void findAllTasksShouldReturnErrorIfOwnerIsNotFound() throws Exception {

        Mockito.when(taskService.findAllTasks(any(String.class))).thenThrow(new OwnerNotFoundException("-1"));

        mockMvc.perform(get("/api/v1/owner/{ownerId}/task", "-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is("Owner with this id: -1 does not exists.")))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("owner.notfound")))
                .andDo(document("v1/task/{method-name}",
                        ownerPathParametersSnippet(),
                        apiError()));
    }

    private LinksSnippet taskCollectionLinksSnippet() {
        return links(
                halLinks(),
                linkWithRel("self").description("Self <<Resource>>").ignored()
        );
    }

    private ResponseFieldsSnippet taskCollectionResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("_embedded.taskDtoList[].taskTitle").description("Title of the task"),
                fieldWithPath("_embedded.taskDtoList[].createdDate").description("Creation date of the task"),
                fieldWithPath("_embedded.taskDtoList[].isDone").description("Information about that if taks is done or not"),
                fieldWithPath("_embedded.taskDtoList[].taskNumber").description("Number of owner's task"),
                subsectionWithPath("_embedded.taskDtoList[]._links").description("Task resource <<Resource>>").ignored(),
                subsectionWithPath("_links").description("Links <<Resource>>").ignored()
        );

    }

    private ResponseHeadersSnippet taskPageHeadersSnippet() {
        return responseHeaders(headerWithName("X-Tasks-Total").description("The total amount of tasks"));

    }

    private PathParametersSnippet ownerPathParametersSnippet() {
        return pathParameters(
                parameterWithName("ownerId").description("The unique identifier of the owner")
        );
    }

    private ResponseFieldsSnippet apiError() {
        return responseFields(
                fieldWithPath("[].codes").description("A list of technical codes describing the error"),
                fieldWithPath("[].message").description("A message describing the error").optional());
    }

}