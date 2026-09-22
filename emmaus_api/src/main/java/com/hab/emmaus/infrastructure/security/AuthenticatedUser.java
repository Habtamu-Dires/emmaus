package com.hab.emmaus.infrastructure.security;


import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userPublicId,
        Set<String> roles
) {}
