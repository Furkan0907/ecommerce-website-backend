package com.furkan.controller;

import com.furkan.utils.PagerUtil;
import com.furkan.utils.RestPageableEntity;
import com.furkan.utils.RestPageableRequest;
import com.furkan.utils.RootEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class RestBaseController {

    public <T> RootEntity<T> ok(T payload) {
        return RootEntity.ok(payload);
    }

    public <T> RootEntity<T> error(String errorMessage) {
        return RootEntity.error(errorMessage);
    }

    public RootEntity<Void> ok() {
        return RootEntity.ok();
    }

    public Pageable toPageable(RestPageableRequest request) {
        return PagerUtil.toPageable(request);
    }

    public <T> RestPageableEntity<T> toPageableResponse(Page<?> page, List<T> content) {
        return PagerUtil.toPageableResponse(page, content);
    }
}
