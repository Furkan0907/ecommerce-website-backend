package com.furkan.controller.impl;

import com.furkan.controller.IRestComplaintController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoComplaintIU;
import com.furkan.dto.response.DtoComplaint;
import com.furkan.service.IComplaintService;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class RestComplaintControllerImpl extends RestBaseController implements IRestComplaintController {

    @Autowired
    private IComplaintService complaintService;

    @PreAuthorize("@securityService.isOnlyCustomer()")
    @PostMapping()
    @Override
    public RootEntity<DtoComplaint> create(@Valid @RequestBody DtoComplaintIU input) {
        return ok(complaintService.create(input));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @Override
    public RootEntity<List<DtoComplaint>> findAll() {
        return ok(complaintService.findAll());
    }

    @PreAuthorize("@securityService.canAccessUser(#userId)")
    @GetMapping("/user/{userId}")
    @Override
    public RootEntity<List<DtoComplaint>> findAllByUserId(@PathVariable Long userId) {
        return ok(complaintService.findAllByUserId(userId));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isComplaintOwner(#id)")
    @GetMapping("/{id}")
    @Override
    public RootEntity<DtoComplaint> findById(@PathVariable Long id) {
        return ok(complaintService.findById(id));
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @PutMapping("/{id}/resolved")
    @Override
    public RootEntity<Void> markAsResolved(@PathVariable Long id) {
        complaintService.markAsResolved(id);
        return ok();
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isComplaintOwner()")
    @DeleteMapping("/{id}")
    @Override
    public RootEntity<Void> delete(@PathVariable Long id) {
        complaintService.delete(id);
        return ok();
    }
}
