package tn.esprit.spring.kaddem.services;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.kaddem.controllers.UniversiteRestController;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.services.IUniversiteService;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(UniversiteRestController.class)
class UniversiteRestControllerTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvc allows us to simulate HTTP requests

    @MockBean
    private IUniversiteService universiteService;  // Mock the IUniversiteService interface

    private Universite universite1, universite2;

    @BeforeEach
    void setUp() {
        // Set up mock data
        universite1 = new Universite("University A");
        universite1.setIdUniv(1);
        universite2 = new Universite("University B");
        universite2.setIdUniv(2);
    }

    @Test
    void testGetAllUniversites() throws Exception {
        // Arrange: Mock the service call
        List<Universite> universites = Arrays.asList(universite1, universite2);
        when(universiteService.retrieveAllUniversites()).thenReturn(universites);

        // Act & Assert: Perform a GET request and verify the response
        mockMvc.perform(get("/universite/retrieve-all-universites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomUniv").value("University A"))  // Use 'nomUniv' instead of 'name'
                .andExpect(jsonPath("$[1].nomUniv").value("University B"))  // Use 'nomUniv' instead of 'name'
                .andExpect(jsonPath("$", hasSize(2)));  // Check that the response contains 2 universites
    }

    @Test
    void testGetUniversiteById() throws Exception {
        // Arrange: Mock the service call
        when(universiteService.retrieveUniversite(1)).thenReturn(universite1);

        // Act & Assert: Perform a GET request for a specific Universite
        mockMvc.perform(get("/universite/retrieve-universite/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomUniv").value("University A"))  // Use 'nomUniv' instead of 'name'
                .andExpect(jsonPath("$.idUniv").value(1));
    }

    @Test
    void testAddUniversite() throws Exception {
        // Arrange: Mock the service call
        when(universiteService.addUniversite(any(Universite.class))).thenReturn(universite1);

        // Act & Assert: Perform a POST request to add a Universite
        mockMvc.perform(post("/universite/add-universite")
                        .contentType("application/json")
                        .content("{\"nomUniv\": \"University A\"}"))  // Use 'nomUniv' instead of 'name'
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomUniv").value("University A"))
                .andExpect(jsonPath("$.idUniv").value(1));
    }

    @Test
    void testRemoveUniversite() throws Exception {
        // Arrange: Mock the service call
        doNothing().when(universiteService).deleteUniversite(1);

        // Act & Assert: Perform a DELETE request to remove a Universite
        mockMvc.perform(delete("/universite/remove-universite/1"))
                .andExpect(status().isOk());

        verify(universiteService, times(1)).deleteUniversite(1);  // Verify the service method was called
    }

    @Test
    void testUpdateUniversite() throws Exception {
        // Arrange: Create the updated Universite object with the new name
        Universite updatedUniversite = new Universite("Updated University A");
        updatedUniversite.setIdUniv(1);  // Make sure the ID is correct

        // Mock the service to return the updated Universite object
        when(universiteService.updateUniversite(any(Universite.class))).thenReturn(updatedUniversite);

        // Act & Assert: Perform a PUT request to update a Universite
        mockMvc.perform(put("/universite/update-universite")
                        .contentType("application/json")
                        .content("{\"idUniv\": 1, \"nomUniv\": \"Updated University A\"}"))
                .andExpect(status().isOk())  // Expect status OK
                .andExpect(jsonPath("$.nomUniv").value("Updated University A"))  // Check if name is updated
                .andExpect(jsonPath("$.idUniv").value(1));  // Check if ID is correct
    }

    @Test
    void testAffecterUniversiteToDepartement() throws Exception {
        // Arrange: Mock the service call
        doNothing().when(universiteService).assignUniversiteToDepartement(1, 1);

        // Act & Assert: Perform a PUT request to assign a Universite to a Departement
        mockMvc.perform(put("/universite/affecter-universite-departement/1/1"))
                .andExpect(status().isOk());

        verify(universiteService, times(1)).assignUniversiteToDepartement(1, 1);  // Verify the service method was called
    }

    @Test
    void testListerDepartementsUniversite() throws Exception {
        // Arrange: Mock the service call
        when(universiteService.retrieveDepartementsByUniversite(1)).thenReturn(universite1.getDepartements());

        // Act & Assert: Perform a GET request to list Departements of a Universite
        mockMvc.perform(get("/universite/listerDepartementsUniversite/1"))
                .andExpect(status().isOk());
    }
}
