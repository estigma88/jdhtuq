package co.edu.uniquindio.dhash.resource.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "inputStream" })
public class FileResourceMixin {
}
