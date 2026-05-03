package com.grantflow;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {
    private final ProgramRepository programRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public List<Program> all() {
        return programRepository.findAll();
    }

    @GetMapping("/open")
    public List<Program> open() {
        return programRepository.findByStatus(ProgramStatus.OPEN);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public Program create(@RequestBody ApplicationDtos.CreateProgramRequest request) {
        Program program = Program.builder()
                .name(request.name())
                .description(request.description())
                .awardAmount(request.awardAmount())
                .applicationDeadline(request.applicationDeadline())
                .eligibilityCriteria(request.eligibilityCriteria())
                .status(ProgramStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return programRepository.save(program);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public Program updateStatus(@PathVariable Long id, @RequestParam ProgramStatus status) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));
        program.setStatus(status);
        program.setUpdatedAt(LocalDateTime.now());
        return programRepository.save(program);
    }
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public Program closeProgram(@PathVariable Long id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        program.setStatus(ProgramStatus.CLOSED);
        program.setUpdatedAt(LocalDateTime.now());

        return programRepository.save(program);
    }

    @PatchMapping("/{id}/reopen")
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public Program reopenProgram(@PathVariable Long id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        program.setStatus(ProgramStatus.OPEN);
        program.setUpdatedAt(LocalDateTime.now());

        return programRepository.save(program);
    }


}
