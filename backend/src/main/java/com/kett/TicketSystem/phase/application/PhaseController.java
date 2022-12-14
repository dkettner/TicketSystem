package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.common.exceptions.NoParametersException;
import com.kett.TicketSystem.phase.application.dto.PhasePatchNameDto;
import com.kett.TicketSystem.phase.application.dto.PhasePatchPositionDto;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Transactional
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
@RequestMapping("/phases")
public class PhaseController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public PhaseController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<PhaseResponseDto> getPhaseById(@PathVariable UUID id) {
        PhaseResponseDto phaseResponseDto = ticketSystemService.getPhaseById(id);
        return new ResponseEntity<>(phaseResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PhaseResponseDto>> getPhasesByQuery(
            @RequestParam(name = "project-id", required = true) UUID projectId
    ) {
        if (projectId == null) { // TODO: null check not needed?
            throw new NoParametersException("cannot query if no projectId is specified");
        }

        List<PhaseResponseDto> phaseResponseDtos = ticketSystemService.getPhasesByProjectId(projectId);
        return new ResponseEntity<>(phaseResponseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PhaseResponseDto> postPhase(@RequestBody PhasePostDto phasePostDto) {
        PhaseResponseDto phaseResponseDto = ticketSystemService.addPhase(phasePostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(phaseResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(phaseResponseDto);
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<?> patchPhaseName(@PathVariable UUID id, @RequestBody PhasePatchNameDto phasePatchNameDto) {
        ticketSystemService.patchPhaseName(id, phasePatchNameDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/position")
    public ResponseEntity<?> patchPhasePosition(@PathVariable UUID id, @RequestBody PhasePatchPositionDto phasePatchPositionDto) {
        ticketSystemService.patchPhasePosition(id, phasePatchPositionDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePhase(@PathVariable UUID id) {
        ticketSystemService.deletePhaseById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
