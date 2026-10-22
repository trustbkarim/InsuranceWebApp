package com.insurance.insuranceManagement.controller;

import com.insurance.insuranceManagement.domain.enums.ClaimStatus;
import com.insurance.insuranceManagement.dto.request.ClaimRequestRecord;
import com.insurance.insuranceManagement.dto.response.ClaimResponseRecord;
import com.insurance.insuranceManagement.dto.response.PagedResponse;
import com.insurance.insuranceManagement.dto.update.UpdateClaimStatusRequest;
import com.insurance.insuranceManagement.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/claims")
@RequiredArgsConstructor
@Tag(name = "Claims", description = "Claims management")
public class ClaimController {

    private final ClaimService claimService;


    @PostMapping
    @Operation(summary = "New claim declaration")
    public ResponseEntity<ClaimResponseRecord> createClaim(@Valid @RequestBody ClaimRequestRecord claimRequest) {

        log.info("POST /claims - Policy = {}", claimRequest.policyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(claimService.createClaim(claimRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID")
    public ResponseEntity<ClaimResponseRecord> findById(@PathVariable Long id) {

        return ResponseEntity.ok(claimService.findById(id));
    }

    @GetMapping("/number/{claimNumber}")
    @Operation(summary = "Get claim by number")
    public ResponseEntity<ClaimResponseRecord> findByClaimNumber(@PathVariable String claimNumber) {

        return ResponseEntity.ok(claimService.findByClaimNumber(claimNumber));
    }

    @GetMapping
    @Operation(summary = "Get claims")
    public ResponseEntity<PagedResponse<ClaimResponseRecord>> findAll(@RequestParam(defaultValue = "0")             int page,
                                                                      @RequestParam(defaultValue = "10")            int size,
                                                                      @RequestParam(defaultValue = "createdAt")     String sortBy,
                                                                      @RequestParam(defaultValue = "desc")          String sorDir,
                                                                      @RequestParam(required = false)               ClaimStatus status) {

        Sort sort = sorDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        if(status != null) {
            ResponseEntity.ok(claimService.findByStatus(status, PageRequest.of(page, size, sort)));
        }

        return ResponseEntity.ok(claimService.findAll(PageRequest.of(page, size, sort)));
    }

    @GetMapping("/policy/{policyId}")
    @Operation(summary = "Get claims by policy")
    public ResponseEntity<PagedResponse<ClaimResponseRecord>> findByPolicy(@PathVariable Long policyId,
                                                                           @RequestParam(defaultValue = "0")    int page,
                                                                           @RequestParam(defaultValue = "10")   int size) {

        return ResponseEntity.ok(claimService.findByPolicy(policyId, PageRequest.of(page, size)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update claim status (Approve, Rejected, Closed")
    public ResponseEntity<ClaimResponseRecord> updateClaimStatus(@PathVariable Long id,
                                                                                @Valid @RequestBody UpdateClaimStatusRequest updateClaimStatusRequest) {

        log.info("PATCH /v1/claims/{}/status → {}", id, updateClaimStatusRequest.status());
        return ResponseEntity.ok(claimService.updateClaimStatus(id, updateClaimStatusRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete claim (SUBMITTED status only)")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {

        log.info("Delete  /v1/claims/{}", id);
        claimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }
}
