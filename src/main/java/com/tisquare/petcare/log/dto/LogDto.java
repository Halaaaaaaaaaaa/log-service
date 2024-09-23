package com.tisquare.petcare.log.dto;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDto<T> {
    @JsonUnwrapped
    private HeaderDto header;

    private T data;
}
