package com.example.beertapdispenserwithspringboot.service;

import com.example.beertapdispenserwithspringboot.constants.DispenserConstants;
import com.example.beertapdispenserwithspringboot.constants.DispenserStatus;
import com.example.beertapdispenserwithspringboot.constants.MSExceptionEnum;
import com.example.beertapdispenserwithspringboot.dto.DispenserCreationDTO;
import com.example.beertapdispenserwithspringboot.dto.DispenserDTO;
import com.example.beertapdispenserwithspringboot.dto.UsageDTO;
import com.example.beertapdispenserwithspringboot.exception.MSException;
import com.example.beertapdispenserwithspringboot.model.Dispenser;
import com.example.beertapdispenserwithspringboot.model.Usage;
import com.example.beertapdispenserwithspringboot.repository.DispenserRepository;
import com.example.beertapdispenserwithspringboot.repository.UsageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DispenserService {

    private final DispenserRepository dispenserRepository;
    private final UsageRepository usageRepository;

    @Transactional
    public DispenserCreationDTO createDispenser(final BigDecimal flowVolume) {
        log.info("The dispenser is being created.");
        final Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(flowVolume.toString());
        dispenser.setAmount(BigDecimal.ZERO.toString());
        dispenser.setStatus(DispenserStatus.CLOSE.getValue());
        dispenser.setAmount(BigDecimal.ZERO.toString());
        dispenserRepository.save(dispenser);
        final DispenserCreationDTO dispenserCreationDTO = new DispenserCreationDTO();
        dispenserCreationDTO.setId(dispenser.getId());
        dispenserCreationDTO.setFlowVolume(new BigDecimal(dispenser.getFlowVolume()));
        return dispenserCreationDTO;
    }

    @Transactional
    public void changeDispenserStatus(final String dispenserId, final String status, final LocalDateTime updatedAt) throws MSException {
        log.info("The dispenser status is being changed.");
        final Optional<Dispenser> optionalDispenser = dispenserRepository.findDispenserById(dispenserId);
        if (optionalDispenser.isEmpty()) {
            throw new MSException(MSExceptionEnum.DISPENSER_NOT_FOUND.getErrorCode(), MSExceptionEnum.DISPENSER_NOT_FOUND.getErrorMessage(), dispenserId);
        }
        if (status.equalsIgnoreCase(optionalDispenser.get().getStatus())) {
            throw new MSException(MSExceptionEnum.DISPENSER_STATUS_NOT_VALID.getErrorCode(), MSExceptionEnum.DISPENSER_STATUS_NOT_VALID.getErrorMessage(), dispenserId);
        } else if (status.equalsIgnoreCase(DispenserStatus.OPEN.getValue())) {
            updateDispenserStatus(optionalDispenser.get(), status);
            createUsage(optionalDispenser.get(), updatedAt);
        } else if (status.equalsIgnoreCase(DispenserStatus.CLOSE.getValue())) {
            updateDispenserStatus(optionalDispenser.get(), status);
            updateUsage(optionalDispenser.get(), updatedAt);
            updateDispenserTotalAmount(optionalDispenser.get());
        }
    }

    public DispenserDTO returnMoneySpent(final String dispenserId) throws MSException {
        log.info("The usage list is being prepared.");
        final Optional<Dispenser> optionalDispenser = dispenserRepository.findDispenserById(dispenserId);
        if (optionalDispenser.isEmpty()) {
            throw new MSException(MSExceptionEnum.DISPENSER_NOT_FOUND.getErrorCode(), MSExceptionEnum.DISPENSER_NOT_FOUND.getErrorMessage(), dispenserId);
        }
        final DispenserDTO dispenserDTO = new DispenserDTO();
        dispenserDTO.setAmount(new BigDecimal(optionalDispenser.get().getAmount()));
        dispenserDTO.setUsages(prepareUsagesList(optionalDispenser.get()));
        return dispenserDTO;
    }

    private Long findFlowDurationInSeconds(final LocalDateTime openedAt, final LocalDateTime closedAt) {
        return Duration.between(openedAt, closedAt).toSeconds();
    }

    private BigDecimal calculateAmountOfFlow(final Long flowDuration, final BigDecimal flowVolume) {
        final BigDecimal totalVolume = flowVolume.multiply(new BigDecimal(flowDuration));
        final BigDecimal amount = totalVolume.multiply(new BigDecimal(DispenserConstants.PRICE_PER_LITER));
        return amount.setScale(4);
    }

    private BigDecimal calculateTotalAmount(final Dispenser dispenser) {
        final List<Usage> usages = usageRepository.findAllByDispenser(dispenser);
        final BigDecimal totalAmount = usages.stream()
                .map(Usage::getTotalSpent)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalAmount.setScale(4);
    }

    private void createUsage(final Dispenser dispenser, final LocalDateTime updatedAt) {
        final Usage usage = new Usage();
        usage.setOpenedAt(updatedAt);
        usage.setClosedAt(null);
        usage.setFlowVolume(dispenser.getFlowVolume());
        usage.setTotalSpent(null);
        usage.setDispenser(dispenser);
        usageRepository.save(usage);
    }

    private void updateUsage(final Dispenser dispenser, final LocalDateTime updatedAt) {
        final List<Usage> usages = usageRepository.findAllByDispenserOrderByOpenedAt(dispenser);
        final Usage usage = usages.get(usages.size() - 1);
        final Long flowDuration = findFlowDurationInSeconds(usage.getOpenedAt(), updatedAt);
        final BigDecimal amount = calculateAmountOfFlow(flowDuration, new BigDecimal(dispenser.getFlowVolume()));
        usage.setClosedAt(updatedAt);
        usage.setTotalSpent(amount.toString());
        usageRepository.save(usage);
    }

    private void updateDispenserStatus(final Dispenser dispenser, final String status) {
        dispenser.setStatus(status);
        dispenserRepository.save(dispenser);
    }

    private void updateDispenserTotalAmount(final Dispenser dispenser) {
        final BigDecimal totalAmount = calculateTotalAmount(dispenser);
        dispenser.setAmount(totalAmount.toString());
        dispenserRepository.save(dispenser);
    }

    private List<UsageDTO> prepareUsagesList(final Dispenser dispenser) {
        return usageRepository.findAllByDispenser(dispenser)
                .stream()
                .map(UsageDTO::covert)
                .toList();
    }
}
