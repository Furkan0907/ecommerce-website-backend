package com.furkan.controller;

import com.furkan.dto.request.DtoComplaintIU;
import com.furkan.dto.response.DtoComplaint;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestComplaintController {

    RootEntity<DtoComplaint> create(DtoComplaintIU input);

    RootEntity<List<DtoComplaint>> findAll();

    RootEntity<List<DtoComplaint>> findAllByUserId(Long userId);

    RootEntity<DtoComplaint> findById(Long id);

    RootEntity<Void> markAsResolved(Long id);

    RootEntity<Void> delete(Long id);
}
