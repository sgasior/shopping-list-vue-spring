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
import pl.edu.kopalniakodu.exceptions.TaskNotFoundException;
import pl.edu.kopalniakodu.service.TaskService;
import pl.edu.kopalniakodu.web.model.OwnerDto;
import pl.edu.kopalniakodu.web.model.TaskDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
@WebMvcTest(TaskController.class)
@Log4j2
@ComponentScan(basePackages = "pl.edu.kopalniakodu.web.mapper")
class TaskControllerTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm");
    private static final String MESSAGE_OWNER_NOT_FOUND_EXCEPTION = "Owner with this id: -1 does not exists.";
    private static final String CODE_OWNER_NOT_FOUND_EXCEPTION = "owner.notfound";
    private static final String MESSAGE_TASK_NOT_FOUND_EXCEPTION = "Task with this number: -1 does not exists.";
    private static final String CODE_TASK_NOT_FOUND_EXCEPTION = "task.notfound";
    private static final String NOT_EXISTING_OWNER_ID = "-1";
    private static final Integer NOT_EXISTING_TASK_ID = -1;
    private static final String MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION = "must not be blank";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaskService taskService;

    @Autowired
    ObjectMapper objectMapper;

    OwnerDto ownerDto_1;

    TaskDto taskDto_1;
    TaskDto taskDto_2;

    @BeforeEach
    public void init() {

        ownerDto_1 = OwnerDto
                .builder()
                .id(UUID.randomUUID())
                .name("John")
                .build();

        taskDto_1 = TaskDto
                .builder()
                .taskTitle("Zakupy dla Szymka")
                .createdDate(LocalDateTime.now())
                .isDone(false)
                .taskNumber(1)
                .hexColor("#000000")
                .build();

        taskDto_2 = TaskDto
                .builder()
                .taskTitle("Zakupy dla Anetki")
                .createdDate(LocalDateTime.now())
                .isDone(true)
                .taskNumber(2)
                .hexColor("#ffffff")
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
                .andExpect(jsonPath("$._embedded.taskDtoList[0].hexColor", is(taskDto_1.getHexColor())))
                .andExpect(jsonPath("$._embedded.taskDtoList[1].taskTitle", is(taskDto_2.getTaskTitle())))
                .andExpect(jsonPath("$._embedded.taskDtoList[1].createdDate", is(taskDto_2.getCreatedDate().format(FORMATTER))))
                .andExpect(jsonPath("$._embedded.taskDtoList[1].isDone", is(taskDto_2.getIsDone())))
                .andExpect(jsonPath("$._embedded.taskDtoList[1].taskNumber", is(taskDto_2.getTaskNumber())))
                .andExpect(jsonPath("$._embedded.taskDtoList[1].hexColor", is(taskDto_2.getHexColor())))
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

        Mockito.when(taskService.findAllTasks(any(String.class))).thenThrow(new OwnerNotFoundException(NOT_EXISTING_OWNER_ID));

        mockMvc.perform(get("/api/v1/owner/{ownerId}/task", NOT_EXISTING_OWNER_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/task/{method-name}",
                        ownerPathParametersSnippet(),
                        apiError()));
    }

    @Test
    public void findOneTaskShouldReturnTaskDto() throws Exception {

        Mockito.when(taskService.findTask(anyString(), anyInt()))
                .thenReturn(taskDto_1);

        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("taskTitle", is(taskDto_1.getTaskTitle())))
                .andExpect(jsonPath("createdDate", is(taskDto_1.getCreatedDate().format(FORMATTER))))
                .andExpect(jsonPath("isDone", is(taskDto_1.getIsDone())))
                .andExpect(jsonPath("taskNumber", is(taskDto_1.getTaskNumber())))
                .andExpect(jsonPath("hexColor", is(taskDto_1.getHexColor())))
                .andDo(document("v1/task/{method-name}",
                        taskResponseFieldsSnippet(),
                        taskandOwnerPathParametersSnippet()
                ));
    }

    @Test
    public void findOneTaskShouldReturnErrorIfOwnerNotFound() throws Exception {

        Mockito.when(taskService.findTask(anyString(), anyInt()))
                .thenThrow(new OwnerNotFoundException(NOT_EXISTING_OWNER_ID));

        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/task/{method-name}",
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }

    @Test
    public void findOneTaskShouldReturnErrorIfTaskNotFound() throws Exception {

        Mockito.when(taskService.findTask(anyString(), anyInt()))
                .thenThrow(new TaskNotFoundException(NOT_EXISTING_TASK_ID));

        mockMvc.perform(get("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_TASK_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_TASK_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/task/{method-name}",
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }

    @Test
    public void addTaskShouldReturnTaskDto() throws Exception {

        TaskDto newTask = TaskDto
                .builder()
                .taskTitle(taskDto_1.getTaskTitle())
                .isDone(taskDto_1.getIsDone())
                .hexColor(taskDto_1.getHexColor())
                .build();

        Mockito.when(taskService.add(any(TaskDto.class), anyString()))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(newTask);
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("taskTitle", is(taskDto_1.getTaskTitle())))
                .andExpect(jsonPath("createdDate", is(taskDto_1.getCreatedDate().format(FORMATTER))))
                .andExpect(jsonPath("isDone", is(taskDto_1.getIsDone())))
                .andExpect(jsonPath("taskNumber", is(taskDto_1.getTaskNumber())))
                .andExpect(jsonPath("hexColor", is(taskDto_1.getHexColor())))
                .andDo(document("v1/task/{method-name}",
                        taskRequestFieldsSnippet(),
                        taskResponseFieldsSnippet(),
                        ownerPathParametersSnippet()
                ));

    }

    @Test
    public void addTaskShouldReturnErrorWhenTaskIsEmpty() throws Exception {

        Mockito.when(taskService.add(any(TaskDto.class), anyString()))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle("").build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION)))
                .andDo(document("v1/task/{method-name}", taskRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void addTaskShouldReturnErrorWhenTaskIsNull() throws Exception {

        Mockito.when(taskService.add(any(TaskDto.class), anyString()))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle(null).build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION)))
                .andDo(document("v1/task/{method-name}", taskRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void addTaskShouldReturnErrorWhenTaskIsToShort() throws Exception {

        Mockito.when(taskService.add(any(TaskDto.class), anyString()))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle("ab").build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 50")))
                .andDo(document("v1/task/{method-name}", taskRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void addTaskShouldReturnErrorWhenTaskIsToLong() throws Exception {

        Mockito.when(taskService.add(any(TaskDto.class), anyString()))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle("TOLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOONG").build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 50")))
                .andDo(document("v1/task/{method-name}", taskRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void addTaskShouldReturnErrorWhenHexColorHasBadFormat() throws Exception {

        Mockito.when(taskService.add(any(TaskDto.class), anyString()))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle(taskDto_1.getTaskTitle()).hexColor("#00").build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("Hex color code is not valid")))
                .andDo(document("v1/task/{method-name}", taskRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void addTaskShouldReturnErrorWhenOwnerIsNotFound() throws Exception {

        Mockito.when(taskService.add(any(TaskDto.class), anyString()))
                .thenThrow(new OwnerNotFoundException(NOT_EXISTING_OWNER_ID));
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle("abcd").build());
        mockMvc.perform(post("/api/v1/owner/{ownerId}/task", ownerDto_1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/task/{method-name}", taskRequestFieldsSnippet(), apiError()));
    }

    @Test
    public void deleteTask() throws Exception {
        Mockito.doNothing().when(taskService).delete(anyString(), anyInt());
        mockMvc.perform(delete("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("v1/task/{method-name}",
                        taskandOwnerPathParametersSnippet()));
    }

    @Test
    public void deleteTaskShouldReturnErrorIfOwnerNotFound() throws Exception {
        doThrow(new OwnerNotFoundException(NOT_EXISTING_OWNER_ID)).when(taskService).delete(anyString(), anyInt());
        mockMvc.perform(delete("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/task/{method-name}",
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }

    @Test
    public void deleteTaskShouldReturnErrorIfTaskNotFound() throws Exception {
        doThrow(new TaskNotFoundException(NOT_EXISTING_TASK_ID)).when(taskService).delete(anyString(), anyInt());
        mockMvc.perform(delete("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.[0].message", is(MESSAGE_TASK_NOT_FOUND_EXCEPTION)))
                .andExpect(jsonPath("$.[0].codes", containsInAnyOrder(CODE_TASK_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/task/{method-name}",
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }

    @Test
    public void updateTaskShouldReturnUpdatedTaskDto() throws Exception {

        TaskDto newTask = TaskDto
                .builder()
                .taskTitle(taskDto_1.getTaskTitle())
                .isDone(taskDto_1.getIsDone())
                .hexColor(taskDto_1.getHexColor())
                .build();

        Mockito.when(taskService.update(anyString(), anyInt(), any(TaskDto.class)))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(newTask);
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("taskTitle", is(taskDto_1.getTaskTitle())))
                .andExpect(jsonPath("createdDate", is(taskDto_1.getCreatedDate().format(FORMATTER))))
                .andExpect(jsonPath("isDone", is(taskDto_1.getIsDone())))
                .andExpect(jsonPath("taskNumber", is(taskDto_1.getTaskNumber())))
                .andExpect(jsonPath("hexColor", is(taskDto_1.getHexColor())))
                .andDo(document("v1/task/{method-name}",
                        taskRequestFieldsSnippet(),
                        taskResponseFieldsSnippet(),
                        taskandOwnerPathParametersSnippet()
                ));
    }

    @Test
    public void updateTaskShouldReturnErrorWhenTaskTitleIsEmpty() throws Exception {

        Mockito.when(taskService.update(anyString(), anyInt(), any(TaskDto.class)))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle("").build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION)))
                .andDo(document("v1/task/{method-name}",
                        taskRequestFieldsSnippet(),
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }

    @Test
    public void updateTaskShouldReturnErrorWhenTaskTitleIsNull() throws Exception {

        Mockito.when(taskService.update(anyString(), anyInt(), any(TaskDto.class)))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle(null).build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_NOT_BLANK_VALIDATION_EXCEPTION)))
                .andDo(document("v1/task/{method-name}",
                        taskRequestFieldsSnippet(),
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }


    @Test
    public void updateTaskShouldReturnErrorWhenTaskTitleIsToShort() throws Exception {

        Mockito.when(taskService.update(anyString(), anyInt(), any(TaskDto.class)))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle("ab").build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 50")))
                .andDo(document("v1/task/{method-name}",
                        taskRequestFieldsSnippet(),
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }

    @Test
    public void updateTaskShouldReturnErrorWhenTaskTitleIsToLong() throws Exception {

        Mockito.when(taskService.update(anyString(), anyInt(), any(TaskDto.class)))
                .thenReturn(taskDto_1);
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle("TOLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOONG").build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItem("size must be between 3 and 50")))
                .andDo(document("v1/task/{method-name}",
                        taskRequestFieldsSnippet(),
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }

    @Test
    public void updateTaskShouldReturnErrorWhenOwnerNotFound() throws Exception {

        Mockito.when(taskService.update(anyString(), anyInt(), any(TaskDto.class)))
                .thenThrow(new OwnerNotFoundException(NOT_EXISTING_OWNER_ID));
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle("abcd").build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_OWNER_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/task/{method-name}",
                        taskRequestFieldsSnippet(),
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }

    @Test
    public void updateTaskShouldReturnErrorWhenTaskNotFound() throws Exception {

        Mockito.when(taskService.update(anyString(), anyInt(), any(TaskDto.class)))
                .thenThrow(new TaskNotFoundException(NOT_EXISTING_TASK_ID));
        String taskDtoJson = objectMapper.writeValueAsString(TaskDto.builder().taskTitle("abcd").build());
        mockMvc.perform(put("/api/v1/owner/{ownerId}/task/{taskNumber}", ownerDto_1.getId(), taskDto_1.getTaskNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[*].message", hasItem(MESSAGE_TASK_NOT_FOUND_EXCEPTION)))
                .andDo(document("v1/task/{method-name}",
                        taskRequestFieldsSnippet(),
                        taskandOwnerPathParametersSnippet(),
                        apiError()));
    }

    private RequestFieldsSnippet taskRequestFieldsSnippet() {
        ConstraintDescriptions constraintDescriptions = new ConstraintDescriptions(TaskDto.class);
        return requestFields(
                fieldWithPath("taskTitle").description("Title of the task")
                        .attributes(key("constraints")
                                .value(constraintDescriptions
                                        .descriptionsForProperty("taskTitle"))),
                fieldWithPath("createdDate").description("Creation date of task")
                        .attributes(key("constraints")
                                .value(constraintDescriptions
                                        .descriptionsForProperty("createdDate"))).ignored(),
                fieldWithPath("taskNumber").description("The unique identifier of the task")
                        .attributes(key("constraints")
                                .value(constraintDescriptions
                                        .descriptionsForProperty("taskNumber"))).ignored(),
                fieldWithPath("isDone").description("The unique identifier of the task")
                        .attributes(key("constraints")
                                .value(constraintDescriptions
                                        .descriptionsForProperty("isDone"))).optional(),
                fieldWithPath("hexColor").description("Color of task")
                        .attributes(key("constraints")
                                .value(constraintDescriptions
                                        .descriptionsForProperty("hexColor"))).optional(),
                fieldWithPath("links").description("Link <<Resources>>").ignored()
        );
    }

    private LinksSnippet taskCollectionLinksSnippet() {
        return links(
                halLinks(),
                linkWithRel("self").description("Self <<Resource>>").ignored()
        );
    }

    private LinksSnippet taskLinksSnippet() {
        return links(
                halLinks(),
                linkWithRel("self").description("Self <<Resource>>").ignored(),
                linkWithRel("tasks").description("Tasks <<Resource>>").ignored()
        );
    }

    private ResponseFieldsSnippet taskCollectionResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("_embedded.taskDtoList[].taskTitle").description("Title of the task"),
                fieldWithPath("_embedded.taskDtoList[].createdDate").description("Creation date of the task"),
                fieldWithPath("_embedded.taskDtoList[].isDone").description("Information about that if taks is done or not"),
                fieldWithPath("_embedded.taskDtoList[].taskNumber").description("Number of owner's task"),
                fieldWithPath("_embedded.taskDtoList[].hexColor").description("Color of task"),
                subsectionWithPath("_embedded.taskDtoList[]._links").description("Task resource <<Resource>>").ignored(),
                subsectionWithPath("_links").description("Links <<Resource>>").ignored()
        );

    }

    private ResponseFieldsSnippet taskResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("taskTitle").description("Title of the task"),
                fieldWithPath("createdDate").description("Creation date of the task"),
                fieldWithPath("isDone").description("Information about that if taks is done or not"),
                fieldWithPath("taskNumber").description("Number of owner's task"),
                fieldWithPath("hexColor").description("Color of task"),
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

    private PathParametersSnippet taskandOwnerPathParametersSnippet() {
        return pathParameters(
                parameterWithName("ownerId").description("The unique identifier of the owner"),
                parameterWithName("taskNumber").description("The unique identifier of the taskNumber")
        );
    }

    private ResponseFieldsSnippet apiError() {
        return responseFields(
                fieldWithPath("[].codes").description("A list of technical codes describing the error"),
                fieldWithPath("[].message").description("A message describing the error").optional());
    }

}