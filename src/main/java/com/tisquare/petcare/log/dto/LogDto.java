package com.tisquare.petcare.log.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tisquare.petcare.log.validation.ValidatorGroup.HeaderGroup;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDto<T> {
    @JsonUnwrapped
    @NotNull(groups = {HeaderGroup.class}, message = "header is null")
    private HeaderDto header;

    private T data;
}
