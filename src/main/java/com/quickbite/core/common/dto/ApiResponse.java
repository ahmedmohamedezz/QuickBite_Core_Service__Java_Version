package com.quickbite.core.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Builder
public class ApiResponse<T> {
    private boolean success;

    private T data;

    private Map<String, Object> meta;   // optional

    public static <T> ApiResponse<T> sendSuccess(T data) {
        return ApiResponse.<T>builder().data(data).success(true).build();
    }

    public static <T> ApiResponse<List<T>> sendPaginated(Page<T> page) {
        return ApiResponse.<List<T>>builder()
                .data(page.getContent())
                .success(true)
                .meta(buildPageMeta(page))
                .build();
    }

    private static Map<String, Object> buildPageMeta(Page<?> page) {
         Map<String, Object> meta = new HashMap<>();

         meta.put("totalElements", page.getTotalElements());
         meta.put("totalPages", page.getTotalPages());
         meta.put("page", page.getNumber());
         meta.put("size", page.getSize());
         meta.put("isLast", page.isLast());

         return meta;
    }
}

