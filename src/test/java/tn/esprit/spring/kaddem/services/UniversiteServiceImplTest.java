package tn.esprit.spring.kaddem.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;
import tn.esprit.spring.kaddem.services.UniversiteServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

class UniversiteServiceImplTest {

    @InjectMocks
    UniversiteServiceImpl universiteService;  // Service class that will be tested

    @Mock
    UniversiteRepository universiteRepository;  // Mock the Universite repository

    @Mock
    DepartementRepository departementRepository;  // Mock the Departement repository

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testRetrieveAllUniversites() {
        // Arrange: Mock the behavior of the repository
        Universite univ1 = new Universite("University A");
        Universite univ2 = new Universite("University B");
        List<Universite> mockUniversites = Arrays.asList(univ1, univ2);
        when(universiteRepository.findAll()).thenReturn(mockUniversites);

        // Act: Call the method
        List<Universite> result = universiteService.retrieveAllUniversites();

        // Assert: Verify the results
        assertEquals(2, result.size());
        verify(universiteRepository, times(1)).findAll();  // Ensure the method was called once
    }

    @Test
    void testAddUniversite() {
        // Arrange: Mock the behavior of the repository
        Universite universite = new Universite("University C");
        when(universiteRepository.save(universite)).thenReturn(universite);

        // Act: Call the method
        Universite result = universiteService.addUniversite(universite);

        // Assert: Verify the results
        assertEquals(universite, result);
        verify(universiteRepository, times(1)).save(universite);  // Ensure the method was called once
    }

    @Test
    void testUpdateUniversite() {
        // Arrange: Mock the behavior of the repository
        Universite universite = new Universite("Updated University");
        when(universiteRepository.save(universite)).thenReturn(universite);

        // Act: Call the method
        Universite result = universiteService.updateUniversite(universite);

        // Assert: Verify the results
        assertEquals(universite, result);
        verify(universiteRepository, times(1)).save(universite);  // Ensure the method was called once
    }

    @Test
    void testRetrieveUniversite() {
        // Arrange: Mock the behavior of the repository
        Universite univ = new Universite("University D");
        univ.setIdUniv(1);
        when(universiteRepository.findById(1)).thenReturn(java.util.Optional.of(univ));

        // Act: Call the method
        Universite result = universiteService.retrieveUniversite(1);

        // Assert: Verify the results
        assertEquals(univ, result);
        verify(universiteRepository, times(1)).findById(1);  // Ensure the method was called once
    }

    @Test
    void testDeleteUniversite() {
        // Arrange: Mock the behavior of the repository
        Universite univ = new Universite("University E");
        univ.setIdUniv(2);
        when(universiteRepository.findById(2)).thenReturn(java.util.Optional.of(univ));

        // Act: Call the method
        universiteService.deleteUniversite(2);

        // Assert: Verify that delete was called
        verify(universiteRepository, times(1)).delete(univ);
    }


    @Test
    void testAssignUniversiteToDepartement() {
        // Arrange: Mock the behavior of the repositories
        Universite univ = new Universite("University F");
        univ.setIdUniv(3);

        // Ensure that departements is initialized (to avoid NullPointerException)
        Set<Departement> departements = new HashSet<>();
        univ.setDepartements(departements);  // Initialize the Set to avoid null

        Departement departement = new Departement();  // Assume Departement has a default constructor
        departement.setIdDepart(1);

        when(universiteRepository.findById(3)).thenReturn(java.util.Optional.of(univ));
        when(departementRepository.findById(1)).thenReturn(java.util.Optional.of(departement));

        // Act: Call the method
        universiteService.assignUniversiteToDepartement(3, 1);

        // Assert: Verify the interactions with the repositories
        assertTrue(univ.getDepartements().contains(departement));  // Ensure the departement was added
        verify(universiteRepository, times(1)).save(univ);  // Ensure save was called
    }
    @Test
    void testRetrieveDepartementsByUniversite() {
        // Arrange: Mock the behavior of the repository
        Universite univ = new Universite("University G");
        Departement departement1 = new Departement();
        Departement departement2 = new Departement();
        Set<Departement> departements = new HashSet<>(Arrays.asList(departement1, departement2));
        univ.setDepartements(departements);

        when(universiteRepository.findById(4)).thenReturn(java.util.Optional.of(univ));

        // Act: Call the method
        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(4);

        // Assert: Verify the results
        assertEquals(2, result.size());  // Check if two departements are returned
        verify(universiteRepository, times(1)).findById(4);  // Ensure the method was called once
    }
}