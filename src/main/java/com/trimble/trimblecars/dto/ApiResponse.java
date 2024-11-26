package com.trimble.trimblecars.dto;

import lombok.*;


@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ApiResponse<T>
{
    private ResponseType responseType;
    
    private String responseMessage;
    
    private T returnResponse;
}
