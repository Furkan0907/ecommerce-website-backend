package com.furkan.service;

import com.furkan.dto.request.DtoComplaintIU;
import com.furkan.dto.response.DtoComplaint;

import java.util.List;

public interface IComplaintService {

    DtoComplaint create(DtoComplaintIU input);

    List<DtoComplaint> findAll();

    List<DtoComplaint> findAllByUserId(Long userId);

    DtoComplaint findById(Long id);

    void markAsResolved(Long id);

    void delete(Long id);
}
