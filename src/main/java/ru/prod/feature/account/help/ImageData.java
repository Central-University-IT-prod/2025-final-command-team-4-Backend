package ru.prod.feature.account.help;

import org.springframework.core.io.InputStreamResource;

public record ImageData(InputStreamResource resource, String filename) {
}
