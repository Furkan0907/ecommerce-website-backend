package com.furkan.service.impl;

import com.furkan.dto.request.DtoComplaintIU;
import com.furkan.dto.response.DtoComplaint;
import com.furkan.dto.response.DtoUser;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Complaint;
import com.furkan.model.User;
import com.furkan.repository.ComplaintRepository;
import com.furkan.repository.UserRepository;
import com.furkan.service.IComplaintService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ComplaintServiceImpl implements IComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    private DtoComplaint dtoConverter(Complaint complaint) {
        if (complaint.getUser() == null) {
            throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null));
        }

        DtoComplaint dtoComplaint = new DtoComplaint();
        BeanUtils.copyProperties(complaint, dtoComplaint);

        DtoUser dtoUser = new DtoUser();
        BeanUtils.copyProperties(complaint.getUser(), dtoUser);
        dtoComplaint.setUser(dtoUser);

        return dtoComplaint;
    }

    @Override
    public DtoComplaint create(DtoComplaintIU input) {
        Complaint complaint = new Complaint();

        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, input.getUserId().toString())));

        complaint.setCreatedAt(new Date());
        complaint.setDescription(input.getDescription());
        complaint.setSubject(input.getSubject());
        complaint.setUser(user);
        complaint.setUpdatedAt(new Date());

        Complaint saved = complaintRepository.save(complaint);
        return dtoConverter(saved);
    }

    @Override
    public List<DtoComplaint> findAll() {
        List<DtoComplaint> dtoComplaints = new ArrayList<>();
        List<Complaint> complaints = complaintRepository.findAll();
        for (Complaint complaint : complaints) {
            dtoComplaints.add(dtoConverter(complaint));
        }
        return dtoComplaints;
    }

    @Override
    public List<DtoComplaint> findAllByUserId(Long userId) {
        List<Complaint> complaints = complaintRepository.findAllByUserId(userId);
        List<DtoComplaint> dtoComplaints = new ArrayList<>();
        for (Complaint complaint : complaints) {
            dtoComplaints.add(dtoConverter(complaint));
        }
        return dtoComplaints;
    }

    @Override
    public DtoComplaint findById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.COMPLAINT_NOT_FOUND, id.toString())));
        return dtoConverter(complaint);
    }

    @Override
    public void markAsResolved(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.COMPLAINT_NOT_FOUND, id.toString())));
        if (complaint.isResolved()) {
            throw new BaseException(new ErrorMessage(MessageType.COMPLAINT_IS_ALREADY_RESOLVED, id.toString()));
        }
        complaint.setResolved(true);
        complaintRepository.save(complaint);
    }

    @Override
    public void delete(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.COMPLAINT_NOT_FOUND, id.toString())));
        complaintRepository.delete(complaint);
    }
}
