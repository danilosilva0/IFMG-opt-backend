package edu.ifmg.produtos.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ifmg.produtos.dtos.ProductDTO;
import edu.ifmg.produtos.util.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 200L;
    }

    @Test
    void findAllShouldReturnSortedPageWhenSortByName() throws Exception {

        ResultActions result =

        mockMvc.perform(get("/product?page=0&size=10&sort=name,asc")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
    }

    @Test
    void updateShouldReturnDtoWhenIdExists() throws Exception {

        ProductDTO dto = Factory.createProductDTO();
        String dtoJson = objectMapper.writeValueAsString(dto);

        String nameExpected = dto.getName();
        String descriptionExpected = dto.getDescription();


        ResultActions result =
            mockMvc.perform(
                put("/product/{id}", existingId)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.name").value(nameExpected));
        result.andExpect(jsonPath("$.description").value(descriptionExpected));
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

        ProductDTO dto = Factory.createProductDTO();
        String dtoJson = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(
                        put("/product/{id}", nonExistingId)
                                .content(dtoJson)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNotFound());
    }

    @Test
    void insertShouldReturnNewObject() throws Exception {

        ProductDTO dto = Factory.createProductDTO();
        String dtoJson = objectMapper.writeValueAsString(dto);

        String nameExpected = dto.getName();
        Long idExpected = 26L;


        ResultActions result =
                mockMvc.perform(
                        post("/product", existingId)
                                .content(dtoJson)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").value(idExpected));
        result.andExpect(jsonPath("$.name").value(nameExpected));
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(
                        delete("/product/{id}", existingId)
                );

        result.andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdNotExists() throws Exception {
        ResultActions result =
                mockMvc.perform(
                        delete("/product/{id}", nonExistingId)
                );

        result.andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(
                        get("/product/{id}", existingId)
                );

        result.andExpect(status().isOk());
        String resultJson = result.andReturn().getResponse().getContentAsString();

        ProductDTO dto = objectMapper.readValue(resultJson, ProductDTO.class);
        Assertions.assertEquals(existingId, dto.getId());
        Assertions.assertEquals("The Lord of the Rings", dto.getName());
    }
}
