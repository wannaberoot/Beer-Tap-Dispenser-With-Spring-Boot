package com.example.beertapdispenserwithspringboot.service;

import com.example.beertapdispenserwithspringboot.constants.DispenserStatus;
import com.example.beertapdispenserwithspringboot.constants.TestConstants;
import com.example.beertapdispenserwithspringboot.dto.DispenserCreationDTO;
import com.example.beertapdispenserwithspringboot.dto.DispenserDTO;
import com.example.beertapdispenserwithspringboot.exception.MSException;
import com.example.beertapdispenserwithspringboot.model.Dispenser;
import com.example.beertapdispenserwithspringboot.model.Usage;
import com.example.beertapdispenserwithspringboot.repository.DispenserRepository;
import com.example.beertapdispenserwithspringboot.repository.UsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
class DispenserServiceTest {

    @Mock
    private DispenserRepository dispenserRepository;
    @Mock
    private UsageRepository usageRepository;
    @InjectMocks
    private DispenserService underTest;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        underTest = new DispenserService(dispenserRepository, usageRepository);
    }

    @Test
    void createDispenser_withValidRequest_createAndReturnDispenser() {
        // Given
        Dispenser dispenser = new Dispenser();
        Mockito.when(dispenserRepository.save(dispenser)).thenReturn(dispenser);

        // When
        DispenserCreationDTO dispenserCreationDTO = underTest.createDispenser(TestConstants.MOCK_FLOW_VOLUME);

        // Then
        assertEquals(TestConstants.MOCK_FLOW_VOLUME, dispenserCreationDTO.getFlowVolume());
        assertEquals(dispenser.getId(), dispenserCreationDTO.getId());
    }

    @Test
    void changeDispenserStatus_withInvalidDispenserId_thenThrowException() {
        // Given
        Optional<Dispenser> optionalDispenser = Optional.empty();
        Mockito.when(dispenserRepository.findDispenserById(TestConstants.MOCK_DISPENSER_ID)).thenReturn(optionalDispenser);

        // Then
        assertThrows(MSException.class,
                () -> underTest.changeDispenserStatus(TestConstants.MOCK_DISPENSER_ID,
                        DispenserStatus.OPEN.getValue(),
                        TestConstants.MOCK_UPDATED_AT));
    }

    @Test
    void changeDispenserStatusToOpen_whenDispenserStatusIsAlreadyOpen_thenThrowException() {
        // Given
        Dispenser dispenser = new Dispenser();
        dispenser.setId(TestConstants.MOCK_DISPENSER_ID);
        dispenser.setStatus(DispenserStatus.OPEN.getValue());
        Mockito.when(dispenserRepository.findDispenserById(TestConstants.MOCK_DISPENSER_ID)).thenReturn(Optional.of(dispenser));

        // Then
        assertThrows(MSException.class,
                () -> underTest.changeDispenserStatus(TestConstants.MOCK_DISPENSER_ID,
                        DispenserStatus.OPEN.getValue(),
                        TestConstants.MOCK_UPDATED_AT));
    }

    @Test
    void changeDispenserStatusToClose_whenDispenserStatusIsAlreadyClose_thenThrowException() {
        // Given
        Dispenser dispenser = new Dispenser();
        dispenser.setId(TestConstants.MOCK_DISPENSER_ID);
        dispenser.setStatus(DispenserStatus.CLOSE.getValue());
        Mockito.when(dispenserRepository.findDispenserById(TestConstants.MOCK_DISPENSER_ID)).thenReturn(Optional.of(dispenser));

        // Then
        assertThrows(MSException.class,
                () -> underTest.changeDispenserStatus(TestConstants.MOCK_DISPENSER_ID,
                        DispenserStatus.CLOSE.getValue(),
                        TestConstants.MOCK_UPDATED_AT));
    }

    @Test
    void changeDispenserStatusToOpen_whenDispenserStatusIsClose_thenUpdateDispenserAndCreateUsage() throws MSException {
        // Given
        Dispenser dispenser = new Dispenser();
        dispenser.setId(TestConstants.MOCK_DISPENSER_ID);
        dispenser.setFlowVolume(TestConstants.MOCK_FLOW_VOLUME.toString());
        dispenser.setStatus(DispenserStatus.CLOSE.getValue());
        List<Usage> usages = new ArrayList<>();
        Usage usage = new Usage();
        usage.setOpenedAt(TestConstants.MOCK_UPDATED_AT);
        usage.setFlowVolume(dispenser.getFlowVolume());
        usage.setDispenser(dispenser);
        usages.add(usage);
        Mockito.when(dispenserRepository.findDispenserById(TestConstants.MOCK_DISPENSER_ID)).thenReturn(Optional.of(dispenser));
        Mockito.when(usageRepository.findAllByDispenser(dispenser)).thenReturn(usages);

        // When
        underTest.changeDispenserStatus(TestConstants.MOCK_DISPENSER_ID, DispenserStatus.OPEN.getValue(), TestConstants.MOCK_UPDATED_AT);

        // Then
        assertEquals(DispenserStatus.OPEN.getValue(), dispenser.getStatus());
        assertEquals(1, usageRepository.findAllByDispenser(dispenser).size());
    }

    @Test
    void changeDispenserStatusToClose_whenDispenserStatusIsOpen_thenUpdateDispenserAndUsage() throws MSException {
        // Given
        Dispenser dispenser = new Dispenser();
        dispenser.setId(TestConstants.MOCK_DISPENSER_ID);
        dispenser.setFlowVolume(TestConstants.MOCK_FLOW_VOLUME.toString());
        dispenser.setStatus(DispenserStatus.OPEN.getValue());
        List<Usage> usages = new ArrayList<>();
        Usage usage = new Usage();
        usage.setOpenedAt(TestConstants.MOCK_UPDATED_AT);
        usage.setFlowVolume(dispenser.getFlowVolume());
        usage.setDispenser(dispenser);
        usages.add(usage);
        Mockito.when(dispenserRepository.findDispenserById(TestConstants.MOCK_DISPENSER_ID)).thenReturn(Optional.of(dispenser));
        Mockito.when(usageRepository.findAllByDispenserOrderByOpenedAt(dispenser)).thenReturn(usages);

        // When
        underTest.changeDispenserStatus(TestConstants.MOCK_DISPENSER_ID, DispenserStatus.CLOSE.getValue(), TestConstants.MOCK_UPDATED_AT);

        // Then
        assertEquals(DispenserStatus.CLOSE.getValue(), dispenser.getStatus());
        assertEquals(1, usageRepository.findAllByDispenserOrderByOpenedAt(dispenser).size());
        assertNotNull(usageRepository.findAllByDispenserOrderByOpenedAt(dispenser).get(0).getClosedAt());
        assertNotNull(usageRepository.findAllByDispenserOrderByOpenedAt(dispenser).get(0).getTotalSpent());
        assertNotNull(dispenser.getAmount());
    }

    @Test
    void returnMoneySpent_withInvalidDispenserId_thenThrowException() {
        // Given
        Optional<Dispenser> optionalDispenser = Optional.empty();
        Mockito.when(dispenserRepository.findDispenserById(TestConstants.MOCK_DISPENSER_ID)).thenReturn(optionalDispenser);

        // Then
        assertThrows(MSException.class,
                () -> underTest.returnMoneySpent(TestConstants.MOCK_DISPENSER_ID));
    }

    @Test
    void returnMoneySpent_withValidDispenserId_thenReturnUsageList() throws MSException {
        // Given
        Dispenser dispenser = new Dispenser();
        dispenser.setId(TestConstants.MOCK_DISPENSER_ID);
        dispenser.setFlowVolume(TestConstants.MOCK_FLOW_VOLUME.toString());
        dispenser.setStatus(DispenserStatus.CLOSE.getValue());
        dispenser.setAmount(TestConstants.MOCK_AMOUNT.toString());
        List<Usage> usages = new ArrayList<>();
        Usage usage = new Usage();
        usage.setFlowVolume(dispenser.getFlowVolume());
        usage.setTotalSpent(TestConstants.MOCK_AMOUNT.toString());
        usage.setDispenser(dispenser);
        usages.add(usage);
        Mockito.when(dispenserRepository.findDispenserById(TestConstants.MOCK_DISPENSER_ID)).thenReturn(Optional.of(dispenser));
        Mockito.when(usageRepository.findAllByDispenser(dispenser)).thenReturn(usages);

        // When
        DispenserDTO dispenserDTO = underTest.returnMoneySpent(TestConstants.MOCK_DISPENSER_ID);

        // Then
        assertEquals(dispenser.getAmount(), dispenserDTO.getAmount().toString());
        assertEquals(1, dispenserDTO.getUsages().size());
        assertEquals(dispenser.getFlowVolume(), dispenserDTO.getUsages().get(0).getFlowVolume().toString());
    }
}
