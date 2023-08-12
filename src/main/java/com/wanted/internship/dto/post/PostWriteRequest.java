package com.wanted.internship.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PostWriteRequest(
        @NotEmpty @NotNull
        String content
) {
}
