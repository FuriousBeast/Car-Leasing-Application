package com.trimble.trimblecars.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T>
{
    private ResponseType responseType;
    
    private String responseMessage;
    
    private T returnResponse;
}
