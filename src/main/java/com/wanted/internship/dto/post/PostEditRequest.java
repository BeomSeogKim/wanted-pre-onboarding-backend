package com.wanted.internship.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PostEditRequest(
        @NotEmpty @NotNull
        String content
) {
}
