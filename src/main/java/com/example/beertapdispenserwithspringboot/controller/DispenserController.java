package com.example.beertapdispenserwithspringboot.controller;

import com.example.beertapdispenserwithspringboot.constants.DispenserConstants;
import com.example.beertapdispenserwithspringboot.dto.ChangeDispenserStatusRequest;
import com.example.beertapdispenserwithspringboot.dto.CreateDispenserRequest;
import com.example.beertapdispenserwithspringboot.dto.DispenserCreationDTO;
import com.example.beertapdispenserwithspringboot.dto.DispenserDTO;
import com.example.beertapdispenserwithspringboot.exception.MSException;
import com.example.beertapdispenserwithspringboot.service.DispenserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DispenserConstants.BASE_REQUEST_PATH)
@Slf4j
@RequiredArgsConstructor
public class DispenserController {

    private final DispenserService dispenserService;

    @PostMapping
    public ResponseEntity<DispenserCreationDTO> createDispenser(@RequestBody final CreateDispenserRequest createDispenserRequest) {
        log.info(String.format("Dispenser creation request received with flow volume: %s", createDispenserRequest.getFlowVolume()));
        final DispenserCreationDTO dispenserCreationDTO = dispenserService.createDispenser(createDispenserRequest.getFlowVolume());
        log.info(String.format("Dispenser has been created with id: %s", dispenserCreationDTO.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(dispenserCreationDTO);
    }

    @PutMapping(DispenserConstants.ID_PATH + DispenserConstants.STATUS_PATH)
    public ResponseEntity<String> changeDispenserStatus(@PathVariable final String id,
                                                   @RequestBody final ChangeDispenserStatusRequest changeDispenserStatusRequest) throws MSException {
        log.info(String.format("Change dispenser status request received for dispenser with id: %s", id));
        dispenserService.changeDispenserStatus(id, changeDispenserStatusRequest.getStatus(), changeDispenserStatusRequest.getUpdatedAt());
        log.info(String.format("Dispenser status has been changed as '%s' for dispenser with id: %s", changeDispenserStatusRequest.getStatus(), id));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(DispenserConstants.SUCCESS_CHANGE_DISPENSER_STATUS_MESSAGE);
    }

    @GetMapping(DispenserConstants.ID_PATH + DispenserConstants.SPENDING_PATH)
    public ResponseEntity<DispenserDTO> returnMoneySpent(@PathVariable final String id) throws MSException {
        log.info(String.format("Return money spent request received for dispenser with id: %s", id));
        final DispenserDTO dispenserDTO = dispenserService.returnMoneySpent(id);
        log.info(String.format("Usage list has been prepared for dispenser with id: %s", id));
        return ResponseEntity.status(HttpStatus.OK).body(dispenserDTO);
    }
}
